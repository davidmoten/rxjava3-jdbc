package org.davidmoten.rxjava3.jdbc.pool;

import java.sql.Connection;

import org.davidmoten.rxjava3.jdbc.ConnectionProvider;
import org.davidmoten.rxjava3.jdbc.pool.NonBlockingConnectionPool;
import org.junit.Test;
import org.mockito.Mockito;

public class NonBlockingConnectionPoolTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void testRejectSingletonConnectionProvider() {
        Connection con = Mockito.mock(Connection.class);
        NonBlockingConnectionPool.builder().connectionProvider(ConnectionProvider.from(con));
    }

}
