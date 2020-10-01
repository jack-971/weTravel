package uk.ac.qub.jmccambridge06.wetravel.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Contains full screen image
 */
public class ImageFragment extends Fragment {

    @BindView(R.id.image_view)
    RoundedImageView image;
    String url;

    /**
     * Constructor with image download url
     * @param imageView
     */
    public ImageFragment(String imageView) {
        url = imageView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_slider, container, false);
        ButterKnife.bind(this, view);
        // set fullscreen
        View decorView = ((MainMenuActivity)getActivity()).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // load image
        Glide.with(MyApplication.getContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .error(R.drawable.profile_placeholder_icon))
                .load(url)
                .into(image);
        image.setScaleType(RoundedImageView.ScaleType.CENTER_CROP);
    }
}
