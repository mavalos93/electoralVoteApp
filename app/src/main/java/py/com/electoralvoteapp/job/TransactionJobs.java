package py.com.electoralvoteapp.job;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.network.NetworkQueue;
import py.com.electoralvoteapp.repositories.NotificationRepository;
import py.com.electoralvoteapp.utiles.Constants;
import py.com.electoralvoteapp.utiles.JsonObjectRequest;
import py.com.electoralvoteapp.utiles.URLS;
import py.com.electoralvoteapp.utiles.Utiles;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class TransactionJobs extends Job {
    public static final int PRIORITY = 1;
    private static final int RETRIES_LIMIT = 1;
    private final String TAG_CLASS = TransactionJobs.class.getName();

    private long transactionId;
    private Notifications transaction;
    private NetworkQueue queue;
    private String logTag;
    private String errorMessage;

    public TransactionJobs(long transactionId) {
        super(new Params(PRIORITY).setRequiresNetwork(false));
        this.transactionId = transactionId;
        queue = NetworkQueue.getInstance(getApplicationContext());
    }

    @Override
    public void onAdded() {

        transaction = NotificationRepository.getById(this.transactionId);
        if (transaction != null) {
            transaction.setQueued(1);
            NotificationRepository.store(transaction);
        }
    }

    @Override
    public void onRun() throws Throwable {

        if (transaction != null) {
            logTag = (transaction.getType() + "Job").toUpperCase();


            RequestFuture<JSONObject> future = RequestFuture.newFuture();

            JsonObjectRequest momoJsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST,
                            getUrl(), transaction.getHttpDetail(),
                            future,
                            future, getApplicationContext());

            momoJsonObjectRequest.setRetryPolicy(Utiles.getRetryPolicy());
            momoJsonObjectRequest.setTag(logTag);
            NetworkQueue.getInstance(getApplicationContext()).addToRequestQueue(momoJsonObjectRequest, getApplicationContext());
            try {
                handleResponse(future.get(30, TimeUnit.SECONDS));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected int getRetryLimit() {
        return TransactionJobs.RETRIES_LIMIT;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        errorMessage = NetworkQueue.handleError((VolleyError) throwable.getCause(), getApplicationContext());
        Log.w(logTag, (errorMessage != null) ? errorMessage : "Null message");
        return RetryConstraint.RETRY;
    }

    @Override
    protected void onCancel() {
        transaction.setStatus(Constants.TRANSACTION_NO_SEND);
        transaction.setObservation(errorMessage);
        NotificationRepository.store(transaction);
    }

    protected int handleResponse(JSONObject response) throws Exception {

        int status = -1;

        if (response == null) {
            return status;
        }

        Log.i(logTag, "Response: " + response.toString());

        try {


            if (response.has("status"))
                status = response.getInt("status");


            Notifications mTransaction = NotificationRepository.getById(this.transactionId);
            if (mTransaction.getStatus().equals(Constants.TRANSACTION_SEND))
                return status;

            if (status == Constants.RESPONSE_OK) {
                transaction.setStatus(Constants.TRANSACTION_SEND);
                if (response.has("message"))
                    transaction.setObservation(response.getString("message"));
                NotificationRepository.store(transaction);
                Log.i(logTag, "Transactions updated");
            } else {
                transaction.setStatus(Constants.TRANSACTION_NO_SEND);
                if (response.has("message"))
                    transaction.setObservation(response.getString("message"));
                NotificationRepository.store(transaction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    public String getUrl() {
        String mUrl = "";
        switch (transaction.getType()) {
            case Constants.CLOSE_VOTES_TRANSACTION:
                mUrl = URLS.CLOSE_TABLE_URL;
                break;
        }
        return mUrl;
    }
}
