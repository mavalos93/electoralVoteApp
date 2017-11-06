package py.com.electoralvoteapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import py.com.electoralvoteapp.R;

/**
 * Created by Manu0 on 10/21/2017.
 */

public class MainFragment extends Fragment {

    // View
    private View rootView;

    public MainFragment(){

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
            rootView = inflater.inflate(R.layout.main_fragment, container, false);
            return rootView;
        }


        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onDetach() {
            super.onDetach();
        }

    }
