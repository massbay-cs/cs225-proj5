import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final CommandComponent commandComponent = new CommandComponent(executor);
    private static final BotComponent botComponent = new BotComponent(executor);

    public static void main(String[] args) {
        getBotComponent().submit();
        getCommandComponent().submit();

        try {
            while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                Thread.sleep(0);
            }
        } catch (InterruptedException ignored) { }
    }

    public static BotComponent getBotComponent() {
        return botComponent;
    }

    public static CommandComponent getCommandComponent() {
        return commandComponent;
    }
}
