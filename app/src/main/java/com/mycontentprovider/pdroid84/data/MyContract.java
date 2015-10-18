package com.mycontentprovider.pdroid84.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.mycontentprovider.pdroid84.MyHelper;

/**
 * Created by debashispaul on 23/09/2015.
 *
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 * A contract is the place where all the tables and related columns should be defiend
 * Also this is the place where URIs, Authority, PATHs should be defined along with the methods
 * which can be ued to create different types of URIs to access content provider's data and
 * parse URIs to retrieve data value which can be used in content provider for specific reasons
 */
public class MyContract {

    //Class name to be used in LOG TAG
    private final static String CLASS_NAME = MyContract.class.getSimpleName();

    //As per developer site the content authority name should be <package name>.provider
    public static final String CONTENT_AUTHORITY = "com.mycontentprovider.pdroid84.provider";

    //Define the scheme. ContentResolver.SCHEME_CONTENT =  "content"
    private static final String CONTENT_SCHEME = ContentResolver.SCHEME_CONTENT;

    //path for student table
    public static final String PATH_STUDENT = "student";

    //path for student marks
    public static final String PATH_MARKS = "marks";

    //Create BASE Content URI
    public static final Uri BAS_CONTENT_URI = Uri.parse(CONTENT_SCHEME+"://"+CONTENT_AUTHORITY);

    //As per Developer site -
    //Note: By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID
    // that some Android classes such as cursor adaptors will expect it to have.
    // It's not required, but this can help your database work harmoniously with the Android framework.


    // To prevent from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MyContract() {
        MyHelper.writeMsg(CLASS_NAME+"-->Constructor is called");
    }

    public static final class MyStudent implements BaseColumns {

        //create the content uri path for student table
        public static final Uri CONTENT_URI = BAS_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT).build();


        //Set the MIME type oc the content provider. Should have two types - one for single item and one for more than one.
        //As per developer site use "vnd.android.cursor.dir/vnd.<name>.<type>" (for more than one) and
        // "vnd.android.cursor.item/vnd.<name>.<type>" (for single item)
        // CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir" used for more than one item
        //CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item" used for single item
        //This is the MIME type for more than items - returns "vnd.android.cursor.dir/vnd.com.mycontentprovider.pdroid84.student"
        public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY
                + "." + PATH_STUDENT;
        //This is the MIME type for single item - returns "vnd.android.cursor.item/vnd.com.mycontentprovider.pdroid84.student"
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY
                + "." + PATH_STUDENT;

        //Define the table name and column names
        public static final String TABLE_NAME = "student";
        public static final String COLUMN_NAME_STDNT_ROLL = "stdntid";
        public static final String COLUMN_NAME_STDNT_NAME = "stdntname";
        public static final String COLUMN_NAME_STDNT_CLASS = "stdntclass";

        //Create method to create content uri with _ID
        public static Uri buildStudentUri (long id) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildStudentUri is called");
            //Use ContentUris because it has this pattern -> content://authority/path/id
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        //Create method to create content uri with student name
        public static Uri buildStudentNameUri (String name) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildStudentNameUri is called");
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        //Retrieve the ID from the Uri
        public static long getIdFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME + "-->getIdFromUri is called");
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        //Retrieve the Name from the Uri
        public static String getNameFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME+"-->getNameFromUri is called");
            return uri.getPathSegments().get(1);
        }

    }

    public static final class MyMarks implements BaseColumns {

        //create the content uri path for marks table
        public static final Uri CONTENT_URI = BAS_CONTENT_URI.buildUpon().appendPath(PATH_MARKS).build();

        //Set the MIME type of the content provider. Should have two types - one for single item and one for more than one.
        //As per developer site use "vnd.android.cursor.dir/vnd.<name>.<type>" (for more than one) and
        // "vnd.android.cursor.item/vnd.<name>.<type>" (for single item)
        // CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir" used for more than one item
        //CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item" used for single item
        //This is the MIME type for more than items - returns "vnd.android.cursor.dir/vnd.com.mycontentprovider.pdroid84.marks"
        public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY
                + "." + PATH_MARKS;
        //This is the MIME type for single item - returns "vnd.android.cursor.item/vnd.com.mycontentprovider.pdroid84.marks"
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTENT_AUTHORITY
                + "." + PATH_MARKS;

        public static final String TABLE_NAME = "marks";
        public static final String COLUMN_NAME_STDNT_KEY = "stdntkey";
        public static final String COLUMN_NAME_STDNT_SUBJ = "stdntsubj";
        public static final String COLUMN_NAME_STDNT_MARK = "stdntmark";
        public static final String COLUMN_NAME_STDNT_GRADE = "stdntgrade";

        //Create method to create content uri with _ID
        public static Uri buildMarkUri (long id) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildMarkUri is called");
            //Use ContentUris because it has this pattern -> content://authority/path/id
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        //Create method to create content uri for student roll and subject
        //Both are in different table but provider will handle that, provider just needs unique uri to identify it then
        // it can process based on the logic written for this URI
        public static Uri buildStudentRollWithSubjectUri (int roll,String subject) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildStudentRollWithSubjectUri is called");
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(roll)).appendPath(subject).build();
        }

        //This is same as before but here it's roll and mark
        //Also note here the Uri is built using query parameter (more like web url "...url?key=value")
        //Since it is used using query parameter, so only first part(i.e. roll) will be recognised using "/#"
        //in the content provider
        public static Uri buildStudentRollWithMarkUriAsQPerm (int roll, int mark) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildStudentWithMarkUri is called");
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(roll))
                    .appendQueryParameter(COLUMN_NAME_STDNT_MARK, Integer.toString(mark)).build();
        }

        //This is to build the uri with roll and mark
        public static Uri buildStudentRollWithMarkUri (int roll, int mark) {
            MyHelper.writeMsg(CLASS_NAME+"-->buildStudentWithMarkUri is called");
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(roll))
                    .appendPath(Integer.toString(mark)).build();
        }

        //Retrieve the student roll from the Uri
        //Since table (PATH) is the first appended value in the above two Uri builder methods, so this method
        //can be used to retrieve roll from both the Uris
        public static int getStudentRollFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME+"-->getStudentRollFromUri is called");
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        //Retrieve the subject from the Uri
        public static String getSubjectFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME+"-->getSubjectFromUri is called");
            return uri.getPathSegments().get(2);
        }

        //Retrieve the mark from the Uri
        public static int getMarkFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME+"-->getMarkFromUri is called");
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        //Retrieve the mark from the Uri
        //Since the Uri was built using queryParameter hence the retrieve method is also bit different !!!!
        public static int getQPermMarkFromUri (Uri uri) {
            MyHelper.writeMsg(CLASS_NAME+"-->getQPermMarkFromUri is called");
            String markString = uri.getQueryParameter(COLUMN_NAME_STDNT_MARK);
            //Ensure that the retrieved value is not NULL
            if (markString != null && markString.length() > 0) {
                return Integer.parseInt(markString);
            }
            else
            {
                //Notify that nothing found, send something (-1 here) to indicate error
                return -1;
            }
        }
    }
}
