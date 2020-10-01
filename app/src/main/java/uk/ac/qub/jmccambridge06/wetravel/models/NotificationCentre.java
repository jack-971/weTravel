package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents a users notification centre - containing properties required for user notifications
 */
public class NotificationCentre {

    ArrayList<Notification> notifications;

    /**
     * Contains the number of unread notifications associated with the notification centre
     */
    int unread;

    /**
     * Constructor from json data. Populates the notifications within notifications array and also counts the
     * number of unread notifications
     * @param notificationsArray
     */
    public NotificationCentre(JSONArray notificationsArray) {
        notifications = new ArrayList<>();
        unread = 0;
        for (int loop=0; loop<notificationsArray.length(); loop++) {
            try {
                JSONObject notificationObject = notificationsArray.getJSONObject(loop);
                int id = notificationObject.getInt("NotificationID");
                // check if read
                boolean read = notificationObject.getInt("NotificationRead") == 1 ? true : false;
                if (!read) {
                    unread++;
                }
                Integer userId = ((notificationObject.getString("SenderID").equals("null")) ?
                        null : notificationObject.getInt("SenderID"));
                Integer tripId = ((notificationObject.getString("TripID").equals("null")) ?
                        null : notificationObject.getInt("TripID"));
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

    public void decrementUnread() {
        unread--;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
}
