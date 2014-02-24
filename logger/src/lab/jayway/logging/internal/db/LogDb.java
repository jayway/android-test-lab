
package lab.jayway.logging.internal.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import lab.jayway.logging.internal.Config;

public class LogDb {

    private final Context mContext;

    private SQLiteDatabase mDb;

    public LogDb(Context context) {
        mContext = context;
    }

    public void open() {
        mDb = new DatabaseHelper(mContext).getWritableDatabase();
    }

    public void close() {
        mDb.close();
        mDb = null;
    }

    public boolean isEmpty() {
        return LogEntryHelper.isEmpty(mDb);
    }

    public long getOldestTimeStamp() {
        return LogEntryHelper.getOldestTimestamp(mDb);
    }

    public boolean isOldestTimeStampInTheFuture() {
        return isEmpty() || getOldestTimeStamp() >= System.currentTimeMillis();
    }

    public void dropAll() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Delete all LogEntries.");
        }
        LogEntryHelper.deleteAllLogEntries(mDb);
    }

}
