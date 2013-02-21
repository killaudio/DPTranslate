package com.pigote.dpfinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class localDatabase extends SQLiteOpenHelper{

    private static final String DICTIONARY_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS localWordDB.Dictionary(" +
                "word TEXT, Definition TEXT, Url TEXT)";

    localDatabase(Context context) {
        super(context, "localWordDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
