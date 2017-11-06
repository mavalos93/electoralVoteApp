package py.com.electoralvoteapp.utiles;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import py.com.electoralvoteapp.entities.DaoMaster;
import py.com.electoralvoteapp.entities.DaoSession;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class MainSession extends Application {
    public DaoSession daoSession;
    private static MainSession INSTANCE = null;
    private JobManager jobManager;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        setupDatabase();
        configureJobManager();

    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "votesdb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static MainSession getInstance() {
        return INSTANCE;
    }

    public static DaoSession getDaoSession() {
        return getInstance().daoSession;
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return false;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(1)//up to 3 consumers at a time
                .loadFactor(2)//3 jobs per consumer
                .consumerKeepAlive(10)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }
}
