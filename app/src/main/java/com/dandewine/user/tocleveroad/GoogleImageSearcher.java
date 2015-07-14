package com.dandewine.user.tocleveroad;

import android.app.Application;
import android.database.Cursor;

import com.dandewine.user.tocleveroad.db.MyContentProvider;

import java.util.ArrayList;

public class GoogleImageSearcher extends Application {
    private ArrayList<String> urlList;
    @Override
    public void onCreate() {
        super.onCreate();
        if(urlList==null)
            urlList=new ArrayList<>();
       Cursor c = getContentResolver().query(MyContentProvider.IMAGE_CONTENT_URI, null, null, null, null);
        while(c.moveToNext()){
            urlList.add(c.getString(2));
        }
        c.close();
    }

    public ArrayList<String> getUrlList() {
        return urlList;
    }
}
