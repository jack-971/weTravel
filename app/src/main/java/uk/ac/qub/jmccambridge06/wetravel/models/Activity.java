package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONObject;

/**
 * Class represents an Activity of a leg on a trip. Class extends Itinerary item
 */
public class Activity extends ItineraryItem {

    private String notes;

    /**
     * Constructor to set activity attributes and logtag
     * @param activity
     * @param profile
     * @throws Exception
     */
    public Activity(JSONObject activity, Profile profile) throws Exception {
        super(activity, profile);
        logtag = "Activity";
        this.setNotes((activity.getString("Notes").equals("null")) ? null : activity.getString("Notes"));
    }

    public Activity() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
