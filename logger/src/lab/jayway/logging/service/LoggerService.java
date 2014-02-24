
package lab.jayway.logging.service;

import lab.jayway.logging.internal.db.LogDb;
import android.app.IntentService;
import android.content.Intent;

public class LoggerService extends IntentService {

    public static final String SCHEDULED_SERVICE_CALL = "scheduled_service_call";

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogDb logDb = new LogDb(this);
        LogUploader logUploader = new LogUploader(this, logDb);
        boolean startedFromAlarmManager = intent.getBooleanExtra(SCHEDULED_SERVICE_CALL, false);
        
        logUploader.uploadIfNecessary(startedFromAlarmManager);
    }


}
