package org.apache.openejb.util.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;

public class URLClassLoaderFirst extends URLClassLoader {
   private static final boolean SKIP_LOG4J = "true".equals(SystemInstance.get().getProperty("openejb.skip.log4j", "true")) && skipLib("org.apache.log4j.Logger");
   private static final boolean SKIP_MYFACES = "true".equals(SystemInstance.get().getProperty("openejb.skip.myfaces", "true")) && skipLib("org.apache.myfaces.spi.FactoryFinderProvider");
   private static final boolean SKIP_HSQLDB = skipLib("org.hsqldb.lib.HsqlTimer");
   private static final boolean SKIP_COMMONS_NET = skipLib("org.apache.commons.net.pop3.POP3Client");
   private static final boolean SKIP_JAXWS = skipLib("org.apache.cxf.jaxws.support.JaxWsImplementorInfo");
   private static final boolean SKIP_JMS = skipLib("org.apache.activemq.broker.BrokerFactory");
   private static final boolean EMBEDDED = "true".equals(SystemInstance.get().getProperty("openejb.embedded"));
   public static final Collection<String> FORCED_SKIP = new ArrayList();
   public static final Collection<String> FORCED_LOAD = new ArrayList();
   public static final String SLF4J_BINDER_CLASS = "org/slf4j/impl/StaticLoggerBinder.class";
   private static final URL SLF4J_CONTAINER;
   private static final String CLASS_EXT = ".class";
   public static final ClassLoader SYSTEM_CLASS_LOADER;
   private static final boolean ALLOW_OPEN_EJB_SYSTEM_LOADING;
   private final ClassLoader system = ClassLoader.getSystemClassLoader();

   public static void reloadConfig() {
      list(FORCED_SKIP, "openejb.classloader.forced-skip");
      list(FORCED_LOAD, "openejb.classloader.forced-load");
   }

   private static void list(Collection<String> list, String key) {
      list.clear();
      String s = SystemInstance.get().getOptions().get(key, (String)null);
      if (s != null && !s.trim().isEmpty()) {
         list.addAll(Arrays.asList(s.trim().split(",")));
      }

   }

   private static boolean skipLib(String includedClass) {
      try {
         URLClassLoaderFirst.class.getClassLoader().loadClass(includedClass);
         return "true".equalsIgnoreCase(JavaSecurityManagers.getSystemProperty(includedClass + ".skip", "true"));
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }

   public URLClassLoaderFirst(URL[] urls, ClassLoader parent) {
      super(urls, parent);
   }

   public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      synchronized(this.getClassLoadingLock(name)) {
         Class<?> clazz = this.findLoadedClass(name);
         if (clazz != null) {
            if (resolve) {
               this.resolveClass(clazz);
            }

            return clazz;
         } else {
            if (canBeLoadedFromSystem(name)) {
               try {
                  clazz = this.system.loadClass(name);
                  if (clazz != null) {
                     if (resolve) {
                        this.resolveClass(clazz);
                     }

                     Class var10000 = clazz;
                     return var10000;
                  }
               } catch (ClassNotFoundException | NoClassDefFoundError var7) {
               }
            }

            boolean ok = !shouldSkip(name) && !shouldDelegateToTheContainer(this, name);
            if (ok) {
               clazz = this.loadInternal(name, resolve);
               if (clazz != null) {
                  return clazz;
               }
            }

            clazz = this.loadFromParent(name, resolve);
            if (clazz != null) {
               return clazz;
            } else {
               if (!ok) {
                  clazz = this.loadInternal(name, resolve);
                  if (clazz != null) {
                     return clazz;
                  }
               }

               throw new ClassNotFoundException(name);
            }
         }
      }
   }

   public static boolean shouldDelegateToTheContainer(ClassLoader loader, String name) {
      return shouldSkipJsf(loader, name) || shouldSkipSlf4j(loader, name);
   }

   private Class<?> loadFromParent(String name, boolean resolve) {
      ClassLoader parent = this.getParent();
      if (parent == null) {
         parent = this.system;
      }

      try {
         Class<?> clazz = Class.forName(name, false, parent);
         if (clazz != null) {
            if (resolve) {
               this.resolveClass(clazz);
            }

            return clazz;
         }
      } catch (ClassNotFoundException var5) {
      }

      return null;
   }

   public Class<?> findAlreadyLoadedClass(String name) {
      return super.findLoadedClass(name);
   }

   public Class<?> loadInternal(String name, boolean resolve) {
      try {
         Class<?> clazz = this.findClass(name);
         if (clazz != null) {
            if (resolve) {
               this.resolveClass(clazz);
            }

            return clazz;
         }
      } catch (ClassNotFoundException var4) {
      }

      return null;
   }

   public static boolean canBeLoadedFromSystem(String name) {
      return ALLOW_OPEN_EJB_SYSTEM_LOADING && (!name.startsWith("org.apache.openejb.") || !isWebAppEnrichment(name.substring("org.apache.openejb.".length())));
   }

   public static boolean shouldSkip(String name) {
      if (name == null) {
         return false;
      } else {
         for(String prefix : FORCED_SKIP) {
            if (name.startsWith(prefix)) {
               return true;
            }
         }

         for(String prefix : FORCED_LOAD) {
            if (name.startsWith(prefix)) {
               return false;
            }
         }

         if (name.startsWith("java.")) {
            return true;
         } else if (name.startsWith("javax.faces.")) {
            return false;
         } else if (name.startsWith("javax.mail.")) {
            return false;
         } else if (name.startsWith("javax.")) {
            return isInServer(name);
         } else if (name.startsWith("sun.")) {
            return isInJvm(name);
         } else if (name.startsWith("javax.servlet.jsp.jstl")) {
            return false;
         } else if (name.startsWith("org.")) {
            String org = name.substring("org.".length());
            if (org.startsWith("apache.")) {
               String apache = org.substring("apache.".length());
               if (apache.startsWith("webbeans.jsf")) {
                  return false;
               } else if (apache.startsWith("tomee.mojarra.")) {
                  return false;
               } else if (apache.startsWith("bval.")) {
                  return true;
               } else if (apache.startsWith("openjpa.")) {
                  return true;
               } else if (apache.startsWith("xbean.")) {
                  return !apache.substring("xbean.".length()).startsWith("spring");
               } else if (apache.startsWith("geronimo.")) {
                  return true;
               } else if (apache.startsWith("coyote.")) {
                  return true;
               } else if (apache.startsWith("webbeans.")) {
                  return true;
               } else if (apache.startsWith("log4j.") && SKIP_LOG4J) {
                  return true;
               } else if (apache.startsWith("catalina.")) {
                  return true;
               } else if (apache.startsWith("jasper.")) {
                  return true;
               } else if (apache.startsWith("tomcat.")) {
                  return true;
               } else if (apache.startsWith("el.")) {
                  return true;
               } else if (apache.startsWith("naming.")) {
                  return true;
               } else if (apache.startsWith("taglibs.standard.")) {
                  return true;
               } else if (apache.startsWith("openejb.")) {
                  return !isWebAppEnrichment(apache.substring("openejb.".length()));
               } else if (apache.startsWith("commons.")) {
                  String commons = apache.substring("commons.".length());
                  if (commons.startsWith("beanutils.")) {
                     return isInServer(name);
                  } else if (commons.startsWith("cli.")) {
                     return true;
                  } else if (commons.startsWith("codec.")) {
                     return true;
                  } else if (commons.startsWith("collections.")) {
                     return true;
                  } else if (commons.startsWith("dbcp.")) {
                     return true;
                  } else if (commons.startsWith("dbcp2.")) {
                     return true;
                  } else if (commons.startsWith("digester.")) {
                     return true;
                  } else if (commons.startsWith("jocl.")) {
                     return true;
                  } else if (commons.startsWith("lang.")) {
                     return true;
                  } else if (commons.startsWith("lang3.")) {
                     return true;
                  } else if (commons.startsWith("logging.")) {
                     return false;
                  } else if (commons.startsWith("pool.")) {
                     return true;
                  } else if (commons.startsWith("pool2.")) {
                     return true;
                  } else {
                     return commons.startsWith("net.") && SKIP_COMMONS_NET;
                  }
               } else if (SKIP_MYFACES && apache.startsWith("myfaces.")) {
                  String myfaces = name.substring("myfaces.".length());
                  if (myfaces.startsWith("shared.")) {
                     return true;
                  } else if (myfaces.startsWith("ee6.")) {
                     return true;
                  } else if (myfaces.startsWith("lifecycle.")) {
                     return true;
                  } else if (myfaces.startsWith("context.")) {
                     return true;
                  } else if (myfaces.startsWith("logging.")) {
                     return true;
                  } else if (!myfaces.startsWith("component.visit.") && !myfaces.equals("component.ComponentResourceContainer")) {
                     if (myfaces.startsWith("application.")) {
                        return true;
                     } else if (myfaces.startsWith("config.")) {
                        return true;
                     } else if (myfaces.startsWith("event.")) {
                        return true;
                     } else if (myfaces.startsWith("resource.")) {
                        return true;
                     } else if (myfaces.startsWith("el.")) {
                        return true;
                     } else if (myfaces.startsWith("spi.")) {
                        return true;
                     } else if (myfaces.startsWith("convert.")) {
                        return true;
                     } else if (myfaces.startsWith("debug.")) {
                        return true;
                     } else if (myfaces.startsWith("util.")) {
                        return true;
                     } else if (myfaces.startsWith("view.")) {
                        return true;
                     } else if (myfaces.equals("convert.ConverterUtils")) {
                        return true;
                     } else if (myfaces.startsWith("renderkit.")) {
                        String renderkit = myfaces.substring("renderkit.".length());
                        if (renderkit.startsWith("html.Html")) {
                           return true;
                        } else {
                           char firstNextletter = renderkit.charAt(0);
                           return Character.isUpperCase(firstNextletter);
                        }
                     } else if (myfaces.startsWith("taglib.")) {
                        String taglib = myfaces.substring("taglib.".length());
                        if (taglib.startsWith("html.Html")) {
                           return true;
                        } else {
                           return taglib.startsWith("core.");
                        }
                     } else if (myfaces.startsWith("webapp.")) {
                        String webapp = myfaces.substring("webapp.".length());
                        if (webapp.startsWith("Faces")) {
                           return true;
                        } else if (webapp.startsWith("Jsp")) {
                           return true;
                        } else if (webapp.startsWith("Startup")) {
                           return true;
                        } else if (webapp.equals("AbstractFacesInitializer")) {
                           return true;
                        } else if (webapp.equals("MyFacesServlet")) {
                           return true;
                        } else if (webapp.equals("ManagedBeanDestroyerListener")) {
                           return true;
                        } else {
                           return webapp.equals("WebConfigParamsLogger");
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return true;
                  }
               } else if (!apache.startsWith("activemq.")) {
                  return false;
               } else {
                  return SKIP_JMS && isInServer(name);
               }
            } else if (org.startsWith("hsqldb.") && SKIP_HSQLDB) {
               return true;
            } else if (org.startsWith("codehaus.swizzle.")) {
               String swizzle = org.substring("codehaus.swizzle.".length());
               if (swizzle.startsWith("stream.")) {
                  return true;
               } else if (swizzle.startsWith("rss.")) {
                  return true;
               } else {
                  return swizzle.startsWith("Grep.class") || swizzle.startsWith("Lexer.class");
               }
            } else if (!org.startsWith("w3c.dom.") && !org.startsWith("xml.sax.")) {
               return org.startsWith("eclipse.jdt.");
            } else {
               return isInJvm(name);
            }
         } else {
            if (name.startsWith("com.")) {
               String sub = name.substring("com.".length());
               if (sub.startsWith("sun.")) {
                  return !name.startsWith("sun.mail.") && isInJvm(name);
               }

               if (sub.startsWith("oracle.")) {
                  return true;
               }
            }

            if (name.startsWith("jdk.")) {
               return true;
            } else {
               return name.startsWith("serp.bytecode.");
            }
         }
      }
   }

   private static boolean isInJvm(String name) {
      return SYSTEM_CLASS_LOADER.getResource(name.replace('.', '/') + ".class") != null;
   }

   private static boolean isInServer(String name) {
      if (name.startsWith("javax.")) {
         String sub = name.substring("javax.".length());
         if (sub.startsWith("jws.")) {
            return SKIP_JAXWS || EMBEDDED;
         }

         if (sub.startsWith("jms.")) {
            return SKIP_JMS || EMBEDDED;
         }
      }

      return ParentClassLoaderFinder.Helper.get().getResource(name.replace('.', '/') + ".class") != null;
   }

   public static boolean shouldSkipJsf(ClassLoader loader, String name) {
      if (!name.startsWith("javax.faces.")) {
         return false;
      } else {
         String testClass;
         if ("javax.faces.webapp.FacesServlet".equals(name)) {
            testClass = "javax.faces.FactoryFinder";
         } else {
            testClass = "javax.faces.webapp.FacesServlet";
         }

         String classname = testClass.replace('.', '/') + ".class";

         try {
            Enumeration<URL> resources = loader.getResources(classname);
            Collection<URL> thisJSf = Collections.list(resources);
            return thisJSf.isEmpty() || thisJSf.size() <= 1;
         } catch (IOException var6) {
            return true;
         }
      }
   }

   private static boolean isWebAppEnrichment(String openejb) {
      return openejb.startsWith("hibernate.") || openejb.startsWith("jpa.integration.") || openejb.startsWith("toplink.") || openejb.startsWith("eclipselink.") || openejb.startsWith("arquillian.");
   }

   public Enumeration<URL> getResources(String name) throws IOException {
      return filterResources(name, super.getResources(name));
   }

   public static boolean isFilterableResource(String name) {
      return name != null && ("META-INF/services/javax.validation.spi.ValidationProvider".equals(name) || "META-INF/services/javax.ws.rs.client.ClientBuilder".equals(name) || "META-INF/services/javax.json.spi.JsonProvider".equals(name) || "META-INF/services/javax.cache.spi.CachingProvider".equals(name) || "META-INF/javamail.default.providers".equals(name) || "META-INF/javamail.default.address.map".equals(name) || "META-INF/javamail.charset.map".equals(name) || "META-INF/mailcap".equals(name) || name.startsWith("META-INF/services/org.apache.myfaces.spi") || "org/slf4j/impl/StaticLoggerBinder.class".equals(name));
   }

   public static boolean shouldSkipSlf4j(ClassLoader loader, String name) {
      if (name != null && name.startsWith("org.slf4j.")) {
         try {
            Enumeration<URL> resources = loader.getResources("org/slf4j/impl/StaticLoggerBinder.class");

            while(resources.hasMoreElements()) {
               URL resource = (URL)resources.nextElement();
               if (!resource.equals(SLF4J_CONTAINER)) {
                  return false;
               }
            }
         } catch (Throwable var4) {
         }

         return true;
      } else {
         return false;
      }
   }

   public static Enumeration<URL> filterResources(String name, Enumeration<URL> result) {
      if (isFilterableResource(name)) {
         Collection<URL> values = Collections.list(result);
         if (values.size() > 1) {
            URL url = URLClassLoaderFirst.class.getResource("/" + name);
            if (url != null) {
               values.remove(url);
            }
         }

         return Collections.enumeration(values);
      } else {
         return result;
      }
   }

   static {
      reloadConfig();
      ClassLoader.registerAsParallelCapable();
      SLF4J_CONTAINER = URLClassLoaderFirst.class.getClassLoader().getResource("org/slf4j/impl/StaticLoggerBinder.class");
      SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
      ALLOW_OPEN_EJB_SYSTEM_LOADING = !Boolean.getBoolean("openejb.classloader.first.disallow-system-loading");
   }
}
