
package logging.internal;

/**
 * Holds config information used by other classes.
 */
public class Config {
    /**
     * MUST be used to wrap ALL Log.x statements within the logger.
     */
    public static final boolean DEBUG = true;

    /**
     * MUST be used in ALL Log.x(LOG_TAG... statements within the logger.
     */
    public static final String LOG_TAG = "Jayway Logger Lab";
}
