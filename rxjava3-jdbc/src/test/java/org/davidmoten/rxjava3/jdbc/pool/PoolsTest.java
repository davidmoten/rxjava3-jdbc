package org.davidmoten.rxjava3.jdbc.pool;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class PoolsTest {
    
    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Pools.class);
    }

}
