package py.com.electoralvoteapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.models.Sync;
import py.com.electoralvoteapp.utiles.Constants;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.SyncViewHolder> {
    private List<Sync> mSyncItem = new ArrayList<>();
    private Context mContext;

    public SyncAdapter(List<Sync> syncs, Context context) {
        mSyncItem = syncs;
        mContext = context;
    }


    class SyncViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mProgressData;
        TextView mTimeFinishedData;
        private CircularProgressView mProgressBar;

        SyncViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.title_sync);
            mProgressData = (TextView) view.findViewById(R.id.message_sync);
            mTimeFinishedData = (TextView) view.findViewById(R.id.time_finished_sync);
            mProgressBar = (CircularProgressView) view.findViewById(R.id.item_sync_progressBar);

        }

        public CircularProgressView getProgressBar() {
            return mProgressBar;
        }

        public TextView getmSyncItemDescription() {
            return mTitle;
        }

        public TextView getmSyncProgressDescription() {
            return mProgressData;
        }

        public TextView getSyncTimeFinish() {
            return mTimeFinishedData;
        }
    }

    @Override
    public SyncAdapter.SyncViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sync_data_item, parent, false);
        return new SyncAdapter.SyncViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SyncAdapter.SyncViewHolder holder, int position) {
        int count = 0;
        Sync sync = mSyncItem.get(position);
        holder.getmSyncItemDescription().setText(sync.getTitle());
        holder.getmSyncProgressDescription().setText(sync.getMessage());
        holder.getSyncTimeFinish().setText((sync.getTimeFinished() == null ? "sin hora" : sync.getTimeFinished()));
        if (sync.getProgress() > 0) {
            if (holder.getProgressBar().isIndeterminate()) {
                holder.getProgressBar().setIndeterminate(false);
                holder.getProgressBar().setMaxProgress(sync.getMax());
            }
            count++;
            holder.getProgressBar().setProgress(sync.getProgress());
        }

        if (sync.isStop()) {
            holder.getProgressBar().setIndeterminate(false);
        }

        if (count == sync.getMax()) {
            Log.d("TAG", "FINISH");
        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public Sync getItemAtPosition(int position) {
        return mSyncItem.get(position);
    }

    @Override
    public int getItemCount() {
        return mSyncItem.size();
    }

    public void setData(List<Sync> data) {
        mSyncItem = new ArrayList<>();
        mSyncItem.addAll(data);
        notifyDataSetChanged();
    }

    public void refresh(final int position, Sync item) {
        mSyncItem.set(position, item);
    }

    public List<Sync> getItems() {
        return mSyncItem;
    }

    public int getItemPosition(Sync i) {
        for (Sync item : mSyncItem) {
            if (item.getType().equalsIgnoreCase(i.getType())) {
                return mSyncItem.indexOf(item);
            }
        }
        return 0;
    }

}
