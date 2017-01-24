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
	private static int[] i = new int[3];
	private static WritableRaster inRaster, outRaster;

	public static void warp_forward(BufferedImage original, BufferedImage warp,float[][] coords) {
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

	public static void warp_forward_debug(BufferedImage original, BufferedImage warp, float[][] coords, int px, int py) {
		Graphics g = warp.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear
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
		g.drawLine((int) x_start, (int) y_new, (int) x_end, (int) y_new);
		g.drawLine((int) x_new, (int) y_start, (int) x_new, (int) y_end);
		g.drawLine((int) coords[0][0], (int) coords[0][1], (int) coords[1][0], (int) coords[1][1]);
		g.drawLine((int) coords[1][0], (int) coords[1][1], (int) coords[2][0], (int) coords[2][1]);
		g.drawLine((int) coords[2][0], (int) coords[2][1], (int) coords[3][0], (int) coords[3][1]);
		g.drawLine((int) coords[3][0], (int) coords[3][1], (int) coords[0][0], (int) coords[0][1]);

		g.setColor(Color.yellow);
		g.drawLine((int) px, (int) py, (int) x_new, (int) y_new);
		g.fillRect((int) x_new - 4, (int) y_new - 4, 8, 8);
		g.drawRect((int) px - 4, (int) py - 4, 8, 8);
	}

	public static void warp_backward(BufferedImage original, BufferedImage warp, float[][] coords) {
		Graphics g = warp.getGraphics();
		g.clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear
		inRaster = original.getRaster();
		outRaster = warp.getRaster();

		/*	
		 * when left_length > right_length
			1. for each left_y on left_side, find y on right side
				1.1 find y_share
				1.2 right_y = y_share times right_length + by
				1.3 find x on left side
					ad_diffx = abs(ax-dx)
					left_x = y_share * ad_diffx + min(ax, dx)
					bc_diffx = abs(cx-bx)
					right_x = y_share * bc_diffx + min(bx, cx)
			2. travel between the 2 points
		*/

		//System.out.println((System.currentTimeMillis()-start)/1000);
	}

	public static void warp_backward_debug(BufferedImage original, BufferedImage warp, float[][] coords, int px, int py) {
		Graphics g = warp.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, warp.getWidth(), warp.getHeight()); // clear
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f);
		g2d.setComposite(ac);
		g2d.drawImage(original, 0, 0, null);
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(ac);
		
		g.drawLine((int) coords[0][0], (int) coords[0][1], (int) coords[1][0], (int) coords[1][1]);
		g.drawLine((int) coords[1][0], (int) coords[1][1], (int) coords[2][0], (int) coords[2][1]);
		g.drawLine((int) coords[2][0], (int) coords[2][1], (int) coords[3][0], (int) coords[3][1]);
		g.drawLine((int) coords[3][0], (int) coords[3][1], (int) coords[0][0], (int) coords[0][1]);
		
		float[][] coords_sorted_by_x = new float[4][2];
		
		
		/*	
		 * when left_length > right_length
			1. for each left_y on left_side, find y on right side
				1.1 find y_share
				1.2 right_y = y_share times right_length + by
				1.3 find x on left side
					ad_diffx = abs(ax-dx)
					left_x = y_share * ad_diffx + min(ax, dx)
					bc_diffx = abs(cx-bx)
					right_x = y_share * bc_diffx + min(bx, cx)
			2. travel between the 2 points
		*/

		//System.out.println((System.currentTimeMillis()-start)/1000);
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
