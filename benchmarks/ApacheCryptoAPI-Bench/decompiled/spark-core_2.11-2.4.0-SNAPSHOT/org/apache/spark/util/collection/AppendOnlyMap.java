package org.apache.spark.util.collection;

import java.util.Comparator;
import java.util.NoSuchElementException;
import org.apache.spark.annotation.DeveloperApi;
import org.spark_project.guava.hash.Hashing;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
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
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.Nothing$;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\t\ra\u0001B\u0001\u0003\u00015\u0011Q\"\u00119qK:$wJ\u001c7z\u001b\u0006\u0004(BA\u0002\u0005\u0003)\u0019w\u000e\u001c7fGRLwN\u001c\u0006\u0003\u000b\u0019\tA!\u001e;jY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u000791\u0003g\u0005\u0003\u0001\u001fU\u0011\u0004C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\rE\u0002\u0017=\u0005r!a\u0006\u000f\u000f\u0005aYR\"A\r\u000b\u0005ia\u0011A\u0002\u001fs_>$h(C\u0001\u0013\u0013\ti\u0012#A\u0004qC\u000e\\\u0017mZ3\n\u0005}\u0001#\u0001C%uKJ\f'\r\\3\u000b\u0005u\t\u0002\u0003\u0002\t#I=J!aI\t\u0003\rQ+\b\u000f\\33!\t)c\u0005\u0004\u0001\u0005\u000b\u001d\u0002!\u0019\u0001\u0015\u0003\u0003-\u000b\"!\u000b\u0017\u0011\u0005AQ\u0013BA\u0016\u0012\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001E\u0017\n\u00059\n\"aA!osB\u0011Q\u0005\r\u0003\u0006c\u0001\u0011\r\u0001\u000b\u0002\u0002-B\u0011\u0001cM\u0005\u0003iE\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u000e\u0001\u0003\u0002\u0003\u0006IaN\u0001\u0010S:LG/[1m\u0007\u0006\u0004\u0018mY5usB\u0011\u0001\u0003O\u0005\u0003sE\u00111!\u00138u\u0011\u0015Y\u0004\u0001\"\u0001=\u0003\u0019a\u0014N\\5u}Q\u0011Qh\u0010\t\u0005}\u0001!s&D\u0001\u0003\u0011\u001d1$\b%AA\u0002]Bq!\u0011\u0001C\u0002\u0013%!)A\u0006M\u001f\u0006#uLR!D)>\u0013V#A\"\u0011\u0005A!\u0015BA#\u0012\u0005\u0019!u.\u001e2mK\"1q\t\u0001Q\u0001\n\r\u000bA\u0002T(B\t~3\u0015i\u0011+P%\u0002Bq!\u0013\u0001A\u0002\u0013%!*\u0001\u0005dCB\f7-\u001b;z+\u00059\u0004b\u0002'\u0001\u0001\u0004%I!T\u0001\rG\u0006\u0004\u0018mY5us~#S-\u001d\u000b\u0003\u001dF\u0003\"\u0001E(\n\u0005A\u000b\"\u0001B+oSRDqAU&\u0002\u0002\u0003\u0007q'A\u0002yIEBa\u0001\u0016\u0001!B\u00139\u0014!C2ba\u0006\u001c\u0017\u000e^=!\u0011\u001d1\u0006\u00011A\u0005\n)\u000bA!\\1tW\"9\u0001\f\u0001a\u0001\n\u0013I\u0016\u0001C7bg.|F%Z9\u0015\u00059S\u0006b\u0002*X\u0003\u0003\u0005\ra\u000e\u0005\u00079\u0002\u0001\u000b\u0015B\u001c\u0002\u000b5\f7o\u001b\u0011\t\u000fy\u0003\u0001\u0019!C\u0005\u0015\u000691-\u001e:TSj,\u0007b\u00021\u0001\u0001\u0004%I!Y\u0001\fGV\u00148+\u001b>f?\u0012*\u0017\u000f\u0006\u0002OE\"9!kXA\u0001\u0002\u00049\u0004B\u00023\u0001A\u0003&q'\u0001\u0005dkJ\u001c\u0016N_3!\u0011\u001d1\u0007\u00011A\u0005\n)\u000bQb\u001a:poRC'/Z:i_2$\u0007b\u00025\u0001\u0001\u0004%I![\u0001\u0012OJ|w\u000f\u00165sKNDw\u000e\u001c3`I\u0015\fHC\u0001(k\u0011\u001d\u0011v-!AA\u0002]Ba\u0001\u001c\u0001!B\u00139\u0014AD4s_^$\u0006N]3tQ>dG\r\t\u0005\b]\u0002\u0001\r\u0011\"\u0003p\u0003\u0011!\u0017\r^1\u0016\u0003A\u00042\u0001E9\u0010\u0013\t\u0011\u0018CA\u0003BeJ\f\u0017\u0010C\u0004u\u0001\u0001\u0007I\u0011B;\u0002\u0011\u0011\fG/Y0%KF$\"A\u0014<\t\u000fI\u001b\u0018\u0011!a\u0001a\"1\u0001\u0010\u0001Q!\nA\fQ\u0001Z1uC\u0002BqA\u001f\u0001A\u0002\u0013%10A\u0007iCZ,g*\u001e7m-\u0006dW/Z\u000b\u0002yB\u0011\u0001#`\u0005\u0003}F\u0011qAQ8pY\u0016\fg\u000eC\u0005\u0002\u0002\u0001\u0001\r\u0011\"\u0003\u0002\u0004\u0005\t\u0002.\u0019<f\u001dVdGNV1mk\u0016|F%Z9\u0015\u00079\u000b)\u0001C\u0004S\u007f\u0006\u0005\t\u0019\u0001?\t\u000f\u0005%\u0001\u0001)Q\u0005y\u0006q\u0001.\u0019<f\u001dVdGNV1mk\u0016\u0004\u0003\"CA\u0007\u0001\u0001\u0007I\u0011BA\b\u0003%qW\u000f\u001c7WC2,X-F\u00010\u0011%\t\u0019\u0002\u0001a\u0001\n\u0013\t)\"A\u0007ok2dg+\u00197vK~#S-\u001d\u000b\u0004\u001d\u0006]\u0001\u0002\u0003*\u0002\u0012\u0005\u0005\t\u0019A\u0018\t\u000f\u0005m\u0001\u0001)Q\u0005_\u0005Qa.\u001e7m-\u0006dW/\u001a\u0011\t\u0011\u0005}\u0001\u00011A\u0005\nm\f\u0011\u0002Z3tiJ|\u00170\u001a3\t\u0013\u0005\r\u0002\u00011A\u0005\n\u0005\u0015\u0012!\u00043fgR\u0014x._3e?\u0012*\u0017\u000fF\u0002O\u0003OA\u0001BUA\u0011\u0003\u0003\u0005\r\u0001 \u0005\b\u0003W\u0001\u0001\u0015)\u0003}\u0003)!Wm\u001d;s_f,G\r\t\u0005\n\u0003_\u0001!\u0019!C\u0005\u0003c\t!\u0003Z3tiJ,8\r^5p]6+7o]1hKV\u0011\u00111\u0007\t\u0005\u0003k\ty$\u0004\u0002\u00028)!\u0011\u0011HA\u001e\u0003\u0011a\u0017M\\4\u000b\u0005\u0005u\u0012\u0001\u00026bm\u0006LA!!\u0011\u00028\t11\u000b\u001e:j]\u001eD\u0001\"!\u0012\u0001A\u0003%\u00111G\u0001\u0014I\u0016\u001cHO];di&|g.T3tg\u0006<W\r\t\u0005\b\u0003\u0013\u0002A\u0011AA&\u0003\u0015\t\u0007\u000f\u001d7z)\ry\u0013Q\n\u0005\b\u0003\u001f\n9\u00051\u0001%\u0003\rYW-\u001f\u0005\b\u0003'\u0002A\u0011AA+\u0003\u0019)\b\u000fZ1uKR)a*a\u0016\u0002Z!9\u0011qJA)\u0001\u0004!\u0003bBA.\u0003#\u0002\raL\u0001\u0006m\u0006dW/\u001a\u0005\b\u0003?\u0002A\u0011AA1\u0003-\u0019\u0007.\u00198hKZ\u000bG.^3\u0015\u000b=\n\u0019'!\u001a\t\u000f\u0005=\u0013Q\fa\u0001I!A\u0011qMA/\u0001\u0004\tI'\u0001\u0006va\u0012\fG/\u001a$v]\u000e\u0004b\u0001EA6y>z\u0013bAA7#\tIa)\u001e8di&|gN\r\u0005\b\u0003c\u0002A\u0011IA:\u0003!IG/\u001a:bi>\u0014XCAA;!\u00111\u0012qO\u0011\n\u0007\u0005e\u0004E\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011\u0019\ti\b\u0001C!\u0015\u0006!1/\u001b>f\u0011\u001d\t\t\t\u0001C\u0005\u0003\u0007\u000bQ\"\u001b8de\u0016lWM\u001c;TSj,G#\u0001(\t\u000f\u0005\u001d\u0005\u0001\"\u0003\u0002\n\u00061!/\u001a5bg\"$2aNAF\u0011\u001d\ti)!\"A\u0002]\n\u0011\u0001\u001b\u0005\b\u0003#\u0003A\u0011CAB\u0003%9'o\\<UC\ndW\rC\u0004\u0002\u0016\u0002!I!a&\u0002\u00199,\u0007\u0010\u001e)po\u0016\u0014xJ\u001a\u001a\u0015\u0007]\nI\nC\u0004\u0002\u001c\u0006M\u0005\u0019A\u001c\u0002\u00039Dq!a(\u0001\t\u0003\t\t+A\reKN$(/^2uSZ,7k\u001c:uK\u0012LE/\u001a:bi>\u0014H\u0003BA;\u0003GC\u0001\"!*\u0002\u001e\u0002\u0007\u0011qU\u0001\u000eW\u0016L8i\\7qCJ\fGo\u001c:\u0011\u000b\u0005%\u0016Q\u0016\u0013\u000e\u0005\u0005-&bA\u0003\u0002<%!\u0011qVAV\u0005)\u0019u.\u001c9be\u0006$xN\u001d\u0005\u0007\u0003g\u0003A\u0011A>\u0002\u001f\u0005$xI]8x)\"\u0014Xm\u001d5pY\u0012D3\u0001AA\\!\u0011\tI,a0\u000e\u0005\u0005m&bAA_\r\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005\u0005\u00171\u0018\u0002\r\t\u00164X\r\\8qKJ\f\u0005/[\u0004\b\u0003\u000b\u0014\u0001\u0012BAd\u00035\t\u0005\u000f]3oI>sG._'baB\u0019a(!3\u0007\r\u0005\u0011\u0001\u0012BAf'\u0011\tIm\u0004\u001a\t\u000fm\nI\r\"\u0001\u0002PR\u0011\u0011q\u0019\u0005\n\u0003'\fIM1A\u0005\u0002)\u000b\u0001#T!Y\u00136+VjX\"B!\u0006\u001b\u0015\nV-\t\u0011\u0005]\u0017\u0011\u001aQ\u0001\n]\n\u0011#T!Y\u00136+VjX\"B!\u0006\u001b\u0015\nV-!\u0011)\tY.!3\u0012\u0002\u0013\u0005\u0011Q\\\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u0019\u0016\r\u0005}\u00171_A{+\t\t\tOK\u00028\u0003G\\#!!:\u0011\t\u0005\u001d\u0018q^\u0007\u0003\u0003STA!a;\u0002n\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0004\u0003{\u000b\u0012\u0002BAy\u0003S\u0014\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u00199\u0013\u0011\u001cb\u0001Q\u00111\u0011'!7C\u0002!B!\"!?\u0002J\u0006\u0005I\u0011BA~\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005u\b\u0003BA\u001b\u0003\u007fLAA!\u0001\u00028\t1qJ\u00196fGR\u0004"
)
public class AppendOnlyMap<K, V> implements Iterable<Tuple2<K, V>>, Serializable {
   private final double LOAD_FACTOR;
   private int org$apache$spark$util$collection$AppendOnlyMap$$capacity;
   private int mask;
   private int curSize;
   private int org$apache$spark$util$collection$AppendOnlyMap$$growThreshold;
   private Object[] org$apache$spark$util$collection$AppendOnlyMap$$data;
   private boolean org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue;
   private V org$apache$spark$util$collection$AppendOnlyMap$$nullValue;
   private boolean destroyed;
   private final String org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage;

   public static <K, V> int $lessinit$greater$default$1() {
      return AppendOnlyMap$.MODULE$.$lessinit$greater$default$1();
   }

   public static int MAXIMUM_CAPACITY() {
      return AppendOnlyMap$.MODULE$.MAXIMUM_CAPACITY();
   }

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

   private double LOAD_FACTOR() {
      return this.LOAD_FACTOR;
   }

   public int org$apache$spark$util$collection$AppendOnlyMap$$capacity() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$capacity;
   }

   private void org$apache$spark$util$collection$AppendOnlyMap$$capacity_$eq(int x$1) {
      this.org$apache$spark$util$collection$AppendOnlyMap$$capacity = x$1;
   }

   private int mask() {
      return this.mask;
   }

   private void mask_$eq(int x$1) {
      this.mask = x$1;
   }

   private int curSize() {
      return this.curSize;
   }

   private void curSize_$eq(int x$1) {
      this.curSize = x$1;
   }

   public int org$apache$spark$util$collection$AppendOnlyMap$$growThreshold() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold;
   }

   private void org$apache$spark$util$collection$AppendOnlyMap$$growThreshold_$eq(int x$1) {
      this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold = x$1;
   }

   public Object[] org$apache$spark$util$collection$AppendOnlyMap$$data() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$data;
   }

   private void org$apache$spark$util$collection$AppendOnlyMap$$data_$eq(Object[] x$1) {
      this.org$apache$spark$util$collection$AppendOnlyMap$$data = x$1;
   }

   public boolean org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue;
   }

   private void org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue_$eq(boolean x$1) {
      this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue = x$1;
   }

   public V org$apache$spark$util$collection$AppendOnlyMap$$nullValue() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue;
   }

   private void org$apache$spark$util$collection$AppendOnlyMap$$nullValue_$eq(V x$1) {
      this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue = (V)x$1;
   }

   private boolean destroyed() {
      return this.destroyed;
   }

   private void destroyed_$eq(boolean x$1) {
      this.destroyed = x$1;
   }

   public String org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage() {
      return this.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage;
   }

   public V apply(K key) {
      Predef$.MODULE$.assert(!this.destroyed(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

         public final String apply() {
            return this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Object k = key;
      if (key == null) {
         return (V)this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue();
      } else {
         int pos = this.rehash(key.hashCode()) & this.mask();
         int i = 1;

         while(true) {
            Object curKey = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos];
            if (k == curKey || k.equals(curKey)) {
               return (V)this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1];
            }

            if (curKey == null) {
               Object var10000 = null;
               return null;
            }

            pos = pos + i & this.mask();
            ++i;
         }
      }
   }

   public void update(K key, V value) {
      Predef$.MODULE$.assert(!this.destroyed(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

         public final String apply() {
            return this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Object k = key;
      if (key == null) {
         if (!this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue()) {
            this.incrementSize();
         }

         this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue_$eq(value);
         this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue_$eq(true);
      } else {
         int pos = this.rehash(key.hashCode()) & this.mask();
         int i = 1;

         while(true) {
            Object curKey = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos];
            if (curKey == null) {
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos] = k;
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1] = value;
               this.incrementSize();
               return;
            }

            if (k == curKey || k.equals(curKey)) {
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1] = value;
               return;
            }

            pos = pos + i & this.mask();
            ++i;
         }
      }
   }

   public V changeValue(K key, Function2<Object, V, V> updateFunc) {
      Predef$.MODULE$.assert(!this.destroyed(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

         public final String apply() {
            return this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Object k = key;
      if (key == null) {
         if (!this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue()) {
            this.incrementSize();
         }

         this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue_$eq(updateFunc.apply(BoxesRunTime.boxToBoolean(this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue()), this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue()));
         this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue_$eq(true);
         return (V)this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue();
      } else {
         int pos = this.rehash(key.hashCode()) & this.mask();
         int i = 1;

         while(true) {
            Object curKey = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos];
            if (curKey == null) {
               Object newValue = updateFunc.apply(BoxesRunTime.boxToBoolean(false), (Object)null);
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos] = k;
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1] = newValue;
               this.incrementSize();
               return (V)newValue;
            }

            if (k == curKey || k.equals(curKey)) {
               Object newValue = updateFunc.apply(BoxesRunTime.boxToBoolean(true), this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1]);
               this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * pos + 1] = newValue;
               return (V)newValue;
            }

            pos = pos + i & this.mask();
            ++i;
         }
      }
   }

   public Iterator<Tuple2<K, V>> iterator() {
      Predef$.MODULE$.assert(!this.destroyed(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

         public final String apply() {
            return this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      return new Iterator<Tuple2<K, V>>(this) {
         private int pos;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

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

         private Tuple2<K, V> nextValue() {
            if (this.pos() == -1) {
               if (this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue()) {
                  return new Tuple2<K, V>((Object)null, this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$nullValue());
               }

               this.pos_$eq(this.pos() + 1);
            }

            while(this.pos() < this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$capacity()) {
               if (this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * this.pos()] != null) {
                  return new Tuple2<K, V>(this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * this.pos()], this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * this.pos() + 1]);
               }

               this.pos_$eq(this.pos() + 1);
            }

            return null;
         }

         public boolean hasNext() {
            return this.nextValue() != null;
         }

         public Tuple2<K, V> next() {
            Tuple2 value = this.nextValue();
            if (value == null) {
               throw new NoSuchElementException("End of iterator");
            } else {
               this.pos_$eq(this.pos() + 1);
               return value;
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.pos = -1;
            }
         }
      };
   }

   public int size() {
      return this.curSize();
   }

   private void incrementSize() {
      this.curSize_$eq(this.curSize() + 1);
      if (this.curSize() > this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold()) {
         this.growTable();
      }

   }

   private int rehash(int h) {
      return Hashing.murmur3_32().hashInt(h).asInt();
   }

   public void growTable() {
      int newCapacity = this.org$apache$spark$util$collection$AppendOnlyMap$$capacity() * 2;
      Predef$.MODULE$.require(newCapacity <= AppendOnlyMap$.MODULE$.MAXIMUM_CAPACITY(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't contain more than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Object[] newData = new Object[2 * newCapacity];
      int newMask = newCapacity - 1;

      for(int oldPos = 0; oldPos < this.org$apache$spark$util$collection$AppendOnlyMap$$capacity(); ++oldPos) {
         if (this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * oldPos] != null) {
            Object key = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * oldPos];
            Object value = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * oldPos + 1];
            int newPos = this.rehash(key.hashCode()) & newMask;
            int i = 1;
            boolean keepGoing = true;

            while(keepGoing) {
               Object curKey = newData[2 * newPos];
               if (curKey == null) {
                  newData[2 * newPos] = key;
                  newData[2 * newPos + 1] = value;
                  keepGoing = false;
               } else {
                  newPos = newPos + i & newMask;
                  ++i;
               }
            }
         }
      }

      this.org$apache$spark$util$collection$AppendOnlyMap$$data_$eq(newData);
      this.org$apache$spark$util$collection$AppendOnlyMap$$capacity_$eq(newCapacity);
      this.mask_$eq(newMask);
      this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold_$eq((int)(this.LOAD_FACTOR() * (double)newCapacity));
   }

   private int nextPowerOf2(int n) {
      int highBit = Integer.highestOneBit(n);
      return highBit == n ? n : highBit << 1;
   }

   public Iterator<Tuple2<K, V>> destructiveSortedIterator(Comparator<K> keyComparator) {
      this.destroyed_$eq(true);
      int keyIndex = 0;

      IntRef newIndex;
      for(newIndex = IntRef.create(0); keyIndex < this.org$apache$spark$util$collection$AppendOnlyMap$$capacity(); ++keyIndex) {
         if (this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * keyIndex] != null) {
            this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * newIndex.elem] = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * keyIndex];
            this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * newIndex.elem + 1] = this.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * keyIndex + 1];
            ++newIndex.elem;
         }
      }

      Predef$.MODULE$.assert(this.curSize() == newIndex.elem + (this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue() ? 1 : 0));
      (new Sorter(new KVArraySortDataFormat(ClassTag$.MODULE$.AnyRef()))).sort(this.org$apache$spark$util$collection$AppendOnlyMap$$data(), 0, newIndex.elem, keyComparator);
      return new Iterator<Tuple2<K, V>>(this, newIndex) {
         private int i;
         private boolean nullValueReady;
         // $FF: synthetic field
         private final AppendOnlyMap $outer;
         private final IntRef newIndex$1;

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

         private int i() {
            return this.i;
         }

         private void i_$eq(int x$1) {
            this.i = x$1;
         }

         private boolean nullValueReady() {
            return this.nullValueReady;
         }

         private void nullValueReady_$eq(boolean x$1) {
            this.nullValueReady = x$1;
         }

         public boolean hasNext() {
            return this.i() < this.newIndex$1.elem || this.nullValueReady();
         }

         public Tuple2<K, V> next() {
            Tuple2 var10000;
            if (this.nullValueReady()) {
               this.nullValueReady_$eq(false);
               var10000 = new Tuple2((Object)null, this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$nullValue());
            } else {
               Tuple2 item = new Tuple2(this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * this.i()], this.$outer.org$apache$spark$util$collection$AppendOnlyMap$$data()[2 * this.i() + 1]);
               this.i_$eq(this.i() + 1);
               var10000 = item;
            }

            return var10000;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.newIndex$1 = newIndex$1;
               TraversableOnce$class.$init$(this);
               Iterator$class.$init$(this);
               this.i = 0;
               this.nullValueReady = $outer.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue();
            }
         }
      };
   }

   public boolean atGrowThreshold() {
      return this.curSize() == this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold();
   }

   public AppendOnlyMap(int initialCapacity) {
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      Iterable$class.$init$(this);
      Predef$.MODULE$.require(initialCapacity <= AppendOnlyMap$.MODULE$.MAXIMUM_CAPACITY(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Can't make capacity bigger than ", " elements"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(AppendOnlyMap$.MODULE$.MAXIMUM_CAPACITY())}));
         }
      });
      Predef$.MODULE$.require(initialCapacity >= 1, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Invalid initial capacity";
         }
      });
      this.LOAD_FACTOR = 0.7;
      this.org$apache$spark$util$collection$AppendOnlyMap$$capacity = this.nextPowerOf2(initialCapacity);
      this.mask = this.org$apache$spark$util$collection$AppendOnlyMap$$capacity() - 1;
      this.curSize = 0;
      this.org$apache$spark$util$collection$AppendOnlyMap$$growThreshold = (int)(this.LOAD_FACTOR() * (double)this.org$apache$spark$util$collection$AppendOnlyMap$$capacity());
      this.org$apache$spark$util$collection$AppendOnlyMap$$data = new Object[2 * this.org$apache$spark$util$collection$AppendOnlyMap$$capacity()];
      this.org$apache$spark$util$collection$AppendOnlyMap$$haveNullValue = false;
      this.org$apache$spark$util$collection$AppendOnlyMap$$nullValue = null;
      this.destroyed = false;
      this.org$apache$spark$util$collection$AppendOnlyMap$$destructionMessage = "Map state is invalid from destructive sorting!";
   }
}
