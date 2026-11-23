package org.spark_project.jetty.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.ServletSecurity;
import org.spark_project.jetty.security.IdentityService;
import org.spark_project.jetty.security.RunAsToken;
import org.spark_project.jetty.server.MultiPartCleanerListener;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Servlet Holder")
public class ServletHolder extends Holder<Servlet> implements UserIdentity.Scope, Comparable<ServletHolder> {
   private static final Logger LOG = Log.getLogger(ServletHolder.class);
   private int _initOrder;
   private boolean _initOnStartup;
   private Map<String, String> _roleMap;
   private String _forcedPath;
   private String _runAsRole;
   private RunAsToken _runAsToken;
   private IdentityService _identityService;
   private ServletRegistration.Dynamic _registration;
   private JspContainer _jspContainer;
   private transient Servlet _servlet;
   private transient Config _config;
   private transient long _unavailable;
   private transient boolean _enabled;
   private transient UnavailableException _unavailableEx;
   public static final String APACHE_SENTINEL_CLASS = "org.apache.tomcat.InstanceManager";
   public static final String JSP_GENERATED_PACKAGE_NAME = "org.spark_project.jetty.servlet.jspPackagePrefix";
   public static final Map<String, String> NO_MAPPED_ROLES = Collections.emptyMap();

   public ServletHolder() {
      this(BaseHolder.Source.EMBEDDED);
   }

   public ServletHolder(BaseHolder.Source creator) {
      super(creator);
      this._initOrder = -1;
      this._initOnStartup = false;
      this._enabled = true;
   }

   public ServletHolder(Servlet servlet) {
      this(BaseHolder.Source.EMBEDDED);
      this.setServlet(servlet);
   }

   public ServletHolder(String name, Class<? extends Servlet> servlet) {
      this(BaseHolder.Source.EMBEDDED);
      this.setName(name);
      this.setHeldClass(servlet);
   }

   public ServletHolder(String name, Servlet servlet) {
      this(BaseHolder.Source.EMBEDDED);
      this.setName(name);
      this.setServlet(servlet);
   }

   public ServletHolder(Class<? extends Servlet> servlet) {
      this(BaseHolder.Source.EMBEDDED);
      this.setHeldClass(servlet);
   }

   public UnavailableException getUnavailableException() {
      return this._unavailableEx;
   }

   public synchronized void setServlet(Servlet servlet) {
      if (servlet != null && !(servlet instanceof SingleThreadModel)) {
         this._extInstance = true;
         this._servlet = servlet;
         this.setHeldClass(servlet.getClass());
         if (this.getName() == null) {
            this.setName(servlet.getClass().getName() + "-" + super.hashCode());
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   @ManagedAttribute(
      value = "initialization order",
      readonly = true
   )
   public int getInitOrder() {
      return this._initOrder;
   }

   public void setInitOrder(int order) {
      this._initOnStartup = order >= 0;
      this._initOrder = order;
   }

   public int compareTo(ServletHolder sh) {
      if (sh == this) {
         return 0;
      } else if (sh._initOrder < this._initOrder) {
         return 1;
      } else if (sh._initOrder > this._initOrder) {
         return -1;
      } else {
         int c;
         if (this._className == null && sh._className == null) {
            c = 0;
         } else if (this._className == null) {
            c = -1;
         } else if (sh._className == null) {
            c = 1;
         } else {
            c = this._className.compareTo(sh._className);
         }

         if (c == 0) {
            c = this._name.compareTo(sh._name);
         }

         return c;
      }
   }

   public boolean equals(Object o) {
      return o instanceof ServletHolder && this.compareTo((ServletHolder)o) == 0;
   }

   public int hashCode() {
      return this._name == null ? System.identityHashCode(this) : this._name.hashCode();
   }

   public synchronized void setUserRoleLink(String name, String link) {
      if (this._roleMap == null) {
         this._roleMap = new HashMap();
      }

      this._roleMap.put(name, link);
   }

   public String getUserRoleLink(String name) {
      if (this._roleMap == null) {
         return name;
      } else {
         String link = (String)this._roleMap.get(name);
         return link == null ? name : link;
      }
   }

   @ManagedAttribute(
      value = "forced servlet path",
      readonly = true
   )
   public String getForcedPath() {
      return this._forcedPath;
   }

   public void setForcedPath(String forcedPath) {
      this._forcedPath = forcedPath;
   }

   public boolean isEnabled() {
      return this._enabled;
   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
   }

   public void doStart() throws Exception {
      this._unavailable = 0L;
      if (this._enabled) {
         if (this._forcedPath != null) {
            String precompiled = this.getClassNameForJsp(this._forcedPath);
            if (!StringUtil.isBlank(precompiled)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Checking for precompiled servlet {} for jsp {}", precompiled, this._forcedPath);
               }

               ServletHolder jsp = this.getServletHandler().getServlet(precompiled);
               if (jsp != null && jsp.getClassName() != null) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("JSP file {} for {} mapped to Servlet {}", this._forcedPath, this.getName(), jsp.getClassName());
                  }

                  this.setClassName(jsp.getClassName());
               } else {
                  jsp = this.getServletHandler().getServlet("jsp");
                  if (jsp != null) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("JSP file {} for {} mapped to JspServlet class {}", this._forcedPath, this.getName(), jsp.getClassName());
                     }

                     this.setClassName(jsp.getClassName());

                     for(Map.Entry<String, String> entry : jsp.getInitParameters().entrySet()) {
                        if (!this._initParams.containsKey(entry.getKey())) {
                           this.setInitParameter((String)entry.getKey(), (String)entry.getValue());
                        }
                     }

                     this.setInitParameter("jspFile", this._forcedPath);
                  }
               }
            } else {
               LOG.warn("Bad jsp-file {} conversion to classname in holder {}", this._forcedPath, this.getName());
            }
         }

         try {
            super.doStart();
         } catch (UnavailableException ue) {
            this.makeUnavailable(ue);
            if (this._servletHandler.isStartWithUnavailable()) {
               LOG.ignore(ue);
               return;
            }

            throw ue;
         }

         try {
            this.checkServletType();
         } catch (UnavailableException ue) {
            this.makeUnavailable(ue);
            if (this._servletHandler.isStartWithUnavailable()) {
               LOG.ignore(ue);
               return;
            }

            throw ue;
         }

         this.checkInitOnStartup();
         this._identityService = this._servletHandler.getIdentityService();
         if (this._identityService != null && this._runAsRole != null) {
            this._runAsToken = this._identityService.newRunAsToken(this._runAsRole);
         }

         this._config = new Config();
         if (this._class != null && SingleThreadModel.class.isAssignableFrom(this._class)) {
            this._servlet = new SingleThreadedWrapper();
         }

      }
   }

   public void initialize() throws Exception {
      if (!this._initialized) {
         super.initialize();
         if (this._extInstance || this._initOnStartup) {
            try {
               this.initServlet();
            } catch (Exception e) {
               if (!this._servletHandler.isStartWithUnavailable()) {
                  throw e;
               }

               LOG.ignore(e);
            }
         }
      }

      this._initialized = true;
   }

   public void doStop() throws Exception {
      Object old_run_as = null;
      if (this._servlet != null) {
         try {
            if (this._identityService != null) {
               old_run_as = this._identityService.setRunAs(this._identityService.getSystemUserIdentity(), this._runAsToken);
            }

            this.destroyInstance(this._servlet);
         } catch (Exception e) {
            LOG.warn(e);
         } finally {
            if (this._identityService != null) {
               this._identityService.unsetRunAs(old_run_as);
            }

         }
      }

      if (!this._extInstance) {
         this._servlet = null;
      }

      this._config = null;
      this._initialized = false;
   }

   public void destroyInstance(Object o) throws Exception {
      if (o != null) {
         Servlet servlet = (Servlet)o;
         this.getServletHandler().destroyServlet(servlet);
         servlet.destroy();
      }
   }

   public synchronized Servlet getServlet() throws ServletException {
      if (this._unavailable != 0L) {
         if (this._unavailable < 0L || this._unavailable > 0L && System.currentTimeMillis() < this._unavailable) {
            throw this._unavailableEx;
         }

         this._unavailable = 0L;
         this._unavailableEx = null;
      }

      if (this._servlet == null) {
         this.initServlet();
      }

      return this._servlet;
   }

   public Servlet getServletInstance() {
      return this._servlet;
   }

   public void checkServletType() throws UnavailableException {
      if (this._class == null || !Servlet.class.isAssignableFrom(this._class)) {
         throw new UnavailableException("Servlet " + this._class + " is not a javax.servlet.Servlet");
      }
   }

   public boolean isAvailable() {
      if (this.isStarted() && this._unavailable == 0L) {
         return true;
      } else {
         try {
            this.getServlet();
         } catch (Exception e) {
            LOG.ignore(e);
         }

         return this.isStarted() && this._unavailable == 0L;
      }
   }

   private void checkInitOnStartup() {
      if (this._class != null) {
         if (this._class.getAnnotation(ServletSecurity.class) != null && !this._initOnStartup) {
            this.setInitOrder(Integer.MAX_VALUE);
         }

      }
   }

   private void makeUnavailable(UnavailableException e) {
      if (this._unavailableEx != e || this._unavailable == 0L) {
         this._servletHandler.getServletContext().log("unavailable", e);
         this._unavailableEx = e;
         this._unavailable = -1L;
         if (e.isPermanent()) {
            this._unavailable = -1L;
         } else if (this._unavailableEx.getUnavailableSeconds() > 0) {
            this._unavailable = System.currentTimeMillis() + (long)(1000 * this._unavailableEx.getUnavailableSeconds());
         } else {
            this._unavailable = System.currentTimeMillis() + 5000L;
         }

      }
   }

   private void makeUnavailable(final Throwable e) {
      if (e instanceof UnavailableException) {
         this.makeUnavailable((UnavailableException)e);
      } else {
         ServletContext ctx = this._servletHandler.getServletContext();
         if (ctx == null) {
            LOG.info("unavailable", e);
         } else {
            ctx.log("unavailable", e);
         }

         this._unavailableEx = new UnavailableException(String.valueOf(e), -1) {
            {
               this.initCause(e);
            }
         };
         this._unavailable = -1L;
      }

   }

   private void initServlet() throws ServletException {
      Object old_run_as = null;

      try {
         if (this._servlet == null) {
            this._servlet = this.newInstance();
         }

         if (this._config == null) {
            this._config = new Config();
         }

         if (this._identityService != null) {
            old_run_as = this._identityService.setRunAs(this._identityService.getSystemUserIdentity(), this._runAsToken);
         }

         if (this.isJspServlet()) {
            this.initJspServlet();
            this.detectJspContainer();
         }

         this.initMultiPart();
         if (this._forcedPath != null && this._jspContainer == null) {
            this.detectJspContainer();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Servlet.init {} for {}", this._servlet, this.getName());
         }

         this._servlet.init(this._config);
      } catch (UnavailableException e) {
         this.makeUnavailable(e);
         this._servlet = null;
         this._config = null;
         throw e;
      } catch (ServletException var9) {
         this.makeUnavailable((Throwable)(var9.getCause() == null ? var9 : var9.getCause()));
         this._servlet = null;
         this._config = null;
         throw var9;
      } catch (Exception e) {
         this.makeUnavailable((Throwable)e);
         this._servlet = null;
         this._config = null;
         throw new ServletException(this.toString(), e);
      } finally {
         if (this._identityService != null) {
            this._identityService.unsetRunAs(old_run_as);
         }

      }

   }

   protected void initJspServlet() throws Exception {
      ContextHandler ch = ContextHandler.getContextHandler(this.getServletHandler().getServletContext());
      ch.setAttribute("org.apache.catalina.jsp_classpath", ch.getClassPath());
      if ("?".equals(this.getInitParameter("classpath"))) {
         String classpath = ch.getClassPath();
         if (LOG.isDebugEnabled()) {
            LOG.debug("classpath=" + classpath);
         }

         if (classpath != null) {
            this.setInitParameter("classpath", classpath);
         }
      }

      File scratch = null;
      if (this.getInitParameter("scratchdir") == null) {
         File tmp = (File)this.getServletHandler().getServletContext().getAttribute("javax.servlet.context.tempdir");
         scratch = new File(tmp, "jsp");
         this.setInitParameter("scratchdir", scratch.getAbsolutePath());
      }

      scratch = new File(this.getInitParameter("scratchdir"));
      if (!scratch.exists()) {
         scratch.mkdir();
      }

   }

   protected void initMultiPart() throws Exception {
      if (((Registration)this.getRegistration()).getMultipartConfig() != null) {
         ContextHandler ch = ContextHandler.getContextHandler(this.getServletHandler().getServletContext());
         ch.addEventListener(MultiPartCleanerListener.INSTANCE);
      }

   }

   public String getContextPath() {
      return this._config.getServletContext().getContextPath();
   }

   public Map<String, String> getRoleRefMap() {
      return this._roleMap;
   }

   @ManagedAttribute(
      value = "role to run servlet as",
      readonly = true
   )
   public String getRunAsRole() {
      return this._runAsRole;
   }

   public void setRunAsRole(String role) {
      this._runAsRole = role;
   }

   protected void prepare(Request baseRequest, ServletRequest request, ServletResponse response) throws ServletException, UnavailableException {
      this.ensureInstance();
      MultipartConfigElement mpce = ((Registration)this.getRegistration()).getMultipartConfig();
      if (mpce != null) {
         baseRequest.setAttribute("org.spark_project.jetty.multipartConfig", mpce);
      }

   }

   public synchronized Servlet ensureInstance() throws ServletException, UnavailableException {
      if (this._class == null) {
         throw new UnavailableException("Servlet Not Initialized");
      } else {
         Servlet servlet = this._servlet;
         if (!this.isStarted()) {
            throw new UnavailableException("Servlet not initialized", -1);
         } else {
            if (this._unavailable != 0L || !this._initOnStartup && servlet == null) {
               servlet = this.getServlet();
            }

            if (servlet == null) {
               throw new UnavailableException("Could not instantiate " + this._class);
            } else {
               return servlet;
            }
         }
      }
   }

   public void handle(Request baseRequest, ServletRequest request, ServletResponse response) throws ServletException, UnavailableException, IOException {
      if (this._class == null) {
         throw new UnavailableException("Servlet Not Initialized");
      } else {
         Servlet servlet = this.ensureInstance();
         boolean servlet_error = true;
         Object old_run_as = null;
         boolean suspendable = baseRequest.isAsyncSupported();

         try {
            if (this._forcedPath != null) {
               this.adaptForcedPathToJspContainer(request);
            }

            if (this._identityService != null) {
               old_run_as = this._identityService.setRunAs(baseRequest.getResolvedUserIdentity(), this._runAsToken);
            }

            if (baseRequest.isAsyncSupported() && !this.isAsyncSupported()) {
               try {
                  baseRequest.setAsyncSupported(false, this.toString());
                  servlet.service(request, response);
               } finally {
                  baseRequest.setAsyncSupported(true, (String)null);
               }
            } else {
               servlet.service(request, response);
            }

            servlet_error = false;
         } catch (UnavailableException e) {
            this.makeUnavailable(e);
            throw this._unavailableEx;
         } finally {
            if (this._identityService != null) {
               this._identityService.unsetRunAs(old_run_as);
            }

            if (servlet_error) {
               request.setAttribute("javax.servlet.error.servlet_name", this.getName());
            }

         }

      }
   }

   private boolean isJspServlet() {
      if (this._servlet == null) {
         return false;
      } else {
         Class<?> c = this._servlet.getClass();

         boolean result;
         for(result = false; c != null && !result; c = c.getSuperclass()) {
            result = this.isJspServlet(c.getName());
         }

         return result;
      }
   }

   private boolean isJspServlet(String classname) {
      return classname == null ? false : "org.apache.jasper.servlet.JspServlet".equals(classname);
   }

   private void adaptForcedPathToJspContainer(ServletRequest request) {
   }

   private void detectJspContainer() {
      if (this._jspContainer == null) {
         try {
            Loader.loadClass(Holder.class, "org.apache.tomcat.InstanceManager");
            if (LOG.isDebugEnabled()) {
               LOG.debug("Apache jasper detected");
            }

            this._jspContainer = ServletHolder.JspContainer.APACHE;
         } catch (ClassNotFoundException var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Other jasper detected");
            }

            this._jspContainer = ServletHolder.JspContainer.OTHER;
         }
      }

   }

   public String getNameOfJspClass(String jsp) {
      if (StringUtil.isBlank(jsp)) {
         return "";
      } else {
         jsp = jsp.trim();
         if ("/".equals(jsp)) {
            return "";
         } else {
            int i = jsp.lastIndexOf(47);
            if (i == jsp.length() - 1) {
               return "";
            } else {
               jsp = jsp.substring(i + 1);

               try {
                  Class<?> jspUtil = Loader.loadClass(Holder.class, "org.apache.jasper.compiler.JspUtil");
                  Method makeJavaIdentifier = jspUtil.getMethod("makeJavaIdentifier", String.class);
                  return (String)makeJavaIdentifier.invoke((Object)null, jsp);
               } catch (Exception e) {
                  String tmp = jsp.replace('.', '_');
                  if (LOG.isDebugEnabled()) {
                     LOG.warn("JspUtil.makeJavaIdentifier failed for jsp " + jsp + " using " + tmp + " instead");
                     LOG.warn(e);
                  }

                  return tmp;
               }
            }
         }
      }
   }

   public String getPackageOfJspClass(String jsp) {
      if (jsp == null) {
         return "";
      } else {
         int i = jsp.lastIndexOf(47);
         if (i <= 0) {
            return "";
         } else {
            try {
               Class<?> jspUtil = Loader.loadClass(Holder.class, "org.apache.jasper.compiler.JspUtil");
               Method makeJavaPackage = jspUtil.getMethod("makeJavaPackage", String.class);
               String p = (String)makeJavaPackage.invoke((Object)null, jsp.substring(0, i));
               return p;
            } catch (Exception e) {
               int s = 0;
               if ('/' == jsp.charAt(0)) {
                  s = 1;
               }

               String makeJavaPackage = jsp.substring(s, i);
               makeJavaPackage = makeJavaPackage.replace('/', '.').trim();
               makeJavaPackage = ".".equals(makeJavaPackage) ? "" : makeJavaPackage;
               if (LOG.isDebugEnabled()) {
                  LOG.warn("JspUtil.makeJavaPackage failed for " + jsp + " using " + makeJavaPackage + " instead");
                  LOG.warn(e);
               }

               return makeJavaPackage;
            }
         }
      }
   }

   public String getJspPackagePrefix() {
      String jspPackageName = null;
      if (this.getServletHandler() != null && this.getServletHandler().getServletContext() != null) {
         jspPackageName = this.getServletHandler().getServletContext().getInitParameter("org.spark_project.jetty.servlet.jspPackagePrefix");
      }

      if (jspPackageName == null) {
         jspPackageName = "org.apache.jsp";
      }

      return jspPackageName;
   }

   public String getClassNameForJsp(String jsp) {
      if (jsp == null) {
         return null;
      } else {
         String name = this.getNameOfJspClass(jsp);
         if (StringUtil.isBlank(name)) {
            return null;
         } else {
            StringBuffer fullName = new StringBuffer();
            this.appendPath(fullName, this.getJspPackagePrefix());
            this.appendPath(fullName, this.getPackageOfJspClass(jsp));
            this.appendPath(fullName, name);
            return fullName.toString();
         }
      }
   }

   protected void appendPath(StringBuffer path, String element) {
      if (!StringUtil.isBlank(element)) {
         if (path.length() > 0) {
            path.append(".");
         }

         path.append(element);
      }
   }

   public ServletRegistration.Dynamic getRegistration() {
      if (this._registration == null) {
         this._registration = new Registration();
      }

      return this._registration;
   }

   protected Servlet newInstance() throws ServletException, IllegalAccessException, InstantiationException {
      try {
         ServletContext ctx = this.getServletHandler().getServletContext();
         return ctx instanceof ServletContextHandler.Context ? ((ServletContextHandler.Context)ctx).createServlet(this.getHeldClass()) : (Servlet)this.getHeldClass().newInstance();
      } catch (ServletException se) {
         Throwable cause = se.getRootCause();
         if (cause instanceof InstantiationException) {
            throw (InstantiationException)cause;
         } else if (cause instanceof IllegalAccessException) {
            throw (IllegalAccessException)cause;
         } else {
            throw se;
         }
      }
   }

   public String toString() {
      return String.format("%s@%x==%s,jsp=%s,order=%d,inst=%b", this._name, this.hashCode(), this._className, this._forcedPath, this._initOrder, this._servlet != null);
   }

   public static enum JspContainer {
      APACHE,
      OTHER;
   }

   protected class Config extends Holder<Servlet>.HolderConfig implements ServletConfig {
      public String getServletName() {
         return ServletHolder.this.getName();
      }
   }

   public class Registration extends Holder<Servlet>.HolderRegistration implements ServletRegistration.Dynamic {
      protected MultipartConfigElement _multipartConfig;

      public Set<String> addMapping(String... urlPatterns) {
         ServletHolder.this.illegalStateIfContextStarted();
         Set<String> clash = null;

         for(String pattern : urlPatterns) {
            ServletMapping mapping = ServletHolder.this._servletHandler.getServletMapping(pattern);
            if (mapping != null && !mapping.isDefault()) {
               if (clash == null) {
                  clash = new HashSet();
               }

               clash.add(pattern);
            }
         }

         if (clash != null) {
            return clash;
         } else {
            ServletMapping mapping = new ServletMapping();
            mapping.setServletName(ServletHolder.this.getName());
            mapping.setPathSpecs(urlPatterns);
            ServletHolder.this._servletHandler.addServletMapping(mapping);
            return Collections.emptySet();
         }
      }

      public Collection<String> getMappings() {
         ServletMapping[] mappings = ServletHolder.this._servletHandler.getServletMappings();
         List<String> patterns = new ArrayList();
         if (mappings != null) {
            for(ServletMapping mapping : mappings) {
               if (mapping.getServletName().equals(this.getName())) {
                  String[] specs = mapping.getPathSpecs();
                  if (specs != null && specs.length > 0) {
                     patterns.addAll(Arrays.asList(specs));
                  }
               }
            }
         }

         return patterns;
      }

      public String getRunAsRole() {
         return ServletHolder.this._runAsRole;
      }

      public void setLoadOnStartup(int loadOnStartup) {
         ServletHolder.this.illegalStateIfContextStarted();
         ServletHolder.this.setInitOrder(loadOnStartup);
      }

      public int getInitOrder() {
         return ServletHolder.this.getInitOrder();
      }

      public void setMultipartConfig(MultipartConfigElement element) {
         this._multipartConfig = element;
      }

      public MultipartConfigElement getMultipartConfig() {
         return this._multipartConfig;
      }

      public void setRunAsRole(String role) {
         ServletHolder.this._runAsRole = role;
      }

      public Set<String> setServletSecurity(ServletSecurityElement securityElement) {
         return ServletHolder.this._servletHandler.setServletSecurity(this, securityElement);
      }
   }

   private class SingleThreadedWrapper implements Servlet {
      Stack<Servlet> _stack;

      private SingleThreadedWrapper() {
         this._stack = new Stack();
      }

      public void destroy() {
         synchronized(this) {
            while(this._stack.size() > 0) {
               try {
                  ((Servlet)this._stack.pop()).destroy();
               } catch (Exception e) {
                  ServletHolder.LOG.warn(e);
               }
            }

         }
      }

      public ServletConfig getServletConfig() {
         return ServletHolder.this._config;
      }

      public String getServletInfo() {
         return null;
      }

      public void init(ServletConfig config) throws ServletException {
         synchronized(this) {
            if (this._stack.size() == 0) {
               try {
                  Servlet s = ServletHolder.this.newInstance();
                  s.init(config);
                  this._stack.push(s);
               } catch (ServletException e) {
                  throw e;
               } catch (Exception e) {
                  throw new ServletException(e);
               }
            }

         }
      }

      public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
         Servlet s;
         synchronized(this) {
            if (this._stack.size() > 0) {
               s = (Servlet)this._stack.pop();
            } else {
               try {
                  s = ServletHolder.this.newInstance();
                  s.init(ServletHolder.this._config);
               } catch (ServletException e) {
                  throw e;
               } catch (Exception e) {
                  throw new ServletException(e);
               }
            }
         }

         try {
            s.service(req, res);
         } finally {
            synchronized(this) {
               this._stack.push(s);
            }
         }

      }
   }
}
