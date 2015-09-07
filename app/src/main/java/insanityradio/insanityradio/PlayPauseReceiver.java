package insanityradio.insanityradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayPauseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            FragmentNowPlaying.getInstance().playPauseButtonTapped(false);
        } catch (NullPointerException e) {
            Intent startActivityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(startActivityIntent);
        }
    }
}
