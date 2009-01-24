package dolmisani.toys.fractals;
/*************************************************************************
 *  Compilation:  javac Mandelbrot.java
 *  Execution:    java Mandelbrot xc yc size
 *  Dependencies: StdDraw.java
 *
 *  Plots the size-by-size region of the Mandelbrot set, centered on (xc, yc)
 *
 *  % java Mandelbrot -.5 0 2
 *
 *************************************************************************/

import java.awt.Color;

import dolmisani.toys.utils.Picture;
import dolmisani.toys.utils.Stopwatch;

public class Mandelbrot {

	
	public static final Complex f(Complex z, Complex z0) {
		
		double re = (z.re*z.re-z.im*z.im) + z0.re;
		double im = (2*z.re*z.im) + z0.im;			
		
		//z.re = re;
		//z.im = im;
		
		return new Complex(re, im);
		//return z;
	}
	
	public static final double mod2(Complex z) {
		
		return z.re*z.re + z.im*z.im;
	}
	
	
    // return number of iterations to check if c = a + ib is in Mandelbrot set
    public static int mand(Complex z0, int max) {
    	
        Complex z = z0;
        for (int t = 0; t < max; t++) {
        	
            if (mod2(z) > 4.0) return t;
            
            z = f(z, z0);
            
        }
        return max;
    }

    public static void main(String[] args)  {
    	
    	
    	Stopwatch sw = new Stopwatch();
    	
        //double xc   = Double.parseDouble(args[0]);
        //double yc   = Double.parseDouble(args[1]);
        //double size = Double.parseDouble(args[2]);

        double xc   = -.5;
        double yc   = 0;
        double size = 2;

    	
        int N   = 512;   // create N-by-N image
        int max = 255;   // maximum number of iterations

        Picture pic = new Picture(N, N);
        
        double startX = xc - size/2.0;
        double startY = yc - size/2.0;
        
        double deltaX = size / N;
        double deltaY = size / N;
        
        Color[] palette = new Color[256];
        for(int c=0; c<palette.length; c++) {
        	palette[c] = new Color(c, c, c);
        }
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
            	
                double x0 = startX + i*deltaX;
                double y0 = startY + j*deltaY;
                
                Complex z0 = new Complex(x0, y0);
                int gray = max - mand(z0, max);
                pic.set(i, N-1-j, palette[gray]);
            }
        }
        
        System.out.println(sw.elapsedTime());
        
        pic.show();
    }
    
       
    public static final class Complex {
    	
    	public double re;
    	public double im;
    	
    	public Complex(double re, double im) {
    		
    		this.re = re;
    		this.im = im;
    	}
    	
    	public String toString() {
    		
    		return String.format("%f + %fi", re, im);
    	}
    	
    	
    }
    
}
