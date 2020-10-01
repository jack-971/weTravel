package uk.ac.qub.jmccambridge06.wetravel.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Provides a connection to firebase for saving images
 */
public class FirebaseLink {


    public static String profilePicturePath = "profile_pictures/";
    public static String profilePicturePrefix = "profile_";
    public static String tripHeaderPath = "trip_headers/";
    public static String tripHeaderPrefix = "trip_header_";
    public static String postPath = "post/";
    public static String postPrefix = "post_";

    /**
     * For a given image upload and save in Firebase. Also sends the download URL as part of the callback
     * @param imageUri
     * @param context
     */
    public static void saveInFirebase(Uri imageUri, final Context context, FirebaseCallback callback, String path) {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
            try {
                reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        // retrieve the download url so it can be saved to the databse from firebase callback
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                if(callback != null)
                                    callback.notifySuccess(downloadUrl);
                            }}).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    if(callback != null)
                                        callback.notifyError(exception);
                                    exception.printStackTrace();
                                }
                            });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error occured, please try again!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        // add loading indicator
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
