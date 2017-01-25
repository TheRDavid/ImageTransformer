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

	public static void warp_forward(BufferedImage original, BufferedImage warp, float[][] coords) {
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

	public static void warp_forward_debug(BufferedImage original, BufferedImage debug, float[][] coords, int px,
			int py) {
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

	public static void warp_backward_debug(BufferedImage original, BufferedImage debug, float[][] coords, int px,
			int py) {
		Graphics g = debug.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g.clearRect(0, 0, debug.getWidth(), debug.getHeight()); // clear
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f);
		g2d.setComposite(ac);
		g2d.drawImage(original, 0, 0, null);
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(ac);

		g.drawLine((int) coords[0][0], (int) coords[0][1], (int) coords[1][0], (int) coords[1][1]);
		g.drawLine((int) coords[1][0], (int) coords[1][1], (int) coords[2][0], (int) coords[2][1]);
		g.drawLine((int) coords[2][0], (int) coords[2][1], (int) coords[3][0], (int) coords[3][1]);
		g.drawLine((int) coords[3][0], (int) coords[3][1], (int) coords[0][0], (int) coords[0][1]);

		g.setColor(Color.orange);
		float[][] sortedCoords = simpleSort(coords, 0); // sorted left to right, order stored with chars

		for (int i = 0; i < sortedCoords.length; i++) {
			g2d.drawString((char) sortedCoords[i][2] + "", sortedCoords[i][0] + 20, sortedCoords[i][1] + 10);
		}

		float[][] neighbours = get_neighbours(sortedCoords, 0);
		System.out.println("For " + (char) sortedCoords[0][2] + ": Neighbour 1: " + (char) neighbours[0][2] + ", 2: "
				+ (char) neighbours[1][2]);

		g.setColor(Color.red);
		
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

	private static float[][] get_neighbours(float[][] coords, int idx) {
		float[][] neighbours = new float[2][3];
		char c = (char) coords[idx][2];
		if (c == 'A') {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i][2] == 'D') {
					neighbours[0][0] = coords[i][0];
					neighbours[0][1] = coords[i][1];
					neighbours[0][2] = 'D';
				} else if (coords[i][2] == 'B') {
					neighbours[1][0] = coords[i][0];
					neighbours[1][1] = coords[i][1];
					neighbours[1][2] = 'B';
				}
			}
		} else if (c == 'B') {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i][2] == 'A') {
					neighbours[0][0] = coords[i][0];
					neighbours[0][1] = coords[i][1];
					neighbours[0][2] = 'A';
				} else if (coords[i][2] == 'C') {
					neighbours[1][0] = coords[i][0];
					neighbours[1][1] = coords[i][1];
					neighbours[1][2] = 'C';
				}
			}
		} else if (c == 'C') {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i][2] == 'B') {
					neighbours[0][0] = coords[i][0];
					neighbours[0][1] = coords[i][1];
					neighbours[0][2] = 'B';
				} else if (coords[i][2] == 'D') {
					neighbours[1][0] = coords[i][0];
					neighbours[1][1] = coords[i][1];
					neighbours[1][2] = 'D';
				}
			}
		} else {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i][2] == 'C') {
					neighbours[0][0] = coords[i][0];
					neighbours[0][1] = coords[i][1];
					neighbours[0][2] = 'C';
				} else if (coords[i][2] == 'A') {
					neighbours[1][0] = coords[i][0];
					neighbours[1][1] = coords[i][1];
					neighbours[1][2] = 'A';
				}
			}
		}
		return neighbours;
	}

	private static float[][] simpleSort(float[][] coords, int idx) {
		float[][] newCoords = new float[coords.length][3];
		for (int i = 0; i < coords.length; i++) {
			newCoords[i][0] = coords[i][0];
			newCoords[i][1] = coords[i][1];
			newCoords[i][2] = i + 65;
		}
		for (int i = 0; i < coords.length; i++) {
			for (int j = i; j < coords.length; j++) {
				if (newCoords[i][idx] > newCoords[j][idx]) {
					float temp0 = newCoords[i][0];
					float temp1 = newCoords[i][1];
					float temp2 = newCoords[i][2];
					newCoords[i][0] = newCoords[j][0];
					newCoords[i][1] = newCoords[j][1];
					newCoords[i][2] = newCoords[j][2];
					newCoords[j][0] = temp0;
					newCoords[j][1] = temp1;
					newCoords[j][2] = temp2;
				}
			}
		}
		return newCoords;
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
