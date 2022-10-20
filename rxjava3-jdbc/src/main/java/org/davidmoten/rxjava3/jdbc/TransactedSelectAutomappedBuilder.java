package org.davidmoten.rxjava3.jdbc;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;

public final class TransactedSelectAutomappedBuilder<T> {

    private final SelectAutomappedBuilder<T> selectBuilder;
    private final Database db;

    // mutable
    private boolean valuesOnly = false;

    TransactedSelectAutomappedBuilder(SelectAutomappedBuilder<T> selectBuilder, Database db) {
        this.selectBuilder = selectBuilder;
        this.db = db;
    }

    public TransactedSelectAutomappedBuilder<T> parameters(
            @Nonnull Flowable<List<Object>> parameters) {
        selectBuilder.parameters(parameters);
        return this;
    }

    public TransactedSelectAutomappedBuilder<T> parameters(@Nonnull List<?> values) {
        selectBuilder.parameters(values);
        return this;
    }

    public TransactedSelectAutomappedBuilder<T> parameter(@Nonnull String name, Object value) {
        selectBuilder.parameter(name, value);
        return this;
    }

    public TransactedSelectAutomappedBuilder<T> parameters(@Nonnull Object... values) {
        selectBuilder.parameters(values);
        return this;
    }

    public TransactedSelectAutomappedBuilder<T> parameter(Object value) {
        selectBuilder.parameters(value);
        return this;
    }

    public TransactedSelectAutomappedBuilder<T> transactedValuesOnly() {
        this.valuesOnly = true;
        return this;
    }

    public TransactedSelectAutomappedBuilderValuesOnly<T> valuesOnly() {
        return new TransactedSelectAutomappedBuilderValuesOnly<T>(this, db);
    }

    public static final class TransactedSelectAutomappedBuilderValuesOnly<T> {
        private final TransactedSelectAutomappedBuilder<T> b;
        private final Database db;

        TransactedSelectAutomappedBuilderValuesOnly(TransactedSelectAutomappedBuilder<T> b,
                Database db) {
            this.b = b;
            this.db = db;
        }

        public Flowable<T> get() {
            return createFlowable(b.selectBuilder, db) //
                    .flatMap(Tx.flattenToValuesOnly());
        }

        public <R> Flowable<R> get(@Nonnull Function<? super T, ? extends R> function) {
            return get().map(function);
        }
        
    }

    public Flowable<Tx<T>> get() {
        Flowable<Tx<T>> o = createFlowable(selectBuilder, db);
        if (valuesOnly) {
            return o.filter(tx -> tx.isValue());
        } else {
            return o;
        }
    }
    
    private static <T> Flowable<Tx<T>> createFlowable(SelectAutomappedBuilder<T> sb, Database db) {
        return Flowable.defer(() -> {
            AtomicReference<Connection> connection = new AtomicReference<Connection>();
            return Select.create(sb.selectBuilder.connection //
                    .map(c -> Util.toTransactedConnection(connection, c)), //
                    sb.selectBuilder.parameterGroupsToFlowable(), //
                    sb.selectBuilder.sql, //
                    sb.selectBuilder.fetchSize, //
                    Util.autoMap(sb.cls), //
                    false, //
                    sb.selectBuilder.queryTimeoutSec) //
                    .materialize() //
                    .flatMap(n -> Tx.toTx(n, connection.get(), db)) //
                    .doOnNext(tx -> {
                        if (tx.isComplete()) {
                            ((TxImpl<T>) tx).connection().commit();
                        }
                    });
        });
    }

}
