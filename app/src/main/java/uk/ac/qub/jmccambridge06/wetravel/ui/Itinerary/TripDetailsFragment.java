package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.StatusTypesDb;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;

/**
 * Controller logic for a trips details page
 */
public class TripDetailsFragment extends TripEntryFragment {

    NetworkResultCallback activeTripCallback;
    Trip trip;
    final int ICON_REQUEST_CODE = 03;

    /**
     * Constructor with args - used for when fragment is for an existing trip
     * @param trip
     */
    public TripDetailsFragment(Trip trip) {
        super(trip);
        this.trip = trip;
        this.type = "trip";
    }

    /**
     * constructor to set type. Used when for a new trip and not an existing trip
     */
    public TripDetailsFragment() {
        this.type = "trip";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "trip details";

        if (item == null) {
            entryPictureView.setVisibility(View.GONE);
        } else if (item.getStatus().equalsIgnoreCase("planned")) {
            makeActiveButton.setVisibility(View.VISIBLE);
            entryPictureView.setVisibility(View.VISIBLE);
        } else if (item.getStatus().equalsIgnoreCase("active")) {
            makeActiveButton.setText(R.string.complete);
            makeActiveButton.setVisibility(View.VISIBLE);
            entryPictureView.setVisibility(View.VISIBLE);
        }

        entryPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(ImageUtility.setIntent(), "Select Picture"), ICON_REQUEST_CODE);
            }
        });
    }



    @Override
    public void loadDetails() {
        super.loadDetails();
        if (trip.getTripPicture() != null) {
           entryPicture.setText(R.string.trip_picture_change);
           setMainImageString(trip.getTripPicture());
        }
        // attendees can be added from the users friend list
        addAttendeesSetUp(((MainMenuActivity)getActivity()).getUserAccount().getFriendsList());
        // change trip status in database to active or complete depending on current status
        makeActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeActive();
                JsonFetcher jsonFetcher = new JsonFetcher(activeTripCallback, getContext());
                jsonFetcher.addParam("trip", String.valueOf(trip.getEntryId()));
                String status;
                if (trip.getStatus().equalsIgnoreCase("planned")) {
                    status = String.valueOf(StatusTypesDb.getActive());
                } else {
                    status = String.valueOf(StatusTypesDb.getComplete());
                }
                jsonFetcher.addParam("status", status);
                jsonFetcher.patchData(routes.patchTripStatus(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
            }
        });
    }



    /**
     * Activity result for when change icon is clicked. Chosen image is saved as bitmap and saved to firebase.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == ICON_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                ImageUtility.storeImageAsBitmap(requestCode, resultCode, data, getContext(), this);
                entryPicture.setText(R.string.trip_picture_change);
                setPictureChanged(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendData() {
        if (isPictureChanged() == true) {
            updateImageCallback();
            String path = FirebaseLink.tripHeaderPath+ FirebaseLink.tripHeaderPrefix + UUID.randomUUID().toString();
            FirebaseLink.saveInFirebase(getImageUri(), getContext(), tripImageCallback, path);
        } else {
            saveEntryRequest();
        }
    }

    @Override
    protected void saveEntryRequest() {
        super.saveEntryRequest();
        jsonFetcher.addParam("type", "trip");

        // if there is no exisitng trip then must use post request to create new trip.
        if (trip == null) {
            jsonFetcher.addParam("Picture", getMainImageString());
            jsonFetcher.addParam("status", "planned");
            jsonFetcher.postDataVolley(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        } else {
            jsonFetcher.addParam("status", trip.getStatus());
            jsonFetcher.addParam("Picture", trip.getTripPicture());
            jsonFetcher.addParam("tripId", String.valueOf(trip.getEntryId()));
            jsonFetcher.patchData(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        }


    }

    @Override
    protected void saveEntryData() {
        saveEntryCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // if there is no existing trip, now it has been saved to database can create a trip instance.
                if (trip == null) {
                    try {
                        // create the trip.
                        JSONArray tripArray = response.getJSONArray("data");
                        JSONObject tripData = tripArray.getJSONObject(0);
                        tripData.put("TripStatus", "planned"); // as new trip the status will always be planned.
                        trip = new Trip(tripData, ((MainMenuActivity)getActivity()).getUserAccount().getProfile());
                        trip.setUserList(((MainMenuActivity)getActivity()).getUserAccount());
                        JSONArray legArray = response.getJSONArray("leg");
                        trip.setLegs(legArray);
                        // update the UI.
                        leaveButton.setVisibility(View.VISIBLE);
                        entryAttendeesView.setVisibility(View.VISIBLE);
                        entryAddAttendeesView.setVisibility(View.VISIBLE);
                        entryPictureView.setVisibility(View.VISIBLE);
                        setItem(trip);
                        loadDetails();
                        // Load the trip into the other fragments.
                        TripFragment tripFragment = (TripFragment) getParentFragment();
                        tripFragment.setTrip(trip);
                        //tripFragment.tripItineraryFragment = new TripItineraryFragment(trip);
                        tripFragment.tripItineraryFragment.setTrip(trip);
                        tripFragment.tripItineraryFragment.loadPage();
                        tripFragment.tripMapFragment.setTrip(trip);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //If existing trip overwrites its instance vars.
                    trip.setEntryName(entryName.getText().toString());
                    trip.setStartDate(DateTime.formatDate(startDate.getText().toString()));
                    trip.setEndDate(DateTime.formatDate(finishDate.getText().toString()));
                    trip.setDescription(description.getText().toString());
                    trip.setTripPicture(getMainImageString());
                    if (trip.getLocation().getId() == null) {
                        trip.setLocation(null, null);
                    } else {
                        trip.setLocation(location.getTag().toString(), location.getText().toString());
                    }
                }
                Toast.makeText(getActivity().getApplicationContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void leave() {
        leaveEntryCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                getActivity().getSupportFragmentManager().popBackStack();
                Toast.makeText(getActivity().getApplicationContext(), "You have left "+trip.getEntryName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Callback to complete once trip image has successfully uploaded to firebase. Updates the image download string and then sends the
     * volley request.
     */
    void updateImageCallback() {
        tripImageCallback = new FirebaseCallback() {
            @Override
            public void notifySuccess(String url) {
                trip.setTripPicture(url);
                Log.e(logtag, url);
                setPictureChanged(false);
                saveEntryRequest();
            }

            @Override
            public void notifyError(Exception error) {
                error.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Callback method to change trips status - changes the interface accordingly
     */
    protected void makeActive() {
        activeTripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                String message;
                if (trip.getStatus().equalsIgnoreCase("planned")) {
                    message = getResources().getString(R.string.active_trip);
                    trip.setStatus("active");
                    makeActiveButton.setText(R.string.complete);
                    addGalleryImageButton.setVisibility(View.VISIBLE);
                    galleryButton.setVisibility(View.VISIBLE);
                    postButton.setVisibility(View.VISIBLE);
                } else {
                    message = getResources().getString(R.string.trip_completed);
                    trip.setStatus("complete");
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void sendAttendeeData() {
        super.sendAttendeeData();
        jsonFetcher.postDataVolley(routes.addUserToTrip(item.getEntryId()));
    }
}
