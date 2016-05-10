import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base for a singleton component of the application that's designed to run tasks asynchronously using an event-driven
 * model.
 *
 * @author Paul Buonopane
 */
public abstract class Component implements Runnable {
    /**
     * Thread pool shared by all components.
     */
    private final ExecutorService executor;

    /**
     * Instantiate the component with a global thread pool.
     *
     * @param executor thread pool
     */
    protected Component(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Get the global thread pool.
     *
     * @return the global thread pool
     */
    protected final ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Starts the primary task for this component.  If this component doesn't have a primary task, a no-op task will
     * be submitted that may or may not return immediately.
     *
     * @return the primary task; calling {@link Future#get()} will always return <code>null</code>.
     */
    public Future<?> submit() {
        return getExecutor().submit(this);
    }

    /**
     * Runs the primary task for this component synchronously in the current thread.  Normally this shouldn't be
     * invoked directly; use {@link Component#submit()} instead.
     */
    @Override
    public abstract void run();
}
