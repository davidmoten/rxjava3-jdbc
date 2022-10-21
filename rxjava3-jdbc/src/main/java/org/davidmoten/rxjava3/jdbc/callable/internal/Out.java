package org.davidmoten.rxjava3.jdbc.callable.internal;

import org.davidmoten.rxjava3.jdbc.Type;

public final class Out implements OutParameterPlaceholder {
    final Type type;

    public Out(Type type) {
        this.type = type;
    }

    @Override
    public Type type() {
        return type;
    }
}