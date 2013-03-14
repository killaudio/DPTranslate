package com.pigote.dpfinal;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class SentenceActivity extends ListActivity {

	private PopupWindow pw;
	private String currentWord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(DPfinal.EXTRA_MESSAGE);
	    String[] values = message.split(" ");
	    // Create the list view
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);

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

	private void initiatePopupWindow(String s) {
		//TODO start here: woman returns not in database when querying a second time with inexistent word "woman" then "woman piriguete" 
		String tmpDef = DPfinal.getDBHandler().getDefinition(s);
        currentWord = s;
		if (tmpDef == null){
        	//add new Word definition
        	try {
		        //We need to get the instance of the LayoutInflater, use the context of this activity
		        LayoutInflater inflater = (LayoutInflater) SentenceActivity.this
		                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //Inflate the view from a predefined XML layout
		        View layout = inflater.inflate(R.layout.popup_addword, (ViewGroup) findViewById(R.id.AWmyRelativeLayout));

		        pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        
		        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		        
		        TextView wordText = (TextView) layout.findViewById(R.id.AWword);
		        wordText.setText(s);
		        
		    } catch (Exception e) {
		    	Log.d("myDebug", "initiatePopUpExec!" + e.toString());
		        e.printStackTrace();
		    }
        } else {
        	//word already exists in db with def
			try {
		        //We need to get the instance of the LayoutInflater, use the context of this activity
		        LayoutInflater inflater = (LayoutInflater) SentenceActivity.this
		                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //Inflate the view from a predefined XML layout
		        View layout = inflater.inflate(R.layout.popup_word, (ViewGroup) findViewById(R.id.myRelativeLayout));

		        pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        
		        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		        
		        TextView wordText = (TextView) layout.findViewById(R.id.word);
		        wordText.setText(s);
		        
		        TextView definitionText = (TextView) layout.findViewById(R.id.definition);
		        definitionText.setText(tmpDef);
		        
		    } catch (Exception e) {
		    	Log.d("myDebug", "initiatePopUpExec!" + e.toString());
		        e.printStackTrace();
		    }
        }
	}
	 
	public void tryClose(View v) {
	    pw.dismiss();
	}
	
	public void tryPlay(View v) {
	 	playWord();
	}

	public void tryAdd(View v){
		EditText defText = (EditText) v.findViewById(R.id.AWdefinition);
		DPfinal.getDBHandler().addDefinition(currentWord, defText.getText().toString());
		pw.dismiss();
	}
	
	protected void playWord() {
		MediaPlayer mp = MediaPlayer.create(this, DPfinal.getDBHandler().getUri(currentWord));
		//MediaPlayer.create sometimes returns null because
		//the file uses WAVE 8,000Hz MP3 8 kbit/s format, while android 2.3.3 supports only 8- and 16-bit linear PCM
		mp.start();
	}
}
