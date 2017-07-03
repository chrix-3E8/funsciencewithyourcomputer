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
public class SpaceSimulation2 {

	public static void main(String[] args) throws Exception {
		final SpaceSimulation2 simulation = new SpaceSimulation2();
		simulation.create();
		Screen screen = new Screen(simulation);
		screen.setVisible(true);
		long displayDelayMilliseconds = 50;
		long simulationDelayMilliseconds = displayDelayMilliseconds * 10;
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
		width = 10 * 149.598E9;
		height = width;

		Element hd160691 = new Element();
		hd160691.type = "planet";
		hd160691.id = 1;
		hd160691.x = width / 2;
		hd160691.y = width / 2;
		hd160691.radius = 1.245 * 695E6 * 40;
		hd160691.mass = 1.08 * 1.988435E30;
		hd160691.vx = 0;
		hd160691.vy = 0;
		hd160691.maxEnergy = Double.MAX_VALUE;
		hd160691.energy = hd160691.maxEnergy;
		elements.add(hd160691);
		
		Element hd160691b = new Element();
		hd160691b.type = "planet";
		hd160691b.id = 100;
		hd160691b.x = width / 2 + 1.5 * 149.598E9;
		hd160691b.y = width /2;
		hd160691b.radius = hd160691.radius / 2;
		hd160691b.mass = 1.67 * 1.899E27;
		hd160691b.vx = 0;
		hd160691b.vy = -1e10;
		hd160691b.maxEnergy = Double.MAX_VALUE;
		hd160691b.energy = hd160691b.maxEnergy;
		elements.add(hd160691b);
		
		Element hd160691c = new Element();
		hd160691c.type = "planet";
		hd160691c.id = 10000;
		hd160691c.x = width / 2 + 4.17 * 149.598E9;
		hd160691c.y = width / 2;
		hd160691c.radius = hd160691.radius / 3;
		hd160691c.mass = 3.1 * 1.899E27;
		hd160691c.vx = 0;
		hd160691c.vy = -1e10;
		hd160691c.maxEnergy = Double.MAX_VALUE;
		hd160691c.energy = hd160691c.maxEnergy;
		elements.add(hd160691c);
		
		Element hd160691d = new Element();
		hd160691d.type = "planet";
		hd160691d.id = 1000000;
		hd160691d.x = width / 2 + 0.09 * 149.598E9;
		hd160691d.y = width / 2;
		hd160691d.radius = hd160691.radius / 4;
		hd160691d.mass = 0.044 * 1.899E27;
		hd160691d.vx = 0;
		hd160691d.vy = -1e10;
		hd160691d.maxEnergy = Double.MAX_VALUE;
		hd160691d.energy = hd160691d.maxEnergy;
		elements.add(hd160691d);
		
		Element hd160691e = new Element();
		hd160691e.type = "planet";
		hd160691e.id = 10000000;
		hd160691e.x = width / 2 + 0.921 * 149.598E9;
		hd160691e.y = width / 2;
		hd160691e.radius = hd160691.radius / 5;
		hd160691e.mass = 0.5219 * 1.899E27;
		hd160691e.vx = 0;
		hd160691e.vy = -1e10;
		hd160691e.maxEnergy = Double.MAX_VALUE;
		hd160691e.energy = hd160691e.maxEnergy;
		elements.add(hd160691e);
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
				double constant = G * e.mass * n.mass
						/ ((n.x - e.x) * (n.x - e.x) + (n.y - e.y) * (n.y - e.y));
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
						if (missile.x + missile.radius >= spacecraft.x - spacecraft.radius
								&& missile.x - missile.radius <= spacecraft.x
										+ spacecraft.radius
								&& missile.y + missile.radius >= spacecraft.y
										- spacecraft.radius
								&& missile.y - missile.radius <= spacecraft.y
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
		int WIDTH = 1000, EXTRA = 25;

		SpaceSimulation2 simulation;

		public Screen(final SpaceSimulation2 simulation) {
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
								* Math.cos(spacecraft.thrustAngle) + e.radius
								* Math.cos(e.thrustAngle);
						e.y = spacecraft.y + spacecraft.radius
								* Math.sin(spacecraft.thrustAngle) + e.radius
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
						spacecraft.thrust = 50;
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