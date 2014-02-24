package lab.jayway.logging.service;

import lab.jayway.logging.internal.db.LogDb;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.util.PhoneInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class LogUploader {

	public enum Action {
		NOTHING, UPLOAD, SCHEDULE, CLEAR_DB
	};

	private static final long ONE_DAY = 24L * 60 * 60 * 1000;

	private final Context context;

	private LogDb logDb;

	private final PhoneInfo phoneInfo;

	private final Dispatcher dispatcher;

	public LogUploader(Context context, LogDb logDb, PhoneInfo phoneInfo,
			Dispatcher dispatcher) {
		this.context = context;
		this.logDb = logDb;
		this.phoneInfo = phoneInfo;
		this.dispatcher = dispatcher;
	}

	public Action findAction(boolean startedFromAlarmManager) {
		logDb.open();

		// if automaticServiceCall && db empty -> done, exit
		if (startedFromAlarmManager) {
			if (logDb.isEmpty()) {
				// If this was a scheduled start and the database is empty we
				// don't do anything.
				logDb.close();
				return Action.NOTHING;
			}
		}

		// if last upload timestamp < 1 day ago -> schedule, exit
		if (System.currentTimeMillis() - logDb.getOldestTimeStamp() < ONE_DAY) {
			// scheduleNextAutomaticCheck(logDb.getOldestTimeStamp());
			logDb.close();
			return Action.SCHEDULE;
		}

		// if on wifi -> upload log
		if (phoneInfo.isOnWifi()) {
			logDb.close();
			// dispatcher.dispatch(context);
			return Action.UPLOAD;
		} else {
			logDb.close();
			// logDb.dropAll();
			return Action.CLEAR_DB;
		}
	}

	void upload() {
		dispatcher.dispatch(context);
	}

	void clearDb() {
		logDb.open();
		logDb.dropAll();
		logDb.close();
	}

	void scheduleNextAutomaticCheck() {
		logDb.open();
		long firstLogEntryTimestamp = logDb.getOldestTimeStamp();
		logDb.close();
		long now = System.currentTimeMillis();
		long daysSinceFirstLogEntry = (now - firstLogEntryTimestamp) / ONE_DAY;
		daysSinceFirstLogEntry++;
		long startTime = firstLogEntryTimestamp + daysSinceFirstLogEntry
				* ONE_DAY;

		Intent intent = new Intent(context, LoggerService.class);
		intent.putExtra(LoggerService.SCHEDULED_SERVICE_CALL, true);
		PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);

		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC, startTime, pintent);
	}

}
