package uk.ac.qub.jmccambridge06.wetravel.models;

public class TripLocation {

    private String name;
    private String id;
    private String vicinity;
    private Double latitude;
    private Double longidtude;

    public TripLocation(String id, String name) {

        this.id = id;
        this.name = name;
    }

    public TripLocation(String id, String name, String vicinity) {

        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
    }

    public TripLocation(String name, String id, String vicinity, Double latitude, Double longidtude) {
        this.name = name;
        this.id = id;
        this.vicinity = vicinity;
        this.latitude = latitude;
        this.longidtude = longidtude;
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

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongidtude() {
        return longidtude;
    }

    public void setLongidtude(Double longidtude) {
        this.longidtude = longidtude;
    }
}
