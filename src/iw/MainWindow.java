package iw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {
	int handleSize = 16;
	private BufferedImage warpForward, warpForwardDebug, warpBackward, warpBackwardDebug, original;
	private BuffViewer warpForwardView, warpBackwardView, warpForwardDebugView, warpBackwardDebugView;
	private float points[][] = new float[4][2];
	private int n = 0;

	public MainWindow(BufferedImage i) {
		original = i;
		warpBackward = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		warpForward = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		warpBackwardDebug = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		warpForwardDebug = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		warpForwardView = new BuffViewer(warpForward);
		warpForwardDebugView = new BuffViewer(warpForwardDebug);
		warpBackwardView = new BuffViewer(warpBackward);
		warpBackwardDebugView = new BuffViewer(warpBackwardDebug);
		points[1][0] = i.getWidth();
		points[2][0] = i.getWidth();
		points[2][1] = i.getHeight();
		points[3][1] = i.getHeight();
		Graphics g = warpForward.getGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		g = warpBackward.getGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		setLayout(new GridLayout(2, 0,10,10));
		getContentPane().setBackground(Color.DARK_GRAY);
		add(warpForwardView);
		add(warpBackwardView);
		add(warpForwardDebugView);
		add(warpBackwardDebugView);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		repaintEmAll();
	}

	public void repaintEmAll() {
		warpForwardView.repaint();
		warpForwardDebugView.repaint();
		warpBackwardView.repaint();
		warpBackwardDebugView.repaint();
	}

	private class BuffViewer extends JPanel {
		private BufferedImage image;

		public BuffViewer(BufferedImage i) {
			image = i;
			setPreferredSize(new Dimension(i.getWidth(), i.getHeight()));
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent arg0) {
					// TODO Auto-generated method stub
					points[n][0] = arg0.getX();
					points[n][1] = arg0.getY();
					Transformer.warp_forward(original, warpForward, points);
					Transformer.warp_forward_debug(original, warpForwardDebug, points,100,100);
					Transformer.warp_backward(original, warpBackward, points);
					Transformer.warp_backward_debug(original, warpBackwardDebug, points,100,100);
					repaintEmAll();
					super.mouseDragged(arg0);
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (arg0.getButton() == MouseEvent.BUTTON1) {
						for (int i = 0; i < points.length; i++)
							if (arg0.getX() >= points[i][0] - handleSize / 2 && arg0.getX() <= points[i][0] + handleSize / 2
									&& arg0.getY() >= points[i][1] - handleSize / 2
									&& arg0.getY() <= points[i][1] + handleSize / 2)
								n = i;
					} else if (arg0.getButton() == MouseEvent.BUTTON3) {
						points = new float[4][2];
						points[1][0] = i.getWidth();
						points[2][0] = i.getWidth();
						points[2][1] = i.getHeight();
						points[3][1] = i.getHeight();
						Graphics g = warpForward.getGraphics();
						g.drawImage(original, 0, 0, null);
						g.dispose();
					}
					// TODO Auto-generated method stub
					repaintEmAll();
					super.mouseReleased(arg0);
				}
			});
		}

		protected void paintComponent(java.awt.Graphics arg0) {
			super.paintComponent(arg0);
			arg0.drawImage(image, 0, 0, null);
			for (int i = 0; i < points.length; i ++) {
				arg0.setColor(Color.blue);
				if (i == n)
					arg0.setColor(Color.YELLOW);
				arg0.fillOval((int) points[i][0] - handleSize / 2, (int) points[i][1] - handleSize / 2, handleSize,
						handleSize);
			}
		};

	}
}
