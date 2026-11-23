package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ma!B\u0001\u0003\u0001\nQ!AE$fiRLgn\u001a*fgVdG/\u0012<f]RT!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u000b\u0001Y\u0011#\u0006\r\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u00112#D\u0001\u0003\u0013\t!\"AA\tE\u0003\u001e\u001b6\r[3ek2,'/\u0012<f]R\u0004\"\u0001\u0004\f\n\u0005]i!a\u0002)s_\u0012,8\r\u001e\t\u0003\u0019eI!AG\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011q\u0001!Q3A\u0005\u0002y\t\u0001\u0002^1tW&sgm\\\u0002\u0001+\u0005y\u0002C\u0001\n!\u0013\t\t#A\u0001\u0005UCN\\\u0017J\u001c4p\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012!\u0003;bg.LeNZ8!\u0011\u0015)\u0003\u0001\"\u0001'\u0003\u0019a\u0014N\\5u}Q\u0011q\u0005\u000b\t\u0003%\u0001AQ\u0001\b\u0013A\u0002}AqA\u000b\u0001\u0002\u0002\u0013\u00051&\u0001\u0003d_BLHCA\u0014-\u0011\u001da\u0012\u0006%AA\u0002}AqA\f\u0001\u0012\u0002\u0013\u0005q&\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003AR#aH\u0019,\u0003I\u0002\"a\r\u001d\u000e\u0003QR!!\u000e\u001c\u0002\u0013Ut7\r[3dW\u0016$'BA\u001c\u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0003sQ\u0012\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001dY\u0004!!A\u0005Bq\nQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A\u001f\u0011\u0005y\u001aU\"A \u000b\u0005\u0001\u000b\u0015\u0001\u00027b]\u001eT\u0011AQ\u0001\u0005U\u00064\u0018-\u0003\u0002E\u007f\t11\u000b\u001e:j]\u001eDqA\u0012\u0001\u0002\u0002\u0013\u0005q)\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001I!\ta\u0011*\u0003\u0002K\u001b\t\u0019\u0011J\u001c;\t\u000f1\u0003\u0011\u0011!C\u0001\u001b\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u0001(R!\taq*\u0003\u0002Q\u001b\t\u0019\u0011I\\=\t\u000fI[\u0015\u0011!a\u0001\u0011\u0006\u0019\u0001\u0010J\u0019\t\u000fQ\u0003\u0011\u0011!C!+\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001W!\r9&LT\u0007\u00021*\u0011\u0011,D\u0001\u000bG>dG.Z2uS>t\u0017BA.Y\u0005!IE/\u001a:bi>\u0014\bbB/\u0001\u0003\u0003%\tAX\u0001\tG\u0006tW)];bYR\u0011qL\u0019\t\u0003\u0019\u0001L!!Y\u0007\u0003\u000f\t{w\u000e\\3b]\"9!\u000bXA\u0001\u0002\u0004q\u0005b\u00023\u0001\u0003\u0003%\t%Z\u0001\tQ\u0006\u001c\bnQ8eKR\t\u0001\nC\u0004h\u0001\u0005\u0005I\u0011\t5\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012!\u0010\u0005\bU\u0002\t\t\u0011\"\u0011l\u0003\u0019)\u0017/^1mgR\u0011q\f\u001c\u0005\b%&\f\t\u00111\u0001O\u000f!q'!!A\t\u0002\ty\u0017AE$fiRLgn\u001a*fgVdG/\u0012<f]R\u0004\"A\u00059\u0007\u0011\u0005\u0011\u0011\u0011!E\u0001\u0005E\u001c2\u0001\u001d:\u0019!\u0011\u0019hoH\u0014\u000e\u0003QT!!^\u0007\u0002\u000fI,h\u000e^5nK&\u0011q\u000f\u001e\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004\"B\u0013q\t\u0003IH#A8\t\u000f\u001d\u0004\u0018\u0011!C#Q\"9A\u0010]A\u0001\n\u0003k\u0018!B1qa2LHCA\u0014\u007f\u0011\u0015a2\u00101\u0001 \u0011%\t\t\u0001]A\u0001\n\u0003\u000b\u0019!A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005\u0015\u00111\u0002\t\u0005\u0019\u0005\u001dq$C\u0002\u0002\n5\u0011aa\u00149uS>t\u0007\u0002CA\u0007\u007f\u0006\u0005\t\u0019A\u0014\u0002\u0007a$\u0003\u0007C\u0005\u0002\u0012A\f\t\u0011\"\u0003\u0002\u0014\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t)\u0002E\u0002?\u0003/I1!!\u0007@\u0005\u0019y%M[3di\u0002"
)
public class GettingResultEvent implements DAGSchedulerEvent, Product, Serializable {
   private final TaskInfo taskInfo;

   public static Option<TaskInfo> unapply(GettingResultEvent var0) {
      return GettingResultEvent$.MODULE$.unapply(var0);
   }

   public static GettingResultEvent apply(TaskInfo var0) {
      return GettingResultEvent$.MODULE$.apply(var0);
   }

   public static <A> Function1<TaskInfo, A> andThen(Function1<GettingResultEvent, A> var0) {
      return GettingResultEvent$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, GettingResultEvent> compose(Function1<A, TaskInfo> var0) {
      return GettingResultEvent$.MODULE$.compose(var0);
   }

   public TaskInfo taskInfo() {
      return this.taskInfo;
   }

   public GettingResultEvent copy(TaskInfo taskInfo) {
      return new GettingResultEvent(taskInfo);
   }

   public TaskInfo copy$default$1() {
      return this.taskInfo();
   }

   public String productPrefix() {
      return "GettingResultEvent";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.taskInfo();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof GettingResultEvent;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof GettingResultEvent) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     GettingResultEvent var4 = (GettingResultEvent)x$1;
                     TaskInfo var10000 = this.taskInfo();
                     TaskInfo var5 = var4.taskInfo();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public GettingResultEvent(TaskInfo taskInfo) {
      this.taskInfo = taskInfo;
      Product$class.$init$(this);
   }
}
