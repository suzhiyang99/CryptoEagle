package org.apache.openejb.core.ivm.naming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import org.apache.openejb.AppContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Strings;

public class ContextualJndiReference extends IntraVmJndiReference {
   public static final ThreadLocal<Boolean> followReference = new ThreadLocal<Boolean>() {
      public Boolean initialValue() {
         return true;
      }
   };
   private Object defaultValue;
   private final List<String> prefixes = new CopyOnWriteArrayList();

   public ContextualJndiReference(String jndiName) {
      super(jndiName);
   }

   public void setDefaultValue(Object defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void addPrefix(String value) {
      if (value != null) {
         this.prefixes.add(value);
      }

   }

   public void removePrefix(String value) {
      if (value != null) {
         this.prefixes.remove(value);
      }

   }

   public String lastPrefix() {
      return this.prefixes.isEmpty() ? null : (String)this.prefixes.get(this.prefixes.size() - 1);
   }

   public boolean hasNoMorePrefix() {
      return this.prefixes.isEmpty();
   }

   public int prefixesSize() {
      return this.prefixes.size();
   }

   public Object getObject() throws javax.naming.NamingException {
      Object delegate = this.findDelegate();
      return Reference.class.isInstance(delegate) ? ((Reference)Reference.class.cast(delegate)).getObject() : delegate;
   }

   private Object findDelegate() throws NameNotFoundException {
      Boolean rawValue = !(Boolean)followReference.get();
      followReference.remove();
      if (rawValue) {
         return this;
      } else {
         String prefix = this.findPrefix();
         String jndiName = this.getJndiName();
         if (prefix != null && !prefix.isEmpty()) {
            try {
               return this.lookup(prefix + '/' + jndiName);
            } catch (javax.naming.NamingException var9) {
            }
         }

         Collection<Object> values = new ArrayList();

         for(String p : this.prefixes) {
            if (p != null && !p.isEmpty()) {
               try {
                  values.add(this.lookup(p + '/' + jndiName));
               } catch (javax.naming.NamingException var8) {
               }
            }
         }

         if (1 == values.size()) {
            return values.iterator().next();
         } else if (!values.isEmpty()) {
            throw new NameNotFoundException("Ambiguous resource '" + this.getJndiName() + "'  for classloader " + Thread.currentThread().getContextClassLoader());
         } else {
            return this.defaultValue;
         }
      }
   }

   private String findPrefix() {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

      for(AppContext appContext : containerSystem.getAppContexts()) {
         if (appContext.getClassLoader().equals(loader)) {
            return appContext.getId();
         }

         for(WebContext web : appContext.getWebContexts()) {
            if (web.getClassLoader().equals(loader)) {
               return appContext.getId();
            }
         }
      }

      if (1 == containerSystem.getAppContexts().size()) {
         return ((AppContext)containerSystem.getAppContexts().iterator().next()).getId();
      } else {
         return null;
      }
   }

   private Object lookup(String s) throws javax.naming.NamingException {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      Context jndiContext = containerSystem.getJNDIContext();

      try {
         return s.startsWith("java:") | s.startsWith("openejb:") ? jndiContext.lookup(s) : jndiContext.lookup("openejb/Resource/" + s);
      } catch (NameNotFoundException var5) {
         return jndiContext.lookup("java:module/" + Strings.lastPart(this.getClassName(), '.'));
      } catch (javax.naming.NamingException e) {
         throw (javax.naming.NamingException)(new javax.naming.NamingException("could not look up " + s)).initCause(e);
      }
   }

   public String toString() {
      return "ContextualJndiReference{jndiName='" + this.getJndiName() + '\'' + '}';
   }
}
