package dolmisani.toys.utils;


/*************************************************************************
 *  Compilation:  javac StdStats.java
 *  Execution:    java StdStats
 *
 *  Library of statistical functions.
 *
 *  The test client converts the command-line arguments to an array
 *  of doubles, and then calls min(), mean(), stddev(), and max(),
 *
 *  The functions all throw a NullPointerException if the array
 *  passed in is null.
 *
 *  % java StdStats 3.0 1.0 2.0 5.0 4.0
 *         min   1.000
 *        mean   3.000
 *         max   5.000
 *     std dev   1.581
 *
 *************************************************************************/

/**
 *  <i>Standard statistics</i>. This class provides methods for computing
 *  statistics such as min, max, mean, sample standard deviation, and
 *  sample variance.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://www.cs.princeton.edu/introcs/22libary">Section 2.2</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 *  by Robert Sedgewick and Kevin Wayne.
 */
public class StdStats {

    /**
      * Return maximum value in array, -infinity if no such value.
      */
    public static double max(double[] a) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) max = a[i];
        }
        return max;
    }

   /**
     * Return maximum value of array, Integer.MIN_VALUE if no such value
     */
    public static int max(int[] a) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max) max = a[i];
        }
        return max;
    }

   /**
     * Return minimum value in array, +infinity if no such value.
     */
    public static double min(double[] a) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (a[i] < min) min = a[i];
        }
        return min;
    }

   /**
     * Return minimum value of array, Integer.MAX_VALUE if no such value
     */
    public static int min(int[] a) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            if (a[i] < min) min = a[i];
        }
        return min;
    }

   /**
     * Return average value in array, NaN if no such value.
     */
    public static double mean(double[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
        }
        return sum / a.length;
    }

   /**
     * Return average value in array, NaN if no such value.
     */
    public static double mean(int[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
        }
        return sum / a.length;
    }

   /**
     * Return sample variance of array, NaN if no such value.
     */
    public static double var(double[] a) {
        if (a.length == 0) throw new RuntimeException("Array size is 0.");
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }

   /**
     * Return sample variance of array, NaN if no such value.
     */
    public static double var(int[] a) {
        if (a.length == 0) throw new RuntimeException("Array size is 0.");
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }

   /**
     * Return sample standard deviation of array, NaN if no such value.
     */
    public static double stddev(double[] a) {
        return Math.sqrt(var(a));
    }

   /**
     * Return sample standard deviation of array, NaN if no such value.
     */
    public static double stddev(int[] a) {
        return Math.sqrt(var(a));
    }

   /**
     * Return sum of all values in array.
     */
    public static int sum(int[] a) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

   /**
     * Plot points (i, a[i]) to standard draw.
     */
    public static void plotPoints(double[] a) {
        int N = a.length;
        StdDraw.setXscale(0, N-1);
        StdDraw.setPenRadius(1.0 / (3.0 * N));
        for (int i = 0; i < N; i++) {
            StdDraw.point(i, a[i]);
        }
    }

   /**
     * Plot line segments connecting points (i, a[i]) to standard draw.
     */
    public static void plotLines(double[] a) {
        int N = a.length;
        StdDraw.setXscale(0, N-1);
        StdDraw.setPenRadius();
        for (int i = 1; i < N; i++) {
            StdDraw.line(i-1, a[i-1], i, a[i]);
        }
    }

   /**
     * Plot bars from (0, a[i]) to (i, a[i]) to standard draw.
     */
    public static void plotBars(double[] a) {
        int N = a.length;
        StdDraw.setXscale(0, N-1);
        StdDraw.setPenRadius(0.5 / N);
        for (int i = 0; i < N; i++) {
            StdDraw.line(i, 0, i, a[i]);
        }
    }




   /**
     * Test client.
     * Convert command-line arguments to array of doubles and call various methods.
     */
    public static void main(String[] args) {

        // read in values from command line
        double[] a = new double[args.length];
        for (int i = 0; i < args.length; i++) {
            a[i] = Double.parseDouble(args[i]);
        }

        StdOut.printf("       min %7.3f\n", min(a));
        StdOut.printf("      mean %7.3f\n", mean(a));
        StdOut.printf("       max %7.3f\n", max(a));
        StdOut.printf("   std dev %7.3f\n", stddev(a));
    }
}
