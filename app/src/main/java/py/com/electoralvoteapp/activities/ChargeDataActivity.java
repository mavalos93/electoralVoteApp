package py.com.electoralvoteapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.adapters.CandidatesAdapter;
import py.com.electoralvoteapp.dialogs.AlertDialogFragment;
import py.com.electoralvoteapp.dialogs.CancelableDialogFragment;
import py.com.electoralvoteapp.dialogs.ProgressDialogFragment;
import py.com.electoralvoteapp.entities.Candidates;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.network.NetworkQueue;
import py.com.electoralvoteapp.network.RequestApp;
import py.com.electoralvoteapp.repositories.CandidatesRepository;
import py.com.electoralvoteapp.repositories.NotificationRepository;
import py.com.electoralvoteapp.utiles.AppPreferences;
import py.com.electoralvoteapp.utiles.Constants;
import py.com.electoralvoteapp.utiles.JsonObjectRequest;
import py.com.electoralvoteapp.utiles.RecyclerItemClickListener;
import py.com.electoralvoteapp.utiles.URLS;
import py.com.electoralvoteapp.utiles.Utiles;

public class ChargeDataActivity extends AppCompatActivity implements CandidatesAdapter.OnClickButton {

    private final String TAG_CLASS = ChargeDataActivity.class.getName();

    // VIEW
    private TextView mTotalVotes;
    private CoordinatorLayout mCoordinatorLayout;

    //UTILITARIAN VARIABLE
    private CandidatesAdapter mAdapter;

    // OBJECTS
    private Notifications mNotifications;
    private ChargeDataTask mChargeDataTask;
    private long mNotificationId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.charge_data_coordinator_layout);
        mTotalVotes = (TextView) findViewById(R.id.total_votes);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.candidates_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CandidatesAdapter(new ArrayList<Candidates>(), this, this);
        mRecyclerView.setAdapter(mAdapter);
        mTotalVotes.setText("0");
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.getItemAtPosition(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
        setupData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_done_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.id_action_done) {

        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private void setupData() {
        mAdapter.setData(CandidatesRepository.getAll());
    }

    private void showDialog(final Candidates candidates) {
        final Dialog builder = new Dialog(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View contentView = layoutInflater.inflate(R.layout.charge_vote_dialog, null);
        TextView mCandidateDescription = (TextView) contentView.findViewById(R.id.candidate_value);
        final AppCompatEditText mVoteValue = (AppCompatEditText) contentView.findViewById(R.id.vote_edit_text);
        AppCompatButton mAcceptButton = (AppCompatButton) contentView.findViewById(R.id.accept_button);
        AppCompatButton mCancelButton = (AppCompatButton) contentView.findViewById(R.id.cancel_button);
        mCandidateDescription.setText(candidates.getDescription());
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String mVoteValueString = mVoteValue.getText().toString().trim();
                    if (TextUtils.isEmpty(mVoteValueString)) {
                        Utiles.getToast(ChargeDataActivity.this, getString(R.string.error_vote_required));
                        return;
                    }
                    int addVoteCount = Integer.parseInt(mVoteValueString);
                    notifyAdapterChange(candidates, addVoteCount);
                    builder.dismiss();

                } catch (Exception ex) {
                    Utiles.getToast(ChargeDataActivity.this, getString(R.string.error_parsing_number));
                    ex.printStackTrace();
                    return;
                }
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.setContentView(contentView);
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onClickButton(Candidates candidates) {
        showDialog(candidates);
    }

    private void notifyAdapterChange(Candidates candidates, int voteValue) {
        candidates.setVote(voteValue);
        mTotalVotes.setText(String.valueOf(mAdapter.getListCount()));
        mAdapter.notifyDataSetChanged();
    }

    private class ChargeDataTask extends RequestApp {
        public static final String REQUEST_TAG = "ChargeDataTask";
        private String mDataParams;
        private String mImage;

        ChargeDataTask(String data, String image) {
            this.mDataParams = data;
            this.mImage = image;
        }

        @Override
        protected void confirm() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ChargeDataActivity.this);
            builder.setIcon(R.mipmap.ic_info_black_24dp);
            builder.setTitle(R.string.dialog_confirmation_title);
            builder.setMessage(R.string.dialog_confirmation_message);
            builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execute();
                }
            });
            builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mChargeDataTask = null;
                }
            });
            AlertDialog confirmDialog = builder.create();
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.show();
        }

        @Override
        protected void execute() {

            progressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            progressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            long maxTransactionId = NotificationRepository.getDao().count() + 1;
            NoSalesRequest mNosaleRequest = new NoSalesRequest(maxTransactionId, mDataParams, mImage);
            if (mNotifications == null) {
                mNotifications = new Notifications();
                mNotifications.setSendAppDate(Utiles.formatDate(new Date(), Constants.DEFAULT_DATETIME_FORMAT));
                mNotifications.setCreatedAt(Utiles.getToday().getTime());
                mNotifications.setType(Constants.CLOSE_VOTES_TRANSACTION);
                mNotifications.setStatus(Constants.TRANSACTION_NO_SEND);
                mNotifications.setObservation(getApplication().getString(R.string.message_pending_transaction));
                mNotifications.setHttpDetail(String.valueOf(mNosaleRequest.getParams()));
                mNotificationId = NotificationRepository.store(mNotifications);

                try {
                    if (mNotificationId <= 0) {
                        Utiles.getSnackBar(mCoordinatorLayout, getString(R.string.error_save_transaction));
                        progressDialog.dismiss();
                        finish();
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLS.CLOSE_TABLE_URL,
                    mNosaleRequest.getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            handleResponse(response);
                            jsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String message = NetworkQueue.handleError(error, getApplicationContext());
                            Utiles.updateTransaction(mNotificationId, Constants.TRANSACTION_NO_SEND, message);
                            jsonObjectRequest.cancel();
                            CancelableDialogFragment errorDialog = CancelableDialogFragment.newInstance(getString(R.string.dialog_error_title),
                                    message,
                                    getString(R.string.label_retry),
                                    getString(R.string.label_send_queue),
                                    R.mipmap.ic_error_black_24dp);
                            errorDialog.show(getSupportFragmentManager(), CancelableDialogFragment.TAG);
                        }
                    });

            Log.d(TAG_CLASS, "SEND_PARAMS: " + mNosaleRequest.getParams());
            jsonObjectRequest.setRetryPolicy(Utiles.getRetryPolicy());
            jsonObjectRequest.setTag(REQUEST_TAG);
            NetworkQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest, getApplicationContext());

        }

        @Override
        protected void handleResponse(JSONObject response) {

            String message = null;
            int status = -1;
            int mobileId = 0;

            if (response == null) {
                Utiles.updateTransaction(mNotificationId, Constants.TRANSACTION_NO_SEND, getString(R.string.volley_parse_error));
                Utiles.getToast(ChargeDataActivity.this, getString(R.string.volley_parse_error));
                finish();
                return;
            }

            Log.d(TAG_CLASS, "RESPONSE: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");
                if (response.has("mobile_id")) mobileId = response.getInt("mobile_id");

                Utiles.validateStatusCode(ChargeDataActivity.this, status, message);
                if (status != Constants.RESPONSE_OK) {
                    Utiles.updateTransaction(mobileId, Constants.TRANSACTION_NO_SEND, (message == null) ? getString(R.string.volley_default_error) : message);
                    Utiles.getToast(ChargeDataActivity.this, (message == null) ? getString(R.string.volley_default_error) : message);
                    finish();
                } else {
                    Utiles.updateTransaction(mobileId, Constants.TRANSACTION_SEND, message);
                    mNotifications = null;

                    AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.dialog_success_title),
                            message,
                            getString(R.string.label_accept),
                            R.mipmap.ic_done_black_24dp);
                    alertDialogFragment.show(getFragmentManager(), AlertDialogFragment.TAG_CLASS);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Utiles.getSnackBar(mCoordinatorLayout, getString(R.string.error_parsing_json));
            }
        }

        class NoSalesRequest extends RequestObject {
            private final String TAG_CLASS = NoSalesRequest.class.getName();


            private long mMobileId;
            private String mUsername;
            private String mPassword;
            private String mApp;
            private String mVersion;
            private String mImei;
            private String mParams;
            private String mImage;

            NoSalesRequest(long mobileId, String data, String image) {
                mMobileId = mobileId;
                mUsername = AppPreferences.getAppPreferences(getApplicationContext()).getString(AppPreferences.KEY_USERNAME, null);
                mPassword = AppPreferences.getAppPreferences(getApplicationContext()).getString(AppPreferences.KEY_PASSWORD, null);
                mImei = Utiles.getPhoneIMEI(ChargeDataActivity.this);
                mApp = Constants.APP;
                mVersion = Constants.VERSION;
                mParams = data;
                mImage = image;
            }

            @Override
            public JSONObject getParams() {
                JSONObject params = new JSONObject();
                try {
                    params.put("mobile_id", mMobileId);
                    params.put("username", mUsername);
                    params.put("password", mPassword);
                    params.put("app", mApp);
                    params.put("version", mVersion);
                    params.put("imei", mImei);
                    params.put("createdAt", Utiles.formatDate(new Date(), Constants.DEFAULT_DATE_FORMAT_JSON));
                    params.put("params", mParams);
                    params.put("image", mImage);

                } catch (JSONException jEX) {
                    Log.w(TAG_CLASS, "Error while create JSONObject " + jEX.getMessage());
                }
                return params;
            }
        }
    }
}
