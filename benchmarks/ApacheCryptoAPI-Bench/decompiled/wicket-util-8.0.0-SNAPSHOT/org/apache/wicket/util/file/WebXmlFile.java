package org.apache.wicket.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wicket.util.xml.CustomEntityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WebXmlFile {
   private static final Logger log = LoggerFactory.getLogger(WebXmlFile.class);

   public final String getUniqueFilterPath(boolean isServlet, FilterConfig filterConfig) {
      String filterName = filterConfig.getFilterName();
      Set<String> paths = this.getFilterPath(isServlet, filterConfig.getServletContext(), filterName);
      return this.uniquePath(paths, isServlet, filterName);
   }

   public final Set<String> getFilterPath(boolean isServlet, ServletContext servletContext, String filterName) {
      InputStream is = servletContext.getResourceAsStream("/WEB-INF/web.xml");
      if (is != null) {
         try {
            return this.getFilterPath(isServlet, filterName, is);
         } catch (SAXException | IOException | ParserConfigurationException ex) {
            log.error("Error reading servlet/filter path from web.xml", ex);
         } catch (SecurityException e) {
            log.info("Couldn't read web.xml to automatically pick up servlet/filter path: " + e.getMessage());
         }
      }

      return Collections.emptySet();
   }

   public final String getUniqueFilterPath(boolean isServlet, String filterName, InputStream is) throws ParserConfigurationException, SAXException, IOException {
      return this.uniquePath(this.getFilterPath(isServlet, filterName, is), isServlet, filterName);
   }

   private String uniquePath(Set<String> paths, boolean isServlet, String filterName) {
      if (paths.size() <= 1) {
         return paths.size() == 1 ? (String)paths.iterator().next() : null;
      } else {
         StringBuilder err = new StringBuilder();
         err.append("web.xml: expected one ");
         err.append(isServlet ? "servlet" : "filter");
         err.append(" path for [");
         err.append(filterName);
         err.append("] but found multiple:");

         for(String path : paths) {
            err.append(" [").append(path).append(']');
         }

         throw new RuntimeException(err.toString());
      }
   }

   public final Set<String> getFilterPath(boolean isServlet, String filterName, InputStream is) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setEntityResolver(CustomEntityResolver.getPreloaded());
      Document document = builder.parse(is);
      String tag = isServlet ? "servlet" : "filter";
      String mapping = tag + "-mapping";
      String name = tag + "-name";
      Set<String> urlPatterns = this.getFilterPaths(filterName, mapping, name, document.getChildNodes());
      if (urlPatterns.size() == 0) {
         log.warn("web.xml: No url-pattern found for '{}' with name '{}'", tag, filterName);
      }

      if (log.isInfoEnabled()) {
         StringBuilder msg = new StringBuilder();
         msg.append("web.xml: url mapping found for ").append(tag).append(" with name ").append(filterName).append(':');

         for(String urlPattern : urlPatterns) {
            msg.append(" [");
            msg.append(urlPattern);
            msg.append(']');
         }

         log.info(msg.toString());
      }

      Set<String> stripped = new HashSet(urlPatterns.size());

      for(String urlPattern : urlPatterns) {
         stripped.add(urlPattern.substring(1, urlPattern.length() - 1));
      }

      return stripped;
   }

   private Set<String> getFilterPaths(String filterName, String name, Node node) {
      Set<String> paths = new HashSet();
      String foundUrlPattern = null;
      String foundFilterName = null;

      for(int i = 0; i < node.getChildNodes().getLength(); ++i) {
         Node n = node.getChildNodes().item(i);
         if (name.equals(n.getNodeName())) {
            foundFilterName = n.getTextContent();
         } else if ("url-pattern".equals(n.getNodeName())) {
            foundUrlPattern = n.getTextContent();
         }

         if (foundFilterName != null) {
            foundFilterName = foundFilterName.trim();
         }

         if (filterName.equals(foundFilterName) && foundUrlPattern != null) {
            paths.add(foundUrlPattern.trim());
         }
      }

      return paths;
   }

   private Set<String> getFilterPaths(String filterName, String mapping, String name, NodeList nodeList) {
      Set<String> paths = new HashSet(1);

      for(int i = 0; i < nodeList.getLength(); ++i) {
         Node node = nodeList.item(i);
         if (mapping.equals(node.getNodeName())) {
            paths.addAll(this.getFilterPaths(filterName, name, node));
         } else {
            paths.addAll(this.getFilterPaths(filterName, mapping, name, node.getChildNodes()));
         }
      }

      return paths;
   }
}
