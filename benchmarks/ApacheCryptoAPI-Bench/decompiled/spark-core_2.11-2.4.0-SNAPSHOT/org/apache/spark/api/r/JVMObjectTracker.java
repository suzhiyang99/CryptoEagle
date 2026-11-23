package org.apache.spark.api.r;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;

@ScalaSignature(
   bytes = "\u0006\u0001%4Q!\u0001\u0002\u0001\u00051\u0011\u0001C\u0013,N\u001f\nTWm\u0019;Ue\u0006\u001c7.\u001a:\u000b\u0005\r!\u0011!\u0001:\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0005\u0002\u0001\u001bA\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001aDQ\u0001\u0006\u0001\u0005\u0002Y\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002/A\u0011\u0001\u0004A\u0007\u0002\u0005!1!\u0004\u0001Q\u0001\nm\taa\u001c2k\u001b\u0006\u0004\b\u0003\u0002\u000f$K!j\u0011!\b\u0006\u0003=}\t!bY8oGV\u0014(/\u001a8u\u0015\t\u0001\u0013%\u0001\u0003vi&d'\"\u0001\u0012\u0002\t)\fg/Y\u0005\u0003Iu\u0011\u0011cQ8oGV\u0014(/\u001a8u\u0011\u0006\u001c\b.T1q!\tAb%\u0003\u0002(\u0005\tY!JV'PE*,7\r^%e!\tIC&D\u0001+\u0015\tY\u0013%\u0001\u0003mC:<\u0017BA\u0017+\u0005\u0019y%M[3di\"1q\u0006\u0001Q\u0001\nA\n!b\u001c2k\u0007>,h\u000e^3s!\t\tD'D\u00013\u0015\t\u0019T$\u0001\u0004bi>l\u0017nY\u0005\u0003kI\u0012Q\"\u0011;p[&\u001c\u0017J\u001c;fO\u0016\u0014\b\"B\u001c\u0001\t\u000bA\u0014aA4fiR\u0011\u0011\b\u0010\t\u0004\u001diB\u0013BA\u001e\u0010\u0005\u0019y\u0005\u000f^5p]\")QH\u000ea\u0001K\u0005\u0011\u0011\u000e\u001a\u0005\u0006\u007f\u0001!)\u0001Q\u0001\u0006CB\u0004H.\u001f\u000b\u0003Q\u0005CQ!\u0010 A\u0002\u0015B3AP\"S!\rqAIR\u0005\u0003\u000b>\u0011a\u0001\u001e5s_^\u001c\bCA$P\u001d\tAUJ\u0004\u0002J\u00196\t!J\u0003\u0002L+\u00051AH]8pizJ\u0011\u0001E\u0005\u0003\u001d>\tq\u0001]1dW\u0006<W-\u0003\u0002Q#\n1bj\\*vG\",E.Z7f]R,\u0005pY3qi&|gN\u0003\u0002O\u001f\u0005\n1+\u0001\fjM\u0002ZW-\u001f\u0011e_\u0016\u001c\bE\\8uA\u0015D\u0018n\u001d;/\u0011\u0015)\u0006\u0001\"\u0002W\u0003-\tG\rZ!oI\u001e+G/\u00133\u0015\u0005\u0015:\u0006\"\u0002-U\u0001\u0004A\u0013aA8cU\")!\f\u0001C\u00037\u00061!/Z7pm\u0016$\"!\u000f/\t\u000buJ\u0006\u0019A\u0013\t\u000by\u0003AQA0\u0002\tML'0Z\u000b\u0002AB\u0011a\"Y\u0005\u0003E>\u00111!\u00138u\u0011\u0015!\u0007\u0001\"\u0002f\u0003\u0015\u0019G.Z1s)\u00051\u0007C\u0001\bh\u0013\tAwB\u0001\u0003V]&$\b"
)
public class JVMObjectTracker {
   private final ConcurrentHashMap<JVMObjectId, Object> objMap = new ConcurrentHashMap();
   private final AtomicInteger objCounter = new AtomicInteger();

   public final Option<Object> get(JVMObjectId id) {
      return Option$.MODULE$.<Object>apply(this.objMap.get(id));
   }

   public final Object apply(JVMObjectId id) throws NoSuchElementException {
      return this.get(id).getOrElse(new Serializable(this, id) {
         public static final long serialVersionUID = 0L;
         private final JVMObjectId id$1;

         public final Nothing$ apply() {
            throw new NoSuchElementException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " does not exist."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.id$1})));
         }

         public {
            this.id$1 = id$1;
         }
      });
   }

   public final JVMObjectId addAndGetId(Object obj) {
      JVMObjectId id = new JVMObjectId(BoxesRunTime.boxToInteger(this.objCounter.getAndIncrement()).toString());
      this.objMap.put(id, obj);
      return id;
   }

   public final Option<Object> remove(JVMObjectId id) {
      return Option$.MODULE$.<Object>apply(this.objMap.remove(id));
   }

   public final int size() {
      return this.objMap.size();
   }

   public final void clear() {
      this.objMap.clear();
   }
}
