package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.Trip;


public class LegListFragment extends EntryListFragment {


    public LegListFragment(LinkedHashMap<Integer, Leg> legs) {
        super();
        // Convert Linked hashmap to an array list so can be used in adapter
        ArrayList<Leg> array = new ArrayList<>();
        for (Leg leg : legs.values()) {
            array.add(leg);
        }
        list=array;
        logtag = "LegListFragment";
        Log.d(logtag, "list should be added");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setAdapter() {
        adapter = new LegListAdapter(list, getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
