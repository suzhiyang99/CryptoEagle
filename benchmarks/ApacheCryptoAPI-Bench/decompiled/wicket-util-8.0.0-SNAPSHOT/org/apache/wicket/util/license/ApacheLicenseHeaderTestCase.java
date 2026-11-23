package org.apache.wicket.util.license;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.wicket.util.lang.Generics;
import org.apache.wicket.util.string.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ApacheLicenseHeaderTestCase extends Assert {
   private static final Logger log = LoggerFactory.getLogger(ApacheLicenseHeaderTestCase.class);
   private static final String LINE_ENDING = System.getProperty("line.separator");
   private ILicenseHeaderHandler[] licenseHeaderHandlers;
   private File baseDirectory = (new File("")).getAbsoluteFile();
   protected List<String> javaIgnore = Generics.<String>newArrayList();
   protected List<String> htmlIgnore = Generics.<String>newArrayList();
   protected List<String> xmlPrologIgnore = Generics.<String>newArrayList();
   protected List<String> propertiesIgnore = Generics.<String>newArrayList();
   protected List<String> xmlIgnore = Generics.<String>newArrayList();
   protected List<String> cssIgnore = Generics.<String>newArrayList();
   protected List<String> velocityIgnore = Generics.<String>newArrayList();
   protected List<String> javaScriptIgnore = Generics.<String>newArrayList();
   protected boolean addHeaders = false;

   public ApacheLicenseHeaderTestCase() {
      this.xmlIgnore.add(".settings");
      this.xmlIgnore.add("EclipseCodeFormat.xml");
      this.xmlIgnore.add("nb-configuration.xml");
      this.htmlIgnore.add("src/test/java");
      this.propertiesIgnore.add("src/test/java");
      this.xmlPrologIgnore.add("src/test/java");
      this.xmlPrologIgnore.add("package.html");
   }

   @Before
   public final void before() {
      String property = System.getProperty("basedir");
      if (!Strings.isEmpty(property)) {
         this.baseDirectory = (new File(property)).getAbsoluteFile();
      }

   }

   @Test
   public void licenseHeaders() {
      this.licenseHeaderHandlers = new ILicenseHeaderHandler[]{new JavaLicenseHeaderHandler(this.javaIgnore), new JavaScriptLicenseHeaderHandler(this.javaScriptIgnore), new XmlLicenseHeaderHandler(this.xmlIgnore), new PropertiesLicenseHeaderHandler(this.propertiesIgnore), new HtmlLicenseHeaderHandler(this.htmlIgnore), new VelocityLicenseHeaderHandler(this.velocityIgnore), new XmlPrologHeaderHandler(this.xmlPrologIgnore), new CssLicenseHeaderHandler(this.cssIgnore)};
      final Map<ILicenseHeaderHandler, List<File>> badFiles = new HashMap();

      for(final ILicenseHeaderHandler licenseHeaderHandler : this.licenseHeaderHandlers) {
         this.visitFiles(licenseHeaderHandler.getSuffixes(), licenseHeaderHandler.getIgnoreFiles(), new FileVisitor() {
            public void visitFile(File file) {
               if (!licenseHeaderHandler.checkLicenseHeader(file) && (!ApacheLicenseHeaderTestCase.this.addHeaders || !licenseHeaderHandler.addLicenseHeader(file))) {
                  List<File> files = (List)badFiles.get(licenseHeaderHandler);
                  if (files == null) {
                     files = new ArrayList();
                     badFiles.put(licenseHeaderHandler, files);
                  }

                  files.add(file);
               }

            }
         });
      }

      this.failIncorrectLicenceHeaders(badFiles);
   }

   private void failIncorrectLicenceHeaders(Map<ILicenseHeaderHandler, List<File>> files) {
      if (files.size() > 0) {
         StringBuilder failString = new StringBuilder();

         for(Map.Entry<ILicenseHeaderHandler, List<File>> entry : files.entrySet()) {
            ILicenseHeaderHandler licenseHeaderHandler = (ILicenseHeaderHandler)entry.getKey();
            List<File> fileList = (List)entry.getValue();
            failString.append('\n');
            failString.append(licenseHeaderHandler.getClass().getName());
            failString.append(" failed. The following files(");
            failString.append(fileList.size());
            failString.append(") didn't have correct license header:\n");

            for(File file : fileList) {
               String filename = file.getAbsolutePath();
               String licenseType = licenseHeaderHandler.getLicenseType(file);
               if (licenseType == null) {
                  failString.append("NONE");
               } else {
                  failString.append(licenseType);
               }

               failString.append(' ').append(filename).append(LINE_ENDING);
            }
         }

         System.out.println(failString);
         fail(failString.toString());
      }

   }

   private void visitFiles(List<String> suffixes, List<String> ignoreFiles, FileVisitor fileVisitor) {
      this.visitDirectory(suffixes, ignoreFiles, this.baseDirectory, fileVisitor);
   }

   private void visitDirectory(List<String> suffixes, List<String> ignoreFiles, File directory, FileVisitor fileVisitor) {
      File[] files = directory.listFiles(new SuffixAndIgnoreFileFilter(suffixes, ignoreFiles));
      if (files != null) {
         for(File file : files) {
            fileVisitor.visitFile(file);
         }
      }

      files = directory.listFiles(new DirectoryFileFilter());
      if (files != null) {
         for(File childDirectory : files) {
            this.visitDirectory(suffixes, ignoreFiles, childDirectory, fileVisitor);
         }
      }

   }

   private class SuffixAndIgnoreFileFilter implements FileFilter {
      private final List<String> suffixes;
      private final List<String> ignoreFiles;

      private SuffixAndIgnoreFileFilter(List<String> suffixes, List<String> ignoreFiles) {
         this.suffixes = suffixes;
         this.ignoreFiles = ignoreFiles;
      }

      public boolean accept(File pathname) {
         boolean accept = false;
         if (pathname.isFile()) {
            if (!this.ignoreFile(pathname)) {
               for(String suffix : this.suffixes) {
                  if (pathname.getName().endsWith("." + suffix)) {
                     accept = true;
                     break;
                  }

                  ApacheLicenseHeaderTestCase.log.debug("File ignored: '{}'", pathname);
               }
            } else {
               ApacheLicenseHeaderTestCase.log.debug("File ignored: '{}'", pathname);
            }
         }

         return accept;
      }

      private boolean ignoreFile(File pathname) {
         boolean ignore = false;
         if (this.ignoreFiles != null) {
            String relativePathname = pathname.getAbsolutePath();
            relativePathname = Strings.replaceAll(relativePathname, ApacheLicenseHeaderTestCase.this.baseDirectory.getAbsolutePath() + System.getProperty("file.separator"), "").toString();

            for(String ignorePath : this.ignoreFiles) {
               ignorePath = Strings.replaceAll(ignorePath, "/", System.getProperty("file.separator")).toString();
               File ignoreFile = new File(ApacheLicenseHeaderTestCase.this.baseDirectory, ignorePath);
               if (ignoreFile.isDirectory()) {
                  if (pathname.getAbsolutePath().startsWith(ignoreFile.getAbsolutePath())) {
                     ignore = true;
                     break;
                  }
               } else if (ignoreFile.isFile()) {
                  if (relativePathname.equals(ignorePath)) {
                     ignore = true;
                     break;
                  }
               } else if (pathname.getName().equals(ignorePath)) {
                  ignore = true;
                  break;
               }
            }
         }

         return ignore;
      }
   }

   private class DirectoryFileFilter implements FileFilter {
      private final String[] ignoreDirectory;

      private DirectoryFileFilter() {
         this.ignoreDirectory = new String[]{".git"};
      }

      public boolean accept(File pathname) {
         boolean accept = false;
         if (pathname.isDirectory()) {
            String relativePathname = pathname.getAbsolutePath();
            relativePathname = Strings.replaceAll(relativePathname, ApacheLicenseHeaderTestCase.this.baseDirectory.getAbsolutePath() + System.getProperty("file.separator"), "").toString();
            if (!"target".equals(relativePathname)) {
               boolean found = false;

               for(String ignore : this.ignoreDirectory) {
                  if (pathname.getName().equals(ignore)) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  accept = true;
               }
            }
         }

         return accept;
      }
   }

   interface FileVisitor {
      void visitFile(File var1);
   }
}
