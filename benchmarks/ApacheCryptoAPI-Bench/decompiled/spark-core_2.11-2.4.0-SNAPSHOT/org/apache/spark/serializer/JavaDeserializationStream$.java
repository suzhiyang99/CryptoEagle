package org.apache.spark.serializer;

import scala.Predef;
import scala.Predef$;
import scala.Tuple2;
import scala.collection.immutable.Map;

public final class JavaDeserializationStream$ {
   public static final JavaDeserializationStream$ MODULE$;
   private final Map<String, Class<?>> primitiveMappings;

   static {
      new JavaDeserializationStream$();
   }

   public Map<String, Class<?>> primitiveMappings() {
      return this.primitiveMappings;
   }

   private JavaDeserializationStream$() {
      MODULE$ = this;
      this.primitiveMappings = (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("boolean"), Boolean.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("byte"), Byte.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("char"), Character.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("short"), Short.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("int"), Integer.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("long"), Long.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("float"), Float.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("double"), Double.TYPE), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("void"), Void.class)}));
   }
}
