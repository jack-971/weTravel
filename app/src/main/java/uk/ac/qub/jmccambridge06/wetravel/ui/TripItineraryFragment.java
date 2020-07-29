package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public class TripItineraryFragment extends Fragment {

    //@BindView(R.id.trip_legs_list) RecyclerView legListRecycler;
    @BindView(R.id.leg_list_container) FrameLayout legListContainer;
    @BindView(R.id.add_leg_container) FrameLayout addLegContainer;
    @BindView(R.id.add_leg_button) Button addLegButton;

    LegListFragment legListFragment;
    LegDetailsFragment legDetailsFragment;

    private Trip trip;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        legDetailsFragment = new LegDetailsFragment();
        addLegButton.setText(R.string.add_leg);
        if (trip != null) {
            loadItinerary();
            addLegButton.setVisibility(View.VISIBLE); // only visible once a trip exists.
        }
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_leg_container,
                legDetailsFragment, "leg_list_fragment").commit();


        addLegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if leg details is not visible show, otherwise hide it.
                if (addLegContainer.getVisibility() == View.GONE) {
                    addLegContainer.setVisibility(View.VISIBLE);
                } else {
                    addLegContainer.setVisibility(View.GONE);
                }
            }
        });


    }

    public void loadItinerary() {
        legListFragment = new LegListFragment(trip.getLegs());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.leg_list_container,
                legListFragment, "leg_list_fragment").commit();
        Log.d("tag", "leg fragment created");
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        Log.d("tag", "trip has now been set");
    }
}
