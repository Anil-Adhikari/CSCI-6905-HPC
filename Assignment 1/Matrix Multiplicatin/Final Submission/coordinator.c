/* 
* gcc compiler is used to compile this program
* Example of compilation and running:
* a.mat and b.mat are the files which contains first and second matrix respectively
* c.mat conatins the resultant matrix after multiplication
* syntax to compile:
*		gcc coordinator.c -o coordinator
*		gcc worker.c -o worker
* syntax to run:
*	    ./coordinator a.mat b.mat c.mat
*/

#include<unistd.h>
#include<stdlib.h>
#include<stdio.h>

// method which reads the matrix from file
void readMatrix(char* fileName, int row, int column, int
	matrix[row][column]);

// method that creates vecotr of row from first matrix
void makeRowVector(int vrow, int dimension, int row, int column, int matrix[row][column], char * arguments[20]);

// method that creates vecotr of column from second matrix
void makeColumnVector(int vcolumn, int dimension, int row, int column, int matrix[row][column],char * arguments[20]);

// method that prints the read matrix into console
void printMatrix(int row, int column, int matrix[row][column]);

// method that writes the result of mutliplication into file
void writeMatrix(char* fileName, int row, int column, int matrix[row][column]);

// method to read matrix size from the file
void getMatrixSize(char* fileName, int * row_ptr, int * column_ptr);

// method to check whether input file is empty or not
void checkEmptyFile(char * fileName2);

int main(int argc, char** argv){

	// pointer to input files taken from command line
	char* fileName1;
	char* fileName2;
	char* fileName3;

	fileName1 = argv[1];
	fileName2 = argv[2];
	fileName3 = argv[3];

	// pointers to hold size(row and column) of matrix
	int * row1_ptr = (int *)malloc(sizeof(int));
	int * column1_ptr = (int *)malloc(sizeof(int));
	int * row2_ptr = (int *)malloc(sizeof(int));
	int * column2_ptr = (int *)malloc(sizeof(int));

	// check empty file
	checkEmptyFile(fileName1);
	checkEmptyFile(fileName2);

	// this method call assigns values ot the pointer for the size of matrix by reading given file
	getMatrixSize(fileName1, row1_ptr, column1_ptr);
	getMatrixSize(fileName2, row2_ptr, column2_ptr);

	// int variable declaration for size of matrix
	int row1, column1;
	int row2, column2;

	// assign value from pointer to int variable
	row1 = *row1_ptr;
	column1 = *column1_ptr;
	row2 = *row2_ptr;
	column2 = *column2_ptr;		

	// matrix declaration for first, second and resultant matrix respectively
	int matrix1[row1][column1];
	int matrix2[row2][column2];
	int matrixProduct[row1][column2];

	// int variable declaration
	int i, j, k, pid, returned_status, status, returned_value;

	// reading and printing first matrix
	printf("\nReading first matrix... \n");
	readMatrix(fileName1, row1, column1, matrix1);
	printf("Size of Matrix1: %d x %d \n", row1, column1);
	printf("Matrix1: \n");
	printMatrix(row1, column1, matrix1);

	// reading and printing second matrix
	printf("Reading second matrix... \n");
	readMatrix(fileName2, row2, column2, matrix2);
	printf("Size of Matrix2: %d x %d \n", row2, column2);
	printf("Matrix2: \n");
	printMatrix(row2, column2, matrix2);

	fflush(stdout); // used to flush output buffer

	// if column of first matrix is not equal to row of second matrix, then matrix multiplication is not possible
	if(column1 != row2){
		printf("Sorry!! These two matrices can not be multiplied.\n");
		printf("Because number of columns in Matrix1 is not equal to number of rows in Matrix2 :( \n");
		exit(-1);
	}	

	// loop that creates child process for every element of resultant matrix using fork()
	// worker file is executed using execvp() system call
	// worker file does the multiplication and returns result using exit() system call
	// result form the worker is collected using wait() system call
	for(i = 0; i < row1; i++)
  	{
      	for(j = 0; j < column2; j++)
      	{
      		char * arguments[20];

      		arguments[0] = "./worker";

	      	fflush(stdout);

	        pid = fork();

	        if(pid == -1){
	          	perror("Can't fork\n");
	        }

	        if(pid == 0){ // child process
	        	
	        	fflush(stdout);
	        	printf("\nIn coordinator process: process Id: %d\n", getpid());
	        	fflush(stdout);	        	

    			int * column1_ptr = &column1;
    			arguments[1] = (char *)malloc(sizeof(int));
    			arguments[1] = (char *)column1_ptr;

	        	makeRowVector(i, column1, row1, column1, matrix1, arguments);
	      		makeColumnVector(j, column1, row2, column2, matrix2, arguments);

	      		arguments[1+2*column1+1] = NULL;
	        	
	        	execvp(arguments[0], arguments);
				perror("execvp() failed");

	        }

	        if(pid > 0) //parent process where returned status is processed to get returned value from the child/worker process
	        {
	          	returned_status = wait(&status);
	          	returned_value = status>>8;
	          	matrixProduct[i][j] = returned_value;

	          	printf("\nIn coordinator process: result: %d\n\n", returned_value);
	        }

	    }
  	}

  	// print resultant matrix
	printf("Size of Resultant Matrix: %d x %d \n", row1, column2);
	printf("\nResultant matrix is: \n");
	printMatrix(row1, column2, matrixProduct);

	// write resultant matrix to file
	printf("Writing to the file... \n");
	writeMatrix(fileName3, row1, column2, matrixProduct);
	printf("Done!! \n\n");

	// free allocated memory
	free(row1_ptr);
	free(column1_ptr);
	free(row2_ptr);
	free(column2_ptr);
	

	return(0);
}

// method which reads the matrix from file
void readMatrix(char* fileName, int row, int column, int matrix[row][column]){

	FILE* fp;

	int number;

	int i,j;

	fp = fopen(fileName, "r");

	if(fp == NULL){

		printf("File %s can not be opened!",fileName);
		fclose(fp);
		exit(-1);
	}

	// this is used to skip first two numbers from the file which are actually row and column for the matrix and is already read
	// in another method getMatrixSize
	fscanf(fp, "%d", &number);
	fscanf(fp, "%d", &number);

	for(i = 0; i < row; i++){
		for(j = 0; j < column; j++){
			fscanf(fp, "%d", &number);
			matrix[i][j] = number;
		}
	}

	fclose(fp);
}

// method that assigns row vector to the char pointer array which will be passed to worker using execvp()
void makeRowVector(int vrow, int dimension, int row, int column, int matrix[row][column],char * arguments[20]){
	
	int i;

	for(i = 0; i < dimension; i++){
		int *a = &matrix[vrow][i];
		arguments[1 + i + 1] = (char *)malloc(sizeof(int));
		arguments[1 + i + 1] = (char *)a;
	}
}

// method that assigns column vector to the char pointer array which will be passed to worker using execvp()
void makeColumnVector(int vcolumn, int dimension, int row, int column, int matrix[row][column],char * arguments[20]){
	int i;

	for(i = 0; i < dimension; i++){

		int *a = &matrix[i][vcolumn];
		arguments[1+i+dimension+1] = (char *)malloc(sizeof(int));
		arguments[1+i+dimension+1] = (char *)a;

	}
}

// method that prints the read matrix into console
void printMatrix(int row, int column, int matrix[row][column]){

	int i, j;

	for(i = 0; i < row; i++){
		for(j = 0; j < column; j++){
			printf("%d", matrix[i][j]);
			printf("\t");
		}
		printf("\n");
	}
}

// method that writes the result of mutliplication into file
void writeMatrix(char* fileName, int row, int column, int matrix[row][column]){

	FILE* fp;

	int i, j;

	fp = fopen(fileName, "w");

	if(fp == NULL){

		printf("File %s can not be opened!",fileName);
		fclose(fp);
		exit(-1);
	}

	fprintf(fp, "%d ", row );
	fprintf(fp, "%d\n", column);

	for(i = 0; i < row; i++){
		for(j = 0; j < column; j++){
			fprintf(fp, "%d ", matrix[i][j]);
		}
		fprintf(fp, "%s", "\n");
	}

	fclose(fp);
}

// method that reads size of matrix from the input file
// In the given file, first row contains size of matrix in s(row) t(columnn) format
void getMatrixSize(char * fileName, int * row_ptr, int * column_ptr){

	FILE* fp;

	int i, file_size, number, count = 0;

	fp = fopen(fileName, "r");

	if(fp == NULL){

		printf("File %s can not be opened!",fileName);
		fclose(fp);
		exit(-1);
	}	

	fscanf(fp, "%d", &number);
	*row_ptr = number;

	fscanf(fp, "%d", &number);
	*column_ptr = number;

	fclose(fp);

}

// method that checks whether the file is empty or not
// if the file is empty then the program exits
void checkEmptyFile(char * fileName){

	FILE * fp;

	int i;

	fp = fopen(fileName, "r");
	if(fp == NULL){

		printf("File %s can not be opened!",fileName);
		fclose(fp);
		exit(-1);
	}	
	else {
	    fseek(fp, 0, SEEK_END);
	    int len = ftell(fp);
	    if (len == 0) {  //check if the file is empty or not.
	        printf("Sorry!!! file %s is empty. \n", fileName);
	        fclose(fp);
	        exit(-1);
	    }
	    fclose( fp );
	}
}
