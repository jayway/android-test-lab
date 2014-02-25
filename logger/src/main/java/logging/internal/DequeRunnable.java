
package logging.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.concurrent.LinkedBlockingDeque;

import logging.internal.db.DatabaseHelper;
import logging.internal.db.LogEntryHelper;

/**
 * A {@link Runnable} that takes data ({@link LogEntry})'s from the
 * {@link LinkedBlockingDeque} and inserts in the database.
 */
public class DequeRunnable implements Runnable {

    private final Context mContext;

    private final LinkedBlockingDeque<LogEntry> mQueue;

    public DequeRunnable(Context context, LinkedBlockingDeque<LogEntry> queue) {
        mContext = context;
        mQueue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                writeLogEntryToDatabase(mQueue.take());
            } catch (InterruptedException e) {
                if (Config.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeLogEntryToDatabase(LogEntry logEntry) {
        SQLiteDatabase db = new DatabaseHelper(mContext).getWritableDatabase();
        LogEntryHelper.addLogEntry(db, logEntry);
        db.close();
    }

}
