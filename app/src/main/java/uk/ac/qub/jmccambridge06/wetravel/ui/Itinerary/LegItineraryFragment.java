package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.ActivityListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Holds controller logic for a leg itinerary page - contains option to add new activity and list of
 * existing activities
 */
public class LegItineraryFragment extends ItineraryFragment {

    @BindView(R.id.leg_list_container) FrameLayout legListContainer;
    @BindView(R.id.add_leg_container) FrameLayout addLegContainer;
    @BindView(R.id.add_leg_button) Button addActivityButton;

    ActivityListFragment activityListFragment;
    ActivityDetailsFragment activityDetailsFragment;
    LegDetailsFragment legDetailsFragment;
    Leg leg;

    /**
     * Constructor with leg argument
     * @param leg
     */
    public LegItineraryFragment(Leg leg) {
        super(leg);
        this.leg = leg;
}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void loadPage() {
        super.loadPage();

        // Load the edit Leg details section
        legDetailsFragment = new LegDetailsFragment(leg);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.edit_leg_container,
                legDetailsFragment, "edit_leg_fragment").commit();
        editEntryContainer.setVisibility(View.VISIBLE);

        // Load the add activites section if trip is not compelete
        if (!leg.getStatus().equalsIgnoreCase("complete")) {
            activityDetailsFragment = new ActivityDetailsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_leg_container,
                    activityDetailsFragment, "activities_details_fragment").commit();
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

        // Add the activities
        loadItinerary();
    }

    @Override
    public void loadItinerary() {
        activityListFragment = new ActivityListFragment(leg.getActivities());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.leg_list_container,
                activityListFragment, "activity_list_fragment").commit();
        Log.d("tag", "activity list fragment created");
    }

}
