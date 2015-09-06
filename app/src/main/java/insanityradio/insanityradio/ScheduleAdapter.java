package insanityradio.insanityradio;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleAdapter extends SimpleSectionedAdapter<ScheduleAdapter.ViewHolder> {
    public HashMap<String, ArrayList<HashMap<String, String>>> schedule;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View viewHolderView;

        public ViewHolder(View viewHolderView) {
            super(viewHolderView);

            this.viewHolderView = viewHolderView;
        }
    }

    public class ItemOnClickListener implements View.OnClickListener {
        public String linkURL;

        public ItemOnClickListener(String linkURL) {
            this.linkURL = linkURL;
        }

        @Override
        public void onClick(View view) {
            if (!linkURL.equals("")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkURL));

                FragmentSchedule.getInstance().getActivity().startActivity(browserIntent);
            }
        }
    }

    @Override
    protected ScheduleAdapter.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View viewHolderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);

        return new ViewHolder(viewHolderView);
    }

    @Override
    protected void onBindItemViewHolder(ScheduleAdapter.ViewHolder holder, int section, int position) {
        HashMap<String, String> show = showForPosition(section, position);

        holder.viewHolderView.findViewById(R.id.show_type).setBackgroundColor(colorForShowType(show.get("showType")));
        ((TextView) holder.viewHolderView.findViewById(R.id.show_clock)).setText(show.get("startClock"));
        ((TextView) holder.viewHolderView.findViewById(R.id.show_name)).setText(show.get("showName"));
        ((TextView) holder.viewHolderView.findViewById(R.id.show_presenters)).setText(show.get("showPresenters"));
        holder.viewHolderView.setOnClickListener(new ItemOnClickListener(show.get("linkURL")));
    }

    @Override
    protected int getSectionCount() {
        if (schedule == null) {
            schedule = DataModel.getSchedule(FragmentSchedule.getInstance().getActivity());
        }

        if (schedule != null) {
            return schedule.size();
        }

        return 0;
    }

    @Override
    protected int getItemCountForSection(int section) {
        if (schedule != null) {
            return schedule.get(dayForSection(section)).size();
        }

        return 0;
    }

    @Override
    protected String getSectionHeaderTitle(int section) {
        String day = dayForSection(section);
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    public int sectionForDay(String day) {
        switch (day) {
            case "monday":
                return 0;
            case "tuesday":
                return 1;
            case "wednesday":
                return 2;
            case "thursday":
                return 3;
            case "friday":
                return 4;
            case "saturday":
                return 5;
            case "sunday":
                return 6;
            default:
                return 0;
        }
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

    private HashMap<String, String> showForPosition(int section, int position) {
        return schedule.get(dayForSection(section)).get(position);
    }

    private int colorForShowType(String showType) {
        switch (showType) {
            case "automated":
                return Color.rgb(170, 170, 170);
            case "entertainment":
                return Color.rgb(255, 192, 0);
            case "specialist":
                return Color.rgb(146, 208, 80);
            case "talk":
                return Color.rgb(102, 204, 255);
            default:
                return Color.rgb(170, 170, 170);
        }
    }
}
