import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class LineBase {
	public abstract void twoPointForm(int x0, int y0, int x1, int y1, int framebuffer[][]) throws NullPointerException, ArrayIndexOutOfBoundsException;

	public abstract void parametricForm(int x0, int y0, int x1, int y1, int framebuffer[][]) throws NullPointerException, ArrayIndexOutOfBoundsException;

	public abstract void BresenhamForm(int x0, int y0, int x1, int y1, int framebuffer[][]) throws NullPointerException, ArrayIndexOutOfBoundsException;

	public abstract void BresenhamFormRGB(int x0, int y0, int x1, int y1, int framebuffer[][][], RGBColor C0, RGBColor C1) throws NullPointerException, ArrayIndexOutOfBoundsException;
	public abstract void BresenhamFormRGB2(int x0, int y0, int x1, int y1, int framebuffer[][][], RGBColor C0, RGBColor C1) throws NullPointerException, ArrayIndexOutOfBoundsException;

	public static class RGBColor {
		public int R, G, B;
		public RGBColor() {

		}
		public RGBColor(int R, int G, int B) {
			this.R = R;
			this.G = G;
			this.B = B;
		}
		
	}


	public static void ImageWriteRGB(int img[][][], String filename) throws IOException
	{
		try {
			BufferedImage bi = new BufferedImage(img[0][0].length, img[0].length, BufferedImage.TYPE_INT_RGB);

			// -- prepare output image
			for (int i = 0; i < bi.getHeight(); ++i) {
				for (int j = 0; j < bi.getWidth(); ++j) {
					int pixel =	(img[0][i][j] << 16) | (img[1][i][j] << 8) | (img[2][i][j]);
					bi.setRGB(j, i, pixel);
				}
			}

			// -- write output image
			File outputfile = new File(filename);
			ImageIO.write(bi, "png", outputfile);
//			System.out.println("Render Success");
		}
		catch (IOException e) {
			throw e;
		}
	}


	public static void ImageWrite(int img[][], String filename) throws IOException
	{
		try {
			BufferedImage bi = new BufferedImage(img[0].length, img.length, BufferedImage.TYPE_INT_RGB);
	    	
	    	// -- prepare output image
	    	for (int i = 0; i < bi.getHeight(); ++i) {
	    	    for (int j = 0; j < bi.getWidth(); ++j) {
	    			int pixel =	(img[i][j] << 16) | (img[i][j] << 8) | (img[i][j]);
//	    			int pixel =	((int)(Math.random() * 255) << 16) | (img[i][j] << 8) | (img[i][j]);
	    			bi.setRGB(j, i, pixel);
	    		}
	    	}
	
	    	// -- write output image
	    	File outputfile = new File(filename);
	    	ImageIO.write(bi, "png", outputfile);	
		}
		catch (IOException e) {
			throw e;
		}
	}

	public static void main (String[] args) {
		LineBase lb = new Lines();

		{
			int framebuffer[][] = new int[256][256];
			try {
				for (int x = 0; x < framebuffer[0].length; x += 13) {
					lb.twoPointForm(x, 0, framebuffer[0].length - x - 1, framebuffer.length - 1, framebuffer);
				}
				for (int y = 0; y < framebuffer.length; y += 13) {
					lb.twoPointForm(0, y, framebuffer[0].length - 1, framebuffer.length - y - 1, framebuffer);
				}
				LineBase.ImageWrite(framebuffer, "twopoint.png");
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
		{
			int framebuffer[][] = new int[256][256];
			try {
				for (int x = 0; x < framebuffer[0].length; x += 13) {
					lb.parametricForm(x, 0, framebuffer[0].length - x - 1, framebuffer.length - 1, framebuffer);
				}
				for (int y = 0; y < framebuffer.length; y += 13) {
					lb.parametricForm(0, y, framebuffer[0].length - 1, framebuffer.length - y - 1, framebuffer);
				}
				LineBase.ImageWrite(framebuffer, "parametric.png");
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}

		{
			int framebuffer[][] = new int[256][256];
			try {
				for (int x = 0; x < framebuffer[0].length; x += 13) {
					lb.BresenhamForm(x, 0, framebuffer[0].length - x - 1, framebuffer.length - 1, framebuffer);
				}
				for (int y = 0; y < framebuffer.length; y += 13) {
					lb.BresenhamForm(0, y, framebuffer[0].length - 1, framebuffer.length - y - 1, framebuffer);
				}
				LineBase.ImageWrite(framebuffer, "bresenham.png");
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}

		{
			int framebuffer[][][] = new int[3][256][256];
			RGBColor c0 = new RGBColor(255, 0, 0);
			RGBColor c1 = new RGBColor(255, 255, 0);
			try {
				for (int x = 0; x < framebuffer[0][0].length; x += 13) {
					lb.BresenhamFormRGB(x, 0, framebuffer[0][0].length - x - 1, framebuffer[0].length - 1, framebuffer, c0, c1);
				}
				for (int y = 0; y < framebuffer[0].length; y += 13) {
					lb.BresenhamFormRGB(0, y, framebuffer[0][0].length - 1, framebuffer[0].length - y - 1, framebuffer, c0, c1);
				}
				LineBase.ImageWriteRGB(framebuffer, "bresenhamRGB.png");
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
	}


}