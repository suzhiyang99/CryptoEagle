package org.apache.spark.deploy.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.spark.util.Utils$;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods.;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.List;
import scala.collection.mutable.StringBuilder;
import scala.runtime.Nothing$;

public final class SubmitRestProtocolMessage$ {
   public static final SubmitRestProtocolMessage$ MODULE$;
   private final String packagePrefix;
   private final ObjectMapper org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper;

   static {
      new SubmitRestProtocolMessage$();
   }

   private String packagePrefix() {
      return this.packagePrefix;
   }

   public ObjectMapper org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper() {
      return this.org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper;
   }

   public String parseAction(String json) {
      JsonAST.JValue var3 = .MODULE$.parse(org.json4s.package..MODULE$.string2JsonInput(json), .MODULE$.parse$default$2(), .MODULE$.parse$default$3());
      Object value;
      if (var3 instanceof JsonAST.JObject) {
         JsonAST.JObject var4 = (JsonAST.JObject)var3;
         List fields = var4.obj();
         value = fields.collectFirst(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final <A1 extends Tuple2<String, JsonAST.JValue>, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
               Object var6;
               if (x1 != null) {
                  String var4 = (String)x1._1();
                  JsonAST.JValue v = (JsonAST.JValue)x1._2();
                  if ("action".equals(var4)) {
                     var6 = v;
                     return (B1)var6;
                  }
               }

               var6 = var2.apply(x1);
               return (B1)var6;
            }

            public final boolean isDefinedAt(Tuple2<String, JsonAST.JValue> x1) {
               boolean var4;
               if (x1 != null) {
                  String var3 = (String)x1._1();
                  if ("action".equals(var3)) {
                     var4 = true;
                     return var4;
                  }
               }

               var4 = false;
               return var4;
            }
         }).collect(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final <A1 extends JsonAST.JValue, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
               Object var6;
               if (x2 instanceof JsonAST.JString) {
                  JsonAST.JString var4 = (JsonAST.JString)x2;
                  String s = var4.s();
                  var6 = s;
               } else {
                  var6 = var2.apply(x2);
               }

               return (B1)var6;
            }

            public final boolean isDefinedAt(JsonAST.JValue x2) {
               boolean var3;
               if (x2 instanceof JsonAST.JString) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         });
      } else {
         value = None$.MODULE$;
      }

      return (String)((Option)value).getOrElse(new Serializable(json) {
         public static final long serialVersionUID = 0L;
         private final String json$1;

         public final Nothing$ apply() {
            throw new SubmitRestMissingFieldException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Action field not found in JSON:\\n", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.json$1})));
         }

         public {
            this.json$1 = json$1;
         }
      });
   }

   public SubmitRestProtocolMessage fromJson(String json) {
      String className = this.parseAction(json);
      Class clazz = Utils$.MODULE$.classForName((new StringBuilder()).append((Object)this.packagePrefix()).append((Object)".").append((Object)className).toString()).asSubclass(SubmitRestProtocolMessage.class);
      return this.fromJson(json, clazz);
   }

   public <T extends SubmitRestProtocolMessage> T fromJson(String json, Class<T> clazz) {
      return (T)(this.org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper().readValue(json, clazz));
   }

   private SubmitRestProtocolMessage$() {
      MODULE$ = this;
      this.packagePrefix = this.getClass().getPackage().getName();
      this.org$apache$spark$deploy$rest$SubmitRestProtocolMessage$$mapper = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).enable(SerializationFeature.INDENT_OUTPUT).registerModule(com.fasterxml.jackson.module.scala.DefaultScalaModule..MODULE$);
   }
}
