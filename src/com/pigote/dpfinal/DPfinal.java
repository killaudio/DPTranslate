package com.pigote.dpfinal;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DPfinal extends Activity {

	private EditText text;
	private TextView translated;
	private ConnectivityManager connMgr;
	private NetworkInfo networkInfo;
	private static Activity myActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dpfinal);
		text = (EditText) findViewById(R.id.textToTranslate);
	    translated = (TextView) findViewById(R.id.translatedText);
	    connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	    myActivity = this;
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
	
	public void tryTranslate(View view) {
	    
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
		toastMsg("Working on your audio...");
		if (networkInfo != null && networkInfo.isConnected()) {
	        new DoRead().execute(toRead);
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
}
