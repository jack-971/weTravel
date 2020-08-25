package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

public abstract class TripEntryFragment extends DisplayFragment {

    // All views
    @BindView(R.id.trip_name_view) View tripNameView;
    @BindView(R.id.edit_trip_name) EditText tripName;
    @BindView(R.id.trip_picture_view) View tripPictureView;
    @BindView(R.id.edit_trip_picture) TextView tripPicture;
    @BindView(R.id.trip_date_view) View tripDateView;
    @BindView(R.id.edit_trip_date_start) EditText startDate;
    @BindView(R.id.edit_trip_date_finish) EditText finishDate;
    @BindView(R.id.trip_time_view) View tripTimeView;
    @BindView(R.id.edit_trip_time_start) EditText startTime;
    @BindView(R.id.edit_trip_time_finish) EditText finishTime;
    @BindView(R.id.trip_description_view) View tripDescriptionView;
    @BindView(R.id.edit_trip_description) EditText description;
    @BindView(R.id.trip_location_view) View tripLocationView;
    @BindView(R.id.edit_trip_location) TextView location;

    @BindView(R.id.trip_notes_view) View tripNotesView;
    @BindView(R.id.trip_edit_notes) EditText notes;
    @BindView(R.id.trip_attendees_view) View tripAttendeesView;
    @BindView(R.id.trip_attendees_list) TextView attendees;
    @BindView(R.id.trip_add_attendees_view) View tripAddAttendeesView;
    @BindView(R.id.trip_add_attendee) AutoCompleteTextView addAttendees;
    @BindView(R.id.trip_review_rating_view) View tripRatingView;
    @BindView(R.id.edit_review_rating) EditText rating;
    @BindView(R.id.trip_review_text_view) View tripReviewView;
    @BindView(R.id.edit_review_text) EditText review;
    @BindView(R.id.trip_attachments_view) View tripAttachmentsView;
    @BindView(R.id.edit_attachment_text) EditText attachments;
    @BindView(R.id.trip_active_button) Button makeActiveButton;

    @BindView(R.id.complete_trip_name) TextView completeTripName;
    @BindView(R.id.complete_trip_date_start) TextView completeDateStart;
    @BindView(R.id.complete_trip_date_finish) TextView completeDateFinish;
    @BindView(R.id.complete_trip_description) TextView completeDescription;
    @BindView(R.id.complete_trip_location) TextView completeTripLocation;
    @BindView(R.id.complete_attendees_list) TextView completeAttendees;
    @BindView(R.id.complete_notes) TextView completeNotes;
    @BindView(R.id.complete_review_text) TextView completeReview;

    // All buttons
    @BindView(R.id.trip_edit_button) Button editButton;
    @BindView(R.id.trip_save_button) Button saveButton;
    @BindView(R.id.trip_leave_button) Button leaveButton;
    @BindView(R.id.trip_add_attendee_button) TextView addAttendeeButton;

    @BindView(R.id.checkbox_attendees) CheckBox addAllCheckBox;

    protected Trip trip;

    NetworkResultCallback addAttendeeCallback;
    NetworkResultCallback saveEntryCallback;
    NetworkResultCallback leaveTripCallback;
    FirebaseCallback tripImageCallback;
    JsonFetcher jsonFetcher;
    String logtag;
    ItineraryItem item;

    final int AUTOCOMPLETE_REQUEST_CODE = 01;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        // Set the locations up
        Places.initialize(getContext(), Locations.key);
        PlacesClient placesClient = Places.createClient(getContext());
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
        // Start the autocomplete intent.
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        saveButton.setOnClickListener(v -> {
            // Ensure that something is entered for trip name
            if (tripName.getText().toString().trim().length() == 0) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_name_field, Toast.LENGTH_SHORT).show();
            } else if (DateTime.checkDateBefore(startDate.getText().toString(), finishDate.getText().toString())) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_date_before, Toast.LENGTH_SHORT).show();
            } else {
                sendData();
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLeaveRequest();
            }
        });
    }

    /**
     * Changes all the textboxes to edit text boxes
     */
    protected void displayComplete() {
        tripName.setVisibility(View.GONE);
        startDate.setVisibility(View.INVISIBLE);
        finishDate.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        location.setVisibility(View.GONE);
        notes.setVisibility(View.GONE);
        attendees.setVisibility(View.GONE);
        review.setVisibility(View.GONE);
        completeTripName.setVisibility(View.VISIBLE);
        completeDateStart.setVisibility(View.VISIBLE);
        completeDateFinish.setVisibility(View.VISIBLE);
        completeDescription.setVisibility(View.VISIBLE);
        completeNotes.setVisibility(View.VISIBLE);
        completeAttendees.setVisibility(View.VISIBLE);
        tripAddAttendeesView.setVisibility(View.GONE);
        completeReview.setVisibility(View.VISIBLE);
        leaveButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        makeActiveButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
    }

    protected abstract void sendData();

    public abstract void loadDetails();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                location.setText(place.getAddress());
                location.setTag(place.getId());
                Log.i("entry", "Place: " +place.getAddressComponents().toString());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("entry", status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Method to send data to the database.
     */
    protected void saveTripRequest() {
        saveTripData();
        jsonFetcher = new JsonFetcher(saveEntryCallback, getContext());
        jsonFetcher.addParam("Name", tripName.getText().toString());
        jsonFetcher.addParam("DateStart", DateTime.dateToMilliseconds(startDate.getText().toString()));
        jsonFetcher.addParam("DateFinish", DateTime.dateToMilliseconds(finishDate.getText().toString()));
        jsonFetcher.addParam("Description", description.getText().toString());
        jsonFetcher.addParam("Review", review.getText().toString());
        if (location.getTag()==null) {
            jsonFetcher.addParam("LocationID", "");
            jsonFetcher.addParam("LocationDetail", "");
        } else {
            jsonFetcher.addParam("LocationID", location.getTag().toString());
            jsonFetcher.addParam("LocationDetail", location.getText().toString());
        }
    };

    protected abstract void saveTripData();

    /**
     * Prepares and send the delete request to database to delete user from a trip or activity.
     */
    protected abstract void sendLeaveRequest();

    protected abstract void sendAttendeeData();

    protected abstract void saveAttendee();

    /**
     * Prepare call backs for leaving a trip or activity.
     */
    protected abstract void leave();

    protected void addAttendeesSetUp(ArrayList<Profile> profiles) {
        FriendSuggestionAdapter adapter = new FriendSuggestionAdapter(getContext(), profiles);
        addAttendees.setAdapter(adapter);
        addAttendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check friend has been selected. Error msg if not, if so then send the data via the subclass method.
                if (addAttendees.getTag() == null) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_select_friend_dropdown, Toast.LENGTH_SHORT).show();
                } else {
                    sendAttendeeData();
                }
            }
        });
    }

    /**
     * Takes a list of names and puts them together for use in the attendees textbox.
     * @param userList
     * @return
     */
    protected StringBuilder addUsers(Collection<String> userList) {
        StringBuilder usersString = new StringBuilder("");
        int count = 0;
        for (String user : userList) {
            usersString.append(user);
            //Add comma if not last user.
            if (count != userList.size()-1) {
                usersString.append(", ");
            }
            count++;
        }
        return usersString;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    /**
     * Adapter to filter a given list of profiles depending on a users typed input into an edit text box.
     */
    public class FriendSuggestionAdapter extends ArrayAdapter<Profile> {

        private List<Profile> profileList;

        public FriendSuggestionAdapter(@NonNull Context context, @NonNull List<Profile> profiles) {
            super(context, 0, profiles);
            profileList = new ArrayList<Profile>(profiles);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return profileFilter;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.profile_autocomplete, parent, false
                );
            }
            TextView textNameSuggestion = convertView.findViewById(R.id.trip_add_attendee_name);
            Profile profile = getItem(position);
            if (profile !=null) {
                textNameSuggestion.setText(profile.getName());
            }

            textNameSuggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAttendees.setText(profile.getName());
                    addAttendees.setTag(profile.getUserId());
                }
            });

            return convertView;
        }

        /**
         * Checks the constraint entered into the textbox by the user and compares it to the given profile lists name fields. Returns a new
         * list with the results.
         */
        private Filter profileFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Profile> suggestions = new ArrayList<>();
                if (constraint == null || constraint.length() ==0 ) {
                    suggestions.addAll(profileList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Profile profile : profileList) {
                        if (profile.getProfileType() == ProfileTypes.PROFILE_FRIEND) {
                            if (profile.getName().toLowerCase().trim().startsWith(filterPattern)) {
                                suggestions.add(profile);
                            }
                        }
                    }
                }
                results.values = suggestions;
                results.count = suggestions.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((List)results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Profile)resultValue).getName();
            }

        };
    }

}
