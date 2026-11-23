package org.apache.openejb.cdi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.Properties;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.SecurityService;

public class ManagedSecurityService implements org.apache.webbeans.spi.SecurityService {
   private final org.apache.webbeans.corespi.security.ManagedSecurityService delegate = new org.apache.webbeans.corespi.security.ManagedSecurityService();

   public Principal getCurrentPrincipal() {
      SecurityService<?> service = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
      return service != null ? service.getCallerPrincipal() : null;
   }

   public <T> Constructor<T> doPrivilegedGetDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) {
      return this.delegate.doPrivilegedGetDeclaredConstructor(clazz, parameterTypes);
   }

   public <T> Constructor<T> doPrivilegedGetConstructor(Class<T> clazz, Class<?>... parameterTypes) {
      return this.delegate.doPrivilegedGetConstructor(clazz, parameterTypes);
   }

   public <T> Constructor<?>[] doPrivilegedGetDeclaredConstructors(Class<T> clazz) {
      return this.delegate.doPrivilegedGetDeclaredConstructors(clazz);
   }

   public <T> Method doPrivilegedGetDeclaredMethod(Class<T> clazz, String name, Class<?>... parameterTypes) {
      return this.delegate.doPrivilegedGetDeclaredMethod(clazz, name, parameterTypes);
   }

   public <T> Method[] doPrivilegedGetDeclaredMethods(Class<T> clazz) {
      return this.delegate.doPrivilegedGetDeclaredMethods(clazz);
   }

   public <T> Field doPrivilegedGetDeclaredField(Class<T> clazz, String name) {
      return this.delegate.doPrivilegedGetDeclaredField(clazz, name);
   }

   public <T> Field[] doPrivilegedGetDeclaredFields(Class<T> clazz) {
      return this.delegate.doPrivilegedGetDeclaredFields(clazz);
   }

   public void doPrivilegedSetAccessible(AccessibleObject obj, boolean flag) {
      this.delegate.doPrivilegedSetAccessible(obj, flag);
   }

   public boolean doPrivilegedIsAccessible(AccessibleObject obj) {
      return this.delegate.doPrivilegedIsAccessible(obj);
   }

   public <T> T doPrivilegedObjectCreate(Class<T> clazz) throws PrivilegedActionException, IllegalAccessException, InstantiationException {
      return (T)this.delegate.doPrivilegedObjectCreate(clazz);
   }

   public void doPrivilegedSetSystemProperty(String propertyName, String value) {
      this.delegate.doPrivilegedSetSystemProperty(propertyName, value);
   }

   public String doPrivilegedGetSystemProperty(String propertyName, String defaultValue) {
      return this.delegate.doPrivilegedGetSystemProperty(propertyName, propertyName);
   }

   public Properties doPrivilegedGetSystemProperties() {
      return this.delegate.doPrivilegedGetSystemProperties();
   }
}
