package insanityradio.insanityradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentSchedule extends Fragment {
    public static FragmentSchedule newInstance() {
        FragmentSchedule fragment = new FragmentSchedule();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        TextView textView = (TextView) view;
        textView.setText("Schedule");
        return view;
    }
}
