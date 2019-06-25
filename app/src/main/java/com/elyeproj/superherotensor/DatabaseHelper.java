package com.elyeproj.superherotensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Students.sqlite";
    private static final String TABLE_NAME = "StudentAttendance";

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "S_NAME TEXT,ATT_COUNT INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertStudent(String student_name , Integer att_count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("S_NAME", student_name);
        contentValues.put("ATT_COUNT", att_count);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public int updateattendance(String student_name , Integer att_count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ATT_COUNT", att_count);
        return db.update(TABLE_NAME,contentValues,"S_NAME = ?",new String[]{student_name});
    }


    public Cursor getAttendance(String student_name) {
        SQLiteDatabase db = this.getWritableDatabase();
         return db.query(TABLE_NAME,new String[]{"ATT_COUNT"},"S_NAME = ?",new String[]{student_name},null,null,null);
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+ TABLE_NAME,null);
    }
}


