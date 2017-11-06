package py.com.electoralvoteapp.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import py.com.electoralvoteapp.loaders.NotificationLoader;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class NotificationObserver extends BroadcastReceiver {


    public static final String ACTION_LOAD_TRANSACTIONS = "py.com.electoralvoteapp.LOAD_NOTIFICATIONS";
    private NotificationLoader mLoader;

    public NotificationObserver(NotificationLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_TRANSACTIONS);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
