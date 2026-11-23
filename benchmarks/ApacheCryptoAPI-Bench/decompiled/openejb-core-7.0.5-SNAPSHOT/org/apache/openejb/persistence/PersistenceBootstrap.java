package org.apache.openejb.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.apache.openejb.config.NewLoaderLogic;
import org.apache.openejb.core.TempClassLoader;
import org.apache.openejb.javaagent.Agent;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.JarLocation;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Saxs;
import org.apache.xbean.finder.ClassLoaders;
import org.apache.xbean.finder.UrlSet;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PersistenceBootstrap {
   public static final String DEFAULT_PROVIDER = getDefaultProvider();
   private static volatile boolean systemDone;
   private static boolean debug;

   private static String getDefaultProvider() {
      Class<PersistenceBootstrap> clzz = PersistenceBootstrap.class;
      String name = "/META-INF/" + clzz.getName() + ".provider";

      try {
         URL provider = clzz.getResource(name);
         if (provider != null) {
            String trim = IO.slurp(provider).trim();
            Logger.getLogger(PersistenceBootstrap.class.getName()).info("Default JPA Provider changed to " + trim + " specified by " + provider.toExternalForm());
            return trim;
         }
      } catch (Exception e) {
         Logger.getLogger(PersistenceBootstrap.class.getName()).log(Level.WARNING, "Could not read " + name, e);
      }

      return "org.apache.openjpa.persistence.PersistenceProviderImpl";
   }

   public static void bootstrap(ClassLoader classLoader) {
      if (classLoader == PersistenceBootstrap.class.getClassLoader()) {
         if (systemDone) {
            return;
         }

         systemDone = true;
      }

      Properties args = getAgentArgs(classLoader);
      debug = args.getProperty("debug", "false").equalsIgnoreCase("true");
      boolean enabled = args.getProperty("enabled", "true").equalsIgnoreCase("true");
      if (!enabled) {
         debug("disabled");
      } else {
         try {
            debug("searching for persistence.xml files");
            Collection<String> pXmlNames = new ArrayList();
            String altDD = getAltDD();
            if (altDD != null) {
               for(String p : altDD.split(",")) {
                  pXmlNames.add(p + ".persistence.xml");
                  pXmlNames.add(p + "-persistence.xml");
               }
            } else {
               pXmlNames.add("persistence.xml");
            }

            List<URL> urls = new LinkedList();

            for(String pXmlName : pXmlNames) {
               urls.addAll(Collections.list(classLoader.getResources("META-INF/" + pXmlName)));
               if ("true".equals(args.getProperty("web-scan", "false"))) {
                  try {
                     for(URL url : findUrls(classLoader, args)) {
                        File file = toFile(url);
                        if ("classes".equals(file.getName()) && "WEB-INF".equals(file.getParentFile().getName())) {
                           File pXml = new File(file.getParentFile(), pXmlName);
                           if (pXml.exists()) {
                              urls.add(pXml.toURI().toURL());
                           }
                           break;
                        }

                        if (file.getName().endsWith(".jar") && file.getParentFile().getName().equals("lib") && "WEB-INF".equals(file.getParentFile().getParentFile().getName())) {
                           File pXml = new File(file.getParentFile().getParentFile(), pXmlName);
                           if (pXml.exists()) {
                              urls.add(pXml.toURI().toURL());
                           }
                           break;
                        }
                     }
                  } catch (Throwable var22) {
                  }
               }
            }

            if (urls.size() == 0) {
               debug("no persistence.xml files found");
               return;
            }

            Map<String, Unit> units = new HashMap();

            for(URL url : urls) {
               String urlPath = url.toExternalForm();
               debug("found " + urlPath);

               try {
                  InputStream in = IO.read(url);

                  try {
                     collectUnits(in, units, args);
                  } catch (Throwable e) {
                     debug("failed to read " + urlPath, e);
                     in.close();
                  }
               } catch (Throwable e) {
                  debug("failed to read " + urlPath, e);
               }
            }

            for(Unit unit : units.values()) {
               String provider = unit.provider;
               String extraClassesKey = provider + "@classes";
               String unitNameKey = provider + "@unitName";
               String unitName = args.getProperty(unitNameKey, "classpath-bootstrap");
               String classes = args.getProperty(extraClassesKey);
               if (classes != null) {
                  debug("parsing value of " + extraClassesKey);

                  try {
                     List<String> list = Arrays.asList(classes.split("[ \n\r\t,]"));
                     unit.classes.addAll(list);
                  } catch (Exception e) {
                     debug("cannot parse: " + classes, e);
                  }
               }

               try {
                  if (provider.startsWith("org.hibernate")) {
                     debug("skipping: " + provider);
                  } else {
                     debug("starting: " + provider);
                     PersistenceUnitInfoImpl info = new PersistenceUnitInfoImpl(new Handler());
                     info.setManagedClassNames(new ArrayList(unit.classes));
                     info.setPersistenceProviderClassName(unit.provider);
                     info.setProperties(new Properties());
                     info.setId(unitName);
                     info.setPersistenceUnitName(unitName);
                     info.setRootUrlAndJarUrls("", Collections.EMPTY_LIST);
                     info.setJtaDataSource(new NullDataSource());
                     info.setNonJtaDataSource(new NullDataSource());
                     info.setExcludeUnlistedClasses(true);
                     info.setClassLoader(classLoader);

                     for(String name : unit.classes) {
                        debug("class " + name);
                     }

                     Class clazz = classLoader.loadClass(unit.provider);
                     PersistenceProvider persistenceProvider = (PersistenceProvider)clazz.newInstance();
                     EntityManagerFactory emf = persistenceProvider.createContainerEntityManagerFactory(info, new HashMap());
                     emf.close();
                     debug("success: " + provider);
                  }
               } catch (Throwable e) {
                  debug("failed: " + provider, e);
               }
            }
         } catch (Throwable t) {
            debug("error: ", t);
         }

      }
   }

   private static Set<URL> findUrls(ClassLoader classLoader, Properties args) throws IOException {
      if ("true".equals(args.getProperty("fast-scan", "true"))) {
         try {
            return new HashSet(NewLoaderLogic.applyBuiltinExcludes((new UrlSet(ClassLoaders.findUrls(classLoader))).excludeJvm()).getUrls());
         } catch (Throwable var3) {
         }
      }

      return ClassLoaders.findUrls(classLoader);
   }

   private static String getAltDD() {
      String property = "openejb.altdd.prefix";
      return SystemInstance.isInitialized() ? SystemInstance.get().getOptions().get("openejb.altdd.prefix", (String)null) : JavaSecurityManagers.getSystemProperty("openejb.altdd.prefix");
   }

   private static void debug(String x) {
      if (debug) {
         System.out.println("[PersistenceBootstrap] " + x);
      }

   }

   private static void debug(String x, Throwable t) {
      if (debug) {
         System.out.println(x);
         t.printStackTrace();
      }

   }

   private static Properties getAgentArgs(ClassLoader classLoader) {
      Properties properties = new Properties();
      String args = Agent.getAgentArgs();
      if (args != null && args.length() != 0) {
         for(String string : args.split("[ ,:&]")) {
            String[] strings = string.split("=");
            if (strings.length == 2) {
               properties.put(strings[0], strings[1]);
            }
         }

         debug = properties.getProperty("debug", "false").equalsIgnoreCase("true");
      }

      try {
         URL resource = classLoader.getResource("PersistenceBootstrap.properties");
         if (resource != null) {
            debug("found PersistenceBootstrap.properties file");
            IO.readProperties(resource, properties);
         }
      } catch (Throwable e) {
         debug("can't read PersistenceBootstrap.properties file", e);
      }

      return properties;
   }

   private static void collectUnits(InputStream in, final Map<String, Unit> units, final Properties args) throws ParserConfigurationException, SAXException, IOException {
      InputSource inputSource = new InputSource(in);
      SAXParser parser = Saxs.namespaceAwareFactory().newSAXParser();
      parser.parse(inputSource, new DefaultHandler() {
         private final StringBuilder characters = new StringBuilder(100);
         private Unit unit;

         public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.characters.setLength(0);
            if (localName.equals("persistence-unit")) {
               this.startPersistenceUnit(uri, localName, qName, attributes);
            }

         }

         public void startPersistenceUnit(String uri, String localName, String qName, Attributes attributes) {
            String unitName = attributes.getValue("name");
            this.unit = new Unit(unitName);
         }

         public void characters(char[] ch, int start, int length) {
            String text = new String(ch, start, length);
            this.characters.append(text.trim());
         }

         public void endElement(String uri, String localName, String qName) {
            if (localName.equals("persistence-unit")) {
               this.endPersistenceUnit(uri, localName, qName);
            } else if (localName.equals("provider")) {
               this.endProvider(uri, localName, qName);
            } else if (localName.equals("class")) {
               this.endClass(uri, localName, qName);
            }

         }

         public void endPersistenceUnit(String uri, String localName, String qName) {
            if (args.getProperty(this.unit.name + "@skip", "false").equalsIgnoreCase("true")) {
               PersistenceBootstrap.debug("skipping unit " + this.unit.name);
            } else {
               PersistenceBootstrap.debug("adding unit " + this.unit.name);
               if (this.unit.provider == null) {
                  this.unit.provider = PersistenceBootstrap.DEFAULT_PROVIDER;
               }

               Unit u = (Unit)units.get(this.unit.provider);
               if (u == null) {
                  units.put(this.unit.provider, this.unit);
               } else {
                  u.classes.addAll(this.unit.classes);
               }
            }

            this.unit = null;
         }

         public void endProvider(String uri, String localName, String qName) {
            this.unit.provider = this.characters.toString();
         }

         public void endClass(String uri, String localName, String qName) {
            this.unit.classes.add(this.characters.toString());
         }
      });
   }

   public static File toFile(URL url) {
      if ("jar".equals(url.getProtocol())) {
         try {
            String spec = url.getFile();
            int separator = spec.indexOf(33);
            if (separator == -1) {
               throw new MalformedURLException("no ! found in jar url spec:" + spec);
            } else {
               return toFile(new URL(spec.substring(0, separator++)));
            }
         } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
         }
      } else if ("file".equals(url.getProtocol())) {
         String path = JarLocation.decode(url.getFile());
         if (path.endsWith("!")) {
            path = path.substring(0, path.length() - 1);
         }

         return new File(path);
      } else {
         throw new IllegalArgumentException("Unsupported URL scheme: " + url.toExternalForm());
      }
   }

   private static class Unit {
      private String provider;
      private final Set<String> classes = new HashSet();
      private final String name;

      public Unit(String name) {
         this.name = name;
      }

      public String getProvider() {
         return this.provider;
      }

      public void setProvider(String provider) {
         this.provider = provider;
      }
   }

   private static class Handler implements PersistenceClassLoaderHandler {
      private Handler() {
      }

      public void addTransformer(String unitId, ClassLoader classLoader, ClassFileTransformer classFileTransformer) {
         Instrumentation instrumentation = Agent.getInstrumentation();
         if (instrumentation != null) {
            instrumentation.addTransformer(new Transformer(classFileTransformer));
         }

      }

      public void destroy(String unitId) {
      }

      public ClassLoader getNewTempClassLoader(ClassLoader classLoader) {
         return new TempClassLoader(classLoader);
      }
   }

   public static class Transformer implements ClassFileTransformer {
      private final ClassFileTransformer transformer;

      public Transformer(ClassFileTransformer transformer) {
         this.transformer = transformer;
      }

      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
         try {
            byte[] bytes = this.transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            if (bytes != null) {
               PersistenceBootstrap.debug("enhanced " + className);
            }

            return bytes;
         } catch (Throwable e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   private static class NullDataSource implements DataSource {
      private NullDataSource() {
      }

      public Connection getConnection() throws SQLException {
         return null;
      }

      public Logger getParentLogger() throws SQLFeatureNotSupportedException {
         return null;
      }

      public Connection getConnection(String username, String password) throws SQLException {
         return null;
      }

      public int getLoginTimeout() throws SQLException {
         return 0;
      }

      public PrintWriter getLogWriter() throws SQLException {
         return null;
      }

      public void setLoginTimeout(int seconds) throws SQLException {
      }

      public void setLogWriter(PrintWriter out) throws SQLException {
      }

      public boolean isWrapperFor(Class<?> iface) throws SQLException {
         return false;
      }

      public <T> T unwrap(Class<T> iface) throws SQLException {
         throw new SQLException();
      }
   }
}
