package org.example.rxjava.core;

import org.example.rxjava.functions.Function;
import org.example.rxjava.functions.Predicate;
import org.example.rxjava.operators.*;
import org.example.rxjava.schedulers.Scheduler;

public class Observable<T> {
    private final OnSubscribe<T> onSubscribe;

    private Observable(OnSubscribe<T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
        return new Observable<>(onSubscribe);
    }

    public Disposable subscribe(Observer<T> observer) {
        Subscription subscription = new Subscription();
        // Важно сначала вызвать onSubscribe
        observer.onSubscribe(subscription);
        // Затем уже вызывать остальные методы
        if (!subscription.isDisposed()) {
            onSubscribe.call(observer, subscription);
        }
        return subscription;
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        return new Observable<>(new MapOperator<>(this, mapper));
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return new Observable<>(new FilterOperator<>(this, predicate));
    }

    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        return new Observable<>(new FlatMapOperator<>(this, mapper));
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return new Observable<>(new OnSubscribe<T>() {
            @Override
            public void call(Observer<T> observer, Disposable disposable) {
                scheduler.execute(() -> onSubscribe.call(observer, disposable));
            }
        });
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return new Observable<>(new OnSubscribe<T>() {
            @Override
            public void call(Observer<T> observer, Disposable parentDisposable) {
                subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T item) {
                        if (!parentDisposable.isDisposed()) {
                            scheduler.execute(() -> {
                                if (!parentDisposable.isDisposed()) {
                                    observer.onNext(item);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (!parentDisposable.isDisposed()) {
                            scheduler.execute(() -> {
                                if (!parentDisposable.isDisposed()) {
                                    observer.onError(t);
                                }
                            });
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (!parentDisposable.isDisposed()) {
                            scheduler.execute(() -> {
                                if (!parentDisposable.isDisposed()) {
                                    observer.onComplete();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

 public interface OnSubscribe<T> {
        void call(Observer<T> observer, Disposable disposable);
    }
}
