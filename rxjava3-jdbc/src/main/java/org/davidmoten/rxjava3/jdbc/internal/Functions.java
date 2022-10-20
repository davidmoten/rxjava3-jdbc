package org.davidmoten.rxjava3.jdbc.internal;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.functions.Function;

public final class Functions {

    /** A singleton identity function. */
    static final Function<Object, Object> IDENTITY = new Identity();

    /**
     * Returns an identity function that simply returns its argument.
     * @param <T> the input and output value type
     * @return the identity function
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> Function<T, T> identity() {
        return (Function<T, T>)IDENTITY;
    }
    
    static final class Identity implements Function<Object, Object> {
        @Override
        public Object apply(Object v) {
            return v;
        }

        @Override
        public String toString() {
            return "IdentityFunction";
        }
    }

    
}
