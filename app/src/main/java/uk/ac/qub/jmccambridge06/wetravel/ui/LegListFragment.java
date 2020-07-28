package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import uk.ac.qub.jmccambridge06.wetravel.Leg;


public class LegListFragment extends EntryListFragment {

    public LegListFragment(ArrayList<Leg> array) {
        super();
        list=array;
        logtag = "LegListFragment";
        Log.d(logtag, "list should be added");
    }

    @Override
    public void setAdapter() {
        adapter = new LegListAdapter(list, getContext());
    }



}
