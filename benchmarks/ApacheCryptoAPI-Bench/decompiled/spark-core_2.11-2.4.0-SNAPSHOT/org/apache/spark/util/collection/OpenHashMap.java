package org.apache.spark.util.collection;

import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.BufferedIterator;
import scala.collection.GenIterable;
import scala.collection.GenIterable$class;
import scala.collection.GenTraversable;
import scala.collection.GenTraversable$class;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterable$class;
import scala.collection.IterableLike$class;
import scala.collection.IterableView;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.Parallel;
import scala.collection.Parallelizable$class;
import scala.collection.Seq;
import scala.collection.Traversable;
import scala.collection.Traversable$class;
import scala.collection.TraversableLike$class;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.generic.FilterMonadic;
import scala.collection.generic.GenericCompanion;
import scala.collection.generic.GenericTraversableTemplate$class;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Builder;
import scala.collection.mutable.StringBuilder;
import scala.collection.parallel.Combiner;
import scala.collection.parallel.ParIterable;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rg!B\u0001\u0003\u0001\u0019a!aC(qK:D\u0015m\u001d5NCBT!a\u0001\u0003\u0002\u0015\r|G\u000e\\3di&|gN\u0003\u0002\u0006\r\u0005!Q\u000f^5m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<WcA\u0007'aM!\u0001A\u0004\u000bF!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fMB\u0019QCH\u0011\u000f\u0005YabBA\f\u001c\u001b\u0005A\"BA\r\u001b\u0003\u0019a$o\\8u}\r\u0001\u0011\"A\t\n\u0005u\u0001\u0012a\u00029bG.\fw-Z\u0005\u0003?\u0001\u0012\u0001\"\u0013;fe\u0006\u0014G.\u001a\u0006\u0003;A\u0001Ba\u0004\u0012%_%\u00111\u0005\u0005\u0002\u0007)V\u0004H.\u001a\u001a\u0011\u0005\u00152C\u0002\u0001\u0003\u0006O\u0001\u0011\r\u0001\u000b\u0002\u0002\u0017F\u0011\u0011\u0006\f\t\u0003\u001f)J!a\u000b\t\u0003\u000f9{G\u000f[5oOB\u0011q\"L\u0005\u0003]A\u00111!\u00118z!\t)\u0003\u0007B\u00052\u0001\u0001\u0006\t\u0011!b\u0001Q\t\ta\u000bK\u00031gYZ\u0004\t\u0005\u0002\u0010i%\u0011Q\u0007\u0005\u0002\fgB,7-[1mSj,G-M\u0003$oaR\u0014H\u0004\u0002\u0010q%\u0011\u0011\bE\u0001\u0005\u0019>tw-\r\u0003%-m\t\u0012'B\u0012={}rdBA\b>\u0013\tq\u0004#A\u0002J]R\fD\u0001\n\f\u001c#E*1%\u0011\"E\u0007:\u0011qBQ\u0005\u0003\u0007B\ta\u0001R8vE2,\u0017\u0007\u0002\u0013\u00177E\u0001\"a\u0004$\n\u0005\u001d\u0003\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C%\u0001\u0005\u0003\u0005\u000b\u0011\u0002&\u0002\u001f%t\u0017\u000e^5bY\u000e\u000b\u0007/Y2jif\u0004\"aD&\n\u00051\u0003\"aA%oi\"Aa\n\u0001B\u0002B\u0003-q*\u0001\u0006fm&$WM\\2fIE\u00022\u0001U*%\u001b\u0005\t&B\u0001*\u0011\u0003\u001d\u0011XM\u001a7fGRL!\u0001V)\u0003\u0011\rc\u0017m]:UC\u001eD\u0001B\u0016\u0001\u0003\u0004\u0003\u0006YaV\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004c\u0001)T_!)\u0011\f\u0001C\u00015\u00061A(\u001b8jiz\"\"a\u00171\u0015\u0007qsv\f\u0005\u0003^\u0001\u0011zS\"\u0001\u0002\t\u000b9C\u00069A(\t\u000bYC\u00069A,\t\u000b%C\u0006\u0019\u0001&\t\u000be\u0003A\u0011\u00012\u0015\u0003\r$2\u0001\u00183g\u0011\u001d)\u0017-!AA\u0004=\u000b!\"\u001a<jI\u0016t7-\u001a\u00134\u0011\u001d9\u0017-!AA\u0004]\u000b!\"\u001a<jI\u0016t7-\u001a\u00135\u0011\u001dI\u0007\u00011A\u0005\u0012)\fqaX6fsN+G/F\u0001l!\riF\u000eJ\u0005\u0003[\n\u00111b\u00149f]\"\u000b7\u000f[*fi\"9q\u000e\u0001a\u0001\n#\u0001\u0018aC0lKf\u001cV\r^0%KF$\"!\u001d;\u0011\u0005=\u0011\u0018BA:\u0011\u0005\u0011)f.\u001b;\t\u000fUt\u0017\u0011!a\u0001W\u0006\u0019\u0001\u0010J\u0019\t\r]\u0004\u0001\u0015)\u0003l\u0003!y6.Z=TKR\u0004\u0003\"C=\u0001\u0001\u0004\u0005\r\u0011\"\u0003{\u0003\u001dyf/\u00197vKN,\u0012a\u001f\t\u0004\u001fq|\u0013BA?\u0011\u0005\u0015\t%O]1z\u0011)y\b\u00011AA\u0002\u0013%\u0011\u0011A\u0001\f?Z\fG.^3t?\u0012*\u0017\u000fF\u0002r\u0003\u0007Aq!\u001e@\u0002\u0002\u0003\u00071\u0010C\u0004\u0002\b\u0001\u0001\u000b\u0015B>\u0002\u0011}3\u0018\r\\;fg\u0002B\u0001\"a\u0003\u0001\u0001\u0004%IA_\u0001\u000b?>dGMV1mk\u0016\u001c\b\"CA\b\u0001\u0001\u0007I\u0011BA\t\u00039yv\u000e\u001c3WC2,Xm]0%KF$2!]A\n\u0011!)\u0018QBA\u0001\u0002\u0004Y\bbBA\f\u0001\u0001\u0006Ka_\u0001\f?>dGMV1mk\u0016\u001c\b\u0005\u000b\u0003\u0002\u0016\u0005m\u0001cA\b\u0002\u001e%\u0019\u0011q\u0004\t\u0003\u0013Q\u0014\u0018M\\:jK:$\b\"CA\u0012\u0001\u0001\u0007I\u0011BA\u0013\u00035A\u0017M^3Ok2dg+\u00197vKV\u0011\u0011q\u0005\t\u0004\u001f\u0005%\u0012bAA\u0016!\t9!i\\8mK\u0006t\u0007\"CA\u0018\u0001\u0001\u0007I\u0011BA\u0019\u0003EA\u0017M^3Ok2dg+\u00197vK~#S-\u001d\u000b\u0004c\u0006M\u0002\"C;\u0002.\u0005\u0005\t\u0019AA\u0014\u0011!\t9\u0004\u0001Q!\n\u0005\u001d\u0012A\u00045bm\u0016tU\u000f\u001c7WC2,X\r\t\u0005\n\u0003w\u0001\u0001\u0019!C\u0005\u0003{\t\u0011B\\;mYZ\u000bG.^3\u0016\u0003=B\u0011\"!\u0011\u0001\u0001\u0004%I!a\u0011\u0002\u001b9,H\u000e\u001c,bYV,w\fJ3r)\r\t\u0018Q\t\u0005\tk\u0006}\u0012\u0011!a\u0001_!9\u0011\u0011\n\u0001!B\u0013y\u0013A\u00038vY24\u0016\r\\;fA!9\u0011Q\n\u0001\u0005B\u0005=\u0013\u0001B:ju\u0016,\u0012A\u0013\u0005\b\u0003'\u0002A\u0011AA+\u0003!\u0019wN\u001c;bS:\u001cH\u0003BA\u0014\u0003/Bq!!\u0017\u0002R\u0001\u0007A%A\u0001l\u0011\u001d\ti\u0006\u0001C\u0001\u0003?\nQ!\u00199qYf$2aLA1\u0011\u001d\tI&a\u0017A\u0002\u0011Bq!!\u001a\u0001\t\u0003\t9'\u0001\u0004va\u0012\fG/\u001a\u000b\u0006c\u0006%\u00141\u000e\u0005\b\u00033\n\u0019\u00071\u0001%\u0011\u001d\ti'a\u0019A\u0002=\n\u0011A\u001e\u0005\b\u0003c\u0002A\u0011AA:\u0003-\u0019\u0007.\u00198hKZ\u000bG.^3\u0015\u000f=\n)(a\u001e\u0002\u0002\"9\u0011\u0011LA8\u0001\u0004!\u0003\"CA=\u0003_\"\t\u0019AA>\u00031!WMZ1vYR4\u0016\r\\;f!\u0011y\u0011QP\u0018\n\u0007\u0005}\u0004C\u0001\u0005=Eft\u0017-\\3?\u0011!\t\u0019)a\u001cA\u0002\u0005\u0015\u0015AC7fe\u001e,g+\u00197vKB)q\"a\"0_%\u0019\u0011\u0011\u0012\t\u0003\u0013\u0019+hn\u0019;j_:\f\u0004bBAG\u0001\u0011\u0005\u0013qR\u0001\tSR,'/\u0019;peV\u0011\u0011\u0011\u0013\t\u0005+\u0005M\u0015%C\u0002\u0002\u0016\u0002\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u00033\u0003\u0001\u0019!C\t\u00037\u000bAa\u001a:poV\u0011\u0011Q\u0014\t\u0006\u001f\u0005\u001d%*\u001d\u0005\n\u0003C\u0003\u0001\u0019!C\t\u0003G\u000b\u0001b\u001a:po~#S-\u001d\u000b\u0004c\u0006\u0015\u0006\"C;\u0002 \u0006\u0005\t\u0019AAO\u0011!\tI\u000b\u0001Q!\n\u0005u\u0015!B4s_^\u0004\u0003\"CAW\u0001\u0001\u0007I\u0011CAX\u0003\u0011iwN^3\u0016\u0005\u0005E\u0006CB\b\u00024*S\u0015/C\u0002\u00026B\u0011\u0011BR;oGRLwN\u001c\u001a\t\u0013\u0005e\u0006\u00011A\u0005\u0012\u0005m\u0016\u0001C7pm\u0016|F%Z9\u0015\u0007E\fi\fC\u0005v\u0003o\u000b\t\u00111\u0001\u00022\"A\u0011\u0011\u0019\u0001!B\u0013\t\t,A\u0003n_Z,\u0007\u0005"
)
public class OpenHashMap<K, V> implements Iterable<Tuple2<K, V>>, Serializable {
   public final ClassTag<V> org$apache$spark$util$collection$OpenHashMap$$evidence$2;
   public OpenHashSet<K> org$apache$spark$util$collection$OpenHashMap$$_keySet;
   public Object _values;
   public transient Object _oldValues;
   public boolean org$apache$spark$util$collection$OpenHashMap$$haveNullValue;
   public V nullValue;
   public Function1<Object, BoxedUnit> org$apache$spark$util$collection$OpenHashMap$$grow;
   public Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$OpenHashMap$$move;

   public GenericCompanion<Iterable> companion() {
      return Iterable$class.companion(this);
   }

   public Iterable<Tuple2<K, V>> seq() {
      return Iterable$class.seq(this);
   }

   public Iterable<Tuple2<K, V>> thisCollection() {
      return IterableLike$class.thisCollection(this);
   }

   public Iterable toCollection(Object repr) {
      return IterableLike$class.toCollection(this, repr);
   }

   public <U> void foreach(Function1<Tuple2<K, V>, U> f) {
      IterableLike$class.foreach(this, f);
   }

   public boolean forall(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<Tuple2<K, V>> find(Function1<Tuple2<K, V>, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public boolean isEmpty() {
      return IterableLike$class.isEmpty(this);
   }

   public <B> B foldRight(B z, Function2<Tuple2<K, V>, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<Tuple2<K, V>, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<Tuple2<K, V>> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<Tuple2<K, V>> toIterator() {
      return IterableLike$class.toIterator(this);
   }

   public Object head() {
      return IterableLike$class.head(this);
   }

   public Object slice(int from, int until) {
      return IterableLike$class.slice(this, from, until);
   }

   public Object take(int n) {
      return IterableLike$class.take(this, n);
   }

   public Object drop(int n) {
      return IterableLike$class.drop(this, n);
   }

   public Object takeWhile(Function1 p) {
      return IterableLike$class.takeWhile(this, p);
   }

   public Iterator<Iterable<Tuple2<K, V>>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Iterable<Tuple2<K, V>>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Iterable<Tuple2<K, V>>> sliding(int size, int step) {
      return IterableLike$class.sliding(this, size, step);
   }

   public Object takeRight(int n) {
      return IterableLike$class.takeRight(this, n);
   }

   public Object dropRight(int n) {
      return IterableLike$class.dropRight(this, n);
   }

   public <B> void copyToArray(Object xs, int start, int len) {
      IterableLike$class.copyToArray(this, xs, start, len);
   }

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Iterable<Tuple2<K, V>>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<Tuple2<K, V>> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Object view() {
      return IterableLike$class.view(this);
   }

   public IterableView<Tuple2<K, V>, Iterable<Tuple2<K, V>>> view(int from, int until) {
      return IterableLike$class.view(this, from, until);
   }

   public Builder<Tuple2<K, V>, Iterable<Tuple2<K, V>>> newBuilder() {
      return GenericTraversableTemplate$class.newBuilder(this);
   }

   public <B> Builder<B, Iterable<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<Iterable<A1>, Iterable<A2>> unzip(Function1<Tuple2<K, V>, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<Iterable<A1>, Iterable<A2>, Iterable<A3>> unzip3(Function1<Tuple2<K, V>, Tuple3<A1, A2, A3>> asTriple) {
      return GenericTraversableTemplate$class.unzip3(this, asTriple);
   }

   public GenTraversable flatten(Function1 asTraversable) {
      return GenericTraversableTemplate$class.flatten(this, asTraversable);
   }

   public GenTraversable transpose(Function1 asTraversable) {
      return GenericTraversableTemplate$class.transpose(this, asTraversable);
   }

   public Object repr() {
      return TraversableLike$class.repr(this);
   }

   public final boolean isTraversableAgain() {
      return TraversableLike$class.isTraversableAgain(this);
   }

   public Combiner<Tuple2<K, V>, ParIterable<Tuple2<K, V>>> parCombiner() {
      return TraversableLike$class.parCombiner(this);
   }

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<Tuple2<K, V>, B> f, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<Tuple2<K, V>, GenTraversableOnce<B>> f, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public Object filter(Function1 p) {
      return TraversableLike$class.filter(this, p);
   }

   public Object filterNot(Function1 p) {
      return TraversableLike$class.filterNot(this, p);
   }

   public <B, That> That collect(PartialFunction<Tuple2<K, V>, B> pf, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> partition(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> Map<K, Iterable<Tuple2<K, V>>> groupBy(Function1<Tuple2<K, V>, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, Tuple2<K, V>, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<Tuple2<K, V>, B, B> op, CanBuildFrom<Iterable<Tuple2<K, V>>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<Tuple2<K, V>> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public Object last() {
      return TraversableLike$class.last(this);
   }

   public Option<Tuple2<K, V>> lastOption() {
      return TraversableLike$class.lastOption(this);
   }

   public Object init() {
      return TraversableLike$class.init(this);
   }

   public Object sliceWithKnownDelta(int from, int until, int delta) {
      return TraversableLike$class.sliceWithKnownDelta(this, from, until, delta);
   }

   public Object sliceWithKnownBound(int from, int until) {
      return TraversableLike$class.sliceWithKnownBound(this, from, until);
   }

   public Object dropWhile(Function1 p) {
      return TraversableLike$class.dropWhile(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> span(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Iterable<Tuple2<K, V>>, Iterable<Tuple2<K, V>>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Iterable<Tuple2<K, V>>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Iterable<Tuple2<K, V>>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<Tuple2<K, V>> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, V>, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public String toString() {
      return TraversableLike$class.toString(this);
   }

   public String stringPrefix() {
      return TraversableLike$class.stringPrefix(this);
   }

   public FilterMonadic<Tuple2<K, V>, Iterable<Tuple2<K, V>>> withFilter(Function1<Tuple2<K, V>, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<Tuple2<K, V>> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<Tuple2<K, V>, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, V>, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<Tuple2<K, V>, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, Tuple2<K, V>, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, V>, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<Tuple2<K, V>, B, B> op) {
      return TraversableOnce$class.reduceRightOption(this, op);
   }

   public <A1> A1 reduce(Function2<A1, A1, A1> op) {
      return (A1)TraversableOnce$class.reduce(this, op);
   }

   public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
      return TraversableOnce$class.reduceOption(this, op);
   }

   public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
      return (A1)TraversableOnce$class.fold(this, z, op);
   }

   public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, V>, B> seqop, Function2<B, B, B> combop) {
      return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
   }

   public <B> B sum(Numeric<B> num) {
      return (B)TraversableOnce$class.sum(this, num);
   }

   public <B> B product(Numeric<B> num) {
      return (B)TraversableOnce$class.product(this, num);
   }

   public Object min(Ordering cmp) {
      return TraversableOnce$class.min(this, cmp);
   }

   public Object max(Ordering cmp) {
      return TraversableOnce$class.max(this, cmp);
   }

   public Object maxBy(Function1 f, Ordering cmp) {
      return TraversableOnce$class.maxBy(this, f, cmp);
   }

   public Object minBy(Function1 f, Ordering cmp) {
      return TraversableOnce$class.minBy(this, f, cmp);
   }

   public <B> void copyToBuffer(Buffer<B> dest) {
      TraversableOnce$class.copyToBuffer(this, dest);
   }

   public <B> void copyToArray(Object xs, int start) {
      TraversableOnce$class.copyToArray(this, xs, start);
   }

   public <B> void copyToArray(Object xs) {
      TraversableOnce$class.copyToArray(this, xs);
   }

   public <B> Object toArray(ClassTag<B> evidence$1) {
      return TraversableOnce$class.toArray(this, evidence$1);
   }

   public List<Tuple2<K, V>> toList() {
      return TraversableOnce$class.toList(this);
   }

   public Seq<Tuple2<K, V>> toSeq() {
      return TraversableOnce$class.toSeq(this);
   }

   public IndexedSeq<Tuple2<K, V>> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> Buffer<B> toBuffer() {
      return TraversableOnce$class.toBuffer(this);
   }

   public <B> Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<Tuple2<K, V>> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, V>, Tuple2<T, U>> ev) {
      return TraversableOnce$class.toMap(this, ev);
   }

   public String mkString(String start, String sep, String end) {
      return TraversableOnce$class.mkString(this, start, sep, end);
   }

   public String mkString(String sep) {
      return TraversableOnce$class.mkString(this, sep);
   }

   public String mkString() {
      return TraversableOnce$class.mkString(this);
   }

   public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
      return TraversableOnce$class.addString(this, b, start, sep, end);
   }

   public StringBuilder addString(StringBuilder b, String sep) {
      return TraversableOnce$class.addString(this, b, sep);
   }

   public StringBuilder addString(StringBuilder b) {
      return TraversableOnce$class.addString(this, b);
   }

   public OpenHashSet<K> org$apache$spark$util$collection$OpenHashMap$$_keySet() {
      return this.org$apache$spark$util$collection$OpenHashMap$$_keySet;
   }

   public void org$apache$spark$util$collection$OpenHashMap$$_keySet_$eq(OpenHashSet<K> x$1) {
      this.org$apache$spark$util$collection$OpenHashMap$$_keySet = x$1;
   }

   public Object _values() {
      return this._values;
   }

   public void _values_$eq(Object x$1) {
      this._values = x$1;
   }

   public Object _oldValues() {
      return this._oldValues;
   }

   public void _oldValues_$eq(Object x$1) {
      this._oldValues = x$1;
   }

   public boolean org$apache$spark$util$collection$OpenHashMap$$haveNullValue() {
      return this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue;
   }

   public void org$apache$spark$util$collection$OpenHashMap$$haveNullValue_$eq(boolean x$1) {
      this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue = x$1;
   }

   public V nullValue() {
      return this.nullValue;
   }

   public void nullValue_$eq(V x$1) {
      this.nullValue = (V)x$1;
   }

   public int size() {
      return this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue() ? this.org$apache$spark$util$collection$OpenHashMap$$_keySet().size() + 1 : this.org$apache$spark$util$collection$OpenHashMap$$_keySet().size();
   }

   public boolean contains(K k) {
      return k == null ? this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue() : this.org$apache$spark$util$collection$OpenHashMap$$_keySet().getPos(k) != OpenHashSet$.MODULE$.INVALID_POS();
   }

   public V apply(K k) {
      Object var10000;
      if (k == null) {
         var10000 = this.nullValue();
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().getPos(k);
         var10000 = pos < 0 ? null : ScalaRunTime$.MODULE$.array_apply(this._values(), pos);
      }

      return (V)var10000;
   }

   public void update(K k, V v) {
      if (k == null) {
         this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue_$eq(true);
         this.nullValue_$eq(v);
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().addWithoutResize(k) & OpenHashSet$.MODULE$.POSITION_MASK();
         ScalaRunTime$.MODULE$.array_update(this._values(), pos, v);
         this.org$apache$spark$util$collection$OpenHashMap$$_keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$OpenHashMap$$grow(), this.org$apache$spark$util$collection$OpenHashMap$$move());
         this._oldValues_$eq((Object)null);
      }

   }

   public V changeValue(K k, Function0<V> defaultValue, Function1<V, V> mergeValue) {
      Object var10000;
      if (k == null) {
         if (this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue()) {
            this.nullValue_$eq(mergeValue.apply(this.nullValue()));
         } else {
            this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue_$eq(true);
            this.nullValue_$eq(defaultValue.apply());
         }

         var10000 = this.nullValue();
      } else {
         int pos = this.org$apache$spark$util$collection$OpenHashMap$$_keySet().addWithoutResize(k);
         if ((pos & OpenHashSet$.MODULE$.NONEXISTENCE_MASK()) != 0) {
            Object newValue = defaultValue.apply();
            ScalaRunTime$.MODULE$.array_update(this._values(), pos & OpenHashSet$.MODULE$.POSITION_MASK(), newValue);
            this.org$apache$spark$util$collection$OpenHashMap$$_keySet().rehashIfNeeded(k, this.org$apache$spark$util$collection$OpenHashMap$$grow(), this.org$apache$spark$util$collection$OpenHashMap$$move());
            var10000 = newValue;
         } else {
            ScalaRunTime$.MODULE$.array_update(this._values(), pos, mergeValue.apply(ScalaRunTime$.MODULE$.array_apply(this._values(), pos)));
            var10000 = ScalaRunTime$.MODULE$.array_apply(this._values(), pos);
         }
      }

      return (V)var10000;
   }

   public Iterator<Tuple2<K, V>> iterator() {
      return new Iterator<Tuple2<K, V>>(this) {
         private int pos;
         private Tuple2<K, V> nextPair;
         // $FF: synthetic field
         private final OpenHashMap $outer;

         public Iterator<Tuple2<K, V>> seq() {
            return Iterator$class.seq(this);
         }

         public boolean isEmpty() {
            return Iterator$class.isEmpty(this);
         }

         public boolean isTraversableAgain() {
            return Iterator$class.isTraversableAgain(this);
         }

         public boolean hasDefiniteSize() {
            return Iterator$class.hasDefiniteSize(this);
         }

         public Iterator<Tuple2<K, V>> take(int n) {
            return Iterator$class.take(this, n);
         }

         public Iterator<Tuple2<K, V>> drop(int n) {
            return Iterator$class.drop(this, n);
         }

         public Iterator<Tuple2<K, V>> slice(int from, int until) {
            return Iterator$class.slice(this, from, until);
         }

         public <B> Iterator<B> map(Function1<Tuple2<K, V>, B> f) {
            return Iterator$class.map(this, f);
         }

         public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
            return Iterator$class.$plus$plus(this, that);
         }

         public <B> Iterator<B> flatMap(Function1<Tuple2<K, V>, GenTraversableOnce<B>> f) {
            return Iterator$class.flatMap(this, f);
         }

         public Iterator<Tuple2<K, V>> filter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filter(this, p);
         }

         public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<Tuple2<K, V>, B, Object> p) {
            return Iterator$class.corresponds(this, that, p);
         }

         public Iterator<Tuple2<K, V>> withFilter(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.withFilter(this, p);
         }

         public Iterator<Tuple2<K, V>> filterNot(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.filterNot(this, p);
         }

         public <B> Iterator<B> collect(PartialFunction<Tuple2<K, V>, B> pf) {
            return Iterator$class.collect(this, pf);
         }

         public <B> Iterator<B> scanLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return Iterator$class.scanLeft(this, z, op);
         }

         public <B> Iterator<B> scanRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return Iterator$class.scanRight(this, z, op);
         }

         public Iterator<Tuple2<K, V>> takeWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.takeWhile(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> partition(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.partition(this, p);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> span(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.span(this, p);
         }

         public Iterator<Tuple2<K, V>> dropWhile(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.dropWhile(this, p);
         }

         public <B> Iterator<Tuple2<Tuple2<K, V>, B>> zip(Iterator<B> that) {
            return Iterator$class.zip(this, that);
         }

         public <A1> Iterator<A1> padTo(int len, A1 elem) {
            return Iterator$class.padTo(this, len, elem);
         }

         public Iterator<Tuple2<Tuple2<K, V>, Object>> zipWithIndex() {
            return Iterator$class.zipWithIndex(this);
         }

         public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
            return Iterator$class.zipAll(this, that, thisElem, thatElem);
         }

         public <U> void foreach(Function1<Tuple2<K, V>, U> f) {
            Iterator$class.foreach(this, f);
         }

         public boolean forall(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.forall(this, p);
         }

         public boolean exists(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.exists(this, p);
         }

         public boolean contains(Object elem) {
            return Iterator$class.contains(this, elem);
         }

         public Option<Tuple2<K, V>> find(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.find(this, p);
         }

         public int indexWhere(Function1<Tuple2<K, V>, Object> p) {
            return Iterator$class.indexWhere(this, p);
         }

         public <B> int indexOf(B elem) {
            return Iterator$class.indexOf(this, elem);
         }

         public BufferedIterator<Tuple2<K, V>> buffered() {
            return Iterator$class.buffered(this);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> grouped(int size) {
            return Iterator$class.grouped(this, size);
         }

         public <B> Iterator<Tuple2<K, V>>.GroupedIterator<B> sliding(int size, int step) {
            return Iterator$class.sliding(this, size, step);
         }

         public int length() {
            return Iterator$class.length(this);
         }

         public Tuple2<Iterator<Tuple2<K, V>>, Iterator<Tuple2<K, V>>> duplicate() {
            return Iterator$class.duplicate(this);
         }

         public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
            return Iterator$class.patch(this, from, patchElems, replaced);
         }

         public <B> void copyToArray(Object xs, int start, int len) {
            Iterator$class.copyToArray(this, xs, start, len);
         }

         public boolean sameElements(Iterator<?> that) {
            return Iterator$class.sameElements(this, that);
         }

         public Traversable<Tuple2<K, V>> toTraversable() {
            return Iterator$class.toTraversable(this);
         }

         public Iterator<Tuple2<K, V>> toIterator() {
            return Iterator$class.toIterator(this);
         }

         public Stream<Tuple2<K, V>> toStream() {
            return Iterator$class.toStream(this);
         }

         public String toString() {
            return Iterator$class.toString(this);
         }

         public <B> int sliding$default$2() {
            return Iterator$class.sliding$default$2(this);
         }

         public List<Tuple2<K, V>> reversed() {
            return TraversableOnce$class.reversed(this);
         }

         public int size() {
            return TraversableOnce$class.size(this);
         }

         public boolean nonEmpty() {
            return TraversableOnce$class.nonEmpty(this);
         }

         public int count(Function1<Tuple2<K, V>, Object> p) {
            return TraversableOnce$class.count(this, p);
         }

         public <B> Option<B> collectFirst(PartialFunction<Tuple2<K, V>, B> pf) {
            return TraversableOnce$class.collectFirst(this, pf);
         }

         public <B> B $div$colon(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.$div$colon(this, z, op);
         }

         public <B> B $colon$bslash(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.$colon$bslash(this, z, op);
         }

         public <B> B foldLeft(B z, Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.foldLeft(this, z, op);
         }

         public <B> B foldRight(B z, Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.foldRight(this, z, op);
         }

         public <B> B reduceLeft(Function2<B, Tuple2<K, V>, B> op) {
            return (B)TraversableOnce$class.reduceLeft(this, op);
         }

         public <B> B reduceRight(Function2<Tuple2<K, V>, B, B> op) {
            return (B)TraversableOnce$class.reduceRight(this, op);
         }

         public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<K, V>, B> op) {
            return TraversableOnce$class.reduceLeftOption(this, op);
         }

         public <B> Option<B> reduceRightOption(Function2<Tuple2<K, V>, B, B> op) {
            return TraversableOnce$class.reduceRightOption(this, op);
         }

         public <A1> A1 reduce(Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.reduce(this, op);
         }

         public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
            return TraversableOnce$class.reduceOption(this, op);
         }

         public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
            return (A1)TraversableOnce$class.fold(this, z, op);
         }

         public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<K, V>, B> seqop, Function2<B, B, B> combop) {
            return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
         }

         public <B> B sum(Numeric<B> num) {
            return (B)TraversableOnce$class.sum(this, num);
         }

         public <B> B product(Numeric<B> num) {
            return (B)TraversableOnce$class.product(this, num);
         }

         public Object min(Ordering cmp) {
            return TraversableOnce$class.min(this, cmp);
         }

         public Object max(Ordering cmp) {
            return TraversableOnce$class.max(this, cmp);
         }

         public Object maxBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.maxBy(this, f, cmp);
         }

         public Object minBy(Function1 f, Ordering cmp) {
            return TraversableOnce$class.minBy(this, f, cmp);
         }

         public <B> void copyToBuffer(Buffer<B> dest) {
            TraversableOnce$class.copyToBuffer(this, dest);
         }

         public <B> void copyToArray(Object xs, int start) {
            TraversableOnce$class.copyToArray(this, xs, start);
         }

         public <B> void copyToArray(Object xs) {
            TraversableOnce$class.copyToArray(this, xs);
         }

         public <B> Object toArray(ClassTag<B> evidence$1) {
            return TraversableOnce$class.toArray(this, evidence$1);
         }

         public List<Tuple2<K, V>> toList() {
            return TraversableOnce$class.toList(this);
         }

         public Iterable<Tuple2<K, V>> toIterable() {
            return TraversableOnce$class.toIterable(this);
         }

         public Seq<Tuple2<K, V>> toSeq() {
            return TraversableOnce$class.toSeq(this);
         }

         public IndexedSeq<Tuple2<K, V>> toIndexedSeq() {
            return TraversableOnce$class.toIndexedSeq(this);
         }

         public <B> Buffer<B> toBuffer() {
            return TraversableOnce$class.toBuffer(this);
         }

         public <B> Set<B> toSet() {
            return TraversableOnce$class.toSet(this);
         }

         public Vector<Tuple2<K, V>> toVector() {
            return TraversableOnce$class.toVector(this);
         }

         public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<K, V>, Col> cbf) {
            return (Col)TraversableOnce$class.to(this, cbf);
         }

         public <T, U> Map<T, U> toMap(Predef.$less$colon$less<Tuple2<K, V>, Tuple2<T, U>> ev) {
            return TraversableOnce$class.toMap(this, ev);
         }

         public String mkString(String start, String sep, String end) {
            return TraversableOnce$class.mkString(this, start, sep, end);
         }

         public String mkString(String sep) {
            return TraversableOnce$class.mkString(this, sep);
         }

         public String mkString() {
            return TraversableOnce$class.mkString(this);
         }

         public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
            return TraversableOnce$class.addString(this, b, start, sep, end);
         }

         public StringBuilder addString(StringBuilder b, String sep) {
            return TraversableOnce$class.addString(this, b, sep);
         }

         public StringBuilder addString(StringBuilder b) {
            return TraversableOnce$class.addString(this, b);
         }

         private int pos() {
            return this.pos;
         }

         private void pos_$eq(int x$1) {
            this.pos = x$1;
         }

         private Tuple2<K, V> nextPair() {
            return this.nextPair;
         }

         private void nextPair_$eq(Tuple2<K, V> x$1) {
            this.nextPair = x$1;
         }

         private Tuple2<K, V> computeNextPair() {
            if (this.pos() == -1) {
               if (this.$outer.org$apache$spark$util$collection$OpenHashMap$$haveNullValue()) {
                  this.pos_$eq(this.pos() + 1);
                  return new Tuple2<K, V>((Object)null, this.$outer.nullValue());
               }

               this.pos_$eq(this.pos() + 1);
            }

            this.pos_$eq(this.$outer.org$apache$spark$util$collection$OpenHashMap$$_keySet().nextPos(this.pos()));
            Tuple2 var10000;
            if (this.pos() >= 0) {
               Tuple2 ret = new Tuple2(this.$outer.org$apache$spark$util$collection$OpenHashMap$$_keySet().getValue(this.pos()), ScalaRunTime$.MODULE$.array_apply(this.$outer._values(), this.pos()));
               this.pos_$eq(this.pos() + 1);
               var10000 = ret;
            } else {
               var10000 = null;
            }

            return var10000;
         }

         public boolean hasNext() {
            return this.nextPair() != null;
         }

         public Tuple2<K, V> next() {
            Tuple2 pair = this.nextPair();
            this.nextPair_$eq(this.computeNextPair());
            return pair;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.pos = -1;
               this.nextPair = this.computeNextPair();
            }
         }
      };
   }

   public Function1<Object, BoxedUnit> org$apache$spark$util$collection$OpenHashMap$$grow() {
      return this.org$apache$spark$util$collection$OpenHashMap$$grow;
   }

   public void org$apache$spark$util$collection$OpenHashMap$$grow_$eq(Function1<Object, BoxedUnit> x$1) {
      this.org$apache$spark$util$collection$OpenHashMap$$grow = x$1;
   }

   public Function2<Object, Object, BoxedUnit> org$apache$spark$util$collection$OpenHashMap$$move() {
      return this.org$apache$spark$util$collection$OpenHashMap$$move;
   }

   public void org$apache$spark$util$collection$OpenHashMap$$move_$eq(Function2<Object, Object, BoxedUnit> x$1) {
      this.org$apache$spark$util$collection$OpenHashMap$$move = x$1;
   }

   public double[] _values$mcD$sp() {
      return (double[])this._values();
   }

   public int[] _values$mcI$sp() {
      return (int[])this._values();
   }

   public long[] _values$mcJ$sp() {
      return (long[])this._values();
   }

   public void _values$mcD$sp_$eq(double[] x$1) {
      this._values_$eq(x$1);
   }

   public void _values$mcI$sp_$eq(int[] x$1) {
      this._values_$eq(x$1);
   }

   public void _values$mcJ$sp_$eq(long[] x$1) {
      this._values_$eq(x$1);
   }

   public double[] _oldValues$mcD$sp() {
      return (double[])this._oldValues();
   }

   public int[] _oldValues$mcI$sp() {
      return (int[])this._oldValues();
   }

   public long[] _oldValues$mcJ$sp() {
      return (long[])this._oldValues();
   }

   public void _oldValues$mcD$sp_$eq(double[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public void _oldValues$mcI$sp_$eq(int[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public void _oldValues$mcJ$sp_$eq(long[] x$1) {
      this._oldValues_$eq(x$1);
   }

   public double nullValue$mcD$sp() {
      return BoxesRunTime.unboxToDouble(this.nullValue());
   }

   public int nullValue$mcI$sp() {
      return BoxesRunTime.unboxToInt(this.nullValue());
   }

   public long nullValue$mcJ$sp() {
      return BoxesRunTime.unboxToLong(this.nullValue());
   }

   public void nullValue$mcD$sp_$eq(double x$1) {
      this.nullValue_$eq(BoxesRunTime.boxToDouble(x$1));
   }

   public void nullValue$mcI$sp_$eq(int x$1) {
      this.nullValue_$eq(BoxesRunTime.boxToInteger(x$1));
   }

   public void nullValue$mcJ$sp_$eq(long x$1) {
      this.nullValue_$eq(BoxesRunTime.boxToLong(x$1));
   }

   public double apply$mcD$sp(K k) {
      return BoxesRunTime.unboxToDouble(this.apply(k));
   }

   public int apply$mcI$sp(K k) {
      return BoxesRunTime.unboxToInt(this.apply(k));
   }

   public long apply$mcJ$sp(K k) {
      return BoxesRunTime.unboxToLong(this.apply(k));
   }

   public void update$mcD$sp(K k, double v) {
      this.update(k, BoxesRunTime.boxToDouble(v));
   }

   public void update$mcI$sp(K k, int v) {
      this.update(k, BoxesRunTime.boxToInteger(v));
   }

   public void update$mcJ$sp(K k, long v) {
      this.update(k, BoxesRunTime.boxToLong(v));
   }

   public double changeValue$mcD$sp(K k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToDouble(this.changeValue(k, defaultValue, mergeValue));
   }

   public int changeValue$mcI$sp(K k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToInt(this.changeValue(k, defaultValue, mergeValue));
   }

   public long changeValue$mcJ$sp(K k, Function0<Object> defaultValue, Function1<Object, Object> mergeValue) {
      return BoxesRunTime.unboxToLong(this.changeValue(k, defaultValue, mergeValue));
   }

   public boolean specInstance$() {
      return false;
   }

   public OpenHashMap(int initialCapacity, ClassTag<K> evidence$1, ClassTag<V> evidence$2) {
      this.org$apache$spark$util$collection$OpenHashMap$$evidence$2 = evidence$2;
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      if (!this.specInstance$()) {
         this.org$apache$spark$util$collection$OpenHashMap$$_keySet = new OpenHashSet<K>(initialCapacity, evidence$1);
         this._values_$eq(evidence$2.newArray(this.org$apache$spark$util$collection$OpenHashMap$$_keySet().capacity()));
         this._oldValues = null;
         this.org$apache$spark$util$collection$OpenHashMap$$haveNullValue = false;
         this.nullValue = null;
         this.org$apache$spark$util$collection$OpenHashMap$$grow = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final OpenHashMap $outer;

            public final void apply(int newCapacity) {
               this.apply$mcVI$sp(newCapacity);
            }

            public void apply$mcVI$sp(int newCapacity) {
               this.$outer._oldValues_$eq(this.$outer._values());
               this.$outer._values_$eq(this.$outer.org$apache$spark$util$collection$OpenHashMap$$evidence$2.newArray(newCapacity));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
         this.org$apache$spark$util$collection$OpenHashMap$$move = new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final OpenHashMap $outer;

            public final void apply(int oldPos, int newPos) {
               this.apply$mcVII$sp(oldPos, newPos);
            }

            public void apply$mcVII$sp(int oldPos, int newPos) {
               ScalaRunTime$.MODULE$.array_update(this.$outer._values(), newPos, ScalaRunTime$.MODULE$.array_apply(this.$outer._oldValues(), oldPos));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
      }

   }

   public OpenHashMap(ClassTag<K> evidence$3, ClassTag<V> evidence$4) {
      this(64, evidence$3, evidence$4);
   }
}
