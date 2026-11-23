package org.apache.openejb.rest;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;

public class ThreadLocalConfiguration extends AbstractRestThreadLocalProxy<Configuration> implements Configuration {
   protected ThreadLocalConfiguration() {
      super(Configuration.class);
   }

   public RuntimeType getRuntimeType() {
      return ((Configuration)this.get()).getRuntimeType();
   }

   public Map<String, Object> getProperties() {
      return ((Configuration)this.get()).getProperties();
   }

   public Object getProperty(String name) {
      return ((Configuration)this.get()).getProperty(name);
   }

   public Collection<String> getPropertyNames() {
      return ((Configuration)this.get()).getPropertyNames();
   }

   public boolean isEnabled(Feature feature) {
      return ((Configuration)this.get()).isEnabled(feature);
   }

   public boolean isEnabled(Class<? extends Feature> featureClass) {
      return ((Configuration)this.get()).isEnabled(featureClass);
   }

   public boolean isRegistered(Object component) {
      return ((Configuration)this.get()).isRegistered(component);
   }

   public boolean isRegistered(Class<?> componentClass) {
      return ((Configuration)this.get()).isRegistered(componentClass);
   }

   public Map<Class<?>, Integer> getContracts(Class<?> componentClass) {
      return ((Configuration)this.get()).getContracts(componentClass);
   }

   public Set<Class<?>> getClasses() {
      return ((Configuration)this.get()).getClasses();
   }

   public Set<Object> getInstances() {
      return ((Configuration)this.get()).getInstances();
   }
}
