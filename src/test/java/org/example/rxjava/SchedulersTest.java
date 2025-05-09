package org.example.rxjava;

import org.example.rxjava.core.Disposable;
import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.example.rxjava.schedulers.IOThreadScheduler;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SchedulersTest {
    @Test
    public void testSubscribeOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> threadName = new AtomicReference<>();

        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
                    threadName.set(Thread.currentThread().getName());
                    obs.onNext(1);
                    obs.onComplete();
                    latch.countDown();
                })
                .subscribeOn(new IOThreadScheduler())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer item) {}
                    @Override
                    public void onError(Throwable t) {}
                    @Override
                    public void onComplete() {}
                });

        latch.await(1, TimeUnit.SECONDS);
        assertNotNull(threadName.get());
        assertNotEquals(Thread.currentThread().getName(), threadName.get());
    }
}
