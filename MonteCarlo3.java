import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class MonteCarlo3 {

	public static void main(String[] args) {
		MonteCarlo3 monteCarlo = new MonteCarlo3();
		Screen screen = new Screen();
		screen.setVisible(true);
		while (true) {
			monteCarlo.addRandomPointInSquare();
			screen.draw(monteCarlo);
		}
	}

	double x, y, nCircle, nSquare;

	Random random = new Random();

	void addRandomPointInSquare() {
		x = 2 * random.nextDouble() - 1;
		y = 2 * random.nextDouble() - 1;
		nSquare = nSquare + 1;
		if (isPointInsideCircle()) {
			nCircle = nCircle + 1;
		}
	}

	boolean isPointInsideCircle() {
		return Math.abs(y) < Math.abs(x);
	}

	double getPointX() {
		return x;
	}

	double getPointY() {
		return y;
	}

	double getPointsInsideSquare() {
		return nSquare;
	}

	double getPointsInsideCircle() {
		return nCircle;
	}

	double getPi() {
		return 4 * nCircle / nSquare;
	}

	static class Screen extends Frame {

		int WIDTH = 600, OFFSET = 50;

		public Screen() {
			super("Monte Carlo");
			setSize(WIDTH + WIDTH, WIDTH + OFFSET * 2);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
		}

		void draw(MonteCarlo3 monteCarlo) {
			Graphics g = getGraphics();
			g.setFont(new Font("Tahoma", Font.PLAIN, 32));
			Color pointColor;
			if (monteCarlo.isPointInsideCircle())
				pointColor = Color.YELLOW;
			else
				pointColor = Color.BLUE;
			g.setColor(pointColor);
			int x = toScreen(monteCarlo.getPointX());
			int y = toScreen(monteCarlo.getPointY());
			int width = 1, height = width;
			g.fillRect(x, y, width, height);
			if (monteCarlo.getPointsInsideSquare() % 100 == 0) {
				g.setColor(Color.WHITE);
				x = WIDTH + OFFSET * 2;
				y = WIDTH / 2 + OFFSET;
				g.fillRect(x, y - OFFSET * 4, WIDTH, OFFSET * 8);
				g.setColor(Color.BLACK);
				double nSquare = monteCarlo.getPointsInsideSquare();
				g.drawString("Pi: " + monteCarlo.getPi(), x, y - OFFSET * 2);
				g.drawString("Points inside square: " + (long) nSquare, x, y);
				g.drawString("Points inside circle: "
						+ (long) monteCarlo.getPointsInsideCircle(), x, y + OFFSET * 2);
				if (nSquare == 400 || nSquare == 4000 || nSquare == 40000
						|| nSquare == 400000 || nSquare == 4000000 || nSquare == 4000000) {
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
					}
				}
			}
			g.dispose();
		}

		int toScreen(double x) {
			return (int) (x * WIDTH / 2 + WIDTH / 2 + OFFSET);
		}
	}
}