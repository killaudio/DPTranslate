package com.pigote.dpfinal;

import java.io.InputStream;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

public class DoTranslate extends AsyncTask<String, String, String>{
	
	private TextView translated;
	
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
        Activity activity = DPfinal.getActivity();
        translated = (TextView) activity.findViewById(R.id.translatedText);
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

