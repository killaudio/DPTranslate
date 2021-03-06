package com.pigote.dpfinal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class MWReaderXmlParser {
	private static final String ns = null;
	private String word;
	
	//Defines an "entry", my database table unit. Contains a sound, a definition and the word
    public static class Entry {
        public URL sound;
        public String def;
        public String word;

        private Entry(String word, URL sound, String def) {
            this.sound = sound;
            this.def = def;
            this.word = word;
        }
    }
    
    public Entry parse(InputStream in, String word) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            this.word = word;
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    private Entry readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        
    	Entry myEntry = null;
    	
        parser.require(XmlPullParser.START_TAG, ns, "entry_list");
        while (parser.next() != XmlPullParser.END_TAG && myEntry == null) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                myEntry = readEntry(parser);
            } else {
                skip(parser);
            }
        }  
        return myEntry;
    }      
    
    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        URL sound = null;
        String def = null;
        String previousTagContent = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            
            if (name.equals("entry")) {
                return readEntry(parser);
            } else if (!name.equals("sound")) {
              	previousTagContent = readText(parser);
              	if (sound!=null && name.equals("dt"))
              		def = previousTagContent;
            } else if (name.equals("sound")){
            	String strip = previousTagContent.replace("*", "");
            	if (strip.equals(word))
                sound = buildSoundURL(readSound(parser));
            } else {
                skip(parser);
            }
            
            if(sound!=null && def != null) {
            	if (def.startsWith(":"))
            		def = def.replace(":", " ");
            	break;
            }
        }
        
        return new Entry(word, sound, def);
    }

    private URL buildSoundURL(String readSound) throws MalformedURLException {
		String pre = "http://media.merriam-webster.com/soundc11/";
		if (readSound.startsWith("bix"))
    	pre = pre + "bix"  + '/' + readSound;
		else if (readSound.startsWith("gg"))
			pre = pre + "gg"  + '/' + readSound;
		else if(readSound.startsWith("0") || readSound.startsWith("1") || readSound.startsWith("2") || 
				readSound.startsWith("3") || readSound.startsWith("4") || readSound.startsWith("5") ||
				readSound.startsWith("6") || readSound.startsWith("7") || readSound.startsWith("8") ||
				readSound.startsWith("9")){
			pre = pre + "number" + '/' + readSound;
		} else pre = pre + readSound.toCharArray()[0] + '/' + readSound;
		
		URL soundURL = new URL(pre);
				
		return soundURL;
	}

	// get sound tag in the feed.
    private String readSound(XmlPullParser parser) throws IOException, XmlPullParserException {
    	parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "wav");
        String wavFile = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "wav");
        return wavFile;
    }

     // For the tags sound and readDef, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) { //if text is right after dt tag (<dt>text</dt>)
            result = parser.getText();
        } else { //if text is formatted after dt tag (<dt><un>text</un></dt>)
        	parser.next();
        	result = parser.getText();
        }
        while (parser.getEventType() != XmlPullParser.END_TAG)
        	parser.next();
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }

}
