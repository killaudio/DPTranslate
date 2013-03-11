package com.pigote.dpfinal;

import com.pigote.dpfinal.db.DBHandler;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DPfinal extends Activity implements OnReadCompleted{

	public final static String EXTRA_MESSAGE = "com.pigote.dpfinal.MESSAGE";
	
	private EditText text;
	private TextView translated;
	private ConnectivityManager connMgr;
	private NetworkInfo networkInfo;
	private static Activity myActivity;
	private static DBHandler myDbHandler;
	private String[] originalString;
	private MediaPlayer mp;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dpfinal);
		text = (EditText) findViewById(R.id.textToTranslate);
	    translated = (TextView) findViewById(R.id.translatedText);
	    connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	    myActivity = this;
	    myDbHandler = DBHandler.getInstance(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dpfinal, menu);
		return true;
	}
	
	public static Activity getActivity() {
        return myActivity;
		
    }

	public static DBHandler getDBHandler() {
        //TODO fix singleton
		return myDbHandler;
    }
	
	public void tryTranslate(View view) {
	    //TODO hide keyboard if showing
		translated.setOnClickListener(new View.OnClickListener() {
	    	  @Override
	    	  public void onClick(View v) {
	    	    startSentenceActivity(v);
	    	  }
	    	});
		String toRead = text.getText().toString();  
	    toastMsg("Working on your translation...");
	    if (networkInfo != null && networkInfo.isConnected()) {
	        new DoTranslate().execute(toRead);
	    } else {
	       toastMsg("No network connection available");
	    }
	}
	
	public void tryRead(View view) {
		
		String toRead = translated.getText().toString();
		originalString = toRead.split(" "); 
		toastMsg("Working on your audio...");
		if (networkInfo != null && networkInfo.isConnected()) {
	        new DoRead(this).execute(toRead);
	    } else {
	       toastMsg("No network connection available");
	    }
	}
	
	public void toastMsg(String string) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, string, duration);
		toast.show();
	}

	@Override
	public void onReadCompleted() {
		if (originalString.length>0)
		playNext(DPfinal.getActivity(), DPfinal.getDBHandler().getUri(originalString[0]), 1);
	}

	private void playNext(Activity activity, Uri uri, int i) {
		final int w = i;
		handler = new Handler();
		mp = MediaPlayer.create(activity, uri);
		//MediaPlayer.create sometimes returns null because
		//the file uses WAVE 8,000Hz MP3 8 kbit/s format, while android 2.3.3 supports only 8- and 16-bit linear PCM
		mp.start();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mp.release();
				if (w<originalString.length)
				playNext(DPfinal.getActivity(), DPfinal.getDBHandler().getUri(originalString[w]), w+1);
			}}, mp.getDuration() + 100);
		
	}
	
	public void startSentenceActivity(View view){
		Intent intent = new Intent(this, SentenceActivity.class);
		String message = translated.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
}
