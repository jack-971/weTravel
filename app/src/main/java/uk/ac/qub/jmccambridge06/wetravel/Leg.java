package uk.ac.qub.jmccambridge06.wetravel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class Leg extends ItineraryItem {

    private ArrayList activities;

    public Leg(int legId, String tripName) {
        this.setEntryId(legId);
        this.setEntryName(tripName);
    }

    public Leg(JSONObject leg, Profile profile) throws Exception {
        super(leg, profile);
        logtag = "Leg";
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(JSONArray array) {
        for (int loop=0; loop<array.length(); loop++) {
            try {
                JSONObject activityJson = array.getJSONObject(loop);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*this.activities = new ArrayList<Activity>(10); // leg user list cannot be larger than the trip user list
        ArrayList<Integer> existingActivities = new ArrayList<>(activities.size()); // maximum possible size needed
        for (int loop=0; loop<array.length(); loop++) {
            try {
                JSONObject activity = array.getJSONObject(loop);
                // Check does the leg already exist - if so need to add the new user for this entry (but not add new leg) so add the user
                // to the leg user list. Otherwise, create a new leg.
                int activityId;
                if (activity.has("ActivityID")) {
                    activityId = Integer.parseInt(activity.getString("ActivityID"));
                } else {
                    activityId = Integer.parseInt(activity.getString("ID"));
                }

                if (existingActivities.contains(activityId)) {
                    this.getActivities().get(activityId).getUserList().put(activityId, getUserList().get(activityId));
                } else {
                    int userId = Integer.parseInt(leg.getString("UserID"));
                    if (userId == getProfile().getUserId()) {
                        this.legs.add(new Leg(leg, this.getProfile()));
                        //this.getLegs().get(legId).getUserList().put(legId, this.getUserList().get(legId));
                        this.getLegs().get(loop).getUserList().put(userId, this.getUserList().get(userId));
                        existingLegs.add(legId);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/
    }

}
