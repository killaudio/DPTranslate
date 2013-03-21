package com.pigote.dpfinal.sentence;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.pigote.dpfinal.R;

public class NewWordHandler extends PopupHandler{
	
	@Override
	public void handlePopup(String word, String def, Activity activity, PopupWindow pw) {
		if (def == null){
			//if word is not in database use Chain of Responsibility to handle:
			this.successor.handlePopup(word, def, activity, pw);
		}
		else{
			//The word exists in database, take care of popup
			//word already exists in db with def
			try {
		        //We need to get the instance of the LayoutInflater, use the context of this activity
		        LayoutInflater inflater = (LayoutInflater) activity
		                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //Inflate the view from a predefined XML layout
		        View layout = inflater.inflate(R.layout.popup_word, (ViewGroup) activity.findViewById(R.id.myRelativeLayout));

		        pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        
		        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		        
		        TextView wordText = (TextView) layout.findViewById(R.id.word);
		        wordText.setText(word);
		        
		        TextView definitionText = (TextView) layout.findViewById(R.id.definition);
		        definitionText.setText(def);
		        
		        PopupHandler.pw = pw;
		        this.currentWord = word;
		        this.activity = activity;
		        
		    } catch (Exception e) {
		    	Log.d("myDebug", "initiatePopUpExec! (NewWordHandler)" + e.toString());
		        e.printStackTrace();
		    }
		}
		
	}
}
