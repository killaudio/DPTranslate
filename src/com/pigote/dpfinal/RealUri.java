package com.pigote.dpfinal;

import java.util.Locale;

import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class RealUri extends UriBase implements OnInitListener {
	private String word;
	private TextToSpeech talker;
	
	@Override
	public Uri getUri() {
		talker = new TextToSpeech(DPfinal.getActivity(), this);
		String filename = DPfinal.getActivity().getExternalFilesDir("wavs").toString();
		filename = filename + "/" + word + ".wav" ;
		if (0==talker.synthesizeToFile(word, null, filename))
			Log.d("myDebug", "UriProxy generated sound successfully " + word);
		
		if (talker != null) {
			talker.stop();
			talker.shutdown();
		}
		return Uri.parse(filename);
	}
	
	public RealUri(String w){
		word = w;
	}
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = talker.setLanguage(Locale.US);
	        if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	        	Log.e("TTS", "This Language is not supported");
	        } 
	 
	    } else {
	            Log.e("TTS", "Initilization Failed!");
	    }
	}

}
