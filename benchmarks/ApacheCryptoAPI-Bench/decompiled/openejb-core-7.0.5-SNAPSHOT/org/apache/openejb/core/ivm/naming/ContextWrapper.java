package org.apache.openejb.core.ivm.naming;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;

public class ContextWrapper implements Context {
   protected final Context context;

   public ContextWrapper(Context context) {
      this.context = context;
   }

   public Object addToEnvironment(String propName, Object propVal) throws javax.naming.NamingException {
      return this.context.addToEnvironment(propName, propVal);
   }

   public void bind(Name name, Object obj) throws javax.naming.NamingException {
      this.context.bind(name, obj);
   }

   public void bind(String name, Object obj) throws javax.naming.NamingException {
      this.context.bind(name, obj);
   }

   public void close() throws javax.naming.NamingException {
      this.context.close();
   }

   public Name composeName(Name name, Name prefix) throws javax.naming.NamingException {
      return this.context.composeName(name, prefix);
   }

   public String composeName(String name, String prefix) throws javax.naming.NamingException {
      return this.context.composeName(name, prefix);
   }

   public Context createSubcontext(Name name) throws javax.naming.NamingException {
      return this.context.createSubcontext(name);
   }

   public Context createSubcontext(String name) throws javax.naming.NamingException {
      return this.context.createSubcontext(name);
   }

   public void destroySubcontext(Name name) throws javax.naming.NamingException {
      this.context.destroySubcontext(name);
   }

   public void destroySubcontext(String name) throws javax.naming.NamingException {
      this.context.destroySubcontext(name);
   }

   public Hashtable<?, ?> getEnvironment() throws javax.naming.NamingException {
      return this.context.getEnvironment();
   }

   public String getNameInNamespace() throws javax.naming.NamingException {
      return this.context.getNameInNamespace();
   }

   public NameParser getNameParser(Name name) throws javax.naming.NamingException {
      return this.context.getNameParser(name);
   }

   public NameParser getNameParser(String name) throws javax.naming.NamingException {
      return this.context.getNameParser(name);
   }

   public NamingEnumeration<NameClassPair> list(Name name) throws javax.naming.NamingException {
      return this.context.list(name);
   }

   public NamingEnumeration<NameClassPair> list(String name) throws javax.naming.NamingException {
      return this.context.list(name);
   }

   public NamingEnumeration<Binding> listBindings(Name name) throws javax.naming.NamingException {
      return this.context.listBindings(name);
   }

   public NamingEnumeration<Binding> listBindings(String name) throws javax.naming.NamingException {
      return this.context.listBindings(name);
   }

   public Object lookup(Name name) throws javax.naming.NamingException {
      return this.context.lookup(name);
   }

   public Object lookup(String name) throws javax.naming.NamingException {
      return this.context.lookup(name);
   }

   public Object lookupLink(Name name) throws javax.naming.NamingException {
      return this.context.lookupLink(name);
   }

   public Object lookupLink(String name) throws javax.naming.NamingException {
      return this.context.lookupLink(name);
   }

   public void rebind(Name name, Object obj) throws javax.naming.NamingException {
      this.context.rebind(name, obj);
   }

   public void rebind(String name, Object obj) throws javax.naming.NamingException {
      this.context.rebind(name, obj);
   }

   public Object removeFromEnvironment(String propName) throws javax.naming.NamingException {
      return this.context.removeFromEnvironment(propName);
   }

   public void rename(Name oldName, Name newName) throws javax.naming.NamingException {
      this.context.rename(oldName, newName);
   }

   public void rename(String oldName, String newName) throws javax.naming.NamingException {
      this.context.rename(oldName, newName);
   }

   public void unbind(Name name) throws javax.naming.NamingException {
      this.context.unbind(name);
   }

   public void unbind(String name) throws javax.naming.NamingException {
      this.context.unbind(name);
   }
}
