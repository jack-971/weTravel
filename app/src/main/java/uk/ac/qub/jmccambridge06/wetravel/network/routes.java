package uk.ac.qub.jmccambridge06.wetravel.network;

public final class routes {

    //private static String mainUrl = "http://10.0.2.2:8080/";
    private static String mainUrl = "https://travel-with-4cd49.web.app/";

    private static String userAccount = "profile/";
    private static String admin = "admin/";
    private static String users = "users/";
    private static String userSearchList = users +"search/";
    private static String settings = "settings/";

    public static String getAdminAccountData(int userId) {
        return mainUrl+userAccount+admin+userId;
    }

    public static String getUserAccountData(int userId) {
        return mainUrl+userAccount+userId;
    }

    public static String getUsersRoute(int userId) {
        return mainUrl + users + userId;
    }

    public static String getUserSearchList(String query) {
        return mainUrl + userSearchList + query;
    }

    public static String updateSettings(int userId) {
        return mainUrl+settings+userId;
    }
}
