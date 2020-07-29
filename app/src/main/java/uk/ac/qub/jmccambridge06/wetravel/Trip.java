package uk.ac.qub.jmccambridge06.wetravel;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class Trip extends ItineraryItem {


    private String tripPicture;
    private String status;
    private ArrayList<Leg> legs;

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

    public ArrayList<Leg> getLegs() {
        return legs;
    }

    public void setLegs(JSONArray array) {
        this.legs = new ArrayList<Leg>(10); // leg user list cannot be larger than the trip user list
        ArrayList<Integer> existingLegs = new ArrayList<>(legs.size()); // maximum possible size needed
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

                if (existingLegs.contains(legId)) {
                    for (Leg leg : legs) {
                        if (leg.getEntryId() == legId) {
                            leg.getUserList().put(userId, this.getUserList().get(userId));
                        }
                    }
                } else {
                    Leg leg = new Leg(legJson, this.getProfile());
                    this.legs.add(leg);
                    leg.getUserList().put(userId, this.getUserList().get(userId));
                    existingLegs.add(legId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void addActivities(JSONArray activitiesArray) {

    }


}
