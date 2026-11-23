package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eb\u0001B\u0001\u0003\u0001.\u0011\u0011\u0003V1tWJ+7/\u001e7u\u00052|7m[%e\u0015\t\u0019A!A\u0004ti>\u0014\u0018mZ3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019A1\u0002CA\u0007\u000f\u001b\u0005\u0011\u0011BA\b\u0003\u0005\u001d\u0011En\\2l\u0013\u0012\u0004\"!\u0005\u000b\u000e\u0003IQ\u0011aE\u0001\u0006g\u000e\fG.Y\u0005\u0003+I\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0012/%\u0011\u0001D\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t5\u0001\u0011)\u001a!C\u00017\u00051A/Y:l\u0013\u0012,\u0012\u0001\b\t\u0003#uI!A\b\n\u0003\t1{gn\u001a\u0005\tA\u0001\u0011\t\u0012)A\u00059\u00059A/Y:l\u0013\u0012\u0004\u0003\"\u0002\u0012\u0001\t\u0003\u0019\u0013A\u0002\u001fj]&$h\b\u0006\u0002%KA\u0011Q\u0002\u0001\u0005\u00065\u0005\u0002\r\u0001\b\u0005\u0006O\u0001!\t\u0005K\u0001\u0005]\u0006lW-F\u0001*!\tQSF\u0004\u0002\u0012W%\u0011AFE\u0001\u0007!J,G-\u001a4\n\u00059z#AB*ue&twM\u0003\u0002-%!9\u0011\u0007AA\u0001\n\u0003\u0011\u0014\u0001B2paf$\"\u0001J\u001a\t\u000fi\u0001\u0004\u0013!a\u00019!9Q\u0007AI\u0001\n\u00031\u0014AD2paf$C-\u001a4bk2$H%M\u000b\u0002o)\u0012A\u0004O\u0016\u0002sA\u0011!hP\u0007\u0002w)\u0011A(P\u0001\nk:\u001c\u0007.Z2lK\u0012T!A\u0010\n\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002Aw\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f\t\u0003\u0011\u0011!C!\u0007\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012\u0001\u0012\t\u0003\u000b*k\u0011A\u0012\u0006\u0003\u000f\"\u000bA\u0001\\1oO*\t\u0011*\u0001\u0003kCZ\f\u0017B\u0001\u0018G\u0011\u001da\u0005!!A\u0005\u00025\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012A\u0014\t\u0003#=K!\u0001\u0015\n\u0003\u0007%sG\u000fC\u0004S\u0001\u0005\u0005I\u0011A*\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011Ak\u0016\t\u0003#UK!A\u0016\n\u0003\u0007\u0005s\u0017\u0010C\u0004Y#\u0006\u0005\t\u0019\u0001(\u0002\u0007a$\u0013\u0007C\u0004[\u0001\u0005\u0005I\u0011I.\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012\u0001\u0018\t\u0004;\u0002$V\"\u00010\u000b\u0005}\u0013\u0012AC2pY2,7\r^5p]&\u0011\u0011M\u0018\u0002\t\u0013R,'/\u0019;pe\"91\rAA\u0001\n\u0003!\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005\u0015D\u0007CA\tg\u0013\t9'CA\u0004C_>dW-\u00198\t\u000fa\u0013\u0017\u0011!a\u0001)\"9!\u000eAA\u0001\n\u0003Z\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u00039Cq!\u001c\u0001\u0002\u0002\u0013\u0005c.\u0001\u0004fcV\fGn\u001d\u000b\u0003K>Dq\u0001\u00177\u0002\u0002\u0003\u0007A\u000b\u000b\u0002\u0001cB\u0011!\u000f^\u0007\u0002g*\u0011a\bB\u0005\u0003kN\u0014A\u0002R3wK2|\u0007/\u001a:Ba&<qa\u001e\u0002\u0002\u0002#\u0005\u00010A\tUCN\\'+Z:vYR\u0014En\\2l\u0013\u0012\u0004\"!D=\u0007\u000f\u0005\u0011\u0011\u0011!E\u0001uN\u0019\u0011p\u001f\f\u0011\tq|H\u0004J\u0007\u0002{*\u0011aPE\u0001\beVtG/[7f\u0013\r\t\t! \u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\f\u0004B\u0002\u0012z\t\u0003\t)\u0001F\u0001y\u0011%\tI!_A\u0001\n\u000b\nY!\u0001\u0005u_N#(/\u001b8h)\u0005!\u0005\"CA\bs\u0006\u0005I\u0011QA\t\u0003\u0015\t\u0007\u000f\u001d7z)\r!\u00131\u0003\u0005\u00075\u00055\u0001\u0019\u0001\u000f\t\u0013\u0005]\u00110!A\u0005\u0002\u0006e\u0011aB;oCB\u0004H.\u001f\u000b\u0005\u00037\t\t\u0003\u0005\u0003\u0012\u0003;a\u0012bAA\u0010%\t1q\n\u001d;j_:D\u0011\"a\t\u0002\u0016\u0005\u0005\t\u0019\u0001\u0013\u0002\u0007a$\u0003\u0007C\u0005\u0002(e\f\t\u0011\"\u0003\u0002*\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tY\u0003E\u0002F\u0003[I1!a\fG\u0005\u0019y%M[3di\u0002"
)
public class TaskResultBlockId extends BlockId implements Product, Serializable {
   private final long taskId;

   public static Option<Object> unapply(TaskResultBlockId var0) {
      return TaskResultBlockId$.MODULE$.unapply(var0);
   }

   public static TaskResultBlockId apply(long var0) {
      return TaskResultBlockId$.MODULE$.apply(var0);
   }

   public static <A> Function1<Object, A> andThen(Function1<TaskResultBlockId, A> var0) {
      return TaskResultBlockId$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, TaskResultBlockId> compose(Function1<A, Object> var0) {
      return TaskResultBlockId$.MODULE$.compose(var0);
   }

   public long taskId() {
      return this.taskId;
   }

   public String name() {
      return (new StringBuilder()).append((Object)"taskresult_").append((Object)BoxesRunTime.boxToLong(this.taskId())).toString();
   }

   public TaskResultBlockId copy(long taskId) {
      return new TaskResultBlockId(taskId);
   }

   public long copy$default$1() {
      return this.taskId();
   }

   public String productPrefix() {
      return "TaskResultBlockId";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return BoxesRunTime.boxToLong(this.taskId());
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof TaskResultBlockId;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, Statics.longHash(this.taskId()));
      return Statics.finalizeHash(var1, 1);
   }

   public boolean equals(Object x$1) {
      boolean var10000;
      if (this != x$1) {
         label48: {
            boolean var3;
            if (x$1 instanceof TaskResultBlockId) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               TaskResultBlockId var4 = (TaskResultBlockId)x$1;
               if (this.taskId() == var4.taskId() && var4.canEqual(this)) {
                  break label48;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public TaskResultBlockId(long taskId) {
      this.taskId = taskId;
      Product$class.$init$(this);
   }
}
