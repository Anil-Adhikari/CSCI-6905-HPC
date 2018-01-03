
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author anil
 * The algorithm implemented here works for the string.
 * For example: if the input list has elements; [bac, cba, bca, acb, abc, cab]
 * The output or sorted list will be; [abc, acb, bac, bca, cab, cba]
 * Limitations: This does not works for digits or numbers.
 * 				The sorting is performed using Most Significant Digit.
 */
public class RadixSorting {


    public static void main(String[] args) {
    	

        ForkJoinPool pool = new ForkJoinPool(); // creates default number of threads during the runtime
        
        // Two argument : input filename and output filename
        if (args.length != 3) {
            System.err.println("Number of command line arguments must be 2");
            System.err.println("You have given only " + args.length + "command line arguments");
            System.err.println("Incorrect usage. Program terminated");
            System.err.println("Correct usage: java Extract <input-file-name> <output-file-name> ");
            System.exit(1);
        }
        
       
        String inputFile = args[0];
        System.out.println("Input file name is: " + inputFile);


        String outputFileForParallel = args[1];
        System.out.println("Output file name for writing words is: " + outputFileForParallel);
        String outputFileForSerial = args[2];
        System.out.println("Output file name for writing words is: " + outputFileForSerial);


        BufferedReader buff = null;

        PrintWriter wordWriterForParallel = null;
        PrintWriter wordWriterForSerial = null;
        
        String line;
        int index, maxLength;
        
        List<String> unSortedListForParallel = new ArrayList<>();
        List<String> unSortedListForSerial = new ArrayList<>();
        
        try {
            wordWriterForParallel = new PrintWriter(outputFileForParallel);
            System.out.println(outputFileForParallel + " successfully opened for writing");
        } catch (IOException ex) {
            System.err.println("Unable to open " + outputFileForParallel + " for writing");
            System.err.println("Program terminated\n");
            System.exit(1);
        }
        
        try {
        	wordWriterForSerial = new PrintWriter(outputFileForSerial);
            System.out.println(outputFileForSerial + " successfully opened for writing");
        } catch (IOException ex) {
            System.err.println("Unable to open " + outputFileForSerial + " for writing");
            System.err.println("Program terminated\n");
            System.exit(1);
        }      

        /**
         * Reading content from input file and putting them into the list of string; unSortedListForParallel
         * for parallel sorting.
         * This block of code is surrounded by try/catch to prevent the IOException that could
         * occur during reading content from input file.
         */
        try {
            buff = new BufferedReader(new FileReader(inputFile));
            while ((line = buff.readLine()) != null) {
                line = line.toLowerCase();
                if (line.equals("")) {
                    continue;
                } else {
                    line = line.replaceAll("[-+;{}.^:,'\"_!@#$%^&*()?0-9]", ""); // all the special characters are removed using replaceAll()
                    String[] words = line.split("\\s+"); // split input line using space which will return array of words
                    for (String word : words) {
                    	unSortedListForParallel.add(word); // add every word into the list
                    }
                }
            }
            
            index = 0;        
            maxLength = 0;  

	        // calculate maximum length of word in the list
	        for (String str : unSortedListForParallel) {
	        	int len = str.length();
	            if (len > maxLength) {
	                maxLength = len;
	            }
	
	        }
	        
	        // This block is responsible for providing output in the console for parallel sorting
	        System.out.println();
	        System.out.println("Starting Parallel Radix Sort...");
	        wordWriterForParallel.println("Input: " + unSortedListForParallel);
	        long startTimeForParallel = System.nanoTime(); 
	        List<String> sortedListForParallel = pool.invoke(new ParallelRadixSort(unSortedListForParallel, index, maxLength));            
	        long endTimeForParallel = System.nanoTime();
	        double timeDifferenceInMillisForParallel = (endTimeForParallel - startTimeForParallel)/1.E6;
	        System.out.println("Total time taken: " + timeDifferenceInMillisForParallel);
	        System.out.println("Sorted array: " + sortedListForParallel);
	        wordWriterForParallel.println();
	        wordWriterForParallel.println("Output: " +sortedListForParallel);
	        wordWriterForParallel.close();  
        
        } 
        catch (IOException ex) {
	        System.err.println("File " + inputFile + " not found. Program terminated.\n");
	        System.exit(1);
        }  
        
        System.out.println("==========================================");
        
                   
        /**
         * Reading content from input file and putting them into the list of string; unSortedListForSerial
         * for serial sorting.
         * This block of code is surrounded by try/catch to prevent the IOException that could
         * occur during reading content from input file.
         */
        try {
            buff = new BufferedReader(new FileReader(inputFile));
            while ((line = buff.readLine()) != null) {
                line = line.toLowerCase();
                if (line.equals("")) {
                    continue;
                } else {
                    line = line.replaceAll("[-+;{}.^:,'\"_!@#$%^&*()?0-9]", ""); // all the special characters and digits are removed using replaceAll()
                    String[] words = line.split("\\s+"); // split input line using space which will return array of words
                    for (String word : words) {
                    	unSortedListForSerial.add(word); // add every word into the list
                    }
                }
            }        
            
            index = 0; 
            maxLength = 0;
        
	        // calculate maximum length of word in the list
	        for (String str : unSortedListForSerial) {
	            int len = str.length();
	            if (len > maxLength) {
	                maxLength = len;
	            }
	
	        }  
	        
	        // This block is responsible for providing output in the console for serial sorting
	        System.out.println("Starting Serial Radix Sort...");
	        wordWriterForSerial.println("Input: " + unSortedListForSerial);
	        long startTimeForSerial = System.nanoTime();
	        List<String> sortedListForSerial = serialRadixSort(unSortedListForSerial, index, maxLength);      
	        long endTimeForSerial = System.nanoTime();
	        double timeDifferenceInMillisForSerial = (endTimeForSerial - startTimeForSerial)/1.E6;
	        System.out.println("Total time taken: " + timeDifferenceInMillisForSerial);	
	        System.out.println("Sorted array: " + sortedListForSerial);
	        wordWriterForSerial.println();
	        wordWriterForSerial.println("Output: " + sortedListForSerial);
	        wordWriterForSerial.close();
        }
	    catch (IOException ex) {
	        System.err.println("File " + inputFile + " not found. Program terminated.\n");
	        System.exit(1);
	    }
       
    }
    
    /**
     * A private class which is responsible for performing radix sort using parallel processes.
     * This class extends from RecursiveTask<T> and overrides the compute() method.
     * Every thread created using fork() will recursively calls this compute method for the sorting or computation.
     * When the bucket size is 0 then the thread starts joining.
     */

    private static class ParallelRadixSort extends RecursiveTask<List<String>> {

        /**
		 * As this class extends RecursiveTask<List<String>>, a constant for serialVersionUID 
		 * is defined for this class because RecursiveTask<T> implements Serializable inteface.
		 */
		private static final long serialVersionUID = 1L;

		List<String> privateList = new ArrayList<>();
		
		List<ArrayList<String>> bucket = new ArrayList<ArrayList<String>>();

        int privateIndex;
        int privateMaxLength;

        ParallelRadixSort(List<String> inputList, int currentIndex, int max) {
            privateList = inputList;
            privateIndex = currentIndex;
            privateMaxLength = max;
        }
        
        /**
         * This method performs the sorting by using fork() and join(). Until there is some item
         * in the bucket, subprocess will be created. If the bucket is empty, subprocess will be joined
         * to the parent process.
         */
        @Override
        protected List<String> compute(){
        	           
            // Clearing the bucket by creating each bucket for letters a to z and digits 0 to 9
            for (int k = 0; k < 26; k++) {
                bucket.add(new ArrayList<String>());

            }          

	        // putting the words into their respective bucket
            for (String str : privateList) {
                if (privateIndex < str.length()) {
                    int currentChar = str.charAt(privateIndex);
                    int value = currentChar - 97;

                    bucket.get(value).add(str);
                }
            }            
	 
            // if index is less than maximum length of words in the list, keep forking and create bucket
            // every character recursively. 
            // Create bucket for each character if size of the bucket is greater than 1.
            if (privateIndex < privateMaxLength) {
                for (int k = 0; k < 26; k++) {
                    if (bucket.get(k).size() > 1) {

                    	ParallelRadixSort tempBucket = new ParallelRadixSort(bucket.get(k), privateIndex + 1, privateMaxLength);                        
                        tempBucket.fork();
                        
                        tempBucket.join();

                    }
                }
            }
	            
	        // update value in the list which will result in the sorted list
            int pos = 0;
            for (int i = 0; i < 26; i++) {
                int len = 0;
                while (len != bucket.get(i).size()) {
                    privateList.set(pos++, bucket.get(i).get(len));
                    len++;
                }
            }          
  
           return privateList; 
        }
		
    }
    
    /**
     * This method sorts the input list recursively.
     * This method does the sorting serially.
     * @param list
     * @param index
     * @param maxLength
     * @return list(sorted version)
     */
    private static List<String> serialRadixSort(List<String> list, int index, int maxLength) {

    	List<ArrayList<String>> bucket = new ArrayList<ArrayList<String>>();

		for (int k = 0; k < 26; k++) {
			bucket.add(k, new ArrayList<String>());
		}
		
		// putting the words into their respective buckets
		for (String str : list) {
			if (index < str.length()) {
				int currentChar = str.charAt(index);
				int value = currentChar - 97;

				bucket.get(value).add(str);

			}
		}
		// checking index is less than maximum length of words in the list
		if (index < maxLength) {

			for (int k = 0; k < 26; k++) {
				// recursively calling SerialRadixSort for bucket having more than 1 element in it
				if (bucket.get(k).size() > 1) {

					serialRadixSort(bucket.get(k), index + 1, maxLength);

				}

			}
		}
		// updating position of elements in list where the final list will be sorted using on the basis of Most Significant Bit
		int pos = 0;
		for (int i = 0; i < 26; i++) {

			int len = 0;

			while (len != bucket.get(i).size()) {

				list.set(pos++, bucket.get(i).get(len));
				len++;
			}

		}
		
		return list;

	}
  
}
