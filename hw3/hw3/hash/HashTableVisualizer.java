package hw3.hash;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class HashTableVisualizer {

    public static void main(String[] args) {
        /* scale: StdDraw scale
           N:     number of items
           M:     number of buckets */

        double scale = 1.0;
        int N = 50;
        int M = 10;

        HashTableDrawingUtility.setScale(scale);
        Set<Oomage> oomies = new HashSet<Oomage>();
        for (int i = 0; i < N; i += 1) {
            oomies.add(ComplexOomage.randomComplexOomage());
        }
        visualize(oomies, M, scale);
    }

    public static void visualize(Set<Oomage> set, int M, double scale) {
        HashTableDrawingUtility.drawLabels(M);
        HashTableDrawingUtility.setScale(scale);
        int[] tracker = new int[M];
        int index = 0;
        while (index < M) {
          tracker[index] = 0;
          index += 1;
        }
        Iterator<Oomage> iterator = set.iterator();
        while (iterator.hasNext()) {
            Oomage inspect = iterator.next();
            int bucketNumber = inspect.hashCode() % M;
            if (bucketNumber < 0) {
                bucketNumber += M;
            }
            int bucketPosition = tracker[bucketNumber];
            
            inspect.draw(HashTableDrawingUtility.xCoord(bucketPosition), HashTableDrawingUtility.yCoord(M - 1 - bucketNumber, M), scale);
            tracker[bucketNumber] += 1;
        }

        /* When done with visualizer, be sure to try 
           scale = 0.5, N = 2000, M = 100. */           
    }
} 
