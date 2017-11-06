package py.com.electoralvoteapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.adapters.SyncAdapter;
import py.com.electoralvoteapp.models.Sync;
import py.com.electoralvoteapp.network.NetworkQueue;
import py.com.electoralvoteapp.recivers.TableVotesObserver;
import py.com.electoralvoteapp.repositories.CandidatesRepository;
import py.com.electoralvoteapp.repositories.TableVotesRepository;
import py.com.electoralvoteapp.utiles.AppPreferences;
import py.com.electoralvoteapp.utiles.Constants;
import py.com.electoralvoteapp.utiles.JsonObjectRequest;
import py.com.electoralvoteapp.utiles.URLS;
import py.com.electoralvoteapp.utiles.Utiles;

public class SyncActivity extends AppCompatActivity {
    private static final String TAG_CLASS = SyncActivity.class.getName();
    private CoordinatorLayout mCoordinator;
    private SyncAdapter mAdapter;
    private Handler handler;
    private FloatingActionButton fab;
    private TextView mSyncMessage;

    // UTILITARIAN VARIABLE
    private boolean inProgress;
    private boolean syncTableVote;
    private boolean syncCandidates;
    private int countServiceToSync;
    private int finishToSync;
    private boolean mAsyncTaskTableVoteStatus = false;
    private boolean mAsyncTaskCandidatesStatus = false;


    // REQUEST
    private JsonObjectRequest tableVotesRequest;
    private JsonObjectRequest candidatesRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.sync_progress_coordinator);
        mSyncMessage = (TextView) findViewById(R.id.status_sync_value);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_sync_progress);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SyncAdapter(new ArrayList<Sync>(), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        handler = new Handler();
        mSyncMessage.setText("Reintentar");
        initSync();

        fab = (FloatingActionButton) findViewById(R.id.fab_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSyncMessage.getText().equals("Listo")) {
                    if (mAsyncTaskTableVoteStatus || mAsyncTaskCandidatesStatus) {
                        Utiles.getToast(SyncActivity.this, "Proceso en ejecución favor espere");
                    } else {
                        startActivity(new Intent(SyncActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    if (mAsyncTaskTableVoteStatus || mAsyncTaskCandidatesStatus) {
                        Utiles.getToast(SyncActivity.this, "Proceso en ejecución favor espere");
                    } else {
                        initSync();
                    }
                }


            }
        });
    }

    private void initSync() {
        setCountServiceToSync(0);
        setFinishToSync(0);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle.containsKey(Sync.TABLE_VOTES)) {
            setSyncTableVote(bundle.getBoolean(Sync.TABLE_VOTES));
            setCountServiceToSync(getCountServiceToSync() + 1);
        }

        if (bundle.containsKey(Sync.CANDIDATES)) {
            setSyncCandidates(bundle.getBoolean(Sync.CANDIDATES));
            setCountServiceToSync(getCountServiceToSync() + 1);
        }
        setDataSync();
    }


    private void setDataSync() {
        List<Sync> syncListItem = new ArrayList<>();

        Sync syncTableVotes = new Sync();
        syncTableVotes.setType(Sync.TABLE_VOTES);
        syncTableVotes.setTitle(getString(R.string.tag_sync_table_votes));
        syncTableVotes.setMessage(getString(R.string.tag_start_sync));

        Sync syncCandidates = new Sync();
        syncCandidates.setType(Sync.CANDIDATES);
        syncCandidates.setTitle(getString(R.string.tag_sync_candidates));
        syncCandidates.setMessage(getString(R.string.tag_start_sync));

        if (isSyncTableVote()) {
            syncListItem.add(syncTableVotes);
        }


        if (isSyncCandidates()) {
            syncListItem.add(syncCandidates);
        }

        Log.d(TAG_CLASS, "LISTA_SYNC: " + syncListItem.toString());
        mAdapter.setData(syncListItem);
        makeRequest();
        setInProgress(true);
    }

    private void makeRequest() {
        String mRequest = "";
        String mUrl = "";

        for (final Sync i : mAdapter.getItems()) {
            if (i.getType().equals(Sync.TABLE_VOTES)) {
                String REQUEST_TAG = "TABLE_VOTES_REQUEST";
                mUrl = URLS.SYNC_TABLES_URL;
                JSONObject params = new JSONObject();
                try {
                    params.put(Constants.JSON_PARAM_USERNAME, AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_USERNAME, null));
                    params.put(Constants.JSON_PARAM_PASSWORD, AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_PASSWORD, null));
                    params.put(Constants.JSON_PARAM_APP, Constants.APP);
                    params.put(Constants.JSON_PARAM_VERSION, Constants.VERSION);
                    params.put(Constants.JSON_PARAM_IMEI, Utiles.getPhoneIMEI(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tableVotesRequest = new JsonObjectRequest(Request.Method.POST, mUrl, String.valueOf(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                handleTableVotesResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                setInProgress(false);
                                String message = Utiles.volleyErrorHandler(error, getApplicationContext());
                                final Sync tableVotes = new Sync();
                                tableVotes.setMax(0);
                                tableVotes.setProgress(0);
                                tableVotes.setStop(true);
                                tableVotes.setType(Sync.TABLE_VOTES);
                                tableVotes.setTitle(getString(R.string.error_unexpected_sync));
                                tableVotes.setMessage(message);
                                final Runnable e = new Runnable() {
                                    public void run() {
                                        mAdapter.refresh(mAdapter.getItemPosition(tableVotes), tableVotes);
                                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(tableVotes));
                                    }
                                };
                                handler.post(e);
                                Utiles.getSnackBar(mCoordinator, message);
                            }
                        }, this
                );
                tableVotesRequest.setRetryPolicy(Utiles.getSyncRetryPolicy());
                tableVotesRequest.setTag(REQUEST_TAG);
                NetworkQueue.getInstance(getApplicationContext()).addToRequestQueue(tableVotesRequest, getApplicationContext());
            } else {
                String REQUEST_TAG = "CANDIDATES_REQUEST";
                mUrl = URLS.SYNC_CANDIDATES_URL;
                JSONObject params = new JSONObject();
                try {
                    params.put(Constants.JSON_PARAM_USERNAME, AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_USERNAME, null));
                    params.put(Constants.JSON_PARAM_PASSWORD, AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_PASSWORD, null));
                    params.put(Constants.JSON_PARAM_APP, Constants.APP);
                    params.put(Constants.JSON_PARAM_VERSION, Constants.VERSION);
                    params.put(Constants.JSON_PARAM_IMEI, Utiles.getPhoneIMEI(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                candidatesRequest = new JsonObjectRequest(Request.Method.POST, mUrl, String.valueOf(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                handleCandidatesResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                setInProgress(false);
                                String message = Utiles.volleyErrorHandler(error, getApplicationContext());
                                final Sync candidatesSync = new Sync();
                                candidatesSync.setMax(0);
                                candidatesSync.setProgress(0);
                                candidatesSync.setStop(true);
                                candidatesSync.setType(Sync.CANDIDATES);
                                candidatesSync.setTitle(getString(R.string.error_unexpected_sync));
                                candidatesSync.setMessage(message);
                                final Runnable e = new Runnable() {
                                    public void run() {
                                        mAdapter.refresh(mAdapter.getItemPosition(candidatesSync), candidatesSync);
                                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesSync));
                                    }
                                };
                                handler.post(e);
                                Utiles.getSnackBar(mCoordinator, message);
                            }
                        }, this
                );
                candidatesRequest.setRetryPolicy(Utiles.getSyncRetryPolicy());
                candidatesRequest.setTag(REQUEST_TAG);
                NetworkQueue.getInstance(getApplicationContext()).addToRequestQueue(candidatesRequest, getApplicationContext());
            }
        }
    }


    private void handleTableVotesResponse(JSONObject response) {
        int status = 0;
        String message = "";


        try {
            Log.d(TAG_CLASS, "RESPONSE: " + response);

            if (response.has("status")) {
                status = response.getInt("status");
                Utiles.validateStatusCode(this, status, message);
            }

            if (response.has("message")) {
                message = response.getString("message");
            }


            if (status != 1) {
                stopService(getString(R.string.tag_sync_table_votes), Sync.TABLE_VOTES, (message.equalsIgnoreCase(null) || message.equalsIgnoreCase("") || message.equalsIgnoreCase("null")) ? getString(R.string.volley_default_error) : message);
                return;
            } else {
                if (response.has("result")) {
                    JSONArray result = response.getJSONArray("result");
                    if (result.length() > 0) {
                        AsyncTaskTablesStorage mTableVotesTask = new AsyncTaskTablesStorage(result);
                        mTableVotesTask.execute();
                    } else {
                        setFinishToSync(getFinishToSync() + 1);
                        stopService(getString(R.string.tag_sync_table_votes), Sync.TABLE_VOTES, getString(R.string.sync_tables_null));
                        setInProgress(false);
                        mSyncMessage.setText("Listo");
                    }
                }

            }
        } catch (Exception e) {
            stopService(getString(R.string.tag_sync_table_votes), Sync.TABLE_VOTES, getString(R.string.volley_parse_error));
            return;
        }
    }


    private void handleCandidatesResponse(JSONObject response) {
        int status = 0;
        String message = "";


        try {
            Log.d(TAG_CLASS, "RESPONSE: " + response);

            if (response.has("status")) {
                status = response.getInt("status");
                Utiles.validateStatusCode(this, status, message);
            }

            if (response.has("message")) {
                message = response.getString("message");
            }


            if (status != 1) {
                stopService(getString(R.string.tag_sync_candidates), Sync.CANDIDATES, (message.equalsIgnoreCase(null) || message.equalsIgnoreCase("") || message.equalsIgnoreCase("null")) ? getString(R.string.volley_default_error) : message);
                return;
            } else {
                if (response.has("result")) {
                    JSONArray result = response.getJSONArray("result");
                    if (result.length() > 0) {
                        AsyncTaskCandidatesStorage mCandidatesTask = new AsyncTaskCandidatesStorage(result);
                        mCandidatesTask.execute();
                    } else {
                        setFinishToSync(getFinishToSync() + 1);
                        stopService(getString(R.string.tag_sync_candidates), Sync.CANDIDATES, getString(R.string.sync_candidates_null));
                        setInProgress(false);
                        mSyncMessage.setText("Listo");
                    }
                }

            }
        } catch (Exception e) {
            stopService(getString(R.string.tag_sync_candidates), Sync.CANDIDATES, getString(R.string.sync_candidates_null));
            return;
        }
    }

    private void stopService(String titleSync, String syncType, String message) {
        setInProgress(false);
        final Sync syncProgress = new Sync();
        syncProgress.setMax(0);
        syncProgress.setProgress(0);
        syncProgress.setType(syncType);
        syncProgress.setTitle(titleSync);
        syncProgress.setStop(true);
        syncProgress.setMessage(new StringBuilder().append(message).toString());
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.refresh(mAdapter.getItemPosition(syncProgress), syncProgress);
                mAdapter.notifyItemChanged(mAdapter.getItemPosition(syncProgress));
            }
        };
        handler.post(r);
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isSyncTableVote() {
        return syncTableVote;
    }

    public void setSyncTableVote(boolean syncTableVote) {
        this.syncTableVote = syncTableVote;
    }

    public boolean isSyncCandidates() {
        return syncCandidates;
    }

    public void setSyncCandidates(boolean syncCandidates) {
        this.syncCandidates = syncCandidates;
    }

    public int getCountServiceToSync() {
        return countServiceToSync;
    }

    public void setCountServiceToSync(int countServiceToSync) {
        this.countServiceToSync = countServiceToSync;
    }

    public int getFinishToSync() {
        return finishToSync;
    }

    public void setFinishToSync(int finishToSync) {
        this.finishToSync = finishToSync;
    }

    private boolean checkSynchronizedItems() {
        if (getCountServiceToSync() != getFinishToSync()) {
            return true;
        }
        setInProgress(false);
        AppPreferences.getAppPreferences(this).edit().putLong(AppPreferences.KEY_CURRENT_DATA_SYNC, new Date().getTime()).apply();
        return false;
    }

    private class AsyncTaskTablesStorage extends AsyncTask<Void, Void, Integer> {
        private int registers = 0;
        private JSONArray array;


        AsyncTaskTablesStorage(JSONArray array) {
            this.array = array;
        }

        @Override
        protected void onPreExecute() {
            TableVotesRepository.clearAll();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            mAsyncTaskTableVoteStatus = true;
            int counter = 0;
            registers = array.length();
            if (TableVotesRepository.count() == 0) { // control para asegurar que la tabla de mesas se limpio entes de insertar los datos
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) array.get(i);
                        long rowId = TableVotesRepository.store(jsonObject);
                        if (rowId > 0) {
                            counter++;
                            final Sync tableVotesProgress = new Sync();
                            tableVotesProgress.setMax(registers);
                            tableVotesProgress.setProgress(counter);
                            tableVotesProgress.setType(Sync.TABLE_VOTES);
                            tableVotesProgress.setTitle(getString(R.string.tag_sync_table_votes));
                            tableVotesProgress.setMessage(new StringBuilder().append(getString(R.string.tag_sync_registers)).append(" ").append(counter).append("/").append(registers).toString());
                            final Runnable r = new Runnable() {
                                public void run() {
                                    mAdapter.refresh(mAdapter.getItemPosition(tableVotesProgress), tableVotesProgress);
                                    mAdapter.notifyItemChanged(mAdapter.getItemPosition(tableVotesProgress));
                                }
                            };
                            handler.post(r);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                setInProgress(false);
                final Sync tableVotesProgress = new Sync();
                tableVotesProgress.setMax(0);
                tableVotesProgress.setProgress(0);
                tableVotesProgress.setType(Sync.TABLE_VOTES);
                tableVotesProgress.setTitle(getString(R.string.tag_sync_table_votes));
                tableVotesProgress.setStop(true);
                tableVotesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(tableVotesProgress), tableVotesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(tableVotesProgress));
                    }
                };
                handler.post(r);
            }

            if (counter < registers) {
                setInProgress(false);
                final Sync tableVotesProgress = new Sync();
                tableVotesProgress.setMax(0);
                tableVotesProgress.setProgress(0);
                tableVotesProgress.setType(Sync.TABLE_VOTES);
                tableVotesProgress.setTitle(getString(R.string.tag_sync_table_votes));
                tableVotesProgress.setStop(true);
                tableVotesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(tableVotesProgress), tableVotesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(tableVotesProgress));
                    }
                };
                handler.post(r);
            }
            return counter;
        }

        @Override
        protected void onPostExecute(Integer count) {
            mAsyncTaskTableVoteStatus = false;
            if (registers == 0) {
                // control para notificar que el caminante ya distribuyo todos sus lotes
                setFinishToSync(getFinishToSync() + 1);
                stopService(getString(R.string.tag_sync_table_votes), Sync.TABLE_VOTES, new StringBuilder().append(getString(R.string.sync_table_votes_complete)).toString());
                setInProgress(false);
                return;
            }

            if (count == registers) {
                mSyncMessage.setText("Listo");
                sendBroadcast(new Intent(TableVotesObserver.ACTION_LOAD_TABLE_VOTES).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                AppPreferences.getAppPreferences(getApplicationContext()).edit().putLong(AppPreferences.KEY_CURRENT_DATA_SYNC, Utiles.getCurrentDate().getTime()).apply();
                fab.setImageResource(R.mipmap.ic_done_white_36dp);
                setInProgress(false);
                final Sync syncTableProgress = new Sync();
                syncTableProgress.setMax(registers);
                syncTableProgress.setProgress(count);
                syncTableProgress.setType(Sync.TABLE_VOTES);
                syncTableProgress.setTimeFinished(Utiles.formatDate(new Date(), Constants.DEFAULT_DATETIME_FORMAT));
                syncTableProgress.setTitle(getString(R.string.tag_sync_table_votes));
                syncTableProgress.setMessage(new StringBuilder().append(getString(R.string.sync_success)).append(" ").append(count).append("/").append(registers).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(syncTableProgress), syncTableProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(syncTableProgress));
                    }
                };
                handler.post(r);
                setFinishToSync(getFinishToSync() + 1);
            } else {
                mSyncMessage.setText("Reintentar");
                setInProgress(false);
                final Sync syncTableProgress = new Sync();
                syncTableProgress.setMax(0);
                syncTableProgress.setProgress(0);
                syncTableProgress.setType(Sync.TABLE_VOTES);
                syncTableProgress.setTitle(getString(R.string.tag_sync_table_votes));
                syncTableProgress.setStop(true);
                syncTableProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(syncTableProgress), syncTableProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(syncTableProgress));
                    }
                };
                handler.post(r);
            }
            super.onPostExecute(count);
        }

    }


    private class AsyncTaskCandidatesStorage extends AsyncTask<Void, Void, Integer> {
        private int registers = 0;
        private JSONArray array;

        AsyncTaskCandidatesStorage(JSONArray array) {
            this.array = array;
        }

        @Override
        protected void onPreExecute() {
            CandidatesRepository.clearAll();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            mAsyncTaskCandidatesStatus = true;
            int counter = 0;
            registers = array.length();
            if (CandidatesRepository.count() == 0) { // control para asegurar que la tabla de mesas se limpio entes de insertar los datos
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) array.get(i);
                        long rowId = CandidatesRepository.store(jsonObject);
                        if (rowId > 0) {
                            counter++;
                            final Sync candidatesProgress = new Sync();
                            candidatesProgress.setMax(registers);
                            candidatesProgress.setProgress(counter);
                            candidatesProgress.setType(Sync.CANDIDATES);
                            candidatesProgress.setTitle(getString(R.string.tag_sync_candidates));
                            candidatesProgress.setMessage(new StringBuilder().append(getString(R.string.tag_sync_registers)).append(" ").append(counter).append("/").append(registers).toString());
                            final Runnable r = new Runnable() {
                                public void run() {
                                    mAdapter.refresh(mAdapter.getItemPosition(candidatesProgress), candidatesProgress);
                                    mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesProgress));
                                }
                            };
                            handler.post(r);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                setInProgress(false);
                final Sync candidatesProgress = new Sync();
                candidatesProgress.setMax(0);
                candidatesProgress.setProgress(0);
                candidatesProgress.setType(Sync.CANDIDATES);
                candidatesProgress.setTitle(getString(R.string.tag_sync_candidates));
                candidatesProgress.setStop(true);
                candidatesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(candidatesProgress), candidatesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesProgress));
                    }
                };
                handler.post(r);
            }

            if (counter < registers) {
                setInProgress(false);
                final Sync candidatesProgress = new Sync();
                candidatesProgress.setMax(0);
                candidatesProgress.setProgress(0);
                candidatesProgress.setType(Sync.CANDIDATES);
                candidatesProgress.setTitle(getString(R.string.tag_sync_candidates));
                candidatesProgress.setStop(true);
                candidatesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(candidatesProgress), candidatesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesProgress));
                    }
                };
                handler.post(r);
            }
            return counter;
        }

        @Override
        protected void onPostExecute(Integer count) {
            mAsyncTaskCandidatesStatus = false;
            if (registers == 0) {
                // control para notificar que el caminante ya distribuyo todos sus lotes
                setFinishToSync(getFinishToSync() + 1);
                stopService(getString(R.string.tag_sync_candidates), Sync.CANDIDATES, new StringBuilder().append(getString(R.string.sync_candidates_complete)).toString());
                setInProgress(false);
                return;
            }

            if (count == registers) {
                mSyncMessage.setText("Listo");
                fab.setImageResource(R.mipmap.ic_done_white_36dp);
                setInProgress(false);
                final Sync candidatesProgress = new Sync();
                candidatesProgress.setMax(registers);
                candidatesProgress.setProgress(count);
                candidatesProgress.setType(Sync.CANDIDATES);
                candidatesProgress.setTimeFinished(Utiles.formatDate(new Date(), Constants.DEFAULT_DATETIME_FORMAT));
                candidatesProgress.setTitle(getString(R.string.tag_sync_candidates));
                candidatesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_success)).append(" ").append(count).append("/").append(registers).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(candidatesProgress), candidatesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesProgress));
                    }
                };
                handler.post(r);
                setFinishToSync(getFinishToSync() + 1);
            } else {
                mSyncMessage.setText("Reintentar");
                setInProgress(false);
                final Sync candidatesProgress = new Sync();
                candidatesProgress.setMax(0);
                candidatesProgress.setProgress(0);
                candidatesProgress.setType(Sync.CANDIDATES);
                candidatesProgress.setTitle(getString(R.string.tag_sync_candidates));
                candidatesProgress.setStop(true);
                candidatesProgress.setMessage(new StringBuilder().append(getString(R.string.sync_failed)).toString());
                final Runnable r = new Runnable() {
                    public void run() {
                        mAdapter.refresh(mAdapter.getItemPosition(candidatesProgress), candidatesProgress);
                        mAdapter.notifyItemChanged(mAdapter.getItemPosition(candidatesProgress));
                    }
                };
                handler.post(r);
            }
            super.onPostExecute(count);
        }

    }
}
