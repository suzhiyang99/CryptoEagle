package org.apache.openejb.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ThreadLocalServletRequest extends AbstractRestThreadLocalProxy<ServletRequest> implements ServletRequest {
   protected ThreadLocalServletRequest() {
      super(ServletRequest.class);
   }

   public AsyncContext getAsyncContext() {
      return ((ServletRequest)this.get()).getAsyncContext();
   }

   public Object getAttribute(String string) {
      return ((ServletRequest)this.get()).getAttribute(string);
   }

   public Enumeration<String> getAttributeNames() {
      return ((ServletRequest)this.get()).getAttributeNames();
   }

   public String getCharacterEncoding() {
      return ((ServletRequest)this.get()).getCharacterEncoding();
   }

   public int getContentLength() {
      return ((ServletRequest)this.get()).getContentLength();
   }

   public long getContentLengthLong() {
      return ((ServletRequest)this.get()).getContentLengthLong();
   }

   public String getContentType() {
      return ((ServletRequest)this.get()).getContentType();
   }

   public DispatcherType getDispatcherType() {
      return ((ServletRequest)this.get()).getDispatcherType();
   }

   public ServletInputStream getInputStream() throws IOException {
      return ((ServletRequest)this.get()).getInputStream();
   }

   public String getLocalAddr() {
      return ((ServletRequest)this.get()).getLocalAddr();
   }

   public Locale getLocale() {
      return ((ServletRequest)this.get()).getLocale();
   }

   public Enumeration<Locale> getLocales() {
      return ((ServletRequest)this.get()).getLocales();
   }

   public String getLocalName() {
      return ((ServletRequest)this.get()).getLocalName();
   }

   public int getLocalPort() {
      return ((ServletRequest)this.get()).getLocalPort();
   }

   public String getParameter(String string) {
      return ((ServletRequest)this.get()).getParameter(string);
   }

   public Map<String, String[]> getParameterMap() {
      return ((ServletRequest)this.get()).getParameterMap();
   }

   public String[] getParameterValues(String string) {
      return ((ServletRequest)this.get()).getParameterValues(string);
   }

   public Enumeration<String> getParameterNames() {
      return ((ServletRequest)this.get()).getParameterNames();
   }

   public String getProtocol() {
      return ((ServletRequest)this.get()).getProtocol();
   }

   public BufferedReader getReader() throws IOException {
      return ((ServletRequest)this.get()).getReader();
   }

   public String getRealPath(String string) {
      return ((ServletRequest)this.get()).getRealPath(string);
   }

   public String getRemoteAddr() {
      return ((ServletRequest)this.get()).getRemoteAddr();
   }

   public String getRemoteHost() {
      return ((ServletRequest)this.get()).getRemoteHost();
   }

   public int getRemotePort() {
      return ((ServletRequest)this.get()).getRemotePort();
   }

   public RequestDispatcher getRequestDispatcher(String string) {
      return ((ServletRequest)this.get()).getRequestDispatcher(string);
   }

   public String getScheme() {
      return ((ServletRequest)this.get()).getScheme();
   }

   public String getServerName() {
      return ((ServletRequest)this.get()).getServerName();
   }

   public int getServerPort() {
      return ((ServletRequest)this.get()).getServerPort();
   }

   public ServletContext getServletContext() {
      return ((ServletRequest)this.get()).getServletContext();
   }

   public boolean isAsyncStarted() {
      return ((ServletRequest)this.get()).isAsyncStarted();
   }

   public boolean isAsyncSupported() {
      return ((ServletRequest)this.get()).isAsyncSupported();
   }

   public boolean isSecure() {
      return ((ServletRequest)this.get()).isSecure();
   }

   public void removeAttribute(String string) {
      ((ServletRequest)this.get()).removeAttribute(string);
   }

   public void setAttribute(String string, Object object) {
      ((ServletRequest)this.get()).setAttribute(string, object);
   }

   public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
      ((ServletRequest)this.get()).setCharacterEncoding(string);
   }

   public AsyncContext startAsync() {
      return ((ServletRequest)this.get()).startAsync();
   }

   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
      return ((ServletRequest)this.get()).startAsync(servletRequest, servletResponse);
   }
}
