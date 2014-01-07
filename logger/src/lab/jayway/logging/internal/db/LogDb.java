
package lab.jayway.logging.internal.db;

/**
 * Facade towards the database.
 */
public interface LogDb {

    /**
     * Opens the database. Don't forget to close it when you're done!
     */
    void open();

    /**
     * Closes the database. All method calls (except for {@link #open()}) will
     * probably throw {@link NullPointerException} after this call.
     */
    void close();

    /**
     * Checks if the database is empty.
     *
     * @return <code>true</code> if the database is empty, <code>false</code>
     *         otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieves the oldest time stamp in the database. To be sure that the
     * value is valid, you should probably check {@link #isEmpty()} first.
     *
     * @return the oldest time stamp in the database
     */
    long getOldestTimeStamp();

    /**
     * Checks if the oldest time stamp is reasonable. An example of an
     * unreasonable timestamp would be a timestamp from the future. An empty
     * database is considered to have reasonable time stamps.
     *
     * @return <code>true</code> if the time stamps seems reasonable,
     *         <code>false</code> otherwise.
     */
    boolean isOldestTimeStampInTheFuture();

    /**
     * Deletes everything in the database.
     */
    void dropAll();

}
