package uk.ac.qub.jmccambridge06.wetravel.network;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.ac.qub.jmccambridge06.wetravel.utilities.TokenOperator;

/**
 * Class used to send HTTP requests via the Android Volley library. Send json data within the requests.
 * Each request generates headers and can have parameters added manually. Every request links back to a callback
 * with response.
 */
public class JsonFetcher {

    NetworkResultCallback networkResultCallback = null;
    Context context;
    private Map<String, String> header;
    private Map<String, Object> params;

    /**
     * Constructor with args - requries callback for HTTP response
     * @param resultCallback
     * @param context
     */
    public JsonFetcher(NetworkResultCallback resultCallback, Context context){
        networkResultCallback = resultCallback;
        this.context = context;
        header = new HashMap<>();
        params = new HashMap<>();
    }

    /**
     * Add a new parameter to the request body
     * @param key
     * @param value
     */
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    /**
     * Send a patch request via Volley
     * @param url
     */
    public void patchData(String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject parameters = new JSONObject(params);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.PATCH, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifySuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifyError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addHeaders();
                }};
            queue.add(jsonObj);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Send a post request via volley
     * @param url
     */
    public void postDataVolley(String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject parameters = new JSONObject(params);
            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifySuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifyError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addHeaders();
                }};

            queue.add(jsonObj);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Send a get request via volley
     * @param url
     */
    public void getData(String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            Log.i("tag", url);
            JSONObject parameters = new JSONObject(params);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, parameters, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifySuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifyError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addHeaders();
                }};

            queue.add(jsonObject);

        }catch(Exception e){

        }
    }

    /**
     * Send a delete request via volley
     * @param url
     */
    public void deleteData(String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            Log.i("tag", url);
            JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifySuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(networkResultCallback != null)
                        networkResultCallback.notifyError(error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return addHeaders();
                }};

            queue.add(jsonObject);

        }catch(Exception e){

        }
    }

    /**
     * Adds the headers to a volley HTTP request
     * @return
     */
    private Map<String, String> addHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("X-HTTP-Method-Override","GET");
        headers.put("Content-Type", "application/json; charset=utf-8");
        // put authorization token in req header
        headers.put("authorization", TokenOperator.getToken(context));
        return headers;
    }


}
