package insanityradio.insanityradio;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    public static HashMap getSchedule(Context context) {
        String scheduleString = getPrefsString(context, "schedule");

        try {
            HashMap<String, ArrayList<HashMap<String, String>>> scheduleMap = new Gson().fromJson(scheduleString, new TypeToken<HashMap<String, ArrayList<HashMap<String, String>>>>() {}.getType());
            return scheduleMap;
        } catch (JSONException e) {
            return null;
        }
    }

    public static void updateData(final FragmentActivity context) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, "http://www.insanityradio.com/app.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject schedule = jsonObject.getJSONObject("schedule");

                    SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putString("schedule", schedule.toString());
                    editor.commit();

                    FragmentSchedule.getInstance().updateSchedule();
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

    public static String getPrefsString(Context context, String key) {
        return ((FragmentActivity) context).getPreferences(Context.MODE_PRIVATE).getString(key, "0");
    }
}
