package com.mycontentprovider.pdroid84.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mycontentprovider.pdroid84.MyHelper;
import com.mycontentprovider.pdroid84.data.MyContract.MyStudent;
import com.mycontentprovider.pdroid84.data.MyContract.MyMarks;

/**
 * Created by debashispaul on 23/09/2015.
 */
public class MyDatabase extends SQLiteOpenHelper{

    //Class name to be used in LOG TAG
    private final static String CLASS_NAME = MyDatabase.class.getSimpleName();

    // If there is any change in the database schema, the version must be incremented.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Students.db";

    private static final String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE " + MyStudent.TABLE_NAME + " (" +
            MyStudent._ID + " INTEGER PRIMARY KEY," +
            MyStudent.COLUMN_NAME_STDNT_NAME + " TEXT NOT NULL, " +
            MyStudent.COLUMN_NAME_STDNT_ROLL + " INTEGER NOT NULL, " +
            MyStudent.COLUMN_NAME_STDNT_CLASS + " TEXT NOT NULL " +
            " );";

    private static final String SQL_CREATE_STUDENT_MARKS_TABLE = "CREATE TABLE " + MyMarks.TABLE_NAME + " (" +
            MyMarks._ID + " INTEGER PRIMARY KEY," +
            //STDNT_KEY is the Foreign key, associated with _ID of MyStudent
            MyMarks.COLUMN_NAME_STDNT_KEY + " INTEGER NOT NULL, " +
            MyMarks.COLUMN_NAME_STDNT_SUBJ + " TEXT NOT NULL, " +
            MyMarks.COLUMN_NAME_STDNT_MARK + " INTEGER NOT NULL, " +
            MyMarks.COLUMN_NAME_STDNT_GRADE + " TEXT NOT NULL, " +
            // Set up the STDNT_KEY column as a foreign key to MyStudent table.
            " FOREIGN KEY (" + MyMarks.COLUMN_NAME_STDNT_KEY + ") REFERENCES " +
            MyStudent.TABLE_NAME + " (" + MyStudent._ID + "), " +
            // To assure that the table has only one entry for each subject for each student,
            // it's created a UNIQUE constraint with REPLACE strategy
            " UNIQUE (" + MyMarks.COLUMN_NAME_STDNT_SUBJ + ", " +
            MyMarks.COLUMN_NAME_STDNT_KEY + ") ON CONFLICT REPLACE);";

    private static final String SQL_DELETE_STUDENT_TABLE = "DROP TABLE IF EXISTS " + MyStudent.TABLE_NAME;
    private static final String SQL_DELETE_STUDENT_MARKS_TABLE = "DROP TABLE IF EXISTS " + MyMarks.TABLE_NAME;

    //No default constructor is available in SQLiteOpenHelper, so define one
    public MyDatabase (Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        MyHelper.writeMsg(CLASS_NAME + "-->Constructor is called");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        MyHelper.writeMsg(CLASS_NAME + "-->onCreate is called");

        //Create the tables
        db.execSQL(SQL_CREATE_STUDENT_TABLE);
        db.execSQL(SQL_CREATE_STUDENT_MARKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MyHelper.writeMsg(CLASS_NAME + "-->onUpgrade is called");

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_STUDENT_TABLE);
        db.execSQL(SQL_DELETE_STUDENT_MARKS_TABLE);
        onCreate(db);
    }

    // Downgrade method is not abstract, so it is not mandatory for a customer to implement it.
    // If not overridden, default implementation will reject downgrade and throws SQLiteException.
    //This is strictly similar to onUpgrade(SQLiteDatabase, int, int) method,
    // but is called whenever current version is newer than requested one.
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MyHelper.writeMsg(CLASS_NAME + "-->onDowngrade is called");

        onUpgrade(db, oldVersion, newVersion);
    }
}
