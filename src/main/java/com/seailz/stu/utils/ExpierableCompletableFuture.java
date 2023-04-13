package com.seailz.stu.utils;

import java.util.concurrent.CompletableFuture;

/**
 * This completable future will expire after a certain amount of time.
 * <br>Once expired, the onExpire runnable will be executed.
 * @param <T> The type of the completable future.
 */
public class ExpierableCompletableFuture<T> extends CompletableFuture<T> {

    private Runnable onExpire;

    public ExpierableCompletableFuture(int expireMs) {
        new Thread(() -> {
            try {
                Thread.sleep(expireMs);

                if (isDone() || onExpire == null)
                    return;

                onExpire.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onExpire(Runnable onExpire) {
        this.onExpire = onExpire;
    }

}
