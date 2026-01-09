import cli.CommandLineInterface;
import storage.InMemoryStorage;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final InMemoryStorage storage = new InMemoryStorage();

    public static void main(String[] args) {
        // Фоновая очистка каждые 10 минут
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                storage.cleanupExpired();
            }
        }, 60_000, 600_000); // первая через 1 мин, потом каждые 10 мин

        new CommandLineInterface().start();

        timer.cancel();
    }
}
