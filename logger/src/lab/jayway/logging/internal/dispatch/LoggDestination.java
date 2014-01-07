package lab.jayway.logging.internal.dispatch;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for the classes the {@link DispatcherHelper} can talk to. Used to
 * enable easier mocking when testing.
 */
public interface LoggDestination {

    public OutputStream open() throws IOException;

    public void close() throws IOException;

}
