package com.suffhillrabbitfarm.rabbitinfosystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RabbitFarm.db";
    private static final int DATABASE_VERSION = 1;

    // Rabbit Profile Table
    private static final String TABLE_RABBITS = "rabbits";
    private static final String COL_ID = "id";
    static final String COL_RABBIT_ID = "rabbit_id";
    private static final String COL_BREED = "breed";
    private static final String COL_COLOR = "color";
    private static final String COL_WEIGHT = "weight";
    private static final String COL_DOB = "dob";
    private static final String COL_FATHER_ID = "father_id";
    private static final String COL_MOTHER_ID = "mother_id";
    private static final String COL_STATUS = "status";

    private static final String COL_SLAUGHTER_DATE = "slaughter_date";
    private static final String COL_DEATH_DATE = "death_date";
    private static final String COL_DEATH_CAUSE = "death_cause";
    private static final String COL_OBSERVATIONS = "observations";

    private static final String COL_UPDATE_TIMESTAMP = "update_timestamp";

    // Mating History Table
    private static final String TABLE_MATING = "mating_history";
    private static final String COL_MATING_ID = "mating_id";
    private static final String COL_RABBIT_ID_FK = "rabbit_id_fk";
    private static final String COL_MATING_DATE = "mating_date";
    private static final String COL_MATING_PARTNER = "mating_partner";
    private static final String COL_NUM_NEWBORNS = "num_newborns";
    private static final String COL_NUM_DEATHS = "num_deaths";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRabbitTable = "CREATE TABLE " + TABLE_RABBITS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RABBIT_ID + " TEXT UNIQUE, " +
                COL_BREED + " TEXT, " +
                COL_COLOR + " TEXT, " +
                COL_WEIGHT + " REAL, " +
                COL_DOB + " TEXT, " +
                COL_FATHER_ID + " TEXT, " +
                COL_MOTHER_ID + " TEXT, " +
                COL_STATUS + " TEXT, " +
                COL_SLAUGHTER_DATE + " TEXT, " +
                COL_DEATH_DATE + " TEXT, " +
                COL_DEATH_CAUSE + " TEXT, " +
                COL_OBSERVATIONS + " TEXT, " +
                COL_UPDATE_TIMESTAMP + " TEXT)";
        db.execSQL(createRabbitTable);

        String createMatingTable = "CREATE TABLE " + TABLE_MATING + " (" +
                COL_MATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RABBIT_ID_FK + " TEXT, " +
                COL_MATING_DATE + " TEXT, " +
                COL_MATING_PARTNER + " TEXT, " +
                COL_NUM_NEWBORNS + " INTEGER, " +
                COL_NUM_DEATHS + " INTEGER, " +
                "FOREIGN KEY(" + COL_RABBIT_ID_FK + ") REFERENCES " + TABLE_RABBITS + "(" + COL_RABBIT_ID + "))";
        db.execSQL(createMatingTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RABBITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATING);
        onCreate(db);
    }

    // Add a new rabbit
    public boolean addRabbit(String rabbitId, String breed, String color, float weight, String dob,
                             String fatherId, String motherId, String status, String observations) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RABBIT_ID, rabbitId);
        values.put(COL_BREED, breed);
        values.put(COL_COLOR, color);
        values.put(COL_WEIGHT, weight);
        values.put(COL_DOB, dob);
        values.put(COL_FATHER_ID, fatherId);
        values.put(COL_MOTHER_ID, motherId);
        values.put(COL_STATUS, status);
        values.put(COL_OBSERVATIONS, observations);
        values.put(COL_UPDATE_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        long result = db.insert(TABLE_RABBITS, null, values);
        db.close();
        return result != -1;
    }

    // Get all rabbits
    public Cursor getAllRabbits() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RABBITS, null);
    }

    // Update rabbit
    public boolean updateRabbit(String rabbitId, String breed, String color, float weight, String status,
                                String observations, String slaughterDate, String deathDate, String deathCause) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BREED, breed);
        values.put(COL_COLOR, color);
        values.put(COL_WEIGHT, weight);
        values.put(COL_STATUS, status);
        values.put(COL_OBSERVATIONS, observations);
        values.put(COL_SLAUGHTER_DATE, slaughterDate);
        values.put(COL_DEATH_DATE, deathDate);
        values.put(COL_DEATH_CAUSE, deathCause);
        values.put(COL_UPDATE_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        int result = db.update(TABLE_RABBITS, values, COL_RABBIT_ID + "=?", new String[]{rabbitId});
        db.close();
        return result > 0;
    }

    // Add mating record
    public boolean addMatingRecord(String rabbitId, String matingDate, String matingPartner, int numNewborns, int numDeaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_RABBIT_ID_FK, rabbitId);
        values.put(COL_MATING_DATE, matingDate);
        values.put(COL_MATING_PARTNER, matingPartner);
        values.put(COL_NUM_NEWBORNS, numNewborns);
        values.put(COL_NUM_DEATHS, numDeaths);

        long result = db.insert(TABLE_MATING, null, values);
        db.close();
        return result != -1;
    }
}