
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class RecursiveSummation {
	
	private static final int BASE_LENGTH = 100000000;

	public static void main(String[] args) {
		
		Scanner kbd = new Scanner(System.in);
		
		System.out.print("Enter number of threads (0 for default): ");
		int threadNumbers = kbd.nextInt();
		
		System.out.print("Enter size for array (maximum 100000000): ");		
		int arraySize = kbd.nextInt(); // maximum value for integer
		Integer[] numArray = new Integer[arraySize]; // creates array of size arraySize
			  
		// add random numbers between 1 to 100(excluded) into the array
		Random rand = new Random();
		for (int i = 1; i <= arraySize; i++)
		{
			numArray[i-1] = rand.nextInt(99)+1;
		}
		
		// initialize first and last index of the array
		int first = 0;
		int last = arraySize - 1;
		
		/**
		 * creates ForkJoinPool with available number of processors 
		 * if threadNumbers are greater than 0, create the pool with that number of threads
		 * else create default number of threads available during runtime
		 * computes the total time taken by the number of processors(in parallel) to compute the summation
		 */
		ForkJoinPool pool;
		if(threadNumbers > 0){
			pool = new ForkJoinPool(threadNumbers);
		}
		else{
			pool = new ForkJoinPool(); // creates ForkJoinPool with available number of processors during runtime
		}
		System.out.println("\nBase case length of array or subarray: " + BASE_LENGTH);
		System.out.println("\nStarting Parallel Summation...");
		long startTimeForParallel = System.nanoTime(); 
		long finalSumForParallel = pool.invoke(new RecursiveSummationHelper(numArray, first, last));
		long endTimeForParallel = System.nanoTime();
		double timeDifferenceInMillisForParallel = (endTimeForParallel - startTimeForParallel)/1.E6;
		System.out.println("Total time taken: " + timeDifferenceInMillisForParallel);	
		System.out.println("Final sum: " + finalSumForParallel);
		
		/**
		 * A static private method sumRecursiveSerial() method of the private class is called for the
		 * serial computation of the sum.
		 * The purpose of this block of code is to compare the total time of execution between serial
		 * and parallel tasks
		 */	
		System.out.println("==========================================");
		System.out.println("Starting Serial Summation...");
		long startTimeForSerial = System.nanoTime(); 
		long finalSumForSerial = new RecursiveSummationHelper().sumRecursiveSerial(numArray, first, last);
		long endTimeForSerial = System.nanoTime();
		double timeDifferenceInMillisForSerial = (endTimeForSerial - startTimeForSerial)/1.E6;
		System.out.println("Total time taken: " + timeDifferenceInMillisForSerial);	
		System.out.println("Final sum: " + finalSumForSerial);
	
		kbd.close();

	}
	
	/**
	 * A private class which is subclass of RecursiveTask<T>.
	 * This class overrides a method compute() which checks the size of the given array and decides
	 * whether to perform serial recursive summation or parallel recursive summation.
	 * compute() method is responsible for all the computations.
	 */
	private static class RecursiveSummationHelper extends RecursiveTask<Long>{

		/**
		 * As this class extends RecursiveTask<List<String>>, a constant for serialVersionUID 
		 * is defined for this class because RecursiveTask<T> implements Serializable interface.
		 */
		private static final long serialVersionUID = 1L;
		private Integer[] array;
		private int firstIndex;
		private int lastIndex;	
		
		public RecursiveSummationHelper(){}
		
		public RecursiveSummationHelper(Integer[] intArray, int first, int last) {
			array = intArray;
			firstIndex = first;
			lastIndex = last;
		}

		/**
		 * This method checks the size of the array and decides whether to calculate sum using parallel recursive or serial recursive
		 *
		 */
		@Override
		protected Long compute() {
			int length = lastIndex - firstIndex + 1;
			long partialResult;
			
			/**
			 * if the length of the array is less than 1000000, then there is no need to use fork and join for parallel recursive
			 * computation because too much fork and join takes some time and becomes slower than serial recursive computation.
			 * Because of this reason, we call sumRecursiveSerial() method when the length of the array is less than 1000000
			 * else we continue splitting the array into two sub array using fork(). At some point, there is no worth of splitting
			 * the array into sub arrays (when length is less than 1000000 in this case) so we start calculating sum using serial recursive.
			 */
			if (length <= BASE_LENGTH)
			{
				partialResult = sumRecursiveSerial(array, firstIndex, lastIndex);
			}
			else
			{
				partialResult = sumRecursiveParallel();
			}
			
			return partialResult;
		}
		
		/**
		 * This method recursively divides the input array into two parts; leftSubArray and rightSubArray
		 * And assigns each part to a newly created thread (using fork) to perform summation.
		 * Each thread waits until the summation is finished for the subArray and joins to the parent by returning the result
		 * Returned value from both the leftSubArray and rightSubArray (threads) is added and returned to the caller
		 */
		private long sumRecursiveParallel()
	   	{
		   	int mid = (firstIndex + lastIndex)/2;
		   	RecursiveSummationHelper leftSubArray = new RecursiveSummationHelper(array, firstIndex, mid);
		   	leftSubArray.fork();
		   
		   	RecursiveSummationHelper rightSubArray = new RecursiveSummationHelper(array, mid+1, lastIndex);
		   	rightSubArray.fork();
		   
		   	long leftSum = leftSubArray.join();
		   	long rightSum = rightSubArray.join();
		   	return leftSum + rightSum;
	   	}
		
		/**
		 * This method is called for the serial computation of sum.
		 * Even in the parallel computation, this method is called by checking the size of subArray
		 * to remove the overhead of creating threads recursively for small size array.
		 * @param array
		 * @param first
		 * @param last
		 * @return
		 */
		private long sumRecursiveSerial(Integer[] array, int first, int last)
	   	{
		   	if (last == first)
		   	{
			   return array[first];
		   	}
		   
		   	int mid = (first + last)/2;
		   	long leftSum = sumRecursiveSerial(array, first, mid);
		   	long rightSum = sumRecursiveSerial(array, mid+1, last);
		   	return leftSum + rightSum;
	  	 }
		
	}

}
