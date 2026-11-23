package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.reflect.ScalaSignature;
import scala.util.matching.Regex;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005}a!B\u0001\u0003\u0003CY!a\u0002\"m_\u000e\\\u0017\n\u001a\u0006\u0003\u0007\u0011\tqa\u001d;pe\u0006<WM\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g\u0011\u0015\u0019\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}Q\tQ\u0003\u0005\u0002\u0017\u00015\t!\u0001C\u0003\u0019\u0001\u0019\u0005\u0011$\u0001\u0003oC6,W#\u0001\u000e\u0011\u0005mqbBA\u0007\u001d\u0013\tib\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003?\u0001\u0012aa\u0015;sS:<'BA\u000f\u000f\u0011\u0015\u0011\u0003\u0001\"\u0001$\u0003\u001d\t7O\u0015#E\u0013\u0012,\u0012\u0001\n\t\u0004\u001b\u0015:\u0013B\u0001\u0014\u000f\u0005\u0019y\u0005\u000f^5p]B\u0011a\u0003K\u0005\u0003S\t\u0011!B\u0015#E\u00052|7m[%e\u0011\u0015Y\u0003\u0001\"\u0001-\u0003\u0015I7O\u0015#E+\u0005i\u0003CA\u0007/\u0013\tycBA\u0004C_>dW-\u00198\t\u000bE\u0002A\u0011\u0001\u0017\u0002\u0013%\u001c8\u000b[;gM2,\u0007\"B\u001a\u0001\t\u0003a\u0013aC5t\u0005J|\u0017\rZ2bgRDQ!\u000e\u0001\u0005BY\n\u0001\u0002^8TiJLgn\u001a\u000b\u00025%Z\u0001\u0001\u000f\u0015;yy\u0002%\t\u0012$I\u0013\tI$A\u0001\tCe>\fGmY1ti\ncwnY6JI&\u00111H\u0001\u0002\u000f'\",hM\u001a7f\u00052|7m[%e\u0013\ti$A\u0001\nTQV4g\r\\3ECR\f'\t\\8dW&#\u0017BA \u0003\u0005M\u0019\u0006.\u001e4gY\u0016Le\u000eZ3y\u00052|7m[%e\u0013\t\t%AA\u0007TiJ,\u0017-\u001c\"m_\u000e\\\u0017\nZ\u0005\u0003\u0007\n\u0011\u0011\u0003V1tWJ+7/\u001e7u\u00052|7m[%e\u0013\t)%A\u0001\tUK6\u0004Hj\\2bY\ncwnY6JI&\u0011qI\u0001\u0002\u0013)\u0016l\u0007o\u00155vM\u001adWM\u00117pG.LE-\u0003\u0002J\u0005\tYA+Z:u\u00052|7m[%eQ\t\u00011\n\u0005\u0002M\u001f6\tQJ\u0003\u0002O\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005Ak%\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!\u0002*\u0003\u0011\u0003\u0019\u0016a\u0002\"m_\u000e\\\u0017\n\u001a\t\u0003-Q3Q!\u0001\u0002\t\u0002U\u001b\"\u0001\u0016\u0007\t\u000bM!F\u0011A,\u0015\u0003MCq!\u0017+C\u0002\u0013\u0005!,A\u0002S\t\u0012+\u0012a\u0017\t\u00039\u0006l\u0011!\u0018\u0006\u0003=~\u000b\u0001\"\\1uG\"Lgn\u001a\u0006\u0003A:\tA!\u001e;jY&\u0011!-\u0018\u0002\u0006%\u0016<W\r\u001f\u0005\u0007IR\u0003\u000b\u0011B.\u0002\tI#E\t\t\u0005\bMR\u0013\r\u0011\"\u0001[\u0003\u001d\u0019\u0006*\u0016$G\u0019\u0016Ca\u0001\u001b+!\u0002\u0013Y\u0016\u0001C*I+\u001a3E*\u0012\u0011\t\u000f)$&\u0019!C\u00015\u0006a1\u000bS+G\r2+u\fR!U\u0003\"1A\u000e\u0016Q\u0001\nm\u000bQb\u0015%V\r\u001acUi\u0018#B)\u0006\u0003\u0003b\u00028U\u0005\u0004%\tAW\u0001\u000e'\"+fI\u0012'F?&sE)\u0012-\t\rA$\u0006\u0015!\u0003\\\u00039\u0019\u0006*\u0016$G\u0019\u0016{\u0016J\u0014#F1\u0002BqA\u001d+C\u0002\u0013\u0005!,A\u0005C%>\u000bEiQ!T)\"1A\u000f\u0016Q\u0001\nm\u000b!B\u0011*P\u0003\u0012\u001b\u0015i\u0015+!\u0011\u001d1HK1A\u0005\u0002i\u000b!\u0002V!T\u0017J+5+\u0016'U\u0011\u0019AH\u000b)A\u00057\u0006YA+Q*L%\u0016\u001bV\u000b\u0014+!\u0011\u001dQHK1A\u0005\u0002i\u000baa\u0015+S\u000b\u0006k\u0005B\u0002?UA\u0003%1,A\u0004T)J+\u0015)\u0014\u0011\t\u000fy$&\u0019!C\u00015\u0006QA+R'Q?2{5)\u0011'\t\u000f\u0005\u0005A\u000b)A\u00057\u0006YA+R'Q?2{5)\u0011'!\u0011!\t)\u0001\u0016b\u0001\n\u0003Q\u0016\u0001\u0004+F\u001bB{6\u000bS+G\r2+\u0005bBA\u0005)\u0002\u0006IaW\u0001\u000e)\u0016k\u0005kX*I+\u001a3E*\u0012\u0011\t\u0011\u00055AK1A\u0005\u0002i\u000bA\u0001V#T)\"9\u0011\u0011\u0003+!\u0002\u0013Y\u0016!\u0002+F'R\u0003\u0003bBA\u000b)\u0012\u0005\u0011qC\u0001\u0006CB\u0004H.\u001f\u000b\u0004+\u0005e\u0001B\u0002\r\u0002\u0014\u0001\u0007!\u0004\u000b\u0002U\u0017\"\u0012\u0011k\u0013"
)
public abstract class BlockId {
   public static BlockId apply(String var0) {
      return BlockId$.MODULE$.apply(var0);
   }

   public static Regex TEST() {
      return BlockId$.MODULE$.TEST();
   }

   public static Regex TEMP_SHUFFLE() {
      return BlockId$.MODULE$.TEMP_SHUFFLE();
   }

   public static Regex TEMP_LOCAL() {
      return BlockId$.MODULE$.TEMP_LOCAL();
   }

   public static Regex STREAM() {
      return BlockId$.MODULE$.STREAM();
   }

   public static Regex TASKRESULT() {
      return BlockId$.MODULE$.TASKRESULT();
   }

   public static Regex BROADCAST() {
      return BlockId$.MODULE$.BROADCAST();
   }

   public static Regex SHUFFLE_INDEX() {
      return BlockId$.MODULE$.SHUFFLE_INDEX();
   }

   public static Regex SHUFFLE_DATA() {
      return BlockId$.MODULE$.SHUFFLE_DATA();
   }

   public static Regex SHUFFLE() {
      return BlockId$.MODULE$.SHUFFLE();
   }

   public static Regex RDD() {
      return BlockId$.MODULE$.RDD();
   }

   public abstract String name();

   public Option<RDDBlockId> asRDDId() {
      return (Option<RDDBlockId>)(this.isRDD() ? new Some((RDDBlockId)this) : None$.MODULE$);
   }

   public boolean isRDD() {
      return this instanceof RDDBlockId;
   }

   public boolean isShuffle() {
      return this instanceof ShuffleBlockId;
   }

   public boolean isBroadcast() {
      return this instanceof BroadcastBlockId;
   }

   public String toString() {
      return this.name();
   }
}
