package dolmisani.toys.utils;

/*************************************************************************
 *  Compilation:  javac StdOut.java
 *  Execution:    java StdOut
 *
 *  Writes data of various types to standard output.
 *
 *************************************************************************/

import java.io.UnsupportedEncodingException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

/**
 *  <i>Standard output</i>. This class provides methods for writing strings
 *  and numbers to standard output.
 *  <p>
 *  For additional documentation, see <a href="http://www.cs.princeton.edu/introcs/15inout">Section 1.5</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by Robert Sedgewick and Kevin Wayne.
 */
public final class StdOut {

    // force Unicode UTF-8 encoding; otherwise it's system dependent
    private static String charsetName = "UTF-8";

    // send output here
    private static PrintWriter out;

    // this is called before invoking any methods
    static {
        try {
            out = new PrintWriter(new OutputStreamWriter(System.out, charsetName), true);
        }
        catch (UnsupportedEncodingException e) { System.out.println(e); }
    }

    // singleton pattern - can't instantiate
    private StdOut() { }

    // close the output stream (not required)
   /**
     * Close standard output.
     */
    public static void close() {
        out.close();
    }

   /**
     * Terminate the current line by printing the line separator string.
     */
    public static void println() {
        out.println();
    }

   /**
     * Print an object to standard output and then terminate the line.
     */
    public static void println(Object x) {
        out.println(x);
    }

   /**
     * Print a boolean to standard output and then terminate the line.
     */
    public static void println(boolean x) {
        out.println(x);
    }

   /**
     * Print a char to standard output and then terminate the line.
     */
    public static void println(char x) {
        out.println(x);
    }

   /**
     * Print a double to standard output and then terminate the line.
     */
    public static void println(double x) {
        out.println(x);
    }

   /**
     * Print a float to standard output and then terminate the line.
     */
    public static void println(float x) {
        out.println(x);
    }

   /**
     * Print an int to standard output and then terminate the line.
     */
    public static void println(int x) {
        out.println(x);
    }

   /**
     * Print a long to standard output and then terminate the line.
     */
    public static void println(long x) {
        out.println(x);
    }

   /**
     * Flush standard output.
     */
    public static void print() {
        out.flush();
    }

   /**
     * Print an Object to standard output and flush standard output.
     */
    public static void print(Object x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a boolean to standard output and flush standard output.
     */
    public static void print(boolean x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a char to standard output and flush standard output.
     */
    public static void print(char x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a double to standard output and flush standard output.
     */
    public static void print(double x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a float to standard output and flush standard output.
     */
    public static void print(float x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print an int to standard output and flush standard output.
     */
    public static void print(int x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a long to standard output and flush standard output.
     */
    public static void print(long x) {
        out.print(x);
        out.flush();
    }

   /**
     * Print a formatted string to standard output using the specified
     * format string and arguments.
     */
    public static void printf(String format, Object... args) {
        out.printf(format, args);
    }


    // This method is just here to test the class
    public static void main(String[] args) {

        // write to stdout
        StdOut.println("Test");
        StdOut.println(17);
        StdOut.println(true);
        StdOut.printf("%.6f\n", 1.0/7.0);
    }

}
