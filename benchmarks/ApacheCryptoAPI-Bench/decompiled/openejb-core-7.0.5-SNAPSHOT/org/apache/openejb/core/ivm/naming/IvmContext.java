package org.apache.openejb.core.ivm.naming;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.ObjectFactory;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.core.ivm.IntraVmCopyMonitor;
import org.apache.openejb.core.ivm.IntraVmProxy;
import org.apache.openejb.core.ivm.naming.java.javaURLContextFactory;
import org.apache.openejb.core.ivm.naming.openejb.openejbURLContextFactory;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.xbean.naming.context.ContextUtil;

public class IvmContext implements Context, Serializable {
   private static final long serialVersionUID = -626353930051783641L;
   Hashtable<String, Object> myEnv;
   boolean readOnly;
   Map<String, Object> fastCache;
   static final String JNDI_EXCEPTION_ON_FAILED_WRITE = "openejb.jndiExceptionOnFailedWrite";
   public NameNode mynode;
   static ObjectFactory[] federatedFactories;

   public static IvmContext createRootContext() {
      return new IvmContext();
   }

   public IvmContext() {
      this(new NameNode((NameNode)null, new ParsedName(""), (Object)null, (NameNode)null));
   }

   public IvmContext(String nodeName) {
      this(new NameNode((NameNode)null, new ParsedName(nodeName), (Object)null, (NameNode)null));
   }

   public IvmContext(NameNode node) {
      this(node, false);
   }

   public IvmContext(NameNode node, boolean isReadOnly) {
      this.fastCache = new ConcurrentHashMap();
      this.mynode = node;
      this.readOnly = isReadOnly;
   }

   public IvmContext(Hashtable<String, Object> environment) throws javax.naming.NamingException {
      this();
      if (environment == null) {
         throw new javax.naming.NamingException("Invalid Argument");
      } else {
         this.myEnv = (Hashtable)environment.clone();
      }
   }

   public Object lookup(String compositName) throws javax.naming.NamingException {
      if (compositName.equals("")) {
         return this;
      } else {
         int index = compositName.indexOf(":");
         if (index > -1) {
            String prefix = compositName.substring(0, index);
            String path = compositName.substring(index + 1);
            ParsedName name = new ParsedName(path);
            if (prefix.equals("openejb")) {
               path = name.path();
               return openejbURLContextFactory.getContext().lookup(path);
            } else if (prefix.equals("java")) {
               if (name.getComponent().equals("openejb")) {
                  path = name.remaining().path();
                  return openejbURLContextFactory.getContext().lookup(path);
               } else {
                  path = name.path();
                  return javaURLContextFactory.getContext().lookup(path);
               }
            } else {
               throw new javax.naming.NamingException("Unknown JNDI name prefix '" + prefix + ":'");
            }
         } else {
            String compoundName = this.mynode.getAtomicName() + '/' + compositName;
            Object obj = this.fastCache.get(compoundName);
            if (obj == null) {
               try {
                  obj = this.mynode.resolve(new ParsedName(compoundName), this.readOnly);
               } catch (NameNotFoundException var7) {
                  obj = this.federate(compositName);
               }

               if (!(obj instanceof IntraVmProxy) && !(obj instanceof ContextualJndiReference)) {
                  this.fastCache.put(compoundName, obj);
               }
            }

            if (obj == null) {
               throw new NameNotFoundException("Name \"" + compositName + "\" not found.");
            } else {
               if (obj.getClass() == IvmContext.class) {
                  ((IvmContext)obj).myEnv = this.myEnv;
               } else if (obj instanceof Reference) {
                  obj = ((Reference)obj).getObject();
               } else if (obj instanceof LinkRef) {
                  obj = this.lookup(((LinkRef)obj).getLinkName());
               }

               return obj;
            }
         }
      }
   }

   protected Object federate(String compositName) throws javax.naming.NamingException {
      ObjectFactory[] factories = getFederatedFactories();

      for(ObjectFactory factory : factories) {
         try {
            CompositeName name = new CompositeName(compositName);
            Object obj = factory.getObjectInstance((Object)null, name, (Context)null, (Hashtable)null);
            if (obj instanceof Context) {
               return ((Context)obj).lookup(compositName);
            }

            if (obj != null) {
               return obj;
            }
         } catch (Exception var9) {
         }
      }

      throw new NameNotFoundException("Name \"" + compositName + "\" not found.");
   }

   public static ObjectFactory[] getFederatedFactories() throws javax.naming.NamingException {
      if (federatedFactories == null) {
         Set<ObjectFactory> factories = new HashSet();
         String urlPackagePrefixes = getUrlPackagePrefixes();
         if (urlPackagePrefixes == null) {
            return new ObjectFactory[0];
         }

         StringTokenizer tokenizer = new StringTokenizer(urlPackagePrefixes, ":");

         while(tokenizer.hasMoreTokens()) {
            String urlPackagePrefix = tokenizer.nextToken();
            String className = urlPackagePrefix + ".java.javaURLContextFactory";
            if (!className.equals("org.apache.openejb.core.ivm.naming.java.javaURLContextFactory")) {
               try {
                  ClassLoader cl = ClassLoaderUtil.getContextClassLoader();
                  Class factoryClass = Class.forName(className, true, cl);
                  ObjectFactory factoryInstance = (ObjectFactory)factoryClass.newInstance();
                  factories.add(factoryInstance);
               } catch (ClassNotFoundException var8) {
               } catch (Throwable e) {
                  javax.naming.NamingException ne = new javax.naming.NamingException("Federation failed: Cannot instantiate " + className);
                  ne.setRootCause(e);
                  throw ne;
               }
            }
         }

         Object[] temp = factories.toArray();
         federatedFactories = new ObjectFactory[temp.length];
         System.arraycopy(temp, 0, federatedFactories, 0, federatedFactories.length);
      }

      return federatedFactories;
   }

   private static String getUrlPackagePrefixes() {
      String urlPackagePrefixes = JavaSecurityManagers.getSystemProperty("java.naming.factory.url.pkgs");
      if (urlPackagePrefixes == null) {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
         }

         InputStream in;
         try {
            for(Enumeration<URL> resources = classLoader.getResources("jndi.properties"); urlPackagePrefixes == null && resources.hasMoreElements(); urlPackagePrefixes = getUrlPackagePrefixes(in)) {
               URL resource = (URL)resources.nextElement();
               in = IO.read(resource);
            }
         } catch (IOException var11) {
         }
      }

      if (urlPackagePrefixes == null) {
         String javahome = JavaSecurityManagers.getSystemProperty("java.home");
         if (javahome != null) {
            InputStream in = null;

            try {
               File propertiesFile = new File(new File(javahome, "lib"), "jndi.properties");
               in = IO.read(propertiesFile);
               urlPackagePrefixes = getUrlPackagePrefixes(in);
            } catch (FileNotFoundException var9) {
            } finally {
               IO.close(in);
            }
         }
      }

      return urlPackagePrefixes;
   }

   private static String getUrlPackagePrefixes(InputStream in) {
      try {
         Properties properties = IO.readProperties(in, new Properties());
         return properties.getProperty("java.naming.factory.url.pkgs");
      } catch (IOException var2) {
         return null;
      }
   }

   public Object lookup(Name compositName) throws javax.naming.NamingException {
      return this.lookup(compositName.toString());
   }

   public void bind(String name, Object obj) throws javax.naming.NamingException {
      if (!this.checkReadOnly()) {
         int indx = name.indexOf(":");
         if (indx > -1) {
            name = name.substring(indx + 1);
         }

         if (this.fastCache.containsKey(name)) {
            throw new NameAlreadyBoundException();
         } else {
            ParsedName parsedName = this.getParsedNameFor(name);
            this.mynode.bind(parsedName, obj);
         }
      }
   }

   private ParsedName getParsedNameFor(String name) {
      return new ParsedName(this.mynode.getAtomicName() + "/" + name);
   }

   public void bind(Name name, Object obj) throws javax.naming.NamingException {
      this.bind(name.toString(), obj);
   }

   public void rebind(String name, Object obj) throws javax.naming.NamingException {
      try {
         this.unbind(name);
      } catch (NameNotFoundException var4) {
      }

      this.bind(name, obj);
   }

   public void rebind(Name name, Object obj) throws javax.naming.NamingException {
      this.rebind(name.toString(), obj);
   }

   public void unbind(String name) throws javax.naming.NamingException {
      if (!this.checkReadOnly()) {
         int indx = name.indexOf(":");
         if (indx > -1) {
            name = name.substring(indx + 1);
         }

         this.fastCache.clear();
         this.mynode.clearCache();
         this.mynode.unbind(this.getParsedNameFor(name));
      }
   }

   public void unbind(Name name) throws javax.naming.NamingException {
      this.unbind(name.toString());
   }

   public void prune(String name) throws javax.naming.NamingException {
      IvmContext ctx = (IvmContext)this.lookup(name);
      ctx.prune();
   }

   public void prune() throws javax.naming.NamingException {
      this.mynode.prune();
   }

   public void rename(String oldname, String newname) throws javax.naming.NamingException {
      throw new OperationNotSupportedException();
   }

   public void rename(Name oldname, Name newname) throws javax.naming.NamingException {
      this.rename(oldname.toString(), newname.toString());
   }

   public NamingEnumeration<NameClassPair> list(String name) throws javax.naming.NamingException {
      Object obj = this.lookup(name);
      return obj.getClass() == IvmContext.class ? new MyListEnumeration(((IvmContext)obj).mynode) : null;
   }

   public NamingEnumeration<NameClassPair> list(Name name) throws javax.naming.NamingException {
      return this.list(name.toString());
   }

   public NamingEnumeration<Binding> listBindings(String name) throws javax.naming.NamingException {
      Object obj = this.lookup(name);
      return obj.getClass() == IvmContext.class ? new MyBindingEnumeration(((IvmContext)obj).mynode) : null;
   }

   public NamingEnumeration<Binding> listBindings(Name name) throws javax.naming.NamingException {
      return this.listBindings(name.toString());
   }

   public void destroySubcontext(String name) throws javax.naming.NamingException {
      throw new OperationNotSupportedException();
   }

   public void destroySubcontext(Name name) throws javax.naming.NamingException {
      this.destroySubcontext(name.toString());
   }

   public Context createSubcontext(String name) throws javax.naming.NamingException {
      if (this.checkReadOnly()) {
         return null;
      } else {
         int indx = name.indexOf(":");
         if (indx > -1) {
            name = name.substring(indx + 1);
         }

         if (this.fastCache.containsKey(name)) {
            throw new NameAlreadyBoundException();
         } else {
            return this.mynode.createSubcontext(this.getParsedNameFor(name), this.readOnly);
         }
      }
   }

   public Context createSubcontext(Name name) throws javax.naming.NamingException {
      return this.createSubcontext(name.toString());
   }

   public Object lookupLink(String name) throws javax.naming.NamingException {
      return this.lookup(name);
   }

   public Object lookupLink(Name name) throws javax.naming.NamingException {
      return this.lookupLink(name.toString());
   }

   public NameParser getNameParser(String name) throws javax.naming.NamingException {
      return ContextUtil.NAME_PARSER;
   }

   public NameParser getNameParser(Name name) throws javax.naming.NamingException {
      return this.getNameParser(name.toString());
   }

   public String composeName(String name, String prefix) throws javax.naming.NamingException {
      Name result = this.composeName((Name)(new CompositeName(name)), (Name)(new CompositeName(prefix)));
      return result.toString();
   }

   public Name composeName(Name name, Name prefix) throws javax.naming.NamingException {
      Name result = (Name)prefix.clone();
      result.addAll(name);
      return result;
   }

   public Object addToEnvironment(String propName, Object propVal) throws javax.naming.NamingException {
      if (this.myEnv == null) {
         this.myEnv = new Hashtable(5);
      }

      return this.myEnv.put(propName, propVal);
   }

   public Object removeFromEnvironment(String propName) throws javax.naming.NamingException {
      return this.myEnv == null ? null : this.myEnv.remove(propName);
   }

   public Hashtable getEnvironment() throws javax.naming.NamingException {
      return this.myEnv == null ? new Hashtable(3) : (Hashtable)this.myEnv.clone();
   }

   public String getNameInNamespace() throws javax.naming.NamingException {
      return "";
   }

   public void close() throws javax.naming.NamingException {
      this.checkReadOnly();
   }

   protected boolean checkReadOnly() throws OperationNotSupportedException {
      if (this.readOnly) {
         if ("true".equals(SystemInstance.get().getProperty("openejb.jndiExceptionOnFailedWrite", "true"))) {
            throw new OperationNotSupportedException();
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public void setReadOnly(boolean isReadOnly) {
      this.readOnly = isReadOnly;
      if (this.mynode != null) {
         this.mynode.setReadOnly(this.readOnly);
      }

   }

   public void tree(PrintStream out) {
      this.mynode.tree("", out);
   }

   public String toString() {
      return "IvmContext{mynode=" + this.mynode.getAtomicName() + '}';
   }

   protected Object writeReplace() throws ObjectStreamException {
      if (IntraVmCopyMonitor.isStatefulPassivationOperation()) {
         return new JndiEncArtifact(this);
      } else if (IntraVmCopyMonitor.isCrossClassLoaderOperation()) {
         return new JndiEncArtifact(this);
      } else {
         throw new NotSerializableException("IntraVM java.naming.Context objects can not be passed as arguments");
      }
   }

   protected class MyBindingEnumeration extends MyNamingEnumeration {
      public MyBindingEnumeration(NameNode parentNode) {
         super(parentNode);
      }

      protected void buildEnumeration(Vector vect) {
         for(int i = 0; i < vect.size(); ++i) {
            NameNode node = (NameNode)vect.elementAt(i);
            String className = node.getBinding().getClass().getName();
            vect.setElementAt(new Binding(node.getAtomicName(), className, node.getBinding()), i);
         }

         this.myEnum = vect.elements();
      }
   }

   protected class MyListEnumeration extends MyNamingEnumeration {
      public MyListEnumeration(NameNode parentNode) {
         super(parentNode);
      }

      protected void buildEnumeration(Vector vect) {
         for(int i = 0; i < vect.size(); ++i) {
            NameNode node = (NameNode)vect.elementAt(i);
            String className = node.getBinding().getClass().getName();
            vect.setElementAt(new NameClassPair(node.getAtomicName(), className), i);
         }

         this.myEnum = vect.elements();
      }
   }

   protected abstract class MyNamingEnumeration implements NamingEnumeration {
      Enumeration myEnum;

      public MyNamingEnumeration(NameNode parentNode) {
         Vector vect = new Vector();
         NameNode node = parentNode.getSubTree();
         if (node == null) {
            node = parentNode;
         } else {
            vect.addElement(node);
         }

         this.gatherNodes(parentNode, node, vect);
         this.buildEnumeration(vect);
      }

      protected abstract void buildEnumeration(Vector<NameNode> var1);

      protected void gatherNodes(NameNode initiallyRequestedNode, NameNode node, Vector vect) {
         this.addInListIfNeeded(initiallyRequestedNode, node.getLessTree(), vect);
         this.addInListIfNeeded(initiallyRequestedNode, node.getGrtrTree(), vect);
         this.addInListIfNeeded(initiallyRequestedNode, node.getSubTree(), vect);
         if (NameNode.Federation.class.isInstance(initiallyRequestedNode.getObject())) {
            for(Context c : (NameNode.Federation)NameNode.Federation.class.cast(initiallyRequestedNode.getObject())) {
               if (c != IvmContext.this && IvmContext.class.isInstance(c)) {
                  IvmContext ctx = (IvmContext)IvmContext.class.cast(c);
                  if (ctx.mynode != node && !vect.contains(ctx.mynode)) {
                     this.addInListIfNeeded(ctx.mynode, ctx.mynode.getGrtrTree(), vect);
                     this.addInListIfNeeded(ctx.mynode, ctx.mynode.getLessTree(), vect);
                     this.addInListIfNeeded(ctx.mynode, ctx.mynode.getSubTree(), vect);
                  }
               }
            }
         }

      }

      private void addInListIfNeeded(NameNode parent, NameNode node, Vector vect) {
         if (node != null && !vect.contains(node) && this.isMyChild(parent, node)) {
            vect.addElement(node);
            this.gatherNodes(parent, node, vect);
         }
      }

      private boolean isMyChild(NameNode parent, NameNode node) {
         if (node.getParent() == parent) {
            return true;
         } else {
            return null == node.getParent() && null == parent.getParentTree();
         }
      }

      public void close() {
         this.myEnum = null;
      }

      public boolean hasMore() {
         return this.hasMoreElements();
      }

      public Object next() {
         return this.nextElement();
      }

      public boolean hasMoreElements() {
         return this.myEnum.hasMoreElements();
      }

      public Object nextElement() {
         return this.myEnum.nextElement();
      }
   }
}
