package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.ProfileTypes;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class ProfileFragment extends DisplayFragment {

    private Profile profile;

    /**
     * Indicates the profile type of the fragment. Is of class profiletype.
     */
    private int profileType = ProfileTypes.PROFILE_ADMIN;

    private boolean editing = false;

    @BindView(R.id.profile_picture) CircleImageView profileImage;
    @BindView(R.id.profile_name) TextView profileName;
    @BindView((R.id.profile_edit_name)) EditText profileNameEdit;
    @BindView(R.id.profile_username) TextView profileUsername;
    @BindView(R.id.profile_age) TextView profileAge;
    @BindView(R.id.profile_home_location) TextView profileHomeLocation;
    @BindView((R.id.profile_edit_home_location)) EditText profileHomeLocationEdit;
    @BindView(R.id.profile_current_trip) TextView profileCurrentTrip;
    @BindView((R.id.profile_description)) TextView profileDescription;
    @BindView((R.id.profile_description_edit)) EditText profileDescriptionEdit;
    @BindView(R.id.profile_save) Button saveButton;
    @BindView(R.id.profile_edit) Button editButton;

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
        MainMenuActivity.removeNavBar();
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
        if (profileType == ProfileTypes.PROFILE_ADMIN) {
            setProfile(((MainMenuActivity)getActivity()).getUserAccount().getProfile());
            savedViews = new View[]{profileName, profileHomeLocation, profileDescription, editButton};
            editViews = new View[]{profileNameEdit, profileHomeLocationEdit, profileDescriptionEdit, saveButton};
            editButton.setVisibility(View.VISIBLE);

            // Add the on click listener that will enable editing when clicked.
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int loop = 0; loop<savedViews.length; loop++) {
                        savedViews[loop].setVisibility(View.GONE);
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
                    profile.setName(profileNameEdit.getText().toString());
                    profile.setHomeLocation(profileHomeLocationEdit.getText().toString());
                    profile.setDescription(profileDescriptionEdit.getText().toString());
                    if (getMainImage() != null) {
                        profile.setProfilePictureImage(getMainImage());
                        FirebaseLink.saveInFirebase(getImageUri(), getContext());
                    }

                    // upload text and profile image to database and save prof pic to user profile.
                    for (int loop = 0; loop<savedViews.length; loop++) {

                        editViews[loop].setVisibility(View.GONE);
                        savedViews[loop].setVisibility(View.VISIBLE);
                        profileImage.setClickable(false);


                    }
                    loadViews(); // reload views incase of changes.
                }
            });
        }

        loadViews();
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
                profileImage.setImageBitmap(getMainImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadViews() {
        // create a load views method
        profileImage.setImageBitmap(profile.getProfilePictureImage());
        profileName.setText(profile.getName());
        profileNameEdit.setText(profile.getName());
        //profileAge.setText(profile.getAge());
        profileCurrentTrip.setText("Vietnam Tour - Hanoi, Vietnam");
        profileHomeLocation.setText(profile.getHomeLocation());
        profileHomeLocationEdit.setText(profile.getHomeLocation());
        profileDescription.setText(profile.getDescription());
        profileDescriptionEdit.setText(profile.getDescription());
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
