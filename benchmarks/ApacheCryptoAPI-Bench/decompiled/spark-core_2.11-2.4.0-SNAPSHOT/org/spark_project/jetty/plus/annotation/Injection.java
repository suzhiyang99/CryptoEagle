package org.spark_project.jetty.plus.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.spark_project.jetty.util.IntrospectionUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class Injection {
   private static final Logger LOG = Log.getLogger(Injection.class);
   private Class<?> _targetClass;
   private String _jndiName;
   private String _mappingName;
   private Member _target;
   private Class<?> _paramClass;
   private Class<?> _resourceClass;

   public Class<?> getTargetClass() {
      return this._targetClass;
   }

   public Class<?> getParamClass() {
      return this._paramClass;
   }

   public Class<?> getResourceClass() {
      return this._resourceClass;
   }

   public boolean isField() {
      return this._target != null && this._target instanceof Field;
   }

   public boolean isMethod() {
      return this._target != null && this._target instanceof Method;
   }

   public String getJndiName() {
      return this._jndiName;
   }

   public void setJndiName(String jndiName) {
      this._jndiName = jndiName;
   }

   public String getMappingName() {
      return this._mappingName;
   }

   public void setMappingName(String mappingName) {
      this._mappingName = mappingName;
   }

   public Member getTarget() {
      return this._target;
   }

   public void setTarget(Class<?> clazz, Field field, Class<?> resourceType) {
      this._targetClass = clazz;
      this._target = field;
      this._resourceClass = resourceType;
   }

   public void setTarget(Class<?> clazz, Method method, Class<?> arg, Class<?> resourceType) {
      this._targetClass = clazz;
      this._target = method;
      this._resourceClass = resourceType;
      this._paramClass = arg;
   }

   public void setTarget(Class<?> clazz, String target, Class<?> resourceType) {
      this._targetClass = clazz;
      this._resourceClass = resourceType;
      String setter = "set" + target.substring(0, 1).toUpperCase(Locale.ENGLISH) + target.substring(1);

      try {
         LOG.debug("Looking for method for setter: " + setter + " with arg " + this._resourceClass);
         this._target = IntrospectionUtil.findMethod(clazz, setter, new Class[]{this._resourceClass}, true, false);
         this._targetClass = clazz;
         this._paramClass = this._resourceClass;
      } catch (NoSuchMethodException var8) {
         try {
            this._target = IntrospectionUtil.findField(clazz, target, resourceType, true, false);
            this._targetClass = clazz;
         } catch (NoSuchFieldException var7) {
            throw new IllegalArgumentException("No such field or method " + target + " on class " + this._targetClass);
         }
      }

   }

   public void inject(Object injectable) {
      if (this._target != null) {
         if (this._target instanceof Field) {
            this.injectField((Field)this._target, injectable);
         } else {
            this.injectMethod((Method)this._target, injectable);
         }

      } else {
         throw new IllegalStateException("No method or field to inject with " + this.getJndiName());
      }
   }

   public Object lookupInjectedValue() throws NamingException {
      InitialContext context = new InitialContext();
      return context.lookup("java:comp/env/" + this.getJndiName());
   }

   protected void injectField(Field field, Object injectable) {
      try {
         boolean accessibility = field.isAccessible();
         field.setAccessible(true);
         field.set(injectable, this.lookupInjectedValue());
         field.setAccessible(accessibility);
      } catch (Exception e) {
         LOG.warn(e);
         throw new IllegalStateException("Inject failed for field " + field.getName());
      }
   }

   protected void injectMethod(Method method, Object injectable) {
      try {
         boolean accessibility = method.isAccessible();
         method.setAccessible(true);
         method.invoke(injectable, this.lookupInjectedValue());
         method.setAccessible(accessibility);
      } catch (Exception e) {
         LOG.warn(e);
         throw new IllegalStateException("Inject failed for method " + method.getName());
      }
   }
}
