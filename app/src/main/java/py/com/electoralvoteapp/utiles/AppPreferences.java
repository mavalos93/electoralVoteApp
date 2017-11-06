package py.com.electoralvoteapp.utiles;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class AppPreferences {

    private static SharedPreferences mAppPreferences;
    private static final String SHARED_PREFERENCE_APP = "SHARED_PREFERENCE_APP";
    public static final String KEY_TOKEN_PREFERENCE = "KEY_TOKEN";
    public static final String KEY_USERNAME = "KEY_USERNAME";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_PREFERENCE_LOGGED_IN = "KEY_LOGGED_IN";
    public static final String KEY_CREATE_DB = "KEY_CREATE_DB";
    public static final String KEY_CURRENT_DATA_SYNC = "KEY_SYNC";

    public static SharedPreferences getAppPreferences(Context context) {
        if (mAppPreferences == null) {
            mAppPreferences = context.getSharedPreferences(SHARED_PREFERENCE_APP, Context.MODE_PRIVATE);
        }
        return mAppPreferences;
    }

}

