package insanityradio.insanityradio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleAdapter extends SimpleSectionedAdapter<ScheduleAdapter.ViewHolder> {
    private HashMap<String, ArrayList<HashMap<String, String>>> schedule;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View textView) {
            super(textView);
        }
    }

    @Override
    protected ScheduleAdapter.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View viewHolderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        return new ViewHolder(viewHolderView);
    }

    @Override
    protected void onBindItemViewHolder(ScheduleAdapter.ViewHolder holder, int section, int position) {

    }

    @Override
    protected int getSectionCount() {
        if (schedule == null)
            schedule = DataModel.getSchedule(FragmentSchedule.getInstance().getActivity());
        // else
            // Cannot download schedule error message

        if (schedule != null)
            return schedule.size();

        return 0;
    }

    @Override
    protected String getSectionHeaderTitle(int section) {
        String day = dayForSection(section);
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    @Override
    protected int getItemCountForSection(int section) {
        return schedule.get(dayForSection(section)).size();
    }

    private String dayForSection(int section) {
        switch (section) {
            case 0:
                return "monday";
            case 1:
                return "tuesday";
            case 2:
                return "wednesday";
            case 3:
                return "thursday";
            case 4:
                return "friday";
            case 5:
                return "saturday";
            case 6:
                return "sunday";
            default:
                return "";
        }
    }
}
