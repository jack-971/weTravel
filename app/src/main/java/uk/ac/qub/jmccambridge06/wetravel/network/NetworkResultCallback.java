package uk.ac.qub.jmccambridge06.wetravel.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface NetworkResultCallback {

    public void notifySuccess(JSONObject response);
    public void notifyError(VolleyError error);

}
