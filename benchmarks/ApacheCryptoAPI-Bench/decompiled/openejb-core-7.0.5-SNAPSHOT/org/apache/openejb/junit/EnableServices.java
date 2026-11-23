package org.apache.openejb.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @deprecated */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface EnableServices {
   String[] value() default {};

   boolean httpDebug() default false;
}
