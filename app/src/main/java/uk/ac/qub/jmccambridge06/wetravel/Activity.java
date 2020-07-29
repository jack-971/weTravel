package uk.ac.qub.jmccambridge06.wetravel;

import org.json.JSONObject;

import java.util.ArrayList;

public class Activity extends ItineraryItem {

    private String notes;
    private String attachments;

    public Activity(int legId, String tripName) {
        this.setEntryId(legId);
        this.setEntryName(tripName);
    }

    public Activity(JSONObject activity, Profile profile) throws Exception {
        super(activity, profile);
        logtag = "Leg";
        this.setNotes((activity.getString("Notes").equals("null")) ? null : activity.getString("Notes"));
        this.setAttachments((activity.getString("Attachments").equals("null")) ? null : activity.getString("Attachments"));
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
}
