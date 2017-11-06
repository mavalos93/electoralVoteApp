package py.com.electoralvoteapp.utiles;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class Constants {

    public static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT_JSON = "yyyy-MM-dd HH:mm:ss";


    // RESPONSES CODES
    public static final int RESPONSE_OK = 1;
    public static final int AUTH_ERROR_CODE = 401;
    public static final int TOKEN_EXPIRED_CODE = 409;

    //JSON PARAMS
    public static final String JSON_PARAM_USERNAME = "username";
    public static final String JSON_PARAM_IMEI = "imei";
    public static final String JSON_PARAM_PASSWORD = "password";
    public static final String JSON_PARAM_APP = "app";
    public static final String JSON_PARAM_VERSION = "version";

    //APP CONSTANTS
    public static final String APP = "vote";
    public static final String VERSION = "1.0";
    public static final int DEPRECATED_VERSION = -1;

    //Transaction Status
    public static final int TRANSACTION_SEND = 1;
    public static final int TRANSACTION_NO_SEND = 2;

    // OPERATIONS TYPE
    public static final String CLOSE_VOTES_TRANSACTION = "CIERRE DE MESA";
}
