package uk.ac.qub.jmccambridge06.wetravel.network;

import uk.ac.qub.jmccambridge06.wetravel.utilities.Locations;

public final class routes {

    private static String mainUrl = "http://10.0.2.2:8080/";
    //private static String mainUrl = "https://us-central1-travel-with-4cd49.cloudfunctions.net/app/";

    private static String secure = "secure/";
    private static String userAccount = "profile/";
    private static String admin = "admin/";
    private static String users = "users/";
    private static String userSearchList = users +"search/";
    private static String settings = "settings/";
    private static String trips = "trips/";
    private static String trip = "trip/";
    private static String addUserToTrip = "user/";
    private static String leaveTrip = "leave/";
    private static String status = "status/";
    private static String notification = "notification/";
    private static String image = "image/";
    private static String wishlist = "wishlist/";

    private static String placesAPI = "https://maps.googleapis.com/maps/api/geocode/json?place_id=";
    private static String locationsAPI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    /**
     * Returns the route for the logged in users details
     * @param userId
     * @return
     */
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

    /**
     * Returns the route for an accounts friends
     * @param userId
     * @return
     */
    public static String getUsersRoute(int userId) {
        return mainUrl +secure  + users + userId;
    }

    /**
     * Returns the route for a user search
     * @param query
     * @return
     */
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

    /**
     * Returns the route to get a users trips - must specify trip status
     * @param userId
     * @param status
     * @return
     */
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

    /**
     * Returns the route for a user to leave an entry - must specify type of entry
     * @param userId
     * @param id
     * @param type
     * @return
     */
    public static String leaveEntry(int userId, int id, String type) {
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

    /**
     * Returns the URL to get details from Google Places API
     * @param placeKey
     * @return
     */
    public static String getPlacesAPI(String placeKey) {
        return placesAPI+placeKey+"&fields=name,geometry,formatted_address,place_id&key="+ Locations.key;
    }

    /**
     * Returns the url to get a list of locations nearby a set of coordinates from Google Places API
     * @param latitude
     * @param longditude
     * @return
     */
    public static String getPlacesNearby(double latitude, double longditude) {
        return locationsAPI+"location="+latitude+","+longditude+"&radius=20&key="+Locations.key;
    }


    /**
     * Returns route to change the status of a trip
     * @param userId
     * @return
     */
    public static String patchTripStatus(int userId) {
        return mainUrl+secure + trips + status + userId;
    }

    /**
     * Returns the route to create a new user
     * @return
     */
    public static String registerUser() {
        return mainUrl + "register";
    }

    /**
     * Returns the route to check a users login credentials
     * @return
     */
    public static String loginUser() {
        return mainUrl + "login";
    }

    /**
     * Returns the route to add a new notification key
     * @param userId
     * @return
     */
    public static String postNotificationKey(int userId) {
        return mainUrl + secure + notification + userId;
    }

    /**
     * Returns the route to change a notification key
     * @param notificationId
     * @return
     */
    public static String patchNotification(int notificationId) {
        return mainUrl+secure+notification+"read/"+notificationId;
    }

    /**
     * Returns the route to post an image
     * @param postId
     * @return
     */
    public static String postImage(int postId) {
        return mainUrl + secure + image + postId;
    }

    /**
     * Returns the route to get an image
     * @param postId
     * @param userId
     * @param type
     * @return
     */
    public static String getImage(int postId, int userId, String type) {
        return mainUrl + secure + image + postId+"?user="+userId+"&type="+type;
    }

    /**
     * Returns the route to update a post to posted
     * @param postId
     * @return
     */
    public static String patchPostStatus(int postId) {
        return mainUrl + secure + trips + "post/"+postId;
    }

    /**
     * Returns the route for a users wishlist
     * @param userId
     * @return
     */
    public static String wishlistRoute(int userId) {
        return mainUrl + secure + wishlist + userId;
    }
}
