package game.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * @author kenny
 * a singleton class used for playing game sounds
 */
public class SoundPlayer {
	private static final String DATA_PATH = "";
	private static final String FILE_TYPE = "wav";
	private static final SoundPlayer instance = new SoundPlayer();
	private Map<Sound, Clip> audioMap;
	
	/**
	 * Returns true if the sound is played, false otherwise
	 * @param sound
	 * @return
	 */
	public boolean playSound(Sound sound){
		if(!audioMap.containsKey(sound)) return false;
		final Clip c = audioMap.get(sound);
		c.stop();
		c.setFramePosition(0);
		new Thread(){
			@Override public void run(){
				c.start();
				try {
					Thread.sleep(c.getMicrosecondLength()/1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				c.stop();
			}
		}.start();
		return true;
	}
	
	public static SoundPlayer getInstance(){
		return instance;
	}
	/**
	 * Loads up the sounds
	 */
	private SoundPlayer(){
		audioMap = new HashMap<Sound, Clip>();
		for(Sound s : Sound.values()){
			try {
				Clip clip = AudioSystem.getClip();
				//clip.open(AudioSystem.getAudioInputStream(s.getFile()));
				clip.open(AudioSystem.getAudioInputStream(SoundPlayer.class.getClassLoader() 
						.getResource(s.getFile().getName())));
				clip.stop();
				audioMap.put(s, clip);
			} catch (Exception e){
				System.err.println("Unable to load sound: "+s.getFile().getAbsolutePath()+"\n " +
						"This is most likely a classpath issue.");
			}
		}
	}
	
	/**
	 * Sounds don't store the actual sound but rather a file path for a sound to be played
	 * To play sounds, use the SoundPlayer's playSound method
	 * @author kenny
	 *
	 */
	public enum Sound{
		LEFT_PADDLE("leftpaddle"), RIGHT_PADDLE("rightpaddle"), HIT_WALL("wall"),
			SERVE("serve"), GAME_OVER("gameover"),START("start");
		
		private File file;
		private Sound(String name){
			//file = new File(SoundPlayer.class.getClassLoader().getResource(name+"."+FILE_TYPE));
			file = new File(DATA_PATH+name+"."+FILE_TYPE);
		}
		
		/**
		 * Returns the file associated with this sound clip
		 * @return
		 */
		public File getFile(){
			return file;
		}
	}
}
