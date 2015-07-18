package com.dandewine.user.tocleveroad.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public class ToFavouriteService extends IntentService {

    public static final int ADD_IMAGE=1;
    public static final int DELETE_BY_ID=2;
    public static final int DELETE_BY_URL=3;
    public ToFavouriteService() {
        super("saveImageToDB");
        Log.d("myTag","onCreateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("myTag","onHandleIntent");
        int action = intent.getIntExtra("action",-1);
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        switch (action){
            case ADD_IMAGE:
                addImage(title,url);
                break;
            case DELETE_BY_ID:
                break;
            case DELETE_BY_URL:
                removeImage(url);
                break;
            default:
                break;
        }


    }
    private void addImage(String title,String url){
        ContentValues cv = new ContentValues();
        cv.put(MyContentProvider.IMAGE_TITLE,title);
        cv.put(MyContentProvider.IMAGE_URL, url);
        Uri result = null;
        int update = getContentResolver().update(MyContentProvider.IMAGE_CONTENT_URI, cv, MyContentProvider.IMAGE_URL + "=?", new String[]{url});
        if(update==0)
           result = getContentResolver().insert(MyContentProvider.IMAGE_CONTENT_URI,cv);
        Log.d("myTag", "" + result);
    }
    /**
     * For removing item from DB create new
     * */
    private void removeImage(String url){
        getContentResolver().delete(MyContentProvider.IMAGE_CONTENT_URI,MyContentProvider.IMAGE_URL+"=?",new String[]{url});
        Log.d("myTag","removeImage");
    }

}
