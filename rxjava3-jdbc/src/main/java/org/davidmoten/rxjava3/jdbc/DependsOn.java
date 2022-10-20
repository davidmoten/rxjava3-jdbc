package org.davidmoten.rxjava3.jdbc;

import javax.annotation.Nonnull;

import com.github.davidmoten.guavamini.Preconditions;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface DependsOn<T> {

    T dependsOn(Flowable<?> flowable);

    default T dependsOn(@Nonnull Observable<?> observable) {
        Preconditions.checkNotNull(observable, "observable cannot be null");
        return dependsOn(observable.ignoreElements().toFlowable());
    }

    default T dependsOn(@Nonnull Single<?> single) {
        Preconditions.checkNotNull(single, "single cannot be null");
        return dependsOn(single.toFlowable());
    }

    default T dependsOn(@Nonnull Completable completable) {
        Preconditions.checkNotNull(completable, "completable cannot be null");
        return dependsOn(completable.toFlowable());
    }

}
