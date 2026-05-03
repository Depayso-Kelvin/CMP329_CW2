package tests;
import org.junit.jupiter.api.extension.*;
import java.util.logging.Logger;
// Logs test start, pass and fail events to the console.
// attached to test classes with @ExtendWith
public class LoggingTestWatcher implements TestWatcher, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger LOGGER = Logger.getLogger(LoggingTestWatcher.class.getName());

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        LOGGER.info("Starting test: " + context.getDisplayName());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        LOGGER.info("Finished test (SUCCESS): " + context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        LOGGER.info("Finished test (FAILED): " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
    }
}
