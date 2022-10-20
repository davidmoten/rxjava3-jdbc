package org.davidmoten.rxjava3.jdbc.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    final String NOT_SPECIFIED = "*COLUMN_NOT_SPECIFIED*";

    String value() default NOT_SPECIFIED;
}