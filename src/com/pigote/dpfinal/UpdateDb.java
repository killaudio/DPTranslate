package com.pigote.dpfinal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.pigote.dpfinal.MWReaderXmlParser.Entry;

public class UpdateDb extends AsyncTask<String, String, String>{
	
	private OnDBUpdateCompleted listener;
	private TextView translated;
	private List<Entry> entries = new ArrayList<Entry>();
	
	@Override
	protected String doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
    
	    try {
            return goRead(params[0]);
        } catch (Exception e) {
        	Log.d("myDebug", "UpdateDB Exception! " + e.getMessage());
            return null;
        }
	}
	
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        if (result!=null){
        	Log.d("myDebug", "Entry list ready to play");
        	listener.onReadCompleted();
        }
    }

	public UpdateDb(OnDBUpdateCompleted listener){
		this.listener = listener;
	}
	
     // Do the web service tango here
    private String goRead(String toRead) throws Exception {
    	// Query local db, fill array of missing words
    	String[] missing = DPfinal.getDBHandler().getMissingWords(toRead);
    	// go online, find missing words and add to db
    	InputStream is = null;
    	MWReaderXmlParser xmlParser = new MWReaderXmlParser();
    	
    	final String pre = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";
    	final String post = "?key=df7d3120-f7c0-4150-bcf4-93e04f72f6db";
        	       
        for (int i = 0; i<missing.length; i++){
        	if (!missing[i].equals("*")){
	        	try {
	        		URL url = new URL(pre+missing[i].toLowerCase(Locale.ENGLISH)+post);
	        		//URL url = new URL(pre+"the"+post);
	        		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        		conn.setReadTimeout(10000 /* milliseconds */);
		            conn.setConnectTimeout(15000 /* milliseconds */);
		            conn.setRequestMethod("GET");
		            conn.setDoInput(true);
		            // Starts the query
		            conn.connect();
		            int response = conn.getResponseCode();
		            Log.d("myDebug", "Trying to get : " + missing[i].toLowerCase(Locale.ENGLISH));
		            if (response==200){
		            	is = conn.getInputStream();
		            	entries.add(xmlParser.parse(is, missing[i].toLowerCase(Locale.ENGLISH)));
			            storeWavToExternal(entries.get(entries.size()-1));
		            } else {
		            	Activity activity = DPfinal.getActivity();
		            	translated = (TextView) activity.findViewById(R.id.translatedText);
		            	translated.setText("Connection error" + response);
		            }
		            conn.disconnect();
		            
		         } catch (Exception e ) {
		        	 Log.d("myDebug", missing[i] + "Couldnt be found online" + e.toString());
		         } finally {
		             if (is != null) {
		                 is.close();
		             } 
		         }
        	}
        } 
        
       for (int w =0; w<entries.size(); w++){
        	DPfinal.getDBHandler().addEntry(entries.get(w));
        }
        return toRead;
     }

	private void storeWavToExternal(Entry entry) {
		try {
	        //this is the file to be downloaded
	        URL url = entry.sound;

	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();

	        //set the path where we want to save the file
	        //in this case, going to save it to a dir called wavs
	        File SDCardDir = DPfinal.getActivity().getExternalFilesDir("wavs");
	        
	        //check that SDCardDir is not null (we have SDCard!!)
	        if (SDCardDir==null)
	        	throw new IOException("SD CARD DIR == NULL");
	        
	        //create a new file, specifying the path, and the filename
	        File file = new File(SDCardDir,entry.word+".wav");

	        FileOutputStream fileOutput = new FileOutputStream(file);

	        //this will be used in reading the data from the internet
	        InputStream inputStream = urlConnection.getInputStream();

	        //create a buffer...
	        byte[] buffer = new byte[1024];
	        int bufferLength = 0; //used to store a temporary size of the buffer

	        //read through the input buffer and write the contents to the file
	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                fileOutput.write(buffer, 0, bufferLength);
	        }
	        //close the output stream when done
	        fileOutput.close();
	        URL myURL = new URL("http://"+file.getAbsolutePath());
	        entry.sound = myURL;
		//catch some possible errors...
		} catch (MalformedURLException e) {
			Log.d("myDebug", "Malformed URL Exc!" + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("myDebug", "IO Exc!" + e.toString());
		    e.printStackTrace();
		}
		
	}
}
