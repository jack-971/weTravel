package uk.ac.qub.jmccambridge06.wetravel;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;


public abstract class ItineraryItem {

    protected String logtag;
    private int entryId;
    private String entryName;
    private String review;

    private Date startDate;
    private Date endDate;

    private String description;
    private TripLocation location;

    protected HashMap<Integer, String> userList;
    private Profile profile;
    protected String status;

    public ItineraryItem() {

    }

    public ItineraryItem(JSONObject item, Profile profile){
        logtag = "item";
        Log.d("tag", "in item create");
        // Load the location from Google Places API if there is a location key
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
}
