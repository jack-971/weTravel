package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationCentreTest {

    JSONArray notifications;
    int notificationId = 0;
    String notificationTime = "1600444724657";
    int senderId = 10;
    int tripId = 20;
    int notification1Read = 1;
    int notification2Read = 1;
    int notification3Read = 0;
    int unread;
    String notification1Type = "friend_request";
    String notification2Type = "friend_accept";
    String notification3Type = "trip_added";

    @Before
    public void setUp() throws Exception {
        unread = 3 - notification1Read - notification2Read - notification3Read;
        notifications = new JSONArray();
        JSONObject notification1 = new JSONObject().put("NotificationID", notificationId+1).put("NotificationRead",
                notification1Read).put("Type", notification1Type).put("SenderID", senderId+1)
                .put("TripID", "null").put("Time", notificationTime);
        JSONObject notification2 = new JSONObject().put("NotificationID", notificationId+2).put("NotificationRead",
                notification2Read).put("Type", notification2Type).put("SenderID", senderId+2)
                .put("TripID", "null").put("Time", notificationTime);
        JSONObject notification3 = new JSONObject().put("NotificationID", notificationId+3).put("NotificationRead",
                notification3Read).put("Type", notification3Type).put("SenderID", "null")
                .put("TripID", tripId+1).put(
                "Time", notificationTime);
        notifications.put(notification1).put(notification2).put(notification3);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests constructor and decrementer methods. Also tests notification constructor method
     */
    @Test
    public void constructorTest() {
        NotificationCentre notificationCentre = new NotificationCentre(notifications);
        // check friend request
        assertEquals(notificationCentre.getNotifications().get(0).getId(), notificationId+1);
        assertEquals(notificationCentre.getNotifications().get(0).getNotificationType(), notification1Type);
        assertEquals(notificationCentre.getNotifications().get(0).isRead(), true);
        assertNull(notificationCentre.getNotifications().get(0).getTripId()); //no trip id as friend request type
        //check friend accept
        assertEquals(notificationCentre.getNotifications().get(1).getId(), notificationId+2);
        assertEquals(notificationCentre.getNotifications().get(1).getNotificationType(), notification2Type);
        assertEquals(notificationCentre.getNotifications().get(1).isRead(), true);
        assertNull(notificationCentre.getNotifications().get(1).getTripId()); //no trip id
        // check trip added
        assertEquals(notificationCentre.getNotifications().get(2).getId(), notificationId+3);
        assertEquals(notificationCentre.getNotifications().get(2).getNotificationType(), notification3Type);
        assertEquals(notificationCentre.getNotifications().get(2).isRead(), false);
        assertNull(notificationCentre.getNotifications().get(2).getSenderId()); // no sender id

        // check number unread
        assertEquals(notificationCentre.unread, unread);
    }

}