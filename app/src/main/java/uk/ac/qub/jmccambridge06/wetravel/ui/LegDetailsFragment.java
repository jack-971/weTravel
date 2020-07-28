package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;

public class LegDetailsFragment extends TripEntryFragment {

    Leg leg;

    /**
     * Constructor with trip argument - used for showing existing trips
     * @param leg
     */
    public LegDetailsFragment(Leg leg) {
        super();
        this.leg = leg;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "Leg Details";
        tripPictureView.setVisibility(View.GONE);
        tripTimeView.setVisibility(View.GONE);
        tripNotesView.setVisibility(View.GONE);
        tripRatingView.setVisibility(View.GONE);
        tripReviewView.setVisibility(View.GONE);
        attachments.setVisibility(View.GONE);

        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;
        if (leg != null) {
            initialiseStartDate=leg.getStartDate();
            initialiseFinishDate=leg.getEndDate();
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
        tripName.setText(leg.getEntryName());
        if (leg.getStartDate() != null) {
            startDate.setText(DateTime.formatDate(leg.getStartDate()));
        }
        if (leg.getEndDate() != null) {
            finishDate.setText(DateTime.formatDate(leg.getEndDate()));
        }
        if (leg.getDescription() != null) {
            description.setText(leg.getDescription());
        }
        if (leg.getLocation() !=null) {
            location.setText(leg.getLocation().getName());
            location.setTag(leg.getLocation().getId());
        }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(leg.getUserList().values()));
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
