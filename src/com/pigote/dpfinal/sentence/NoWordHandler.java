package com.pigote.dpfinal.sentence;

import com.pigote.dpfinal.R;

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

public class NoWordHandler extends PopupHandler{

	@Override
	public void handlePopup(String word, String def, Activity activity, PopupWindow pw) {
		if (def == null){
			//if word is not in database, handle Popup
			try{
				 //We need to get the instance of the LayoutInflater, use the context of this activity
		        LayoutInflater inflater = (LayoutInflater) activity
		                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //Inflate the view from a predefined XML layout
		        View layout = inflater.inflate(R.layout.popup_addword, (ViewGroup) activity.findViewById(R.id.AWmyRelativeLayout));
	
		        pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		        
		        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		        
		        TextView wordText = (TextView) layout.findViewById(R.id.AWword);
		        wordText.setText(word);
		        PopupHandler.pw = pw;
		        this.currentWord = word;
		        this.activity = activity;
		        
		        } catch (Exception e) {
		    	Log.d("myDebug", "initiatePopUpExec! (NoWordHandler)" + e.toString());
		        e.printStackTrace();
		    }
		}
		else{
			//use CoR for future case scenarios. Right now we log that the chain reached the end unattended.
			Log.d("myDebug", "Chain of Responsibility reached the end unattended");
		}
	}
}
