package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Holds logic for a list of trips
 */
public class TripListFragment extends ListFragment {

    private String status;
    private Profile profile;

    protected String logtag;
    protected JsonFetcher jsonFetcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).removeNavBar();
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "TripListFragment";
        loadCallback();
        getData();
    }

    /**
     * Gets the trips data
     */
    void getData() {
        jsonFetcher = new JsonFetcher(getCallback,getContext());
        jsonFetcher.getData(routes.getTrips(profile.getUserId(), status));
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setAdapter() {
        adapter = new TripListAdapter(list, getContext());
    }

    @Override
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    protected void noData() {
        super.noData();
        noDataText.setText(R.string.error_no_trips);
    }

    @Override
    protected void fillList(View view, JSONArray jsonArray) throws JSONException {
        for (int loop=0; loop<jsonArray.length(); loop++) {
            JSONObject item = jsonArray.getJSONObject(loop);
            try {
                Trip currentTrip = new Trip(item, profile);
                currentTrip.setProfile(profile);
                list.add(currentTrip);
            } catch (Exception e) {
                Log.e(logtag, "Error creating entry from data:" + item);
                e.printStackTrace();
            }
        }
        updateList();
    }

}
