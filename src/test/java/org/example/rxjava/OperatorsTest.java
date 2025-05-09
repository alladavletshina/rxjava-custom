package org.example.rxjava;

import org.example.rxjava.core.Disposable;
import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.junit.Test;
import static org.junit.Assert.*;

public class OperatorsTest {
    @Test
    public void testMapOperator() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
                    obs.onNext(1);
                    obs.onNext(2);
                    obs.onComplete();
                })
                .map(x -> x * 2)
                .subscribe(new Observer<Integer>() {
                    int sum = 0;

                    @Override
                    public void onSubscribe(Disposable d) {
                        // Пустая реализация, если не используется
                    }

                    @Override
                    public void onNext(Integer item) {
                        sum += item;
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Не ожидалась ошибка при преобразовании");
                    }

                    @Override
                    public void onComplete() {
                        assertEquals("Сумма должна быть 6 (1*2 + 2*2)", 6, sum);
                    }
                });
    }

    @Test
    public void testMapOperatorWithError() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
                    obs.onNext(1);
                    obs.onNext(2);
                    // Убрали вызов onComplete(), так как после ошибки он не должен вызываться
                })
                .map(x -> {
                    if (x == 2) throw new RuntimeException("Ошибка преобразования");
                    return x * 2;
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Пустая реализация, если не используется
                    }

                    @Override
                    public void onNext(Integer item) {
                        assertEquals("Ожидалось значение 2 (1*2)", 2, (int) item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        assertEquals("Сообщение об ошибке не совпадает",
                                "Ошибка преобразования", t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        fail("onComplete не должен вызываться при ошибке");
                    }
                });
    }

    @Test
    public void testFilterOperator() {
        Observable.create((Observer<Integer> obs, org.example.rxjava.core.Disposable d) -> {
                    obs.onNext(1);
                    obs.onNext(2);
                    obs.onNext(3);
                    obs.onComplete();
                })
                .filter(x -> x % 2 == 0)
                .subscribe(new Observer<Integer>() {
                    int count = 0;

                    @Override
                    public void onSubscribe(Disposable d) {
                        // Пустая реализация, если не используется
                    }

                    @Override
                    public void onNext(Integer item) {
                        count++;
                        assertEquals("Ожидалось четное число", 2, (int) item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        fail("Не ожидалась ошибка при фильтрации");
                    }

                    @Override
                    public void onComplete() {
                        assertEquals("Должен быть только 1 элемент", 1, count);
                    }
                });
    }
}