package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.qub.jmccambridge06.wetravel.models.Activity;


public class ActivityListFragment extends EntryListFragment {

    public ActivityListFragment(LinkedHashMap<Integer, Activity> activities) {
        super();
        // Convert Linked hashmap to an array list so can be used in adapter
        ArrayList<Activity> array = new ArrayList<>();
        for (Activity activity : activities.values()) {
            array.add(activity);
        }
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
