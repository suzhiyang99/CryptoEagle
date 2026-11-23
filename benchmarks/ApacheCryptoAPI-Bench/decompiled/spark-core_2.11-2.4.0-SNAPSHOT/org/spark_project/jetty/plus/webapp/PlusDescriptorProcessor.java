package org.spark_project.jetty.plus.webapp;

import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import org.spark_project.jetty.jndi.NamingUtil;
import org.spark_project.jetty.plus.annotation.Injection;
import org.spark_project.jetty.plus.annotation.InjectionCollection;
import org.spark_project.jetty.plus.annotation.LifeCycleCallback;
import org.spark_project.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.spark_project.jetty.plus.annotation.PostConstructCallback;
import org.spark_project.jetty.plus.annotation.PreDestroyCallback;
import org.spark_project.jetty.plus.annotation.RunAsCollection;
import org.spark_project.jetty.plus.jndi.EnvEntry;
import org.spark_project.jetty.plus.jndi.Link;
import org.spark_project.jetty.plus.jndi.NamingEntry;
import org.spark_project.jetty.plus.jndi.NamingEntryUtil;
import org.spark_project.jetty.util.TypeUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.webapp.Descriptor;
import org.spark_project.jetty.webapp.FragmentDescriptor;
import org.spark_project.jetty.webapp.IterativeDescriptorProcessor;
import org.spark_project.jetty.webapp.Origin;
import org.spark_project.jetty.webapp.WebAppContext;
import org.spark_project.jetty.xml.XmlParser;

public class PlusDescriptorProcessor extends IterativeDescriptorProcessor {
   private static final Logger LOG = Log.getLogger(PlusDescriptorProcessor.class);

   public PlusDescriptorProcessor() {
      try {
         this.registerVisitor("env-entry", this.getClass().getMethod("visitEnvEntry", __signature));
         this.registerVisitor("resource-ref", this.getClass().getMethod("visitResourceRef", __signature));
         this.registerVisitor("resource-env-ref", this.getClass().getMethod("visitResourceEnvRef", __signature));
         this.registerVisitor("message-destination-ref", this.getClass().getMethod("visitMessageDestinationRef", __signature));
         this.registerVisitor("post-construct", this.getClass().getMethod("visitPostConstruct", __signature));
         this.registerVisitor("pre-destroy", this.getClass().getMethod("visitPreDestroy", __signature));
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   public void start(WebAppContext context, Descriptor descriptor) {
      InjectionCollection injections = (InjectionCollection)context.getAttribute("org.spark_project.jetty.injectionCollection");
      if (injections == null) {
         injections = new InjectionCollection();
         context.setAttribute("org.spark_project.jetty.injectionCollection", injections);
      }

      LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection");
      if (callbacks == null) {
         callbacks = new LifeCycleCallbackCollection();
         context.setAttribute("org.spark_project.jetty.lifecyleCallbackCollection", callbacks);
      }

      RunAsCollection runAsCollection = (RunAsCollection)context.getAttribute("org.spark_project.jetty.runAsCollection");
      if (runAsCollection == null) {
         runAsCollection = new RunAsCollection();
         context.setAttribute("org.spark_project.jetty.runAsCollection", runAsCollection);
      }

   }

   public void end(WebAppContext context, Descriptor descriptor) {
   }

   public void visitEnvEntry(WebAppContext context, Descriptor descriptor, XmlParser.Node node) throws Exception {
      String name = node.getString("env-entry-name", false, true);
      String type = node.getString("env-entry-type", false, true);
      String valueStr = node.getString("env-entry-value", false, true);
      if (valueStr != null && !valueStr.equals("")) {
         Origin o = context.getMetaData().getOrigin("env-entry." + name);
         switch (o) {
            case NotSet:
               context.getMetaData().setOrigin("env-entry." + name, descriptor);
               this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
               Object value = TypeUtil.valueOf(type, valueStr);
               this.bindEnvEntry(name, value);
               break;
            case WebXml:
            case WebDefaults:
            case WebOverride:
               if (!(descriptor instanceof FragmentDescriptor)) {
                  context.getMetaData().setOrigin("env-entry." + name, descriptor);
                  this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
                  Object value = TypeUtil.valueOf(type, valueStr);
                  this.bindEnvEntry(name, value);
               } else {
                  Descriptor d = context.getMetaData().getOriginDescriptor("env-entry." + name + ".injection");
                  if (d == null || d instanceof FragmentDescriptor) {
                     this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
                  }
               }
               break;
            case WebFragment:
               throw new IllegalStateException("Conflicting env-entry " + name + " in " + descriptor.getResource());
         }

      } else {
         LOG.warn("No value for env-entry-name " + name);
      }
   }

   public void visitResourceRef(WebAppContext context, Descriptor descriptor, XmlParser.Node node) throws Exception {
      String jndiName = node.getString("res-ref-name", false, true);
      String type = node.getString("res-type", false, true);
      String auth = node.getString("res-auth", false, true);
      String shared = node.getString("res-sharing-scope", false, true);
      Origin o = context.getMetaData().getOrigin("resource-ref." + jndiName);
      switch (o) {
         case NotSet:
            context.getMetaData().setOrigin("resource-ref." + jndiName, descriptor);
            Class<?> typeClass = TypeUtil.fromName(type);
            if (typeClass == null) {
               typeClass = context.loadClass(type);
            }

            this.addInjections(context, descriptor, node, jndiName, typeClass);
            this.bindResourceRef(context, jndiName, typeClass);
            break;
         case WebXml:
         case WebDefaults:
         case WebOverride:
            if (!(descriptor instanceof FragmentDescriptor)) {
               context.getMetaData().setOrigin("resource-ref." + jndiName, descriptor);
               Class<?> typeClass = TypeUtil.fromName(type);
               if (typeClass == null) {
                  typeClass = context.loadClass(type);
               }

               this.addInjections(context, descriptor, node, jndiName, typeClass);
               this.bindResourceRef(context, jndiName, typeClass);
            } else {
               Descriptor d = context.getMetaData().getOriginDescriptor("resource-ref." + jndiName + ".injection");
               if (d == null || d instanceof FragmentDescriptor) {
                  Class<?> typeClass = TypeUtil.fromName(type);
                  if (typeClass == null) {
                     context.loadClass(type);
                  }

                  this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
               }
            }
            break;
         case WebFragment:
            Descriptor otherFragment = context.getMetaData().getOriginDescriptor("resource-ref." + jndiName);
            XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
            Iterator<Object> iter = otherFragmentRoot.iterator();
            XmlParser.Node otherNode = null;

            while(iter.hasNext() && otherNode == null) {
               Object obj = iter.next();
               if (obj instanceof XmlParser.Node) {
                  XmlParser.Node n = (XmlParser.Node)obj;
                  if ("resource-ref".equals(n.getTag()) && jndiName.equals(n.getString("res-ref-name", false, true))) {
                     otherNode = n;
                  }
               }
            }

            if (otherNode == null) {
               throw new IllegalStateException("resource-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
            }

            String otherType = otherNode.getString("res-type", false, true);
            String otherAuth = otherNode.getString("res-auth", false, true);
            String otherShared = otherNode.getString("res-sharing-scope", false, true);
            type = type == null ? "" : type;
            otherType = otherType == null ? "" : otherType;
            auth = auth == null ? "" : auth;
            otherAuth = otherAuth == null ? "" : otherAuth;
            shared = shared == null ? "" : shared;
            otherShared = otherShared == null ? "" : otherShared;
            if (!type.equals(otherType) || !auth.equals(otherAuth) || !shared.equals(otherShared)) {
               throw new IllegalStateException("Conflicting resource-ref " + jndiName + " in " + descriptor.getResource());
            }

            this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
      }

   }

   public void visitResourceEnvRef(WebAppContext context, Descriptor descriptor, XmlParser.Node node) throws Exception {
      String jndiName = node.getString("resource-env-ref-name", false, true);
      String type = node.getString("resource-env-ref-type", false, true);
      Origin o = context.getMetaData().getOrigin("resource-env-ref." + jndiName);
      switch (o) {
         case NotSet:
            Class<?> typeClass = TypeUtil.fromName(type);
            if (typeClass == null) {
               typeClass = context.loadClass(type);
            }

            this.addInjections(context, descriptor, node, jndiName, typeClass);
            this.bindResourceEnvRef(context, jndiName, typeClass);
            break;
         case WebXml:
         case WebDefaults:
         case WebOverride:
            if (!(descriptor instanceof FragmentDescriptor)) {
               context.getMetaData().setOrigin("resource-env-ref." + jndiName, descriptor);
               Class<?> typeClass = TypeUtil.fromName(type);
               if (typeClass == null) {
                  typeClass = context.loadClass(type);
               }

               this.addInjections(context, descriptor, node, jndiName, typeClass);
               this.bindResourceEnvRef(context, jndiName, typeClass);
            } else {
               Descriptor d = context.getMetaData().getOriginDescriptor("resource-env-ref." + jndiName + ".injection");
               if (d == null || d instanceof FragmentDescriptor) {
                  Class<?> typeClass = TypeUtil.fromName(type);
                  if (typeClass == null) {
                     typeClass = context.loadClass(type);
                  }

                  this.addInjections(context, descriptor, node, jndiName, typeClass);
               }
            }
            break;
         case WebFragment:
            Descriptor otherFragment = context.getMetaData().getOriginDescriptor("resource-env-ref." + jndiName);
            XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
            Iterator<Object> iter = otherFragmentRoot.iterator();
            XmlParser.Node otherNode = null;

            while(iter.hasNext() && otherNode == null) {
               Object obj = iter.next();
               if (obj instanceof XmlParser.Node) {
                  XmlParser.Node n = (XmlParser.Node)obj;
                  if ("resource-env-ref".equals(n.getTag()) && jndiName.equals(n.getString("resource-env-ref-name", false, true))) {
                     otherNode = n;
                  }
               }
            }

            if (otherNode == null) {
               throw new IllegalStateException("resource-env-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
            }

            String otherType = otherNode.getString("resource-env-ref-type", false, true);
            type = type == null ? "" : type;
            otherType = otherType == null ? "" : otherType;
            if (!type.equals(otherType)) {
               throw new IllegalStateException("Conflicting resource-env-ref " + jndiName + " in " + descriptor.getResource());
            }

            this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
      }

   }

   public void visitMessageDestinationRef(WebAppContext context, Descriptor descriptor, XmlParser.Node node) throws Exception {
      String jndiName = node.getString("message-destination-ref-name", false, true);
      String type = node.getString("message-destination-type", false, true);
      String usage = node.getString("message-destination-usage", false, true);
      Origin o = context.getMetaData().getOrigin("message-destination-ref." + jndiName);
      switch (o) {
         case NotSet:
            Class<?> typeClass = TypeUtil.fromName(type);
            if (typeClass == null) {
               typeClass = context.loadClass(type);
            }

            this.addInjections(context, descriptor, node, jndiName, typeClass);
            this.bindMessageDestinationRef(context, jndiName, typeClass);
            context.getMetaData().setOrigin("message-destination-ref." + jndiName, descriptor);
            break;
         case WebXml:
         case WebDefaults:
         case WebOverride:
            if (!(descriptor instanceof FragmentDescriptor)) {
               Class<?> typeClass = TypeUtil.fromName(type);
               if (typeClass == null) {
                  typeClass = context.loadClass(type);
               }

               this.addInjections(context, descriptor, node, jndiName, typeClass);
               this.bindMessageDestinationRef(context, jndiName, typeClass);
               context.getMetaData().setOrigin("message-destination-ref." + jndiName, descriptor);
            } else {
               Descriptor d = context.getMetaData().getOriginDescriptor("message-destination-ref." + jndiName + ".injection");
               if (d == null || d instanceof FragmentDescriptor) {
                  Class<?> typeClass = TypeUtil.fromName(type);
                  if (typeClass == null) {
                     typeClass = context.loadClass(type);
                  }

                  this.addInjections(context, descriptor, node, jndiName, typeClass);
               }
            }
            break;
         case WebFragment:
            Descriptor otherFragment = context.getMetaData().getOriginDescriptor("message-destination-ref." + jndiName);
            XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
            Iterator<Object> iter = otherFragmentRoot.iterator();
            XmlParser.Node otherNode = null;

            while(iter.hasNext() && otherNode == null) {
               Object obj = iter.next();
               if (obj instanceof XmlParser.Node) {
                  XmlParser.Node n = (XmlParser.Node)obj;
                  if ("message-destination-ref".equals(n.getTag()) && jndiName.equals(n.getString("message-destination-ref-name", false, true))) {
                     otherNode = n;
                  }
               }
            }

            if (otherNode == null) {
               throw new IllegalStateException("message-destination-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
            }

            String otherType = node.getString("message-destination-type", false, true);
            String otherUsage = node.getString("message-destination-usage", false, true);
            type = type == null ? "" : type;
            usage = usage == null ? "" : usage;
            if (!type.equals(otherType) || !usage.equalsIgnoreCase(otherUsage)) {
               throw new IllegalStateException("Conflicting message-destination-ref " + jndiName + " in " + descriptor.getResource());
            }

            this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
      }

   }

   public void visitPostConstruct(WebAppContext context, Descriptor descriptor, XmlParser.Node node) {
      String className = node.getString("lifecycle-callback-class", false, true);
      String methodName = node.getString("lifecycle-callback-method", false, true);
      if (className != null && !className.equals("")) {
         if (methodName != null && !methodName.equals("")) {
            Origin o = context.getMetaData().getOrigin("post-construct");
            switch (o) {
               case NotSet:
                  context.getMetaData().setOrigin("post-construct", descriptor);

                  try {
                     Class<?> clazz = context.loadClass(className);
                     LifeCycleCallback callback = new PostConstructCallback();
                     callback.setTarget(clazz, methodName);
                     ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                  } catch (ClassNotFoundException var11) {
                     LOG.warn("Couldn't load post-construct target class " + className);
                  }
                  break;
               case WebXml:
               case WebDefaults:
               case WebOverride:
                  if (!(descriptor instanceof FragmentDescriptor)) {
                     try {
                        Class<?> clazz = context.loadClass(className);
                        LifeCycleCallback callback = new PostConstructCallback();
                        callback.setTarget(clazz, methodName);
                        ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                     } catch (ClassNotFoundException var10) {
                        LOG.warn("Couldn't load post-construct target class " + className);
                     }
                  }
                  break;
               case WebFragment:
                  try {
                     Class<?> clazz = context.loadClass(className);
                     LifeCycleCallback callback = new PostConstructCallback();
                     callback.setTarget(clazz, methodName);
                     ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                  } catch (ClassNotFoundException var9) {
                     LOG.warn("Couldn't load post-construct target class " + className);
                  }
            }

         } else {
            LOG.warn("No lifecycle-callback-method specified for class " + className);
         }
      } else {
         LOG.warn("No lifecycle-callback-class specified");
      }
   }

   public void visitPreDestroy(WebAppContext context, Descriptor descriptor, XmlParser.Node node) {
      String className = node.getString("lifecycle-callback-class", false, true);
      String methodName = node.getString("lifecycle-callback-method", false, true);
      if (className != null && !className.equals("")) {
         if (methodName != null && !methodName.equals("")) {
            Origin o = context.getMetaData().getOrigin("pre-destroy");
            switch (o) {
               case NotSet:
                  context.getMetaData().setOrigin("pre-destroy", descriptor);

                  try {
                     Class<?> clazz = context.loadClass(className);
                     LifeCycleCallback callback = new PreDestroyCallback();
                     callback.setTarget(clazz, methodName);
                     ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                  } catch (ClassNotFoundException var11) {
                     LOG.warn("Couldn't load pre-destory target class " + className);
                  }
                  break;
               case WebXml:
               case WebDefaults:
               case WebOverride:
                  if (!(descriptor instanceof FragmentDescriptor)) {
                     try {
                        Class<?> clazz = context.loadClass(className);
                        LifeCycleCallback callback = new PreDestroyCallback();
                        callback.setTarget(clazz, methodName);
                        ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                     } catch (ClassNotFoundException var10) {
                        LOG.warn("Couldn't load pre-destory target class " + className);
                     }
                  }
                  break;
               case WebFragment:
                  try {
                     Class<?> clazz = context.loadClass(className);
                     LifeCycleCallback callback = new PreDestroyCallback();
                     callback.setTarget(clazz, methodName);
                     ((LifeCycleCallbackCollection)context.getAttribute("org.spark_project.jetty.lifecyleCallbackCollection")).add(callback);
                  } catch (ClassNotFoundException var9) {
                     LOG.warn("Couldn't load pre-destory target class " + className);
                  }
            }

         } else {
            LOG.warn("No lifecycle-callback-method specified for pre-destroy class " + className);
         }
      } else {
         LOG.warn("No lifecycle-callback-class specified for pre-destroy");
      }
   }

   public void addInjections(WebAppContext context, Descriptor descriptor, XmlParser.Node node, String jndiName, Class<?> valueClass) {
      Iterator<XmlParser.Node> itor = node.iterator("injection-target");

      while(itor.hasNext()) {
         XmlParser.Node injectionNode = (XmlParser.Node)itor.next();
         String targetClassName = injectionNode.getString("injection-target-class", false, true);
         String targetName = injectionNode.getString("injection-target-name", false, true);
         if (targetClassName != null && !targetClassName.equals("")) {
            if (targetName != null && !targetName.equals("")) {
               InjectionCollection injections = (InjectionCollection)context.getAttribute("org.spark_project.jetty.injectionCollection");
               if (injections == null) {
                  injections = new InjectionCollection();
                  context.setAttribute("org.spark_project.jetty.injectionCollection", injections);
               }

               try {
                  Class<?> clazz = context.loadClass(targetClassName);
                  Injection injection = new Injection();
                  injection.setJndiName(jndiName);
                  injection.setTarget(clazz, targetName, valueClass);
                  injections.add(injection);
                  if (context.getMetaData().getOriginDescriptor(node.getTag() + "." + jndiName + ".injection") == null) {
                     context.getMetaData().setOrigin(node.getTag() + "." + jndiName + ".injection", descriptor);
                  }
               } catch (ClassNotFoundException var13) {
                  LOG.warn("Couldn't load injection target class " + targetClassName);
               }
            } else {
               LOG.warn("No field or method name in injection-target");
            }
         } else {
            LOG.warn("No classname found in injection-target");
         }
      }

   }

   public void bindEnvEntry(String name, Object value) throws Exception {
      InitialContext ic = null;
      boolean bound = false;
      ic = new InitialContext();

      try {
         NamingEntry ne = (NamingEntry)ic.lookup("java:comp/env/" + NamingEntryUtil.makeNamingEntryName(ic.getNameParser(""), name));
         if (ne != null && ne instanceof EnvEntry) {
            EnvEntry ee = (EnvEntry)ne;
            bound = ee.isOverrideWebXml();
         }
      } catch (NameNotFoundException var7) {
         bound = false;
      }

      if (!bound) {
         Context envCtx = (Context)ic.lookup("java:comp/env");
         NamingUtil.bind(envCtx, name, value);
      }

   }

   public void bindResourceRef(WebAppContext context, String name, Class<?> typeClass) throws Exception {
      this.bindEntry(context, name, typeClass);
   }

   public void bindResourceEnvRef(WebAppContext context, String name, Class<?> typeClass) throws Exception {
      this.bindEntry(context, name, typeClass);
   }

   public void bindMessageDestinationRef(WebAppContext context, String name, Class<?> typeClass) throws Exception {
      this.bindEntry(context, name, typeClass);
   }

   protected void bindEntry(WebAppContext context, String name, Class<?> typeClass) throws Exception {
      String nameInEnvironment = name;
      boolean bound = false;
      NamingEntry ne = NamingEntryUtil.lookupNamingEntry(context, name);
      if (ne != null && ne instanceof Link) {
         nameInEnvironment = ((Link)ne).getLink();
      }

      bound = NamingEntryUtil.bindToENC(context, name, nameInEnvironment);
      if (!bound) {
         Object scope = context.getServer();
         bound = NamingEntryUtil.bindToENC(scope, name, nameInEnvironment);
         if (!bound) {
            bound = NamingEntryUtil.bindToENC((Object)null, name, nameInEnvironment);
            if (!bound) {
               nameInEnvironment = typeClass.getName() + "/default";
               NamingEntry defaultNE = NamingEntryUtil.lookupNamingEntry(context.getServer(), nameInEnvironment);
               if (defaultNE == null) {
                  defaultNE = NamingEntryUtil.lookupNamingEntry((Object)null, nameInEnvironment);
               }

               if (defaultNE != null) {
                  defaultNE.bindToENC(name);
               } else {
                  throw new IllegalStateException("Nothing to bind for name " + name);
               }
            }
         }
      }
   }
}
