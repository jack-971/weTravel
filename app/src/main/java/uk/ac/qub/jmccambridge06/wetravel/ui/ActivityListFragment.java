package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.Activity;
import uk.ac.qub.jmccambridge06.wetravel.Leg;


public class ActivityListFragment extends EntryListFragment {

    public ActivityListFragment(ArrayList<Activity> array) {
        super();
        list=array;
        logtag = "ActivityListFragment";
    }

    public ActivityListFragment() {
        super();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setAdapter() {
        adapter = new ActivityListAdapter(list, getContext());
    }



}
