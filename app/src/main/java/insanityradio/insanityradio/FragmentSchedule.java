package insanityradio.insanityradio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSchedule extends Fragment {
    private static FragmentSchedule instance = null;

    private RecyclerView recyclerView;

    public static FragmentSchedule getInstance() {
        if (instance == null) {
            instance = new FragmentSchedule();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateSchedule();

        return view;
    }

    public void updateSchedule() {
        recyclerView.setAdapter(new ScheduleAdapter(getActivity()));
    }
}
