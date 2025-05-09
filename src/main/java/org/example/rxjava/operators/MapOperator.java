package org.example.rxjava.operators;

import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.example.rxjava.core.Disposable;
import org.example.rxjava.functions.Function;

public class MapOperator<T, R> implements Observable.OnSubscribe<R> {
    private final Observable<T> source;
    private final Function<T, R> mapper;

    public MapOperator(Observable<T> source, Function<T, R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public void call(Observer<R> observer, Disposable disposable) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T item) {
                if (!disposable.isDisposed()) {
                    try {
                        observer.onNext(mapper.apply(item));
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!disposable.isDisposed()) {
                    observer.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (!disposable.isDisposed()) {
                    observer.onComplete();
                }
            }
        });
    }
}
