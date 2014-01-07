
package lab.jayway.logging.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import lab.jayway.logging.internal.Config;
import lab.jayway.logging.internal.db.LogDb;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.util.PhoneInfo;
import lab.jayway.logging.util.SharedPreferencesUtil;

/**
 * {@link LoggerScheduler} helps with deciding whether it's time to upload data
 * to the server or schedule the service to run at a later time.
 */
public class LoggerScheduler {

    public enum Action {
        NOOP, CLEAR_DB, SCHEDULE, UPLOAD
    };

    private final LogDb mDb;

    private final PhoneInfo mPhoneInfo;

    private final Dispatcher mDispatcher;

    private final Context mContext;

    public LoggerScheduler(Context context, LogDb db, PhoneInfo phoneInfo, Dispatcher dispatcher) {
        mContext = context;
        mDb = db;
        mPhoneInfo = phoneInfo;
        mDispatcher = dispatcher;
    }

    public Action getAction(boolean startedByAlarmManager) {
        Action result;

        mDb.open();

        if (startedByAlarmManager && mDb.isEmpty()) {
            result = Action.NOOP;
        } else if (mDb.isOldestTimeStampInTheFuture()) {
            result = Action.CLEAR_DB;
        } else if (timeToUploadOnWifi() && mPhoneInfo.isOnWifi()) {
            result = Action.UPLOAD;
        } else if (timeToUploadOnMobileNetwork() && mPhoneInfo.isRoaming()) {
            result = Action.CLEAR_DB;
        } else if (timeToUploadOnMobileNetwork() && !mPhoneInfo.isRoaming()) {
            result = Action.UPLOAD;
        } else {
            result = Action.SCHEDULE;
        }

        mDb.close();
        return result;
    }

    private boolean timeToUploadOnMobileNetwork() {
        long firstLogEntryTimestamp = mDb.getOldestTimeStamp();
        long now = System.currentTimeMillis();
        long timeSinceFirstLogEntry = now - firstLogEntryTimestamp;

        long maxWaitForWIFI = SharedPreferencesUtil.getMaxWaitForWIFI(mContext);
        return timeSinceFirstLogEntry > maxWaitForWIFI;
    }

    private boolean timeToUploadOnWifi() {
        long firstLogEntryTimestamp = mDb.getOldestTimeStamp();
        long now = System.currentTimeMillis();
        long timeSinceFirstLogEntry = now - firstLogEntryTimestamp;

        long minWaitWhenOnWIFI = SharedPreferencesUtil.getMinWaitWhenOnWIFI(mContext);

        return timeSinceFirstLogEntry > minWaitWhenOnWIFI;
    }

    public void perform(Action action) {
        switch (action) {
            case CLEAR_DB:
                clearDb();
                scheduleNextAutomaticCheck();
                break;
            case UPLOAD:
                uploadData();
                scheduleNextAutomaticCheck();
                break;
            case SCHEDULE:
                scheduleNextAutomaticCheck();
                break;
            default: // NOOP
                noop();
                break;
        }
    }

    private void noop() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Nothing to do.");
        }
    }

    private void uploadData() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Uploading data");
        }
        mDispatcher.dispatch(mContext);
    }

    private void clearDb() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Clearing database");
        }
        mDb.open();
        mDb.dropAll();
        mDb.close();
    }

    private void scheduleNextAutomaticCheck() {
        mDb.open();
        long firstLogEntryTimestamp = mDb.getOldestTimeStamp();
        mDb.close();

        long wifiPeriod = SharedPreferencesUtil.getMinWaitWhenOnWIFI(mContext);
        long now = System.currentTimeMillis();

        long periodsSinceFirstLogEntry = (now - firstLogEntryTimestamp) / wifiPeriod;
        periodsSinceFirstLogEntry++;
        long startTime = firstLogEntryTimestamp + periodsSinceFirstLogEntry * wifiPeriod;

        Intent intent = new Intent(mContext, LoggerService.class);
        intent.putExtra(LoggerService.SCHEDULED_SERVICE_CALL, true);
        PendingIntent pintent = PendingIntent.getService(mContext, 0, intent, 0);

        AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, startTime, pintent);
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Scheduled next automatick check at: " + startTime);
        }
    }
}
