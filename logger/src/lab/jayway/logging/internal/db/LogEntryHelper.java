
package lab.jayway.logging.internal.db;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.service.textservice.SpellCheckerService.Session;
import android.util.Log;

import java.util.Date;
import java.util.Map;

import lab.jayway.logging.internal.Config;
import lab.jayway.logging.internal.LogEntry;
import lab.jayway.logging.util.CursorUtil;
import lab.jayway.logging.util.ISO8601DateFormat;
import lab.jayway.logging.util.Utils;

/**
 * This class acts as a glue layer between the {@link SQLiteDatabase} and the
 * rest of the logger when manipulating {@link LogEntry} data.
 */
public class LogEntryHelper extends DatabaseHelper {

    /**
     * Key used for time stamping the {@link LogEvent}'s before storing them in
     * the {@link SQLiteDatabase}.
     */
    private static final String FORMATED_TIMESTAMP_KEY = "timestamp";

    private LogEntryHelper(Context context) {
        super(context);
    }

    /**
     * Returns true if the {@link LogEntryTable} contains any rows.
     *
     * @param db the {@link SQLiteDatabase} to insert data in.
     * @return
     */
    public static boolean isEmpty(SQLiteDatabase db) {
        boolean isEmpty = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT count(*) FROM " + LogEntryTable.NAME, null);
            if (cursor.moveToFirst()) {
                isEmpty = cursor.getInt(0) == 0;
            }
        } catch (SQLiteException sQLiteException) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                        sQLiteException);
            }
        }
        CursorUtil.closeSilently(cursor);
        return isEmpty;
    }

    /**
     * Adds a {@link LogEntry} to the {@link SQLiteDatabase}.
     *
     * @param db the {@link SQLiteDatabase} to insert data in.
     * @param logEntry to be written to the {@link SQLiteDatabase}.
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public static int addLogEntry(SQLiteDatabase db, LogEntry logEntry) {
        int result = -1;
        if (logEntry != null && logEntry.getMap().size() > 0
                && logEntry.getMap().get("type") != null) {

            long timeStamp = System.currentTimeMillis();
            String formatedTimeStamp = ISO8601DateFormat.format(new Date(timeStamp));
            logEntry.getMap().put(FORMATED_TIMESTAMP_KEY, formatedTimeStamp);

            Gson gson = new Gson();
            String data = gson.toJson(logEntry.getMap());

            ContentValues values = new ContentValues();
            values.put(LogEntryTable.Columns.DATA, data);
            values.put(LogEntryTable.Columns.TIMESTAMP, timeStamp);

            try {
                result = (int) insert(db, LogEntryTable.NAME, values);
            } catch (SQLiteException sQLiteException) {
                if (Config.DEBUG) {
                    Log.w(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                            sQLiteException);
                }
            }
        }
        return result;
    }

    /**
     * Returns a {@link Cursor} that iterates over all {@link LogEntry}'s.
     *
     * @param db the {@link SQLiteDatabase} to get the {@link Session} from.
     * @return a {@link Cursor} that iterates over all {@link LogEntry}'s.
     */
    public static Cursor getAllLogEntries(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(LogEntryTable.NAME, null, null, null, null, null, null);
        } catch (SQLiteException sQLiteException) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                        sQLiteException);
            }
        }
        return cursor;
    }

    /**
     * Returns the oldest time stamp found in the LogEntry table.
     *
     * @param db the {@link SQLiteDatabase} to get the {@link Session} from.
     * @return the oldest time stamp found in the LogEntry table.
     */
    public static long getOldestTimestamp(SQLiteDatabase db) {
        long oldestTimestamp = Long.MAX_VALUE;
        Cursor cursor = null;
        try {
            String orderBy = LogEntryTable.Columns.TIMESTAMP + " ASC";
            cursor = db.query(LogEntryTable.NAME, new String[] {
                LogEntryTable.Columns.TIMESTAMP
            }, null, null, null, null, orderBy);
            if (cursor.moveToFirst()) {
                int timestampIndex = cursor.getColumnIndex(LogEntryTable.Columns.TIMESTAMP);
                oldestTimestamp = cursor.getLong(timestampIndex);
            }
        } catch (SQLiteException sQLiteException) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                        sQLiteException);
            }
        } finally {
            CursorUtil.closeSilently(cursor);
        }
        if (oldestTimestamp == Long.MAX_VALUE) {
            oldestTimestamp = System.currentTimeMillis();
        }

        return oldestTimestamp;
    }

    /**
     * Picks some json data field from a {@link Cursor}.
     *
     * @param cursor The cursor to pick the json data from.
     * @return a {@link Map} containing the json data found in the cursor
     */
    public static String getDataFromCursor(Cursor cursor) {
        if (!Utils.isEmpty(cursor)) {
            int dataIndex = cursor.getColumnIndex(LogEntryTable.Columns.DATA);
            if (!cursor.isAfterLast() && !cursor.isBeforeFirst()) {
                String data = cursor.getString(dataIndex);
                return data;
            }
        }
        return null;
    }

    /**
     * Convenience method for deleting all rows in the {@link LogEntryTable.}
     *
     * @return the number of rows affected if a whereClause is passed in, 0
     *         otherwise. To remove all rows and get a count pass "1" as the
     *         whereClause.
     */
    public static int deleteAllLogEntries(SQLiteDatabase db) {
        int res = 0;
        try {
            res = db.delete(LogEntryTable.NAME, null, null);
        } catch (SQLiteException sQLiteException) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                        sQLiteException);
            }
        }
        return res;
    }

}
