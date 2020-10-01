package uk.ac.qub.jmccambridge06.wetravel.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Callback interface for requests to the server
 */
public interface NetworkResultCallback {

    /**
     * On successful request to server this method is triggered with any attached json response
     * @param response
     */
    void notifySuccess(JSONObject response);

    /**
     * On unsuccessful request to server this method is triggered with an error
     * @param error
     */
    void notifyError(VolleyError error);

}
