package org.apache.openejb.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.openejb.util.JavaSecurityManagers;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class QuickServerXmlParser extends DefaultHandler {
   private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();
   private static final String STOP_KEY = "STOP";
   private static final String HTTP_KEY = "HTTP";
   private static final String SECURED_SUFFIX = "S";
   private static final String AJP_KEY = "AJP";
   private static final String HOST_KEY = "host";
   private static final String APP_BASE_KEY = "app-base";
   private static final String DEFAULT_CONNECTOR_KEY = "HTTP";
   private static final String KEYSTORE_KEY = "keystoreFile";
   public static final String DEFAULT_HTTP_PORT = "8080";
   public static final String DEFAULT_HTTPS_PORT = "8443";
   public static final String DEFAULT_STOP_PORT = "8005";
   public static final String DEFAULT_AJP_PORT = "8009";
   public static final String DEFAULT_HOST = "localhost";
   public static final String DEFAULT_APP_BASE = "webapps";
   public static final String DEFAULT_KEYSTORE;
   private final Map<String, String> values;

   public QuickServerXmlParser() {
      this.values = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      this.values.put("STOP", "8005");
      this.values.put("HTTP", "8080");
      this.values.put("AJP", "8009");
      this.values.put("host", "localhost");
      this.values.put("app-base", "webapps");
      this.values.put("keystoreFile", DEFAULT_KEYSTORE);
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

         String keystore = attributes.getValue("keystoreFile");
         if (null != keystore) {
            this.values.put("keystoreFile", keystore);
         }
      } else if ("Host".equalsIgnoreCase(localName)) {
         String host = attributes.getValue("name");
         if (host != null) {
            this.values.put("host", host);
         }

         String appBase = attributes.getValue("appBase");
         if (appBase != null) {
            this.values.put("app-base", appBase);
         }
      }

   }

   public static QuickServerXmlParser parse(File serverXml) {
      QuickServerXmlParser handler = new QuickServerXmlParser();

      try {
         SAXParser parser = FACTORY.newSAXParser();
         parser.parse(serverXml, handler);
      } catch (Exception var3) {
      }

      return handler;
   }

   public static QuickServerXmlParser parse(String serverXmlContents) {
      QuickServerXmlParser handler = new QuickServerXmlParser();

      try {
         SAXParser parser = FACTORY.newSAXParser();
         parser.parse(new ByteArrayInputStream(serverXmlContents.getBytes()), handler);
      } catch (Exception var3) {
      }

      return handler;
   }

   public String http() {
      return this.value("HTTP", "8080");
   }

   public String https() {
      return this.securedValue("HTTP", "8443");
   }

   public String ajp() {
      return this.value("AJP", "8009");
   }

   public String stop() {
      return this.value("STOP", "8005");
   }

   public String appBase() {
      return this.value("app-base", "webapps");
   }

   public String host() {
      return this.value("host", "localhost");
   }

   public String keystore() {
      return this.value("keystoreFile", DEFAULT_KEYSTORE);
   }

   public String value(String key, String defaultValue) {
      String val = (String)this.values.get(key);
      return val == null ? defaultValue : val;
   }

   public String securedValue(String key, String defaultValue) {
      return this.value(key + "S", defaultValue);
   }

   public String toString() {
      return "QuickServerXmlParser: " + this.values;
   }

   static {
      FACTORY.setNamespaceAware(true);
      FACTORY.setValidating(false);
      DEFAULT_KEYSTORE = (new File(JavaSecurityManagers.getSystemProperty("user.home"), ".keystore")).getAbsolutePath();
   }
}
