package be.zedutchgandalf.ld48;

import java.util.ArrayList;

public class Entity {
	int width, height, lives, color;
	Main main;
	double dx, dy, x, y;
	Sound pang;
	boolean destroy, render;
	static ArrayList<Entity> entities;
	static Graphic gWallU, gWallB, gVault, gEnemyN, gEnemyNE, gEnemyE, gEnemySE, gEnemyS, gEnemySW, gEnemyW, gEnemyNW, gPlayerN, gPlayerNE, gPlayerE, gPlayerSE, gPlayerS, gPlayerSW, gPlayerW,
			gPlayerNW, gQuit, gGoat;

	public Entity(Main main) {
		this(main, 0, 0, 5, 5, 2, 0xFF0000FF);
	}

	public Entity(Main m, double x, double y, int width, int height) {
		this(m, x, y, width, height, 2, 0xFF0000FF);
	}

	public Entity(Main m, double x, double y, int width, int height, int lives) {
		this(m, x, y, width, height, lives, 0xFF0000FF);
	}

	public Entity(Main m, double x, double y, int width, int height, int lives, int color) {
		main = m;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.lives = lives;
		this.color = color;
		if (entities == null)
			entities = new ArrayList<Entity>();
		entities.add(this);
		render = false;
	}

	public void hit() {
		if (lives == -1 || this instanceof indestructable)
			return;
		lives--;
		if (lives <= 0) {
			if (this instanceof Player) {
				Main.score -= 50;
				render = false;
				Main.instance.lose = true;
			} else if (this instanceof Goat) {
				Level.goats--;
				destroy = true;
			} else if (!(this instanceof Wall))
				destroy = true;
		}
	}

	public boolean intersect(Entity e) {
		return intersect(e.x, e.y, e.width, e.height);
	}

	public boolean intersect(double x, double y, int width, int height) {
		return (x + width >= this.x && x <= this.x + this.width && y + height >= this.y && y <= this.y + this.height);
	}

	public void seen(Entity e) {
		if (e.equals(Main.instance.player))
			render = true;
	}

	public void update() {
		x += dx;
		y += dy;
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x + width > Main.WIDTH)
			x = Main.WIDTH - width;
		if (y + height > Main.HEIGHT)
			y = Main.HEIGHT - height;
	}

	public void render() {
		if (this instanceof Block && render)
			main.screen.drawImage(gWallB, (int) x, (int) y);
		else if (this instanceof Wall && render)
			main.screen.drawImage(gWallU, (int) x, (int) y);
		else if (this instanceof Vault && render)
			main.screen.drawImage(gVault, (int) x, (int) y);
		else if (this instanceof Goat && render)
			main.screen.drawImage(gGoat, (int) x, (int) y);
		else if (render)
			main.screen.draw(color, (int) x, (int) y, (int) x + width, (int) y + height);
	}

	public static synchronized void updateEntities() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update();
			if (entities.get(i).destroy)
				entities.remove(i--);
		}
	}

	public static void renderEntities() {
		for (int i = 0; i < entities.size(); i++)
			if (entities.size() > i)
				if (entities.get(i) != null)
					entities.get(i).render();
	}

	@Override
	public String toString() {
		return "Entity[" + x + ", " + y + "]";
	}

	public Sound getPang() {
		if (pang == null)
			return pang = new Sound("enPang.wav");
		else
			return pang;
	}

}
