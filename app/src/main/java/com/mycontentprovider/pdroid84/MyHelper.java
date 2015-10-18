package com.mycontentprovider.pdroid84;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mycontentprovider.pdroid84.data.MyContract.*;

import java.net.URI;
import java.util.List;
import java.util.Vector;

/**
 * Created by debashispaul on 27/09/2015.
 */
public class MyHelper {
    private final static String LOG_TAG = "DEB";

    public static void writeMsg(String msg) {
        Log.d(LOG_TAG, msg);
    }

    public static void showMytoast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void testMimeType(Context context) {
        String retVal;
        retVal = null;
        //Test student type
        retVal = context.getContentResolver().getType(MyStudent.CONTENT_URI);
        writeMsg(MyStudent.CONTENT_URI.toString() + "-->" + retVal);
        //Test student type with id
        retVal = context.getContentResolver().getType(MyStudent.buildStudentUri(9));
        writeMsg(MyStudent.buildStudentUri(9).toString() + "-->" + retVal);
        //Test student type with Name
        retVal = context.getContentResolver().getType(MyStudent.buildStudentNameUri("Name1"));
        writeMsg(MyStudent.buildStudentNameUri("Name1").toString() + "-->" + retVal);
        //Test marks type
        retVal = context.getContentResolver().getType(MyMarks.CONTENT_URI);
        writeMsg(MyMarks.CONTENT_URI.toString() + "-->" + retVal);
        //Test marks type with roll and subject
        retVal = context.getContentResolver().getType(MyMarks.buildStudentRollWithSubjectUri(8, "English"));
        writeMsg(MyMarks.buildStudentRollWithSubjectUri(8, "English") + "-->" + retVal);
        //Test marks type with roll and mark
        retVal = context.getContentResolver().getType(MyMarks.buildStudentRollWithMarkUri(8, 70));
        writeMsg(MyMarks.buildStudentRollWithMarkUri(8, 70) + "-->" + retVal);
        //Test marks type with roll and mark - use the Query Parameter URI builder method
        retVal = context.getContentResolver().getType(MyMarks.buildStudentRollWithMarkUriAsQPerm(9, 90));
        writeMsg(MyMarks.buildStudentRollWithMarkUriAsQPerm(9, 90) + "-->" + retVal);
    }

    //This method is to insert records in Student table
    //Just to use same method to add records with different values special handling is done
    //which is dynamic and self explanatory
    public static MarksHelper[] insertStudent(Context context, int seq) {
        String[] classArray = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
        //Define a content value
        ContentValues mNewValues = new ContentValues();
        //Define an array for MarksHelper
        MarksHelper [] arrayMarksHelper = new MarksHelper[3];
        //Define the return URI
        Uri mNewUri;
        //Set the values
        mNewValues.put(MyStudent.COLUMN_NAME_STDNT_ROLL, seq);
        mNewValues.put(MyStudent.COLUMN_NAME_STDNT_CLASS, classArray[seq]);
        mNewValues.put(MyStudent.COLUMN_NAME_STDNT_NAME, "Name " + seq + 1);

        mNewUri = context.getContentResolver().insert(MyStudent.CONTENT_URI, mNewValues);
        writeMsg("Inserted record-> " + mNewUri);

        //Now create the associated marks entries
        long id = MyStudent.getIdFromUri(mNewUri);
        String grade = getGrade(seq*10);
        MarksHelper marksHelper1 = new MarksHelper(id, "Chemistry", seq * 10, grade);
        MarksHelper marksHelper2 = new MarksHelper(id, "Physics", seq * 10, grade);
        MarksHelper marksHelper3 = new MarksHelper(id, "Math", seq * 10, grade);
        arrayMarksHelper[0] = marksHelper1;
        arrayMarksHelper[1] = marksHelper2;
        arrayMarksHelper[2] = marksHelper3;
        return arrayMarksHelper;
    }

    //Determine the grade based on marks
    public static String getGrade(int mark) {
        String retGrade;
        if (mark > 80) {
            retGrade = "Grade A+";
        } else if (mark > 70) {
            retGrade = "Grade A";
        } else {
            retGrade = "Grade A-";
        }
        writeMsg("Marks-->" + mark);
        writeMsg("Grade-->" + retGrade);
        return retGrade;
    }

    //This method is to delete records from Student table
    //which is dynamic and self explanatory
    public static void deleteStudent(Context context, int id) {
        String mSelectionClause = MyStudent._ID + " >= ?";
        String[] mSelectionArgs = {Integer.toString(id)};
        int mRowsDeleted = 0;

        mRowsDeleted = context.getContentResolver().delete(MyStudent.CONTENT_URI, mSelectionClause, mSelectionArgs);

        writeMsg("Students Records deleted-> " + mRowsDeleted);

    }

    //This method is to delete records from Mark table
    //which is dynamic and self explanatory
    public static void deleteMark (Context context, int id) {
        String mSelectionClause = MyMarks.COLUMN_NAME_STDNT_KEY + " >= ?";
        String[] mSelectionArgs = {Integer.toString(id)};
        int mRowsDeleted = 0;

        mRowsDeleted = context.getContentResolver().delete(MyMarks.CONTENT_URI, mSelectionClause, mSelectionArgs);

        writeMsg("Marks Records deleted-> " + mRowsDeleted);

    }

    public static void insertMarks(Context context, List<MarksHelper> listMark) {

        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(listMark.size());

        for (int i = 0; i < listMark.size(); i++) {
            ContentValues markValues = new ContentValues();
            MarksHelper mhelp = listMark.get(i);
            markValues.put(MyMarks.COLUMN_NAME_STDNT_KEY, mhelp.getKey());
            markValues.put(MyMarks.COLUMN_NAME_STDNT_SUBJ, mhelp.getSubj());
            markValues.put(MyMarks.COLUMN_NAME_STDNT_MARK, mhelp.getMark());
            markValues.put(MyMarks.COLUMN_NAME_STDNT_GRADE, mhelp.getGrade());
            cVVector.add(markValues);
        }

        int inserted =0;
        //call bulkInsert to load the data
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(MyMarks.CONTENT_URI,cvArray);
        }
        writeMsg("Bulk Insert of Marks complete. Total insert: " + cVVector.size());
    }

    public static void selectQuery (Context context) {
        String[] projectionStudent = {
                MyStudent._ID,
                MyStudent.COLUMN_NAME_STDNT_ROLL,
                MyStudent.COLUMN_NAME_STDNT_NAME,
                MyStudent.COLUMN_NAME_STDNT_CLASS
        };
        String[] projectionMarks = {
                MyMarks._ID,
                MyMarks.COLUMN_NAME_STDNT_KEY,
                MyMarks.COLUMN_NAME_STDNT_SUBJ,
                MyMarks.COLUMN_NAME_STDNT_MARK,
                MyMarks.COLUMN_NAME_STDNT_GRADE
        };
        String[] projectionJoin = {
                MyStudent.TABLE_NAME + "." + MyStudent._ID,
                MyStudent.COLUMN_NAME_STDNT_ROLL,
                MyStudent.COLUMN_NAME_STDNT_NAME,
                MyStudent.COLUMN_NAME_STDNT_CLASS,
                MyMarks.TABLE_NAME + "." + MyMarks._ID,
                MyMarks.COLUMN_NAME_STDNT_KEY,
                MyMarks.COLUMN_NAME_STDNT_SUBJ,
                MyMarks.COLUMN_NAME_STDNT_MARK,
                MyMarks.COLUMN_NAME_STDNT_GRADE
        };
        Cursor crsr;
        String studentSortOrder = MyStudent._ID + " ASC";
        String markSortOrder = MyMarks._ID + " ASC";
        String joinOrder = MyStudent.TABLE_NAME + "." + MyStudent._ID + ", " + MyMarks.COLUMN_NAME_STDNT_KEY + " ASC";
        Uri uri;

        //Query the student data
        uri = MyStudent.CONTENT_URI;
        crsr = context.getContentResolver().query(uri,projectionStudent,null,null,studentSortOrder);
        displayStudent(crsr);

        //Query student data with id
        uri = MyStudent.buildStudentUri(5);
        crsr = context.getContentResolver().query(uri, projectionStudent, null, null, studentSortOrder);
        displayStudent(crsr);

        //Query student with name
        uri = MyStudent.buildStudentNameUri("Name 81");
        crsr = context.getContentResolver().query(uri, projectionStudent, null, null, studentSortOrder);
        displayStudent(crsr);

        //Query marks
        uri = MyMarks.CONTENT_URI;
        crsr = context.getContentResolver().query(uri,projectionMarks,null,null,markSortOrder);
        displayMark(crsr);

        //Query join (two tables student and marks) with roll and subject
        uri = MyMarks.buildStudentRollWithSubjectUri(3, "Math");
        crsr = context.getContentResolver().query(uri,projectionJoin,null,null,joinOrder);
        displayJoin(crsr);

        //Query join (two tables student and marks) with roll and mark
        uri = MyMarks.buildStudentRollWithMarkUri(5, 30);
        crsr = context.getContentResolver().query(uri, projectionJoin, null, null, joinOrder);
        displayJoin(crsr);

        //Query join (two tables student and marks) with roll and mark - QueryParm
        uri = MyMarks.buildStudentRollWithMarkUriAsQPerm(7,30);
        crsr = context.getContentResolver().query(uri, projectionJoin, null, null, joinOrder);
        displayJoin(crsr);
    }

    public static void updateStudent (Context context, String newName, int roll) {
        Uri uri;
        uri = MyStudent.CONTENT_URI;
        ContentValues cValue = new ContentValues();
        String selection = MyStudent.COLUMN_NAME_STDNT_ROLL + " = ? ";
        String selArgs[] = {Integer.toString(roll)};
        cValue.put(MyStudent.COLUMN_NAME_STDNT_NAME,newName);
        int count;
        count = context.getContentResolver().update(uri,cValue,selection,selArgs);
        writeMsg("Total student record update -->" + count);

    }


    public static void updateMark (Context context, int id, int mark) {
        Uri uri;
        uri = MyMarks.CONTENT_URI;
        ContentValues cValue = new ContentValues();
        String selection = MyMarks.COLUMN_NAME_STDNT_KEY + " = ? ";
        String selArgs[] = {Integer.toString(id)};
        cValue.put(MyMarks.COLUMN_NAME_STDNT_MARK,mark);
        int count;
        count = context.getContentResolver().update(uri,cValue,selection,selArgs);
        writeMsg("Total marks record update -->" + count);

    }

    private static void displayStudent (Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            writeMsg("Student Query result -->");
            while (cursor.isAfterLast() == false) {
                writeMsg(MyStudent._ID + " = " + cursor.getInt(cursor.getColumnIndex(MyStudent._ID))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_ROLL + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_ROLL))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_NAME + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_NAME))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_CLASS + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_CLASS)));
                cursor.moveToNext();
            }
        }
        else {
            writeMsg("No record fetched!!");
        }
        cursor.close();
    }

    private static void displayMark (Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            writeMsg("Mark Query result -->");
            while (cursor.isAfterLast() == false) {
                writeMsg(MyMarks._ID + " = " + cursor.getInt(cursor.getColumnIndex(MyMarks._ID))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_KEY + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_KEY))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_SUBJ + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_SUBJ))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_MARK + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_MARK))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_GRADE + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_GRADE)));
                cursor.moveToNext();
            }
        }
        else {
            writeMsg("No record fetched!!");
        }
        cursor.close();
    }

    private static void displayJoin (Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            writeMsg("Join (Student & Marks) Query result -->");
            while (cursor.isAfterLast() == false) {
                writeMsg(MyStudent._ID + " = " + cursor.getInt(cursor.getColumnIndex(MyStudent._ID))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_ROLL + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_ROLL))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_NAME + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_NAME))
                        + " || " +
                        MyStudent.COLUMN_NAME_STDNT_CLASS + " = " + cursor.getString(cursor.getColumnIndex(MyStudent.COLUMN_NAME_STDNT_CLASS))
                        + " || " +
                        MyMarks._ID + " = " + cursor.getInt(cursor.getColumnIndex(MyMarks._ID))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_KEY + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_KEY))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_SUBJ + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_SUBJ))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_MARK + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_MARK))
                        + " || " +
                        MyMarks.COLUMN_NAME_STDNT_GRADE + " = " + cursor.getString(cursor.getColumnIndex(MyMarks.COLUMN_NAME_STDNT_GRADE)));
                cursor.moveToNext();
            }
        }
        else {
            writeMsg("No record fetched!!");
        }
        cursor.close();
    }
}
