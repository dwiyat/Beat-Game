package edu.mills.cs280.audiorunner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class AudioAnalyzer{

	private File file;
	private InputStream inputStream;
	private Bitstream bitstream;
	private Decoder decoder;

	public AudioAnalyzer(String fileLocation){
		file = new File(fileLocation);
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file), 1024);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		bitstream = new Bitstream(inputStream);

		decoder = new Decoder();
	}

	public int singleSamples(float[] samples)
	{

		try {
			Header header;
			header = bitstream.readFrame();

			SampleBuffer output;
			try{
				output = (SampleBuffer)decoder.decodeFrame(header, bitstream);
			}catch(Exception e){
				//TODO find a better way to exit than empty catch
				return 0;
			}
			
			//System.out.print("samples.length " + samples.length);
			for(int i = 0; i < samples.length; i++){
				samples[i] = output.getBuffer()[i];
				//System.out.print(samples[i]);
			}

			bitstream.closeFrame();

			//System.out.println("");
			//System.out.println("samples return: " + samples.toString());
//			for(float i:samples){
//				System.out.print(i + " ");
//			}
//			System.out.println("");
			return 1;

		} catch (BitstreamException e) {
			//e.printStackTrace();
			//TODO find a better way to exit than empty catch
			return 0;
		}

	}

	//currently unused
	public int readSamples(float[] samples)
	{

		boolean done = false;

		try {

			while(!done){
				Header header;
				header = bitstream.readFrame();

				SampleBuffer output;
				try{
					output = (SampleBuffer)decoder.decodeFrame(header, bitstream);
				}catch(Exception e){
					break;
				}


				//System.out.println(output.getBufferLength());

				//for(int i = 0; i < output.getBuffer().length; i++){
				//samples[i] = 
				//}
				for(short e : output.getBuffer()){
					System.out.print(e + " , ");
				}

				bitstream.closeFrame();

				System.out.println();
			}

			System.out.println("SUCCESS!!!");

			return 1;

		} catch (BitstreamException e) {
			//e.printStackTrace();
			return 0;
		}

	}
	
	public void dispose(){
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	

	public static byte[] decode(String path, int startMs, int maxMs)
	throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);

		float totalMs = 0;
		boolean seeking = true;

		File file = new File(path);
		InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
		try {
			Bitstream bitstream = new Bitstream(inputStream);
			Decoder decoder = new Decoder();

			boolean done = false;
			while (! done) {
				Header frameHeader = bitstream.readFrame();
				if (frameHeader == null) {
					done = true;
				} else {
					totalMs += frameHeader.ms_per_frame();
					if (totalMs >= startMs) {
						seeking = false;
					}

					if (! seeking) {
						SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
						//if (output.getSampleFrequency() != 44100
						//		|| output.getChannelCount() != 2) {
						//	throw new DecoderException("mono or non-44100 MP3 not supported", throwableFromStop);
						//}

						short[] pcm = output.getBuffer();
						for (short s : pcm) {
							outStream.write(s & 0xff);
							outStream.write((s >> 8 ) & 0xff);
						}
					}

					if (totalMs >= (startMs + maxMs)) {
						done = true;
					}
				}
				bitstream.closeFrame();
			}

			return outStream.toByteArray();
		} catch (BitstreamException e) {
			throw new IOException("Bitstream error: " + e);
		} catch (DecoderException e) {
			//Log.w(TAG, "Decoder error", e);
			//throw new DecoderException(e);
		}
		return null;


	}*/
}
