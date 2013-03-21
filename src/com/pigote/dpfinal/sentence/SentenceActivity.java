package com.pigote.dpfinal.sentence;

import java.util.Locale;

import com.pigote.dpfinal.DPfinal;
import com.pigote.dpfinal.R;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class SentenceActivity extends ListActivity implements OnInitListener {

	private PopupWindow pw;
	private String currentWord;
	
	private PopupHandler noWordHandler;
	private PopupHandler newWordHandler;
	
	private TextToSpeech talker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		talker = new TextToSpeech(this, this);

		super.onCreate(savedInstanceState);
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(DPfinal.EXTRA_MESSAGE);
	    String[] values = message.split(" ");
	    
	    // Create the list view
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	    
	    //initialize my CoR adapters
	    noWordHandler = new NoWordHandler();
	    newWordHandler = new NewWordHandler();
	    
	    newWordHandler.setSuccessor(noWordHandler);
	    noWordHandler.setSuccessor(null);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onDestroy() {
		if (talker != null) {
			talker.stop();
			talker.shutdown();
		}
		super.onDestroy();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		initiatePopupWindow(item);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = talker.setLanguage(Locale.US);
	        if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	        	Log.e("TTS", "This Language is not supported");
	        } 
	 
	    } else {
	            Log.e("TTS", "Initilization Failed!");
	    }
	}
	
	private void initiatePopupWindow(String s) {
		String tmpDef = DPfinal.getDBHandler().getDefinition(s);
        currentWord = s.toLowerCase();
        //Use Chain of Responsibility to handle Popup Window
        newWordHandler.handlePopup(s, tmpDef, this, pw);
        pw = PopupHandler.getPw();
	}
	 
	public void tryClose(View v) {
	    pw.dismiss();
	}
	
	public void tryPlay(View v) {
		Uri myUri = DPfinal.getDBHandler().getUri(currentWord);
		if(myUri!=null){
		MediaPlayer mp = MediaPlayer.create(this, myUri);
		//MediaPlayer.create sometimes returns null because
		//the file uses WAVE 8,000Hz MP3 8 kbit/s format, while android 2.3.3 supports only 8- and 16-bit linear PCM
		mp.start();
		} else {
			//fall back to text to speech but this shouldn't happen so we log it
			Log.d("myDebug", "tryPlay missing! " + currentWord);
			talker.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	
	public void tryAdd(View v){
		//Save talker to wav, update uri
		String filename = DPfinal.getActivity().getExternalFilesDir("wavs").toString();
		filename = filename + "/" + currentWord + ".wav" ;
		if (0==talker.synthesizeToFile(currentWord, null, filename))
			Log.d("myDebug", "tryADD generated sound successfully" + currentWord);

		//add word, definition, and sound to DB
		EditText defText = (EditText) pw.getContentView().findViewById(R.id.AWdefinition);
		DPfinal.getDBHandler().addDefinition(currentWord, defText.getText().toString(), filename);
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, currentWord + " definition updated", duration);
		toast.show();
		pw.dismiss();
	}
	
}
