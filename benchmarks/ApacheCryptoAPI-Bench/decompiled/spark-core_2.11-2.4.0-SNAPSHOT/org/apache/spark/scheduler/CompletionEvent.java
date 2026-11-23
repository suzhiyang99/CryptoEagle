package org.apache.spark.scheduler;

import org.apache.spark.TaskEndReason;
import org.apache.spark.util.AccumulatorV2;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple5;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\tub!B\u0001\u0003\u0001\nQ!aD\"p[BdW\r^5p]\u00163XM\u001c;\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7#\u0002\u0001\f#UA\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\u0013'5\t!!\u0003\u0002\u0015\u0005\t\tB)Q$TG\",G-\u001e7fe\u00163XM\u001c;\u0011\u000511\u0012BA\f\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D\r\n\u0005ii!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\u000f\u0001\u0005+\u0007I\u0011\u0001\u0010\u0002\tQ\f7o[\u0002\u0001+\u0005y\u0002G\u0001\u0011&!\r\u0011\u0012eI\u0005\u0003E\t\u0011A\u0001V1tWB\u0011A%\n\u0007\u0001\t%1s%!A\u0001\u0002\u000b\u0005QFA\u0002`IaB\u0001\u0002\u000b\u0001\u0003\u0012\u0003\u0006I!K\u0001\u0006i\u0006\u001c8\u000e\t\u0019\u0003U1\u00022AE\u0011,!\t!C\u0006B\u0005'O\u0005\u0005\t\u0011!B\u0001[E\u0011a&\r\t\u0003\u0019=J!\u0001M\u0007\u0003\u000f9{G\u000f[5oOB\u0011ABM\u0005\u0003g5\u00111!\u00118z\u0011!)\u0004A!f\u0001\n\u00031\u0014A\u0002:fCN|g.F\u00018!\tA\u0014(D\u0001\u0005\u0013\tQDAA\u0007UCN\\WI\u001c3SK\u0006\u001cxN\u001c\u0005\ty\u0001\u0011\t\u0012)A\u0005o\u00059!/Z1t_:\u0004\u0003\u0002\u0003 \u0001\u0005+\u0007I\u0011A \u0002\rI,7/\u001e7u+\u0005\t\u0004\u0002C!\u0001\u0005#\u0005\u000b\u0011B\u0019\u0002\u000fI,7/\u001e7uA!A1\t\u0001BK\u0002\u0013\u0005A)\u0001\u0007bG\u000e,X.\u00169eCR,7/F\u0001F!\r1e*\u0015\b\u0003\u000f2s!\u0001S&\u000e\u0003%S!AS\u000f\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011BA'\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!a\u0014)\u0003\u0007M+\u0017O\u0003\u0002N\u001bA\u001a!+\u00174\u0011\tM3\u0006,Z\u0007\u0002)*\u0011Q\u000bB\u0001\u0005kRLG.\u0003\u0002X)\ni\u0011iY2v[Vd\u0017\r^8s-J\u0002\"\u0001J-\u0005\u0013i[\u0016\u0011!A\u0001\u0006\u0003i#aA0%s!AA\f\u0001B\tB\u0003%Q,A\u0007bG\u000e,X.\u00169eCR,7\u000f\t\t\u0004\r:s\u0006gA0bGB!1K\u00161c!\t!\u0013\rB\u0005[7\u0006\u0005\t\u0011!B\u0001[A\u0011Ae\u0019\u0003\nIn\u000b\t\u0011!A\u0003\u00025\u0012Aa\u0018\u00132aA\u0011AE\u001a\u0003\nIn\u000b\t\u0011!A\u0003\u00025B\u0001\u0002\u001b\u0001\u0003\u0016\u0004%\t![\u0001\ti\u0006\u001c8.\u00138g_V\t!\u000e\u0005\u0002\u0013W&\u0011AN\u0001\u0002\t)\u0006\u001c8.\u00138g_\"Aa\u000e\u0001B\tB\u0003%!.A\u0005uCN\\\u0017J\u001c4pA!)\u0001\u000f\u0001C\u0001c\u00061A(\u001b8jiz\"rA]:ysj\f)\u0001\u0005\u0002\u0013\u0001!)Ad\u001ca\u0001iB\u0012Qo\u001e\t\u0004%\u00052\bC\u0001\u0013x\t%13/!A\u0001\u0002\u000b\u0005Q\u0006C\u00036_\u0002\u0007q\u0007C\u0003?_\u0002\u0007\u0011\u0007C\u0003D_\u0002\u00071\u0010E\u0002G\u001dr\u0004D!`@\u0002\u0004A)1K\u0016@\u0002\u0002A\u0011Ae \u0003\n5j\f\t\u0011!A\u0003\u00025\u00022\u0001JA\u0002\t%!'0!A\u0001\u0002\u000b\u0005Q\u0006C\u0003i_\u0002\u0007!\u000eC\u0005\u0002\n\u0001\t\t\u0011\"\u0001\u0002\f\u0005!1m\u001c9z)-\u0011\u0018QBA\b\u0003#\t\u0019\"!\u0006\t\u0011q\t9\u0001%AA\u0002QD\u0001\"NA\u0004!\u0003\u0005\ra\u000e\u0005\t}\u0005\u001d\u0001\u0013!a\u0001c!A1)a\u0002\u0011\u0002\u0003\u00071\u0010\u0003\u0005i\u0003\u000f\u0001\n\u00111\u0001k\u0011%\tI\u0002AI\u0001\n\u0003\tY\"\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u0005u\u0001\u0007BA\u0010\u0003G\u0001BAE\u0011\u0002\"A\u0019A%a\t\u0005\u0015\u0019\n9\"!A\u0001\u0002\u000b\u0005Q\u0006C\u0005\u0002(\u0001\t\n\u0011\"\u0001\u0002*\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012TCAA\u0016U\r9\u0014QF\u0016\u0003\u0003_\u0001B!!\r\u0002<5\u0011\u00111\u0007\u0006\u0005\u0003k\t9$A\u0005v]\u000eDWmY6fI*\u0019\u0011\u0011H\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002>\u0005M\"!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I\u0011\u0011\t\u0001\u0012\u0002\u0013\u0005\u00111I\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\t\t)EK\u00022\u0003[A\u0011\"!\u0013\u0001#\u0003%\t!a\u0013\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%iU\u0011\u0011Q\n\u0016\u0004\u000b\u00065\u0002\"CA)\u0001E\u0005I\u0011AA*\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIU*\"!!\u0016+\u0007)\fi\u0003C\u0005\u0002Z\u0001\t\t\u0011\"\u0011\u0002\\\u0005i\u0001O]8ek\u000e$\bK]3gSb,\"!!\u0018\u0011\t\u0005}\u0013\u0011N\u0007\u0003\u0003CRA!a\u0019\u0002f\u0005!A.\u00198h\u0015\t\t9'\u0001\u0003kCZ\f\u0017\u0002BA6\u0003C\u0012aa\u0015;sS:<\u0007\"CA8\u0001\u0005\u0005I\u0011AA9\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\t\t\u0019\bE\u0002\r\u0003kJ1!a\u001e\u000e\u0005\rIe\u000e\u001e\u0005\n\u0003w\u0002\u0011\u0011!C\u0001\u0003{\na\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000fF\u00022\u0003\u007fB!\"!!\u0002z\u0005\u0005\t\u0019AA:\u0003\rAH%\r\u0005\n\u0003\u000b\u0003\u0011\u0011!C!\u0003\u000f\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003\u0013\u0003R!a#\u0002\u0012Fj!!!$\u000b\u0007\u0005=U\"\u0001\u0006d_2dWm\u0019;j_:LA!a%\u0002\u000e\nA\u0011\n^3sCR|'\u000fC\u0005\u0002\u0018\u0002\t\t\u0011\"\u0001\u0002\u001a\u0006A1-\u00198FcV\fG\u000e\u0006\u0003\u0002\u001c\u0006\u0005\u0006c\u0001\u0007\u0002\u001e&\u0019\u0011qT\u0007\u0003\u000f\t{w\u000e\\3b]\"I\u0011\u0011QAK\u0003\u0003\u0005\r!\r\u0005\n\u0003K\u0003\u0011\u0011!C!\u0003O\u000b\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0003\u0003gB\u0011\"a+\u0001\u0003\u0003%\t%!,\u0002\u0011Q|7\u000b\u001e:j]\u001e$\"!!\u0018\t\u0013\u0005E\u0006!!A\u0005B\u0005M\u0016AB3rk\u0006d7\u000f\u0006\u0003\u0002\u001c\u0006U\u0006\"CAA\u0003_\u000b\t\u00111\u00012\u000f)\tILAA\u0001\u0012\u0003\u0011\u00111X\u0001\u0010\u0007>l\u0007\u000f\\3uS>tWI^3oiB\u0019!#!0\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005}6#BA_\u0003\u0003D\u0002\u0003DAb\u0003\u0013\fimN\u0019\u0002V*\u0014XBAAc\u0015\r\t9-D\u0001\beVtG/[7f\u0013\u0011\tY-!2\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tW\u0007\r\u0003\u0002P\u0006M\u0007\u0003\u0002\n\"\u0003#\u00042\u0001JAj\t)1\u0013QXA\u0001\u0002\u0003\u0015\t!\f\t\u0005\r:\u000b9\u000e\r\u0004\u0002Z\u0006u\u0017\u0011\u001d\t\u0007'Z\u000bY.a8\u0011\u0007\u0011\ni\u000e\u0002\u0006[\u0003{\u000b\t\u0011!A\u0003\u00025\u00022\u0001JAq\t)!\u0017QXA\u0001\u0002\u0003\u0015\t!\f\u0005\ba\u0006uF\u0011AAs)\t\tY\f\u0003\u0006\u0002,\u0006u\u0016\u0011!C#\u0003[C!\"a;\u0002>\u0006\u0005I\u0011QAw\u0003\u0015\t\u0007\u000f\u001d7z)-\u0011\u0018q^A}\u0003w\fiP!\u0004\t\u000fq\tI\u000f1\u0001\u0002rB\"\u00111_A|!\u0011\u0011\u0012%!>\u0011\u0007\u0011\n9\u0010\u0002\u0006'\u0003_\f\t\u0011!A\u0003\u00025Ba!NAu\u0001\u00049\u0004B\u0002 \u0002j\u0002\u0007\u0011\u0007C\u0004D\u0003S\u0004\r!a@\u0011\t\u0019s%\u0011\u0001\u0019\u0007\u0005\u0007\u00119Aa\u0003\u0011\rM3&Q\u0001B\u0005!\r!#q\u0001\u0003\u000b5\u0006u\u0018\u0011!A\u0001\u0006\u0003i\u0003c\u0001\u0013\u0003\f\u0011QA-!@\u0002\u0002\u0003\u0005)\u0011A\u0017\t\r!\fI\u000f1\u0001k\u0011)\u0011\t\"!0\u0002\u0002\u0013\u0005%1C\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\u0011)B!\f1\t\t]!q\u0005\t\u0006\u0019\te!QD\u0005\u0004\u00057i!AB(qi&|g\u000e\u0005\u0006\r\u0005?\u0011\u0019cN\u0019\u0003*)L1A!\t\u000e\u0005\u0019!V\u000f\u001d7fkA!!#\tB\u0013!\r!#q\u0005\u0003\u000bM\t=\u0011\u0011!A\u0001\u0006\u0003i\u0003#BAF\u0005W\t\u0016bA(\u0002\u000e\"I!q\u0006B\b\u0003\u0003\u0005\rA]\u0001\u0004q\u0012\u0002\u0004B\u0003B\u001a\u0003{\u000b\t\u0011\"\u0003\u00036\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\u00119\u0004\u0005\u0003\u0002`\te\u0012\u0002\u0002B\u001e\u0003C\u0012aa\u00142kK\u000e$\b"
)
public class CompletionEvent implements DAGSchedulerEvent, Product, Serializable {
   private final Task<?> task;
   private final TaskEndReason reason;
   private final Object result;
   private final Seq<AccumulatorV2<?, ?>> accumUpdates;
   private final TaskInfo taskInfo;

   public static Option<Tuple5<Task<Object>, TaskEndReason, Object, Seq<AccumulatorV2<?, ?>>, TaskInfo>> unapply(CompletionEvent var0) {
      return CompletionEvent$.MODULE$.unapply(var0);
   }

   public static CompletionEvent apply(Task<?> var0, TaskEndReason var1, Object var2, Seq<AccumulatorV2<?, ?>> var3, TaskInfo var4) {
      return CompletionEvent$.MODULE$.apply(var0, var1, var2, var3, var4);
   }

   public static Function1<Tuple5<Task<?>, TaskEndReason, Object, Seq<AccumulatorV2<?, ?>>, TaskInfo>, CompletionEvent> tupled() {
      return CompletionEvent$.MODULE$.tupled();
   }

   public static Function1<Task<?>, Function1<TaskEndReason, Function1<Object, Function1<Seq<AccumulatorV2<?, ?>>, Function1<TaskInfo, CompletionEvent>>>>> curried() {
      return CompletionEvent$.MODULE$.curried();
   }

   public Task<?> task() {
      return this.task;
   }

   public TaskEndReason reason() {
      return this.reason;
   }

   public Object result() {
      return this.result;
   }

   public Seq<AccumulatorV2<?, ?>> accumUpdates() {
      return this.accumUpdates;
   }

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public CompletionEvent copy(Task<?> task, TaskEndReason reason, Object result, Seq<AccumulatorV2<?, ?>> accumUpdates, TaskInfo taskInfo) {
      return new CompletionEvent(task, reason, result, accumUpdates, taskInfo);
   }

   public Task<?> copy$default$1() {
      return this.task();
   }

   public TaskEndReason copy$default$2() {
      return this.reason();
   }

   public Object copy$default$3() {
      return this.result();
   }

   public Seq<AccumulatorV2<?, ?>> copy$default$4() {
      return this.accumUpdates();
   }

   public TaskInfo copy$default$5() {
      return this.taskInfo();
   }

   public String productPrefix() {
      return "CompletionEvent";
   }

   public int productArity() {
      return 5;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.task();
            break;
         case 1:
            var10000 = this.reason();
            break;
         case 2:
            var10000 = this.result();
            break;
         case 3:
            var10000 = this.accumUpdates();
            break;
         case 4:
            var10000 = this.taskInfo();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof CompletionEvent;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var13;
      if (this != x$1) {
         label83: {
            boolean var3;
            if (x$1 instanceof CompletionEvent) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label64: {
                  label73: {
                     CompletionEvent var4 = (CompletionEvent)x$1;
                     Task var10000 = this.task();
                     Task var5 = var4.task();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label73;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label73;
                     }

                     TaskEndReason var9 = this.reason();
                     TaskEndReason var6 = var4.reason();
                     if (var9 == null) {
                        if (var6 != null) {
                           break label73;
                        }
                     } else if (!var9.equals(var6)) {
                        break label73;
                     }

                     if (BoxesRunTime.equals(this.result(), var4.result())) {
                        label74: {
                           Seq var10 = this.accumUpdates();
                           Seq var7 = var4.accumUpdates();
                           if (var10 == null) {
                              if (var7 != null) {
                                 break label74;
                              }
                           } else if (!var10.equals(var7)) {
                              break label74;
                           }

                           TaskInfo var11 = this.taskInfo();
                           TaskInfo var8 = var4.taskInfo();
                           if (var11 == null) {
                              if (var8 != null) {
                                 break label74;
                              }
                           } else if (!var11.equals(var8)) {
                              break label74;
                           }

                           if (var4.canEqual(this)) {
                              var13 = true;
                              break label64;
                           }
                        }
                     }
                  }

                  var13 = false;
               }

               if (var13) {
                  break label83;
               }
            }

            var13 = false;
            return var13;
         }
      }

      var13 = true;
      return var13;
   }

   public CompletionEvent(Task<?> task, TaskEndReason reason, Object result, Seq<AccumulatorV2<?, ?>> accumUpdates, TaskInfo taskInfo) {
      this.task = task;
      this.reason = reason;
      this.result = result;
      this.accumUpdates = accumUpdates;
      this.taskInfo = taskInfo;
      Product$class.$init$(this);
   }
}
