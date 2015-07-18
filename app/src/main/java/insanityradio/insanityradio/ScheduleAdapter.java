package insanityradio.insanityradio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View textView) {
            super(textView);
        }
    }

    public ScheduleAdapter() {

    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        return new ViewHolder(viewHolderView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1; // Temp
    }
}
