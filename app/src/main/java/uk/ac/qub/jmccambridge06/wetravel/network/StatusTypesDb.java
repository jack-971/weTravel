package uk.ac.qub.jmccambridge06.wetravel.network;

public class StatusTypesDb {


    public static final String PLANNED = "planned";
    public static final String ACTIVE = "active";
    public static final String COMPLETE = "complete";

    public static int getPlanned() {
        return 1;
    }

    public static int getActive() {
        return 2;
    }

    public static int getComplete() {
        return 3;
    }

}
