package org.example.rxjava;

import org.example.rxjava.core.Disposable;
import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.junit.Test;
import static org.junit.Assert.*;

public class ObservableTest {
    @Test
    public void testBasicObservable() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
            obs.onNext(1);
            obs.onNext(2);
            obs.onComplete();
        }).subscribe(new Observer<Integer>() {
            int count = 0;

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer item) {
                count++;
                assertTrue("Элемент должен быть 1 или 2, получено: " + item,
                        item == 1 || item == 2);
            }

            @Override
            public void onError(Throwable t) {
                fail("Ошибка не ожидалась");
            }

            @Override
            public void onComplete() {
                assertEquals("Должно быть получено 2 элемента", 2, count);
            }
        });
    }

    @Test
    public void testErrorHandling() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
            obs.onNext(1);
            obs.onError(new RuntimeException("Тестовая ошибка"));
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer item) {
                assertEquals("Ожидалось значение 1", 1, (int) item);
            }

            @Override
            public void onError(Throwable t) {
                assertEquals("Сообщение об ошибке не совпадает",
                        "Тестовая ошибка", t.getMessage());
            }

            @Override
            public void onComplete() {
                fail("onComplete не должен вызываться при ошибке");
            }
        });
    }

    @Test
    public void testEmptyObservable() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
            obs.onComplete();
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer item) {
                fail("Не должно быть элементов в пустом Observable");
            }

            @Override
            public void onError(Throwable t) {
                fail("Не должно быть ошибок в пустом Observable");
            }

            @Override
            public void onComplete() {
                assertTrue("onComplete должен быть вызван", true);
            }
        });
    }
}