package edu.mills.cs280.audiorunner;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.analysis.FFT;

public class PeakFinder{

	public static final int THRESHOLD_WINDOW_SIZE = 10;
	public static final float MULTIPLIER = 1.5f;

	private boolean useTestMusicData = false;

	//action performed in returnPeaks
	public PeakFinder(){}

	public void setTestable(boolean testable){
		useTestMusicData = testable;
	}

	//takes a list of of floats representing aggregate onset amplitudes
	//and prunes them by creating an average threshold of amplitude
	public List<Float> returnPeaks()
	{
		//this breaks threading - do not un comment
//		if(useTestMusicData){
//    		//System.out.print("****************************" + SerializableMusicData.getDefaultName());
//    		SerializableMusicData load = SerializableMusicData.load();
//    		MusicData.setSamples(load.getSamples());
//    		return load.getPeaks();
//    	}

		try{
			//MP3Decoder decoder = new MP3Decoder(  new FileInputStream (fileName ) );
			AudioAnalyzer decoder = new AudioAnalyzer(MusicData.getFileLocation());
			FFT fft = new FFT( 1024, 44100 );
			fft.window( FFT.HAMMING );
			float[] samples = new float[1024];
			float[] spectrum = new float[1024 / 2 + 1];
			float[] lastSpectrum = new float[1024 / 2 + 1];
			List<Float> spectralFlux = new ArrayList<Float>( );
			List<Float> threshold = new ArrayList<Float>( );
			List<Float> prunedSpectralFlux = new ArrayList<Float>();
			List<Float> peaks = new ArrayList<Float>();
			Boolean flip = true;


			float value = 0.0f;
			float flux = 0;
			while( decoder.singleSamples( samples ) > 0 )
			{
				fft.forward( samples );
				if(flip){
					System.arraycopy( fft.getSpectrum(), 0, spectrum, 0, spectrum.length );
					flux = 0;
					for( int i = 0; i < spectrum.length; i++ )	
					{
						value = (spectrum[i] - lastSpectrum[i]);
						flux += value < 0? 0: value;
					}
					spectralFlux.add( flux );
					flip = !flip;
				}
				else{
					System.arraycopy( fft.getSpectrum(), 0, lastSpectrum, 0, lastSpectrum.length );
					flux = 0;
					for( int i = 0; i < lastSpectrum.length; i++ )	
					{
						value = (lastSpectrum[i] - spectrum[i]);
						flux += value < 0? 0: value;
					}
					spectralFlux.add( flux );
					flip = !flip;
				}
			}

			for( int i = 0; i < spectralFlux.size(); i++ )
			{
				int start = Math.max( 0, i - THRESHOLD_WINDOW_SIZE );
				int end = Math.min( spectralFlux.size() - 1, i + THRESHOLD_WINDOW_SIZE );
				float mean = 0;
				for( int j = start; j <= end; j++ )
					mean += spectralFlux.get(j);
				mean /= (end - start);
				threshold.add( mean * MULTIPLIER );
			}

			//now prune the threshold sizes
			for( int i = 0; i < threshold.size(); i++ )
			{
				if( threshold.get(i) <= spectralFlux.get(i) )
					prunedSpectralFlux.add( spectralFlux.get(i) - threshold.get(i) );
				else
					prunedSpectralFlux.add( (float)0 );
			}

			//and finally choose the peaks
			for( int i = 0; i < prunedSpectralFlux.size() - 1; i++ )
			{
				if( prunedSpectralFlux.get(i) > prunedSpectralFlux.get(i+1) )
					peaks.add( prunedSpectralFlux.get(i) );
				else
					peaks.add( (float)0 );				
			}
			//       System.out.println(prunedSpectralFlux.toString());
			//       System.out.println(peaks.toString());
			//       System.out.println("size = " + peaks.size());
			//       

			decoder.dispose();

			return peaks;

			//       Plot plot = new Plot( "Spectral Flux", 1024, 512 );
			//       plot.plot( spectralFlux, 1, Color.red );		
			//       plot.plot( threshold, 1, Color.green ) ;
			//       new PlaybackVisualizer( plot, 1024, new MP3Decoder( new FileInputStream( fileName ) ) );
			//       
		}catch(Exception e){
			//What should I do here???
			System.out.println("FILE FAILED TO LOAD");
			return null;
		}

	}
}