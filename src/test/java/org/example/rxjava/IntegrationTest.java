package org.example.rxjava;

import org.example.rxjava.core.Disposable;
import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    @Test
    public void testCombinedOperators() {
        AtomicInteger sum = new AtomicInteger();

        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
                    obs.onNext(1);
                    obs.onNext(2);
                    obs.onNext(3);
                    obs.onComplete();
                })
                .map(x -> x * 2)
                .filter(x -> x > 3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer item) {
                        sum.addAndGet(item);
                    }
                    @Override
                    public void onError(Throwable t) {
                        fail("Не ожидалась ошибка в комбинированном тесте");
                    }
                    @Override
                    public void onComplete() {
                        assertEquals(10, sum.get(), "Сумма должна быть 10 (4 + 6)");
                    }
                });
    }
}