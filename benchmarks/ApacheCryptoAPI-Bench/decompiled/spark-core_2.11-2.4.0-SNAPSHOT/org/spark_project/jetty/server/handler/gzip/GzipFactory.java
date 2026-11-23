package org.spark_project.jetty.server.handler.gzip;

import java.util.zip.Deflater;
import org.spark_project.jetty.server.Request;

public interface GzipFactory {
   Deflater getDeflater(Request var1, long var2);

   boolean isMimeTypeGzipable(String var1);

   void recycle(Deflater var1);
}
