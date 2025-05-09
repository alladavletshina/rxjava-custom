package org.example.rxjava.operators;

import org.example.rxjava.core.Observable;
import org.example.rxjava.core.Observer;
import org.example.rxjava.core.Disposable;
import org.example.rxjava.functions.Predicate;

public class FilterOperator<T> implements Observable.OnSubscribe<T> {
    private final Observable<T> source;
    private final Predicate<T> predicate;

    public FilterOperator(Observable<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public void call(Observer<T> observer, Disposable disposable) {
        source.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T item) {
                if (!disposable.isDisposed()) {
                    try {
                        if (predicate.test(item)) {
                            observer.onNext(item);
                        }
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