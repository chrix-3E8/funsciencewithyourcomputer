import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class SlowFast1Linear {

	public static void main(String[] args) throws Exception {
		TimerGraph timer = new TimerGraph("Linear Timing Test");
		timer.setVisible(true);
		for (int n = 1; n <= 5; n++) {
			int calculationTime = 1234 * n;
			timer.start();
			Thread.sleep(calculationTime);
			timer.stop(n, n);
		}
	}

	public static class TimerGraph extends Frame {

		int WIDTH = 1000, OFFSET = WIDTH / 10, MAX_POINTS = 100;

		long startTime;

		long[] times = new long[MAX_POINTS], inputs = new long[MAX_POINTS],
				results = new long[MAX_POINTS];

		int cnt = 0;

		String title;

		public TimerGraph(String title) {
			super(title);
			this.title = title;
			setSize(WIDTH, WIDTH);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
		}

		public void start() {
			startTime = System.currentTimeMillis();
		}

		public void stop(long input, long result) {
			long stopTime = System.currentTimeMillis();
			times[cnt] = stopTime - startTime;
			inputs[cnt] = input;
			results[cnt] = result;
			cnt = cnt + 1;
			repaint();
		}

		public void paint(Graphics g) {
			if (cnt < 2)
				return;
			g.setFont(new Font("Tahoma", Font.PLAIN, 32));
			drawAxes(g);
			drawPoints(g);
		}

		void drawAxes(Graphics g) {
			g.setColor(Color.BLACK);
			g.drawString(title, WIDTH / 2 - OFFSET, OFFSET);
			int max = WIDTH - OFFSET;
			g.drawLine(OFFSET, max, max, max);
			g.drawLine(max, max, max - 10, max - 10);
			g.drawLine(max, max, max - 10, max + 10);
			g.drawString("n", max, WIDTH - OFFSET / 2);
			g.drawLine(OFFSET, OFFSET, OFFSET, max);
			g.drawLine(OFFSET, OFFSET, OFFSET - 10, OFFSET + 10);
			g.drawLine(OFFSET, OFFSET, OFFSET + 10, OFFSET + 10);
			g.drawString("time", OFFSET / 4, OFFSET);
		}

		void drawPoints(Graphics g) {
			g.setColor(Color.BLUE);
			Font smallFont = new Font("Tahoma", Font.BOLD, 24);
			Font bigFont = new Font("Tahoma", Font.PLAIN, 32);
			for (int i = 0; i < cnt; i++) {
				int x = toScreenX(inputs[i]), y = toScreenY(times[i]);
				int r = OFFSET / 10;
				g.fillOval(x - r, y - r, r * 2, r * 2);
				if (i > 0)
					g.drawLine(toScreenX(inputs[i - 1]), toScreenY(times[i - 1]),
							toScreenX(inputs[i]), toScreenY(times[i]));
				g.setFont(bigFont);
				g.drawString("" + i, x, y - 32);
				g.setFont(smallFont);
				g.drawString(String.format("n= %1.0g", (double) inputs[i]), x, y + 32);
				g.drawString(String.format("f(n)= %1.0g", (double) results[i]), x,
						y + 2 * 32);
				g.drawString("t= " + times[i] + " ms", x, y + 3 * 32);
			}
		}

		int toScreenX(long input) {
			return (int) ((input - min(inputs)) * (WIDTH - OFFSET * 2)
					/ range(inputs) + OFFSET);
		}

		int toScreenY(long time) {
			long range = range(times);
			if (range == 0)
				return WIDTH / 2;
			return (int) (WIDTH - (time - min(times)) * (WIDTH - OFFSET * 2)
					/ range(times) - OFFSET);
		}

		long min(long[] values) {
			long min = Long.MAX_VALUE;
			for (int i = 0; i < cnt; i++) {
				if (values[i] < min)
					min = values[i];
			}
			return min;
		}

		long range(long[] values) {
			long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
			for (int i = 0; i < cnt; i++) {
				if (values[i] < min)
					min = values[i];
				if (values[i] > max)
					max = values[i];
			}
			return max - min;
		}
	}
}