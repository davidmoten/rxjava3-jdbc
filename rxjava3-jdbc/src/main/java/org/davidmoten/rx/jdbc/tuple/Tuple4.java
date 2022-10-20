package org.davidmoten.rx.jdbc.tuple;

/**
 * An explicitly typed tuple.
 * 
 * @param <T1> type of first element
 * @param <T2> type of second element
 * @param <T3> type of third element
 * @param <T4> type of fourth element
 */
public class Tuple4<T1, T2, T3, T4> {

    private final T1 value1;
    private final T2 value2;
    private final T3 value3;
    private final T4 value4;

    /**
     * Constructor.
     * 
     * @param value1 first element
     * @param value2 second element
     * @param value3 third element
     * @param value4 fourth element
     */
    public Tuple4(T1 value1, T2 value2, T3 value3, T4 value4) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> create(T1 value1, T2 value2, T3 value3, T4 value4) {
        return new Tuple4<T1, T2, T3, T4>(value1, value2, value3, value4);
    }

    public T1 value1() {
        return value1;
    }

    public T2 value2() {
        return value2;
    }

    public T3 value3() {
        return value3;
    }

    public T4 value4() {
        return value4;
    }

    public T1 _1() {
        return value1;
    }

    public T2 _2() {
        return value2;
    }

    public T3 _3() {
        return value3;
    }

    public T4 _4() {
        return value4;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
        result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
        result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
        result = prime * result + ((value4 == null) ? 0 : value4.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) obj;
        if (value1 == null) {
            if (other.value1 != null)
                return false;
        } else if (!value1.equals(other.value1))
            return false;
        if (value2 == null) {
            if (other.value2 != null)
                return false;
        } else if (!value2.equals(other.value2))
            return false;
        if (value3 == null) {
            if (other.value3 != null)
                return false;
        } else if (!value3.equals(other.value3))
            return false;
        if (value4 == null) {
            if (other.value4 != null)
                return false;
        } else if (!value4.equals(other.value4))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tuple4 [value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4 + "]";
    }

}
