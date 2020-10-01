package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.models.Notification;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Holds controller logic for a notification list
 */
public class NotificationListFragment extends ListFragment {

    ArrayList<Notification> notifications;

    /**
     * Constructor defaults notification list to null
     */
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
    }

    /**
     * populates the notification list
     */
    private void addNotifications() {
        for (int loop=0; loop<notifications.size(); loop++) {
            list.add(notifications.get(loop));
        }
        updateList();
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
