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

import uk.ac.qub.jmccambridge06.wetravel.models.Activity;
import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class ActivityDetailsFragment extends TripEntryFragment {

    Activity activity;
    private Leg leg;
    private TripLocation quickAddLocation;
    boolean quickAdd;

    /**
     *
     * @param activity
     */
    public ActivityDetailsFragment(Activity activity) {
        super(activity);
        this.activity = activity;
        this.quickAdd = false;
        this.type = "activity";
    }

    /**
     *
     */
    public ActivityDetailsFragment() {
        super();
        this.quickAdd = false;
        this.type = "activity";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Activity Details";

        if (activity == null) {
            if (quickAdd == true) {
                tripName.setText(quickAddLocation.getName());
                location.setText(quickAddLocation.getVicinity());
                location.setTag(quickAddLocation.getId());
                startDate.setText(DateTime.todaysDate());
                finishDate.setText(DateTime.todaysDate());
            }
        }

        if (quickAdd == false) {
            LegItineraryFragment legItineraryFragment = (LegItineraryFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("leg_itinerary_fragment");
            leg = legItineraryFragment.leg;
        }
    }

    @Override
    public void loadDetails() {
        super.loadDetails();

        if (activity.getNotes() != null) {
            notes.setText(activity.getNotes());
            completeNotes.setText(activity.getNotes());
        }

        if (quickAdd == false) {
            LegItineraryFragment legItineraryFragment = (LegItineraryFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("leg_itinerary_fragment");
            leg = legItineraryFragment.leg;
        }

        ArrayList<Profile> profiles = new ArrayList<>();
        // for each person on the trip, if they are not already in the user activity list then they can be added.
        for (int value : leg.getUserList().keySet()) {
            if (!activity.getUserList().containsKey(value)) {
                Profile profile = new Profile(value, leg.getUserList().get(value), 1);
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
        jsonFetcher.addParam("leg", String.valueOf(leg.getEntryId()));
        jsonFetcher.addParam("Notes", notes.getText().toString());
        jsonFetcher.addParam("type", "activity");
        jsonFetcher.addParam("status", leg.getStatus()); // use leg so that if activity doesnt exist (is being created) then won't throw an exception
        if (activity == null) {
            jsonFetcher.postDataVolley(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        } else {
            jsonFetcher.addParam("activityId", String.valueOf(activity.getEntryId()));
            jsonFetcher.patchData(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        }
    }

    @Override
    protected void saveTripData() {
        saveEntryCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                if (activity == null) {
                    try {
                        // create the activity.
                        JSONArray activityArray = response.getJSONArray("data");
                        JSONObject activityData = activityArray.getJSONObject(0);
                        Activity activity = new Activity(activityData, ((MainMenuActivity) getActivity()).getUserAccount().getProfile());
                        activity.setStatus(leg.getStatus());
                        leg.addActivity(activity);
                        activity.setUserList(((MainMenuActivity)getActivity()).getUserAccount());
                        setItem(activity);
                        if (quickAdd == false) {
                            ((MainMenuActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                                    new ActivityDetailsFragment(activity)).addToBackStack(null).commit();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
                            tripAttendeesView.setVisibility(View.VISIBLE);
                            tripAddAttendeesView.setVisibility(View.VISIBLE);
                            leaveButton.setVisibility(View.VISIBLE);
                            addGalleryImageButton.setVisibility(View.VISIBLE);
                            galleryButton.setVisibility(View.VISIBLE);
                            loadDetails();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //If existing trip overwrites its instance vars.
                    activity.setEntryName(tripName.getText().toString());
                    activity.setStartDate(DateTime.formatDate(startDate.getText().toString()));
                    activity.setEndDate(DateTime.formatDate(finishDate.getText().toString()));
                    activity.setDescription(description.getText().toString());
                    if (activity.getLocation().getId() == null) {
                        activity.setLocation(null, null);
                    } else {
                        activity.setLocation(location.getTag().toString(), location.getText().toString());
                    }
                    activity.setNotes(notes.getText().toString());
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
                leg.getActivities().remove(activity.getEntryId());
                ((MainMenuActivity)getActivity()).getSupportFragmentManager().popBackStack();
                Toast.makeText(getActivity().getApplicationContext(), "You have left "+activity.getEntryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Error - please try again", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Prepares the fragment for quick add activity by setting the autofill values carried through from the quick add dialog.
     */
    public void quickAdd(Leg leg, TripLocation tripLocation) {
        quickAdd = true;
        this.leg = leg;
        this.quickAddLocation = tripLocation;
    }

}
