package org.apache.spark.util;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Function1$class;
import scala.Function2;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.PartialFunction$class;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.GenIterable;
import scala.collection.GenIterable$class;
import scala.collection.GenMapLike$class;
import scala.collection.GenTraversable;
import scala.collection.GenTraversable$class;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.IterableLike$class;
import scala.collection.IterableView;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Parallel;
import scala.collection.Parallelizable$class;
import scala.collection.Seq;
import scala.collection.Set;
import scala.collection.Traversable;
import scala.collection.Traversable$class;
import scala.collection.TraversableLike;
import scala.collection.TraversableLike$class;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.generic.FilterMonadic;
import scala.collection.generic.GenericCompanion;
import scala.collection.generic.GenericTraversableTemplate$class;
import scala.collection.generic.Growable;
import scala.collection.generic.Growable$class;
import scala.collection.generic.Shrinkable;
import scala.collection.generic.Shrinkable$class;
import scala.collection.generic.Subtractable$class;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Stream;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Builder;
import scala.collection.mutable.Builder$class;
import scala.collection.mutable.Cloneable$class;
import scala.collection.mutable.Iterable$class;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.Map$class;
import scala.collection.mutable.MapLike;
import scala.collection.mutable.MapLike$class;
import scala.collection.mutable.StringBuilder;
import scala.collection.parallel.Combiner;
import scala.collection.parallel.mutable.ParMap;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005uh!B\u0001\u0003\u0001\u0011Q!A\u0005+j[\u0016\u001cF/Y7qK\u0012D\u0015m\u001d5NCBT!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sOV\u00191\u0002H\u0014\u0014\t\u0001a!#\u000b\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\tMA\"DJ\u0007\u0002))\u0011QCF\u0001\b[V$\u0018M\u00197f\u0015\t9b\"\u0001\u0006d_2dWm\u0019;j_:L!!\u0007\u000b\u0003\u00075\u000b\u0007\u000f\u0005\u0002\u001c91\u0001A!B\u000f\u0001\u0005\u0004y\"!A!\u0004\u0001E\u0011\u0001e\t\t\u0003\u001b\u0005J!A\t\b\u0003\u000f9{G\u000f[5oOB\u0011Q\u0002J\u0005\u0003K9\u00111!\u00118z!\tYr\u0005B\u0003)\u0001\t\u0007qDA\u0001C!\tQS&D\u0001,\u0015\taC!\u0001\u0005j]R,'O\\1m\u0013\tq3FA\u0004M_\u001e<\u0017N\\4\t\u0011A\u0002!\u0011!Q\u0001\nE\nA#\u001e9eCR,G+[7f'R\fW\u000e](o\u000f\u0016$\bCA\u00073\u0013\t\u0019dBA\u0004C_>dW-\u00198\t\u000bU\u0002A\u0011\u0001\u001c\u0002\rqJg.\u001b;?)\t9\u0014\b\u0005\u00039\u0001i1S\"\u0001\u0002\t\u000fA\"\u0004\u0013!a\u0001c!91\b\u0001b\u0001\n\u0013a\u0014aC5oi\u0016\u0014h.\u00197NCB,\u0012!\u0010\t\u0005}\u0011Sb)D\u0001@\u0015\t\u0001\u0015)\u0001\u0006d_:\u001cWO\u001d:f]RT!a\u0001\"\u000b\u0003\r\u000bAA[1wC&\u0011Qi\u0010\u0002\u0012\u0007>t7-\u001e:sK:$\b*Y:i\u001b\u0006\u0004\bc\u0001\u001dHM%\u0011\u0001J\u0001\u0002\u0011)&lWm\u0015;b[B,GMV1mk\u0016DaA\u0013\u0001!\u0002\u0013i\u0014\u0001D5oi\u0016\u0014h.\u00197NCB\u0004\u0003\"\u0002'\u0001\t\u0003i\u0015aA4fiR\u0011a*\u0015\t\u0004\u001b=3\u0013B\u0001)\u000f\u0005\u0019y\u0005\u000f^5p]\")!k\u0013a\u00015\u0005\u00191.Z=\t\u000bQ\u0003A\u0011A+\u0002\u0011%$XM]1u_J,\u0012A\u0016\t\u0004/~\u0013gB\u0001-^\u001d\tIF,D\u0001[\u0015\tYf$\u0001\u0004=e>|GOP\u0005\u0002\u001f%\u0011aLD\u0001\ba\u0006\u001c7.Y4f\u0013\t\u0001\u0017M\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\tqf\u0002\u0005\u0003\u000eGj1\u0013B\u00013\u000f\u0005\u0019!V\u000f\u001d7fe!)a\r\u0001C\u0001O\u0006Yq-\u001a;F]R\u0014\u0018pU3u+\u0005A\u0007cA5kY6\t\u0011)\u0003\u0002l\u0003\n\u00191+\u001a;\u0011\t5$(D\u0012\b\u0003]Jt!a\\9\u000f\u0005e\u0003\u0018\"A\"\n\u0005\r\u0011\u0015BA:B\u0003\ri\u0015\r]\u0005\u0003kZ\u0014Q!\u00128uefT!a]!\t\u000ba\u0004A\u0011I=\u0002\u000b\u0011\u0002H.^:\u0016\u0005ilHcA>\u0002\u0002A!1\u0003\u0007\u000e}!\tYR\u0010B\u0003\u007fo\n\u0007qP\u0001\u0002CcE\u0011ae\t\u0005\b\u0003\u00079\b\u0019AA\u0003\u0003\tYg\u000f\u0005\u0003\u000eGja\bbBA\u0005\u0001\u0011\u0005\u00131B\u0001\u0007I5Lg.^:\u0015\u0007I\ti\u0001\u0003\u0004S\u0003\u000f\u0001\rA\u0007\u0005\b\u0003#\u0001A\u0011IA\n\u0003!!\u0003\u000f\\;tI\u0015\fH\u0003BA\u000b\u0003/i\u0011\u0001\u0001\u0005\b\u0003\u0007\ty\u00011\u0001c\u0011\u001d\tY\u0002\u0001C!\u0003;\t\u0011\u0002J7j]V\u001cH%Z9\u0015\t\u0005U\u0011q\u0004\u0005\u0007%\u0006e\u0001\u0019\u0001\u000e\t\u000f\u0005\r\u0002\u0001\"\u0011\u0002&\u00051Q\u000f\u001d3bi\u0016$b!a\n\u0002.\u0005=\u0002cA\u0007\u0002*%\u0019\u00111\u0006\b\u0003\tUs\u0017\u000e\u001e\u0005\u0007%\u0006\u0005\u0002\u0019\u0001\u000e\t\u000f\u0005E\u0012\u0011\u0005a\u0001M\u0005)a/\u00197vK\"9\u0011Q\u0007\u0001\u0005B\u0005]\u0012!B1qa2LHc\u0001\u0014\u0002:!1!+a\rA\u0002iAq!!\u0010\u0001\t\u0003\ny$\u0001\u0004gS2$XM\u001d\u000b\u0004%\u0005\u0005\u0003\u0002CA\"\u0003w\u0001\r!!\u0012\u0002\u0003A\u0004R!DA$EFJ1!!\u0013\u000f\u0005%1UO\\2uS>t\u0017\u0007C\u0004\u0002N\u0001!\t%a\u0014\u0002\u000b\u0015l\u0007\u000f^=\u0016\u0003IAq!a\u0015\u0001\t\u0003\n)&\u0001\u0003tSj,WCAA,!\ri\u0011\u0011L\u0005\u0004\u00037r!aA%oi\"9\u0011q\f\u0001\u0005B\u0005\u0005\u0014a\u00024pe\u0016\f7\r[\u000b\u0005\u0003G\ni\u0007\u0006\u0003\u0002(\u0005\u0015\u0004\u0002CA4\u0003;\u0002\r!!\u001b\u0002\u0003\u0019\u0004b!DA$E\u0006-\u0004cA\u000e\u0002n\u00119\u0011qNA/\u0005\u0004y\"!A+\t\u000f\u0005M\u0004\u0001\"\u0001\u0002v\u0005Y\u0001/\u001e;JM\u0006\u00137/\u001a8u)\u0015q\u0015qOA=\u0011\u0019\u0011\u0016\u0011\u000fa\u00015!9\u0011\u0011GA9\u0001\u00041\u0003bBA?\u0001\u0011\u0005\u0011qP\u0001\u0007aV$\u0018\t\u001c7\u0015\t\u0005\u001d\u0012\u0011\u0011\u0005\t\u0003\u0007\u000bY\b1\u0001\u0002\u0006\u0006\u0019Q.\u00199\u0011\r\u0005\u001d\u0015Q\u0012\u000e'\u001d\ri\u0011\u0011R\u0005\u0004\u0003\u0017s\u0011A\u0002)sK\u0012,g-C\u0002\u001a\u0003\u001fS1!a#\u000f\u0011\u001d\t\u0019\n\u0001C\u0001\u0003+\u000bQ\u0001^8NCB,\"!!\"\t\u000f\u0005e\u0005\u0001\"\u0001\u0002\u001c\u0006q1\r\\3be>cGMV1mk\u0016\u001cHCBA\u0014\u0003;\u000b9\u000b\u0003\u0005\u0002 \u0006]\u0005\u0019AAQ\u0003)!\bN]3tQRKW.\u001a\t\u0004\u001b\u0005\r\u0016bAAS\u001d\t!Aj\u001c8h\u0011!\t9'a&A\u0002\u0005%\u0006cB\u0007\u0002,j1\u0013qE\u0005\u0004\u0003[s!!\u0003$v]\u000e$\u0018n\u001c83\u0011\u001d\tI\n\u0001C\u0001\u0003c#B!a\n\u00024\"A\u0011qTAX\u0001\u0004\t\t\u000bC\u0004\u00028\u0002!I!!/\u0002\u0017\r,(O]3oiRKW.Z\u000b\u0003\u0003CCq!!0\u0001\t\u0003\ty,A\nhKR$\u0016.\\3Ti\u0006l\u0007/\u001a3WC2,X\r\u0006\u0003\u0002B\u0006\r\u0007cA\u0007P\r\"1!+a/A\u0002iAq!a2\u0001\t\u0003\tI-\u0001\u0007hKR$\u0016.\\3ti\u0006l\u0007\u000f\u0006\u0003\u0002L\u00065\u0007\u0003B\u0007P\u0003CCaAUAc\u0001\u0004QrACAi\u0005\u0005\u0005\t\u0012\u0001\u0003\u0002T\u0006\u0011B+[7f'R\fW\u000e]3e\u0011\u0006\u001c\b.T1q!\rA\u0014Q\u001b\u0004\n\u0003\t\t\t\u0011#\u0001\u0005\u0003/\u001c2!!6\r\u0011\u001d)\u0014Q\u001bC\u0001\u00037$\"!a5\t\u0015\u0005}\u0017Q[I\u0001\n\u0003\t\t/A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%M\u000b\u0007\u0003G\fI0a?\u0016\u0005\u0005\u0015(fA\u0019\u0002h.\u0012\u0011\u0011\u001e\t\u0005\u0003W\f)0\u0004\u0002\u0002n*!\u0011q^Ay\u0003%)hn\u00195fG.,GMC\u0002\u0002t:\t!\"\u00198o_R\fG/[8o\u0013\u0011\t90!<\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004\u001e\u0003;\u0014\ra\b\u0003\u0007Q\u0005u'\u0019A\u0010"
)
public class TimeStampedHashMap<A, B> implements Map<A, B>, Logging {
   private final boolean updateTimeStampOnGet;
   private final ConcurrentHashMap<A, TimeStampedValue<B>> internalMap;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static <A, B> boolean $lessinit$greater$default$1() {
      return TimeStampedHashMap$.MODULE$.$lessinit$greater$default$1();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public Map<A, B> seq() {
      return Map$class.seq(this);
   }

   public Map<A, B> withDefault(Function1<A, B> d) {
      return Map$class.withDefault(this, d);
   }

   public Map<A, B> withDefaultValue(B d) {
      return Map$class.withDefaultValue(this, d);
   }

   public Builder<Tuple2<A, B>, Map<A, B>> newBuilder() {
      return MapLike$class.newBuilder(this);
   }

   public Combiner<Tuple2<A, B>, ParMap<A, B>> parCombiner() {
      return MapLike$class.parCombiner(this);
   }

   public Option<B> put(A key, B value) {
      return MapLike$class.put(this, key, value);
   }

   public <B1> Map<A, B1> updated(A key, B1 value) {
      return MapLike$class.updated(this, key, value);
   }

   public <B1> Map<A, B1> $plus(Tuple2<A, B1> elem1, Tuple2<A, B1> elem2, Seq<Tuple2<A, B1>> elems) {
      return MapLike$class.$plus(this, elem1, elem2, elems);
   }

   public <B1> Map<A, B1> $plus$plus(GenTraversableOnce<Tuple2<A, B1>> xs) {
      return MapLike$class.$plus$plus(this, xs);
   }

   public Option<B> remove(A key) {
      return MapLike$class.remove(this, key);
   }

   public void clear() {
      MapLike$class.clear(this);
   }

   public B getOrElseUpdate(A key, Function0<B> op) {
      return (B)MapLike$class.getOrElseUpdate(this, key, op);
   }

   public MapLike<A, B, Map<A, B>> transform(Function2<A, B, B> f) {
      return MapLike$class.transform(this, f);
   }

   public MapLike<A, B, Map<A, B>> retain(Function2<A, B, Object> p) {
      return MapLike$class.retain(this, p);
   }

   public Map<A, B> clone() {
      return MapLike$class.clone(this);
   }

   public Map<A, B> result() {
      return MapLike$class.result(this);
   }

   public Map<A, B> $minus(A elem1, A elem2, Seq<A> elems) {
      return MapLike$class.$minus(this, elem1, elem2, elems);
   }

   public Map<A, B> $minus$minus(GenTraversableOnce<A> xs) {
      return MapLike$class.$minus$minus(this, xs);
   }

   // $FF: synthetic method
   public Object scala$collection$mutable$Cloneable$$super$clone() {
      return super.clone();
   }

   public Shrinkable<A> $minus$eq(A elem1, A elem2, Seq<A> elems) {
      return Shrinkable$class.$minus$eq(this, elem1, elem2, elems);
   }

   public Shrinkable<A> $minus$minus$eq(TraversableOnce<A> xs) {
      return Shrinkable$class.$minus$minus$eq(this, xs);
   }

   public void sizeHint(int size) {
      Builder$class.sizeHint(this, size);
   }

   public void sizeHint(TraversableLike<?, ?> coll) {
      Builder$class.sizeHint(this, coll);
   }

   public void sizeHint(TraversableLike<?, ?> coll, int delta) {
      Builder$class.sizeHint(this, coll, delta);
   }

   public void sizeHintBounded(int size, TraversableLike<?, ?> boundingColl) {
      Builder$class.sizeHintBounded(this, size, boundingColl);
   }

   public <NewTo> Builder<Tuple2<A, B>, NewTo> mapResult(Function1<Map<A, B>, NewTo> f) {
      return Builder$class.mapResult(this, f);
   }

   public Growable $plus$eq(Object elem1, Object elem2, Seq elems) {
      return Growable$class.$plus$eq(this, elem1, elem2, elems);
   }

   public Growable<Tuple2<A, B>> $plus$plus$eq(TraversableOnce<Tuple2<A, B>> xs) {
      return Growable$class.$plus$plus$eq(this, xs);
   }

   public boolean isEmpty() {
      return scala.collection.MapLike$class.isEmpty(this);
   }

   public <B1> B1 getOrElse(A key, Function0<B1> var2) {
      return (B1)scala.collection.MapLike$class.getOrElse(this, key, var2);
   }

   public boolean contains(A key) {
      return scala.collection.MapLike$class.contains(this, key);
   }

   public boolean isDefinedAt(A key) {
      return scala.collection.MapLike$class.isDefinedAt(this, key);
   }

   public Set<A> keySet() {
      return scala.collection.MapLike$class.keySet(this);
   }

   public Iterator<A> keysIterator() {
      return scala.collection.MapLike$class.keysIterator(this);
   }

   public Iterable<A> keys() {
      return scala.collection.MapLike$class.keys(this);
   }

   public Iterable<B> values() {
      return scala.collection.MapLike$class.values(this);
   }

   public Iterator<B> valuesIterator() {
      return scala.collection.MapLike$class.valuesIterator(this);
   }

   public B default(A key) {
      return (B)scala.collection.MapLike$class.default(this, key);
   }

   public scala.collection.Map<A, B> filterKeys(Function1<A, Object> p) {
      return scala.collection.MapLike$class.filterKeys(this, p);
   }

   public <C> scala.collection.Map<A, C> mapValues(Function1<B, C> f) {
      return scala.collection.MapLike$class.mapValues(this, f);
   }

   public scala.collection.Map filterNot(Function1 p) {
      return scala.collection.MapLike$class.filterNot(this, p);
   }

   public Seq<Tuple2<A, B>> toSeq() {
      return scala.collection.MapLike$class.toSeq(this);
   }

   public <C> Buffer<C> toBuffer() {
      return scala.collection.MapLike$class.toBuffer(this);
   }

   public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
      return scala.collection.MapLike$class.addString(this, b, start, sep, end);
   }

   public String stringPrefix() {
      return scala.collection.MapLike$class.stringPrefix(this);
   }

   public String toString() {
      return scala.collection.MapLike$class.toString(this);
   }

   public <A1 extends A, B1> PartialFunction<A1, B1> orElse(PartialFunction<A1, B1> that) {
      return PartialFunction$class.orElse(this, that);
   }

   public <C> PartialFunction<A, C> andThen(Function1<B, C> k) {
      return PartialFunction$class.andThen(this, k);
   }

   public Function1<A, Option<B>> lift() {
      return PartialFunction$class.lift(this);
   }

   public <A1 extends A, B1> B1 applyOrElse(A1 x, Function1<A1, B1> var2) {
      return (B1)PartialFunction$class.applyOrElse(this, x, var2);
   }

   public <U> Function1<A, Object> runWith(Function1<B, U> action) {
      return PartialFunction$class.runWith(this, action);
   }

   public boolean apply$mcZD$sp(double v1) {
      return Function1$class.apply$mcZD$sp(this, v1);
   }

   public double apply$mcDD$sp(double v1) {
      return Function1$class.apply$mcDD$sp(this, v1);
   }

   public float apply$mcFD$sp(double v1) {
      return Function1$class.apply$mcFD$sp(this, v1);
   }

   public int apply$mcID$sp(double v1) {
      return Function1$class.apply$mcID$sp(this, v1);
   }

   public long apply$mcJD$sp(double v1) {
      return Function1$class.apply$mcJD$sp(this, v1);
   }

   public void apply$mcVD$sp(double v1) {
      Function1$class.apply$mcVD$sp(this, v1);
   }

   public boolean apply$mcZF$sp(float v1) {
      return Function1$class.apply$mcZF$sp(this, v1);
   }

   public double apply$mcDF$sp(float v1) {
      return Function1$class.apply$mcDF$sp(this, v1);
   }

   public float apply$mcFF$sp(float v1) {
      return Function1$class.apply$mcFF$sp(this, v1);
   }

   public int apply$mcIF$sp(float v1) {
      return Function1$class.apply$mcIF$sp(this, v1);
   }

   public long apply$mcJF$sp(float v1) {
      return Function1$class.apply$mcJF$sp(this, v1);
   }

   public void apply$mcVF$sp(float v1) {
      Function1$class.apply$mcVF$sp(this, v1);
   }

   public boolean apply$mcZI$sp(int v1) {
      return Function1$class.apply$mcZI$sp(this, v1);
   }

   public double apply$mcDI$sp(int v1) {
      return Function1$class.apply$mcDI$sp(this, v1);
   }

   public float apply$mcFI$sp(int v1) {
      return Function1$class.apply$mcFI$sp(this, v1);
   }

   public int apply$mcII$sp(int v1) {
      return Function1$class.apply$mcII$sp(this, v1);
   }

   public long apply$mcJI$sp(int v1) {
      return Function1$class.apply$mcJI$sp(this, v1);
   }

   public void apply$mcVI$sp(int v1) {
      Function1$class.apply$mcVI$sp(this, v1);
   }

   public boolean apply$mcZJ$sp(long v1) {
      return Function1$class.apply$mcZJ$sp(this, v1);
   }

   public double apply$mcDJ$sp(long v1) {
      return Function1$class.apply$mcDJ$sp(this, v1);
   }

   public float apply$mcFJ$sp(long v1) {
      return Function1$class.apply$mcFJ$sp(this, v1);
   }

   public int apply$mcIJ$sp(long v1) {
      return Function1$class.apply$mcIJ$sp(this, v1);
   }

   public long apply$mcJJ$sp(long v1) {
      return Function1$class.apply$mcJJ$sp(this, v1);
   }

   public void apply$mcVJ$sp(long v1) {
      Function1$class.apply$mcVJ$sp(this, v1);
   }

   public <A> Function1<A, B> compose(Function1<A, A> g) {
      return Function1$class.compose(this, g);
   }

   public int hashCode() {
      return GenMapLike$class.hashCode(this);
   }

   public boolean equals(Object that) {
      return GenMapLike$class.equals(this, that);
   }

   public GenericCompanion<scala.collection.mutable.Iterable> companion() {
      return Iterable$class.companion(this);
   }

   public Iterable<Tuple2<A, B>> thisCollection() {
      return IterableLike$class.thisCollection(this);
   }

   public Iterable toCollection(Object repr) {
      return IterableLike$class.toCollection(this, repr);
   }

   public boolean forall(Function1<Tuple2<A, B>, Object> p) {
      return IterableLike$class.forall(this, p);
   }

   public boolean exists(Function1<Tuple2<A, B>, Object> p) {
      return IterableLike$class.exists(this, p);
   }

   public Option<Tuple2<A, B>> find(Function1<Tuple2<A, B>, Object> p) {
      return IterableLike$class.find(this, p);
   }

   public <B> B foldRight(B z, Function2<Tuple2<A, B>, B, B> op) {
      return (B)IterableLike$class.foldRight(this, z, op);
   }

   public <B> B reduceRight(Function2<Tuple2<A, B>, B, B> op) {
      return (B)IterableLike$class.reduceRight(this, op);
   }

   public Iterable<Tuple2<A, B>> toIterable() {
      return IterableLike$class.toIterable(this);
   }

   public Iterator<Tuple2<A, B>> toIterator() {
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

   public Iterator<Map<A, B>> grouped(int size) {
      return IterableLike$class.grouped(this, size);
   }

   public Iterator<Map<A, B>> sliding(int size) {
      return IterableLike$class.sliding(this, size);
   }

   public Iterator<Map<A, B>> sliding(int size, int step) {
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

   public <A1, B, That> That zip(GenIterable<B> that, CanBuildFrom<Map<A, B>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zip(this, that, bf);
   }

   public <B, A1, That> That zipAll(GenIterable<B> that, A1 thisElem, B thatElem, CanBuildFrom<Map<A, B>, Tuple2<A1, B>, That> bf) {
      return (That)IterableLike$class.zipAll(this, that, thisElem, thatElem, bf);
   }

   public <A1, That> That zipWithIndex(CanBuildFrom<Map<A, B>, Tuple2<A1, Object>, That> bf) {
      return (That)IterableLike$class.zipWithIndex(this, bf);
   }

   public <B> boolean sameElements(GenIterable<B> that) {
      return IterableLike$class.sameElements(this, that);
   }

   public Stream<Tuple2<A, B>> toStream() {
      return IterableLike$class.toStream(this);
   }

   public boolean canEqual(Object that) {
      return IterableLike$class.canEqual(this, that);
   }

   public Object view() {
      return IterableLike$class.view(this);
   }

   public IterableView<Tuple2<A, B>, Map<A, B>> view(int from, int until) {
      return IterableLike$class.view(this, from, until);
   }

   public <B> Builder<B, scala.collection.mutable.Iterable<B>> genericBuilder() {
      return GenericTraversableTemplate$class.genericBuilder(this);
   }

   public <A1, A2> Tuple2<scala.collection.mutable.Iterable<A1>, scala.collection.mutable.Iterable<A2>> unzip(Function1<Tuple2<A, B>, Tuple2<A1, A2>> asPair) {
      return GenericTraversableTemplate$class.unzip(this, asPair);
   }

   public <A1, A2, A3> Tuple3<scala.collection.mutable.Iterable<A1>, scala.collection.mutable.Iterable<A2>, scala.collection.mutable.Iterable<A3>> unzip3(Function1<Tuple2<A, B>, Tuple3<A1, A2, A3>> asTriple) {
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

   public boolean hasDefiniteSize() {
      return TraversableLike$class.hasDefiniteSize(this);
   }

   public <B, That> That $plus$plus(GenTraversableOnce<B> that, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus(this, that, bf);
   }

   public <B, That> That $plus$plus$colon(TraversableOnce<B> that, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (TraversableOnce)that, bf);
   }

   public <B, That> That $plus$plus$colon(Traversable<B> that, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.$plus$plus$colon(this, (Traversable)that, bf);
   }

   public <B, That> That map(Function1<Tuple2<A, B>, B> f, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.map(this, f, bf);
   }

   public <B, That> That flatMap(Function1<Tuple2<A, B>, GenTraversableOnce<B>> f, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.flatMap(this, f, bf);
   }

   public <B, That> That collect(PartialFunction<Tuple2<A, B>, B> pf, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.collect(this, pf, bf);
   }

   public Tuple2<Map<A, B>, Map<A, B>> partition(Function1<Tuple2<A, B>, Object> p) {
      return TraversableLike$class.partition(this, p);
   }

   public <K> scala.collection.immutable.Map<K, Map<A, B>> groupBy(Function1<Tuple2<A, B>, K> f) {
      return TraversableLike$class.groupBy(this, f);
   }

   public <B, That> That scan(B z, Function2<B, B, B> op, CanBuildFrom<Map<A, B>, B, That> cbf) {
      return (That)TraversableLike$class.scan(this, z, op, cbf);
   }

   public <B, That> That scanLeft(B z, Function2<B, Tuple2<A, B>, B> op, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.scanLeft(this, z, op, bf);
   }

   public <B, That> That scanRight(B z, Function2<Tuple2<A, B>, B, B> op, CanBuildFrom<Map<A, B>, B, That> bf) {
      return (That)TraversableLike$class.scanRight(this, z, op, bf);
   }

   public Option<Tuple2<A, B>> headOption() {
      return TraversableLike$class.headOption(this);
   }

   public Object tail() {
      return TraversableLike$class.tail(this);
   }

   public Object last() {
      return TraversableLike$class.last(this);
   }

   public Option<Tuple2<A, B>> lastOption() {
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

   public Tuple2<Map<A, B>, Map<A, B>> span(Function1<Tuple2<A, B>, Object> p) {
      return TraversableLike$class.span(this, p);
   }

   public Tuple2<Map<A, B>, Map<A, B>> splitAt(int n) {
      return TraversableLike$class.splitAt(this, n);
   }

   public Iterator<Map<A, B>> tails() {
      return TraversableLike$class.tails(this);
   }

   public Iterator<Map<A, B>> inits() {
      return TraversableLike$class.inits(this);
   }

   public Traversable<Tuple2<A, B>> toTraversable() {
      return TraversableLike$class.toTraversable(this);
   }

   public <Col> Col to(CanBuildFrom<Nothing$, Tuple2<A, B>, Col> cbf) {
      return (Col)TraversableLike$class.to(this, cbf);
   }

   public FilterMonadic<Tuple2<A, B>, Map<A, B>> withFilter(Function1<Tuple2<A, B>, Object> p) {
      return TraversableLike$class.withFilter(this, p);
   }

   public Parallel par() {
      return Parallelizable$class.par(this);
   }

   public List<Tuple2<A, B>> reversed() {
      return TraversableOnce$class.reversed(this);
   }

   public boolean nonEmpty() {
      return TraversableOnce$class.nonEmpty(this);
   }

   public int count(Function1<Tuple2<A, B>, Object> p) {
      return TraversableOnce$class.count(this, p);
   }

   public <B> Option<B> collectFirst(PartialFunction<Tuple2<A, B>, B> pf) {
      return TraversableOnce$class.collectFirst(this, pf);
   }

   public <B> B $div$colon(B z, Function2<B, Tuple2<A, B>, B> op) {
      return (B)TraversableOnce$class.$div$colon(this, z, op);
   }

   public <B> B $colon$bslash(B z, Function2<Tuple2<A, B>, B, B> op) {
      return (B)TraversableOnce$class.$colon$bslash(this, z, op);
   }

   public <B> B foldLeft(B z, Function2<B, Tuple2<A, B>, B> op) {
      return (B)TraversableOnce$class.foldLeft(this, z, op);
   }

   public <B> B reduceLeft(Function2<B, Tuple2<A, B>, B> op) {
      return (B)TraversableOnce$class.reduceLeft(this, op);
   }

   public <B> Option<B> reduceLeftOption(Function2<B, Tuple2<A, B>, B> op) {
      return TraversableOnce$class.reduceLeftOption(this, op);
   }

   public <B> Option<B> reduceRightOption(Function2<Tuple2<A, B>, B, B> op) {
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

   public <B> B aggregate(Function0<B> z, Function2<B, Tuple2<A, B>, B> seqop, Function2<B, B, B> combop) {
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

   public List<Tuple2<A, B>> toList() {
      return TraversableOnce$class.toList(this);
   }

   public IndexedSeq<Tuple2<A, B>> toIndexedSeq() {
      return TraversableOnce$class.toIndexedSeq(this);
   }

   public <B> scala.collection.immutable.Set<B> toSet() {
      return TraversableOnce$class.toSet(this);
   }

   public Vector<Tuple2<A, B>> toVector() {
      return TraversableOnce$class.toVector(this);
   }

   public <T, U> scala.collection.immutable.Map<T, U> toMap(Predef.$less$colon$less<Tuple2<A, B>, Tuple2<T, U>> ev) {
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

   public StringBuilder addString(StringBuilder b, String sep) {
      return TraversableOnce$class.addString(this, b, sep);
   }

   public StringBuilder addString(StringBuilder b) {
      return TraversableOnce$class.addString(this, b);
   }

   private ConcurrentHashMap<A, TimeStampedValue<B>> internalMap() {
      return this.internalMap;
   }

   public Option<B> get(A key) {
      TimeStampedValue value = (TimeStampedValue)this.internalMap().get(key);
      if (value != null && this.updateTimeStampOnGet) {
         BoxesRunTime.boxToBoolean(this.internalMap().replace(key, value, new TimeStampedValue(value.value(), this.currentTime())));
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return Option$.MODULE$.<B>apply(value).<B>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final B apply(TimeStampedValue<B> x$1) {
            return (B)x$1.value();
         }
      });
   }

   public Iterator<Tuple2<A, B>> iterator() {
      return ((Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.getEntrySet().iterator()).asScala()).<Tuple2<A, B>>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<A, B> apply(java.util.Map.Entry<A, TimeStampedValue<B>> kv) {
            return new Tuple2<A, B>(kv.getKey(), ((TimeStampedValue)kv.getValue()).value());
         }
      });
   }

   public java.util.Set<java.util.Map.Entry<A, TimeStampedValue<B>>> getEntrySet() {
      return this.internalMap().entrySet();
   }

   public <B1> Map<A, B1> $plus(Tuple2<A, B1> kv) {
      TimeStampedHashMap newMap = new TimeStampedHashMap(TimeStampedHashMap$.MODULE$.$lessinit$greater$default$1());
      ConcurrentHashMap oldInternalMap = this.internalMap();
      newMap.internalMap().putAll(oldInternalMap);
      if (kv != null) {
         Object a = kv._1();
         Object b = kv._2();
         TimeStampedValue var7 = (TimeStampedValue)newMap.internalMap().put(a, new TimeStampedValue(b, this.currentTime()));
         return newMap;
      } else {
         throw new MatchError(kv);
      }
   }

   public Map<A, B> $minus(A key) {
      TimeStampedHashMap newMap = new TimeStampedHashMap(TimeStampedHashMap$.MODULE$.$lessinit$greater$default$1());
      newMap.internalMap().putAll(this.internalMap());
      newMap.internalMap().remove(key);
      return newMap;
   }

   public TimeStampedHashMap<A, B> $plus$eq(Tuple2<A, B> kv) {
      if (kv != null) {
         Object a = kv._1();
         Object b = kv._2();
         TimeStampedValue var5 = (TimeStampedValue)this.internalMap().put(a, new TimeStampedValue(b, this.currentTime()));
         return this;
      } else {
         throw new MatchError(kv);
      }
   }

   public TimeStampedHashMap<A, B> $minus$eq(A key) {
      this.internalMap().remove(key);
      return this;
   }

   public void update(A key, B value) {
      this.$plus$eq(new Tuple2(key, value));
   }

   public B apply(A key) {
      return (B)this.get(key).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nothing$ apply() {
            throw new NoSuchElementException();
         }
      });
   }

   public Map<A, B> filter(Function1<Tuple2<A, B>, Object> p) {
      return (Map)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.mapAsScalaConcurrentMapConverter(this.internalMap()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Tuple2<A, B> apply(Tuple2<A, TimeStampedValue<B>> x0$1) {
            if (x0$1 != null) {
               Object k = x0$1._1();
               TimeStampedValue var4 = (TimeStampedValue)x0$1._2();
               if (var4 != null) {
                  Object v = var4.value();
                  Tuple2 var6 = new Tuple2(k, v);
                  return var6;
               }
            }

            throw new MatchError(x0$1);
         }
      }, Map$.MODULE$.canBuildFrom())).filter(p);
   }

   public Map<A, B> empty() {
      return new TimeStampedHashMap<A, B>(TimeStampedHashMap$.MODULE$.$lessinit$greater$default$1());
   }

   public int size() {
      return this.internalMap().size();
   }

   public <U> void foreach(Function1<Tuple2<A, B>, U> f) {
      for(java.util.Map.Entry entry : this.getEntrySet()) {
         Tuple2 kv = new Tuple2(entry.getKey(), ((TimeStampedValue)entry.getValue()).value());
         f.apply(kv);
      }

   }

   public Option<B> putIfAbsent(A key, B value) {
      TimeStampedValue prev = (TimeStampedValue)this.internalMap().putIfAbsent(key, new TimeStampedValue(value, this.currentTime()));
      return Option$.MODULE$.<B>apply(prev).<B>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final B apply(TimeStampedValue<B> x$2) {
            return (B)x$2.value();
         }
      });
   }

   public void putAll(scala.collection.immutable.Map<A, B> map) {
      map.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TimeStampedHashMap $outer;

         public final void apply(Tuple2<A, B> x0$2) {
            if (x0$2 != null) {
               Object k = x0$2._1();
               Object v = x0$2._2();
               this.$outer.update(k, v);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$2);
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
   }

   public scala.collection.immutable.Map<A, B> toMap() {
      return this.iterator().toMap(Predef$.MODULE$.$conforms());
   }

   public void clearOldValues(long threshTime, Function2<A, B, BoxedUnit> f) {
      java.util.Iterator it = this.getEntrySet().iterator();

      while(it.hasNext()) {
         java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
         if (((TimeStampedValue)entry.getValue()).timestamp() < threshTime) {
            f.apply(entry.getKey(), ((TimeStampedValue)entry.getValue()).value());
            this.logDebug(new Serializable(this, entry) {
               public static final long serialVersionUID = 0L;
               private final java.util.Map.Entry entry$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Removing key ").append(this.entry$1.getKey()).toString();
               }

               public {
                  this.entry$1 = entry$1;
               }
            });
            it.remove();
         }
      }

   }

   public void clearOldValues(long threshTime) {
      this.clearOldValues(threshTime, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(A x$3, B x$4) {
         }
      });
   }

   private long currentTime() {
      return System.currentTimeMillis();
   }

   public Option<TimeStampedValue<B>> getTimeStampedValue(A key) {
      return Option$.MODULE$.<TimeStampedValue<B>>apply(this.internalMap().get(key));
   }

   public Option<Object> getTimestamp(A key) {
      return this.getTimeStampedValue(key).<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(TimeStampedValue<B> x$5) {
            return x$5.timestamp();
         }
      });
   }

   public TimeStampedHashMap(boolean updateTimeStampOnGet) {
      this.updateTimeStampOnGet = updateTimeStampOnGet;
      TraversableOnce$class.$init$(this);
      Parallelizable$class.$init$(this);
      TraversableLike$class.$init$(this);
      GenericTraversableTemplate$class.$init$(this);
      GenTraversable$class.$init$(this);
      Traversable$class.$init$(this);
      scala.collection.mutable.Traversable$class.$init$(this);
      GenIterable$class.$init$(this);
      IterableLike$class.$init$(this);
      scala.collection.Iterable$class.$init$(this);
      Iterable$class.$init$(this);
      GenMapLike$class.$init$(this);
      Function1$class.$init$(this);
      PartialFunction$class.$init$(this);
      Subtractable$class.$init$(this);
      scala.collection.MapLike$class.$init$(this);
      scala.collection.Map$class.$init$(this);
      Growable$class.$init$(this);
      Builder$class.$init$(this);
      Shrinkable$class.$init$(this);
      Cloneable$class.$init$(this);
      MapLike$class.$init$(this);
      Map$class.$init$(this);
      Logging$class.$init$(this);
      this.internalMap = new ConcurrentHashMap();
   }
}
