package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.apache.openejb.OpenEJBException;

public interface EjbTimerService {
   Timer getTimer(long var1);

   Collection<Timer> getTimers(Object var1);

   Timer createTimer(Object var1, Method var2, Date var3, long var4, TimerConfig var6);

   Timer createTimer(Object var1, Method var2, Date var3, TimerConfig var4);

   Timer createTimer(Object var1, Method var2, long var3, long var5, TimerConfig var7);

   Timer createTimer(Object var1, Method var2, long var3, TimerConfig var5);

   Timer createTimer(Object var1, Method var2, ScheduleExpression var3, TimerConfig var4);

   void start() throws OpenEJBException;

   void stop();

   TimerStore getTimerStore();

   boolean isStarted();
}
