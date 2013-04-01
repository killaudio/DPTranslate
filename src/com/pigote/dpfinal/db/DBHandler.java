package com.pigote.dpfinal.db;

import com.pigote.dpfinal.MWReaderXmlParser.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper{
	// Instance for singleton
	private static DBHandler INSTANCE = null;
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "wordDb";
 
    // Contacts table name
    private static final String TABLE_DICTIONARY = "mydictionary";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_DEFINITION = "definition";
    private static final String KEY_URI = "fileloc";
    
    protected DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DICTIONARY_TABLE = "CREATE TABLE " + TABLE_DICTIONARY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD + " TEXT,"
                + KEY_DEFINITION + " TEXT," + KEY_URI + " TEXT" + ")";
        db.execSQL(CREATE_DICTIONARY_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
 
        // Create tables again
        onCreate(db);
    }
    
    // Adding new entry
    public void addEntry(Entry entry) {
    	if (entry != null){
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	 
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_WORD, entry.word); 
	    	values.put(KEY_DEFINITION, entry.def);
	    	values.put(KEY_URI, entry.sound.toString()); 
	    	 
	    	// Inserting Row
	    	db.insert(TABLE_DICTIONARY, null, values);
	    	db.close(); // Closing database connection
    	}
    }
     
    public Uri getUri(String myWord){
    	Uri myUri = null;
    	String stringToFixUri;
    	SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor;
	    if (myWord.contains("'")){
	    	String[] words = myWord.split("'");
	    	cursor = db.rawQuery("SELECT " + KEY_URI + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
	    						" LIKE \'" + words[0] + "\'\'" + words[1] + "\'" , null);
	    } else {
	    	cursor = db.rawQuery("SELECT " + KEY_URI + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
					" LIKE \'" + myWord + "\'", null);
	    }

	    cursor.moveToFirst();
	    if(cursor.getCount()>0){
		    stringToFixUri = cursor.getString(0);
		    if (stringToFixUri.length() > 2)
		    myUri = Uri.parse(stringToFixUri.substring(5));
	    }else{
	    	Log.d("myDebug", myWord+" DOESN'T EXISTS IN DB!! (DBHandler.getUri)" );
   		}
    	return myUri;
    }

	public String getDefinition(String s) {
		String def = null;
		Cursor cursor;
		SQLiteDatabase db = this.getReadableDatabase();
		if (s.contains("'")){
			String[] words = s.split("'");
		    cursor = db.rawQuery("SELECT " + KEY_DEFINITION + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
		    					" LIKE \'" + words[0] + "\'\'" + words[1] + "\'" , null);
		} else {
		    cursor = db.rawQuery("SELECT " + KEY_DEFINITION + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
								" LIKE \'" + s + "\'", null);
		}
		cursor.moveToFirst();
		if (cursor.getCount()<1){
	    	Log.d("myDebug", s+" DOESN'T EXISTS IN DB!! (DBHandler.getDefinition)" );
   		} else {
   			def = cursor.getString(0);
   		}
		return def;
	}
    
    // returns an array of missing words
    public String[] getMissingWords(String words) {
    	String[] myWords = words.split(" ");
    	
    	for (int i = 0; i<myWords.length; i++){
    		if (wordExists(myWords[i]))
    			myWords[i] = "*";  
    	}
    	return myWords;
    }
    
    //getters for singleton
    public static synchronized DBHandler getInstance(Context context)
    {
    	if (INSTANCE != null)
        return INSTANCE;
    	else return new DBHandler(context);
    }

	public void addDefinition(String currentWord, String string, String uri) {
		SQLiteDatabase db = this.getWritableDatabase();
   	 
    	ContentValues values = new ContentValues();
    	values.put(KEY_WORD, currentWord); 
    	values.put(KEY_DEFINITION, string);
    	values.put(KEY_URI, uri);
    	 
    	// Inserting Row
    	db.insert(TABLE_DICTIONARY, null, values);
    	db.close(); // Closing database connection
		
	}
	
	private boolean wordExists(String myWord){
	    boolean exists = false;
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor;
	    if (myWord.contains("'")){
	    	String[] words = myWord.split("'");
	    	cursor = db.rawQuery("SELECT " + KEY_WORD + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
	    						" LIKE \'" + words[0] + "\'\'" + words[1] + "\'" , null);
	    } else {
	    	cursor  = db.rawQuery("SELECT " + KEY_WORD + " FROM " + TABLE_DICTIONARY + " WHERE " + KEY_WORD + 
					" LIKE \'" + myWord + "\'", null);
	    }
		    
	    if (cursor.getCount()>0){
	    	exists = true;
		}
	    return exists;
	}
}
