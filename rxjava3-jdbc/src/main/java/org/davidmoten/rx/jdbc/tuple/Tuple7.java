package org.davidmoten.rx.jdbc.tuple;

import java.util.Objects;

/**
 * An explicitly typed tuple.
 * 
 * @param <T1>
 *            type of first element
 * @param <T2>
 *            type of second element
 * @param <T3>
 *            type of third element
 * @param <T4>
 *            type of fourth element
 * @param <T5>
 *            type of fifth element
 * @param <T6>
 *            type of sixth element
 * @param <T7>
 *            type of seventh element
 */
public class Tuple7<T1, T2, T3, T4, T5, T6, T7> {

    private final T1 value1;
    private final T2 value2;
    private final T3 value3;
    private final T4 value4;
    private final T5 value5;
    private final T6 value6;
    private final T7 value7;

    /**
     * Constructor.
     * 
     * @param value1
     *            first element
     * @param value2
     *            second element
     * @param value3
     *            third element
     * @param value4
     *            fourth element
     * @param value5
     *            fifth element
     * @param value6
     *            sixth element
     * @param value7
     *            seventh element
     */
    public Tuple7(T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.value5 = value5;
        this.value6 = value6;
        this.value7 = value7;
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> create(T1 value1,
            T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7) {
        return new Tuple7<T1, T2, T3, T4, T5, T6, T7>(value1, value2, value3, value4, value5,
                value6, value7);
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

    public T5 value5() {
        return value5;
    }

    public T6 value6() {
        return value6;
    }

    public T7 value7() {
        return value7;
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

    public T5 _5() {
        return value5;
    }

    public T6 _6() {
        return value6;
    }

    public T7 _7() {
        return value7;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
        result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
        result = prime * result + ((value3 == null) ? 0 : value3.hashCode());
        result = prime * result + ((value4 == null) ? 0 : value4.hashCode());
        result = prime * result + ((value5 == null) ? 0 : value5.hashCode());
        result = prime * result + ((value6 == null) ? 0 : value6.hashCode());
        result = prime * result + ((value7 == null) ? 0 : value7.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        final Tuple7 other = (Tuple7) obj;
        return Objects.equals(value1, other.value1) //
                && Objects.equals(value2, other.value2) //
                && Objects.equals(value3, other.value3) //
                && Objects.equals(value4, other.value4) //
                && Objects.equals(value5, other.value5) //
                && Objects.equals(value6, other.value6) //
                && Objects.equals(value7, other.value7);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tuple7 [value1=");
        builder.append(value1);
        builder.append(", value2=");
        builder.append(value2);
        builder.append(", value3=");
        builder.append(value3);
        builder.append(", value4=");
        builder.append(value4);
        builder.append(", value5=");
        builder.append(value5);
        builder.append(", value6=");
        builder.append(value6);
        builder.append(", value7=");
        builder.append(value7);
        builder.append("]");
        return builder.toString();
    }

}
