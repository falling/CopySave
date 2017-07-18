package com.falling.copysave.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.falling.copysave.green.DaoMaster;
import com.falling.copysave.green.DaoSession;
import com.falling.copysave.green.NoteBeanDao;

/**
 * Created by falling on 2017/7/18.
 */

public class MyApplication extends Application {
    private NoteBeanDao mNoteDao;
    private static MyApplication sMyApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "NoteBean-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoSession daoSession = new DaoMaster(db).newSession();
        mNoteDao = daoSession.getNoteBeanDao();
    }

    public static NoteBeanDao getNoteDao() {
        return sMyApplication.mNoteDao;
    }
}
