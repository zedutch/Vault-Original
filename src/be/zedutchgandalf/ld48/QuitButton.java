package be.zedutchgandalf.ld48;

public class QuitButton extends Entity {
	static QuitButton instance;

	public QuitButton(Main m, double x, double y) {
		super(m, x, y, 50, 25);
		instance = this;
	}

	@Override
	public void render() {
		render = true;
		Main.instance.screen.drawImage(gQuit, (int) x, (int) y);
	}

}
