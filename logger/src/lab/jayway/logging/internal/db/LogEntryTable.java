
package lab.jayway.logging.internal.db;

import lab.jayway.logging.internal.LogEntry;

/**
 * This class defines the logger {@link LogEntry} table layout.
 */
public class LogEntryTable {

    /**
     * The name of the table
     */
    public static final String NAME = "LogEntryTable";

    /**
     * The name of the columns
     */
    static final class Columns {

        /**
         * The name of the ID column
         */
        public static final String ID = "_id";

        /**
         * The name of the data column
         */
        public static final String DATA = "data";

        /**
         * The name of the data column
         */
        public static final String TIMESTAMP = "timestamp";
    }
}
