package com.pigote.dpfinal.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import com.pigote.dpfinal.db.LocalWordDatabaseHelper;
import com.pigote.dpfinal.db.WordDbTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyLocalWordDbContentProvider extends ContentProvider{

	// database
	private LocalWordDatabaseHelper database;

	// Used for the UriMacher
	private static final int LOCALWORDDATABASE = 10;
	private static final int WORDDB_ID = 20;

	private static final String AUTHORITY = "com.pigote.dpfinal.contentprovider";

	private static final String BASE_PATH = "localworddatabase";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	    + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	    + "/localworddatabase";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	    + "/worddb";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, LOCALWORDDATABASE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", WORDDB_ID);
	}
	
	//checks that we are requesting valid columns
	private void checkColumns(String[] projection) {
		String[] available = { WordDbTable.COLUMN_ID, WordDbTable.COLUMN_WORD, 
				WordDbTable.COLUMN_DEFINITION, WordDbTable.COLUMN_URI };
	    if (projection != null) {
	    	HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	    	HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	    	// Check if all columns which are requested are available
	    	if (!availableColumns.containsAll(requestedColumns)) {
	    		throw new IllegalArgumentException("Unknown columns in projection");
	    	}
	    }
	}
	
	@Override
	public boolean onCreate() {
	    database = new LocalWordDatabaseHelper(getContext());
		return false;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,	String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    
	    switch (uriType) {
	    case LOCALWORDDATABASE:
	    	rowsUpdated = sqlDB.update(WordDbTable.TABLE_WORDDB, 
	        values, 
	        selection,
	        selectionArgs);
	    	break;
	    case WORDDB_ID:
	    	String id = uri.getLastPathSegment();
	    	if (TextUtils.isEmpty(selection)) {
	    		rowsUpdated = sqlDB.update(WordDbTable.TABLE_WORDDB, values,
	            WordDbTable.COLUMN_ID + "=" + id, null);
	    	} else {
	    		rowsUpdated = sqlDB.update(WordDbTable.TABLE_WORDDB, values,
	    		WordDbTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
	    	}
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	  }
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;

		switch (uriType) {
	    case LOCALWORDDATABASE:
	    	id = sqlDB.insert(WordDbTable.TABLE_WORDDB, null, values);
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
		getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	  }

	 @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    
	    switch (uriType) {
	    case LOCALWORDDATABASE:
	    	rowsDeleted = sqlDB.delete(WordDbTable.TABLE_WORDDB, selection,
	    			selectionArgs);
	    	break;
	    case WORDDB_ID:
	    	String id = uri.getLastPathSegment();
	    	if (TextUtils.isEmpty(selection)) {
	    		rowsDeleted = sqlDB.delete(WordDbTable.TABLE_WORDDB,
	        		WordDbTable.COLUMN_ID + "=" + id, null);
	    	} else {
	    		rowsDeleted = sqlDB.delete(WordDbTable.TABLE_WORDDB,
	        	WordDbTable.COLUMN_ID + "=" + id 
	        	+ " and " + selection, selectionArgs);
	    	}
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// Using SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Check if the caller has requested a column which does not exists
	    checkColumns(projection);

	    // Set the table
	    queryBuilder.setTables(WordDbTable.TABLE_WORDDB);
	    int uriType = sURIMatcher.match(uri);
	    
	    switch (uriType) {
	    case LOCALWORDDATABASE:
	    	break;
	    case WORDDB_ID:
	    	// Adding the ID to the original query
	    	queryBuilder.appendWhere(WordDbTable.COLUMN_ID + "="
	        + uri.getLastPathSegment());
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = database.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // Make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}
}
