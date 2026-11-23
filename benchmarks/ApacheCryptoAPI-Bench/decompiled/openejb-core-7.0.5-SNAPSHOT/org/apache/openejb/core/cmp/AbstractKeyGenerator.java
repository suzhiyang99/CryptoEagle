package org.apache.openejb.core.cmp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.ejb.EJBException;
import org.apache.openejb.OpenEJBException;

public abstract class AbstractKeyGenerator implements KeyGenerator {
   public static boolean isValidPkField(Field field) {
      int modifiers = field.getModifiers();
      return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
   }

   public static Field getField(Class clazz, String fieldName) throws OpenEJBException {
      try {
         return clazz.getField(fieldName);
      } catch (NoSuchFieldException e) {
         throw new OpenEJBException("Unable to get primary key field from entity bean class: " + clazz.getName(), e);
      }
   }

   public static Object getFieldValue(Field field, Object object) throws EJBException {
      if (field == null) {
         throw new NullPointerException("field is null");
      } else if (object == null) {
         throw new NullPointerException("object is null");
      } else {
         try {
            return field.get(object);
         } catch (Exception e) {
            throw new EJBException("Could not get field value for field " + field, e);
         }
      }
   }

   public static void setFieldValue(Field field, Object object, Object value) throws EJBException {
      if (field == null) {
         throw new NullPointerException("field is null");
      } else if (object == null) {
         throw new NullPointerException("object is null");
      } else {
         try {
            field.set(object, value);
         } catch (Exception e) {
            throw new EJBException("Could not set field value for field " + field, e);
         }
      }
   }
}
