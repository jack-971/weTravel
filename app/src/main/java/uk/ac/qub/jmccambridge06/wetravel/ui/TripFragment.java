package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
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

    private FusedLocationProviderClient fusedLocationClient;

    JsonFetcher jsonFetcher;
    NetworkResultCallback getTripCallback;
    NetworkResultCallback getLocationCallback;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check to see if there is an existing trip attached (this shows that the trip has come from a list and so data should be
        // retrieved from database. Otherwise it is a new trip and so no data required.
        if (trip != null) {
            loadTripDataCallback();
            jsonFetcher = new JsonFetcher(getTripCallback, getContext());
            jsonFetcher.getData(routes.getTrip(trip.getProfile().getUserId(), trip.getEntryId(), trip.getStatus()));
            if (trip.getStatus().equals("active")) {
                FloatingActionButton quickAdd = ((MainMenuActivity)getActivity()).findViewById(R.id.floating_action_button);
                //quickAdd.setVisibility(View.VISIBLE);
                quickAdd.show();
                quickAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            double latitude = location.getLatitude();
                                            double longditude =  location.getLongitude();
                                            Toast.makeText(getContext(), "location changed, latitude: "+latitude+" longditude: "+longditude, Toast.LENGTH_SHORT).show();
                                            quickAdd();
                                            JsonFetcher jsonFetcher = new JsonFetcher(getLocationCallback, getContext());
                                            jsonFetcher.getData(routes.getPlacesNearby(latitude, longditude));
                                        }
                                    }
                                });
                        // check locations are enabled on app.
                        ////if (((MainMenuActivity)getActivity()).getUserAccount().getSettings().get("location") == true) {
                            // check locations enabled in device

                            ////Locater locater = new Locater(getContext());
                            // load callback method
                            //get location
                            ////quickAdd();
                            /////locater.getLocation(getLocationCallback);
                            // once callback activated filter results and display in dialog - clickable to create new activity

                            //LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                            // check gps is on.
                            // get location coordinates (last known location)?
                            // geocoder check to see if coorindates match anything and retrieve top 5 within certain range
                            // show results to user and ask to pick one in dialog.
                            // once user picks one then edit activity opens and autopopulates. user can then save.
                            //Snackbar.make(view, "Add new event", Snackbar.LENGTH_LONG)
                                    //.setAction("Action", null).show();
                        //} else {
                            // toast
                        //}

                    }
                });
            }
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

    }

    /**
     * Callback to recieve the data from the Google Places API from the locater Java class. Extracts the name, id and address for each and prompts
     * a dialog fragment.
     */
    public void quickAdd() {
        getLocationCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {

                Log.i("data", response.toString());
                try {
                    ArrayList<TripLocation> locations = new ArrayList<>();
                    JSONArray resultsArray = response.getJSONArray("results");
                    for (int loop = 0; loop<resultsArray.length(); loop++) {
                        JSONObject result = resultsArray.getJSONObject(loop);
                        String name = result.getString("name");
                        String placeId = result.getString("place_id");
                        String vicinity = result.getString("vicinity");
                        TripLocation tripLocation = new TripLocation(placeId, name, vicinity);
                        locations.add(tripLocation);
                    }
                    showQuickAddDialog(locations);
                    return;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(VolleyError error) {

            }
        };
    }

    public void showQuickAddDialog(ArrayList<TripLocation> locations) {
        TripLocationDropdownAdapter tripLocationDropdownAdapter = new TripLocationDropdownAdapter(getContext(), locations);
        List legList = new ArrayList(trip.getLegs().values());
        LegDropdownAdapter legDropdownAdapter = new LegDropdownAdapter(getContext(), legList);
        addLocationDialog addLocationDialog = new addLocationDialog(legDropdownAdapter, tripLocationDropdownAdapter);
        addLocationDialog.show((getActivity()).getSupportFragmentManager(), "quick_add_location");
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
        private final int[] TAB_TITLES = new int[]{R.string.trip_details, R.string.trip_itinerary, R.string.trip_map};
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
            return 3;
        }

    }

    /**
     *
     * A dialog to enter a new activity. User must choose a leg and location from dropdown lists. Submission will open an autofilled activity.
     */
    public static class addLocationDialog extends DialogFragment {

        LegDropdownAdapter legs;
        TripLocationDropdownAdapter locations;

        public addLocationDialog(LegDropdownAdapter legs, TripLocationDropdownAdapter locations) {
            this.legs = legs;
            this.locations = locations;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setTitle(R.string.quick_add_title);
            View view = inflater.inflate(R.layout.dialog_location_quick_add, null);
            builder.setView(view);
            Spinner legSpinner = view.findViewById(R.id.leg_selection);
            Spinner locationSpinner = view.findViewById(R.id.location_selection);
            legSpinner.setAdapter(legs);
            locationSpinner.setAdapter(locations);
            builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // launch activity details fragment.
                            ActivityDetailsFragment activityDetailsFragment = new ActivityDetailsFragment();
                            // pass details into activity fragment
                            activityDetailsFragment.quickAdd((Leg) legSpinner.getSelectedItem(), (TripLocation) locationSpinner.getSelectedItem());
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                                    activityDetailsFragment, "activities_details_fragment").addToBackStack(null).commit();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }

    public class TripLocationDropdownAdapter extends ArrayAdapter<TripLocation> {

        private List<TripLocation> locationsList;

        public TripLocationDropdownAdapter(@NonNull Context context, @NonNull List<TripLocation> locations) {
            super(context, 0, locations);
            locationsList = new ArrayList<TripLocation>(locations);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_dropdown, parent, false
                );
            }
            TextView textName = convertView.findViewById(R.id.spinner_dropdown);
            TripLocation tripLocation = getItem(position);
            if (tripLocation !=null) {
                textName.setText(tripLocation.getName());
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View dropDownView = inflater.inflate(R.layout.spinner_dropdown, parent, false);

            TextView text = (TextView)dropDownView.findViewById(R.id.spinner_dropdown);
            text.setText(locationsList.get(position).getName());

            return dropDownView;
        }
    }

    public class LegDropdownAdapter extends ArrayAdapter<Leg> {

        private List<Leg> legList;

        public LegDropdownAdapter(@NonNull Context context, @NonNull List<Leg> legs) {
            super(context, 0, legs);
            legList = new ArrayList<Leg>(legs);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_dropdown, parent, false
                );
            }
            TextView textName = convertView.findViewById(R.id.spinner_dropdown);
            Leg leg = getItem(position);
            if (leg !=null) {
                textName.setText(leg.getEntryName());
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View dropDownView = inflater.inflate(R.layout.spinner_dropdown, parent, false);

            TextView text = (TextView)dropDownView.findViewById(R.id.spinner_dropdown);
            text.setText(legList.get(position).getEntryName());

            return dropDownView;
        }
    }


}


