package com.mycontentprovider.pdroid84.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.mycontentprovider.pdroid84.MyHelper;
import com.mycontentprovider.pdroid84.data.MyContract;
import com.mycontentprovider.pdroid84.data.MyContract.MyStudent;
import com.mycontentprovider.pdroid84.data.MyContract.MyMarks;
import com.mycontentprovider.pdroid84.data.MyDatabase;

/**
 * Created by debashispaul on 24/09/2015.
 * As per Developer site -
 * A content provider manages access to a central repository of data.
 * A provider is part of an Android application, which often provides its own UI for working with the data.
 */
public class MyProvider extends ContentProvider {

    //Class name to be used in LOG TAG
    private final static String CLASS_NAME = MyProvider.class.getSimpleName();

    // Creates a UriMatcher object. Use a method myUriMatcher to put everything  together
    private static final UriMatcher sUriMatcher = myUriMatcher();
    //Create a variable for Database
    private MyDatabase mMyDatabase;

    //Define integer values which will be used to attach an authority and path
    //Same value is return by the match() method
    private static final int STUDENT = 10;
    private static final int STUDENT_WITH_ID = 11;
    private static final int STUDENT_WITH_NAME = 12;
    private static final int MARKS = 20;
    private static final int MARKS_WITH_ROLL_SUBJECT = 21;
    private static final int MARKS_WITH_ROLL_MARK = 22;
    private static final int MARKS_WITH_ROLL_MARK_QPERM = 23;

    private static final SQLiteQueryBuilder myQueryBuilderJoin, myQueryBuilderStudent, myQueryBuilderMarks;

    //Usually the selection argument is something passed from the calling module but these are defined here
    //to keep everything in a single place ask call module to just pass null
    private static final String selectionStudent = MyStudent.TABLE_NAME + "." + MyStudent._ID + " > ? ";
    //format -> student._id = ?
    private static final String selectionStudentId = MyStudent.TABLE_NAME + "." + MyStudent._ID + " = ?";
    //format -> student.stdntname = ?
    private static final String selectionStudentName = MyStudent.TABLE_NAME + "." + MyStudent.COLUMN_NAME_STDNT_NAME + " = ?";
    //format -> marks._id > ?
    private static final String selectionMarks = MyMarks.TABLE_NAME + "." + MyMarks._ID + " > ? ";
    //format -> student.stdntid = ? AND marks.stdntsubj = ?
    private static final String selectionRollSubj = MyStudent.TABLE_NAME + "." + MyStudent.COLUMN_NAME_STDNT_ROLL
            + " = ? AND " + MyMarks.TABLE_NAME + "." + MyMarks.COLUMN_NAME_STDNT_SUBJ + " = ? ";
    //format -> student.stdntid = ? AND marks.stdntmark >= ?
    private static final String selectionRollMark = MyStudent.TABLE_NAME + "." + MyStudent.COLUMN_NAME_STDNT_ROLL
            + " = ? AND " + MyMarks.TABLE_NAME + "." + MyMarks.COLUMN_NAME_STDNT_MARK + " >= ? ";

    static {
        MyHelper.writeMsg(CLASS_NAME + "-->static part is called");
        //Define the helper classes which will built the query for us
        //This is for student table only
        myQueryBuilderStudent = new SQLiteQueryBuilder();
        myQueryBuilderStudent.setTables(MyStudent.TABLE_NAME);

        //This is for marks table only
        myQueryBuilderMarks = new SQLiteQueryBuilder();
        myQueryBuilderMarks.setTables(MyMarks.TABLE_NAME);

        //This is for join becuase data required from both the tables
        myQueryBuilderJoin = new SQLiteQueryBuilder();

        //If the data is fetched from a single table then that particular table is set using .setTables but
        //since here the data will be retrieved from two tables, so an inner join is required to retrieve the data.
        //it looks like "student INNER JOIN marks ON student._id = marks.stdntkey"
        myQueryBuilderJoin.setTables(MyStudent.TABLE_NAME + " INNER JOIN " +
                MyMarks.TABLE_NAME +
                " ON " + MyStudent.TABLE_NAME +
                "." + MyStudent._ID +
                " = " + MyMarks.TABLE_NAME +
                "." + MyMarks.COLUMN_NAME_STDNT_KEY);
    }

    private static UriMatcher myUriMatcher (){
        MyHelper.writeMsg(CLASS_NAME + "-->myUriMatcher is called");

        //The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Let's add all the URIs
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_STUDENT,STUDENT);
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_STUDENT+"/#",STUDENT_WITH_ID);
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_STUDENT+"/*",STUDENT_WITH_NAME);
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_MARKS,MARKS);
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_MARKS+"/#/#",MARKS_WITH_ROLL_MARK);
        //Note - order matter. If you use /#/# after /#/* then second numeric is considered as alphanumeric!! Need further investigation
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_MARKS+"/#/*",MARKS_WITH_ROLL_SUBJECT);
        //This is for the URI where Student roll and mark URI is built using query parameter.
        //But notice that we are just considering roll, hence "/#" pattern
        matcher.addURI(MyContract.CONTENT_AUTHORITY,MyContract.PATH_MARKS+"/#",MARKS_WITH_ROLL_MARK_QPERM);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        MyHelper.writeMsg(CLASS_NAME + "-->onCreate is called");

        //Android calls this when it starts up the provider
        //This is called when a ContentResolver tries to access the provider for the first time
        //Do only fast running initialisation, do not perform lengthy process here

        //Create an instance of the database
        mMyDatabase = new MyDatabase(getContext());

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MyHelper.writeMsg(CLASS_NAME + "-->query is called");
        Cursor retCursor;
        //Identify the URIs by using the URI matcher
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENT:
                retCursor = myQueryBuilderStudent.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionStudent,
                        new String[] {Integer.toString(0)},
                        null,
                        null,
                        sortOrder
                        );
                break;
            case STUDENT_WITH_ID:
                long id = MyStudent.getIdFromUri(uri);
                retCursor = myQueryBuilderStudent.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionStudentId,
                        new String[] {Long.toString(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case STUDENT_WITH_NAME:
                String name = MyStudent.getNameFromUri(uri);
                retCursor = myQueryBuilderStudent.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionStudentName,
                        new String[] {name},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKS:
                retCursor = myQueryBuilderMarks.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionMarks,
                        new String[] {Integer.toString(0)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKS_WITH_ROLL_SUBJECT:
                int roll = MyMarks.getStudentRollFromUri(uri);
                String subj = MyMarks.getSubjectFromUri(uri);
                retCursor = myQueryBuilderJoin.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionRollSubj,
                        new String[] {Integer.toString(roll),subj},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKS_WITH_ROLL_MARK:
                int roll2 = MyMarks.getStudentRollFromUri(uri);
                int mark = MyMarks.getMarkFromUri(uri);
                //MyHelper.writeMsg("roll->"+roll2);
                //MyHelper.writeMsg("mark->"+mark);
                retCursor = myQueryBuilderJoin.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        selectionRollMark,
                        new String[]{Integer.toString(roll2), Integer.toString(mark)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MARKS_WITH_ROLL_MARK_QPERM:
                int roll3 = MyMarks.getStudentRollFromUri(uri);
                int mark2 = MyMarks.getQPermMarkFromUri(uri);
                retCursor = myQueryBuilderJoin.query(mMyDatabase.getReadableDatabase(),
                        projection,
                        //Since the selection parameters are same, so used the same variable
                        selectionRollMark,
                        new String[] {Integer.toString(roll3),Integer.toString(mark2)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Cannot serve the uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        MyHelper.writeMsg(CLASS_NAME + "-->getType is called");

        //Identify the URIs by using the URI matcher
        final int match = sUriMatcher.match(uri);

        //Return DIR type if more than one record or ITEM if single record
        switch (match) {
            case STUDENT:
                return MyStudent.CONTENT_TYPE_DIR;
            case STUDENT_WITH_ID:
                return MyStudent.CONTENT_TYPE_ITEM;
            case STUDENT_WITH_NAME:
                return MyStudent.CONTENT_TYPE_ITEM;
            case MARKS:
                return MyMarks.CONTENT_TYPE_DIR;
            case MARKS_WITH_ROLL_SUBJECT:
                //We are expecting a single record when student roll and subject is given
                return MyMarks.CONTENT_TYPE_ITEM;
            case MARKS_WITH_ROLL_MARK:
                //Here we will retrieve => mark. So will be more than one record
                return MyMarks.CONTENT_TYPE_DIR;
            case MARKS_WITH_ROLL_MARK_QPERM:
                //Here we will retrieve => mark. So will be more than one record
                return MyMarks.CONTENT_TYPE_DIR;
            default:
                throw new UnsupportedOperationException("Cannot serve the uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        MyHelper.writeMsg(CLASS_NAME + "-->insert is called");
        //Create an instance of writable database
        final SQLiteDatabase db = mMyDatabase.getWritableDatabase();
        //Identify the URIs by using the URI matcher
        final int match = sUriMatcher.match(uri);
        //define a long if to hold the return value
        long _id = 0;
        //define the return uri
        Uri retUri;
        switch (match) {
            case STUDENT:
                _id = db.insert(MyStudent.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = MyContract.MyStudent.buildStudentUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed the insertion for uri "+uri);
                }
                break;
            case MARKS:
                _id = db.insert(MyMarks.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = MyContract.MyMarks.buildMarkUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed the insertion for uri "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        //Notify that there is a change becuase new data got added
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        MyHelper.writeMsg(CLASS_NAME + "-->delete is called");
        final SQLiteDatabase db = mMyDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowCount = 0;
        // If selection is null then throw an error
        if ( selection == null ) {
            throw new UnsupportedOperationException("Cannot perform delete without selection. uri: "+uri);
        }
        switch(match) {
            case STUDENT:
                rowCount = db.delete(MyStudent.TABLE_NAME,selection,selectionArgs);
                break;
            case MARKS:
                rowCount = db.delete(MyMarks.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: "+uri);
        }
        //If all records are not deleted then notify the data change
        if(rowCount != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        MyHelper.writeMsg(CLASS_NAME + "-->update is called");
        final SQLiteDatabase db = mMyDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowCount = 0;
        switch(match) {
            case STUDENT:
                rowCount = db.update(MyStudent.TABLE_NAME,values,selection,selectionArgs);
                break;
            case MARKS:
                rowCount = db.update(MyMarks.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: "+uri);
        }
        //Notify the data change
        if(rowCount != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowCount;
    }

    //bulkInsert is not an abstract method so override is optional
    //Use this method to insert in bulk
    //make sure callNotify is called
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        MyHelper.writeMsg(CLASS_NAME + "-->bulkInsert is called");
        //Create an instance of writable database
        final SQLiteDatabase db = mMyDatabase.getWritableDatabase();
        //Identify the URIs by using the URI matcher
        final int match = sUriMatcher.match(uri);
        //Initialise the return counter
        int returnCount = 0;
        switch (match) {
            case MARKS:
                //Start a transaction
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MyMarks.TABLE_NAME,null,value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                //Notify that there is a change becuase new data got added
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri,values);
        }
    }
}
