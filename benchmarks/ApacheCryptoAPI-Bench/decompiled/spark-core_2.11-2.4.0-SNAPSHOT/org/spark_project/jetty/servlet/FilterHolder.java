package org.spark_project.jetty.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.spark_project.jetty.util.TypeUtil;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class FilterHolder extends Holder<Filter> {
   private static final Logger LOG = Log.getLogger(FilterHolder.class);
   private transient Filter _filter;
   private transient Config _config;
   private transient FilterRegistration.Dynamic _registration;

   public FilterHolder() {
      this(BaseHolder.Source.EMBEDDED);
   }

   public FilterHolder(BaseHolder.Source source) {
      super(source);
   }

   public FilterHolder(Class<? extends Filter> filter) {
      this(BaseHolder.Source.EMBEDDED);
      this.setHeldClass(filter);
   }

   public FilterHolder(Filter filter) {
      this(BaseHolder.Source.EMBEDDED);
      this.setFilter(filter);
   }

   public void doStart() throws Exception {
      super.doStart();
      if (!Filter.class.isAssignableFrom(this._class)) {
         String msg = this._class + " is not a javax.servlet.Filter";
         super.stop();
         throw new IllegalStateException(msg);
      }
   }

   public void initialize() throws Exception {
      if (!this._initialized) {
         super.initialize();
         if (this._filter == null) {
            try {
               ServletContext context = this._servletHandler.getServletContext();
               this._filter = context instanceof ServletContextHandler.Context ? ((ServletContextHandler.Context)context).createFilter(this.getHeldClass()) : (Filter)this.getHeldClass().newInstance();
            } catch (ServletException se) {
               Throwable cause = se.getRootCause();
               if (cause instanceof InstantiationException) {
                  throw (InstantiationException)cause;
               }

               if (cause instanceof IllegalAccessException) {
                  throw (IllegalAccessException)cause;
               }

               throw se;
            }
         }

         this._config = new Config();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Filter.init {}", this._filter);
         }

         this._filter.init(this._config);
      }

      this._initialized = true;
   }

   public void doStop() throws Exception {
      if (this._filter != null) {
         try {
            this.destroyInstance(this._filter);
         } catch (Exception e) {
            LOG.warn(e);
         }
      }

      if (!this._extInstance) {
         this._filter = null;
      }

      this._config = null;
      this._initialized = false;
      super.doStop();
   }

   public void destroyInstance(Object o) throws Exception {
      if (o != null) {
         Filter f = (Filter)o;
         f.destroy();
         this.getServletHandler().destroyFilter(f);
      }
   }

   public synchronized void setFilter(Filter filter) {
      this._filter = filter;
      this._extInstance = true;
      this.setHeldClass(filter.getClass());
      if (this.getName() == null) {
         this.setName(filter.getClass().getName());
      }

   }

   public Filter getFilter() {
      return this._filter;
   }

   public String toString() {
      return this.getName();
   }

   public void dump(Appendable out, String indent) throws IOException {
      super.dump(out, indent);
      if (this._filter instanceof Dumpable) {
         ((Dumpable)this._filter).dump(out, indent);
      }

   }

   public FilterRegistration.Dynamic getRegistration() {
      if (this._registration == null) {
         this._registration = new Registration();
      }

      return this._registration;
   }

   protected class Registration extends Holder<Filter>.HolderRegistration implements FilterRegistration.Dynamic {
      public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
         FilterHolder.this.illegalStateIfContextStarted();
         FilterMapping mapping = new FilterMapping();
         mapping.setFilterHolder(FilterHolder.this);
         mapping.setServletNames(servletNames);
         mapping.setDispatcherTypes(dispatcherTypes);
         if (isMatchAfter) {
            FilterHolder.this._servletHandler.addFilterMapping(mapping);
         } else {
            FilterHolder.this._servletHandler.prependFilterMapping(mapping);
         }

      }

      public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
         FilterHolder.this.illegalStateIfContextStarted();
         FilterMapping mapping = new FilterMapping();
         mapping.setFilterHolder(FilterHolder.this);
         mapping.setPathSpecs(urlPatterns);
         mapping.setDispatcherTypes(dispatcherTypes);
         if (isMatchAfter) {
            FilterHolder.this._servletHandler.addFilterMapping(mapping);
         } else {
            FilterHolder.this._servletHandler.prependFilterMapping(mapping);
         }

      }

      public Collection<String> getServletNameMappings() {
         FilterMapping[] mappings = FilterHolder.this._servletHandler.getFilterMappings();
         List<String> names = new ArrayList();

         for(FilterMapping mapping : mappings) {
            if (mapping.getFilterHolder() == FilterHolder.this) {
               String[] servlets = mapping.getServletNames();
               if (servlets != null && servlets.length > 0) {
                  names.addAll(Arrays.asList(servlets));
               }
            }
         }

         return names;
      }

      public Collection<String> getUrlPatternMappings() {
         FilterMapping[] mappings = FilterHolder.this._servletHandler.getFilterMappings();
         List<String> patterns = new ArrayList();

         for(FilterMapping mapping : mappings) {
            if (mapping.getFilterHolder() == FilterHolder.this) {
               String[] specs = mapping.getPathSpecs();
               patterns.addAll(TypeUtil.asList(specs));
            }
         }

         return patterns;
      }
   }

   class Config extends Holder<Filter>.HolderConfig implements FilterConfig {
      public String getFilterName() {
         return FilterHolder.this._name;
      }
   }
}
