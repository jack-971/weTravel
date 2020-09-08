package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;

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
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder> {

    public static class NotificationListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_image) CircleImageView notificationImage;
        @BindView(R.id.notification_text) TextView notificationText;
        @BindView(R.id.notification_time) TextView notificationTime;
        @BindView(R.id.notification_card) View notificationCard;

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
        switch (current.getNotificationType()) {
            case "friend_accept":
                text = R.string.friend_accept_notification;
                image = R.drawable.ic_notification_request_accepted;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        jsonFetcher.getData(routes.getTrip( ((MainMenuActivity)context).getUserAccount().getUserId(), current.getTripId()));
                        updateRead(current);
                    }
                };
                break;
        }
        holder.notificationText.setText(text);
        holder.notificationTime.setText(DateTime.formatTime(DateTime.sqlToDate(current.getTime()))+ " "
                + DateTime.formatDate(DateTime.sqlToDate(current.getTime())));
        holder.notificationImage.setImageResource(image);
        holder.notificationCard.setOnClickListener(onClickListener);

        if (!current.isRead()) {
            holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_unread));
        } else {
            holder.notificationCard.setBackgroundColor(context.getResources().getColor(R.color.card_read));
        }

    }

    private void updateRead(Notification notification) {
        notification.setRead(true);
        ((MainMenuActivity)context).getUserAccount().getNotificationCentre().decrementUnread();
        JsonFetcher jsonFetcher = new JsonFetcher(null,context);
        jsonFetcher.patchData(routes.patchNotification(notification.getId()));
    }

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

    private void loadProfile(Profile profile) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setProfile(profile);
        ((MainMenuActivity)context).setFragment(profileFragment, "notification_profile", true);
    }

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
