package org.apache.spark.api.java;

import org.apache.spark.Partitioner;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.util.Utils$;
import scala.Array$;
import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mh\u0001B\u0001\u0003\u00015\u0011qAS1wCJ#EI\u0003\u0002\u0004\t\u0005!!.\u0019<b\u0015\t)a!A\u0002ba&T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001+\tqQc\u0005\u0002\u0001\u001fA!\u0001#E\n\"\u001b\u0005\u0011\u0011B\u0001\n\u0003\u0005M\t%m\u001d;sC\u000e$(*\u0019<b%\u0012#E*[6f!\t!R\u0003\u0004\u0001\u0005\u000bY\u0001!\u0019A\f\u0003\u0003Q\u000b\"\u0001\u0007\u0010\u0011\u0005eaR\"\u0001\u000e\u000b\u0003m\tQa]2bY\u0006L!!\b\u000e\u0003\u000f9{G\u000f[5oOB\u0011\u0011dH\u0005\u0003Ai\u00111!\u00118z!\r\u0001\u0002a\u0005\u0005\tG\u0001\u0011)\u0019!C\u0001I\u0005\u0019!\u000f\u001a3\u0016\u0003\u0015\u00022A\n\u0015\u0014\u001b\u00059#BA\u0012\u0007\u0013\tIsEA\u0002S\t\u0012C\u0001b\u000b\u0001\u0003\u0002\u0003\u0006I!J\u0001\u0005e\u0012$\u0007\u0005\u0003\u0005.\u0001\t\u0015\r\u0011b\u0001/\u0003!\u0019G.Y:t)\u0006<W#A\u0018\u0011\u0007A\u001a4#D\u00012\u0015\t\u0011$$A\u0004sK\u001adWm\u0019;\n\u0005Q\n$\u0001C\"mCN\u001cH+Y4\t\u0011Y\u0002!\u0011!Q\u0001\n=\n\u0011b\u00197bgN$\u0016m\u001a\u0011\t\u000ba\u0002A\u0011A\u001d\u0002\rqJg.\u001b;?)\tQD\b\u0006\u0002\"w!)Qf\u000ea\u0002_!)1e\u000ea\u0001K!)a\b\u0001C!\u007f\u00059qO]1q%\u0012#ECA\u0011A\u0011\u0015\u0019S\b1\u0001&\u0011\u0015\u0011\u0005\u0001\"\u0001D\u0003\u0015\u0019\u0017m\u00195f)\u0005\t\u0003\"B#\u0001\t\u00031\u0015a\u00029feNL7\u000f\u001e\u000b\u0003C\u001dCQ\u0001\u0013#A\u0002%\u000b\u0001B\\3x\u0019\u00164X\r\u001c\t\u0003\u00156k\u0011a\u0013\u0006\u0003\u0019\u001a\tqa\u001d;pe\u0006<W-\u0003\u0002O\u0017\na1\u000b^8sC\u001e,G*\u001a<fY\")\u0001\u000b\u0001C\u0001\u0007\u0006IQO\u001c9feNL7\u000f\u001e\u0005\u0006!\u0002!\tA\u0015\u000b\u0003CMCQ\u0001V)A\u0002U\u000b\u0001B\u00197pG.Lgn\u001a\t\u00033YK!a\u0016\u000e\u0003\u000f\t{w\u000e\\3b]\")\u0011\f\u0001C\u0001\u0007\u0006AA-[:uS:\u001cG\u000fC\u0003Z\u0001\u0011\u00051\f\u0006\u0002\"9\")QL\u0017a\u0001=\u0006ia.^7QCJ$\u0018\u000e^5p]N\u0004\"!G0\n\u0005\u0001T\"aA%oi\")!\r\u0001C\u0001G\u00061a-\u001b7uKJ$\"!\t3\t\u000b\u0015\f\u0007\u0019\u00014\u0002\u0003\u0019\u0004Ba\u001a6\u0014Y6\t\u0001N\u0003\u0002j\u0005\u0005Aa-\u001e8di&|g.\u0003\u0002lQ\nAa)\u001e8di&|g\u000e\u0005\u0002nc6\taN\u0003\u0002pa\u0006!A.\u00198h\u0015\u0005\u0019\u0011BA,o\u0011\u0015\u0019\b\u0001\"\u0001u\u0003!\u0019w.\u00197fg\u000e,GCA\u0011v\u0011\u0015i&\u000f1\u0001_\u0011\u0015\u0019\b\u0001\"\u0001x)\r\t\u00030\u001f\u0005\u0006;Z\u0004\rA\u0018\u0005\u0006uZ\u0004\r!V\u0001\bg\",hM\u001a7f\u0011\u0015a\b\u0001\"\u0001~\u0003-\u0011X\r]1si&$\u0018n\u001c8\u0015\u0005\u0005r\b\"B/|\u0001\u0004q\u0006bBA\u0001\u0001\u0011\u0005\u00111A\u0001\u0007g\u0006l\u0007\u000f\\3\u0015\u000b\u0005\n)!!\u0003\t\r\u0005\u001dq\u00101\u0001V\u0003=9\u0018\u000e\u001e5SKBd\u0017mY3nK:$\bbBA\u0006\u007f\u0002\u0007\u0011QB\u0001\tMJ\f7\r^5p]B\u0019\u0011$a\u0004\n\u0007\u0005E!D\u0001\u0004E_V\u0014G.\u001a\u0005\b\u0003\u0003\u0001A\u0011AA\u000b)\u001d\t\u0013qCA\r\u00037Aq!a\u0002\u0002\u0014\u0001\u0007Q\u000b\u0003\u0005\u0002\f\u0005M\u0001\u0019AA\u0007\u0011!\ti\"a\u0005A\u0002\u0005}\u0011\u0001B:fK\u0012\u00042!GA\u0011\u0013\r\t\u0019C\u0007\u0002\u0005\u0019>tw\rC\u0004\u0002(\u0001!\t!!\u000b\u0002\u0017I\fg\u000eZ8n'Bd\u0017\u000e\u001e\u000b\u0005\u0003W\t\t\u0004\u0005\u0003\u001a\u0003[\t\u0013bAA\u00185\t)\u0011I\u001d:bs\"A\u00111GA\u0013\u0001\u0004\t)$A\u0004xK&<\u0007\u000e^:\u0011\u000be\ti#!\u0004\t\u000f\u0005\u001d\u0002\u0001\"\u0001\u0002:Q1\u00111FA\u001e\u0003{A\u0001\"a\r\u00028\u0001\u0007\u0011Q\u0007\u0005\t\u0003;\t9\u00041\u0001\u0002 !9\u0011\u0011\t\u0001\u0005\u0002\u0005\r\u0013!B;oS>tGcA\u0011\u0002F!9\u0011qIA \u0001\u0004\t\u0013!B8uQ\u0016\u0014\bbBA&\u0001\u0011\u0005\u0011QJ\u0001\rS:$XM]:fGRLwN\u001c\u000b\u0004C\u0005=\u0003bBA$\u0003\u0013\u0002\r!\t\u0005\b\u0003'\u0002A\u0011AA+\u0003!\u0019XO\u0019;sC\u000e$HcA\u0011\u0002X!9\u0011qIA)\u0001\u0004\t\u0003bBA*\u0001\u0011\u0005\u00111\f\u000b\u0006C\u0005u\u0013q\f\u0005\b\u0003\u000f\nI\u00061\u0001\"\u0011\u0019i\u0016\u0011\fa\u0001=\"9\u00111\u000b\u0001\u0005\u0002\u0005\rD#B\u0011\u0002f\u0005\u001d\u0004bBA$\u0003C\u0002\r!\t\u0005\t\u0003S\n\t\u00071\u0001\u0002l\u0005\t\u0001\u000f\u0005\u0003\u0002n\u0005=T\"\u0001\u0004\n\u0007\u0005EdAA\u0006QCJ$\u0018\u000e^5p]\u0016\u0014\bbBA;\u0001\u0011\u0005\u0013qO\u0001\ti>\u001cFO]5oOR\u0011\u0011\u0011\u0010\t\u0005\u0003w\n\tID\u0002\u001a\u0003{J1!a \u001b\u0003\u0019\u0001&/\u001a3fM&!\u00111QAC\u0005\u0019\u0019FO]5oO*\u0019\u0011q\u0010\u000e\t\u000f\u0005%\u0005\u0001\"\u0001\u0002\f\u000691/\u001a;OC6,GcA\u0011\u0002\u000e\"A\u0011qRAD\u0001\u0004\tI(\u0001\u0003oC6,\u0007bBAJ\u0001\u0011\u0005\u0011QS\u0001\u0007g>\u0014HOQ=\u0016\t\u0005]\u0015q\u0014\u000b\bC\u0005e\u00151UAT\u0011\u001d)\u0017\u0011\u0013a\u0001\u00037\u0003Ra\u001a6\u0014\u0003;\u00032\u0001FAP\t\u001d\t\t+!%C\u0002]\u0011\u0011a\u0015\u0005\b\u0003K\u000b\t\n1\u0001V\u0003%\t7oY3oI&tw\r\u0003\u0004^\u0003#\u0003\rAX\u0004\b\u0003W\u0013\u0001\u0012AAW\u0003\u001dQ\u0015M^1S\t\u0012\u00032\u0001EAX\r\u0019\t!\u0001#\u0001\u00022N1\u0011qVAZ\u0003s\u00032!GA[\u0013\r\t9L\u0007\u0002\u0007\u0003:L(+\u001a4\u0011\u0007e\tY,C\u0002\u0002>j\u0011AbU3sS\u0006d\u0017N_1cY\u0016Dq\u0001OAX\t\u0003\t\t\r\u0006\u0002\u0002.\"A\u0011QYAX\t\u0007\t9-A\u0004ge>l'\u000b\u0012#\u0016\t\u0005%\u0017\u0011\u001b\u000b\u0005\u0003\u0017\fI\u000e\u0006\u0003\u0002N\u0006M\u0007\u0003\u0002\t\u0001\u0003\u001f\u00042\u0001FAi\t\u00191\u00121\u0019b\u0001/!Q\u0011Q[Ab\u0003\u0003\u0005\u001d!a6\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007\u0005\u00031g\u0005=\u0007bB\u0012\u0002D\u0002\u0007\u00111\u001c\t\u0005M!\ny\r\u0003\u0005\u0002`\u0006=F1AAq\u0003\u0015!xN\u0015#E+\u0011\t\u0019/!;\u0015\t\u0005\u0015\u00181\u001e\t\u0005M!\n9\u000fE\u0002\u0015\u0003S$aAFAo\u0005\u00049\u0002bB\u0012\u0002^\u0002\u0007\u0011Q\u001e\t\u0005!\u0001\t9\u000f\u0003\u0006\u0002r\u0006=\u0016\u0011!C\u0005\u0003g\f1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u001f\t\u0004[\u0006]\u0018bAA}]\n1qJ\u00196fGR\u0004"
)
public class JavaRDD<T> extends AbstractJavaRDDLike<T, JavaRDD<T>> {
   private final RDD<T> rdd;
   private final ClassTag<T> classTag;

   public static <T> RDD<T> toRDD(JavaRDD<T> var0) {
      return JavaRDD$.MODULE$.<T>toRDD(var0);
   }

   public static <T> JavaRDD<T> fromRDD(RDD<T> var0, ClassTag<T> var1) {
      return JavaRDD$.MODULE$.<T>fromRDD(var0, var1);
   }

   public RDD<T> rdd() {
      return this.rdd;
   }

   public ClassTag<T> classTag() {
      return this.classTag;
   }

   public JavaRDD<T> wrapRDD(RDD<T> rdd) {
      return JavaRDD$.MODULE$.<T>fromRDD(rdd, this.classTag());
   }

   public JavaRDD<T> cache() {
      return this.wrapRDD(this.rdd().cache());
   }

   public JavaRDD<T> persist(StorageLevel newLevel) {
      return this.wrapRDD(this.rdd().persist(newLevel));
   }

   public JavaRDD<T> unpersist() {
      return this.wrapRDD(this.rdd().unpersist(this.rdd().unpersist$default$1()));
   }

   public JavaRDD<T> unpersist(boolean blocking) {
      return this.wrapRDD(this.rdd().unpersist(blocking));
   }

   public JavaRDD<T> distinct() {
      return this.wrapRDD(this.rdd().distinct());
   }

   public JavaRDD<T> distinct(int numPartitions) {
      Ordering x$2 = this.rdd().distinct$default$2(numPartitions);
      return this.wrapRDD(this.rdd().distinct(numPartitions, x$2));
   }

   public JavaRDD<T> filter(Function<T, Boolean> f) {
      return this.wrapRDD(this.rdd().filter(new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         private final Function f$1;

         public final boolean apply(T x) {
            return (Boolean)this.f$1.call(x);
         }

         public {
            this.f$1 = f$1;
         }
      }));
   }

   public JavaRDD<T> coalesce(int numPartitions) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      boolean x$4 = this.rdd().coalesce$default$2();
      Option x$5 = this.rdd().coalesce$default$3();
      Ordering x$6 = this.rdd().coalesce$default$4(numPartitions, x$4, x$5);
      return var10000.<T>fromRDD(this.rdd().coalesce(numPartitions, x$4, x$5, x$6), this.classTag());
   }

   public JavaRDD<T> coalesce(int numPartitions, boolean shuffle) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      Option x$9 = this.rdd().coalesce$default$3();
      Ordering x$10 = this.rdd().coalesce$default$4(numPartitions, shuffle, x$9);
      return var10000.<T>fromRDD(this.rdd().coalesce(numPartitions, shuffle, x$9, x$10), this.classTag());
   }

   public JavaRDD<T> repartition(int numPartitions) {
      JavaRDD$ var10000 = JavaRDD$.MODULE$;
      Ordering x$12 = this.rdd().repartition$default$2(numPartitions);
      return var10000.<T>fromRDD(this.rdd().repartition(numPartitions, x$12), this.classTag());
   }

   public JavaRDD<T> sample(boolean withReplacement, double fraction) {
      return this.sample(withReplacement, fraction, Utils$.MODULE$.random().nextLong());
   }

   public JavaRDD<T> sample(boolean withReplacement, double fraction, long seed) {
      return this.wrapRDD(this.rdd().sample(withReplacement, fraction, seed));
   }

   public JavaRDD<T>[] randomSplit(double[] weights) {
      return this.randomSplit(weights, Utils$.MODULE$.random().nextLong());
   }

   public JavaRDD<T>[] randomSplit(double[] weights, long seed) {
      return (JavaRDD[])Predef$.MODULE$.refArrayOps(this.rdd().randomSplit(weights, seed)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final JavaRDD $outer;

         public final JavaRDD<T> apply(RDD<T> rdd) {
            return this.$outer.wrapRDD(rdd);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(JavaRDD.class)));
   }

   public JavaRDD<T> union(JavaRDD<T> other) {
      return this.wrapRDD(this.rdd().union(other.rdd()));
   }

   public JavaRDD<T> intersection(JavaRDD<T> other) {
      return this.wrapRDD(this.rdd().intersection(other.rdd()));
   }

   public JavaRDD<T> subtract(JavaRDD<T> other) {
      return this.wrapRDD(this.rdd().subtract(JavaRDD$.MODULE$.toRDD(other)));
   }

   public JavaRDD<T> subtract(JavaRDD<T> other, int numPartitions) {
      return this.wrapRDD(this.rdd().subtract(JavaRDD$.MODULE$.toRDD(other), numPartitions));
   }

   public JavaRDD<T> subtract(JavaRDD<T> other, Partitioner p) {
      RDD x$13 = JavaRDD$.MODULE$.toRDD(other);
      Ordering x$15 = this.rdd().subtract$default$3(x$13, p);
      return this.wrapRDD(this.rdd().subtract(x$13, p, x$15));
   }

   public String toString() {
      return this.rdd().toString();
   }

   public JavaRDD<T> setName(String name) {
      this.rdd().setName(name);
      return this;
   }

   public <S> JavaRDD<T> sortBy(Function<T, S> f, boolean ascending, int numPartitions) {
      org.spark_project.guava.collect.Ordering ordering = org.spark_project.guava.collect.Ordering.natural();
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return this.wrapRDD(this.rdd().sortBy(this.fn$1(f), ascending, numPartitions, Ordering$.MODULE$.comparatorToOrdering(ordering), ctag));
   }

   private final Function1 fn$1(Function f$2) {
      return new Serializable(this, f$2) {
         public static final long serialVersionUID = 0L;
         private final Function f$2;

         public final S apply(T x) {
            return (S)this.f$2.call(x);
         }

         public {
            this.f$2 = f$2;
         }
      };
   }

   public JavaRDD(RDD<T> rdd, ClassTag<T> classTag) {
      this.rdd = rdd;
      this.classTag = classTag;
   }
}
