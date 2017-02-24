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

	public static void scale_nn_forward(BufferedImage original, BufferedImage transformImage, float[] scalePoints) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		for (int x = 0; x < original.getWidth(); x++) {
			int xn = (int) (scalePoints[0] * x);
			if (xn >= 0 && xn < original.getWidth())
				for (int y = 0; y < original.getHeight(); y++) {
					int yn = (int) (scalePoints[1] * y);
					if (yn >= 0 && yn < original.getHeight())
						outRaster.setPixel(xn, yn, inRaster.getPixel(x, y, i));
				}
		}
	}

	public static void scale_nn_backward(BufferedImage original, BufferedImage transformImage, float[] scalePoints) {
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

	public static void scale_bl_backward(BufferedImage original, BufferedImage transformImage, float[] scalePoints) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		int w = (int) (original.getWidth() * scalePoints[0]);
		int h = (int) (original.getHeight() * scalePoints[1]);
		float nx = 1 / scalePoints[0];
		float ny = 1 / scalePoints[1];
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		for (int x = 0; x < w; x++) {
			float oldCol = nx * x;
			int leftCol = (int) Math.floor(oldCol);
			leftCol = leftCol >= 0 ? leftCol : 0;
			leftCol = leftCol < original.getWidth() ? leftCol : original.getWidth() - 1;
			int rightCol = (int) Math.ceil(oldCol);
			rightCol = rightCol < original.getWidth() ? rightCol : original.getWidth() - 1;
			rightCol = rightCol >= 0 ? rightCol : 0;
			double rightShare = oldCol % 1;
			double leftShare = (1 - rightShare);
			if (x >= 0 && x < original.getWidth())
				for (int y = 0; y < h; y++) {
					float oldRow = ny * y;
					double lowerShare = oldRow % 1;
					double upperShare = (1 - lowerShare);
					int upperRow = (int) Math.floor(oldRow);
					upperRow = upperRow >= 0 ? upperRow : 0;
					upperRow = upperRow < original.getHeight() ? upperRow : original.getHeight() - 1;
					int lowerRow = (int) Math.ceil(oldRow);
					lowerRow = lowerRow < original.getHeight() ? lowerRow : original.getHeight() - 1;
					lowerRow = lowerRow >= 0 ? lowerRow : 0;

					int interpolatedColor[] = new int[i.length];
					for (int ink = 0; ink < i.length; ink++) {
						interpolatedColor[ink] += inRaster.getPixel(leftCol, upperRow, i)[ink] * upperShare * leftShare;
						interpolatedColor[ink] += inRaster.getPixel(rightCol, upperRow, i)[ink] * upperShare
								* rightShare;
						interpolatedColor[ink] += inRaster.getPixel(leftCol, lowerRow, i)[ink] * lowerShare * leftShare;
						interpolatedColor[ink] += inRaster.getPixel(rightCol, lowerRow, i)[ink] * lowerShare
								* rightShare;
					}

					if (y >= 0 && y < original.getHeight())
						outRaster.setPixel(x, y, interpolatedColor);
				}
		}
	}

	public static void rotate_nn_forward(BufferedImage original, BufferedImage transformImage, float angle) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		int widthDivBy2 = original.getWidth() / 2;
		int heightDivBy2 = original.getHeight() / 2;
		double sDeg = Math.sin(angle), cDeg = Math.cos(angle);
		for (int x = 0; x < original.getWidth(); x++) {
			int xt = x - widthDivBy2;
			for (int y = 0; y < original.getHeight(); y++) {
				int yt = y - heightDivBy2;

				int xn = (int) Math.round(xt * cDeg - yt * sDeg) + widthDivBy2; // calculate the new COORDINATES
				int yn = (int) Math.round(xt * sDeg + yt * cDeg) + heightDivBy2;

				if (xn >= 0 && xn < original.getWidth() && yn >= 0 && yn < original.getHeight())
					outRaster.setPixel(xn, yn, inRaster.getPixel(x, y, i));
			}
		}
	}

	public static void rotate_nn_backward(BufferedImage original, BufferedImage transformImage, float angle) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		int widthDivBy2 = original.getWidth() / 2;
		int heightDivBy2 = original.getHeight() / 2;
		double sDeg = Math.sin(angle), cDeg = Math.cos(angle);
		for (int x = 0; x < original.getWidth(); x++) {
			int xt = x - widthDivBy2;
			for (int y = 0; y < original.getHeight(); y++) {
				int yt = y - heightDivBy2;

				int xn = (int) Math.round(xt * cDeg - yt * sDeg) + widthDivBy2; // calculate the new COORDINATES
				int yn = (int) Math.round(xt * sDeg + yt * cDeg) + heightDivBy2;

				if (xn >= 0 && xn < original.getWidth() && yn >= 0 && yn < original.getHeight())
					outRaster.setPixel(x, y, inRaster.getPixel(xn, yn, i));
			}
		}
	}

	public static void rotate_bil_backward(BufferedImage original, BufferedImage transformImage, float angle) {
		Graphics g = transformImage.getGraphics();
		g.clearRect(0, 0, transformImage.getWidth(), transformImage.getHeight()); // clear
		inRaster = original.getRaster();
		outRaster = transformImage.getRaster();
		int widthDivBy2 = original.getWidth() / 2;
		int heightDivBy2 = original.getHeight() / 2;
		double sDeg = Math.sin(angle), cDeg = Math.cos(angle);
		for (int x = 0; x < original.getWidth(); x++) {
			int xt = x - widthDivBy2;
			for (int y = 0; y < original.getHeight(); y++) {
				int yt = y - heightDivBy2;

				double xn =  xt * cDeg - yt * sDeg + widthDivBy2; // calculate the new COORDINATES
				double yn = xt * sDeg + yt * cDeg + heightDivBy2;
				
				//interpolate

				int leftCol = (int) Math.floor(xn);
				leftCol = leftCol >= 0 ? leftCol : 0;
				leftCol = leftCol < original.getWidth() ? leftCol : original.getWidth() - 1;
				int rightCol = (int) Math.ceil(xn);
				rightCol = rightCol < original.getWidth() ? rightCol : original.getWidth() - 1;
				rightCol = rightCol >= 0 ? rightCol : 0;
				double rightShare =  xn % 1;
				double leftShare = (1 - rightShare);

				double lowerShare = yn % 1;
				double upperShare = (1 - lowerShare);
				int upperRow = (int) Math.floor(yn);
				upperRow = upperRow >= 0 ? upperRow : 0;
				upperRow = upperRow < original.getHeight() ? upperRow : original.getHeight() - 1;
				int lowerRow = (int) Math.ceil(yn);
				lowerRow = lowerRow < original.getHeight() ? lowerRow : original.getHeight() - 1;
				lowerRow = lowerRow >= 0 ? lowerRow : 0;
				
				int interpolatedColor[] = new int[i.length];
				for (int ink = 0; ink < i.length; ink++) {
					interpolatedColor[ink] += inRaster.getPixel(leftCol, upperRow, i)[ink] * upperShare * leftShare;
					interpolatedColor[ink] += inRaster.getPixel(rightCol, upperRow, i)[ink] * upperShare
							* rightShare;
					interpolatedColor[ink] += inRaster.getPixel(leftCol, lowerRow, i)[ink] * lowerShare * leftShare;
					interpolatedColor[ink] += inRaster.getPixel(rightCol, lowerRow, i)[ink] * lowerShare
							* rightShare;
				}

				if (xn >= 0 && xn < original.getWidth() && yn >= 0 && yn < original.getHeight())
					outRaster.setPixel(x, y, interpolatedColor);
			}
		}
	}
}