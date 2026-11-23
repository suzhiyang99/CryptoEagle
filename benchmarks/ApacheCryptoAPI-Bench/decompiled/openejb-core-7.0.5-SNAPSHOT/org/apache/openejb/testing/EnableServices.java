package org.apache.openejb.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableServices {
   String[] value() default {};

   boolean jaxrs() default false;

   boolean jaxws() default false;

   boolean httpDebug() default false;
}
