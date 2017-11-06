package py.com.electoralvoteapp.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import py.com.electoralvoteapp.loaders.TableVotesLoader;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class TableVotesObserver extends BroadcastReceiver {


    public static final String ACTION_LOAD_TABLE_VOTES = "py.com.electoralvoteapp.LOAD_TABLE_VOTES";
    private TableVotesLoader mLoader;

    public TableVotesObserver(TableVotesLoader loader) {
        mLoader = loader;
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_LOAD_TABLE_VOTES);
        mLoader.getContext().registerReceiver(this, mFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}
