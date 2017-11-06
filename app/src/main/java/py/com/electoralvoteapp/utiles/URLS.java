package py.com.electoralvoteapp.utiles;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class URLS {

    private static final String MAIN_URL = "http://45.55.133.168/mobile";

    // OPERATION URL
    public static final String LOGIN_URL = MAIN_URL + "/auth/signin";
    public static final String CLOSE_TABLE_URL = MAIN_URL + "/votes/close";

    // SYNC DATA URL
    public static final String SYNC_TABLES_URL = MAIN_URL + "/votes/sync-table";
    public static final String SYNC_CANDIDATES_URL = MAIN_URL + "/votes/sync-candidate";

}
