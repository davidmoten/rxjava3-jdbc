package org.davidmoten.rxjava3.jdbc.exceptions;

public final class QueryAnnotationMissingException extends SQLRuntimeException {

    private static final long serialVersionUID = 6725264065633977594L;

    public QueryAnnotationMissingException(String message) {
        super(message);
    }
}
