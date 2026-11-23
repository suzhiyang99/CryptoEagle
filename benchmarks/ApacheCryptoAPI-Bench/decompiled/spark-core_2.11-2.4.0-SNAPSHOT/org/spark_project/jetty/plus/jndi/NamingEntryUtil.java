package org.spark_project.jetty.plus.jndi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.spark_project.jetty.jndi.NamingUtil;
import org.spark_project.jetty.util.log.Logger;

public class NamingEntryUtil {
   private static Logger __log;

   public static boolean bindToENC(Object scope, String asName, String mappedName) throws NamingException {
      if (asName != null && !asName.trim().equals("")) {
         if (mappedName == null || "".equals(mappedName)) {
            mappedName = asName;
         }

         NamingEntry entry = lookupNamingEntry(scope, mappedName);
         if (entry == null) {
            return false;
         } else {
            entry.bindToENC(asName);
            return true;
         }
      } else {
         throw new NamingException("No name for NamingEntry");
      }
   }

   public static NamingEntry lookupNamingEntry(Object scope, String jndiName) throws NamingException {
      NamingEntry entry = null;

      try {
         Name scopeName = getNameForScope(scope);
         InitialContext ic = new InitialContext();
         NameParser parser = ic.getNameParser("");
         Name namingEntryName = makeNamingEntryName(parser, jndiName);
         scopeName.addAll(namingEntryName);
         entry = (NamingEntry)ic.lookup(scopeName);
      } catch (NameNotFoundException var7) {
      }

      return entry;
   }

   public static Object lookup(Object scope, String jndiName) throws NamingException {
      Name scopeName = getNameForScope(scope);
      InitialContext ic = new InitialContext();
      NameParser parser = ic.getNameParser("");
      scopeName.addAll(parser.parse(jndiName));
      return ic.lookup(scopeName);
   }

   public static List<Object> lookupNamingEntries(Object scope, Class<?> clazz) throws NamingException {
      try {
         Context scopeContext = getContextForScope(scope);
         Context namingEntriesContext = (Context)scopeContext.lookup("__");
         ArrayList<Object> list = new ArrayList();
         lookupNamingEntries(list, namingEntriesContext, clazz);
         return list;
      } catch (NameNotFoundException var5) {
         return Collections.emptyList();
      }
   }

   public static Name makeNamingEntryName(NameParser parser, NamingEntry namingEntry) throws NamingException {
      return makeNamingEntryName(parser, namingEntry == null ? null : namingEntry.getJndiName());
   }

   public static Name makeNamingEntryName(NameParser parser, String jndiName) throws NamingException {
      if (jndiName == null) {
         return null;
      } else {
         if (parser == null) {
            InitialContext ic = new InitialContext();
            parser = ic.getNameParser("");
         }

         Name name = parser.parse("");
         name.add("__");
         name.addAll(parser.parse(jndiName));
         return name;
      }
   }

   public static Name getNameForScope(Object scope) {
      try {
         InitialContext ic = new InitialContext();
         NameParser parser = ic.getNameParser("");
         Name name = parser.parse("");
         if (scope != null) {
            name.add(canonicalizeScope(scope));
         }

         return name;
      } catch (NamingException e) {
         __log.warn(e);
         return null;
      }
   }

   public static Context getContextForScope(Object scope) throws NamingException {
      InitialContext ic = new InitialContext();
      NameParser parser = ic.getNameParser("");
      Name name = parser.parse("");
      if (scope != null) {
         name.add(canonicalizeScope(scope));
      }

      return (Context)ic.lookup(name);
   }

   public static Context getContextForNamingEntries(Object scope) throws NamingException {
      Context scopeContext = getContextForScope(scope);
      return (Context)scopeContext.lookup("__");
   }

   private static List<Object> lookupNamingEntries(List<Object> list, Context context, Class<?> clazz) throws NamingException {
      try {
         NamingEnumeration<Binding> nenum = context.listBindings("");

         while(nenum.hasMoreElements()) {
            Binding binding = (Binding)nenum.next();
            if (binding.getObject() instanceof Context) {
               lookupNamingEntries(list, (Context)binding.getObject(), clazz);
            } else if (clazz.isInstance(binding.getObject())) {
               list.add(binding.getObject());
            }
         }
      } catch (NameNotFoundException var5) {
         __log.debug("No entries of type " + clazz.getName() + " in context=" + context);
      }

      return list;
   }

   private static String canonicalizeScope(Object scope) {
      if (scope == null) {
         return "";
      } else {
         String str = scope.getClass().getName() + "@" + Long.toHexString((long)scope.hashCode());
         str = str.replace('/', '_').replace(' ', '_');
         return str;
      }
   }

   static {
      __log = NamingUtil.__log;
   }
}
