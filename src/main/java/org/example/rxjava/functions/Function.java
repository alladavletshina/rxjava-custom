package org.example.rxjava.functions;

public interface Function<T, R> {
    R apply(T t) throws Exception;
}
