package org.apache.openejb.config.rules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.RemoteBean;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;

public class CheckMethods extends ValidationBase {
   public void validate(EjbModule ejbModule) {
      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         if (bean instanceof RemoteBean) {
            RemoteBean b = (RemoteBean)bean;
            if (b.getHome() != null) {
               this.check_remoteInterfaceMethods(b);
               this.check_homeInterfaceMethods(b);
            }

            if (b.getLocalHome() != null) {
               this.check_localInterfaceMethods(b);
               this.check_localHomeInterfaceMethods(b);
            }

            this.check_unusedCreateMethods(b);
            this.check_unusedPostCreateMethods(b);
         }
      }

   }

   private void check_localHomeInterfaceMethods(RemoteBean b) {
      Class home = null;
      Class bean = null;

      try {
         home = this.loadClass(b.getLocalHome());
         bean = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var5) {
         return;
      }

      if (EJBLocalHome.class.isAssignableFrom(home)) {
         if (this.check_hasCreateMethod(b, bean, home)) {
            this.check_createMethodsAreImplemented(b, bean, home);
         }

      }
   }

   private void check_localInterfaceMethods(RemoteBean b) {
      Class intrface = null;
      Class beanClass = null;

      try {
         intrface = this.loadClass(b.getLocal());
         beanClass = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var14) {
         return;
      }

      if (EJBLocalObject.class.isAssignableFrom(intrface)) {
         Method[] interfaceMethods = intrface.getMethods();

         for(int i = 0; i < interfaceMethods.length; ++i) {
            if (interfaceMethods[i].getDeclaringClass() != EJBLocalObject.class) {
               String name = interfaceMethods[i].getName();

               try {
                  Class[] params = interfaceMethods[i].getParameterTypes();
                  beanClass.getMethod(name, params);
               } catch (NoSuchMethodException var15) {
                  List<Method> differentArgs = new ArrayList();
                  List<Method> differentCase = new ArrayList();

                  for(Method method : beanClass.getMethods()) {
                     if (method.getName().equals(name)) {
                        differentArgs.add(method);
                     } else if (method.getName().equalsIgnoreCase(name)) {
                        differentCase.add(method);
                     }
                  }

                  if (differentArgs.size() > 0) {
                     this.fail(b, "no.busines.method.args", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "local", intrface.getName(), beanClass.getName(), differentArgs.size()});
                  }

                  if (differentCase.size() > 0) {
                     this.fail(b, "no.busines.method.case", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "local", intrface.getName(), beanClass.getName(), differentCase.size()});
                  }

                  if (differentArgs.size() == 0 && differentCase.size() == 0) {
                     this.fail(b, "no.busines.method", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "local", intrface.getName(), beanClass.getName()});
                  }
               }
            }
         }

      }
   }

   private void check_remoteInterfaceMethods(RemoteBean b) {
      Class intrface = null;
      Class beanClass = null;

      try {
         intrface = this.loadClass(b.getRemote());
         beanClass = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var14) {
         return;
      }

      if (EJBObject.class.isAssignableFrom(intrface)) {
         Method[] interfaceMethods = intrface.getMethods();

         for(int i = 0; i < interfaceMethods.length; ++i) {
            if (interfaceMethods[i].getDeclaringClass() != EJBObject.class) {
               String name = interfaceMethods[i].getName();

               try {
                  Class[] params = interfaceMethods[i].getParameterTypes();
                  beanClass.getMethod(name, params);
               } catch (NoSuchMethodException var15) {
                  List<Method> differentArgs = new ArrayList();
                  List<Method> differentCase = new ArrayList();

                  for(Method method : beanClass.getMethods()) {
                     if (method.getName().equals(name)) {
                        differentArgs.add(method);
                     } else if (method.getName().equalsIgnoreCase(name)) {
                        differentCase.add(method);
                     }
                  }

                  if (differentArgs.size() > 0) {
                     this.fail(b, "no.busines.method.args", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "remote", intrface.getName(), beanClass.getName(), differentArgs.size()});
                  }

                  if (differentCase.size() > 0) {
                     this.fail(b, "no.busines.method.case", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "remote", intrface.getName(), beanClass.getName(), differentCase.size()});
                  }

                  if (differentArgs.size() == 0 && differentCase.size() == 0) {
                     this.fail(b, "no.busines.method", new Object[]{interfaceMethods[i].getName(), interfaceMethods[i].toString(), "remote", intrface.getName(), beanClass.getName()});
                  }
               }
            }
         }

      }
   }

   private void check_homeInterfaceMethods(RemoteBean b) {
      Class home = null;
      Class bean = null;

      try {
         home = this.loadClass(b.getHome());
         bean = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var5) {
         return;
      }

      if (EJBHome.class.isAssignableFrom(home)) {
         if (this.check_hasCreateMethod(b, bean, home)) {
            this.check_createMethodsAreImplemented(b, bean, home);
         }

      }
   }

   public boolean check_hasCreateMethod(RemoteBean b, Class bean, Class home) {
      if (b instanceof SessionBean && !javax.ejb.SessionBean.class.isAssignableFrom(bean)) {
         return false;
      } else {
         Method[] homeMethods = home.getMethods();
         boolean hasCreateMethod = false;

         for(int i = 0; i < homeMethods.length && !hasCreateMethod; ++i) {
            hasCreateMethod = homeMethods[i].getName().startsWith("create");
         }

         if (!hasCreateMethod && !(b instanceof EntityBean)) {
            this.fail(b, "no.home.create", new Object[]{b.getHome(), b.getRemote()});
         }

         return hasCreateMethod;
      }
   }

   public boolean check_createMethodsAreImplemented(RemoteBean b, Class bean, Class home) {
      boolean result = true;
      Method[] homeMethods = home.getMethods();

      for(int i = 0; i < homeMethods.length; ++i) {
         if (homeMethods[i].getName().startsWith("create")) {
            Method create = homeMethods[i];
            StringBuilder ejbCreateName = new StringBuilder(create.getName());
            ejbCreateName.replace(0, 1, "ejbC");

            try {
               if (javax.ejb.EnterpriseBean.class.isAssignableFrom(bean)) {
                  bean.getMethod(ejbCreateName.toString(), create.getParameterTypes());
               }
            } catch (NoSuchMethodException var12) {
               result = false;
               String paramString = this.getParameters(create);
               if (b instanceof EntityBean) {
                  EntityBean entity = (EntityBean)b;
                  this.fail(b, "entity.no.ejb.create", new Object[]{b.getEjbClass(), entity.getPrimKeyClass(), ejbCreateName.toString(), paramString});
               } else if (b instanceof SessionBean) {
                  SessionBean sb = (SessionBean)b;
                  if (!sb.getSessionType().equals(SessionType.STATELESS)) {
                     this.fail(b, "session.no.ejb.create", new Object[]{b.getEjbClass(), ejbCreateName.toString(), paramString});
                  }
               }
            }
         }
      }

      return result;
   }

   public boolean check_postCreateMethodsAreImplemented(RemoteBean b, Class bean, Class home) {
      boolean result = true;
      if (b instanceof SessionBean) {
         return true;
      } else {
         Method[] homeMethods = home.getMethods();
         Method[] beanMethods = bean.getMethods();

         for(int i = 0; i < homeMethods.length; ++i) {
            if (homeMethods[i].getName().startsWith("create")) {
               Method create = homeMethods[i];
               StringBuilder ejbPostCreateName = new StringBuilder(create.getName());
               ejbPostCreateName.replace(0, 1, "ejbPostC");

               try {
                  bean.getMethod(ejbPostCreateName.toString(), create.getParameterTypes());
               } catch (NoSuchMethodException var12) {
                  result = false;
                  String paramString = this.getParameters(create);
                  this.fail(b, "no.ejb.post.create", new Object[]{b.getEjbClass(), ejbPostCreateName.toString(), paramString});
               }
            }
         }

         return result;
      }
   }

   public void check_unusedCreateMethods(RemoteBean b) {
      Class home = null;
      Class localHome = null;
      Class bean = null;

      try {
         if (b.getLocalHome() != null) {
            localHome = this.loadClass(b.getLocalHome());
         }

         if (b.getHome() != null) {
            home = this.loadClass(b.getHome());
         }

         bean = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var15) {
         return;
      }

      for(Method ejbCreate : bean.getMethods()) {
         if (ejbCreate.getName().startsWith("ejbCreate")) {
            StringBuilder create = new StringBuilder(ejbCreate.getName());
            create.replace(0, "ejbC".length(), "c");
            boolean inLocalHome = false;
            boolean inHome = false;

            try {
               if (localHome != null) {
                  localHome.getMethod(create.toString(), ejbCreate.getParameterTypes());
                  inLocalHome = true;
               }
            } catch (NoSuchMethodException var14) {
            }

            try {
               if (home != null) {
                  home.getMethod(create.toString(), ejbCreate.getParameterTypes());
                  inHome = true;
               }
            } catch (NoSuchMethodException var13) {
            }

            if (!inLocalHome && !inHome) {
               String paramString = this.getParameters(ejbCreate);
               this.warn(b, "unused.ejb.create", new Object[]{b.getEjbClass(), ejbCreate.getName(), paramString, create.toString()});
            }
         }
      }

   }

   public void check_unusedPostCreateMethods(RemoteBean b) {
      Class bean = null;

      try {
         bean = this.loadClass(b.getEjbClass());
      } catch (OpenEJBException var11) {
         return;
      }

      for(Method postCreate : bean.getMethods()) {
         if (postCreate.getName().startsWith("ejbPostCreate")) {
            StringBuilder ejbCreate = new StringBuilder(postCreate.getName());
            ejbCreate.replace(0, "ejbPostCreate".length(), "ejbCreate");

            try {
               bean.getMethod(ejbCreate.toString(), postCreate.getParameterTypes());
            } catch (NoSuchMethodException var10) {
               String paramString = this.getParameters(postCreate);
               this.warn(b, "unused.ejbPostCreate", new Object[]{b.getEjbClass(), postCreate.getName(), paramString, ejbCreate.toString()});
            }
         }
      }

   }
}
