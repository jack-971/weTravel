package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class LegDetailsFragment extends TripEntryFragment {

    private Leg leg;

    /**
     * Constructor with leg argument - used for showing existing legs
     * @param leg
     */
    public LegDetailsFragment(Leg leg) {
        super(leg);
        this.leg = leg;
        this.type = "leg";
    }

    public LegDetailsFragment() {
        this.type = "leg";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Leg Details";

        TripFragment tripFragment = (TripFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("user_trip_fragment");
        trip = tripFragment.getTrip();

    }

    @Override
    public void loadDetails() {
        super.loadDetails();

        ArrayList<Profile> profiles = new ArrayList<>();

        TripFragment tripFragment = (TripFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("user_trip_fragment");
        trip = tripFragment.getTrip();

        // for each person on the trip, if they are not already in the user leg list then they can be added.
        for (int value : trip.getUserList().keySet()) {
            if (!leg.getUserList().containsKey(value)) {
                Profile profile = new Profile(value, trip.getUserList().get(value), 1);
                profiles.add(profile);
            }
        }
        addAttendeesSetUp(profiles);
    }

    @Override
    protected void sendData() {
        saveTripRequest();
    }

    @Override
    protected void saveTripRequest() {
        super.saveTripRequest();
        jsonFetcher.addParam("type", "leg");
        jsonFetcher.addParam("status", trip.getStatus());
        jsonFetcher.addParam("trip", String.valueOf(trip.getEntryId()));
        if (leg == null) {
            jsonFetcher.postDataVolley(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        } else {
            jsonFetcher.addParam("legId", String.valueOf(leg.getEntryId()));
            jsonFetcher.patchData(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        }


    }

    @Override
    protected void saveTripData() {
        saveEntryCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {

                if (leg == null) {
                    try {
                        // create the leg.
                        JSONArray tripArray = response.getJSONArray("data");
                        JSONObject legData = tripArray.getJSONObject(0);
                        Leg leg = new Leg(legData, ((MainMenuActivity) getActivity()).getUserAccount().getProfile());
                        trip.addLeg(leg);
                        leg.setUserList(((MainMenuActivity)getActivity()).getUserAccount());
                        ((MainMenuActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                                new LegItineraryFragment(leg), "leg_itinerary_fragment").addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //If existing trip overwrites its instance vars.
                    leg.setEntryName(tripName.getText().toString());
                    leg.setStartDate(DateTime.formatDate(startDate.getText().toString()));
                    leg.setEndDate(DateTime.formatDate(finishDate.getText().toString()));
                    leg.setDescription(description.getText().toString());
                    if (leg.getLocation().getId() == null) {
                        leg.setLocation(null, null);
                    } else {
                        leg.setLocation(location.getTag().toString(), location.getText().toString());
                    }
                    Toast.makeText(getActivity().getApplicationContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void leave() {
        leaveTripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                trip.getLegs().remove(leg.getEntryId());
                ((MainMenuActivity)getActivity()).getSupportFragmentManager().popBackStack();
                Toast.makeText(getActivity().getApplicationContext(), "You have left "+leg.getEntryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Error - please try again", Toast.LENGTH_SHORT).show();
            }
        };
    }

}
