package edu.mills.cs280.audiorunner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadMusic extends Activity{

	public String musicFile;
	private LinearLayout linProgressBar;
	private static int GAME  = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_bar);

		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{
			musicFile = extras.getString("song");
		}

		linProgressBar = (LinearLayout) findViewById(R.id.lin_progress_bar);
		linProgressBar.setVisibility(View.VISIBLE);

		new decodeMusic().execute();

	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GAME){
    		finish();
        }
    }
	private class decodeMusic extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if(musicFile!=null){
				MusicData.setFile(musicFile);
			}
			MusicData.setDuration(298000f);	//*****NEEDS TO GET THE ACTUAL SONG DURATION!!!

			AssetManager assetManager = getAssets();
			InputStream fis;
			ObjectInputStream in;

			try {
				fis = assetManager.open("data/testpeaks.ar");
				in = new ObjectInputStream(fis);
				@SuppressWarnings("unchecked")
				List<Float> peaks = (List<Float>)in.readObject();
				in.close();

				MusicData.setpeaks(peaks);

			}
			catch (Exception e) {
				System.out.println(e);
			}

			try {
				fis = assetManager.open("data/testsamples.ar");
				in = new ObjectInputStream(fis);
				@SuppressWarnings("unchecked")
				ArrayList<float[]> samples = (ArrayList<float[]>)in.readObject();
				in.close();
				MusicData.setSamples(samples);

			}
			catch (Exception e) {
				System.out.println(e);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			Toast.makeText(getBaseContext(), 
					"Decoding finished", 
					Toast.LENGTH_SHORT).show();

			Intent gameIntent = new Intent(getApplicationContext(), AudioRunnerActivity.class);
			if(musicFile!=null){
				gameIntent.putExtra("song", musicFile);
			}
			startActivityForResult(gameIntent, GAME);
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

	}
}
