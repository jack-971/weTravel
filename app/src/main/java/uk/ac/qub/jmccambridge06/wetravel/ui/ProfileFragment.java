package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class ProfileFragment extends DisplayFragment {

    private final String logTag = "Profile";
    NetworkResultCallback patchProfileCallback = null;
    NetworkResultCallback requestFriendCallback = null;
    NetworkResultCallback acceptFriendCallback = null;
    FirebaseCallback uploadProfilePictureCallback = null;

    private boolean profilePictureChanged;
    @BindView(R.id.profile_picture) CircleImageView profileImage;
    @BindView(R.id.profile_name) TextView profileName;
    @BindView((R.id.profile_edit_name)) EditText profileNameEdit;
    @BindView(R.id.profile_username) TextView profileUsername;
    @BindView(R.id.profile_age) TextView profileAge;
    @BindView(R.id.profile_home_location) TextView profileHomeLocation;
    @BindView((R.id.profile_edit_home_location)) TextView profileHomeLocationEdit;
    @BindView(R.id.profile_date_joined) TextView dateJoined;
    @BindView((R.id.profile_description)) TextView profileDescription;
    @BindView((R.id.profile_description_edit)) EditText profileDescriptionEdit;
    @BindView(R.id.profile_save) Button saveButton;
    @BindView(R.id.profile_edit) Button editButton;
    @BindView(R.id.profile_friends_button) Button friendsButton;
    @BindView(R.id.profile_friends) Button friendsTag;
    @BindView(R.id.add_friend_button) Button addFriendButton;
    @BindView(R.id.profile_friends_requested) Button friendRequestedTag;
    @BindView(R.id.friend_accept) Button acceptFriendButton;
    @BindView(R.id.profile_body) View profileBody;
    @BindView(R.id.profile_private) View profilePrivateText;
    @BindView(R.id.profile_trips_button) Button tripsButton;
    @BindView(R.id.profile_map_button) Button mapButton;
    private Profile profile;

    final int AUTOCOMPLETE_REQUEST_CODE = 01;

    // Arrays containing views to be shown or hidden in display or edit phase.
    private View[] savedViews;
    private View[] editViews;

    /**
     * Creates the view and loads in the profile fragment returning to the main activity.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(logTag, "creating profile view");
        ((MainMenuActivity)getActivity()).removeNavBar();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    /**
     * When view is created it will determine is it a user or friend profile and show the appropriate boxes.
     * Will pull the appropriate data.
     * ALso instantiates onclick events.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadProfile();
    }

    /**
     * Check what the profile type is to determine the setup. If for main user then profile created for the account holder loaded.
     * Profile type will determine what buttons and tags are set regarding edit profile, friend tags, requests tags/buttons.
     * Loads text views with profile data.
     */
    private void loadProfile() {
        if (profile.getProfileType() == ProfileTypes.PROFILE_USER) {
            addFriendButton.setVisibility(View.VISIBLE);
            addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFriend();
                    JsonFetcher jsonFetcher = new JsonFetcher(requestFriendCallback, getContext());
                    jsonFetcher.addParam("requestee", String.valueOf(profile.getUserId()));
                    jsonFetcher.addParam("time", String.valueOf(DateTime.dateToSQL(Calendar.getInstance().getTime())));
                    String url = routes.getUsersRoute(((MainMenuActivity) getActivity()).getUserAccount().getUserId());
                    Log.i(logTag, url);
                    jsonFetcher.postDataVolley(url);
                }
            });
        } else if (profile.getProfileType() == ProfileTypes.PROFILE_REQUEST_SENT) {
            friendRequestedTag.setVisibility(View.VISIBLE);
        } else if (profile.getProfileType() == ProfileTypes.PROFILE_REQUEST_RECEIVED) {
            acceptFriendButton.setVisibility(View.VISIBLE);
            acceptFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptFriend();
                    JsonFetcher jsonFetcher = new JsonFetcher(acceptFriendCallback, getContext());
                    jsonFetcher.addParam("requestor", String.valueOf(profile.getUserId()));
                    jsonFetcher.addParam("time", String.valueOf(DateTime.dateToSQL(Calendar.getInstance().getTime())));
                    String url = routes.getUsersRoute(((MainMenuActivity) getActivity()).getUserAccount().getUserId());
                    Log.i(logTag, url);
                    jsonFetcher.patchData(url);
                }
            });
        } else if (profile.getProfileType() == ProfileTypes.PROFILE_FRIEND) {
            friendsTag.setVisibility(View.VISIBLE);
        } else {
            savedViews = new View[]{profileName, profileHomeLocation, profileDescription, editButton};
            editViews = new View[]{profileNameEdit, profileHomeLocationEdit, profileDescriptionEdit, saveButton};
            editButton.setVisibility(View.VISIBLE);

            // Add the on click listener that will enable editing when clicked.
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profilePictureChanged = false;
                    for (int loop = 0; loop<savedViews.length; loop++) {
                        savedViews[loop].setVisibility(View.INVISIBLE);
                        editViews[loop].setVisibility(View.VISIBLE);
                    }
                    profileImage.setOnClickListener(new View.OnClickListener() { // may look at refactoring if used more than once.
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(Intent.createChooser(ImageUtility.setIntent(), "Select Picture"), ImageUtility.IMG_REQUEST_ID);
                        }
                    });

                }
            });

            // Removes editing ability and uploads changes to database
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profileNameEdit.getText().toString().equals("")) {
                        Toast.makeText(getActivity().getApplicationContext(), "Name cannot be blank!", Toast.LENGTH_SHORT).show();
                    } else {
                        profile.setName(profileNameEdit.getText().toString());
                        profile.setHomeLocation(profileHomeLocationEdit.getText().toString());
                        profile.setDescription(profileDescriptionEdit.getText().toString());
                        if (profilePictureChanged == true) {
                            updateImageCallback();
                            String path = FirebaseLink.profilePicturePath+ FirebaseLink.profilePicturePrefix + UUID.randomUUID().toString();
                            FirebaseLink.saveInFirebase(getImageUri(), getContext(), uploadProfilePictureCallback, path);
                            profilePictureChanged = false;
                        } else {
                            updateData(profile.getProfilePicture());
                            loadViews(); // reload views incase of changes.
                        }

                        // upload text and profile image to database and save prof pic to user profile.
                        for (int loop = 0; loop<savedViews.length; loop++) {
                            editViews[loop].setVisibility(View.GONE);
                            savedViews[loop].setVisibility(View.VISIBLE);
                            profileImage.setClickable(false);
                        }
                    }

                }
            });

            friendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainMenuActivity)getActivity()).adminFriendList = new UserListFragment();
                    ((MainMenuActivity)getActivity()).adminFriendList.setProfile(profile);
                    ((MainMenuActivity)getActivity()).setFragment(((MainMenuActivity)getActivity()).adminFriendList, "admin_friends", true);
                }
            });

        }
            friendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserListFragment friendListFragment = new UserListFragment();
                    friendListFragment.setProfile(profile);
                    // some code to set type or else user different lists
                    MainMenuActivity.fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                            friendListFragment).addToBackStack(null).commit();
                }
            });

        tripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)getActivity()).completedTrips.setProfile(profile);
                ((MainMenuActivity) getActivity()).setFragment(((MainMenuActivity)getActivity()).completedTrips, "user_completed_trips_fragment", true);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity) getActivity()).setFragment(new ProfileMapFragment(profile), "user_profile_map_fragment", true);
            }
        });

        // Set the locations up
        Places.initialize(getContext(), Locations.key);
        PlacesClient placesClient = Places.createClient(getContext());
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
        // Start the autocomplete intent.
        profileHomeLocationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        loadViews();
        checkPrivate();


    }

    /**
     * Activity result for profile image clicks or location setters.
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
                profilePictureChanged = true;
                Glide.with(this)
                        .load(new BitmapDrawable(getResources(), getMainImage()))
                        .into(profileImage);
            } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == AutocompleteActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    profileHomeLocationEdit.setText(place.getAddress());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i("entry", status.getStatusMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadViews() {
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .error(R.drawable.profile_placeholder_icon))
                .load(profile.getProfilePicture())
                .into(profileImage);
        profileName.setText(profile.getName());
        profileUsername.setText(profile.getUsername());
        profileNameEdit.setText(profile.getName());
        profileAge.setText(String.valueOf(profile.getAge()) +" " + getContext().getResources().getString(R.string.years_old));
        dateJoined.setText("Joined "+DateTime.formatDate(DateTime.sqlToDate(profile.getDateJoined())));
        if (profile.getHomeLocation() == null) {
            profileHomeLocation.setText(R.string.unknown_location);
            profileHomeLocationEdit.setText(R.string.unknown_location);
        } else {
            profileHomeLocation.setText(profile.getHomeLocation());
            profileHomeLocationEdit.setText(profile.getHomeLocation());
        }
        if (profile.getDescription() == null || profile.getDescription().equals("")) {
            profileDescription.setText(R.string.no_description);
            profileDescriptionEdit.setText(profile.getDescription());
        } else {
            profileDescription.setText(profile.getDescription());
            profileDescriptionEdit.setText(profile.getDescription());
        }

    }

    void updateImageCallback() {
        uploadProfilePictureCallback = new FirebaseCallback() {
            @Override
            public void notifySuccess(String url) {
                updateData(url);
                profile.setProfilePicture(url);
                loadViews();
            }
            @Override
            public void notifyError(Exception error) {

            }
        };
    }

    private void checkPrivate() {
        if (profile.isPrivateProfile() && (profile.getProfileType() != ProfileTypes.PROFILE_ADMIN && profile.getProfileType() != ProfileTypes.PROFILE_FRIEND)) {
            profilePrivateText.setVisibility(View.VISIBLE);
            profileBody.setVisibility(View.GONE);
        } else {
            profilePrivateText.setVisibility(View.GONE);
            profileBody.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Loads callbacks for when the user profile data is updated. Toast message will confirm this has been completed.
     */
    void updateProfileCallback(){
        patchProfileCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.profile_change_saved, Toast.LENGTH_SHORT).show();
                ((MainMenuActivity)getActivity()).loadDrawer();
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.d(logTag, "Error patching profile data");
                error.printStackTrace();

            }
        };
    }

    void requestFriend() {
        requestFriendCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logTag, "successful friend request");
                profile.setProfileType(ProfileTypes.PROFILE_REQUEST_SENT);
                ((MainMenuActivity)getActivity()).getUserAccount().addFriend(profile);
                addFriendButton.setVisibility(View.GONE);
                friendRequestedTag.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), R.string.friend_requested, Toast.LENGTH_SHORT).show();
                sendNotification();
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.e(logTag, "Error with friend request: ");
                error.printStackTrace();
            }
        };
    }

    void acceptFriend() {
        acceptFriendCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logTag, "Friend accepted");
                profile.setProfileType(ProfileTypes.PROFILE_FRIEND);
                //((MainMenuActivity)getActivity()).getUserAccount().addFriend(profile);
                acceptFriendButton.setVisibility(View.GONE);
                friendsTag.setVisibility(View.VISIBLE);
                checkPrivate();
                Toast.makeText(getActivity().getApplicationContext(), R.string.friend_accepted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Log.e(logTag, "Error with friend accept: ");
                error.printStackTrace();
            }
        };
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Send a patch request to the API to update database with edited data.
     * @param downloadUrl
     */
    private void updateData(String downloadUrl) {
        updateProfileCallback();
        JsonFetcher jsonFetcher = new JsonFetcher(patchProfileCallback, getActivity());
        jsonFetcher.addParam("name", profileNameEdit.getText().toString());
        jsonFetcher.addParam("home", profileHomeLocationEdit.getText().toString());
        jsonFetcher.addParam("image", downloadUrl);
        jsonFetcher.addParam("description", profileDescriptionEdit.getText().toString());
        jsonFetcher.patchData((routes.getUserAccountData(((MainMenuActivity) getActivity()).getUserAccount().getUserId())));
    }



    private void sendNotification() {

    }

}
