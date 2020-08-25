package uk.ac.qub.jmccambridge06.wetravel.ui;

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
import java.util.Date;
import java.util.UUID;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.StatusTypesDb;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;

public class TripDetailsFragment extends TripEntryFragment {

    NetworkResultCallback activeTripCallback;

    /**
     * Constructor with trip argument - used for showing existing trips
     * @param trip
     */
    public TripDetailsFragment(Trip trip) {
        super();
        this.trip = trip;
    }

    /**
     * Default constructor - used for new trips
     */
    public TripDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logtag = "trip details";

        // Set visibility for all components.
        tripTimeView.setVisibility(View.GONE);
        tripNotesView.setVisibility(View.GONE);
        addAllCheckBox.setVisibility(View.GONE);
        tripAttachmentsView.setVisibility(View.GONE);
        editButton.setVisibility(View.INVISIBLE);
        tripRatingView.setVisibility(View.GONE);

        if (trip == null) {
            tripReviewView.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else if (trip.getStatus().equalsIgnoreCase("planned")) {
            makeActiveButton.setVisibility(View.VISIBLE);
            tripReviewView.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else if (trip.getStatus().equalsIgnoreCase("active")) {
            saveButton.setVisibility(View.VISIBLE);
            leaveButton.setVisibility(View.GONE);
            makeActiveButton.setText(R.string.complete);
            makeActiveButton.setVisibility(View.VISIBLE);
            tripAddAttendeesView.setVisibility(View.GONE);
            leaveButton.setVisibility(View.GONE);
        } else {
            displayComplete();

        }

        // execute following code if trip is in editing phase. else it is a new trip being created so placeholders should be used. If new trip
        // dates in calendar picker automated to null otherwise come from the database values.
        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;
        if (trip != null) {
            initialiseStartDate=trip.getStartDate();
            initialiseFinishDate=trip.getEndDate();
            loadDetails();
        } else {
            tripAttendeesView.setVisibility(View.GONE);
            tripAddAttendeesView.setVisibility(View.GONE);
            leaveButton.setVisibility(View.GONE);
            tripPictureView.setVisibility(View.GONE);
        }

        startDate.setOnClickListener(new EditTextDateClicker(getContext(), startDate, initialiseStartDate));
        finishDate.setOnClickListener(new EditTextDateClicker(getContext(), finishDate, initialiseFinishDate));

        tripPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(ImageUtility.setIntent(), "Select Picture"), ImageUtility.IMG_REQUEST_ID);
            }
        });
    }



    @Override
    public void loadDetails() {
       tripName.setText(trip.getEntryName());
       completeTripName.setText(trip.getEntryName());
       if (trip.getStartDate() != null) {
           startDate.setText(DateTime.formatDate(trip.getStartDate()));
           completeDateStart.setText(DateTime.formatDate(trip.getStartDate()));
       }
       if (trip.getEndDate() != null) {
           finishDate.setText(DateTime.formatDate(trip.getEndDate()));
           completeDateFinish.setText(DateTime.formatDate(trip.getEndDate()));
       }
       if (trip.getDescription() != null) {
           description.setText(trip.getDescription());
           completeDescription.setText(trip.getDescription());
       }
       if (trip.getTripPicture() != null) {
           tripPicture.setText(R.string.trip_picture_change);
           setMainImageString(trip.getTripPicture());
       }
       if (trip.getLocation() !=null) {
           location.setText(trip.getLocation().getName());
           location.setTag(trip.getLocation().getId());
           completeTripLocation.setText(trip.getLocation().getName());
       }
       if (trip.getReview() != null) {
           review.setText(trip.getReview());
           completeReview.setText(trip.getReview());
       }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(trip.getUserList().values()));
        completeAttendees.setText(addUsers(trip.getUserList().values()));
        addAttendeesSetUp(((MainMenuActivity)getActivity()).getUserAccount().getFriendsList());

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
     * Activity result for when profile picture is clicked. Chosen image is saved as bitmap and saved to firebase.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == ImageUtility.IMG_REQUEST_ID && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                ImageUtility.storeImageAsBitmap(requestCode, resultCode, data, getContext(), this);
                tripPicture.setText(R.string.trip_picture_change);
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
            saveTripRequest();
        }
    }

    @Override
    protected void saveTripRequest() {
        super.saveTripRequest();
        jsonFetcher.addParam("type", "trip");
        jsonFetcher.addParam("status", trip.getStatus());
        // if there is no exisitng trip then must use post request to create new trip.
        if (trip == null) {
            jsonFetcher.addParam("Picture", getMainImageString());
            jsonFetcher.postDataVolley(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        } else {
            jsonFetcher.addParam("Picture", trip.getTripPicture());
            jsonFetcher.addParam("tripId", String.valueOf(trip.getEntryId()));
            jsonFetcher.patchData(routes.saveTripDetails(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
        }


    }

    @Override
    protected void sendAttendeeData() {
        // Check to make sure not already in trip - checking the id captured in suggestions box
        for (int userId : trip.getUserList().keySet()) {
            if ((Integer)addAttendees.getTag() == userId) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_friend_already_added, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // If not on trip then send data to database
        saveAttendee();
        jsonFetcher = new JsonFetcher(addAttendeeCallback, getContext());
        jsonFetcher.addParam("user", addAttendees.getTag().toString());
        jsonFetcher.addParam("type", "trip");
        jsonFetcher.postDataVolley(routes.addUserToTrip(trip.getEntryId()));
    }

    protected void saveAttendee() {
        addAttendeeCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // Update the phone display to reflect new attendee in attendees list
                trip.getUserList().put((Integer)addAttendees.getTag(), addAttendees.getText().toString());
                attendees.setText(addUsers(trip.getUserList().values()));
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_attendee, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void sendLeaveRequest() {
        leave();
        jsonFetcher = new JsonFetcher(leaveTripCallback, getContext());
        jsonFetcher.deleteData(routes.leaveTrip(((MainMenuActivity)getActivity()).getUserAccount().getUserId(), trip.getEntryId(), "trip"));
    }



    @Override
    protected void saveTripData() {
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
                        tripAttendeesView.setVisibility(View.VISIBLE);
                        tripAddAttendeesView.setVisibility(View.VISIBLE);
                        tripPictureView.setVisibility(View.VISIBLE);
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
                    trip.setEntryName(tripName.getText().toString());
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
        leaveTripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                ((MainMenuActivity)getActivity()).getSupportFragmentManager().popBackStack();
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
                saveTripRequest();
            }

            @Override
            public void notifyError(Exception error) {
                error.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected void makeActive() {
        activeTripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                String message;
                if (trip.getStatus().equalsIgnoreCase("planned")) {
                    message = getResources().getString(R.string.active_trip);
                    trip.setStatus("active");
                    makeActiveButton.setText(R.string.complete);
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
}
