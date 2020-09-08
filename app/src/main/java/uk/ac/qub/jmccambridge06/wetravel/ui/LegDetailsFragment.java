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

import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;

public class LegDetailsFragment extends TripEntryFragment {

    private Leg leg;

    /**
     * Constructor with leg argument - used for showing existing legs
     * @param leg
     */
    public LegDetailsFragment(Leg leg) {
        super();
        this.leg = leg;
    }

    public LegDetailsFragment() {
        super();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Leg Details";
        addAllCheckBox.setVisibility(View.GONE);
        tripPictureView.setVisibility(View.GONE);
        tripTimeView.setVisibility(View.GONE);
        tripNotesView.setVisibility(View.GONE);
        tripRatingView.setVisibility(View.GONE);

        tripAttachmentsView.setVisibility(View.GONE);

        TripFragment tripFragment = (TripFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("user_trip_fragment");
        trip = tripFragment.getTrip();

        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;
        if (leg != null) {
            initialiseStartDate=leg.getStartDate();
            initialiseFinishDate=leg.getEndDate();
            loadDetails();
            if (leg.getStatus().equalsIgnoreCase("complete")) {
                displayComplete();
            } else if (leg.getStatus().equalsIgnoreCase("active")) {

            } else{
                tripReviewView.setVisibility(View.GONE);
            }
        } else {
            tripAttendeesView.setVisibility(View.GONE);
            tripAddAttendeesView.setVisibility(View.GONE);
            leaveButton.setVisibility(View.GONE);
        }

        startDate.setOnClickListener(new EditTextDateClicker(getContext(), startDate, initialiseStartDate));
        finishDate.setOnClickListener(new EditTextDateClicker(getContext(), finishDate, initialiseFinishDate));


    }

    @Override
    public void loadDetails() {
        tripName.setText(leg.getEntryName());
        completeTripName.setText(leg.getEntryName());
        if (leg.getStartDate() != null) {
            startDate.setText(DateTime.formatDate(leg.getStartDate()));
            completeDateStart.setText(DateTime.formatDate(leg.getStartDate()));
        }
        if (leg.getEndDate() != null) {
            finishDate.setText(DateTime.formatDate(leg.getEndDate()));
            completeDateFinish.setText(DateTime.formatDate(leg.getEndDate()));
        }
        if (leg.getDescription() != null) {
            description.setText(leg.getDescription());
            completeDescription.setText(leg.getDescription());
        }
        if (leg.getLocation() !=null) {
            location.setText(leg.getLocation().getName());
            completeTripLocation.setText(leg.getLocation().getName());
            location.setTag(leg.getLocation().getId());
        }
        if (trip.getReview() != null) {
            review.setText(leg.getReview());
            completeReview.setText(leg.getReview());
        }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(leg.getUserList().values()));
        completeAttendees.setText(addUsers(leg.getUserList().values()));
        ArrayList<Profile> profiles = new ArrayList<>();

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
    protected void sendLeaveRequest() {
        leave();
        jsonFetcher = new JsonFetcher(leaveTripCallback, getContext());
        jsonFetcher.deleteData(routes.leaveTrip(((MainMenuActivity)getActivity()).getUserAccount().getUserId(), leg.getEntryId(), "leg"));
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

    @Override
    protected void sendAttendeeData() {
        // Check to make sure not already in trip - checking the id captured in suggestions box
        for (int userId : leg.getUserList().keySet()) {
            if ((Integer)addAttendees.getTag() == userId) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_friend_already_added, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // If not on trip then send data to database
        saveAttendee();
        jsonFetcher = new JsonFetcher(addAttendeeCallback, getContext());
        jsonFetcher.addParam("user", addAttendees.getTag().toString());
        jsonFetcher.addParam("type", "leg");
        jsonFetcher.addParam("tripId", String.valueOf(trip.getEntryId()));
        jsonFetcher.postDataVolley(routes.addUserToTrip(leg.getEntryId()));
    };

    protected void saveAttendee() {
        addAttendeeCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // Update the phone display to reflect new attendee in attendees list
                leg.getUserList().put((Integer)addAttendees.getTag(), addAttendees.getText().toString());
                attendees.setText(addUsers(leg.getUserList().values()));
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_attendee, Toast.LENGTH_SHORT).show();
            }
        };
    }


}
