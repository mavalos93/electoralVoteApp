package py.com.electoralvoteapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.utiles.Constants;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notifications> mNotificationList = new ArrayList<>();
    private Context mContext;

    public NotificationAdapter(List<Notifications> notificationses, Context context) {
        mNotificationList = notificationses;
        mContext = context;
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView mNotificationType;
        TextView mTabletDescription;
        TextView mObservationDescription;
        TextView mHourNotification;
        ImageView mStatusIcon;

        NotificationViewHolder(View view) {
            super(view);
            mNotificationType = (TextView) view.findViewById(R.id.notification_type_text_view);
            mTabletDescription = (TextView) view.findViewById(R.id.table_vote_value);
            mObservationDescription = (TextView) view.findViewById(R.id.notification_message_value);
            mHourNotification = (TextView) view.findViewById(R.id.notification_hour_text_view);
            mStatusIcon = (ImageView) view.findViewById(R.id.notification_status);
        }
    }

    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);
        return new NotificationAdapter.NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.NotificationViewHolder holder, int position) {
        Notifications mNotifications = mNotificationList.get(position);
        int iconResource;

        String transactionType = "N/A";
        holder.mTabletDescription.setText(mNotifications.getTabletNumber());
        holder.mObservationDescription.setText(mNotifications.getSendAppDate());
        holder.mObservationDescription.setText(mNotifications.getObservation());

        if (mNotifications.getStatus() == Constants.TRANSACTION_NO_SEND) {
            iconResource = R.mipmap.ic_error_black_24dp;
            holder.mObservationDescription.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        } else {
            iconResource = R.mipmap.ic_done_black_24dp;
            holder.mObservationDescription.setTextColor(ContextCompat.getColor(mContext, R.color.colorStatusOK));
        }
        holder.mStatusIcon.setBackgroundResource(iconResource);

        switch (mNotifications.getType()) {
            case Constants.CLOSE_VOTES_TRANSACTION:
                transactionType = mContext.getString(R.string.tag_close_tablet);
                break;
        }

        holder.mNotificationType.setText(transactionType);
    }

    @Override
    public long getItemId(int position) {
        return mNotificationList.get(position).getId();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public Notifications getItemAtPosition(int position) {
        return mNotificationList.get(position);
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public void setData(List<Notifications> data) {
        mNotificationList = new ArrayList<>();
        mNotificationList.addAll(data);
        notifyDataSetChanged();
    }
}