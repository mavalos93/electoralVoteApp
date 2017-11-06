package py.com.electoralvoteapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.adapters.NotificationAdapter;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.loaders.NotificationLoader;
import py.com.electoralvoteapp.utiles.RecyclerItemClickListener;

/**
 * Created by Manu0 on 10/21/2017.
 */

public class NotificationFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Notifications>> {

    private OnItemNotificationsListenerSelected mListener;
    private View rootView;
    private NotificationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;


    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransactionFragment.
     */

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
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
        rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notification_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        container = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_transaction);
        mAdapter = new NotificationAdapter(new ArrayList<Notifications>(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.onItemNotificationsListenerSelected(mAdapter.getItemAtPosition(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

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
        if (context instanceof OnItemNotificationsListenerSelected) {
            mListener = (OnItemNotificationsListenerSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemNotificationsListenerSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Notifications>> onCreateLoader(int id, Bundle args) {
        return new NotificationLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Notifications>> loader, List<Notifications> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Notifications>> loader) {
        mAdapter.setData(new ArrayList<Notifications>());
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
    public interface OnItemNotificationsListenerSelected {
        // TODO: Update argument type and name
        void onItemNotificationsListenerSelected(Notifications transactions);
    }

}
