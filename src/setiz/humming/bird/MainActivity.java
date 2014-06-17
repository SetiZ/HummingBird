package setiz.humming.bird;

import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity {

	// public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	private ProgressDialog dlg;
	private TextView mTextView;
	private NfcAdapter mNfcAdapter;
	private String sound;
	private ParseFile music;
	private String musicUrl;
	private MediaPlayer mediaPlayer;
	private int playbackPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.tv);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
			Toast.makeText(this, "no nfc", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// handleIntent(getIntent());
		resolveIntent(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent intent = new Intent(this, this.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		IntentFilter[] filters = null;
		String[][] techListArray = null;
		mNfcAdapter.enableForegroundDispatch(this, pIntent, filters,
				techListArray);

		// setupForegroundDispatch(this, mNfcAdapter);
	}

	@Override
	protected void onPause() {
		/**
		 * Call this before onPause, otherwise an IllegalArgumentException is
		 * thrown as well.
		 */
		// stopForegroundDispatch(this, mNfcAdapter);

		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				NdefMessage[] messages = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					messages[i] = (NdefMessage) rawMsgs[i];
				}
				// Exemple basique de récupération des données dans le tableau
				String str = new String(
						messages[0].getRecords()[0].getPayload());
				parseText(str);
				try {
					Log.i("music", musicUrl);
				} catch (NullPointerException e) {

				}
				// mTextView.setText(sound);
			}
		}
	}

	private void parseText(String str) {
		Log.i("uri", str);
		int pos = str.lastIndexOf('/') + 1;
		String res = str.substring(pos);
		Log.i("uri", res);
		// MUSIC

		ParseQuery<ParseObject> query = ParseQuery.getQuery("music");
		query.whereEqualTo("hb", Integer.valueOf(res));

		// Set up a progress dialog
		dlg = new ProgressDialog(MainActivity.this);
		dlg.setTitle("Please wait.");
		dlg.setMessage("Reading tag...");
		dlg.show();

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> scoreList, ParseException e) {
				if (e == null) {
					dlg.hide();
					Log.d("score", "Retrieved " + scoreList.size() + " scores");
					for (ParseObject score : scoreList) {
						sound = score.getString("name");
						Log.i("score", "" + score.getString("name"));
						music = score.getParseFile("sound");
						musicUrl = music.getUrl();
						Log.i("music", musicUrl);
						mTextView.setText(sound);
					}
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

		/*
		 * music.getDataInBackground(new GetDataCallback() { public void
		 * done(byte[] data, ParseException e) { if (e == null) { Log.i("tag",
		 * "" + data); } else { Log.d("music", "Error: " + e.getMessage()); } }
		 * });
		 */
		// return musicUrl;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		/**
		 * This method gets called, when a new Intent gets associated with the
		 * current activity instance. Instead of creating a new activity,
		 * onNewIntent will be called. For more information have a look at the
		 * documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to
		 * the device.
		 */
		resolveIntent(intent);
	}

	public void doClick(View view) {
		// if (musicUrl != null) {
		LinearLayout playerButtons = (LinearLayout) findViewById(R.id.playerButtons);
		playerButtons.setVisibility(View.VISIBLE);
		switch (view.getId()) {
		case R.id.startPlayerBtn:
			try {
				playAudio("http://files.parsetfss.com/d5529496-a020-49f0-b9e7-69851d5e7d50/tfss-03a755c2-3bd0-4428-8456-26383b27f7dd-05.Daft%20Punk%20feat.%20Julian%20Casablancas%20-%20Instant%20Crush.mp3"
);
				// playLocalAudio();
				// playLocalAudio_UsingDescriptor();
				Log.i("clic", "clic");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.pausePlayerBtn:
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				playbackPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.pause();
			}
			break;
		case R.id.restartPlayerBtn:
			if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
				mediaPlayer.seekTo(playbackPosition);
				mediaPlayer.start();
			}
			break;
		case R.id.stopPlayerBtn:
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				playbackPosition = 0;
			}
			break;
		}
		// }
	}

	private void playAudio(String url) throws Exception {
		killMediaPlayer();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(url);
		mediaPlayer.prepare();
		mediaPlayer.start();
	}

	/*
	 * private void playLocalAudio() throws Exception { mediaPlayer =
	 * MediaPlayer.create(this, R.raw.music_file); mediaPlayer.start(); }
	 */

	/*
	 * private void playLocalAudio_UsingDescriptor() throws Exception {
	 * 
	 * AssetFileDescriptor fileDesc = getResources().openRawResourceFd(
	 * R.raw.music_file); if (fileDesc != null) {
	 * 
	 * mediaPlayer = new MediaPlayer();
	 * mediaPlayer.setDataSource(fileDesc.getFileDescriptor(),
	 * fileDesc.getStartOffset(), fileDesc.getLength());
	 * 
	 * fileDesc.close();
	 * 
	 * mediaPlayer.prepare(); mediaPlayer.start(); } }
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		killMediaPlayer();
	}

	private void killMediaPlayer() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}