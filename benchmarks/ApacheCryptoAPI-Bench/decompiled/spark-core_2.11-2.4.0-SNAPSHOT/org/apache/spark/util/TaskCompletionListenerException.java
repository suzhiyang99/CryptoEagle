package org.apache.spark.util;

import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00054Q!\u0001\u0002\u0001\t)\u0011q\u0004V1tW\u000e{W\u000e\u001d7fi&|g\u000eT5ti\u0016tWM]#yG\u0016\u0004H/[8o\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0018\u001d\tiAC\u0004\u0002\u000f%5\tqB\u0003\u0002\u0011#\u00051AH]8piz\u001a\u0001!C\u0001\u0014\u0003\u0015\u00198-\u00197b\u0013\t)b#A\u0004qC\u000e\\\u0017mZ3\u000b\u0003MI!\u0001G\r\u0003!I+h\u000e^5nK\u0016C8-\u001a9uS>t'BA\u000b\u0017\u0011!Y\u0002A!A!\u0002\u0013a\u0012!D3se>\u0014X*Z:tC\u001e,7\u000fE\u0002\r;}I!AH\r\u0003\u0007M+\u0017\u000f\u0005\u0002!I9\u0011\u0011EI\u0007\u0002-%\u00111EF\u0001\u0007!J,G-\u001a4\n\u0005\u00152#AB*ue&twM\u0003\u0002$-!A\u0001\u0006\u0001BC\u0002\u0013\u0005\u0011&A\u0007qe\u00164\u0018n\\;t\u000bJ\u0014xN]\u000b\u0002UA\u0019\u0011eK\u0017\n\u000512\"AB(qi&|g\u000e\u0005\u0002\r]%\u0011q&\u0007\u0002\n)\"\u0014xn^1cY\u0016D\u0001\"\r\u0001\u0003\u0002\u0003\u0006IAK\u0001\u000faJ,g/[8vg\u0016\u0013(o\u001c:!\u0011\u0015\u0019\u0004\u0001\"\u00015\u0003\u0019a\u0014N\\5u}Q\u0019Qg\u000e\u001d\u0011\u0005Y\u0002Q\"\u0001\u0002\t\u000bm\u0011\u0004\u0019\u0001\u000f\t\u000f!\u0012\u0004\u0013!a\u0001U!)!\b\u0001C!w\u0005Qq-\u001a;NKN\u001c\u0018mZ3\u0015\u0003}9\u0001\"\u0010\u0002\u0002\u0002#\u0005AAP\u0001 )\u0006\u001c8nQ8na2,G/[8o\u0019&\u001cH/\u001a8fe\u0016C8-\u001a9uS>t\u0007C\u0001\u001c@\r!\t!!!A\t\u0002\u0011\u00015cA B\tB\u0011\u0011EQ\u0005\u0003\u0007Z\u0011a!\u00118z%\u00164\u0007CA\u0011F\u0013\t1eC\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u00034\u007f\u0011\u0005\u0001\nF\u0001?\u0011\u001dQu(%A\u0005\u0002-\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u0012T#\u0001'+\u0005)j5&\u0001(\u0011\u0005=#V\"\u0001)\u000b\u0005E\u0013\u0016!C;oG\",7m[3e\u0015\t\u0019f#\u0001\u0006b]:|G/\u0019;j_:L!!\u0016)\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004X\u007f\u0005\u0005I\u0011\u0002-\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u00023B\u0011!lX\u0007\u00027*\u0011A,X\u0001\u0005Y\u0006twMC\u0001_\u0003\u0011Q\u0017M^1\n\u0005\u0001\\&AB(cU\u0016\u001cG\u000f"
)
public class TaskCompletionListenerException extends RuntimeException {
   private final Seq<String> errorMessages;
   private final Option<Throwable> previousError;

   public static Option<Throwable> $lessinit$greater$default$2() {
      return TaskCompletionListenerException$.MODULE$.$lessinit$greater$default$2();
   }

   public Option<Throwable> previousError() {
      return this.previousError;
   }

   public String getMessage() {
      String listenerErrorMessage = this.errorMessages.size() == 1 ? (String)this.errorMessages.head() : ((TraversableOnce)((TraversableLike)this.errorMessages.zipWithIndex(Seq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Tuple2<String, Object> x0$1) {
            if (x0$1 != null) {
               String msg = (String)x0$1._1();
               int i = x0$1._2$mcI$sp();
               String var5 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(i), msg}));
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).mkString("\n");
      String previousErrorMessage = (String)this.previousError().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Throwable e) {
            return (new StringBuilder()).append((Object)"\n\nPrevious exception in task: ").append((Object)e.getMessage()).append((Object)"\n").append((Object)Predef$.MODULE$.refArrayOps((Object[])e.getStackTrace()).mkString("\t", "\n\t", "")).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      });
      return (new StringBuilder()).append((Object)listenerErrorMessage).append((Object)previousErrorMessage).toString();
   }

   public TaskCompletionListenerException(Seq<String> errorMessages, Option<Throwable> previousError) {
      this.errorMessages = errorMessages;
      this.previousError = previousError;
   }
}
