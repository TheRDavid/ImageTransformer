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

public class MainWindow extends JFrame {
	private JPanel p = new JPanel() {
		protected void paintComponent(java.awt.Graphics arg0) {
			super.paintComponent(arg0);
			arg0.drawImage(warp, 0, 0, null);
			for (int i = 0; i < 4; i++) {
				Point2D.Double po = points[i];
				arg0.setColor(Color.blue);
				if (i == n)
					arg0.setColor(Color.RED);
				arg0.fillRect((int) po.getX() - 5, (int) po.getY() - 5, 10, 10);
			}
		};
	};
	private BufferedImage warp, original;
	private Point2D.Double points[];
	private int n = 0;

	public MainWindow(BufferedImage i) {
		warp = i;
		original = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
		points = new Point2D.Double[] { new Point2D.Double(0, 0), new Point2D.Double(original.getWidth(), 0), new Point2D.Double(original.getWidth(), original.getHeight()), new Point2D.Double(0, original.getHeight()) };
		Graphics g = original.getGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		add(p, BorderLayout.CENTER);
		p.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				points[n] = new Point2D.Double(arg0.getX(), arg0.getY());
				warp = Start.warp(original, warp, points[0], points[1], points[2], points[3]);
				p.repaint();
				super.mouseDragged(arg0);
			}
		});
		p.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON2)
					n = n == 3 ? 0 : n + 1;
				else if (arg0.getButton() == MouseEvent.BUTTON3) {
					points = new Point2D.Double[] { new Point2D.Double(0, 0), new Point2D.Double(original.getWidth(), 0), new Point2D.Double(original.getWidth(), original.getHeight()), new Point2D.Double(0, original.getHeight()) };
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
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
