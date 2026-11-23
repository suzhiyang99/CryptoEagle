package org.apache.spark.rdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.util.NextIterator;
import scala.Function0;
import scala.Function1;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.TraversableOnce;
import scala.collection.immutable.IndexedSeq$;
import scala.collection.immutable.Nil$;
import scala.math.BigInt;
import scala.math.BigInt$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mh\u0001B\u0001\u0003\u0001-\u0011qA\u00133cGJ#EI\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001!\u0006\u0002\r'M\u0019\u0001!D\u0010\u0011\u00079y\u0011#D\u0001\u0003\u0013\t\u0001\"AA\u0002S\t\u0012\u0003\"AE\n\r\u0001\u0011)A\u0003\u0001b\u0001+\t\tA+\u0005\u0002\u00179A\u0011qCG\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t9aj\u001c;iS:<\u0007CA\f\u001e\u0013\tq\u0002DA\u0002B]f\u0004\"\u0001I\u0012\u000e\u0003\u0005R!A\t\u0003\u0002\u0011%tG/\u001a:oC2L!\u0001J\u0011\u0003\u000f1{wmZ5oO\"Aa\u0005\u0001B\u0001B\u0003%q%\u0001\u0002tGB\u0011\u0001&K\u0007\u0002\t%\u0011!\u0006\u0002\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\tY\u0001\u0011\t\u0011)A\u0005[\u0005iq-\u001a;D_:tWm\u0019;j_:\u00042a\u0006\u00181\u0013\ty\u0003DA\u0005Gk:\u001cG/[8oaA\u0011\u0011GN\u0007\u0002e)\u00111\u0007N\u0001\u0004gFd'\"A\u001b\u0002\t)\fg/Y\u0005\u0003oI\u0012!bQ8o]\u0016\u001cG/[8o\u0011!\u0019\u0004A!A!\u0002\u0013I\u0004C\u0001\u001e>\u001d\t92(\u0003\u0002=1\u00051\u0001K]3eK\u001aL!AP \u0003\rM#(/\u001b8h\u0015\ta\u0004\u0004\u0003\u0005B\u0001\t\u0005\t\u0015!\u0003C\u0003)awn^3s\u0005>,h\u000e\u001a\t\u0003/\rK!\u0001\u0012\r\u0003\t1{gn\u001a\u0005\t\r\u0002\u0011\t\u0011)A\u0005\u0005\u0006QQ\u000f\u001d9fe\n{WO\u001c3\t\u0011!\u0003!\u0011!Q\u0001\n%\u000bQB\\;n!\u0006\u0014H/\u001b;j_:\u001c\bCA\fK\u0013\tY\u0005DA\u0002J]RD\u0001\"\u0014\u0001\u0003\u0002\u0003\u0006IAT\u0001\u0007[\u0006\u0004(k\\<\u0011\t]y\u0015+E\u0005\u0003!b\u0011\u0011BR;oGRLwN\\\u0019\u0011\u0005E\u0012\u0016BA*3\u0005%\u0011Vm];miN+G\u000f\u0003\u0005V\u0001\t\r\t\u0015a\u0003W\u0003))g/\u001b3f]\u000e,G%\r\t\u0004/j\u000bR\"\u0001-\u000b\u0005eC\u0012a\u0002:fM2,7\r^\u0005\u00037b\u0013\u0001b\u00117bgN$\u0016m\u001a\u0005\u0006;\u0002!\tAX\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0011}\u00137\rZ3gO\"$\"\u0001Y1\u0011\u00079\u0001\u0011\u0003C\u0003V9\u0002\u000fa\u000bC\u0003'9\u0002\u0007q\u0005C\u0003-9\u0002\u0007Q\u0006C\u000349\u0002\u0007\u0011\bC\u0003B9\u0002\u0007!\tC\u0003G9\u0002\u0007!\tC\u0003I9\u0002\u0007\u0011\nC\u0004N9B\u0005\t\u0019\u0001(\t\u000b)\u0004A\u0011I6\u0002\u001b\u001d,G\u000fU1si&$\u0018n\u001c8t+\u0005a\u0007cA\fn_&\u0011a\u000e\u0007\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003QAL!!\u001d\u0003\u0003\u0013A\u000b'\u000f^5uS>t\u0007\"B:\u0001\t\u0003\"\u0018aB2p[B,H/\u001a\u000b\u0006k\u0006\r\u0011q\u0001\t\u0004mz\fbBA<}\u001d\tA80D\u0001z\u0015\tQ(\"\u0001\u0004=e>|GOP\u0005\u00023%\u0011Q\u0010G\u0001\ba\u0006\u001c7.Y4f\u0013\ry\u0018\u0011\u0001\u0002\t\u0013R,'/\u0019;pe*\u0011Q\u0010\u0007\u0005\u0007\u0003\u000b\u0011\b\u0019A8\u0002\u000fQDW\rU1si\"9\u0011\u0011\u0002:A\u0002\u0005-\u0011aB2p]R,\u0007\u0010\u001e\t\u0004Q\u00055\u0011bAA\b\t\tYA+Y:l\u0007>tG/\u001a=u\u000f\u001d\t\u0019B\u0001E\u0001\u0003+\tqA\u00133cGJ#E\tE\u0002\u000f\u0003/1a!\u0001\u0002\t\u0002\u0005e1CBA\f\u00037\t\t\u0003E\u0002\u0018\u0003;I1!a\b\u0019\u0005\u0019\te.\u001f*fMB\u0019q#a\t\n\u0007\u0005\u0015\u0002D\u0001\u0007TKJL\u0017\r\\5{C\ndW\rC\u0004^\u0003/!\t!!\u000b\u0015\u0005\u0005U\u0001\u0002CA\u0017\u0003/!\t!a\f\u0002-I,7/\u001e7u'\u0016$Hk\\(cU\u0016\u001cG/\u0011:sCf$B!!\r\u0002@A!q#\\A\u001a!\u0011\t)$a\u000f\u000e\u0005\u0005]\"bAA\u001di\u0005!A.\u00198h\u0013\u0011\ti$a\u000e\u0003\r=\u0013'.Z2u\u0011\u001d\t\t%a\u000bA\u0002E\u000b!A]:\u0007\u0015\u0005\u0015\u0013q\u0003I\u0001$\u0003\t9EA\tD_:tWm\u0019;j_:4\u0015m\u0019;pef\u001cb!a\u0011\u0002\u001c\u0005\u0005\u0002b\u0002\u0017\u0002D\u0019\u0005\u00111J\u000b\u0002a!2\u0011\u0011JA(\u00037\u0002RaFA)\u0003+J1!a\u0015\u0019\u0005\u0019!\bN]8xgB\u0019a/a\u0016\n\t\u0005e\u0013\u0011\u0001\u0002\n\u000bb\u001cW\r\u001d;j_:\fdAH\u001d\u0002^\u0005\u0005\u0015'C\u0012\u0002`\u0005\u0015\u0014qOA4+\u0011\t\t'a\u0019\u0016\u0003e\"a\u0001\u0006\u0006C\u0002\u00055\u0014\u0002BA4\u0003S\n1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\n$bAA61\u00051A\u000f\u001b:poN\f2AFA8!\u0011\t\t(a\u001d\u000f\u0005]a\u0018\u0002BA;\u0003\u0003\u0011\u0011\u0002\u00165s_^\f'\r\\32\u0013\r\nI(a\u001f\u0002~\u0005-dbA\f\u0002|%\u0019\u00111\u000e\r2\u000b\t:\u0002$a \u0003\u000bM\u001c\u0017\r\\12\u0007\u0019\n)\u0006\u0003\u0005\u0002\u0006\u0006]A\u0011AAD\u0003\u0019\u0019'/Z1uKV!\u0011\u0011RAN)A\tY)!(\u0002&\u00065\u0016qVAY\u0003g\u000b)\f\u0005\u0004\u0002\u000e\u0006U\u0015\u0011T\u0007\u0003\u0003\u001fS1!NAI\u0015\r\t\u0019\nB\u0001\u0004CBL\u0017\u0002BAL\u0003\u001f\u0013qAS1wCJ#E\tE\u0002\u0013\u00037#a\u0001FAB\u0005\u0004)\u0002b\u0002\u0014\u0002\u0004\u0002\u0007\u0011q\u0014\t\u0005\u0003\u001b\u000b\t+\u0003\u0003\u0002$\u0006=%\u0001\u0005&bm\u0006\u001c\u0006/\u0019:l\u0007>tG/\u001a=u\u0011!\t9+a!A\u0002\u0005%\u0016!E2p]:,7\r^5p]\u001a\u000b7\r^8ssB!\u00111VA\"\u001b\t\t9\u0002\u0003\u00044\u0003\u0007\u0003\r!\u000f\u0005\u0007\u0003\u0006\r\u0005\u0019\u0001\"\t\r\u0019\u000b\u0019\t1\u0001C\u0011\u0019A\u00151\u0011a\u0001\u0013\"9Q*a!A\u0002\u0005]\u0006cBA]\u0003\u007f\u000b\u0016\u0011T\u0007\u0003\u0003wSA!!0\u0002\u0010\u0006Aa-\u001e8di&|g.\u0003\u0003\u0002B\u0006m&\u0001\u0003$v]\u000e$\u0018n\u001c8\t\u0011\u0005\u0015\u0015q\u0003C\u0001\u0003\u000b$b\"a2\u0002J\u0006-\u0017QZAh\u0003#\f\u0019\u000e\u0005\u0004\u0002\u000e\u0006U\u0015\u0011\u0007\u0005\bM\u0005\r\u0007\u0019AAP\u0011!\t9+a1A\u0002\u0005%\u0006BB\u001a\u0002D\u0002\u0007\u0011\b\u0003\u0004B\u0003\u0007\u0004\rA\u0011\u0005\u0007\r\u0006\r\u0007\u0019\u0001\"\t\r!\u000b\u0019\r1\u0001J\u0011)\t9.a\u0006\u0012\u0002\u0013\u0005\u0011\u0011\\\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001c\u0016\t\u0005m\u00171_\u000b\u0003\u0003;TC!a8\u0002bB)qcT)\u00022-\u0012\u00111\u001d\t\u0005\u0003K\fy/\u0004\u0002\u0002h*!\u0011\u0011^Av\u0003%)hn\u00195fG.,GMC\u0002\u0002nb\t!\"\u00198o_R\fG/[8o\u0013\u0011\t\t0a:\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r\u0002\u0004\u0015\u0003+\u0014\r!\u0006\u0005\u000b\u0003o\f9\"!A\u0005\n\u0005e\u0018a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\r"
)
public class JdbcRDD<T> extends RDD<T> {
   public final Function0<Connection> org$apache$spark$rdd$JdbcRDD$$getConnection;
   public final String org$apache$spark$rdd$JdbcRDD$$sql;
   public final long org$apache$spark$rdd$JdbcRDD$$lowerBound;
   private final long upperBound;
   public final int org$apache$spark$rdd$JdbcRDD$$numPartitions;
   public final Function1<ResultSet, T> org$apache$spark$rdd$JdbcRDD$$mapRow;

   public static <T> Function1<ResultSet, Object[]> $lessinit$greater$default$7() {
      return JdbcRDD$.MODULE$.$lessinit$greater$default$7();
   }

   public static JavaRDD<Object[]> create(JavaSparkContext var0, ConnectionFactory var1, String var2, long var3, long var5, int var7) {
      return JdbcRDD$.MODULE$.create(var0, var1, var2, var3, var5, var7);
   }

   public static <T> JavaRDD<T> create(JavaSparkContext var0, ConnectionFactory var1, String var2, long var3, long var5, int var7, Function<ResultSet, T> var8) {
      return JdbcRDD$.MODULE$.<T>create(var0, var1, var2, var3, var5, var7, var8);
   }

   public static Object[] resultSetToObjectArray(ResultSet var0) {
      return JdbcRDD$.MODULE$.resultSetToObjectArray(var0);
   }

   public Partition[] getPartitions() {
      BigInt length = scala.package$.MODULE$.BigInt().apply(1).$plus(BigInt$.MODULE$.long2bigInt(this.upperBound)).$minus(BigInt$.MODULE$.long2bigInt(this.org$apache$spark$rdd$JdbcRDD$$lowerBound));
      return (Partition[])((TraversableOnce)RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.org$apache$spark$rdd$JdbcRDD$$numPartitions).map(new Serializable(this, length) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final JdbcRDD $outer;
         private final BigInt length$1;

         public final JdbcPartition apply(int i) {
            BigInt start = BigInt$.MODULE$.long2bigInt(this.$outer.org$apache$spark$rdd$JdbcRDD$$lowerBound).$plus(BigInt$.MODULE$.int2bigInt(i).$times(this.length$1).$div(BigInt$.MODULE$.int2bigInt(this.$outer.org$apache$spark$rdd$JdbcRDD$$numPartitions)));
            BigInt end = BigInt$.MODULE$.long2bigInt(this.$outer.org$apache$spark$rdd$JdbcRDD$$lowerBound).$plus(BigInt$.MODULE$.int2bigInt(i + 1).$times(this.length$1).$div(BigInt$.MODULE$.int2bigInt(this.$outer.org$apache$spark$rdd$JdbcRDD$$numPartitions))).$minus(BigInt$.MODULE$.int2bigInt(1));
            return new JdbcPartition(i, start.toLong(), end.toLong());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.length$1 = length$1;
            }
         }
      }, IndexedSeq$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply(Partition.class));
   }

   public Iterator<T> compute(Partition thePart, TaskContext context) {
      return new NextIterator<T>(this, thePart, context) {
         private final JdbcPartition part;
         private final Connection conn;
         private final PreparedStatement org$apache$spark$rdd$JdbcRDD$$anon$$stmt;
         private final String url;
         private final ResultSet rs;
         // $FF: synthetic field
         private final JdbcRDD $outer;

         private JdbcPartition part() {
            return this.part;
         }

         private Connection conn() {
            return this.conn;
         }

         public PreparedStatement org$apache$spark$rdd$JdbcRDD$$anon$$stmt() {
            return this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt;
         }

         private String url() {
            return this.url;
         }

         private ResultSet rs() {
            return this.rs;
         }

         public T getNext() {
            Object var10000;
            if (this.rs().next()) {
               var10000 = this.$outer.org$apache$spark$rdd$JdbcRDD$$mapRow.apply(this.rs());
            } else {
               this.finished_$eq(true);
               var10000 = null;
            }

            return (T)var10000;
         }

         public void close() {
            try {
               if (this.rs() != null) {
                  this.rs().close();
               }
            } catch (Exception var6) {
               this.$outer.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Exception closing resultset";
                  }
               }, var6);
            }

            try {
               if (this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt() != null) {
                  this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().close();
               }
            } catch (Exception var5) {
               this.$outer.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Exception closing statement";
                  }
               }, var5);
            }

            try {
               if (this.conn() != null) {
                  this.conn().close();
               }

               this.$outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "closed connection";
                  }
               });
            } catch (Exception var4) {
               this.$outer.logWarning(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return "Exception closing connection";
                  }
               }, var4);
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               context$1.addTaskCompletionListener((Function1)(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final void apply(TaskContext context) {
                     this.$outer.closeIfNeeded();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }));
               this.part = (JdbcPartition)thePart$1;
               this.conn = $outer.org$apache$spark$rdd$JdbcRDD$$getConnection.apply();
               this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt = this.conn().prepareStatement($outer.org$apache$spark$rdd$JdbcRDD$$sql, 1003, 1007);
               this.url = this.conn().getMetaData().getURL();
               if (this.url().startsWith("jdbc:mysql:")) {
                  this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().setFetchSize(Integer.MIN_VALUE);
               } else {
                  this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().setFetchSize(100);
               }

               $outer.logInfo(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"statement fetch size set to: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().getFetchSize())}));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().setLong(1, this.part().lower());
               this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().setLong(2, this.part().upper());
               this.rs = this.org$apache$spark$rdd$JdbcRDD$$anon$$stmt().executeQuery();
            }
         }
      };
   }

   public JdbcRDD(SparkContext sc, Function0<Connection> getConnection, String sql, long lowerBound, long upperBound, int numPartitions, Function1<ResultSet, T> mapRow, ClassTag<T> evidence$1) {
      super(sc, Nil$.MODULE$, evidence$1);
      this.org$apache$spark$rdd$JdbcRDD$$getConnection = getConnection;
      this.org$apache$spark$rdd$JdbcRDD$$sql = sql;
      this.org$apache$spark$rdd$JdbcRDD$$lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.org$apache$spark$rdd$JdbcRDD$$numPartitions = numPartitions;
      this.org$apache$spark$rdd$JdbcRDD$$mapRow = mapRow;
   }

   public interface ConnectionFactory extends Serializable {
      Connection getConnection() throws Exception;
   }
}
