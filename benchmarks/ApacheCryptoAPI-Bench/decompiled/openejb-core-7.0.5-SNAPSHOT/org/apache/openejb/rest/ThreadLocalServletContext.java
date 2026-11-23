package org.apache.openejb.rest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class ThreadLocalServletContext extends AbstractRestThreadLocalProxy<ServletContext> implements ServletContext {
   protected ThreadLocalServletContext() {
      super(ServletContext.class);
   }

   public String getContextPath() {
      return ((ServletContext)this.get()).getContextPath();
   }

   public ServletContext getContext(String uripath) {
      return ((ServletContext)this.get()).getContext(uripath);
   }

   public int getMajorVersion() {
      return ((ServletContext)this.get()).getMajorVersion();
   }

   public int getMinorVersion() {
      return ((ServletContext)this.get()).getMinorVersion();
   }

   public String getMimeType(String file) {
      return ((ServletContext)this.get()).getMimeType(file);
   }

   public Set<String> getResourcePaths(String path) {
      return ((ServletContext)this.get()).getResourcePaths(path);
   }

   public URL getResource(String path) throws MalformedURLException {
      return ((ServletContext)this.get()).getResource(path);
   }

   public InputStream getResourceAsStream(String path) {
      return ((ServletContext)this.get()).getResourceAsStream(path);
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      return ((ServletContext)this.get()).getRequestDispatcher(path);
   }

   public RequestDispatcher getNamedDispatcher(String name) {
      return ((ServletContext)this.get()).getNamedDispatcher(name);
   }

   public Servlet getServlet(String name) throws ServletException {
      return ((ServletContext)this.get()).getServlet(name);
   }

   public Enumeration<Servlet> getServlets() {
      return ((ServletContext)this.get()).getServlets();
   }

   public Enumeration<String> getServletNames() {
      return ((ServletContext)this.get()).getServletNames();
   }

   public void log(String msg) {
      ((ServletContext)this.get()).log(msg);
   }

   public void log(Exception exception, String msg) {
      ((ServletContext)this.get()).log(exception, msg);
   }

   public void log(String message, Throwable throwable) {
      ((ServletContext)this.get()).log(message, throwable);
   }

   public String getRealPath(String path) {
      return ((ServletContext)this.get()).getRealPath(path);
   }

   public String getServerInfo() {
      return ((ServletContext)this.get()).getServerInfo();
   }

   public String getInitParameter(String name) {
      return ((ServletContext)this.get()).getInitParameter(name);
   }

   public Enumeration<String> getInitParameterNames() {
      return ((ServletContext)this.get()).getInitParameterNames();
   }

   public boolean setInitParameter(String name, String value) {
      return ((ServletContext)this.get()).setInitParameter(name, value);
   }

   public Object getAttribute(String name) {
      return ((ServletContext)this.get()).getAttribute(name);
   }

   public Enumeration<String> getAttributeNames() {
      return ((ServletContext)this.get()).getAttributeNames();
   }

   public void setAttribute(String name, Object object) {
      ((ServletContext)this.get()).setAttribute(name, object);
   }

   public void removeAttribute(String name) {
      ((ServletContext)this.get()).removeAttribute(name);
   }

   public String getServletContextName() {
      return ((ServletContext)this.get()).getServletContextName();
   }

   public ServletRegistration.Dynamic addServlet(String servletName, String className) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addServlet(servletName, className);
   }

   public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addServlet(servletName, servlet);
   }

   public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> clazz) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addServlet(servletName, clazz);
   }

   public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
      return (T)((ServletContext)this.get()).createServlet(clazz);
   }

   public ServletRegistration getServletRegistration(String servletName) {
      return ((ServletContext)this.get()).getServletRegistration(servletName);
   }

   public Map<String, ? extends ServletRegistration> getServletRegistrations() {
      return ((ServletContext)this.get()).getServletRegistrations();
   }

   public FilterRegistration.Dynamic addFilter(String filterName, String className) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addFilter(filterName, className);
   }

   public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addFilter(filterName, filter);
   }

   public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) throws IllegalArgumentException, IllegalStateException {
      return ((ServletContext)this.get()).addFilter(filterName, filterClass);
   }

   public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
      return (T)((ServletContext)this.get()).createFilter(clazz);
   }

   public FilterRegistration getFilterRegistration(String filterName) {
      return ((ServletContext)this.get()).getFilterRegistration(filterName);
   }

   public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
      return ((ServletContext)this.get()).getFilterRegistrations();
   }

   public void addListener(Class<? extends EventListener> listenerClass) {
      ((ServletContext)this.get()).addListener(listenerClass);
   }

   public void addListener(String className) {
      ((ServletContext)this.get()).addListener(className);
   }

   public <T extends EventListener> void addListener(T t) {
      ((ServletContext)this.get()).addListener(t);
   }

   public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
      return (T)((ServletContext)this.get()).createListener(clazz);
   }

   public void declareRoles(String... roleNames) {
      ((ServletContext)this.get()).declareRoles(roleNames);
   }

   public String getVirtualServerName() {
      return ((ServletContext)this.get()).getVirtualServerName();
   }

   public SessionCookieConfig getSessionCookieConfig() {
      return ((ServletContext)this.get()).getSessionCookieConfig();
   }

   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
      ((ServletContext)this.get()).setSessionTrackingModes(sessionTrackingModes);
   }

   public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
      return ((ServletContext)this.get()).getDefaultSessionTrackingModes();
   }

   public int getEffectiveMajorVersion() throws UnsupportedOperationException {
      return ((ServletContext)this.get()).getEffectiveMajorVersion();
   }

   public int getEffectiveMinorVersion() throws UnsupportedOperationException {
      return ((ServletContext)this.get()).getEffectiveMinorVersion();
   }

   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
      return ((ServletContext)this.get()).getEffectiveSessionTrackingModes();
   }

   public ClassLoader getClassLoader() {
      return ((ServletContext)this.get()).getClassLoader();
   }

   public JspConfigDescriptor getJspConfigDescriptor() {
      return ((ServletContext)this.get()).getJspConfigDescriptor();
   }
}
