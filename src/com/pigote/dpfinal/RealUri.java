package com.pigote.dpfinal;

import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class RealUri extends UriBase{
	private String word;
	
	@Override
	public Uri getUri(TextToSpeech talker) {
		String filename = DPfinal.getActivity().getExternalFilesDir("wavs").toString();
		filename = filename + "/" + word + ".wav" ;
		if (0==talker.synthesizeToFile(word, null, filename)){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.d("myDebug", "UriProxy interrupted in sleep while recording" + word);
				e.printStackTrace();
			}
			Log.d("myDebug", "UriProxy generated sound successfully " + word);
			return Uri.parse(filename);
		} else {
			Log.d("myDebug", "UriProxy couldnt create word " + word);
			return null;
		} 
	}
	
	public RealUri(String w){
		word = w;
	}
}
