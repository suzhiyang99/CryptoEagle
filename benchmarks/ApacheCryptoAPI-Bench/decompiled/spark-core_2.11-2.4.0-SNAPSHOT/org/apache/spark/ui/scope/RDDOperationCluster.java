package org.apache.spark.ui.scope;

import java.util.Objects;
import scala.Predef$;
import scala.Serializable;
import scala.collection.GenTraversableOnce;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.mutable.BufferLike;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%a!B\u0001\u0003\u0001\u0019a!a\u0005*E\t>\u0003XM]1uS>t7\t\\;ti\u0016\u0014(BA\u0002\u0005\u0003\u0015\u00198m\u001c9f\u0015\t)a!\u0001\u0002vS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001aD\u0001\u0002\u0006\u0001\u0003\u0006\u0004%\tAF\u0001\u0003S\u0012\u001c\u0001!F\u0001\u0018!\tA2D\u0004\u0002\u000f3%\u0011!dD\u0001\u0007!J,G-\u001a4\n\u0005qi\"AB*ue&twM\u0003\u0002\u001b\u001f!Aq\u0004\u0001B\u0001B\u0003%q#A\u0002jI\u0002B\u0001\"\t\u0001\u0003\u0002\u0004%IAF\u0001\u0006?:\fW.\u001a\u0005\tG\u0001\u0011\t\u0019!C\u0005I\u0005IqL\\1nK~#S-\u001d\u000b\u0003K!\u0002\"A\u0004\u0014\n\u0005\u001dz!\u0001B+oSRDq!\u000b\u0012\u0002\u0002\u0003\u0007q#A\u0002yIEB\u0001b\u000b\u0001\u0003\u0002\u0003\u0006KaF\u0001\u0007?:\fW.\u001a\u0011\t\u000b5\u0002A\u0011\u0001\u0018\u0002\rqJg.\u001b;?)\ry\u0013G\r\t\u0003a\u0001i\u0011A\u0001\u0005\u0006)1\u0002\ra\u0006\u0005\u0006C1\u0002\ra\u0006\u0005\bi\u0001\u0011\r\u0011\"\u00036\u0003-y6\r[5mI:{G-Z:\u0016\u0003Y\u00022a\u000e\u001f?\u001b\u0005A$BA\u001d;\u0003\u001diW\u000f^1cY\u0016T!aO\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002>q\tQA*[:u\u0005V4g-\u001a:\u0011\u0005Az\u0014B\u0001!\u0003\u0005A\u0011F\tR(qKJ\fG/[8o\u001d>$W\r\u0003\u0004C\u0001\u0001\u0006IAN\u0001\r?\u000eD\u0017\u000e\u001c3O_\u0012,7\u000f\t\u0005\b\t\u0002\u0011\r\u0011\"\u0003F\u00039y6\r[5mI\u000ecWo\u001d;feN,\u0012A\u0012\t\u0004oqz\u0003B\u0002%\u0001A\u0003%a)A\b`G\"LG\u000eZ\"mkN$XM]:!\u0011\u0015Q\u0005\u0001\"\u0001\u0017\u0003\u0011q\u0017-\\3\t\u000b1\u0003A\u0011A'\u0002\u000fM,GOT1nKR\u0011QE\u0014\u0005\u0006\u001f.\u0003\raF\u0001\u0002]\")\u0011\u000b\u0001C\u0001%\u0006Q1\r[5mI:{G-Z:\u0016\u0003M\u00032\u0001\u0016/?\u001d\t)&L\u0004\u0002W36\tqK\u0003\u0002Y+\u00051AH]8pizJ\u0011\u0001E\u0005\u00037>\tq\u0001]1dW\u0006<W-\u0003\u0002^=\n\u00191+Z9\u000b\u0005m{\u0001\"\u00021\u0001\t\u0003\t\u0017!D2iS2$7\t\\;ti\u0016\u00148/F\u0001c!\r!Fl\f\u0005\u0006I\u0002!\t!Z\u0001\u0010CR$\u0018m\u00195DQ&dGMT8eKR\u0011QE\u001a\u0005\u0006O\u000e\u0004\rAP\u0001\nG\"LG\u000e\u001a(pI\u0016DQ!\u001b\u0001\u0005\u0002)\f!#\u0019;uC\u000eD7\t[5mI\u000ecWo\u001d;feR\u0011Qe\u001b\u0005\u0006Y\"\u0004\raL\u0001\rG\"LG\u000eZ\"mkN$XM\u001d\u0005\u0006]\u0002!\tAU\u0001\u000fO\u0016$8)Y2iK\u0012tu\u000eZ3t\u0011\u0015\u0001\b\u0001\"\u0001r\u0003!\u0019\u0017M\\#rk\u0006dGC\u0001:v!\tq1/\u0003\u0002u\u001f\t9!i\\8mK\u0006t\u0007\"\u0002<p\u0001\u00049\u0018!B8uQ\u0016\u0014\bC\u0001\by\u0013\tIxBA\u0002B]fDQa\u001f\u0001\u0005Bq\fa!Z9vC2\u001cHC\u0001:~\u0011\u00151(\u00101\u0001x\u0011\u0019y\b\u0001\"\u0011\u0002\u0002\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002\u0004A\u0019a\"!\u0002\n\u0007\u0005\u001dqBA\u0002J]R\u0004"
)
public class RDDOperationCluster {
   private final String id;
   private String _name;
   private final ListBuffer<RDDOperationNode> _childNodes;
   private final ListBuffer<RDDOperationCluster> _childClusters;

   public String id() {
      return this.id;
   }

   private String _name() {
      return this._name;
   }

   private void _name_$eq(String x$1) {
      this._name = x$1;
   }

   private ListBuffer<RDDOperationNode> _childNodes() {
      return this._childNodes;
   }

   private ListBuffer<RDDOperationCluster> _childClusters() {
      return this._childClusters;
   }

   public String name() {
      return this._name();
   }

   public void setName(String n) {
      this._name_$eq(n);
   }

   public Seq<RDDOperationNode> childNodes() {
      return this._childNodes().iterator().toSeq();
   }

   public Seq<RDDOperationCluster> childClusters() {
      return this._childClusters().iterator().toSeq();
   }

   public void attachChildNode(RDDOperationNode childNode) {
      this._childNodes().$plus$eq(childNode);
   }

   public void attachChildCluster(RDDOperationCluster childCluster) {
      this._childClusters().$plus$eq(childCluster);
   }

   public Seq<RDDOperationNode> getCachedNodes() {
      return ((BufferLike)this._childNodes().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(RDDOperationNode x$1) {
            return x$1.cached();
         }
      })).$plus$plus((GenTraversableOnce)this._childClusters().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<RDDOperationNode> apply(RDDOperationCluster x$2) {
            return x$2.getCachedNodes();
         }
      }, ListBuffer$.MODULE$.canBuildFrom()));
   }

   public boolean canEqual(Object other) {
      return other instanceof RDDOperationCluster;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof RDDOperationCluster) {
         boolean var10;
         label40: {
            label46: {
               RDDOperationCluster var3 = (RDDOperationCluster)other;
               if (var3.canEqual(this)) {
                  label45: {
                     ListBuffer var10000 = this._childClusters();
                     ListBuffer var5 = var3._childClusters();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label45;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label45;
                     }

                     String var8 = this.id();
                     String var6 = var3.id();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label45;
                        }
                     } else if (!var8.equals(var6)) {
                        break label45;
                     }

                     var8 = this._name();
                     String var7 = var3._name();
                     if (var8 == null) {
                        if (var7 == null) {
                           break label46;
                        }
                     } else if (var8.equals(var7)) {
                        break label46;
                     }
                  }
               }

               var10 = false;
               break label40;
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
      Seq state = (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Object[]{this._childClusters(), this.id(), this._name()}));
      return BoxesRunTime.unboxToInt(((TraversableOnce)state.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Object x$1) {
            return Objects.hashCode(x$1);
         }
      }, Seq$.MODULE$.canBuildFrom())).foldLeft(BoxesRunTime.boxToInteger(0), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(int a, int b) {
            return this.apply$mcIII$sp(a, b);
         }

         public int apply$mcIII$sp(int a, int b) {
            return 31 * a + b;
         }
      }));
   }

   public RDDOperationCluster(String id, String _name) {
      this.id = id;
      this._name = _name;
      super();
      this._childNodes = new ListBuffer<RDDOperationNode>();
      this._childClusters = new ListBuffer<RDDOperationCluster>();
   }
}
