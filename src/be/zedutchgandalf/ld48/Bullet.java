package be.zedutchgandalf.ld48;

public class Bullet extends Entity {
	Entity origin;

	public Bullet(Main main, double x, double y, int width, int height, double dx, double dy, Entity origin) {
		super(main, x, y, width, height);
		this.dx = dx;
		this.dy = dy;
		this.origin = origin;
		if (origin instanceof Player)
			main.pang.play();
		else
			origin.getPang().play();
	}

	@Override
	public synchronized void update() {
		super.update();
		render = origin.render;
		for (int i = 0; i < Entity.entities.size(); i++) {
			Entity e = Entity.entities.get(i);
			if (e != this && e != origin && !(e instanceof Ray) && e.intersect(this)) {
				e.hit();
				destroy = true;
			} else if (e instanceof Ray && e.intersect(this)) {
				e.destroy = true;
				render = true;
			}
		}
		if (x == 0 || y == 0 || x + width == Main.WIDTH || y + height == Main.HEIGHT)
			destroy = true;
	}

	public void hit() {
		destroy = true;
	}
}
