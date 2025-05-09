package org.example.rxjava;

import org.example.rxjava.core.Disposable;
import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.junit.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

public class DisposableTest {
    @Test
    public void testDisposableStopsEmissions() {
        AtomicInteger emittedCount = new AtomicInteger();
        AtomicInteger receivedCount = new AtomicInteger();

        Observable.create((Observer<Integer> obs, Disposable d) -> {
            for (int i = 0; i < 10; i++) {
                if (d.isDisposed()) {
                    System.out.println("Подписка отменена, прерываем эмиссию");
                    break;
                }
                obs.onNext(i);
                emittedCount.incrementAndGet();
                System.out.println("Эмитирован элемент: " + i);
            }
            obs.onComplete();
        }).subscribe(new Observer<Integer>() {
            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
            }

            @Override
            public void onNext(Integer item) {
                receivedCount.incrementAndGet();
                System.out.println("Получен элемент: " + item);
                if (receivedCount.get() == 3) {
                    System.out.println("Отменяем подписку");
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable t) {
                fail("Не ожидалась ошибка");
            }

            @Override
            public void onComplete() {
                System.out.println("Завершено. Получено элементов: " + receivedCount.get());
                assertEquals("Должно быть получено 3 элемента", 3, receivedCount.get());
                assertTrue("Должно быть эмитировано не больше 3 элементов", emittedCount.get() <= 3);
            }
        });
    }
}