package com.pigote.dpfinal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.pigote.dpfinal.MWReaderXmlParser;
import com.pigote.dpfinal.MWReaderXmlParser.Entry;

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
import android.widget.Toast;


public class DPfinal extends Activity {

	private EditText text;
	private TextView translated;
	private ConnectivityManager connMgr;
	private NetworkInfo networkInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dpfinal);
		text = (EditText) findViewById(R.id.textToTranslate);
	    translated = (TextView) findViewById(R.id.translatedText);
	    connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dpfinal, menu);
		return true;
	}
	
	public void tryTranslate(View view) {
	    
		String toRead = text.getText().toString();  
	    toastMsg("Working on your translation...");
	    if (networkInfo != null && networkInfo.isConnected()) {
	        new DoTranslate().execute(toRead);
	    } else {
	       toastMsg("No network connection available");
	    	//translated.setText("No network connection available");
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
	
	private void toastMsg(String string) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, string, duration);
		toast.show();
	}

	private class DoTranslate extends AsyncTask<String, String, String>{
		
		@Override
		protected String doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
            try {
                return goTranslate(params[0]);
            } catch (Exception e) {
                return "Unable to connect to translation service.";
            }
		}
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            translated.setText(result);
        }
       
	     // Do the web service tango here
        private String goTranslate(String myText) throws Exception {
	         InputStream is = null;
	         try {

	        	 Translate.setClientId("DPFinal");
	        	 Translate.setClientSecret("+K1b9VU83HAp3hY2N/qYjKyjFVB8MWqOM+Yy1w9PAds=");

	        	 String translatedText = Translate.execute(myText, Language.SPANISH, Language.ENGLISH);
	             return translatedText;
	             
	         // Makes sure that the InputStream is closed after the app is
	         // finished using it.
	         } finally {
	             if (is != null) {
	                 is.close();
	             } 
	         }
	    }
	}
	private class DoRead extends AsyncTask<String, String, List<Entry>>{
		
		@Override
		protected List<Entry> doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
            try {
                return goRead(params[0]);
            } catch (Exception e) {
            	toastMsg("Unable to connect to reading service");
                return null;
            }
		}
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Entry> result) {
            if (result!=null)
        	translated.setText(result.get(0).sound.toString());
        }
       
	     // Do the web service tango here
        private List<Entry> goRead(String toRead) throws Exception {
        	
        	List<Entry> entries = new ArrayList<Entry>();
        	InputStream is = null;
        	MWReaderXmlParser xmlParser = new MWReaderXmlParser();
        	
        	String tokens[] = toRead.split(" ");
        	String pre = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
        	String post = "?key=df7d3120-f7c0-4150-bcf4-93e04f72f6db";
	        	        
	        for (int i = 0; i<tokens.length; i++){
	        	try {
	        		URL url = new URL(pre+tokens[i].toLowerCase(Locale.ENGLISH)+post);
	        		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        		conn.setReadTimeout(10000 /* milliseconds */);
		            conn.setConnectTimeout(15000 /* milliseconds */);
		            conn.setRequestMethod("GET");
		            conn.setDoInput(true);
		            // Starts the query
		            conn.connect();
		            int response = conn.getResponseCode();
		            Log.d("Debug", "The goRead response is: " + response);
		            Log.d("Debug", "Trying to get : " + tokens[i].toLowerCase(Locale.ENGLISH));
		            is = conn.getInputStream();
		            entries.add(xmlParser.parse(is));
		            conn.disconnect();
		            
		         } finally {
		             if (is != null) {
		                 is.close();
		             } 
		         }	    	        	
	        }
	        return entries;
	     }
	}	
}
