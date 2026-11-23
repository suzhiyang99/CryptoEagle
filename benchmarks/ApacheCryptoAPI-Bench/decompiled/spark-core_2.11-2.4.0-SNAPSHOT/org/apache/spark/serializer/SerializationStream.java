package org.apache.spark.serializer;

import java.io.Closeable;
import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Iterator;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001m4Q!\u0001\u0002\u0002\u0002-\u00111cU3sS\u0006d\u0017N_1uS>t7\u000b\u001e:fC6T!a\u0001\u0003\u0002\u0015M,'/[1mSj,'O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\u0007\u0015!\ti!#D\u0001\u000f\u0015\ty\u0001#\u0001\u0003mC:<'\"A\t\u0002\t)\fg/Y\u0005\u0003'9\u0011aa\u00142kK\u000e$\bCA\u000b\u0019\u001b\u00051\"BA\f\u0011\u0003\tIw.\u0003\u0002\u001a-\tI1\t\\8tK\u0006\u0014G.\u001a\u0005\u00067\u0001!\t\u0001H\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003u\u0001\"A\b\u0001\u000e\u0003\tAQ\u0001\t\u0001\u0007\u0002\u0005\n1b\u001e:ji\u0016|%M[3diV\u0011!\u0005\r\u000b\u0003Gi\"\"!\b\u0013\t\u000f\u0015z\u0012\u0011!a\u0002M\u0005QQM^5eK:\u001cW\r\n\u001b\u0011\u0007\u001dbc&D\u0001)\u0015\tI#&A\u0004sK\u001adWm\u0019;\u000b\u0003-\nQa]2bY\u0006L!!\f\u0015\u0003\u0011\rc\u0017m]:UC\u001e\u0004\"a\f\u0019\r\u0001\u0011)\u0011g\bb\u0001e\t\tA+\u0005\u00024oA\u0011A'N\u0007\u0002U%\u0011aG\u000b\u0002\b\u001d>$\b.\u001b8h!\t!\u0004(\u0003\u0002:U\t\u0019\u0011I\\=\t\u000bmz\u0002\u0019\u0001\u0018\u0002\u0003QDQ!\u0010\u0001\u0005\u0002y\n\u0001b\u001e:ji\u0016\\U-_\u000b\u0003\u007f\u0015#\"\u0001\u0011$\u0015\u0005u\t\u0005b\u0002\"=\u0003\u0003\u0005\u001daQ\u0001\u000bKZLG-\u001a8dK\u0012*\u0004cA\u0014-\tB\u0011q&\u0012\u0003\u0006cq\u0012\rA\r\u0005\u0006\u000fr\u0002\r\u0001R\u0001\u0004W\u0016L\b\"B%\u0001\t\u0003Q\u0015AC<sSR,g+\u00197vKV\u00111*\u0015\u000b\u0003\u0019J#\"!H'\t\u000f9C\u0015\u0011!a\u0002\u001f\u0006QQM^5eK:\u001cW\r\n\u001c\u0011\u0007\u001db\u0003\u000b\u0005\u00020#\u0012)\u0011\u0007\u0013b\u0001e!)1\u000b\u0013a\u0001!\u0006)a/\u00197vK\")Q\u000b\u0001D\u0001-\u0006)a\r\\;tQR\tq\u000b\u0005\u000251&\u0011\u0011L\u000b\u0002\u0005+:LG\u000fC\u0003\\\u0001\u0019\u0005c+A\u0003dY>\u001cX\rC\u0003^\u0001\u0011\u0005a,\u0001\u0005xe&$X-\u00117m+\tyV\r\u0006\u0002aMR\u0011Q$\u0019\u0005\bEr\u000b\t\u0011q\u0001d\u0003))g/\u001b3f]\u000e,Ge\u000e\t\u0004O1\"\u0007CA\u0018f\t\u0015\tDL1\u00013\u0011\u00159G\f1\u0001i\u0003\u0011IG/\u001a:\u0011\u0007%\fHM\u0004\u0002k_:\u00111N\\\u0007\u0002Y*\u0011QNC\u0001\u0007yI|w\u000e\u001e \n\u0003-J!\u0001\u001d\u0016\u0002\u000fA\f7m[1hK&\u0011!o\u001d\u0002\t\u0013R,'/\u0019;pe*\u0011\u0001O\u000b\u0015\u0003\u0001U\u0004\"A^=\u000e\u0003]T!\u0001\u001f\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002{o\naA)\u001a<fY>\u0004XM]!qS\u0002"
)
public abstract class SerializationStream implements Closeable {
   public abstract <T> SerializationStream writeObject(T var1, ClassTag<T> var2);

   public <T> SerializationStream writeKey(T key, ClassTag<T> evidence$5) {
      return this.writeObject(key, evidence$5);
   }

   public <T> SerializationStream writeValue(T value, ClassTag<T> evidence$6) {
      return this.writeObject(value, evidence$6);
   }

   public abstract void flush();

   public abstract void close();

   public <T> SerializationStream writeAll(Iterator<T> iter, ClassTag<T> evidence$7) {
      while(iter.hasNext()) {
         this.writeObject(iter.next(), evidence$7);
      }

      return this;
   }
}
