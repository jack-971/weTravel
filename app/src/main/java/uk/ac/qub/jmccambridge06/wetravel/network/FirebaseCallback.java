package uk.ac.qub.jmccambridge06.wetravel.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Callback interface for Firebase requests
 */
public interface FirebaseCallback {

    /**
     * On successful request to firebase this method is triggered with an uploads URL location
     * @param url
     */
    void notifySuccess(String url);

    /**
     * On unsuccessful request to Firebase this method is triggered with an error
     * @param error
     */
    void notifyError(Exception error);

}
