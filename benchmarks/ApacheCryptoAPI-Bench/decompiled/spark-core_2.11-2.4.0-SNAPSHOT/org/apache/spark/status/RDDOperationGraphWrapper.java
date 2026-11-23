package org.apache.spark.status;

import org.apache.spark.ui.scope.RDDOperationEdge;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.apache.spark.util.kvstore.KVIndex;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00194Q!\u0001\u0002\u0001\t)\u0011\u0001D\u0015#E\u001fB,'/\u0019;j_:<%/\u00199i/J\f\u0007\u000f]3s\u0015\t\u0019A!\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0011\u0001a\u0003\t\u0003\u0019=i\u0011!\u0004\u0006\u0002\u001d\u0005)1oY1mC&\u0011\u0001#\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011I\u0001!Q1A\u0005\u0002Q\tqa\u001d;bO\u0016LEm\u0001\u0001\u0016\u0003U\u0001\"\u0001\u0004\f\n\u0005]i!aA%oi\"\u0012\u0011#\u0007\u0016\u00035\t\u0002\"a\u0007\u0011\u000e\u0003qQ!!\b\u0010\u0002\u000f-48\u000f^8sK*\u0011q\u0004B\u0001\u0005kRLG.\u0003\u0002\"9\t91JV%oI\u0016D8&A\u0012\u0011\u0005\u0011JS\"A\u0013\u000b\u0005\u0019:\u0013\u0001B7fi\u0006T!\u0001K\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002+K\t1q-\u001a;uKJD\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006I!F\u0001\tgR\fw-Z%eA!Aa\u0006\u0001BC\u0002\u0013\u0005q&A\u0003fI\u001e,7/F\u00011!\r\t\u0014\b\u0010\b\u0003e]r!a\r\u001c\u000e\u0003QR!!N\n\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0011B\u0001\u001d\u000e\u0003\u001d\u0001\u0018mY6bO\u0016L!AO\u001e\u0003\u0007M+\u0017O\u0003\u00029\u001bA\u0011QHQ\u0007\u0002})\u0011q\bQ\u0001\u0006g\u000e|\u0007/\u001a\u0006\u0003\u0003\u0012\t!!^5\n\u0005\rs$\u0001\u0005*E\t>\u0003XM]1uS>tW\tZ4f\u0011!)\u0005A!A!\u0002\u0013\u0001\u0014AB3eO\u0016\u001c\b\u0005\u0003\u0005H\u0001\t\u0015\r\u0011\"\u00010\u00035yW\u000f^4pS:<W\tZ4fg\"A\u0011\n\u0001B\u0001B\u0003%\u0001'\u0001\bpkR<w.\u001b8h\u000b\u0012<Wm\u001d\u0011\t\u0011-\u0003!Q1A\u0005\u0002=\nQ\"\u001b8d_6LgnZ#eO\u0016\u001c\b\u0002C'\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u001d%t7m\\7j]\u001e,EmZ3tA!Aq\n\u0001BC\u0002\u0013\u0005\u0001+A\u0006s_>$8\t\\;ti\u0016\u0014X#A)\u0011\u0005I\u001bV\"\u0001\u0002\n\u0005Q\u0013!A\u0007*E\t>\u0003XM]1uS>t7\t\\;ti\u0016\u0014xK]1qa\u0016\u0014\b\u0002\u0003,\u0001\u0005\u0003\u0005\u000b\u0011B)\u0002\u0019I|w\u000e^\"mkN$XM\u001d\u0011\t\u000ba\u0003A\u0011A-\u0002\rqJg.\u001b;?)\u0019Q6\fX/_?B\u0011!\u000b\u0001\u0005\u0006%]\u0003\r!\u0006\u0005\u0006]]\u0003\r\u0001\r\u0005\u0006\u000f^\u0003\r\u0001\r\u0005\u0006\u0017^\u0003\r\u0001\r\u0005\u0006\u001f^\u0003\r!\u0015\u0005\u0006C\u0002!\tAY\u0001\u0014i>\u0014F\tR(qKJ\fG/[8o\u000fJ\f\u0007\u000f\u001b\u000b\u0002GB\u0011Q\bZ\u0005\u0003Kz\u0012\u0011C\u0015#E\u001fB,'/\u0019;j_:<%/\u00199i\u0001"
)
public class RDDOperationGraphWrapper {
   private final int stageId;
   private final Seq<RDDOperationEdge> edges;
   private final Seq<RDDOperationEdge> outgoingEdges;
   private final Seq<RDDOperationEdge> incomingEdges;
   private final RDDOperationClusterWrapper rootCluster;

   @KVIndex
   public int stageId() {
      return this.stageId;
   }

   public Seq<RDDOperationEdge> edges() {
      return this.edges;
   }

   public Seq<RDDOperationEdge> outgoingEdges() {
      return this.outgoingEdges;
   }

   public Seq<RDDOperationEdge> incomingEdges() {
      return this.incomingEdges;
   }

   public RDDOperationClusterWrapper rootCluster() {
      return this.rootCluster;
   }

   public RDDOperationGraph toRDDOperationGraph() {
      return new RDDOperationGraph(this.edges(), this.outgoingEdges(), this.incomingEdges(), this.rootCluster().toRDDOperationCluster());
   }

   public RDDOperationGraphWrapper(int stageId, Seq<RDDOperationEdge> edges, Seq<RDDOperationEdge> outgoingEdges, Seq<RDDOperationEdge> incomingEdges, RDDOperationClusterWrapper rootCluster) {
      this.stageId = stageId;
      this.edges = edges;
      this.outgoingEdges = outgoingEdges;
      this.incomingEdges = incomingEdges;
      this.rootCluster = rootCluster;
   }
}
