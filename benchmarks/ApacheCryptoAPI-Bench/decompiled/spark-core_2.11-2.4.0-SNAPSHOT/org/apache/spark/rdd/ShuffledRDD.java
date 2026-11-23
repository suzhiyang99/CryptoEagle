package org.apache.spark.rdd;

import org.apache.spark.Aggregator;
import org.apache.spark.Dependency;
import org.apache.spark.MapOutputTrackerMaster;
import org.apache.spark.Partition;
import org.apache.spark.Partitioner;
import org.apache.spark.ShuffleDependency;
import org.apache.spark.SparkEnv$;
import org.apache.spark.TaskContext;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerManager;
import scala.Array$;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product2;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List$;
import scala.collection.immutable.Nil$;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}h\u0001B\u0001\u0003\u0001-\u00111b\u00155vM\u001adW\r\u001a*E\t*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001U!A\"\u0007\u001d$'\t\u0001Q\u0002E\u0002\u000f\u001fEi\u0011AA\u0005\u0003!\t\u00111A\u0015#E!\u0011\u0011Rc\u0006\u0012\u000e\u0003MQ\u0011\u0001F\u0001\u0006g\u000e\fG.Y\u0005\u0003-M\u0011a\u0001V;qY\u0016\u0014\u0004C\u0001\r\u001a\u0019\u0001!QA\u0007\u0001C\u0002m\u0011\u0011aS\t\u00039}\u0001\"AE\u000f\n\u0005y\u0019\"a\u0002(pi\"Lgn\u001a\t\u0003%\u0001J!!I\n\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u0019G\u0011)A\u0005\u0001b\u00017\t\t1\t\u0003\u0005'\u0001\t\u0005\r\u0011\"\u0001(\u0003\u0011\u0001(/\u001a<\u0016\u0003!\u0002$!K\u0016\u0011\u00079y!\u0006\u0005\u0002\u0019W\u0011IA&LA\u0001\u0002\u0003\u0015\ta\r\u0002\u0004?\u0012\n\u0004\u0002\u0003\u0018\u0001\u0005\u0003\u0005\u000b\u0015B\u0018\u0002\u000bA\u0014XM\u001e\u00111\u0005A\u0012\u0004c\u0001\b\u0010cA\u0011\u0001D\r\u0003\nY5\n\t\u0011!A\u0003\u0002M\n\"\u0001\b\u001b\u0011\tI)tcN\u0005\u0003mM\u0011\u0001\u0002\u0015:pIV\u001cGO\r\t\u00031a\"Q!\u000f\u0001C\u0002m\u0011\u0011A\u0016\u0015\u0003[m\u0002\"A\u0005\u001f\n\u0005u\u001a\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011!y\u0004A!a\u0001\n\u0003\u0001\u0015\u0001\u00039sKZ|F%Z9\u0015\u0005\u0005#\u0005C\u0001\nC\u0013\t\u00195C\u0001\u0003V]&$\bbB#?\u0003\u0003\u0005\rAR\u0001\u0004q\u0012\n\u0004GA$J!\rqq\u0002\u0013\t\u00031%#\u0011\u0002L\u0017\u0002\u0002\u0003\u0005)\u0011A\u001a\t\u0011-\u0003!\u0011!Q\u0001\n1\u000bA\u0001]1siB\u0011QJT\u0007\u0002\t%\u0011q\n\u0002\u0002\f!\u0006\u0014H/\u001b;j_:,'\u000f\u0003\u0005R\u0001\t\r\t\u0015a\u0003S\u0003))g/\u001b3f]\u000e,G%\r\t\u0004'Z;R\"\u0001+\u000b\u0005U\u001b\u0012a\u0002:fM2,7\r^\u0005\u0003/R\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\t3\u0002\u0011\u0019\u0011)A\u00065\u0006QQM^5eK:\u001cW\r\n\u001a\u0011\u0007M3v\u0007\u0003\u0005]\u0001\t\r\t\u0015a\u0003^\u0003))g/\u001b3f]\u000e,Ge\r\t\u0004'Z\u0013\u0003\"B0\u0001\t\u0003\u0001\u0017A\u0002\u001fj]&$h\bF\u0002bM.$BAY2eKB)a\u0002A\f8E!)\u0011K\u0018a\u0002%\")\u0011L\u0018a\u00025\")AL\u0018a\u0002;\")aE\u0018a\u0001OB\u0012\u0001N\u001b\t\u0004\u001d=I\u0007C\u0001\rk\t%ac-!A\u0001\u0002\u000b\u00051\u0007C\u0003L=\u0002\u0007A\nC\u0004n\u0001\u0001\u0007I\u0011\u00028\u0002/U\u001cXM]*qK\u000eLg-[3e'\u0016\u0014\u0018.\u00197ju\u0016\u0014X#A8\u0011\u0007I\u0001(/\u0003\u0002r'\t1q\n\u001d;j_:\u0004\"a\u001d<\u000e\u0003QT!!\u001e\u0003\u0002\u0015M,'/[1mSj,'/\u0003\u0002xi\nQ1+\u001a:jC2L'0\u001a:\t\u000fe\u0004\u0001\u0019!C\u0005u\u0006YRo]3s'B,7-\u001b4jK\u0012\u001cVM]5bY&TXM]0%KF$\"!Q>\t\u000f\u0015C\u0018\u0011!a\u0001_\"1Q\u0010\u0001Q!\n=\f\u0001$^:feN\u0003XmY5gS\u0016$7+\u001a:jC2L'0\u001a:!\u0011!y\b\u00011A\u0005\n\u0005\u0005\u0011aC6fs>\u0013H-\u001a:j]\u001e,\"!a\u0001\u0011\tI\u0001\u0018Q\u0001\t\u0006\u0003\u000f\t9b\u0006\b\u0005\u0003\u0013\t\u0019B\u0004\u0003\u0002\f\u0005EQBAA\u0007\u0015\r\tyAC\u0001\u0007yI|w\u000e\u001e \n\u0003QI1!!\u0006\u0014\u0003\u001d\u0001\u0018mY6bO\u0016LA!!\u0007\u0002\u001c\tAqJ\u001d3fe&twMC\u0002\u0002\u0016MA\u0011\"a\b\u0001\u0001\u0004%I!!\t\u0002\u001f-,\u0017p\u0014:eKJLgnZ0%KF$2!QA\u0012\u0011%)\u0015QDA\u0001\u0002\u0004\t\u0019\u0001\u0003\u0005\u0002(\u0001\u0001\u000b\u0015BA\u0002\u00031YW-_(sI\u0016\u0014\u0018N\\4!\u0011%\tY\u0003\u0001a\u0001\n\u0013\ti#\u0001\u0006bO\u001e\u0014XmZ1u_J,\"!a\f\u0011\tI\u0001\u0018\u0011\u0007\t\u0007\u001b\u0006Mrc\u000e\u0012\n\u0007\u0005UBA\u0001\u0006BO\u001e\u0014XmZ1u_JD\u0011\"!\u000f\u0001\u0001\u0004%I!a\u000f\u0002\u001d\u0005<wM]3hCR|'o\u0018\u0013fcR\u0019\u0011)!\u0010\t\u0013\u0015\u000b9$!AA\u0002\u0005=\u0002\u0002CA!\u0001\u0001\u0006K!a\f\u0002\u0017\u0005<wM]3hCR|'\u000f\t\u0005\n\u0003\u000b\u0002\u0001\u0019!C\u0005\u0003\u000f\na\"\\1q'&$WmQ8nE&tW-\u0006\u0002\u0002JA\u0019!#a\u0013\n\u0007\u000553CA\u0004C_>dW-\u00198\t\u0013\u0005E\u0003\u00011A\u0005\n\u0005M\u0013AE7baNKG-Z\"p[\nLg.Z0%KF$2!QA+\u0011%)\u0015qJA\u0001\u0002\u0004\tI\u0005\u0003\u0005\u0002Z\u0001\u0001\u000b\u0015BA%\u0003=i\u0017\r]*jI\u0016\u001cu.\u001c2j]\u0016\u0004\u0003bBA/\u0001\u0011\u0005\u0011qL\u0001\u000eg\u0016$8+\u001a:jC2L'0\u001a:\u0015\u0007\t\f\t\u0007\u0003\u0004v\u00037\u0002\rA\u001d\u0005\b\u0003K\u0002A\u0011AA4\u00039\u0019X\r^&fs>\u0013H-\u001a:j]\u001e$2AYA5\u0011\u001dy\u00181\ra\u0001\u0003\u000bAq!!\u001c\u0001\t\u0003\ty'A\u0007tKR\fum\u001a:fO\u0006$xN\u001d\u000b\u0004E\u0006E\u0004\u0002CA\u0016\u0003W\u0002\r!!\r\t\u000f\u0005U\u0004\u0001\"\u0001\u0002x\u0005\t2/\u001a;NCB\u001c\u0016\u000eZ3D_6\u0014\u0017N\\3\u0015\u0007\t\fI\b\u0003\u0005\u0002F\u0005M\u0004\u0019AA%\u0011\u001d\ti\b\u0001C!\u0003\u007f\nqbZ3u\t\u0016\u0004XM\u001c3f]\u000eLWm]\u000b\u0003\u0003\u0003\u0003b!a\u0002\u0002\u0004\u0006\u001d\u0015\u0002BAC\u00037\u00111aU3ra\u0011\tI)!%\u0011\u000b5\u000bY)a$\n\u0007\u00055EA\u0001\u0006EKB,g\u000eZ3oGf\u00042\u0001GAI\t-\t\u0019*a\u001f\u0002\u0002\u0003\u0005)\u0011A\u000e\u0003\u0007}##\u0007C\u0005\u0002\u0018\u0002\u0011\r\u0011\"\u0011\u0002\u001a\u0006Y\u0001/\u0019:uSRLwN\\3s+\t\tY\n\u0005\u0003\u0013\u0003;c\u0015bAAP'\t!1k\\7f\u0011!\t\u0019\u000b\u0001Q\u0001\n\u0005m\u0015\u0001\u00049beRLG/[8oKJ\u0004\u0003bBAT\u0001\u0011\u0005\u0013\u0011V\u0001\u000eO\u0016$\b+\u0019:uSRLwN\\:\u0016\u0005\u0005-\u0006#\u0002\n\u0002.\u0006E\u0016bAAX'\t)\u0011I\u001d:bsB\u0019Q*a-\n\u0007\u0005UFAA\u0005QCJ$\u0018\u000e^5p]\"9\u0011\u0011\u0018\u0001\u0005R\u0005m\u0016!F4fiB\u0013XMZ3se\u0016$Gj\\2bi&|gn\u001d\u000b\u0005\u0003{\u000bi\r\u0005\u0004\u0002\b\u0005\r\u0015q\u0018\t\u0005\u0003\u0003\f9MD\u0002\u0013\u0003\u0007L1!!2\u0014\u0003\u0019\u0001&/\u001a3fM&!\u0011\u0011ZAf\u0005\u0019\u0019FO]5oO*\u0019\u0011QY\n\t\u0011\u0005=\u0017q\u0017a\u0001\u0003c\u000b\u0011\u0002]1si&$\u0018n\u001c8\t\u000f\u0005M\u0007\u0001\"\u0011\u0002V\u000691m\\7qkR,GCBAl\u0003;\f\t\u000fE\u0003\u0002\b\u0005e\u0017#\u0003\u0003\u0002\\\u0006m!\u0001C%uKJ\fGo\u001c:\t\u0011\u0005}\u0017\u0011\u001ba\u0001\u0003c\u000bQa\u001d9mSRD\u0001\"a9\u0002R\u0002\u0007\u0011Q]\u0001\bG>tG/\u001a=u!\ri\u0015q]\u0005\u0004\u0003S$!a\u0003+bg.\u001cuN\u001c;fqRDq!!<\u0001\t\u0003\ny/A\tdY\u0016\f'\u000fR3qK:$WM\\2jKN$\u0012!\u0011\u0015\u0004\u0001\u0005M\b\u0003BA{\u0003wl!!a>\u000b\u0007\u0005eH!\u0001\u0006b]:|G/\u0019;j_:LA!!@\u0002x\naA)\u001a<fY>\u0004XM]!qS\u0002"
)
public class ShuffledRDD<K, V, C> extends RDD<Tuple2<K, C>> {
   private transient RDD<? extends Product2<K, V>> prev;
   private final Partitioner part;
   public final ClassTag<K> org$apache$spark$rdd$ShuffledRDD$$evidence$1;
   public final ClassTag<V> org$apache$spark$rdd$ShuffledRDD$$evidence$2;
   public final ClassTag<C> org$apache$spark$rdd$ShuffledRDD$$evidence$3;
   private Option<Serializer> userSpecifiedSerializer;
   private Option<Ordering<K>> keyOrdering;
   private Option<Aggregator<K, V, C>> aggregator;
   private boolean org$apache$spark$rdd$ShuffledRDD$$mapSideCombine;
   private final Some<Partitioner> partitioner;

   public RDD<? extends Product2<K, V>> prev() {
      return this.prev;
   }

   public void prev_$eq(RDD<? extends Product2<K, V>> x$1) {
      this.prev = x$1;
   }

   private Option<Serializer> userSpecifiedSerializer() {
      return this.userSpecifiedSerializer;
   }

   private void userSpecifiedSerializer_$eq(Option<Serializer> x$1) {
      this.userSpecifiedSerializer = x$1;
   }

   private Option<Ordering<K>> keyOrdering() {
      return this.keyOrdering;
   }

   private void keyOrdering_$eq(Option<Ordering<K>> x$1) {
      this.keyOrdering = x$1;
   }

   private Option<Aggregator<K, V, C>> aggregator() {
      return this.aggregator;
   }

   private void aggregator_$eq(Option<Aggregator<K, V, C>> x$1) {
      this.aggregator = x$1;
   }

   public boolean org$apache$spark$rdd$ShuffledRDD$$mapSideCombine() {
      return this.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine;
   }

   private void org$apache$spark$rdd$ShuffledRDD$$mapSideCombine_$eq(boolean x$1) {
      this.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine = x$1;
   }

   public ShuffledRDD<K, V, C> setSerializer(Serializer serializer) {
      this.userSpecifiedSerializer_$eq(Option$.MODULE$.apply(serializer));
      return this;
   }

   public ShuffledRDD<K, V, C> setKeyOrdering(Ordering<K> keyOrdering) {
      this.keyOrdering_$eq(Option$.MODULE$.apply(keyOrdering));
      return this;
   }

   public ShuffledRDD<K, V, C> setAggregator(Aggregator<K, V, C> aggregator) {
      this.aggregator_$eq(Option$.MODULE$.apply(aggregator));
      return this;
   }

   public ShuffledRDD<K, V, C> setMapSideCombine(boolean mapSideCombine) {
      this.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine_$eq(mapSideCombine);
      return this;
   }

   public Seq<Dependency<?>> getDependencies() {
      Serializer serializer = (Serializer)this.userSpecifiedSerializer().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffledRDD $outer;

         public final Serializer apply() {
            SerializerManager serializerManager = SparkEnv$.MODULE$.get().serializerManager();
            return this.$outer.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine() ? serializerManager.getSerializer((ClassTag)Predef$.MODULE$.implicitly(this.$outer.org$apache$spark$rdd$ShuffledRDD$$evidence$1), (ClassTag)Predef$.MODULE$.implicitly(this.$outer.org$apache$spark$rdd$ShuffledRDD$$evidence$3)) : serializerManager.getSerializer((ClassTag)Predef$.MODULE$.implicitly(this.$outer.org$apache$spark$rdd$ShuffledRDD$$evidence$1), (ClassTag)Predef$.MODULE$.implicitly(this.$outer.org$apache$spark$rdd$ShuffledRDD$$evidence$2));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new ShuffleDependency[]{new ShuffleDependency(this.prev(), this.part, serializer, this.keyOrdering(), this.aggregator(), this.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine(), this.org$apache$spark$rdd$ShuffledRDD$$evidence$1, this.org$apache$spark$rdd$ShuffledRDD$$evidence$2, this.org$apache$spark$rdd$ShuffledRDD$$evidence$3)}));
   }

   public Some<Partitioner> partitioner() {
      return this.partitioner;
   }

   public Partition[] getPartitions() {
      return (Partition[])Array$.MODULE$.tabulate(this.part.numPartitions(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ShuffledRDDPartition apply(int i) {
            return new ShuffledRDDPartition(i);
         }
      }, ClassTag$.MODULE$.apply(Partition.class));
   }

   public Seq<String> getPreferredLocations(Partition partition) {
      MapOutputTrackerMaster tracker = (MapOutputTrackerMaster)SparkEnv$.MODULE$.get().mapOutputTracker();
      ShuffleDependency dep = (ShuffleDependency)this.dependencies().head();
      return tracker.getPreferredLocationsForShuffle(dep, partition.index());
   }

   public Iterator<Tuple2<K, C>> compute(Partition split, TaskContext context) {
      ShuffleDependency dep = (ShuffleDependency)this.dependencies().head();
      return SparkEnv$.MODULE$.get().shuffleManager().getReader(dep.shuffleHandle(), split.index(), split.index() + 1, context).read();
   }

   public void clearDependencies() {
      super.clearDependencies();
      this.prev_$eq((RDD)null);
   }

   public ShuffledRDD(RDD<? extends Product2<K, V>> prev, Partitioner part, ClassTag<K> evidence$1, ClassTag<V> evidence$2, ClassTag<C> evidence$3) {
      this.prev = prev;
      this.part = part;
      this.org$apache$spark$rdd$ShuffledRDD$$evidence$1 = evidence$1;
      this.org$apache$spark$rdd$ShuffledRDD$$evidence$2 = evidence$2;
      this.org$apache$spark$rdd$ShuffledRDD$$evidence$3 = evidence$3;
      super(prev.context(), Nil$.MODULE$, ClassTag$.MODULE$.apply(Tuple2.class));
      this.userSpecifiedSerializer = None$.MODULE$;
      this.keyOrdering = None$.MODULE$;
      this.aggregator = None$.MODULE$;
      this.org$apache$spark$rdd$ShuffledRDD$$mapSideCombine = false;
      this.partitioner = new Some<Partitioner>(part);
   }
}
