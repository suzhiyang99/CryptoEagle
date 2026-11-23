package org.apache.openejb.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JaxrsProviders {
   Class<?>[] value() default {};

   String applicationName() default "jaxrs-application";
}
