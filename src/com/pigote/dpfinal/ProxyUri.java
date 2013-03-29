package com.pigote.dpfinal;

import android.net.Uri;

public class ProxyUri extends UriBase{
	private Uri myUri;
	private String word;
	private UriBase realUri;
	@Override
	public Uri getUri() {
		if (myUri == null){
			realUri = new RealUri(word);
			myUri = realUri.getUri();
		}
		return myUri;
	}
	
	public ProxyUri(String w){
		word = w;
		myUri = DPfinal.getDBHandler().getUri(word);
	}

}
