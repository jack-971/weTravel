package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

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
                addLegButton.setText(R.string.add_leg);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_leg_container,
                        legDetailsFragment, "leg_details_fragment").commit();
                addLegButton.setVisibility(View.VISIBLE); // only visible once a trip exists.
            }

            noLegs.setVisibility(View.GONE);
        } else {
            noLegs.setVisibility(View.VISIBLE);
        }



    }

    public void loadItinerary() {
        legListFragment = new LegListFragment(trip.getLegs());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.leg_list_container,
                legListFragment, "leg_list_fragment").commit();
        Log.d("tag", "leg fragment created");
    }


    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
