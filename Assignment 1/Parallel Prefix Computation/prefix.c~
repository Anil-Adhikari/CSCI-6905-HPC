/* 
* gcc compiler is used to compile this program
* Example of compilation and running:
* array.mat is the file which contains elements for the array
* syntax to compile:
*		gcc prefix.c -lpthread -o prefix
* syntax to run:
*	    	./prefix array.mat
*/

#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>

int * arr; // original array which holds elements from the file
int * sum; // pointer array to holds final elements during every increment
int * temp; // pointer array to hold temporary sum during every increment

pthread_barrier_t barrier;

int increment = 1;
int i;

int sizeOfArray;

void *compute(void *id_ptr);

void printArray(int inc);

void createThread();

int countElementFromFile(char* fileName);

void readArrayElement(char* fileName, int sizeOfArray, int * arr);

int main(int argc, char **argv){

	char * fileName = argv[1];

	sizeOfArray = countElementFromFile(fileName);

	// dynamically allocating size of the pointer array
	arr = (int *) malloc( sizeOfArray*sizeof(int));
	sum = (int *) malloc( sizeOfArray*sizeof(int));
	temp = (int *) malloc( sizeOfArray*sizeof(int));

	// statement to read array elements form file 
	readArrayElement(fileName, sizeOfArray, arr);

	// block to print initial array
	printf("Initial Array: ");
	for(i = 0; i < sizeOfArray; i++){

		sum[i] = arr[i];
		
		printf("%d  ", sum[i]);
	}

	// loop which controls the increment/distance and creates all the threads 
	while(increment < sizeOfArray){

		createThread();

		printf("\n");

		printArray(increment);

		increment = increment * 2;
	}

	printf("\n");

	return(0);
}

// method computes the value using distance and assigns into a temp array
void *compute(void *id_ptr){

	int thread_id = *(int*)id_ptr;

	// addition is done according to the increment/distance
	if(increment <= thread_id){
		temp[thread_id] = sum[thread_id] + sum[thread_id - increment];
	}else{
		temp[thread_id] = sum[thread_id];
	}

	// statement which cause all the threads to wait for other threads to finish their work
	pthread_barrier_wait(&barrier);	

	return NULL;
}

// method to print array in the console
void printArray(int increment){

	int i;

	printf("\nSum after distance %d: ", increment);

	for(i = 0; i < 6; i++){

		printf("%d  ",sum[i]);
	}
}

// method to create thread
void createThread(){

	pthread_t tids[sizeOfArray]; // array of all the pthreads
	int ids[sizeOfArray];		 // array of thread ids

	// creates thread barrier
	pthread_barrier_init(&barrier, NULL, sizeOfArray+1);

	// loop to create thread for every element of the array
	for(i = 0; i < sizeOfArray; i++){
		ids[i] = i;
		pthread_create(&tids[i], NULL, compute, &ids[i]);
	}

	pthread_barrier_wait(&barrier);

	// loop to join all the finished thread to the main thread
	for(i = 0; i < sizeOfArray; i++){
		pthread_join(tids[i], NULL);
	}

	// destroys the barrier after all threads finsih thier work
	pthread_barrier_destroy(&barrier);

	// loop to copy all the element from temp array to sum array
	for(i = 0; i < sizeOfArray; i++){

		sum[i] = temp[i];

	}

}

// method to count elements for the size of the array
int countElementFromFile(char* fileName){

	FILE* fp;

	int i;
	int count = 1; 

	fp = fopen(fileName, "r");

	if(fp == NULL){

		printf("File %s can not be opened!",fileName);
		fclose(fp);
		//exit(-1);
	}

	while((i = fgetc(fp)) != '\n'){
		if(i == ' '){
			++count;
		}
	}

	fclose(fp);
	return count;
	
}

// method which reads array element from the file
void readArrayElement(char* fileName, int sizeOfArray, int * arr){

	FILE* fp;

	int number;

	int j;

	fp = fopen(fileName, "r");

	for(j = 0; j < sizeOfArray; j++){
		fscanf(fp, "%d", &number);		
		arr[j] = number;
	}

	fclose(fp);
}
