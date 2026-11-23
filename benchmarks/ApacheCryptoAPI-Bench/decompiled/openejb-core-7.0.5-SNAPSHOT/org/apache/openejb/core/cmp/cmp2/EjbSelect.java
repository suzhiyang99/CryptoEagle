package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Method;
import java.util.HashMap;
import javax.ejb.FinderException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.cmp.CmpContainer;

public class EjbSelect {
   private static final HashMap<Class<?>, Method> selectMethods = new HashMap();

   public static Method getSelectMethod(Class<?> returnType) {
      Method method = (Method)selectMethods.get(returnType);
      return method == null ? (Method)selectMethods.get(Object.class) : method;
   }

   public static void execute_void(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         cmpContainer.update(beanContext, methodSignature, args);
      }
   }

   public static Object execute_Object(Object obj, String methodSignature, String returnType, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         return cmpContainer.select(beanContext, methodSignature, returnType, args);
      }
   }

   public static char execute_char(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Character result = (Character)cmpContainer.select(beanContext, methodSignature, "char", args);
         return result;
      }
   }

   public static byte execute_byte(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "byte", args);
         return result.byteValue();
      }
   }

   public static boolean execute_boolean(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Boolean result = (Boolean)cmpContainer.select(beanContext, methodSignature, "byte", args);
         return result;
      }
   }

   public static short execute_short(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "short", args);
         return result.shortValue();
      }
   }

   public static int execute_int(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "int", args);
         return result.intValue();
      }
   }

   public static long execute_long(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "long", args);
         return result.longValue();
      }
   }

   public static float execute_float(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "float", args);
         return result.floatValue();
      }
   }

   public static double execute_double(Object obj, String methodSignature, Object... args) throws FinderException {
      BeanContext beanContext = (BeanContext)obj;
      Container container = beanContext.getContainer();
      if (!(container instanceof CmpContainer)) {
         throw new FinderException("Deployment is not connected to a CmpContainer " + beanContext.getDeploymentID());
      } else {
         CmpContainer cmpContainer = (CmpContainer)container;
         Number result = (Number)cmpContainer.select(beanContext, methodSignature, "double", args);
         return result.doubleValue();
      }
   }

   static {
      try {
         selectMethods.put(Object.class, EjbSelect.class.getMethod("execute_Object", Object.class, String.class, String.class, Object[].class));
         selectMethods.put(Void.TYPE, EjbSelect.class.getMethod("execute_void", Object.class, String.class, Object[].class));
         selectMethods.put(Boolean.TYPE, EjbSelect.class.getMethod("execute_boolean", Object.class, String.class, Object[].class));
         selectMethods.put(Byte.TYPE, EjbSelect.class.getMethod("execute_byte", Object.class, String.class, Object[].class));
         selectMethods.put(Character.TYPE, EjbSelect.class.getMethod("execute_char", Object.class, String.class, Object[].class));
         selectMethods.put(Short.TYPE, EjbSelect.class.getMethod("execute_short", Object.class, String.class, Object[].class));
         selectMethods.put(Integer.TYPE, EjbSelect.class.getMethod("execute_int", Object.class, String.class, Object[].class));
         selectMethods.put(Long.TYPE, EjbSelect.class.getMethod("execute_long", Object.class, String.class, Object[].class));
         selectMethods.put(Float.TYPE, EjbSelect.class.getMethod("execute_float", Object.class, String.class, Object[].class));
         selectMethods.put(Double.TYPE, EjbSelect.class.getMethod("execute_double", Object.class, String.class, Object[].class));
      } catch (NoSuchMethodException e) {
         throw new OpenEJBRuntimeException(e);
      }
   }
}
