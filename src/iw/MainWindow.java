package iw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {
	int handleSize = 16;
	private JPanel p = new JPanel() {
		protected void paintComponent(java.awt.Graphics arg0) {
			super.paintComponent(arg0);
			arg0.drawImage(warp, 0, 0, null);
			for (int i = 0; i < points.length; i += 2) {
				arg0.setColor(Color.blue);
				if (i == n)
					arg0.setColor(Color.RED);
				arg0.fillRect((int) points[i] - handleSize / 2, (int) points[i + 1] - handleSize / 2, handleSize,
						handleSize);
			}
		};
	};
	private BufferedImage warp, original;
	private float points[] = new float[8];
	private int n = 0;

	public MainWindow(BufferedImage i) {
		warp = i;
		original = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		points[2] = i.getWidth();
		points[4] = i.getWidth();
		points[5] = i.getHeight();
		points[7] = i.getHeight();
		Graphics g = original.getGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		add(p, BorderLayout.CENTER);
		p.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				points[n] = arg0.getX();
				points[n + 1] = arg0.getY();
				Start.warp(original, warp, points[0], points[1], points[2], points[3], points[4], points[5],
						points[6], points[7]);
				p.repaint();
				super.mouseDragged(arg0);
			}
		});
		p.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					for (int i = 0; i < points.length; i += 2)
						if (arg0.getX() >= points[i] - handleSize / 2 && arg0.getX() <= points[i] + handleSize / 2
								&& arg0.getY() >= points[i + 1] - handleSize / 2
								&& arg0.getY() <= points[i + 1] + handleSize / 2)
							n = i;
				} else if (arg0.getButton() == MouseEvent.BUTTON3) {
					points = new float[8];
					Graphics g = warp.getGraphics();
					g.drawImage(original, 0, 0, null);
					g.dispose();
				}
				// TODO Auto-generated method stub
				p.repaint();
				super.mouseReleased(arg0);
			}
		});
		setSize(original.getWidth() + 100, original.getHeight() + 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		p.setBorder(new EmptyBorder(50, 50, 50, 50));
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
