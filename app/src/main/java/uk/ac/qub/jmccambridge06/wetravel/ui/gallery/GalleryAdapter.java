package uk.ac.qub.jmccambridge06.wetravel.ui.gallery;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Adapter for a View Pager of images
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    /**
     * View holder for each image
     */
    class ImageViewHolder extends RecyclerView.ViewHolder{

        @BindView (R.id.image_view) RoundedImageView imageView;

        /**
         * Constructor for each view
         * @param itemView
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Sets the image from Firebase using the Glide library
         * @param url
         */
        void setImage(String url) {
            Glide.with(MyApplication.getContext())
                    .applyDefaultRequestOptions(new RequestOptions()
                            .error(R.drawable.ic_trip_placeholder_24dp))
                    .load(url)
                    .into(imageView);
        }
    }

    private ArrayList<String> urls;
    private ViewPager2 viewPager;
    Context context;

    /**
     * Constructor with args - sets the viewpager and list of image download links
     * @param urls
     * @param viewPager
     * @param context
     */
    public GalleryAdapter(ArrayList<String> urls, ViewPager2 viewPager, Context context) {
        this.urls = urls;
        this.viewPager = viewPager;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.setImage(urls.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)context).setFragment(new ImageFragment(urls.get(position)), "image_fragment", true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }




}
