package org.apache.wicket.util.license;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.util.string.Strings;
import org.junit.Assert;

class JavaLicenseHeaderHandler extends AbstractLicenseHeaderHandler {
   private final Pattern javaHeaderPattern = Pattern.compile("^(.*?)package.*$", 40);

   public JavaLicenseHeaderHandler(List<String> ignoreFiles) {
      super(ignoreFiles);
   }

   public boolean addLicenseHeader(File file) {
      boolean added = false;

      try {
         String fileContent = (new org.apache.wicket.util.file.File(file)).readString();
         Matcher mat = this.javaHeaderPattern.matcher(fileContent);
         if (mat.matches()) {
            String header = mat.group(1);
            if (!header.equals(this.getLicenseHeader())) {
               String newContent = Strings.replaceAll(fileContent, header, "").toString();
               newContent = this.getLicenseHeader().trim() + LINE_ENDING + newContent;
               (new org.apache.wicket.util.file.File(file)).write(newContent);
               added = true;
            }
         } else {
            Assert.fail();
         }
      } catch (Exception e) {
         e.printStackTrace();
         Assert.fail(e.getMessage());
      }

      return added;
   }

   public boolean checkLicenseHeader(File file) {
      String header = this.extractLicenseHeader(file, 0, 16);
      return this.getLicenseHeader().equals(header);
   }

   public List<String> getSuffixes() {
      return Arrays.asList("java");
   }

   protected String getLicenseHeaderFilename() {
      return "javaLicense.txt";
   }

   public String getLicenseType(File file) {
      String licenseType = null;
      String header = this.extractLicenseHeader(file, 0, 20);
      if (header.contains("Apache License, Version 2.0")) {
         licenseType = "ASL2";
      } else if (header.contains("The Apache Software License, Version 1.1")) {
         licenseType = "ASL1.1";
      }

      return licenseType;
   }
}
