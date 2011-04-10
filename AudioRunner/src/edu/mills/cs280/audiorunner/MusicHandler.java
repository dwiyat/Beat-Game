package edu.mills.cs280.audiorunner;

import com.badlogic.gdx.audio.Music;

public class MusicHandler {
	static public final float FRAMERATE = 60.0f;
	static private final float TIMESCALE = 1000/FRAMERATE;  //Milliseconds / Desire FrameRate

	static private Music music;
	static private long prevTime = 0L;
	static private long currTime = 0L;
	static private long timeDiff = 0L;

	private MusicHandler(){
	}

	public static void setMusic(Music music){
		MusicHandler.music = music;
		currTime = System.currentTimeMillis();
		prevTime = System.currentTimeMillis();
	}

	public static float getTime(){
		return music.getPosition();
	}

	public static void updateTime(){		
		currTime = System.currentTimeMillis();
		if(currTime != prevTime){
			timeDiff = currTime - prevTime;
			prevTime = currTime;
		}
	}

	public static float getTransitionScale(){
		return timeDiff/TIMESCALE;
	}
}