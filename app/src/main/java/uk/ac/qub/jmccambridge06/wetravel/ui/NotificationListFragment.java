package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.models.Notification;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class NotificationListFragment extends ListFragment {

    ArrayList<Notification> notifications;

    public NotificationListFragment() {
        this.notifications = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logtag = "NotificationListFragment";
        super.onViewCreated(view, savedInstanceState);
        setNotifications(((MainMenuActivity)getActivity()).getUserAccount().getNotificationCentre().getNotifications());
        if (notifications.size() == 0) {
            noData();
        }
        /*loadCallback();
        getData();*/

    }

    private void addNotifications() {
        for (int loop=0; loop<notifications.size(); loop++) {
            list.add(notifications.get(loop));
        }
        updateList();
    }

    /**
     * Checks whether it is for a search and determines which route to send.
     */
    void getData() {
        /*Log.i(logtag, "getting data");
            jsonFetcher = new JsonFetcher(getCallback,getContext());
            String url;
            if (query != null) {
                url = routes.getUserSearchList(query);
            } else {
                url = routes.getUsersRoute(profile.getUserId());
            }
            jsonFetcher.getData(url);*/
    }


    @Override
    public void fillList(View view, JSONArray jsonArray) throws JSONException {
        /*Log.i(logtag, "filling list now");
        for (int loop=0; loop<jsonArray.length(); loop++) {
            JSONObject friend = jsonArray.getJSONObject(loop);
            int userId = Integer.parseInt(friend.getString("UserID"));
            Profile profile = ((MainMenuActivity) getActivity()).getUserAccount().checkFriend(userId);
            // For both search and friend list if it exists in userAcc friend list add it from there. If a friend list check that they are
            // friends before adding. If a search can add even if not friends.
            if (query == null) {
                String profileType = friend.getString("RelationshipType");
                if (profileType.equals(RelationshipTypesDb.FRIENDS)) {
                    if (profile != null) {
                        list.add(profile);
                    } else {
                        list.add(new Profile(friend, 2));
                    }
                }
            } else {
                if (profile != null) {
                    list.add(profile);
                } else {
                    list.add(new Profile(friend, 2)); // 2 equates to a user not in friends list
                }
            }
        }
        if (list.size() == 0) {
            noData();
        } else {
            updateList();
        }*/

    }

    @Override
    public void setAdapter() {
        adapter = new NotificationListAdapter(list, getContext());
    }

    @Override
    protected void noData() {
        super.noData();
        noDataText.setText(R.string.no_notifications);
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
        addNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainMenuActivity)getActivity()).updateNotificationBadge();
    }

}
