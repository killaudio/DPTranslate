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
		if (0==talker.synthesizeToFile(word, null, filename))
			Log.d("myDebug", "UriProxy generated sound successfully " + word);
		return Uri.parse(filename);
	}
	
	public RealUri(String w){
		word = w;
	}
}
