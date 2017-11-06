package py.com.electoralvoteapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.adapters.SyncAdapter;
import py.com.electoralvoteapp.dialogs.ProgressDialogFragment;
import py.com.electoralvoteapp.models.Sync;
import py.com.electoralvoteapp.network.NetworkQueue;
import py.com.electoralvoteapp.network.RequestApp;
import py.com.electoralvoteapp.utiles.AppPreferences;
import py.com.electoralvoteapp.utiles.Constants;
import py.com.electoralvoteapp.utiles.JsonObjectRequest;
import py.com.electoralvoteapp.utiles.URLS;
import py.com.electoralvoteapp.utiles.Utiles;

public class LoginActivity extends AppCompatActivity {
    private final String TAG_CLASS = LoginActivity.class.getName();

    // Coordinator view & Layout
    private CoordinatorLayout mCoordinatorLayout;


    // EditText & Button
    private AppCompatEditText mUsernameInputText;
    private AppCompatEditText mPasswordInputText;
    private Button mLoginButton;


    // Object
    private LoginTask mLoginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean isLogged = AppPreferences.getAppPreferences(this).getBoolean(AppPreferences.KEY_PREFERENCE_LOGGED_IN, false);
        if (isLogged) {
            long now = Utiles.getCurrentDate().getTime();
            long lastSync = AppPreferences.getAppPreferences(this).getLong(AppPreferences.KEY_CURRENT_DATA_SYNC, 0);
            if (Utiles.hoursDifference(new Date(now), new Date(lastSync)) >= 24) {
                Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
                intent.putExtra(Sync.TABLE_VOTES, true);
                intent.putExtra(Sync.CANDIDATES, true);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        } else {
            AppPreferences.getAppPreferences(this).edit().clear().apply();
            setupView();
        }


    }

    private void setupView() {
        mUsernameInputText = (AppCompatEditText) findViewById(R.id.login_username_inputTex);
        mPasswordInputText = (AppCompatEditText) findViewById(R.id.login_password_inputText);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_coordinator_layout);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

    }


    private void validateFields() {

        mUsernameInputText.setError(null);
        mPasswordInputText.setError(null);

        boolean cancel = false;
        View focusView = null;

        String mUserName = mUsernameInputText.getText().toString();
        String mPassword = mPasswordInputText.getText().toString();


        if (TextUtils.isEmpty(mUserName)) {
            mUsernameInputText.setError(getString(R.string.error_required_field));
            cancel = true;
            focusView = mUsernameInputText;
        }

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordInputText.setError(getString(R.string.error_required_field));
            cancel = true;
            focusView = mPasswordInputText;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String imei = Utiles.getPhoneIMEI(this);
            mLoginTask = new LoginTask(mUserName, mPassword, imei);
            mLoginTask.execute();
            cleanView();
        }

    }

    private class LoginTask extends RequestApp {
        public static final String REQUEST_TAG = "LoginTask";

        private String mUsername;
        private String mPassword;
        private String mImei;

        LoginTask(String username, String password, String imei) {
            mUsername = username;
            mPassword = username; // son iguales el user y pass  hasta nuevo aviso
            mImei = imei;
        }

        @Override
        protected void confirm() {

        }

        @Override
        protected void execute() {
            progressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            progressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);


            LoginRequest mLoginRequest = new LoginRequest(mUsername, mPassword, mImei);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLS.LOGIN_URL,
                    mLoginRequest.getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            handleResponse(response);
                            mLoginTask = null;
                            jsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String message = Utiles.volleyErrorHandler(error, LoginActivity.this);
                            jsonObjectRequest.cancel();
                            Utiles.getSnackBar(mCoordinatorLayout, message);
                        }
                    });

            jsonObjectRequest.setRetryPolicy(Utiles.getRetryPolicy());
            jsonObjectRequest.setTag(LoginActivity.LoginTask.REQUEST_TAG);
            NetworkQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest, getApplicationContext());
        }

        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            JSONObject result = null;
            int status = -1;

            if (response == null) {
                Utiles.getSnackBar(mCoordinatorLayout, getString(R.string.volley_parse_error));
                return;
            }

            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status == Constants.DEPRECATED_VERSION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(getString(R.string.label_update_app));
                    builder.setIcon(R.mipmap.ic_store_black_36dp);
                    builder.setMessage(getString(R.string.update_app_message));
                    builder.setPositiveButton(getString(R.string.label_update), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=py.com.unnaki.telecomsales&hl=es")));
                        }
                    });
                    builder.create();
                    builder.setCancelable(false);
                    builder.show();
                    return;
                }

                if (status != Constants.RESPONSE_OK) {
                    Utiles.getSnackBar(mCoordinatorLayout, (message == null) ? getString(R.string.volley_default_error) : message);
                } else {
                    AppPreferences.getAppPreferences(getApplicationContext()).edit().putBoolean(AppPreferences.KEY_PREFERENCE_LOGGED_IN, true).apply();
                    AppPreferences.getAppPreferences(getApplicationContext()).edit().putString(AppPreferences.KEY_USERNAME, mUsername).apply();
                    AppPreferences.getAppPreferences(getApplicationContext()).edit().putString(AppPreferences.KEY_PASSWORD, mPassword).apply();

                    Utiles.getToast(getApplicationContext(), getString(R.string.label_message_welcome));
                    finish();
                    Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
                    intent.putExtra(Sync.TABLE_VOTES, true);
                    intent.putExtra(Sync.CANDIDATES, true);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utiles.getSnackBar(mCoordinatorLayout, getString(R.string.error_parsing_json));
            } catch (Exception ex) {
                ex.printStackTrace();
                Utiles.getSnackBar(mCoordinatorLayout, getString(R.string.volley_default_error));
            }

        }


        class LoginRequest extends RequestObject {

            private final String TAG_CLASS = LoginActivity.LoginTask.LoginRequest.class.getName();

            private String mUserName;
            private String mPassword;
            private String mApp;
            private String mVersion;
            private String mImei;

            public LoginRequest(String username, String password, String imei) {
                mUserName = username;
                mPassword = password;
                mApp = Constants.APP;
                mVersion = Constants.VERSION;
                mImei = imei;

            }

            @Override
            public JSONObject getParams() {
                JSONObject params = new JSONObject();
                try {
                    params.put("username", mUserName);
                    params.put("password", mPassword);
                    params.put("app", mApp);
                    params.put("version", mVersion);
                    params.put("imei", mImei);
                    params.put("createdAt", Utiles.formatDate(new Date(), Constants.DEFAULT_DATE_FORMAT_JSON));

                } catch (JSONException jEX) {
                    Log.w(TAG_CLASS, "Error while create JSONObject " + jEX.getMessage());
                }
                return params;
            }
        }
    }

    public void cleanView() {
        mUsernameInputText.getText().clear();
        mPasswordInputText.getText().clear();
        mUsernameInputText.requestFocus();
    }


}
