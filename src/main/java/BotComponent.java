import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BotComponent extends Component {
    private final PrintStream out;
    private boolean hungry;
    private boolean needsMedication;
    private boolean skipUpdate;

    public BotComponent(ExecutorService executor) {
        this(executor, System.out);
    }

    @SuppressWarnings("WeakerAccess")
    public BotComponent(ExecutorService executor, PrintStream out) {
        super(executor);

        this.out = out;
    }

    @Override
    public void run() {
        // The bot doesn't do anything unless it's told to.
    }

    public Future<String> getStatus() {
        return getExecutor().submit(() -> {
            try {
                updateStatus();

                String status = "";
                if (hungry) {
                    status += "Hungry\n";
                }
                if (needsMedication) {
                    status += "Needs medication\n";
                }

                return status.isEmpty() ? "Normal" : status.trim();
            } catch (InterruptedException ex) {
                return "Interrupted before status could be determined";
            }
        });
    }

    public Future<String> feed() {
        return getExecutor().submit(() -> {
            if (!hungry) {
                return "Not hungry right now.";
            }

            Thread.sleep(1000L);

            hungry = false;
            skipUpdate = true;

            return "Food administered.";
        });
    }

    public Future<String> giveMedication() {
        return getExecutor().submit(() -> {
            if (!needsMedication) {
                return "Doesn't need medication right now.";
            }

            Thread.sleep(1000L);

            needsMedication = false;
            skipUpdate = true;

            return "Medication administered.";
        });
    }

    private void updateStatus() throws InterruptedException {
        Thread.sleep(1000L);

        if (skipUpdate) {
            skipUpdate = false;
            return;
        }

        double r = Math.random();

        if (r < 0.3) {
            return;
        }

        if (r < 0.6) {
            needsMedication = true;
        } else {
            hungry = true;
        }
    }
}
