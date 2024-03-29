package uk.ac.qub.jmccambridge06.wetravel.models;

import android.app.Application;
import android.content.Context;

/**
 * Class provides helper methods for the application
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
