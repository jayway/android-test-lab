
package lab.jayway.logging.internal.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import lab.jayway.logging.internal.Config;

public class SqliteLogDb implements LogDb {

    private final Context mContext;

    private SQLiteDatabase mDb;

    public SqliteLogDb(Context context) {
        mContext = context;
    }

    @Override
    public void open() {
        mDb = new DatabaseHelper(mContext).getWritableDatabase();
    }

    @Override
    public void close() {
        mDb.close();
        mDb = null;
    }

    @Override
    public boolean isEmpty() {
        return LogEntryHelper.isEmpty(mDb);
    }

    @Override
    public long getOldestTimeStamp() {
        return LogEntryHelper.getOldestTimestamp(mDb);
    }

    @Override
    public boolean isOldestTimeStampInTheFuture() {
        return isEmpty() || getOldestTimeStamp() >= System.currentTimeMillis();
    }

    @Override
    public void dropAll() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Delete all LogEntries.");
        }
        LogEntryHelper.deleteAllLogEntries(mDb);
    }

}
