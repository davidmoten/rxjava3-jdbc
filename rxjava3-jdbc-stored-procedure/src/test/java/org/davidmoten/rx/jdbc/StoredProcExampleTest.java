package org.davidmoten.rx.jdbc;

import org.davidmoten.rxjava3.jdbc.StoredProcExample;
import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class StoredProcExampleTest {
    
    @Test
    public void isUtilClass() {
        Asserts.assertIsUtilityClass(StoredProcExample.class);
    }

}
