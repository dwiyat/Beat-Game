package edu.mills.cs280.audiorunner;

public class FindThreshTest {
	
	public static void main( String[] argv ){
		MusicData.setFile("data/music/test.mp3");
		PeakFinder george = new PeakFinder();
		System.out.println(george.returnPeaks().toString());
		System.out.println(george.returnPeaks().size());
	}
}
