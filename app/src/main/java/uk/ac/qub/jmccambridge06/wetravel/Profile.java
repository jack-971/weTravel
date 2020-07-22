package uk.ac.qub.jmccambridge06.wetravel;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class Profile {

    private String name;
    private String username;
    private int age;
    private String homeLocation;
    private String currentLocation;
    private String profilePicture;
    private String description;
    private int userId;
    private int profileType;
    private boolean privateProfile;

    /**
     * Constructor taking JSON data and adding to a profile to be used for account admin
     *
     * @param user
     */
    public Profile(JSONObject user, int profileType) throws JSONException {
        this(user.getString("Name"),
                user.getString("Username"),
                user.getString("Dob"),
                user.getString("HomeLocation"),
                user.getString("CurrentLocation"),
                user.getString("ProfilePicture"),
                user.getString("Description"),
                user.getString("UserID"),
                user.getString("Private"),
                profileType);


    }

    /**
     * Constructor with args.
     *
     * @param name
     * @param username
     * @param dob
     * @param homeLocation
     * @param currentLocation
     * @param profilePicture
     * @param description
     */
    private Profile(String name, String username, String dob, String homeLocation, String currentLocation, String profilePicture,
                    String description, String userId, String privateProfile, int profileType) {
        this.name = name;
        this.username = username;
        this.setAge(dob);
        this.homeLocation = homeLocation;
        this.currentLocation = currentLocation;
        this.profilePicture = profilePicture;
        this.description = description;
        this.userId = Integer.parseInt(userId);
        this.profileType = profileType;
        if (Integer.parseInt(privateProfile) == 1) {
            this.privateProfile = true;
        } else {
            this.privateProfile = false;
        }
        Log.i("profile", "new profile created for "+ getName() + profileType);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    /**
     * Sets an age based on a date of birth string
     *
     * @param dob
     */
    public void setAge(String dob) {
        this.age = DateTime.getAge(dob);
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProfileType() {
        return profileType;
    }

    public void setProfileType(int profileType) {
        this.profileType = profileType;
    }

    public boolean isPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        this.privateProfile = privateProfile;
    }
}
