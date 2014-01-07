
package lab.jayway.logging.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import lab.jayway.logging.internal.db.DatabaseHelper;
import lab.jayway.logging.internal.db.LogEntryHelper;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.internal.dispatch.LoggDestination;
import lab.jayway.logging.internal.dispatch.Network;
import lab.jayway.logging.util.PhoneInfo;
import lab.jayway.logging.util.SharedPreferencesUtil;

public class LoggerService extends IntentService {

    public static final String SCHEDULED_SERVICE_CALL = "scheduled_service_call";

    private static final long ONE_DAY = 24L * 60 * 60 * 1000;

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String rootUrl = SharedPreferencesUtil.getRootUrl(this);

        PhoneInfo phoneInfo = new PhoneInfo(this);

        LoggDestination destination = new Network(rootUrl);
        Dispatcher dispatcher = new Dispatcher(destination);

        // if automaticServiceCall && db empty -> done, exit
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if (intent.getBooleanExtra(SCHEDULED_SERVICE_CALL, false)) {
            if (LogEntryHelper.isEmpty(db)) {
                // If this was a scheduled start and the database is empty we
                // don't do anything.
                return;
            }
        }

        // if last upload timestamp < 1 day ago -> schedule, exit
        if (System.currentTimeMillis() - LogEntryHelper.getOldestTimestamp(db) < ONE_DAY) {
            scheduleNextAutomaticCheck(LogEntryHelper.getOldestTimestamp(db));
            return;
        }

        // if on wifi -> upload log
        if (phoneInfo.isOnWifi()) {
            dispatcher.dispatch(this);
        } else {
            LogEntryHelper.deleteAllLogEntries(db);
        }
    }

    private void scheduleNextAutomaticCheck(long firstLogEntryTimestamp) {
        long now = System.currentTimeMillis();
        long daysSinceFirstLogEntry = (now - firstLogEntryTimestamp) / ONE_DAY;
        daysSinceFirstLogEntry++;
        long startTime = firstLogEntryTimestamp + daysSinceFirstLogEntry * ONE_DAY;

        Intent intent = new Intent(this, LoggerService.class);
        intent.putExtra(LoggerService.SCHEDULED_SERVICE_CALL, true);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, startTime, pintent);
    }

}
