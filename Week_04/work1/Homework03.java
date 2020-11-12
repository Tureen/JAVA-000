package java0.conc0303;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 * <p>
 * 一个简单的代码参考：
 */
public class Homework03 {

    public static void main(String[] args) throws Exception {
        int i = 0;
        wayOne(++i);
        System.out.println();
        wayTwo(++i);
        System.out.println();
        wayThree(++i);
        System.out.println();
        wayFour(++i);
        System.out.println();
        wayFive(++i);
        System.out.println();
        waySix(++i);
        System.out.println();
        waySeven(++i);
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }

    /**
     * FutureTask
     *
     * @throws Exception
     */
    public static void wayOne(int i) throws Exception {
        long start = System.currentTimeMillis();
        FutureTask futureTask = new FutureTask(() -> sum());
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(futureTask);
        final Object result = futureTask.get();
        System.out.println(i + "-异步计算结果为：" + result);
        System.out.println(i + "-使用时间：" + (System.currentTimeMillis() - start) + " ms");
        executorService.shutdown();
    }

    /**
     * Future executorService
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void wayTwo(int i) throws Exception {
        long start = System.currentTimeMillis();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<Integer> submit = executorService.submit(() -> sum());
        final Object result = submit.get();
        System.out.println(i + "-异步计算结果为：" + result);
        System.out.println(i + "-使用时间：" + (System.currentTimeMillis() - start) + " ms");
        executorService.shutdown();
    }

    /**
     * CountDownLatch
     *
     * @throws Exception
     */
    public static void wayThree(int i) throws Exception {
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int sums[] = new int[1];
        new Thread(() -> {
            sums[0] = sum();
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
        System.out.println(i + "-异步计算结果为：" + sums[0]);
        System.out.println(i + "-使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * CyclicBarrier
     */
    public static void wayFour(int i) {
        long start = System.currentTimeMillis();
        int[] sums = new int[1];

        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            System.out.println(i + "异步计算结果为：" + sums[0]);
            System.out.println(i + "使用时间：" + (System.currentTimeMillis() - start) + " ms");
        });

        new Thread(() -> {
            try {
                sums[0] = sum();
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Semaphore
     * @param i
     * @throws InterruptedException
     */
    public static void wayFive(int i) throws InterruptedException {
        long start = System.currentTimeMillis();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire(1);
        int[] sums = new int[1];
        new Thread(() -> {
            sums[0] = sum();
            semaphore.release();
        }).start();
        semaphore.acquire();
        System.out.println(i + "异步计算结果为：" + sums[0]);
        System.out.println(i + "使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * CAS AtomicInteger
     * @param i
     */
    public static void waySix(int i){
        long start = System.currentTimeMillis();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        int[] sums = new int[1];
        new Thread(() -> {
            sums[0] = sum();
            atomicInteger.incrementAndGet();
        }).start();
        while(atomicInteger.get() == 1){}
        System.out.println(i + "异步计算结果为：" + sums[0]);
        System.out.println(i + "使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    private static volatile  int[] x = new int[]{1};

    /**
     * CAS volatile
     * @param i
     */
    public static void waySeven(int i){
        long start = System.currentTimeMillis();
        int[] sums = new int[1];
        new Thread(() -> {
            sums[0] = sum();
            x[0]++;
        }).start();
        while(x[0] == 1){}
        System.out.println(i + "异步计算结果为：" + sums[0]);
        System.out.println(i + "使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
}
