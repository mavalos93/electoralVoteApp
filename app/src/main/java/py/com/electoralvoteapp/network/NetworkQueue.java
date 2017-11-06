package py.com.electoralvoteapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import py.com.electoralvoteapp.R;


/**
 * Created by diego on 12/10/16.
 * Create the RequestQueue for volley and adds object request in  queue.
 *
 * @Context Android context
 */
public class NetworkQueue {
    private static final String TAG_CLASS = NetworkQueue.class.getName();
    private static DefaultRetryPolicy mDefaultRetryPolicy;
    private final Context mContext;
    private static NetworkQueue mInstance;
    private static RequestQueue mRequestQueue;

    private static final int DEFAULT_REQUEST_TIMEOUT = 60000; //30 Seconds
    private static final int DEFAULT_RETRY_INTENTS = 0; //0 Retry

    public NetworkQueue(Context context) {
        mContext = context;
    }


    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static synchronized NetworkQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkQueue(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }


    public static DefaultRetryPolicy getDefaultRetryPolicy() {

        if (mDefaultRetryPolicy == null) {
            // Create a singleton Default Retry Policy
            // for the requests
            mDefaultRetryPolicy = new DefaultRetryPolicy(DEFAULT_REQUEST_TIMEOUT, DEFAULT_RETRY_INTENTS, 0);
        }

        return mDefaultRetryPolicy;

    }

    public static String handleError(VolleyError error, Context context) {
        NetworkResponse response = error.networkResponse;
        String message = "No message";
        StringBuilder sb = new StringBuilder();
        int statusCode;

        Log.d(TAG_CLASS,"response_error: "+error.getMessage());
        if (response == null) {
            if (error instanceof NoConnectionError) {
                message = context.getResources().getString(R.string.volley_no_connection_error);
            } else if (error instanceof NetworkError) {
                message = context.getResources().getString(R.string.volley_network_error);
            } else if (error instanceof ServerError) {
                message = context.getResources().getString(R.string.volley_server_error);
            } else if (error instanceof AuthFailureError) {
                message = context.getResources().getString(R.string.volley_authentication_error);
            } else if (error instanceof ParseError) {
                message = context.getResources().getString(R.string.volley_parse_error);
            } else if (error instanceof TimeoutError) {
                message = context.getResources().getString(R.string.volley_time_out_error);
            }
        } else {

            try {
                message = new String(response.data);
                Log.d("TAG","RESPOSNSE_MESSAGE: "+message);
                JSONObject jsonObject = new JSONObject(new String(response.data));
                if (jsonObject.has("desRetorno")) {
                    message = jsonObject.getString("desRetorno");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = context.getString(R.string.dialog_error_unexpected) + e.getMessage();
            }
        }
        Log.w(TAG_CLASS, message);
        return message;
    }

}
