package org.apache.openejb.monitoring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ManagedCollection {
   String description() default "";

   String key() default "";

   boolean append() default false;

   Class<?> type();
}
