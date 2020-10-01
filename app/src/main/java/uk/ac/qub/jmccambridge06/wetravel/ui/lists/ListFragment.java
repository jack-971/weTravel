package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;

/**
 * Abstract class for creating list fragments.
 */
public abstract class ListFragment extends Fragment {

    @BindView(R.id.list_no_data) protected TextView noDataText;

    public String logtag;
    public ArrayList list;
    private RecyclerView recyclerView;
    protected RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    NetworkResultCallback getCallback = null;
    JsonFetcher jsonFetcher;

    Profile profile;

    /**
     * default constructor
     */
    public ListFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        setAdapter();
        recyclerView.setAdapter(adapter);
    }

    /**
     * Callback for loading the user data into the fragment.
     */
    public void loadCallback() {
        getCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logtag, "Successful JSON list request:" + response);
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        noData();
                    }
                    fillList(getView(), jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.d(logtag, "Error on JSON callback for list");
                error.printStackTrace();
            }
        };
    };

    /**
     * Updates the list with data from a JSON array. Error thrown if issue reading data.
     * @param view
     * @param jsonArray
     * @throws JSONException
     */
    protected void fillList(View view, JSONArray jsonArray) throws JSONException {
        Log.i(logtag, "filling list now" + jsonArray);
        for (int loop=0; loop<jsonArray.length(); loop++) {
            JSONObject item = jsonArray.getJSONObject(loop);
            try {
                Trip currentTrip = new Trip(item, profile);
                Log.d(logtag, "in add item");
                currentTrip.setProfile(profile);
                list.add(currentTrip);
            } catch (Exception e) {
                Log.e(logtag, "Error creating entry from data:" + item);
                e.printStackTrace();
            }
        }
        updateList();
    }


    /**
     * Notify adapter of change to lsit content
     */
    public void updateList() {
        adapter.notifyDataSetChanged();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public abstract void setAdapter();

    /**
     * Displays a message stating there is no data to display for the fragment list.
     */
    protected void noData() {
        noDataText.setVisibility(View.VISIBLE);
    }
}



