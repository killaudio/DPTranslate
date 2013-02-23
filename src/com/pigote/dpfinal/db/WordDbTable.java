package com.pigote.dpfinal.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WordDbTable {

  // Database table
  public static final String TABLE_WORDDB = "worddb";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_WORD = "word";
  public static final String COLUMN_DEFINITION = "definition";
  public static final String COLUMN_URI = "uri";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table " 
      + TABLE_WORDDB
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_WORD + " text not null, " 
      + COLUMN_DEFINITION + " text not null," 
      + COLUMN_URI + " text not null" 
      + ");";

  public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    Log.w(WordDbTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDDB);
    onCreate(database);
  }
} 

