package org.apache.spark.storage.memory;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000152q!\u0001\u0002\u0011\u0002G%QB\u0001\nNK6|'/_#oiJL()^5mI\u0016\u0014(BA\u0002\u0005\u0003\u0019iW-\\8ss*\u0011QAB\u0001\bgR|'/Y4f\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001A\u000b\u0003\u001d\u0011\u001a\"\u0001A\b\u0011\u0005A\u0019R\"A\t\u000b\u0003I\tQa]2bY\u0006L!\u0001F\t\u0003\r\u0005s\u0017PU3g\u0011\u00151\u0002A\"\u0001\u0018\u0003-\u0001(/Z2jg\u0016\u001c\u0016N_3\u0016\u0003a\u0001\"\u0001E\r\n\u0005i\t\"\u0001\u0002'p]\u001eDQ\u0001\b\u0001\u0007\u0002u\tQAY;jY\u0012$\u0012A\b\t\u0004?\u0001\u0012S\"\u0001\u0002\n\u0005\u0005\u0012!aC'f[>\u0014\u00180\u00128uef\u0004\"a\t\u0013\r\u0001\u0011)Q\u0005\u0001b\u0001M\t\tA+\u0005\u0002(UA\u0011\u0001\u0003K\u0005\u0003SE\u0011qAT8uQ&tw\r\u0005\u0002\u0011W%\u0011A&\u0005\u0002\u0004\u0003:L\b"
)
public interface MemoryEntryBuilder<T> {
   long preciseSize();

   MemoryEntry<T> build();
}
