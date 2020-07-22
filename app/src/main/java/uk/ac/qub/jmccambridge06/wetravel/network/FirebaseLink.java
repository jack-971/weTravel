package uk.ac.qub.jmccambridge06.wetravel.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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


    public static String profilePicturePath = "profile_pictures/";
    public static String profilePicturePrefix = "profile_";
    public static String tripHeaderPath = "trip_headers/";
    public static String tripHeaderPrefix = "trip_header_";

    private static StorageReference TripPictureFolder;

    /**
     * For a given image, saves the file into firebase storage.
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
