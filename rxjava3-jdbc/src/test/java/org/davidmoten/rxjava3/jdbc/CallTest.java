package org.davidmoten.rxjava3.jdbc;

import org.davidmoten.rxjava3.jdbc.Call;
import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class CallTest {
    
    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Call.class);
    }

}
