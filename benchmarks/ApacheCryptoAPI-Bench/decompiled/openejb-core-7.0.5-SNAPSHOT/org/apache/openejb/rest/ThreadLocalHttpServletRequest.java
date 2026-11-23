package org.apache.openejb.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

public class ThreadLocalHttpServletRequest extends AbstractRestThreadLocalProxy<HttpServletRequest> implements HttpServletRequest {
   protected ThreadLocalHttpServletRequest() {
      super(HttpServletRequest.class);
   }

   public AsyncContext getAsyncContext() {
      return ((HttpServletRequest)this.get()).getAsyncContext();
   }

   public Object getAttribute(String string) {
      return ((HttpServletRequest)this.get()).getAttribute(string);
   }

   public Enumeration<String> getAttributeNames() {
      return ((HttpServletRequest)this.get()).getAttributeNames();
   }

   public String getCharacterEncoding() {
      return ((HttpServletRequest)this.get()).getCharacterEncoding();
   }

   public int getContentLength() {
      return ((HttpServletRequest)this.get()).getContentLength();
   }

   public long getContentLengthLong() {
      return ((HttpServletRequest)this.get()).getContentLengthLong();
   }

   public String getContentType() {
      return ((HttpServletRequest)this.get()).getContentType();
   }

   public DispatcherType getDispatcherType() {
      return ((HttpServletRequest)this.get()).getDispatcherType();
   }

   public ServletInputStream getInputStream() throws IOException {
      return ((HttpServletRequest)this.get()).getInputStream();
   }

   public String getLocalAddr() {
      return ((HttpServletRequest)this.get()).getLocalAddr();
   }

   public Locale getLocale() {
      return ((HttpServletRequest)this.get()).getLocale();
   }

   public Enumeration<Locale> getLocales() {
      return ((HttpServletRequest)this.get()).getLocales();
   }

   public String getLocalName() {
      return ((HttpServletRequest)this.get()).getLocalName();
   }

   public int getLocalPort() {
      return ((HttpServletRequest)this.get()).getLocalPort();
   }

   public String getParameter(String string) {
      return ((HttpServletRequest)this.get()).getParameter(string);
   }

   public Map<String, String[]> getParameterMap() {
      return ((HttpServletRequest)this.get()).getParameterMap();
   }

   public String[] getParameterValues(String string) {
      return ((HttpServletRequest)this.get()).getParameterValues(string);
   }

   public Enumeration<String> getParameterNames() {
      return ((HttpServletRequest)this.get()).getParameterNames();
   }

   public String getProtocol() {
      return ((HttpServletRequest)this.get()).getProtocol();
   }

   public BufferedReader getReader() throws IOException {
      return ((HttpServletRequest)this.get()).getReader();
   }

   public String getRealPath(String string) {
      return ((HttpServletRequest)this.get()).getRealPath(string);
   }

   public String getRemoteAddr() {
      return ((HttpServletRequest)this.get()).getRemoteAddr();
   }

   public String getRemoteHost() {
      return ((HttpServletRequest)this.get()).getRemoteHost();
   }

   public int getRemotePort() {
      return ((HttpServletRequest)this.get()).getRemotePort();
   }

   public RequestDispatcher getRequestDispatcher(String string) {
      return ((HttpServletRequest)this.get()).getRequestDispatcher(string);
   }

   public String getScheme() {
      return ((HttpServletRequest)this.get()).getScheme();
   }

   public String getServerName() {
      return ((HttpServletRequest)this.get()).getServerName();
   }

   public int getServerPort() {
      return ((HttpServletRequest)this.get()).getServerPort();
   }

   public ServletContext getServletContext() {
      return ((HttpServletRequest)this.get()).getServletContext();
   }

   public boolean isAsyncStarted() {
      return ((HttpServletRequest)this.get()).isAsyncStarted();
   }

   public boolean isAsyncSupported() {
      return ((HttpServletRequest)this.get()).isAsyncSupported();
   }

   public boolean isSecure() {
      return ((HttpServletRequest)this.get()).isSecure();
   }

   public void removeAttribute(String string) {
      ((HttpServletRequest)this.get()).removeAttribute(string);
   }

   public void setAttribute(String string, Object object) {
      ((HttpServletRequest)this.get()).setAttribute(string, object);
   }

   public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
      ((HttpServletRequest)this.get()).setCharacterEncoding(string);
   }

   public AsyncContext startAsync() {
      return ((HttpServletRequest)this.get()).startAsync();
   }

   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
      return ((HttpServletRequest)this.get()).startAsync(servletRequest, servletResponse);
   }

   public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
      return ((HttpServletRequest)this.get()).authenticate(httpServletResponse);
   }

   public String getAuthType() {
      return ((HttpServletRequest)this.get()).getAuthType();
   }

   public String getContextPath() {
      return ((HttpServletRequest)this.get()).getContextPath();
   }

   public Cookie[] getCookies() {
      return ((HttpServletRequest)this.get()).getCookies();
   }

   public long getDateHeader(String s) {
      return ((HttpServletRequest)this.get()).getDateHeader(s);
   }

   public String getHeader(String s) {
      return ((HttpServletRequest)this.get()).getHeader(s);
   }

   public Enumeration<String> getHeaderNames() {
      return ((HttpServletRequest)this.get()).getHeaderNames();
   }

   public Enumeration<String> getHeaders(String s) {
      return ((HttpServletRequest)this.get()).getHeaders(s);
   }

   public int getIntHeader(String s) {
      return ((HttpServletRequest)this.get()).getIntHeader(s);
   }

   public String getMethod() {
      return ((HttpServletRequest)this.get()).getMethod();
   }

   public Part getPart(String s) throws IOException, ServletException {
      return ((HttpServletRequest)this.get()).getPart(s);
   }

   public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
      return (T)((HttpServletRequest)this.get()).upgrade(httpUpgradeHandlerClass);
   }

   public Collection<Part> getParts() throws IOException, ServletException {
      return ((HttpServletRequest)this.get()).getParts();
   }

   public String getPathInfo() {
      return ((HttpServletRequest)this.get()).getPathInfo();
   }

   public String getPathTranslated() {
      return ((HttpServletRequest)this.get()).getPathTranslated();
   }

   public String getQueryString() {
      return ((HttpServletRequest)this.get()).getQueryString();
   }

   public String getRemoteUser() {
      return ((HttpServletRequest)this.get()).getRemoteUser();
   }

   public String getRequestedSessionId() {
      return ((HttpServletRequest)this.get()).getRequestedSessionId();
   }

   public String getRequestURI() {
      return ((HttpServletRequest)this.get()).getRequestURI();
   }

   public StringBuffer getRequestURL() {
      return ((HttpServletRequest)this.get()).getRequestURL();
   }

   public String getServletPath() {
      return ((HttpServletRequest)this.get()).getServletPath();
   }

   public HttpSession getSession() {
      return ((HttpServletRequest)this.get()).getSession();
   }

   public String changeSessionId() {
      return ((HttpServletRequest)this.get()).changeSessionId();
   }

   public HttpSession getSession(boolean b) {
      return ((HttpServletRequest)this.get()).getSession(b);
   }

   public Principal getUserPrincipal() {
      return ((HttpServletRequest)this.get()).getUserPrincipal();
   }

   public boolean isRequestedSessionIdFromCookie() {
      return ((HttpServletRequest)this.get()).isRequestedSessionIdFromCookie();
   }

   public boolean isRequestedSessionIdFromUrl() {
      return ((HttpServletRequest)this.get()).isRequestedSessionIdFromUrl();
   }

   public boolean isRequestedSessionIdFromURL() {
      return ((HttpServletRequest)this.get()).isRequestedSessionIdFromURL();
   }

   public boolean isRequestedSessionIdValid() {
      return ((HttpServletRequest)this.get()).isRequestedSessionIdValid();
   }

   public boolean isUserInRole(String s) {
      return ((HttpServletRequest)this.get()).isUserInRole(s);
   }

   public void login(String s, String s1) throws ServletException {
      ((HttpServletRequest)this.get()).login(s, s1);
   }

   public void logout() throws ServletException {
      ((HttpServletRequest)this.get()).logout();
   }
}
