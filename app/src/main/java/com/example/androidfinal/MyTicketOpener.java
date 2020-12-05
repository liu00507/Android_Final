package com.example.androidfinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyTicketOpener extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME ="TicketDB";
    protected final static int VERSION_NUM = 2;
    public final static String TABLE_NAME ="TICKET";
    public final static String COL_NAME = "event_Name";
    public final static String COL_START_DATE_TIME = "startDateTime";
    public final static String COL_PRICE_MIN = "price_Min";
    public final static String COL_PRICE_MAX = "price_Max";
    public final static String COL_URL = "ticket_URL";
    public final static String COL_IMAGE = "tickect_image";

     public final static String COL_ID = "_ID";


    /*
        constructor
     */

    public MyTicketOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME +" text,"  + COL_START_DATE_TIME +" text," + COL_PRICE_MIN +" text,"
                + COL_PRICE_MAX + " text," + COL_URL + " text,"
                        + COL_IMAGE + " text);");  //add or remove columns
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db); //Create a new table
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

}
