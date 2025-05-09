package org.example.rxjava.operators;

import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.example.rxjava.core.Disposable;
import org.example.rxjava.functions.Function;

public class FlatMapOperator<T, R> implements Observable.OnSubscribe<R> {
    private final Observable<T> source;
    private final Function<T, Observable<R>> mapper;

    public FlatMapOperator(Observable<T> source, Function<T, Observable<R>> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public void call(Observer<R> observer, Disposable parentDisposable) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T item) {
                if (!parentDisposable.isDisposed()) {
                    try {
                        Observable<R> observable = mapper.apply(item);
                        observable.subscribe(new Observer<R>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(R item) {
                                if (!parentDisposable.isDisposed()) {
                                    observer.onNext(item);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (!parentDisposable.isDisposed()) {
                                    observer.onError(t);
                                }
                            }

                            @Override
                            public void onComplete() {
                                // Ничего не делаем, ждем завершения основного потока
                            }
                        });
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!parentDisposable.isDisposed()) {
                    observer.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (!parentDisposable.isDisposed()) {
                    observer.onComplete();
                }
            }
        });
    }
}
