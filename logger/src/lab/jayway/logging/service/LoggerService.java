
package lab.jayway.logging.service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import lab.jayway.logging.internal.Config;
import lab.jayway.logging.internal.db.LogDb;
import lab.jayway.logging.internal.db.SqliteLogDb;
import lab.jayway.logging.internal.dispatch.Dispatcher;
import lab.jayway.logging.internal.dispatch.LoggDestination;
import lab.jayway.logging.internal.dispatch.Network;
import lab.jayway.logging.service.LoggerScheduler.Action;
import lab.jayway.logging.util.PhoneInfo;
import lab.jayway.logging.util.SharedPreferencesUtil;

/**
 * --- General Requirements --- </br> No real-time logging </br> Max wait for
 * wifi before uploading on mobile network is one week. </br> No uploading when
 * roaming. Should we discard data that is older than </br> one week? </br> Log
 * when offline </br> API Key is needed to access service </br> SSL to access
 * service </br>
 */
public class LoggerService extends IntentService {

    public static final String SCHEDULED_SERVICE_CALL = "scheduled_service_call";

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "LoggerService is now running.");
        }
        boolean automaticServiceCall = false;
        if (intent != null) {
            automaticServiceCall = intent.getBooleanExtra(SCHEDULED_SERVICE_CALL, false);
        }

        LogDb db = new SqliteLogDb(this);
        String apiKey = SharedPreferencesUtil.getAPIKey(this);
        String rootUrl = SharedPreferencesUtil.getRootUrl(this);

        LoggDestination destination = new Network(rootUrl, apiKey);
        Dispatcher dispatcher = new Dispatcher(destination);
        LoggerScheduler scheduler = new LoggerScheduler(this, db, new PhoneInfo(this), dispatcher);
        Action action = scheduler.getAction(automaticServiceCall);
        scheduler.perform(action);

        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "LoggerService is done and will shut down now.");
        }
    }

}
