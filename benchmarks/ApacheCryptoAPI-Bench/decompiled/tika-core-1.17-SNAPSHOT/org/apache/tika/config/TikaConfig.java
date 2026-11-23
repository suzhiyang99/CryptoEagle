package org.apache.tika.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tika.concurrent.ConfigurableThreadPoolExecutor;
import org.apache.tika.concurrent.SimpleThreadPoolExecutor;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.CompositeEncodingDetector;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.translate.DefaultTranslator;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.apache.tika.parser.AbstractEncodingDetectorParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.utils.AnnotationUtils;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TikaConfig {
   protected static AtomicInteger TIMES_INSTANTIATED = new AtomicInteger();
   private final ServiceLoader serviceLoader;
   private final CompositeParser parser;
   private final CompositeDetector detector;
   private final Translator translator;
   private final MimeTypes mimeTypes;
   private final ExecutorService executorService;
   private final EncodingDetector encodingDetector;

   private static MimeTypes getDefaultMimeTypes(ClassLoader loader) {
      return MimeTypes.getDefaultMimeTypes(loader);
   }

   protected static CompositeDetector getDefaultDetector(MimeTypes types, ServiceLoader loader) {
      return new DefaultDetector(types, loader);
   }

   protected static CompositeEncodingDetector getDefaultEncodingDetector(ServiceLoader loader) {
      return new DefaultEncodingDetector(loader);
   }

   private static CompositeParser getDefaultParser(MimeTypes types, ServiceLoader loader, EncodingDetector encodingDetector) {
      return new DefaultParser(types.getMediaTypeRegistry(), loader, encodingDetector);
   }

   private static Translator getDefaultTranslator(ServiceLoader loader) {
      return new DefaultTranslator(loader);
   }

   private static ConfigurableThreadPoolExecutor getDefaultExecutorService() {
      return new SimpleThreadPoolExecutor();
   }

   public TikaConfig(String file) throws TikaException, IOException, SAXException {
      this(Paths.get(file));
   }

   public TikaConfig(Path path) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(path.toFile()));
   }

   public TikaConfig(Path path, ServiceLoader loader) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(path.toFile()), loader);
   }

   public TikaConfig(File file) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(file));
   }

   public TikaConfig(File file, ServiceLoader loader) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(file), loader);
   }

   public TikaConfig(URL url) throws TikaException, IOException, SAXException {
      this(url, ServiceLoader.getContextClassLoader());
   }

   public TikaConfig(URL url, ClassLoader loader) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(url.toString()).getDocumentElement(), loader);
   }

   public TikaConfig(URL url, ServiceLoader loader) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(url.toString()).getDocumentElement(), loader);
   }

   public TikaConfig(InputStream stream) throws TikaException, IOException, SAXException {
      this(XMLReaderUtils.getDocumentBuilder().parse(stream));
   }

   public TikaConfig(Document document) throws TikaException, IOException {
      this(document.getDocumentElement());
   }

   public TikaConfig(Document document, ServiceLoader loader) throws TikaException, IOException {
      this(document.getDocumentElement(), loader);
   }

   public TikaConfig(Element element) throws TikaException, IOException {
      this(element, serviceLoaderFromDomElement(element, (ClassLoader)null));
   }

   public TikaConfig(Element element, ClassLoader loader) throws TikaException, IOException {
      this(element, serviceLoaderFromDomElement(element, loader));
   }

   private TikaConfig(Element element, ServiceLoader loader) throws TikaException, IOException {
      DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
      TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
      ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
      EncodingDetectorXmlLoader encodingDetectorXmlLoader = new EncodingDetectorXmlLoader();
      this.mimeTypes = typesFromDomElement(element);
      this.detector = (CompositeDetector)detectorLoader.loadOverall(element, this.mimeTypes, loader);
      this.encodingDetector = (EncodingDetector)encodingDetectorXmlLoader.loadOverall(element, this.mimeTypes, loader);
      ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
      this.parser = (CompositeParser)parserLoader.loadOverall(element, this.mimeTypes, loader);
      this.translator = (Translator)translatorLoader.loadOverall(element, this.mimeTypes, loader);
      this.executorService = (ExecutorService)executorLoader.loadOverall(element, this.mimeTypes, loader);
      this.serviceLoader = loader;
      TIMES_INSTANTIATED.incrementAndGet();
   }

   public TikaConfig(ClassLoader loader) throws MimeTypeException, IOException {
      this.serviceLoader = new ServiceLoader(loader);
      this.mimeTypes = getDefaultMimeTypes(loader);
      this.detector = getDefaultDetector(this.mimeTypes, this.serviceLoader);
      this.encodingDetector = getDefaultEncodingDetector(this.serviceLoader);
      this.parser = getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
      this.translator = getDefaultTranslator(this.serviceLoader);
      this.executorService = getDefaultExecutorService();
      TIMES_INSTANTIATED.incrementAndGet();
   }

   public TikaConfig() throws TikaException, IOException {
      String config = System.getProperty("tika.config");
      if (config == null) {
         config = System.getenv("TIKA_CONFIG");
      }

      if (config == null) {
         this.serviceLoader = new ServiceLoader();
         this.mimeTypes = getDefaultMimeTypes(ServiceLoader.getContextClassLoader());
         this.encodingDetector = getDefaultEncodingDetector(this.serviceLoader);
         this.parser = getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
         this.detector = getDefaultDetector(this.mimeTypes, this.serviceLoader);
         this.translator = getDefaultTranslator(this.serviceLoader);
         this.executorService = getDefaultExecutorService();
      } else {
         ServiceLoader tmpServiceLoader = new ServiceLoader();

         try {
            InputStream stream = getConfigInputStream(config, tmpServiceLoader);
            Throwable var4 = null;

            try {
               Element element = XMLReaderUtils.getDocumentBuilder().parse(stream).getDocumentElement();
               this.serviceLoader = serviceLoaderFromDomElement(element, tmpServiceLoader.getLoader());
               DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
               EncodingDetectorXmlLoader encodingDetectorLoader = new EncodingDetectorXmlLoader();
               TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
               ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
               this.mimeTypes = typesFromDomElement(element);
               this.encodingDetector = (EncodingDetector)encodingDetectorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
               ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
               this.parser = (CompositeParser)parserLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
               this.detector = (CompositeDetector)detectorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
               this.translator = (Translator)translatorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
               this.executorService = (ExecutorService)executorLoader.loadOverall(element, this.mimeTypes, this.serviceLoader);
            } catch (Throwable var19) {
               var4 = var19;
               throw var19;
            } finally {
               if (stream != null) {
                  if (var4 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var18) {
                        var4.addSuppressed(var18);
                     }
                  } else {
                     stream.close();
                  }
               }

            }
         } catch (SAXException e) {
            throw new TikaException("Specified Tika configuration has syntax errors: " + config, e);
         }
      }

      TIMES_INSTANTIATED.incrementAndGet();
   }

   private static InputStream getConfigInputStream(String config, ServiceLoader serviceLoader) throws TikaException, IOException {
      InputStream stream = null;

      try {
         stream = (new URL(config)).openStream();
      } catch (IOException var4) {
      }

      if (stream == null) {
         stream = serviceLoader.getResourceAsStream(config);
      }

      if (stream == null) {
         Path file = Paths.get(config);
         if (Files.isRegularFile(file, new LinkOption[0])) {
            stream = Files.newInputStream(file);
         }
      }

      if (stream == null) {
         throw new TikaException("Specified Tika configuration not found: " + config);
      } else {
         return stream;
      }
   }

   private static String getText(Node node) {
      if (node.getNodeType() == 3) {
         return node.getNodeValue();
      } else if (node.getNodeType() != 1) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder();
         NodeList list = node.getChildNodes();

         for(int i = 0; i < list.getLength(); ++i) {
            builder.append(getText(list.item(i)));
         }

         return builder.toString();
      }
   }

   /** @deprecated */
   public Parser getParser(MediaType mimeType) {
      return (Parser)this.parser.getParsers().get(mimeType);
   }

   public Parser getParser() {
      return this.parser;
   }

   public Detector getDetector() {
      return this.detector;
   }

   public EncodingDetector getEncodingDetector() {
      return this.encodingDetector;
   }

   public Translator getTranslator() {
      return this.translator;
   }

   public ExecutorService getExecutorService() {
      return this.executorService;
   }

   public MimeTypes getMimeRepository() {
      return this.mimeTypes;
   }

   public MediaTypeRegistry getMediaTypeRegistry() {
      return this.mimeTypes.getMediaTypeRegistry();
   }

   public ServiceLoader getServiceLoader() {
      return this.serviceLoader;
   }

   public static TikaConfig getDefaultConfig() {
      try {
         return new TikaConfig();
      } catch (IOException e) {
         throw new RuntimeException("Unable to read default configuration", e);
      } catch (TikaException e) {
         throw new RuntimeException("Unable to access default configuration", e);
      }
   }

   private static Element getChild(Element element, String name) {
      for(Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1 && name.equals(child.getNodeName())) {
            return (Element)child;
         }
      }

      return null;
   }

   private static List<Element> getTopLevelElementChildren(Element element, String parentName, String childrenName) throws TikaException {
      Node parentNode = null;
      if (parentName != null) {
         NodeList nodes = element.getElementsByTagName(parentName);
         if (nodes.getLength() > 1) {
            throw new TikaException("Properties may not contain multiple " + parentName + " entries");
         }

         if (nodes.getLength() == 1) {
            parentNode = nodes.item(0);
         }
      } else {
         parentNode = element;
      }

      if (parentNode != null) {
         NodeList nodes = parentNode.getChildNodes();
         List<Element> elements = new ArrayList();

         for(int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
               Element nodeE = (Element)node;
               if (childrenName.equals(nodeE.getTagName())) {
                  elements.add(nodeE);
               }
            }
         }

         return elements;
      } else {
         return Collections.emptyList();
      }
   }

   private static MimeTypes typesFromDomElement(Element element) throws TikaException, IOException {
      Element mtr = getChild(element, "mimeTypeRepository");
      return mtr != null && mtr.hasAttribute("resource") ? MimeTypesFactory.create(mtr.getAttribute("resource")) : getDefaultMimeTypes((ClassLoader)null);
   }

   private static Set<MediaType> mediaTypesListFromDomElement(Element node, String tag) throws TikaException, IOException {
      Set<MediaType> types = null;
      NodeList children = node.getChildNodes();

      for(int i = 0; i < children.getLength(); ++i) {
         Node cNode = children.item(i);
         if (cNode instanceof Element) {
            Element cElement = (Element)cNode;
            if (tag.equals(cElement.getTagName())) {
               String mime = getText(cElement);
               MediaType type = MediaType.parse(mime);
               if (type == null) {
                  throw new TikaException("Invalid media type name: " + mime);
               }

               if (types == null) {
                  types = new HashSet();
               }

               types.add(type);
            }
         }
      }

      if (types != null) {
         return types;
      } else {
         return Collections.emptySet();
      }
   }

   private static ServiceLoader serviceLoaderFromDomElement(Element element, ClassLoader loader) throws TikaConfigException {
      Element serviceLoaderElement = getChild(element, "service-loader");
      ServiceLoader serviceLoader;
      if (serviceLoaderElement != null) {
         boolean dynamic = Boolean.parseBoolean(serviceLoaderElement.getAttribute("dynamic"));
         LoadErrorHandler loadErrorHandler = LoadErrorHandler.IGNORE;
         String loadErrorHandleConfig = serviceLoaderElement.getAttribute("loadErrorHandler");
         if (LoadErrorHandler.WARN.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
            loadErrorHandler = LoadErrorHandler.WARN;
         } else if (LoadErrorHandler.THROW.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
            loadErrorHandler = LoadErrorHandler.THROW;
         }

         InitializableProblemHandler initializableProblemHandler = getInitializableProblemHandler(serviceLoaderElement.getAttribute("initializableProblemHandler"));
         if (loader == null) {
            loader = ServiceLoader.getContextClassLoader();
         }

         serviceLoader = new ServiceLoader(loader, loadErrorHandler, initializableProblemHandler, dynamic);
      } else if (loader != null) {
         serviceLoader = new ServiceLoader(loader);
      } else {
         serviceLoader = new ServiceLoader();
      }

      return serviceLoader;
   }

   private static InitializableProblemHandler getInitializableProblemHandler(String initializableProblemHandler) throws TikaConfigException {
      if (initializableProblemHandler != null && initializableProblemHandler.length() != 0) {
         if (InitializableProblemHandler.IGNORE.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.IGNORE;
         } else if (InitializableProblemHandler.INFO.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.INFO;
         } else if (InitializableProblemHandler.WARN.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.WARN;
         } else if (InitializableProblemHandler.THROW.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.THROW;
         } else {
            throw new TikaConfigException(String.format(Locale.US, "Couldn't parse non-null '%s'. Must be one of 'ignore', 'info', 'warn' or 'throw'", initializableProblemHandler));
         }
      } else {
         return InitializableProblemHandler.DEFAULT;
      }
   }

   private abstract static class XmlLoader<CT, T> {
      protected static final String PARAMS_TAG_NAME = "params";

      private XmlLoader() {
      }

      abstract boolean supportsComposite();

      abstract String getParentTagName();

      abstract String getLoaderTagName();

      abstract Class<? extends T> getLoaderClass();

      abstract boolean isComposite(T var1);

      abstract boolean isComposite(Class<? extends T> var1);

      abstract T preLoadOne(Class<? extends T> var1, String var2, MimeTypes var3) throws TikaException;

      abstract CT createDefault(MimeTypes var1, ServiceLoader var2);

      abstract CT createComposite(List<T> var1, MimeTypes var2, ServiceLoader var3);

      abstract T createComposite(Class<? extends T> var1, List<T> var2, Set<Class<? extends T>> var3, MimeTypes var4, ServiceLoader var5) throws InvocationTargetException, IllegalAccessException, InstantiationException;

      abstract T decorate(T var1, Element var2) throws IOException, TikaException;

      CT loadOverall(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
         List<T> loaded = new ArrayList();

         for(Element le : TikaConfig.getTopLevelElementChildren(element, this.getParentTagName(), this.getLoaderTagName())) {
            T loadedChild = (T)this.loadOne(le, mimeTypes, loader);
            if (loadedChild != null) {
               loaded.add(loadedChild);
            }
         }

         if (loaded.isEmpty()) {
            return (CT)this.createDefault(mimeTypes, loader);
         } else {
            if (loaded.size() == 1) {
               T single = (T)loaded.get(0);
               if (this.isComposite(single)) {
                  return (CT)single;
               }
            } else if (!this.supportsComposite()) {
               return (CT)loaded.get(0);
            }

            return (CT)this.createComposite(loaded, mimeTypes, loader);
         }
      }

      T loadOne(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
         String name = element.getAttribute("class");
         String initProbHandler = element.getAttribute("initializableProblemHandler");
         InitializableProblemHandler initializableProblemHandler;
         if (initProbHandler != null && initProbHandler.length() != 0) {
            initializableProblemHandler = TikaConfig.getInitializableProblemHandler(initProbHandler);
         } else {
            initializableProblemHandler = loader.getInitializableProblemHandler();
         }

         T loaded = null;

         try {
            Class<? extends T> loadedClass = loader.<T>getServiceClass(this.getLoaderClass(), name);
            loaded = (T)this.preLoadOne(loadedClass, name, mimeTypes);
            if (loaded != null) {
               return loaded;
            } else {
               if (this.isComposite(loadedClass)) {
                  List<T> children = new ArrayList();
                  NodeList childNodes = element.getElementsByTagName(this.getLoaderTagName());
                  if (childNodes.getLength() > 0) {
                     for(int i = 0; i < childNodes.getLength(); ++i) {
                        T loadedChild = (T)this.loadOne((Element)childNodes.item(i), mimeTypes, loader);
                        if (loadedChild != null) {
                           children.add(loadedChild);
                        }
                     }
                  }

                  Set<Class<? extends T>> excludeChildren = new HashSet();
                  NodeList excludeChildNodes = element.getElementsByTagName(this.getLoaderTagName() + "-exclude");
                  if (excludeChildNodes.getLength() > 0) {
                     for(int i = 0; i < excludeChildNodes.getLength(); ++i) {
                        Element excl = (Element)excludeChildNodes.item(i);
                        String exclName = excl.getAttribute("class");
                        excludeChildren.add(loader.getServiceClass(this.getLoaderClass(), exclName));
                     }
                  }

                  loaded = (T)this.createComposite(loadedClass, children, excludeChildren, mimeTypes, loader);
                  if (loaded == null) {
                     loaded = (T)this.newInstance(loadedClass);
                  }
               } else {
                  loaded = (T)this.newInstance(loadedClass);
               }

               Map<String, Param> params = this.getParams(element);
               AnnotationUtils.assignFieldParams(loaded, params);
               if (loaded instanceof Initializable) {
                  ((Initializable)loaded).initialize(params);
                  ((Initializable)loaded).checkInitialization(initializableProblemHandler);
               }

               loaded = (T)this.decorate(loaded, element);
               return loaded;
            }
         } catch (ClassNotFoundException e) {
            if (loader.getLoadErrorHandler() == LoadErrorHandler.THROW) {
               throw new TikaException("Unable to find a " + this.getLoaderTagName() + " class: " + name, e);
            } else {
               loader.getLoadErrorHandler().handleLoadError(name, e);
               return null;
            }
         } catch (IllegalAccessException e) {
            throw new TikaException("Unable to access a " + this.getLoaderTagName() + " class: " + name, e);
         } catch (InvocationTargetException e) {
            throw new TikaException("Unable to create a " + this.getLoaderTagName() + " class: " + name, e);
         } catch (InstantiationException e) {
            throw new TikaException("Unable to instantiate a " + this.getLoaderTagName() + " class: " + name, e);
         } catch (NoSuchMethodException e) {
            throw new TikaException("Unable to find the right constructor for " + this.getLoaderTagName() + " class: " + name, e);
         }
      }

      T newInstance(Class<? extends T> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
         return (T)loadedClass.newInstance();
      }

      Map<String, Param> getParams(Element el) {
         Map<String, Param> params = new HashMap();

         for(Node child = el.getFirstChild(); child != null; child = child.getNextSibling()) {
            if ("params".equals(child.getNodeName())) {
               if (child.hasChildNodes()) {
                  NodeList childNodes = child.getChildNodes();

                  for(int i = 0; i < childNodes.getLength(); ++i) {
                     Node item = childNodes.item(i);
                     if (item.getNodeType() == 1) {
                        Param<?> param = Param.load(item);
                        params.put(param.getName(), param);
                     }
                  }
               }
               break;
            }
         }

         return params;
      }
   }

   private static class ParserXmlLoader extends XmlLoader<CompositeParser, Parser> {
      private final EncodingDetector encodingDetector;

      boolean supportsComposite() {
         return true;
      }

      String getParentTagName() {
         return "parsers";
      }

      String getLoaderTagName() {
         return "parser";
      }

      private ParserXmlLoader(EncodingDetector encodingDetector) {
         this.encodingDetector = encodingDetector;
      }

      Class<? extends Parser> getLoaderClass() {
         return Parser.class;
      }

      Parser preLoadOne(Class<? extends Parser> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
         if (AutoDetectParser.class.isAssignableFrom(loadedClass)) {
            throw new TikaException("AutoDetectParser not supported in a <parser> configuration element: " + classname);
         } else {
            return null;
         }
      }

      boolean isComposite(Parser loaded) {
         return loaded instanceof CompositeParser;
      }

      boolean isComposite(Class<? extends Parser> loadedClass) {
         return CompositeParser.class.isAssignableFrom(loadedClass) || ParserDecorator.class.isAssignableFrom(loadedClass);
      }

      CompositeParser createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
         return TikaConfig.getDefaultParser(mimeTypes, loader, this.encodingDetector);
      }

      CompositeParser createComposite(List<Parser> parsers, MimeTypes mimeTypes, ServiceLoader loader) {
         MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
         return new CompositeParser(registry, parsers);
      }

      Parser createComposite(Class<? extends Parser> parserClass, List<Parser> childParsers, Set<Class<? extends Parser>> excludeParsers, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         Parser parser = null;
         Constructor<? extends Parser> c = null;
         MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
         if (parser == null) {
            try {
               c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class, EncodingDetector.class);
               parser = (Parser)c.newInstance(registry, loader, excludeParsers, this.encodingDetector);
            } catch (NoSuchMethodException var13) {
            }
         }

         if (parser == null) {
            try {
               c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class);
               parser = (Parser)c.newInstance(registry, loader, excludeParsers);
            } catch (NoSuchMethodException var12) {
            }
         }

         if (parser == null) {
            try {
               c = parserClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
               parser = (Parser)c.newInstance(registry, childParsers, excludeParsers);
            } catch (NoSuchMethodException var11) {
            }
         }

         if (parser == null) {
            try {
               c = parserClass.getConstructor(MediaTypeRegistry.class, List.class);
               parser = (Parser)c.newInstance(registry, childParsers);
            } catch (NoSuchMethodException var10) {
            }
         }

         if (parser == null && ParserDecorator.class.isAssignableFrom(parserClass)) {
            try {
               CompositeParser cp = null;
               if (childParsers.size() == 1 && excludeParsers.size() == 0 && childParsers.get(0) instanceof CompositeParser) {
                  cp = (CompositeParser)childParsers.get(0);
               } else {
                  cp = new CompositeParser(registry, childParsers, excludeParsers);
               }

               c = parserClass.getConstructor(Parser.class);
               parser = (Parser)c.newInstance(cp);
            } catch (NoSuchMethodException var14) {
            }
         }

         return parser;
      }

      Parser newInstance(Class<? extends Parser> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
         if (AbstractEncodingDetectorParser.class.isAssignableFrom(loadedClass)) {
            Constructor ctor = loadedClass.getConstructor(EncodingDetector.class);
            return (Parser)ctor.newInstance(this.encodingDetector);
         } else {
            return (Parser)loadedClass.newInstance();
         }
      }

      Parser decorate(Parser created, Element element) throws IOException, TikaException {
         Parser parser = created;
         Set<MediaType> parserTypes = TikaConfig.mediaTypesListFromDomElement(element, "mime");
         if (!parserTypes.isEmpty()) {
            parser = ParserDecorator.withTypes(created, parserTypes);
         }

         Set<MediaType> parserExclTypes = TikaConfig.mediaTypesListFromDomElement(element, "mime-exclude");
         if (!parserExclTypes.isEmpty()) {
            parser = ParserDecorator.withoutTypes(parser, parserExclTypes);
         }

         return parser;
      }
   }

   private static class DetectorXmlLoader extends XmlLoader<CompositeDetector, Detector> {
      private DetectorXmlLoader() {
      }

      boolean supportsComposite() {
         return true;
      }

      String getParentTagName() {
         return "detectors";
      }

      String getLoaderTagName() {
         return "detector";
      }

      Class<? extends Detector> getLoaderClass() {
         return Detector.class;
      }

      Detector preLoadOne(Class<? extends Detector> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
         return MimeTypes.class.equals(loadedClass) ? mimeTypes : null;
      }

      boolean isComposite(Detector loaded) {
         return loaded instanceof CompositeDetector;
      }

      boolean isComposite(Class<? extends Detector> loadedClass) {
         return CompositeDetector.class.isAssignableFrom(loadedClass);
      }

      CompositeDetector createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
         return TikaConfig.getDefaultDetector(mimeTypes, loader);
      }

      CompositeDetector createComposite(List<Detector> detectors, MimeTypes mimeTypes, ServiceLoader loader) {
         MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
         return new CompositeDetector(registry, detectors);
      }

      Detector createComposite(Class<? extends Detector> detectorClass, List<Detector> childDetectors, Set<Class<? extends Detector>> excludeDetectors, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         Detector detector = null;
         MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
         if (detector == null) {
            try {
               Constructor<? extends Detector> c = detectorClass.getConstructor(MimeTypes.class, ServiceLoader.class, Collection.class);
               detector = (Detector)c.newInstance(mimeTypes, loader, excludeDetectors);
            } catch (NoSuchMethodException var13) {
            }
         }

         if (detector == null) {
            try {
               Constructor<? extends Detector> c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
               detector = (Detector)c.newInstance(registry, childDetectors, excludeDetectors);
            } catch (NoSuchMethodException var12) {
            }
         }

         if (detector == null) {
            try {
               Constructor<? extends Detector> c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class);
               detector = (Detector)c.newInstance(registry, childDetectors);
            } catch (NoSuchMethodException var11) {
            }
         }

         if (detector == null) {
            try {
               Constructor<? extends Detector> c = detectorClass.getConstructor(List.class);
               detector = (Detector)c.newInstance(childDetectors);
            } catch (NoSuchMethodException var10) {
            }
         }

         return detector;
      }

      Detector decorate(Detector created, Element element) {
         return created;
      }
   }

   private static class TranslatorXmlLoader extends XmlLoader<Translator, Translator> {
      private TranslatorXmlLoader() {
      }

      boolean supportsComposite() {
         return false;
      }

      String getParentTagName() {
         return null;
      }

      String getLoaderTagName() {
         return "translator";
      }

      Class<? extends Translator> getLoaderClass() {
         return Translator.class;
      }

      Translator preLoadOne(Class<? extends Translator> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
         return null;
      }

      boolean isComposite(Translator loaded) {
         return false;
      }

      boolean isComposite(Class<? extends Translator> loadedClass) {
         return false;
      }

      Translator createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
         return TikaConfig.getDefaultTranslator(loader);
      }

      Translator createComposite(List<Translator> loaded, MimeTypes mimeTypes, ServiceLoader loader) {
         return (Translator)loaded.get(0);
      }

      Translator createComposite(Class<? extends Translator> compositeClass, List<Translator> children, Set<Class<? extends Translator>> excludeChildren, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         throw new InstantiationException("Only one translator supported");
      }

      Translator decorate(Translator created, Element element) {
         return created;
      }
   }

   private static class ExecutorServiceXmlLoader extends XmlLoader<ConfigurableThreadPoolExecutor, ConfigurableThreadPoolExecutor> {
      private ExecutorServiceXmlLoader() {
      }

      ConfigurableThreadPoolExecutor createComposite(Class<? extends ConfigurableThreadPoolExecutor> compositeClass, List<ConfigurableThreadPoolExecutor> children, Set<Class<? extends ConfigurableThreadPoolExecutor>> excludeChildren, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         throw new InstantiationException("Only one executor service supported");
      }

      ConfigurableThreadPoolExecutor createComposite(List<ConfigurableThreadPoolExecutor> loaded, MimeTypes mimeTypes, ServiceLoader loader) {
         return (ConfigurableThreadPoolExecutor)loaded.get(0);
      }

      ConfigurableThreadPoolExecutor createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
         return TikaConfig.getDefaultExecutorService();
      }

      ConfigurableThreadPoolExecutor decorate(ConfigurableThreadPoolExecutor created, Element element) throws IOException, TikaException {
         Element maxThreadElement = TikaConfig.getChild(element, "max-threads");
         if (maxThreadElement != null) {
            created.setMaximumPoolSize(Integer.parseInt(TikaConfig.getText(maxThreadElement)));
         }

         Element coreThreadElement = TikaConfig.getChild(element, "core-threads");
         if (coreThreadElement != null) {
            created.setCorePoolSize(Integer.parseInt(TikaConfig.getText(coreThreadElement)));
         }

         return created;
      }

      Class<? extends ConfigurableThreadPoolExecutor> getLoaderClass() {
         return ConfigurableThreadPoolExecutor.class;
      }

      ConfigurableThreadPoolExecutor loadOne(Element element, MimeTypes mimeTypes, ServiceLoader loader) throws TikaException, IOException {
         return (ConfigurableThreadPoolExecutor)super.loadOne(element, mimeTypes, loader);
      }

      boolean supportsComposite() {
         return false;
      }

      String getParentTagName() {
         return null;
      }

      String getLoaderTagName() {
         return "executor-service";
      }

      boolean isComposite(ConfigurableThreadPoolExecutor loaded) {
         return false;
      }

      boolean isComposite(Class<? extends ConfigurableThreadPoolExecutor> loadedClass) {
         return false;
      }

      ConfigurableThreadPoolExecutor preLoadOne(Class<? extends ConfigurableThreadPoolExecutor> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
         return null;
      }
   }

   private static class EncodingDetectorXmlLoader extends XmlLoader<EncodingDetector, EncodingDetector> {
      private EncodingDetectorXmlLoader() {
      }

      boolean supportsComposite() {
         return true;
      }

      String getParentTagName() {
         return "encodingDetectors";
      }

      String getLoaderTagName() {
         return "encodingDetector";
      }

      Class<? extends EncodingDetector> getLoaderClass() {
         return EncodingDetector.class;
      }

      boolean isComposite(EncodingDetector loaded) {
         return loaded instanceof CompositeEncodingDetector;
      }

      boolean isComposite(Class<? extends EncodingDetector> loadedClass) {
         return CompositeEncodingDetector.class.isAssignableFrom(loadedClass);
      }

      EncodingDetector preLoadOne(Class<? extends EncodingDetector> loadedClass, String classname, MimeTypes mimeTypes) throws TikaException {
         return null;
      }

      EncodingDetector createDefault(MimeTypes mimeTypes, ServiceLoader loader) {
         return TikaConfig.getDefaultEncodingDetector(loader);
      }

      CompositeEncodingDetector createComposite(List<EncodingDetector> encodingDetectors, MimeTypes mimeTypes, ServiceLoader loader) {
         return new CompositeEncodingDetector(encodingDetectors);
      }

      EncodingDetector createComposite(Class<? extends EncodingDetector> encodingDetectorClass, List<EncodingDetector> childEncodingDetectors, Set<Class<? extends EncodingDetector>> excludeDetectors, MimeTypes mimeTypes, ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
         EncodingDetector encodingDetector = null;
         if (encodingDetector == null) {
            try {
               Constructor<? extends EncodingDetector> c = encodingDetectorClass.getConstructor(ServiceLoader.class, Collection.class);
               encodingDetector = (EncodingDetector)c.newInstance(loader, excludeDetectors);
            } catch (NoSuchMethodException me) {
               me.printStackTrace();
            }
         }

         if (encodingDetector == null) {
            try {
               Constructor<? extends EncodingDetector> c = encodingDetectorClass.getConstructor(List.class);
               encodingDetector = (EncodingDetector)c.newInstance(childEncodingDetectors);
            } catch (NoSuchMethodException me) {
               me.printStackTrace();
            }
         }

         return encodingDetector;
      }

      EncodingDetector decorate(EncodingDetector created, Element element) {
         return created;
      }
   }
}
