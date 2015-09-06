package insanityradio.insanityradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlayPauseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FragmentNowPlaying.getInstance().playPauseButtonTapped(false);
    }
}
