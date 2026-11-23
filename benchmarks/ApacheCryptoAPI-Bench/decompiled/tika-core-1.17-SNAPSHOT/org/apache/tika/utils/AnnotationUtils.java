package org.apache.tika.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.Field;
import org.apache.tika.config.Param;
import org.apache.tika.config.ParamField;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaConfigException;

public class AnnotationUtils {
   private static final Map<Class<?>, List<ParamField>> PARAM_INFO = new HashMap();

   private static List<AccessibleObject> collectInfo(Class<?> clazz, Class<? extends Annotation> annotation) {
      Class superClazz = clazz;
      List<AccessibleObject> members = new ArrayList();

      List<AccessibleObject> annotatedMembers;
      for(annotatedMembers = new ArrayList(); superClazz != null && superClazz != Object.class; superClazz = superClazz.getSuperclass()) {
         members.addAll(Arrays.asList(superClazz.getDeclaredFields()));
         members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
      }

      for(final AccessibleObject member : members) {
         if (member.isAnnotationPresent(annotation)) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  member.setAccessible(true);
                  return null;
               }
            });
            annotatedMembers.add(member);
         }
      }

      return annotatedMembers;
   }

   public static void assignFieldParams(Object bean, Map<String, Param> params) throws TikaConfigException {
      Class<?> beanClass = bean.getClass();
      if (!PARAM_INFO.containsKey(beanClass)) {
         synchronized(TikaConfig.class) {
            if (!PARAM_INFO.containsKey(beanClass)) {
               List<AccessibleObject> aObjs = collectInfo(beanClass, Field.class);
               List<ParamField> fields = new ArrayList(aObjs.size());

               for(AccessibleObject aObj : aObjs) {
                  fields.add(new ParamField(aObj));
               }

               PARAM_INFO.put(beanClass, fields);
            }
         }
      }

      List<ParamField> fields = (List)PARAM_INFO.get(beanClass);
      Set<String> validFieldNames = new HashSet();

      for(ParamField field : fields) {
         validFieldNames.add(field.getName());
         Param<?> param = (Param)params.get(field.getName());
         if (param != null) {
            if (!field.getType().isAssignableFrom(param.getType())) {
               String msg = String.format(Locale.ROOT, "Value '%s' of type '%s' cant be assigned to field '%s' of defined type '%s'", param.getValue(), param.getValue().getClass(), field.getName(), field.getType());
               throw new TikaConfigException(msg);
            }

            try {
               field.assignValue(bean, param.getValue());
            } catch (Exception e) {
               throw new TikaConfigException(e.getMessage(), e);
            }
         } else if (field.isRequired()) {
            String msg = String.format(Locale.ROOT, "Param %s is required for %s, but it is not given in config.", field.getName(), bean.getClass().getName());
            throw new TikaConfigException(msg);
         }
      }

   }
}
