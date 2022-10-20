package org.davidmoten.rxjava3.jdbc.callable.internal;

import org.davidmoten.rxjava3.jdbc.Type;

public final class Out implements OutParameterPlaceholder {
    final Type type;
    final Class<?> cls;

    public Out(Type type, Class<?> cls) {
        this.type = type;
        this.cls = cls;
    }

    @Override
    public Type type() {
        return type;
    }
}