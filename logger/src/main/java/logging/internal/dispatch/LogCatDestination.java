
package logging.internal.dispatch;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import logging.internal.Config;

public class LogCatDestination implements LoggDestination {

    private ByteArrayOutputStream out;

    public OutputStream open() {
        out = new ByteArrayOutputStream();
        return out;
    }

    public void close() {
        try {
            out.close();
            Log.d(Config.LOG_TAG, "Sent log data:");
            Log.d(Config.LOG_TAG, new String(out.toByteArray()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
