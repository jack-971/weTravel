package uk.ac.qub.jmccambridge06.wetravel.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import uk.ac.qub.jmccambridge06.wetravel.network.RelationshipTypesDb;
import uk.ac.qub.jmccambridge06.wetravel.utilities.ProfileTypes;

/**
 * Represents a users account - used for the signed in user to the app
 */
public class UserAccount {

    private int userId;
    private Profile profile;
    private ArrayList<Profile> friendsList;

    /**
     * Contains the setting type and whether it is on or off
     */
    private HashMap<String, Boolean> settings;

    private NotificationCentre notificationCentre;

    /**
     * Contains the time of post and the item associated with the post
     */
    private TreeMap<Long, ItineraryItem> newsfeed;

    /**
     * Constructor to assign logged in user
     * @param userId
     */
    public UserAccount(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public ArrayList<Profile> getFriendsList() {
        return friendsList;
    }

    /**
     * Sets a friend for the user account. Takes a JSON array of user data as input. determines from the relations table data
     * if it is a friend or requested or pending and creates a profile with the appropriate profile type. Adds this profile to the
     * friends list.
     * @param friendsList
     * @throws JSONException
     */
    public void setFriendsList(JSONArray friendsList) throws JSONException {
        this.friendsList = new ArrayList<>();
        for (int loop = 0; loop<friendsList.length(); loop++) {
            JSONObject friend = friendsList.getJSONObject(loop);
            int profileType = 1;
            switch(friend.getString("RelationshipType")) {
                case RelationshipTypesDb.FRIENDS:
                    profileType = 1; //1 equates to a friend profile type
                break;
                case RelationshipTypesDb.PENDING_FIRST:
                    profileType = (Integer.parseInt(friend.getString("FirstUserID")) == getUserId()) ? 3: 4;
                    break;
                case RelationshipTypesDb.PENDING_SECOND:
                    profileType = (Integer.parseInt(friend.getString("FirstUserID")) == getUserId()) ? 4: 3;
                    break;
            }
            Profile profile = new Profile(friend, profileType);
            addFriend(profile);
        }
    }

    /**
     * Checks for a user id within the profile array and returns it. If not found returns null
     * @param id
     * @return
     */
    public Profile getFriendsProfile(int id) {
        for (int loop = 0; loop<friendsList.size(); loop++) {
            if (friendsList.get(loop).getUserId() == id) {
                return friendsList.get(loop);
            }
        }
        return null;
    }

    /**
     * Determines whether a friend list contains a profile
     * @param id
     * @return
     */
    public boolean checkFriendsProfile(int id) {
        if (friendsList.contains(profile.getUserId() == id)) {
            return true;
        }
        return false;
    }

    /**
     * Adds a profile to the friends list.
     * @param profile
     */
    public void addFriend(Profile profile) {
        this.friendsList.add(profile);
    }

    /**
     * Checks if a profile is contained within the friends list (or if it the admin profile for the account.
     * If in there the profile type saved in the friends list is returned, otherwise the user profile type is returned.
     * @return
     */
    public Profile checkFriend(int userId) {
        if (userId == getUserId()) {
            return getProfile();
        } else {
            for (Profile friend : friendsList) {
                if (friend.getUserId() == userId) {
                    return friend;
                }
            }
            return null;
        }
    }

    public HashMap<String, Boolean> getSettings() {
        return settings;
    }

    /**
     * Creates the settings for the user account
     * @param data
     */
    public void createSettings(JSONObject data) {
        try {
            settings = new HashMap<String, Boolean>();
            int privacy = data.getInt("Private");
            int location = data.getInt("Locations");
            int notifications = data.getInt("Notifications");
            settings.put("private", privacy > 0 ? true : false);
            settings.put("location", location > 0 ? true : false);
            settings.put("notifications", notifications > 0 ? true : false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public NotificationCentre getNotificationCentre() {
        return notificationCentre;
    }

    public void setNotificationCentre(NotificationCentre notificationCentre) {
        this.notificationCentre = notificationCentre;
    }

    public TreeMap<Long, ItineraryItem> getNewsfeed() {
        return newsfeed;
    }

    /**
     * Assigns the newsfeed based on json data. loops three arrays (trips, legs, activities), then loops each one to get
     * each entry.
     * @param data
     */
    public void setNewsfeed(JSONArray data) {
        this.newsfeed = new TreeMap<>();

        for (int outerLoop = 0; outerLoop<data.length(); outerLoop++) {
            try {
                JSONArray array = data.getJSONArray(outerLoop);
                for (int innerLoop = 0; innerLoop<array.length(); innerLoop++) {
                    JSONObject post = array.getJSONObject(innerLoop);
                    if (!newsfeed.containsKey(post.getLong("Time"))) {
                        ItineraryItem item = new ItineraryItem(post, getFriendsProfile(post.getInt("UserID")));
                        item.setGallery();
                        item.setPostTime(post.getLong("Time"));
                        item.setStatus("complete");
                        if (!post.getString("Url").equalsIgnoreCase("null")) {
                            item.addImage(post.getString("Url"));
                        }
                        newsfeed.put(post.getLong("Time"), item);
                    } else {
                        ItineraryItem item = newsfeed.get(post.getLong("Time"));
                        item.addImage(post.getString("Url"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
