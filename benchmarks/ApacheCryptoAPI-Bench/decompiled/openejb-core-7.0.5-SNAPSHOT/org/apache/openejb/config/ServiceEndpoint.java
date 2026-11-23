package org.apache.openejb.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceEndpoint {
   Class[] value() default {};
}
