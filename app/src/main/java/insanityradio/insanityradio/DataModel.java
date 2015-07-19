package insanityradio.insanityradio;

import android.content.Context;
import android.provider.ContactsContract;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class DataModel {
    /*
    private static DataModel instance = null;

    private DataModel(Context context) {

    }

    public static DataModel getInstance(Context context) {
        if (instance == null) {
            instance = new DataModel(context);
        }

        return instance;
    }
    */

    public static void updateData(Context context) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, "http://www.insanityradio.com/app.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject schedule = jsonObject.getJSONObject("schedule"); // Then save internally
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(context).getRequestQueue().add(objectRequest);
    }
}
