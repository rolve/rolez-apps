package ch.trick17.rolezapps;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.openjdk.jmh.annotations.Param;

/**
 * Use on a {@link Param} field to provide actual values for
 * string placeholders.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface IntValues {
    int[] value();
}
