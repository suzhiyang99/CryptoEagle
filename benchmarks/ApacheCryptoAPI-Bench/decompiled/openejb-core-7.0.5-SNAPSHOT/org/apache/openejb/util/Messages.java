package org.apache.openejb.util;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Messages {
   private static Map<String, ResourceBundle> _rbBundles = new ConcurrentHashMap();
   private static Map<String, Map<String, MessageFormat>> _rbFormats = new ConcurrentHashMap();
   private static Locale _globalLocale;
   private ResourceBundle messages;
   private Map<String, MessageFormat> formats;
   private final Locale locale;
   private final String resourceName;

   public Messages(Class clazz) {
      this(packageName(clazz));
   }

   private static String packageName(Class clazz) {
      String name = clazz.getName();
      return name.substring(0, name.lastIndexOf("."));
   }

   public Messages(String resourceName) {
      this.resourceName = resourceName + ".Messages";
      synchronized(Messages.class) {
         this.locale = _globalLocale;
      }
   }

   protected void init() {
      if (this.formats == null || this.messages == null) {
         ResourceBundle rb = (ResourceBundle)_rbBundles.get(this.resourceName);
         if (rb == null) {
            try {
               if (this.locale == null) {
                  this.messages = ResourceBundle.getBundle(this.resourceName);
               } else {
                  this.messages = ResourceBundle.getBundle(this.resourceName, this.locale);
               }
            } catch (Exception var3) {
               this.messages = new EmptyResourceBundle();
            }

            this.formats = new ConcurrentHashMap();
            _rbBundles.put(this.resourceName, this.messages);
            _rbFormats.put(this.resourceName, this.formats);
         } else {
            this.messages = rb;
            this.formats = (Map)_rbFormats.get(this.resourceName);
         }

      }
   }

   public String format(String message) {
      return this.message(message);
   }

   public String format(String message, Object... args) {
      this.init();
      if (this.locale != _globalLocale) {
         synchronized(Messages.class) {
            this.init();
         }
      }

      try {
         MessageFormat mf = (MessageFormat)this.formats.get(message);
         if (mf == null) {
            String msg;
            try {
               msg = this.messages.getString(message);
            } catch (MissingResourceException var7) {
               return message + (args != null ? " " + Arrays.toString(args) : "");
            }

            mf = new MessageFormat(msg);
            this.formats.put(message, mf);
         }

         return mf.format(args);
      } catch (Exception var8) {
         return "An internal error occured while processing message " + message;
      }
   }

   public String message(String message) {
      this.init();
      if (this.locale != _globalLocale) {
         synchronized(Messages.class) {
            this.init();
         }
      }

      try {
         return this.messages.getString(message);
      } catch (MissingResourceException var4) {
         return message;
      }
   }

   public static void setLocale(Locale locale) {
      synchronized(Messages.class) {
         _globalLocale = locale;
         _rbBundles = new Hashtable();
         _rbFormats = new Hashtable();
      }
   }

   static {
      setLocale(Locale.getDefault());
   }

   private static final class EmptyResourceBundle extends ResourceBundle implements Enumeration {
      private EmptyResourceBundle() {
      }

      public Enumeration getKeys() {
         return this;
      }

      protected Object handleGetObject(String name) {
         return "[Missing message " + name + "]";
      }

      public boolean hasMoreElements() {
         return false;
      }

      public Object nextElement() {
         return null;
      }
   }
}
