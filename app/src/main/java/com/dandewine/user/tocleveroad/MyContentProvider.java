package com.dandewine.user.tocleveroad;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

public class MyContentProvider extends ContentProvider {
    final String LOG_TAG = "Provider";
    //db
    static final String DB_NAME="toCleveroad";
    static final int DB_VERSION = 1;
    //table name
    static final String IMAGE_TABLE = "images";
    //fields
    static final String IMAGE_TABLE_ID = "_id";
    static final String IMAGE_TITLE = "title";
    static final String IMAGE_URL = "url";
    //create query
    static final String IMAGE_CREATE="CREATE TABLE "+IMAGE_TABLE+" ("+
            IMAGE_TABLE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            IMAGE_TITLE+" TEXT, "+IMAGE_URL+" TEXT);";
    //authority+path and general uri
    static final String AUTHORITY = "com.dandewine.user.tocleveroad";
    static final String CONTACT_PATH = "images";
    public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://"+
    AUTHORITY+"/"+CONTACT_PATH);

    //general uri for table
    static final int URI_IMAGES = 1;

    //general uri for particular record
    static final int URI_IMAGES_ID = 2;

    //data types
    //a set of rows
    static final String IMAGE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+
            AUTHORITY+"."+CONTACT_PATH;

    //one result_item
    static final String IMAGE_CONTENT_ITEM_TYPE = "vnd.android.cursor.result_item/vnd."+
            AUTHORITY+"."+CONTACT_PATH;
    //create associations
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,CONTACT_PATH,URI_IMAGES);
        uriMatcher.addURI(AUTHORITY,CONTACT_PATH+"/#",URI_IMAGES_ID);
    }

    SQLiteDatabase db;
    DBHelper dbHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            case URI_IMAGES:
                break;
            case URI_IMAGES_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection))
                    selection=IMAGE_TABLE_ID+"="+id;
                else
                    selection=selection+" AND "+IMAGE_TABLE_ID+"="+id;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI:" +uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(IMAGE_TABLE,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(LOG_TAG,"getType()");
       switch (uriMatcher.match(uri)){
           case URI_IMAGES:
               return IMAGE_CONTENT_TYPE;
           case URI_IMAGES_ID:
              return IMAGE_CONTENT_ITEM_TYPE;
           default:
               throw new IllegalArgumentException("Wrong URI: "+uri);
       }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG,"insert()");
      if(uriMatcher.match(uri)!=URI_IMAGES)
          throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(IMAGE_TABLE,null,values);
        Uri resultUri = ContentUris.withAppendedId(CONTACT_CONTENT_URI,rowID);
        getContext().getContentResolver().notifyChange(resultUri,null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG,"onCreate()");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG,"query()");
       switch (uriMatcher.match(uri)){
           case URI_IMAGES:
               if(TextUtils.isEmpty(sortOrder))
                   sortOrder=IMAGE_TITLE+" ASC";
               break;
           case URI_IMAGES_ID:
               String id = uri.getLastPathSegment();
               if(TextUtils.isEmpty(selection))
                   selection=IMAGE_TITLE+" = "+id;
               else
                    selection=selection+" AND "+IMAGE_TABLE_ID+" ="+id;
               break;
           default:
               throw new IllegalArgumentException("Wrong URI: "+uri);
       }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(IMAGE_TABLE,projection,selection,selectionArgs,
                null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),CONTACT_CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG,"update()");
        switch (uriMatcher.match(uri)){
            case URI_IMAGES:
                break;
            case URI_IMAGES_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection))
                    selection=IMAGE_TABLE_ID+" = "+id;
                else
                    selection=selection+" AND "+IMAGE_TABLE_ID+" = "+id;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: "+uri);
        }
        db =dbHelper.getWritableDatabase();
        int upd = db.update(IMAGE_TABLE,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return upd;
    }
    private class DBHelper extends SQLiteOpenHelper{
        public DBHelper(Context context) {
            super(context,DB_NAME,null,DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(IMAGE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST "+IMAGE_TABLE);
            onCreate(db);
        }
    }
}
