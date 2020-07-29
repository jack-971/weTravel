package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.qub.jmccambridge06.wetravel.Activity;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;

public class ActivityDetailsFragment extends TripEntryFragment {

    Activity activity;

    /**
     * Constructor with leg argument - used for showing existing legs
     * @param activity
     */
    public ActivityDetailsFragment(Activity activity) {
        super();
        this.activity = activity;
    }

    /**
     * Default constructor used for creating new legs
     */
    public ActivityDetailsFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Leg Details";
        tripPictureView.setVisibility(View.GONE);
        tripTimeView.setVisibility(View.GONE);
        tripRatingView.setVisibility(View.GONE);
        tripReviewView.setVisibility(View.GONE);

        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;
        if (activity != null) {
            initialiseStartDate=activity.getStartDate();
            initialiseFinishDate=activity.getEndDate();
            loadDetails();
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
        tripName.setText(activity.getEntryName());
        if (activity.getStartDate() != null) {
            startDate.setText(DateTime.formatDate(activity.getStartDate()));
        }
        if (activity.getEndDate() != null) {
            finishDate.setText(DateTime.formatDate(activity.getEndDate()));
        }
        if (activity.getDescription() != null) {
            description.setText(activity.getDescription());
        }
        if (activity.getLocation() !=null) {
            location.setText(activity.getLocation().getName());
            location.setTag(activity.getLocation().getId());
        }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(activity.getUserList().values()));
        ArrayList<Profile> profiles = new ArrayList<>();
        // for each person on the trip, if they are not already in the user leg list then they can be added.
        /*for (int value : trip.getUserList().keySet()) {
            if (!leg.getUserList().containsKey(value)) {
                Profile profile = new Profile(value, trip.getUserList().get(value));
                profiles.add(profile);
            }
        }
        addAttendeesSetUp(profiles);*/
    }

    @Override
    protected void sendData() {

    }

    @Override
    protected void saveTripData() {

    }

    @Override
    protected void sendLeaveRequest() {

    }

    @Override
    protected void leave() {

    }
}
