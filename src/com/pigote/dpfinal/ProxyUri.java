package com.pigote.dpfinal;

import android.net.Uri;
import android.speech.tts.TextToSpeech;

public class ProxyUri extends UriBase{
	private Uri myUri;
	private String word;
	private UriBase realUri;
	@Override
	public Uri getUri(TextToSpeech talker) {
		if (myUri == null){
			realUri = new RealUri(word);
			myUri = realUri.getUri(talker);
		}
		return myUri;
	}
	
	public ProxyUri(String w){
		word = w;
		myUri = DPfinal.getDBHandler().getUri(word);
	}

}
