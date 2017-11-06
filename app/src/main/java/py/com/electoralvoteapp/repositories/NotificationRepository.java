package py.com.electoralvoteapp.repositories;

import android.app.Notification;

import java.util.List;

import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.entities.NotificationsDao;
import py.com.electoralvoteapp.utiles.MainSession;
import py.com.electoralvoteapp.utiles.Utiles;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class NotificationRepository {

    public static NotificationsDao getDao() {
        return MainSession.getDaoSession().getNotificationsDao();
    }

    public static List<Notifications> getAll() {
        return getDao().queryBuilder().list();
    }

    public static Notifications getById(long id) {
        return getDao().queryBuilder().where(NotificationsDao.Properties.Id.eq(id)).unique();
    }

    public static long store(Notifications transaction) {
        long id = getDao().insertOrReplace(transaction);
        Utiles.callTransactionLoader(MainSession.getInstance());
        return id;
    }

    public static Notifications getNotificationByNumberTable(int numberTable) {
        return getDao().queryBuilder().where(NotificationsDao.Properties.TabletNumber.eq(numberTable)).unique();
    }
}
