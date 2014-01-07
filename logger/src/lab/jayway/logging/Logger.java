
package lab.jayway.logging;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.AbstractQueue;

import lab.jayway.logging.internal.LogEntry;
import lab.jayway.logging.internal.Queue;
import lab.jayway.logging.service.LoggerService;
import lab.jayway.logging.util.SharedPreferencesUtil;

/**
 * Logger is used to log messages from a client to the logging server.
 */
public class Logger {

    /**
     * Creates and returns a {@link LogEntry.Builder} that in turn can be used
     * to create a {@link LogEntry} and send it to the {@link AbstractQueue}.
     *
     * @param type the type of the log entry to create. The types are completely
     *            client specific.
     * @return the {@link LogEntry.Builder} that will create a {@link LogEntry}
     *         when asked to.
     */
    public LogEntry.Builder log(String type) {
        return new LogEntry.Builder(Queue.getLogQueue()).with("type", type);
    }

    /**
     * Initialize the {@link Logger} and its {@link AbstractQueue}, keeps a
     * reference to the context, the context should preferable be an
     * ApplicationContext.
     *
     * @param context The {@link Context} used by the {@link AbstractQueue} for
     *            {@link SQLiteDatabase} access.
     * @param rootUrl the rootUrl.
     */
    public void init(Context context, String rootUrl) {
        SharedPreferencesUtil.setRootUrl(context, rootUrl);
        Queue.init(context);
        startService(context);
    }

    private void startService(Context context) {
        Intent msgIntent = new Intent(context, LoggerService.class);
        context.startService(msgIntent);
    }

}
