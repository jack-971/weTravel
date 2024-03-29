package uk.ac.qub.jmccambridge06.wetravel.ui.newsfeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.TreeMap;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.ListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Contains controller logic for newsfeed list page
 */
public class NewsfeedFragment extends ListFragment {

    /**
     * Default constructor
     */
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
        // if there is a newsfeed update it
        if (((MainMenuActivity)getActivity()).getUserAccount().getNewsfeed() != null) {
            updateFeed(((MainMenuActivity)getActivity()).getUserAccount().getNewsfeed());
        }
    }



    @Override
    public void setAdapter() {
        adapter = new NewsfeedAdapter(list, getContext());
    }

    /**
     * Takes a newsfeed sorted by time and adds them to an array list before reversing
     * to get the most recent posts first
     * @param mapItems
     */
    public void updateFeed(TreeMap<Long, ItineraryItem> mapItems) {
        if (mapItems.size() == 0) {
            noData();
        } else {
            for (ItineraryItem item : mapItems.values()) {
                list.add(item);
            }
            Collections.reverse(list);
            updateList();
        }

    }

    @Override
    protected void noData() {
        super.noData();
        noDataText.setText("No items to view - add friends to view their latest posts!" +
                "\n\nSearch for friends using the search bar in the top corner.");
    }

}
