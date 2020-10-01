package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Class represents a leg of a trip. Extends itinerary item
 */
public class Leg extends ItineraryItem {

    /**
     * Default constructor used for testing purposes
     */
    public Leg() {
        activities = new LinkedHashMap<>();
    }


    /**
     * A collection of activities. Integer value represents the activity id for quick look up
     */
    private LinkedHashMap<Integer, Activity> activities;

    /**
     * Constructor to define leg attributes. Creates new activities list
     * @param leg
     * @param profile
     * @throws Exception
     */
    public Leg(JSONObject leg, Profile profile) throws Exception {
        super(leg, profile);
        this.activities = new LinkedHashMap<>();
        logtag = "Leg";
    }

    public LinkedHashMap<Integer, Activity> getActivities() {
        return activities;
    }

    /**
     * Adds an activity to the leg list in correct location chronologically by date.
     * @param activity
     */
    public void addActivity(Activity activity) {
        activity.status = this.status;
        if (activity.getStartDate() == null) {
            // if null add at end
            activities.put(activity.getEntryId(), activity);
        } else {

            if (activities.values().size() > 0) {
                LinkedHashMap<Integer, Activity> newActivities = new LinkedHashMap<>();
                boolean dateInserted = false;
                for (Activity existingActivity : activities.values()) {
                    if (dateInserted == false) {
                        if (existingActivity.getStartDate() == null || activity.getStartDate().before(existingActivity.getStartDate())) {
                            newActivities.put(activity.getEntryId(), activity);
                            dateInserted = true;
                        }
                    }
                    newActivities.put(existingActivity.getEntryId(), existingActivity);
                }
                activities = newActivities;
            } else {
                activities.put(activity.getEntryId(), activity);
            }

        }
    }

}
