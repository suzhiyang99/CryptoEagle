package org.apache.wicket.util.license;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;

class XmlPrologHeaderHandler extends XmlLicenseHeaderHandler {
   public XmlPrologHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
   }

   public List<String> getSuffixes() {
      return Arrays.asList("html");
   }

   public boolean checkLicenseHeader(File file) {
      try {
         String header = this.extractLicenseHeader(file, 0, 1);
         return header.startsWith("<?xml");
      } catch (Exception e) {
         Assert.fail(e.getMessage());
         return true;
      }
   }
}
