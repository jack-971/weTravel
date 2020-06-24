package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

import java.io.IOException;

import uk.ac.qub.jmccambridge06.wetravel.ui.DisplayFragment;

public class ImageUtility {

    public static final int IMG_REQUEST_ID = 10;

    /**
     * Get user to select an image and then start next activity.
     */
    public static Intent setIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    /**
     * Stores an image as a Bitmap.
     * @param requestCode
     * @param resultCode
     * @param data
     * @param context
     * @return
     * @throws IOException
     */
    public static void storeImageAsBitmap(int requestCode, int resultCode, @Nullable Intent data, Context context, DisplayFragment fragment) throws IOException {
        Uri imageUri = data.getData();
        fragment.setImageUri(imageUri);
        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        fragment.setMainImage(bitmapImage);
    }

}
