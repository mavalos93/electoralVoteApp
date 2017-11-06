package py.com.electoralvoteapp.network;

import org.json.JSONObject;

import py.com.electoralvoteapp.dialogs.ProgressDialogFragment;
import py.com.electoralvoteapp.utiles.JsonObjectRequest;


/**
 * Created by diego on 18/10/16.
 */

public abstract class RequestApp {

    protected ProgressDialogFragment progressDialog;
    protected JsonObjectRequest jsonObjectRequest;


    protected abstract void handleResponse(JSONObject response);

    protected abstract void confirm();

    protected abstract void execute();

    public abstract class RequestObject {
        public abstract JSONObject getParams();
    }

    public JsonObjectRequest getMomoJsonObjectRequest() {
        return jsonObjectRequest;
    }

}
