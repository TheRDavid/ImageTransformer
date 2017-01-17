package iw;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Start {
	private static int[] i = new int[3];
	private static WritableRaster inRaster, outRaster;
	public static void warp(BufferedImage original, BufferedImage warp, float ax, float ay, 
			float bx, float by,float cx, float cy,float dx, float dy) {
		Graphics g = warp.getGraphics();
		g.clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear
		
		inRaster  = original.getRaster();
		outRaster = warp.getRaster();
	  
		for (int x = 0; x < original.getWidth(); x++) {

			float x_share = (float) (x) / original.getWidth(); // how far on x axis (of original 0-1)
			float x_share_comp = (1 - x_share); // diff of 1 and original_x_share

			float y_start = (ay * x_share_comp + by * x_share); // most left y coordiante
			float y_end = (dy * x_share_comp + cy * x_share); // most right y coordinate

			for (int y = 0; y < original.getHeight(); y++) {
				float y_share = (float) (y) / original.getHeight(); // how far on y axis (of original, 0-1)
				float y_share_comp = (1 - y_share); // diff of 1 and original_y_share

				float x_start = (ax * y_share_comp + dx * y_share); // most top x coordinate
				float x_end = (bx * y_share_comp + cx * y_share); // most bottom x coordiante

				float x_length = x_end - x_start;
				float y_length = y_end - y_start;

				float x_new = x_start + x_share * x_length; // position on warped x axis
				float y_new = y_start + y_share * y_length; // position on warped y axis
				try {
					outRaster.setPixel((int)x_new, (int) y_new, inRaster.getPixel(x, y, i));
				} catch (Exception e) {
					// whatevs
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			BufferedImage original = ImageIO.read(new File("image.png"));
			new MainWindow(original);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
