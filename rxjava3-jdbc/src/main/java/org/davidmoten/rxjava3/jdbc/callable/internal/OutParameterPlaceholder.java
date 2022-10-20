package org.davidmoten.rxjava3.jdbc.callable.internal;

import org.davidmoten.rxjava3.jdbc.Type;

public interface OutParameterPlaceholder extends ParameterPlaceholder {
    Type type();
}
