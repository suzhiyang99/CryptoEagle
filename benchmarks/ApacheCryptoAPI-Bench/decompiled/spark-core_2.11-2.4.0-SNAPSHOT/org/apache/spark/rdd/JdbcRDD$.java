package org.apache.spark.rdd;

import java.sql.Connection;
import java.sql.ResultSet;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext$;
import org.apache.spark.api.java.function.Function;
import scala.Array$;
import scala.Function1;
import scala.Serializable;
import scala.reflect.ClassTag$;

public final class JdbcRDD$ implements Serializable {
   public static final JdbcRDD$ MODULE$;

   static {
      new JdbcRDD$();
   }

   public Object[] resultSetToObjectArray(ResultSet rs) {
      return Array$.MODULE$.tabulate(rs.getMetaData().getColumnCount(), new Serializable(rs) {
         public static final long serialVersionUID = 0L;
         private final ResultSet rs$1;

         public final Object apply(int i) {
            return this.rs$1.getObject(i + 1);
         }

         public {
            this.rs$1 = rs$1;
         }
      }, ClassTag$.MODULE$.Object());
   }

   public <T> JavaRDD<T> create(JavaSparkContext sc, JdbcRDD.ConnectionFactory connectionFactory, String sql, long lowerBound, long upperBound, int numPartitions, Function<ResultSet, T> mapRow) {
      JdbcRDD jdbcRDD = new JdbcRDD(sc.sc(), new Serializable(connectionFactory) {
         public static final long serialVersionUID = 0L;
         private final JdbcRDD.ConnectionFactory connectionFactory$1;

         public final Connection apply() {
            return this.connectionFactory$1.getConnection();
         }

         public {
            this.connectionFactory$1 = connectionFactory$1;
         }
      }, sql, lowerBound, upperBound, numPartitions, new Serializable(mapRow) {
         public static final long serialVersionUID = 0L;
         private final Function mapRow$1;

         public final T apply(ResultSet resultSet) {
            return (T)this.mapRow$1.call(resultSet);
         }

         public {
            this.mapRow$1 = mapRow$1;
         }
      }, JavaSparkContext$.MODULE$.fakeClassTag());
      return new JavaRDD<T>(jdbcRDD, JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public JavaRDD<Object[]> create(JavaSparkContext sc, JdbcRDD.ConnectionFactory connectionFactory, String sql, long lowerBound, long upperBound, int numPartitions) {
      Function mapRow = new Function<ResultSet, Object[]>() {
         public Object[] call(ResultSet resultSet) {
            return JdbcRDD$.MODULE$.resultSetToObjectArray(resultSet);
         }
      };
      return this.<Object[]>create(sc, connectionFactory, sql, lowerBound, upperBound, numPartitions, mapRow);
   }

   public <T> Function1<ResultSet, Object[]> $lessinit$greater$default$7() {
      return new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Object[] apply(ResultSet rs) {
            return JdbcRDD$.MODULE$.resultSetToObjectArray(rs);
         }
      };
   }

   private Object readResolve() {
      return MODULE$;
   }

   private JdbcRDD$() {
      MODULE$ = this;
   }
}
