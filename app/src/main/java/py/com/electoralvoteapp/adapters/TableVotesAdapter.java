package py.com.electoralvoteapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.entities.TableVotes;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class TableVotesAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<TableVotes> data = new ArrayList<>();

    public TableVotesAdapter(Context context, int layoutResourceId, List<TableVotes> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.table_votes_description);
            holder.tableVoteStatus = (TextView) row.findViewById(R.id.table_vote_status);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        TableVotes tableVotes = data.get(position);
        holder.imageTitle.setText(tableVotes.getDescription());

        int parseStatus = tableVotes.getComplete() ? 1 : 2;
        switch (parseStatus) {
            case 1:
                holder.tableVoteStatus.setText("Completo");
                break;
            case 2:
                holder.tableVoteStatus.setText("Incompleto");
                break;
        }
        return row;
    }

    public void setData(List<TableVotes> tableVotes) {
        data = new ArrayList<>();
        data.addAll(tableVotes);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView tableVoteStatus;
    }
}

