package com.pigote.dpfinal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class DPfinal extends Activity {

	private EditText text;
	private TextView translated;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dpfinal);
		text = (EditText) findViewById(R.id.textToTranslate);
	    translated = (TextView) findViewById(R.id.translatedText);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dpfinal, menu);
		return true;
	}
	
	public void tryTranslate(View view) {
	    
		String myText = text.getText().toString();  
		
	    ConnectivityManager connMgr = (ConnectivityManager) 
	        getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    if (networkInfo != null && networkInfo.isConnected()) {
	        new DoTranslate().execute(myText);
	    } else {
	       translated.setText("No network connection available");
	    }
	    
	}
	
	private class DoTranslate extends AsyncTask<String, String, String>{
		
		private final String DEBUG_TAG = null;

		@Override
		protected String doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
            try {
                return goTranslate(params[0]);
            } catch (IOException e) {
                return "Unable to connect to translation service.";
            }
		}
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            translated.setText(result);
        }
       
	     // Do the web service tango here
        private String goTranslate(String myurl) throws IOException {
	         InputStream is = null;
	         // Only display the first 500 characters of the retrieved
	         // web page content.
	         int len = 500;
	             
	         try {
	             URL url = new URL(myurl);
	             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	             conn.setReadTimeout(10000 /* milliseconds */);
	             conn.setConnectTimeout(15000 /* milliseconds */);
	             conn.setRequestMethod("GET");
	             conn.setDoInput(true);
	             // Starts the query
	             conn.connect();
	             int response = conn.getResponseCode();
	             Log.d(DEBUG_TAG, "The response is: " + response);
	             is = conn.getInputStream();
	
	             // Convert the InputStream into a string
	             String contentAsString = readIt(is, len);
	             return contentAsString;
	             
	         // Makes sure that the InputStream is closed after the app is
	         // finished using it.
	         } finally {
	             if (is != null) {
	                 is.close();
	             } 
	         }
	    }
     // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");        
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
        
	}
	
}
