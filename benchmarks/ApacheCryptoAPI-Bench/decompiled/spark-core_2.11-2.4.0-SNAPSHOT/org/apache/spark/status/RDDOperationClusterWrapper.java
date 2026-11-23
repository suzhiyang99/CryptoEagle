package org.apache.spark.status;

import org.apache.spark.ui.scope.RDDOperationCluster;
import org.apache.spark.ui.scope.RDDOperationNode;
import scala.Serializable;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A3Q!\u0001\u0002\u0001\t)\u0011!D\u0015#E\u001fB,'/\u0019;j_:\u001cE.^:uKJ<&/\u00199qKJT!a\u0001\u0003\u0002\rM$\u0018\r^;t\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#\u0001\u0002jI\u000e\u0001Q#A\u000b\u0011\u0005YIbB\u0001\u0007\u0018\u0013\tAR\"\u0001\u0004Qe\u0016$WMZ\u0005\u00035m\u0011aa\u0015;sS:<'B\u0001\r\u000e\u0011!i\u0002A!A!\u0002\u0013)\u0012aA5eA!Aq\u0004\u0001BC\u0002\u0013\u0005A#\u0001\u0003oC6,\u0007\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011B\u000b\u0002\u000b9\fW.\u001a\u0011\t\u0011\r\u0002!Q1A\u0005\u0002\u0011\n!b\u00195jY\u0012tu\u000eZ3t+\u0005)\u0003c\u0001\u0014/c9\u0011q\u0005\f\b\u0003Q-j\u0011!\u000b\u0006\u0003UM\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u00055j\u0011a\u00029bG.\fw-Z\u0005\u0003_A\u00121aU3r\u0015\tiS\u0002\u0005\u00023o5\t1G\u0003\u00025k\u0005)1oY8qK*\u0011a\u0007B\u0001\u0003k&L!\u0001O\u001a\u0003!I#Ei\u00149fe\u0006$\u0018n\u001c8O_\u0012,\u0007\u0002\u0003\u001e\u0001\u0005\u0003\u0005\u000b\u0011B\u0013\u0002\u0017\rD\u0017\u000e\u001c3O_\u0012,7\u000f\t\u0005\ty\u0001\u0011)\u0019!C\u0001{\u0005i1\r[5mI\u000ecWo\u001d;feN,\u0012A\u0010\t\u0004M9z\u0004C\u0001!\u0001\u001b\u0005\u0011\u0001\u0002\u0003\"\u0001\u0005\u0003\u0005\u000b\u0011\u0002 \u0002\u001d\rD\u0017\u000e\u001c3DYV\u001cH/\u001a:tA!)A\t\u0001C\u0001\u000b\u00061A(\u001b8jiz\"Ra\u0010$H\u0011&CQAE\"A\u0002UAQaH\"A\u0002UAQaI\"A\u0002\u0015BQ\u0001P\"A\u0002yBQa\u0013\u0001\u0005\u00021\u000bQ\u0003^8S\t\u0012{\u0005/\u001a:bi&|gn\u00117vgR,'\u000fF\u0001N!\t\u0011d*\u0003\u0002Pg\t\u0019\"\u000b\u0012#Pa\u0016\u0014\u0018\r^5p]\u000ecWo\u001d;fe\u0002"
)
public class RDDOperationClusterWrapper {
   private final String id;
   private final String name;
   private final Seq<RDDOperationNode> childNodes;
   private final Seq<RDDOperationClusterWrapper> childClusters;

   public String id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public Seq<RDDOperationNode> childNodes() {
      return this.childNodes;
   }

   public Seq<RDDOperationClusterWrapper> childClusters() {
      return this.childClusters;
   }

   public RDDOperationCluster toRDDOperationCluster() {
      RDDOperationCluster cluster = new RDDOperationCluster(this.id(), this.name());
      this.childNodes().foreach(new Serializable(this, cluster) {
         public static final long serialVersionUID = 0L;
         private final RDDOperationCluster cluster$1;

         public final void apply(RDDOperationNode childNode) {
            this.cluster$1.attachChildNode(childNode);
         }

         public {
            this.cluster$1 = cluster$1;
         }
      });
      this.childClusters().foreach(new Serializable(this, cluster) {
         public static final long serialVersionUID = 0L;
         private final RDDOperationCluster cluster$1;

         public final void apply(RDDOperationClusterWrapper child) {
            this.cluster$1.attachChildCluster(child.toRDDOperationCluster());
         }

         public {
            this.cluster$1 = cluster$1;
         }
      });
      return cluster;
   }

   public RDDOperationClusterWrapper(String id, String name, Seq<RDDOperationNode> childNodes, Seq<RDDOperationClusterWrapper> childClusters) {
      this.id = id;
      this.name = name;
      this.childNodes = childNodes;
      this.childClusters = childClusters;
   }
}
