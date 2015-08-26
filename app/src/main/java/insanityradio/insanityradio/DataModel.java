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
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class DataModel {
    public static HashMap<String, String> getCurrentShow(Context context) {
        HashMap<String, String> currentShow = new HashMap<>();

        HashMap<String, ArrayList<HashMap<String, String>>> schedule = getSchedule(context);

        if (schedule != null) {
            Calendar showTimeCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
            showTimeCalendar.set(Calendar.YEAR, 1982);
            showTimeCalendar.set(Calendar.MONTH, 7);

            int weekdayInt = showTimeCalendar.get(Calendar.DAY_OF_WEEK) - 1;

            showTimeCalendar.set(Calendar.DATE, weekdayInt);

            long showTimeLong = showTimeCalendar.getTimeInMillis() / 1000;

            String dayString = getDayStringForDayInt(weekdayInt);

            ArrayList<HashMap<String, String>> day = schedule.get(dayString);

            if (day != null) {
                for (HashMap<String, String> show : day) {
                    try {
                        int startTime = Integer.parseInt(show.get("startTime"));
                        int endTime = Integer.parseInt(show.get("endTime"));

                        if (startTime <= showTimeLong + 1 && endTime > showTimeLong) {
                            String showName = show.get("showName");
                            String showPresenters = show.get("showPresenters");
                            String linkURL = show.get("linkURL");
                            String imageURL = show.get("imageURL");

                            currentShow.put("day", dayString);
                            currentShow.put("name", showName);
                            currentShow.put("presenters", showPresenters);
                            currentShow.put("link", linkURL);
                            currentShow.put("imageURL", imageURL);

                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (currentShow.isEmpty()) {
            currentShow.put("day", "");
            currentShow.put("name", "");
            currentShow.put("presenters", "");
            currentShow.put("link", "");
            currentShow.put("imageURL", "");
        }

        return currentShow;
    }

    private static String getDayStringForDayInt(int day) {
        switch (day) {
            case 1:
                return "sunday";
            case 2:
                return "monday";
            case 3:
                return "tuesday";
            case 4:
                return "wednesday";
            case 5:
                return "thursday";
            case 6:
                return "friday";
            case 7:
                return "saturday";
            default:
                return "";
        }
    }

    public static HashMap<String, ArrayList<HashMap<String, String>>> getSchedule(Context context) {
        String scheduleString = getPrefsString(context, "schedule");

        if (scheduleString != null) {
            return new Gson().fromJson(scheduleString, new TypeToken<HashMap<String, ArrayList<HashMap<String, String>>>>() {}.getType());
        }

        return null;
    }

    public static String getShareText(Context context) {
        String shareTextString = getPrefsString(context, "shareText");

        if (shareTextString != null) {
            return shareTextString;
        }

        // TODO: Determine final default text before release
        return "I'm listening to Insanity Radio via the Insanity Radio 103.2FM app www.insanityradio.com/listen";
    }

    public static boolean getEnableComment(Context context) {
        // TODO: Determine final default value before release
        return getPrefsBoolean(context, "enableComment", true);
    }

    public static void updateData(final FragmentActivity context) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, "http://www.insanityradio.com/app.json", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();

                try {
                    JSONObject schedule = jsonObject.getJSONObject("schedule");

                    editor.putString("schedule", schedule.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    String shareText = jsonObject.getString("shareText");

                    editor.putString("shareText", shareText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    boolean enableComment = jsonObject.getBoolean("enableComment");

                    editor.putBoolean("enableComment", enableComment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                editor.commit();

                ((MainActivity) context).updateUI();
                FragmentSchedule.getInstance().updateSchedule();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(context).getRequestQueue().add(objectRequest);
    }

    private static String getPrefsString(Context context, String key) {
        return getPrefs(context).getString(key, null);
    }

    private static boolean getPrefsBoolean(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    private static SharedPreferences getPrefs(Context context) {
        return ((FragmentActivity) context).getPreferences(Context.MODE_PRIVATE);
    }
}
