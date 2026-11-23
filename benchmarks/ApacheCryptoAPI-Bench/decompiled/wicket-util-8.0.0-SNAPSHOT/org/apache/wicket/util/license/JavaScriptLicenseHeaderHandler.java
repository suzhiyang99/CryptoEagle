package org.apache.wicket.util.license;

import java.io.File;
import java.util.Arrays;
import java.util.List;

class JavaScriptLicenseHeaderHandler extends AbstractLicenseHeaderHandler {
   public JavaScriptLicenseHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
   }

   protected String getLicenseHeaderFilename() {
      return "javaScriptLicense.txt";
   }

   public boolean checkLicenseHeader(File file) {
      String header = this.extractLicenseHeader(file, 0, 16);
      return this.getLicenseHeader().equals(header);
   }

   public List<String> getSuffixes() {
      return Arrays.asList("js", "json");
   }

   public boolean addLicenseHeader(File file) {
      this.prependLicenseHeader(file);
      return true;
   }
}
