package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

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
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

/**
 * Holds controller logic for an activities details
 */
public class ActivityDetailsFragment extends TripEntryFragment {

    Activity activity;
    private Leg leg;
    private TripLocation quickAddLocation;

    /**
     * if true then activity has been set up using quick add button. This means some details will be
     * autocompleted
     */
    boolean quickAdd;

    /**
     *Constructor with args - used for an existing activity
     * @param activity
     */
    public ActivityDetailsFragment(Activity activity) {
        super(activity);
        this.activity = activity;
        this.quickAdd = false;
        this.type = "activity";
    }

    /**
     *Constructor for new activities
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
                entryName.setText(quickAddLocation.getName());
                location.setText(quickAddLocation.getVicinity());
                location.setTag(quickAddLocation.getId());
                startDate.setText(DateTime.todaysDate());
                finishDate.setText(DateTime.todaysDate());
            }
        }

        // if not a quick add then leg fragment will already be open and can be identified by tag
        if (quickAdd == false) {
            LegItineraryFragment legItineraryFragment = (LegItineraryFragment) getActivity().getSupportFragmentManager().findFragmentByTag("leg_itinerary_fragment");
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
        // if not a quick add then leg fragment will already be open and can be identified by tag
        if (quickAdd == false) {
            LegItineraryFragment legItineraryFragment = (LegItineraryFragment) getActivity().getSupportFragmentManager().findFragmentByTag("leg_itinerary_fragment");
            leg = legItineraryFragment.leg;
        }

        ArrayList<Profile> profiles = new ArrayList<>();
        // for each person on the leg, if they are not already in the user activity list then they can be added.
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
        saveEntryRequest();
    }

    @Override
    protected void saveEntryRequest() {
        super.saveEntryRequest();
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
    protected void saveEntryData() {
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
                            // if not quick add then open up in a new activity fragment
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                                    new ActivityDetailsFragment(activity)).addToBackStack(null).commit();
                        } else {
                            // if its a quick add then no new activity opened so these are made visible
                            Toast.makeText(getActivity().getApplicationContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
                            item = activity;
                            entryAttendeesView.setVisibility(View.VISIBLE);
                            entryAddAttendeesView.setVisibility(View.VISIBLE);
                            leaveButton.setVisibility(View.VISIBLE);
                            addGalleryImageButton.setVisibility(View.VISIBLE);
                            galleryButton.setVisibility(View.VISIBLE);
                            loadDetails();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //If existing activity overwrites its instance vars.
                    activity.setEntryName(entryName.getText().toString());
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
        leaveEntryCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                leg.getActivities().remove(item.getEntryId());
                getActivity().getSupportFragmentManager().popBackStack();
                Toast.makeText(getActivity().getApplicationContext(), "You have left "+item.getEntryName(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void sendAttendeeData() {
        super.sendAttendeeData();
        jsonFetcher.addParam("legId", String.valueOf(leg.getEntryId()));
        jsonFetcher.postDataVolley(routes.addUserToTrip(item.getEntryId()));
    }
}
