package be.zedutchgandalf.ld48;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Graphic {
	int[] tex;
	int width, height;

	public Graphic(String file) {
		BufferedImage img;
		try {
			img = ImageIO.read(Graphic.class.getResource(file));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load " + file);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(file + " = null!");
		}
		tex = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		width = img.getWidth();
		height = img.getHeight();
		System.out.println("Succesfully loaded " + file + "[" + width + ", " + height + "].");
	}

	public int length() {
		return tex.length;
	}

	public int get(int index) {
		return tex[index];
	}
}
