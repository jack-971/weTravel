package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;

public class TripFragment extends Fragment {

    TripDetailsFragment tripDetailsFragment;
    TripItineraryFragment tripItineraryFragment;
    TripAddFragment tripAddFragment;
    TripNotesFragment tripNotesFragment;
    TripMapFragment tripMapFragment;

    private Trip trip;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    JsonFetcher jsonFetcher;
    NetworkResultCallback getTripCallback;

    String logtag = "Trip Fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        View v = inflater.inflate(R.layout.trip_fragment, container, false);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check to see if there is an existing trip attached (this shows that the trip has come from a list and so data should be
        // retrieved from database. Otherwise it is a new trip and so no data required.
        if (trip != null) {
            loadTripDataCallback();
            jsonFetcher = new JsonFetcher(getTripCallback, getContext());
            jsonFetcher.getData(routes.getTrip(trip.getProfile().getUserId(), trip.getEntryId()));
        } else {
            tripDetailsFragment = new TripDetailsFragment();
            tripItineraryFragment = new TripItineraryFragment();
            tripAddFragment = new TripAddFragment();
            tripNotesFragment = new TripNotesFragment();
            tripMapFragment = new TripMapFragment();
            sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), (getChildFragmentManager()));
            viewPager = getView().findViewById(R.id.view_pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            TabLayout tabs = getView().findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
        }

        /*if (trip.getStatus().equals("active")) {
            FloatingActionButton fab = ((MainMenuActivity)getActivity()).findViewById(R.id.floating_action_button);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Add new event", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }*/

    }

    /**
     * Callback for all data for the trip. Takes it in JSON format, parses it and calls utility methods to arrange save and arrange the data for
     * later processing. Once the data is loaded then the view pager and associated fragments can be loaded with the data.
     */
    void loadTripDataCallback() {
        getTripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logtag, "Successful JSON trip data request:" + response);
                try {
                    // load the users data into the trip
                    JSONArray users = response.getJSONArray("users");
                    getTrip().setUserList(users);
                    // load the legs data into the trip
                    JSONArray legs = response.getJSONArray("legs");
                    getTrip().setLegs(legs);
                    JSONArray activities = response.getJSONArray("activities");
                    getTrip().addActivities(activities);
                    tripDetailsFragment = new TripDetailsFragment(trip);
                    tripItineraryFragment = new TripItineraryFragment(trip);
                    tripAddFragment = new TripAddFragment();
                    tripNotesFragment = new TripNotesFragment();
                    tripMapFragment = new TripMapFragment();
                    sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), (getChildFragmentManager()));
                    viewPager = getView().findViewById(R.id.view_pager);
                    viewPager.setAdapter(sectionsPagerAdapter);
                    TabLayout tabs = getView().findViewById(R.id.tabs);
                    tabs.setupWithViewPager(viewPager);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Log.e(logtag, "Error retrieving data for trip");
                error.printStackTrace();
            }
        };
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {
        return sectionsPagerAdapter;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.trip_details, R.string.trip_itinerary, R.string.trip_notes, R.string.trip_map};
        private final Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = tripDetailsFragment;
                    break;
                case 1:
                    fragment = tripItineraryFragment;
                    break;
                case 2:
                    fragment = tripNotesFragment;
                    break;
                case 3:
                    fragment = tripMapFragment;
                    break;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return 4;
        }

    }

}


