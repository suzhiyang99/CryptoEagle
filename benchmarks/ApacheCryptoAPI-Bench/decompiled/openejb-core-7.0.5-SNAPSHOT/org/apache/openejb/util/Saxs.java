package org.apache.openejb.util;

import javax.xml.parsers.SAXParserFactory;

public final class Saxs {
   private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();
   private static final SAXParserFactory NAMESPACE_AWARE_FACTORY = SAXParserFactory.newInstance();

   private Saxs() {
   }

   public static SAXParserFactory factory() {
      return FACTORY;
   }

   public static SAXParserFactory namespaceAwareFactory() {
      return NAMESPACE_AWARE_FACTORY;
   }

   static {
      FACTORY.setNamespaceAware(true);
      FACTORY.setValidating(false);
      NAMESPACE_AWARE_FACTORY.setNamespaceAware(true);
      NAMESPACE_AWARE_FACTORY.setValidating(false);
   }
}
