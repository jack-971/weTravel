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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

/**
 * Holds controller logic for a profiles map page - specific to a user containing their wishlist
 * and all locations they've visited within the app
 */
public class ProfileMapFragment extends MapFragment {

    private Profile profile;
    private HashMap<Marker, TripLocation> visited;
    private NetworkResultCallback visitedPlacesCallback;

    /**
     * Constructor sets the maps profile
     * @param profile
     */
    public ProfileMapFragment(Profile profile) {
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jsonFetcher.getData(routes.wishlistRoute(profile.getUserId())+"?type=profile");

        wishlistFilterView.setVisibility(View.VISIBLE);
        visitedFilterView.setVisibility(View.VISIBLE);

        // if it is not the logged in users profile then cannot add or remove items from the wishlist
        if (profile.getProfileType() != ProfileTypes.PROFILE_ADMIN) {
            autocompleteSupportFragment.getView().setVisibility(View.GONE);
        } else {
            wishlistRemove.setVisibility(View.VISIBLE);
        }

        /**
         * Adds filter for visited locations
         */
        visitedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMarkers(visited);
                } else {
                    hideMarkers(visited);
                }
            }
        });


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        super.onMarkerClick(marker);
        if (marker.getSnippet() != null) { // do not want wishlist markers to follow this
            addWishlist(currentPlace.getId());
            jsonFetcher = new JsonFetcher(addWishlistCallback, getContext());
            jsonFetcher.addParam("location", currentPlace.getId());
            jsonFetcher.postDataVolley(routes.wishlistRoute(profile.getUserId()));
        }
        return false;
    }

    @Override
    protected void loadMapData() {
        loadMapDataCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                processWishlist(response);
                processVisited(response);
            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    /**
     * Processes a json data set for all users visited locations and requests info for each from Google Places
     * @param response
     */
    protected void processVisited(JSONObject response) {
        try {
            JSONArray locationArray = response.getJSONArray("locations");
            addVisitedFromGooglePlaces();
            visited = new HashMap<>();
            for (int loop = 0; loop<locationArray.length(); loop++) {
                JSONObject locationObject = locationArray.getJSONObject(loop);
                String locationKeyString = locationObject.getString("LocationID");
                jsonFetcher = new JsonFetcher(visitedPlacesCallback, getContext());
                jsonFetcher.getData(routes.getPlacesAPI(locationKeyString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method callback for visited locations from google places. Adds to visited hashmap and sets marker
     */
    protected void addVisitedFromGooglePlaces() {
        visitedPlacesCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    TripLocation tripLocation = parsePlacesJSON(response);
                    visited.put(addMarker(tripLocation, R.drawable.location_visited), tripLocation);
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
