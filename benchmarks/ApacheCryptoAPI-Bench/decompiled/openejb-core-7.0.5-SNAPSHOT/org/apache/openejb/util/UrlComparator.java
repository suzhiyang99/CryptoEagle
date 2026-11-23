package org.apache.openejb.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class UrlComparator implements Comparator<URL> {
   private final File dir;
   private final List<String> rootPath;

   public UrlComparator(File directory) {
      this.dir = directory;
      this.rootPath = this.path(this.dir);
   }

   public UrlComparator(URL base) {
      this(URLs.toFile(base));
   }

   public int compare(URL a, URL b) {
      return this.score(b) - this.score(a);
   }

   private int score(URL url) {
      File file = URLs.toFile(url);
      List<String> filePath = this.path(file);
      int matches = 0;
      ListIterator<String> a = this.rootPath.listIterator();

      for(ListIterator<String> b = filePath.listIterator(); a.hasNext() && b.hasNext(); ++matches) {
         String nameA = (String)a.next();
         String nameB = (String)b.next();
         if (!nameA.equals(nameB)) {
            break;
         }
      }

      return matches;
   }

   private List<String> path(File file) {
      ArrayList<String> path = new ArrayList();
      this.path(file, path);
      return path;
   }

   private void path(File file, List<String> path) {
      if (file != null) {
         this.path(file.getParentFile(), path);
         path.add(file.getName());
      }
   }
}
