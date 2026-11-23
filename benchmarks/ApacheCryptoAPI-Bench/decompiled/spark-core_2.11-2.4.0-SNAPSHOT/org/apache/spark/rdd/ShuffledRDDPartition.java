package org.apache.spark.rdd;

import org.apache.spark.Partition;
import org.apache.spark.Partition$class;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001!2Q!\u0001\u0002\u0001\t)\u0011Ac\u00155vM\u001adW\r\u001a*E\tB\u000b'\u000f^5uS>t'BA\u0002\u0005\u0003\r\u0011H\r\u001a\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u00112#D\u0001\u0005\u0013\t!BAA\u0005QCJ$\u0018\u000e^5p]\"Aa\u0003\u0001BC\u0002\u0013\u0005\u0001$A\u0002jIb\u001c\u0001!F\u0001\u001a!\ta!$\u0003\u0002\u001c\u001b\t\u0019\u0011J\u001c;\t\u0011u\u0001!\u0011!Q\u0001\ne\tA!\u001b3yA!)q\u0004\u0001C\u0001A\u00051A(\u001b8jiz\"\"!I\u0012\u0011\u0005\t\u0002Q\"\u0001\u0002\t\u000bYq\u0002\u0019A\r\t\u000f\u0015\u0002!\u0019!C!1\u0005)\u0011N\u001c3fq\"1q\u0005\u0001Q\u0001\ne\ta!\u001b8eKb\u0004\u0003"
)
public class ShuffledRDDPartition implements Partition {
   private final int idx;
   private final int index;

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

   public int idx() {
      return this.idx;
   }

   public int index() {
      return this.index;
   }

   public ShuffledRDDPartition(int idx) {
      this.idx = idx;
      Partition$class.$init$(this);
      this.index = idx;
   }
}
