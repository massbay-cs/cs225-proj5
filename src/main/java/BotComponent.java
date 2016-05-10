import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Component that monitors and interacts with the patient.
 *
 * @author Paul Buonopane
 */
public class BotComponent extends Component {
    /**
     * Whether the patient needs food.
     */
    private AtomicBoolean hungry = new AtomicBoolean(false);

    /**
     * Whether the patient needs medication.
     */
    private AtomicBoolean needsMedication = new AtomicBoolean(false);

    /**
     * For the sake of realistic simulation, this will prevent the state of the patient from changing the first time
     * they're observed after an action is taken.
     */
    private AtomicBoolean skipUpdate = new AtomicBoolean(false);

    /**
     * Instantiate bot component with a global thread pool.
     *
     * @param executor the global thread pool
     */
    public BotComponent(ExecutorService executor) {
        super(executor);
    }

    /**
     * No-op.  This component only reacts do events; it doesn't have a background task.
     */
    @Override
    public void run() {
        // The bot doesn't do anything unless it's told to.
    }

    /**
     * Asynchronously determine the current status of the patient.
     *
     * @return a friendly status of the patient for presentation to the user
     */
    public Future<String> getStatus() {
        return getExecutor().submit(() -> {
            try {
                updateStatus();

                String status = "";
                if (hungry.get()) {
                    status += "Hungry\n";
                }
                if (needsMedication.get()) {
                    status += "Needs medication\n";
                }

                return status.isEmpty() ? "Normal" : status.trim();
            } catch (InterruptedException ex) {
                return "Interrupted before status could be determined";
            }
        });
    }

    /**
     * Attempts to perform an action on the patient asynchronously.  Will only succeed in doing so if the action is
     * required by the current state of the patient.
     *
     * @param flag the flag that determines whether this action needs to be performed
     * @param success status to be returned on success
     * @param failure status to be returned if the action doesn't need to be performed
     * @return a status message indicating whether the action was performed successfully
     */
    private Future<String> performAction(AtomicBoolean flag, String success, String failure) {
        return getExecutor().submit(() -> {
            // Return immediately if we already know that action isn't necessary.
            if (!flag.get()) {
                return failure;
            }

            // Simulate doing stuff.
            Thread.sleep(1000L);

            // This doesn't need to be atomic with flag.getAndSet, although that would be ideal.  Race would be highly
            // unlikely, and impact would only affect simulation, not real patient.  No need to lock.
            skipUpdate.set(true);

            if (!flag.compareAndSet(true, false)) {
                // Another thread beat us in a race.
                return failure;
            }

            // If skipUpdate has been read since we set it, it's also be set to false.  This lock-free trick could
            // result in the state of the patient not being updated for two rounds; while this changes the probability
            // of each change slightly, it doesn't negatively impact the functionality of the simulation.  The user has
            // no way of determining whether this has happened.  It's also extremely unlikely.  We could choose to skip
            // this, but we have no guarantee that the user hasn't queried the status of the patient in between
            // skipUpdate.set(true) and flag.compareAndSet(true, false), thereby resetting skipUpdate to false.
            //
            // Of course, this could easily be solved with locking, but I'm trying to write this application with
            // minimal locking.
            skipUpdate.set(true);

            return success;
        });
    }

    /**
     * Attempt to feed the patient.
     *
     * @return a status message indicating whether the action was performed successfully
     */
    public Future<String> feed() {
        return performAction(hungry, "Food administered.", "Not hungry right now.");
    }

    /**
     * Attempt to administer medication to the patient.
     *
     * @return a status message indicating whether the action was performed successfully
     */
    public Future<String> giveMedication() {
        return performAction(needsMedication, "Medication administered.", "Doesn't need medication right now.");
    }

    /**
     * Update the status of the patient for simulation purposes.  Not asynchronous; takes significant time.
     *
     * @throws InterruptedException if the update is interrupted and canceled
     */
    private void updateStatus() throws InterruptedException {
        Thread.sleep(1000L);

        // Run early in case we already know we're to be skipping this round, but not before sleeping.  We'll run it
        // again later so the update happens close to the other atomic operations.
        if (skipUpdate.compareAndSet(true, false)) {
            return;
        }

        double r = Math.random();

        if (r < 0.5) {
            return;
        }

        // Keep all the atomic operations close together.

        if (skipUpdate.compareAndSet(true, false)) {
            return;
        }

        if (r < 0.75) {
            needsMedication.set(true);
        } else {
            hungry.set(true);
        }
    }
}
