package uk.ac.qub.jmccambridge06.wetravel.models;

/**
 * Represents a Notification item.
 */
public class Notification {

    private int id;
    private  long time;
    private Integer senderId;
    private Integer tripId;
    private String notificationType;
    private boolean read;

    /**
     * Constructor with args.
     * @param id
     * @param time
     * @param senderId
     * @param tripId
     * @param notificationType
     * @param read
     */
    public Notification(int id, long time, Integer senderId, Integer tripId, String notificationType, boolean read) {
        this.id = id;
        this.time = time;
        this.senderId = senderId;
        this.tripId = tripId;
        this.notificationType = notificationType;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
