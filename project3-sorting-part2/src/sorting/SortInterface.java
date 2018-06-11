package sorting;

/** An interface that describes several algorithms for sorting a list */
public interface SortInterface {

    public void bucketSort(Elem[] array, int lowindex, int highindex);

    public void radixSort(int[] array, int lowindex, int highindex);

    public void externalSort(String inputFile, String outputFile, int n, int k);

    public void print(Elem[] elems);

}
