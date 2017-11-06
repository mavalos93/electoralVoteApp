package py.com.electoralvoteapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.activities.ChargeDataActivity;
import py.com.electoralvoteapp.activities.MainActivity;
import py.com.electoralvoteapp.adapters.NotificationAdapter;
import py.com.electoralvoteapp.adapters.TableVotesAdapter;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.entities.TableVotes;
import py.com.electoralvoteapp.loaders.NotificationLoader;
import py.com.electoralvoteapp.loaders.TableVotesLoader;
import py.com.electoralvoteapp.repositories.TableVotesRepository;
import py.com.electoralvoteapp.utiles.RecyclerItemClickListener;

/**
 * Created by Manu0 on 10/21/2017.
 */

public class TabletVotesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TableVotes>> {

    private OnItemTableVoteListenerSelected mListener;
    private View rootView;
    private TableVotesAdapter mAdapter;
    private GridView gridView;
    ;
    private CoordinatorLayout coordinatorLayout;


    public TabletVotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransactionFragment.
     */

    public static TabletVotesFragment newInstance() {
        TabletVotesFragment fragment = new TabletVotesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.tablet_votes_fragment, container, false);

        container = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_transaction);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(), ChargeDataActivity.class));

            }
        });
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemTableVoteListenerSelected) {
            mListener = (OnItemTableVoteListenerSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemTableVoteListenerSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<TableVotes>> onCreateLoader(int id, Bundle args) {
        return new TableVotesLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<TableVotes>> loader, List<TableVotes> data) {
        mAdapter = new TableVotesAdapter(getContext(), R.layout.table_votes_item, data);
        gridView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<TableVotes>> loader) {
        mAdapter.setData(new ArrayList<TableVotes>());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemTableVoteListenerSelected {
        // TODO: Update argument type and name
        void onItemTableVoteListenerSelected(TableVotes tableVotes);
    }

}
