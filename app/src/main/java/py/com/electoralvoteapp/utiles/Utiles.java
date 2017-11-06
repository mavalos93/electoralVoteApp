package py.com.electoralvoteapp.utiles;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.IntentCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.activities.LoginActivity;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.recivers.NotificationObserver;
import py.com.electoralvoteapp.repositories.NotificationRepository;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class Utiles {


    public static boolean checkNetworkConnection(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    public static String formatDate(Date date, String format) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        result = sdf.format(date);
        return result;
    }

    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        Date today = new Date(cal.getTimeInMillis());
        return today;
    }

    public static int hoursDifference(Date date1, Date date2) {
        long fecha_1 = date1.getTime();
        long fecha_2 = date2.getTime();

        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        long test = fecha_1 - fecha_2;
        double diff = Math.floor(test / MILLI_TO_HOUR);
        return (int) diff;
    }

    public static String getPhoneIMEI(Context mContext) {

        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String id = tm.getDeviceId();
        String imei = "";

        if (id == null) {
            id = "not available";
        }

        int phoneType = tm.getPhoneType();
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                imei = "NONE: " + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                imei = "GSM: IMEI=" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                imei = "CDMA: MEID/ESN=" + id;

            case TelephonyManager.PHONE_TYPE_SIP:
                imei = "SIP: " + id;
            /*
             * for API Level 11 or above case TelephonyManager.PHONE_TYPE_SIP:
			 * return "SIP";
			 */
            default:
                imei = "UNKNOWN: ID=" + id;
        }
        return id;

    }

    public static void updateTransaction(long mNotificationId, int status, String message) {

        Notifications mNotifications = NotificationRepository.getById(mNotificationId);
        if (mNotifications != null) {
            mNotifications.setUpdatedAt(new Date());
            mNotifications.setStatus(status);
            mNotifications.setObservation(message);
            NotificationRepository.store(mNotifications);
        }
    }


    public static DefaultRetryPolicy getSyncRetryPolicy() {

        int TIME_OUT_MS = 90000;

        return new DefaultRetryPolicy(
                TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 0 Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    }

    public static DefaultRetryPolicy getRetryPolicy() {
        int TIME_OUT_MS = 15000;


        return new DefaultRetryPolicy(
                TIME_OUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 0 Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static void getToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void callTransactionLoader(Context context) {
        context.sendBroadcast(new Intent(NotificationObserver.ACTION_LOAD_TRANSACTIONS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static String volleyErrorHandler(Object err, Context context) {
        VolleyError error = (VolleyError) err;
        NetworkResponse response = error.networkResponse;
        String message = "No message";
        if (response == null) {
            if (error instanceof NetworkError) {
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_network_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_network_error);
            } else if (error instanceof ServerError) {
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_server_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_server_error);
            } else if (error instanceof AuthFailureError) {
                expireSession(context, message);
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_auth_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_auth_error);
            } else if (error instanceof ParseError) {
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_parse_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_parse_error);
            } else if (error instanceof NoConnectionError) {
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_no_connection_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_no_connection_error);
            } else if (error instanceof TimeoutError) {
                //message = (error.getMessage() == null) ? context.getResources().getString(R.string.volley_time_out_error) : error.getMessage();
                message = context.getResources().getString(R.string.volley_time_out_error);
            }
        } else {
            try {
                message = new String(response.data);
                JSONObject jsonObject = new JSONObject(new String(response.data));
                if (response.statusCode == Constants.AUTH_ERROR_CODE) {
                    message = context.getString(R.string.error_invalid_credentials);
                } else if (response.statusCode == Constants.TOKEN_EXPIRED_CODE) {
                    message = context.getString(R.string.error_expired_session);
                    Utiles.expireSession(context, message);
                }
                if (jsonObject.has("message")) {
                    message = jsonObject.getString("message");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = context.getString(R.string.dialog_error_unexpected) + e.getMessage();
            }
        }
        return message;
    }

    public static void expireSession(Context context, String message) {
        // Clear login data
        AppPreferences.getAppPreferences(context).edit().clear().apply();
        //cancelExecutors();
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK));
    }
    /*public static void cancelExecutors() {
        TokenExecutor.cancel();
        TransactionQueueExecutor.cancel();
    }*/

    public static void getSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                snackbar.dismiss();
            }
        };
        snackbar.setAction(R.string.label_close, clickListener);
        snackbar.show();
    }

    public static void validateStatusCode(Context mContext, int status, String message) {
        boolean outSession = false;
        switch (status) {
            case Constants.TOKEN_EXPIRED_CODE:
                outSession = true;
                break;
            case Constants.AUTH_ERROR_CODE:
                outSession = true;
                break;
        }
        if (outSession) {
            expireSession(mContext, message);
        }
    }

    public static void addTextChangeListener(TextInputEditText txtMonto) {
        txtMonto.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                if (s != null && !TextUtils.isEmpty(s.toString())) {
                    String str = s.toString().replaceAll("[^\\d]", "");
                    double s1 = Double.parseDouble(str);
                    DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("es"));
                    dfs.setDecimalSeparator('.');
                    DecimalFormat df = new DecimalFormat("###,###.###", dfs);
                    s.replace(0, s.length(), df.format(s1));
                }
                isEditing = false;
            }
        });
    }

    public static Date getToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();
        return today;
    }


}
