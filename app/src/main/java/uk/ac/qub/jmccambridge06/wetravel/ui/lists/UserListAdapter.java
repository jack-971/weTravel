package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.ui.users.ProfileFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

/**
 * Holds controller logic for a user lists adapter
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    /**
     * Constructs the user list viewholder
     */
    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_card_image) CircleImageView userImage;
        @BindView(R.id.user_card_name) TextView userName;
        @BindView(R.id.user_card_username) TextView username;
        @BindView(R.id.user_card_home) TextView home;
        @BindView(R.id.user_card) View userCard;
        @BindView(R.id.card_friends_tag) Button friendsTag;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private ArrayList<Profile> userCardList;
    private Context context;

    /**
     * Constructor with args for user list - saves as a list of user profiles
     * @param userCardList
     * @param context
     */
    public UserListAdapter(ArrayList<Profile> userCardList, Context context) {
        this.userCardList = userCardList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user, parent, false);
        UserListViewHolder holder = new UserListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        Profile current = userCardList.get(position);

        // If it is the admins profile then it cannot be clicked on. If already friends a friends tag is added.
        if (current.getProfileType() != ProfileTypes.PROFILE_ADMIN) {
            if (current.getProfileType() == ProfileTypes.PROFILE_FRIEND) {
                holder.friendsTag.setVisibility(View.VISIBLE);
            }
            holder.userCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setProfile(current);
                    MainMenuActivity.fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                            profileFragment).addToBackStack(null).commit();
                }
            });
        }
        holder.userName.setText(current.getName());
        holder.username.setText(current.getUsername());
        holder.home.setText(current.getHomeLocation());
        try {
            // load the image
            Glide.with(holder.itemView)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.profile_placeholder_icon))
                    .load(current.getProfilePicture())
                    .into(holder.userImage);
        } catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return userCardList.size();
    }
}
