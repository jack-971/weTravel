package uk.ac.qub.jmccambridge06.wetravel.ui.newsfeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.ui.ListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

public class NewsfeedFragment extends ListFragment {

    public NewsfeedFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).showNavBar();
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logtag = "NotificationListFragment";
        super.onViewCreated(view, savedInstanceState);
        if (((MainMenuActivity)getActivity()).getUserAccount().getNewsfeed() != null) {
            updateFeed(((MainMenuActivity)getActivity()).getUserAccount().getNewsfeed());
        }
    }



    @Override
    public void setAdapter() {
        adapter = new NewsfeedAdapter(list, getContext());
    }

    public void updateFeed(TreeMap<Long, ItineraryItem> mapItems) {
        for (ItineraryItem item : mapItems.values()) {
            list.add(item);
        }
        Collections.reverse(list);
        updateList();
    }

}
