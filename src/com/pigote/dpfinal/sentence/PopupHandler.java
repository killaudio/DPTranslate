package com.pigote.dpfinal.sentence;

import android.app.Activity;
import android.widget.PopupWindow;

public abstract class PopupHandler {
	
	protected PopupHandler successor;
	
	protected static PopupWindow pw;
	protected String currentWord;
	protected Activity activity;
	
	public void setSuccessor(PopupHandler mySuccessor){
		successor = mySuccessor;
	};
	
	public abstract void handlePopup(String word, String def, Activity activity, PopupWindow pw);

	public static PopupWindow getPw() {
		return pw;
	}
}
