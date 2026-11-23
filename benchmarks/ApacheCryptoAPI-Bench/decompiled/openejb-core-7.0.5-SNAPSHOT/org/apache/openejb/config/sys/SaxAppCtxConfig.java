package org.apache.openejb.config.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.BeanProperties;
import org.apache.openejb.config.DeploymentModule;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.EnvEntriesPropertiesDeployer;
import org.apache.openejb.config.PojoConfiguration;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Saxs;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxAppCtxConfig {
   private static final Logger LOGGER;

   public static void parse(AppModule appModule, InputSource source, EnvEntriesPropertiesDeployer envEntriesDeployer, BeanProperties beanProperties) throws SAXException, ParserConfigurationException, IOException {
      Saxs.factory().newSAXParser().parse(source, new SaxAppContextConfig(appModule, envEntriesDeployer, beanProperties));
   }

   private static boolean acceptModule(String id, DeploymentModule ejbModule) {
      return id == null || id.equals(ejbModule.getModuleId());
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, SaxAppContextConfig.class);
   }

   private static class SaxAppContextConfig extends StackHandler {
      private static final Collection<String> IMPORT_ALIASES = Arrays.asList("import", "include");
      private static final Collection<String> APPLICATION_ALIASES = Arrays.asList("appcontext", "app-context", "application");
      private static final Collection<String> POJOS_ALIASES = Arrays.asList("pojocontexts", "pojo-contexts", "pojos");
      private static final Collection<String> POJO_ALIASES = Arrays.asList("pojo");
      private static final Collection<String> BEAN_CONTEXTS_ALIASES = Arrays.asList("beancontexts", "bean-contexts", "ejbs");
      private static final Collection<String> WEBAPP_ALIASES = Arrays.asList("webapps", "webcontexts", "web-contexts", "wars");
      private static final Collection<String> MODULE_ALIASES = Arrays.asList("modulecontext", "module");
      private static final Collection<String> BEAN_CONTEXT_ALIASES = Arrays.asList("ejb", "beancontext", "bean-context");
      private static final Collection<String> CONFIGURATION_ALIASES = Arrays.asList("configuration", "properties", "settings");
      private static final Collection<String> RESOURCES_ALIASES = Arrays.asList("resources");
      private static final Collection<String> SERVICE_ALIASES = Arrays.asList("service");
      private static final Collection<String> RESOURCE_ALIASES = Arrays.asList("resource");
      private static final Collection<String> ENV_ENTRIES_ALIASES = Arrays.asList("enventries", "env-entries");
      private static final Collection<String> ENV_ENTRY_ALIASES = Arrays.asList("enventry", "env-entry");
      private final AppModule module;
      private final EnvEntriesPropertiesDeployer envEntriesDeployer;
      private final BeanProperties beanPropertiesDeployer;

      public SaxAppContextConfig(AppModule appModule, EnvEntriesPropertiesDeployer envEntriesDeployer, BeanProperties beanProperties) {
         this.module = appModule;
         this.envEntriesDeployer = envEntriesDeployer;
         this.beanPropertiesDeployer = beanProperties;
      }

      public void startDocument() throws SAXException {
         this.push(new Document());
      }

      private class Document extends DefaultHandler {
         private Document() {
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!SaxAppCtxConfig.SaxAppContextConfig.APPLICATION_ALIASES.contains(localName.toLowerCase()) && SystemInstance.get().getOptions().get("openejb.configuration.strict-tags", false)) {
               throw new IllegalStateException("Unsupported Element: " + localName);
            } else {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Root());
            }
         }
      }

      private class Root extends DefaultHandler {
         private Root() {
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String name = localName.toLowerCase();
            if (SaxAppCtxConfig.SaxAppContextConfig.CONFIGURATION_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Configuration("", SaxAppContextConfig.this.module.getProperties()));
            } else if (SaxAppCtxConfig.SaxAppContextConfig.ENV_ENTRIES_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new EnvEntries());
            } else if (SaxAppCtxConfig.SaxAppContextConfig.BEAN_CONTEXTS_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new BeanContexts((String)null));
            } else if (SaxAppCtxConfig.SaxAppContextConfig.MODULE_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new ModuleContext(attributes.getValue("id")));
            } else if (SaxAppCtxConfig.SaxAppContextConfig.WEBAPP_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new WebAppContext(attributes.getValue("id")));
            } else if (SaxAppCtxConfig.SaxAppContextConfig.POJOS_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Pojos());
            } else if (SaxAppCtxConfig.SaxAppContextConfig.RESOURCES_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new ResourcesConfig());
            } else {
               if (!SaxAppCtxConfig.SaxAppContextConfig.IMPORT_ALIASES.contains(name)) {
                  throw new IllegalStateException("Unsupported Element: " + localName);
               }

               this.importFile(attributes.getValue("path"));
               SaxAppContextConfig.this.push(new DefaultHandler());
            }

         }

         private void importFile(String path) throws SAXException {
            File file = new File(path);
            if (file.exists()) {
               try {
                  InputStream inputStream = new FileInputStream(file);
                  Throwable var4 = null;

                  try {
                     SaxAppCtxConfig.parse(SaxAppContextConfig.this.module, new InputSource(inputStream), SaxAppContextConfig.this.envEntriesDeployer, SaxAppContextConfig.this.beanPropertiesDeployer);
                  } catch (Throwable var35) {
                     var4 = var35;
                     throw var35;
                  } finally {
                     if (inputStream != null) {
                        if (var4 != null) {
                           try {
                              inputStream.close();
                           } catch (Throwable var31) {
                              var4.addSuppressed(var31);
                           }
                        } else {
                           inputStream.close();
                        }
                     }

                  }
               } catch (IOException | ParserConfigurationException e) {
                  throw new SAXException(e);
               }
            } else {
               ClassLoader cl = SaxAppContextConfig.this.module.getClassLoader();
               if (cl != null) {
                  try {
                     InputStream is = cl.getResourceAsStream(path);
                     Throwable var5 = null;

                     try {
                        if (is != null) {
                           try {
                              SaxAppCtxConfig.parse(SaxAppContextConfig.this.module, new InputSource(is), SaxAppContextConfig.this.envEntriesDeployer, SaxAppContextConfig.this.beanPropertiesDeployer);
                           } catch (IOException | ParserConfigurationException e) {
                              throw new SAXException(e);
                           }
                        } else {
                           SaxAppCtxConfig.LOGGER.warning("Can't find " + path);
                        }
                     } catch (Throwable var34) {
                        var5 = var34;
                        throw var34;
                     } finally {
                        if (is != null) {
                           if (var5 != null) {
                              try {
                                 is.close();
                              } catch (Throwable var32) {
                                 var5.addSuppressed(var32);
                              }
                           } else {
                              is.close();
                           }
                        }

                     }
                  } catch (IOException e) {
                     SaxAppCtxConfig.LOGGER.warning("Can't find " + path, (Throwable)e);
                  }
               } else {
                  SaxAppCtxConfig.LOGGER.warning("Can't find " + path + ", no classloader for the module " + SaxAppContextConfig.this.module);
               }
            }

         }
      }

      private final class Configuration extends StackHandler.Content {
         private final Properties properties;
         private final String prefix;

         private Configuration(String prefix, Properties properties) {
            this.properties = properties;
            this.prefix = prefix;
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) {
            SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Configuration(this.prefix + localName + ".", this.properties));
         }

         public void setValue(String text) {
            if (this.properties != null) {
               try {
                  for(Map.Entry<Object, Object> entry : (new PropertiesAdapter()).unmarshal(text).entrySet()) {
                     this.properties.put(this.prefix + entry.getKey(), entry.getValue());
                  }

               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }

      private final class MultipleConfiguration extends StackHandler.Content {
         private final Collection<Properties> properties;
         private final String prefix;

         private MultipleConfiguration(String prefix, Collection<Properties> properties) {
            this.properties = properties;
            this.prefix = prefix;
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) {
            SaxAppContextConfig.this.push(SaxAppContextConfig.this.new MultipleConfiguration(this.prefix + localName + ".", this.properties));
         }

         public void setValue(String text) {
            if (this.properties != null) {
               try {
                  for(Properties p : this.properties) {
                     for(Map.Entry<Object, Object> entry : (new PropertiesAdapter()).unmarshal(text).entrySet()) {
                        p.put(this.prefix + entry.getKey(), entry.getValue());
                     }
                  }

               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }

      private class Pojos extends DefaultHandler {
         protected final List<PojoConfig> genericConfigs;
         protected final Collection<String> aliases;

         private Pojos() {
            this((Collection)SaxAppCtxConfig.SaxAppContextConfig.POJO_ALIASES);
         }

         protected Pojos(Collection<String> aliases) {
            this.genericConfigs = new ArrayList();
            this.aliases = aliases;
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!this.aliases.contains(localName.toLowerCase())) {
               throw new IllegalStateException("Unsupported Element: " + localName);
            } else {
               PojoConfig beanConfig = this.newItem();
               String id = attributes.getValue("id");
               if (id == null || "*".equals(id)) {
                  this.genericConfigs.add(beanConfig);
               }

               SaxAppContextConfig.this.push(this.childParser(id, beanConfig));
            }
         }

         protected DefaultHandler childParser(String id, PojoConfig beanConfig) {
            return SaxAppContextConfig.this.new Pojo(id, beanConfig);
         }

         protected PojoConfig newItem() {
            return new PojoConfig();
         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            for(PojoConfig generic : this.genericConfigs) {
               for(PojoConfiguration config : SaxAppContextConfig.this.module.getPojoConfigurations().values()) {
                  for(String key : generic.getProperties().stringPropertyNames()) {
                     if (!config.getProperties().containsKey(key)) {
                        config.getProperties().put(key, generic.getProperties().get(key));
                     }
                  }
               }
            }

         }
      }

      private class ModuleContext extends DefaultHandler {
         protected final String id;

         ModuleContext(String id) {
            this.id = id;
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String name = localName.toLowerCase();
            if (SaxAppCtxConfig.SaxAppContextConfig.BEAN_CONTEXTS_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new BeanContexts(this.id));
            } else if (SaxAppCtxConfig.SaxAppContextConfig.POJOS_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Pojos());
            } else {
               if (!SaxAppCtxConfig.SaxAppContextConfig.CONFIGURATION_ALIASES.contains(name)) {
                  throw new IllegalStateException("Unsupported Element: " + localName);
               }

               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new MultipleConfiguration("", this.propertiesForModule(this.id)));
            }

         }

         protected Collection<Properties> propertiesForModule(String id) {
            Collection<Properties> props = new ArrayList();

            for(DeploymentModule m : SaxAppContextConfig.this.module.getDeploymentModule()) {
               if (SaxAppCtxConfig.acceptModule(id, m)) {
                  props.add(m.getProperties());
               }
            }

            return props;
         }
      }

      private final class WebAppContext extends ModuleContext {
         private WebAppContext(String id) {
            super(id);
         }

         protected Collection<Properties> propertiesForModule(String id) {
            Collection<Properties> props = new ArrayList();

            for(WebModule m : SaxAppContextConfig.this.module.getWebModules()) {
               if (SaxAppCtxConfig.acceptModule(id, m)) {
                  props.add(m.getProperties());
               }
            }

            return props;
         }
      }

      private class BeanContexts extends Pojos {
         private final String id;

         public BeanContexts(String id) {
            super((Collection)SaxAppCtxConfig.SaxAppContextConfig.BEAN_CONTEXT_ALIASES);
            this.id = id;
         }

         protected DefaultHandler childParser(String beanId, PojoConfig beanConfig) {
            return SaxAppContextConfig.this.new BeanContext(this.id, beanId, (BeanContextConfig)beanConfig);
         }

         protected PojoConfig newItem() {
            return new BeanContextConfig();
         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            for(PojoConfig generic : this.genericConfigs) {
               if (generic.hasProperties()) {
                  SaxAppContextConfig.this.beanPropertiesDeployer.addGlobalProperties(this.id, generic.getProperties());
               }
            }

         }
      }

      private class Pojo extends DefaultHandler {
         protected final PojoConfig pojoConfig;
         protected final String id;

         public Pojo(String id, PojoConfig beanConfig) {
            this.id = id;
            this.pojoConfig = beanConfig;
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (SaxAppCtxConfig.SaxAppContextConfig.CONFIGURATION_ALIASES.contains(localName.toLowerCase())) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new Configuration("", this.pojoConfig.getProperties()));
            } else {
               throw new IllegalStateException("Unsupported Element: " + localName);
            }
         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            SaxAppContextConfig.this.module.getPojoConfigurations().put(this.id, new PojoConfiguration(this.pojoConfig.getProperties()));
         }
      }

      private class BeanContext extends Pojo {
         private final String moduleId;

         public BeanContext(String moduleId, String id, BeanContextConfig beanConfig) {
            super(id, beanConfig);
            this.moduleId = moduleId;
         }

         public void endElement(String uri, String localName, String qName) throws SAXException {
            for(EjbModule ejbModule : SaxAppContextConfig.this.module.getEjbModules()) {
               if (SaxAppCtxConfig.acceptModule(this.moduleId, ejbModule) && this.pojoConfig.hasProperties()) {
                  SaxAppContextConfig.this.beanPropertiesDeployer.addProperties(this.id, this.pojoConfig.getProperties());
               }
            }

         }
      }

      private class ResourcesConfig extends DefaultHandler {
         private ResourcesConfig() {
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String name = localName.toLowerCase();
            if (SaxAppCtxConfig.SaxAppContextConfig.SERVICE_ALIASES.contains(name)) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new DeclaredServiceElement(SaxAppContextConfig.this.module.getServices()));
            } else {
               if (!SaxAppCtxConfig.SaxAppContextConfig.RESOURCE_ALIASES.contains(name)) {
                  throw new IllegalStateException("Unsupported Element: " + localName);
               }

               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new ResourceElement(SaxAppContextConfig.this.module.getResources()));
            }

            SaxAppContextConfig.this.get().startElement(uri, localName, qName, attributes);
         }
      }

      private class EnvEntries extends DefaultHandler {
         private EnvEntries() {
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (SaxAppCtxConfig.SaxAppContextConfig.ENV_ENTRY_ALIASES.contains(localName.toLowerCase())) {
               SaxAppContextConfig.this.push(SaxAppContextConfig.this.new EnvEntry());
               SaxAppContextConfig.this.get().startElement(uri, localName, qName, attributes);
            } else {
               throw new IllegalStateException("Unsupported Element: " + localName);
            }
         }
      }

      private class EnvEntry extends StackHandler.Content {
         private String key;

         private EnvEntry() {
         }

         public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (SaxAppCtxConfig.SaxAppContextConfig.ENV_ENTRY_ALIASES.contains(localName.toLowerCase())) {
               this.key = attributes.getValue("key");
            } else {
               throw new IllegalStateException("Unsupported Element: " + localName);
            }
         }

         public void setValue(String text) {
            SaxAppContextConfig.this.envEntriesDeployer.addEnvEntries(this.key, text);
         }
      }
   }
}
