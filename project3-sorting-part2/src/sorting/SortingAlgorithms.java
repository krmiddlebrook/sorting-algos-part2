package sorting;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;

import java.util.ListIterator;
import java.util.Random;

/**  A class that implements SortInterface. Has various methods
 *   to sort a list of elements. */
public class SortingAlgorithms implements SortInterface {

    /**
     * Sorts a sub-array of records using bucket sort.
     * @param array array of records
     * @param lowindex the beginning index of the sublist to sort
     * @param highindex the end index of the sublist to sort
     */
    @Override
    public void bucketSort(Elem[] array, int lowindex, int highindex) {
        int i;
        int j;
        int numBuckets = ((highindex - lowindex) / 2) + 1; // calculate number of buckets
        int max = findMax(array); // find the maximum key value in the list
        int binWidth = (int) Math.ceil((max + 1)/numBuckets); // the range for each bucket
        LinkedList<Elem>[] buckets = new LinkedList[numBuckets + 1]; // create the list to hold buckets
        // create each bucket
        for (i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<Elem>();
        }

        // place Elem objects into buckets
        for (i = lowindex; i <= highindex; i++) {
            j = (int) Math.floor(array[i].key()/binWidth); // finds the index of the bucket that will store elem object
            buckets[j].addLast(array[i]); // adds elem object to the proper bucket
        }

        // sort each bucket
        for (i = 0; i < numBuckets + 1; i++) {
            if (buckets[i].size() != 0) {
                insertionSort(buckets[i]);
            }
        }

        // add sorted buckets to array
        int insertIndex = lowindex;
        for (i = 0; i < numBuckets + 1; i++) {
            Iterator<Elem> it = buckets[i].iterator();
            while (it.hasNext()) {
                if (insertIndex >= highindex + 1) {
                    break;
                }
                Elem elem = it.next();
                array[insertIndex] = elem;
                insertIndex++;
            }
        }
    }

    /**
     * Helper method for bucket sort. Sorts a given linked list using insertion sort.
     * @param bucket a linked list to sort be sorted
     */
    private void insertionSort(LinkedList<Elem> bucket) {
        int j;
        Elem element;
        Elem[] elems = new Elem[bucket.size()];
        elems[0] = bucket.remove(0);
        // insertion sort bucket list
        for (int i = 1; i < elems.length; i++) {
           element = bucket.remove();
           j = i - 1;
           while (j >= 0 && elems[j].key() > element.key()) {
               elems[j + 1] = elems[j];
               j--;
           }
           elems[j + 1] = element;
        }

        // insert sorted elements back into linked list
        for (int i = 0; i < elems.length; i++) {
            bucket.addLast(elems[i]);
        }
    }

    /**
     * Helper method for bucket sort to find the the max value
     * in the list. This will be useful when we create buckets.
     * @param array array of records
     * @return the max value
     */
    private int findMax(Elem[] array) {
        int max = array[0].key();
        for (int i = 1; i < array.length; i++) {
            if (array[i].key() > max) {
                max = array[i].key();
            }
        }
        return max;
    }

    /**
     * Sorts a sub-array from lowindex to highindex using radix sort.
     * Uses base 10.
     * @param array
     * @param lowindex
     * @param highindex
     */
    public void radixSort(int[] array, int lowindex, int highindex) {
        // First, compute the number of digits in each key
        // Since we assume they all have the same # of digits,
        // it's enough to compute the # of digits in the first key
        int range = (highindex - lowindex) + 1;
        if (array.length == 0) {
            return;
        }
        if (highindex >= array.length) {
            highindex = array.length - 1;
        }

        int ndigits = (int) (Math.log10(array[lowindex]) + 1); // initialize the num of digits to the num of digits for the low index integer

        int[] tmp = new int[range]; // tmp array to store elements after each sort
        int[] count = new int[10]; // count array for counting sort
        for (int i = 0, place = 1; i < ndigits; i++, place *= 10) {
            // place will be 1, then 10, then 100, then 1000, etc.
            // initialize count array
            for (int j = 0; j < 10; j++) {
                count[j] = 0;
            }
            // iterate over array and fill out count array
            for (int j = lowindex; j <= highindex; j++) {
                int k = (array[j] / place) % 10;
                count[k]++;
            }

            for (int j = 1; j < 10; j++) { // modified count array
                count[j] = count[j] + count[j - 1];
            }
            // result will be in temp
            for (int j = highindex; j >= lowindex; j--) {
                    tmp[--count[(array[j] / place) % 10]] = array[j];
            }

            // copy the result back into array
            for (int j = 0; j < array.length; j++) {
                if ((j >= lowindex) && (j <= highindex)) {
                    array[j] = tmp[j - lowindex];
                }
            }
        }
    }

    /**
     * Implements external sort method
     * @param inputFile The file that contains the input list
     * @param outputFile The file where to output the sorted list
     * @param n number of elements to sort
     * @param k number of elements that fit into memory at once
     */
    public void externalSort(String inputFile, String outputFile, int n, int k) {
        BufferedReader reader;
        String prefixOfFile = "temp";
        try {
            int numChunks = (int) Math.ceil(n / k);
            String[] tempFileNames = new String[numChunks];
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            while (line != null) {
                for (int m = 0; m < numChunks; m++) { // m is the chunk index
                    int[] tmp = new int[k];
                    for (int i = 0; i < k; i++) {
                        if (line != null) {
                            tmp[i] = Integer.parseInt(line);
                            line = reader.readLine();
                        }
                    }
                    randomizedQuickSort(tmp, 0, tmp.length - 1);
                    String tempName = prefixOfFile.concat(Integer.toString(m) + ".txt");
                    tempFileNames[m] = tempName;
                    printToFile(tmp, tempName);
                }
            }
            reader.close(); // close the input file

            try {
                int minValueIndex;
                PrintWriter writeToOutput = new PrintWriter(outputFile);
                BufferedReader[] bufferedReaders = new BufferedReader[numChunks];
                for (int m = 0; m < numChunks; m++) {
                    bufferedReaders[m] = new BufferedReader(new FileReader(tempFileNames[m]));
                }
                int[] compareIntsArray = new int[numChunks]; // to store an integer from each file
                for (int m = 0; m < numChunks; m++) {
                    compareIntsArray[m] = Integer.parseInt(bufferedReaders[m].readLine()); // populates compareIntsArray with the first integer in each temp file
                }
                minValueIndex = getMinIndex(compareIntsArray); // store the index of the min value
                writeToOutput.println(compareIntsArray[minValueIndex]); // write the min value to output file
                String fileLine;
                for (int i = 1; i < n; i++) {
                    fileLine = bufferedReaders[minValueIndex].readLine();
                    if (fileLine != null) {
                        compareIntsArray[minValueIndex] = Integer.parseInt(fileLine);
                    }
                    else {
                        compareIntsArray[minValueIndex] = Integer.MAX_VALUE;
                    }
                    minValueIndex = getMinIndex(compareIntsArray); // get new min value index after updating compareIntsArray
                    writeToOutput.println(compareIntsArray[minValueIndex]); // write min value to output file
                }
                writeToOutput.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }

        } catch (FileNotFoundException e1) {
            System.out.println("Input file not found!");
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    /**
     * Helper method for external sort. Finds the index of the min value
     * in an array.
     * @param array the array
     * @return index of the min value
     */
    private int getMinIndex(int[] array) {
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * Helper method for external sort to write sorted array to a file.
     * Output given words to the file with the given filename
     * @param integers array of ints
     * @param filename name of the output file
     */
    private void printToFile(int[] integers, String filename) {
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (int num : integers) {
                pw.println(num);
            }
            pw.close();
        }
        catch (IOException e) {
            System.out.println("Error writing to a file: "  + e);
        }
    }

    /**
     * Helper method for external sort.
     * Sorts the sublist of the given list (from lowindex to highindex) using the randomizedQuickSort.
     * @param array array to sort
     * @param lowindex the beginning index of a sublist
     * @param highindex the end index of a sublist
     */
    private void randomizedQuickSort(int[] array, int lowindex, int highindex) {
        int pivot; // index of the pivot
        if (lowindex < highindex) {
            pivot = partition(array, lowindex, highindex);
            randomizedQuickSort(array, lowindex, pivot - 1);
            randomizedQuickSort(array, pivot + 1, highindex);
        }
    }

    /**
     * Helper method for quickSort.
     * @param arr array of integers
     * @param low the starting value of i
     * @param high the starting value of j
     * @return
     */
    private int partition(int[] arr, int low, int high) {
        int pivot;
        int tmp;
        int max = high;
        int median = getMedianIndex(arr, low, high); // returns the index of the median value

        // swaps elem at pivot with last elem in list
        tmp = arr[median];
        arr[median] = arr[high];
        arr[high] = tmp;
        pivot = arr[high];
        low--;
        do {
            while ((low < high) && (arr[++low] < pivot )) // increments low index when low elem is smaller than pivot
                ;
            while ((low < high) && (arr[--high] > pivot)) // decrements high index when high elem is larger than pivot
                ;
            // swap values at low and high
            tmp = arr[low];
            arr[low] = arr[high];
            arr[high] = tmp;
        } while (low < high);

        // swap pivot elem with elem at low index
        tmp = arr[low];
        arr[low] = arr[max];
        arr[max] = tmp;
        return low;
    }

    /**
     * Helper method for partition. Generates three random indices and finds the index
     * of the median value.
     * @param arr array of comparables
     * @param lowIndex starting index of the sublist
     * @param highIndex ending index of the sublist
     * @return index of the median
     */
    private int getMedianIndex(int[] arr, int lowIndex, int highIndex) {
        int[] randomIndices = new int[3]; // to store the random indices
        int[] randomElems = new int[3]; // to store the elements at each random index
        int medianElem; // to store the median element
        int medianIndex = 0; // to store the median index

        for (int i = 0; i < randomIndices.length; i++) {
            randomIndices[i] = generateRandom(lowIndex, highIndex); // add random integers to the randoms array
            randomElems[i] = arr[randomIndices[i]]; // add element at the random index to randomElems
        }
        // sorts randomElems list using insertion sort method
        int j;
        for (int i = 1; i < randomElems.length; i++) {
            j = i - 1;
            while (j >= 0 && (randomElems[j] > randomElems[i])) {
                randomElems[j + 1] = randomElems[j]; // shifts larger elems to the right
                j--;
            }
            randomElems[j + 1] = randomElems[i]; // insert first elem of unsorted list into the sorted list at the appropriate spot
        }

        if (randomIndices[2] == 0) { // checks if sublist only contains 2 elements
            Random r = new Random();
            medianElem = randomElems[r.nextInt(2)]; // randomly pick medianElem
        } else {
            medianElem = randomElems[1]; // assigns the middle element to medianElem
        }

        for (int i = 0; i < randomIndices.length; i++) { // finds the index of the median element
            if (arr[randomIndices[i]] == medianElem) {
                medianIndex = randomIndices[i];
                return medianIndex;
            }
        }
        return medianIndex;
    }

    /**
     * Generates a random number within range (range = highIndex - lowIndex).
     * @param lowIndex minimum integer value to generate
     * @param highIndex maximum integer value to generate
     * @return a random integer between lowIndex (inclusive) and highIndex (inclusive)
     */
    private int generateRandom(int lowIndex, int highIndex) {
        Random random = new Random();
        return random.nextInt((highIndex - lowIndex) + 1) + lowIndex;
    }

    /**
     * Method to print the list
     * @param elems the array to print
     */
    public void print(Elem[] elems) {
        for (int i = 0; i < elems.length; i++) {
            System.out.print(elems[i].toString() + "; ");
        }
        System.out.println();
    }
}
