package org.example.rxjava.schedulers;

import org.example.rxjava.core.Disposable;

public interface Scheduler {
    void execute(Runnable task);
    Disposable schedule(Runnable task);
}
