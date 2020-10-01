package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.LegListFragment;

/**
 * Holds controller logic for a trip itinerary fragment -contains option to create new legs and list of attached legs
 */
public class TripItineraryFragment extends ItineraryFragment {

    private Trip trip;
    LegListFragment legListFragment;
    LegDetailsFragment legDetailsFragment;

    /**
     * Constructor with trip argument - used for showing existing trips
     * @param trip
     */
    public TripItineraryFragment(Trip trip) {
        super();
        this.trip = trip;
    }

    public TripItineraryFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void loadPage() {
        super.loadPage();
        // If no trip attached (new trip is being created but not yet saved) then can't view legs.
        if (trip != null) {
            loadItinerary();
            if (!trip.getStatus().equalsIgnoreCase("complete")) {
                legDetailsFragment = new LegDetailsFragment();
                addEntryButton.setText(R.string.add_leg);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_leg_container,
                        legDetailsFragment, "leg_details_fragment").commit();
                addEntryButton.setVisibility(View.VISIBLE); // only visible once a trip exists.
            }

            noEntries.setVisibility(View.GONE);
        } else {
            noEntries.setVisibility(View.VISIBLE);
        }



    }

    /**
     * Loads the list of legs into the list container
     */
    public void loadItinerary() {
        legListFragment = new LegListFragment(trip.getLegs());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.leg_list_container,
                legListFragment, "leg_list_fragment").commit();
    }


    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }


}
