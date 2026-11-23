package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.scheduler.TaskLocation;
import scala.Array$;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.IndexedSeqOptimized;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.LinkedHashMap;
import scala.collection.mutable.LinkedHashMap$;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.ResizableArray;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.RichInt$;
import scala.util.Random;

@ScalaSignature(
   bytes = "\u0006\u0001\t5b\u0001B\u0001\u0003\t-\u0011\u0011\u0004R3gCVdG\u000fU1si&$\u0018n\u001c8D_\u0006dWm]2fe*\u00111\u0001B\u0001\u0004e\u0012$'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019B#D\u0001\u0003\u0013\t)\"A\u0001\nQCJ$\u0018\u000e^5p]\u000e{\u0017\r\\3tG\u0016\u0014\b\u0002C\f\u0001\u0005\u000b\u0007I\u0011\u0001\r\u0002\u0019\t\fG.\u00198dKNc\u0017mY6\u0016\u0003e\u0001\"!\u0004\u000e\n\u0005mq!A\u0002#pk\ndW\r\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u001a\u00035\u0011\u0017\r\\1oG\u0016\u001cF.Y2lA!)q\u0004\u0001C\u0001A\u00051A(\u001b8jiz\"\"!\t\u0012\u0011\u0005M\u0001\u0001bB\f\u001f!\u0003\u0005\r!\u0007\u0005\u0006I\u0001!\t!J\u0001\bG>l\u0007/\u0019:f)\r1\u0013F\f\t\u0003\u001b\u001dJ!\u0001\u000b\b\u0003\u000f\t{w\u000e\\3b]\")!f\ta\u0001W\u0005\u0011q.\r\t\u0003'1J!!\f\u0002\u0003\u001dA\u000b'\u000f^5uS>twI]8va\")qf\ta\u0001W\u0005\u0011qN\r\u0005\u0006I\u0001!\t!\r\u000b\u0004MI2\u0004\"\u0002\u00161\u0001\u0004\u0019\u0004cA\u00075W%\u0011QG\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u000b=\u0002\u0004\u0019A\u001a\t\u000fa\u0002!\u0019!C\u0001s\u0005\u0019!O\u001c3\u0016\u0003i\u0002\"a\u000f \u000e\u0003qR!!\u0010\b\u0002\tU$\u0018\u000e\\\u0005\u0003\u007fq\u0012aAU1oI>l\u0007BB!\u0001A\u0003%!(\u0001\u0003s]\u0012\u0004\u0003bB\"\u0001\u0005\u0004%\t\u0001R\u0001\tOJ|W\u000f]!seV\tQ\tE\u0002G\u0017.j\u0011a\u0012\u0006\u0003\u0011&\u000bq!\\;uC\ndWM\u0003\u0002K\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u00051;%aC!se\u0006L()\u001e4gKJDaA\u0014\u0001!\u0002\u0013)\u0015!C4s_V\u0004\u0018I\u001d:!\u0011\u001d\u0001\u0006A1A\u0005\u0002E\u000b\u0011b\u001a:pkBD\u0015m\u001d5\u0016\u0003I\u0003BAR*V\u000b&\u0011Ak\u0012\u0002\u0004\u001b\u0006\u0004\bC\u0001,Z\u001d\tiq+\u0003\u0002Y\u001d\u00051\u0001K]3eK\u001aL!AW.\u0003\rM#(/\u001b8h\u0015\tAf\u0002\u0003\u0004^\u0001\u0001\u0006IAU\u0001\u000bOJ|W\u000f\u001d%bg\"\u0004\u0003bB0\u0001\u0005\u0004%\t\u0001Y\u0001\fS:LG/[1m\u0011\u0006\u001c\b.F\u0001b!\r1%\rZ\u0005\u0003G\u001e\u00131aU3u!\t)g-D\u0001\u0005\u0013\t9GAA\u0005QCJ$\u0018\u000e^5p]\"1\u0011\u000e\u0001Q\u0001\n\u0005\fA\"\u001b8ji&\fG\u000eS1tQ\u0002Bqa\u001b\u0001A\u0002\u0013\u0005A.\u0001\u0006o_2{7-\u00197jif,\u0012A\n\u0005\b]\u0002\u0001\r\u0011\"\u0001p\u00039qw\u000eT8dC2LG/_0%KF$\"\u0001]:\u0011\u00055\t\u0018B\u0001:\u000f\u0005\u0011)f.\u001b;\t\u000fQl\u0017\u0011!a\u0001M\u0005\u0019\u0001\u0010J\u0019\t\rY\u0004\u0001\u0015)\u0003'\u0003-qw\u000eT8dC2LG/\u001f\u0011\t\u000ba\u0004A\u0011A=\u0002\u0019\r,(O\u001d)sK\u001adunY:\u0015\u000bi\fi!!\u0005\u0011\tm\f9!\u0016\b\u0004y\u0006\rabA?\u0002\u00025\taP\u0003\u0002\u0000\u0015\u00051AH]8pizJ\u0011aD\u0005\u0004\u0003\u000bq\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003\u0013\tYAA\u0002TKFT1!!\u0002\u000f\u0011\u0019\tya\u001ea\u0001I\u0006!\u0001/\u0019:u\u0011\u001d\t\u0019b\u001ea\u0001\u0003+\tA\u0001\u001d:fmB\"\u0011qCA\u0011!\u0015\u0019\u0012\u0011DA\u000f\u0013\r\tYB\u0001\u0002\u0004%\u0012#\u0005\u0003BA\u0010\u0003Ca\u0001\u0001\u0002\u0007\u0002$\u0005E\u0011\u0011!A\u0001\u0006\u0003\t)CA\u0002`IM\nB!a\n\u0002.A\u0019Q\"!\u000b\n\u0007\u0005-bBA\u0004O_RD\u0017N\\4\u0011\u00075\ty#C\u0002\u000229\u00111!\u00118z\r\u0019\t)\u0004\u0001\u0001\u00028\t\u0011\u0002+\u0019:uSRLwN\u001c'pG\u0006$\u0018n\u001c8t'\r\t\u0019\u0004\u0004\u0005\f\u0003'\t\u0019D!A!\u0002\u0013\tY\u0004\r\u0003\u0002>\u0005\u0005\u0003#B\n\u0002\u001a\u0005}\u0002\u0003BA\u0010\u0003\u0003\"A\"a\u0011\u0002:\u0005\u0005\t\u0011!B\u0001\u0003K\u00111a\u0018\u00135\u0011\u001dy\u00121\u0007C\u0001\u0003\u000f\"B!!\u0013\u0002NA!\u00111JA\u001a\u001b\u0005\u0001\u0001\u0002CA\n\u0003\u000b\u0002\r!a\u00141\t\u0005E\u0013Q\u000b\t\u0006'\u0005e\u00111\u000b\t\u0005\u0003?\t)\u0006\u0002\u0007\u0002D\u00055\u0013\u0011!A\u0001\u0006\u0003\t)\u0003\u0003\u0006\u0002Z\u0005M\"\u0019!C\u0001\u00037\n\u0001\u0003]1siN<\u0016\u000e\u001e5pkRdunY:\u0016\u0005\u0005u\u0003c\u0001$LI\"I\u0011\u0011MA\u001aA\u0003%\u0011QL\u0001\u0012a\u0006\u0014Ho],ji\"|W\u000f\u001e'pGN\u0004\u0003BCA3\u0003g\u0011\r\u0011\"\u0001\u0002h\u0005i\u0001/\u0019:ug^KG\u000f\u001b'pGN,\"!!\u001b\u0011\t\u0019[\u00151\u000e\t\u0006\u001b\u00055T\u000bZ\u0005\u0004\u0003_r!A\u0002+va2,'\u0007C\u0005\u0002t\u0005M\u0002\u0015!\u0003\u0002j\u0005q\u0001/\u0019:ug^KG\u000f\u001b'pGN\u0004\u0003\u0002CA<\u0003g!\t!!\u001f\u0002\u001d\u001d,G/\u00117m!J,g\rT8dgR\u0019\u0001/a\u001f\t\u0011\u0005M\u0011Q\u000fa\u0001\u0003{\u0002D!a \u0002\u0004B)1#!\u0007\u0002\u0002B!\u0011qDAB\t1\t))a\u001f\u0002\u0002\u0003\u0005)\u0011AA\u0013\u0005\ryF%\u000e\u0005\b\u0003\u0013\u0003A\u0011AAF\u0003E9W\r\u001e'fCN$xI]8va\"\u000b7\u000f\u001b\u000b\u0004g\u00055\u0005bBAH\u0003\u000f\u0003\r!V\u0001\u0004W\u0016L\bbBAJ\u0001\u0011\u0005\u0011QS\u0001\u0010C\u0012$\u0007+\u0019:u)>\u0004vI]8vaR)a%a&\u0002\u001a\"9\u0011qBAI\u0001\u0004!\u0007bBAN\u0003#\u0003\raK\u0001\u0007a\u001e\u0014x.\u001e9\t\u000f\u0005}\u0005\u0001\"\u0001\u0002\"\u0006Y1/\u001a;va\u001e\u0013x.\u001e9t)\u0015\u0001\u00181UAW\u0011!\t)+!(A\u0002\u0005\u001d\u0016!\u0003;be\u001e,G\u000fT3o!\ri\u0011\u0011V\u0005\u0004\u0003Ws!aA%oi\"A\u0011qVAO\u0001\u0004\tI%A\u0007qCJ$\u0018\u000e^5p]2{7m\u001d\u0005\b\u0003g\u0003A\u0011AA[\u0003\u001d\u0001\u0018nY6CS:$\u0012bKA\\\u0003w\u000b9-!3\t\u000f\u0005e\u0016\u0011\u0017a\u0001I\u0006\t\u0001\u000f\u0003\u0005\u0002\u0014\u0005E\u0006\u0019AA_a\u0011\ty,a1\u0011\u000bM\tI\"!1\u0011\t\u0005}\u00111\u0019\u0003\r\u0003\u000b\fY,!A\u0001\u0002\u000b\u0005\u0011Q\u0005\u0002\u0004?\u00122\u0004BB\f\u00022\u0002\u0007\u0011\u0004\u0003\u0005\u00020\u0006E\u0006\u0019AA%\u0011\u001d\ti\r\u0001C\u0001\u0003\u001f\f!\u0002\u001e5s_^\u0014\u0015\r\u001c7t)%\u0001\u0018\u0011[Ak\u0003C\f\u0019\u000f\u0003\u0005\u0002T\u0006-\u0007\u0019AAT\u00035i\u0017\r\u001f)beRLG/[8og\"A\u00111CAf\u0001\u0004\t9\u000e\r\u0003\u0002Z\u0006u\u0007#B\n\u0002\u001a\u0005m\u0007\u0003BA\u0010\u0003;$A\"a8\u0002V\u0006\u0005\t\u0011!B\u0001\u0003K\u00111a\u0018\u00138\u0011\u00199\u00121\u001aa\u00013!A\u0011qVAf\u0001\u0004\tI\u0005C\u0004\u0002h\u0002!\t!!;\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\t\tY\u000f\u0005\u0003\u000e\u0003[\\\u0013bAAx\u001d\t)\u0011I\u001d:bs\"9\u00111\u001f\u0001\u0005\u0002\u0005U\u0018\u0001C2pC2,7oY3\u0015\r\u0005-\u0018q_A}\u0011!\t\u0019.!=A\u0002\u0005\u001d\u0006\u0002CA\n\u0003c\u0004\r!a?1\t\u0005u(\u0011\u0001\t\u0006'\u0005e\u0011q \t\u0005\u0003?\u0011\t\u0001\u0002\u0007\u0003\u0004\u0005e\u0018\u0011!A\u0001\u0006\u0003\t)CA\u0002`Ia:\u0011Ba\u0002\u0003\u0003\u0003EIA!\u0003\u00023\u0011+g-Y;miB\u000b'\u000f^5uS>t7i\\1mKN\u001cWM\u001d\t\u0004'\t-a\u0001C\u0001\u0003\u0003\u0003EIA!\u0004\u0014\u0007\t-A\u0002C\u0004 \u0005\u0017!\tA!\u0005\u0015\u0005\t%\u0001B\u0003B\u000b\u0005\u0017\t\n\u0011\"\u0001\u0003\u0018\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIE*\"A!\u0007+\u0007e\u0011Yb\u000b\u0002\u0003\u001eA!!q\u0004B\u0015\u001b\t\u0011\tC\u0003\u0003\u0003$\t\u0015\u0012!C;oG\",7m[3e\u0015\r\u00119CD\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002\u0002B\u0016\u0005C\u0011\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0001"
)
public class DefaultPartitionCoalescer implements PartitionCoalescer {
   private final double balanceSlack;
   private final Random rnd;
   private final ArrayBuffer<PartitionGroup> groupArr;
   private final Map<String, ArrayBuffer<PartitionGroup>> groupHash;
   private final Set<Partition> initialHash;
   private boolean noLocality;

   public static double $lessinit$greater$default$1() {
      return DefaultPartitionCoalescer$.MODULE$.$lessinit$greater$default$1();
   }

   public double balanceSlack() {
      return this.balanceSlack;
   }

   public boolean compare(PartitionGroup o1, PartitionGroup o2) {
      return o1.numPartitions() < o2.numPartitions();
   }

   public boolean compare(Option<PartitionGroup> o1, Option<PartitionGroup> o2) {
      boolean var10000;
      label39: {
         None$ var3 = None$.MODULE$;
         if (o1 == null) {
            if (var3 == null) {
               break label39;
            }
         } else if (o1.equals(var3)) {
            break label39;
         }

         label40: {
            None$ var4 = None$.MODULE$;
            if (o2 == null) {
               if (var4 == null) {
                  break label40;
               }
            } else if (o2.equals(var4)) {
               break label40;
            }

            var10000 = this.compare((PartitionGroup)o1.get(), (PartitionGroup)o2.get());
            return var10000;
         }

         var10000 = true;
         return var10000;
      }

      var10000 = false;
      return var10000;
   }

   public Random rnd() {
      return this.rnd;
   }

   public ArrayBuffer<PartitionGroup> groupArr() {
      return this.groupArr;
   }

   public Map<String, ArrayBuffer<PartitionGroup>> groupHash() {
      return this.groupHash;
   }

   public Set<Partition> initialHash() {
      return this.initialHash;
   }

   public boolean noLocality() {
      return this.noLocality;
   }

   public void noLocality_$eq(boolean x$1) {
      this.noLocality = x$1;
   }

   public Seq<String> currPrefLocs(Partition part, RDD<?> prev) {
      return (Seq)prev.context().getPreferredLocs(prev, part.index()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(TaskLocation tl) {
            return tl.host();
         }
      }, Seq$.MODULE$.canBuildFrom());
   }

   public Option<PartitionGroup> getLeastGroupHash(String key) {
      return this.groupHash().get(key).<PartitionGroup>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final DefaultPartitionCoalescer $outer;

         public final PartitionGroup apply(ArrayBuffer<PartitionGroup> x$5) {
            return (PartitionGroup)((IndexedSeqOptimized)x$5.sortWith(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final boolean apply(PartitionGroup o1, PartitionGroup o2) {
                  return this.$outer.org$apache$spark$rdd$DefaultPartitionCoalescer$$anonfun$$$outer().compare(o1, o2);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            })).head();
         }

         // $FF: synthetic method
         public DefaultPartitionCoalescer org$apache$spark$rdd$DefaultPartitionCoalescer$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public boolean addPartToPGroup(Partition part, PartitionGroup pgroup) {
      boolean var10000;
      if (this.initialHash().contains(part)) {
         var10000 = false;
      } else {
         pgroup.partitions().$plus$eq(part);
         this.initialHash().$plus$eq(part);
         var10000 = true;
      }

      return var10000;
   }

   public void setupGroups(int targetLen, PartitionLocations partitionLocs) {
      if (partitionLocs.partsWithLocs().isEmpty()) {
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), targetLen).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DefaultPartitionCoalescer $outer;

            public final ArrayBuffer<PartitionGroup> apply(int x) {
               return this.$outer.groupArr().$plus$eq(new PartitionGroup(PartitionGroup$.MODULE$.$lessinit$greater$default$1()));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
      } else {
         this.noLocality_$eq(false);
         int expectedCoupons2 = 2 * (int)(scala.math.package$.MODULE$.log((double)targetLen) * (double)targetLen + (double)targetLen + (double)0.5F);
         int numCreated = 0;
         int tries = 0;
         int numPartsToLookAt = scala.math.package$.MODULE$.min(expectedCoupons2, partitionLocs.partsWithLocs().length());

         while(numCreated < targetLen && tries < numPartsToLookAt) {
            Tuple2 var8 = (Tuple2)partitionLocs.partsWithLocs().apply(tries);
            if (var8 == null) {
               throw new MatchError(var8);
            }

            String nxt_replica = (String)var8._1();
            Partition nxt_part = (Partition)var8._2();
            Tuple2 var11 = new Tuple2(nxt_replica, nxt_part);
            String nxt_replica = (String)var11._1();
            Partition nxt_part = (Partition)var11._2();
            ++tries;
            if (!this.groupHash().contains(nxt_replica)) {
               PartitionGroup pgroup = new PartitionGroup(new Some(nxt_replica));
               this.groupArr().$plus$eq(pgroup);
               this.addPartToPGroup(nxt_part, pgroup);
               this.groupHash().put(nxt_replica, ArrayBuffer$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new PartitionGroup[]{pgroup})));
               ++numCreated;
            }
         }

         tries = 0;

         while(numCreated < targetLen) {
            Tuple2 var16 = (Tuple2)partitionLocs.partsWithLocs().apply(tries);
            if (var16 == null) {
               throw new MatchError(var16);
            }

            String nxt_replica = (String)var16._1();
            Partition nxt_part = (Partition)var16._2();
            Tuple2 var19 = new Tuple2(nxt_replica, nxt_part);
            String nxt_replica = (String)var19._1();
            Partition nxt_part = (Partition)var19._2();
            ++tries;
            PartitionGroup pgroup = new PartitionGroup(new Some(nxt_replica));
            this.groupArr().$plus$eq(pgroup);
            ((ArrayBuffer)this.groupHash().getOrElseUpdate(nxt_replica, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final ArrayBuffer<PartitionGroup> apply() {
                  return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
               }
            })).$plus$eq(pgroup);
            this.addPartToPGroup(nxt_part, pgroup);
            ++numCreated;
            if (tries >= partitionLocs.partsWithLocs().length()) {
               tries = 0;
            }
         }

      }
   }

   public PartitionGroup pickBin(Partition p, RDD<?> prev, double balanceSlack, PartitionLocations partitionLocs) {
      int slack;
      Object var10000;
      label31: {
         label30: {
            slack = (int)(balanceSlack * (double)prev.partitions().length);
            Seq pref = (Seq)((SeqLike)this.currPrefLocs(p, prev).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DefaultPartitionCoalescer $outer;

               public final Option<PartitionGroup> apply(String x$8) {
                  return this.$outer.getLeastGroupHash(x$8);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Seq$.MODULE$.canBuildFrom())).sortWith(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DefaultPartitionCoalescer $outer;

               public final boolean apply(Option<PartitionGroup> o1, Option<PartitionGroup> o2) {
                  return this.$outer.compare(o1, o2);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            Nil$ var9 = Nil$.MODULE$;
            if (pref == null) {
               if (var9 == null) {
                  break label30;
               }
            } else if (pref.equals(var9)) {
               break label30;
            }

            var10000 = (Option)pref.head();
            break label31;
         }

         var10000 = None$.MODULE$;
      }

      Option prefPart = (Option)var10000;
      int r1 = this.rnd().nextInt(this.groupArr().size());
      int r2 = this.rnd().nextInt(this.groupArr().size());
      PartitionGroup minPowerOfTwo = ((PartitionGroup)this.groupArr().apply(r1)).numPartitions() < ((PartitionGroup)this.groupArr().apply(r2)).numPartitions() ? (PartitionGroup)this.groupArr().apply(r1) : (PartitionGroup)this.groupArr().apply(r2);
      if (prefPart.isEmpty()) {
         return minPowerOfTwo;
      } else {
         PartitionGroup prefPartActual = (PartitionGroup)prefPart.get();
         return minPowerOfTwo.numPartitions() + slack <= prefPartActual.numPartitions() ? minPowerOfTwo : prefPartActual;
      }
   }

   public void throwBalls(int maxPartitions, RDD<?> prev, double balanceSlack, PartitionLocations partitionLocs) {
      if (this.noLocality()) {
         if (maxPartitions > this.groupArr().size()) {
            Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(prev.partitions()).zipWithIndex(Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Tuple2.class)))).withFilter(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final boolean apply(Tuple2<Partition, Object> check$ifrefutable$1) {
                  boolean var3;
                  if (check$ifrefutable$1 != null) {
                     var3 = true;
                  } else {
                     var3 = false;
                  }

                  return var3;
               }
            }).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DefaultPartitionCoalescer $outer;

               public final ArrayBuffer<Partition> apply(Tuple2<Partition, Object> x$9) {
                  if (x$9 != null) {
                     Partition p = (Partition)x$9._1();
                     int i = x$9._2$mcI$sp();
                     ArrayBuffer var5 = ((PartitionGroup)this.$outer.groupArr().apply(i)).partitions().$plus$eq(p);
                     return var5;
                  } else {
                     throw new MatchError(x$9);
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         } else {
            RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), maxPartitions).foreach$mVc$sp(new Serializable(this, maxPartitions, prev) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final DefaultPartitionCoalescer $outer;
               private final int maxPartitions$1;
               public final RDD prev$2;

               public final void apply(int i) {
                  this.apply$mcVI$sp(i);
               }

               public void apply$mcVI$sp(int i) {
                  int rangeStart = (int)((long)i * (long)this.prev$2.partitions().length / (long)this.maxPartitions$1);
                  int rangeEnd = (int)(((long)i + 1L) * (long)this.prev$2.partitions().length / (long)this.maxPartitions$1);
                  RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(rangeStart), rangeEnd).foreach(new Serializable(this, i) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final int i$1;

                     public final ArrayBuffer<Partition> apply(int j) {
                        return ((PartitionGroup)this.$outer.org$apache$spark$rdd$DefaultPartitionCoalescer$$anonfun$$$outer().groupArr().apply(this.i$1)).partitions().$plus$eq(this.$outer.prev$2.partitions()[j]);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.i$1 = i$1;
                        }
                     }
                  });
               }

               // $FF: synthetic method
               public DefaultPartitionCoalescer org$apache$spark$rdd$DefaultPartitionCoalescer$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.maxPartitions$1 = maxPartitions$1;
                     this.prev$2 = prev$2;
                  }
               }
            });
         }
      } else {
         Iterator partIter = partitionLocs.partsWithLocs().iterator();
         ((ResizableArray)this.groupArr().filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(PartitionGroup pg) {
               return pg.numPartitions() == 0;
            }
         })).foreach(new Serializable(this, partIter) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DefaultPartitionCoalescer $outer;
            private final Iterator partIter$1;

            public final void apply(PartitionGroup pg) {
               while(true) {
                  if (this.partIter$1.hasNext() && pg.numPartitions() == 0) {
                     Tuple2 var3 = (Tuple2)this.partIter$1.next();
                     if (var3 != null) {
                        String nxt_replica = (String)var3._1();
                        Partition nxt_part = (Partition)var3._2();
                        Tuple2 var6 = new Tuple2(nxt_replica, nxt_part);
                        String nxt_replica = (String)var6._1();
                        Partition nxt_part = (Partition)var6._2();
                        if (this.$outer.initialHash().contains(nxt_part)) {
                           BoxedUnit var10000 = BoxedUnit.UNIT;
                        } else {
                           pg.partitions().$plus$eq(nxt_part);
                           this.$outer.initialHash().$plus$eq(nxt_part);
                        }
                        continue;
                     }

                     throw new MatchError(var3);
                  }

                  return;
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.partIter$1 = partIter$1;
               }
            }
         });
         Iterator partNoLocIter = partitionLocs.partsWithoutLocs().iterator();
         ((ResizableArray)this.groupArr().filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(PartitionGroup pg) {
               return pg.numPartitions() == 0;
            }
         })).foreach(new Serializable(this, partNoLocIter) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DefaultPartitionCoalescer $outer;
            private final Iterator partNoLocIter$1;

            public final void apply(PartitionGroup pg) {
               while(this.partNoLocIter$1.hasNext() && pg.numPartitions() == 0) {
                  Partition nxt_part = (Partition)this.partNoLocIter$1.next();
                  if (this.$outer.initialHash().contains(nxt_part)) {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  } else {
                     pg.partitions().$plus$eq(nxt_part);
                     this.$outer.initialHash().$plus$eq(nxt_part);
                  }
               }

            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.partNoLocIter$1 = partNoLocIter$1;
               }
            }
         });
         Predef$.MODULE$.refArrayOps(prev.partitions()).withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DefaultPartitionCoalescer $outer;

            public final boolean apply(Partition p) {
               return !this.$outer.initialHash().contains(p);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).foreach(new Serializable(this, prev, balanceSlack, partitionLocs) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final DefaultPartitionCoalescer $outer;
            private final RDD prev$2;
            private final double balanceSlack$1;
            private final PartitionLocations partitionLocs$1;

            public final ArrayBuffer<Partition> apply(Partition p) {
               return this.$outer.pickBin(p, this.prev$2, this.balanceSlack$1, this.partitionLocs$1).partitions().$plus$eq(p);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.prev$2 = prev$2;
                  this.balanceSlack$1 = balanceSlack$1;
                  this.partitionLocs$1 = partitionLocs$1;
               }
            }
         });
      }

   }

   public PartitionGroup[] getPartitions() {
      return (PartitionGroup[])((TraversableOnce)this.groupArr().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(PartitionGroup pg) {
            return pg.numPartitions() > 0;
         }
      })).toArray(ClassTag$.MODULE$.apply(PartitionGroup.class));
   }

   public PartitionGroup[] coalesce(int maxPartitions, RDD<?> prev) {
      PartitionLocations partitionLocs = new PartitionLocations(prev);
      this.setupGroups(scala.math.package$.MODULE$.min(prev.partitions().length, maxPartitions), partitionLocs);
      this.throwBalls(maxPartitions, prev, this.balanceSlack(), partitionLocs);
      return this.getPartitions();
   }

   public DefaultPartitionCoalescer(double balanceSlack) {
      this.balanceSlack = balanceSlack;
      this.rnd = new Random(7919);
      this.groupArr = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.groupHash = (Map)Map$.MODULE$.apply(Nil$.MODULE$);
      this.initialHash = (Set)Set$.MODULE$.apply(Nil$.MODULE$);
      this.noLocality = true;
   }

   public class PartitionLocations {
      private final ArrayBuffer<Partition> partsWithoutLocs;
      private final ArrayBuffer<Tuple2<String, Partition>> partsWithLocs;
      // $FF: synthetic field
      public final DefaultPartitionCoalescer $outer;

      public ArrayBuffer<Partition> partsWithoutLocs() {
         return this.partsWithoutLocs;
      }

      public ArrayBuffer<Tuple2<String, Partition>> partsWithLocs() {
         return this.partsWithLocs;
      }

      public void getAllPrefLocs(RDD<?> prev) {
         LinkedHashMap tmpPartsWithLocs = (LinkedHashMap)LinkedHashMap$.MODULE$.apply(Nil$.MODULE$);
         Predef$.MODULE$.refArrayOps(prev.partitions()).foreach(new Serializable(this, prev, tmpPartsWithLocs) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PartitionLocations $outer;
            private final RDD prev$1;
            private final LinkedHashMap tmpPartsWithLocs$1;

            public final Object apply(Partition p) {
               Seq locs = this.$outer.org$apache$spark$rdd$DefaultPartitionCoalescer$PartitionLocations$$$outer().currPrefLocs(p, this.prev$1);
               return locs.nonEmpty() ? this.tmpPartsWithLocs$1.put(p, locs) : this.$outer.partsWithoutLocs().$plus$eq(p);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.prev$1 = prev$1;
                  this.tmpPartsWithLocs$1 = tmpPartsWithLocs$1;
               }
            }
         });
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(0), 2).foreach$mVc$sp(new Serializable(this, tmpPartsWithLocs) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final PartitionLocations $outer;
            private final LinkedHashMap tmpPartsWithLocs$1;

            public final void apply(int x) {
               this.apply$mcVI$sp(x);
            }

            public void apply$mcVI$sp(int x) {
               this.tmpPartsWithLocs$1.foreach(new Serializable(this, x) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int x$11;

                  public final Object apply(Tuple2<Partition, Seq<String>> parts) {
                     Partition p = (Partition)parts._1();
                     Seq locs = (Seq)parts._2();
                     return locs.size() > this.x$11 ? this.$outer.org$apache$spark$rdd$DefaultPartitionCoalescer$PartitionLocations$$anonfun$$$outer().partsWithLocs().$plus$eq(new Tuple2(locs.apply(this.x$11), p)) : BoxedUnit.UNIT;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.x$11 = x$11;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public PartitionLocations org$apache$spark$rdd$DefaultPartitionCoalescer$PartitionLocations$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.tmpPartsWithLocs$1 = tmpPartsWithLocs$1;
               }
            }
         });
      }

      // $FF: synthetic method
      public DefaultPartitionCoalescer org$apache$spark$rdd$DefaultPartitionCoalescer$PartitionLocations$$$outer() {
         return this.$outer;
      }

      public PartitionLocations(DefaultPartitionCoalescer prev) {
         if (DefaultPartitionCoalescer.this == null) {
            throw null;
         } else {
            this.$outer = DefaultPartitionCoalescer.this;
            super();
            this.partsWithoutLocs = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
            this.partsWithLocs = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
            this.getAllPrefLocs(prev);
         }
      }
   }
}
