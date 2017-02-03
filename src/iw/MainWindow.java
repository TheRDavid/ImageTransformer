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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {
	int handleSize = 16;
	private BufferedImage transformImage, debugImage, original;
	private BuffViewer transformPanel, debugPanel;
	private int n = 0;

	private float distortPoints[][] = new float[4][2];

	private enum modeType {
		DISTORT, EUCLIDEAN
	};

	private JComboBox<modeType> modeCombobox = new JComboBox<>(new modeType[] { modeType.DISTORT, modeType.EUCLIDEAN });

	public MainWindow(BufferedImage i) {
		original = i;
		transformImage = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		debugImage = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		transformPanel = new BuffViewer(transformImage);
		debugPanel = new BuffViewer(debugImage);
		distortPoints[1][0] = i.getWidth();
		distortPoints[2][0] = i.getWidth();
		distortPoints[2][1] = i.getHeight();
		distortPoints[3][1] = i.getHeight();
		Graphics g = transformImage.getGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		getContentPane().setBackground(Color.DARK_GRAY);
		add(transformPanel, BorderLayout.NORTH);
		add(debugPanel, BorderLayout.CENTER);
		add(modeCombobox, BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		repaintEmAll();
	}

	public void repaintEmAll() {
		transformPanel.repaint();
		debugPanel.repaint();
	}

	private class BuffViewer extends JPanel {
		private BufferedImage image;

		public BuffViewer(BufferedImage i) {
			image = i;
			setPreferredSize(new Dimension(i.getWidth(), i.getHeight()));
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent arg0) {
					if (modeCombobox.getSelectedItem().equals(modeType.DISTORT)) {
						distortPoints[n][0] = arg0.getX();
						distortPoints[n][1] = arg0.getY();
						Transformer.distort(original, transformImage, distortPoints);
						Transformer.distort_debug(original, debugImage, distortPoints, 70, 70);
					}
					repaintEmAll();
					super.mouseDragged(arg0);
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (modeCombobox.getSelectedItem().equals(modeType.DISTORT)) {
						if (arg0.getButton() == MouseEvent.BUTTON1) {
							for (int i = 0; i < distortPoints.length; i++)
								if (arg0.getX() >= distortPoints[i][0] - handleSize / 2
										&& arg0.getX() <= distortPoints[i][0] + handleSize / 2
										&& arg0.getY() >= distortPoints[i][1] - handleSize / 2
										&& arg0.getY() <= distortPoints[i][1] + handleSize / 2)
									n = i;
						} else if (arg0.getButton() == MouseEvent.BUTTON3) {
							distortPoints = new float[4][2];
							distortPoints[1][0] = i.getWidth();
							distortPoints[2][0] = i.getWidth();
							distortPoints[2][1] = i.getHeight();
							distortPoints[3][1] = i.getHeight();
							Graphics g = transformImage.getGraphics();
							g.drawImage(original, 0, 0, null);
							g.dispose();
						}
					}
					repaintEmAll();
					super.mouseReleased(arg0);
				}
			});
		}

		protected void paintComponent(java.awt.Graphics arg0) {
			super.paintComponent(arg0);
			arg0.drawImage(image, 0, 0, null);
			if (modeCombobox.getSelectedItem().equals(modeType.DISTORT)) {
				for (int i = 0; i < distortPoints.length; i++) {
					arg0.setColor(Color.blue);
					if (i == n)
						arg0.setColor(Color.YELLOW);
					arg0.fillOval((int) distortPoints[i][0] - handleSize / 2,
							(int) distortPoints[i][1] - handleSize / 2, handleSize, handleSize);
				}
			}
		};

	}
}
