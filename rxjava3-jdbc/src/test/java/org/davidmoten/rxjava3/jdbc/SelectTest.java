package org.davidmoten.rxjava3.jdbc;

import org.davidmoten.rxjava3.jdbc.Select;
import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class SelectTest {

    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Select.class);
    }

}
