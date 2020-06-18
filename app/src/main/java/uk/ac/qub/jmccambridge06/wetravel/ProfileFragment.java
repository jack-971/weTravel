package uk.ac.qub.jmccambridge06.wetravel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {


    private CircleImageView profileImage;
    private Button saveButton;
    private Button editProfile;

    /**
     * Firebase references
     */
    FirebaseStorage storage;
    StorageReference storageReference;

    public static final int IMG_REQUEST_ID = 10;

    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialise all components.
        profileImage = (CircleImageView) getView().findViewById(R.id.profile_picture);
        profileImage.setOnClickListener(new View.OnClickListener() { // may look at refactoring if used more than once.
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Im Clicked!", Toast.LENGTH_SHORT).show();
                requestImage();
            }
        });

        editProfile = (Button) getView().findViewById(R.id.profile_edit);
        saveButton = (Button) getView().findViewById(R.id.profile_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInFirebase();
            }
        });
    }

    /**
     * Ask user to choose image
     */
    private void requestImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_REQUEST_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("Intent", "It is in activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmapImage);
                saveButton.setEnabled(true);
                saveButton.setVisibility(View.VISIBLE);
                editProfile.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveInFirebase() {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            StorageReference reference = storageReference.child("profile_pictures/picture"+ UUID.randomUUID().toString());

            try {
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        editProfile.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error occured, please try again!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getBytesTransferred());
                        progressDialog.setMessage("Saved" + (int) progress + "%");
                        saveButton.setEnabled(false);
                        saveButton.setVisibility(View.GONE);
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
