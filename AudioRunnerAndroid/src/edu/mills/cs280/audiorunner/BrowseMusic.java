package edu.mills.cs280.audiorunner;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.util.Log;

public class BrowseMusic extends Activity {
	/** Called when the activity is first created. */
	private ListView musiclist;
	private TextView selectedSong,duration,name;
	private Button startGame;
	private Cursor musicCursor;
	private int musicColumnIndex;
	private int count;
	//MediaPlayer mMediaPlayer;
	public String filename;
	
	static final private int LOAD_ACTIVITY = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		initMusicList();	
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==LOAD_ACTIVITY){
    		System.gc();
        }
    }

	private void initMusicList() {
		System.gc();
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Video.Media.DURATION };
		musicCursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
		selectedSong = (TextView) findViewById(R.id.selected_song);
		if(musicCursor == null){
			//TODO Ideally prompt user to a new screen
			selectedSong.setText("There is no song on your SD card");
			startGame.setEnabled(false);
		}else{
			count = musicCursor.getCount();
			musiclist = (ListView) findViewById(R.id.PhoneMusicList);
			//musiclist.layout(0, 0,getWindowManager().getDefaultDisplay().getWidth(), (int) (0.7*getWindowManager().getDefaultDisplay().getHeight()) );
			
			duration = (TextView) findViewById(R.id.song_duration);
			name = (TextView)findViewById(R.id.song_name);
			
			startGame = (Button) findViewById(R.id.Start_Game_Button);
			startGame.setEnabled(false);
			musiclist.setAdapter(new MusicAdapter(getApplicationContext()));
			musiclist.setOnItemClickListener(musicgridlistener);
			
			//start the game activity
			startGame.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					Intent loadIntent = new Intent(getApplicationContext(), LoadMusic.class);
	                loadIntent.putExtra("song", filename);
					startActivityForResult(loadIntent, LOAD_ACTIVITY);
				}
			});
		}
	}

	private OnItemClickListener musicgridlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View v, int position, long id) {
			System.gc();
			
			musicColumnIndex = musicCursor
			.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			musicCursor.moveToPosition(position);
			//TODO: filename tells where the music is
			//needs to pass it onto GameHandler
			filename = musicCursor.getString(musicColumnIndex);
			selectedSong.setText(filename);

			musicColumnIndex = musicCursor
			.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			musicCursor.moveToPosition(position);
			String songName = musicCursor.getString(musicColumnIndex);
			name.setText(songName);
			
			musicColumnIndex = musicCursor
			.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
			musicCursor.moveToPosition(position);
			String songDuration = musicCursor.getString(musicColumnIndex);
			duration.setText(convertTime(songDuration));
			
			startGame.setEnabled(true);
		}
		
	};

	public class MusicAdapter extends BaseAdapter {
		private Context mContext;
		
		public MusicAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			
			String title,artist = null;
			
			musicColumnIndex = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			musicCursor.moveToPosition(position);
			title = musicCursor.getString(musicColumnIndex);
            tt.setText(title);                            

			musicColumnIndex = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			musicCursor.moveToPosition(position);
			artist = musicCursor.getString(musicColumnIndex);
			bt.setText(artist);
          
			return v;
		}
	}
	
	public String convertTime(String ms){
		int milisec = Integer.parseInt(ms);
		int s = milisec/1000;
		int min = s/60;
		int sec = s%60;
		return min+":"+(sec >= 10? sec: "0"+sec);
	}
	
	public boolean isMP3(String fileName){
		return fileName.contains("mp3");
	}

}