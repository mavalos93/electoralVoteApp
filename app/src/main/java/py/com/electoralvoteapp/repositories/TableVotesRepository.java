package py.com.electoralvoteapp.repositories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.entities.TableVotes;
import py.com.electoralvoteapp.entities.TableVotesDao;
import py.com.electoralvoteapp.utiles.MainSession;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class TableVotesRepository {

    public static TableVotesDao getDao() {
        return MainSession.getDaoSession().getTableVotesDao();
    }

    public static List<TableVotes> getAll() {
        return getDao().queryBuilder().list();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static long count() {
        return getDao().count();
    }

    public static long store(JSONObject tableVotesJson) {
        TableVotes tableVotes = new TableVotes();
        try {

            if (tableVotesJson.has("id")) {
                tableVotes.setTableVotesId(tableVotesJson.getInt("id"));
            }

            if (tableVotesJson.has("completed")) {
                tableVotes.setComplete(tableVotesJson.getBoolean("completed"));
            }

            if (tableVotesJson.has("description")) {
                tableVotes.setDescription(tableVotesJson.getString("description"));
            }
            if (tableVotesJson.has("number_table")) {
                tableVotes.setNumberTable(tableVotesJson.getInt("number_table"));
            }
            return store(updateTableStatus(tableVotes));

        } catch (JSONException jsx) {
            jsx.printStackTrace();
        }
        return 0L;
    }

    public static long store(TableVotes tableVotes) {
        return getDao().insertOrReplace(tableVotes);
    }

    public static TableVotes updateTableStatus(TableVotes tableVotes) {
        if (tableVotes != null) {
            Notifications notifications = NotificationRepository.getNotificationByNumberTable(tableVotes.getNumberTable());
            if (notifications != null) {
                tableVotes.setComplete(true);
            }
        }
        return tableVotes;
    }

    private static TableVotes mapStock(JSONObject tableVotesJson) {
        TableVotes tableVotes = new TableVotes();
        try {

            if (tableVotesJson.has("id")) {
                tableVotes.setTableVotesId(tableVotesJson.getInt("id"));
            }

            if (tableVotesJson.has("completed")) {
                tableVotes.setComplete(tableVotesJson.getBoolean("completed"));
            }

            if (tableVotesJson.has("description")) {
                tableVotes.setDescription(tableVotesJson.getString("description"));
            }
            if (tableVotesJson.has("number_table")) {
                tableVotes.setNumberTable(tableVotesJson.getInt("number_table"));
            }

        } catch (JSONException jsx) {
            jsx.printStackTrace();
        }
        return tableVotes;
    }
}
