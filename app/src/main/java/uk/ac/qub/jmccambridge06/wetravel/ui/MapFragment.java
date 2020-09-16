package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;

public abstract class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @BindView(R.id.wishlist_filter) RelativeLayout wishlistFilterView;
    @BindView(R.id.visited_filter) RelativeLayout visitedFilterView;
    @BindView(R.id.location_filter) RelativeLayout locationFilterView;
    @BindView(R.id.wishlist_filter_checkbox) CheckBox wishlistCheckbox;
    @BindView(R.id.visited_filter_checkbox) CheckBox visitedCheckbox;
    @BindView(R.id.location_filter_checkbox) CheckBox locationCheckbox;
    @BindView(R.id.wishlist_remove) TextView wishlistRemove;

    protected GoogleMap Map;
    MapView mapView;
    AutocompleteSupportFragment autocompleteSupportFragment;
    protected Marker currentMarker;
    protected Place currentPlace;
    protected Marker selectedWishlitMarker;

    protected HashMap<Marker, TripLocation> wishlist;

    protected JsonFetcher jsonFetcher;
    protected NetworkResultCallback loadMapDataCallback;
    protected NetworkResultCallback loadPlaceCallback;
    protected NetworkResultCallback addWishlistCallback;
    protected NetworkResultCallback removeWishlistCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).removeNavBar();

        View view = inflater.inflate(R.layout.fragment_trip_map, container, false);
        Places.initialize(getContext(), Locations.key);
        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                place.getLatLng();
                Log.d("Maps", "Place selected: " + place.getName());
                if (currentMarker != null) currentMarker.remove();
                currentMarker = Map.addMarker(new MarkerOptions().position(place.getLatLng()).icon(bitmapDescriptorFromVector(getActivity(), R.drawable.location_add)).title("Add "+place.getName()).snippet("Click to Add!"));
                currentPlace = place;
                float zoomLevel = 12.0f;
                Map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoomLevel));
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });


        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadMapData();
        jsonFetcher = new JsonFetcher(loadMapDataCallback, getContext());

        wishlistCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMarkers(wishlist);
                    wishlistRemove.setVisibility(View.VISIBLE);
                } else {
                    hideMarkers(wishlist);
                    wishlistRemove.setVisibility(View.GONE);
                }

            }
        });

        wishlistRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedWishlitMarker != null) {
                    deleteData(selectedWishlitMarker);
                    jsonFetcher = new JsonFetcher(removeWishlistCallback, getContext());
                    jsonFetcher.deleteData(routes.wishlistRoute(((MainMenuActivity)getActivity()).getUserAccount().getUserId())
                        + "?location="+wishlist.get(selectedWishlitMarker).getId());
                }
            }
        });
    }

    protected void hideMarkers(HashMap<Marker, TripLocation> list) {
        for (Marker marker : list.keySet()) {
            marker.setVisible(false);
        }
    }

    protected void showMarkers(HashMap<Marker, TripLocation> list) {
        for (Marker marker : list.keySet()) {
            marker.setVisible(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;
        googleMap.setOnMarkerClickListener(this::onMarkerClick);
    }

    protected Marker addMarker(TripLocation tripLocation, int vectorResId) {
        Marker marker = Map.addMarker(new MarkerOptions().position(new LatLng(tripLocation.getLatitude(), tripLocation.getLongidtude())).icon(bitmapDescriptorFromVector(getActivity(), vectorResId)).title(tripLocation.getName()));
        return marker;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (wishlist !=null) {
            if (wishlist.containsKey(marker)) {
                selectedWishlitMarker = marker;
            }
        }
        return false;
    }

    protected void deleteData(Marker marker) {
        removeWishlistCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // remove from wishlist
                marker.remove();
                wishlist.remove(marker);
                Toast.makeText(getActivity().getApplicationContext(), R.string.wishlist_remove, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected void processWishlist(JSONObject response) {
        try {
            JSONArray locationArray = response.getJSONArray("wishlist");
            addWishlistFromGooglePlaces();
            wishlist = new HashMap<>();
            for (int loop = 0; loop<locationArray.length(); loop++) {
                JSONObject locationObject = locationArray.getJSONObject(loop);
                String locationKeyString = locationObject.getString("LocationID");
                jsonFetcher = new JsonFetcher(loadPlaceCallback, getContext());
                jsonFetcher.getData(routes.getPlacesAPI(locationKeyString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected abstract void loadMapData();

    protected void addWishlistFromGooglePlaces() {
        loadPlaceCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    TripLocation tripLocation = parsePlacesJSON(response);
                    wishlist.put(addMarker(tripLocation, R.drawable.location_wishlist), tripLocation);
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

    protected void addWishlist(String id) {
        addWishlistCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                currentMarker.remove();
                Toast.makeText(getActivity().getApplicationContext(), R.string.location_added, Toast.LENGTH_SHORT).show();
                jsonFetcher = new JsonFetcher(loadPlaceCallback, getContext());
                jsonFetcher.getData(routes.getPlacesAPI(id));
            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected TripLocation parsePlacesJSON(JSONObject response) throws JSONException {
        JSONObject jsonObject = response.getJSONArray("results").getJSONObject(0);
        String name = jsonObject.getJSONArray("address_components").getJSONObject(0).getString("long_name");
        String address = jsonObject.getString("formatted_address");
        String place = jsonObject.getString("place_id");
        JSONObject geometryObject = jsonObject.getJSONObject("geometry");
        Double lat = Double.valueOf(geometryObject.getJSONObject("location").getString("lat"));
        Double lng = Double.valueOf(geometryObject.getJSONObject("location").getString("lng"));
        return new TripLocation(name, place, address, lat, lng);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
