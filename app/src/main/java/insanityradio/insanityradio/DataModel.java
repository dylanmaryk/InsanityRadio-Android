package insanityradio.insanityradio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
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
            Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
            currentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);

            Calendar showTimeCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
            showTimeCalendar.set(Calendar.YEAR, 1982);
            showTimeCalendar.set(Calendar.MONTH, 7);

            int weekdayInt = currentCalendar.get(Calendar.DAY_OF_WEEK);

            showTimeCalendar.set(Calendar.DATE, weekdayInt);

            long showTimeLong = showTimeCalendar.getTimeInMillis() / 1000;

            String dayString = getDayStringForDayInt(weekdayInt);

            ArrayList<HashMap<String, String>> shows = schedule.get(dayString);

            if (shows != null) {
                // Add last show of previous day to shows, in case current show started before midnight

                int weekdayYesterdayInt = weekdayInt - 1;

                if (weekdayYesterdayInt == 0) {
                    weekdayYesterdayInt = 7;
                }

                String dayYesterdayString = getDayStringForDayInt(weekdayYesterdayInt);

                ArrayList<HashMap<String, String>> showsYesterday = schedule.get(dayYesterdayString);

                if (showsYesterday != null && !showsYesterday.isEmpty()) {
                    HashMap<String, String> lastShowYesterday = showsYesterday.get(showsYesterday.size() - 1);

                    shows.add(lastShowYesterday);
                }

                for (HashMap<String, String> show : shows) {
                    try {
                        int startTime = Integer.parseInt(show.get("startTime"));
                        int endTime = Integer.parseInt(show.get("endTime"));

                        // Note: Making assumption that if the last show of the week ends after the end of the week, it ends when the first show of the week begins
                        boolean showEndsAfterEndOfWeek = endTime > 397609200;

                        if ((startTime <= showTimeLong + 1 && endTime > showTimeLong) || showEndsAfterEndOfWeek) {
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
                    } catch (NullPointerException | NumberFormatException e) {
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

    public static HashMap<String, String> getNowPlaying(Context context) {
        String nowPlayingString = getPrefsString(context, "nowPlaying");

        if (nowPlayingString != null) {
            return new Gson().fromJson(nowPlayingString, new TypeToken<HashMap<String, String>>() {}.getType());
        }

        HashMap<String, String> nowPlaying = new HashMap<>();
        nowPlaying.put("song", "");
        nowPlaying.put("artist", "");
        return nowPlaying;
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

        return "I'm listening to Insanity Radio via the Insanity Radio 103.2FM app www.insanityradio.com/listen";
    }

    public static boolean getEnableComment(Context context) {
        return getPrefsBoolean(context, "enableComment", true);
    }

    public static void updateData(final FragmentActivity context) {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, "http://www.insanityradio.com/app.json", null, new Response.Listener<JSONObject>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onResponse(JSONObject jsonObject) {
                SharedPreferences.Editor editor = context.getPreferences(Context.MODE_PRIVATE).edit();

                try {
                    JSONObject nowPlaying = jsonObject.getJSONObject("nowPlaying");

                    editor.putString("nowPlaying", nowPlaying.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                context.invalidateOptionsMenu();
                FragmentNowPlaying.getInstance().updatePlayer();
                FragmentSchedule.getInstance().updateSchedule();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        objectRequest.setRetryPolicy(getRetryPolicy());

        VolleySingleton.getInstance(context).getRequestQueue().add(objectRequest);
    }

    public static DefaultRetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
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
