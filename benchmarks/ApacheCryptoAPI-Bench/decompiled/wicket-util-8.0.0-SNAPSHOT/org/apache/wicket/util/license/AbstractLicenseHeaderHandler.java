package org.apache.wicket.util.license;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.junit.Assert;

abstract class AbstractLicenseHeaderHandler implements ILicenseHeaderHandler {
   protected static final String LINE_ENDING = System.getProperty("line.separator");
   private String licenseHeader;
   private final List<String> ignoreFiles;

   public AbstractLicenseHeaderHandler(List<String> ignoreFiles) {
      this.ignoreFiles = ignoreFiles;
   }

   public List<String> getIgnoreFiles() {
      return this.ignoreFiles;
   }

   public boolean addLicenseHeader(File file) {
      System.out.println("Not supported yet.");
      return false;
   }

   public String getLicenseType(File file) {
      return null;
   }

   protected abstract String getLicenseHeaderFilename();

   protected String getLicenseHeader() {
      if (Strings.isEmpty(this.licenseHeader)) {
         LineNumberReader lineNumberReader = null;
         InputStream inputStream = null;
         InputStreamReader inputStreamReader = null;

         try {
            inputStream = ApacheLicenseHeaderTestCase.class.getResourceAsStream(this.getLicenseHeaderFilename());
            inputStreamReader = new InputStreamReader(inputStream);
            lineNumberReader = new LineNumberReader(inputStreamReader);
            StringBuilder header = new StringBuilder();

            for(String line = lineNumberReader.readLine(); line != null; line = lineNumberReader.readLine()) {
               header.append(line);
               header.append(LINE_ENDING);
            }

            this.licenseHeader = header.toString().trim();
         } catch (Exception e) {
            Assert.fail(e.getMessage());
         } finally {
            IOUtils.closeQuietly(lineNumberReader);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStreamReader);
         }
      }

      return this.licenseHeader;
   }

   protected String extractLicenseHeader(File file, int start, int length) {
      StringBuilder header = new StringBuilder();
      LineNumberReader lineNumberReader = null;

      try {
         FileReader fileReader = new FileReader(file);
         lineNumberReader = new LineNumberReader(fileReader);

         for(int i = start; i < length; ++i) {
            header.append(lineNumberReader.readLine());
            header.append(LINE_ENDING);
         }
      } catch (Exception e) {
         Assert.fail(e.getMessage());
      } finally {
         try {
            IOUtils.close(lineNumberReader);
         } catch (IOException e) {
            Assert.fail(e.getMessage());
         }

      }

      return header.toString().trim();
   }

   protected void prependLicenseHeader(File file) {
      try {
         String content = (new org.apache.wicket.util.file.File(file)).readString();
         content = this.getLicenseHeader() + LINE_ENDING + content;
         (new org.apache.wicket.util.file.File(file)).write(content);
      } catch (Exception e) {
         Assert.fail(e.getMessage());
      }

   }
}
