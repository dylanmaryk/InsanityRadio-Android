package insanityradio.insanityradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentNowPlaying extends Fragment {
    public static FragmentNowPlaying newInstance() {
        FragmentNowPlaying fragment = new FragmentNowPlaying();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DataModel.updateData(getActivity());

        View view = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        return view;
    }
}
