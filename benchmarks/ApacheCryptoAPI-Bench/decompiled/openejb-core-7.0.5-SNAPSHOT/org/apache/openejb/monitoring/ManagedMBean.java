package org.apache.openejb.monitoring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import org.apache.openejb.util.Classes;
import org.apache.xbean.finder.ClassFinder;
import org.apache.xbean.propertyeditor.PropertyEditors;

public class ManagedMBean implements DynamicMBean {
   private static final MBeanNotificationInfo[] EMPTY_NOTIFICATIONS = new MBeanNotificationInfo[0];
   private final List<MBeanAttributeInfo> attributes;
   private final List<MBeanOperationInfo> operations;
   private final Map<String, Member> attributesMap;
   private final Map<String, MethodMember> operationsMap;
   private final List<Member> dynamic;
   private Pattern includes;
   private Pattern excludes;
   private boolean filterAttributes;
   private MBeanParameterInfo excludeInfo;
   private MBeanParameterInfo includeInfo;

   public ManagedMBean(Object managed) {
      this(managed, "");

      try {
         Method method = this.getClass().getMethod("setAttributesFilter", String.class, String.class);
         String description = "Filters the attributes that show up in the MBeanInfo.  The exclude is applied first, then any attributes that match the include are re-added.  It may be required to disconnect and reconnect the JMX console to force a refresh of the MBeanInfo";
         this.excludeInfo = new MBeanParameterInfo("excludeRegex", "java.lang.String", "\"" + this.excludes.pattern() + "\"");
         this.includeInfo = new MBeanParameterInfo("includeRegex", "java.lang.String", "\"" + this.includes.pattern() + "\"");
         MBeanOperationInfo filterOperation = new MBeanOperationInfo("FilterAttributes", "Filters the attributes that show up in the MBeanInfo.  The exclude is applied first, then any attributes that match the include are re-added.  It may be required to disconnect and reconnect the JMX console to force a refresh of the MBeanInfo", new MBeanParameterInfo[]{this.excludeInfo, this.includeInfo}, "void", 3);
         this.operations.add(filterOperation);
         this.operationsMap.put(filterOperation.getName(), new MethodMember(method, this, ""));
         this.filterAttributes = true;
      } catch (NoSuchMethodException e) {
         throw new IllegalStateException(e);
      }
   }

   ManagedMBean(Object managed, String prefix) {
      this.attributes = new ArrayList();
      this.operations = new ArrayList();
      this.attributesMap = new HashMap();
      this.operationsMap = new HashMap();
      this.dynamic = new ArrayList();
      this.includes = Pattern.compile("");
      this.excludes = Pattern.compile("");
      this.scan(managed, prefix);

      for(Member member : this.attributesMap.values()) {
         this.attributes.add(new MBeanAttributeInfo(member.getName(), member.getType().getName(), "", true, false, false));
      }

      for(Member member : this.operationsMap.values()) {
         MBeanOperationInfo op = new MBeanOperationInfo("", ((MethodMember)member).getter);
         this.operations.add(new MBeanOperationInfo(member.getName(), "", op.getSignature(), op.getReturnType(), op.getImpact()));
      }

      this.filterAttributes = true;
      this.excludeInfo = new MBeanParameterInfo("excludeRegex", "java.lang.String", "\"" + this.excludes.pattern() + "\"");
      this.includeInfo = new MBeanParameterInfo("includeRegex", "java.lang.String", "\"" + this.includes.pattern() + "\"");
   }

   private void scan(Object target, String prefix) {
      ClassFinder finder = new ClassFinder(Classes.ancestors(target.getClass()));

      for(Field field : finder.findAnnotatedFields(Managed.class)) {
         this.attribute(new FieldMember(field, target, prefix));
      }

      for(Method method : finder.findAnnotatedMethods(Managed.class)) {
         MethodMember member = new MethodMember(method, target, prefix);
         if (!method.getName().matches("(get|is)([A-Z_].*|)")) {
            this.operationsMap.put(member.getName(), member);
         } else {
            this.attribute(new MethodMember(method, target, prefix));
         }
      }

      for(Method method : finder.findAnnotatedMethods(ManagedCollection.class)) {
         this.dynamic.add(new MethodMember(method, target, prefix));
      }

   }

   private void attribute(Member member) {
      Class<?> type = member.getType();
      Managed managed = (Managed)type.getAnnotation(Managed.class);
      if (managed != null) {
         try {
            String s = "";
            if (managed.append()) {
               s = member.getName();
            }

            this.scan(member.get(), s);
         } catch (InvocationTargetException | IllegalAccessException e) {
            ((ReflectiveOperationException)e).printStackTrace();
         }
      } else {
         this.attributesMap.put(member.getName(), member);
      }

   }

   public Object getAttribute(String s) throws AttributeNotFoundException, MBeanException, ReflectionException {
      try {
         Member member = (Member)this.attributesMap.get(s);
         if (member == null) {
            throw new AttributeNotFoundException(s);
         } else {
            return member.get();
         }
      } catch (Exception e) {
         e.printStackTrace();
         throw new ReflectionException(e);
      }
   }

   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
   }

   public AttributeList getAttributes(String[] strings) {
      AttributeList list = new AttributeList(strings.length);

      for(String attribute : strings) {
         try {
            list.add(new Attribute(attribute, this.getAttribute(attribute)));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return list;
   }

   public AttributeList setAttributes(AttributeList attributeList) {
      return new AttributeList();
   }

   public Object invoke(String operation, Object[] args, String[] types) throws MBeanException, ReflectionException {
      MethodMember member = (MethodMember)this.operationsMap.get(operation);
      Method method = member.getter;

      for(int i = 0; i < method.getParameterTypes().length; ++i) {
         Object value = args[i];
         Class<?> expectedType = method.getParameterTypes()[i];
         if (value instanceof String && expectedType != Object.class) {
            String stringValue = (String)value;
            value = PropertyEditors.getValue(expectedType, stringValue);
         }

         args[i] = value;
      }

      try {
         return method.invoke(member.target, args);
      } catch (InvocationTargetException e) {
         throw new ReflectionException((Exception)e.getCause());
      } catch (Exception e) {
         throw new ReflectionException(e);
      }
   }

   public MBeanInfo getMBeanInfo() {
      List<MBeanAttributeInfo> attributes = new ArrayList(this.attributes);
      List<MBeanOperationInfo> operations = new ArrayList(this.operations);

      for(Member member : this.dynamic) {
         try {
            ManagedCollection managedCollection = (ManagedCollection)member.getAnnotation(ManagedCollection.class);

            for(Object o : (Collection)member.get()) {
               try {
                  Field field = o.getClass().getDeclaredField(managedCollection.key());
                  field.setAccessible(true);
                  Object key = field.get(o);
                  ManagedMBean bean = new ManagedMBean(o, key.toString());
                  Collections.addAll(attributes, bean.getMBeanInfo().getAttributes());
                  Collections.addAll(operations, bean.getMBeanInfo().getOperations());
                  this.attributesMap.putAll(bean.attributesMap);
                  this.operationsMap.putAll(bean.operationsMap);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      Collections.sort(operations, ManagedMBean.MBeanFeatureInfoComparator.INSTANCE);
      Collections.sort(attributes, ManagedMBean.MBeanFeatureInfoComparator.INSTANCE);
      if (this.filterAttributes) {
         Iterator<MBeanAttributeInfo> iterator = attributes.iterator();

         while(iterator.hasNext()) {
            MBeanAttributeInfo info = (MBeanAttributeInfo)iterator.next();
            if (!this.includes.matcher(info.getName()).matches() && this.excludes.matcher(info.getName()).matches()) {
               iterator.remove();
            }
         }
      }

      return new MBeanInfo(this.getClass().getName(), "", (MBeanAttributeInfo[])attributes.toArray(new MBeanAttributeInfo[attributes.size()]), new MBeanConstructorInfo[0], (MBeanOperationInfo[])operations.toArray(new MBeanOperationInfo[operations.size()]), EMPTY_NOTIFICATIONS);
   }

   public void setAttributesFilter(String exclude, String include) {
      if (include == null) {
         include = "";
      }

      if (exclude == null) {
         exclude = "";
      }

      this.includes = Pattern.compile(include);
      this.excludes = Pattern.compile(exclude);

      try {
         Field field = MBeanFeatureInfo.class.getDeclaredField("description");
         field.setAccessible(true);
         field.set(this.includeInfo, "\"" + this.includes.pattern() + "\"");
         field.set(this.excludeInfo, "\"" + this.excludes.pattern() + "\"");
      } catch (Exception var4) {
      }

   }

   public static class MethodMember implements Member {
      private final Method getter;
      private final Object target;
      private final String prefix;

      public MethodMember(Method getter, Object target, String prefix) {
         getter.setAccessible(true);
         this.getter = getter;
         this.target = target;
         this.prefix = prefix;
      }

      public Class getType() {
         return this.getter.getReturnType();
      }

      public Class getDeclaringClass() {
         return this.getter.getDeclaringClass();
      }

      public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
         return (T)this.getter.getAnnotation(annotationClass);
      }

      public String getName() {
         String method = this.getter.getName();
         StringBuilder name = new StringBuilder(method);
         if (method.matches("get([A-Z].*|)")) {
            name.delete(0, 3);
         }

         if (method.matches("is([A-Z].*|)")) {
            name.delete(0, 2);
         }

         if (!"".equals(this.prefix)) {
            if (!"".equals(name.toString())) {
               name.insert(0, ".");
            }

            name.insert(0, this.prefix);
         }

         return name.toString();
      }

      public String toString() {
         return this.getter.toString();
      }

      public Object get() throws IllegalAccessException, InvocationTargetException {
         return this.getter.invoke(this.target);
      }
   }

   public static class FieldMember implements Member {
      private final Field field;
      private final Object target;
      private final String prefix;

      public FieldMember(Field field, Object target, String prefix) {
         field.setAccessible(true);
         this.field = field;
         this.target = target;
         this.prefix = prefix;
      }

      public Class getType() {
         return this.unwrap(this.field.getType());
      }

      public String toString() {
         return this.field.toString();
      }

      public Class getDeclaringClass() {
         return this.field.getDeclaringClass();
      }

      public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
         return (T)this.field.getAnnotation(annotationClass);
      }

      public String getName() {
         StringBuilder name = new StringBuilder(this.field.getName());
         name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
         if (!"".equals(this.prefix)) {
            if (!"".equals(name.toString())) {
               name.insert(0, ".");
            }

            name.insert(0, this.prefix);
         }

         return name.toString();
      }

      public Object get() throws IllegalAccessException {
         return this.unwrap(this.field.get(this.target));
      }

      public Class<?> unwrap(Class<?> clazz) {
         if (clazz == AtomicInteger.class) {
            return Integer.TYPE;
         } else if (clazz == AtomicBoolean.class) {
            return Boolean.TYPE;
         } else if (clazz == AtomicLong.class) {
            return Long.TYPE;
         } else {
            return clazz == AtomicReference.class ? Object.class : clazz;
         }
      }

      public Object unwrap(Object clazz) {
         if (clazz instanceof AtomicInteger) {
            return ((AtomicInteger)clazz).get();
         } else if (clazz instanceof AtomicBoolean) {
            return ((AtomicBoolean)clazz).get();
         } else if (clazz instanceof AtomicLong) {
            return ((AtomicLong)clazz).get();
         } else {
            return clazz instanceof AtomicReference ? ((AtomicReference)clazz).get() : clazz;
         }
      }
   }

   private static class MBeanFeatureInfoComparator implements Comparator<MBeanFeatureInfo> {
      private static final MBeanFeatureInfoComparator INSTANCE = new MBeanFeatureInfoComparator();

      public int compare(MBeanFeatureInfo o1, MBeanFeatureInfo o2) {
         return o1.getName().compareTo(o2.getName());
      }
   }

   public interface Member {
      Object get() throws IllegalAccessException, InvocationTargetException;

      String getName();

      Class getType();

      <T extends Annotation> T getAnnotation(Class<T> var1);
   }
}
