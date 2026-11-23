package org.apache.spark.api.java;

import org.apache.spark.Partitioner;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.partial.BoundedDouble;
import org.apache.spark.partial.PartialResult;
import org.apache.spark.rdd.DoubleRDDFunctions;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.RDD$;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.StatCounter;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\tEc\u0001B\u0001\u0003\u00015\u0011QBS1wC\u0012{WO\u00197f%\u0012#%BA\u0002\u0005\u0003\u0011Q\u0017M^1\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001q\u0001\u0003B\b\u0011%ei\u0011AA\u0005\u0003#\t\u00111#\u00112tiJ\f7\r\u001e&bm\u0006\u0014F\t\u0012'jW\u0016\u0004\"aE\f\u000e\u0003QQ!!\u0006\f\u0002\t1\fgn\u001a\u0006\u0002\u0007%\u0011\u0001\u0004\u0006\u0002\u0007\t>,(\r\\3\u0011\u0005=\u0001\u0001\u0002C\u000e\u0001\u0005\u000b\u0007I\u0011\u0001\u000f\u0002\tM\u0014H\rZ\u000b\u0002;A\u0019a$I\u0012\u000e\u0003}Q!\u0001\t\u0004\u0002\u0007I$G-\u0003\u0002#?\t\u0019!\u000b\u0012#\u0011\u0005\u0011:S\"A\u0013\u000b\u0003\u0019\nQa]2bY\u0006L!\u0001G\u0013\t\u0011%\u0002!\u0011!Q\u0001\nu\tQa\u001d:eI\u0002BQa\u000b\u0001\u0005\u00021\na\u0001P5oSRtDCA\r.\u0011\u0015Y\"\u00061\u0001\u001e\u0011\u001dy\u0003A1A\u0005BA\n\u0001b\u00197bgN$\u0016mZ\u000b\u0002cA\u0019!'\u000e\n\u000e\u0003MR!\u0001N\u0013\u0002\u000fI,g\r\\3di&\u0011ag\r\u0002\t\u00072\f7o\u001d+bO\"1\u0001\b\u0001Q\u0001\nE\n\u0011b\u00197bgN$\u0016m\u001a\u0011\t\u000f\u0001\u0002!\u0019!C!uU\t1\bE\u0002\u001fCIAa!\u0010\u0001!\u0002\u0013Y\u0014\u0001\u0002:eI\u0002BQa\u0010\u0001\u0005B\u0001\u000bqa\u001e:baJ#E\t\u0006\u0002\u001a\u0003\")\u0001E\u0010a\u0001w!)1\t\u0001C\u0001\t\u0006)1-Y2iKR\t\u0011\u0004C\u0003G\u0001\u0011\u0005q)A\u0004qKJ\u001c\u0018n\u001d;\u0015\u0005eA\u0005\"B%F\u0001\u0004Q\u0015\u0001\u00038fo2+g/\u001a7\u0011\u0005-sU\"\u0001'\u000b\u000553\u0011aB:u_J\fw-Z\u0005\u0003\u001f2\u0013Ab\u0015;pe\u0006<W\rT3wK2DQ!\u0015\u0001\u0005\u0002\u0011\u000b\u0011\"\u001e8qKJ\u001c\u0018n\u001d;\t\u000bE\u0003A\u0011A*\u0015\u0005e!\u0006\"B+S\u0001\u00041\u0016\u0001\u00032m_\u000e\\\u0017N\\4\u0011\u0005\u0011:\u0016B\u0001-&\u0005\u001d\u0011un\u001c7fC:DQA\u0017\u0001\u0005Bm\u000bQAZ5sgR$\u0012A\u0005\u0005\u0006;\u0002!\t\u0001R\u0001\tI&\u001cH/\u001b8di\")Q\f\u0001C\u0001?R\u0011\u0011\u0004\u0019\u0005\u0006Cz\u0003\rAY\u0001\u000e]Vl\u0007+\u0019:uSRLwN\\:\u0011\u0005\u0011\u001a\u0017B\u00013&\u0005\rIe\u000e\u001e\u0005\u0006M\u0002!\taZ\u0001\u0007M&dG/\u001a:\u0015\u0005eA\u0007\"B5f\u0001\u0004Q\u0017!\u00014\u0011\t-t'\u0003]\u0007\u0002Y*\u0011QNA\u0001\tMVt7\r^5p]&\u0011q\u000e\u001c\u0002\t\rVt7\r^5p]B\u00111#]\u0005\u00031RAQa\u001d\u0001\u0005\u0002Q\f\u0001bY8bY\u0016\u001c8-\u001a\u000b\u00033UDQ!\u0019:A\u0002\tDQa\u001d\u0001\u0005\u0002]$2!\u0007=z\u0011\u0015\tg\u000f1\u0001c\u0011\u0015Qh\u000f1\u0001W\u0003\u001d\u0019\b.\u001e4gY\u0016DQ\u0001 \u0001\u0005\u0002u\f1B]3qCJ$\u0018\u000e^5p]R\u0011\u0011D \u0005\u0006Cn\u0004\rA\u0019\u0005\b\u0003\u0003\u0001A\u0011AA\u0002\u0003!\u0019XO\u0019;sC\u000e$HcA\r\u0002\u0006!1\u0011qA@A\u0002e\tQa\u001c;iKJDq!!\u0001\u0001\t\u0003\tY\u0001F\u0003\u001a\u0003\u001b\ty\u0001C\u0004\u0002\b\u0005%\u0001\u0019A\r\t\r\u0005\fI\u00011\u0001c\u0011\u001d\t\t\u0001\u0001C\u0001\u0003'!R!GA\u000b\u0003/Aq!a\u0002\u0002\u0012\u0001\u0007\u0011\u0004\u0003\u0005\u0002\u001a\u0005E\u0001\u0019AA\u000e\u0003\u0005\u0001\b\u0003BA\u000f\u0003?i\u0011AB\u0005\u0004\u0003C1!a\u0003)beRLG/[8oKJDq!!\n\u0001\t\u0003\t9#\u0001\u0004tC6\u0004H.\u001a\u000b\u00063\u0005%\u0012Q\u0006\u0005\b\u0003W\t\u0019\u00031\u0001W\u0003=9\u0018\u000e\u001e5SKBd\u0017mY3nK:$\bbBA\u0018\u0003G\u0001\rAE\u0001\tMJ\f7\r^5p]\"9\u0011Q\u0005\u0001\u0005\u0002\u0005MBcB\r\u00026\u0005]\u0012\u0011\b\u0005\b\u0003W\t\t\u00041\u0001W\u0011\u001d\ty#!\rA\u0002IA\u0001\"a\u000f\u00022\u0001\u0007\u0011QH\u0001\u0005g\u0016,G\rE\u0002%\u0003\u007fI1!!\u0011&\u0005\u0011auN\\4\t\u000f\u0005\u0015\u0003\u0001\"\u0001\u0002H\u0005)QO\\5p]R\u0019\u0011$!\u0013\t\u000f\u0005\u001d\u00111\ta\u00013!9\u0011Q\n\u0001\u0005\u0002\u0005=\u0013\u0001D5oi\u0016\u00148/Z2uS>tGcA\r\u0002R!9\u0011qAA&\u0001\u0004I\u0002BBA+\u0001\u0011\u00051,A\u0002tk6Da!!\u0017\u0001\t\u0003Y\u0016aA7j]\"1\u0011Q\f\u0001\u0005\u0002m\u000b1!\\1y\u0011\u001d\t\t\u0007\u0001C\u0001\u0003G\nQa\u001d;biN$\"!!\u001a\u0011\t\u0005\u001d\u0014QN\u0007\u0003\u0003SR1!a\u001b\u0007\u0003\u0011)H/\u001b7\n\t\u0005=\u0014\u0011\u000e\u0002\f'R\fGoQ8v]R,'\u000f\u0003\u0004\u0002t\u0001!\taW\u0001\u0005[\u0016\fg\u000e\u0003\u0004\u0002x\u0001!\taW\u0001\tm\u0006\u0014\u0018.\u00198dK\"1\u00111\u0010\u0001\u0005\u0002m\u000bQa\u001d;eKZDa!a \u0001\t\u0003Y\u0016aC:b[BdWm\u0015;eKZDa!a!\u0001\t\u0003Y\u0016AD:b[BdWMV1sS\u0006t7-\u001a\u0005\u0007\u0003\u000f\u0003A\u0011A.\u0002\u0011A|\u0007o\u0015;eKZDc!!\"\u0002\f\u0006]\u0005\u0003BAG\u0003'k!!a$\u000b\u0007\u0005Ee!\u0001\u0006b]:|G/\u0019;j_:LA!!&\u0002\u0010\n)1+\u001b8dK\u0006\u0012\u0011\u0011T\u0001\u0006e9\nd\u0006\r\u0005\u0007\u0003;\u0003A\u0011A.\u0002\u0017A|\u0007OV1sS\u0006t7-\u001a\u0015\u0007\u00037\u000bY)a&\t\u000f\u0005\r\u0006\u0001\"\u0001\u0002&\u0006QQ.Z1o\u0003B\u0004(o\u001c=\u0015\r\u0005\u001d\u0016\u0011XA_!\u0019\tI+a,\u000246\u0011\u00111\u0016\u0006\u0004\u0003[3\u0011a\u00029beRL\u0017\r\\\u0005\u0005\u0003c\u000bYKA\u0007QCJ$\u0018.\u00197SKN,H\u000e\u001e\t\u0005\u0003S\u000b),\u0003\u0003\u00028\u0006-&!\u0004\"pk:$W\r\u001a#pk\ndW\r\u0003\u0005\u0002<\u0006\u0005\u0006\u0019AA\u001f\u0003\u001d!\u0018.\\3pkRDq!a0\u0002\"\u0002\u0007!#\u0001\u0006d_:4\u0017\u000eZ3oG\u0016Dq!a)\u0001\t\u0003\t\u0019\r\u0006\u0003\u0002(\u0006\u0015\u0007\u0002CA^\u0003\u0003\u0004\r!!\u0010\t\u000f\u0005%\u0007\u0001\"\u0001\u0002L\u0006I1/^7BaB\u0014x\u000e\u001f\u000b\u0007\u0003O\u000bi-a4\t\u0011\u0005m\u0016q\u0019a\u0001\u0003{Aq!a0\u0002H\u0002\u0007!\u0003C\u0004\u0002J\u0002!\t!a5\u0015\t\u0005\u001d\u0016Q\u001b\u0005\t\u0003w\u000b\t\u000e1\u0001\u0002>!9\u0011\u0011\u001c\u0001\u0005\u0002\u0005m\u0017!\u00035jgR|wM]1n)\u0011\ti.a;\u0011\u000f\u0011\ny.a9\u0002j&\u0019\u0011\u0011]\u0013\u0003\rQ+\b\u000f\\33!\u0011!\u0013Q]\u0012\n\u0007\u0005\u001dXEA\u0003BeJ\f\u0017\u0010E\u0003%\u0003K\fi\u0004C\u0004\u0002n\u0006]\u0007\u0019\u00012\u0002\u0017\t,8m[3u\u0007>,h\u000e\u001e\u0005\b\u00033\u0004A\u0011AAy)\u0011\tI/a=\t\u0011\u0005U\u0018q\u001ea\u0001\u0003G\fqAY;dW\u0016$8\u000fC\u0004\u0002Z\u0002!\t!!?\u0015\r\u0005%\u00181`A\u0000\u0011!\t)0a>A\u0002\u0005u\b\u0003\u0002\u0013\u0002fJAqA!\u0001\u0002x\u0002\u0007a+A\u0006fm\u0016t')^2lKR\u001c\bb\u0002B\u0003\u0001\u0011\u0005!qA\u0001\bg\u0016$h*Y7f)\rI\"\u0011\u0002\u0005\t\u0005\u0017\u0011\u0019\u00011\u0001\u0003\u000e\u0005!a.Y7f!\u0011\u0011yA!\u0006\u000f\u0007\u0011\u0012\t\"C\u0002\u0003\u0014\u0015\na\u0001\u0015:fI\u00164\u0017\u0002\u0002B\f\u00053\u0011aa\u0015;sS:<'b\u0001B\nK\u001d9!Q\u0004\u0002\t\u0002\t}\u0011!\u0004&bm\u0006$u.\u001e2mKJ#E\tE\u0002\u0010\u0005C1a!\u0001\u0002\t\u0002\t\r2C\u0002B\u0011\u0005K\u0011Y\u0003E\u0002%\u0005OI1A!\u000b&\u0005\u0019\te.\u001f*fMB\u0019AE!\f\n\u0007\t=RE\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004,\u0005C!\tAa\r\u0015\u0005\t}\u0001\u0002\u0003B\u001c\u0005C!\tA!\u000f\u0002\u000f\u0019\u0014x.\u001c*E\tR\u0019\u0011Da\u000f\t\r\u0001\u0012)\u00041\u0001\u001e\u0011!\u0011yD!\t\u0005\u0004\t\u0005\u0013!\u0002;p%\u0012#EcA\u000f\u0003D!1\u0001E!\u0010A\u0002eA!Ba\u0012\u0003\"\u0005\u0005I\u0011\u0002B%\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\t-\u0003cA\n\u0003N%\u0019!q\n\u000b\u0003\r=\u0013'.Z2u\u0001"
)
public class JavaDoubleRDD extends AbstractJavaRDDLike<Double, JavaDoubleRDD> {
   private final RDD<Object> srdd;
   private final ClassTag<Double> classTag;
   private final RDD<Double> rdd;

   public static RDD<Object> toRDD(JavaDoubleRDD var0) {
      return JavaDoubleRDD$.MODULE$.toRDD(var0);
   }

   public static JavaDoubleRDD fromRDD(RDD<Object> var0) {
      return JavaDoubleRDD$.MODULE$.fromRDD(var0);
   }

   public RDD<Object> srdd() {
      return this.srdd;
   }

   public ClassTag<Double> classTag() {
      return this.classTag;
   }

   public RDD<Double> rdd() {
      return this.rdd;
   }

   public JavaDoubleRDD wrapRDD(RDD<Double> rdd) {
      return new JavaDoubleRDD(rdd.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$1) {
            return x$1;
         }
      }, ClassTag$.MODULE$.Double()));
   }

   public JavaDoubleRDD cache() {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().cache());
   }

   public JavaDoubleRDD persist(StorageLevel newLevel) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().persist(newLevel));
   }

   public JavaDoubleRDD unpersist() {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().unpersist(this.srdd().unpersist$default$1()));
   }

   public JavaDoubleRDD unpersist(boolean blocking) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().unpersist(blocking));
   }

   public Double first() {
      return Predef$.MODULE$.double2Double(BoxesRunTime.unboxToDouble(this.srdd().first()));
   }

   public JavaDoubleRDD distinct() {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().distinct());
   }

   public JavaDoubleRDD distinct(int numPartitions) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().distinct(numPartitions, Ordering.Double$.MODULE$));
   }

   public JavaDoubleRDD filter(Function<Double, Boolean> f) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().filter(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         private final Function f$1;

         public final boolean apply(double x) {
            return this.apply$mcZD$sp(x);
         }

         public boolean apply$mcZD$sp(double x) {
            return (Boolean)this.f$1.call(Predef$.MODULE$.double2Double(x));
         }

         public {
            this.f$1 = f$1;
         }
      }));
   }

   public JavaDoubleRDD coalesce(int numPartitions) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().coalesce(numPartitions, this.srdd().coalesce$default$2(), this.srdd().coalesce$default$3(), Ordering.Double$.MODULE$));
   }

   public JavaDoubleRDD coalesce(int numPartitions, boolean shuffle) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().coalesce(numPartitions, shuffle, this.srdd().coalesce$default$3(), Ordering.Double$.MODULE$));
   }

   public JavaDoubleRDD repartition(int numPartitions) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().repartition(numPartitions, Ordering.Double$.MODULE$));
   }

   public JavaDoubleRDD subtract(JavaDoubleRDD other) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().subtract(JavaDoubleRDD$.MODULE$.toRDD(other)));
   }

   public JavaDoubleRDD subtract(JavaDoubleRDD other, int numPartitions) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().subtract(JavaDoubleRDD$.MODULE$.toRDD(other), numPartitions));
   }

   public JavaDoubleRDD subtract(JavaDoubleRDD other, Partitioner p) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().subtract(JavaDoubleRDD$.MODULE$.toRDD(other), p, Ordering.Double$.MODULE$));
   }

   public JavaDoubleRDD sample(boolean withReplacement, Double fraction) {
      return this.sample(withReplacement, fraction, Utils$.MODULE$.random().nextLong());
   }

   public JavaDoubleRDD sample(boolean withReplacement, Double fraction, long seed) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().sample(withReplacement, Predef$.MODULE$.Double2double(fraction), seed));
   }

   public JavaDoubleRDD union(JavaDoubleRDD other) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().union(other.srdd()));
   }

   public JavaDoubleRDD intersection(JavaDoubleRDD other) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.srdd().intersection(other.srdd()));
   }

   public Double sum() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).sum());
   }

   public Double min() {
      return (Double)this.min(org.spark_project.guava.collect.Ordering.natural());
   }

   public Double max() {
      return (Double)this.max(org.spark_project.guava.collect.Ordering.natural());
   }

   public StatCounter stats() {
      return RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).stats();
   }

   public Double mean() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).mean());
   }

   public Double variance() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).variance());
   }

   public Double stdev() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).stdev());
   }

   public Double sampleStdev() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).sampleStdev());
   }

   public Double sampleVariance() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).sampleVariance());
   }

   public Double popStdev() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).popStdev());
   }

   public Double popVariance() {
      return Predef$.MODULE$.double2Double(RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).popVariance());
   }

   public PartialResult<BoundedDouble> meanApprox(long timeout, Double confidence) {
      return RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).meanApprox(timeout, Predef$.MODULE$.Double2double(confidence));
   }

   public PartialResult<BoundedDouble> meanApprox(long timeout) {
      DoubleRDDFunctions qual$1 = RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd());
      double x$4 = qual$1.meanApprox$default$2();
      return qual$1.meanApprox(timeout, x$4);
   }

   public PartialResult<BoundedDouble> sumApprox(long timeout, Double confidence) {
      return RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).sumApprox(timeout, Predef$.MODULE$.Double2double(confidence));
   }

   public PartialResult<BoundedDouble> sumApprox(long timeout) {
      DoubleRDDFunctions qual$2 = RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd());
      double x$6 = qual$2.sumApprox$default$2();
      return qual$2.sumApprox(timeout, x$6);
   }

   public Tuple2<double[], long[]> histogram(int bucketCount) {
      Tuple2 result = RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).histogram(bucketCount);
      return new Tuple2<double[], long[]>(result._1(), result._2());
   }

   public long[] histogram(double[] buckets) {
      return RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).histogram(buckets, false);
   }

   public long[] histogram(Double[] buckets, boolean evenBuckets) {
      return RDD$.MODULE$.doubleRDDToDoubleRDDFunctions(this.srdd()).histogram((double[])Predef$.MODULE$.refArrayOps((Object[])buckets).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$2) {
            return Predef$.MODULE$.Double2double(x$2);
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Double())), evenBuckets);
   }

   public JavaDoubleRDD setName(String name) {
      this.srdd().setName(name);
      return this;
   }

   public JavaDoubleRDD(RDD<Object> srdd) {
      this.srdd = srdd;
      this.classTag = (ClassTag)Predef$.MODULE$.implicitly(ClassTag$.MODULE$.apply(Double.class));
      this.rdd = srdd.<Double>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Double apply(double x) {
            return x;
         }
      }, ClassTag$.MODULE$.apply(Double.class));
   }
}
