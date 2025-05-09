package org.example.rxjava.core;


public class Subscription implements Disposable {
    private volatile boolean disposed = false;

    @Override
    public void dispose() {
        disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
}

