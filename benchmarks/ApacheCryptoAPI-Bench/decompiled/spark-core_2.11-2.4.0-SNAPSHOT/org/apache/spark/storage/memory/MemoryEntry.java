package org.apache.spark.storage.memory;

import org.apache.spark.memory.MemoryMode;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001q2q!\u0001\u0002\u0011\u0002G%RBA\u0006NK6|'/_#oiJL(BA\u0002\u0005\u0003\u0019iW-\\8ss*\u0011QAB\u0001\bgR|'/Y4f\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\u000b\u0003\u001d9\u001a\"\u0001A\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151\u0002A\"\u0001\u0018\u0003\u0011\u0019\u0018N_3\u0016\u0003a\u0001\"\u0001E\r\n\u0005i\t\"\u0001\u0002'p]\u001eDQ\u0001\b\u0001\u0007\u0002u\t!\"\\3n_JLXj\u001c3f+\u0005q\u0002CA\u0010\"\u001b\u0005\u0001#BA\u0002\u0007\u0013\t\u0011\u0003E\u0001\u0006NK6|'/_'pI\u0016DQ\u0001\n\u0001\u0007\u0002\u0015\n\u0001b\u00197bgN$\u0016mZ\u000b\u0002MA\u0019qE\u000b\u0017\u000e\u0003!R!!K\t\u0002\u000fI,g\r\\3di&\u00111\u0006\u000b\u0002\t\u00072\f7o\u001d+bOB\u0011QF\f\u0007\u0001\t\u0015y\u0003A1\u00011\u0005\u0005!\u0016CA\u00195!\t\u0001\"'\u0003\u00024#\t9aj\u001c;iS:<\u0007C\u0001\t6\u0013\t1\u0014CA\u0002B]fL3\u0001\u0001\u001d;\u0013\tI$AA\fEKN,'/[1mSj,G-T3n_JLXI\u001c;ss&\u00111H\u0001\u0002\u0016'\u0016\u0014\u0018.\u00197ju\u0016$W*Z7pef,e\u000e\u001e:z\u0001"
)
public interface MemoryEntry<T> {
   long size();

   MemoryMode memoryMode();

   ClassTag<T> classTag();
}
