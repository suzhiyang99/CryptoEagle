package org.apache.openejb.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.MBeanServer;
import org.apache.openejb.monitoring.LocalMBeanServer;

public class HeapDump {
   public static String to(String fileName) {
      dumpHeap(fileName);
      return fileName;
   }

   public static void dumpHeap(String fileName) {
      Class clazz;
      try {
         clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
      } catch (ClassNotFoundException var6) {
         System.out.println("ERROR: dumpHeap only works on a Sun Java 1.6+ VM containing the class com.sun.management.HotSpotDiagnosticMXBean");
         return;
      }

      Object hotspotMBean = null;

      try {
         MBeanServer server = LocalMBeanServer.get();
         hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", clazz);
      } catch (Throwable e) {
         System.out.print("ERROR: dumpHeap was unable to obtain the HotSpotDiagnosticMXBean: ");
         e.printStackTrace();
      }

      try {
         Method method = hotspotMBean.getClass().getMethod("dumpHeap", String.class);
         method.invoke(hotspotMBean, fileName);
      } catch (InvocationTargetException e) {
         Throwable t = (Throwable)(e.getCause() != null ? e.getCause() : e);
         System.out.print("ERROR: dumpHeap threw an exception: ");
         t.printStackTrace();
      } catch (Throwable e) {
         System.out.print("ERROR: dumpHeap threw an exception: ");
         e.printStackTrace();
      }

   }
}
