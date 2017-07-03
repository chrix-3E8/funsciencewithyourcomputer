import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class PathFinder1 {

	public static void main(String[] args) {
		Screen screen = new Screen();
		screen.setVisible(true);
		PathFinder1 maze = new PathFinder1();
		int milliseconds = 500;
		maze.searchExit(screen, milliseconds);
	}

	String maze = "W W W W WW" + "          " + " W WWW W  " + " W     W W"
			+ "WWWWEW W  " + "   WWWWWW " + " W W W    " + " WWW   W  "
			+ "     W W  " + "WWWWWWSWWW";

	int width = (int) Math.sqrt(maze.length());

	Screen screen;

	int milliseconds;

	void searchExit(Screen screen, int milliseconds) {
		this.screen = screen;
		this.milliseconds = milliseconds;
		int[] startCoordinates = getCoordinates(maze.indexOf("S"));
		maze = visit(maze, startCoordinates[0], startCoordinates[1]);
	}

	String visit(String maze, int x, int y) {
		screen.showMaze(maze, milliseconds);
		if (foundExit(maze))
			return maze;

		if (isEmpty(maze, x - 1, y)) {
			String newMaze = moveLeft(maze, x, y);
			String visitedNewMaze = visit(newMaze, x - 1, y);
			if (foundExit(visitedNewMaze))
				return visitedNewMaze;
		}
		if (isEmpty(maze, x, y - 1)) {
			String newMaze = moveUp(maze, x, y);
			String visitedNewMaze = visit(newMaze, x, y - 1);
			if (foundExit(visitedNewMaze))
				return visitedNewMaze;
		}
		if (isEmpty(maze, x + 1, y)) {
			String newMaze = moveRight(maze, x, y);
			String visitedNewMaze = visit(newMaze, x + 1, y);
			if (foundExit(visitedNewMaze))
				return visitedNewMaze;
		}
		if (isEmpty(maze, x, y + 1)) {
			String newMaze = moveDown(maze, x, y);
			String visitedNewMaze = visit(newMaze, x, y + 1);
			if (foundExit(visitedNewMaze))
				return visitedNewMaze;
		}
		return maze;
	}

	int[] getCoordinates(int pos) {
		return new int[] { pos % width, pos / width };
	}

	int getPosition(int x, int y) {
		return y * width + x;
	}

	boolean isInsideMaze(String maze, int x, int y) {
		if (x < 0 || x >= width)
			return false;
		if (y < 0 || y >= width)
			return false;
		int pos = getPosition(x, y);
		return pos >= 0 && pos < maze.length();
	}

	char look(String maze, int x, int y) {
		return maze.charAt(getPosition(x, y));
	}

	boolean isEmpty(String maze, int x, int y) {
		if (!isInsideMaze(maze, x, y))
			return false;
		return look(maze, x, y) == ' ';
	}

	boolean isStone(String maze, int x, int y) {
		if (!isInsideMaze(maze, x, y))
			return false;
		return look(maze, x, y) == '.';
	}

	boolean foundExit(String maze) {
		int[] exitCoordinates = getCoordinates(maze.indexOf('E'));
		if (isStone(maze, exitCoordinates[0] - 1, exitCoordinates[1])) {
			return true;
		}
		if (isStone(maze, exitCoordinates[0], exitCoordinates[1] - 1)) {
			return true;
		}
		if (isStone(maze, exitCoordinates[0] + 1, exitCoordinates[1])) {
			return true;
		}
		if (isStone(maze, exitCoordinates[0], exitCoordinates[1] + 1)) {
			return true;
		}
		return false;
	}

	String setStone(String maze, int x, int y) {
		int pos = getPosition(x, y);
		return maze.substring(0, pos) + '.'
				+ maze.substring(pos + 1, maze.length());
	}

	String moveUp(String maze, int x, int y) {
		return setStone(maze, x, y - 1);
	}

	String moveDown(String maze, int x, int y) {
		return setStone(maze, x, y + 1);
	}

	String moveLeft(String maze, int x, int y) {
		return setStone(maze, x - 1, y);
	}

	String moveRight(String maze, int x, int y) {
		return setStone(maze, x + 1, y);
	}

	static class Screen extends Frame {

		int WIDTH = 1000;

		String maze = "";

		public Screen() {
			super("Path Finder");
			setSize(WIDTH, WIDTH);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
		}

		public void paint(Graphics g) {
			BufferedImage image = (BufferedImage) createImage(WIDTH, WIDTH);
			Graphics g2 = image.getGraphics();
			g2.setColor(Color.DARK_GRAY);
			g2.fillRect(0, 0, WIDTH, WIDTH);
			int n = (int) Math.sqrt(maze.length());
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					char type = maze.charAt(i + j * n);
					drawMazeArea(g2, i, j, n, type);
				}
			}
			g.drawImage(image, 0, 0, this);
		}

		void drawMazeArea(Graphics g, int i, int j, int n, char type) {
			if (type == 'W')
				g.setColor(Color.DARK_GRAY);
			if (type == ' ')
				g.setColor(Color.WHITE);
			if (type == 'S' || type == '.')
				g.setColor(Color.GREEN);
			if (type == 'E')
				g.setColor(Color.ORANGE);
			int r = WIDTH / (n + 3);
			int x = i * r + r, y = j * r + r, width = r, height = r;
			g.fillRect(x, y, width, height);
			g.setColor(Color.WHITE);
			if (type == 'S')
				g.fillArc(x + r / 4, y + r / 4, width / 2, height / 2, 0, 360);
			if (type == 'E')
				g.fillRect(x + r / 3, y + r / 3, width / 3, height / 3);
			g.setColor(new Color(10, 200, 10));
			if (type == '.')
				g.fillArc(x + r / 3, y + r / 3, width / 3, height / 3, 0, 360);
		}

		void showMaze(String maze, int milliseconds) {
			this.maze = maze;
			repaint();
			try {
				Thread.sleep(milliseconds);
			} catch (Exception e) {
			}
		}

		public void update(Graphics g) {
			paint(g);
		}
	}
}