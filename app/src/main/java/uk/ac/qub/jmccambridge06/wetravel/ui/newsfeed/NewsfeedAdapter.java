package uk.ac.qub.jmccambridge06.wetravel.ui.newsfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.models.Notification;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.ui.ProfileFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.TripFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.gallery.GalleryAdapter;
import uk.ac.qub.jmccambridge06.wetravel.ui.gallery.GalleryFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedViewHolder> {

    public static class NewsfeedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.post_user_image) CircleImageView userImage;
        @BindView(R.id.post_card_text) TextView userText;
        @BindView(R.id.post_card_time) TextView time;
        @BindView(R.id.newsfeed_card) View newsfeedCard;
        @BindView(R.id.post_view_pager) ViewPager2 viewPager;
        @BindView(R.id.post_card_description) TextView description;
        @BindView(R.id.post_card_review) TextView review;
        @BindView(R.id.trip_wishlist_button) Button wishlistButton;

        public NewsfeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

    private ArrayList<ItineraryItem> items;
    private Context context;
    private JsonFetcher jsonFetcher;
    NetworkResultCallback wishlist;

    public NewsfeedAdapter(ArrayList items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsfeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        NewsfeedViewHolder holder = new NewsfeedViewHolder(v);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull NewsfeedViewHolder holder, int position) {
        ItineraryItem current = items.get(position);
        Glide.with(holder.itemView)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_profile_placeholder))
                .load(current.getProfile().getProfilePicture())
                .into(holder.userImage);
        String locationText = "";
        if (current.getLocation().getName() != null)
            locationText = context.getResources().getString(R.string.from) +" "+ current.getLocation().getName();

        holder.userText.setText(current.getProfile().getName() +" " + context.getResources().getString(R.string.has_posted) + " " +
                current.getEntryName() + " " + locationText);
        holder.time.setText(DateTime.formatTime(DateTime.sqlToDate(current.getPostTime()))+ " "
                + DateTime.formatDate(DateTime.sqlToDate(current.getPostTime())));
        if (current.getDescription()!= null)
            holder.description.setText(current.getDescription());
        else
            holder.description.setVisibility(View.GONE);
        if (current.getReview() !=null)
            holder.review.setText(current.getReview());

        else
            holder.review.setVisibility(View.GONE);
        if (current.getGallery().size() > 0)
            holder.viewPager.setVisibility(View.VISIBLE);

        //holder.viewPager = holder.findViewById(R.id.gallery_view_pager);
        holder.viewPager.setAdapter(new GalleryAdapter(current.getGallery(), holder.viewPager, context));
        holder.viewPager.setClipToPadding(false);
        holder.viewPager.setClipChildren(false);
        holder.viewPager.setOffscreenPageLimit(3);
        holder.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        holder.viewPager.setPageTransformer(compositePageTransformer);
        holder.viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity)context).setFragment(new GalleryFragment(current.getGallery()), "gallery_fragment_enlarged", true);
            }
        });

        holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current.getLocation().getId() != null) {
                    updateWishlist();
                    jsonFetcher = new JsonFetcher(wishlist, context);
                    jsonFetcher.addParam("location", current.getLocation().getId());
                    jsonFetcher.postDataVolley(routes.wishlistRoute(((MainMenuActivity)context).getUserAccount().getUserId()));
                } else {
                    Toast.makeText(context, R.string.no_location_data, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void updateWishlist() {
        wishlist = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Toast.makeText(context, R.string.location_added, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(context, R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        };
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
