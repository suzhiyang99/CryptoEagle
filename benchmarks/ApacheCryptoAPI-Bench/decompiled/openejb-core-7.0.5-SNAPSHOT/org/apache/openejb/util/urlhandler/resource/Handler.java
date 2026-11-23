package org.apache.openejb.util.urlhandler.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Handler extends URLStreamHandler {
   protected URLConnection openConnection(URL url) throws IOException {
      String cln = url.getHost();
      String resrce = url.getFile().substring(1);
      URL realURL;
      if (cln != null && cln.length() != 0) {
         ClassLoader cl = getContextClassLoader();

         try {
            Class.forName(cln, true, cl);
         } catch (ClassNotFoundException ex) {
            throw (IOException)(new MalformedURLException("Class " + cln + " cannot be found (" + ex + ")")).initCause(ex);
         }

         realURL = cl.getResource(resrce);
         if (realURL == null) {
            throw new FileNotFoundException("Class resource " + resrce + " of class " + cln + " cannot be found");
         }
      } else {
         ClassLoader cl = getContextClassLoader();
         realURL = cl.getResource(resrce);
         if (realURL == null) {
            throw new FileNotFoundException("System resource " + resrce + " cannot be found");
         }
      }

      return realURL.openConnection();
   }

   public static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }
}
