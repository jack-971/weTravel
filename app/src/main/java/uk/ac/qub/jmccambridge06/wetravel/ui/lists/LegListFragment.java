package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.qub.jmccambridge06.wetravel.models.Leg;

/**
 * Contains controller logic for a list of legs
 */
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
