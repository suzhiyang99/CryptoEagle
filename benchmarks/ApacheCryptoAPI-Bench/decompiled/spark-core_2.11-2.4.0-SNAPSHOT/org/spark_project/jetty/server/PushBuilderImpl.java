package org.spark_project.jetty.server;

import java.util.Set;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpFields;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpMethod;
import org.spark_project.jetty.http.HttpURI;
import org.spark_project.jetty.http.MetaData;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class PushBuilderImpl implements PushBuilder {
   private static final Logger LOG = Log.getLogger(PushBuilderImpl.class);
   private static final HttpField JettyPush = new HttpField("x-http2-push", "PushBuilder");
   private final Request _request;
   private final HttpFields _fields;
   private String _method;
   private String _queryString;
   private String _sessionId;
   private boolean _conditional;
   private String _path;
   private String _etag;
   private String _lastModified;

   public PushBuilderImpl(Request request, HttpFields fields, String method, String queryString, String sessionId, boolean conditional) {
      this._request = request;
      this._fields = fields;
      this._method = method;
      this._queryString = queryString;
      this._sessionId = sessionId;
      this._conditional = conditional;
      this._fields.add(JettyPush);
      if (LOG.isDebugEnabled()) {
         LOG.debug("PushBuilder({} {}?{} s={} c={})", this._method, this._request.getRequestURI(), this._queryString, this._sessionId, this._conditional);
      }

   }

   public String getMethod() {
      return this._method;
   }

   public PushBuilder method(String method) {
      this._method = method;
      return this;
   }

   public String getQueryString() {
      return this._queryString;
   }

   public PushBuilder queryString(String queryString) {
      this._queryString = queryString;
      return this;
   }

   public String getSessionId() {
      return this._sessionId;
   }

   public PushBuilder sessionId(String sessionId) {
      this._sessionId = sessionId;
      return this;
   }

   public boolean isConditional() {
      return this._conditional;
   }

   public PushBuilder conditional(boolean conditional) {
      this._conditional = conditional;
      return this;
   }

   public Set<String> getHeaderNames() {
      return this._fields.getFieldNamesCollection();
   }

   public String getHeader(String name) {
      return this._fields.get(name);
   }

   public PushBuilder setHeader(String name, String value) {
      this._fields.put(name, value);
      return this;
   }

   public PushBuilder addHeader(String name, String value) {
      this._fields.add(name, value);
      return this;
   }

   public String getPath() {
      return this._path;
   }

   public PushBuilder path(String path) {
      this._path = path;
      return this;
   }

   public String getEtag() {
      return this._etag;
   }

   public PushBuilder etag(String etag) {
      this._etag = etag;
      return this;
   }

   public String getLastModified() {
      return this._lastModified;
   }

   public PushBuilder lastModified(String lastModified) {
      this._lastModified = lastModified;
      return this;
   }

   public void push() {
      if (!HttpMethod.POST.is(this._method) && !HttpMethod.PUT.is(this._method)) {
         if (this._path != null && this._path.length() != 0) {
            String path = this._path;
            String query = this._queryString;
            int q = path.indexOf(63);
            if (q >= 0) {
               query = query != null && query.length() > 0 ? this._path.substring(q + 1) + '&' + query : this._path.substring(q + 1);
               path = this._path.substring(0, q);
            }

            if (!path.startsWith("/")) {
               path = URIUtil.addPaths(this._request.getContextPath(), path);
            }

            String param = null;
            if (this._sessionId != null && this._request.isRequestedSessionIdFromURL()) {
               param = "jsessionid=" + this._sessionId;
            }

            if (this._conditional) {
               if (this._etag != null) {
                  this._fields.add(HttpHeader.IF_NONE_MATCH, this._etag);
               } else if (this._lastModified != null) {
                  this._fields.add(HttpHeader.IF_MODIFIED_SINCE, this._lastModified);
               }
            }

            HttpURI uri = HttpURI.createHttpURI(this._request.getScheme(), this._request.getServerName(), this._request.getServerPort(), path, param, query, (String)null);
            MetaData.Request push = new MetaData.Request(this._method, uri, this._request.getHttpVersion(), this._fields);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Push {} {} inm={} ims={}", this._method, uri, this._fields.get(HttpHeader.IF_NONE_MATCH), this._fields.get(HttpHeader.IF_MODIFIED_SINCE));
            }

            this._request.getHttpChannel().getHttpTransport().push(push);
            this._path = null;
            this._etag = null;
            this._lastModified = null;
         } else {
            throw new IllegalStateException("Bad Path " + this._path);
         }
      } else {
         throw new IllegalStateException("Bad Method " + this._method);
      }
   }
}
