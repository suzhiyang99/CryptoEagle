package org.apache.wicket.util.collections;

import java.io.Serializable;
import java.net.URL;
import java.util.Comparator;

public class UrlExternalFormComparator implements Comparator<URL>, Serializable {
   private static final long serialVersionUID = 1L;

   public int compare(URL url1, URL url2) {
      return url1.toExternalForm().compareTo(url2.toExternalForm());
   }
}
