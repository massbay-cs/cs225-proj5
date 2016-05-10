import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for the application.  Also stores components.
 *
 * @author Paul Buonopane
 */
public class Main {
    /**
     * Thread pool.
     */
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // Interdependent components are stored as their own fields so that components can access each other.
    // In the future, this would likely be replaced with a more versatile system that could handle a large/configurable
    // number of components.  This future intention is represented in the UML.

    /**
     * Handles input from the user in the form of commands.  Also capable of reading from scripts.
     */
    private static final CommandComponent commandComponent = new CommandComponent(executor);

    /**
     * Bot the monitors and interacts with the patient.
     */
    private static final BotComponent botComponent = new BotComponent(executor);

    /**
     * Entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // This is suitable for now, but the idea is that Main could store a variable-sized collection of components
        // if this application were expanded.  It would run all of them and wait for them to complete.  Thus, the
        // lifetime of Main is tied to the lifetime of the components, and components can't outlive Main.  This future
        // intention is represented in the UML.
        getBotComponent().submit();
        getCommandComponent().submit();

        try {
            // Wait for all components to exit.
            while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                Thread.sleep(0);
            }
        } catch (InterruptedException ignored) { }
    }

    /**
     * Gets a singleton component that handles input from the user or a script.
     *
     * @return a singleton component that handles input from the user or a script
     */
    public static BotComponent getBotComponent() {
        return botComponent;
    }

    /**
     * Gets a singleton component that monitors and interacts with the patient.
     *
     * @return a singleton component that monitors and interacts with the patient
     */
    public static CommandComponent getCommandComponent() {
        return commandComponent;
    }
}
