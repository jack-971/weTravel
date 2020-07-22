package uk.ac.qub.jmccambridge06.wetravel.network;

import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;

public final class routes {

    private static String mainUrl = "http://10.0.2.2:8080/";
    //private static String mainUrl = "https://travel-with-4cd49.web.app/";

    private static String userAccount = "profile/";
    private static String admin = "admin/";
    private static String users = "users/";
    private static String userSearchList = users +"search/";
    private static String settings = "settings/";
    private static String trips = "trips/";
    private static String trip = "trip/";
    private static String addUserToTrip = "user/";

    private static String placesAPI = "https://maps.googleapis.com/maps/api/geocode/json?place_id=";

    public static String getAdminAccountData(int userId) {
        return mainUrl+userAccount+admin+userId;
    }

    /**
     * Returns the route for user profile data.
     * @param userId
     * @return
     */
    public static String getUserAccountData(int userId) {
        return mainUrl+userAccount+userId;
    }

    public static String getUsersRoute(int userId) {
        return mainUrl + users + userId;
    }

    public static String getUserSearchList(String query) {
        return mainUrl + userSearchList + query;
    }

    /**
     * Returns the route to make a change to a users settings.
     * @param userId
     * @return
     */
    public static String updateSettings(int userId) {
        return mainUrl+settings+userId;
    }

    public static String getTrips(int userId, String status) {
        return mainUrl + trips + userId+"?status="+status;
    }

    /**
     * Get route to retrieve a trips details
     * @param userId
     * @param tripId
     * @return
     */
    public static String getTrip(int userId, int tripId) {
        return mainUrl + trips + trip + userId+"?trip="+tripId;
    }

    /**
     * Get route to save a trips details
     */
    public static String saveTripDetails(int id) {
        return mainUrl + trips + trip + id;
    }

    /**
     * Get route to add a user to a trip.
     * @param tripId
     * @return
     */
    public static String addUserToTrip(int tripId) {
        return mainUrl+trips+trip+addUserToTrip+tripId;
    }

    public static String getPlacesAPI(String placeKey) {
        return placesAPI+placeKey+"&key="+ Locations.key;
    }
}