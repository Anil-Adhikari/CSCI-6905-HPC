#include<stdio.h>
#include<stdlib.h>

int main(int args, char** argv){	

	printf("\nIn worker process: process Id: %d", getpid());

	// length of vector is in argv[1]
	char * vlength_ptr = argv[1];
	int vlength = (int)*vlength_ptr;
	
	
	int vector1[vlength];
	int vector2[vlength];

	int i,j;

	int m = 2 + vlength;

	// making vector1
	for(i = 2; i < m; i++){
		char * value = argv[i];
		vector1[i-2] = (int)*value;
	}

	// printing vector 1
	printf("\nIn worker process: vector1: ");
	for(i = 0; i < vlength; i++){
		printf(" %d  ", vector1[i]);
	}

	int n = m + vlength;

	// making vector2
	for(i = m; i < n; i++){
		char * value = argv[i];
		vector2[i-m] = (int)*value;
	}

	// printing vector 2
	printf("\nIn worker process: vector2: ");
	for(i = 0; i < vlength; i++){
		printf(" %d  ", vector2[i]);
	}

	printf("\n");

	//perform multiplication
	int sum = 0;
	for(i = 0; i < vlength; i++){
		sum = sum + vector1[i] * vector2[i];
	}

	printf("In worker process: result: %d\n", sum);
	
	// putting sum in exit() system call so that parent process can get the result using wait()
	exit(sum);
}