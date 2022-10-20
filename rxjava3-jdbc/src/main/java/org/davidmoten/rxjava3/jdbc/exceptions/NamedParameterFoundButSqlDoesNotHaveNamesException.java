package org.davidmoten.rxjava3.jdbc.exceptions;

public final class NamedParameterFoundButSqlDoesNotHaveNamesException extends SQLRuntimeException {

    private static final long serialVersionUID = -1318412883565459579L;
    
    public NamedParameterFoundButSqlDoesNotHaveNamesException(String message) {
        super(message);
    }

}
