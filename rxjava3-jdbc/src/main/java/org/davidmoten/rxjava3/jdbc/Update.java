package org.davidmoten.rxjava3.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Notification;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;

final class Update {

    private static final Logger log = LoggerFactory.getLogger(Update.class);

    private Update() {
        // prevent instantiation
    }

    static Flowable<Notification<Integer>> create(Single<Connection> connection,
                                                  Flowable<List<Object>> parameterGroups, String sql, int batchSize,
                                                  boolean eagerDispose, int queryTimeoutSec) {
        return connection //
                .toFlowable() //
                .flatMap(con -> create(con, sql, parameterGroups, batchSize, eagerDispose, queryTimeoutSec), true,
                        1);
    }

    private static Flowable<Notification<Integer>> create(Connection con, String sql,
                                                          Flowable<List<Object>> parameterGroups, int batchSize, boolean eagerDispose, int queryTimeoutSec) {
        log.debug("Update.create {}", sql);
        Supplier<NamedPreparedStatement> resourceFactory = () -> Util.prepare(con, sql, queryTimeoutSec);
        final Function<NamedPreparedStatement, Flowable<Notification<Integer>>> flowableFactory;
        if (batchSize == 0) {
            flowableFactory = ps -> parameterGroups //
                    .flatMap(parameters -> create(ps, Util.toParameters(parameters), sql)
                            .toFlowable()) //
                    .materialize() //
                    .doOnComplete(() -> Util.commit(ps.ps)) //
                    .doOnError(e -> Util.rollback(ps.ps));
        } else {
            flowableFactory = ps -> {
                int[] count = new int[1];
                return parameterGroups //
                        .flatMap(parameters -> {
                            List<Parameter> params = Util.toParameters(parameters);
                            if (Util.hasCollection(params)) {
                                return create(ps, params, sql).toFlowable();
                            } else {
                                Util.incrementCounter(ps.ps.getConnection());
                                count[0] += 1;
                                Flowable<Integer> result;
                                if (count[0] == batchSize) {
                                    count[0] = 0;
                                    result = createExecuteBatch(ps, parameters);
                                } else {
                                    result = createAddBatch(ps, parameters).toFlowable();
                                }
                                return result;
                            }
                        }) //
                        .materialize() //
                        .flatMap(n -> executeFinalBatch(ps, n, count[0] > 0)) //
                        .doOnComplete(() -> Util.commit(ps.ps)) //
                        .doOnError(e -> Util.rollback(ps.ps));
            };
        }
        Consumer<NamedPreparedStatement> disposer = Util::closePreparedStatementAndConnection;
        return Flowable.using(resourceFactory, flowableFactory, disposer, eagerDispose);
    }

    private static Flowable<Notification<Integer>> executeFinalBatch(NamedPreparedStatement ps,
            Notification<Integer> n, boolean outstandingBatch) throws SQLException {
        if (n.isOnComplete() && outstandingBatch) {
            log.debug("executing final batch");
            return toFlowable(ps.ps.executeBatch()) //
                    .map(x -> Notification.createOnNext(x)) //
                    .concatWith(Flowable.just(n));
        } else {
            return Flowable.just(n);
        }
    }

    private static Single<Integer> create(NamedPreparedStatement ps, List<Parameter> params,
            String sql) {
        return Single.fromCallable(() -> {
            Util.incrementCounter(ps.ps.getConnection());
            boolean hasCollection = Util.hasCollection(params);
            PreparedStatement ps2 = null;
            try {
                if (hasCollection) {
                    // create a new prepared statement with the collection ? substituted with
                    // ?s to match the size of the collection parameter
                    ps2 = Util.prepare(ps.ps.getConnection(), 0, sql, params, ps.ps.getQueryTimeout());
                } else {
                    ps2 = ps.ps;
                }
                Util.setParameters(ps2, params, ps.names);
                return ps2.executeUpdate();
            } catch (Throwable e) {
                if (hasCollection && ps2 != null) {
                    ps2.close();
                }
                throw e;
            }
        });
    }

    private static Flowable<Integer> createExecuteBatch(NamedPreparedStatement ps,
            List<Object> parameters) {
        return Flowable.defer(() -> {
            Util.convertAndSetParameters(ps.ps, parameters, ps.names);
            ps.ps.addBatch();
            log.debug("batch added with {}", parameters);
            Flowable<Integer> o = toFlowable(ps.ps.executeBatch());
            log.debug("batch executed");
            return o;
        });
    }

    private static Flowable<Integer> toFlowable(int[] a) {
        return Flowable.range(0, a.length).map(i -> a[i]);
    }

    private static Completable createAddBatch(NamedPreparedStatement ps, List<Object> parameters) {
        return Completable.fromAction(() -> {
            Util.convertAndSetParameters(ps.ps, parameters, ps.names);
            ps.ps.addBatch();
            log.debug("batch added with {}", parameters);
        });
    }

    static <T> Flowable<T> createReturnGeneratedKeys(Single<Connection> connection,
            Flowable<List<Object>> parameterGroups, String sql,
            Function<? super ResultSet, ? extends T> mapper, boolean eagerDispose) {
        return connection //
                .toFlowable() //
                .flatMap(con -> createReturnGeneratedKeys(con, parameterGroups, sql, mapper,
                        eagerDispose), true, 1);
    }

    private static <T> Flowable<T> createReturnGeneratedKeys(Connection con,
            Flowable<List<Object>> parameterGroups, String sql,
            Function<? super ResultSet, T> mapper, boolean eagerDispose) {
        Supplier<NamedPreparedStatement> resourceFactory = () -> Util
                .prepareReturnGeneratedKeys(con, sql);
        Function<NamedPreparedStatement, Flowable<T>> obsFactory = ps -> parameterGroups
                .flatMap(parameters -> create(ps, parameters, mapper), true, 1) //
                .doOnComplete(() -> Util.commit(ps.ps)) //
                .doOnError(e -> Util.rollback(ps.ps));
        Consumer<NamedPreparedStatement> disposer = Util::closePreparedStatementAndConnection;
        return Flowable.using(resourceFactory, obsFactory, disposer, eagerDispose);
    }

    private static <T> Flowable<T> create(NamedPreparedStatement ps, List<Object> parameters,
            Function<? super ResultSet, T> mapper) {
        Supplier<ResultSet> initialState = () -> {
            Util.convertAndSetParameters(ps.ps, parameters, ps.names);
            ps.ps.execute();
            return ps.ps.getGeneratedKeys();
        };
        BiConsumer<ResultSet, Emitter<T>> generator = (rs, emitter) -> {
            if (rs.next()) {
                emitter.onNext(mapper.apply(rs));
            } else {
                emitter.onComplete();
            }
        };
        Consumer<ResultSet> disposer = Util::closeSilently;
        return Flowable.generate(initialState, generator, disposer);
    }

}
