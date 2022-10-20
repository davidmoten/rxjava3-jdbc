package org.davidmoten.rxjava3.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public final class TransactedUpdateBuilder implements DependsOn<TransactedUpdateBuilder> {

    private static final Logger log = LoggerFactory.getLogger(TransactedUpdateBuilder.class);

    final UpdateBuilder updateBuilder;
    private final Database db;
    private boolean valuesOnly;

    TransactedUpdateBuilder(UpdateBuilder b, Database db) {
        this.updateBuilder = b;
        this.db = db;
    }

    public TransactedUpdateBuilder parameterStream(@Nonnull Flowable<?> values) {
        updateBuilder.parameterStream(values);
        return this;
    }

    public TransactedUpdateBuilder parameterListStream(@Nonnull Flowable<List<?>> valueLists) {
        updateBuilder.parameterListStream(valueLists);
        return this;
    }

    public TransactedUpdateBuilder parameters(@Nonnull List<?> values) {
        updateBuilder.parameters(values);
        return this;
    }

    public TransactedUpdateBuilder parameter(@Nonnull String name, Object value) {
        updateBuilder.parameter(name, value);
        return this;
    }

    public TransactedUpdateBuilder parameter(Object value) {
        return parameters(value);
    }

    public TransactedUpdateBuilder parameters(@Nonnull Object... values) {
        updateBuilder.parameters(values);
        return this;
    }

    @Override
    public TransactedUpdateBuilder dependsOn(@Nonnull Flowable<?> dependency) {
        updateBuilder.dependsOn(dependency);
        return this;
    }

    public TransactedUpdateBuilder batchSize(int batchSize) {
        updateBuilder.batchSize(batchSize);
        return this;
    }

    /**
     * Returns a builder used to specify how to process the generated keys
     * {@link ResultSet}. Not all jdbc drivers support this functionality and some
     * have limitations in their support (h2 for instance only returns the last
     * generated key when multiple inserts happen in the one statement).
     * 
     * @return a builder used to specify how to process the generated keys ResultSet
     */
    public TransactedReturnGeneratedKeysBuilder returnGeneratedKeys() {
        return new TransactedReturnGeneratedKeysBuilder(this, db);
    }

    public TransactedUpdateBuilder transactedValuesOnly() {
        this.valuesOnly = true;
        return this;
    }

    public TransactedUpdateBuilderValuesOnly valuesOnly() {
        return new TransactedUpdateBuilderValuesOnly(this, db);
    }

    public static final class TransactedUpdateBuilderValuesOnly {
        private final TransactedUpdateBuilder b;
        private final Database db;

        TransactedUpdateBuilderValuesOnly(TransactedUpdateBuilder b, Database db) {
            this.b = b;
            this.db = db;
        }

        // TODO add other methods e.g. parameter setting methods? Lots of
        // copy-and-paste not attractive here so may accept restricting
        // functionality once valuesOnly() called

        public Flowable<Integer> counts() {
            return createFlowable(b.updateBuilder, db) //
                    .flatMap(Tx.flattenToValuesOnly());
        }
    }

    public Flowable<Tx<Integer>> counts() {
        Flowable<Tx<Integer>> o = createFlowable(updateBuilder, db);
        if (valuesOnly) {
            return o.filter(tx -> tx.isValue());
        } else {
            return o;
        }
    }

    public Flowable<Integer> countsOnly() {
        return valuesOnly().counts();
    }

    @SuppressWarnings("unchecked")
    public Flowable<Tx<?>> tx() {
        return (Flowable<Tx<?>>) (Flowable<?>) createFlowable(updateBuilder, db) //
                .filter(x -> x.isValue());
    }

    private static Flowable<Tx<Integer>> createFlowable(UpdateBuilder ub, Database db) {
        return Flowable.defer(() -> {
            log.debug("creating deferred flowable");
            AtomicReference<Connection> connection = new AtomicReference<Connection>();
            Single<Connection> con = ub.connections //
                    .map(c -> Util.toTransactedConnection(connection, c));
            TxImpl<?>[] t = new TxImpl[1];
            return ub.startWithDependency( //
                    Update.create(con, //
                            ub.parameterGroupsToFlowable(), //
                            ub.sql, //
                            ub.batchSize, //
                            false, //
                            ub.queryTimeoutSec) //
                            .flatMap(n -> Tx.toTx(n, connection.get(), db)) //
                            .doOnNext(tx -> {
                                t[0] = ((TxImpl<Integer>) tx);
                            }) //
                            .doOnComplete(() -> {
                                TxImpl<?> tx = t[0];
                                if (tx.isComplete()) {
                                    tx.connection().commit();
                                }
                                Util.closeSilently(tx.connection());
                            }));
        });
    }

    public Flowable<List<Object>> parameterGroupsToFlowable() {
        return updateBuilder.parameterGroupsToFlowable();
    }
}
