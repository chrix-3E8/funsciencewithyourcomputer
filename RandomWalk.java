import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class RandomWalk {

	public static void main(String[] args) throws Exception {
		int steps = 1000000;
		Screen screen = new Screen(steps);
		screen.setVisible(true);
		double theoreticalDistance = Math.sqrt(steps);
		double sum = 0;
		for (int i = 1; i <= 100; i++) {
			RandomWalk randomWalk = new RandomWalk();
			double measuredDistance = sum / (i - 1);
			screen.showWalk(i, theoreticalDistance, measuredDistance);
			double distance = randomWalk.walk(steps, screen);
			sum = sum + distance;
			Thread.sleep(1000);
		}
	}

	public double walk(int steps, Screen screen) {
		Random random = new Random();
		double x = 0, y = 0;
		for (int i = 0; i < steps; i++) {
			double angle = random.nextDouble() * 2 * Math.PI;
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			screen.showStep(i, x, y, x + dx, y + dy);
			x = x + dx;
			y = y + dy;
		}
		double distance = Math.sqrt(x * x + y * y);
		return distance;
	}

	static class Screen extends Frame {

		int steps;

		int WIDTH = 800;

		public Screen(int steps) {
			super("Random Walk");
			setSize(WIDTH, WIDTH);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			this.steps = steps;
		}

		public void showWalk(int count, double theoretical, double measured) {
			Graphics g = getGraphics();
			g.clearRect(0, 0, WIDTH, WIDTH);
			g.setFont(new Font("Tahoma", Font.PLAIN, 32));
			g.drawString("Random walk " + count + " (" + steps + " steps)", 160, 100);
			showCircleAtDistance(theoretical, Color.RED);
			showCircleAtDistance(measured, Color.BLUE);
			g.dispose();
		}

		public void showStep(double stepNumber, double x1, double y1, double x2,
				double y2) {
			Graphics g = getGraphics();
			int progress = (int) (255 * stepNumber / steps);
			Color color = new Color(0, 255 - progress, progress);
			g.setColor(color);
			g.drawLine(toScreen(x1), toScreen(y1), toScreen(x2), toScreen(y2));
			g.dispose();
		}

		public void showCircleAtDistance(double distance, Color color) {
			Graphics g = getGraphics();
			g.setColor(color);
			int x = toScreen(-distance);
			int y = toScreen(-distance);
			int width = toScreenSize(distance * 2);
			int height = toScreenSize(distance * 2);
			g.drawArc(x, y, width, height, 0, 360);
			g.dispose();
		}

		int toScreenSize(double d) {
			double average = Math.sqrt(steps);
			return (int) (d * WIDTH / average / 4);
		}

		int toScreen(double x) {
			return toScreenSize(x) + WIDTH / 2;
		}
	}
}