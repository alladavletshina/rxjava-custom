package org.example.rxjava.schedulers;

import org.example.rxjava.core.Disposable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComputationScheduler implements Scheduler {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }

    @Override
    public Disposable schedule(Runnable task) {
        Future<?> future = executor.submit(task);
        return new Disposable() {
            @Override
            public void dispose() {
                future.cancel(true);
            }

            @Override
            public boolean isDisposed() {
                return future.isDone() || future.isCancelled();
            }
        };
    }
}
