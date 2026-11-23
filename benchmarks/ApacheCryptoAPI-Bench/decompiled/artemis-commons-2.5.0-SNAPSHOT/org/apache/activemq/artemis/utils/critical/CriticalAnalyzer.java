package org.apache.activemq.artemis.utils.critical;

import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.core.server.ActiveMQComponent;

public interface CriticalAnalyzer extends ActiveMQComponent {
   default void clear() {
   }

   boolean isMeasuring();

   void add(CriticalComponent var1);

   void remove(CriticalComponent var1);

   CriticalAnalyzer setCheckTime(long var1, TimeUnit var3);

   long getCheckTimeNanoSeconds();

   CriticalAnalyzer setTimeout(long var1, TimeUnit var3);

   long getTimeout(TimeUnit var1);

   CriticalAnalyzer addAction(CriticalAction var1);

   void check();
}
