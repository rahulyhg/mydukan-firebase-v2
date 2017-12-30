package org.app.mydukan.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MyDukan on 13-11-2017.
 */

public class VolleyNetworkRequest {

    RequestQueue requestQueue;
    Context context;
    String url = "https://fcm.googleapis.com/fcm/send";
    String key = "AAAAYFkNNDI:APA91bGDYM9qVODYNO4GHzb4IQ1N677p573lREFZxizfGu2Db1DLgongOsHuD-Dc7SbR-q1M529JUKb9I-H9xOh8kW-a48e26X3UuKiaypE4KUeWVRCQeYmMgGMhyhQfnoQZA2OqhhIs7pI_31KufClZZmIw6PT0HQ";

        public VolleyNetworkRequest(Context context){
            requestQueue = Volley.newRequestQueue(context);
            this.context = context;
        }

        public void JsonObjectRequest(String token, final String message, final String type, final String feedId){

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("to", token);
//                jsonObject.put("notification", object);
                if(feedId != null) {
                    JSONObject body = new JSONObject();
                    body.put("feedId", feedId);
                    body.put("title", "MyDukan Notification");
                    switch (type){
                        case "like":
                            body.put("message", message + " has liked your post");
                            break;
                        case "comment":
                            body.put("message", message + " has commented on your post");
                            break;
                        case "follow":
                            body.put("message", message + " is now following you");
                            break;
                        case "post":
                            body.put("message", message + "has posted in MyDukan");
                    }
                    jsonObject.put("data", body);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // TODO Auto-generated method stub
                    if(response.has("success")){
                        try {
                            if(response.getInt("success") == 1){
                                if(type.equalsIgnoreCase("like")) {
//                                    Toast.makeText(context, "Liked " + message + " post.", Toast.LENGTH_SHORT).show();
                                }
                                else if(type.equalsIgnoreCase("comment")){
//                                    Toast.makeText(context, "Commented on "+message+" post.", Toast.LENGTH_SHORT).show();
                                }
                                else if(type.equalsIgnoreCase("follow")){
                                    Toast.makeText(context, "Following the user", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                try {
                                    if(response.has("results")) {
                                        JSONArray errorArray = response.getJSONArray("results");
                                        if(errorArray.length() > 0) {
                                            JSONObject errorObject = errorArray.getJSONObject(0);
                                            if (errorObject.has("error")) {
                                                String error = errorObject.getString("error");
                                                Log.e("VolleyNetworkRequest Error ", error);

                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.e("VolleyNeworkResponse: ",error.toString());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "key="+key);
                params.put("Content-Type", "application/json;");

                return params;
            }
            };
            requestQueue.add(jsObjRequest);
        }

}
