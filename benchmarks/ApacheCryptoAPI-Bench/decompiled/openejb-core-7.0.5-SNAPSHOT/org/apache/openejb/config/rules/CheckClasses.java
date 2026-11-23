package org.apache.openejb.config.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EJBs;
import javax.ejb.Init;
import javax.ejb.Local;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Timeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionManagement;
import javax.jws.WebService;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.dyni.DynamicSubclass;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.Interceptor;
import org.apache.openejb.jee.RemoteBean;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.Strings;
import org.apache.openejb.util.proxy.DynamicProxyImplFactory;
import org.apache.xbean.finder.ClassFinder;

public class CheckClasses extends ValidationBase {
   private static final List<Class<? extends Annotation>> beanOnlyAnnotations = new ArrayList();

   public void validate(EjbModule ejbModule) {
      ClassLoader loader = ejbModule.getClassLoader();

      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         try {
            Class<?> beanClass = this.check_hasEjbClass(loader, bean);
            if (beanClass != null && bean instanceof RemoteBean && (!(bean instanceof SessionBean) || ((SessionBean)bean).getProxy() == null)) {
               RemoteBean b = (RemoteBean)bean;
               this.check_isEjbClass(b);
               this.check_hasDependentClasses(b, b.getEjbClass(), "ejb-class");
               this.check_hasInterface(b);
               if (b.getRemote() != null) {
                  this.checkInterface(loader, b, beanClass, "remote", b.getRemote());
               }

               if (b.getHome() != null) {
                  this.checkInterface(loader, b, beanClass, "home", b.getHome());
               }

               if (b.getLocal() != null) {
                  this.checkInterface(loader, b, beanClass, "local", b.getLocal());
               }

               if (b.getLocalHome() != null) {
                  this.checkInterface(loader, b, beanClass, "local-home", b.getLocalHome());
               }

               if (b instanceof SessionBean) {
                  SessionBean sessionBean = (SessionBean)b;

                  for(String interfce : sessionBean.getBusinessLocal()) {
                     this.checkInterface(loader, b, beanClass, "business-local", interfce);
                  }

                  for(String interfce : sessionBean.getBusinessRemote()) {
                     this.checkInterface(loader, b, beanClass, "business-remote", interfce);
                  }
               }
            }
         } catch (RuntimeException e) {
            throw new OpenEJBRuntimeException(bean.getEjbName(), e);
         }
      }

      for(Interceptor interceptor : ejbModule.getEjbJar().getInterceptors()) {
         this.check_hasInterceptorClass(loader, interceptor);
      }

   }

   private void checkInterface(ClassLoader loader, RemoteBean b, Class<?> beanClass, String tag, String className) {
      Class<?> interfce = this.lookForClass(loader, className, tag, b.getEjbName());
      if (interfce != null) {
         this.check_hasDependentClasses(b, className, tag);
         tag = Strings.lcfirst(Strings.camelCase(tag));
         this.isValidInterface(b, interfce, beanClass, tag);
         ClassFinder finder = new ClassFinder(new Class[]{interfce});

         for(Class<? extends Annotation> annotation : beanOnlyAnnotations) {
            if (interfce.isAnnotationPresent(annotation)) {
               this.warn(b, "interface.beanOnlyAnnotation", new Object[]{annotation.getSimpleName(), interfce.getName(), b.getEjbClass()});
            }

            for(Method method : finder.findAnnotatedMethods(annotation)) {
               this.warn(b, "interfaceMethod.beanOnlyAnnotation", new Object[]{annotation.getSimpleName(), interfce.getName(), method.getName(), b.getEjbClass()});
            }
         }

      }
   }

   private void check_hasInterface(RemoteBean b) {
      if (b.getRemote() == null) {
         if (b.getLocal() == null) {
            Class<?> beanClass = null;

            try {
               beanClass = this.loadClass(b.getEjbClass());
            } catch (OpenEJBException var4) {
            }

            if (b instanceof EntityBean) {
               this.fail(b, "noInterfaceDeclared.entity", new Object[]{beanClass.getSimpleName()});
            } else if (b.getBusinessLocal().size() <= 0) {
               if (b.getBusinessRemote().size() <= 0) {
                  if (((SessionBean)b).getServiceEndpoint() == null) {
                     if (!beanClass.isAnnotationPresent(WebService.class)) {
                        ;
                     }
                  }
               }
            }
         }
      }
   }

   private void check_hasDependentClasses(RemoteBean b, String className, String type) {
      try {
         ClassLoader cl = this.module.getClassLoader();
         Class<?> clazz = cl.loadClass(className);

         for(Object item : clazz.getFields()) {
            item.toString();
         }

         for(Object item : clazz.getMethods()) {
            item.toString();
         }

         for(Object item : clazz.getConstructors()) {
            item.toString();
         }

         for(Object item : clazz.getAnnotations()) {
            item.toString();
         }

         for(Class klass : clazz.getClasses()) {
            if (klass.isEnum()) {
               klass.toString();
            }
         }
      } catch (NoClassDefFoundError | ClassNotFoundException e) {
         String missingClass = ((Throwable)e).getMessage();
         this.fail(b, "missing.dependent.class", new Object[]{className, missingClass, type, b.getEjbName()});
      }

   }

   public Class<?> check_hasEjbClass(ClassLoader loader, EnterpriseBean b) {
      String ejbName = b.getEjbName();
      Class<?> beanClass = this.lookForClass(loader, b.getEjbClass(), "ejb-class", ejbName);
      boolean isDynamicProxyImpl = DynamicProxyImplFactory.isKnownDynamicallyImplemented(beanClass);
      if (beanClass == null) {
         return null;
      } else {
         if (beanClass.isInterface() && !isDynamicProxyImpl) {
            this.fail(ejbName, "interfaceDeclaredAsBean", new Object[]{beanClass.getName()});
         }

         if (this.isCmp(b)) {
            return beanClass;
         } else {
            if (Modifier.isAbstract(beanClass.getModifiers()) && !isAbstractAllowed(beanClass)) {
               this.fail(ejbName, "abstractDeclaredAsBean", new Object[]{beanClass.getName()});
            }

            return beanClass;
         }
      }
   }

   public static boolean isAbstractAllowed(Class clazz) {
      if (DynamicProxyImplFactory.isKnownDynamicallyImplemented(clazz)) {
         return true;
      } else {
         return DynamicSubclass.isDynamic(clazz);
      }
   }

   private void check_hasInterceptorClass(ClassLoader loader, Interceptor i) {
      this.lookForClass(loader, i.getInterceptorClass(), "interceptor-class", "Interceptor");
   }

   private void check_isEjbClass(RemoteBean b) {
      if (!(b instanceof SessionBean) && b instanceof EntityBean) {
         this.compareTypes(b, b.getEjbClass(), javax.ejb.EntityBean.class);
      }

   }

   private Class<?> lookForClass(ClassLoader loader, String clazz, String type, String ejbName) {
      try {
         return this.loadClass(loader, clazz);
      } catch (OpenEJBException var6) {
         this.fail(ejbName, "missing.class", new Object[]{clazz, type, ejbName});
         return null;
      } catch (NoClassDefFoundError e) {
         this.fail(ejbName, "misslocated.class", new Object[]{clazz, type, ejbName, e.getMessage()});
         throw e;
      }
   }

   private boolean isValidInterface(RemoteBean b, Class clazz, Class beanClass, String tag) {
      if (clazz.equals(beanClass)) {
         this.fail(b, "xml." + tag + ".beanClass", new Object[]{clazz.getName()});
      } else if (!clazz.isInterface()) {
         this.fail(b, "xml." + tag + ".notInterface", new Object[]{clazz.getName()});
      } else if (EJBHome.class.isAssignableFrom(clazz)) {
         if (tag.equals("home")) {
            return true;
         }

         this.fail(b, "xml." + tag + ".ejbHome", new Object[]{clazz.getName()});
      } else if (EJBLocalHome.class.isAssignableFrom(clazz)) {
         if (tag.equals("localHome")) {
            return true;
         }

         this.fail(b, "xml." + tag + ".ejbLocalHome", new Object[]{clazz.getName()});
      } else if (EJBObject.class.isAssignableFrom(clazz)) {
         if (tag.equals("remote")) {
            return true;
         }

         this.fail(b, "xml." + tag + ".ejbObject", new Object[]{clazz.getName()});
      } else if (EJBLocalObject.class.isAssignableFrom(clazz)) {
         if (tag.equals("local")) {
            return true;
         }

         this.fail(b, "xml." + tag + ".ejbLocalObject", new Object[]{clazz.getName()});
      } else {
         if (tag.equals("businessLocal") || tag.equals("businessRemote")) {
            return true;
         }

         if (clazz.isAnnotationPresent(Local.class)) {
            this.fail(b, "xml." + tag + ".businessLocal", new Object[]{clazz.getName()});
         } else if (clazz.isAnnotationPresent(Remote.class)) {
            this.fail(b, "xml." + tag + ".businessRemote", new Object[]{clazz.getName()});
         } else {
            this.fail(b, "xml." + tag + ".unknown", new Object[]{clazz.getName()});
         }
      }

      return false;
   }

   private void compareTypes(RemoteBean b, String clazz1, Class<?> class2) {
      Class<?> class1 = null;

      try {
         class1 = this.loadClass(clazz1);
      } catch (OpenEJBException var6) {
         return;
      }

      if (class1 != null && !class2.isAssignableFrom(class1)) {
         this.fail(b, "wrong.class.type", new Object[]{clazz1, class2.getName()});
      }

   }

   protected Class<?> loadClass(ClassLoader cl, String clazz) throws OpenEJBException {
      try {
         return Class.forName(clazz, false, cl == null ? this.module.getClassLoader() : cl);
      } catch (ClassNotFoundException cnfe) {
         throw new OpenEJBException((new Messages("org.apache.openejb.util.resources")).format("cl0007", clazz, this.module.getJarLocation()), cnfe);
      }
   }

   static {
      beanOnlyAnnotations.add(PostConstruct.class);
      beanOnlyAnnotations.add(PreDestroy.class);
      beanOnlyAnnotations.add(Resource.class);
      beanOnlyAnnotations.add(Resources.class);
      beanOnlyAnnotations.add(DeclareRoles.class);
      beanOnlyAnnotations.add(DenyAll.class);
      beanOnlyAnnotations.add(PermitAll.class);
      beanOnlyAnnotations.add(RolesAllowed.class);
      beanOnlyAnnotations.add(RunAs.class);
      beanOnlyAnnotations.add(EJB.class);
      beanOnlyAnnotations.add(EJBs.class);
      beanOnlyAnnotations.add(Init.class);
      beanOnlyAnnotations.add(PostActivate.class);
      beanOnlyAnnotations.add(PrePassivate.class);
      beanOnlyAnnotations.add(Remove.class);
      beanOnlyAnnotations.add(Timeout.class);
      beanOnlyAnnotations.add(TransactionAttribute.class);
      beanOnlyAnnotations.add(TransactionManagement.class);
   }
}
