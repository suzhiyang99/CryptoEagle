package org.spark_project.jetty.client.api;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpVersion;
import org.spark_project.jetty.util.Fields;

public interface Request {
   String getScheme();

   Request scheme(String var1);

   String getHost();

   int getPort();

   String getMethod();

   Request method(HttpMethod var1);

   Request method(String var1);

   String getPath();

   Request path(String var1);

   String getQuery();

   URI getURI();

   HttpVersion getVersion();

   Request version(HttpVersion var1);

   Fields getParams();

   Request param(String var1, String var2);

   HttpFields getHeaders();

   Request header(String var1, String var2);

   Request header(HttpHeader var1, String var2);

   List<HttpCookie> getCookies();

   Request cookie(HttpCookie var1);

   Request attribute(String var1, Object var2);

   Map<String, Object> getAttributes();

   ContentProvider getContent();

   Request content(ContentProvider var1);

   Request content(ContentProvider var1, String var2);

   Request file(Path var1) throws IOException;

   Request file(Path var1, String var2) throws IOException;

   String getAgent();

   Request agent(String var1);

   Request accept(String... var1);

   long getIdleTimeout();

   Request idleTimeout(long var1, TimeUnit var3);

   long getTimeout();

   Request timeout(long var1, TimeUnit var3);

   boolean isFollowRedirects();

   Request followRedirects(boolean var1);

   <T extends RequestListener> List<T> getRequestListeners(Class<T> var1);

   Request listener(Listener var1);

   Request onRequestQueued(QueuedListener var1);

   Request onRequestBegin(BeginListener var1);

   Request onRequestHeaders(HeadersListener var1);

   Request onRequestCommit(CommitListener var1);

   Request onRequestContent(ContentListener var1);

   Request onRequestSuccess(SuccessListener var1);

   Request onRequestFailure(FailureListener var1);

   Request onResponseBegin(Response.BeginListener var1);

   Request onResponseHeader(Response.HeaderListener var1);

   Request onResponseHeaders(Response.HeadersListener var1);

   Request onResponseContent(Response.ContentListener var1);

   Request onResponseContentAsync(Response.AsyncContentListener var1);

   Request onResponseSuccess(Response.SuccessListener var1);

   Request onResponseFailure(Response.FailureListener var1);

   Request onComplete(Response.CompleteListener var1);

   ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException;

   void send(Response.CompleteListener var1);

   boolean abort(Throwable var1);

   Throwable getAbortCause();

   public interface BeginListener extends RequestListener {
      void onBegin(Request var1);
   }

   public interface CommitListener extends RequestListener {
      void onCommit(Request var1);
   }

   public interface ContentListener extends RequestListener {
      void onContent(Request var1, ByteBuffer var2);
   }

   public interface FailureListener extends RequestListener {
      void onFailure(Request var1, Throwable var2);
   }

   public interface HeadersListener extends RequestListener {
      void onHeaders(Request var1);
   }

   public interface Listener extends QueuedListener, BeginListener, HeadersListener, CommitListener, ContentListener, SuccessListener, FailureListener {
      public static class Adapter implements Listener {
         public void onQueued(Request request) {
         }

         public void onBegin(Request request) {
         }

         public void onHeaders(Request request) {
         }

         public void onCommit(Request request) {
         }

         public void onContent(Request request, ByteBuffer content) {
         }

         public void onSuccess(Request request) {
         }

         public void onFailure(Request request, Throwable failure) {
         }
      }
   }

   public interface QueuedListener extends RequestListener {
      void onQueued(Request var1);
   }

   public interface RequestListener extends EventListener {
   }

   public interface SuccessListener extends RequestListener {
      void onSuccess(Request var1);
   }
}
