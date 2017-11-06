package py.com.electoralvoteapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import dmax.dialog.SpotsDialog;
import py.com.electoralvoteapp.R;

/**
 * Created by Diego on 9/23/2017.
 */

public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG_CLASS = ProgressDialogFragment.class.getName();
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    public static ProgressDialogFragment newInstance(Context context) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, context.getString(R.string.prompt_process_title));
        args.putString(ARG_MESSAGE, context.getString(R.string.prompt_process_message));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        SpotsDialog dialog = new SpotsDialog(getActivity(), R.style.MyCutomDialog);
        if (getArguments().containsKey(ARG_TITLE)) {
            title = getArguments().getString(ARG_TITLE);
            dialog.setTitle(title);
        }

        if (getArguments().containsKey(ARG_MESSAGE)) {
            message = getArguments().getString(ARG_MESSAGE);
            dialog.setMessage(message);
        }

        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}
