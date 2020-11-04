package club.tulane.customgateway.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceFactory {

    private static volatile ExecutorService executorService;

    public static ExecutorService getInstance() {

        if (executorService == null) {

            synchronized (ExecutorServiceFactory.class) {
                if (executorService == null) {
                    executorService = Executors.newFixedThreadPool(8);
                }
            }
        }
        return executorService;
    }
}
