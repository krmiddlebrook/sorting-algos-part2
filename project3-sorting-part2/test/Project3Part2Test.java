import org.junit.Assert;
import org.junit.Test;
import sorting.Elem;
import sorting.SortingAlgorithms;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

/** Test file for Project 3 part 2. Tests bucket, radix and external sorts. */
public class Project3Part2Test {

    public final static int NUM_ITERS = 10; // how many times to test it
    public final static int SIZE = 32; // number of elements in the list
    public final static String INPUT_FILE = "inputFile";
    public final static String SORTED_FILE = "sortedFile";


    @Test
    public void testBucketSort() {
        int n = 15;
        SortingAlgorithms algo  = new SortingAlgorithms();
        Elem[] arr = new Elem[n];
        Random r = new Random();
        for (int i = 0; i < NUM_ITERS; i++) {

            int lowindex = r.nextInt(arr.length / 2);
            int highindex = arr.length / 2 + r.nextInt(arr.length / 2);
            for (int j = 0; j < n; j++) {
                int key = r.nextInt(40);
                int data = r.nextInt(1000);
                arr[j] = new Elem(key, new Integer(data));
            }
            algo.bucketSort(arr, lowindex, highindex);

            if (!areElemsSorted(arr, lowindex, highindex)) {
                System.out.println("In Bucket Sort Test: Not sorted correctly");
                for (Elem rr : arr) {
                    System.out.print(rr.key() + " ");
                }
                System.out.println();
                System.out.println("Testing for range: low = " + lowindex + " high = " + highindex);

                Assert.fail("Bucket sort test failed.");
            }

        }

    }

    @Test
    public void testRadixSort() {
        SortingAlgorithms algo  = new SortingAlgorithms();
        int[] arr1 = { 834, 123, 672, 999, 193, 783, 352, 144, 782, 319, 961, 563, 673 };
        int[] arr1Copy = { 834, 123, 672, 999, 193, 783, 352, 144, 782, 319, 961, 563, 673 };
        int[] arr2 = { 834, 123, 672, 999, 193, 783, 352, 144, 782, 319, 961, 563, 673 };
        int[] arr2Copy = { 834, 123, 672, 999, 193, 783, 352, 144, 782, 319, 961, 563, 673 };

        Random r = new Random();
        // try some lowindex, highindex
        int lowindex = r.nextInt(arr2.length / 2);
        int highindex = arr2.length / 2 + r.nextInt(arr2.length / 2);
        algo.radixSort(arr1, 0, arr1.length - 1);
        // test on arbitrary low, high
        algo.radixSort(arr2, lowindex, highindex);
        if (!isSorted(arr1, 0, arr1.length - 1)) {
            System.out.println("------- Not sorted correctly---------");
            System.out.println("Before  radix sort: arr1 = " + Arrays.toString(arr1Copy));
            System.out.println("After radix sort, for range: low =  0, high = " + (arr1.length - 1) + ", arr1 ="
                    + Arrays.toString(arr1));

            Assert.fail("Radix sort test failed (range from 0 to length-1).");
        }

        // System.out.println("(" + lowindex + " " + highindex + ") " +
        // Arrays.toString(arr2));

        if (!isSorted(arr2, lowindex, highindex)) {
            System.out.println("------- Not sorted correctly---------");
            System.out.println("Before radix sort: arr2 = " + Arrays.toString(arr2Copy));
            System.out.println("After radix sort, for range: low = " + +lowindex + " high = " + highindex + ", arr2 = "
                    + Arrays.toString(arr2));

            Assert.fail("Radix sort test failed (random low, high).");
        }
    }

    @Test
    public void testExternalSort() {
        SortingAlgorithms algo = new SortingAlgorithms();
        try {
            Files.deleteIfExists(Paths.get(INPUT_FILE));
            Files.deleteIfExists(Paths.get(SORTED_FILE));
            for (int i = 0; i < 100; i++) {
                Files.deleteIfExists(Paths.get("temp" + i + ".txt"));
            }

        } catch (IOException e1) {
            System.out.println("inputFile did not exist, no need to remove");
        }
        generateLargeFile(INPUT_FILE, 1000000);
        algo.externalSort(INPUT_FILE, SORTED_FILE, 10000, 100);
//        algo.externalSort(INPUT_FILE, SORTED_FILE, 100, 10);
        try (BufferedReader br = new BufferedReader(new FileReader(SORTED_FILE))) {
            String line;
            int num = 0;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                int nextNum = Integer.parseInt(line);
                lineNum++;
                if ((nextNum < num) || (nextNum == Integer.MAX_VALUE)) {
                    Assert.fail("External file is not sorted properly. See line # " + lineNum);
                }
                num = nextNum;
            }
            // Delete temp files
            for (int i = 0; i < 100; i++) {
                Files.deleteIfExists(Paths.get("temp" + i + ".txt"));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + e);
            Assert.fail();
        } catch (IOException e) {
            System.out.println("Could not read from file: " + e);
            Assert.fail();
        }
    }

    /** Helper method for bucket sort.
     * Checks if the subarray (from startIndex to endIndex)
     * of records is sorted by key in ascending order.
     * @param arr array of records (of type Elem)
     * @param startIndex the starting index of a subarray
     * @param endIndex the end index of a subarray
     * @return true if sorted
     */
    private static boolean areElemsSorted(Elem[] arr, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            if (!(arr[i].key() <= arr[i + 1].key())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the part of the array of integers from startIndex to endIndex
     * (inclusive) is sorted
     * @param arr array of integers
     * @param startIndex the first index of the subarray
     * @param endIndex the last index of the subarray
     * @return true if sorted, false otherwise
     */
    public static boolean isSorted(int[] arr, int startIndex, int endIndex) {
        if (startIndex > endIndex) {
            System.out.println("startIndex < endIndex");
            return false;
        }
        if (startIndex == endIndex - 1)
            return true;

        if ((arr[startIndex] <= (arr[startIndex + 1]))) {
            return isSorted(arr, startIndex + 1, endIndex);
        } else
            return false;
    }

    /**
     * Used for testing external sort. Generates a large file of ints.
     * @param filename name of the file to create
     * @param n number of elements in the file
     */
    public static void generateLargeFile(String filename, int n) {
        Random rand = new Random();
        try (PrintWriter pw = new PrintWriter(filename)) {
            for (int i = 0; i < n; i++)
                pw.println(rand.nextInt(1000) + rand.nextInt(100));
        } catch (IOException e) {
            System.out.println("Error writing to a file " + filename);
        }

    }

}
