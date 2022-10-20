package org.davidmoten.rxjava3.jdbc.pool.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.davidmoten.rxjava3.jdbc.ConnectionProvider;
import org.davidmoten.rxjava3.jdbc.exceptions.SQLRuntimeException;
import org.davidmoten.rxjava3.jdbc.internal.DelegatedConnection;
import org.davidmoten.rxjava3.pool.Member;
import org.davidmoten.rxjava3.pool.Pool;

import com.github.davidmoten.guavamini.Preconditions;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public final class ConnectionProviderBlockingPool implements Pool<Connection> {

    private final ConnectionProvider connectionProvider;

    public ConnectionProviderBlockingPool(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Single<Member<Connection>> member() {
        return Single.fromCallable(() -> new MemberWithValueConnection(connectionProvider));
    }

    @Override
    public void close() throws Exception {
        connectionProvider.close();
    }

    static final class MemberWithValueConnection implements Member<Connection>, DelegatedConnection {

        private final ConnectionProvider connectionProvider;

        public MemberWithValueConnection(ConnectionProvider cp) {
            this.connectionProvider = cp;
        }

        volatile PooledConnection connection;
        final AtomicBoolean hasConnection = new AtomicBoolean();

        @Override
        public Connection con() {
            if (hasConnection.compareAndSet(false, true)) {
                // blocking
                Connection c = connectionProvider.get();
                Preconditions.checkNotNull(c, "connectionProvider should not return null");
                connection = new PooledConnection(c, this);
            }
            return connection;
        }

        @Override
        public void checkin() {
            try {
                connection.con().close();
            } catch (SQLException e) {
                throw new SQLRuntimeException(e);
            }
        }

        @Override
        public Connection value() {
            return con();
        }

        @Override
        public void disposeValue() {
            try {
                connection.con().close();
            } catch (SQLException e) {
                RxJavaPlugins.onError(e);
            }
        }
    }
}
