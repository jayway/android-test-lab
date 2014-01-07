
package lab.jayway.logging.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Used to format a date according to ISO 8601.
 */
public class ISO8601DateFormat {

    /**
     * ISO8601 date-time format without time zone
     */
    private static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Current timezone for the sytem.
     */
    private static TimeZone timeZone = Calendar.getInstance().getTimeZone();

    /**
     * Used to format hours and minutes as two digits.
     */
    private static DecimalFormat TWO_DIGIT_FORMAT = new DecimalFormat("00");

    /**
     * <p>
     * Formats the given date according to a ISO8601 date on the form
     * YYYY-MM-DDThh:mm:ssZTD, where T is a literal and ZTD is the time zone
     * that is either 'Z', meaning UTC, or +/-hh:mm for other time zones.
     * </p>
     * <p>
     * Since {@link Date} doesn't contain time zone information, the time zone
     * is the system's default time zone.
     * </p>
     *
     * @param date the date to format
     * @return a string representation of the time in ISO860 compliant format.
     */
    public static String format(Date date) {
        StringBuilder sb = new StringBuilder(24);
        sb.append(new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US).format(date));
        int offsetInMs = timeZone.getRawOffset();
        if (offsetInMs == 0) {
            sb.append("Z");
        } else {
            sb.append(offsetInMs > 0 ? '+' : '-');
            int hours = Math.abs(offsetInMs / (60 * 60 * 1000));
            int minutes = Math.abs(offsetInMs / (60 * 1000)) % 60;
            sb.append(TWO_DIGIT_FORMAT.format(hours));
            sb.append(":");
            sb.append(TWO_DIGIT_FORMAT.format(minutes));
        }
        return sb.toString();
    }

}
