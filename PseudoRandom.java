import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class PseudoRandom {

	public static void main(String[] args) throws Exception {
		PseudoRandom random = new PseudoRandom();
		long value = 12345678;
		Screen screen = new Screen();
		screen.setVisible(true);
		while (true) {
			value = random.calculateNext(value);
			int dice = random.dice(value);
			screen.showDice(dice, value);
			Thread.sleep(1000);
		}
	}

	public long calculateNext(long value) {
		value = value * value;
		String result = Long.toString(value);
		while (result.length() != 16)
			result = result + "0";
		String newResult = result.substring(4, 12);
		value = Long.parseLong(newResult);
		return value;
	}

	public int dice(long value) {
		return (int) (value % 6) + 1;
	}

	static class Screen extends Frame {

		int WIDTH = 900;

		int DICE_PER_ROW = 6, DICE_PER_COLUMN = 6;

		int[] dices = new int[DICE_PER_COLUMN * DICE_PER_ROW];

		long[] values = new long[DICE_PER_COLUMN * DICE_PER_ROW];

		int currentDiceIndex = -1;

		public Screen() {
			super("Pseudo Random");
			setSize(WIDTH, WIDTH);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
		}

		public void showDice(int dice, long value) {
			currentDiceIndex = currentDiceIndex + 1;
			if (currentDiceIndex >= dices.length)
				currentDiceIndex = 0;

			dices[currentDiceIndex] = dice;
			values[currentDiceIndex] = value;
			repaint();
		}

		public void paint(Graphics g) {
			BufferedImage image = (BufferedImage) createImage(WIDTH, WIDTH);
			Graphics g2 = image.getGraphics();
			for (int i = 0; i < DICE_PER_ROW; i++) {
				for (int j = 0; j < DICE_PER_COLUMN; j++) {
					int index = i + j * DICE_PER_ROW;
					int dice = dices[index];
					long value = values[index];
					boolean isCurrentDice = index == currentDiceIndex;
					drawDice(g2, i, j, dice, value, isCurrentDice);
				}
			}
			g.drawImage(image, 50, 50, this);
		}

		public void update(Graphics g) {
			paint(g);
		}

		void drawDice(Graphics g, int i, int j, int dice, long value,
				boolean isCurrentDice) {
			int width = 80, height = width, arcWidth = 10, arcHeight = arcWidth, r = width / 3;
			int x = (width + width / 3) * i + width;
			int y = (height + height / 3) * j + height;
			if (isCurrentDice) {
				g.setFont(new Font("Tahoma", Font.PLAIN, 32));
				g.drawString("Dice: " + dice + " (from calculated pseudo random value "
						+ value + ")", 0, 30);
				g.setColor(Color.ORANGE);
				g.fillRect(x - r, y - r, width + 2 * r, height + 2 * r);
			}
			g.setColor(new Color((int) value));
			g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
			g.setColor(Color.BLACK);
			if (dice == 1 || dice == 3 || dice == 5)
				g.fillArc(x + r, y + r, r, r, 0, 360);
			if (dice == 2 || dice == 4 || dice == 5 || dice == 6) {
				g.fillArc(x, y, r, r, 0, 360);
				g.fillArc(x + 2 * r, y + 2 * r, r, r, 0, 360);
			}
			if (dice == 3 || dice == 4 || dice == 5 || dice == 6) {
				g.fillArc(x, y + 2 * r, r, r, 0, 360);
				g.fillArc(x + 2 * r, y, r, r, 0, 360);
			}
			if (dice == 6) {
				g.fillArc(x, y + r, r, r, 0, 360);
				g.fillArc(x + 2 * r, y + r, r, r, 0, 360);
			}
		}
	}
}