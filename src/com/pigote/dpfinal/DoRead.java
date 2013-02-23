package com.pigote.dpfinal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.pigote.dpfinal.MWReaderXmlParser.Entry;

public class DoRead extends AsyncTask<String, String, List<Entry>>{
	
	private TextView translated;
	
	@Override
	protected List<Entry> doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
        try {
            return goRead(params[0]);
        } catch (Exception e) {
        	//toastMsg("Unable to connect to reading service");
        	Log.d("myDebug", "goRead Exception! " + e.getMessage());
            return null;
        }
	}
	
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<Entry> result) {
        if (result!=null){
        	Activity activity = DPfinal.getActivity();
        	translated = (TextView) activity.findViewById(R.id.translatedText);
        	translated.setText(result.get(0).sound.toString());
        }
    }
   
     // Do the web service tango here
    private List<Entry> goRead(String toRead) throws Exception {
    	
    	List<Entry> entries = new ArrayList<Entry>();
    	InputStream is = null;
    	MWReaderXmlParser xmlParser = new MWReaderXmlParser();
    	
    	String tokens[] = toRead.split(" ");
    	final String pre = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
    	final String post = "?key=df7d3120-f7c0-4150-bcf4-93e04f72f6db";
        	        
        for (int i = 0; i<tokens.length; i++){
        	try {
        		URL url = new URL(pre+tokens[i].toLowerCase(Locale.ENGLISH)+post);
        		//URL url = new URL(pre+"the"+post);
        		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        		conn.setReadTimeout(10000 /* milliseconds */);
	            conn.setConnectTimeout(15000 /* milliseconds */);
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);
	            // Starts the query
	            conn.connect();
	            int response = conn.getResponseCode();
	            Log.d("myDebug", "Trying to get : " + tokens[i].toLowerCase(Locale.ENGLISH));
	            if (response==200){
	            is = conn.getInputStream();
	            entries.add(xmlParser.parse(is));
	            } else {
	            	Activity activity = DPfinal.getActivity();
	            	translated = (TextView) activity.findViewById(R.id.translatedText);
	            	translated.setText("Connection error" + response);
	            }
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
