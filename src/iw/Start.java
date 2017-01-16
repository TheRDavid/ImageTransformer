package iw;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Start {

	public static BufferedImage warp(BufferedImage original, BufferedImage warp, Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D) {
		warp.getGraphics().clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear
		for (int x = 0; x < original.getWidth(); x++) {

			double original_x_share = (double) x / original.getWidth(); // how far on x axis (of original 0-1)
			double original_x_share_complement = (1 - original_x_share); // diff of 1 and original_x_share

			double y_start = (A.y * original_x_share_complement + B.y * original_x_share); // most left y coordiante
			double y_end = (D.y * original_x_share_complement + C.y * original_x_share); // most right y coordinate

			for (int y = 0; y < original.getHeight(); y++) {
				double original_y_share = (double) y / original.getHeight(); // how far on y axis (of original, 0-1)

				double original_y_share_complement = (1 - original_y_share); // diff of 1 and original_y_share

				double x_start = (A.x * original_y_share_complement + D.x * original_y_share); // most top x coordinate
				double x_end = (B.x * original_y_share_complement + C.x * original_y_share); // most bottom x coordiante

				double x_length = x_end - x_start;
				double y_length = y_end - y_start;

				double x0 = x_start + original_x_share * x_length; // position on warped x axis
				double y0 = y_start + original_y_share * y_length; // position on warped y axis
				try {
					warp.setRGB((int) (x0), (int) (y0), original.getRGB(x, y)); // waaaaaarp
				} catch (Exception e) {
					// whatevs
				}
			}
		}
		return warp;
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
