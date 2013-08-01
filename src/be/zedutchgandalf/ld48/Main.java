package be.zedutchgandalf.ld48;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 6181612111777646203L;

	Screen screen;
	boolean running, renderFPS, win, lose, nextLevel, restartLevel, fbi, startMenu, changeList, nagFocus, secretEnd;
	int FPS, timeLeft, mCounter;
	LoadingAnim anim;
	static int backgroundColor, score;
	ArrayList<Text> text;
	Player player;
	Sound ding, pang, enPang, siren;

	static int WIDTH = 300, HEIGHT = 300, SCALE = 3;
	static Main instance;

	@Override
	public void run() {
		requestFocus();
		screen = new Screen(this);
		anim = new LoadingAnim();
		anim.start(screen);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		renderFPS = true;
		win = false;
		lose = false;
		secretEnd = false;
		fbi = false;
		startMenu = true;
		score = 0;
		mCounter = 0;
		text = new ArrayList<Text>();
		changeList = !startMenu;
		backgroundColor = 0xFFFFFFFF;
		Graphic tut1 = new Graphic("tut1.png");
		Graphic tut2 = new Graphic("tut2.png");
		Graphic tut3 = new Graphic("tut3.png");
		Graphic congratz = new Graphic("congratz.png");
		Graphic gFbi = new Graphic("fbi.png");
		Graphic gstart = new Graphic("start.png");
		Graphic gFoc = new Graphic("focus.png");
		Graphic bg = new Graphic("bg.png");
		Graphic gold = new Graphic("gold.png");
		Graphic menu1 = new Graphic("menu1.gif");
		Graphic menu2 = new Graphic("menu2.gif");
		Entity.gPlayerN = new Graphic("player.png");
		Entity.gPlayerNE = new Graphic("playerNE.png");
		Entity.gPlayerE = new Graphic("playerE.png");
		Entity.gPlayerSE = new Graphic("playerSE.png");
		Entity.gPlayerS = new Graphic("playerS.png");
		Entity.gPlayerSW = new Graphic("playerSW.png");
		Entity.gPlayerW = new Graphic("playerW.png");
		Entity.gPlayerNW = new Graphic("playerNW.png");
		Entity.gWallB = new Graphic("wallBreakable.png");
		Entity.gWallU = new Graphic("wallUnbreakable.png");
		Entity.gQuit = new Graphic("exit.png");
		Entity.gEnemyN = new Graphic("enemy.png");
		Entity.gEnemyNE = new Graphic("enemyNE.png");
		Entity.gEnemyE = new Graphic("enemyE.png");
		Entity.gEnemySE = new Graphic("enemySE.png");
		Entity.gEnemyS = new Graphic("enemyS.png");
		Entity.gEnemySW = new Graphic("enemySW.png");
		Entity.gEnemyW = new Graphic("enemyW.png");
		Entity.gEnemyNW = new Graphic("enemyNW.png");
		Entity.gVault = new Graphic("vault.png");
		Entity.gGoat = new Graphic("goat.png");
		ding = new Sound("ping.wav");
		pang = new Sound("pang.wav");
		enPang = new Sound("enPang.wav");
		siren = new Sound("siren.wav");
		player = new Player(this, 250, 250, 5, 5, 1);
		MenuButton bStart = new MenuButton(MenuButton.START, gstart, 125, 205);
		MenuButton bExit = new MenuButton(MenuButton.EXIT, Entity.gQuit, 125, 235);
		try {
			Level.init(getClass().getResourceAsStream("levels.dat"));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		long lastTime = System.nanoTime();
		long lastTimer = System.currentTimeMillis();
		double unprocessed = 0;
		double nsPerFrame = 1000000000.0 / 100;
		int frames = 0;

		while (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerFrame;
			boolean shouldRender = false;
			lastTime = now;
			nagFocus = !hasFocus();
			while (unprocessed >= 1 && (Level.current != null && !Level.current.complete) && !lose) {
				Entity.updateEntities();
				unprocessed--;
				shouldRender = true;
			}

			if (Level.goats == 0) {
				Level.goats = Level.totGoats;
				timeLeft = 0;
				secretEnd = true;
				score += 5000;
			}

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (shouldRender || (Level.current != null && Level.current.complete) || lose) {
				frames++;
				if (!startMenu && !nagFocus) {
					if (changeList) {
						removeAllListeners();
						addKeyListener(player);
						addMouseListener(player);
						addMouseMotionListener(player);
						changeList = false;
					}
					if (restartLevel && lose && !fbi) {
						lose = false;
						win = false;
						fbi = false;
						restartLevel = false;
						player.render = true;
						player.lives = 1;
						int curr = Level.current.id;
						timeLeft = 20;
						Level.current.unload();
						if (QuitButton.instance != null) {
							QuitButton.instance.destroy = true;
							QuitButton.instance = null;
						}
						Level.levels[curr - 1].load();
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (fbi) {
						screen.drawImage(gFbi, 0, 0);
						if (!secretEnd)
							score -= 100;
						swap(false, true);
						try {
							Thread.sleep(2500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						fbi = false;
						lose = true;
						win = false;
						continue;
					}

					if (nextLevel || (Level.current != null && Level.current.id < 3 && Level.current.enemies <= 0)) {
						System.out.println("Level complete.");
						lose = false;
						win = false;
						restartLevel = false;
						nextLevel = false;
						int curr = Level.current.id;
						timeLeft = 20;
						Level.current.unload();
						int m = text.size();
						for (int i = 0; i < m; i++) {
							text.remove(0);
						}
						if (curr < Level.levels.length) {
							Level.levels[curr].load();
							try {
								Thread.sleep(2);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

					///// RENDERING /////
					screen.fill(backgroundColor);

					if (Level.current != null) {
						screen.drawBackground(bg);
						switch (Level.current.id) {
							case 1:
								screen.drawBackground(tut1);
								break;
							case 2:
								screen.drawBackground(tut2);
								break;
							case 3:
								screen.drawBackground(tut3);
							default:
								break;
						}
					}

					if (win) {
						screen.drawImage(gold, 230, 2);
						if (QuitButton.instance == null)
							new QuitButton(this, 125, 191);
						QuitButton.instance.render();
					} else if (lose) {
						screen.drawImage(gold, 230, 2);
						if (QuitButton.instance == null)
							new QuitButton(this, 125, 191);
						QuitButton.instance.render();
					} else {
						screen.drawLine(inverse(backgroundColor), 0, 10, WIDTH, 10);
						Entity.renderEntities();
						if (!fbi)
							screen.drawImage(gold, 230, 2);
					}

					if (!lose && !win && Level.current != null && Level.current.complete && Level.current.id != Level.levels.length) {
						screen.drawImage(congratz, 25, 100);
						screen.drawImage(gold, 230, 2);
					} else if (!lose && !win && Level.current != null && Level.current.complete) {
						win = true;
						player.render = false;
						int m = text.size();
						for (int i = 0; i < m; i++) {
							text.remove(0);
						}
					}
				} else if (startMenu) {
					if (mCounter++ % 100 > 50)
						screen.drawBackground(menu1);
					else
						screen.drawBackground(menu2);
					if (changeList != startMenu) {
						removeAllListeners();
						addMouseListener(bStart);
						addMouseListener(bExit);
					}
					bExit.render();
					bStart.render();
				} else if (nagFocus) {
					screen.drawImage(gFoc, 100, 100);
				}

				swap(false, startMenu);
			}
			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				FPS = frames;
				frames = 0;
				if (!startMenu && shouldRender) {
					if (Level.current != null && Level.current.id > 2 && !Level.current.complete)
						timeLeft--;
					if (timeLeft < 0)
						timeLeft = 0;
					if (timeLeft == 0 && Level.current.id > 2 && !lose) {
						fbi = true;
					}
				}
			}

		}
		stop(0);
	}

	void swap(boolean loading, boolean skipAll) {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		int screenW = getWidth();
		int screenH = getHeight();
		int w = WIDTH * SCALE;
		int h = HEIGHT * SCALE;
		g.drawImage(screen.image, (screenW - w) / 2, (screenH - h) / 2, w, h, null);
		if (!loading && !skipAll) {
			g.setFont(new Font("Helvetica", Font.PLAIN, 7 * SCALE));
			if (renderFPS) {
				g.setColor(new Color(inverse(backgroundColor)));
				g.drawString("FPS: " + FPS, 2, 2 + 6 * SCALE);
				if (score < 0)
					g.setColor(Color.RED);
				g.drawString("" + Math.abs(score), 237 * SCALE, 2 + 6 * SCALE);
			}
			if (!win && Level.current != null && Level.current.id > 2) {
				g.setColor(new Color(inverse(backgroundColor)));
				g.drawString("Time: " + timeLeft, 40 * SCALE, 2 + 6 * SCALE);
			}
			if (!secretEnd && (lose || win)) {
				g.setColor(new Color(inverse(backgroundColor)));
				g.fillRect(48 * SCALE, 73 * SCALE, 214 * SCALE, 114 * SCALE);
				g.setColor(new Color(backgroundColor));
				g.fillRect(50 * SCALE, 75 * SCALE, 210 * SCALE, 110 * SCALE);
				g.setColor(new Color(inverse(backgroundColor)));
				for (int i = 0; i < text.size(); i++)
					text.remove(i--);
				g.setFont(new Font("Helvetica", Font.BOLD, 24 * SCALE));
				String s1 = "Congratulations!";
				String s2 = lose ? "You have died!" : "You beat the game!";
				g.drawString(s1, 60 * SCALE, 100 * SCALE);
				if (win)
					g.setFont(new Font("Helvetica", Font.BOLD, 20 * SCALE));
				g.drawString(s2, 65 * SCALE, 130 * SCALE);
				g.setFont(new Font("Helvetica", Font.BOLD, 12 * SCALE));
				g.drawString("Gold: " + score, 123 * SCALE, 155 * SCALE);
				if (lose)
					g.drawString("Click to try again.", 100 * SCALE, 178 * SCALE);
				if (win) {
					g.drawString("Click to return to main menu.", 75 * SCALE, 178 * SCALE);
					g.fillRect(6 * SCALE, 223 * SCALE, 288 * SCALE, 47 * SCALE);
					g.setColor(Color.WHITE);
					g.fillRect(8 * SCALE, 225 * SCALE, 284 * SCALE, 43 * SCALE);
					g.setColor(Color.BLACK);
					g.drawString("If you got this at Ludum Dare 25,", 10 * SCALE, 240 * SCALE);
					g.drawString("please leave a reaction/rating with your gold.", 10 * SCALE, 253 * SCALE);
					g.drawString("I would love to hear what you thought about this!", 10 * SCALE, 266 * SCALE);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (secretEnd && lose) {
				g.setColor(new Color(inverse(backgroundColor)));
				g.fillRect(48 * SCALE, 73 * SCALE, 214 * SCALE, 114 * SCALE);
				g.setColor(new Color(backgroundColor));
				g.fillRect(50 * SCALE, 75 * SCALE, 210 * SCALE, 110 * SCALE);
				g.setColor(new Color(inverse(backgroundColor)));
				for (int i = 0; i < text.size(); i++)
					text.remove(i--);
				g.setFont(new Font("Helvetica", Font.BOLD, 24 * SCALE));
				String s1 = "How could you?!";
				String s2 = "That's just pure evil!";
				g.drawString(s1, 60 * SCALE, 100 * SCALE);
				g.setFont(new Font("Helvetica", Font.BOLD, 20 * SCALE));
				g.drawString(s2, 60 * SCALE, 130 * SCALE);
				g.setFont(new Font("Helvetica", Font.BOLD, 12 * SCALE));
				g.drawString("Gold: " + score, 123 * SCALE, 155 * SCALE);
				g.drawString("Click to return to main menu.", 75 * SCALE, 178 * SCALE);
				g.fillRect(6 * SCALE, 223 * SCALE, 288 * SCALE, 47 * SCALE);
				g.setColor(Color.WHITE);
				g.fillRect(8 * SCALE, 225 * SCALE, 284 * SCALE, 43 * SCALE);
				g.setColor(Color.BLACK);
				g.drawString("If you got this at Ludum Dare 25,", 10 * SCALE, 240 * SCALE);
				g.drawString("please leave a reaction/rating with your gold.", 10 * SCALE, 253 * SCALE);
				g.drawString("I would love to hear what you thought about this!", 10 * SCALE, 266 * SCALE);
			}
			for (Text t : text) {
				g.setColor(t.color);
				g.drawString(t.string, t.x, t.y);
			}
			screen.drawLines(g);
		} else if (loading) {
			g.setFont(new Font("Helvetica", Font.PLAIN, 7 * SCALE));
			g.setColor(Color.BLACK);
			g.drawString("Zedutchgandalf's", 40 * SCALE, 105 * SCALE - 1);
			g.setFont(new Font("Helvetica", Font.BOLD, 50 * SCALE));
			g.drawString("VAULT", 60 * SCALE, 150 * SCALE);
			g.setFont(new Font("Helvetica", Font.BOLD, 12 * SCALE));
			g.drawString("For Ludum Dare 25", 95 * SCALE, 212 * SCALE);
			g.setFont(new Font("Helvetica", Font.PLAIN, 7 * SCALE));
			g.drawString("Loading", 137 * SCALE, 263 * SCALE);
		}
		if (startMenu) {
			if (anim.run) {
				anim.stop();
				//try to catch up
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			g.setFont(new Font("Helvetica", Font.PLAIN, 40 * SCALE));
			g.setColor(Color.BLACK);
			g.drawString("VAULT", 5 * SCALE, 45 * SCALE);
			g.setFont(new Font("Helvetica", Font.BOLD, 8 * SCALE));
			g.drawString("You have one mission: to become as rich as possible!", 5 * SCALE, 85 * SCALE);
			g.drawString("Evidently, this means you have to rob every single bank.", 5 * SCALE, 98 * SCALE);
			g.drawString("Get caught by the police and you lose 100 gold.", 5 * SCALE, 111 * SCALE);
			g.drawString("Get caught by a guard and you lose 50 gold.", 5 * SCALE, 124 * SCALE);
			g.drawString("You have 20 seconds before the police arrives and catches you.", 5 * SCALE, 137 * SCALE);
			g.drawString("This game was created in less than 48 hours for Ludum Dare 25.", 5 * SCALE, 150 * SCALE);
			g.drawString("Everything you see and hear is made by me, Zedutchgandalf.", 5 * SCALE, 163 * SCALE);
			g.drawString("Good Luck!", 5 * SCALE, 176 * SCALE);
			//g.drawString("or has been licensed to me.", 5 * SCALE, 128 * SCALE);
		}
		g.dispose();
		bs.show();
	}

	public void restart() {
		win = false;
		lose = false;
		fbi = false;
		startMenu = true;
		secretEnd = false;
		score = 0;
		removeAllListeners();
		for (int i = 0; i < Entity.entities.size(); i++) {
			if (!(Entity.entities.get(i) instanceof Player)) {
				Entity.entities.remove(i);
				i--;
			}
		}
		if (Level.levels[0] != null)
			Level.levels[0].load();
	}

	private void removeAllListeners() {
		KeyListener[] k = getKeyListeners();
		MouseListener[] m = getMouseListeners();
		MouseMotionListener[] mm = getMouseMotionListeners();
		if (k != null)
			for (int i = 0; i < k.length; i++) {
				removeKeyListener(k[i]);
			}
		if (m != null)
			for (int i = 0; i < m.length; i++) {
				removeMouseListener(m[i]);
			}
		if (mm != null)
			for (int i = 0; i < mm.length; i++) {
				removeMouseMotionListener(mm[i]);
			}
	}

	public static void removeText(int textId) {
		if (instance.text.size() > textId)
			instance.text.remove(textId);
	}

	public static int write(String text, int color, int x, int y) {
		instance.text.add(new Text(text, color, x, y));
		return instance.text.size() - 1;
	}

	public static void main(String[] args) {
		Main comp = new Main();
		JFrame app = new JFrame("Vault");
		app.add(comp);
		app.pack();
		app.setResizable(false);
		app.setLocationRelativeTo(null);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
		comp.start();
	}

	public void start() {
		running = true;
		new Thread(this, "Main").start();
	}

	public void stop(int code) {
		running = false;
		System.out.println("Exited with error code: " + code);
		System.exit(code);
	}

	void swap() {
		swap(false, false);
	}

	void swap(boolean loading) {
		swap(true, false);
	}

	public static int inverse(int color) {
		return 0xFFFFFF - color;
	}

	public Main() {
		instance = this;
		int screenY = Toolkit.getDefaultToolkit().getScreenSize().height;
		while (HEIGHT * SCALE > screenY) {
			SCALE--;
			if (SCALE == 1)
				break;
		}
		System.out.println("Resolution: " + WIDTH * SCALE + ", " + HEIGHT * SCALE);
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
	}
}

class Text {
	int x, y;
	String string;
	Color color;

	Text(String text, int color, int x, int y) {
		this.color = new Color(color);
		string = text;
		this.x = x;
		this.y = y;
	}
}
