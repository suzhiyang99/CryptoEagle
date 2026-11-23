package org.apache.tika.fork;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class MemoryURLStreamHandlerFactory implements URLStreamHandlerFactory {
   public URLStreamHandler createURLStreamHandler(String protocol) {
      return "tika-in-memory".equals(protocol) ? new MemoryURLStreamHandler() : null;
   }
}
