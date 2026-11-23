package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.shuffle.ShuffleHandle;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Product2;
import scala.Serializable;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\tMa\u0001B\u0001\u0003\u0001%\u0011\u0011c\u00155vM\u001adW\rR3qK:$WM\\2z\u0015\t\u0019A!A\u0003ta\u0006\u00148N\u0003\u0002\u0006\r\u00051\u0011\r]1dQ\u0016T\u0011aB\u0001\u0004_J<7\u0001A\u000b\u0005\u0015]\t\u0013n\u0005\u0002\u0001\u0017A\u0019A\"D\b\u000e\u0003\tI!A\u0004\u0002\u0003\u0015\u0011+\u0007/\u001a8eK:\u001c\u0017\u0010\u0005\u0003\u0011'U\u0001S\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\u0011A\u0013x\u000eZ;diJ\u0002\"AF\f\r\u0001\u0011)\u0001\u0004\u0001b\u00013\t\t1*\u0005\u0002\u001b;A\u0011\u0001cG\u0005\u00039E\u0011qAT8uQ&tw\r\u0005\u0002\u0011=%\u0011q$\u0005\u0002\u0004\u0003:L\bC\u0001\f\"\t\u0015\u0011\u0003A1\u0001\u001a\u0005\u00051\u0006\u0002\u0003\u0013\u0001\u0005\u000b\u0007I\u0011B\u0013\u0002\t}\u0013H\rZ\u000b\u0002MA\u0012qE\f\t\u0004Q-jS\"A\u0015\u000b\u0005)\u0012\u0011a\u0001:eI&\u0011A&\u000b\u0002\u0004%\u0012#\u0005C\u0001\f/\t%y\u0003'!A\u0001\u0002\u000b\u0005aGA\u0002`IEB\u0001\"\r\u0001\u0003\u0002\u0003\u0006IAM\u0001\u0006?J$G\r\t\u0019\u0003gU\u00022\u0001K\u00165!\t1R\u0007B\u00050a\u0005\u0005\t\u0011!B\u0001mE\u0011!d\u0004\u0015\u0003aa\u0002\"\u0001E\u001d\n\u0005i\n\"!\u0003;sC:\u001c\u0018.\u001a8u\u0011!a\u0004A!b\u0001\n\u0003i\u0014a\u00039beRLG/[8oKJ,\u0012A\u0010\t\u0003\u0019}J!\u0001\u0011\u0002\u0003\u0017A\u000b'\u000f^5uS>tWM\u001d\u0005\t\u0005\u0002\u0011\t\u0011)A\u0005}\u0005a\u0001/\u0019:uSRLwN\\3sA!AA\t\u0001BC\u0002\u0013\u0005Q)\u0001\u0006tKJL\u0017\r\\5{KJ,\u0012A\u0012\t\u0003\u000f&k\u0011\u0001\u0013\u0006\u0003\t\nI!A\u0013%\u0003\u0015M+'/[1mSj,'\u000f\u0003\u0005M\u0001\t\u0005\t\u0015!\u0003G\u0003-\u0019XM]5bY&TXM\u001d\u0011\t\u00119\u0003!Q1A\u0005\u0002=\u000b1b[3z\u001fJ$WM]5oOV\t\u0001\u000bE\u0002\u0011#NK!AU\t\u0003\r=\u0003H/[8o!\r!F,\u0006\b\u0003+js!AV-\u000e\u0003]S!\u0001\u0017\u0005\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0011\u0012BA.\u0012\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u00180\u0003\u0011=\u0013H-\u001a:j]\u001eT!aW\t\t\u0011\u0001\u0004!\u0011!Q\u0001\nA\u000bAb[3z\u001fJ$WM]5oO\u0002B\u0001B\u0019\u0001\u0003\u0006\u0004%\taY\u0001\u000bC\u001e<'/Z4bi>\u0014X#\u00013\u0011\u0007A\tV\rE\u0003\rMV\u0001\u0003.\u0003\u0002h\u0005\tQ\u0011iZ4sK\u001e\fGo\u001c:\u0011\u0005YIG!\u00026\u0001\u0005\u0004I\"!A\"\t\u00111\u0004!\u0011!Q\u0001\n\u0011\f1\"Y4he\u0016<\u0017\r^8sA!Aa\u000e\u0001BC\u0002\u0013\u0005q.\u0001\bnCB\u001c\u0016\u000eZ3D_6\u0014\u0017N\\3\u0016\u0003A\u0004\"\u0001E9\n\u0005I\f\"a\u0002\"p_2,\u0017M\u001c\u0005\ti\u0002\u0011\t\u0011)A\u0005a\u0006yQ.\u00199TS\u0012,7i\\7cS:,\u0007\u0005\u0003\u0005w\u0001\t\r\t\u0015a\u0003x\u0003))g/\u001b3f]\u000e,G%\r\t\u0004qn,R\"A=\u000b\u0005i\f\u0012a\u0002:fM2,7\r^\u0005\u0003yf\u0014\u0001b\u00117bgN$\u0016m\u001a\u0005\t}\u0002\u0011\u0019\u0011)A\u0006\u007f\u0006QQM^5eK:\u001cW\r\n\u001a\u0011\u0007a\\\b\u0005\u0003\u0006\u0002\u0004\u0001\u0011\u0019\u0011)A\u0006\u0003\u000b\t!\"\u001a<jI\u0016t7-\u001a\u00134!\rA8\u0010\u001b\u0005\b\u0003\u0013\u0001A\u0011AA\u0006\u0003\u0019a\u0014N\\5u}Qq\u0011QBA\f\u0003C\t\u0019#!\n\u0002(\u0005%B\u0003CA\b\u0003#\t\u0019\"!\u0006\u0011\u000b1\u0001Q\u0003\t5\t\rY\f9\u0001q\u0001x\u0011\u0019q\u0018q\u0001a\u0002\u007f\"A\u00111AA\u0004\u0001\b\t)\u0001C\u0004%\u0003\u000f\u0001\r!!\u00071\t\u0005m\u0011q\u0004\t\u0005Q-\ni\u0002E\u0002\u0017\u0003?!!bLA\f\u0003\u0003\u0005\tQ!\u00017\u0011\u0019a\u0014q\u0001a\u0001}!AA)a\u0002\u0011\u0002\u0003\u0007a\t\u0003\u0005O\u0003\u000f\u0001\n\u00111\u0001Q\u0011!\u0011\u0017q\u0001I\u0001\u0002\u0004!\u0007\u0002\u00038\u0002\bA\u0005\t\u0019\u00019\t\r)\u0002A\u0011IA\u0017+\t\ty\u0003E\u0002)W=A!\"a\r\u0001\u0005\u0004%\tAAA\u001b\u00031YW-_\"mCN\u001ch*Y7f+\t\t9\u0004\u0005\u0003\u0002:\u0005}bb\u0001\t\u0002<%\u0019\u0011QH\t\u0002\rA\u0013X\rZ3g\u0013\u0011\t\t%a\u0011\u0003\rM#(/\u001b8h\u0015\r\ti$\u0005\u0005\t\u0003\u000f\u0002\u0001\u0015!\u0003\u00028\u0005i1.Z=DY\u0006\u001c8OT1nK\u0002B!\"a\u0013\u0001\u0005\u0004%\tAAA\u001b\u000391\u0018\r\\;f\u00072\f7o\u001d(b[\u0016D\u0001\"a\u0014\u0001A\u0003%\u0011qG\u0001\u0010m\u0006dW/Z\"mCN\u001ch*Y7fA!Q\u00111\u000b\u0001C\u0002\u0013\u0005!!!\u0016\u0002#\r|WNY5oKJ\u001cE.Y:t\u001d\u0006lW-\u0006\u0002\u0002XA!\u0001#UA\u001c\u0011!\tY\u0006\u0001Q\u0001\n\u0005]\u0013AE2p[\nLg.\u001a:DY\u0006\u001c8OT1nK\u0002B\u0011\"a\u0018\u0001\u0005\u0004%\t!!\u0019\u0002\u0013MDWO\u001a4mK&#WCAA2!\r\u0001\u0012QM\u0005\u0004\u0003O\n\"aA%oi\"A\u00111\u000e\u0001!\u0002\u0013\t\u0019'\u0001\u0006tQV4g\r\\3JI\u0002B\u0011\"a\u001c\u0001\u0005\u0004%\t!!\u001d\u0002\u001bMDWO\u001a4mK\"\u000bg\u000e\u001a7f+\t\t\u0019\b\u0005\u0003\u0002v\u0005mTBAA<\u0015\r\tIHA\u0001\bg\",hM\u001a7f\u0013\u0011\ti(a\u001e\u0003\u001bMCWO\u001a4mK\"\u000bg\u000e\u001a7f\u0011!\t\t\t\u0001Q\u0001\n\u0005M\u0014AD:ik\u001a4G.\u001a%b]\u0012dW\r\t\u0015\u0004\u0001\u0005\u0015\u0005\u0003BAD\u0003\u001bk!!!#\u000b\u0007\u0005-%!\u0001\u0006b]:|G/\u0019;j_:LA!a$\u0002\n\naA)\u001a<fY>\u0004XM]!qS\u001eI\u00111\u0013\u0002\u0002\u0002#\u0005\u0011QS\u0001\u0012'\",hM\u001a7f\t\u0016\u0004XM\u001c3f]\u000eL\bc\u0001\u0007\u0002\u0018\u001aA\u0011AAA\u0001\u0012\u0003\tIj\u0005\u0004\u0002\u0018\u0006m\u0015\u0011\u0015\t\u0004!\u0005u\u0015bAAP#\t1\u0011I\\=SK\u001a\u00042\u0001EAR\u0013\r\t)+\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t\u0003\u0013\t9\n\"\u0001\u0002*R\u0011\u0011Q\u0013\u0005\u000b\u0003[\u000b9*%A\u0005\u0002\u0005=\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$3'\u0006\u0005\u00022\u0006\u0015\u0017qYAe+\t\t\u0019LK\u0002G\u0003k[#!a.\u0011\t\u0005e\u0016\u0011Y\u0007\u0003\u0003wSA!!0\u0002@\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003\u0017\u000b\u0012\u0002BAb\u0003w\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u0019A\u00121\u0016b\u00013\u00111!%a+C\u0002e!aA[AV\u0005\u0004I\u0002BCAg\u0003/\u000b\n\u0011\"\u0001\u0002P\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIQ*\u0002\"!5\u0002\\\u0006u\u0017q\\\u000b\u0003\u0003'TC!!6\u00026:\u0019\u0001#a6\n\u0007\u0005e\u0017#\u0001\u0003O_:,GA\u0002\r\u0002L\n\u0007\u0011\u0004\u0002\u0004#\u0003\u0017\u0014\r!\u0007\u0003\u0007U\u0006-'\u0019A\r\t\u0015\u0005\r\u0018qSI\u0001\n\u0003\t)/A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%N\u000b\t\u0003#\f9/!;\u0002l\u00121\u0001$!9C\u0002e!aAIAq\u0005\u0004IBA\u00026\u0002b\n\u0007\u0011\u0004\u0003\u0006\u0002p\u0006]\u0015\u0013!C\u0001\u0003c\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u00122T\u0003CAz\u0003o\fI0a?\u0016\u0005\u0005U(f\u00019\u00026\u00121\u0001$!<C\u0002e!aAIAw\u0005\u0004IBA\u00026\u0002n\n\u0007\u0011\u0004\u0003\u0006\u0002\u0000\u0006]\u0015\u0011!C\u0005\u0005\u0003\t1B]3bIJ+7o\u001c7wKR\u0011!1\u0001\t\u0005\u0005\u000b\u0011y!\u0004\u0002\u0003\b)!!\u0011\u0002B\u0006\u0003\u0011a\u0017M\\4\u000b\u0005\t5\u0011\u0001\u00026bm\u0006LAA!\u0005\u0003\b\t1qJ\u00196fGR\u0004"
)
public class ShuffleDependency<K, V, C> extends Dependency<Product2<K, V>> {
   private final transient RDD<? extends Product2<K, V>> _rdd;
   private final Partitioner partitioner;
   private final Serializer serializer;
   private final Option<Ordering<K>> keyOrdering;
   private final Option<Aggregator<K, V, C>> aggregator;
   private final boolean mapSideCombine;
   private final String keyClassName;
   private final String valueClassName;
   private final Option<String> combinerClassName;
   private final int shuffleId;
   private final ShuffleHandle shuffleHandle;

   public static <K, V, C> boolean $lessinit$greater$default$6() {
      return ShuffleDependency$.MODULE$.$lessinit$greater$default$6();
   }

   public static <K, V, C> None$ $lessinit$greater$default$5() {
      return ShuffleDependency$.MODULE$.$lessinit$greater$default$5();
   }

   public static <K, V, C> None$ $lessinit$greater$default$4() {
      return ShuffleDependency$.MODULE$.$lessinit$greater$default$4();
   }

   public static <K, V, C> Serializer $lessinit$greater$default$3() {
      return ShuffleDependency$.MODULE$.$lessinit$greater$default$3();
   }

   private RDD<? extends Product2<K, V>> _rdd() {
      return this._rdd;
   }

   public Partitioner partitioner() {
      return this.partitioner;
   }

   public Serializer serializer() {
      return this.serializer;
   }

   public Option<Ordering<K>> keyOrdering() {
      return this.keyOrdering;
   }

   public Option<Aggregator<K, V, C>> aggregator() {
      return this.aggregator;
   }

   public boolean mapSideCombine() {
      return this.mapSideCombine;
   }

   public RDD<Product2<K, V>> rdd() {
      return this._rdd();
   }

   public String keyClassName() {
      return this.keyClassName;
   }

   public String valueClassName() {
      return this.valueClassName;
   }

   public Option<String> combinerClassName() {
      return this.combinerClassName;
   }

   public int shuffleId() {
      return this.shuffleId;
   }

   public ShuffleHandle shuffleHandle() {
      return this.shuffleHandle;
   }

   public ShuffleDependency(RDD<? extends Product2<K, V>> _rdd, Partitioner partitioner, Serializer serializer, Option<Ordering<K>> keyOrdering, Option<Aggregator<K, V, C>> aggregator, boolean mapSideCombine, ClassTag<K> evidence$1, ClassTag<V> evidence$2, ClassTag<C> evidence$3) {
      this._rdd = _rdd;
      this.partitioner = partitioner;
      this.serializer = serializer;
      this.keyOrdering = keyOrdering;
      this.aggregator = aggregator;
      this.mapSideCombine = mapSideCombine;
      this.keyClassName = scala.reflect.package$.MODULE$.classTag(evidence$1).runtimeClass().getName();
      this.valueClassName = scala.reflect.package$.MODULE$.classTag(evidence$2).runtimeClass().getName();
      this.combinerClassName = Option$.MODULE$.<String>apply(scala.reflect.package$.MODULE$.classTag(evidence$3)).<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(ClassTag<C> x$1) {
            return x$1.runtimeClass().getName();
         }
      });
      this.shuffleId = _rdd.context().newShuffleId();
      this.shuffleHandle = _rdd.context().env().shuffleManager().registerShuffle(this.shuffleId(), _rdd.partitions().length, this);
      _rdd.sparkContext().cleaner().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ShuffleDependency $outer;

         public final void apply(ContextCleaner x$2) {
            x$2.registerShuffleForCleanup(this.$outer);
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
}
