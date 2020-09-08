package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;

public class TripsFragment extends Fragment {

    @BindView(R.id.planned_trips) View plannedTrips;
    @BindView(R.id.current_trips) View currentTrips;
    @BindView(R.id.complete_trips) View completeTrips;
    @BindView(R.id.add_trip) View addTrip;

    private Profile profile;
    NetworkResultCallback networkResultCallback;

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
                getActiveTrip();
                JsonFetcher jsonFetcher = new JsonFetcher(networkResultCallback, getContext());
                jsonFetcher.getData(routes.getTrips(((MainMenuActivity)getActivity()).getUserAccount().getProfile().getUserId(), "active"));
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
                ((MainMenuActivity) getActivity()).setFragment(new TripFragment(), "user_trip_fragment", true);
            }
        });

    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    private void getActiveTrip() {
        networkResultCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("trip");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_active, Toast.LENGTH_SHORT).show();
                    }
                    JSONObject item = jsonArray.getJSONObject(0);
                    Trip currentTrip = new Trip(item, profile);
                    TripFragment tripFrag = new TripFragment();
                    tripFrag.setTrip(currentTrip);
                    MainMenuActivity.fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                            tripFrag, "user_trip_fragment").addToBackStack(null).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.no_active, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
