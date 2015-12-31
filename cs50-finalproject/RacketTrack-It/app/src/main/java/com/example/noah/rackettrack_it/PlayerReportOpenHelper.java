package com.example.noah.rackettrack_it;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * assist in the creation of a database of reports
 */
public class PlayerReportOpenHelper extends SQLiteOpenHelper {
    // database fields
    public static int NUM_OVERALL_RATINGS = 4;
    public static int NUM_STROKES = 5;
    public static String ID = "id";
    public static String PLAYERID = "playerid";
    public static String DT = "dt";
    public static String ATTACK = "attack";
    public static String CONSISTENCY = "consistency";
    public static String POWER = "power";
    public static String RUNSPEED = "runspeed";
    public static String FOREHAND = "forehand";
    public static String BACKHAND = "backhand";
    public static String FIRSTSERVE = "firstserve";
    public static String SECONDSERVE = "secondserve";
    public static String NETPLAY = "netplay";
    public static String REPORTID = "reportid";
    public static String OVERALL = "overall";
    public static String FIRSTSET = "firstset";
    public static String SECONDSET = "secondset";
    public static String THIRDSET = "thirdset";
    public static String WINLOSS = "winloss";
    public static String[] OVERALL_RATINGS = {"attack", "consistency", "power", "runspeed"};
    // used to display stroke types for user interface
    public static String[] STROKE_TYPES_PRETTY = {"forehand", "backhand", "first serve", "second serve", "net play"};
    // used to display comments on different stroke types, including overall
    public static String[] STROKE_TYPES_BIG = {"overall", "forehand", "backhand", "firstserve", "secondserve", "netplay"};
    public static String[] STROKE_TYPES = {"forehand", "backhand", "firstserve", "secondserve", "netplay"};
    public static String[] SETS = {"firstset", "secondset", "thirdset"};



    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "tennisrackettrackit2";
    public static final int NUM_FIELDS = 12;
    public static final String PLAYER_TABLE_NAME = "playerreports";
    public static final String ADVANCED_TABLE_NAME = "advancedreports";
    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + PLAYER_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PLAYERID + " INTEGER, " +
                    DT + " INTEGER, " +
                    ATTACK + " INTEGER(1), " +
                    CONSISTENCY + " INTEGER(1), " +
                    POWER + " INTEGER(1), " +
                    RUNSPEED + " INTEGER(1), " +
                    FOREHAND + " INTEGER(2), " +
                    BACKHAND + " INTEGER(2), " +
                    FIRSTSERVE + " INTEGER(2), " +
                    SECONDSERVE + " INTEGER(2), " +
                    NETPLAY + " INTEGER(2)" +
                    ");";
    private static final String ADVANCED_TABLE_CREATE =
            "CREATE TABLE " + ADVANCED_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    REPORTID + " INTEGER, " +
                    OVERALL + " TEXT, " +
                    FOREHAND + " TEXT, " +
                    BACKHAND + " TEXT, " +
                    FIRSTSERVE + " TEXT, " +
                    SECONDSERVE + " TEXT, " +
                    NETPLAY + " TEXT, " +
                    FIRSTSET + " INTEGER(3), " +
                    SECONDSET + " INTEGER(3), " +
                    THIRDSET + " INTEGER(3), " +
                    WINLOSS + " VARCHAR(1)" +
                    ");";

    PlayerReportOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLAYER_TABLE_CREATE);
        db.execSQL(ADVANCED_TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int OldVersion, int newVersion) {
        try {
            throw new NoSuchMethodException();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
