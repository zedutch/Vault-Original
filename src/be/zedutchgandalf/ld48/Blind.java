package be.zedutchgandalf.ld48;

public class Blind extends Enemy {

	public Blind(Main m, double x, double y, int width, int height) {
		super(m, x, y, width, height, 1, 0);
		render = true;
	}

	@Override
	public void update() {}

}
