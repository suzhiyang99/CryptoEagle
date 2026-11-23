package org.spark_project.jetty.http;

import java.util.Collections;
import java.util.Iterator;

public class MetaData implements Iterable<HttpField> {
   private HttpVersion _httpVersion;
   private HttpFields _fields;
   private long _contentLength;

   public MetaData(HttpVersion version, HttpFields fields) {
      this(version, fields, Long.MIN_VALUE);
   }

   public MetaData(HttpVersion version, HttpFields fields, long contentLength) {
      this._httpVersion = version;
      this._fields = fields;
      this._contentLength = contentLength;
   }

   protected void recycle() {
      this._httpVersion = null;
      if (this._fields != null) {
         this._fields.clear();
      }

      this._contentLength = Long.MIN_VALUE;
   }

   public boolean isRequest() {
      return false;
   }

   public boolean isResponse() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public HttpVersion getVersion() {
      return this.getHttpVersion();
   }

   public HttpVersion getHttpVersion() {
      return this._httpVersion;
   }

   public void setHttpVersion(HttpVersion httpVersion) {
      this._httpVersion = httpVersion;
   }

   public HttpFields getFields() {
      return this._fields;
   }

   public long getContentLength() {
      if (this._contentLength == Long.MIN_VALUE && this._fields != null) {
         HttpField field = this._fields.getField(HttpHeader.CONTENT_LENGTH);
         this._contentLength = field == null ? -1L : field.getLongValue();
      }

      return this._contentLength;
   }

   public Iterator<HttpField> iterator() {
      HttpFields fields = this.getFields();
      return fields == null ? Collections.emptyIterator() : fields.iterator();
   }

   public String toString() {
      StringBuilder out = new StringBuilder();

      for(HttpField field : this) {
         out.append(field).append(System.lineSeparator());
      }

      return out.toString();
   }

   public static class Request extends MetaData {
      private String _method;
      private HttpURI _uri;

      public Request(HttpFields fields) {
         this((String)null, (HttpURI)null, (HttpVersion)null, fields);
      }

      public Request(String method, HttpURI uri, HttpVersion version, HttpFields fields) {
         this(method, uri, version, fields, Long.MIN_VALUE);
      }

      public Request(String method, HttpURI uri, HttpVersion version, HttpFields fields, long contentLength) {
         super(version, fields, contentLength);
         this._method = method;
         this._uri = uri;
      }

      public Request(String method, HttpScheme scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields) {
         this(method, new HttpURI(scheme == null ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields);
      }

      public Request(String method, HttpScheme scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields, long contentLength) {
         this(method, new HttpURI(scheme == null ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
      }

      public Request(String method, String scheme, HostPortHttpField hostPort, String uri, HttpVersion version, HttpFields fields, long contentLength) {
         this(method, new HttpURI(scheme, hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
      }

      public Request(Request request) {
         this(request.getMethod(), new HttpURI(request.getURI()), request.getHttpVersion(), new HttpFields(request.getFields()), request.getContentLength());
      }

      public void recycle() {
         super.recycle();
         this._method = null;
         if (this._uri != null) {
            this._uri.clear();
         }

      }

      public boolean isRequest() {
         return true;
      }

      public String getMethod() {
         return this._method;
      }

      public void setMethod(String method) {
         this._method = method;
      }

      public HttpURI getURI() {
         return this._uri;
      }

      public String getURIString() {
         return this._uri == null ? null : this._uri.toString();
      }

      public void setURI(HttpURI uri) {
         this._uri = uri;
      }

      public String toString() {
         HttpFields fields = this.getFields();
         return String.format("%s{u=%s,%s,h=%d}", this.getMethod(), this.getURI(), this.getHttpVersion(), fields == null ? -1 : fields.size());
      }
   }

   public static class Response extends MetaData {
      private int _status;
      private String _reason;

      public Response() {
         this((HttpVersion)null, 0, (HttpFields)null);
      }

      public Response(HttpVersion version, int status, HttpFields fields) {
         this(version, status, fields, Long.MIN_VALUE);
      }

      public Response(HttpVersion version, int status, HttpFields fields, long contentLength) {
         super(version, fields, contentLength);
         this._status = status;
      }

      public Response(HttpVersion version, int status, String reason, HttpFields fields, long contentLength) {
         super(version, fields, contentLength);
         this._reason = reason;
         this._status = status;
      }

      public boolean isResponse() {
         return true;
      }

      public int getStatus() {
         return this._status;
      }

      public String getReason() {
         return this._reason;
      }

      public void setStatus(int status) {
         this._status = status;
      }

      public void setReason(String reason) {
         this._reason = reason;
      }

      public String toString() {
         HttpFields fields = this.getFields();
         return String.format("%s{s=%d,h=%d}", this.getHttpVersion(), this.getStatus(), fields == null ? -1 : fields.size());
      }
   }
}
