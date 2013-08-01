package be.zedutchgandalf.ld48;

public class Vault extends Entity implements indestructable {

	public Vault(Main m, double x, double y) {
		super(m, x, y, 5, 5, -1, 0xFFE18E2E);
	}

	public void get() {
		if (Level.current != null && Main.instance.timeLeft > 0) {
			Level.current.complete = true;
			Main.score += 300;
			main.ding.play();
		}
	}

	public void update() {}
}
