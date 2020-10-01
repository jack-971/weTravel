package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

import android.app.Activity;
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
import android.widget.FrameLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.DisplayFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.ui.gallery.GalleryFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;
import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

/**
 * Abstract class wholes controller logic for trip entry pages (trip, leg, activity)
 */
public abstract class TripEntryFragment extends DisplayFragment {

    // All views
    @BindView(R.id.trip_name_view) View entryNameView;
    @BindView(R.id.edit_trip_name) EditText entryName;
    @BindView(R.id.trip_picture_view) View entryPictureView;
    @BindView(R.id.edit_trip_picture) Button entryPicture;
    @BindView(R.id.trip_date_view) View entryDateView;
    @BindView(R.id.edit_trip_date_start) EditText startDate;
    @BindView(R.id.edit_trip_date_finish) EditText finishDate;
    @BindView(R.id.trip_description_view) View entryDescriptionView;
    @BindView(R.id.edit_trip_description) EditText description;
    @BindView(R.id.trip_description) TextView descriptionHeader;
    @BindView(R.id.trip_location_view) View entryLocationView;
    @BindView(R.id.edit_trip_location) TextView location;
    @BindView(R.id.trip_date_hyphen) TextView dateHyphen;

    @BindView(R.id.trip_notes_view) View entryNotesView;
    @BindView(R.id.trip_notes) TextView notesHeader;
    @BindView(R.id.trip_edit_notes) EditText notes;
    @BindView(R.id.trip_attendees_view) View entryAttendeesView;
    @BindView(R.id.trip_attendees_list) TextView attendees;
    @BindView(R.id.trip_add_attendees_view) View entryAddAttendeesView;
    @BindView(R.id.trip_add_attendee) AutoCompleteTextView addAttendees;
    @BindView(R.id.trip_review_rating_view) View entryRatingView;
    @BindView(R.id.edit_review_rating) EditText rating;
    @BindView(R.id.trip_review_text_view) View entryReviewView;
    @BindView(R.id.trip_review_text) TextView reviewHeader;
    @BindView(R.id.edit_review_text) EditText review;
    @BindView(R.id.trip_active_button) Button makeActiveButton;
    @BindView(R.id.edit_trip_date_view) View editDateView;

    @BindView(R.id.complete_trip_name) TextView completeEntryName;
    @BindView(R.id.complete_trip_date_start) TextView completeDateStart;
    @BindView(R.id.complete_trip_date_finish) TextView completeDateFinish;
    @BindView(R.id.complete_trip_description) TextView completeDescription;
    @BindView(R.id.complete_trip_location) TextView completeEntryLocation;
    @BindView(R.id.complete_attendees_list) TextView completeAttendees;
    @BindView(R.id.complete_notes) TextView completeNotes;
    @BindView(R.id.complete_review_text) TextView completeReview;
    @BindView(R.id.complete_trip_date_hyphen) TextView completeDateHyphen;
    @BindView((R.id.complete_trip_date_view)) View completeDateView;

    @BindView(R.id.trip_save_button) Button saveButton;
    @BindView(R.id.trip_leave_button) Button leaveButton;
    @BindView(R.id.trip_add_attendee_button) Button addAttendeeButton;
    @BindView(R.id.trip_gallery_button) protected Button galleryButton;
    @BindView(R.id.trip_add_picture_button) Button addGalleryImageButton;
    @BindView(R.id.trip_post_button) Button postButton;
    @BindView(R.id.trip_wishlist_button) Button wishlistButton;

    @BindView(R.id.checkbox_attendees) CheckBox addAllCheckBox;

    @BindView(R.id.gallery_container)
    protected FrameLayout galleryContainer;

    /**
     * The trip the entry is attached to
     */
    protected Trip trip;

    NetworkResultCallback addAttendeeCallback;
    NetworkResultCallback saveEntryCallback;
    NetworkResultCallback leaveEntryCallback;
    FirebaseCallback newImageCallback;
    FirebaseCallback tripImageCallback;
    NetworkResultCallback loadImagesCallback;
    NetworkResultCallback postCallback;

    JsonFetcher jsonFetcher;
    String logtag;

    /**
     * The item the fragment is for - will be a trip, leg or activity
     */
    protected ItineraryItem item;
    protected String type;

    final int AUTOCOMPLETE_REQUEST_CODE = 01;
    final int PICTURE_REQUEST_CODE = 02;

    GalleryFragment galleryFragment;

    /**
     * Default constructor
     */
    public TripEntryFragment() {

    }

    /**
     * Constructor to set the item - item passed will be a trip, leg or activity
     * @param item
     */
    public TripEntryFragment(ItineraryItem item) {
        this.item = item;
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
        ButterKnife.bind(this, view);

        // execute following code if trip is in editing phase. else it is a new trip being created so placeholders should be used. If new trip
        // dates in calendar picker automated to null otherwise come from the database values.
        Date initialiseStartDate = null;
        Date initialiseFinishDate = null;
        if (item != null) {
            initialiseStartDate=item.getStartDate();
            initialiseFinishDate=item.getEndDate();
            loadDetails();
            if (item.getStatus().equalsIgnoreCase("planned")) {
                leaveButton.setVisibility(View.VISIBLE);
                entryAttendeesView.setVisibility(View.VISIBLE);
                entryAddAttendeesView.setVisibility(View.VISIBLE);
                addGalleryImageButton.setVisibility(View.GONE);
                galleryButton.setVisibility(View.GONE);
            } else if (item.getStatus().equalsIgnoreCase("complete")){
                displayComplete();
            } else {
                entryReviewView.setVisibility(View.VISIBLE);
                leaveButton.setVisibility(View.VISIBLE);
                postButton.setVisibility(View.VISIBLE);
            }
        } else {
            entryAttendeesView.setVisibility(View.GONE);
            entryAddAttendeesView.setVisibility(View.GONE);
            addGalleryImageButton.setVisibility(View.GONE);
            galleryButton.setVisibility(View.GONE);
        }

        startDate.setOnClickListener(new EditTextDateClicker(getContext(), startDate, initialiseStartDate));
        finishDate.setOnClickListener(new EditTextDateClicker(getContext(), finishDate, initialiseFinishDate));

        // Set the locations up
        Places.initialize(getContext(), Locations.key);
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
            if (entryName.getText().toString().trim().length() == 0) {
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

        addGalleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(ImageUtility.setIntent(), "Select Picture"), ImageUtility.IMG_REQUEST_ID);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getGallery() != null) {
                    if (galleryContainer.getVisibility() == View.VISIBLE)
                        galleryContainer.setVisibility(View.GONE);
                    else
                    if (item.getGallery().size() == 0)
                        Toast.makeText(getActivity().getApplicationContext(), R.string.no_gallery, Toast.LENGTH_SHORT).show();
                    else
                        galleryContainer.setVisibility(View.VISIBLE);
                }

            }
        });

        // launches gallery fragment as a separate page
        galleryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)getContext()).setFragment(new GalleryFragment(item.getGallery()), "gallery_fragment_enlarged", true);
            }
        });

        // posts an entry to newsfeed
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
                JsonFetcher jsonFetcher = new JsonFetcher(postCallback, getContext());
                jsonFetcher.addParam("user", String.valueOf(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
                jsonFetcher.addParam("type", type);
                jsonFetcher.addParam("time", String.valueOf(DateTime.dateToSQL(Calendar.getInstance().getTime())));
                jsonFetcher.patchData(routes.patchPostStatus(item.getEntryId()));
            }
        });

    }

    /**
     * Used when a trip status is complete. Changes all the textboxes to edit text boxes and removes buttons
     */
    protected void displayComplete() {
        entryName.setVisibility(View.GONE);
        editDateView.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        location.setVisibility(View.GONE);
        notes.setVisibility(View.GONE);
        attendees.setVisibility(View.GONE);
        review.setVisibility(View.GONE);
        addGalleryImageButton.setVisibility(View.GONE);
        completeDateView.setVisibility(View.VISIBLE);
        if (item.getStartDate() == null) {
            entryDateView.setVisibility(View.GONE);
        } else if (item.getEndDate() == null) {
            completeDateFinish.setVisibility(View.GONE);
            dateHyphen.setVisibility(View.GONE);
            completeDateStart.setVisibility(View.VISIBLE);
        } else {
            completeDateStart.setVisibility(View.VISIBLE);
            completeDateHyphen.setVisibility(View.VISIBLE);
            completeDateFinish.setVisibility(View.VISIBLE);
        }
        if (item.getLocation().getName() != null) {
            completeEntryLocation.setVisibility(View.VISIBLE);
        } else {
            entryLocationView.setVisibility(View.GONE);
        }
        completeEntryName.setVisibility(View.VISIBLE);
        completeDescription.setVisibility(View.VISIBLE);
        descriptionHeader.setVisibility(View.GONE);
        completeNotes.setVisibility(View.VISIBLE);
        notesHeader.setVisibility(View.GONE);
        reviewHeader.setVisibility(View.GONE);
        entryAddAttendeesView.setVisibility(View.GONE);
        completeReview.setVisibility(View.VISIBLE);
        leaveButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        makeActiveButton.setVisibility(View.GONE);

        // if not admin profile then give add to wishlist option
        if (((MainMenuActivity)getActivity()).getUserAccount().getProfile().getUserId() != item.getProfile().getUserId()) {
            wishlistButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Saves entry data to the database
     */
    protected abstract void sendData();

    /**
     * Loads entry data from item model into views.
     */
    public void loadDetails() {
        entryName.setText(item.getEntryName());
        completeEntryName.setText(item.getEntryName());
        if (item.getStartDate() != null) {
            startDate.setText(DateTime.formatDate(item.getStartDate()));
            completeDateStart.setText(DateTime.formatDate(item.getStartDate()));
        }
        if (item.getEndDate() != null) {
            finishDate.setText(DateTime.formatDate(item.getEndDate()));
            completeDateFinish.setText(DateTime.formatDate(item.getEndDate()));
        }
        if (item.getDescription() != null) {
            description.setText(item.getDescription());
            completeDescription.setText(item.getDescription());
        }
        if (item.getLocation() !=null) {
            location.setText(item.getLocation().getName());
            location.setTag(item.getLocation().getId());
            completeEntryLocation.setText(item.getLocation().getName());
        }
        if (item.getReview() != null) {
            review.setText(item.getReview());
            completeReview.setText(item.getReview());
        }

        // add users to a string to add to attendees text view.
        attendees.setText(addUsers(item.getUserList().values()));
        completeAttendees.setText(addUsers(item.getUserList().values()));
        getGalleryData(); // requests gallery data for entry
    };

    /**
     * method callback to post an entry to friends newsfeeds.
     */
    private void createPost() {
        postCallback  = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.successful_post, Toast.LENGTH_SHORT).show();
                postButton.setVisibility(View.GONE);
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.post_error, Toast.LENGTH_SHORT).show();

            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            // for trip autocomplete
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                location.setText(place.getAddress());
                location.setTag(place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("entry", status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // The user canceled the operation.
            }
        } else if (requestCode == ImageUtility.IMG_REQUEST_ID) {
            // for choosing picture
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (requestCode == ImageUtility.IMG_REQUEST_ID && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    ImageUtility.storeImageAsBitmap(requestCode, resultCode, data, getContext(), this);
                    addImageCallback();
                    String path = FirebaseLink.postPath+ FirebaseLink.postPrefix + UUID.randomUUID().toString();
                    FirebaseLink.saveInFirebase(getImageUri(), getContext(), newImageCallback, path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method callback to add an image to firebase. Launches request to save download URL in database
     */
    public void addImageCallback() {
        newImageCallback = new FirebaseCallback() {
            @Override
            public void notifySuccess(String url) {
                Toast.makeText(getActivity().getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                JsonFetcher jsonFetcher = new JsonFetcher(null, getContext());
                jsonFetcher.addParam("type", type);
                jsonFetcher.addParam("url", url);
                jsonFetcher.addParam("user", String.valueOf(((MainMenuActivity)getActivity()).getUserAccount().getUserId()));
                jsonFetcher.postDataVolley(routes.postImage(item.getEntryId()));
                updateGallery(url);
            }
            @Override
            public void notifyError(Exception error) {
                error.printStackTrace();
            }
        };
    }

    /**
     * adds a new image into the gallery
     * @param url
     */
    protected void updateGallery(String url) {
        item.addImage(url);
        setGalleryFragment(false);
    }

    /**
     * Method to send data to the database.
     */
    protected void saveEntryRequest() {
        saveEntryData();
        jsonFetcher = new JsonFetcher(saveEntryCallback, getContext());
        jsonFetcher.addParam("Name", entryName.getText().toString());
        jsonFetcher.addParam("DateStart", DateTime.dateToMilliseconds(startDate.getText().toString()));
        jsonFetcher.addParam("DateFinish", DateTime.dateToMilliseconds(finishDate.getText().toString()));
        jsonFetcher.addParam("Description", description.getText().toString());
        jsonFetcher.addParam("Review", review.getText().toString());
        if (location.getTag()==null) {
            jsonFetcher.addParam("LocationID", "null");
            jsonFetcher.addParam("LocationDetail", "null");
        } else {
            jsonFetcher.addParam("LocationID", location.getTag().toString());
            jsonFetcher.addParam("LocationDetail", location.getText().toString());
        }
    };

    /**
     * method callback for saving trip to database. Updates all item models accordingly and if was a new entry,
     * updates view to show additional edit fields and buttons
     */
    protected abstract void saveEntryData();

    /**
     * Prepares and send the delete request to database to delete user from a trip or activity.
     */
    protected void sendLeaveRequest() {
        leave();
        jsonFetcher = new JsonFetcher(leaveEntryCallback, getContext());
        jsonFetcher.deleteData(routes.leaveEntry(((MainMenuActivity)getActivity()).getUserAccount().getUserId(), item.getEntryId(), type));
    }

    /**
     * Adds a user to an entry in the database. First ensures it is a valid addition
     */
    protected void sendAttendeeData() {
        // Check to make sure not already in trip - checking the id captured in suggestions box
        for (int userId : item.getUserList().keySet()) {
            if ((Integer)addAttendees.getTag() == userId) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_friend_already_added, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // If not on trip then send data to database
        saveAttendee();
        jsonFetcher = new JsonFetcher(addAttendeeCallback, getContext());
        jsonFetcher.addParam("user", addAttendees.getTag().toString());
        jsonFetcher.addParam("type", type);
        jsonFetcher.addParam("time", String.valueOf(DateTime.dateToSQL(Calendar.getInstance().getTime())));
    };

    /**
     * method callback for adding an attendee to the trip. Updates the user list to reflect the new attendee
     */
    protected void saveAttendee() {
        addAttendeeCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                // Update the phone display to reflect new attendee in attendees list
                item.getUserList().put((Integer)addAttendees.getTag(), addAttendees.getText().toString());
                attendees.setText(addUsers(item.getUserList().values()));
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_attendee, Toast.LENGTH_SHORT).show();
            }
        };
    };

    public void setItem(ItineraryItem item) {
        this.item = item;
    }

    /**
     * Prepare call backs for leaving a trip or activity.
     */
    protected abstract void leave();

    /**
     * Method callback for getting gallery urls
     */
    protected void loadGallery() {
        loadImagesCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                if (item.setGallery(response)) {
                    setGalleryFragment(false);
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    /**
     * Sets the gallery fragment in the fragment manager
     * @param fullScreen
     */
    private void setGalleryFragment(boolean fullScreen) {
        galleryFragment = new GalleryFragment(item.getGallery());
        getChildFragmentManager().beginTransaction().replace(R.id.gallery_container,
                galleryFragment, "gallery_fragment").addToBackStack(null).commit();
    }

    /**
     * sends request to get gallery urls from database
     */
    protected void getGalleryData() {
        if (item != null) {
            loadGallery();
            JsonFetcher jsonFetcher = new JsonFetcher(loadImagesCallback, getContext());
            jsonFetcher.getData(routes.getImage(item.getEntryId(), item.getProfile().getUserId(), type));
        }

    }

    /**
     * Adds adapter to add attendee text box so it can be autocompleted when a users name is typed.
     * @param profiles
     */
    protected void addAttendeesSetUp(ArrayList<Profile> profiles) {
        FriendSuggestionAdapter adapter = new FriendSuggestionAdapter(getContext(), profiles);
        addAttendees.setAdapter(adapter);
        addAttendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check friend has been selected. Error msg if not, if so then send the data via the subclass method.
                if (addAttendees.getTag() == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_select_friend_dropdown, Toast.LENGTH_SHORT).show();
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
