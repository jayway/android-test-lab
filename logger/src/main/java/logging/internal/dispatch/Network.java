
package logging.internal.dispatch;


import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import logging.internal.Config;

public class Network implements LoggDestination {

    private final String mRootUrl;

    private HttpURLConnection connection;

    public Network(String rootUrl) {
        mRootUrl = rootUrl;
    }

    public OutputStream open() throws IOException {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Open a connection to: " + mRootUrl);
        }
        connection = (HttpURLConnection)new URL(mRootUrl).openConnection();
        connection.setDoOutput(true);
        return connection.getOutputStream();
    }

    public void close() throws IOException {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Close the connection to: " + mRootUrl);
        }
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Wrong response code from server: " + responseCode);
        }
    }

}
