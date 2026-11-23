package org.spark_project.jetty.server;

import java.nio.ByteBuffer;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.util.Callback;

public interface HttpTransport {
   void send(MetaData.Response var1, boolean var2, ByteBuffer var3, boolean var4, Callback var5);

   boolean isPushSupported();

   void push(MetaData.Request var1);

   void onCompleted();

   void abort(Throwable var1);

   boolean isOptimizedForDirectBuffers();
}
