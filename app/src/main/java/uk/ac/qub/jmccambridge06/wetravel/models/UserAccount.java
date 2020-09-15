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

public class UserAccount {

    private int userId;
    private Profile profile;
    private ArrayList<Profile> friendsList;
    private HashMap<String, Boolean> settings;
    private NotificationCentre notificationCentre;
    private TreeMap<Long, ItineraryItem> newsfeed;

/*

    private TripsList tripsList;
    private Map map;
    private Wishlist wishlist;
    private PrivacySetting privacySettings;
    private MessageCentre inbox;
    private NotificationCentre notificationCentre;*/

    public UserAccount(int userId) {
        this.userId = userId;
        //this.profile = profile;

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
        this.friendsList = new ArrayList<Profile>();
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

    public Profile getFriendsProfile(int id) {
        for (int loop = 0; loop<friendsList.size(); loop++) {
            if (friendsList.get(loop).getUserId() == id) {
                return friendsList.get(loop);
            }
        }
        //int index = friendsList.indexOf(profile.getUserId() == id);
        return null;
    }

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
     * Checks if a profile is contained within the friends list. If in there the profile type saved in the friends list is returned,
     * otherwise the user profile type is returned.
     * @return
     */
    public int checkRelationship(int userId) {
        for (Profile friend : friendsList) {
            if (friend.getUserId() == userId) {
                return friend.getProfileType();
            } else if (userId == getUserId()) {
                return ProfileTypes.PROFILE_ADMIN;
            }
        }
        return ProfileTypes.PROFILE_USER;

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

    public void setNewsfeed(JSONArray data) {
        this.newsfeed = new TreeMap<>();

        for (int outerLoop = 0; outerLoop<data.length(); outerLoop++) {
            try {
                //Log.d("tagger", data.get(outerLoop).toString());
                //String test = data.get(outerLoop).toString();

                JSONArray array = data.getJSONArray(outerLoop);
                for (int innerLoop = 0; innerLoop<array.length(); innerLoop++) {
                    Log.d("tagger", array.get(innerLoop).toString());
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

                        //newsfeed.get(post.getLong("Time")).addImage(post.getString("Url"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
