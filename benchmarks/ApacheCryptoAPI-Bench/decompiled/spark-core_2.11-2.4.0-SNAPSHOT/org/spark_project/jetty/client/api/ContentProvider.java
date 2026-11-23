package org.spark_project.jetty.client.api;

import java.nio.ByteBuffer;

public interface ContentProvider extends Iterable<ByteBuffer> {
   long getLength();

   public interface Typed extends ContentProvider {
      String getContentType();
   }
}
