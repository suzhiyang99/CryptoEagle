package org.apache.spark.deploy.rest;

import org.apache.spark.util.Utils$;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;
import scala.runtime.Null$;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005a!B\u0001\u0003\u0001\ta!aF\"sK\u0006$XmU;c[&\u001c8/[8o%\u0016\fX/Z:u\u0015\t\u0019A!\u0001\u0003sKN$(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011abD\u0007\u0002\u0005%\u0011\u0001C\u0001\u0002\u001a'V\u0014W.\u001b;SKN$\bK]8u_\u000e|GNU3rk\u0016\u001cH\u000fC\u0003\u0013\u0001\u0011\u0005A#\u0001\u0004=S:LGOP\u0002\u0001)\u0005)\u0002C\u0001\b\u0001\u0011\u001d9\u0002\u00011A\u0005\u0002a\t1\"\u00199q%\u0016\u001cx.\u001e:dKV\t\u0011\u0004\u0005\u0002\u001bA9\u00111DH\u0007\u00029)\tQ$A\u0003tG\u0006d\u0017-\u0003\u0002 9\u00051\u0001K]3eK\u001aL!!\t\u0012\u0003\rM#(/\u001b8h\u0015\tyB\u0004C\u0004%\u0001\u0001\u0007I\u0011A\u0013\u0002\u001f\u0005\u0004\bOU3t_V\u00148-Z0%KF$\"AJ\u0015\u0011\u0005m9\u0013B\u0001\u0015\u001d\u0005\u0011)f.\u001b;\t\u000f)\u001a\u0013\u0011!a\u00013\u0005\u0019\u0001\u0010J\u0019\t\r1\u0002\u0001\u0015)\u0003\u001a\u00031\t\u0007\u000f\u001d*fg>,(oY3!\u0011\u001dq\u0003\u00011A\u0005\u0002a\t\u0011\"\\1j]\u000ec\u0017m]:\t\u000fA\u0002\u0001\u0019!C\u0001c\u0005iQ.Y5o\u00072\f7o]0%KF$\"A\n\u001a\t\u000f)z\u0013\u0011!a\u00013!1A\u0007\u0001Q!\ne\t!\"\\1j]\u000ec\u0017m]:!\u0011\u001d1\u0004\u00011A\u0005\u0002]\nq!\u00199q\u0003J<7/F\u00019!\rY\u0012(G\u0005\u0003uq\u0011Q!\u0011:sCfDq\u0001\u0010\u0001A\u0002\u0013\u0005Q(A\u0006baB\f%oZ:`I\u0015\fHC\u0001\u0014?\u0011\u001dQ3(!AA\u0002aBa\u0001\u0011\u0001!B\u0013A\u0014\u0001C1qa\u0006\u0013xm\u001d\u0011\t\u000f\t\u0003\u0001\u0019!C\u0001\u0007\u0006y1\u000f]1sWB\u0013x\u000e]3si&,7/F\u0001E!\u0011QR)G\r\n\u0005\u0019\u0013#aA'ba\"9\u0001\n\u0001a\u0001\n\u0003I\u0015aE:qCJ\\\u0007K]8qKJ$\u0018.Z:`I\u0015\fHC\u0001\u0014K\u0011\u001dQs)!AA\u0002\u0011Ca\u0001\u0014\u0001!B\u0013!\u0015\u0001E:qCJ\\\u0007K]8qKJ$\u0018.Z:!\u0011\u001dq\u0005\u00011A\u0005\u0002\r\u000bA#\u001a8wSJ|g.\\3oiZ\u000b'/[1cY\u0016\u001c\bb\u0002)\u0001\u0001\u0004%\t!U\u0001\u0019K:4\u0018N]8o[\u0016tGOV1sS\u0006\u0014G.Z:`I\u0015\fHC\u0001\u0014S\u0011\u001dQs*!AA\u0002\u0011Ca\u0001\u0016\u0001!B\u0013!\u0015!F3om&\u0014xN\\7f]R4\u0016M]5bE2,7\u000f\t\u0005\u0006-\u0002!\tfV\u0001\u000bI>4\u0016\r\\5eCR,G#\u0001\u0014\t\u000be\u0003A\u0011\u0002.\u0002'\u0005\u001c8/\u001a:u!J|\u0007/\u001a:us&\u001b8+\u001a;\u0015\u0005\u0019Z\u0006\"\u0002/Y\u0001\u0004I\u0012aA6fs\")a\f\u0001C\u0005?\u00069\u0012m]:feR\u0004&o\u001c9feRL\u0018j\u001d\"p_2,\u0017M\u001c\u000b\u0003M\u0001DQ\u0001X/A\u0002eAQA\u0019\u0001\u0005\n\r\fq#Y:tKJ$\bK]8qKJ$\u00180S:Ok6,'/[2\u0015\u0005\u0019\"\u0007\"\u0002/b\u0001\u0004I\u0002\"\u00024\u0001\t\u00139\u0017AF1tg\u0016\u0014H\u000f\u0015:pa\u0016\u0014H/_%t\u001b\u0016lwN]=\u0015\u0005\u0019B\u0007\"\u0002/f\u0001\u0004I\u0002\"\u00026\u0001\t\u0013Y\u0017AD1tg\u0016\u0014H\u000f\u0015:pa\u0016\u0014H/_\u000b\u0003Y^$BAJ7oa\")A,\u001ba\u00013!)q.\u001ba\u00013\u0005Ia/\u00197vKRK\b/\u001a\u0005\u0006c&\u0004\rA]\u0001\bG>tg/\u001a:u!\u0011Y2/G;\n\u0005Qd\"!\u0003$v]\u000e$\u0018n\u001c82!\t1x\u000f\u0004\u0001\u0005\u000baL'\u0019A=\u0003\u0003Q\u000b\"A_?\u0011\u0005mY\u0018B\u0001?\u001d\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\u0007@\n\u0005}d\"aA!os\u0002"
)
public class CreateSubmissionRequest extends SubmitRestProtocolRequest {
   private String appResource = null;
   private String mainClass = null;
   private String[] appArgs = null;
   private Map<String, String> sparkProperties = null;
   private Map<String, String> environmentVariables = null;

   public String appResource() {
      return this.appResource;
   }

   public void appResource_$eq(String x$1) {
      this.appResource = x$1;
   }

   public String mainClass() {
      return this.mainClass;
   }

   public void mainClass_$eq(String x$1) {
      this.mainClass = x$1;
   }

   public String[] appArgs() {
      return this.appArgs;
   }

   public void appArgs_$eq(String[] x$1) {
      this.appArgs = x$1;
   }

   public Map<String, String> sparkProperties() {
      return this.sparkProperties;
   }

   public void sparkProperties_$eq(Map<String, String> x$1) {
      this.sparkProperties = x$1;
   }

   public Map<String, String> environmentVariables() {
      return this.environmentVariables;
   }

   public void environmentVariables_$eq(Map<String, String> x$1) {
      this.environmentVariables = x$1;
   }

   public void doValidate() {
      super.doValidate();
      this.assert(this.sparkProperties() != null, "No Spark properties set!");
      this.assertFieldIsSet(this.appResource(), "appResource");
      this.assertFieldIsSet(this.appArgs(), "appArgs");
      this.assertFieldIsSet(this.environmentVariables(), "environmentVariables");
      this.assertPropertyIsSet("spark.app.name");
      this.assertPropertyIsBoolean("spark.driver.supervise");
      this.assertPropertyIsNumeric("spark.driver.cores");
      this.assertPropertyIsNumeric("spark.cores.max");
      this.assertPropertyIsMemory("spark.driver.memory");
      this.assertPropertyIsMemory("spark.executor.memory");
   }

   private void assertPropertyIsSet(String key) {
      this.assertFieldIsSet(this.sparkProperties().getOrElse(key, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Null$ apply() {
            return null;
         }
      }), key);
   }

   private void assertPropertyIsBoolean(String key) {
      this.assertProperty(key, "boolean", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$1) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$1))).toBoolean();
         }
      });
   }

   private void assertPropertyIsNumeric(String key) {
      this.assertProperty(key, "numeric", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(String x$2) {
            return (new StringOps(Predef$.MODULE$.augmentString(x$2))).toDouble();
         }
      });
   }

   private void assertPropertyIsMemory(String key) {
      this.assertProperty(key, "memory", new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(String str) {
            return Utils$.MODULE$.memoryStringToMb(str);
         }
      });
   }

   private <T> void assertProperty(String key, String valueType, Function1<String, T> convert) {
      this.sparkProperties().get(key).foreach(new Serializable(this, key, valueType, convert) {
         public static final long serialVersionUID = 0L;
         public final String key$1;
         public final String valueType$1;
         public final Function1 convert$1;

         public final T apply(String value) {
            return (T)Try$.MODULE$.apply(new Serializable(this, value) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String value$1;

               public final T apply() {
                  return (T)this.$outer.convert$1.apply(this.value$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.value$1 = value$1;
                  }
               }
            }).getOrElse(new Serializable(this, value) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String value$1;

               public final Nothing$ apply() {
                  throw new SubmitRestProtocolException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Property '", "' expected ", " value: actual was '", "'."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.key$1, this.$outer.valueType$1, this.value$1})), SubmitRestProtocolException$.MODULE$.$lessinit$greater$default$2());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.value$1 = value$1;
                  }
               }
            });
         }

         public {
            this.key$1 = key$1;
            this.valueType$1 = valueType$1;
            this.convert$1 = convert$1;
         }
      });
   }
}
