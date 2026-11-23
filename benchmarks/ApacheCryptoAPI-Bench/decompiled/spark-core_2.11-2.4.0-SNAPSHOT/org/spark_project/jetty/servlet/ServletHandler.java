package org.spark_project.jetty.servlet;

import [Lorg.spark_project.jetty.servlet.FilterHolder;;
import [Lorg.spark_project.jetty.servlet.ServletHolder;;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletSecurityElement;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.http.HttpHeaderValue;
import org.spark_project.jetty.http.PathMap;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.io.RuntimeIOException;
import org.spark_project.jetty.security.IdentityService;
import org.spark_project.jetty.security.SecurityHandler;
import org.spark_project.jetty.server.QuietServletException;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.ServletRequestHttpWrapper;
import org.spark_project.jetty.server.ServletResponseHttpWrapper;
import org.spark_project.jetty.server.UserIdentity;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.server.handler.ScopedHandler;
import org.spark_project.jetty.util.ArrayUtil;
import org.spark_project.jetty.util.LazyList;
import org.spark_project.jetty.util.MultiException;
import org.spark_project.jetty.util.MultiMap;
import org.spark_project.jetty.util.URIUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.LifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Servlet Handler")
public class ServletHandler extends ScopedHandler {
   private static final Logger LOG = Log.getLogger(ServletHandler.class);
   public static final String __DEFAULT_SERVLET = "default";
   private ServletContextHandler _contextHandler;
   private ServletContext _servletContext;
   private FilterHolder[] _filters = new FilterHolder[0];
   private FilterMapping[] _filterMappings;
   private int _matchBeforeIndex = -1;
   private int _matchAfterIndex = -1;
   private boolean _filterChainsCached = true;
   private int _maxFilterChainsCacheSize = 512;
   private boolean _startWithUnavailable = false;
   private boolean _ensureDefaultServlet = true;
   private IdentityService _identityService;
   private boolean _allowDuplicateMappings = false;
   private ServletHolder[] _servlets = new ServletHolder[0];
   private ServletMapping[] _servletMappings;
   private final Map<String, FilterHolder> _filterNameMap = new HashMap();
   private List<FilterMapping> _filterPathMappings;
   private MultiMap<FilterMapping> _filterNameMappings;
   private final Map<String, ServletHolder> _servletNameMap = new HashMap();
   private PathMap<ServletHolder> _servletPathMap;
   private ListenerHolder[] _listeners = new ListenerHolder[0];
   protected final ConcurrentMap<String, FilterChain>[] _chainCache = new ConcurrentMap[31];
   protected final Queue<String>[] _chainLRU = new Queue[31];

   protected synchronized void doStart() throws Exception {
      ContextHandler.Context context = ContextHandler.getCurrentContext();
      this._servletContext = (ServletContext)(context == null ? new ContextHandler.StaticContext() : context);
      this._contextHandler = (ServletContextHandler)(context == null ? null : context.getContextHandler());
      if (this._contextHandler != null) {
         SecurityHandler security_handler = (SecurityHandler)this._contextHandler.getChildHandlerByClass(SecurityHandler.class);
         if (security_handler != null) {
            this._identityService = security_handler.getIdentityService();
         }
      }

      this.updateNameMappings();
      this.updateMappings();
      if (this.getServletMapping("/") == null && this._ensureDefaultServlet) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Adding Default404Servlet to {}", this);
         }

         this.addServletWithMapping(Default404Servlet.class, "/");
         this.updateMappings();
         this.getServletMapping("/").setDefault(true);
      }

      if (this._filterChainsCached) {
         this._chainCache[1] = new ConcurrentHashMap();
         this._chainCache[2] = new ConcurrentHashMap();
         this._chainCache[4] = new ConcurrentHashMap();
         this._chainCache[8] = new ConcurrentHashMap();
         this._chainCache[16] = new ConcurrentHashMap();
         this._chainLRU[1] = new ConcurrentLinkedQueue();
         this._chainLRU[2] = new ConcurrentLinkedQueue();
         this._chainLRU[4] = new ConcurrentLinkedQueue();
         this._chainLRU[8] = new ConcurrentLinkedQueue();
         this._chainLRU[16] = new ConcurrentLinkedQueue();
      }

      if (this._contextHandler == null) {
         this.initialize();
      }

      super.doStart();
   }

   public boolean isEnsureDefaultServlet() {
      return this._ensureDefaultServlet;
   }

   public void setEnsureDefaultServlet(boolean ensureDefaultServlet) {
      this._ensureDefaultServlet = ensureDefaultServlet;
   }

   protected void start(LifeCycle l) throws Exception {
      if (!(l instanceof Holder)) {
         super.start(l);
      }

   }

   protected synchronized void doStop() throws Exception {
      super.doStop();
      List<FilterHolder> filterHolders = new ArrayList();
      List<FilterMapping> filterMappings = ArrayUtil.<FilterMapping>asMutableList(this._filterMappings);
      if (this._filters != null) {
         int i = this._filters.length;

         while(i-- > 0) {
            try {
               this._filters[i].stop();
            } catch (Exception e) {
               LOG.warn("EXCEPTION ", (Throwable)e);
            }

            if (this._filters[i].getSource() != BaseHolder.Source.EMBEDDED) {
               this._filterNameMap.remove(this._filters[i].getName());
               ListIterator<FilterMapping> fmitor = filterMappings.listIterator();

               while(fmitor.hasNext()) {
                  FilterMapping fm = (FilterMapping)fmitor.next();
                  if (fm.getFilterName().equals(this._filters[i].getName())) {
                     fmitor.remove();
                  }
               }
            } else {
               filterHolders.add(this._filters[i]);
            }
         }
      }

      FilterHolder[] fhs = (FilterHolder[])LazyList.toArray(filterHolders, FilterHolder.class);
      this.updateBeans(this._filters, fhs);
      this._filters = fhs;
      FilterMapping[] fms = (FilterMapping[])LazyList.toArray(filterMappings, FilterMapping.class);
      this.updateBeans(this._filterMappings, fms);
      this._filterMappings = fms;
      this._matchAfterIndex = this._filterMappings != null && this._filterMappings.length != 0 ? this._filterMappings.length - 1 : -1;
      this._matchBeforeIndex = -1;
      List<ServletHolder> servletHolders = new ArrayList();
      List<ServletMapping> servletMappings = ArrayUtil.<ServletMapping>asMutableList(this._servletMappings);
      if (this._servlets != null) {
         int i = this._servlets.length;

         while(i-- > 0) {
            try {
               this._servlets[i].stop();
            } catch (Exception e) {
               LOG.warn("EXCEPTION ", (Throwable)e);
            }

            if (this._servlets[i].getSource() != BaseHolder.Source.EMBEDDED) {
               this._servletNameMap.remove(this._servlets[i].getName());
               ListIterator<ServletMapping> smitor = servletMappings.listIterator();

               while(smitor.hasNext()) {
                  ServletMapping sm = (ServletMapping)smitor.next();
                  if (sm.getServletName().equals(this._servlets[i].getName())) {
                     smitor.remove();
                  }
               }
            } else {
               servletHolders.add(this._servlets[i]);
            }
         }
      }

      ServletHolder[] shs = (ServletHolder[])LazyList.toArray(servletHolders, ServletHolder.class);
      this.updateBeans(this._servlets, shs);
      this._servlets = shs;
      ServletMapping[] sms = (ServletMapping[])LazyList.toArray(servletMappings, ServletMapping.class);
      this.updateBeans(this._servletMappings, sms);
      this._servletMappings = sms;
      List<ListenerHolder> listenerHolders = new ArrayList();
      if (this._listeners != null) {
         int i = this._listeners.length;

         while(i-- > 0) {
            try {
               this._listeners[i].stop();
            } catch (Exception e) {
               LOG.warn("EXCEPTION ", (Throwable)e);
            }

            if (this._listeners[i].getSource() == BaseHolder.Source.EMBEDDED) {
               listenerHolders.add(this._listeners[i]);
            }
         }
      }

      ListenerHolder[] listeners = (ListenerHolder[])LazyList.toArray(listenerHolders, ListenerHolder.class);
      this.updateBeans(this._listeners, listeners);
      this._listeners = listeners;
      this._filterPathMappings = null;
      this._filterNameMappings = null;
      this._servletPathMap = null;
   }

   protected IdentityService getIdentityService() {
      return this._identityService;
   }

   public Object getContextLog() {
      return null;
   }

   @ManagedAttribute(
      value = "filters",
      readonly = true
   )
   public FilterMapping[] getFilterMappings() {
      return this._filterMappings;
   }

   @ManagedAttribute(
      value = "filters",
      readonly = true
   )
   public FilterHolder[] getFilters() {
      return this._filters;
   }

   public PathMap.MappedEntry<ServletHolder> getHolderEntry(String pathInContext) {
      return this._servletPathMap == null ? null : this._servletPathMap.getMatch(pathInContext);
   }

   public ServletContext getServletContext() {
      return this._servletContext;
   }

   @ManagedAttribute(
      value = "mappings of servlets",
      readonly = true
   )
   public ServletMapping[] getServletMappings() {
      return this._servletMappings;
   }

   public ServletMapping getServletMapping(String pathSpec) {
      if (pathSpec != null && this._servletMappings != null) {
         ServletMapping mapping = null;

         for(int i = 0; i < this._servletMappings.length && mapping == null; ++i) {
            ServletMapping m = this._servletMappings[i];
            if (m.getPathSpecs() != null) {
               for(String p : m.getPathSpecs()) {
                  if (pathSpec.equals(p)) {
                     mapping = m;
                     break;
                  }
               }
            }
         }

         return mapping;
      } else {
         return null;
      }
   }

   @ManagedAttribute(
      value = "servlets",
      readonly = true
   )
   public ServletHolder[] getServlets() {
      return this._servlets;
   }

   public ServletHolder getServlet(String name) {
      return (ServletHolder)this._servletNameMap.get(name);
   }

   public void doScope(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      String old_servlet_path = baseRequest.getServletPath();
      String old_path_info = baseRequest.getPathInfo();
      DispatcherType type = baseRequest.getDispatcherType();
      ServletHolder servlet_holder = null;
      UserIdentity.Scope old_scope = null;
      if (target.startsWith("/")) {
         PathMap.MappedEntry<ServletHolder> entry = this.getHolderEntry(target);
         if (entry != null) {
            servlet_holder = entry.getValue();
            String servlet_path_spec = entry.getKey();
            String servlet_path = entry.getMapped() != null ? entry.getMapped() : PathMap.pathMatch(servlet_path_spec, target);
            String path_info = PathMap.pathInfo(servlet_path_spec, target);
            if (DispatcherType.INCLUDE.equals(type)) {
               baseRequest.setAttribute("javax.servlet.include.servlet_path", servlet_path);
               baseRequest.setAttribute("javax.servlet.include.path_info", path_info);
            } else {
               baseRequest.setServletPath(servlet_path);
               baseRequest.setPathInfo(path_info);
            }
         }
      } else {
         servlet_holder = (ServletHolder)this._servletNameMap.get(target);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("servlet {}|{}|{} -> {}", baseRequest.getContextPath(), baseRequest.getServletPath(), baseRequest.getPathInfo(), servlet_holder);
      }

      try {
         old_scope = baseRequest.getUserIdentityScope();
         baseRequest.setUserIdentityScope(servlet_holder);
         if (this.never()) {
            this.nextScope(target, baseRequest, request, response);
         } else if (this._nextScope != null) {
            this._nextScope.doScope(target, baseRequest, request, response);
         } else if (this._outerScope != null) {
            this._outerScope.doHandle(target, baseRequest, request, response);
         } else {
            this.doHandle(target, baseRequest, request, response);
         }
      } finally {
         if (old_scope != null) {
            baseRequest.setUserIdentityScope(old_scope);
         }

         if (!DispatcherType.INCLUDE.equals(type)) {
            baseRequest.setServletPath(old_servlet_path);
            baseRequest.setPathInfo(old_path_info);
         }

      }

   }

   public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      DispatcherType type = baseRequest.getDispatcherType();
      ServletHolder servlet_holder = (ServletHolder)baseRequest.getUserIdentityScope();
      FilterChain chain = null;
      if (target.startsWith("/")) {
         if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
            chain = this.getFilterChain(baseRequest, target, servlet_holder);
         }
      } else if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
         chain = this.getFilterChain(baseRequest, (String)null, servlet_holder);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("chain={}", chain);
      }

      Throwable th = null;

      try {
         if (servlet_holder == null) {
            this.notFound(baseRequest, request, response);
         } else {
            ServletRequest req = request;
            if (request instanceof ServletRequestHttpWrapper) {
               req = ((ServletRequestHttpWrapper)request).getRequest();
            }

            ServletResponse res = response;
            if (response instanceof ServletResponseHttpWrapper) {
               res = ((ServletResponseHttpWrapper)response).getResponse();
            }

            servlet_holder.prepare(baseRequest, req, res);
            if (chain != null) {
               chain.doFilter(req, res);
            } else {
               servlet_holder.handle(baseRequest, req, res);
            }
         }
      } catch (EofException e) {
         throw e;
      } catch (RuntimeIOException e) {
         if (e.getCause() instanceof IOException) {
            LOG.debug(e);
            throw (IOException)e.getCause();
         }

         throw e;
      } catch (Exception var19) {
         if (baseRequest.isAsyncStarted() || !DispatcherType.REQUEST.equals(type) && !DispatcherType.ASYNC.equals(type)) {
            if (var19 instanceof IOException) {
               throw (IOException)var19;
            }

            if (var19 instanceof RuntimeException) {
               throw (RuntimeException)var19;
            }

            if (var19 instanceof ServletException) {
               throw (ServletException)var19;
            }
         }

         th = var19;
         if (var19 instanceof ServletException) {
            if (var19 instanceof QuietServletException) {
               LOG.warn(((Throwable)var19).toString());
               LOG.debug(var19);
            } else {
               LOG.warn(var19);
            }
         } else {
            if (var19 instanceof EofException) {
               throw (EofException)var19;
            }

            LOG.warn(request.getRequestURI(), (Throwable)var19);
            if (LOG.isDebugEnabled()) {
               LOG.debug(request.toString());
            }
         }

         request.setAttribute("javax.servlet.error.exception_type", var19.getClass());
         request.setAttribute("javax.servlet.error.exception", var19);
         if (response.isCommitted()) {
            if (var19 instanceof IOException) {
               throw (IOException)var19;
            }

            if (var19 instanceof RuntimeException) {
               throw (RuntimeException)var19;
            }

            if (var19 instanceof ServletException) {
               throw (ServletException)var19;
            }

            throw new IllegalStateException("response already committed", var19);
         }

         baseRequest.getResponse().getHttpFields().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
         if (var19 instanceof UnavailableException) {
            UnavailableException ue = (UnavailableException)var19;
            if (ue.isPermanent()) {
               response.sendError(404);
            } else {
               response.sendError(503);
            }
         } else {
            response.sendError(500);
         }
      } catch (Error e) {
         if ("ContinuationThrowable".equals(e.getClass().getSimpleName())) {
            throw e;
         }

         th = e;
         if (!DispatcherType.REQUEST.equals(type) && !DispatcherType.ASYNC.equals(type)) {
            throw e;
         }

         LOG.warn("Error for " + request.getRequestURI(), (Throwable)e);
         if (LOG.isDebugEnabled()) {
            LOG.debug(request.toString());
         }

         request.setAttribute("javax.servlet.error.exception_type", e.getClass());
         request.setAttribute("javax.servlet.error.exception", e);
         if (!response.isCommitted()) {
            baseRequest.getResponse().getHttpFields().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
            response.sendError(500);
         } else {
            LOG.debug("Response already committed for handling ", (Throwable)e);
         }
      } finally {
         if (th != null && request.isAsyncStarted()) {
            baseRequest.getHttpChannelState().errorComplete();
         }

         if (servlet_holder != null) {
            baseRequest.setHandled(true);
         }

      }

   }

   protected FilterChain getFilterChain(Request baseRequest, String pathInContext, ServletHolder servletHolder) {
      String key = pathInContext == null ? servletHolder.getName() : pathInContext;
      int dispatch = FilterMapping.dispatch(baseRequest.getDispatcherType());
      if (this._filterChainsCached && this._chainCache != null) {
         FilterChain chain = (FilterChain)this._chainCache[dispatch].get(key);
         if (chain != null) {
            return chain;
         }
      }

      List<FilterHolder> filters = new ArrayList();
      if (pathInContext != null && this._filterPathMappings != null) {
         for(FilterMapping filterPathMapping : this._filterPathMappings) {
            if (filterPathMapping.appliesTo(pathInContext, dispatch)) {
               filters.add(filterPathMapping.getFilterHolder());
            }
         }
      }

      if (servletHolder != null && this._filterNameMappings != null && this._filterNameMappings.size() > 0 && this._filterNameMappings.size() > 0) {
         Object o = this._filterNameMappings.get(servletHolder.getName());

         for(int i = 0; i < LazyList.size(o); ++i) {
            FilterMapping mapping = (FilterMapping)LazyList.get(o, i);
            if (mapping.appliesTo(dispatch)) {
               filters.add(mapping.getFilterHolder());
            }
         }

         o = this._filterNameMappings.get("*");

         for(int i = 0; i < LazyList.size(o); ++i) {
            FilterMapping mapping = (FilterMapping)LazyList.get(o, i);
            if (mapping.appliesTo(dispatch)) {
               filters.add(mapping.getFilterHolder());
            }
         }
      }

      if (filters.isEmpty()) {
         return null;
      } else {
         FilterChain chain = null;
         if (this._filterChainsCached) {
            if (filters.size() > 0) {
               chain = new CachedChain(filters, servletHolder);
            }

            Map<String, FilterChain> cache = this._chainCache[dispatch];
            Queue<String> lru = this._chainLRU[dispatch];

            while(this._maxFilterChainsCacheSize > 0 && cache.size() >= this._maxFilterChainsCacheSize) {
               String k = (String)lru.poll();
               if (k == null) {
                  cache.clear();
                  break;
               }

               cache.remove(k);
            }

            cache.put(key, chain);
            lru.add(key);
         } else if (filters.size() > 0) {
            chain = new Chain(baseRequest, filters, servletHolder);
         }

         return chain;
      }
   }

   protected void invalidateChainsCache() {
      if (this._chainLRU[1] != null) {
         this._chainLRU[1].clear();
         this._chainLRU[2].clear();
         this._chainLRU[4].clear();
         this._chainLRU[8].clear();
         this._chainLRU[16].clear();
         this._chainCache[1].clear();
         this._chainCache[2].clear();
         this._chainCache[4].clear();
         this._chainCache[8].clear();
         this._chainCache[16].clear();
      }

   }

   public boolean isAvailable() {
      if (!this.isStarted()) {
         return false;
      } else {
         ServletHolder[] holders = this.getServlets();

         for(ServletHolder holder : holders) {
            if (holder != null && !holder.isAvailable()) {
               return false;
            }
         }

         return true;
      }
   }

   public void setStartWithUnavailable(boolean start) {
      this._startWithUnavailable = start;
   }

   public boolean isAllowDuplicateMappings() {
      return this._allowDuplicateMappings;
   }

   public void setAllowDuplicateMappings(boolean allowDuplicateMappings) {
      this._allowDuplicateMappings = allowDuplicateMappings;
   }

   public boolean isStartWithUnavailable() {
      return this._startWithUnavailable;
   }

   public void initialize() throws Exception {
      MultiException mx = new MultiException();
      if (this._filters != null) {
         for(FilterHolder f : this._filters) {
            try {
               f.start();
               f.initialize();
            } catch (Exception e) {
               mx.add(e);
            }
         }
      }

      if (this._servlets != null) {
         ServletHolder[] servlets = (ServletHolder[])this._servlets.clone();
         Arrays.sort(servlets);

         for(ServletHolder servlet : servlets) {
            try {
               servlet.start();
               servlet.initialize();
            } catch (Throwable e) {
               LOG.debug("EXCEPTION ", e);
               mx.add(e);
            }
         }
      }

      for(Holder<?> h : this.getBeans(Holder.class)) {
         try {
            if (!h.isStarted()) {
               h.start();
               h.initialize();
            }
         } catch (Exception e) {
            mx.add(e);
         }
      }

      mx.ifExceptionThrow();
   }

   public boolean isFilterChainsCached() {
      return this._filterChainsCached;
   }

   public void addListener(ListenerHolder listener) {
      if (listener != null) {
         this.setListeners((ListenerHolder[])ArrayUtil.addToArray(this.getListeners(), listener, ListenerHolder.class));
      }

   }

   public ListenerHolder[] getListeners() {
      return this._listeners;
   }

   public void setListeners(ListenerHolder[] listeners) {
      if (listeners != null) {
         for(ListenerHolder holder : listeners) {
            holder.setServletHandler(this);
         }
      }

      this.updateBeans(this._listeners, listeners);
      this._listeners = listeners;
   }

   public ListenerHolder newListenerHolder(BaseHolder.Source source) {
      return new ListenerHolder(source);
   }

   public ServletHolder newServletHolder(BaseHolder.Source source) {
      return new ServletHolder(source);
   }

   public ServletHolder addServletWithMapping(String className, String pathSpec) {
      ServletHolder holder = this.newServletHolder(BaseHolder.Source.EMBEDDED);
      holder.setClassName(className);
      this.addServletWithMapping(holder, pathSpec);
      return holder;
   }

   public ServletHolder addServletWithMapping(Class<? extends Servlet> servlet, String pathSpec) {
      ServletHolder holder = this.newServletHolder(BaseHolder.Source.EMBEDDED);
      holder.setHeldClass(servlet);
      this.addServletWithMapping(holder, pathSpec);
      return holder;
   }

   public void addServletWithMapping(ServletHolder servlet, String pathSpec) {
      ServletHolder[] holders = this.getServlets();
      if (holders != null) {
         holders = (ServletHolder[])((ServletHolder;)holders).clone();
      }

      try {
         synchronized(this) {
            if (servlet != null && !this.containsServletHolder(servlet)) {
               this.setServlets((ServletHolder[])ArrayUtil.addToArray(holders, servlet, ServletHolder.class));
            }
         }

         ServletMapping mapping = new ServletMapping();
         mapping.setServletName(servlet.getName());
         mapping.setPathSpec(pathSpec);
         this.setServletMappings((ServletMapping[])ArrayUtil.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
      } catch (Exception e) {
         this.setServlets(holders);
         if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
         } else {
            throw new RuntimeException(e);
         }
      }
   }

   public void addServlet(ServletHolder holder) {
      if (holder != null) {
         synchronized(this) {
            if (!this.containsServletHolder(holder)) {
               this.setServlets((ServletHolder[])ArrayUtil.addToArray(this.getServlets(), holder, ServletHolder.class));
            }

         }
      }
   }

   public void addServletMapping(ServletMapping mapping) {
      this.setServletMappings((ServletMapping[])ArrayUtil.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
   }

   public Set<String> setServletSecurity(ServletRegistration.Dynamic registration, ServletSecurityElement servletSecurityElement) {
      return this._contextHandler != null ? this._contextHandler.setServletSecurity(registration, servletSecurityElement) : Collections.emptySet();
   }

   public FilterHolder newFilterHolder(BaseHolder.Source source) {
      return new FilterHolder(source);
   }

   public FilterHolder getFilter(String name) {
      return (FilterHolder)this._filterNameMap.get(name);
   }

   public FilterHolder addFilterWithMapping(Class<? extends Filter> filter, String pathSpec, EnumSet<DispatcherType> dispatches) {
      FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
      holder.setHeldClass(filter);
      this.addFilterWithMapping(holder, pathSpec, dispatches);
      return holder;
   }

   public FilterHolder addFilterWithMapping(String className, String pathSpec, EnumSet<DispatcherType> dispatches) {
      FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
      holder.setClassName(className);
      this.addFilterWithMapping(holder, pathSpec, dispatches);
      return holder;
   }

   public void addFilterWithMapping(FilterHolder holder, String pathSpec, EnumSet<DispatcherType> dispatches) {
      FilterHolder[] holders = this.getFilters();
      if (holders != null) {
         holders = (FilterHolder[])((FilterHolder;)holders).clone();
      }

      try {
         synchronized(this) {
            if (holder != null && !this.containsFilterHolder(holder)) {
               this.setFilters((FilterHolder[])ArrayUtil.addToArray(holders, holder, FilterHolder.class));
            }
         }

         FilterMapping mapping = new FilterMapping();
         mapping.setFilterName(holder.getName());
         mapping.setPathSpec(pathSpec);
         mapping.setDispatcherTypes(dispatches);
         this.addFilterMapping(mapping);
      } catch (RuntimeException e) {
         this.setFilters(holders);
         throw e;
      } catch (Error e) {
         this.setFilters(holders);
         throw e;
      }
   }

   public FilterHolder addFilterWithMapping(Class<? extends Filter> filter, String pathSpec, int dispatches) {
      FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
      holder.setHeldClass(filter);
      this.addFilterWithMapping(holder, pathSpec, dispatches);
      return holder;
   }

   public FilterHolder addFilterWithMapping(String className, String pathSpec, int dispatches) {
      FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
      holder.setClassName(className);
      this.addFilterWithMapping(holder, pathSpec, dispatches);
      return holder;
   }

   public void addFilterWithMapping(FilterHolder holder, String pathSpec, int dispatches) {
      FilterHolder[] holders = this.getFilters();
      if (holders != null) {
         holders = (FilterHolder[])((FilterHolder;)holders).clone();
      }

      try {
         synchronized(this) {
            if (holder != null && !this.containsFilterHolder(holder)) {
               this.setFilters((FilterHolder[])ArrayUtil.addToArray(holders, holder, FilterHolder.class));
            }
         }

         FilterMapping mapping = new FilterMapping();
         mapping.setFilterName(holder.getName());
         mapping.setPathSpec(pathSpec);
         mapping.setDispatches(dispatches);
         this.addFilterMapping(mapping);
      } catch (RuntimeException e) {
         this.setFilters(holders);
         throw e;
      } catch (Error e) {
         this.setFilters(holders);
         throw e;
      }
   }

   /** @deprecated */
   @Deprecated
   public FilterHolder addFilter(String className, String pathSpec, EnumSet<DispatcherType> dispatches) {
      return this.addFilterWithMapping(className, pathSpec, dispatches);
   }

   public void addFilter(FilterHolder filter, FilterMapping filterMapping) {
      if (filter != null) {
         synchronized(this) {
            if (!this.containsFilterHolder(filter)) {
               this.setFilters((FilterHolder[])ArrayUtil.addToArray(this.getFilters(), filter, FilterHolder.class));
            }
         }
      }

      if (filterMapping != null) {
         this.addFilterMapping(filterMapping);
      }

   }

   public void addFilter(FilterHolder filter) {
      if (filter != null) {
         synchronized(this) {
            if (!this.containsFilterHolder(filter)) {
               this.setFilters((FilterHolder[])ArrayUtil.addToArray(this.getFilters(), filter, FilterHolder.class));
            }

         }
      }
   }

   public void addFilterMapping(FilterMapping mapping) {
      if (mapping != null) {
         BaseHolder.Source source = mapping.getFilterHolder() == null ? null : mapping.getFilterHolder().getSource();
         FilterMapping[] mappings = this.getFilterMappings();
         if (mappings != null && mappings.length != 0) {
            if (source != null && BaseHolder.Source.JAVAX_API == source) {
               this.setFilterMappings(this.insertFilterMapping(mapping, mappings.length - 1, false));
               if (this._matchAfterIndex < 0) {
                  this._matchAfterIndex = this.getFilterMappings().length - 1;
               }
            } else if (this._matchAfterIndex < 0) {
               this.setFilterMappings(this.insertFilterMapping(mapping, mappings.length - 1, false));
            } else {
               FilterMapping[] new_mappings = this.insertFilterMapping(mapping, this._matchAfterIndex, true);
               ++this._matchAfterIndex;
               this.setFilterMappings(new_mappings);
            }
         } else {
            this.setFilterMappings(this.insertFilterMapping(mapping, 0, false));
            if (source != null && source == BaseHolder.Source.JAVAX_API) {
               this._matchAfterIndex = 0;
            }
         }
      }

   }

   public void prependFilterMapping(FilterMapping mapping) {
      if (mapping != null) {
         BaseHolder.Source source = mapping.getFilterHolder() == null ? null : mapping.getFilterHolder().getSource();
         FilterMapping[] mappings = this.getFilterMappings();
         if (mappings != null && mappings.length != 0) {
            if (source != null && BaseHolder.Source.JAVAX_API == source) {
               if (this._matchBeforeIndex < 0) {
                  this._matchBeforeIndex = 0;
                  FilterMapping[] new_mappings = this.insertFilterMapping(mapping, 0, true);
                  this.setFilterMappings(new_mappings);
               } else {
                  FilterMapping[] new_mappings = this.insertFilterMapping(mapping, this._matchBeforeIndex, false);
                  ++this._matchBeforeIndex;
                  this.setFilterMappings(new_mappings);
               }
            } else {
               FilterMapping[] new_mappings = this.insertFilterMapping(mapping, 0, true);
               this.setFilterMappings(new_mappings);
            }

            if (this._matchAfterIndex >= 0) {
               ++this._matchAfterIndex;
            }
         } else {
            this.setFilterMappings(this.insertFilterMapping(mapping, 0, false));
            if (source != null && BaseHolder.Source.JAVAX_API == source) {
               this._matchBeforeIndex = 0;
            }
         }
      }

   }

   protected FilterMapping[] insertFilterMapping(FilterMapping mapping, int pos, boolean before) {
      if (pos < 0) {
         throw new IllegalArgumentException("FilterMapping insertion pos < 0");
      } else {
         FilterMapping[] mappings = this.getFilterMappings();
         if (mappings != null && mappings.length != 0) {
            FilterMapping[] new_mappings = new FilterMapping[mappings.length + 1];
            if (before) {
               System.arraycopy(mappings, 0, new_mappings, 0, pos);
               new_mappings[pos] = mapping;
               System.arraycopy(mappings, pos, new_mappings, pos + 1, mappings.length - pos);
            } else {
               System.arraycopy(mappings, 0, new_mappings, 0, pos + 1);
               new_mappings[pos + 1] = mapping;
               if (mappings.length > pos + 1) {
                  System.arraycopy(mappings, pos + 1, new_mappings, pos + 2, mappings.length - (pos + 1));
               }
            }

            return new_mappings;
         } else {
            return new FilterMapping[]{mapping};
         }
      }
   }

   protected synchronized void updateNameMappings() {
      this._filterNameMap.clear();
      if (this._filters != null) {
         for(FilterHolder filter : this._filters) {
            this._filterNameMap.put(filter.getName(), filter);
            filter.setServletHandler(this);
         }
      }

      this._servletNameMap.clear();
      if (this._servlets != null) {
         for(ServletHolder servlet : this._servlets) {
            this._servletNameMap.put(servlet.getName(), servlet);
            servlet.setServletHandler(this);
         }
      }

   }

   protected synchronized void updateMappings() {
      if (this._filterMappings == null) {
         this._filterPathMappings = null;
         this._filterNameMappings = null;
      } else {
         this._filterPathMappings = new ArrayList();
         this._filterNameMappings = new MultiMap<FilterMapping>();

         for(FilterMapping filtermapping : this._filterMappings) {
            FilterHolder filter_holder = (FilterHolder)this._filterNameMap.get(filtermapping.getFilterName());
            if (filter_holder == null) {
               throw new IllegalStateException("No filter named " + filtermapping.getFilterName());
            }

            filtermapping.setFilterHolder(filter_holder);
            if (filtermapping.getPathSpecs() != null) {
               this._filterPathMappings.add(filtermapping);
            }

            if (filtermapping.getServletNames() != null) {
               String[] names = filtermapping.getServletNames();

               for(String name : names) {
                  if (name != null) {
                     this._filterNameMappings.add(name, filtermapping);
                  }
               }
            }
         }
      }

      if (this._servletMappings != null && this._servletNameMap != null) {
         PathMap<ServletHolder> pm = new PathMap<ServletHolder>();
         Map<String, ServletMapping> servletPathMappings = new HashMap();
         HashMap<String, List<ServletMapping>> sms = new HashMap();

         for(ServletMapping servletMapping : this._servletMappings) {
            String[] pathSpecs = servletMapping.getPathSpecs();
            if (pathSpecs != null) {
               for(String pathSpec : pathSpecs) {
                  List<ServletMapping> mappings = (List)sms.get(pathSpec);
                  if (mappings == null) {
                     mappings = new ArrayList();
                     sms.put(pathSpec, mappings);
                  }

                  mappings.add(servletMapping);
               }
            }
         }

         for(String pathSpec : sms.keySet()) {
            List<ServletMapping> mappings = (List)sms.get(pathSpec);
            ServletMapping finalMapping = null;

            for(ServletMapping mapping : mappings) {
               ServletHolder servlet_holder = (ServletHolder)this._servletNameMap.get(mapping.getServletName());
               if (servlet_holder == null) {
                  throw new IllegalStateException("No such servlet: " + mapping.getServletName());
               }

               if (servlet_holder.isEnabled()) {
                  if (finalMapping == null) {
                     finalMapping = mapping;
                  } else if (finalMapping.isDefault()) {
                     finalMapping = mapping;
                  } else if (this.isAllowDuplicateMappings()) {
                     LOG.warn("Multiple servlets map to path {}: {} and {}, choosing {}", pathSpec, finalMapping.getServletName(), mapping.getServletName(), mapping);
                     finalMapping = mapping;
                  } else if (!mapping.isDefault()) {
                     throw new IllegalStateException("Multiple servlets map to path: " + pathSpec + ": " + finalMapping.getServletName() + "," + mapping.getServletName());
                  }
               }
            }

            if (finalMapping == null) {
               throw new IllegalStateException("No acceptable servlet mappings for " + pathSpec);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Chose path={} mapped to servlet={} from default={}", pathSpec, finalMapping.getServletName(), finalMapping.isDefault());
            }

            servletPathMappings.put(pathSpec, finalMapping);
            pm.put(pathSpec, this._servletNameMap.get(finalMapping.getServletName()));
         }

         this._servletPathMap = pm;
      } else {
         this._servletPathMap = null;
      }

      if (this._chainCache != null) {
         int i = this._chainCache.length;

         while(i-- > 0) {
            if (this._chainCache[i] != null) {
               this._chainCache[i].clear();
            }
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("filterNameMap=" + this._filterNameMap);
         LOG.debug("pathFilters=" + this._filterPathMappings);
         LOG.debug("servletFilterMap=" + this._filterNameMappings);
         LOG.debug("servletPathMap=" + this._servletPathMap);
         LOG.debug("servletNameMap=" + this._servletNameMap);
      }

      try {
         if (this._contextHandler != null && this._contextHandler.isStarted() || this._contextHandler == null && this.isStarted()) {
            this.initialize();
         }

      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   protected void notFound(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Not Found {}", request.getRequestURI());
      }

      if (this.getHandler() != null) {
         this.nextHandle(URIUtil.addPaths(request.getServletPath(), request.getPathInfo()), baseRequest, request, response);
      }

   }

   protected synchronized boolean containsFilterHolder(FilterHolder holder) {
      if (this._filters == null) {
         return false;
      } else {
         boolean found = false;

         for(FilterHolder f : this._filters) {
            if (f == holder) {
               found = true;
            }
         }

         return found;
      }
   }

   protected synchronized boolean containsServletHolder(ServletHolder holder) {
      if (this._servlets == null) {
         return false;
      } else {
         boolean found = false;

         for(ServletHolder s : this._servlets) {
            if (s == holder) {
               found = true;
            }
         }

         return found;
      }
   }

   public void setFilterChainsCached(boolean filterChainsCached) {
      this._filterChainsCached = filterChainsCached;
   }

   public void setFilterMappings(FilterMapping[] filterMappings) {
      this.updateBeans(this._filterMappings, filterMappings);
      this._filterMappings = filterMappings;
      if (this.isStarted()) {
         this.updateMappings();
      }

      this.invalidateChainsCache();
   }

   public synchronized void setFilters(FilterHolder[] holders) {
      if (holders != null) {
         for(FilterHolder holder : holders) {
            holder.setServletHandler(this);
         }
      }

      this.updateBeans(this._filters, holders);
      this._filters = holders;
      this.updateNameMappings();
      this.invalidateChainsCache();
   }

   public void setServletMappings(ServletMapping[] servletMappings) {
      this.updateBeans(this._servletMappings, servletMappings);
      this._servletMappings = servletMappings;
      if (this.isStarted()) {
         this.updateMappings();
      }

      this.invalidateChainsCache();
   }

   public synchronized void setServlets(ServletHolder[] holders) {
      if (holders != null) {
         for(ServletHolder holder : holders) {
            holder.setServletHandler(this);
         }
      }

      this.updateBeans(this._servlets, holders);
      this._servlets = holders;
      this.updateNameMappings();
      this.invalidateChainsCache();
   }

   public int getMaxFilterChainsCacheSize() {
      return this._maxFilterChainsCacheSize;
   }

   public void setMaxFilterChainsCacheSize(int maxFilterChainsCacheSize) {
      this._maxFilterChainsCacheSize = maxFilterChainsCacheSize;
   }

   void destroyServlet(Servlet servlet) {
      if (this._contextHandler != null) {
         this._contextHandler.destroyServlet(servlet);
      }

   }

   void destroyFilter(Filter filter) {
      if (this._contextHandler != null) {
         this._contextHandler.destroyFilter(filter);
      }

   }

   private class CachedChain implements FilterChain {
      FilterHolder _filterHolder;
      CachedChain _next;
      ServletHolder _servletHolder;

      CachedChain(List<FilterHolder> filters, ServletHolder servletHolder) {
         if (filters.size() > 0) {
            this._filterHolder = (FilterHolder)filters.get(0);
            filters.remove(0);
            this._next = ServletHandler.this.new CachedChain(filters, servletHolder);
         } else {
            this._servletHolder = servletHolder;
         }

      }

      public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
         Request baseRequest = Request.getBaseRequest(request);
         if (this._filterHolder != null) {
            if (ServletHandler.LOG.isDebugEnabled()) {
               ServletHandler.LOG.debug("call filter {}", this._filterHolder);
            }

            Filter filter = this._filterHolder.getFilter();
            if (baseRequest.isAsyncSupported() && !this._filterHolder.isAsyncSupported()) {
               try {
                  baseRequest.setAsyncSupported(false, this._filterHolder.toString());
                  filter.doFilter(request, response, this._next);
               } finally {
                  baseRequest.setAsyncSupported(true, (String)null);
               }
            } else {
               filter.doFilter(request, response, this._next);
            }

         } else {
            HttpServletRequest srequest = (HttpServletRequest)request;
            if (this._servletHolder == null) {
               ServletHandler.this.notFound(baseRequest, srequest, (HttpServletResponse)response);
            } else {
               if (ServletHandler.LOG.isDebugEnabled()) {
                  ServletHandler.LOG.debug("call servlet " + this._servletHolder);
               }

               this._servletHolder.handle(baseRequest, request, response);
            }

         }
      }

      public String toString() {
         if (this._filterHolder != null) {
            return this._filterHolder + "->" + this._next.toString();
         } else {
            return this._servletHolder != null ? this._servletHolder.toString() : "null";
         }
      }
   }

   private class Chain implements FilterChain {
      final Request _baseRequest;
      final List<FilterHolder> _chain;
      final ServletHolder _servletHolder;
      int _filter = 0;

      Chain(Request baseRequest, List<FilterHolder> filters, ServletHolder servletHolder) {
         this._baseRequest = baseRequest;
         this._chain = filters;
         this._servletHolder = servletHolder;
      }

      public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
         if (ServletHandler.LOG.isDebugEnabled()) {
            ServletHandler.LOG.debug("doFilter " + this._filter);
         }

         if (this._filter < this._chain.size()) {
            FilterHolder holder = (FilterHolder)this._chain.get(this._filter++);
            if (ServletHandler.LOG.isDebugEnabled()) {
               ServletHandler.LOG.debug("call filter " + holder);
            }

            Filter filter = holder.getFilter();
            if (!holder.isAsyncSupported() && this._baseRequest.isAsyncSupported()) {
               try {
                  this._baseRequest.setAsyncSupported(false, holder.toString());
                  filter.doFilter(request, response, this);
               } finally {
                  this._baseRequest.setAsyncSupported(true, (String)null);
               }
            } else {
               filter.doFilter(request, response, this);
            }

         } else {
            HttpServletRequest srequest = (HttpServletRequest)request;
            if (this._servletHolder == null) {
               ServletHandler.this.notFound(Request.getBaseRequest(request), srequest, (HttpServletResponse)response);
            } else {
               if (ServletHandler.LOG.isDebugEnabled()) {
                  ServletHandler.LOG.debug("call servlet {}", this._servletHolder);
               }

               this._servletHolder.handle(this._baseRequest, request, response);
            }

         }
      }

      public String toString() {
         StringBuilder b = new StringBuilder();

         for(FilterHolder f : this._chain) {
            b.append(f.toString());
            b.append("->");
         }

         b.append(this._servletHolder);
         return b.toString();
      }
   }

   public static class Default404Servlet extends HttpServlet {
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         resp.sendError(404);
      }
   }
}
