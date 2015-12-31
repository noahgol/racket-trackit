package com.example.noah.rackettrack_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * assist in the creation/reading/writing of a database of players
 */
public class PlayerOpenHelper extends SQLiteOpenHelper {
    // database columns
    public static String ID = "id";
    public static String GENDER = "gender";
    public static String HAND = "hand";
    public static String FIRSTNAME = "firstname";
    public static String LASTNAME = "lastname";
    public static String TOWN = "town";
    public static String BIRTH = "birthyr";

    private static final int DATABASE_VERSION = 2;
    public static final int NUM_FIELDS = 5;
    public static final String DATABASE_NAME = "tennistrackerdb";
    public static final String PLAYER_TABLE_NAME = "players";
    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + PLAYER_TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            GENDER + " VARCHAR(1), " +
            HAND + " VARCHAR(1), " +
            FIRSTNAME + " VARCHAR(255), " +
            LASTNAME + " VARCHAR(255), " +
            TOWN + " VARCHAR(255), " +
            BIRTH + " INT(4)" +
            ")";

    PlayerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLAYER_TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int OldVersion, int newVersion) {
        try {
            throw new NoSuchMethodException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


}
