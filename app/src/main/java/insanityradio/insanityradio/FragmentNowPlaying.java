package insanityradio.insanityradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.mobiwise.library.RadioListener;
import co.mobiwise.library.RadioManager;

public class FragmentNowPlaying extends Fragment implements RadioListener {
    private static FragmentNowPlaying instance;

    private RadioManager radioManager;

    public static FragmentNowPlaying getInstance() {
        if (instance == null) {
            instance = new FragmentNowPlaying();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        radioManager = RadioManager.with(getActivity());
        radioManager.connect();
        radioManager.registerListener(this);

        View view = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        return view;
    }

    @Override
    public void onRadioConnected() {
        radioManager.startRadio("http://stream.insanityradio.com:8000/insanity320.mp3");
    }

    @Override
    public void onRadioStarted() {

    }

    @Override
    public void onRadioStopped() {

    }

    @Override
    public void onMetaDataReceived(String s, String s1) {
        DataModel.updateData(getActivity());
    }
}
