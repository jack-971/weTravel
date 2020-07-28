package uk.ac.qub.jmccambridge06.wetravel;

import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public class Leg extends ItineraryItem {

    private ArrayList activities;

    public Leg(int legId, String tripName) {
        this.setEntryId(legId);
        this.setEntryName(tripName);
    }

    public Leg(JSONObject leg) throws Exception {
        super(leg);
        logtag = "Leg";
    }

}
