package org.apache.openejb.config.sys;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.SimpleJSonParser;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class JSonConfigReader {
   private static final String COMMENT_KEY = "__";

   private static Map map(Object rawMap) {
      return (Map)Map.class.cast(rawMap);
   }

   public static <T> T read(Class<T> clazz, InputStream is) throws OpenEJBException {
      if (!Openejb.class.equals(clazz) && !Tomee.class.equals(clazz)) {
         if (Resources.class.equals(clazz)) {
            Resources resources = new Resources();
            Openejb openejb = ((SaxOpenejb)read(is, "openejb", Arrays.asList("Resource", "Container", "JndiProvider", "Connector", "Import", "Service"), new SaxOpenejb())).getOpenejb();
            resources.getContainer().addAll(openejb.getContainer());
            resources.getResource().addAll(openejb.getResource());
            resources.getService().addAll(openejb.getServices());
            resources.getConnector().addAll(openejb.getConnector());
            resources.getJndiProvider().addAll(openejb.getJndiProvider());
            return (T)clazz.cast(resources);
         } else {
            throw new IllegalArgumentException(clazz.getName() + " not supported");
         }
      } else {
         SaxOpenejb handler = (SaxOpenejb)read(is, "openejb", Arrays.asList("System-Property", "Resource", "Container", "JndiProvider", "TransactionManager", "ConnectionManager", "ProxyFactory", "Connector", "Deployments", "Import", "Service", "SecurityService"), new SaxOpenejb());
         return (T)clazz.cast(handler.getOpenejb());
      }
   }

   private static <T extends DefaultHandler> T read(InputStream is, String mainRoot, Collection<String> roots, T handler) throws OpenEJBException {
      try {
         handler.startDocument();
         handler.startElement((String)null, mainRoot, (String)null, new AttributesImpl());
         Map<?, ?> jsConfig = map(SimpleJSonParser.read(is));
         jsConfig.remove("__");

         for(String root : roots) {
            String currentRoot;
            if (root.endsWith("s")) {
               currentRoot = root.toLowerCase(Locale.ENGLISH);
            } else {
               currentRoot = root.toLowerCase(Locale.ENGLISH) + "s";
            }

            Map<String, Map<String, Map<String, String>>> resources = map(jsConfig.get(currentRoot));
            if (resources != null) {
               resources.remove("__");

               for(Map.Entry<String, Map<String, Map<String, String>>> resource : resources.entrySet()) {
                  AttributesImpl attributes = toAttributes(map(resource.getValue()), "properties");
                  if (!"deployments".equals(currentRoot)) {
                     attributes.addAttribute((String)null, "id", "id", (String)null, (String)resource.getKey());
                  }

                  if ("resources".equals(currentRoot) && attributes.getIndex("type") == -1 && attributes.getIndex("class-name") == -1 && attributes.getIndex("provider") == -1) {
                     attributes.addAttribute((String)null, "type", "type", (String)null, "DataSource");
                  }

                  handler.startElement((String)null, root, root, attributes);
                  String propertiesAsStr = toString(map(((Map)resource.getValue()).get("properties")));
                  handler.characters(propertiesAsStr.toCharArray(), 0, propertiesAsStr.length());
                  handler.endElement((String)null, root, root);
               }
            }
         }

         handler.endElement((String)null, mainRoot, (String)null);
         if (jsConfig.containsKey("system-properties")) {
            Map<?, ?> sysProps = map(jsConfig.get("system-properties"));
            setProperties("", sysProps);
         }

         if (jsConfig.containsKey("daemons")) {
            Map<String, ?> daemons = map(jsConfig.get("daemons"));

            for(Map.Entry<String, ?> entry : daemons.entrySet()) {
               setProperties((String)entry.getKey() + '.', map(entry.getValue()));
            }
         }

         return handler;
      } catch (Exception e) {
         throw new OpenEJBException(e.getMessage(), e);
      }
   }

   private static void setProperties(String prefix, Map<?, ?> sysProps) {
      sysProps.remove("__");

      for(Map.Entry<?, ?> entry : sysProps.entrySet()) {
         String key = prefix + entry.getKey().toString();
         Object value = entry.getValue();
         if (String.class.isInstance(value)) {
            String str = (String)String.class.cast(value);
            SystemInstance.get().setProperty(key, str);
            JavaSecurityManagers.setSystemProperty(key, str);
         } else {
            setProperties(key + '.', map(value));
         }
      }

   }

   private static String toString(Map<String, ?> properties) {
      if (properties == null) {
         return "";
      } else {
         properties.remove("__");
         Properties builder = new Properties();

         for(Map.Entry<String, ?> entry : properties.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().toString());
         }

         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         try {
            builder.store(baos, "");
         } catch (IOException var4) {
         }

         return new String(baos.toByteArray());
      }
   }

   private static AttributesImpl toAttributes(Map<String, String> map, String... ignored) {
      map.remove("__");
      AttributesImpl attributes = new AttributesImpl();

      for(Map.Entry<String, String> entry : map.entrySet()) {
         String key = (String)entry.getKey();
         boolean add = true;

         for(String i : ignored) {
            if (key.equals(i)) {
               add = false;
               break;
            }
         }

         if (add) {
            attributes.addAttribute((String)null, key, key, (String)null, (String)entry.getValue());
         }
      }

      return attributes;
   }
}
