package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.fragment.app.Fragment;

/**
 * Contains controller logic for a fragment displaying pictures
 * Holds picture information while it is being uploaded to firebase and then displays it on screen
 */
public class DisplayFragment extends Fragment {

    private Uri imageUri;
    private Bitmap mainImage;
    private String mainImageString;
    private boolean pictureChanged = false;

    public Uri getImageUri() {
        return imageUri;
    }

    public Bitmap getMainImage() {
        return mainImage;
    }

    public void setMainImage(Bitmap mainImage) {
        this.mainImage = mainImage;
    }

    /**
     * Sets the Uri for the profile image.
     * @param newImageUri
     */
    public void setImageUri(Uri newImageUri) {
        imageUri = newImageUri;
    }

    public String getMainImageString() {
        return mainImageString;
    }

    public void setMainImageString(String mainImageString) {
        this.mainImageString = mainImageString;
    }

    public boolean isPictureChanged() {
        return pictureChanged;
    }

    public void setPictureChanged(boolean pictureChanged) {
        this.pictureChanged = pictureChanged;
    }
}
