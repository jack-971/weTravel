package uk.ac.qub.jmccambridge06.wetravel.ui;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public class LegItineraryFragment extends Fragment {

    //@BindView(R.id.trip_legs_list) RecyclerView legListRecycler;
    @BindView(R.id.leg_list_container) FrameLayout legListContainer;
    @BindView(R.id.add_leg_container) FrameLayout addLegContainer;
    @BindView(R.id.add_leg_button) Button addActivityButton;
    @BindView(R.id.edit_leg_container) FrameLayout editLegContainer;

    ActivityListFragment activityListFragment;
    ActivityDetailsFragment activityDetailsFragment;
    LegDetailsFragment legDetailsFragment;
    Leg leg;

    private Trip trip;

    /**
     * Constructor with leg argument
     * @param leg
     */
    public LegItineraryFragment(Leg leg) {
        super();
        this.leg = leg;
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
        //legDetailsFragment = new LegDetailsFragment();

        // Load the edit Leg details section
        legDetailsFragment = new LegDetailsFragment(leg);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.edit_leg_container,
                legDetailsFragment, "edit_leg_fragment").commit();
        editLegContainer.setVisibility(View.VISIBLE);

        // Load the add activites section
        activityDetailsFragment = new ActivityDetailsFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_leg_container,
                activityDetailsFragment, "activities_details_fragment").commit();

        // Add the activities
        //loadItinerary();

        // Add the add activity button
        addActivityButton.setText(R.string.add_activity);
        addActivityButton.setVisibility(View.VISIBLE); // only visible once a trip exists.
        addActivityButton.setOnClickListener(new View.OnClickListener() {
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
        activityListFragment = new ActivityListFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.leg_list_container,
                activityListFragment, "leg_list_fragment").commit();
        Log.d("tag", "activity list fragment created");
    }

}
