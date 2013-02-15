package com.pigote.dpfinal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.pigote.dpfinal.MWReaderXmlParser.Entry;

import android.util.Xml;

public class MWReaderXmlParser {
	private static final String ns = null;
	   
    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
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
    
    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "entry_list");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }  
        return entries;
    }
    
    public static class Entry {
        public final String sound;
        public final String def;

        private Entry(String sound, String def) {
            this.sound = sound;
            this.def = def;
        }
    }
      
    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String sound = null;
        String def = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("sound")) {
                sound = readSound(parser);
            } 
            if (name.equals("def")) {
                def = readDef(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(sound, def);
    }

    // get sound tag in the feed.
    private String readSound(XmlPullParser parser) throws IOException, XmlPullParserException {
    	parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "wav");
        String wavFile = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "wav");
        //parser.nextToken(); TODO hacer pop del parser para que no haga illegal exception en Def
        return wavFile;
    }
      
    // Processes first definition in the feed.
    private String readDef(XmlPullParser parser) throws IOException, XmlPullParserException {
        String definition = "";
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "dt");
        definition = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "dt");
        return definition;
    }

     // For the tags sound and readDef, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
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
