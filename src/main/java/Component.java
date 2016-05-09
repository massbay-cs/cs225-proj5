import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class Component implements Runnable {
    private final ExecutorService executor;

    protected Component(ExecutorService executor) {
        this.executor = executor;
    }

    protected ExecutorService getExecutor() {
        return executor;
    }

    public Future<?> submit() {
        return getExecutor().submit(this);
    }
}
