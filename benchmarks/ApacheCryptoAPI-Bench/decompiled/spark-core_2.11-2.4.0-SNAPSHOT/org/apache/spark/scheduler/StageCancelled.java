package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}b!B\u0001\u0003\u0001\nQ!AD*uC\u001e,7)\u00198dK2dW\r\u001a\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cR\u0001A\u0006\u0012+a\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0014\u001b\u0005\u0011\u0011B\u0001\u000b\u0003\u0005E!\u0015iR*dQ\u0016$W\u000f\\3s\u000bZ,g\u000e\u001e\t\u0003\u0019YI!aF\u0007\u0003\u000fA\u0013x\u000eZ;diB\u0011A\"G\u0005\u000355\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\u0002\b\u0001\u0003\u0016\u0004%\tAH\u0001\bgR\fw-Z%e\u0007\u0001)\u0012a\b\t\u0003\u0019\u0001J!!I\u0007\u0003\u0007%sG\u000f\u0003\u0005$\u0001\tE\t\u0015!\u0003 \u0003!\u0019H/Y4f\u0013\u0012\u0004\u0003\u0002C\u0013\u0001\u0005+\u0007I\u0011\u0001\u0014\u0002\rI,\u0017m]8o+\u00059\u0003c\u0001\u0007)U%\u0011\u0011&\u0004\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0005-rcB\u0001\u0007-\u0013\tiS\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003_A\u0012aa\u0015;sS:<'BA\u0017\u000e\u0011!\u0011\u0004A!E!\u0002\u00139\u0013a\u0002:fCN|g\u000e\t\u0005\u0006i\u0001!\t!N\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0007Y:\u0004\b\u0005\u0002\u0013\u0001!)Ad\ra\u0001?!)Qe\ra\u0001O!9!\bAA\u0001\n\u0003Y\u0014\u0001B2paf$2A\u000e\u001f>\u0011\u001da\u0012\b%AA\u0002}Aq!J\u001d\u0011\u0002\u0003\u0007q\u0005C\u0004@\u0001E\u0005I\u0011\u0001!\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t\u0011I\u000b\u0002 \u0005.\n1\t\u0005\u0002E\u00136\tQI\u0003\u0002G\u000f\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u00116\t!\"\u00198o_R\fG/[8o\u0013\tQUIA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u0014\u0001\u0012\u0002\u0013\u0005Q*\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u00039S#a\n\"\t\u000fA\u0003\u0011\u0011!C!#\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012A\u0015\t\u0003'bk\u0011\u0001\u0016\u0006\u0003+Z\u000bA\u0001\\1oO*\tq+\u0001\u0003kCZ\f\u0017BA\u0018U\u0011\u001dQ\u0006!!A\u0005\u0002y\tA\u0002\u001d:pIV\u001cG/\u0011:jifDq\u0001\u0018\u0001\u0002\u0002\u0013\u0005Q,\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005y\u000b\u0007C\u0001\u0007`\u0013\t\u0001WBA\u0002B]fDqAY.\u0002\u0002\u0003\u0007q$A\u0002yIEBq\u0001\u001a\u0001\u0002\u0002\u0013\u0005S-A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\u00051\u0007cA4k=6\t\u0001N\u0003\u0002j\u001b\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005-D'\u0001C%uKJ\fGo\u001c:\t\u000f5\u0004\u0011\u0011!C\u0001]\u0006A1-\u00198FcV\fG\u000e\u0006\u0002peB\u0011A\u0002]\u0005\u0003c6\u0011qAQ8pY\u0016\fg\u000eC\u0004cY\u0006\u0005\t\u0019\u00010\t\u000fQ\u0004\u0011\u0011!C!k\u0006A\u0001.Y:i\u0007>$W\rF\u0001 \u0011\u001d9\b!!A\u0005Ba\f\u0001\u0002^8TiJLgn\u001a\u000b\u0002%\"9!\u0010AA\u0001\n\u0003Z\u0018AB3rk\u0006d7\u000f\u0006\u0002py\"9!-_A\u0001\u0002\u0004qv\u0001\u0003@\u0003\u0003\u0003E\tAA@\u0002\u001dM#\u0018mZ3DC:\u001cW\r\u001c7fIB\u0019!#!\u0001\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\u0005\u0005\r1#BA\u0001\u0003\u000bA\u0002cBA\u0004\u0003\u001byrEN\u0007\u0003\u0003\u0013Q1!a\u0003\u000e\u0003\u001d\u0011XO\u001c;j[\u0016LA!a\u0004\u0002\n\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\u000fQ\n\t\u0001\"\u0001\u0002\u0014Q\tq\u0010\u0003\u0005x\u0003\u0003\t\t\u0011\"\u0012y\u0011)\tI\"!\u0001\u0002\u0002\u0013\u0005\u00151D\u0001\u0006CB\u0004H.\u001f\u000b\u0006m\u0005u\u0011q\u0004\u0005\u00079\u0005]\u0001\u0019A\u0010\t\r\u0015\n9\u00021\u0001(\u0011)\t\u0019#!\u0001\u0002\u0002\u0013\u0005\u0015QE\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t9#a\f\u0011\t1A\u0013\u0011\u0006\t\u0006\u0019\u0005-rdJ\u0005\u0004\u0003[i!A\u0002+va2,'\u0007C\u0005\u00022\u0005\u0005\u0012\u0011!a\u0001m\u0005\u0019\u0001\u0010\n\u0019\t\u0015\u0005U\u0012\u0011AA\u0001\n\u0013\t9$A\u0006sK\u0006$'+Z:pYZ,GCAA\u001d!\r\u0019\u00161H\u0005\u0004\u0003{!&AB(cU\u0016\u001cG\u000f"
)
public class StageCancelled implements DAGSchedulerEvent, Product, Serializable {
   private final int stageId;
   private final Option<String> reason;

   public static Option<Tuple2<Object, Option<String>>> unapply(StageCancelled var0) {
      return StageCancelled$.MODULE$.unapply(var0);
   }

   public static StageCancelled apply(int var0, Option<String> var1) {
      return StageCancelled$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<Object, Option<String>>, StageCancelled> tupled() {
      return StageCancelled$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<Option<String>, StageCancelled>> curried() {
      return StageCancelled$.MODULE$.curried();
   }

   public int stageId() {
      return this.stageId;
   }

   public Option<String> reason() {
      return this.reason;
   }

   public StageCancelled copy(int stageId, Option<String> reason) {
      return new StageCancelled(stageId, reason);
   }

   public int copy$default$1() {
      return this.stageId();
   }

   public Option<String> copy$default$2() {
      return this.reason();
   }

   public String productPrefix() {
      return "StageCancelled";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.stageId());
            break;
         case 1:
            var10000 = this.reason();
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
      return x$1 instanceof StageCancelled;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.stageId());
      var1 = Statics.mix(var1, Statics.anyHash(this.reason()));
      return Statics.finalizeHash(var1, 2);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label54: {
            boolean var3;
            if (x$1 instanceof StageCancelled) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label37: {
                  StageCancelled var4 = (StageCancelled)x$1;
                  if (this.stageId() == var4.stageId()) {
                     label35: {
                        Option var10000 = this.reason();
                        Option var5 = var4.reason();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label35;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label35;
                        }

                        if (var4.canEqual(this)) {
                           var7 = true;
                           break label37;
                        }
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label54;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public StageCancelled(int stageId, Option<String> reason) {
      this.stageId = stageId;
      this.reason = reason;
      Product$class.$init$(this);
   }
}
