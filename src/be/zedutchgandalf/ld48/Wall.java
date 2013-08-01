package be.zedutchgandalf.ld48;

public class Wall extends Entity implements indestructable {
	public Wall(Main m, double x, double y) {
		super(m, x, y, 5, 5, -1);
	}
}
