package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001I2Q!\u0001\u0002\u0001\t)\u0011QB\u00133cGB\u000b'\u000f^5uS>t'BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u00112#D\u0001\u0005\u0013\t!BAA\u0005QCJ$\u0018\u000e^5p]\"Aa\u0003\u0001B\u0001B\u0003%\u0001$A\u0002jIb\u001c\u0001\u0001\u0005\u0002\r3%\u0011!$\u0004\u0002\u0004\u0013:$\b\u0002\u0003\u000f\u0001\u0005\u000b\u0007I\u0011A\u000f\u0002\u000b1|w/\u001a:\u0016\u0003y\u0001\"\u0001D\u0010\n\u0005\u0001j!\u0001\u0002'p]\u001eD\u0001B\t\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0007Y><XM\u001d\u0011\t\u0011\u0011\u0002!Q1A\u0005\u0002u\tQ!\u001e9qKJD\u0001B\n\u0001\u0003\u0002\u0003\u0006IAH\u0001\u0007kB\u0004XM\u001d\u0011\t\u000b!\u0002A\u0011A\u0015\u0002\rqJg.\u001b;?)\u0011QC&\f\u0018\u0011\u0005-\u0002Q\"\u0001\u0002\t\u000bY9\u0003\u0019\u0001\r\t\u000bq9\u0003\u0019\u0001\u0010\t\u000b\u0011:\u0003\u0019\u0001\u0010\t\u000bA\u0002A\u0011I\u0019\u0002\u000b%tG-\u001a=\u0016\u0003a\u0001"
)
public class JdbcPartition implements Partition {
   private final int idx;
   private final long lower;
   private final long upper;

   // $FF: synthetic method
   public boolean org$apache$spark$Partition$$super$equals(Object x$1) {
      return super.equals(x$1);
   }

   public int hashCode() {
      return Partition$class.hashCode(this);
   }

   public boolean equals(Object other) {
      return Partition$class.equals(this, other);
   }

   public long lower() {
      return this.lower;
   }

   public long upper() {
      return this.upper;
   }

   public int index() {
      return this.idx;
   }

   public JdbcPartition(int idx, long lower, long upper) {
      this.idx = idx;
      this.lower = lower;
      this.upper = upper;
      Partition$class.$init$(this);
   }
}
