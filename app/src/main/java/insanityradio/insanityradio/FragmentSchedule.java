package insanityradio.insanityradio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

public class FragmentSchedule extends Fragment {
    private static FragmentSchedule instance;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;

    public static FragmentSchedule getInstance() {
        if (instance == null) {
            instance = new FragmentSchedule();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        scheduleAdapter = new ScheduleAdapter();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(scheduleAdapter);

        scrollToToday();

        return view;
    }

    public void updateSchedule() {
        if (recyclerView != null) {
            scheduleAdapter = new ScheduleAdapter();
            recyclerView.swapAdapter(scheduleAdapter, true);
        }

        scrollToToday();
    }

    private void scrollToToday() {
        HashMap<String, String> currentShow = DataModel.getCurrentShow(getActivity());

        String today = null;

        if (currentShow != null) {
            today = currentShow.get("day");
        }

        if (today != null && linearLayoutManager != null) {
            int sectionToday = scheduleAdapter.sectionForDay(today);
            int itemToday = 0;

            for (int section = 0; section < sectionToday; section++) {
                itemToday += scheduleAdapter.getItemCountForSection(section) + 1;
            }

            linearLayoutManager.scrollToPosition(itemToday);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && scheduleAdapter != null && scheduleAdapter.schedule == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Cannot Download Schedule")
                    .setMessage("There was a problem downloading the schedule. Please check your Internet connection.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
