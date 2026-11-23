package org.apache.openejb.core.timer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;

public interface TimerStore {
   TimerData getTimer(String var1, long var2);

   Collection<TimerData> getTimers(String var1);

   Collection<TimerData> loadTimers(EjbTimerServiceImpl var1, String var2) throws TimerStoreException;

   void addTimerData(TimerData var1) throws TimerStoreException;

   TimerData createSingleActionTimer(EjbTimerServiceImpl var1, String var2, Object var3, Method var4, Date var5, TimerConfig var6) throws TimerStoreException;

   TimerData createIntervalTimer(EjbTimerServiceImpl var1, String var2, Object var3, Method var4, Date var5, long var6, TimerConfig var8) throws TimerStoreException;

   TimerData createCalendarTimer(EjbTimerServiceImpl var1, String var2, Object var3, Method var4, ScheduleExpression var5, TimerConfig var6, boolean var7) throws TimerStoreException;

   void removeTimer(long var1);

   void updateIntervalTimer(TimerData var1);
}
