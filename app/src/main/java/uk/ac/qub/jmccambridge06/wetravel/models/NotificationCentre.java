package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationCentre {

    ArrayList<Notification> notifications;
    int unread;

    public NotificationCentre(JSONArray notificationsArray) {
        notifications = new ArrayList<>();
        unread = 0;
        for (int loop=0; loop<notificationsArray.length(); loop++) {
            try {
                JSONObject notificationObject = notificationsArray.getJSONObject(loop);
                int id = notificationObject.getInt("NotificationID");
                boolean read = notificationObject.getInt("NotificationRead") == 1 ? true : false;
                if (!read) {
                    unread++;
                }
                Integer userId = ((notificationObject.getString("SenderID").equals("null")) ? null : notificationObject.getInt("SenderID"));
                Integer tripId = ((notificationObject.getString("TripID").equals("null")) ? null : notificationObject.getInt("TripID"));
                String notificationType = notificationObject.getString("Type");
                long time = notificationObject.getLong("Time");
                Notification notification = new Notification(id, time, userId, tripId, notificationType, read);
                addNotification(notification);
             } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }


    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public void decrementUnread() {
        unread--;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}
