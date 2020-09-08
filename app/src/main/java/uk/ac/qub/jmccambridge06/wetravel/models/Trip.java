package uk.ac.qub.jmccambridge06.wetravel.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class Trip extends ItineraryItem {


    private String tripPicture;
    private LinkedHashMap<Integer, Leg> legs;

    public Trip(int tripId, String tripName) {
        this.setEntryId(tripId);
        this.setEntryName(tripName);
    }

    public Trip(JSONObject trip, Profile profile) {
        super(trip, profile);
        logtag = "Trip";
        Log.d(logtag, "in trip create");
        // Parse trip details
        try {
            this.setTripPicture((trip.getString("Picture").equals("null")) ? null : trip.getString("Picture"));
            this.status = trip.getString("TripStatus");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTripPicture() {
        return tripPicture;
    }

    public void setTripPicture(String tripPicture) {
        this.tripPicture = tripPicture;
    }

    public LinkedHashMap<Integer, Leg> getLegs() {
        return legs;
    }

    public void setLegs(JSONArray array) {
        this.legs = new LinkedHashMap<>();
        for (int loop=0; loop<array.length(); loop++) {
            try {
                JSONObject legJson = array.getJSONObject(loop);
                // Check does the leg already exist - if so need to add the new user for this entry (but not add new leg) so add the user
                // to the leg user list. Otherwise, create a new leg.
                int legId;
                if (legJson.has("LegID")) {
                    legId = Integer.parseInt(legJson.getString("LegID"));
                } else {
                    legId = Integer.parseInt(legJson.getString("ID"));
                }
                int userId = Integer.parseInt(legJson.getString("UserID"));

                if (legs.containsKey(legId)) {
                    legs.get(legId).getUserList().put(userId, this.getUserList().get(userId));
                } else {
                    Leg leg = new Leg(legJson, this.getProfile());
                    leg.status = this.status;
                    this.legs.put(legId, leg);
                    legs.get(legId).getUserList().put(userId, this.getUserList().get(userId));
                    Log.d("tag", "test");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Adds a leg to the leg list in correct location chronologically by date.
     * @param leg
     */
    public void addLeg(Leg leg) {
        leg.status = this.status;
        if (leg.getStartDate() == null) {
            legs.put(leg.getEntryId(), leg);
        } else {
            LinkedHashMap<Integer, Leg> newLegs = new LinkedHashMap<>();
            boolean dateInserted = false;
            for (Leg existingLeg : legs.values()) {
                if (dateInserted == false) {
                    if (existingLeg.getStartDate() == null || leg.getStartDate().before(existingLeg.getStartDate())) {
                        newLegs.put(leg.getEntryId(), leg);
                        dateInserted = true;
                    }
                }
                newLegs.put(existingLeg.getEntryId(), existingLeg);
            }
            legs = newLegs;
        }

    }

    public void addActivities(JSONArray activitiesArray) {
        for (int loop=0; loop<activitiesArray.length(); loop++) {
            try {
                JSONObject activityJson = activitiesArray.getJSONObject(loop);
                Integer legId = Integer.parseInt(activityJson.getString("LegID"));
                Integer userId = Integer.parseInt(activityJson.getString("ActivityUserID"));
                Integer activityId = Integer.parseInt(activityJson.getString("ActivityID"));
                if (legs.get(legId).getActivities().containsKey(activityId)) { // if the activity has already been added to the leg
                    legs.get(legId).getActivities().get(activityId).getUserList().put(userId, getUserList().get(userId)); // Just add the user to the activity
                } else {
                    Activity activity = new Activity(activityJson, getProfile());
                    activity.setStatus(this.getStatus());
                    legs.get(legId).getActivities().put(activityId, activity);
                    activity.getUserList().put(userId, getUserList().get(userId)); // Add with user id and then lookup name from trip users.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
