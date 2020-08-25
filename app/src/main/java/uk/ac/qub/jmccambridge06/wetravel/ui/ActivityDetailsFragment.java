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
import java.util.Date;

import uk.ac.qub.jmccambridge06.wetravel.Activity;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;

public class ActivityDetailsFragment extends TripEntryFragment {

    Activity activity;
    private Leg leg;
    private TripLocation quickAddLocation;
    boolean quickAdd;

    /**
     * Constructor with leg argument - used for showing existing legs
     * @param activity
     */
    public ActivityDetailsFragment(Activity activity) {
        super();
        this.activity = activity;
        this.quickAdd = false;
    }

    /**
     * Default constructor used for creating new legs
     */
    public ActivityDetailsFragment() {
        this.quickAdd = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Leg Details";
        addAllCheckBox.setVisibility(View.GONE);
        tripPictureView.setVisibility(View.GONE);
        tripTimeView.setVisibility(View.GONE);
        tripRatingView.setVisibility(View.GONE);

        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;

        if (quickAdd == false) {
            LegItineraryFragment legItineraryFragment = (LegItineraryFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("leg_itinerary_fragment");
            leg = legItineraryFragment.leg;
        }

        if (activity != null) {
            initialiseStartDate=activity.getStartDate();
            initialiseFinishDate=activity.getEndDate();
            loadDetails();
            if (activity.getStatus().equalsIgnoreCase("complete")) {
                displayComplete();
            }
        } else {
            tripAttendeesView.setVisibility(View.GONE);
            tripAddAttendeesView.setVisibility(View.GONE);
            leaveButton.setVisibility(View.GONE);
            if (quickAdd == true) {
                tripName.setText(quickAddLocation.getName());
                location.setText(quickAddLocation.getVicinity());
                location.setTag(quickAddLocation.getId());
                startDate.setText(DateTime.todaysDate());
                finishDate.setText(DateTime.todaysDate());
            }
        }

        startDate.setOnClickListener(new EditTextDateClicker(getContext(), startDate, initialiseStartDate));
        finishDate.setOnClickListener(new EditTextDateClicker(getContext(), finishDate, initialiseFinishDate));
    }

    @Override
    public void loadDetails() {
        tripName.setText(activity.getEntryName());
        completeTripName.setText(activity.getEntryName());
        if (activity.getStartDate() != null) {
            startDate.setText(DateTime.formatDate(activity.getStartDate()));
            completeDateStart.setText(DateTime.formatDate(activity.getStartDate()));
        }
        if (activity.getEndDate() != null) {
            finishDate.setText(DateTime.formatDate(activity.getEndDate()));
            completeDateFinish.setText(DateTime.formatDate(activity.getEndDate()));
        }
        if (activity.getDescription() != null) {
            description.setText(activity.getDescription());
            completeDescription.setText(activity.getDescription());
        }
        if (activity.getLocation() !=null) {
            location.setText(activity.getLocation().getName());
            completeTripLocation.setText(activity.getLocation().getName());
            location.setTag(activity.getLocation().getId());
        }
        if (activity.getNotes() != null) {
            notes.setText(activity.getNotes());
            completeNotes.setText(activity.getNotes());
        }

        if (activity.getReview() != null) {
            review.setText(activity.getReview());
            completeReview.setText(activity.getReview());
        }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(activity.getUserList().values()));
        completeAttendees.setText(addUsers(activity.getUserList().values()));
        ArrayList<Profile> profiles = new ArrayList<>();
        // for each person on the trip, if they are not already in the user leg list then they can be added.
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
                        if (quickAdd == false) {
                            ((MainMenuActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                                    new ActivityDetailsFragment(activity)).addToBackStack(null).commit();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
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
    protected void sendLeaveRequest() {
        leave();
        jsonFetcher = new JsonFetcher(leaveTripCallback, getContext());
        jsonFetcher.deleteData(routes.leaveTrip(((MainMenuActivity)getActivity()).getUserAccount().getUserId(), activity.getEntryId(), "activity"));
    }

    @Override
    protected void sendAttendeeData() {
        // Check to make sure not already in trip - checking the id captured in suggestions box
        for (int userId : activity.getUserList().keySet()) {
            if ((Integer)addAttendees.getTag() == userId) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_friend_already_added, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // If not on trip then send data to database
        saveAttendee();
        jsonFetcher = new JsonFetcher(addAttendeeCallback, getContext());
        jsonFetcher.addParam("user", addAttendees.getTag().toString());
        jsonFetcher.addParam("type", "activity");
        jsonFetcher.addParam("legId", String.valueOf(leg.getEntryId()));
        jsonFetcher.postDataVolley(routes.addUserToTrip(activity.getEntryId()));
    }

    protected void saveAttendee() {
        addAttendeeCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // Update the phone display to reflect new attendee in attendees list
                activity.getUserList().put((Integer)addAttendees.getTag(), addAttendees.getText().toString());
                attendees.setText(addUsers(activity.getUserList().values()));
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_attendee, Toast.LENGTH_SHORT).show();
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
