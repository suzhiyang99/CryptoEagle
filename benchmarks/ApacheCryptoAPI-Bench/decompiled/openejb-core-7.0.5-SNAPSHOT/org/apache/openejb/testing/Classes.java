package org.apache.openejb.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Classes {
   Class<?>[] value() default {};

   String[] excludes() default {};

   Class<?>[] cdiInterceptors() default {};

   Class<?>[] cdiDecorators() default {};

   Class<?>[] cdiAlternatives() default {};

   Class<?>[] cdiStereotypes() default {};

   boolean cdi() default false;

   boolean innerClassesAsBean() default false;

   String context() default "openejb";
}
