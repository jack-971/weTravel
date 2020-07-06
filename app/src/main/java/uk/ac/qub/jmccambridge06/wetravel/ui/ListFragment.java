package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;

/**
 * Abstract class for creating list fragments.
 */
public abstract class ListFragment extends Fragment {

    public String logtag;
    public ArrayList list;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    NetworkResultCallback getCallback = null;
    JsonFetcher jsonFetcher;

    Profile profile;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the friends data
        if (savedInstanceState == null) {

        }

        Log.i(logtag, "new list view created");
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new UserListAdapter(list, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadCallback();
        getData();
    }

    /**
     * Method retrieves user data from the API.
     */
    abstract void getData();

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
                    fillList(getView(), jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.d(logtag, "Error on JSON callback for friend list");
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
    abstract void fillList(View view, JSONArray jsonArray) throws JSONException;

    public void updateList() {
        adapter.notifyDataSetChanged();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

}



