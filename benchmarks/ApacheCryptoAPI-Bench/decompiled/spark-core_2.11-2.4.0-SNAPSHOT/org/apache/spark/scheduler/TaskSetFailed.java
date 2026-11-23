package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005]d!B\u0001\u0003\u0001\nQ!!\u0004+bg.\u001cV\r\u001e$bS2,GM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON)\u0001aC\t\u00161A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003#\u0011\u000buiU2iK\u0012,H.\u001a:Fm\u0016tG\u000f\u0005\u0002\r-%\u0011q#\u0004\u0002\b!J|G-^2u!\ta\u0011$\u0003\u0002\u001b\u001b\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001BK\u0002\u0013\u0005a$A\u0004uCN\\7+\u001a;\u0004\u0001U\tq\u0004\u0005\u0002\u0013A%\u0011\u0011E\u0001\u0002\b)\u0006\u001c8nU3u\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012\u0001\u0003;bg.\u001cV\r\u001e\u0011\t\u0011\u0015\u0002!Q3A\u0005\u0002\u0019\naA]3bg>tW#A\u0014\u0011\u0005!ZcB\u0001\u0007*\u0013\tQS\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003Y5\u0012aa\u0015;sS:<'B\u0001\u0016\u000e\u0011!y\u0003A!E!\u0002\u00139\u0013a\u0002:fCN|g\u000e\t\u0005\tc\u0001\u0011)\u001a!C\u0001e\u0005IQ\r_2faRLwN\\\u000b\u0002gA\u0019A\u0002\u000e\u001c\n\u0005Uj!AB(qi&|g\u000e\u0005\u00028\u007f9\u0011\u0001(\u0010\b\u0003sqj\u0011A\u000f\u0006\u0003wu\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u0005yj\u0011a\u00029bG.\fw-Z\u0005\u0003\u0001\u0006\u0013\u0011\u0002\u00165s_^\f'\r\\3\u000b\u0005yj\u0001\u0002C\"\u0001\u0005#\u0005\u000b\u0011B\u001a\u0002\u0015\u0015D8-\u001a9uS>t\u0007\u0005C\u0003F\u0001\u0011\u0005a)\u0001\u0004=S:LGO\u0010\u000b\u0005\u000f\"K%\n\u0005\u0002\u0013\u0001!)A\u0004\u0012a\u0001?!)Q\u0005\u0012a\u0001O!)\u0011\u0007\u0012a\u0001g!9A\nAA\u0001\n\u0003i\u0015\u0001B2paf$Ba\u0012(P!\"9Ad\u0013I\u0001\u0002\u0004y\u0002bB\u0013L!\u0003\u0005\ra\n\u0005\bc-\u0003\n\u00111\u00014\u0011\u001d\u0011\u0006!%A\u0005\u0002M\u000babY8qs\u0012\"WMZ1vYR$\u0013'F\u0001UU\tyRkK\u0001W!\t9F,D\u0001Y\u0015\tI&,A\u0005v]\u000eDWmY6fI*\u00111,D\u0001\u000bC:tw\u000e^1uS>t\u0017BA/Y\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\b?\u0002\t\n\u0011\"\u0001a\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\u0012!\u0019\u0016\u0003OUCqa\u0019\u0001\u0012\u0002\u0013\u0005A-\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0003\u0015T#aM+\t\u000f\u001d\u0004\u0011\u0011!C!Q\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012!\u001b\t\u0003U>l\u0011a\u001b\u0006\u0003Y6\fA\u0001\\1oO*\ta.\u0001\u0003kCZ\f\u0017B\u0001\u0017l\u0011\u001d\t\b!!A\u0005\u0002I\fA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012a\u001d\t\u0003\u0019QL!!^\u0007\u0003\u0007%sG\u000fC\u0004x\u0001\u0005\u0005I\u0011\u0001=\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011\u0011\u0010 \t\u0003\u0019iL!a_\u0007\u0003\u0007\u0005s\u0017\u0010C\u0004~m\u0006\u0005\t\u0019A:\u0002\u0007a$\u0013\u0007\u0003\u0005\u0000\u0001\u0005\u0005I\u0011IA\u0001\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA\u0002!\u0015\t)!a\u0003z\u001b\t\t9AC\u0002\u0002\n5\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\ti!a\u0002\u0003\u0011%#XM]1u_JD\u0011\"!\u0005\u0001\u0003\u0003%\t!a\u0005\u0002\u0011\r\fg.R9vC2$B!!\u0006\u0002\u001cA\u0019A\"a\u0006\n\u0007\u0005eQBA\u0004C_>dW-\u00198\t\u0011u\fy!!AA\u0002eD\u0011\"a\b\u0001\u0003\u0003%\t%!\t\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012a\u001d\u0005\n\u0003K\u0001\u0011\u0011!C!\u0003O\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002S\"I\u00111\u0006\u0001\u0002\u0002\u0013\u0005\u0013QF\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005U\u0011q\u0006\u0005\t{\u0006%\u0012\u0011!a\u0001s\u001eQ\u00111\u0007\u0002\u0002\u0002#\u0005!!!\u000e\u0002\u001bQ\u000b7o[*fi\u001a\u000b\u0017\u000e\\3e!\r\u0011\u0012q\u0007\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003s\u0019R!a\u000e\u0002<a\u0001\u0002\"!\u0010\u0002D}93gR\u0007\u0003\u0003\u007fQ1!!\u0011\u000e\u0003\u001d\u0011XO\u001c;j[\u0016LA!!\u0012\u0002@\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\\\u001a\t\u000f\u0015\u000b9\u0004\"\u0001\u0002JQ\u0011\u0011Q\u0007\u0005\u000b\u0003K\t9$!A\u0005F\u0005\u001d\u0002BCA(\u0003o\t\t\u0011\"!\u0002R\u0005)\u0011\r\u001d9msR9q)a\u0015\u0002V\u0005]\u0003B\u0002\u000f\u0002N\u0001\u0007q\u0004\u0003\u0004&\u0003\u001b\u0002\ra\n\u0005\u0007c\u00055\u0003\u0019A\u001a\t\u0015\u0005m\u0013qGA\u0001\n\u0003\u000bi&A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005}\u0013q\r\t\u0005\u0019Q\n\t\u0007\u0005\u0004\r\u0003GzreM\u0005\u0004\u0003Kj!A\u0002+va2,7\u0007C\u0005\u0002j\u0005e\u0013\u0011!a\u0001\u000f\u0006\u0019\u0001\u0010\n\u0019\t\u0015\u00055\u0014qGA\u0001\n\u0013\ty'A\u0006sK\u0006$'+Z:pYZ,GCAA9!\rQ\u00171O\u0005\u0004\u0003kZ'AB(cU\u0016\u001cG\u000f"
)
public class TaskSetFailed implements DAGSchedulerEvent, Product, Serializable {
   private final TaskSet taskSet;
   private final String reason;
   private final Option<Throwable> exception;

   public static Option<Tuple3<TaskSet, String, Option<Throwable>>> unapply(TaskSetFailed var0) {
      return TaskSetFailed$.MODULE$.unapply(var0);
   }

   public static TaskSetFailed apply(TaskSet var0, String var1, Option<Throwable> var2) {
      return TaskSetFailed$.MODULE$.apply(var0, var1, var2);
   }

   public static Function1<Tuple3<TaskSet, String, Option<Throwable>>, TaskSetFailed> tupled() {
      return TaskSetFailed$.MODULE$.tupled();
   }

   public static Function1<TaskSet, Function1<String, Function1<Option<Throwable>, TaskSetFailed>>> curried() {
      return TaskSetFailed$.MODULE$.curried();
   }

   public TaskSet taskSet() {
      return this.taskSet;
   }

   public String reason() {
      return this.reason;
   }

   public Option<Throwable> exception() {
      return this.exception;
   }

   public TaskSetFailed copy(TaskSet taskSet, String reason, Option<Throwable> exception) {
      return new TaskSetFailed(taskSet, reason, exception);
   }

   public TaskSet copy$default$1() {
      return this.taskSet();
   }

   public String copy$default$2() {
      return this.reason();
   }

   public Option<Throwable> copy$default$3() {
      return this.exception();
   }

   public String productPrefix() {
      return "TaskSetFailed";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.taskSet();
            break;
         case 1:
            var10000 = this.reason();
            break;
         case 2:
            var10000 = this.exception();
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
      return x$1 instanceof TaskSetFailed;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof TaskSetFailed) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     TaskSetFailed var4 = (TaskSetFailed)x$1;
                     TaskSet var10000 = this.taskSet();
                     TaskSet var5 = var4.taskSet();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     String var8 = this.reason();
                     String var6 = var4.reason();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var6)) {
                        break label62;
                     }

                     Option var9 = this.exception();
                     Option var7 = var4.exception();
                     if (var9 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var9.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public TaskSetFailed(TaskSet taskSet, String reason, Option<Throwable> exception) {
      this.taskSet = taskSet;
      this.reason = reason;
      this.exception = exception;
      Product$class.$init$(this);
   }
}
