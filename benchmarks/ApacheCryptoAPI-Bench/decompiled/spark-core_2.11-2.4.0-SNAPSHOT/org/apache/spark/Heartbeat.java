package org.apache.spark;

import org.apache.spark.storage.BlockManagerId;
import org.apache.spark.util.AccumulatorV2;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=h!B\u0001\u0003\u0001\nA!!\u0003%fCJ$(-Z1u\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0003\u0002\u0001\n\u001fI\u0001\"AC\u0007\u000e\u0003-Q\u0011\u0001D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001d-\u0011a!\u00118z%\u00164\u0007C\u0001\u0006\u0011\u0013\t\t2BA\u0004Qe>$Wo\u0019;\u0011\u0005)\u0019\u0012B\u0001\u000b\f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!1\u0002A!f\u0001\n\u0003A\u0012AC3yK\u000e,Ho\u001c:JI\u000e\u0001Q#A\r\u0011\u0005iibB\u0001\u0006\u001c\u0013\ta2\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003=}\u0011aa\u0015;sS:<'B\u0001\u000f\f\u0011!\t\u0003A!E!\u0002\u0013I\u0012aC3yK\u000e,Ho\u001c:JI\u0002B\u0001b\t\u0001\u0003\u0016\u0004%\t\u0001J\u0001\rC\u000e\u001cW/\\+qI\u0006$Xm]\u000b\u0002KA\u0019!B\n\u0015\n\u0005\u001dZ!!B!se\u0006L\b\u0003\u0002\u0006*W9J!AK\u0006\u0003\rQ+\b\u000f\\33!\tQA&\u0003\u0002.\u0017\t!Aj\u001c8h!\rysG\u000f\b\u0003aUr!!\r\u001b\u000e\u0003IR!aM\f\u0002\rq\u0012xn\u001c;?\u0013\u0005a\u0011B\u0001\u001c\f\u0003\u001d\u0001\u0018mY6bO\u0016L!\u0001O\u001d\u0003\u0007M+\u0017O\u0003\u00027\u0017A\u001a1hQ-\u0011\tqz\u0014\tW\u0007\u0002{)\u0011aHA\u0001\u0005kRLG.\u0003\u0002A{\ti\u0011iY2v[Vd\u0017\r^8s-J\u0002\"AQ\"\r\u0001\u0011IA)RA\u0001\u0002\u0003\u0015\tA\u0014\u0002\u0004?\u0012\n\u0004\u0002\u0003$\u0001\u0005#\u0005\u000b\u0011B$\u0002\u001b\u0005\u001c7-^7Va\u0012\fG/Z:!!\rQa\u0005\u0013\t\u0005\u0015%Z\u0013\nE\u00020o)\u00034aS'W!\u0011at\bT+\u0011\u0005\tkE!\u0003#F\u0003\u0003\u0005\tQ!\u0001O#\ty%\u000b\u0005\u0002\u000b!&\u0011\u0011k\u0003\u0002\b\u001d>$\b.\u001b8h!\tQ1+\u0003\u0002U\u0017\t\u0019\u0011I\\=\u0011\u0005\t3F!C,F\u0003\u0003\u0005\tQ!\u0001O\u0005\ryFE\r\t\u0003\u0005f#\u0011bV#\u0002\u0002\u0003\u0005)\u0011\u0001(\t\u0011m\u0003!Q3A\u0005\u0002q\u000baB\u00197pG.l\u0015M\\1hKJLE-F\u0001^!\tq\u0016-D\u0001`\u0015\t\u0001'!A\u0004ti>\u0014\u0018mZ3\n\u0005\t|&A\u0004\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\u0005\tI\u0002\u0011\t\u0012)A\u0005;\u0006y!\r\\8dW6\u000bg.Y4fe&#\u0007\u0005C\u0003g\u0001\u0011\u0005q-\u0001\u0004=S:LGO\u0010\u000b\u0005Q*\\W\u000f\u0005\u0002j\u00015\t!\u0001C\u0003\u0017K\u0002\u0007\u0011\u0004C\u0003$K\u0002\u0007A\u000eE\u0002\u000bM5\u0004BAC\u0015,]B\u0019qfN81\u0007A\u0014H\u000f\u0005\u0003=\u007fE\u001c\bC\u0001\"s\t%!5.!A\u0001\u0002\u000b\u0005a\n\u0005\u0002Ci\u0012Iqk[A\u0001\u0002\u0003\u0015\tA\u0014\u0005\u00067\u0016\u0004\r!\u0018\u0005\bo\u0002\t\t\u0011\"\u0001y\u0003\u0011\u0019w\u000e]=\u0015\t!L(p\u001f\u0005\b-Y\u0004\n\u00111\u0001\u001a\u0011\u001d\u0019c\u000f%AA\u00021Dqa\u0017<\u0011\u0002\u0003\u0007Q\fC\u0004~\u0001E\u0005I\u0011\u0001@\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tqPK\u0002\u001a\u0003\u0003Y#!a\u0001\u0011\t\u0005\u0015\u0011qB\u0007\u0003\u0003\u000fQA!!\u0003\u0002\f\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003\u001bY\u0011AC1o]>$\u0018\r^5p]&!\u0011\u0011CA\u0004\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\n\u0003+\u0001\u0011\u0013!C\u0001\u0003/\tabY8qs\u0012\"WMZ1vYR$#'\u0006\u0002\u0002\u001a)\u001aQ%!\u0001\t\u0013\u0005u\u0001!%A\u0005\u0002\u0005}\u0011AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0003CQ3!XA\u0001\u0011%\t)\u0003AA\u0001\n\u0003\n9#A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0003\u0003S\u0001B!a\u000b\u000265\u0011\u0011Q\u0006\u0006\u0005\u0003_\t\t$\u0001\u0003mC:<'BAA\u001a\u0003\u0011Q\u0017M^1\n\u0007y\ti\u0003C\u0005\u0002:\u0001\t\t\u0011\"\u0001\u0002<\u0005a\u0001O]8ek\u000e$\u0018I]5usV\u0011\u0011Q\b\t\u0004\u0015\u0005}\u0012bAA!\u0017\t\u0019\u0011J\u001c;\t\u0013\u0005\u0015\u0003!!A\u0005\u0002\u0005\u001d\u0013A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004%\u0006%\u0003BCA&\u0003\u0007\n\t\u00111\u0001\u0002>\u0005\u0019\u0001\u0010J\u0019\t\u0013\u0005=\u0003!!A\u0005B\u0005E\u0013a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0005\u0005M\u0003#BA+\u00037\u0012VBAA,\u0015\r\tIfC\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA/\u0003/\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003C\u0002\u0011\u0011!C\u0001\u0003G\n\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003K\nY\u0007E\u0002\u000b\u0003OJ1!!\u001b\f\u0005\u001d\u0011un\u001c7fC:D\u0011\"a\u0013\u0002`\u0005\u0005\t\u0019\u0001*\t\u0013\u0005=\u0004!!A\u0005B\u0005E\u0014\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0005\u0005u\u0002\"CA;\u0001\u0005\u0005I\u0011IA<\u0003!!xn\u0015;sS:<GCAA\u0015\u0011%\tY\bAA\u0001\n\u0003\ni(\u0001\u0004fcV\fGn\u001d\u000b\u0005\u0003K\ny\bC\u0005\u0002L\u0005e\u0014\u0011!a\u0001%\u001eQ\u00111\u0011\u0002\u0002\u0002#\u0005!!!\"\u0002\u0013!+\u0017M\u001d;cK\u0006$\bcA5\u0002\b\u001aI\u0011AAA\u0001\u0012\u0003\u0011\u0011\u0011R\n\u0006\u0003\u000f\u000bYI\u0005\t\n\u0003\u001b\u000b\u0019*GAL;\"l!!a$\u000b\u0007\u0005E5\"A\u0004sk:$\u0018.\\3\n\t\u0005U\u0015q\u0012\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u001c\u0004\u0003\u0002\u0006'\u00033\u0003RAC\u0015,\u00037\u0003BaL\u001c\u0002\u001eB2\u0011qTAR\u0003O\u0003b\u0001P \u0002\"\u0006\u0015\u0006c\u0001\"\u0002$\u0012QA)a\"\u0002\u0002\u0003\u0005)\u0011\u0001(\u0011\u0007\t\u000b9\u000b\u0002\u0006X\u0003\u000f\u000b\t\u0011!A\u0003\u00029CqAZAD\t\u0003\tY\u000b\u0006\u0002\u0002\u0006\"Q\u0011QOAD\u0003\u0003%)%a\u001e\t\u0015\u0005E\u0016qQA\u0001\n\u0003\u000b\u0019,A\u0003baBd\u0017\u0010F\u0004i\u0003k\u000b9,a3\t\rY\ty\u000b1\u0001\u001a\u0011\u001d\u0019\u0013q\u0016a\u0001\u0003s\u0003BA\u0003\u0014\u0002<B)!\"K\u0016\u0002>B!qfNA`a\u0019\t\t-!2\u0002JB1AhPAb\u0003\u000f\u00042AQAc\t)!\u0015qWA\u0001\u0002\u0003\u0015\tA\u0014\t\u0004\u0005\u0006%GAC,\u00028\u0006\u0005\t\u0011!B\u0001\u001d\"11,a,A\u0002uC!\"a4\u0002\b\u0006\u0005I\u0011QAi\u0003\u001d)h.\u00199qYf$B!a5\u0002`B)!\"!6\u0002Z&\u0019\u0011q[\u0006\u0003\r=\u0003H/[8o!\u0019Q\u00111\\\r&;&\u0019\u0011Q\\\u0006\u0003\rQ+\b\u000f\\34\u0011%\t\t/!4\u0002\u0002\u0003\u0007\u0001.A\u0002yIAB!\"!:\u0002\b\u0006\u0005I\u0011BAt\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005%\b\u0003BA\u0016\u0003WLA!!<\u0002.\t1qJ\u00196fGR\u0004"
)
public class Heartbeat implements Product, Serializable {
   private final String executorId;
   private final Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates;
   private final BlockManagerId blockManagerId;

   public static Option<Tuple3<String, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], BlockManagerId>> unapply(Heartbeat var0) {
      return Heartbeat$.MODULE$.unapply(var0);
   }

   public static Heartbeat apply(String var0, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] var1, BlockManagerId var2) {
      return Heartbeat$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<String, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], BlockManagerId>, Heartbeat> tupled() {
      return Heartbeat$.MODULE$.tupled();
   }

   public static Function1<String, Function1<Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[], Function1<BlockManagerId, Heartbeat>>> curried() {
      return Heartbeat$.MODULE$.curried();
   }

   public String executorId() {
      return this.executorId;
   }

   public Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates() {
      return this.accumUpdates;
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public Heartbeat copy(String executorId, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates, BlockManagerId blockManagerId) {
      return new Heartbeat(executorId, accumUpdates, blockManagerId);
   }

   public String copy$default$1() {
      return this.executorId();
   }

   public Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] copy$default$2() {
      return this.accumUpdates();
   }

   public BlockManagerId copy$default$3() {
      return this.blockManagerId();
   }

   public String productPrefix() {
      return "Heartbeat";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.executorId();
            break;
         case 1:
            var10000 = this.accumUpdates();
            break;
         case 2:
            var10000 = this.blockManagerId();
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
      return x$1 instanceof Heartbeat;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label63: {
            boolean var3;
            if (x$1 instanceof Heartbeat) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label46: {
                  label45: {
                     Heartbeat var4 = (Heartbeat)x$1;
                     String var10000 = this.executorId();
                     String var5 = var4.executorId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label45;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label45;
                     }

                     if (this.accumUpdates() == var4.accumUpdates()) {
                        label39: {
                           BlockManagerId var7 = this.blockManagerId();
                           BlockManagerId var6 = var4.blockManagerId();
                           if (var7 == null) {
                              if (var6 != null) {
                                 break label39;
                              }
                           } else if (!var7.equals(var6)) {
                              break label39;
                           }

                           if (var4.canEqual(this)) {
                              var9 = true;
                              break label46;
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label63;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public Heartbeat(String executorId, Tuple2<Object, Seq<AccumulatorV2<?, ?>>>[] accumUpdates, BlockManagerId blockManagerId) {
      this.executorId = executorId;
      this.accumUpdates = accumUpdates;
      this.blockManagerId = blockManagerId;
      Product$class.$init$(this);
   }
}
