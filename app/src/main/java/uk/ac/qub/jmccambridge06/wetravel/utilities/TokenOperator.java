package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenOperator {

    private final static String USER_TOKEN = "uk.ac.qub.jmccambridge06.wetravel";
    private final static String TOKEN_KEY = "uk.ac.qub.jmccambridge06.wetravel.TOKEN_KEY";

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY, "");
    }

    public static void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

}
