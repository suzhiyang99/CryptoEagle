package org.apache.spark.storage.memory;

import org.apache.spark.memory.MemoryMode;
import org.apache.spark.util.io.ChunkedByteBuffer;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ef\u0001B\u0001\u0003\t6\u0011QcU3sS\u0006d\u0017N_3e\u001b\u0016lwN]=F]R\u0014\u0018P\u0003\u0002\u0004\t\u00051Q.Z7pefT!!\u0002\u0004\u0002\u000fM$xN]1hK*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00059Y2#\u0002\u0001\u0010+\u0011:\u0003C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\rE\u0002\u0017/ei\u0011AA\u0005\u00031\t\u00111\"T3n_JLXI\u001c;ssB\u0011!d\u0007\u0007\u0001\t\u0015a\u0002A1\u0001\u001e\u0005\u0005!\u0016C\u0001\u0010\"!\t\u0001r$\u0003\u0002!#\t9aj\u001c;iS:<\u0007C\u0001\t#\u0013\t\u0019\u0013CA\u0002B]f\u0004\"\u0001E\u0013\n\u0005\u0019\n\"a\u0002)s_\u0012,8\r\u001e\t\u0003!!J!!K\t\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011-\u0002!Q3A\u0005\u00021\naAY;gM\u0016\u0014X#A\u0017\u0011\u00059\u001aT\"A\u0018\u000b\u0005A\n\u0014AA5p\u0015\t\u0011d!\u0001\u0003vi&d\u0017B\u0001\u001b0\u0005E\u0019\u0005.\u001e8lK\u0012\u0014\u0015\u0010^3Ck\u001a4WM\u001d\u0005\tm\u0001\u0011\t\u0012)A\u0005[\u00059!-\u001e4gKJ\u0004\u0003\u0002\u0003\u001d\u0001\u0005+\u0007I\u0011A\u001d\u0002\u00155,Wn\u001c:z\u001b>$W-F\u0001;!\tYT(D\u0001=\u0015\t\u0019a!\u0003\u0002?y\tQQ*Z7peflu\u000eZ3\t\u0011\u0001\u0003!\u0011#Q\u0001\ni\n1\"\\3n_JLXj\u001c3fA!A!\t\u0001BK\u0002\u0013\u00051)\u0001\u0005dY\u0006\u001c8\u000fV1h+\u0005!\u0005cA#I35\taI\u0003\u0002H#\u00059!/\u001a4mK\u000e$\u0018BA%G\u0005!\u0019E.Y:t)\u0006<\u0007\u0002C&\u0001\u0005#\u0005\u000b\u0011\u0002#\u0002\u0013\rd\u0017m]:UC\u001e\u0004\u0003\"B'\u0001\t\u0003q\u0015A\u0002\u001fj]&$h\b\u0006\u0003P!F\u0013\u0006c\u0001\f\u00013!)1\u0006\u0014a\u0001[!)\u0001\b\u0014a\u0001u!)!\t\u0014a\u0001\t\")A\u000b\u0001C\u0001+\u0006!1/\u001b>f+\u00051\u0006C\u0001\tX\u0013\tA\u0016C\u0001\u0003M_:<\u0007b\u0002.\u0001\u0003\u0003%\taW\u0001\u0005G>\u0004\u00180\u0006\u0002]?R!Q\fY1c!\r1\u0002A\u0018\t\u00035}#Q\u0001H-C\u0002uAqaK-\u0011\u0002\u0003\u0007Q\u0006C\u000493B\u0005\t\u0019\u0001\u001e\t\u000f\tK\u0006\u0013!a\u0001GB\u0019Q\t\u00130\t\u000f\u0015\u0004\u0011\u0013!C\u0001M\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nTCA4s+\u0005A'FA\u0017jW\u0005Q\u0007CA6q\u001b\u0005a'BA7o\u0003%)hn\u00195fG.,GM\u0003\u0002p#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005Ed'!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u0012)A\u0004\u001ab\u0001;!9A\u000fAI\u0001\n\u0003)\u0018AD2paf$C-\u001a4bk2$HEM\u000b\u0003mb,\u0012a\u001e\u0016\u0003u%$Q\u0001H:C\u0002uAqA\u001f\u0001\u0012\u0002\u0013\u000510\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005qtX#A?+\u0005\u0011KG!\u0002\u000fz\u0005\u0004i\u0002\"CA\u0001\u0001\u0005\u0005I\u0011IA\u0002\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\u0001\t\u0005\u0003\u000f\t\t\"\u0004\u0002\u0002\n)!\u00111BA\u0007\u0003\u0011a\u0017M\\4\u000b\u0005\u0005=\u0011\u0001\u00026bm\u0006LA!a\u0005\u0002\n\t11\u000b\u001e:j]\u001eD\u0011\"a\u0006\u0001\u0003\u0003%\t!!\u0007\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0005\u0005m\u0001c\u0001\t\u0002\u001e%\u0019\u0011qD\t\u0003\u0007%sG\u000fC\u0005\u0002$\u0001\t\t\u0011\"\u0001\u0002&\u0005q\u0001O]8ek\u000e$X\t\\3nK:$HcA\u0011\u0002(!Q\u0011\u0011FA\u0011\u0003\u0003\u0005\r!a\u0007\u0002\u0007a$\u0013\u0007C\u0005\u0002.\u0001\t\t\u0011\"\u0011\u00020\u0005y\u0001O]8ek\u000e$\u0018\n^3sCR|'/\u0006\u0002\u00022A)\u00111GA\u001dC5\u0011\u0011Q\u0007\u0006\u0004\u0003o\t\u0012AC2pY2,7\r^5p]&!\u00111HA\u001b\u0005!IE/\u001a:bi>\u0014\b\"CA \u0001\u0005\u0005I\u0011AA!\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BA\"\u0003\u0013\u00022\u0001EA#\u0013\r\t9%\u0005\u0002\b\u0005>|G.Z1o\u0011%\tI#!\u0010\u0002\u0002\u0003\u0007\u0011\u0005C\u0005\u0002N\u0001\t\t\u0011\"\u0011\u0002P\u0005A\u0001.Y:i\u0007>$W\r\u0006\u0002\u0002\u001c!I\u00111\u000b\u0001\u0002\u0002\u0013\u0005\u0013QK\u0001\ti>\u001cFO]5oOR\u0011\u0011Q\u0001\u0005\n\u00033\u0002\u0011\u0011!C!\u00037\na!Z9vC2\u001cH\u0003BA\"\u0003;B\u0011\"!\u000b\u0002X\u0005\u0005\t\u0019A\u0011\b\u0013\u0005\u0005$!!A\t\n\u0005\r\u0014!F*fe&\fG.\u001b>fI6+Wn\u001c:z\u000b:$(/\u001f\t\u0004-\u0005\u0015d\u0001C\u0001\u0003\u0003\u0003EI!a\u001a\u0014\t\u0005\u0015tb\n\u0005\b\u001b\u0006\u0015D\u0011AA6)\t\t\u0019\u0007\u0003\u0006\u0002T\u0005\u0015\u0014\u0011!C#\u0003+B!\"!\u001d\u0002f\u0005\u0005I\u0011QA:\u0003\u0015\t\u0007\u000f\u001d7z+\u0011\t)(a\u001f\u0015\u0011\u0005]\u0014QPA@\u0003\u0003\u0003BA\u0006\u0001\u0002zA\u0019!$a\u001f\u0005\rq\tyG1\u0001\u001e\u0011\u0019Y\u0013q\u000ea\u0001[!1\u0001(a\u001cA\u0002iBqAQA8\u0001\u0004\t\u0019\t\u0005\u0003F\u0011\u0006e\u0004BCAD\u0003K\n\t\u0011\"!\u0002\n\u00069QO\\1qa2LX\u0003BAF\u0003;#B!!$\u0002 B)\u0001#a$\u0002\u0014&\u0019\u0011\u0011S\t\u0003\r=\u0003H/[8o!\u001d\u0001\u0012QS\u0017;\u00033K1!a&\u0012\u0005\u0019!V\u000f\u001d7fgA!Q\tSAN!\rQ\u0012Q\u0014\u0003\u00079\u0005\u0015%\u0019A\u000f\t\u0015\u0005\u0005\u0016QQA\u0001\u0002\u0004\t\u0019+A\u0002yIA\u0002BA\u0006\u0001\u0002\u001c\"Q\u0011qUA3\u0003\u0003%I!!+\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003W\u0003B!a\u0002\u0002.&!\u0011qVA\u0005\u0005\u0019y%M[3di\u0002"
)
public class SerializedMemoryEntry<T> implements MemoryEntry<T>, Product, Serializable {
   private final ChunkedByteBuffer buffer;
   private final MemoryMode memoryMode;
   private final ClassTag<T> classTag;

   public static <T> Option<Tuple3<ChunkedByteBuffer, MemoryMode, ClassTag<T>>> unapply(SerializedMemoryEntry<T> var0) {
      return SerializedMemoryEntry$.MODULE$.unapply(var0);
   }

   public static <T> SerializedMemoryEntry<T> apply(ChunkedByteBuffer var0, MemoryMode var1, ClassTag<T> var2) {
      return SerializedMemoryEntry$.MODULE$.<T>apply(var0, var1, var2);
   }

   public ChunkedByteBuffer buffer() {
      return this.buffer;
   }

   public MemoryMode memoryMode() {
      return this.memoryMode;
   }

   public ClassTag<T> classTag() {
      return this.classTag;
   }

   public long size() {
      return this.buffer().size();
   }

   public <T> SerializedMemoryEntry<T> copy(ChunkedByteBuffer buffer, MemoryMode memoryMode, ClassTag<T> classTag) {
      return new SerializedMemoryEntry<T>(buffer, memoryMode, classTag);
   }

   public <T> ChunkedByteBuffer copy$default$1() {
      return this.buffer();
   }

   public <T> MemoryMode copy$default$2() {
      return this.memoryMode();
   }

   public <T> ClassTag<T> copy$default$3() {
      return this.classTag();
   }

   public String productPrefix() {
      return "SerializedMemoryEntry";
   }

   public int productArity() {
      return 3;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.buffer();
            break;
         case 1:
            var10000 = this.memoryMode();
            break;
         case 2:
            var10000 = this.classTag();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof SerializedMemoryEntry;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var11;
      if (this != x$1) {
         label71: {
            boolean var3;
            if (x$1 instanceof SerializedMemoryEntry) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label53: {
                  label62: {
                     SerializedMemoryEntry var4 = (SerializedMemoryEntry)x$1;
                     ChunkedByteBuffer var10000 = this.buffer();
                     ChunkedByteBuffer var5 = var4.buffer();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label62;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label62;
                     }

                     MemoryMode var8 = this.memoryMode();
                     MemoryMode var6 = var4.memoryMode();
                     if (var8 == null) {
                        if (var6 != null) {
                           break label62;
                        }
                     } else if (!var8.equals(var6)) {
                        break label62;
                     }

                     ClassTag var9 = this.classTag();
                     ClassTag var7 = var4.classTag();
                     if (var9 == null) {
                        if (var7 != null) {
                           break label62;
                        }
                     } else if (!var9.equals(var7)) {
                        break label62;
                     }

                     if (var4.canEqual(this)) {
                        var11 = true;
                        break label53;
                     }
                  }

                  var11 = false;
               }

               if (var11) {
                  break label71;
               }
            }

            var11 = false;
            return var11;
         }
      }

      var11 = true;
      return var11;
   }

   public SerializedMemoryEntry(ChunkedByteBuffer buffer, MemoryMode memoryMode, ClassTag<T> classTag) {
      this.buffer = buffer;
      this.memoryMode = memoryMode;
      this.classTag = classTag;
      Product$class.$init$(this);
   }
}
