package dolmisani.games.jrogue;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;


/**
 * 
 * 
 * Original code by Will Thimbleby (http://will.thimbleby.net/roguelike/)
 * @author HP_Proprietario
 *
 */

public class JRogue extends Applet {
	
	private static final long serialVersionUID = 6615604848203638984L;
	
	DrawPanel drawPanel;
	int width = 50;
	int height = 50;

	char map[][] = new char[50][50];
	char floorchar = '\u00B7';
	Vector<JRogue.Monster> monsters = new Vector<JRogue.Monster>();
	Character player;

	static Font drawFont = new Font("Monospaced", Font.PLAIN, 14);

	public class DrawPanel extends JPanel implements KeyListener {
		HashMap<java.lang.Character, TextLayout> textcache = new HashMap<java.lang.Character, TextLayout>();

		public DrawPanel() {
			addKeyListener(this);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					DrawPanel.this.requestFocusInWindow();
				}
			});
		}

		public boolean isFocusable() {
			return true;
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			Rectangle r = getBounds();
			Rectangle2D rbounds = new Rectangle2D.Float(0f, 0f, (float) r
					.getWidth() - 1, (float) r.getHeight() - 1);

			g2d.setColor(Color.black);
			g2d.fill(rbounds);

			FontRenderContext frc = g2d.getFontRenderContext();

			if (player.health < 0) {
				g.setColor(new Color(255, 0, 0, 255));
				String txt = "Dead & buried on level " + level + " with $"
						+ player.score;
				TextLayout layout = new TextLayout(txt, drawFont, frc);
				layout.draw(g2d, (600 / 11 - txt.length()) * 11 / 2,
						(height + 1) * 11 / 2);
				return;
			}

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color tileColor;
					char c = map[x][y];
					if (player.seen[x][y] == false || c == ' ')
						continue;

					// lighting
					float light = 1;
					if (player.sees[x][y]) {
						int dx = player.x - x;
						int dy = player.y - y;
						float rr = (dx * dx + dy * dy);
						light = rr > 1 ? 0.5f + 4 / (rr - 1) : 1;
						if (light > 1)
							light = 1;
					} else
						light = 0.25f;

					// colouring
					if (c == '$')
						tileColor = new Color(1.0f, 1.0f, 0, 1.0f);
					else if (c == ',' || c == ';' || c == '\'' || c == '`')
						tileColor = new Color(1.0f, 0, 0, 1.0f);
					else if (c == '!')
						tileColor = new Color(0, 1.0f, 0, 1.0f);
					else
						tileColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

					if (player.sees[x][y]) {
						for (Monster m : monsters) {
							   if(m.x == x && m.y == y)
	                            {
	                              
	                                tileColor = m.tileColor;
	                                c = m.symbol;
	                                if(c == '@')
	                         
	                                	tileColor = m.tileColor;
	                                break;
	                            }
						}
					}

					tileColor = new Color(tileColor.getRed() / 255 * light,
							tileColor.getGreen() / 255 * light, tileColor
									.getBlue()
									/ 255 * light, tileColor.getAlpha() / 255);

					// drawing
					g.setColor(tileColor);
					TextLayout layout = (TextLayout) textcache.get(c);
					if (layout == null)
						layout = new TextLayout("" + c, drawFont, frc);
					textcache.put(c, layout);

					layout.draw(g2d, x * 11, y * 11);
				}
			}

			// draw player info
			g.setColor(Color.white);
			TextLayout layout = new TextLayout("Score: " + player.score,
					drawFont, frc);
			layout.draw(g2d, 1 * 11, (height + 1) * 11);
			layout = new TextLayout("Lvl:   " + level, drawFont, frc);
			layout.draw(g2d, 1 * 11, (height + 2) * 11);

			layout = new TextLayout("Health: " + player.health + "/"
					+ player.maxhealth, drawFont, frc);
			layout.draw(g2d, 16 * 11, (height + 1) * 11);
			layout = new TextLayout("Expr:   " + (1 + player.experience / 20)
					+ ":" + player.experience, drawFont, frc);
			layout.draw(g2d, 16 * 11, (height + 2) * 11);

			// status
			for (int i = 0; i < 3; i++) {
				g.setColor(player.statusColor[i]);
				layout = new TextLayout(player.status[i], drawFont, frc);
				layout.draw(g2d, 30 * 11, (height + i + 1) * 11);
			}
		}

		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_Y:
			case KeyEvent.VK_NUMPAD7:
				player.move(-1, -1);
				break;
			case KeyEvent.VK_K:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_UP:
				player.move(0, -1);
				break;
			case KeyEvent.VK_U:
			case KeyEvent.VK_NUMPAD9:
				player.move(1, -1);
				break;
			case KeyEvent.VK_L:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_RIGHT:
				player.move(1, 0);
				break;
			case KeyEvent.VK_N:
			case KeyEvent.VK_NUMPAD3:
				player.move(1, 1);
				break;
			case KeyEvent.VK_J:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_DOWN:
				player.move(0, 1);
				break;
			case KeyEvent.VK_B:
			case KeyEvent.VK_NUMPAD1:
				player.move(-1, 1);
				break;
			case KeyEvent.VK_H:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_LEFT:
				player.move(-1, 0);
				break;
			case KeyEvent.VK_PERIOD:
			case KeyEvent.VK_NUMPAD5:
				player.move(0, 0);
				break;
			}
			if (e.getKeyChar() == '>' && map[player.x][player.y] == '>')
				player.goDown();
			repaint();
		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}

	}

	class Character extends Monster {
		boolean seen[][] = new boolean[50][50];
		boolean sees[][] = new boolean[50][50];
		int score = 0;
		int rest = 0;
		int seeRadius = 7;

		String status[] = { " ", " ", " " };
		Color statusColor[] = { Color.white, Color.white, Color.white };

		public Character() {
			super(20, 5, '@', "player", Color.yellow);
		}

		public void reset() {
			super.place();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++)
					seen[x][y] = false;
			}
			see();
		}

		public void setStatus(String s, Color c) {
			for (int i = 1; i >= 0; i--) {
				statusColor[i + 1] = statusColor[i];
				status[i + 1] = status[i];
			}
			status[0] = s;
			statusColor[0] = c;
		}

		public void see() {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++)
					sees[x][y] = false;
			}

			for (int yy = -seeRadius; yy < seeRadius; yy++) {
				if (y + yy < 0 || y + yy >= height)
					continue;
				for (int xx = -seeRadius; xx < seeRadius; xx++) {
					if (xx * xx + yy * yy > seeRadius * seeRadius)
						continue;
					if (x + xx < 0 || x + xx >= width)
						continue;
					if (y + yy < 0 || y + yy >= height)
						continue;
					if (lineOfSight(x + xx, y + yy))
						sees[x + xx][y + yy] = seen[x + xx][y + yy] = true;
				}
			}
		}

		public void gainExperience(int e) {
			if (experience / 20 < (experience + e) / 20) {
				setStatus("Gained a new level.", new Color(255, 255, 0, 255));
				maxhealth += 2;
			}
			experience += e;
		}

		public void damage(int d, Monster m) {
			super.damage(d, m);
			setStatus("Hurt by an " + m.name + ".", Color.white);
		}

		public void move(int dx, int dy) {
			super.move(dx, dy);

			if (map[x][y] == '$') {
				map[x][y] = floorchar;
				int dscore = (int) (Math.random() * 20) + 2;
				setStatus("Got $" + dscore + ".", Color.white);
				score += dscore;
			}
			if (map[x][y] == '!') {
				map[x][y] = floorchar;
				health += (int) (Math.random() * 5 + 5);
				if (health > maxhealth)
					health = maxhealth;
				setStatus("Drank a healing potion.", Color.white);
			}

			if (health < maxhealth)
				rest++;
			if (rest > 20) {
				rest = 0;
				health++;
			}
			see();
			tick();
			drawPanel.repaint();
		}

		public void goDown() {
			monsters.removeAllElements();
			monsters.add(player);
			genMap();
			reset();
			setStatus("Went downstairs.", Color.white);
		}

		public void ai() {
		}
	}

	class Monster {
		int x = 10, y = 10;
		char symbol = 'a';
		int health, maxhealth;
		int strength;
		String name;
		int experience;
		
		Color tileColor;

		public Monster(int h, int str, char s, String n, Color tileColor) {
			maxhealth = health = h;
			strength = str;
			symbol = s;
			name = n;
			experience = 0;
			
			this.tileColor = tileColor;
		}

		public void place() {
			while (true) {
				x = (int) (Math.random() * width);
				y = (int) (Math.random() * height);
				if (map[x][y] == floorchar)
					break;
			}
		}

		public boolean lineOfSight(float x2, float y2) {
			// pick corner nearest monster
			if (x2 < x)
				x2 += 0.5f;
			else if (x2 > x)
				x2 -= 0.5f;
			if (y2 < y)
				y2 += 0.5f;
			else if (y2 > y)
				y2 -= 0.5f;

			float dx = x2 - x;
			float dy = y2 - y;

			float l = Math.max(Math.abs(dx), Math.abs(dy));
			dx /= l;
			dy /= l;

			float xx = x;
			float yy = y;

			while (l > 0) {
				int ix = (int) (xx + 0.5);
				int iy = (int) (yy + 0.5);

				if (x2 == ix && y2 == iy)
					break;
				if (!(x == ix && y == iy) && map[ix][iy] == '#')
					return false;

				xx += dx;
				yy += dy;
				l--;
			}
			return true;
		}

		public void gainExperience(int e) {
			experience += e;
		}

		public void damage(int d, Monster m) {
			health -= d;
			if (health < 0) {
				// death
				char fl = name.charAt(0);
				player.setStatus(((fl == 'a' /* ... */) ? "An " : "A ") + name
						+ " dies.", Color.white);
				monsters.remove(this);

				// experience
				m.gainExperience((int) (Math.random() * strength));
			}
			// carnage
			if (map[x][y] == floorchar) {
				switch ((int) (Math.random() * 3)) {
				case 0:
					map[x][y] = ',';
					break;
				case 1:
					map[x][y] = '\'';
					break;
				case 2:
					map[x][y] = '`';
					break;
				}
			}
		}

		public void move(int dx, int dy) {
			if (dx == 0 && dy == 0)
				return;
			if (x + dx < 0 || x + dx >= width)
				return;
			if (y + dy < 0 || y + dy >= height)
				return;

			if (map[x + dx][y + dy] == '#')
				return;

			// if walk into a monster -- do damage
			for (Monster m : monsters) {
				if (m.x == x + dx && m.y == y + dy) {
					m
							.damage(
									(int) (Math.random() * (strength + (experience / 20))),
									this);
					return;
				}
			}

			x += dx;
			y += dy;
		}

		double ai_interest = 0.3;
		int tx, ty;
		boolean memory = false;

		public void ai() {
			// if can see/remember player move towards them
			boolean see = lineOfSight(player.x, player.y);
			if ((see || memory) && Math.random() > ai_interest) {
				if (see) {
					memory = true;
					tx = player.x;
					ty = player.y;
				}
				int dx = tx - x;
				int dy = ty - y;

				// attempts to move straight or diagonally
				int ddx = dx > 0 ? 1 : dx < 0 ? -1 : 0;
				int ddy = dy > 0 ? 1 : dy < 0 ? -1 : 0;

				int attempt[][] = new int[3][2];

				for (int c = 0; c < 3; c++) {
					attempt[c][0] = ddx;
					attempt[c][1] = ddy;
				}
				if (dx * dx > dy * dy) {
					attempt[1][1] = 0;
					attempt[2][0] = 0;
				} else {
					attempt[1][0] = 0;
					attempt[2][1] = 0;
				}

				for (int c = 0; c < 3; c++) {
					ddx = attempt[c][0];
					ddy = attempt[c][1];
					if (map[x + ddx][y + ddy] != '#')
						break;
				}

				move(ddx, ddy);
			} else
				move((int) (Math.random() * 3) - 1,
						(int) (Math.random() * 3) - 1);
		}
	}

	class Room {
		int x, y, w, h;

		public Room() {
			while (true) {
				x = (int) (Math.random() * (width - 13)) + 2;
				y = (int) (Math.random() * (height - 13)) + 2;
				w = (int) (Math.random() * 10) + 3;
				h = (int) (Math.random() * 10) + 3;

				boolean clear = true;
				for (int yy = y - 1; yy < y + h + 1; yy++) {
					for (int xx = x - 1; xx < x + w + 1; xx++) {
						if (xx >= width || yy >= height
								|| map[xx][yy] == floorchar) {
							clear = false;
							break;
						}
					}
					if (!clear)
						break;
				}
				if (!clear)
					continue;

				for (int yy = y; yy < y + h; yy++) {
					for (int xx = x; xx < x + w; xx++) {
						map[xx][yy] = floorchar;
					}
				}
				break;
			}
		}

		public void createPathTo(Room b) {
			int x = (int) (Math.random() * (w)) + this.x;
			int y = (int) (Math.random() * (h)) + this.y;
			int x2 = (int) (Math.random() * (b.w)) + b.x;
			int y2 = (int) (Math.random() * (b.h)) + b.y;

			int dx = x2 - x;
			int dy = y2 - y;
			dx = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
			dy = dy > 0 ? 1 : (dy < 0 ? -1 : 0);
			boolean horizontal = (Math.random() > 0.5);

			while (x != x2 || y != y2) {
				if (y == y2 || (horizontal && x != x2))
					x += dx;
				else
					y += dy;

				if (!(x > 0 && y > 0 && x < width && y < height))
					break;
				map[x][y] = floorchar;

				if (Math.random() < 0.1)
					horizontal = !horizontal;
			}
		}
	}

	public void tick() {
		for (int i = 0; i < monsters.size(); i++) {
			Monster m = monsters.elementAt(i);
			m.ai();
		}
	}

	int level = 0;
	Monster mtypes[] = { 
			new Monster(3, 2, 'a', "ant", Color.red),
			new Monster(5, 3, 'd', "dog", Color.cyan), 
			new Monster(7, 4, 'k', "kobol", Color.white),
			new Monster(3, 6, 'g', "goblin", Color.white),
			new Monster(10, 3, 'h', "hobbit", Color.white), 
			new Monster(7, 7, 'x', "xan", Color.white),
			new Monster(15, 15, 'D', "dragon", Color.white) 
	};

	public void genMap() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				map[x][y] = '#';
		}

		Vector<JRogue.Room> connectedRooms = new Vector<JRogue.Room>();
		Vector<JRogue.Room> rooms = new Vector<JRogue.Room>();

		int nrooms = (int) (Math.random() * 4) + 5;
		for (int i = 0; i < nrooms; i++) {
			rooms.add(new Room());
		}
		connectedRooms.add(new Room());
		while (rooms.size() > 0) {
			int a, b;
			a = (int) (Math.random() * connectedRooms.size());
			b = (int) (Math.random() * rooms.size());
			connectedRooms.get(a).createPathTo(rooms.get(b));
			connectedRooms.add(rooms.get(b));
			rooms.remove(b);
		}

		// gold
		for (int i = 0; i < (level + 10); i++) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			map[x][y] = '$';
		}
		// potions
		for (int i = 0; i < 10; i++) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			map[x][y] = '!';
		}
		// down staircase
		while (true) {
			int x = (int) (Math.random() * width);
			int y = (int) (Math.random() * height);
			if (map[x][y] == floorchar) {
				map[x][y] = '>';
				break;
			}
		}

		// monsters
		int c = nrooms + (int) (Math.random() * (nrooms + ((float) level / 4)));
		for (int i = 0; i < c; i++) {
			int ind = (int) (Math.abs((Math.random() + Math.random() - 1)) * (level + 2));
			if (ind >= mtypes.length)
				ind = mtypes.length - 1;
			Monster t = mtypes[ind];
			t = new Monster(t.health, t.strength, t.symbol, t.name, t.tileColor);
			t.ai_interest = Math.random() / (ind + 2);
			monsters.add(t);
			t.place();
		}
		level++;
	}

	public void init() {
		setLayout(new BorderLayout());

		drawPanel = new DrawPanel();
		drawPanel.setPreferredSize(new Dimension(300, 300));
		add(drawPanel, BorderLayout.CENTER);

		genMap();

		player = new Character();

		monsters.add(player);

		player.reset();
		drawPanel.repaint();
	}
}
