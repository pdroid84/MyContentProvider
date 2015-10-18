package com.mycontentprovider.pdroid84;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mycontentprovider.pdroid84.data.MyContract;
import com.mycontentprovider.pdroid84.mycontentprovider.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Class name to be used in LOG TAG
    private final static String CLASS_NAME = MainActivity.class.getSimpleName();

    private static final String TAG = "DEB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyHelper.writeMsg(CLASS_NAME + "-->onCreate is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Call this to test the MIME type
        //MyHelper.testMimeType(this);

        //Call this to create student record. Helper class cane handle 10 records
        //Refer the method description in helper class for more details
        //Please note this should not be done on UI thread but this is testing after all!!
        /*
        MarksHelper[] marksHelper;
        List<MarksHelper> lMarksHelper = new ArrayList<MarksHelper>();
        marksHelper = MyHelper.insertStudent(this, 1);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,2);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,3);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,4);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,5);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,6);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,7);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,8);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);
        marksHelper = MyHelper.insertStudent(this,9);
        lMarksHelper = retriwveData(lMarksHelper, marksHelper);

        //Now pass the list and create the mark records
        MyHelper.insertMarks(this,lMarksHelper);
        */

        //Call this with id to delete the record from Student
        //MyHelper.deleteStudent(this,1);

        //Call this with id to delete the record from Marks
        //MyHelper.deleteMark(this,1);

        //call the student update query
        MyHelper.updateStudent(this,"Robert", 7);
        //call the mark update query
        MyHelper.updateMark(this,6,88);

        //call the select query
        MyHelper.selectQuery(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyHelper.writeMsg(CLASS_NAME + "-->onCreateOptionsMenu is called");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyHelper.writeMsg(CLASS_NAME + "-->onOptionsItemSelected is called");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static List<MarksHelper> retriwveData (List<MarksHelper> lMarkList, MarksHelper[] marksHelpers) {
        for (int i = 0 ; i < marksHelpers.length; i++) {
            lMarkList.add(marksHelpers[i]);
        }
        return lMarkList;
    }
}
