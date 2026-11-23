package org.apache.openejb.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @deprecated */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface AppResource {
}
