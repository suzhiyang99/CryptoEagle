package org.apache.meecrowave.tomcat;

import java.beans.PropertyChangeListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.util.LifecycleBase;

public class ProvidedLoader extends LifecycleBase implements Loader {
   private static final ClassLoader MOCK = new TomcatSettersClassLoader(ProvidedLoader.class.getClassLoader());
   private final ClassLoader delegate;
   private Context context;
   private int mockReturns = -1;

   public ProvidedLoader(ClassLoader loader, boolean wrap) {
      ClassLoader impl = loader == null ? ClassLoader.getSystemClassLoader() : loader;
      this.delegate = (ClassLoader)(wrap ? new TomcatSettersClassLoader(impl) : impl);
   }

   public void backgroundProcess() {
   }

   public ClassLoader getClassLoader() {
      if (this.mockReturns > 0) {
         --this.mockReturns;
         return MOCK;
      } else {
         return this.delegate;
      }
   }

   public Context getContext() {
      return this.context;
   }

   public void setContext(Context context) {
      this.context = context;
   }

   public boolean modified() {
      return false;
   }

   public boolean getDelegate() {
      return false;
   }

   public void setDelegate(boolean delegate) {
   }

   public boolean getReloadable() {
      return false;
   }

   public void setReloadable(boolean reloadable) {
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
   }

   protected void startInternal() throws LifecycleException {
      this.mockReturns = 4;
      this.setState(LifecycleState.STARTING);
   }

   protected void initInternal() throws LifecycleException {
   }

   protected void stopInternal() throws LifecycleException {
      this.setState(LifecycleState.STOPPING);
   }

   protected void destroyInternal() throws LifecycleException {
   }

   public static class TomcatSettersClassLoader extends ClassLoader {
      private TomcatSettersClassLoader(ClassLoader classLoader) {
         super(classLoader);
      }

      public void setClearReferencesHttpClientKeepAliveThread(boolean ignored) {
      }

      public void setClearReferencesRmiTargets(boolean ignored) {
      }

      public void setClearReferencesStopThreads(boolean ignored) {
      }

      public void setClearReferencesStopTimerThreads(boolean ignored) {
      }
   }
}
