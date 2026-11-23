package org.apache.spark.rdd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.slf4j.Logger;
import org.spark_project.guava.base.Objects;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.reflect.ScalaSignature;
import scala.runtime.Nothing$;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "parent"})
@ScalaSignature(
   bytes = "\u0006\u0001\u0005-g!B\u0001\u0003\u0001\u0011Q!!\u0005*E\t>\u0003XM]1uS>t7kY8qK*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0011q\u0017-\\3\u0004\u0001U\tQ\u0003\u0005\u0002\u001739\u0011AbF\u0005\u000315\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000e\u001c\u0005\u0019\u0019FO]5oO*\u0011\u0001$\u0004\u0005\t;\u0001\u0011\t\u0011)A\u0005+\u0005)a.Y7fA!Aq\u0004\u0001BC\u0002\u0013\u0005\u0001%\u0001\u0004qCJ,g\u000e^\u000b\u0002CA\u0019AB\t\u0013\n\u0005\rj!AB(qi&|g\u000e\u0005\u0002&\u00015\t!\u0001\u0003\u0005(\u0001\t\u0005\t\u0015!\u0003\"\u0003\u001d\u0001\u0018M]3oi\u0002B\u0001\"\u000b\u0001\u0003\u0006\u0004%\t\u0001F\u0001\u0003S\u0012D\u0001b\u000b\u0001\u0003\u0002\u0003\u0006I!F\u0001\u0004S\u0012\u0004\u0003\"B\u0017\u0001\t\u0003q\u0013A\u0002\u001fj]&$h\b\u0006\u0003%_A\n\u0004\"\u0002\n-\u0001\u0004)\u0002bB\u0010-!\u0003\u0005\r!\t\u0005\bS1\u0002\n\u00111\u0001\u0016\u0011\u0015\u0019\u0004\u0001\"\u0001\u0015\u0003\u0019!xNS:p]\")Q\u0007\u0001C\u0001m\u0005aq-\u001a;BY2\u001c6m\u001c9fgV\tq\u0007E\u00029\u0001\u0012r!!\u000f \u000f\u0005ijT\"A\u001e\u000b\u0005q\u001a\u0012A\u0002\u001fs_>$h(C\u0001\u000f\u0013\tyT\"A\u0004qC\u000e\\\u0017mZ3\n\u0005\u0005\u0013%aA*fc*\u0011q(\u0004\u0015\u0003i\u0011\u0003\"!\u0012(\u000e\u0003\u0019S!a\u0012%\u0002\u0015\u0005tgn\u001c;bi&|gN\u0003\u0002J\u0015\u00069!.Y2lg>t'BA&M\u0003%1\u0017m\u001d;feblGNC\u0001N\u0003\r\u0019w.\\\u0005\u0003\u001f\u001a\u0013!BS:p]&;gn\u001c:f\u0011\u0015\t\u0006\u0001\"\u0011S\u0003\u0019)\u0017/^1mgR\u00111K\u0016\t\u0003\u0019QK!!V\u0007\u0003\u000f\t{w\u000e\\3b]\")q\u000b\u0015a\u00011\u0006)q\u000e\u001e5feB\u0011A\"W\u0005\u000356\u00111!\u00118z\u0011\u0015a\u0006\u0001\"\u0011^\u0003!A\u0017m\u001d5D_\u0012,G#\u00010\u0011\u00051y\u0016B\u00011\u000e\u0005\rIe\u000e\u001e\u0005\u0006E\u0002!\teY\u0001\ti>\u001cFO]5oOR\tQ\u0003\u000b\u0003\u0001K\"L\u0007CA#g\u0013\t9gIA\tKg>t\u0007K]8qKJ$\u0018p\u0014:eKJ\fQA^1mk\u0016d3A[6mC\u0005I\u0013%\u0001\n\"\u0003}AC\u0001\u00018icB\u0011Qi\\\u0005\u0003a\u001a\u00131BS:p]&s7\r\\;eK\u0012\n!/\u0003\u0002ti\u0006Aaj\u0014(`\u001dVcEJ\u0003\u0002vm\u00069\u0011J\\2mk\u0012,'BA<G\u0003-Q5o\u001c8J]\u000edW\u000fZ3\b\re\u0014\u0001\u0012\u0001\u0003{\u0003E\u0011F\tR(qKJ\fG/[8o'\u000e|\u0007/\u001a\t\u0003Km4a!\u0001\u0002\t\u0002\u0011a8cA>\f{B\u0019a0a\u0001\u000e\u0003}T1!!\u0001\u0005\u0003!Ig\u000e^3s]\u0006d\u0017bAA\u0003\u007f\n9Aj\\4hS:<\u0007BB\u0017|\t\u0003\tI\u0001F\u0001{\u0011%\tia\u001fb\u0001\n\u0013\ty!\u0001\u0006kg>tW*\u00199qKJ,\"!!\u0005\u0011\t\u0005M\u0011\u0011D\u0007\u0003\u0003+Q1!a\u0006I\u0003!!\u0017\r^1cS:$\u0017\u0002BA\u000e\u0003+\u0011Ab\u00142kK\u000e$X*\u00199qKJD\u0001\"a\b|A\u0003%\u0011\u0011C\u0001\fUN|g.T1qa\u0016\u0014\b\u0005C\u0005\u0002$m\u0014\r\u0011\"\u0003\u0002&\u0005a1oY8qK\u000e{WO\u001c;feV\u0011\u0011q\u0005\t\u0005\u0003S\tY$\u0004\u0002\u0002,)!\u0011QFA\u0018\u0003\u0019\tGo\\7jG*!\u0011\u0011GA\u001a\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u0005\u0003k\t9$\u0001\u0003vi&d'BAA\u001d\u0003\u0011Q\u0017M^1\n\t\u0005u\u00121\u0006\u0002\u000e\u0003R|W.[2J]R,w-\u001a:\t\u0011\u0005\u00053\u0010)A\u0005\u0003O\tQb]2pa\u0016\u001cu.\u001e8uKJ\u0004\u0003bBA#w\u0012\u0005\u0011qI\u0001\tMJ|WNS:p]R\u0019A%!\u0013\t\u000f\u0005-\u00131\ta\u0001+\u0005\t1\u000f\u0003\u0004\u0002Pm$\t!X\u0001\f]\u0016DHoU2pa\u0016LE\r\u0003\u0005\u0002Tm$\t\u0001BA+\u0003%9\u0018\u000e\u001e5TG>\u0004X-\u0006\u0003\u0002X\u0005}CCBA-\u0003k\n\t\t\u0006\u0003\u0002\\\u0005-\u0004\u0003BA/\u0003?b\u0001\u0001\u0002\u0005\u0002b\u0005E#\u0019AA2\u0005\u0005!\u0016cAA31B\u0019A\"a\u001a\n\u0007\u0005%TBA\u0004O_RD\u0017N\\4\t\u0013\u00055\u0014\u0011\u000bCA\u0002\u0005=\u0014\u0001\u00022pIf\u0004R\u0001DA9\u00037J1!a\u001d\u000e\u0005!a$-\u001f8b[\u0016t\u0004\u0002CA<\u0003#\u0002\r!!\u001f\u0002\u0005M\u001c\u0007\u0003BA>\u0003{j\u0011\u0001B\u0005\u0004\u0003\u007f\"!\u0001D*qCJ\\7i\u001c8uKb$\b\"CAB\u0003#\u0002\n\u00111\u0001T\u00031\tG\u000e\\8x\u001d\u0016\u001cH/\u001b8h\u0011!\t\u0019f\u001fC\u0001\t\u0005\u001dU\u0003BAE\u0003\u001f#\"\"a#\u0002\u0016\u0006]\u0015\u0011TAN)\u0011\ti)!%\u0011\t\u0005u\u0013q\u0012\u0003\t\u0003C\n)I1\u0001\u0002d!I\u0011QNAC\t\u0003\u0007\u00111\u0013\t\u0006\u0019\u0005E\u0014Q\u0012\u0005\t\u0003o\n)\t1\u0001\u0002z!1!#!\"A\u0002UAq!a!\u0002\u0006\u0002\u00071\u000bC\u0004\u0002\u001e\u0006\u0015\u0005\u0019A*\u0002\u0019%<gn\u001c:f!\u0006\u0014XM\u001c;\t\u0013\u0005\u000560%A\u0005\u0002\u0005\r\u0016aE<ji\"\u001c6m\u001c9fI\u0011,g-Y;mi\u0012\u0012T\u0003BAS\u0003s+\"!a*+\u0007M\u000bIk\u000b\u0002\u0002,B!\u0011QVA[\u001b\t\tyK\u0003\u0003\u00022\u0006M\u0016!C;oG\",7m[3e\u0015\t9U\"\u0003\u0003\u00028\u0006=&!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u0012A\u0011\u0011MAP\u0005\u0004\t\u0019\u0007C\u0005\u0002>n\f\n\u0011\"\u0001\u0002@\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uII*\"!!1+\u0007\u0005\nI\u000bC\u0005\u0002Fn\f\n\u0011\"\u0001\u0002H\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIM*\"!!3+\u0007U\tI\u000b"
)
public class RDDOperationScope {
   private final String name;
   private final Option<RDDOperationScope> parent;
   private final String id;

   public static boolean initializeLogIfNecessary$default$2() {
      return RDDOperationScope$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return RDDOperationScope$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      RDDOperationScope$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return RDDOperationScope$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      RDDOperationScope$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      RDDOperationScope$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      RDDOperationScope$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      RDDOperationScope$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      RDDOperationScope$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      RDDOperationScope$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      RDDOperationScope$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      RDDOperationScope$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      RDDOperationScope$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      RDDOperationScope$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return RDDOperationScope$.MODULE$.log();
   }

   public static String logName() {
      return RDDOperationScope$.MODULE$.logName();
   }

   public static String $lessinit$greater$default$3() {
      return RDDOperationScope$.MODULE$.$lessinit$greater$default$3();
   }

   public static Option<RDDOperationScope> $lessinit$greater$default$2() {
      return RDDOperationScope$.MODULE$.$lessinit$greater$default$2();
   }

   public static <T> boolean withScope$default$2() {
      return RDDOperationScope$.MODULE$.withScope$default$2();
   }

   public static int nextScopeId() {
      return RDDOperationScope$.MODULE$.nextScopeId();
   }

   public static RDDOperationScope fromJson(String var0) {
      return RDDOperationScope$.MODULE$.fromJson(var0);
   }

   public String name() {
      return this.name;
   }

   public Option<RDDOperationScope> parent() {
      return this.parent;
   }

   public String id() {
      return this.id;
   }

   public String toJson() {
      return RDDOperationScope$.MODULE$.org$apache$spark$rdd$RDDOperationScope$$jsonMapper().writeValueAsString(this);
   }

   @JsonIgnore
   public Seq<RDDOperationScope> getAllScopes() {
      return (Seq)((TraversableLike)this.parent().<RDDOperationScope>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<RDDOperationScope> apply(RDDOperationScope x$1) {
            return x$1.getAllScopes();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Nothing$> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      })).$plus$plus(Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new RDDOperationScope[]{this})), Seq$.MODULE$.canBuildFrom());
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof RDDOperationScope) {
         boolean var10;
         label37: {
            label36: {
               label41: {
                  RDDOperationScope var3 = (RDDOperationScope)other;
                  String var10000 = this.id();
                  String var5 = var3.id();
                  if (var10000 == null) {
                     if (var5 != null) {
                        break label41;
                     }
                  } else if (!var10000.equals(var5)) {
                     break label41;
                  }

                  var10000 = this.name();
                  String var6 = var3.name();
                  if (var10000 == null) {
                     if (var6 != null) {
                        break label41;
                     }
                  } else if (!var10000.equals(var6)) {
                     break label41;
                  }

                  Option var9 = this.parent();
                  Option var7 = var3.parent();
                  if (var9 == null) {
                     if (var7 == null) {
                        break label36;
                     }
                  } else if (var9.equals(var7)) {
                     break label36;
                  }
               }

               var10 = false;
               break label37;
            }

            var10 = true;
         }

         var4 = var10;
      } else {
         var4 = false;
      }

      return var4;
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.id(), this.name(), this.parent()});
   }

   public String toString() {
      return this.toJson();
   }

   public RDDOperationScope(String name, Option<RDDOperationScope> parent, String id) {
      this.name = name;
      this.parent = parent;
      this.id = id;
   }
}
