import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CommandComponent extends Component {
    private final Scanner in;
    private final PrintStream out;

    public CommandComponent(ExecutorService executor) {
        this(executor, System.in, System.out);
    }

    @SuppressWarnings("WeakerAccess")
    public CommandComponent(ExecutorService executor, InputStream in, PrintStream out) {
        super(executor);

        this.in = new Scanner(in);
        this.out = out;
    }

    @Override
    public void run() {
        onHelpCommand();

        while (true) {
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

            if (!runCommand(args)) {
                break;
            }
        }
    }

    private boolean runCommand(String[] args) {
        String command = args[0].toLowerCase();

        switch (command) {
            case "exit":
            case "quit":
            case "x":
            case "q":
                System.exit(0);
                return false;

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
        }

        return true;
    }

    private void onHelpCommand() {
        //        ->|                                                                                       |<-
        out.println("exit        Exit the program.");
        out.println("help        Show this help message.");
        out.println("pills       Administer medication.");
        out.println("food        Administer food.");
        out.println("status      Check status of monitored person.");
    }

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
