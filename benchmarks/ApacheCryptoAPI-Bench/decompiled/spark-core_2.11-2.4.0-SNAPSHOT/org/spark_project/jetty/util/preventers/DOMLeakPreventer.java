package org.spark_project.jetty.util.preventers;

import javax.xml.parsers.DocumentBuilderFactory;

public class DOMLeakPreventer extends AbstractLeakPreventer {
   public void prevent(ClassLoader loader) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      try {
         factory.newDocumentBuilder();
      } catch (Exception e) {
         LOG.warn(e);
      }

   }
}
