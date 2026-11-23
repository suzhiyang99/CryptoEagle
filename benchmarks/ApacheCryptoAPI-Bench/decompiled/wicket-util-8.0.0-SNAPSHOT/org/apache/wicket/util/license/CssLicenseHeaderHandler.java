package org.apache.wicket.util.license;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.util.diff.Diff;
import org.apache.wicket.util.diff.Revision;
import org.junit.Assert;

class CssLicenseHeaderHandler extends AbstractLicenseHeaderHandler {
   public CssLicenseHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
   }

   protected String getLicenseHeaderFilename() {
      return "cssLicense.txt";
   }

   public boolean checkLicenseHeader(File file) {
      Revision revision = null;

      try {
         String header = this.extractLicenseHeader(file, 0, 16);
         revision = Diff.diff(this.getLicenseHeader().split(LINE_ENDING), header.split(LINE_ENDING));
      } catch (Exception e) {
         Assert.fail(e.getMessage());
      }

      return revision.size() == 0;
   }

   public List<String> getSuffixes() {
      return Arrays.asList("css");
   }

   public boolean addLicenseHeader(File file) {
      this.prependLicenseHeader(file);
      return true;
   }
}
