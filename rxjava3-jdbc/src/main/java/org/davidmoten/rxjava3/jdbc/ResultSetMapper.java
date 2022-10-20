package org.davidmoten.rxjava3.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import io.reactivex.rxjava3.functions.Function;

public interface ResultSetMapper<T> extends Function<ResultSet, T>{

    @Override
    T apply(@Nonnull ResultSet rs) throws SQLException;
}
