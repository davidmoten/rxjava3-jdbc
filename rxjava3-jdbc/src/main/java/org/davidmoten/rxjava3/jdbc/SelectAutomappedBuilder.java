package org.davidmoten.rxjava3.jdbc;

import java.sql.Connection;
import java.util.List;

import javax.annotation.Nonnull;

import org.davidmoten.rxjava3.jdbc.annotations.Query;
import org.davidmoten.rxjava3.jdbc.exceptions.QueryAnnotationMissingException;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;

public final class SelectAutomappedBuilder<T> {

    final SelectBuilder selectBuilder;

    final Class<T> cls;

    private final Database db;

    SelectAutomappedBuilder(Class<T> cls, Single<Connection> connections, Database db) {
        this.selectBuilder = new SelectBuilder(getSql(cls), connections, db);
        this.cls = cls;
        this.db = db;
    }

    private static String getSql(Class<?> cls) {
        Query q = cls.getDeclaredAnnotation(Query.class);
        if (q == null) {
            throw new QueryAnnotationMissingException(
                    "the sql for the automapped interface should be specified in a Query annotation on the interface");
        }
        return q.value();
    }

    public TransactedSelectAutomappedBuilder<T> transacted() {
        return new TransactedSelectAutomappedBuilder<T>(this, db);
    }

    public TransactedSelectAutomappedBuilder<T> transactedValuesOnly() {
        return transacted().transactedValuesOnly();
    }

    public Flowable<T> get() {
        return selectBuilder.autoMap(cls);
    }
    
    public <R> Flowable<R> get(@Nonnull Function<? super T, ? extends R> function) {
        return get().map(function);
    }

    public SelectAutomappedBuilder<T> parameterStream(@Nonnull Flowable<?> values) {
        selectBuilder.parameterStream(values);
        return this;
    }

    public SelectAutomappedBuilder<T> fetchSize(int size) {
        selectBuilder.fetchSize(size);
        return this;
    }

    public SelectAutomappedBuilder<T> parameterListStream(@Nonnull Flowable<List<?>> valueLists) {
        selectBuilder.parameterListStream(valueLists);
        return this;
    }

    public SelectAutomappedBuilder<T> parameters(@Nonnull List<?> values) {
        selectBuilder.parameters(values);
        return this;
    }

    public SelectAutomappedBuilder<T> parameter(@Nonnull String name, Object value) {
        selectBuilder.parameter(name, value);
        return this;
    }
    
    public SelectAutomappedBuilder<T> parameter(Object value) {
        return parameters(value);
    }

    public SelectAutomappedBuilder<T> parameters(@Nonnull Object... values) {
        selectBuilder.parameters(values);
        return this;
    }

    public Single<Long> count() {
        return selectBuilder.count();
    }

}
