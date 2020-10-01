package uk.ac.qub.jmccambridge06.wetravel.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary.TripFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

/**
 * Holds controller logic for a trips map page - specific to a trip and a user.
 * Contains wishlist and all locations visting/visited on the trip
 */
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
        // if there is no trip then should not be able to add locations from the map
        if (trip != null) {
            if (!trip.getStatus().equalsIgnoreCase("complete")) {
                jsonFetcher.getData(routes.wishlistRoute(trip.getProfile().getUserId())+"?type=planned");
                wishlistFilterView.setVisibility(View.VISIBLE);
                // if it is logged in users trip then can edit wishlsit
                if (trip.getProfile().getProfileType() == ProfileTypes.PROFILE_ADMIN) wishlistRemove.setVisibility(View.VISIBLE);
            } else {
                // if trip is complete then cannot add locations to the map
                autocompleteSupportFragment.getView().setVisibility(View.GONE);
            }
        }

        processLocations(trip.getLocations());

        /**
         * Set filter for locations
         */
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

    /**
     * Processes an array list containing location ids and sends requests to Google Places API for their info
     * @param locationIds
     */
    protected void processLocations(ArrayList<String> locationIds) {
            addLocationsFromGooglePlaces();
            locations = new HashMap<>();
            for (int loop = 0; loop<locationIds.size(); loop++) {
                String locationKeyString = locationIds.get(loop);
                jsonFetcher = new JsonFetcher(locationsPlacesCallback, getContext());
                jsonFetcher.getData(routes.getPlacesAPI(locationKeyString));
            }
    }

    /**
     * Method callback for google places API for locations - adds to locations hashmap and adds marker
     */
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
