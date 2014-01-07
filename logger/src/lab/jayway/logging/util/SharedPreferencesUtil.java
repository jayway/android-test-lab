
package lab.jayway.logging.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Util for storing stuff in the SharedPreferences class.
 */
public class SharedPreferencesUtil {

    /**
     * Default max time (in milliseconds) to wait for wifi before starting a
     * upload over a non-wifi connection.
     */
    private static final long DEFAULT_MAX_WAIT_FOR_WIFI_IN_MS = 7L * 24 * 60 * 60 * 1000;

    /**
     * Default min time (in milliseconds) to wait when on wifi before starting a
     * upload. Defaults to 21 hours.
     */
    private static final long DEFAULT_MIN_WAIT_WHEN_ON_WIFI_IN_MS = 21 * 60 * 60 * 1000;

    private static final String ROOT_URL = "root_url";

    private static final String MAX_WAIT_FOR_WIFI = "max_wait_for_WIFI";

    private static final String MIN_WAIT_WHEN_ON_WIFI = "min_wait_when_on_WIFI";

    /**
     * Clears the SharedPreferences.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     */
    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Stores the root URL.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param rootUrl The rootURL to store.
     */
    public static void setRootUrl(Context context, String rootUrl) {
        if (!Utils.isEmpty(rootUrl)) {
            savePreferences(context, ROOT_URL, rootUrl);
        }
    }

    /**
     * Sets the max time (in milliseconds) to wait for WIFI before starting a
     * upload over a non-WIFI connection.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param maxWaitForWIFI The max time (in milliseconds) to wait for WIFI
     *            before starting a upload over a non-WIFI connection to store.
     */
    public static void setMaxWaitForWIFI(Context context, long maxWaitForWIFI) {
        savePreferences(context, MAX_WAIT_FOR_WIFI, maxWaitForWIFI);
    }

    /**
     * Sets the min time (in milliseconds) to wait when on WIFI before starting
     * a upload.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param maxWaitWhenOnWIFI The the min time (in milliseconds) to wait when
     *            on WIFI before starting a upload to store.
     */
    public static void setMinWaitWhenOnWIFI(Context context, long minWaitWhenOnWIFI) {
        savePreferences(context, MIN_WAIT_WHEN_ON_WIFI, minWaitWhenOnWIFI);
    }

    /**
     * Reads and returns the root URL.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the root URL.
     */
    public static String getRootUrl(Context context) {
        return loadSavedPreferencesString(context, ROOT_URL);
    }

    /**
     * /** Reads and returns the max wait for WIFI.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the the max wait for WIFI.
     */
    public static long getMaxWaitForWIFI(Context context) {
        return loadSavedPreferencesLong(context, MAX_WAIT_FOR_WIFI, DEFAULT_MAX_WAIT_FOR_WIFI_IN_MS);
    }

    /**
     * Reads and returns the max wait when on WIFI.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the max wait when on WIFI.
     */
    public static long getMinWaitWhenOnWIFI(Context context) {
        return loadSavedPreferencesLong(context, MIN_WAIT_WHEN_ON_WIFI,
                DEFAULT_MIN_WAIT_WHEN_ON_WIFI_IN_MS);
    }

    private static void savePreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void savePreferences(Context context, String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static String loadSavedPreferencesString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    private static long loadSavedPreferencesLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        long value = sharedPreferences.getLong(key, defaultValue);
        return value;
    }

}
