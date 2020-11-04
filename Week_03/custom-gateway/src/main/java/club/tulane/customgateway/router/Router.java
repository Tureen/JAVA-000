package club.tulane.customgateway.router;

import java.util.concurrent.atomic.AtomicLong;

public class Router {

    private static AtomicLong num = new AtomicLong(0);

    public static String getWay(String[] backUrls){
        // TODO 先写死, 可以抽象策略做简单工厂
        // 轮询
        final long l = num.incrementAndGet();
        final int index = (int) (l % backUrls.length);
        return backUrls[index];
    }
}
