package org.apache.tika.sax.xpath;

import java.util.HashMap;
import java.util.Map;

public class XPathParser {
   private final Map<String, String> prefixes = new HashMap();

   public XPathParser() {
   }

   public XPathParser(String prefix, String namespace) {
      this.addPrefix(prefix, namespace);
   }

   public void addPrefix(String prefix, String namespace) {
      this.prefixes.put(prefix, namespace);
   }

   public Matcher parse(String xpath) {
      if (xpath.equals("/text()")) {
         return TextMatcher.INSTANCE;
      } else if (xpath.equals("/node()")) {
         return NodeMatcher.INSTANCE;
      } else if (!xpath.equals("/descendant::node()") && !xpath.equals("/descendant:node()")) {
         if (xpath.equals("/@*")) {
            return AttributeMatcher.INSTANCE;
         } else if (xpath.length() == 0) {
            return ElementMatcher.INSTANCE;
         } else if (xpath.startsWith("/@")) {
            String name = xpath.substring(2);
            String prefix = null;
            int colon = name.indexOf(58);
            if (colon != -1) {
               prefix = name.substring(0, colon);
               name = name.substring(colon + 1);
            }

            return (Matcher)(this.prefixes.containsKey(prefix) ? new NamedAttributeMatcher((String)this.prefixes.get(prefix), name) : Matcher.FAIL);
         } else if (xpath.startsWith("/*")) {
            return new ChildMatcher(this.parse(xpath.substring(2)));
         } else if (xpath.startsWith("///")) {
            return Matcher.FAIL;
         } else if (xpath.startsWith("//")) {
            return new SubtreeMatcher(this.parse(xpath.substring(1)));
         } else if (xpath.startsWith("/")) {
            int slash = xpath.indexOf(47, 1);
            if (slash == -1) {
               slash = xpath.length();
            }

            String name = xpath.substring(1, slash);
            String prefix = null;
            int colon = name.indexOf(58);
            if (colon != -1) {
               prefix = name.substring(0, colon);
               name = name.substring(colon + 1);
            }

            return (Matcher)(this.prefixes.containsKey(prefix) ? new NamedElementMatcher((String)this.prefixes.get(prefix), name, this.parse(xpath.substring(slash))) : Matcher.FAIL);
         } else {
            return Matcher.FAIL;
         }
      } else {
         return new CompositeMatcher(TextMatcher.INSTANCE, new ChildMatcher(new SubtreeMatcher(NodeMatcher.INSTANCE)));
      }
   }
}
