import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class SpaceSimulation3 {

	public static void main(String[] args) throws Exception {
		final SpaceSimulation3 simulation = new SpaceSimulation3();
		simulation.create();
		Screen screen = new Screen(simulation);
		screen.setVisible(true);
		long displayDelayMilliseconds = 100;
		long simulationDelayMilliseconds = 3600*24*displayDelayMilliseconds;
		while (true) {
			Thread.sleep(displayDelayMilliseconds);
			simulation.update(simulationDelayMilliseconds);
			screen.repaint();
		}
	}

	List elements = new ArrayList();

	double width, height; // Unit: meter

	static class Element {
		String type; // Spacecraft, planet or missile

		int id; // Identifier

		double x, y; // Unit: meter

		double radius; // Unit: meter

		double mass; // Unit: kg

		double vx, vy; // Unit: meter per second

		double thrust;

		double thrustAngle; // Unit: radians

		double energy; // Unit: joule

		double maxEnergy; // Unit: joule
	}

	public void create() {
		width = 384E6 * 4;
		height = width;

		Element planet1 = new Element();
		planet1.type = "planet";
		planet1.id = 100;
		planet1.x = width / 2;
		planet1.y = width / 2;
		planet1.radius = 6371E4;
		planet1.mass = 5.9736E24;
		planet1.vx = 0;
		planet1.vy = 0;
		planet1.maxEnergy = Double.MAX_VALUE;
		planet1.energy = planet1.maxEnergy;
		elements.add(planet1);

		Element planet2 = new Element();
		planet2.type = "planet";
		planet2.id = 1000000;
		planet2.x = planet1.x + 384400000;
		planet2.y = planet1.y;
		planet2.radius = 1738E4;
		planet2.mass = 7.349E22;
		planet2.vx = 0;
		planet2.vy = 400;
		planet2.maxEnergy = Double.MAX_VALUE;
		planet2.energy = planet2.maxEnergy;
		elements.add(planet2);
	}

	static double G = 6.6742E-11; // m3s-2kg-1

	public void update(double milliseconds) {
		double dt = milliseconds / 1000;
		List newElements = new ArrayList();
		Element[] oldElements = (Element[]) elements.toArray(new Element[elements
				.size()]);
		for (int i = 0; i < oldElements.length; i++) {
			Element e = oldElements[i];
			double gx = 0, gy = 0;
			for (int j = 0; j < oldElements.length; j++) {
				if (i == j)
					continue;
				Element n = oldElements[j];
				double r = Math.sqrt((n.x - e.x) * (n.x - e.x) + (n.y - e.y) * (n.y - e.y));
				double constant = G * e.mass * n.mass
						/ (r*r*r);
				gx += constant * (n.x - e.x);
				gy += constant * (n.y - e.y);
			}
			double dvx = gx / e.mass * dt + e.thrust * Math.cos(e.thrustAngle) * dt;
			double dvy = gy / e.mass * dt + e.thrust * Math.sin(e.thrustAngle) * dt;
			double dx = dvx * dt + e.vx * dt;
			double dy = dvy * dt + e.vy * dt;

			Element newElement = new Element();
			newElement.type = e.type;
			newElement.id = e.id;
			newElement.maxEnergy = e.maxEnergy;
			newElement.energy = e.energy;
			if (e.thrust != 0)
				newElement.energy = newElement.energy - 2;
			else if (newElement.type.equals("spacecraft")
					&& newElement.energy < newElement.maxEnergy)
				newElement.energy = newElement.energy + 1;
			newElement.mass = e.mass;
			newElement.radius = e.radius;
			newElement.vx = e.vx + dvx;
			newElement.vy = e.vy + dvy;
			newElement.thrust = e.thrust;
			if (newElement.energy == 0)
				newElement.thrust = 0;
			newElement.thrustAngle = e.thrustAngle;
			newElement.x = tore(e.x + dx);
			newElement.y = tore(e.y + dy);
			if (newElement.energy > 0)
				newElements.add(newElement);
		}
		Element[] array = (Element[]) newElements.toArray(new Element[newElements
				.size()]);
		for (int i = 0; i < array.length; i++) {
			if (array[i].type.equals("missile")) {
				Element missile = array[i];
				for (int j = 0; j < array.length; j++) {
					if (array[j].type.equals("spacecraft")) {
						Element spacecraft = array[j];
						if (missile.x  >= spacecraft.x - spacecraft.radius
								&& missile.x  <= spacecraft.x
										+ spacecraft.radius
								&& missile.y  >= spacecraft.y
										- spacecraft.radius
								&& missile.y  <= spacecraft.y
										+ spacecraft.radius) {
							/* collision */
							newElements.remove(missile);
							spacecraft.energy = spacecraft.energy - missile.energy;
						}
					}
				}
			}
		}
		elements = newElements;
	}

	double tore(double d) {
		return (d + width) % width;
	}

	Element getSpacecraft(int id) {
		Element[] oldElements = (Element[]) elements.toArray(new Element[elements
				.size()]);
		Element spacecraft = null;
		for (int i = 0; i < oldElements.length; i++) {
			Element e = oldElements[i];
			if (e.type.equals("spacecraft") && e.id == id) {
				spacecraft = oldElements[i];
				break;
			}
		}
		return spacecraft;
	}

	public static class Screen extends Frame {
		int WIDTH = 800, EXTRA = 25;

		SpaceSimulation3 simulation;

		public Screen(final SpaceSimulation3 simulation) {
			super("Space Simulator");
			this.simulation = simulation;
			setSize(WIDTH + 50, WIDTH + 50);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent event) {
					char c = event.getKeyChar();
					if (c == 'f') {
						Element spacecraft = simulation.getSpacecraft(1);
						if (spacecraft == null || spacecraft.energy < 4)
							return;
						spacecraft.energy = spacecraft.energy - 4;
						Element e = new Element();
						e.energy = 100;
						e.mass = 0.01;
						e.radius = 0.2;
						e.thrust = 200;
						e.thrustAngle = spacecraft.thrustAngle;
						e.type = "missile";
						double velocity = Math.sqrt(spacecraft.vx * spacecraft.vx
								+ spacecraft.vy * spacecraft.vy);
						e.vx = 2 * velocity * Math.cos(spacecraft.thrustAngle);
						e.vy = 2 * velocity * Math.sin(spacecraft.thrustAngle);
						e.x = spacecraft.x + spacecraft.radius 
								* Math.cos(spacecraft.thrustAngle) +  e.radius
								* Math.cos(e.thrustAngle);
						e.y = spacecraft.y + spacecraft.radius 
								* Math.sin(spacecraft.thrustAngle) +  e.radius
								* Math.sin(e.thrustAngle);
						simulation.elements.add(e);
					}
					if (c == 'a') {
						Element spacecraft = simulation.getSpacecraft(1);
						if (spacecraft == null)
							return;
						spacecraft.thrustAngle = spacecraft.thrustAngle + Math.PI * 2 / 90;
					}
					if (c == 's') {
						Element spacecraft = simulation.getSpacecraft(1);
						if (spacecraft == null)
							return;
						spacecraft.thrustAngle = spacecraft.thrustAngle - Math.PI * 2 / 90;
					}
					if (c == 'd') {
						Element spacecraft = simulation.getSpacecraft(1);
						if (spacecraft == null)
							return;
						spacecraft.thrust = 100;
					}
					if (c == ';') {
						Element spacecraft = simulation.getSpacecraft(2);
						if (spacecraft == null || spacecraft.energy < 4)
							return;
						spacecraft.energy = spacecraft.energy - 4;
						Element e = new Element();
						e.energy = 100;
						e.mass = 0.01;
						e.radius = 0.2;
						e.thrust = 200;
						e.thrustAngle = spacecraft.thrustAngle;
						e.type = "missile";
						double velocity = Math.sqrt(spacecraft.vx * spacecraft.vx
								+ spacecraft.vy * spacecraft.vy);
						e.vx = 2 * velocity * Math.cos(spacecraft.thrustAngle);
						e.vy = 2 * velocity * Math.sin(spacecraft.thrustAngle);
						e.x = spacecraft.x + spacecraft.radius
								* Math.cos(spacecraft.thrustAngle) + e.radius
								* Math.cos(e.thrustAngle);
						e.y = spacecraft.y + spacecraft.radius
								* Math.sin(spacecraft.thrustAngle) + e.radius
								* Math.sin(e.thrustAngle);
						simulation.elements.add(e);
					}
					if (c == 'j') {
						Element spacecraft = simulation.getSpacecraft(2);
						if (spacecraft == null)
							return;
						spacecraft.thrustAngle = spacecraft.thrustAngle + Math.PI * 2 / 90;
					}
					if (c == 'k') {
						Element spacecraft = simulation.getSpacecraft(2);
						if (spacecraft == null)
							return;
						spacecraft.thrustAngle = spacecraft.thrustAngle - Math.PI * 2 / 90;
					}
					if (c == 'l') {
						Element spacecraft = simulation.getSpacecraft(2);
						if (spacecraft == null)
							return;
						spacecraft.thrust = 50;
					}
				}

				public void keyReleased(KeyEvent event) {
					char c = event.getKeyChar();
					if (c == 'd') {
						Element spacecraft = simulation.getSpacecraft(1);
						if (spacecraft == null)
							return;
						spacecraft.thrust = 0;
					}
					if (c == 'l') {
						Element spacecraft = simulation.getSpacecraft(2);
						if (spacecraft == null)
							return;
						spacecraft.thrust = 0;
					}
				}
			});
		}

		public void update(Graphics g) {
			paint(g);
		}

		public void paint(Graphics g) {
			BufferedImage image = (BufferedImage) createImage(WIDTH, WIDTH);
			Graphics g2 = image.getGraphics();
			List elements = simulation.elements;
			for (Iterator iElements = elements.iterator(); iElements.hasNext();) {
				Element element = (Element) iElements.next();
				drawElement(g2, element);
			}
			g.drawImage(image, EXTRA, EXTRA, this);
		}

		public void drawElement(Graphics g, Element e) {
			double ratioWidth = WIDTH / simulation.width;
			double ratioHeight = WIDTH / simulation.height;
			if (e.type.equals("planet")) {
				int r = 64 + e.id % 127;
				g.setColor(new Color(r, r, r));
			}
			if (e.type.equals("spacecraft")) {
				g.setColor(e.id == 1 ? Color.GREEN : Color.MAGENTA);
			}
			if (e.type.equals("missile")) {
				g.setColor(Color.RED);
			}
			int x = (int) (e.x * ratioWidth);
			int y = (int) (e.y * ratioHeight);
			int width = (int) (e.radius * ratioWidth);
			int height = (int) (e.radius * ratioHeight);
			g.fillArc(x - width, y - height, width * 2, height * 2, 0, 360);
			if (e.type.equals("spacecraft")) {
				g.setColor(Color.YELLOW);
				int powerWidth = (int) (e.radius * ratioWidth * 0.5 * e.energy / e.maxEnergy);
				int powerHeight = (int) (e.radius * ratioHeight * 0.5 * e.energy / e.maxEnergy);
				g.fillArc(x - powerWidth, y - powerHeight, powerWidth * 2,
						powerHeight * 2, 0, 360);

				g.setColor(Color.BLACK);
				int x2 = x + (int) (e.radius * Math.cos(e.thrustAngle) * ratioWidth);
				int y2 = y + (int) (e.radius * Math.sin(e.thrustAngle) * ratioHeight);
				g.drawLine(x, y, x2, y2);
				if (e.thrust != 0) {
					g.setColor(Color.ORANGE);
					g.fillArc(x - width, y - width, width * 2, height * 2,
							toDegrees(-e.thrustAngle + Math.PI - Math.PI / 4),
							toDegrees(Math.PI / 4 * 2));
				}
			}
		}

		int toDegrees(double radians) {
			return (int) (radians * 360 / (Math.PI * 2));
		}
	}
}