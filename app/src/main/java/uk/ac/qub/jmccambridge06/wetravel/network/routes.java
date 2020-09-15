package uk.ac.qub.jmccambridge06.wetravel.network;

import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;

public final class routes {

    private static String mainUrl = "http://10.0.2.2:8080/";
    //private static String mainUrl = "https://travel-with-4cd49.web.app/";
    //private static String mainUrl = "https://us-central1-travel-with-4cd49.cloudfunctions.net/app/";

    private static String secure = "secure/";
    private static String userAccount = "profile/";
    private static String admin = "admin/";
    private static String users = "users/";
    private static String userSearchList = users +"search/";
    private static String settings = "settings/";
    private static String trips = "trips/";
    private static String trip = "trip/";
    private static String leg = "leg/";
    private static String activity = "activity/";
    private static String addUserToTrip = "user/";
    private static String leaveTrip = "leave/";
    private static String status = "status/";
    private static String notification = "notification/";
    private static String image = "image/";

    private static String placesAPI = "https://maps.googleapis.com/maps/api/geocode/json?place_id=";
    private static String locationsAPI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    public static String getAdminAccountData(int userId) {
        return mainUrl+secure+userAccount+admin+userId;
    }

    /**
     * Returns the route for user profile data.
     * @param userId
     * @return
     */
    public static String getUserAccountData(int userId) {
        return mainUrl+secure+userAccount+userId;
    }

    public static String getUsersRoute(int userId) {
        return mainUrl +secure  + users + userId;
    }

    public static String getUserSearchList(String query) {
        return mainUrl + secure + userSearchList + query;
    }

    /**
     * Returns the route to make a change to a users settings.
     * @param userId
     * @return
     */
    public static String updateSettings(int userId) {
        return mainUrl+secure+settings+userId;
    }

    public static String getTrips(int userId, String status) {
        return mainUrl +secure+ trips + userId+"?status="+status;
    }

    /**
     * Get route to retrieve a trips details
     * @param userId
     * @param tripId
     * @return
     */
    public static String getTrip(int userId, int tripId, String status) {
        return mainUrl +secure+ trips + trip + userId+"?trip="+tripId+"&status="+status;
    }

    public static String leaveTrip(int userId, int id, String type) {
        return mainUrl + secure + trips + leaveTrip + userId+"?id="+id+"&type="+type;
    }

    /**
     * Get route to save a trips details
     */
    public static String saveTripDetails(int id) {
        return mainUrl +secure + trips + trip + id;
    }

    /**
     * Get route to add a user to a trip.
     * @param tripId
     * @return
     */
    public static String addUserToTrip(int tripId) {
        return mainUrl+secure+trips+trip+addUserToTrip+tripId;
    }

    public static String getPlacesAPI(String placeKey) {
        return placesAPI+placeKey+"&key="+ Locations.key;
    }

    public static String getPlacesNearby(double latitude, double longditude) {
        return locationsAPI+"location="+latitude+","+longditude+"&radius=20&key="+Locations.key;
    }


    public static String patchTripStatus(int userId) {
        return mainUrl+secure + trips + status + userId;
    }

    public static String registerUser() {
        return mainUrl + "register";
    }

    public static String loginUser() {
        return mainUrl + "login";
    }

    public static String postNotificationKey(int userId) {
        return mainUrl + secure + notification + userId;
    }

    public static String patchNotification(int notificationId) {
        return mainUrl+secure+notification+"read/"+notificationId;
    }

    public static String postImage(int postId) {
        return mainUrl + secure + image + postId;
    }

    public static String getImage(int postId, int userId, String type) {
        return mainUrl + secure + image + postId+"?user="+userId+"&type="+type;
    }

    public static String patchPostStatus(int postId) {
        return mainUrl + secure + trips + "post/"+postId;
    }
}
