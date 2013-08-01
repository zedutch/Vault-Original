package be.zedutchgandalf.ld48;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen {
	private static final int maskColor = 0xFFFF00FF;
	public BufferedImage image;
	int[] pixels;
	int[] lines;
	Main main;

	public Screen(Main main) {
		image = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		this.main = main;
		lines = new int[0];
		/*
		Graphic splash = new Graphic("splash.png", false);
		if (splash.tex == null) {
			System.err.println("Splash == null!");
			return;
		}
		drawBackground(splash);*/
	}

	public void draw(int color, int x0, int y0, int x1, int y1) {
		if (x0 > x1) {
			int t = x0;
			x0 = x1;
			x1 = t;
		}
		if (y0 > y1) {
			int t = y0;
			y0 = y1;
			y1 = t;
		}
		if (x0 != x1 && y0 != y1)
			for (int j = y0; j < y1; j++) {
				for (int i = x0; i < x1; i++) {
					pixels[j * Main.WIDTH + i] = color;
				}
			}
		else if (y0 == y1)
			for (int i = x0; i < x1; i++) {
				pixels[y0 * Main.WIDTH + i] = color;
			}
		else if (x0 == x1)
			for (int i = y0; i < y1; i++) {
				pixels[i * Main.WIDTH + x0] = color;
			}
	}

	public void drawLine(int color, int x0, int y0, int x1, int y1) {
		drawLine(color, x0, y0, x1, y1, false);
	}

	public void drawLine(int color, int x0, int y0, int x1, int y1, boolean dontScaleLast) {
		if (lines != null) {
			int[] temp = new int[lines.length + 5];
			for (int i = 0; i < lines.length; i++) {
				temp[i] = lines[i];
			}
			temp[lines.length] = color;
			temp[lines.length + 1] = x0 * Main.SCALE;
			temp[lines.length + 2] = y0 * Main.SCALE;
			temp[lines.length + 3] = x1 * (dontScaleLast ? 1 : Main.SCALE);
			temp[lines.length + 4] = y1 * (dontScaleLast ? 1 : Main.SCALE);
			lines = temp;
		} else {
			lines = new int[5];
			lines[0] = color;
			lines[1] = x0 * Main.SCALE;
			lines[2] = y0 * Main.SCALE;
			lines[3] = x1 * (dontScaleLast ? 1 : Main.SCALE);
			lines[4] = y1 * (dontScaleLast ? 1 : Main.SCALE);
		}
	}

	public void drawLines(Graphics g) {
		for (int i = 0; i < lines.length; i += 5) {
			g.setColor(new Color(lines[i]));
			g.drawLine(lines[i + 1], lines[i + 2], lines[i + 3], lines[i + 4]);
			lines = new int[0];
		}
	}

	public void drawImage(Graphic image, int x, int y) {
		for (int i = 0; i < image.length(); i++) {
			int b = i / image.width + y;
			int a = i % image.width + x;
			if (b * Main.WIDTH + a >= pixels.length || a >= Main.WIDTH || b * Main.WIDTH + a < 0)
				break;
			int c = image.get(i);//pixels[b * Main.WIDTH + a] | image.get(i);
			if (c != maskColor)
				pixels[b * Main.WIDTH + a] = c;
		}
	}

	public void fill(int color) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = color;
		}
	}

	public void drawBackground(Graphic bg) {
		for (int i = 0; i < bg.length(); i++) {
			int b = i / Main.WIDTH;
			int a = i % Main.WIDTH;
			if (b * Main.WIDTH + a >= pixels.length || a >= Main.WIDTH)
				break;
			//int c = pixels[b * Main.WIDTH + a] | bg.get(i);
			pixels[b * Main.WIDTH + a] = bg.get(i);//c;
		}
	}
}
