package org.apache.openejb.util.doc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE, ElementType.PARAMETER})
public @interface Revisit {
   String value();
}
