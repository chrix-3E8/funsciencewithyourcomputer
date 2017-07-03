import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class VectorGraphics {

	public static void main(String[] args) {
		VectorScreen screen = new VectorScreen();
		screen.read("chess.svg");
		String board = "rnbqkbnr" + "pppppppp" + "        " + "        "
				+ "        " + "        " + "PPPPPPPP" + "RNBQKBNR";
		screen.drawBoard(board);
		screen.setVisible(true);
	}

	static class VectorScreen extends Frame {

		int WIDTH = 1000, OFFSET = 25;

		Hashtable symbols = new Hashtable();

		String board = null;

		public VectorScreen() {
			super("Vector Graphics");
			setSize(WIDTH + OFFSET * 2, WIDTH + OFFSET * 2);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
		}

		public void read(String svg) {
			String line = null;
			try {
				BufferedReader file = new BufferedReader(new FileReader(svg));
				while ((line = file.readLine()) != null) {
					if (line.indexOf("<symbol>") != -1) {
						String symbol = line.substring(line.indexOf("<symbol>")
								+ "<symbol>".length(), line.indexOf("</symbol>"));
						line = file.readLine();
						String polygon = line.substring(line.indexOf("<polygon>")
								+ "<polygon>".length(), line.indexOf("</polygon>"));
						int cnt = 0, index = 0;
						while ((index = polygon.indexOf(",", index + 1)) != -1)
							cnt++;
						int[] points = new int[cnt + 1];
						cnt = 0;
						int previous = 0;
						for (int i = 0; i < polygon.length(); i++) {
							if (polygon.charAt(i) == ',') {
								points[cnt] = Integer.parseInt(polygon.substring(previous, i));
								previous = i + 1;
								cnt++;
							}
						}
						points[cnt] = Integer.parseInt(polygon.substring(previous,
								polygon.length()));
						symbols.put(symbol, points);
					}
				}
				file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void fill(Graphics g, BufferedImage image, int x, int y,
				Color srcColor, Color fillColor) {
			if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
				return;
			List points = new ArrayList();
			points.add(new int[] { x, y });
			while (!points.isEmpty()) {
				int[] point = (int[]) points.remove(0);
				x = point[0];
				y = point[1];
				if (image.getRGB(x, y) != srcColor.getRGB())
					continue; // wall
				image.setRGB(x, y, fillColor.getRGB());
				points.add(new int[] { x + 1, y });
				points.add(new int[] { x - 1, y });
				points.add(new int[] { x, y + 1 });
				points.add(new int[] { x, y - 1 });
			}
		}

		public int[] center(int[] points) {
			int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
			for (int i = 0; i < points.length - 2; i = i + 2) {
				if (points[i] < minX)
					minX = points[i];
				if (points[i] > maxX)
					maxX = points[i];
				if (points[i + 1] < minY)
					minY = points[i + 1];
				if (points[i + 1] > maxY)
					maxY = points[i + 1];
			}
			return new int[] { (maxX + minX) / 2, (maxY + minY) / 2 };
		}

		public void paint(Graphics g) {
			if (board == null)
				return;
			g.setColor(Color.BLACK);
			BufferedImage image = (BufferedImage) createImage(WIDTH, WIDTH);
			int SIZE = WIDTH / 10;
			Graphics g2 = image.getGraphics();
			Color color = Color.WHITE;
			Font font = new Font("Tahoma", Font.BOLD, SIZE / 2);
			g2.setFont(font);
			for (int y = 0; y <= 7; y++) {
				for (int x = 0; x <= 7; x++) {
					String piece = "" + board.charAt(y * 8 + x);
					boolean isWhite = "PRNBQK".indexOf(piece) != -1;
					Color pieceColor = isWhite ? Color.ORANGE : Color.DARK_GRAY;
					String name = getSymbolName(piece);
					drawPiece(name, g2, image, (x + 1) * SIZE, (y + 1) * SIZE, SIZE,
							color, Color.BLACK, pieceColor);
					if (x < 7)
						color = color == Color.GREEN ? Color.WHITE : Color.GREEN;
					g2.setColor(Color.BLACK);
					if (y == 0)
						g2.drawString("" + (char) ('a' + x), (x + 1) * SIZE + SIZE / 2,
								SIZE / 2);
					if (y == 7)
						g2.drawString("" + (char) ('a' + x), (x + 1) * SIZE + SIZE / 2,
								SIZE * 9 + SIZE / 2);
					if (x == 0)
						g2.drawString("" + (8 - y), SIZE / 2, SIZE * (y + 1) + SIZE / 2);
					if (x == 7)
						g2.drawString("" + (8 - y), SIZE * 9 + SIZE / 2, SIZE * (y + 1)
								+ SIZE / 2);
				}
			}
			g.drawImage(image, OFFSET, OFFSET, this);
		}

		public void drawPiece(String piece, Graphics g, BufferedImage image, int x,
				int y, int size, Color squareColor, Color strokeColor, Color fillColor) {
			g.setColor(squareColor);
			g.fillRect(x, y, size, size);
			if (piece == null)
				return;
			int[] points = (int[]) symbols.get(piece);
			int size_piece = 40;
			g.setColor(strokeColor);
			for (int i = 0; i < points.length - 2; i = i + 2) {
				g.drawLine(points[i] * size / size_piece + x, points[i + 1] * size
						/ size_piece + y, points[i + 2] * size / size_piece + x,
						points[i + 3] * size / size_piece + y);
			}
			int[] center = center(points);
			fill(g, image, center[0] * size / size_piece + x, center[1] * size
					/ size_piece + y, squareColor, fillColor);
		}

		public void drawBoard(String board) {
			this.board = board;
			repaint();
		}

		String getSymbolName(String shortcut) {
			shortcut = shortcut.toLowerCase();
			String name = null;
			if (shortcut.equals("p"))
				name = "pawn";
			if (shortcut.equals("n"))
				name = "knight";
			if (shortcut.equals("r"))
				name = "rook";
			if (shortcut.equals("q"))
				name = "queen";
			if (shortcut.equals("k"))
				name = "king";
			if (shortcut.equals("b"))
				name = "bishop";
			return name;
		}

		public void update(Graphics g) {
			paint(g);
		}
	}
}