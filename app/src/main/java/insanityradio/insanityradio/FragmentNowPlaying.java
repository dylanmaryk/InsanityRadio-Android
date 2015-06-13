package insanityradio.insanityradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentNowPlaying extends Fragment {
    public static FragmentNowPlaying newInstance() {
        FragmentNowPlaying fragment = new FragmentNowPlaying();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        TextView textView = (TextView) view;
        textView.setText("Now Playing");
        return view;
    }
}
