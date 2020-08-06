package uk.ac.qub.jmccambridge06.wetravel;

import android.location.Location;

public class TripLocation {

    private String name;
    private String id;
    private Location location;

    public TripLocation(String id, String name) {

        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
