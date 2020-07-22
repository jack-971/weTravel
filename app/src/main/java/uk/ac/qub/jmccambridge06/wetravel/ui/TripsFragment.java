package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public class TripsFragment extends Fragment {

    @BindView(R.id.planned_trips) View plannedTrips;
    @BindView(R.id.current_trips) View currentTrips;
    @BindView(R.id.complete_trips) View completeTrips;
    @BindView(R.id.add_trip) View addTrip;

    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.showNavBar();
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        plannedTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile.getUserId() == ((MainMenuActivity)getActivity()).getUserAccount().getProfile().getUserId()) {
                    ((MainMenuActivity)getActivity()).plannedTrips.setProfile(profile);
                    ((MainMenuActivity) getActivity()).setFragment(((MainMenuActivity)getActivity()).plannedTrips, "user_planned_trips_fragment", true);
                }
            }
        });

        currentTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity) getActivity()).setFragment(new TripFragment(), "user_current_trip_fragment", true);
            }
        });

        completeTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile.getUserId() == ((MainMenuActivity)getActivity()).getUserAccount().getProfile().getUserId()) {
                    ((MainMenuActivity)getActivity()).completedTrips.setProfile(profile);
                    ((MainMenuActivity) getActivity()).setFragment(((MainMenuActivity)getActivity()).completedTrips, "user_completed_trips_fragment", true);
                }
            }
        });

        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity) getActivity()).setFragment(new TripFragment(), "user_add_trip_fragment", true);
            }
        });

    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
