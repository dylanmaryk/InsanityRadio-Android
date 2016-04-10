package insanityradio.insanityradio;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import co.mobiwise.library.RadioListener;
import co.mobiwise.library.RadioManager;

public class FragmentNowPlaying extends Fragment implements RadioListener {
    private static FragmentNowPlaying instance;

    private Bitmap defaultImage;
    private Bitmap previousNotificationLargeIcon;
    private RadioManager radioManager;
    private HashMap<String, String> currentShow;
    private HashMap<String, String> nowPlaying;
    private boolean cancelNotificationOnStop;
    private ImageButton playPauseButton;
    private TextView currentShowTextView;
    private TextView nowPlayingTextView;
    private ImageView albumArtImageView;

    public static FragmentNowPlaying getInstance() {
        if (instance == null) {
            instance = new FragmentNowPlaying();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        defaultImage = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.insanity_icon);
        radioManager = RadioManager.with(getActivity());
        radioManager.connect();
        radioManager.registerListener(this);

        View view = inflater.inflate(R.layout.fragment_nowplaying, container, false);

        playPauseButton = (ImageButton) view.findViewById(R.id.play_pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseButtonTapped(true);
            }
        });
        currentShowTextView = (TextView) view.findViewById(R.id.current_show);
        nowPlayingTextView = (TextView) view.findViewById(R.id.now_playing);
        albumArtImageView = (ImageView) view.findViewById(R.id.album_art);

        Calendar currentCalendar = Calendar.getInstance();

        int millisecondsUntilNextHour = (3600 * 1000) - (currentCalendar.get(Calendar.MINUTE) * 60 * 1000) - (currentCalendar.get(Calendar.SECOND) * 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCurretShowTimer();
            }
        }, millisecondsUntilNextHour);

        updateCurrentShow();

        return view;
    }

    public void updatePlayer() {
        updateCurrentShow();

        nowPlaying = DataModel.getNowPlaying(getActivity());

        String nowPlayingArtist = nowPlaying.get("artist");
        String nowPlayingSong = nowPlaying.get("song");

        nowPlayingTextView.setText(nowPlayingArtist + "\n" + nowPlayingSong);

        String url = "http://ws.audioscrobbler.com/2.0/?method=track.getinfo&api_key=" + getString(R.string.LASTFM_API_KEY) + "&artist=" + nowPlayingArtist + "&track=" + nowPlayingSong + "&autocorrect&format=json";
        url = url.replaceAll(" ", "%20");

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                updateImageWithResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayCurrentShowImage();
            }
        });
        objectRequest.setRetryPolicy(DataModel.getRetryPolicy());

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(objectRequest);

        // Updates notification sooner, before image is retrieved, but means notification is updated twice
        displayNotification(previousNotificationLargeIcon);
    }

    private void updateCurrentShow() {
        currentShow = DataModel.getCurrentShow(getActivity());

        String currentShowTextViewText = currentShow.get("name");
        String currentShowPresenters = currentShow.get("presenters");

        if (!currentShowPresenters.equals("")) {
            currentShowTextViewText += "\nwith " + currentShowPresenters;
        }

        currentShowTextView.setText(currentShowTextViewText);
    }

    private void updateImageWithResponse(JSONObject jsonObject) {
        try {
            JSONObject track = (JSONObject) jsonObject.get("track");
            JSONObject album = (JSONObject) track.get("album");

            JSONArray images = (JSONArray) album.get("image");

            for (int i = 0; i < images.length(); i++) {
                JSONObject image = (JSONObject) images.get(i);

                if (image.get("size").equals("extralarge")) {
                    updateImageWithURL((String) image.get("#text"));

                    return;
                }
            }
        } catch (JSONException | ClassCastException e) {
            e.printStackTrace();
        }

        displayCurrentShowImage();
    }

    private void updateImageWithURL(String imageURL) {
        ImageRequest imageRequest = new ImageRequest(imageURL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                displayFinalImage(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                displayCurrentShowImage();
            }
        });
        imageRequest.setRetryPolicy(DataModel.getRetryPolicy());

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(imageRequest);
    }

    private void displayCurrentShowImage() {
        ImageRequest imageRequest = new ImageRequest(currentShow.get("imageURL"), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                displayFinalImage(bitmap);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                displayDefaultImage();
            }
        });
        imageRequest.setRetryPolicy(DataModel.getRetryPolicy());

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(imageRequest);
    }

    private void displayFinalImage(Bitmap bitmap) {
        albumArtImageView.setImageBitmap(bitmap);

        displayNotification(bitmap);
    }

    private void displayDefaultImage() {
        albumArtImageView.setImageBitmap(defaultImage);

        displayNotification(null);
    }

    public void playPauseButtonTapped(boolean cancelNotification) {
        cancelNotificationOnStop = cancelNotification;

        if (radioManager.isPlaying()) {
            pauseRadio();
        } else {
            playRadio();
        }
    }

    private void playRadio() {
        playPauseButton.setEnabled(false);
        playPauseButton.setAlpha(0.5f);
        radioManager.startRadio("http://stream.insanityradio.com:8000/insanity320.mp3");
    }

    private void pauseRadio() {
        radioPaused();

        radioManager.stopRadio();
    }

    private void radioPaused() {
        playPauseButton.setEnabled(true);
        playPauseButton.setAlpha(1.0f);
        playPauseButton.setImageResource(R.drawable.play);
        nowPlayingTextView.setText("");
    }

    @Override
    public void onRadioConnected() {
        playRadio();
    }

    @Override
    public void onRadioStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playPauseButton.setEnabled(true);
                playPauseButton.setAlpha(1.0f);
                playPauseButton.setImageResource(R.drawable.stop);
            }
        });
    }

    @Override
    public void onRadioStopped() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                radioPaused(); // Called twice if radio stopped by user
                displayDefaultImage();
            }
        });
    }

    @Override
    public void onMetaDataReceived(String s, String s1) {
        if (s != null && s.equals("StreamTitle")) {
            DataModel.updateData(getActivity());
        }
    }

    @Override
    public void onPlayerException(Throwable throwable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                radioPaused();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Cannot Stream Insanity")
                        .setMessage("There was a problem streaming Insanity Radio. Please check your Internet connection.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void displayNotification(Bitmap largeIconBitmap) {
        if (Build.VERSION.SDK_INT < 16) {
            return;
        }

        previousNotificationLargeIcon = largeIconBitmap;

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (!radioManager.isPlaying() && cancelNotificationOnStop) {
            notificationManager.cancel(1);
        } else {
            String contentTitle;
            String contentText;
            String actionTitle;

            int actionIcon;

            if (radioManager.isPlaying()) {
                contentTitle = nowPlaying.get("song");
                contentText = currentShow.get("name");
                actionTitle = "Stop";
                actionIcon = R.drawable.stop;
            } else {
                contentTitle = "Insanity Radio";
                contentText = "103.2FM";
                actionTitle = "Play";
                actionIcon = R.drawable.play;
            }

            Intent playPauseIntent = new Intent(getActivity(), PlayPauseReceiver.class);
            Intent openAppIntent = new Intent(getActivity(), MainActivity.class);

            PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent openAppPendingIntent = PendingIntent.getActivity(getActivity(), 0, openAppIntent, 0);

            Notification.Builder notificationBuilder = new Notification.Builder(getActivity())
                    .setSmallIcon(R.drawable.ic_headphone)
                    .setLargeIcon(largeIconBitmap)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setContentIntent(openAppPendingIntent)
                    .setOngoing(radioManager.isPlaying());

            if (Build.VERSION.SDK_INT >= 17) {
                notificationBuilder.setShowWhen(false);
            }

            if (Build.VERSION.SDK_INT >= 21) {
                MediaMetadata.Builder mediaMetadataBuilder = new MediaMetadata.Builder();
                mediaMetadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, largeIconBitmap);

                MediaSession mediaSession = new MediaSession(getActivity(), "insanityradio");
                mediaSession.setActive(true);
                mediaSession.setMetadata(mediaMetadataBuilder.build());

                notificationBuilder
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setStyle(new Notification.MediaStyle()
                                .setMediaSession(mediaSession.getSessionToken())
                                .setShowActionsInCompactView(0))
                        .setColor(Color.BLACK);
            }

            if (Build.VERSION.SDK_INT >= 23) {
                Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(getActivity(), actionIcon), actionTitle, playPausePendingIntent).build();

                notificationBuilder.addAction(action);
            } else {
                notificationBuilder.addAction(actionIcon, actionTitle, playPausePendingIntent);
            }

            notificationManager.notify(1, notificationBuilder.build());
        }

        cancelNotificationOnStop = false;
    }

    private void startCurretShowTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCurrentShow();
                    }
                });
            }
        };

        new Timer().schedule(timerTask, 0, 3600 * 1000);
    }
}
