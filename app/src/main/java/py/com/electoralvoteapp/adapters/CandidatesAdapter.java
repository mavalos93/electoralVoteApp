package py.com.electoralvoteapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.entities.Candidates;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.utiles.Constants;

/**
 * Created by Manu0 on 11/4/2017.
 */

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.CandidatesViewHolder> {
    private List<Candidates> mCandidatesList = new ArrayList<>();
    private Context mContext;
    private OnClickButton mInterface;


    public CandidatesAdapter(List<Candidates> candidates, Context context, OnClickButton onClickInterface) {
        this.mCandidatesList = candidates;
        this.mContext = context;
        this.mInterface = onClickInterface;

    }


    class CandidatesViewHolder extends RecyclerView.ViewHolder {
        TextView mCandidateDescription;
        TextView mVoteValue;
        AppCompatImageButton mAddButton;


        CandidatesViewHolder(View view) {
            super(view);
            mCandidateDescription = (TextView) view.findViewById(R.id.cantidate_description);
            mVoteValue = (TextView) view.findViewById(R.id.votes_value);
            mAddButton = (AppCompatImageButton) view.findViewById(R.id.add_votes_button);


        }
    }

    @Override
    public CandidatesAdapter.CandidatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidate_votes, parent, false);
        return new CandidatesAdapter.CandidatesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CandidatesAdapter.CandidatesViewHolder holder, int position) {
        final Candidates mCandidates = mCandidatesList.get(position);
        holder.mCandidateDescription.setText(mCandidates.getDescription());
        holder.mVoteValue.setText(String.valueOf(mCandidates.getVote()));
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterface.onClickButton(mCandidates);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mCandidatesList.get(position).getId();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public Candidates getItemAtPosition(int position) {
        return mCandidatesList.get(position);
    }

    @Override
    public int getItemCount() {
        return mCandidatesList.size();
    }

    public void setData(List<Candidates> data) {
        mCandidatesList = new ArrayList<>();
        mCandidatesList.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnClickButton {
        void onClickButton(Candidates candidates);
    }

    public int getListCount() {
        int counterVote = 0;
        if (mCandidatesList.size() != 0) {
            for (Candidates candidates : mCandidatesList) {
                counterVote = counterVote + candidates.getVote();
            }
        }
        return counterVote;
    }

}
