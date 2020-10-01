package uk.ac.qub.jmccambridge06.wetravel.models;

/**
 * A location for an itinerary item
 */
public class TripLocation {

    private String name;
    private String id;
    private String vicinity;
    private Double latitude;
    private Double longidtude;

    /**
     * Constructor for standard entry locations requires name for display and id for saving
     * @param id
     * @param name
     */
    public TripLocation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor for quick add entries - contains vicinity as well
     * @param id
     * @param name
     * @param vicinity
     */
    public TripLocation(String id, String name, String vicinity) {
        this(id, name);
        this.vicinity = vicinity;
    }

    /**
     * Constructor for map entries - contains location coordinates
     * @param name
     * @param id
     * @param vicinity
     * @param latitude
     * @param longidtude
     */
    public TripLocation(String name, String id, String vicinity, Double latitude, Double longidtude) {
        this(id, name);
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongidtude() {
        return longidtude;
    }

}
