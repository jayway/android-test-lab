
package lab.jayway.logging.internal;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lab.jayway.logging.util.Utils;

/**
 * Definition of a entry in the Captain's log.
 */
public class LogEntry {

    private final Map<String, String> mMap = new HashMap<String, String>();

    /**
     * Gets the map that holds this {@link LogEntry}'s data.
     *
     * @return the Map.
     */
    public Map<String, String> getMap() {
        return mMap;
    }

    /**
     * Add a new key/value pair to the {@link LogEntry}
     *
     * @param key The key of the pair.
     * @param value The Value of the pair.
     */
    public void addKeyValuePair(String key, String value) {
        mMap.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = mMap.keySet();
        for (String key : keySet) {
            sb.append(key);
            sb.append(": ");
            sb.append(mMap.get(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Builds new {@link LogEntry}'s.
     */
    public static class Builder {

        private final LogEntry mLogEntry;

        private final AbstractQueue<LogEntry> mQueue;

        /**
         * Constructs a new {@link Builder} to use for creating a
         * {@link LogEntry}.
         *
         * @param queue The {@link AbstractQueue} that the {@link LogEntry}
         *            should be put on when send() is called.
         */
        public Builder(AbstractQueue<LogEntry> queue) {
            mLogEntry = new LogEntry();
            mQueue = queue;
        }

        /**
         * Adds a key/value pair to the {@link LogEntry}.
         *
         * @param key the Key.
         * @param value the Value.
         * @return The builder, for further building using this method.
         */
        public Builder with(String key, String value) {
            if (!Utils.isEmpty(key) && !Utils.isEmpty(value)) {
                mLogEntry.addKeyValuePair(key, value);
            } else if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Trying to log with empty key or value");
            }
            return this;
        }

        /**
         * Push out the {@link LogEntry} to the {@link AbstractQueue}, the
         * {@link LogEntry} will eventually be taken from the
         * {@link AbstractQueue} and written to the {@link SQLiteDatabase} where
         * it will stay until the {@link Service} decides that its time to send
         * the {@link LogEntry} to the server.
         */
        public void submit() {
            mQueue.add(mLogEntry);
        }

    }
}
