package org.davidmoten.rxjava3.jdbc.callable.internal;

import org.davidmoten.rxjava3.jdbc.Type;

public final class InOut implements InParameterPlaceholder, OutParameterPlaceholder {
    final Type type;

    public InOut(Type type) {
        this.type = type;
    }

    @Override
    public Type type() {
        return type;
    }
}
