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


public class JsonFetcher {

    NetworkResultCallback networkResultCallback = null;
    Context context;
    private Map<String, String> header;
    private Map<String, Object> params;

    public JsonFetcher(NetworkResultCallback resultCallback, Context context){
        networkResultCallback = resultCallback;
        this.context = context;
        header = new HashMap<>();
        params = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

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
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-HTTP-Method-Override","PATCH");
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", TokenOperator.getToken(context));
                    return headers;
                }};

            queue.add(jsonObj);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

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
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-HTTP-Method-Override","POST");
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    //headers.put("x-api-key", "YOUR API KEY");
                    headers.put("authorization", TokenOperator.getToken(context));

                    return headers;
                }};

            queue.add(jsonObj);

        }catch(Exception e){

        }
    }

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
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-HTTP-Method-Override","GET");
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    //headers.put("x-api-key", "YOUR API KEY");
                    headers.put("authorization", TokenOperator.getToken(context));
                    return headers;
                }};

            queue.add(jsonObject);

        }catch(Exception e){

        }
    }

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
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-HTTP-Method-Override","GET");
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    //headers.put("x-api-key", "YOUR API KEY");
                    headers.put("authorization", TokenOperator.getToken(context));
                    return headers;
                }};

            queue.add(jsonObject);

        }catch(Exception e){

        }
    }


}
