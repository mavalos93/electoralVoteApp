package py.com.electoralvoteapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Manu0 on 11/5/2017.
 */

public class CancelableDialogFragment  extends DialogFragment {

    public static final String TAG = CancelableDialogFragment.class.getName();

    private CancelableDialogFragment.CancelableAlertDialogFragmentListener mListener;

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON = "positiveButton";
    private static final String ARG_NEGATIVE_BUTTON = "negativeButton";
    private static final String ARG_ICON_RESOURCES = "iconResource";


    public interface CancelableAlertDialogFragmentListener {
        void onCancelableAlertDialogPositiveClick(DialogFragment dialog);

        void onCancelableAlertDialogNegativeClick(DialogFragment dialog);
    }

    public static CancelableDialogFragment newInstance(String title, String message, String positiveButton, String negativeButton, int iconResource) {
        CancelableDialogFragment frag = new CancelableDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        args.putString(ARG_NEGATIVE_BUTTON, negativeButton);
        args.putInt(ARG_ICON_RESOURCES, iconResource);
        frag.setArguments(args);
        frag.setCancelable(false);
        return frag;
    }


    public static CancelableDialogFragment newInstance(String title, String message, String positiveButton, String negativeButton) {
        CancelableDialogFragment frag = new CancelableDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON, positiveButton);
        args.putString(ARG_NEGATIVE_BUTTON, negativeButton);
        frag.setArguments(args);
        frag.setCancelable(false);
        return frag;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        //noinspection deprecation
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CancelableDialogFragment.CancelableAlertDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement CancelableAlertDialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title;
        String message;
        String positiveButton;
        String negativeButton;
        int iconResource;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        if (getArguments().containsKey(ARG_TITLE)) {
            title = getArguments().getString(ARG_TITLE);
            builder.setTitle(title);
        }

        if (getArguments().containsKey(ARG_MESSAGE)) {
            message = getArguments().getString(ARG_MESSAGE);
            builder.setMessage(message);
        }

        if (getArguments().containsKey(ARG_POSITIVE_BUTTON)) {
            positiveButton = getArguments().getString(ARG_POSITIVE_BUTTON);
            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onCancelableAlertDialogPositiveClick(CancelableDialogFragment.this);
                }
            });
        }

        if (getArguments().containsKey(ARG_NEGATIVE_BUTTON)) {
            negativeButton = getArguments().getString(ARG_NEGATIVE_BUTTON);
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onCancelableAlertDialogNegativeClick(CancelableDialogFragment.this);
                }
            });
        }

        if (getArguments().containsKey(ARG_ICON_RESOURCES)) {
            iconResource = getArguments().getInt(ARG_ICON_RESOURCES);
            builder.setIcon(iconResource);
        }

        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }
}
