package be.zedutchgandalf.ld48;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MenuButton implements MouseListener {
	static final int START = 0, EXIT = 1;
	int x, y, width, height, id;
	Graphic g;

	public MenuButton(int id, Graphic g, int x, int y) {
		this.id = id;
		this.g = g;
		this.x = x;
		this.y = y;
		width = 50;
		height = 25;
	}

	public void render() {
		Main.instance.screen.drawImage(g, x, y);
	}

	public void clicked() {
		if (id == START) {
			Main m = Main.instance;
			m.changeList = true;
			m.startMenu = false;
		} else if (id == EXIT) {
			Main.instance.stop(0);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int mx = e.getX() / Main.SCALE;
		int my = e.getY() / Main.SCALE;
		if (mx > x && mx < x + width && my > y && my < y + height)
			clicked();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
