package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.models.Notification;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary.TripFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.ui.users.ProfileFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

/**
 * Holds controller logic for a notification lists adapter
 */
public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder> {

    /**
     * Holds controller lofic for a notifications viewholder
     */
    public static class NotificationListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_image) CircleImageView notificationImage;
        @BindView(R.id.notification_text) TextView notificationText;
        @BindView(R.id.notification_time) TextView notificationTime;
        @BindView(R.id.notification_card) View notificationCard;

        /**
         * Constructs a notifications view holder
         * @param itemView
         */
        public NotificationListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private ArrayList<Notification> notifications;
    private Context context;
    private JsonFetcher jsonFetcher;
    NetworkResultCallback friendCallbackRequest;
    NetworkResultCallback friendCallbackAccept;
    NetworkResultCallback tripCallback;

    /**
     * Constructor for adapter sets the notification list
     * @param notifications
     * @param context
     */
    public NotificationListAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        NotificationListViewHolder holder = new NotificationListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListViewHolder holder, int position) {
        Notification current = notifications.get(position);
        int text = 0;
        int image = 0;
        View.OnClickListener onClickListener = null;
        // determine type and set up view accordingly
        switch (current.getNotificationType()) {
            case "friend_accept":
                text = R.string.friend_accept_notification;
                image = R.drawable.ic_notification_request_accepted;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add user as friend to friend list
                        ((MainMenuActivity) context).getUserAccount().getFriendsProfile(current.getSenderId()).setProfileType(1);
                        loadProfile(((MainMenuActivity)context).getUserAccount().getFriendsProfile(current.getSenderId())); // load the profile
                        holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_read));
                        updateRead(current);
                    }
                };
                break;
            case "friend_request":
                text = R.string.friend_req_notification;
                image = R.drawable.ic_notification_friend_request;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((MainMenuActivity)context).getUserAccount().checkFriendsProfile(current.getSenderId()) == false) {
                            loadProfileRequest();
                            jsonFetcher = new JsonFetcher(friendCallbackRequest, context);
                            jsonFetcher.getData(routes.getUserAccountData(current.getSenderId()));
                        } else {
                            ((MainMenuActivity) context).getUserAccount().getFriendsProfile(current.getSenderId()).setProfileType(4);
                            loadProfile(((MainMenuActivity)context).getUserAccount().getFriendsProfile(current.getSenderId())); // load the profile
                            holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_read));
                        }
                        updateRead(current);
                    }
                };
                break;
            case "trip_added":
                text = R.string.added_trip_notification;
                image = R.drawable.ic_notification_trip_added;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTrip();
                        jsonFetcher = new JsonFetcher(tripCallback, context);
                        jsonFetcher.getData(routes.getTrip( ((MainMenuActivity)context).getUserAccount().getUserId(), current.getTripId(), "planned"));
                        updateRead(current);
                    }
                };
                break;
        }
        holder.notificationText.setText(text);
        holder.notificationTime.setText(DateTime.formatTime(DateTime.sqlToDate(current.getTime()))+ " "
                + DateTime.formatDate(DateTime.sqlToDate(current.getTime())));
        holder.notificationImage.setImageResource(image);
        // on click relevant page is opened and if unread status changed to read in database
        if (current.isRead() == false) {
            holder.notificationCard.setOnClickListener(onClickListener);
        }


        // unread notifications should be highlighted
        if (!current.isRead()) {
            holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_unread));
        } else {
            holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_read));
        }

    }

    /**
     * Sends database request to update notification to read. Also decrements unread in notification centre by 1
     * @param notification
     */
    private void updateRead(Notification notification) {
        notification.setRead(true);
        ((MainMenuActivity)context).getUserAccount().getNotificationCentre().decrementUnread();
        JsonFetcher jsonFetcher = new JsonFetcher(null,context);
        jsonFetcher.patchData(routes.patchNotification(notification.getId()));
    }

    /**
     * Method callback for a profile request
     */
    private void loadProfileRequest() {
        friendCallbackRequest = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("data");
                    JSONObject user = jsonArray.getJSONObject(0);
                    Profile profile = new Profile(user, 4);
                    ((MainMenuActivity)context).getUserAccount().addFriend(profile);
                    loadProfile(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    /**
     * Loads a profile to the screen
     * @param profile
     */
    private void loadProfile(Profile profile) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setProfile(profile);
        ((MainMenuActivity)context).setFragment(profileFragment, "notification_profile", true);
    }

    /**
     * Loads a trip to the screen
     */
    private void loadTrip() {
        tripCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("trip");
                    JSONObject item = jsonArray.getJSONObject(0);
                    item.put("TripStatus", "planned");
                    Trip trip = new Trip(item, ((MainMenuActivity)context).getUserAccount().getProfile());
                    TripFragment tripFrag = new TripFragment();
                    tripFrag.setTrip(trip);
                    MainMenuActivity.fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                            tripFrag, "user_trip_fragment").addToBackStack(null).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


}
