
package lab.jayway.logging.service;

import lab.jayway.logging.internal.db.LogDb;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.internal.dispatch.LoggDestination;
import lab.jayway.logging.internal.dispatch.Network;
import lab.jayway.logging.util.PhoneInfo;
import lab.jayway.logging.util.SharedPreferencesUtil;
import android.app.IntentService;
import android.content.Intent;

public class LoggerService extends IntentService {

    public static final String SCHEDULED_SERVICE_CALL = "scheduled_service_call";

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
		String rootUrl = SharedPreferencesUtil.getRootUrl(this);

        PhoneInfo phoneInfo = new PhoneInfo(this);

        LoggDestination destination = new Network(rootUrl);
        Dispatcher dispatcher = new Dispatcher(destination);

        LogDb logDb = new LogDb(this);

        LogUploader logUploader = new LogUploader(this, logDb, phoneInfo, dispatcher);
        boolean startedFromAlarmManager = intent.getBooleanExtra(SCHEDULED_SERVICE_CALL, false);
        
        logUploader.uploadIfNecessary(startedFromAlarmManager);
    }


}
