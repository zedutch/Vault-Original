package be.zedutchgandalf.ld48;

public class LoadingAnim implements Runnable {
	Graphic spl1;
	Graphic spl2;
	public volatile boolean run;
	Screen screen;
	public int ani;

	public void start(Screen scr) {
		spl1 = new Graphic("splash1.gif");
		spl2 = new Graphic("splash2.gif");
		screen = scr;
		Main.instance.swap(true);
		run = true;
		new Thread(this, "Animation").start();
	}

	@Override
	public void run() {
		while (run) {
			ani++;
			if (ani % 100 > 50)
				screen.drawBackground(spl1);
			else
				screen.drawBackground(spl2);
			Main.instance.swap(true);
		}
	}

	public void stop() {
		run = false;
	}

}
