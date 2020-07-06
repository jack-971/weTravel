package uk.ac.qub.jmccambridge06.wetravel.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface FirebaseCallback {

    public void notifySuccess(String url);
    public void notifyError(Exception error);

}
