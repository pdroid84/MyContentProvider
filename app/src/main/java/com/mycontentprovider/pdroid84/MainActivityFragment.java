package com.mycontentprovider.pdroid84;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycontentprovider.pdroid84.mycontentprovider.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //Class name to be used in LOG TAG
    private final static String CLASS_NAME = MainActivityFragment.class.getSimpleName();

    private final static String TAG = "DEB";
    public MainActivityFragment() {
        MyHelper.writeMsg(CLASS_NAME + "-->Constructor is called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyHelper.writeMsg(CLASS_NAME + "-->onCreateView is called");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
