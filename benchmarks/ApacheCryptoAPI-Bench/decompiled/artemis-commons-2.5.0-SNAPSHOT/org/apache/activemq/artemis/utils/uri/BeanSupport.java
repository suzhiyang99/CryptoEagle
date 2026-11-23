package org.apache.activemq.artemis.utils.uri;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;

public class BeanSupport {
   private static final BeanUtilsBean beanUtils = new BeanUtilsBean();

   public static void registerConverter(Converter converter, Class type) {
      synchronized(beanUtils) {
         beanUtils.getConvertUtils().register(converter, type);
      }
   }

   public static <P> P copyData(P source, P target) throws Exception {
      synchronized(beanUtils) {
         beanUtils.copyProperties(source, target);
         return target;
      }
   }

   public static <P> P setData(URI uri, P obj, Map<String, String> query) throws Exception {
      synchronized(beanUtils) {
         beanUtils.setProperty(obj, "host", uri.getHost());
         beanUtils.setProperty(obj, "port", uri.getPort());
         beanUtils.setProperty(obj, "userInfo", uri.getUserInfo());
         beanUtils.populate(obj, query);
         return obj;
      }
   }

   public static <P> P setData(P obj, Map<String, Object> data) throws Exception {
      synchronized(beanUtils) {
         beanUtils.populate(obj, data);
         return obj;
      }
   }

   public static void setData(URI uri, HashMap<String, Object> properties, Set<String> allowableProperties, Map<String, String> query, Map<String, Object> extraProps) {
      if (allowableProperties.contains("host")) {
         properties.put("host", "" + uri.getHost());
      }

      if (allowableProperties.contains("port")) {
         properties.put("port", "" + uri.getPort());
      }

      if (allowableProperties.contains("userInfo")) {
         properties.put("userInfo", "" + uri.getUserInfo());
      }

      for(Map.Entry<String, String> entry : query.entrySet()) {
         if (allowableProperties.contains(entry.getKey())) {
            properties.put((String)entry.getKey(), entry.getValue());
         } else {
            extraProps.put((String)entry.getKey(), entry.getValue());
         }
      }

   }

   public static String getData(List<String> ignored, Object... beans) throws Exception {
      StringBuilder sb = new StringBuilder();
      boolean empty = true;
      synchronized(beanUtils) {
         for(Object bean : beans) {
            if (bean != null) {
               PropertyDescriptor[] descriptors = beanUtils.getPropertyUtils().getPropertyDescriptors(bean);

               for(PropertyDescriptor descriptor : descriptors) {
                  if (descriptor.getReadMethod() != null && isWriteable(descriptor, ignored)) {
                     String value = beanUtils.getProperty(bean, descriptor.getName());
                     if (value != null) {
                        if (!empty) {
                           sb.append("&");
                        }

                        empty = false;
                        sb.append(descriptor.getName()).append("=").append(encodeURI(value));
                     }
                  }
               }
            }
         }
      }

      return sb.toString();
   }

   private static boolean isWriteable(PropertyDescriptor descriptor, List<String> ignored) {
      if (ignored != null && ignored.contains(descriptor.getName())) {
         return false;
      } else {
         Class<?> type = descriptor.getPropertyType();
         return type == Double.class || type == Double.TYPE || type == Long.class || type == Long.TYPE || type == Integer.class || type == Integer.TYPE || type == Float.class || type == Float.TYPE || type == Boolean.class || type == Boolean.TYPE || type == String.class;
      }
   }

   public static String decodeURI(String value) throws UnsupportedEncodingException {
      return URLDecoder.decode(value, "UTF-8");
   }

   public static String encodeURI(String value) throws UnsupportedEncodingException {
      return URLEncoder.encode(value, "UTF-8");
   }

   static {
      beanUtils.getPropertyUtils().addBeanIntrospector(new FluentPropertyBeanIntrospectorWithIgnores());
   }
}
