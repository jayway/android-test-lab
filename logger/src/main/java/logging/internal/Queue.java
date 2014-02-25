
package logging.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.AbstractQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Wrapper of the {@link DequeRunnable} and {@link LinkedBlockingDeque}.
 */
public class Queue {

    private static final LinkedBlockingDeque<LogEntry> mQueue = new LinkedBlockingDeque<LogEntry>();

    private static DequeRunnable mDequeRunnable;

    /**
     * Returns the {@link AbstractQueue} so that the caller may put new
     * {@link LogEntry}'s in it.
     *
     * @return the {@link AbstractQueue}.
     */
    public synchronized static AbstractQueue<LogEntry> getLogQueue() {

        return mQueue;
    }

    /**
     * Creates a instance of the {@link DequeRunnable} if there is none, and
     * starts a new {@link Thread} for running the the {@link DequeRunnable}.
     *
     * @param context used by the {@link DequeRunnable} to gain access to the
     *            {@link SQLiteDatabase}.
     */
    public synchronized static void init(Context context) {
        if (mDequeRunnable == null) {
            // Start the thread that will take LogEntries from the queue
            // and write them to the database.
            mDequeRunnable = new DequeRunnable(context, mQueue);
            Thread worker = new Thread(mDequeRunnable);
            worker.start();
        }

    }

}
