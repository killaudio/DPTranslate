package com.pigote.dpfinal;

import java.util.Locale;

import com.pigote.dpfinal.db.DBHandler;
import com.pigote.dpfinal.sentence.SentenceActivity;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DPfinal extends Activity implements OnDBUpdateCompleted, OnTranslateCompleted, OnInitListener{

	public final static String EXTRA_MESSAGE = "com.pigote.dpfinal.MESSAGE";
	public final static String EXTRA_WORDS = "com.pigote.dpfinal.WORDS";
	
	private EditText text;
	private TextView translated;
	private ConnectivityManager connMgr;
	private Button readButton;
	private NetworkInfo networkInfo;
	private static Activity myActivity;
	private static DBHandler myDbHandler;
	private String[] originalString;
	private MediaPlayer mp;
	private Handler handler, handler2;
	private TextToSpeech talker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		talker = new TextToSpeech(this, this);
		
		setContentView(R.layout.activity_dpfinal);
		text = (EditText) findViewById(R.id.textToTranslate);
	    translated = (TextView) findViewById(R.id.translatedText);
	    readButton = (Button) findViewById(R.id.read);
	    connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	    myActivity = this;
	    myDbHandler = DBHandler.getInstance(this);
	    
	    text.setOnClickListener(new View.OnClickListener() {
	    	  @Override
	    	  public void onClick(View v) {
	    	    readButton.setEnabled(false);
	    	  }
	    	});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dpfinal, menu);
		return true;
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


	@Override
	public void onTranslatedCompleted() {
		String toRead = translated.getText().toString();
		String[] missing = DPfinal.getDBHandler().getMissingWords(toRead);		
		
		readButton.setEnabled(true);
		
		//Update DB with missing entries
		for (int i = 0; i<missing.length; i++){
        	if (!missing[i].equals("*")){
        		toastMsg("updating online db...");
        		if (networkInfo != null && networkInfo.isConnected()) {
        	        new UpdateDb(this).execute(toRead);
        	    } else {
        	       toastMsg("No network connection available");
        	    }
        		break;
        	}
		}
	}
	
	@Override
	public void onReadCompleted() {
		toastMsg("db updated");
	}
	
	@Override
	public void onDestroy() {
		if (talker != null) {
			talker.stop();
			talker.shutdown();
		}
		super.onDestroy();
	}
	
	public static Activity getActivity() {
        return myActivity;
    }

	public static DBHandler getDBHandler() {
		return myDbHandler;
    }
	
	public void startSentenceActivity(View view){
		Intent intent = new Intent(this, SentenceActivity.class);
		String message = translated.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	public void tryTranslate(View view) {
		//Close soft keyboard if showing
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    	
        //enable clickable translated text
        translated.setOnClickListener(new View.OnClickListener() {
	    	  @Override
	    	  public void onClick(View v) {
	    	    startSentenceActivity(v);
	    	  }
	    	});
        
		String toRead = text.getText().toString();  
	    toastMsg("Working on your translation...");
	    
	    //launch asyncTask DoTranslate 
	    if (networkInfo != null && networkInfo.isConnected()) {
	        new DoTranslate(this).execute(toRead);
	    } else {
	       toastMsg("No network connection available");
	    }
	    
	}
	
	public void tryRead(View view) {
		handler2 = new Handler();
		//Use the proxy design pattern to get a valid sound file.		
		originalString = translated.getText().toString().split(" ");
		UriBase proxyUri = new ProxyUri(originalString[0]);
		if (originalString.length>0){
			final Uri uri = proxyUri.getUri(talker);
			//TODO start testing here!! check tryRead and addDefinition in popup win
			handler2.postDelayed(new Runnable(){
				@Override
				public void run() {
				playNext(DPfinal.getActivity(), uri, 1);
			}}, 100);
		}
	}
	
	private void playNext(Activity activity, Uri uri, int i) {
		final int w = i;
		handler = new Handler();
		if (uri!= null){
			mp = MediaPlayer.create(activity, uri);
			//MediaPlayer.create sometimes returns null because
			//the file uses WAVE 8,000Hz MP3 8 kbit/s format, while android 2.3.3 supports only 8- and 16-bit linear PCM
			if (mp!=null){
				mp.start();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mp.release();
						if (w<originalString.length)
						playNext(DPfinal.getActivity(), new ProxyUri(originalString[w]).getUri(talker), w+1);
					}}, mp.getDuration() + 100);
			} else {toastMsg("MediaPlayer create failed");}
		} else {
			toastMsg("Null URI, something wrong happened");
		}
	}
	
	public void toastMsg(String string) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, string, duration);
		toast.show();
	}
}
