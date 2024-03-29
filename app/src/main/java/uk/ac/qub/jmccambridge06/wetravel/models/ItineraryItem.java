package uk.ac.qub.jmccambridge06.wetravel.models;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

/**
 * Generalised class represents an item that can be added to an itinerary. Contains common attributes and methods
 */
public class ItineraryItem {

    protected String logtag;
    private int entryId;
    private String entryName;
    private String review;

    private Date startDate;
    private Date endDate;

    private String description;
    private TripLocation location;

    /**
     * Contains user id and user name
     */
    protected HashMap<Integer, String> userList;
    private Profile profile;
    protected String status;

    protected ArrayList<String> gallery;

    private Long postTime;

    /**
     * Default constructor
     */
    public ItineraryItem() {

    }

    /**
     * Constructor from json data. Sets attributes and associated profile of whos entry is being viewed
     * @param item
     * @param profile
     */
    public ItineraryItem(JSONObject item, Profile profile){
        logtag = "item";
        Log.d("tag", "in item create");
        try {
            this.setEntryId(Integer.parseInt(item.getString("ID")));
            this.setEntryName(item.getString("Name"));
            this.setStartDate((item.getString("DateStart").equals("null")) ? null : DateTime.sqlToDate(item.getLong("DateStart")));
            this.setEndDate((item.getString("DateFinish").equals("null")) ? null : DateTime.sqlToDate(item.getLong("DateFinish")));
            this.setDescription((item.getString("Description").equals("null")) ? null : item.getString("Description"));
            this.setReview((item.getString("Review").equals("null")) ? null : item.getString("Review"));
            this.setLocation((item.getString("LocationID").equals("null")) ? null : item.getString("LocationID"),
                    (item.getString("LocationDetail").equals("null")) ? null : item.getString("LocationDetail"));
            userList = new HashMap<>();
            this.profile = profile;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<Integer, String> getUserList() {
        return userList;
    }

    /**
     * Populates the user list from json data.
     * @param jsonUsers
     */
    public void setUserList(JSONArray jsonUsers) {
        HashMap<Integer, String> list = new HashMap<>();
        for (int loop=0; loop<jsonUsers.length(); loop++) {
            try {
                JSONObject user = jsonUsers.getJSONObject(loop);
                int userId = Integer.parseInt(user.getString("UserID"));
                String name = user.getString("Name");
                list.put(userId, name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.userList = list;
    }

    /**
     * Populates userlist with the user account - used when a new entry is being created and the only the creator
     * needs to be added
     * @param userAccount
     */
    public void setUserList(UserAccount userAccount) {
        HashMap<Integer, String> list = new HashMap<>();
        list.put(userAccount.getUserId(), userAccount.getProfile().getName());
        this.userList = list;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public TripLocation getLocation() {
        return location;
    }

    public void setLocation(String id, String name) {
        this.location = new TripLocation(id, name);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getGallery() {
        return gallery;
    }

    /**
     * Populates an entries gallery from json data. Checks if there is data and if so adds the image url is added to
     * the gallery array
     * @param response
     * @return
     */
    public boolean setGallery(JSONObject response) {
        setGallery();
        try {
            if (response.getString("status").equals("true")) {
                 JSONArray array = response.getJSONArray("images");
                 for (int loop = 0; loop<array.length(); loop++) {
                     JSONObject image = array.getJSONObject(loop);
                     this.gallery.add(image.getString("Url"));
                 }
                 return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setGallery() {
        this.gallery = new ArrayList<>();
    }

    public void addImage(String url) {
        gallery.add(url);
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }
}
