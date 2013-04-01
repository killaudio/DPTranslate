package com.pigote.dpfinal;

import android.net.Uri;
import android.speech.tts.TextToSpeech;

public abstract class UriBase {
	public abstract Uri getUri(TextToSpeech talker);
}
