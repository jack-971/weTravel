package uk.ac.qub.jmccambridge06.wetravel.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import uk.ac.qub.jmccambridge06.wetravel.ui.ProfileFragment;

public class FirebaseLink {

    /**
     * File path in firebase for saving profile pictures.
     */
    private static String profilePicturePath = "profile_pictures/";

    /**
     * Prefix for saving profile pictures in firebase.
     */
    private static String profilePicturePrefix = "profile_";

    private static StorageReference TripPictureFolder;

    /**
     * Takes an image path for an image existing in the profile picture folder in firebase storage and gets the location.
     * @param imagePath
     * @return
     */
    private static StorageReference getProfilePictureFolder(String imagePath) {
        String path = profilePicturePath + imagePath;
        Log.i("Firebase Link", "profile link is: "+path);
        StorageReference profilePicture = FirebaseStorage.getInstance().getReference().child(path);
        return profilePicture;
    }

    /**
     * Takes an image path and retrieves it from Firebase saving locally before sending back to a fragment.
     * @param imagePath
     * @throws IOException
     */
    public static void retrieveImageFirebase(String imagePath) throws IOException {
        Log.i("tag", "just into retrieve");
        StorageReference storageReference = getProfilePictureFolder(imagePath);

        final File localFile = File.createTempFile("profilepic", "jpg");

        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i("tag", "In on success");
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                Log.i("tag", "Have decoded file");
                ProfileFragment.setProfileImage(bitmap);
                Log.i("tag", "setting profile image");
                // can add code to determine if its profile or something else.
            }
        });

    }

    /**
     * For a given image, saves the file into firebase storage.
     * @param imageUri
     * @param context
     */
    public static void saveInFirebase(Uri imageUri, final Context context) {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            String path = profilePicturePath+ profilePicturePrefix + UUID.randomUUID().toString();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
            Log.i("tagger", "the path now is: "+path);
            try {
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error occured, please try again!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getBytesTransferred());
                        progressDialog.setMessage("Saved" + (int) progress + "%");
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


}
