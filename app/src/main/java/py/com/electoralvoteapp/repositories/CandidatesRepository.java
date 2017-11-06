package py.com.electoralvoteapp.repositories;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import py.com.electoralvoteapp.entities.Candidates;
import py.com.electoralvoteapp.entities.CandidatesDao;
import py.com.electoralvoteapp.utiles.MainSession;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class CandidatesRepository {


    public static List<Candidates> getAll() {
        return getDao().queryBuilder().list();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static CandidatesDao getDao() {
        return MainSession.getDaoSession().getCandidatesDao();
    }

    public static long count() {
        return getDao().count();
    }


    public static long store(JSONObject candidatesJson) {
        Candidates candidates = new Candidates();
        try {

            if (candidatesJson.has("id")) {
                candidates.setCandidatesId(candidatesJson.getInt("id"));
            }

            if (candidatesJson.has("active")) {
                candidates.setActive(candidatesJson.getBoolean("active"));
            }

            if (candidatesJson.has("description")) {
                candidates.setDescription(candidatesJson.getString("description"));
            }
            if (candidatesJson.has("created_at")) {
                candidates.setCreatedAt(candidatesJson.getString("created_at"));
            }

            if (candidatesJson.has("updated_at")) {
                candidates.setUpdatedAt(candidatesJson.getString("updated_at"));
            }
            candidates.setVote(0); // para mostrar en memoria los votos
            return store(candidates);

        } catch (JSONException jsx) {
            jsx.printStackTrace();
        }
        return 0L;
    }

    public static long store(Candidates candidates) {
        return getDao().insertOrReplace(candidates);
    }

}
