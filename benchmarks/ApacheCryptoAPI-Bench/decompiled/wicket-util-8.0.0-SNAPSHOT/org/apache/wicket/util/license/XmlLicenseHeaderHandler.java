package org.apache.wicket.util.license;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.diff.Diff;
import org.apache.wicket.util.diff.Revision;
import org.apache.wicket.util.string.Strings;
import org.junit.Assert;

class XmlLicenseHeaderHandler extends AbstractLicenseHeaderHandler {
   private final Pattern xmlHeader;

   public XmlLicenseHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
      this.xmlHeader = Pattern.compile("^(\\<\\?xml[^" + LINE_ENDING + "]+?)" + LINE_ENDING + "(.*)$", 40);
   }

   protected String getLicenseHeaderFilename() {
      return "xmlLicense.txt";
   }

   public boolean checkLicenseHeader(File file) {
      Revision revision = null;

      try {
         String header = this.extractLicenseHeader(file, 0, 17);
         if (header.startsWith("<?xml")) {
            header = header.substring(header.indexOf(LINE_ENDING) + LINE_ENDING.length());
         } else {
            String[] headers = header.split(LINE_ENDING);
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < 16 && i < headers.length; ++i) {
               if (sb.length() > 0) {
                  sb.append(LINE_ENDING);
               }

               sb.append(headers[i]);
            }

            header = sb.toString();
         }

         revision = Diff.diff(this.getLicenseHeader().split(LINE_ENDING), header.split(LINE_ENDING));
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail(e.getMessage());
      }

      return revision.size() == 0;
   }

   public List<String> getSuffixes() {
      return Arrays.asList("xml", "fml");
   }

   public boolean addLicenseHeader(File file) {
      boolean added = false;

      try {
         String content = (new org.apache.wicket.util.file.File(file)).readString();
         String xml = "";
         StringBuilder newContent = new StringBuilder();
         Matcher mat = this.xmlHeader.matcher(content);
         if (mat.matches()) {
            xml = mat.group(1);
            content = mat.group(2);
         }

         if (!Strings.isEmpty(xml)) {
            newContent.append(xml).append(LINE_ENDING);
         }

         newContent.append(this.getLicenseHeader()).append(LINE_ENDING);
         newContent.append(content);
         (new org.apache.wicket.util.file.File(file)).write(newContent.toString());
         added = true;
      } catch (Exception e) {
         Assert.fail(e.getMessage());
      }

      return added;
   }
}
