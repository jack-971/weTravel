package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

public class TripMapFragment extends MapFragment {

    private Trip trip;
    private NetworkResultCallback locationsPlacesCallback;
    private HashMap<Marker, TripLocation> locations;

    public TripMapFragment(Trip trip) {
        this.trip = trip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationFilterView.setVisibility(View.VISIBLE);

        if (trip != null) {
            if (!trip.getStatus().equalsIgnoreCase("complete")) {
                jsonFetcher.getData(routes.wishlistRoute(trip.getProfile().getUserId())+"?type=planned");
                wishlistFilterView.setVisibility(View.VISIBLE);
                if (trip.getProfile().getProfileType() == ProfileTypes.PROFILE_ADMIN) wishlistRemove.setVisibility(View.VISIBLE);
            } else {
                autocompleteSupportFragment.getView().setVisibility(View.GONE);
            }
        }

        processLocations(trip.getLocations());

        locationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMarkers(locations);
                } else {
                    hideMarkers(locations);
                }
            }
        });

    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        super.onMarkerClick(marker);
        if (wishlist != null) {
            if (marker.getSnippet() != null || wishlist.containsKey(marker)) { // confirm it is a new add marker, not an existing one
                // get reference to the trip fragment from the fragment manager.
                ArrayList<TripLocation> locations = new ArrayList<>();
                TripLocation tripLocation;
                if (marker.getSnippet() != null) tripLocation = new TripLocation(currentPlace.getId(), currentPlace.getName(), currentPlace.getAddress());
                else tripLocation = wishlist.get(marker);
                locations.add(tripLocation);
                TripFragment tripFragment = (TripFragment) ((MainMenuActivity)getActivity()).getSupportFragmentManager().findFragmentByTag("user_trip_fragment");
                tripFragment.showQuickAddDialog(locations);
            }
        }
        return false;
    }

    protected void loadMapData() {
        loadMapDataCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                processWishlist(response);

            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    protected void processLocations(ArrayList<String> locationIds) {
            addLocationsFromGooglePlaces();
            locations = new HashMap<>();
            for (int loop = 0; loop<locationIds.size(); loop++) {
                String locationKeyString = locationIds.get(loop);
                jsonFetcher = new JsonFetcher(locationsPlacesCallback, getContext());
                jsonFetcher.getData(routes.getPlacesAPI(locationKeyString));
            }
    }

    protected void addLocationsFromGooglePlaces() {
        locationsPlacesCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    TripLocation tripLocation = parsePlacesJSON(response);
                    locations.put(addMarker(tripLocation, R.drawable.location_planned), tripLocation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }


}
