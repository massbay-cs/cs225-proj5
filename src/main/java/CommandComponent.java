import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Component that processes commands from the user or a script.
 *
 * @author Paul Buonopane
 */
public class CommandComponent extends Component {
    /**
     * Input, either from console or script.
     */
    private final Scanner in;

    /**
     * Output, typically to console.
     */
    private final PrintStream out;

    /**
     * Instantiates a new interactive command component using stdin and stdout.
     *
     * @param executor the global thread pool
     */
    public CommandComponent(ExecutorService executor) {
        this(executor, System.in, System.out);
    }

    /**
     * Instantiates a new command component with configurable input and output streams.
     *
     * @param executor the global thread pool
     * @param in the input stream from which commands will be read
     * @param out the output stream to which updates and results will be written
     */
    public CommandComponent(ExecutorService executor, InputStream in, PrintStream out) {
        super(executor);

        this.in = new Scanner(in);
        this.out = out;
    }

    /**
     * Reads and processes commands from the input stream until interrupted or the user runs an exit command.
     */
    @Override
    public void run() {
        onHelpCommand();

        while (!Thread.interrupted()) {
            String line;
            try {
                line = in.nextLine();
            } catch (Exception ex) {
                break;
            }

            line = line.trim();

            if (line.isEmpty() || line.charAt(0) == '#') {
                continue;
            }

            String[] args = line.split("\\s+");

            Future<?> command = getExecutor().submit(() -> runCommand(args));

            // If the command is quick, wait for it.
            try {
                command.get(20, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {
                out.println("Command execution was interrupted");
                break;
            } catch (ExecutionException e) {
                out.println("Error executing command: " + e.getMessage());
            } catch (TimeoutException ignored) {
                // Took too long; let the user run other commands
            }
        }
    }

    /**
     * Runs a command given a set of arguments.  The first argument is the actual command.
     *
     * @param args the arguments passed by the user/script, where <code>args[0]</code> is the command
     */
    private void runCommand(String[] args) {
        String command = args[0].toLowerCase();

        switch (command) {
            case "exit":
            case "quit":
            case "x":
            case "q":
                System.exit(0);
                break;

            case "help":
            case "?":
                onHelpCommand();
                break;

            case "pills":
            case "meds":
            case "medication":
                onPillsCommand();
                break;

            case "food":
            case "feed":
                onFoodCommand();
                break;

            case "status":
                onStatusCommand();
                break;

            default:
                out.println("Unknown command: " + command);
                break;
        }
    }

    /**
     * Display help info.
     */
    private void onHelpCommand() {
        //       ==>|------------|------------------ Hard Wrap Width -------------------------------|<==
        out.println("exit         Exit the program.");
        out.println("help         Show this help message.");
        out.println("pills        Administer medication.");
        out.println("food         Administer food.");
        out.println("status       Check status of monitored person.");
        //       ==>|------------|------------------ Hard Wrap Width -------------------------------|<==
    }

    /**
     * Attempt to administer medication to the patient.
     */
    private void onPillsCommand() {
        Future<String> result = Main.getBotComponent().giveMedication();

        out.println("Attempting to administer medication...");
        try {
            out.println(result.get());
        } catch (InterruptedException e) {
            out.println("Interrupted while administering medication.");
        } catch (ExecutionException e) {
            out.println("Error while administering medication: " + e.getMessage());
        }
    }

    /**
     * Attempt to feed the patient.
     */
    private void onFoodCommand() {
        Future<String> result = Main.getBotComponent().feed();

        out.println("Attempting to feed...");
        try {
            out.println(result.get());
        } catch (InterruptedException e) {
            out.println("Interrupted while feeding.");
        } catch (ExecutionException e) {
            out.println("Error while feeding: " + e.getMessage());
        }
    }

    /**
     * Check the status of the patient.
     */
    private void onStatusCommand() {
        Future<String> status = Main.getBotComponent().getStatus();

        out.println("Retrieving status...");
        try {
            String s = status.get();
            out.println("Status:" + (s.contains("\n") ? "\n" : " ") + s);
        } catch (InterruptedException e) {
            out.println("Interrupted while retrieving status.");
        } catch (ExecutionException e) {
            out.println("Error while retrieving status: " + e.getMessage());
        }
    }
}
