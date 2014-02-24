package lab.jayway.logging.service;

import lab.jayway.logging.internal.db.DatabaseHelper;
import lab.jayway.logging.internal.db.LogEntryHelper;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.internal.dispatch.LoggDestination;
import lab.jayway.logging.internal.dispatch.Network;
import lab.jayway.logging.util.PhoneInfo;
import lab.jayway.logging.util.SharedPreferencesUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class LogUploader {
	
    private static final long ONE_DAY = 24L * 60 * 60 * 1000;

	private final Context context;

	public LogUploader(Context context) {
		this.context = context;
	}
	
	public void uploadIfNecessary(boolean startedFromAlarmManager) {
		String rootUrl = SharedPreferencesUtil.getRootUrl(context);

        PhoneInfo phoneInfo = new PhoneInfo(context);

        LoggDestination destination = new Network(rootUrl);
        Dispatcher dispatcher = new Dispatcher(destination);

        // if automaticServiceCall && db empty -> done, exit
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        		
        if (startedFromAlarmManager) {
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
            dispatcher.dispatch(context);
        } else {
            LogEntryHelper.deleteAllLogEntries(db);
        }
	}
	
    private void scheduleNextAutomaticCheck(long firstLogEntryTimestamp) {
        long now = System.currentTimeMillis();
        long daysSinceFirstLogEntry = (now - firstLogEntryTimestamp) / ONE_DAY;
        daysSinceFirstLogEntry++;
        long startTime = firstLogEntryTimestamp + daysSinceFirstLogEntry * ONE_DAY;

        Intent intent = new Intent(context, LoggerService.class);
        intent.putExtra(LoggerService.SCHEDULED_SERVICE_CALL, true);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, startTime, pintent);
    }


}
