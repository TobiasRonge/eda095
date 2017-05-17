package client;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Audio{
	
	private Vector<Clip> clips;
	private Clip bg, bg_win;
	private Clip shoot;
	private byte[] shootAudio;
	 
	public Audio(){
		clips = new Vector<Clip>();
		loadShootSound();
	}
	
	public void loadShootSound(){
		AudioInputStream audioStream;
		try {
			audioStream = AudioSystem.getAudioInputStream(new File("bin/client/shoot.wav"));
		
			AudioFormat format2 = audioStream.getFormat();
			
			int size = (int) (audioStream.getFrameLength()*format2.getFrameSize());
			
			DataLine.Info info2 = new DataLine.Info(Clip.class, format2,size);
			
			Clip clip = (Clip) AudioSystem.getLine((DataLine.Info)info2);
			clips.add(clip);
			shootAudio = new byte[size];
			audioStream.read(shootAudio, 0, size);
	        clip.open(format2, shootAudio, 0, size);
			shoot = clip;

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
				
	}

	public Clip playSound(String fileUrl,boolean loop){
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(fileUrl));
			AudioFormat format2 = audioStream.getFormat();
			
			int size = (int) (audioStream.getFrameLength()*format2.getFrameSize());
			
			DataLine.Info info2 = new DataLine.Info(Clip.class, format2,size);
			
			Clip clip = (Clip) AudioSystem.getLine((DataLine.Info)info2);
			clips.add(clip);
			byte[] audio = new byte[size];
			audioStream.read(audio, 0, size);
            clip.open(format2, audio, 0, size);
            if(loop)
            	clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
			/*SourceDataLine line2 = (SourceDataLine) AudioSystem.getLine(info);
			line2.open(format2);
			line2.start();
			*/
            return clip;

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void startBackgroundMusic(){
		if(bg_win!=null){
			bg_win.close();
		}
		bg = playSound("bin/client/music.wav",true);
	}
	
	public void startWinMusic(){
		bg.close();
		bg_win = playSound("bin/client/win.wav",true);
	}
	
	public void playShootSound(){
		shoot.stop();
		shoot.setFramePosition(0);
        shoot.start();
	}
	
	public void close(){
		for(Clip c:clips){
			c.close();
		}
	}
}
