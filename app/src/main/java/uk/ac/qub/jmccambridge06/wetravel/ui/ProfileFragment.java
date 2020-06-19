package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.ProfileTypes;
import uk.ac.qub.jmccambridge06.wetravel.network.FirebaseLink;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ImageUtility;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class ProfileFragment extends Fragment {

    /**
     * Profile picture Image view
     */
    private static CircleImageView profileImage;
    private String profilePicturePath = "IMG-20191103-WA0006.jpg";
    private static Uri imageUri;
    private int profileType = ProfileTypes.PROFILE_ADMIN;
    private Bitmap profileBitmap = null;

    private Button saveButton;
    private Button editProfile;

    private boolean editing = false;


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

        // Set the profile picture.
        profileImage = (CircleImageView) getView().findViewById(R.id.profile_picture);
        try {
            FirebaseLink.retrieveImageFirebase(profilePicturePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Establish button references
        editProfile = (Button) getView().findViewById(R.id.profile_edit);

        // Check what the type is (admin, friend, user). Show buttons accordingly.
        if (profileType == ProfileTypes.PROFILE_ADMIN) {
            editProfile.setVisibility(View.VISIBLE);

            // Add the on click listener that will enable editing when clicked.
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editProfile.setVisibility(View.GONE);
                    saveButton.setVisibility(View.VISIBLE);
                    profileImage.setOnClickListener(new View.OnClickListener() { // may look at refactoring if used more than once.
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(Intent.createChooser(ImageUtility.setIntent(), "Select Picture"), ImageUtility.IMG_REQUEST_ID);
                        }
                    });
                }
            });
        }

        //edit profile on click method to add edit text boxes, hide normal textboxes, make profile clickable, make save visible, make edit
        // gone.

        saveButton = (Button) getView().findViewById(R.id.profile_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tasty code to make save  gone, make edit visible, upload image to firebase, placeholder for upload to database.
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
                Bitmap bitmap = ImageUtility.storeImageAsBitmap(requestCode, resultCode, data, getContext());
                FirebaseLink.saveInFirebase(imageUri, getContext());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the profile image to the given bitmap image.
     * @param bitmap
     */
    public static void setProfileImage(Bitmap bitmap) {
        profileImage.setImageBitmap(bitmap);
    }

    /**
     * Sets the Uri for the profile image.
     * @param newImageUri
     */
    public static void setImageUri(Uri newImageUri) {
        imageUri = newImageUri;
    }

    /**
     * When edit button is pressed - edit button disappears, save button appears, profile becomes clickable and edit texts replace text views.
     */
    public void onClickEdit() {

    }
}
