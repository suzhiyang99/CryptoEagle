package org.apache.meecrowave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.ServletContainerInitializer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.MeecrowaveContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.johnzon.core.BufferStrategy;
import org.apache.meecrowave.api.StartListening;
import org.apache.meecrowave.api.StopListening;
import org.apache.meecrowave.cxf.CxfCdiAutoSetup;
import org.apache.meecrowave.io.IO;
import org.apache.meecrowave.logging.jul.Log4j2Logger;
import org.apache.meecrowave.logging.log4j2.Log4j2Shutdown;
import org.apache.meecrowave.logging.openwebbeans.Log4j2LoggerFactory;
import org.apache.meecrowave.logging.tomcat.Log4j2Log;
import org.apache.meecrowave.logging.tomcat.LogFacade;
import org.apache.meecrowave.openwebbeans.OWBAutoSetup;
import org.apache.meecrowave.runner.cli.CliOption;
import org.apache.meecrowave.service.ValueTransformer;
import org.apache.meecrowave.tomcat.CDIInstanceManager;
import org.apache.meecrowave.tomcat.LoggingAccessLogPattern;
import org.apache.meecrowave.tomcat.NoDescriptorRegistry;
import org.apache.meecrowave.tomcat.OWBJarScanner;
import org.apache.meecrowave.tomcat.ProvidedLoader;
import org.apache.meecrowave.tomcat.TomcatAutoInitializer;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.xbean.finder.ResourceFinder;
import org.apache.xbean.recipe.ObjectRecipe;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Meecrowave implements AutoCloseable {
   private final Builder configuration;
   protected File base;
   protected final File ownedTempDir;
   protected InternalTomcat tomcat;
   protected volatile Thread hook;
   private final Map<String, Runnable> contexts;
   private Runnable postTask;
   private boolean clearCatalinaSystemProperties;

   public Meecrowave() {
      this(new Builder());
   }

   public Meecrowave(Builder builder) {
      this.contexts = new HashMap();
      this.configuration = builder;
      this.ownedTempDir = new File(this.configuration.tempDir, "meecrowave_" + System.nanoTime());
   }

   public Builder getConfiguration() {
      return this.configuration;
   }

   public File getBase() {
      return this.base;
   }

   public Tomcat getTomcat() {
      return this.tomcat;
   }

   public boolean isServing() {
      return this.tomcat != null && this.tomcat.getHost().getState() == LifecycleState.STARTED;
   }

   public void undeploy(String root) {
      Optional.ofNullable(this.contexts.remove(root)).ifPresent(Runnable::run);
   }

   public Meecrowave deployClasspath(DeploymentMeta meta) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      ClassLoader parentLoader = this.tomcat.getServer().getParentClassLoader();
      if (parentLoader.getParent() == classLoader) {
         classLoader = parentLoader;
      }

      ProvidedLoader loader = new ProvidedLoader(classLoader, this.configuration.isTomcatWrapLoader());
      Consumer<Context> builtInCustomizer = (c) -> c.setLoader(loader);
      return this.deployWebapp(new DeploymentMeta(meta.context, meta.docBase, (Consumer)Optional.ofNullable(meta.consumer).map((c) -> (ctx) -> {
            builtInCustomizer.accept(ctx);
            c.accept(ctx);
         }).orElse(builtInCustomizer)));
   }

   public Meecrowave deployClasspath() {
      return this.deployClasspath("");
   }

   public Meecrowave bake(Consumer<Context> customizer) {
      this.start();
      return this.deployClasspath(new DeploymentMeta("", (File)null, customizer));
   }

   public Meecrowave deployClasspath(String context) {
      return this.deployClasspath(new DeploymentMeta(context, (File)null, (Consumer)null));
   }

   public Meecrowave deployWebapp(File warOrDir) {
      return this.deployWebapp("", warOrDir);
   }

   public Meecrowave deployWebapp(String context, File warOrDir) {
      return this.deployWebapp(new DeploymentMeta(context, warOrDir, (Consumer)null));
   }

   public Meecrowave deployWebapp(DeploymentMeta meta) {
      if (this.contexts.containsKey(meta.context)) {
         throw new IllegalArgumentException("Already deployed: '" + meta.context + "'");
      } else {
         String base = this.tomcat.getService().findConnectors().length > 0 ? this.configuration.getActiveProtocol() + "://" + this.tomcat.getHost().getName() + ':' + this.configuration.getActivePort() : "";
         (new LogFacade(Meecrowave.class.getName())).info("--------------- " + base + meta.context);
         OWBJarScanner scanner = new OWBJarScanner();
         StandardContext ctx = new StandardContext() {
            public void setApplicationEventListeners(Object[] listeners) {
               if (listeners == null) {
                  super.setApplicationEventListeners((Object[])null);
               } else {
                  for(int i = 1; i < listeners.length; ++i) {
                     if (OWBAutoSetup.EagerBootListener.class.isInstance(listeners[i])) {
                        Object first = listeners[0];
                        listeners[0] = listeners[i];
                        listeners[i] = first;
                        break;
                     }
                  }

                  super.setApplicationEventListeners(listeners);
               }
            }
         };
         ctx.setPath(meta.context);
         ctx.setName(meta.context);
         ctx.setJarScanner(scanner);
         ctx.setInstanceManager(new CDIInstanceManager());
         Optional.ofNullable(meta.docBase).ifPresent((d) -> {
            try {
               ctx.setDocBase(meta.docBase.getCanonicalPath());
            } catch (IOException var4) {
               ctx.setDocBase(meta.docBase.getAbsolutePath());
            }

         });
         Optional.ofNullable(this.configuration.tomcatFilter).ifPresent((filter) -> {
            try {
               scanner.setJarScanFilter((JarScanFilter)JarScanFilter.class.cast(Thread.currentThread().getContextClassLoader().loadClass(filter).newInstance()));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
               throw new IllegalArgumentException(e);
            }
         });
         AtomicReference<Runnable> releaseSCI = new AtomicReference();
         ServletContainerInitializer meecrowaveInitializer = (c, ctx1) -> {
            (new OWBAutoSetup()).onStartup(c, ctx1);
            (new CxfCdiAutoSetup()).onStartup(c, ctx1);
            (new TomcatAutoInitializer()).onStartup(c, ctx1);
            if (this.configuration.isInjectServletContainerInitializer()) {
               Field f;
               try {
                  f = StandardContext.class.getDeclaredField("initializers");
                  if (!f.isAccessible()) {
                     f.setAccessible(true);
                  }
               } catch (Exception e) {
                  throw new IllegalStateException("Bad tomcat version", e);
               }

               List<AutoCloseable> cc;
               try {
                  cc = (List)((Map)f.get(ctx)).keySet().stream().filter((i) -> !i.getClass().getName().startsWith(Meecrowave.class.getName())).map((i) -> {
                     try {
                        return this.inject(i);
                     } catch (IllegalArgumentException var3) {
                        return null;
                     }
                  }).filter(Objects::nonNull).collect(Collectors.toList());
               } catch (IllegalAccessException e) {
                  throw new IllegalStateException("Can't read initializers", e);
               }

               releaseSCI.set((Runnable)() -> cc.forEach((closeable) -> {
                     try {
                        closeable.close();
                     } catch (Exception e) {
                        throw new IllegalStateException(e);
                     }
                  }));
            }

         };
         ctx.addLifecycleListener(new MeecrowaveContextConfig(this.configuration, meta.docBase != null, meecrowaveInitializer));
         ctx.addLifecycleListener((event) -> {
            switch (event.getType()) {
               case "after_start":
                  ctx.getResources().setCachingAllowed(this.configuration.webResourceCached);
                  break;
               case "before_init":
                  ctx.getServletContext().setAttribute("meecrowave.configuration", this.configuration);
                  ctx.getServletContext().setAttribute("meecrowave.instance", this);
                  if (this.configuration.loginConfig != null) {
                     ctx.setLoginConfig(this.configuration.loginConfig.build());
                  }

                  for(SecurityConstaintBuilder sc : this.configuration.securityConstraints) {
                     ctx.addConstraint(sc.build());
                  }

                  if (this.configuration.webXml != null) {
                     ctx.getServletContext().setAttribute("org.apache.catalina.deploy.alt_dd", this.configuration.webXml);
                  }
            }

         });
         ctx.addLifecycleListener(new Tomcat.FixContextListener());
         ctx.addServletContainerInitializer(meecrowaveInitializer, Collections.emptySet());
         if (this.configuration.isUseTomcatDefaults()) {
            ctx.setSessionTimeout(30);
            ctx.addWelcomeFile("index.html");
            ctx.addWelcomeFile("index.htm");

            try {
               Field mimesField = Tomcat.class.getDeclaredField("DEFAULT_MIME_MAPPINGS");
               if (!mimesField.isAccessible()) {
                  mimesField.setAccessible(true);
               }

               String[] defaultMimes = (String[])String[].class.cast(mimesField.get((Object)null));
               int i = 0;

               while(i < defaultMimes.length) {
                  ctx.addMimeMapping(defaultMimes[i++], defaultMimes[i++]);
               }
            } catch (IllegalAccessException | NoSuchFieldException e) {
               throw new IllegalStateException("Incompatible Tomcat", e);
            }
         }

         Optional.ofNullable(meta.consumer).ifPresent((c) -> c.accept(ctx));
         Host host = this.tomcat.getHost();
         host.addChild(ctx);
         ClassLoader classLoader = ctx.getLoader().getClassLoader();
         if (host.getState().isAvailable()) {
            this.fire(new StartListening(this.findFirstConnector(), host, ctx), classLoader);
         }

         this.contexts.put(meta.context, (Runnable)() -> {
            if (host.getState().isAvailable()) {
               this.fire(new StopListening(this.findFirstConnector(), host, ctx), classLoader);
            }

            Optional.ofNullable(releaseSCI.get()).ifPresent(Runnable::run);
            this.tomcat.getHost().removeChild(ctx);
         });
         return this;
      }
   }

   public Meecrowave bake() {
      return this.bake("");
   }

   public Meecrowave bake(String ctx) {
      this.start();
      return this.deployClasspath(ctx);
   }

   public Meecrowave start() {
      Map<String, String> systemPropsToRestore = new HashMap();
      if (this.configuration.getMeecrowaveProperties() != null && !"meecrowave.properties".equals(this.configuration.getMeecrowaveProperties())) {
         this.configuration.loadFrom(this.configuration.getMeecrowaveProperties());
      }

      if (this.configuration.isUseLog4j2JulLogManager()) {
         System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
      }

      if (this.configuration.loggingGlobalSetup) {
         this.setSystemProperty(systemPropsToRestore, "log4j.shutdownHookEnabled", "false");
         this.setSystemProperty(systemPropsToRestore, "openwebbeans.logging.factory", Log4j2LoggerFactory.class.getName());
         this.setSystemProperty(systemPropsToRestore, "org.apache.cxf.Logger", Log4j2Logger.class.getName());
         this.setSystemProperty(systemPropsToRestore, "org.apache.tomcat.Logger", Log4j2Log.class.getName());
         this.postTask = () -> {
            (new Log4j2Shutdown()).shutodwn();
            systemPropsToRestore.entrySet().forEach((entry) -> {
               if (entry.getValue() == null) {
                  System.clearProperty((String)entry.getKey());
               } else {
                  System.setProperty((String)entry.getKey(), (String)entry.getValue());
               }

            });
         };
      }

      this.setupJmx(this.configuration.isTomcatNoJmx());
      this.clearCatalinaSystemProperties = System.getProperty("catalina.base") == null && System.getProperty("catalina.home") == null;
      if (this.configuration.quickSession) {
         this.tomcat = new TomcatWithFastSessionIDs();
      } else {
         this.tomcat = new InternalTomcat();
      }

      this.base = new File(this.newBaseDir());
      File conf = this.createDirectory(this.base, "conf");
      this.createDirectory(this.base, "lib");
      this.createDirectory(this.base, "logs");
      this.createDirectory(this.base, "temp");
      this.createDirectory(this.base, "work");
      this.createDirectory(this.base, "webapps");
      this.synchronize(conf, this.configuration.conf);
      Properties props = this.configuration.properties;
      StrSubstitutor substitutor = null;

      for(String s : props.stringPropertyNames()) {
         String v = props.getProperty(s);
         if (v != null && v.contains("${")) {
            if (substitutor == null) {
               Map<String, String> placeHolders = new HashMap();
               placeHolders.put("meecrowave.embedded.http", Integer.toString(this.configuration.httpPort));
               placeHolders.put("meecrowave.embedded.https", Integer.toString(this.configuration.httpsPort));
               placeHolders.put("meecrowave.embedded.stop", Integer.toString(this.configuration.stopPort));
               substitutor = new StrSubstitutor(placeHolders);
            }

            props.put(s, substitutor.replace(v));
         }
      }

      File conf = new File(this.base, "conf");
      File webapps = new File(this.base, "webapps");
      this.tomcat.setBaseDir(this.base.getAbsolutePath());
      this.tomcat.setHostname(this.configuration.host);
      boolean initialized;
      if (this.configuration.serverXml != null) {
         File file = new File(conf, "server.xml");
         if (!file.equals(this.configuration.serverXml)) {
            try {
               InputStream is = new FileInputStream(this.configuration.serverXml);
               Throwable var9 = null;

               try {
                  FileOutputStream fos = new FileOutputStream(file);
                  Throwable var11 = null;

                  try {
                     IO.copy(is, fos);
                  } catch (Throwable var105) {
                     var11 = var105;
                     throw var105;
                  } finally {
                     if (fos != null) {
                        if (var11 != null) {
                           try {
                              fos.close();
                           } catch (Throwable var102) {
                              var11.addSuppressed(var102);
                           }
                        } else {
                           fos.close();
                        }
                     }

                  }
               } catch (Throwable var109) {
                  var9 = var109;
                  throw var109;
               } finally {
                  if (is != null) {
                     if (var9 != null) {
                        try {
                           is.close();
                        } catch (Throwable var101) {
                           var9.addSuppressed(var101);
                        }
                     } else {
                        is.close();
                     }
                  }

               }
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }
         }

         QuickServerXmlParser ports = Meecrowave.QuickServerXmlParser.parse(file);
         if (this.configuration.keepServerXmlAsThis) {
            this.configuration.httpPort = Integer.parseInt(ports.http());
            this.configuration.stopPort = Integer.parseInt(ports.stop());
         } else {
            Map<String, String> replacements = new HashMap();
            replacements.put(ports.http(), String.valueOf(this.configuration.httpPort));
            replacements.put(ports.https(), String.valueOf(this.configuration.httpsPort));
            replacements.put(ports.stop(), String.valueOf(this.configuration.stopPort));

            String serverXmlContent;
            try {
               InputStream stream = new FileInputStream(file);
               Throwable var12 = null;

               try {
                  serverXmlContent = IO.toString(stream);

                  for(Map.Entry<String, String> pair : replacements.entrySet()) {
                     serverXmlContent = serverXmlContent.replace((CharSequence)pair.getKey(), (CharSequence)pair.getValue());
                  }
               } catch (Throwable var113) {
                  var12 = var113;
                  throw var113;
               } finally {
                  if (stream != null) {
                     if (var12 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var100) {
                           var12.addSuppressed(var100);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }

            try {
               OutputStream os = new FileOutputStream(file);
               Throwable var139 = null;

               try {
                  os.write(serverXmlContent.getBytes(StandardCharsets.UTF_8));
               } catch (Throwable var104) {
                  var139 = var104;
                  throw var104;
               } finally {
                  if (os != null) {
                     if (var139 != null) {
                        try {
                           os.close();
                        } catch (Throwable var99) {
                           var139.addSuppressed(var99);
                        }
                     } else {
                        os.close();
                     }
                  }

               }
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }
         }

         this.tomcat.server(createServer(file.getAbsolutePath()));
         initialized = true;
      } else {
         this.tomcat.getServer().setPort(this.configuration.stopPort);
         initialized = false;
      }

      Optional.ofNullable(this.configuration.getSharedLibraries()).map(File::new).filter(File::isDirectory).ifPresent((libRoot) -> {
         Collection<URL> libs = new ArrayList();

         try {
            libs.add(libRoot.toURI().toURL());
         } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
         }

         libs.addAll((Collection)Optional.ofNullable(libRoot.listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"))).map(Stream::of).map((s) -> (List)s.map((f) -> {
               try {
                  return f.toURI().toURL();
               } catch (MalformedURLException e) {
                  throw new IllegalStateException(e);
               }
            }).collect(Collectors.toList())).orElse(Collections.emptyList()));
         this.tomcat.getServer().setParentClassLoader(new MeecrowaveContainerLoader((URL[])libs.toArray(new URL[libs.size()]), Thread.currentThread().getContextClassLoader()));
      });
      if (!initialized) {
         this.tomcat.setHostname(this.configuration.host);
         this.tomcat.getEngine().setDefaultHost(this.configuration.host);
         StandardHost host = new StandardHost();
         host.setName(this.configuration.host);
         host.setAppBase(webapps.getAbsolutePath());
         host.setUnpackWARs(true);

         try {
            host.setWorkDir((new File(this.base, "work")).getCanonicalPath());
         } catch (IOException var103) {
            host.setWorkDir((new File(this.base, "work")).getAbsolutePath());
         }

         this.tomcat.setHost(host);
      }

      Optional.ofNullable(this.configuration.getTomcatAccessLogPattern()).ifPresent((pattern) -> this.tomcat.getHost().getPipeline().addValve(new LoggingAccessLogPattern(pattern)));
      if (this.configuration.realm != null) {
         this.tomcat.getEngine().setRealm(this.configuration.realm);
      }

      if (this.tomcat.getRawConnector() == null && !this.configuration.skipHttp) {
         Connector connector = this.createConnector();
         connector.setPort(this.configuration.httpPort);
         if (connector.getAttribute("connectionTimeout") == null) {
            connector.setAttribute("connectionTimeout", "3000");
         }

         this.tomcat.getService().addConnector(connector);
         this.tomcat.setConnector(connector);
      }

      if (this.configuration.ssl) {
         Connector httpsConnector = this.createConnector();
         httpsConnector.setPort(this.configuration.httpsPort);
         httpsConnector.setSecure(true);
         httpsConnector.setProperty("SSLEnabled", "true");
         httpsConnector.setProperty("sslProtocol", this.configuration.sslProtocol);
         if (this.configuration.keystoreFile != null) {
            httpsConnector.setAttribute("keystoreFile", this.configuration.keystoreFile);
         }

         if (this.configuration.keystorePass != null) {
            httpsConnector.setAttribute("keystorePass", this.configuration.keystorePass);
         }

         httpsConnector.setAttribute("keystoreType", this.configuration.keystoreType);
         if (this.configuration.clientAuth != null) {
            httpsConnector.setAttribute("clientAuth", this.configuration.clientAuth);
         }

         if (this.configuration.keyAlias != null) {
            httpsConnector.setAttribute("keyAlias", this.configuration.keyAlias);
         }

         this.tomcat.getService().addConnector(httpsConnector);
         if (this.configuration.skipHttp) {
            this.tomcat.setConnector(httpsConnector);
         }
      }

      if (this.configuration.http2) {
         Connector c = this.configuration.ssl ? this.tomcat.getService().findConnectors()[this.tomcat.getService().findConnectors().length - 1] : (Connector)Optional.ofNullable(this.tomcat.getRawConnector()).orElse(this.tomcat.getService().findConnectors()[0]);
         c.addUpgradeProtocol(new Http2Protocol());
         c.addSslHostConfig(this.buildSslHostConfig());
      }

      for(Connector c : this.configuration.connectors) {
         this.tomcat.getService().addConnector(c);
      }

      if (!this.configuration.skipHttp && !this.configuration.ssl && !this.configuration.connectors.isEmpty()) {
         this.tomcat.setConnector((Connector)this.configuration.connectors.iterator().next());
      }

      if (this.configuration.users != null) {
         for(Map.Entry<String, String> user : this.configuration.users.entrySet()) {
            this.tomcat.addUser((String)user.getKey(), (String)user.getValue());
         }
      }

      if (this.configuration.roles != null) {
         for(Map.Entry<String, String> user : this.configuration.roles.entrySet()) {
            for(String role : ((String)user.getValue()).split(" *, *")) {
               this.tomcat.addRole((String)user.getKey(), role);
            }
         }
      }

      StreamSupport.stream(ServiceLoader.load(InstanceCustomizer.class).spliterator(), false).forEach((cx) -> cx.accept(this.tomcat));
      this.configuration.instanceCustomizers.forEach((cx) -> cx.accept(this.tomcat));
      this.beforeStart();

      try {
         if (!initialized) {
            this.tomcat.init();
         }

         this.tomcat.getHost().addLifecycleListener((event) -> {
            if (Host.class.isInstance(event.getSource())) {
               this.broadcastHostEvent(event.getType(), (Host)Host.class.cast(event.getSource()));
            }
         });
         this.tomcat.start();
      } catch (LifecycleException e) {
         throw new IllegalStateException(e);
      }

      Optional.ofNullable(this.configuration.getPidFile()).ifPresent((pidFile) -> {
         if (pidFile.getParentFile() != null && !pidFile.getParentFile().isDirectory() && !pidFile.getParentFile().mkdirs()) {
            throw new IllegalArgumentException("Can't create " + pidFile);
         } else {
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            int at = pid.indexOf(64);

            try {
               Writer w = new FileWriter(pidFile);
               Throwable var4 = null;

               try {
                  w.write(at > 0 ? pid.substring(0, at) : pid);
               } catch (Throwable var14) {
                  var4 = var14;
                  throw var14;
               } finally {
                  if (w != null) {
                     if (var4 != null) {
                        try {
                           w.close();
                        } catch (Throwable var13) {
                           var4.addSuppressed(var13);
                        }
                     } else {
                        w.close();
                     }
                  }

               }

            } catch (IOException e) {
               throw new IllegalStateException("Can't write the pid in " + pid, e);
            }
         }
      });
      if (this.configuration.isUseShutdownHook()) {
         this.hook = new Thread(() -> {
            this.hook = null;
            this.close();
         }, "meecrowave-stop-hook");
         Runtime.getRuntime().addShutdownHook(this.hook);
      }

      return this;
   }

   private void setSystemProperty(Map<String, String> backupPropertyMap, String propertyKey, String newValue) {
      backupPropertyMap.put(propertyKey, System.getProperty(propertyKey));
      System.setProperty(propertyKey, newValue);
   }

   private void broadcastHostEvent(String event, Host host) {
      switch (event) {
         case "after_start":
            Connector connector = this.findFirstConnector();
            this.findContexts(host).forEach((ctx) -> this.fire(new StartListening(connector, host, ctx), ctx.getLoader().getClassLoader()));
            break;
         case "before_stop":
            Connector connector = this.findFirstConnector();
            this.findContexts(host).forEach((ctx) -> this.fire(new StopListening(connector, host, ctx), ctx.getLoader().getClassLoader()));
      }

   }

   private Connector findFirstConnector() {
      return (Connector)Stream.of(this.tomcat.getServer().findServices()).flatMap((s) -> Stream.of(s.findConnectors())).findFirst().orElse((Object)null);
   }

   private Stream<Context> findContexts(Host host) {
      Stream var10000 = Stream.of(host.findChildren());
      Context.class.getClass();
      var10000 = var10000.filter(Context.class::isInstance);
      Context.class.getClass();
      return var10000.map(Context.class::cast).filter((ctx) -> ctx.getState().isAvailable());
   }

   private <T> void fire(T event, ClassLoader classLoader) {
      Thread thread = Thread.currentThread();
      ClassLoader loader = thread.getContextClassLoader();
      thread.setContextClassLoader(classLoader);

      try {
         WebBeansContext.currentInstance().getBeanManagerImpl().getEvent().select((Class)Class.class.cast(event.getClass()), new Annotation[0]).fire(event);
      } finally {
         thread.setContextClassLoader(loader);
      }

   }

   private void setupJmx(boolean skip) {
      try {
         Field registry = Registry.class.getDeclaredField("registry");
         registry.setAccessible(true);
         registry.set((Object)null, skip ? new NoDescriptorRegistry() : new Registry());
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   private SSLHostConfig buildSslHostConfig() {
      ObjectRecipe recipe = new ObjectRecipe(SSLHostConfig.class);

      for(String key : this.configuration.properties.stringPropertyNames()) {
         if (key.startsWith("connector.sslhostconfig.")) {
            String substring = key.substring("connector.sslhostconfig.".length());
            recipe.setProperty(substring, this.configuration.properties.getProperty(key));
         }
      }

      return (SSLHostConfig)SSLHostConfig.class.cast(recipe.create());
   }

   protected void beforeStart() {
   }

   protected void beforeStop() {
   }

   public <T> AutoCloseable inject(T instance) {
      BeanManager bm = CDI.current().getBeanManager();
      AnnotatedType<?> annotatedType = bm.createAnnotatedType(instance.getClass());
      InjectionTarget injectionTarget = bm.createInjectionTarget(annotatedType);
      CreationalContext<Object> creationalContext = bm.createCreationalContext((Contextual)null);
      injectionTarget.inject(instance, creationalContext);
      return creationalContext::release;
   }

   public void close() {
      if (this.tomcat != null) {
         if (this.hook != null) {
            Runtime.getRuntime().removeShutdownHook(this.hook);
            this.hook = null;
         }

         this.beforeStop();
         if (MeecrowaveContainerLoader.class.isInstance(this.tomcat.getServer().getParentClassLoader())) {
            try {
               ((MeecrowaveContainerLoader)MeecrowaveContainerLoader.class.cast(this.tomcat.getServer().getParentClassLoader())).close();
            } catch (IOException e) {
               (new LogFacade(Meecrowave.class.getName())).error(e.getMessage(), e);
            }
         }

         try {
            this.contexts.values().forEach(Runnable::run);
         } finally {
            try {
               this.tomcat.stop();
               this.tomcat.destroy();
            } catch (LifecycleException e) {
               throw new IllegalStateException(e);
            } finally {
               if (this.clearCatalinaSystemProperties) {
                  Stream.of("catalina.base", "catalina.home").forEach(System::clearProperty);
               }

               if (this.configuration.isUseLog4j2JulLogManager()) {
                  System.clearProperty("java.util.logging.manager");
               }

               Optional.ofNullable(this.postTask).ifPresent(Runnable::run);
               this.postTask = null;

               try {
                  if (this.base != null) {
                     IO.delete(this.base);
                  }

                  if (this.ownedTempDir != null) {
                     IO.delete(this.ownedTempDir);
                  }
               } catch (IllegalArgumentException var100) {
               } finally {
                  this.base = null;
                  Optional.ofNullable(this.configuration.getPidFile()).ifPresent(File::delete);
               }

            }
         }

      }
   }

   protected Connector createConnector() {
      Properties properties = this.configuration.properties;
      Connector connector;
      if (properties != null) {
         Map<String, String> attributes = new HashMap();
         ObjectRecipe recipe = new ObjectRecipe(Connector.class);

         for(String key : properties.stringPropertyNames()) {
            if (key.startsWith("connector.")) {
               String substring = key.substring("connector.".length());
               if (!substring.startsWith("sslhostconfig.")) {
                  if (!substring.startsWith("attributes.")) {
                     recipe.setProperty(substring, properties.getProperty(key));
                  } else {
                     attributes.put(substring.substring("attributes.".length()), properties.getProperty(key));
                  }
               }
            }
         }

         connector = recipe.getProperties().isEmpty() ? new Connector() : (Connector)Connector.class.cast(recipe.create());

         for(Map.Entry<String, String> attr : attributes.entrySet()) {
            connector.setAttribute((String)attr.getKey(), attr.getValue());
         }
      } else {
         connector = new Connector();
      }

      return connector;
   }

   private static Server createServer(String serverXml) {
      Catalina catalina = new Catalina() {
         protected void initDirs() {
         }

         protected void initStreams() {
         }

         protected void initNaming() {
         }
      };
      catalina.setConfigFile(serverXml);
      catalina.load();
      return catalina.getServer();
   }

   private File createDirectory(File parent, String directory) {
      File dir = new File(parent, directory);
      IO.mkdirs(dir);
      return dir;
   }

   private void synchronize(File base, String resourceBase) {
      if (resourceBase != null) {
         try {
            Map<String, URL> urls = (new ResourceFinder("")).getResourcesMap(resourceBase);

            for(Map.Entry<String, URL> u : urls.entrySet()) {
               InputStream is = ((URL)u.getValue()).openStream();
               Throwable var7 = null;

               try {
                  File to = new File(base, (String)u.getKey());
                  OutputStream os = new FileOutputStream(to);
                  Throwable var10 = null;

                  try {
                     IO.copy(is, os);
                  } catch (Throwable var35) {
                     var10 = var35;
                     throw var35;
                  } finally {
                     if (os != null) {
                        if (var10 != null) {
                           try {
                              os.close();
                           } catch (Throwable var34) {
                              var10.addSuppressed(var34);
                           }
                        } else {
                           os.close();
                        }
                     }

                  }

                  if ("server.xml".equals(u.getKey())) {
                     this.configuration.setServerXml(to.getAbsolutePath());
                  }
               } catch (Throwable var37) {
                  var7 = var37;
                  throw var37;
               } finally {
                  if (is != null) {
                     if (var7 != null) {
                        try {
                           is.close();
                        } catch (Throwable var33) {
                           var7.addSuppressed(var33);
                        }
                     } else {
                        is.close();
                     }
                  }

               }
            }

         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   private String newBaseDir() {
      String dir = this.configuration.dir;
      if (dir != null) {
         File dirFile = new File(dir);
         if (dirFile.exists()) {
            if (this.base.exists() && this.configuration.deleteBaseOnStartup) {
               IO.delete(this.base);
            }

            return dir;
         } else {
            IO.mkdirs(dirFile);
            return dirFile.getAbsolutePath();
         }
      } else {
         File file = new File((File)Stream.of((new File(System.getProperty("meecrowave.base", "."), "temp")).getAbsolutePath(), "target", "build", ".").map(File::new).filter(File::isDirectory).findFirst().get(), "meecrowave-" + System.nanoTime());
         IO.mkdirs(file);
         return file.getAbsolutePath();
      }
   }

   public void await() {
      this.tomcat.getServer().await();
   }

   public static class Builder {
      @CliOption(
         name = "pid-file",
         description = "A file path to write the process id if the server starts"
      )
      private File pidFile;
      @CliOption(
         name = "watcher-bouncing",
         description = "Activate redeployment on directories update using this bouncing."
      )
      private int watcherBouncing = 500;
      @CliOption(
         name = "http",
         description = "HTTP port"
      )
      private int httpPort = 8080;
      @CliOption(
         name = "https",
         description = "HTTPS port"
      )
      private int httpsPort = 8443;
      @CliOption(
         name = "stop",
         description = "Shutdown port if used or -1"
      )
      private int stopPort = -1;
      @CliOption(
         name = "host",
         description = "Default host"
      )
      private String host = "localhost";
      @CliOption(
         name = "dir",
         description = "Root folder if provided otherwise a fake one is created in tmp-dir"
      )
      private String dir;
      @CliOption(
         name = "server-xml",
         description = "Provided server.xml"
      )
      private File serverXml;
      @CliOption(
         name = "keep-server-xml-as-this",
         description = "Don't replace ports in server.xml"
      )
      private boolean keepServerXmlAsThis;
      @CliOption(
         name = "properties",
         description = "Passthrough properties"
      )
      private Properties properties = new Properties();
      @CliOption(
         name = "quick-session",
         description = "Should an unsecured but fast session id generator be used"
      )
      private boolean quickSession = true;
      @CliOption(
         name = "skip-http",
         description = "Skip HTTP connector"
      )
      private boolean skipHttp;
      @CliOption(
         name = "ssl",
         description = "Use HTTPS"
      )
      private boolean ssl;
      @CliOption(
         name = "keystore-file",
         description = "HTTPS keystore location"
      )
      private String keystoreFile;
      @CliOption(
         name = "keystore-password",
         description = "HTTPS keystore password"
      )
      private String keystorePass;
      @CliOption(
         name = "keystore-type",
         description = "HTTPS keystore type"
      )
      private String keystoreType = "JKS";
      @CliOption(
         name = "client-auth",
         description = "HTTPS keystore client authentication"
      )
      private String clientAuth;
      @CliOption(
         name = "keystore-alias",
         description = "HTTPS keystore alias"
      )
      private String keyAlias;
      @CliOption(
         name = "ssl-protocol",
         description = "HTTPS protocol"
      )
      private String sslProtocol;
      @CliOption(
         name = "web-xml",
         description = "Global web.xml"
      )
      private String webXml;
      @CliOption(
         name = "login-config",
         description = "web.xml login config"
      )
      private LoginConfigBuilder loginConfig;
      @CliOption(
         name = "security-constraint",
         description = "web.xml security constraint"
      )
      private Collection<SecurityConstaintBuilder> securityConstraints = new LinkedList();
      @CliOption(
         name = "realm",
         description = "realm"
      )
      private Realm realm;
      @CliOption(
         name = "users",
         description = "In memory users"
      )
      private Map<String, String> users;
      @CliOption(
         name = "roles",
         description = "In memory roles"
      )
      private Map<String, String> roles;
      @CliOption(
         name = "http2",
         description = "Activate HTTP 2"
      )
      private boolean http2;
      @CliOption(
         name = "connector",
         description = "Custom connectors"
      )
      private final Collection<Connector> connectors = new ArrayList();
      @CliOption(
         name = "tmp-dir",
         description = "Temporary directory"
      )
      private String tempDir = System.getProperty("java.io.tmpdir");
      @CliOption(
         name = "web-resource-cached",
         description = "Cache web resources"
      )
      private boolean webResourceCached = true;
      @CliOption(
         name = "conf",
         description = "Conf folder to synchronize"
      )
      private String conf;
      @CliOption(
         name = "delete-on-startup",
         description = "Should the directory be cleaned on startup if existing"
      )
      private boolean deleteBaseOnStartup = true;
      @CliOption(
         name = "jaxrs-mapping",
         description = "Default jaxrs mapping"
      )
      private String jaxrsMapping = "/*";
      @CliOption(
         name = "cdi-conversation",
         description = "Should CDI conversation be activated"
      )
      private boolean cdiConversation;
      @CliOption(
         name = "jaxrs-provider-setup",
         description = "Should default JAX-RS provider be configured"
      )
      private boolean jaxrsProviderSetup = true;
      @CliOption(
         name = "jaxrs-default-providers",
         description = "If jaxrsProviderSetup is true the list of default providers to load (or defaulting to johnson jsonb and jsonp ones)"
      )
      private String jaxrsDefaultProviders;
      @CliOption(
         name = "jaxrs-beanvalidation",
         description = "Should bean validation be activated on JAX-RS endpoint if present in the classpath."
      )
      private boolean jaxrsAutoActivateBeanValidation = true;
      @CliOption(
         name = "jaxrs-log-provider",
         description = "Should JAX-RS providers be logged"
      )
      private boolean jaxrsLogProviders = false;
      @CliOption(
         name = "jsonp-buffer-strategy",
         description = "JSON-P JAX-RS provider buffer strategy (see johnzon)"
      )
      private String jsonpBufferStrategy;
      @CliOption(
         name = "jsonp-max-string-length",
         description = "JSON-P JAX-RS provider max string limit size (see johnzon)"
      )
      private int jsonpMaxStringLen;
      @CliOption(
         name = "jsonp-read-buffer-length",
         description = "JSON-P JAX-RS provider read buffer limit size (see johnzon)"
      )
      private int jsonpMaxReadBufferLen;
      @CliOption(
         name = "jsonp-write-buffer-length",
         description = "JSON-P JAX-RS provider write buffer limit size (see johnzon)"
      )
      private int jsonpMaxWriteBufferLen;
      @CliOption(
         name = "jsonp-supports-comment",
         description = "Should JSON-P JAX-RS provider support comments (see johnzon)"
      )
      private boolean jsonpSupportsComment;
      @CliOption(
         name = "jsonp-supports-comment",
         description = "Should JSON-P JAX-RS provider prettify the outputs (see johnzon)"
      )
      private boolean jsonpPrettify;
      @CliOption(
         name = "jsonb-encoding",
         description = "Which encoding provider JSON-B should use"
      )
      private String jsonbEncoding;
      @CliOption(
         name = "jsonb-nulls",
         description = "Should JSON-B provider serialize nulls"
      )
      private boolean jsonbNulls;
      @CliOption(
         name = "jsonb-ijson",
         description = "Should JSON-B provider comply to I-JSON"
      )
      private boolean jsonbIJson;
      @CliOption(
         name = "jsonb-prettify",
         description = "Should JSON-B provider prettify the output"
      )
      private boolean jsonbPrettify;
      @CliOption(
         name = "jsonb-binary-strategy",
         description = "Should JSON-B provider prettify the output"
      )
      private String jsonbBinaryStrategy;
      @CliOption(
         name = "jsonb-naming-strategy",
         description = "Should JSON-B provider prettify the output"
      )
      private String jsonbNamingStrategy;
      @CliOption(
         name = "jsonb-order-strategy",
         description = "Should JSON-B provider prettify the output"
      )
      private String jsonbOrderStrategy;
      @CliOption(
         name = "logging-global-setup",
         description = "Should logging be configured to use log4j2 (it is global)"
      )
      private boolean loggingGlobalSetup;
      @CliOption(
         name = "cxf-servlet-params",
         description = "Init parameters passed to CXF servlet"
      )
      private Map<String, String> cxfServletParams;
      @CliOption(
         name = "tomcat-scanning",
         description = "Should Tomcat scanning be used (@HandleTypes, @WebXXX)"
      )
      private boolean tomcatScanning;
      @CliOption(
         name = "tomcat-default-setup",
         description = "Add default servlet"
      )
      private boolean tomcatAutoSetup;
      @CliOption(
         name = "use-shutdown-hook",
         description = "Use shutdown hook to automatically stop the container on Ctrl+C"
      )
      private boolean useShutdownHook;
      @CliOption(
         name = "tomcat-filter",
         description = "A Tomcat JarScanFilter"
      )
      private String tomcatFilter;
      @CliOption(
         name = "scanning-include",
         description = "A forced include list of jar names (comma separated values)"
      )
      private String scanningIncludes;
      @CliOption(
         name = "scanning-exclude",
         description = "A forced exclude list of jar names (comma separated values)"
      )
      private String scanningExcludes;
      @CliOption(
         name = "scanning-package-include",
         description = "A forced include list of packages names (comma separated values)"
      )
      private String scanningPackageIncludes;
      @CliOption(
         name = "scanning-package-exclude",
         description = "A forced exclude list of packages names (comma separated values)"
      )
      private String scanningPackageExcludes;
      @CliOption(
         name = "tomcat-default",
         description = "Should Tomcat default be set (session timeout, mime mapping etc...)"
      )
      private boolean useTomcatDefaults;
      @CliOption(
         name = "tomcat-wrap-loader",
         description = "(Experimental) When deploying a classpath (current classloader), should meecrowave wrap the loader to define another loader identity but still use the same classes and resources."
      )
      private boolean tomcatWrapLoader;
      @CliOption(
         name = "tomcat-skip-jmx",
         description = "(Experimental) Should Tomcat MBeans be skipped."
      )
      private boolean tomcatNoJmx;
      @CliOption(
         name = "shared-libraries",
         description = "A folder containing shared libraries.",
         alias = {"shared-librairies"}
      )
      private String sharedLibraries;
      @CliOption(
         name = "log4j2-jul-bridge",
         description = "Should JUL logs be redirected to Log4j2 - only works before JUL usage."
      )
      private boolean useLog4j2JulLogManager;
      @CliOption(
         name = "servlet-container-initializer-injection",
         description = "Should ServletContainerInitialize support injections."
      )
      private boolean injectServletContainerInitializer;
      @CliOption(
         name = "tomcat-access-log-pattern",
         description = "Activates and configure the access log valve. Value example: '%h %l %u %t \"%r\" %s %b \"%{Referer}i\" \"%{User-Agent}i\"'"
      )
      private String tomcatAccessLogPattern;
      @CliOption(
         name = "meecrowave-properties",
         description = "Loads a meecrowave properties, defaults to meecrowave.properties."
      )
      private String meecrowaveProperties;
      @CliOption(
         name = "jaxws-support-if-present",
         description = "Should @WebService CDI beans be deployed if cxf-rt-frontend-jaxws is in the classpath."
      )
      private boolean jaxwsSupportIfAvailable;
      private final Map<Class<?>, Object> extensions;
      private final Collection<Consumer<Tomcat>> instanceCustomizers;

      public Builder() {
         this.jsonpBufferStrategy = BufferStrategy.QUEUE.name();
         this.jsonpMaxStringLen = 65536;
         this.jsonpMaxReadBufferLen = 65536;
         this.jsonpMaxWriteBufferLen = 65536;
         this.jsonpSupportsComment = false;
         this.jsonpPrettify = false;
         this.jsonbEncoding = "UTF-8";
         this.jsonbNulls = false;
         this.jsonbIJson = false;
         this.jsonbPrettify = false;
         this.loggingGlobalSetup = true;
         this.tomcatScanning = true;
         this.tomcatAutoSetup = true;
         this.useShutdownHook = true;
         this.useTomcatDefaults = true;
         this.tomcatWrapLoader = false;
         this.tomcatNoJmx = true;
         this.useLog4j2JulLogManager = System.getProperty("java.util.logging.manager") == null;
         this.injectServletContainerInitializer = true;
         this.meecrowaveProperties = "meecrowave.properties";
         this.jaxwsSupportIfAvailable = true;
         this.extensions = new HashMap();
         this.instanceCustomizers = new ArrayList();
         this.extensions.put(ValueTransformers.class, new ValueTransformers());
         StreamSupport.stream(ServiceLoader.load(ConfigurationCustomizer.class).spliterator(), false).forEach((c) -> c.accept(this));
         this.loadFrom(this.meecrowaveProperties);
      }

      public <T> T getExtension(Class<T> extension) {
         return (T)extension.cast(this.extensions.computeIfAbsent(extension, (k) -> {
            try {
               return this.bind(k.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
               throw new IllegalArgumentException(e);
            }
         }));
      }

      public boolean isJaxwsSupportIfAvailable() {
         return this.jaxwsSupportIfAvailable;
      }

      public void setJaxwsSupportIfAvailable(boolean jaxwsSupportIfAvailable) {
         this.jaxwsSupportIfAvailable = jaxwsSupportIfAvailable;
      }

      public int getWatcherBouncing() {
         return this.watcherBouncing;
      }

      public void setWatcherBouncing(int watcherBouncing) {
         this.watcherBouncing = watcherBouncing;
      }

      public String getTomcatAccessLogPattern() {
         return this.tomcatAccessLogPattern;
      }

      public void setTomcatAccessLogPattern(String tomcatAccessLogPattern) {
         this.tomcatAccessLogPattern = tomcatAccessLogPattern;
      }

      public boolean isTomcatNoJmx() {
         return this.tomcatNoJmx;
      }

      public void setTomcatNoJmx(boolean tomcatNoJmx) {
         this.tomcatNoJmx = tomcatNoJmx;
      }

      public File getPidFile() {
         return this.pidFile;
      }

      public void setPidFile(File pidFile) {
         this.pidFile = pidFile;
      }

      public String getScanningPackageIncludes() {
         return this.scanningPackageIncludes;
      }

      public void setScanningPackageIncludes(String scanningPackageIncludes) {
         this.scanningPackageIncludes = scanningPackageIncludes;
      }

      public String getScanningPackageExcludes() {
         return this.scanningPackageExcludes;
      }

      public void setScanningPackageExcludes(String scanningPackageExcludes) {
         this.scanningPackageExcludes = scanningPackageExcludes;
      }

      public Builder excludePackages(String packages) {
         this.setScanningPackageExcludes(packages);
         return this;
      }

      public Builder includePackages(String packages) {
         this.setScanningPackageIncludes(packages);
         return this;
      }

      public void setExtension(Class<?> type, Object value) {
         this.extensions.put(type, value);
      }

      public String getScanningIncludes() {
         return this.scanningIncludes;
      }

      public void setScanningIncludes(String scanningIncludes) {
         this.scanningIncludes = scanningIncludes;
      }

      public String getScanningExcludes() {
         return this.scanningExcludes;
      }

      public void setScanningExcludes(String scanningExcludes) {
         this.scanningExcludes = scanningExcludes;
      }

      public String getJsonpBufferStrategy() {
         return this.jsonpBufferStrategy;
      }

      public String getJsonbEncoding() {
         return this.jsonbEncoding;
      }

      public void setJsonbEncoding(String jsonbEncoding) {
         this.jsonbEncoding = jsonbEncoding;
      }

      public boolean isJsonbNulls() {
         return this.jsonbNulls;
      }

      public void setJsonbNulls(boolean jsonbNulls) {
         this.jsonbNulls = jsonbNulls;
      }

      public boolean isJsonbIJson() {
         return this.jsonbIJson;
      }

      public void setJsonbIJson(boolean jsonbIJson) {
         this.jsonbIJson = jsonbIJson;
      }

      public boolean isJsonbPrettify() {
         return this.jsonbPrettify;
      }

      public void setJsonbPrettify(boolean jsonbPrettify) {
         this.jsonbPrettify = jsonbPrettify;
      }

      public String getJsonbBinaryStrategy() {
         return this.jsonbBinaryStrategy;
      }

      public void setJsonbBinaryStrategy(String jsonbBinaryStrategy) {
         this.jsonbBinaryStrategy = jsonbBinaryStrategy;
      }

      public String getJsonbNamingStrategy() {
         return this.jsonbNamingStrategy;
      }

      public void setJsonbNamingStrategy(String jsonbNamingStrategy) {
         this.jsonbNamingStrategy = jsonbNamingStrategy;
      }

      public String getJsonbOrderStrategy() {
         return this.jsonbOrderStrategy;
      }

      public void setJsonbOrderStrategy(String jsonbOrderStrategy) {
         this.jsonbOrderStrategy = jsonbOrderStrategy;
      }

      public void setJsonpBufferStrategy(String jsonpBufferStrategy) {
         this.jsonpBufferStrategy = jsonpBufferStrategy;
      }

      public int getJsonpMaxStringLen() {
         return this.jsonpMaxStringLen;
      }

      public void setJsonpMaxStringLen(int jsonpMaxStringLen) {
         this.jsonpMaxStringLen = jsonpMaxStringLen;
      }

      public int getJsonpMaxReadBufferLen() {
         return this.jsonpMaxReadBufferLen;
      }

      public void setJsonpMaxReadBufferLen(int jsonpMaxReadBufferLen) {
         this.jsonpMaxReadBufferLen = jsonpMaxReadBufferLen;
      }

      public int getJsonpMaxWriteBufferLen() {
         return this.jsonpMaxWriteBufferLen;
      }

      public void setJsonpMaxWriteBufferLen(int jsonpMaxWriteBufferLen) {
         this.jsonpMaxWriteBufferLen = jsonpMaxWriteBufferLen;
      }

      public boolean isJsonpSupportsComment() {
         return this.jsonpSupportsComment;
      }

      public void setJsonpSupportsComment(boolean jsonpSupportsComment) {
         this.jsonpSupportsComment = jsonpSupportsComment;
      }

      public boolean isJsonpPrettify() {
         return this.jsonpPrettify;
      }

      public void setJsonpPrettify(boolean jsonpPrettify) {
         this.jsonpPrettify = jsonpPrettify;
      }

      public String getSharedLibraries() {
         return this.sharedLibraries;
      }

      public Builder sharedLibraries(String sharedLibraries) {
         this.setSharedLibraries(sharedLibraries);
         return this;
      }

      public void setSharedLibraries(String sharedLibraries) {
         this.sharedLibraries = sharedLibraries;
      }

      public boolean isJaxrsLogProviders() {
         return this.jaxrsLogProviders;
      }

      public void setJaxrsLogProviders(boolean jaxrsLogProviders) {
         this.jaxrsLogProviders = jaxrsLogProviders;
      }

      public boolean isUseTomcatDefaults() {
         return this.useTomcatDefaults;
      }

      public void setUseTomcatDefaults(boolean useTomcatDefaults) {
         this.useTomcatDefaults = useTomcatDefaults;
      }

      public String getTomcatFilter() {
         return this.tomcatFilter;
      }

      public void setTomcatFilter(String tomcatFilter) {
         this.tomcatFilter = tomcatFilter;
      }

      public boolean isTomcatScanning() {
         return this.tomcatScanning;
      }

      public void setTomcatScanning(boolean tomcatScanning) {
         this.tomcatScanning = tomcatScanning;
      }

      public Map<String, String> getCxfServletParams() {
         return this.cxfServletParams;
      }

      public void setCxfServletParams(Map<String, String> cxfServletParams) {
         this.cxfServletParams = cxfServletParams;
      }

      public boolean isLoggingGlobalSetup() {
         return this.loggingGlobalSetup;
      }

      public void setLoggingGlobalSetup(boolean loggingGlobalSetup) {
         this.loggingGlobalSetup = loggingGlobalSetup;
      }

      public boolean isJaxrsAutoActivateBeanValidation() {
         return this.jaxrsAutoActivateBeanValidation;
      }

      public void setJaxrsAutoActivateBeanValidation(boolean jaxrsAutoActivateBeanValidation) {
         this.jaxrsAutoActivateBeanValidation = jaxrsAutoActivateBeanValidation;
      }

      public boolean isJaxrsProviderSetup() {
         return this.jaxrsProviderSetup;
      }

      public void setJaxrsProviderSetup(boolean jaxrsProviderSetup) {
         this.jaxrsProviderSetup = jaxrsProviderSetup;
      }

      public int getHttpPort() {
         return this.httpPort;
      }

      public void setHttpPort(int httpPort) {
         this.httpPort = httpPort;
      }

      public int getHttpsPort() {
         return this.httpsPort;
      }

      public void setHttpsPort(int httpsPort) {
         this.httpsPort = httpsPort;
      }

      public int getStopPort() {
         return this.stopPort;
      }

      public void setStopPort(int stopPort) {
         this.stopPort = stopPort;
      }

      public String getHost() {
         return this.host;
      }

      public void setHost(String host) {
         this.host = host;
      }

      public String getDir() {
         return this.dir;
      }

      public void setDir(String dir) {
         this.dir = dir;
      }

      public File getServerXml() {
         return this.serverXml;
      }

      public void setServerXml(File serverXml) {
         this.serverXml = serverXml;
      }

      public boolean isKeepServerXmlAsThis() {
         return this.keepServerXmlAsThis;
      }

      public void setKeepServerXmlAsThis(boolean keepServerXmlAsThis) {
         this.keepServerXmlAsThis = keepServerXmlAsThis;
      }

      public Properties getProperties() {
         return this.properties;
      }

      public void setProperties(Properties properties) {
         this.properties = properties;
      }

      public boolean isQuickSession() {
         return this.quickSession;
      }

      public void setQuickSession(boolean quickSession) {
         this.quickSession = quickSession;
      }

      public boolean isSkipHttp() {
         return this.skipHttp;
      }

      public void setSkipHttp(boolean skipHttp) {
         this.skipHttp = skipHttp;
      }

      public boolean isSsl() {
         return this.ssl;
      }

      public void setSsl(boolean ssl) {
         this.ssl = ssl;
      }

      public String getKeystoreFile() {
         return this.keystoreFile;
      }

      public void setKeystoreFile(String keystoreFile) {
         this.keystoreFile = keystoreFile;
      }

      public String getKeystorePass() {
         return this.keystorePass;
      }

      public void setKeystorePass(String keystorePass) {
         this.keystorePass = keystorePass;
      }

      public String getKeystoreType() {
         return this.keystoreType;
      }

      public void setKeystoreType(String keystoreType) {
         this.keystoreType = keystoreType;
      }

      public String getClientAuth() {
         return this.clientAuth;
      }

      public void setClientAuth(String clientAuth) {
         this.clientAuth = clientAuth;
      }

      public String getKeyAlias() {
         return this.keyAlias;
      }

      public void setKeyAlias(String keyAlias) {
         this.keyAlias = keyAlias;
      }

      public String getSslProtocol() {
         return this.sslProtocol;
      }

      public void setSslProtocol(String sslProtocol) {
         this.sslProtocol = sslProtocol;
      }

      public String getWebXml() {
         return this.webXml;
      }

      public void setWebXml(String webXml) {
         this.webXml = webXml;
      }

      public LoginConfigBuilder getLoginConfig() {
         return this.loginConfig;
      }

      public Builder loginConfig(LoginConfigBuilder loginConfig) {
         this.setLoginConfig(loginConfig);
         return this;
      }

      public void setLoginConfig(LoginConfigBuilder loginConfig) {
         this.loginConfig = loginConfig;
      }

      public Collection<SecurityConstaintBuilder> getSecurityConstraints() {
         return this.securityConstraints;
      }

      public Builder securityConstraints(SecurityConstaintBuilder securityConstraint) {
         this.securityConstraints = (Collection<SecurityConstaintBuilder>)(this.securityConstraints == null ? new ArrayList() : this.securityConstraints);
         this.securityConstraints.add(securityConstraint);
         return this;
      }

      public void setSecurityConstraints(Collection<SecurityConstaintBuilder> securityConstraints) {
         this.securityConstraints = securityConstraints;
      }

      public Realm getRealm() {
         return this.realm;
      }

      public Builder realm(Realm realm) {
         this.setRealm(realm);
         return this;
      }

      public void setRealm(Realm realm) {
         this.realm = realm;
      }

      public Map<String, String> getUsers() {
         return this.users;
      }

      public void setUsers(Map<String, String> users) {
         this.users = users;
      }

      public Map<String, String> getRoles() {
         return this.roles;
      }

      public void setRoles(Map<String, String> roles) {
         this.roles = roles;
      }

      public boolean isHttp2() {
         return this.http2;
      }

      public void setHttp2(boolean http2) {
         this.http2 = http2;
      }

      public Collection<Connector> getConnectors() {
         return this.connectors;
      }

      public String getTempDir() {
         return this.tempDir;
      }

      public void setTempDir(String tempDir) {
         this.tempDir = tempDir;
      }

      public boolean isWebResourceCached() {
         return this.webResourceCached;
      }

      public void setWebResourceCached(boolean webResourceCached) {
         this.webResourceCached = webResourceCached;
      }

      public String getConf() {
         return this.conf;
      }

      public void setConf(String conf) {
         this.conf = conf;
      }

      public boolean isDeleteBaseOnStartup() {
         return this.deleteBaseOnStartup;
      }

      public void setDeleteBaseOnStartup(boolean deleteBaseOnStartup) {
         this.deleteBaseOnStartup = deleteBaseOnStartup;
      }

      public String getJaxrsMapping() {
         return this.jaxrsMapping;
      }

      public void setJaxrsMapping(String jaxrsMapping) {
         this.jaxrsMapping = jaxrsMapping;
      }

      public boolean isCdiConversation() {
         return this.cdiConversation;
      }

      public void setCdiConversation(boolean cdiConversation) {
         this.cdiConversation = cdiConversation;
      }

      public Builder randomHttpPort() {
         try {
            ServerSocket serverSocket = new ServerSocket(0);
            Throwable var2 = null;

            try {
               this.httpPort = serverSocket.getLocalPort();
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (serverSocket != null) {
                  if (var2 != null) {
                     try {
                        serverSocket.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     serverSocket.close();
                  }
               }

            }

            return this;
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      }

      public Builder loadFrom(String resource) {
         try {
            final InputStream is = this.findStream(resource);
            Throwable var3 = null;

            Builder var17;
            try {
               if (is != null) {
                  Properties config = new Properties() {
                     {
                        this.load(is);
                     }
                  };
                  this.loadFromProperties(config);
               }

               var17 = this;
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (is != null) {
                  if (var3 != null) {
                     try {
                        is.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     is.close();
                  }
               }

            }

            return var17;
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      }

      public void setServerXml(String file) {
         if (file == null) {
            this.serverXml = null;
         } else {
            File sXml = new File(file);
            if (sXml.exists()) {
               this.serverXml = sXml;
            }
         }

      }

      public Builder property(String key, String value) {
         this.properties.setProperty(key, value);
         return this;
      }

      private InputStream findStream(String resource) throws FileNotFoundException {
         InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
         if (stream == null) {
            File file = new File(resource);
            if (file.exists()) {
               return new FileInputStream(file);
            }
         }

         return stream;
      }

      public Builder user(String name, String pwd) {
         if (this.users == null) {
            this.users = new HashMap();
         }

         this.users.put(name, pwd);
         return this;
      }

      public Builder role(String user, String roles) {
         if (this.roles == null) {
            this.roles = new HashMap();
         }

         this.roles.put(user, roles);
         return this;
      }

      public Builder cxfServletParam(String key, String value) {
         if (this.cxfServletParams == null) {
            this.cxfServletParams = new HashMap();
         }

         this.cxfServletParams.put(key, value);
         return this;
      }

      public String getActiveProtocol() {
         return this.isSkipHttp() ? "https" : "http";
      }

      public int getActivePort() {
         return this.isSkipHttp() ? this.getHttpsPort() : this.getHttpPort();
      }

      public boolean isTomcatAutoSetup() {
         return this.tomcatAutoSetup;
      }

      public void setTomcatAutoSetup(boolean tomcatAutoSetup) {
         this.tomcatAutoSetup = tomcatAutoSetup;
      }

      public boolean isUseShutdownHook() {
         return this.useShutdownHook;
      }

      public void setUseShutdownHook(boolean useShutdownHook) {
         this.useShutdownHook = useShutdownHook;
      }

      public Builder noShutdownHook() {
         this.setUseShutdownHook(false);
         return this;
      }

      public boolean isTomcatWrapLoader() {
         return this.tomcatWrapLoader;
      }

      public void setTomcatWrapLoader(boolean tomcatWrapLoader) {
         this.tomcatWrapLoader = tomcatWrapLoader;
      }

      public void addInstanceCustomizer(Consumer<Tomcat> customizer) {
         this.instanceCustomizers.add(customizer);
      }

      public Builder instanceCustomizer(Consumer<Tomcat> customizer) {
         this.addInstanceCustomizer(customizer);
         return this;
      }

      public void addCustomizer(Consumer<Builder> configurationCustomizer) {
         configurationCustomizer.accept(this);
      }

      public String getJaxrsDefaultProviders() {
         return this.jaxrsDefaultProviders;
      }

      public void setJaxrsDefaultProviders(String jaxrsDefaultProviders) {
         this.jaxrsDefaultProviders = jaxrsDefaultProviders;
      }

      public boolean isUseLog4j2JulLogManager() {
         return this.useLog4j2JulLogManager;
      }

      public void setUseLog4j2JulLogManager(boolean useLog4j2JulLogManager) {
         this.useLog4j2JulLogManager = useLog4j2JulLogManager;
      }

      public void loadFromProperties(final Properties config) {
         StrSubstitutor strSubstitutor = new StrSubstitutor(new StrLookup<String>() {
            public String lookup(String key) {
               String property = System.getProperty(key);
               return property == null ? config.getProperty(key) : null;
            }
         });
         ValueTransformers transformers = (ValueTransformers)this.getExtension(ValueTransformers.class);

         for(String key : config.stringPropertyNames()) {
            String val = config.getProperty(key);
            if (val != null && !val.trim().isEmpty()) {
               String newVal = transformers.apply(strSubstitutor.replace(config.getProperty(key)));
               if (!val.equals(newVal)) {
                  config.setProperty(key, newVal);
               }
            }
         }

         for(Field field : Builder.class.getDeclaredFields()) {
            CliOption annotation = (CliOption)field.getAnnotation(CliOption.class);
            if (annotation != null) {
               String name = field.getName();
               Stream var10000 = Stream.of(Stream.of(annotation.name()), Stream.of(annotation.alias())).flatMap((a) -> a);
               config.getClass();
               var10000.map(config::getProperty).filter(Objects::nonNull).findAny().ifPresent((valx) -> {
                  Object toSet;
                  if (field.getType() == String.class) {
                     toSet = valx;
                  } else if (field.getType() == Integer.TYPE) {
                     if ("httpPort".equals(name) && "-1".equals(valx)) {
                        this.randomHttpPort();
                        toSet = null;
                     } else {
                        toSet = Integer.parseInt(valx);
                     }
                  } else if (field.getType() == Boolean.TYPE) {
                     toSet = Boolean.parseBoolean(valx);
                  } else if (field.getType() == File.class) {
                     toSet = new File(valx);
                  } else {
                     toSet = null;
                  }

                  if (toSet != null) {
                     if (!field.isAccessible()) {
                        field.setAccessible(true);
                     }

                     try {
                        field.set(this, toSet);
                     } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                     }
                  }
               });
            }
         }

         for(String prop : config.stringPropertyNames()) {
            if (prop.startsWith("properties.")) {
               this.property(prop.substring("properties.".length()), config.getProperty(prop));
            } else if (prop.startsWith("users.")) {
               this.user(prop.substring("users.".length()), config.getProperty(prop));
            } else if (prop.startsWith("roles.")) {
               this.role(prop.substring("roles.".length()), config.getProperty(prop));
            } else if (prop.startsWith("cxf.servlet.params.")) {
               this.cxfServletParam(prop.substring("cxf.servlet.params.".length()), config.getProperty(prop));
            } else if (prop.startsWith("connector.")) {
               this.property(prop, config.getProperty(prop));
            } else if (prop.equals("realm")) {
               ObjectRecipe recipe = new ObjectRecipe(config.getProperty(prop));

               for(String realmConfig : config.stringPropertyNames()) {
                  if (realmConfig.startsWith("realm.")) {
                     recipe.setProperty(realmConfig.substring("realm.".length()), config.getProperty(realmConfig));
                  }
               }

               this.realm = (Realm)Realm.class.cast(recipe.create());
            } else if (prop.equals("login")) {
               ObjectRecipe recipe = new ObjectRecipe(LoginConfigBuilder.class.getName());

               for(String nestedConfig : config.stringPropertyNames()) {
                  if (nestedConfig.startsWith("login.")) {
                     recipe.setProperty(nestedConfig.substring("login.".length()), config.getProperty(nestedConfig));
                  }
               }

               this.loginConfig = (LoginConfigBuilder)LoginConfigBuilder.class.cast(recipe.create());
            } else if (prop.equals("securityConstraint")) {
               ObjectRecipe recipe = new ObjectRecipe(SecurityConstaintBuilder.class.getName());

               for(String nestedConfig : config.stringPropertyNames()) {
                  if (nestedConfig.startsWith("securityConstraint.")) {
                     recipe.setProperty(nestedConfig.substring("securityConstraint.".length()), config.getProperty(nestedConfig));
                  }
               }

               this.securityConstraints.add(SecurityConstaintBuilder.class.cast(recipe.create()));
            } else if (prop.equals("configurationCustomizer")) {
               ObjectRecipe recipe = new ObjectRecipe(this.properties.getProperty(prop));

               for(String nestedConfig : config.stringPropertyNames()) {
                  if (nestedConfig.startsWith(prop + '.')) {
                     recipe.setProperty(nestedConfig.substring(prop.length() + 2), config.getProperty(nestedConfig));
                  }
               }

               this.addCustomizer((Consumer)Consumer.class.cast(recipe.create()));
            }
         }

      }

      public <T> T bind(T instance) {
         ValueTransformers transformers = (ValueTransformers)this.getExtension(ValueTransformers.class);
         Class<? extends Object> type = instance.getClass();

         do {
            Stream.of(type.getDeclaredFields()).filter((f) -> f.isAnnotationPresent(CliOption.class)).forEach((f) -> {
               CliOption annotation = (CliOption)f.getAnnotation(CliOption.class);
               String value = this.properties.getProperty(annotation.name());
               if (value == null) {
                  Stream var10000 = Stream.of(annotation.alias());
                  Properties var10001 = this.properties;
                  var10001.getClass();
                  value = (String)var10000.map(var10001::getProperty).findFirst().orElse((Object)null);
                  if (value == null) {
                     return;
                  }
               }

               value = transformers.apply(value);
               if (!f.isAccessible()) {
                  f.setAccessible(true);
               }

               Class<?> t = f.getType();

               try {
                  if (t == String.class) {
                     f.set(instance, value);
                  } else if (t == Integer.TYPE) {
                     f.set(instance, Integer.parseInt(value));
                  } else {
                     if (t != Boolean.TYPE) {
                        throw new IllegalArgumentException("Unsupported type " + t);
                     }

                     f.set(instance, Boolean.parseBoolean(value));
                  }

               } catch (IllegalAccessException iae) {
                  throw new IllegalStateException(iae);
               }
            });
            type = type.getSuperclass();
         } while(type != Object.class);

         return instance;
      }

      public boolean isInjectServletContainerInitializer() {
         return this.injectServletContainerInitializer;
      }

      public void setInjectServletContainerInitializer(boolean injectServletContainerInitializer) {
         this.injectServletContainerInitializer = injectServletContainerInitializer;
      }

      public String getMeecrowaveProperties() {
         return this.meecrowaveProperties;
      }

      public void setMeecrowaveProperties(String meecrowaveProperties) {
         this.meecrowaveProperties = meecrowaveProperties;
      }
   }

   public static class ValueTransformers implements Function<String, String> {
      private final Map<String, ValueTransformer> transformers = new HashMap();

      public String apply(String value) {
         if (!value.startsWith("decode:")) {
            return value;
         } else {
            if (this.transformers.isEmpty()) {
               this.transformers.put("Static3DES", new ValueTransformer() {
                  private final SecretKeySpec key = new SecretKeySpec(new byte[]{118, 111, -70, 57, 49, 47, 13, 74, -93, -112, 85, -2, 85, 101, 97, 19, 52, -126, 18, 23, -84, 119, 57, 25}, "DESede");

                  public String name() {
                     return "Static3DES";
                  }

                  public String apply(String encodedPassword) {
                     Objects.requireNonNull(encodedPassword, "value can't be null");

                     try {
                        byte[] cipherText = Base64.getDecoder().decode(encodedPassword);
                        Cipher cipher = Cipher.getInstance("DESede");
                        cipher.init(2, this.key);
                        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
                     } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                     }
                  }
               });

               for(ValueTransformer t : ServiceLoader.load(ValueTransformer.class)) {
                  this.transformers.put(t.name(), t);
               }
            }

            String substring = value.substring("decode:".length());
            int sep = substring.indexOf(58);
            if (sep < 0) {
               throw new IllegalArgumentException("No transformer algorithm for " + value);
            } else {
               String algo = substring.substring(0, sep);
               return (String)((ValueTransformer)Objects.requireNonNull(this.transformers.get(algo), "No ValueTransformer for value '" + value + "'")).apply(substring.substring(sep + 1));
            }
         }
      }
   }

   public static class LoginConfigBuilder {
      private final LoginConfig loginConfig = new LoginConfig();

      public void setErrorPage(String errorPage) {
         this.loginConfig.setErrorPage(errorPage);
      }

      public void setLoginPage(String loginPage) {
         this.loginConfig.setLoginPage(loginPage);
      }

      public void setRealmName(String realmName) {
         this.loginConfig.setRealmName(realmName);
      }

      public void setAuthMethod(String authMethod) {
         this.loginConfig.setAuthMethod(authMethod);
      }

      public LoginConfigBuilder errorPage(String errorPage) {
         this.loginConfig.setErrorPage(errorPage);
         return this;
      }

      public LoginConfigBuilder loginPage(String loginPage) {
         this.loginConfig.setLoginPage(loginPage);
         return this;
      }

      public LoginConfigBuilder realmName(String realmName) {
         this.loginConfig.setRealmName(realmName);
         return this;
      }

      public LoginConfigBuilder authMethod(String authMethod) {
         this.loginConfig.setAuthMethod(authMethod);
         return this;
      }

      public LoginConfig build() {
         return this.loginConfig;
      }

      public LoginConfigBuilder basic() {
         return this.authMethod("BASIC");
      }

      public LoginConfigBuilder digest() {
         return this.authMethod("DIGEST");
      }

      public LoginConfigBuilder clientCert() {
         return this.authMethod("CLIENT-CERT");
      }

      public LoginConfigBuilder form() {
         return this.authMethod("FORM");
      }
   }

   public static class SecurityConstaintBuilder {
      private final SecurityConstraint securityConstraint = new SecurityConstraint();

      public SecurityConstaintBuilder authConstraint(boolean authConstraint) {
         this.securityConstraint.setAuthConstraint(authConstraint);
         return this;
      }

      public SecurityConstaintBuilder displayName(String displayName) {
         this.securityConstraint.setDisplayName(displayName);
         return this;
      }

      public SecurityConstaintBuilder userConstraint(String constraint) {
         this.securityConstraint.setUserConstraint(constraint);
         return this;
      }

      public SecurityConstaintBuilder addAuthRole(String authRole) {
         this.securityConstraint.addAuthRole(authRole);
         return this;
      }

      public SecurityConstaintBuilder addCollection(String name, String pattern, String... methods) {
         SecurityCollection collection = new SecurityCollection();
         collection.setName(name);
         collection.addPattern(pattern);

         for(String httpMethod : methods) {
            collection.addMethod(httpMethod);
         }

         this.securityConstraint.addCollection(collection);
         return this;
      }

      public void setAuthConstraint(boolean authConstraint) {
         this.securityConstraint.setAuthConstraint(authConstraint);
      }

      public void setDisplayName(String displayName) {
         this.securityConstraint.setDisplayName(displayName);
      }

      public void setUserConstraint(String userConstraint) {
         this.securityConstraint.setUserConstraint(userConstraint);
      }

      public void setAuthRole(String authRole) {
         this.addAuthRole(authRole);
      }

      public void setCollection(String value) {
         String[] split = value.split(":");
         if (split.length != 3 && split.length != 2) {
            throw new IllegalArgumentException("Can't parse " + value + ", syntax is: name:pattern:method1/method2");
         } else {
            this.addCollection(split[0], split[1], split.length == 2 ? new String[0] : split[2].split("/"));
         }
      }

      public SecurityConstraint build() {
         return this.securityConstraint;
      }
   }

   private static class InternalTomcat extends Tomcat {
      private Connector connector;

      private InternalTomcat() {
      }

      private void server(Server s) {
         this.server = s;
         this.connector = this.server != null && this.server.findServices().length > 0 && this.server.findServices()[0].findConnectors().length > 0 ? this.server.findServices()[0].findConnectors()[0] : null;
      }

      Connector getRawConnector() {
         return this.connector;
      }
   }

   private static class TomcatWithFastSessionIDs extends InternalTomcat {
      private TomcatWithFastSessionIDs() {
      }

      public void start() throws LifecycleException {
         Server server = this.getServer();

         for(Service service : server.findServices()) {
            Container e = service.getContainer();

            for(Container h : e.findChildren()) {
               for(Container c : h.findChildren()) {
                  Manager m = ((Context)c).getManager();
                  if (m == null) {
                     m = new StandardManager();
                     ((Context)Context.class.cast(c)).setManager(m);
                  }

                  if (m instanceof ManagerBase) {
                     ((ManagerBase)ManagerBase.class.cast(m)).setSecureRandomClass("org.apache.catalina.startup.FastNonSecureRandom");
                  }
               }
            }
         }

         super.start();
      }
   }

   private static class QuickServerXmlParser extends DefaultHandler {
      private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();
      private static final String STOP_KEY = "STOP";
      private static final String HTTP_KEY = "HTTP";
      private static final String SECURED_SUFFIX = "S";
      private static final String HOST_KEY = "host";
      private static final String DEFAULT_CONNECTOR_KEY = "HTTP";
      private static final String DEFAULT_HTTP_PORT = "8080";
      private static final String DEFAULT_HTTPS_PORT = "8443";
      private static final String DEFAULT_STOP_PORT = "8005";
      private static final String DEFAULT_HOST = "localhost";
      private final Map<String, String> values;

      private QuickServerXmlParser(boolean useDefaults) {
         this.values = new TreeMap(String.CASE_INSENSITIVE_ORDER);
         if (useDefaults) {
            this.values.put("STOP", "8005");
            this.values.put("HTTP", "8080");
            this.values.put("host", "localhost");
         }

      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if ("Server".equalsIgnoreCase(localName)) {
            String port = attributes.getValue("port");
            if (port != null) {
               this.values.put("STOP", port);
            } else {
               this.values.put("STOP", "8005");
            }
         } else if ("Connector".equalsIgnoreCase(localName)) {
            String protocol = attributes.getValue("protocol");
            if (protocol == null) {
               protocol = "HTTP";
            } else if (protocol.contains("/")) {
               protocol = protocol.substring(0, protocol.indexOf("/"));
            }

            String port = attributes.getValue("port");
            String ssl = attributes.getValue("secure");
            if (ssl != null && !"false".equalsIgnoreCase(ssl)) {
               this.values.put(protocol.toUpperCase() + "S", port);
            } else {
               this.values.put(protocol.toUpperCase(), port);
            }
         } else if ("Host".equalsIgnoreCase(localName)) {
            String host = attributes.getValue("name");
            if (host != null) {
               this.values.put("host", host);
            }
         }

      }

      private static QuickServerXmlParser parse(File serverXml) {
         return parse(serverXml, true);
      }

      private static QuickServerXmlParser parse(File serverXml, boolean defaults) {
         QuickServerXmlParser handler = new QuickServerXmlParser(defaults);

         try {
            SAXParser parser = FACTORY.newSAXParser();
            parser.parse(serverXml, handler);
         } catch (Exception var4) {
         }

         return handler;
      }

      public String http() {
         return this.value("HTTP", "8080");
      }

      private String https() {
         return this.securedValue("HTTP", "8443");
      }

      private String stop() {
         return this.value("STOP", "8005");
      }

      private String value(String key, String defaultValue) {
         String val = (String)this.values.get(key);
         return val == null ? defaultValue : val;
      }

      private String securedValue(String key, String defaultValue) {
         return this.value(key + "S", defaultValue);
      }

      static {
         FACTORY.setNamespaceAware(true);
         FACTORY.setValidating(false);
      }
   }

   public static class DeploymentMeta {
      private final String context;
      private final File docBase;
      private final Consumer<Context> consumer;

      public DeploymentMeta(String context, File docBase, Consumer<Context> consumer) {
         this.context = context;
         this.docBase = docBase;
         this.consumer = consumer;
      }
   }

   private static final class MeecrowaveContainerLoader extends URLClassLoader {
      private MeecrowaveContainerLoader(URL[] urls, ClassLoader parent) {
         super(urls, parent);
      }
   }

   public interface ConfigurationCustomizer extends Consumer<Builder> {
   }

   public interface InstanceCustomizer extends Consumer<Tomcat> {
   }
}
