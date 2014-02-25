
package logging.internal.dispatch;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import logging.internal.Config;
import logging.internal.db.DatabaseHelper;
import logging.internal.db.LogEntryHelper;
import logging.util.CursorUtil;
import logging.util.Utils;

/**
 * Reads data from the {@link SQLiteDatabase} and dispatches to the server.
 * After dispatching the data it will empty the database. If the server fails in
 * receiving the data the data will be lost.
 */
public class Dispatcher {

    private final DispatcherHelper mDispatcherHelper;

    public Dispatcher(LoggDestination destination) {
        mDispatcherHelper = new DispatcherHelper(destination);
    }

    /**
     * Reads data from the {@link SQLiteDatabase} and sends it to the server,
     * then deletes the local data.
     *
     * @param context {@link Context} for getting hold of the
     *            {@link SQLiteDatabase}.
     */
    public void dispatch(Context context) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        int sentEntries = sendEntries(context, db);
        if (sentEntries > 0) {
            deleteAllEntries(db);
        }

        db.close();

    }

    private void deleteAllEntries(SQLiteDatabase db) {
        int res = LogEntryHelper.deleteAllLogEntries(db);
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Deleted a total of " + res + " LogEntrys from database.");
        }
    }

    public int sendEntries(Context context, SQLiteDatabase db) {
        int count = 0;
        Cursor cursor = LogEntryHelper.getAllLogEntries(db);
        if (Utils.isEmpty(cursor)) {
            return 0;
        }
        try {
            mDispatcherHelper.prepareForSendingLogEntries(context);
        } catch (IOException e) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Problems connecting to server.", e);
            }
            return 0;
        }
        try {
            do {
                String data = LogEntryHelper.getDataFromCursor(cursor);
                if (data != null) {
                    mDispatcherHelper.sendLogEntry(data, cursor.isLast());
                }
                count++;
            } while (cursor.moveToNext());
            mDispatcherHelper.doneSendingLogEntries();
        } catch (IOException e) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Problems sending entries.", e);
            }
            return 0;
        } catch (SQLException e) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Problems sending entries.", e);
            }
            return 0;
        } finally {
            CursorUtil.closeSilently(cursor);
        }
        return count;
    }
}
