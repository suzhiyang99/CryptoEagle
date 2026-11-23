package org.apache.wicket.util.license;

import java.util.Arrays;
import java.util.List;

class HtmlLicenseHeaderHandler extends XmlLicenseHeaderHandler {
   public HtmlLicenseHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
   }

   public List<String> getSuffixes() {
      return Arrays.asList("html");
   }
}
