package org.spark_project.jetty.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import org.spark_project.jetty.security.ConstraintAware;
import org.spark_project.jetty.security.ConstraintMapping;
import org.spark_project.jetty.security.ConstraintSecurityHandler;
import org.spark_project.jetty.security.SecurityHandler;
import org.spark_project.jetty.server.Dispatcher;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.HandlerContainer;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ErrorHandler;
import org.spark_project.jetty.server.handler.HandlerCollection;
import org.spark_project.jetty.server.handler.HandlerWrapper;
import org.spark_project.jetty.server.handler.gzip.GzipHandler;
import org.spark_project.jetty.server.session.SessionHandler;
import org.spark_project.jetty.util.DecoratedObjectFactory;
import org.spark_project.jetty.util.DeprecationWarning;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Servlet Context Handler")
public class ServletContextHandler extends ContextHandler {
   private static final Logger LOG = Log.getLogger(ServletContextHandler.class);
   public static final int SESSIONS = 1;
   public static final int SECURITY = 2;
   public static final int GZIP = 4;
   public static final int NO_SESSIONS = 0;
   public static final int NO_SECURITY = 0;
   protected final DecoratedObjectFactory _objFactory;
   protected Class<? extends SecurityHandler> _defaultSecurityHandlerClass;
   protected SessionHandler _sessionHandler;
   protected SecurityHandler _securityHandler;
   protected ServletHandler _servletHandler;
   protected GzipHandler _gzipHandler;
   protected int _options;
   protected JspConfigDescriptor _jspConfig;

   public ServletContextHandler() {
      this((HandlerContainer)null, (SessionHandler)null, (SecurityHandler)null, (ServletHandler)null, (ErrorHandler)null);
   }

   public ServletContextHandler(int options) {
      this((HandlerContainer)null, (String)null, options);
   }

   public ServletContextHandler(HandlerContainer parent, String contextPath) {
      this(parent, contextPath, (SessionHandler)null, (SecurityHandler)null, (ServletHandler)null, (ErrorHandler)null);
   }

   public ServletContextHandler(HandlerContainer parent, String contextPath, int options) {
      this(parent, contextPath, (SessionHandler)null, (SecurityHandler)null, (ServletHandler)null, (ErrorHandler)null, options);
   }

   public ServletContextHandler(HandlerContainer parent, String contextPath, boolean sessions, boolean security) {
      this(parent, contextPath, (sessions ? 1 : 0) | (security ? 2 : 0));
   }

   public ServletContextHandler(HandlerContainer parent, SessionHandler sessionHandler, SecurityHandler securityHandler, ServletHandler servletHandler, ErrorHandler errorHandler) {
      this(parent, (String)null, sessionHandler, securityHandler, servletHandler, errorHandler);
   }

   public ServletContextHandler(HandlerContainer parent, String contextPath, SessionHandler sessionHandler, SecurityHandler securityHandler, ServletHandler servletHandler, ErrorHandler errorHandler) {
      this(parent, contextPath, sessionHandler, securityHandler, servletHandler, errorHandler, 0);
   }

   public ServletContextHandler(HandlerContainer parent, String contextPath, SessionHandler sessionHandler, SecurityHandler securityHandler, ServletHandler servletHandler, ErrorHandler errorHandler, int options) {
      super((ContextHandler.Context)null);
      this._defaultSecurityHandlerClass = ConstraintSecurityHandler.class;
      this._options = options;
      this._scontext = new Context();
      this._sessionHandler = sessionHandler;
      this._securityHandler = securityHandler;
      this._servletHandler = servletHandler;
      this._objFactory = new DecoratedObjectFactory();
      this._objFactory.addDecorator(new DeprecationWarning());
      if (contextPath != null) {
         this.setContextPath(contextPath);
      }

      if (parent instanceof HandlerWrapper) {
         ((HandlerWrapper)parent).setHandler(this);
      } else if (parent instanceof HandlerCollection) {
         ((HandlerCollection)parent).addHandler(this);
      }

      this.relinkHandlers();
      if (errorHandler != null) {
         this.setErrorHandler(errorHandler);
      }

   }

   public void setHandler(Handler handler) {
      if (handler != null) {
         LOG.warn("ServletContextHandler.setHandler should not be called directly. Use insertHandler or setSessionHandler etc.");
      }

      super.setHandler(handler);
   }

   private void doSetHandler(HandlerWrapper wrapper, Handler handler) {
      if (wrapper == this) {
         super.setHandler(handler);
      } else {
         wrapper.setHandler(handler);
      }

   }

   private void relinkHandlers() {
      HandlerWrapper handler = this;
      if (this.getSessionHandler() != null) {
         while(!(handler.getHandler() instanceof SessionHandler) && !(handler.getHandler() instanceof SecurityHandler) && !(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
            handler = (HandlerWrapper)handler.getHandler();
         }

         if (handler.getHandler() != this._sessionHandler) {
            this.doSetHandler(handler, this._sessionHandler);
         }

         handler = this._sessionHandler;
      }

      if (this.getSecurityHandler() != null) {
         while(!(handler.getHandler() instanceof SecurityHandler) && !(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
            handler = (HandlerWrapper)handler.getHandler();
         }

         if (handler.getHandler() != this._securityHandler) {
            this.doSetHandler(handler, this._securityHandler);
         }

         handler = this._securityHandler;
      }

      if (this.getGzipHandler() != null) {
         while(!(handler.getHandler() instanceof GzipHandler) && !(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
            handler = (HandlerWrapper)handler.getHandler();
         }

         if (handler.getHandler() != this._gzipHandler) {
            this.doSetHandler(handler, this._gzipHandler);
         }

         handler = this._gzipHandler;
      }

      if (this.getServletHandler() != null) {
         while(!(handler.getHandler() instanceof ServletHandler) && handler.getHandler() instanceof HandlerWrapper) {
            handler = (HandlerWrapper)handler.getHandler();
         }

         if (handler.getHandler() != this._servletHandler) {
            this.doSetHandler(handler, this._servletHandler);
         }

         handler = this._servletHandler;
      }

   }

   protected void doStart() throws Exception {
      this.getServletContext().setAttribute(DecoratedObjectFactory.ATTR, this._objFactory);
      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
      this._objFactory.clear();
   }

   public Class<? extends SecurityHandler> getDefaultSecurityHandlerClass() {
      return this._defaultSecurityHandlerClass;
   }

   public void setDefaultSecurityHandlerClass(Class<? extends SecurityHandler> defaultSecurityHandlerClass) {
      this._defaultSecurityHandlerClass = defaultSecurityHandlerClass;
   }

   protected SessionHandler newSessionHandler() {
      return new SessionHandler();
   }

   protected SecurityHandler newSecurityHandler() {
      try {
         return (SecurityHandler)this._defaultSecurityHandlerClass.newInstance();
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   protected ServletHandler newServletHandler() {
      return new ServletHandler();
   }

   protected void startContext() throws Exception {
      ServletContainerInitializerCaller sciBean = (ServletContainerInitializerCaller)this.getBean(ServletContainerInitializerCaller.class);
      if (sciBean != null) {
         sciBean.start();
      }

      if (this._servletHandler != null && this._servletHandler.getListeners() != null) {
         for(ListenerHolder holder : this._servletHandler.getListeners()) {
            this._objFactory.decorate(holder.getListener());
         }
      }

      super.startContext();
      if (this._servletHandler != null) {
         this._servletHandler.initialize();
      }

   }

   protected void stopContext() throws Exception {
      super.stopContext();
   }

   @ManagedAttribute(
      value = "context security handler",
      readonly = true
   )
   public SecurityHandler getSecurityHandler() {
      if (this._securityHandler == null && (this._options & 2) != 0 && !this.isStarted()) {
         this._securityHandler = this.newSecurityHandler();
      }

      return this._securityHandler;
   }

   @ManagedAttribute(
      value = "context servlet handler",
      readonly = true
   )
   public ServletHandler getServletHandler() {
      if (this._servletHandler == null && !this.isStarted()) {
         this._servletHandler = this.newServletHandler();
      }

      return this._servletHandler;
   }

   @ManagedAttribute(
      value = "context session handler",
      readonly = true
   )
   public SessionHandler getSessionHandler() {
      if (this._sessionHandler == null && (this._options & 1) != 0 && !this.isStarted()) {
         this._sessionHandler = this.newSessionHandler();
      }

      return this._sessionHandler;
   }

   @ManagedAttribute(
      value = "context gzip handler",
      readonly = true
   )
   public GzipHandler getGzipHandler() {
      if (this._gzipHandler == null && (this._options & 4) != 0 && !this.isStarted()) {
         this._gzipHandler = new GzipHandler();
      }

      return this._gzipHandler;
   }

   public ServletHolder addServlet(String className, String pathSpec) {
      return this.getServletHandler().addServletWithMapping(className, pathSpec);
   }

   public ServletHolder addServlet(Class<? extends Servlet> servlet, String pathSpec) {
      return this.getServletHandler().addServletWithMapping(servlet, pathSpec);
   }

   public void addServlet(ServletHolder servlet, String pathSpec) {
      this.getServletHandler().addServletWithMapping(servlet, pathSpec);
   }

   public void addFilter(FilterHolder holder, String pathSpec, EnumSet<DispatcherType> dispatches) {
      this.getServletHandler().addFilterWithMapping(holder, pathSpec, dispatches);
   }

   public FilterHolder addFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches) {
      return this.getServletHandler().addFilterWithMapping(filterClass, pathSpec, dispatches);
   }

   public FilterHolder addFilter(String filterClass, String pathSpec, EnumSet<DispatcherType> dispatches) {
      return this.getServletHandler().addFilterWithMapping(filterClass, pathSpec, dispatches);
   }

   protected ServletRegistration.Dynamic dynamicHolderAdded(ServletHolder holder) {
      return holder.getRegistration();
   }

   protected void addRoles(String... roleNames) {
      if (this._securityHandler != null && this._securityHandler instanceof ConstraintAware) {
         HashSet<String> union = new HashSet();
         Set<String> existing = ((ConstraintAware)this._securityHandler).getRoles();
         if (existing != null) {
            union.addAll(existing);
         }

         union.addAll(Arrays.asList(roleNames));
         ((ConstraintSecurityHandler)this._securityHandler).setRoles(union);
      }

   }

   public Set<String> setServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
      Collection<String> pathSpecs = registration.getMappings();
      if (pathSpecs != null) {
         for(String pathSpec : pathSpecs) {
            for(ConstraintMapping m : ConstraintSecurityHandler.createConstraintsWithMappingsForPath(registration.getName(), pathSpec, servletSecurityElement)) {
               ((ConstraintAware)this.getSecurityHandler()).addConstraintMapping(m);
            }
         }
      }

      return Collections.emptySet();
   }

   public void callContextInitialized(ServletContextListener l, ServletContextEvent e) {
      try {
         if (this.isProgrammaticListener(l)) {
            this.getServletContext().setEnabled(false);
         }

         super.callContextInitialized(l, e);
      } finally {
         this.getServletContext().setEnabled(true);
      }

   }

   public void callContextDestroyed(ServletContextListener l, ServletContextEvent e) {
      super.callContextDestroyed(l, e);
   }

   private boolean replaceHandler(Handler handler, Handler replace) {
      HandlerWrapper wrapper;
      for(wrapper = this; wrapper.getHandler() != handler; wrapper = (HandlerWrapper)wrapper.getHandler()) {
         if (!(wrapper.getHandler() instanceof HandlerWrapper)) {
            return false;
         }
      }

      this.doSetHandler(wrapper, replace);
      return true;
   }

   public void setSessionHandler(SessionHandler sessionHandler) {
      if (this.isStarted()) {
         throw new IllegalStateException("STARTED");
      } else {
         Handler next = null;
         if (this._sessionHandler != null) {
            next = this._sessionHandler.getHandler();
            this._sessionHandler.setHandler((Handler)null);
            this.replaceHandler(this._sessionHandler, sessionHandler);
         }

         this._sessionHandler = sessionHandler;
         if (next != null && this._sessionHandler.getHandler() == null) {
            this._sessionHandler.setHandler(next);
         }

         this.relinkHandlers();
      }
   }

   public void setSecurityHandler(SecurityHandler securityHandler) {
      if (this.isStarted()) {
         throw new IllegalStateException("STARTED");
      } else {
         Handler next = null;
         if (this._securityHandler != null) {
            next = this._securityHandler.getHandler();
            this._securityHandler.setHandler((Handler)null);
            this.replaceHandler(this._securityHandler, securityHandler);
         }

         this._securityHandler = securityHandler;
         if (next != null && this._securityHandler.getHandler() == null) {
            this._securityHandler.setHandler(next);
         }

         this.relinkHandlers();
      }
   }

   public void setGzipHandler(GzipHandler gzipHandler) {
      if (this.isStarted()) {
         throw new IllegalStateException("STARTED");
      } else {
         Handler next = null;
         if (this._gzipHandler != null) {
            next = this._gzipHandler.getHandler();
            this._gzipHandler.setHandler((Handler)null);
            this.replaceHandler(this._gzipHandler, gzipHandler);
         }

         this._gzipHandler = gzipHandler;
         if (next != null && this._gzipHandler.getHandler() == null) {
            this._gzipHandler.setHandler(next);
         }

         this.relinkHandlers();
      }
   }

   public void setServletHandler(ServletHandler servletHandler) {
      if (this.isStarted()) {
         throw new IllegalStateException("STARTED");
      } else {
         Handler next = null;
         if (this._servletHandler != null) {
            next = this._servletHandler.getHandler();
            this._servletHandler.setHandler((Handler)null);
            this.replaceHandler(this._servletHandler, servletHandler);
         }

         this._servletHandler = servletHandler;
         if (next != null && this._servletHandler.getHandler() == null) {
            this._servletHandler.setHandler(next);
         }

         this.relinkHandlers();
      }
   }

   public void insertHandler(HandlerWrapper handler) {
      if (handler instanceof SessionHandler) {
         this.setSessionHandler((SessionHandler)handler);
      } else if (handler instanceof SecurityHandler) {
         this.setSecurityHandler((SecurityHandler)handler);
      } else if (handler instanceof GzipHandler) {
         this.setGzipHandler((GzipHandler)handler);
      } else if (handler instanceof ServletHandler) {
         this.setServletHandler((ServletHandler)handler);
      } else {
         HandlerWrapper tail;
         for(tail = handler; tail.getHandler() instanceof HandlerWrapper; tail = (HandlerWrapper)tail.getHandler()) {
         }

         if (tail.getHandler() != null) {
            throw new IllegalArgumentException("bad tail of inserted wrapper chain");
         }

         HandlerWrapper h;
         HandlerWrapper wrapper;
         for(h = this; h.getHandler() instanceof HandlerWrapper; h = wrapper) {
            wrapper = (HandlerWrapper)h.getHandler();
            if (wrapper instanceof SessionHandler || wrapper instanceof SecurityHandler || wrapper instanceof ServletHandler) {
               break;
            }
         }

         wrapper = h.getHandler();
         this.doSetHandler(h, handler);
         this.doSetHandler(tail, wrapper);
      }

      this.relinkHandlers();
   }

   public DecoratedObjectFactory getObjectFactory() {
      return this._objFactory;
   }

   /** @deprecated */
   @Deprecated
   public List<Decorator> getDecorators() {
      List<Decorator> ret = new ArrayList();

      for(org.spark_project.jetty.util.Decorator decorator : this._objFactory) {
         ret.add(new LegacyDecorator(decorator));
      }

      return Collections.unmodifiableList(ret);
   }

   /** @deprecated */
   @Deprecated
   public void setDecorators(List<Decorator> decorators) {
      this._objFactory.setDecorators(decorators);
   }

   /** @deprecated */
   @Deprecated
   public void addDecorator(Decorator decorator) {
      this._objFactory.addDecorator(decorator);
   }

   void destroyServlet(Servlet servlet) {
      this._objFactory.destroy(servlet);
   }

   void destroyFilter(Filter filter) {
      this._objFactory.destroy(filter);
   }

   public static class JspPropertyGroup implements JspPropertyGroupDescriptor {
      private List<String> _urlPatterns = new ArrayList();
      private String _elIgnored;
      private String _pageEncoding;
      private String _scriptingInvalid;
      private String _isXml;
      private List<String> _includePreludes = new ArrayList();
      private List<String> _includeCodas = new ArrayList();
      private String _deferredSyntaxAllowedAsLiteral;
      private String _trimDirectiveWhitespaces;
      private String _defaultContentType;
      private String _buffer;
      private String _errorOnUndeclaredNamespace;

      public Collection<String> getUrlPatterns() {
         return new ArrayList(this._urlPatterns);
      }

      public void addUrlPattern(String s) {
         if (!this._urlPatterns.contains(s)) {
            this._urlPatterns.add(s);
         }

      }

      public String getElIgnored() {
         return this._elIgnored;
      }

      public void setElIgnored(String s) {
         this._elIgnored = s;
      }

      public String getPageEncoding() {
         return this._pageEncoding;
      }

      public void setPageEncoding(String pageEncoding) {
         this._pageEncoding = pageEncoding;
      }

      public void setScriptingInvalid(String scriptingInvalid) {
         this._scriptingInvalid = scriptingInvalid;
      }

      public void setIsXml(String isXml) {
         this._isXml = isXml;
      }

      public void setDeferredSyntaxAllowedAsLiteral(String deferredSyntaxAllowedAsLiteral) {
         this._deferredSyntaxAllowedAsLiteral = deferredSyntaxAllowedAsLiteral;
      }

      public void setTrimDirectiveWhitespaces(String trimDirectiveWhitespaces) {
         this._trimDirectiveWhitespaces = trimDirectiveWhitespaces;
      }

      public void setDefaultContentType(String defaultContentType) {
         this._defaultContentType = defaultContentType;
      }

      public void setBuffer(String buffer) {
         this._buffer = buffer;
      }

      public void setErrorOnUndeclaredNamespace(String errorOnUndeclaredNamespace) {
         this._errorOnUndeclaredNamespace = errorOnUndeclaredNamespace;
      }

      public String getScriptingInvalid() {
         return this._scriptingInvalid;
      }

      public String getIsXml() {
         return this._isXml;
      }

      public Collection<String> getIncludePreludes() {
         return new ArrayList(this._includePreludes);
      }

      public void addIncludePrelude(String prelude) {
         if (!this._includePreludes.contains(prelude)) {
            this._includePreludes.add(prelude);
         }

      }

      public Collection<String> getIncludeCodas() {
         return new ArrayList(this._includeCodas);
      }

      public void addIncludeCoda(String coda) {
         if (!this._includeCodas.contains(coda)) {
            this._includeCodas.add(coda);
         }

      }

      public String getDeferredSyntaxAllowedAsLiteral() {
         return this._deferredSyntaxAllowedAsLiteral;
      }

      public String getTrimDirectiveWhitespaces() {
         return this._trimDirectiveWhitespaces;
      }

      public String getDefaultContentType() {
         return this._defaultContentType;
      }

      public String getBuffer() {
         return this._buffer;
      }

      public String getErrorOnUndeclaredNamespace() {
         return this._errorOnUndeclaredNamespace;
      }

      public String toString() {
         StringBuffer sb = new StringBuffer();
         sb.append("JspPropertyGroupDescriptor:");
         sb.append(" el-ignored=" + this._elIgnored);
         sb.append(" is-xml=" + this._isXml);
         sb.append(" page-encoding=" + this._pageEncoding);
         sb.append(" scripting-invalid=" + this._scriptingInvalid);
         sb.append(" deferred-syntax-allowed-as-literal=" + this._deferredSyntaxAllowedAsLiteral);
         sb.append(" trim-directive-whitespaces" + this._trimDirectiveWhitespaces);
         sb.append(" default-content-type=" + this._defaultContentType);
         sb.append(" buffer=" + this._buffer);
         sb.append(" error-on-undeclared-namespace=" + this._errorOnUndeclaredNamespace);

         for(String prelude : this._includePreludes) {
            sb.append(" include-prelude=" + prelude);
         }

         for(String coda : this._includeCodas) {
            sb.append(" include-coda=" + coda);
         }

         return sb.toString();
      }
   }

   public static class TagLib implements TaglibDescriptor {
      private String _uri;
      private String _location;

      public String getTaglibURI() {
         return this._uri;
      }

      public void setTaglibURI(String uri) {
         this._uri = uri;
      }

      public String getTaglibLocation() {
         return this._location;
      }

      public void setTaglibLocation(String location) {
         this._location = location;
      }

      public String toString() {
         return "TagLibDescriptor: taglib-uri=" + this._uri + " location=" + this._location;
      }
   }

   public static class JspConfig implements JspConfigDescriptor {
      private List<TaglibDescriptor> _taglibs = new ArrayList();
      private List<JspPropertyGroupDescriptor> _jspPropertyGroups = new ArrayList();

      public Collection<TaglibDescriptor> getTaglibs() {
         return new ArrayList(this._taglibs);
      }

      public void addTaglibDescriptor(TaglibDescriptor d) {
         this._taglibs.add(d);
      }

      public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
         return new ArrayList(this._jspPropertyGroups);
      }

      public void addJspPropertyGroup(JspPropertyGroupDescriptor g) {
         this._jspPropertyGroups.add(g);
      }

      public String toString() {
         StringBuffer sb = new StringBuffer();
         sb.append("JspConfigDescriptor: \n");

         for(TaglibDescriptor taglib : this._taglibs) {
            sb.append(taglib + "\n");
         }

         for(JspPropertyGroupDescriptor jpg : this._jspPropertyGroups) {
            sb.append(jpg + "\n");
         }

         return sb.toString();
      }
   }

   public class Context extends ContextHandler.Context {
      public RequestDispatcher getNamedDispatcher(String name) {
         ContextHandler context = ServletContextHandler.this;
         if (ServletContextHandler.this._servletHandler == null) {
            return null;
         } else {
            ServletHolder holder = ServletContextHandler.this._servletHandler.getServlet(name);
            return holder != null && holder.isEnabled() ? new Dispatcher(context, name) : null;
         }
      }

      public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
         if (ServletContextHandler.this.isStarted()) {
            throw new IllegalStateException();
         } else if (filterName != null && !"".equals(filterName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               FilterHolder holder = handler.getFilter(filterName);
               if (holder == null) {
                  holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(filterName);
                  holder.setHeldClass(filterClass);
                  handler.addFilter(holder);
                  return holder.getRegistration();
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setHeldClass(filterClass);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing filter name");
         }
      }

      public FilterRegistration.Dynamic addFilter(String filterName, String className) {
         if (ServletContextHandler.this.isStarted()) {
            throw new IllegalStateException();
         } else if (filterName != null && !"".equals(filterName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               FilterHolder holder = handler.getFilter(filterName);
               if (holder == null) {
                  holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(filterName);
                  holder.setClassName(className);
                  handler.addFilter(holder);
                  return holder.getRegistration();
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setClassName(className);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing filter name");
         }
      }

      public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
         if (ServletContextHandler.this.isStarted()) {
            throw new IllegalStateException();
         } else if (filterName != null && !"".equals(filterName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               FilterHolder holder = handler.getFilter(filterName);
               if (holder == null) {
                  holder = handler.newFilterHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(filterName);
                  holder.setFilter(filter);
                  handler.addFilter(holder);
                  return holder.getRegistration();
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setFilter(filter);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing filter name");
         }
      }

      public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (servletName != null && !"".equals(servletName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               ServletHolder holder = handler.getServlet(servletName);
               if (holder == null) {
                  holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(servletName);
                  holder.setHeldClass(servletClass);
                  handler.addServlet(holder);
                  return ServletContextHandler.this.dynamicHolderAdded(holder);
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setHeldClass(servletClass);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing servlet name");
         }
      }

      public ServletRegistration.Dynamic addServlet(String servletName, String className) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (servletName != null && !"".equals(servletName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               ServletHolder holder = handler.getServlet(servletName);
               if (holder == null) {
                  holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(servletName);
                  holder.setClassName(className);
                  handler.addServlet(holder);
                  return ServletContextHandler.this.dynamicHolderAdded(holder);
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setClassName(className);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing servlet name");
         }
      }

      public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (servletName != null && !"".equals(servletName.trim())) {
            if (!this._enabled) {
               throw new UnsupportedOperationException();
            } else {
               ServletHandler handler = ServletContextHandler.this.getServletHandler();
               ServletHolder holder = handler.getServlet(servletName);
               if (holder == null) {
                  holder = handler.newServletHolder(BaseHolder.Source.JAVAX_API);
                  holder.setName(servletName);
                  holder.setServlet(servlet);
                  handler.addServlet(holder);
                  return ServletContextHandler.this.dynamicHolderAdded(holder);
               } else if (holder.getClassName() == null && holder.getHeldClass() == null) {
                  holder.setServlet(servlet);
                  return holder.getRegistration();
               } else {
                  return null;
               }
            }
         } else {
            throw new IllegalStateException("Missing servlet name");
         }
      }

      public boolean setInitParameter(String name, String value) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            return super.setInitParameter(name, value);
         }
      }

      public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
         try {
            T f = (T)(this.createInstance(c));
            f = (T)(ServletContextHandler.this._objFactory.decorate(f));
            return f;
         } catch (Exception e) {
            throw new ServletException(e);
         }
      }

      public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
         try {
            T s = (T)(this.createInstance(c));
            s = (T)(ServletContextHandler.this._objFactory.decorate(s));
            return s;
         } catch (Exception e) {
            throw new ServletException(e);
         }
      }

      public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
         return ServletContextHandler.this._sessionHandler != null ? ServletContextHandler.this._sessionHandler.getSessionManager().getDefaultSessionTrackingModes() : null;
      }

      public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
         return ServletContextHandler.this._sessionHandler != null ? ServletContextHandler.this._sessionHandler.getSessionManager().getEffectiveSessionTrackingModes() : null;
      }

      public FilterRegistration getFilterRegistration(String filterName) {
         if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            FilterHolder holder = ServletContextHandler.this.getServletHandler().getFilter(filterName);
            return holder == null ? null : holder.getRegistration();
         }
      }

      public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
         if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            HashMap<String, FilterRegistration> registrations = new HashMap();
            ServletHandler handler = ServletContextHandler.this.getServletHandler();
            FilterHolder[] holders = handler.getFilters();
            if (holders != null) {
               for(FilterHolder holder : holders) {
                  registrations.put(holder.getName(), holder.getRegistration());
               }
            }

            return registrations;
         }
      }

      public ServletRegistration getServletRegistration(String servletName) {
         if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            ServletHolder holder = ServletContextHandler.this.getServletHandler().getServlet(servletName);
            return holder == null ? null : holder.getRegistration();
         }
      }

      public Map<String, ? extends ServletRegistration> getServletRegistrations() {
         if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            HashMap<String, ServletRegistration> registrations = new HashMap();
            ServletHandler handler = ServletContextHandler.this.getServletHandler();
            ServletHolder[] holders = handler.getServlets();
            if (holders != null) {
               for(ServletHolder holder : holders) {
                  registrations.put(holder.getName(), holder.getRegistration());
               }
            }

            return registrations;
         }
      }

      public SessionCookieConfig getSessionCookieConfig() {
         if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            return ServletContextHandler.this._sessionHandler != null ? ServletContextHandler.this._sessionHandler.getSessionManager().getSessionCookieConfig() : null;
         }
      }

      public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            if (ServletContextHandler.this._sessionHandler != null) {
               ServletContextHandler.this._sessionHandler.getSessionManager().setSessionTrackingModes(sessionTrackingModes);
            }

         }
      }

      public void addListener(String className) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            super.addListener(className);
         }
      }

      public <T extends EventListener> void addListener(T t) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            super.addListener(t);
            ListenerHolder holder = ServletContextHandler.this.getServletHandler().newListenerHolder(BaseHolder.Source.JAVAX_API);
            holder.setListener(t);
            ServletContextHandler.this.getServletHandler().addListener(holder);
         }
      }

      public void addListener(Class<? extends EventListener> listenerClass) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            super.addListener(listenerClass);
         }
      }

      public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
         try {
            T l = (T)(this.createInstance(clazz));
            l = (T)(ServletContextHandler.this._objFactory.decorate(l));
            return l;
         } catch (Exception e) {
            throw new ServletException(e);
         }
      }

      public JspConfigDescriptor getJspConfigDescriptor() {
         return ServletContextHandler.this._jspConfig;
      }

      public void setJspConfigDescriptor(JspConfigDescriptor d) {
         ServletContextHandler.this._jspConfig = d;
      }

      public void declareRoles(String... roleNames) {
         if (!ServletContextHandler.this.isStarting()) {
            throw new IllegalStateException();
         } else if (!this._enabled) {
            throw new UnsupportedOperationException();
         } else {
            ServletContextHandler.this.addRoles(roleNames);
         }
      }
   }

   private static class LegacyDecorator implements Decorator {
      private org.spark_project.jetty.util.Decorator decorator;

      public LegacyDecorator(org.spark_project.jetty.util.Decorator decorator) {
         this.decorator = decorator;
      }

      public <T> T decorate(T o) {
         return (T)this.decorator.decorate(o);
      }

      public void destroy(Object o) {
         this.decorator.destroy(o);
      }
   }

   /** @deprecated */
   @Deprecated
   public interface Decorator extends org.spark_project.jetty.util.Decorator {
   }

   public interface ServletContainerInitializerCaller extends LifeCycle {
   }
}
