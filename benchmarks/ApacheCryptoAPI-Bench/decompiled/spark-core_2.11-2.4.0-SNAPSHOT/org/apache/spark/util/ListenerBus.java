package org.apache.spark.util;

import com.codahale.metrics.Timer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.spark.internal.Logging;
import scala.Option;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%a\u0001C\u0001\u0003!\u0003\r\t\u0001\u0002\u0006\u0003\u00171K7\u000f^3oKJ\u0014Uo\u001d\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-F\u0002\f_q\u001b2\u0001\u0001\u0007\u0013!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111CF\u0007\u0002))\u0011Q\u0003B\u0001\tS:$XM\u001d8bY&\u0011q\u0003\u0006\u0002\b\u0019><w-\u001b8h\u0011\u0015I\u0002\u0001\"\u0001\u001c\u0003\u0019!\u0013N\\5uI\r\u0001A#\u0001\u000f\u0011\u00055i\u0012B\u0001\u0010\u000f\u0005\u0011)f.\u001b;\t\r\u0001\u0002\u0001\u0015!\u0003\"\u0003Ma\u0017n\u001d;f]\u0016\u00148\u000f\u00157vgRKW.\u001a:t!\r\u0011\u0003FK\u0007\u0002G)\u0011A%J\u0001\u000bG>t7-\u001e:sK:$(BA\u0002'\u0015\u00059\u0013\u0001\u00026bm\u0006L!!K\u0012\u0003)\r{\u0007/_(o/JLG/Z!se\u0006LH*[:u!\u0011i1&L\u001b\n\u00051r!A\u0002+va2,'\u0007\u0005\u0002/_1\u0001A!\u0002\u0019\u0001\u0005\u0004\t$!\u0001'\u0012\u0005Ib\u0001CA\u00074\u0013\t!dBA\u0004O_RD\u0017N\\4\u0011\u000751\u0004(\u0003\u00028\u001d\t1q\n\u001d;j_:\u0004\"!\u000f!\u000e\u0003iR!a\u000f\u001f\u0002\u000f5,GO]5dg*\u0011QHP\u0001\tG>$\u0017\r[1mK*\tq(A\u0002d_6L!!\u0011\u001e\u0003\u000bQKW.\u001a:\t\r\r\u0003A\u0011\u0001\u0003E\u0003%a\u0017n\u001d;f]\u0016\u00148/F\u0001F!\r1u)L\u0007\u0002K%\u0011\u0001*\n\u0002\u0005\u0019&\u001cH\u000fC\u0003K\u0001\u0011E1*\u0001\u0005hKR$\u0016.\\3s)\t)D\nC\u0003N\u0013\u0002\u0007Q&\u0001\u0005mSN$XM\\3s\u0011\u0015y\u0005\u0001\"\u0002Q\u0003-\tG\r\u001a'jgR,g.\u001a:\u0015\u0005q\t\u0006\"B'O\u0001\u0004i\u0003\"B*\u0001\t\u000b!\u0016A\u0004:f[>4X\rT5ti\u0016tWM\u001d\u000b\u00039UCQ!\u0014*A\u00025BQa\u0016\u0001\u0005\u0002a\u000b\u0011\u0002]8tiR{\u0017\t\u001c7\u0015\u0005qI\u0006\"\u0002.W\u0001\u0004Y\u0016!B3wK:$\bC\u0001\u0018]\t\u0015i\u0006A1\u0001_\u0005\u0005)\u0015C\u0001\u001a`!\ti\u0001-\u0003\u0002b\u001d\t\u0019\u0011I\\=\t\u000b\r\u0004a\u0011\u00033\u0002\u0017\u0011|\u0007k\\:u\u000bZ,g\u000e\u001e\u000b\u00049\u00154\u0007\"B'c\u0001\u0004i\u0003\"\u0002.c\u0001\u0004Y\u0006B\u00025\u0001\t\u0003!\u0011.\u0001\u000bgS:$G*[:uK:,'o\u001d\"z\u00072\f7o]\u000b\u0003Uf$\u0012a\u001b\u000b\u0003Yr\u00042!\\;y\u001d\tq7O\u0004\u0002pe6\t\u0001O\u0003\u0002r5\u00051AH]8pizJ\u0011aD\u0005\u0003i:\tq\u0001]1dW\u0006<W-\u0003\u0002wo\n\u00191+Z9\u000b\u0005Qt\u0001C\u0001\u0018z\t\u0015QxM1\u0001|\u0005\u0005!\u0016C\u0001\u001a.\u0011\u001dix-!AA\u0004y\f!\"\u001a<jI\u0016t7-\u001a\u00132!\u0011y\u0018Q\u0001=\u000e\u0005\u0005\u0005!bAA\u0002\u001d\u00059!/\u001a4mK\u000e$\u0018\u0002BA\u0004\u0003\u0003\u0011\u0001b\u00117bgN$\u0016m\u001a"
)
public interface ListenerBus<L, E> extends Logging {
   CopyOnWriteArrayList org$apache$spark$util$ListenerBus$$listenersPlusTimers();

   void org$apache$spark$util$ListenerBus$_setter_$org$apache$spark$util$ListenerBus$$listenersPlusTimers_$eq(CopyOnWriteArrayList var1);

   List<L> listeners();

   Option<Timer> getTimer(L var1);

   void addListener(L var1);

   void removeListener(L var1);

   void postToAll(E var1);

   void doPostEvent(L var1, E var2);

   <T extends L> Seq<T> findListenersByClass(ClassTag<T> var1);
}
