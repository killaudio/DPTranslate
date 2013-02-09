package com.pigote.dpfinal;

import java.io.InputStream;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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
	
}
