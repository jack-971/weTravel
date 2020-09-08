package uk.ac.qub.jmccambridge06.wetravel.models;

public class NotificationTypes {


        private static final String FRIEND_REQUEST = "friend_request";
        private static final String FRIEND_ACCEPT = "friend_accept";
        private static final String TRIP_ADD = "trip_added";

        public static String getFriendRequest() {
                return FRIEND_REQUEST;
        }
        public static String getFriendAccept() {
                return FRIEND_ACCEPT;
        }
        public static String getTripAdd() { return TRIP_ADD; }

}

