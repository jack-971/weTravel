package uk.ac.qub.jmccambridge06.wetravel.models;

public class Notification {

    private int id;
    private  long time;
    private Integer senderId;
    private Integer tripId;
    private String notificationType;
    private boolean read;

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

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
