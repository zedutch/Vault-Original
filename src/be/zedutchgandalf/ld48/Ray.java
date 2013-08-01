package be.zedutchgandalf.ld48;

public class Ray extends Entity {
	Entity origin;

	public Ray(Main main, double x, double y, int width, int height, double dx, double dy, Entity o) {
		super(main, x, y, width, height);
		this.dx = dx;
		this.dy = dy;
		origin = o;
	}

	@Override
	public void update() {
		super.update();
		int m = Entity.entities.size();
		for (int i = 0; i < m; i++) {
			if (i >= Entity.entities.size())
				break;
			Entity e = Entity.entities.get(i);
			if (e.intersect(this))
				if (e instanceof Ray || e.equals(origin))
					continue;
				else if (e instanceof Bullet || e instanceof Wall || e instanceof Block || e instanceof Enemy || e instanceof Player) {
					e.seen(origin);
					destroy = true;
				} else
					e.seen(origin);
		}
		if (x == 0 || y == 0 || x + width == Main.WIDTH || y + height == Main.HEIGHT)
			destroy = true;
	}

	@Override
	public void render() {
		if (Level.current != null && Level.current.showFrustum && origin.render)
			main.screen.draw(0xFF000000, (int) x, (int) y, (int) x + width, (int) y + width);
	}
}
