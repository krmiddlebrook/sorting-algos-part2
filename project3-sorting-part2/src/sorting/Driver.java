package sorting;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.Math;
import java.util.ListIterator;



public class Driver {
    public static void main(String[] args) {
        SortingAlgorithms algo = new SortingAlgorithms();

        // create a bunch of Elem objects
        Elem e1 = new Elem(1, "Kai");
        Elem e2 = new Elem(6, "Shyam");
        Elem e3 = new Elem(4, "Jack");
        Elem e4 = new Elem(13, "Celo");
        Elem e5 = new Elem(11, "Sameer");
        Elem e6 = new Elem(2, "Alexii");
        Elem e7 = new Elem(3, "Matt B.");
        Elem e8 = new Elem(0, "Dad");

        Elem[] elems = {e1, e2, e3, e4, e5, e6, e7, e8};

        algo.print(elems);
        algo.bucketSort(elems, 2, 6);
        algo.print(elems);

        try{
            for (int i = 0; i < 100; i++) {
                Files.deleteIfExists(Paths.get("temp" + i + ".txt"));
            }
        } catch (Exception e) {

        }

    }

}
