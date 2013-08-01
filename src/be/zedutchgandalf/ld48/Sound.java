package be.zedutchgandalf.ld48;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Sound {
	Clip clip;
	AudioInputStream is;

	public Sound(String file) {
		try {
			clip = AudioSystem.getClip();
			is = AudioSystem.getAudioInputStream(Sound.class.getResourceAsStream(file));
			clip.open(is);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Succesfully loaded " + file);
	}

	public void stop() {
		clip.stop();
	}

	public void reload() {
		clip.stop();
		clip.close();
		try {
			clip = AudioSystem.getClip();
			clip.open(is);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void play() {
		clip.stop();
		clip.start();
	}
}
