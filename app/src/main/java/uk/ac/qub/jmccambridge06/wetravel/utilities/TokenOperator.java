package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility functions to get and set the users authorization token
 */
public class TokenOperator {

    private final static String USER_TOKEN = "uk.ac.qub.jmccambridge06.wetravel";
    private final static String TOKEN_KEY = "uk.ac.qub.jmccambridge06.wetravel.TOKEN_KEY";

    /**
     * Retrieves token from the users shared preferences
     * @param context
     * @return
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, "");
    }

    /**
     * Sets a token in the users shared preferences
     * @param context
     * @param token
     */
    public static void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

}
