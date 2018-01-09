package hw2;                       
import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    protected double[] results;
    private int size;
    private int numberOfTrials;

    public PercolationStats(int N, int T) {
        size = N;
        numberOfTrials = T;
        if (N < 1 || T < 1) {
            throw new IllegalArgumentException("Minimum values are 1");
        }
        results = new double[T];
        // Each time, initialize a new percolation
        // Then percolate through it.
        int index = 0;
        while (index < T) {
            Percolation trial = new Percolation(N);
            results[index] = run(trial);
            index += 1;
        }
    }

    private double run(Percolation p) {
        // Loop this until the thing percolates
        int index = 0;
        while (!p.percolates()) {
            // We must, at random, pick one of the sites.
            int row = StdRandom.uniform(size);
            int col = StdRandom.uniform(size);
            p.open(row, col);
            index += 1;
        }
        double fraction = ((double) p.numberOfOpenSites() / (size * size));
        return fraction;
    }

    public double mean() {
        return StdStats.mean(results);
    }

    public double stddev() {
        if (numberOfTrials <= 1) {
            return Double.NaN;
        }
        return StdStats.stddev(results);
    }

    public double confidenceLow() {
        double average = mean();
        double interval = (1.96) * stddev() / Math.sqrt(numberOfTrials);
        return average - interval;
    }

    public double confidenceHigh() {
        double average = mean();
        double interval = (1.96) * stddev() / Math.sqrt(numberOfTrials);
        return average + interval;
    }
}                       
