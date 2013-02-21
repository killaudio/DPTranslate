package com.pigote.dpfinal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class MWReaderXmlParser {
	private static final String ns = null;
	   
    public Entry parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
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
    
    public static class Entry {
        public final URL sound;
        public final String def;

        private Entry(URL sound, String def) {
            this.sound = sound;
            this.def = def;
        }
    }
      
    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        URL sound = null;
        String def = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                return readEntry(parser);
            } else if (name.equals("sound")) {
                sound = buildSoundURL(readSound(parser));
            } else if ((sound != null) && name.equals("def")) {
                def = readDef(parser);
            } else {
                skip(parser);
            }
            if(sound!=null && def != null) {
            	break;
            }
        }
        
        return new Entry(sound, def);
    }

    private URL buildSoundURL(String readSound) throws MalformedURLException {
		String pre = "http://media.merriam-webster.com/soundc11/";
		if (readSound.startsWith("bix"))
    	pre = pre + "bix"  + '/' + readSound;
		else if (readSound.startsWith("gg"))
			pre = pre + "gg"  + '/' + readSound;
		else pre = pre + readSound.toCharArray()[0] + '/' + readSound;
		
		URL soundURL = new URL(pre);
				
		return soundURL;
	}

	// get sound tag in the feed.
    private String readSound(XmlPullParser parser) throws IOException, XmlPullParserException {
    	parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "wav");
        String wavFile = readText(parser);
        parser.next();
        parser.require(XmlPullParser.END_TAG, ns, "wav");
        return wavFile;
    }
      
    // Processes first definition in the feed.
    private String readDef(XmlPullParser parser) throws IOException, XmlPullParserException {
        String definition = null;
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT){
       		if(parser.getEventType() == XmlPullParser.START_TAG)
       			if (parser.getName().equals("dt"))
       				break;
        	parser.next();
        }
        parser.require(XmlPullParser.START_TAG, ns, "dt");
        definition = readText(parser);
//        while (!parser.getName().equals("dt")){
//        	parser.next();
//        }        
//        parser.require(XmlPullParser.END_TAG, ns, "dt");
        return definition;
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
