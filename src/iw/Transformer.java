package iw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Transformer {
	private static int[] i = new int[4];
	private static WritableRaster inRaster, outRaster;

	public static void distort(BufferedImage original, BufferedImage warp, float[][] coords) {
		Graphics g = warp.getGraphics();
		g.clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear

		inRaster = original.getRaster();
		outRaster = warp.getRaster();

		double start = System.currentTimeMillis();

		for (int x = 0; x < original.getWidth(); x++) {

			float x_share = (float) (x) / original.getWidth(); // how far on x axis (of original 0-1)
			float x_share_comp = (1 - x_share); // diff of 1 and original_x_share

			float y_start = (coords[0][1] * x_share_comp + coords[1][1] * x_share); // most left y coordiante
			float y_end = (coords[3][1] * x_share_comp + coords[2][1] * x_share); // most right y coordinate

			for (int y = 0; y < original.getHeight(); y++) {
				float y_share = (float) (y) / original.getHeight(); // how far on y axis (of original, 0-1)
				float y_share_comp = (1 - y_share); // diff of 1 and original_y_share

				float x_start = (coords[0][0] * y_share_comp + coords[3][0] * y_share); // most top x coordinate
				float x_end = (coords[1][0] * y_share_comp + coords[2][0] * y_share); // most bottom x coordiante

				float x_length = x_end - x_start;
				float y_length = y_end - y_start;

				float x_new = x_start + x_share * x_length; // position on warped x axis
				float y_new = y_start + y_share * y_length; // position on warped y axis
				if (x_new >= 0 && x_new < original.getWidth() && y_new >= 0 && y_new < original.getHeight())
					outRaster.setPixel((int) x_new, (int) y_new, inRaster.getPixel(x, y, i));
			}
		}
		System.out.println("warp_foward: " + (System.currentTimeMillis() - start) / 1000);
	}

	public static void distort_debug(BufferedImage original, BufferedImage debug, float[][] coords, int px, int py) {
		Graphics g = debug.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, debug.getWidth(), debug.getHeight()); // clear
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f);
		g2d.setComposite(ac);
		g2d.drawImage(original, 0, 0, null);
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(ac);

		int x = px;

		float x_share = (float) (x) / original.getWidth(); // how far on x axis (of original 0-1)
		float x_share_comp = (1 - x_share); // diff of 1 and original_x_share

		float y_start = (coords[0][1] * x_share_comp + coords[1][1] * x_share); // most left y coordiante
		float y_end = (coords[3][1] * x_share_comp + coords[2][1] * x_share); // most right y coordinate	

		int y = py;
		float y_share = (float) (y) / original.getHeight(); // how far on y axis (of original, 0-1)
		float y_share_comp = (1 - y_share); // diff of 1 and original_y_share

		float x_start = (coords[0][0] * y_share_comp + coords[3][0] * y_share); // most top x coordinate
		float x_end = (coords[1][0] * y_share_comp + coords[2][0] * y_share); // most bottom x coordiante

		float x_length = x_end - x_start;
		float y_length = y_end - y_start;

		float x_new = x_start + x_share * x_length; // position on warped x axis
		float y_new = y_start + y_share * y_length; // position on warped y axis

		g.setColor(Color.white);
		g.drawLine((int) (coords[0][0] + (coords[1][0] - coords[0][0]) * x_share), (int) y_start,
				(int) (coords[3][0] + (coords[2][0] - coords[3][0]) * x_share), (int) y_end);
		g.drawLine((int) x_start, (int) (coords[0][1] + (coords[3][1] - coords[0][1]) * y_share), (int) x_end,
				(int) (coords[1][1] + (coords[2][1] - coords[1][1]) * y_share));
		g.drawLine((int) coords[0][0], (int) coords[0][1], (int) coords[1][0], (int) coords[1][1]);
		g.drawLine((int) coords[1][0], (int) coords[1][1], (int) coords[2][0], (int) coords[2][1]);
		g.drawLine((int) coords[2][0], (int) coords[2][1], (int) coords[3][0], (int) coords[3][1]);
		g.drawLine((int) coords[3][0], (int) coords[3][1], (int) coords[0][0], (int) coords[0][1]);

		g.setColor(Color.yellow);
		g.drawLine((int) px, (int) py, (int) x_new, (int) y_new);
		g.fillRect((int) x_new - 4, (int) y_new - 4, 8, 8);
		g.drawRect((int) px - 4, (int) py - 4, 8, 8);
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

	public static void scale(BufferedImage original, BufferedImage transformImage, float[] scalePoints) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		int w = (int) (original.getWidth() * scalePoints[0]);
		int h = (int) (original.getHeight() * scalePoints[1]);
		float nx = 1 / scalePoints[0];
		float ny = 1 / scalePoints[1];
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		for (int x = 0; x < w; x++) {
			int xn = (int) (x * nx);
			if (x >= 0 && x < original.getWidth())
				for (int y = 0; y < h; y++) {
					if (y >= 0 && y < original.getHeight())
						outRaster.setPixel(x, y, inRaster.getPixel(xn, (int) (y * ny), i));
				}
		}
	}

	public static void scale(BufferedImage original, BufferedImage debugImage, float[] scalePoints, int j, int k) {
		// TODO Auto-generated method stub

	}
}