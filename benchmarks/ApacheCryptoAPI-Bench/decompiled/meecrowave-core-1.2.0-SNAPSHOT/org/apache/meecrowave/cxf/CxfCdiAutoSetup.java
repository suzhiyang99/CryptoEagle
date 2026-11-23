package org.apache.meecrowave.cxf;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.Application;
import org.apache.cxf.cdi.CXFCdiServlet;
import org.apache.cxf.common.util.ReflectionUtil;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxrs.JAXRSServiceFactoryBean;
import org.apache.cxf.jaxrs.model.AbstractResourceInfo;
import org.apache.cxf.jaxrs.model.ApplicationInfo;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.MethodDispatcher;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.ProviderInfo;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.ChainInitiationObserver;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.servlet.ServletDestination;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.logging.tomcat.LogFacade;

public class CxfCdiAutoSetup implements ServletContainerInitializer {
   private static final String NAME = "cxf-cdi";

   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      final Meecrowave.Builder builder = (Meecrowave.Builder)Meecrowave.Builder.class.cast(ctx.getAttribute("meecrowave.configuration"));
      final MeecrowaveCXFCdiServlet delegate = new MeecrowaveCXFCdiServlet();
      FilterRegistration.Dynamic jaxrs = ctx.addFilter("cxf-cdi", new Filter() {
         private final String servletPath = builder.getJaxrsMapping().endsWith("/*") ? builder.getJaxrsMapping().substring(0, builder.getJaxrsMapping().length() - 2) : builder.getJaxrsMapping();

         public void init(final FilterConfig filterConfig) throws ServletException {
            delegate.init(new ServletConfig() {
               public String getServletName() {
                  return "cxf-cdi";
               }

               public ServletContext getServletContext() {
                  return filterConfig.getServletContext();
               }

               public String getInitParameter(String name) {
                  return filterConfig.getInitParameter(name);
               }

               public Enumeration<String> getInitParameterNames() {
                  return filterConfig.getInitParameterNames();
               }
            });
         }

         public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (!HttpServletRequest.class.isInstance(request)) {
               chain.doFilter(request, response);
            } else {
               HttpServletRequest http = (HttpServletRequest)HttpServletRequest.class.cast(request);
               final String path = http.getRequestURI().substring(http.getContextPath().length());
               Stream var10000 = Stream.of(delegate.prefixes);
               path.getClass();
               Optional<String> app = var10000.filter(path::startsWith).findAny();
               if (app.isPresent()) {
                  delegate.service(new HttpServletRequestWrapper(http) {
                     public String getPathInfo() {
                        return path;
                     }

                     public String getServletPath() {
                        return servletPath;
                     }
                  }, response);
               } else {
                  chain.doFilter(request, response);
               }

            }
         }

         public void destroy() {
            delegate.destroy();
         }
      });
      jaxrs.setAsyncSupported(true);
      jaxrs.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, new String[]{builder.getJaxrsMapping()});
      Optional.ofNullable(builder.getCxfServletParams()).ifPresent((m) -> m.forEach(jaxrs::setInitParameter));
   }

   private static String uproxyName(String clazz) {
      return clazz.contains("$$") ? clazz.substring(0, clazz.indexOf("$$")) : clazz;
   }

   private static class Logs {
      private static String forceLength(String httpMethod, int l, boolean right) {
         String http;
         if (httpMethod == null) {
            http = "";
         } else {
            http = httpMethod;
         }

         StringBuilder builder = new StringBuilder();
         if (!right) {
            for(int i = 0; i < l - http.length(); ++i) {
               builder.append(" ");
            }
         }

         builder.append(http);
         if (right) {
            for(int i = 0; i < l - http.length(); ++i) {
               builder.append(" ");
            }
         }

         return builder.toString();
      }

      private static String toSimpleString(Method mtd) {
         try {
            StringBuilder sb = new StringBuilder();
            Type[] typeparms = mtd.getTypeParameters();
            if (typeparms.length > 0) {
               boolean first = true;
               sb.append("<");

               for(Type typeparm : typeparms) {
                  if (!first) {
                     sb.append(",");
                  }

                  sb.append(name(typeparm));
                  first = false;
               }

               sb.append("> ");
            }

            Type genRetType = mtd.getGenericReturnType();
            sb.append(name(genRetType)).append(" ");
            sb.append(mtd.getName()).append("(");
            Type[] params = mtd.getGenericParameterTypes();

            for(int j = 0; j < params.length; ++j) {
               sb.append(name(params[j]));
               if (j < params.length - 1) {
                  sb.append(", ");
               }
            }

            sb.append(")");
            Type[] exceptions = mtd.getGenericExceptionTypes();
            if (exceptions.length > 0) {
               sb.append(" throws ");

               for(int k = 0; k < exceptions.length; ++k) {
                  sb.append(name(exceptions[k]));
                  if (k < exceptions.length - 1) {
                     sb.append(",");
                  }
               }
            }

            return sb.toString();
         } catch (Exception e) {
            return "<" + e + ">";
         }
      }

      private static String name(Type type) {
         if (type instanceof Class) {
            return ((Class)type).getSimpleName().replace("java.lang.", "").replace("java.util", "");
         } else if (!(type instanceof ParameterizedType)) {
            return type.toString();
         } else {
            ParameterizedType pt = (ParameterizedType)type;
            StringBuilder builder = new StringBuilder();
            builder.append(name(pt.getRawType()));
            Type[] args = pt.getActualTypeArguments();
            if (args != null) {
               builder.append("<");

               for(int i = 0; i < args.length; ++i) {
                  builder.append(name(args[i]));
                  if (i < args.length - 1) {
                     builder.append(", ");
                  }
               }

               builder.append(">");
            }

            return builder.toString();
         }
      }

      private static String singleSlash(String address, String value) {
         if (address.endsWith("/") && value.startsWith("/")) {
            return address + value.substring(1);
         } else {
            return !address.endsWith("/") && !value.startsWith("/") ? address + '/' + value : address + value;
         }
      }

      private static class LogOperationEndpointInfo implements Comparable<LogOperationEndpointInfo> {
         private final String http;
         private final String address;
         private final String method;

         private LogOperationEndpointInfo(String http, String address, String method) {
            this.address = address;
            this.method = method;
            if (http != null) {
               this.http = http;
            } else {
               this.http = "";
            }

         }

         public int compareTo(LogOperationEndpointInfo o) {
            int compare = this.http.compareTo(o.http);
            if (compare != 0) {
               return compare;
            } else {
               compare = this.address.compareTo(o.address);
               return compare != 0 ? compare : this.method.compareTo(o.method);
            }
         }
      }

      private static class LogResourceEndpointInfo implements Comparable<LogResourceEndpointInfo> {
         private final String address;
         private final String classname;
         private final List<LogOperationEndpointInfo> operations;
         private final int methodSize;
         private final int methodStrSize;

         private LogResourceEndpointInfo(String address, String classname, List<LogOperationEndpointInfo> operations, int methodSize, int methodStrSize) {
            this.address = address;
            this.classname = classname;
            this.operations = operations;
            this.methodSize = methodSize;
            this.methodStrSize = methodStrSize;
         }

         public int compareTo(LogResourceEndpointInfo o) {
            int compare = this.address.compareTo(o.address);
            return compare != 0 ? compare : this.classname.compareTo(o.classname);
         }
      }
   }

   private static class MeecrowaveCXFCdiServlet extends CXFCdiServlet {
      private String[] prefixes;

      private MeecrowaveCXFCdiServlet() {
      }

      public void init(ServletConfig sc) throws ServletException {
         super.init(sc);
         LogFacade log = new LogFacade(CxfCdiAutoSetup.class.getName());
         DestinationRegistry registry = this.getDestinationRegistryFromBusOrDefault((String)null);
         Stream var10001 = registry.getDestinations().stream();
         ServletDestination.class.getClass();
         var10001 = var10001.filter(ServletDestination.class::isInstance);
         ServletDestination.class.getClass();
         this.prefixes = (String[])var10001.map(ServletDestination.class::cast).map(this.getServletDestinationPath(sc, log)).filter(Objects::nonNull).toArray((x$0) -> new String[x$0]);
      }

      private Function<ServletDestination, String> getServletDestinationPath(ServletConfig sc, LogFacade log) {
         return (sd) -> {
            Endpoint endpoint = ((ChainInitiationObserver)ChainInitiationObserver.class.cast(sd.getMessageObserver())).getEndpoint();
            ApplicationInfo app = (ApplicationInfo)ApplicationInfo.class.cast(endpoint.get(Application.class.getName()));
            JAXRSServiceFactoryBean sfb = (JAXRSServiceFactoryBean)JAXRSServiceFactoryBean.class.cast(endpoint.get(JAXRSServiceFactoryBean.class.getName()));
            String base = sd.getEndpointInfo().getAddress();
            if (sfb != null) {
               List<Logs.LogResourceEndpointInfo> resourcesToLog = new ArrayList();
               int classSize = 0;
               int addressSize = 0;

               for(ClassResourceInfo info : sfb.getClassResourceInfo()) {
                  if (info.getResourceClass() != null) {
                     String address = CxfCdiAutoSetup.Logs.singleSlash(base, info.getURITemplate().getValue());
                     String clazz = CxfCdiAutoSetup.uproxyName(info.getResourceClass().getName());
                     classSize = Math.max(classSize, clazz.length());
                     addressSize = Math.max(addressSize, address.length());
                     int methodSize = 7;
                     int methodStrSize = 0;
                     List<Logs.LogOperationEndpointInfo> toLog = new ArrayList();
                     MethodDispatcher md = info.getMethodDispatcher();

                     for(OperationResourceInfo ori : md.getOperationResourceInfos()) {
                        String httpMethod = ori.getHttpMethod();
                        String currentAddress = CxfCdiAutoSetup.Logs.singleSlash(address, ori.getURITemplate().getValue());
                        String methodToStr = CxfCdiAutoSetup.Logs.toSimpleString(ori.getMethodToInvoke());
                        toLog.add(new Logs.LogOperationEndpointInfo(httpMethod, currentAddress, methodToStr));
                        if (httpMethod != null) {
                           methodSize = Math.max(methodSize, httpMethod.length());
                        }

                        addressSize = Math.max(addressSize, currentAddress.length());
                        methodStrSize = Math.max(methodStrSize, methodToStr.length());
                     }

                     Collections.sort(toLog);
                     resourcesToLog.add(new Logs.LogResourceEndpointInfo(address, clazz, toLog, methodSize, methodStrSize));
                  }
               }

               log.info("REST Application: " + endpoint.getEndpointInfo().getAddress() + " -> " + (String)Optional.ofNullable(app).map(AbstractResourceInfo::getResourceClass).map(Class::getName).map((x$0) -> CxfCdiAutoSetup.uproxyName(x$0)).orElse(""));
               Collections.sort(resourcesToLog);
               resourcesToLog.forEach((resource) -> {
                  log.info("     Service URI: " + CxfCdiAutoSetup.Logs.forceLength(resource.address, addressSize, true) + " -> " + CxfCdiAutoSetup.Logs.forceLength(resource.classname, classSize, true));
                  resource.operations.forEach((info) -> log.info("          " + CxfCdiAutoSetup.Logs.forceLength(info.http, resource.methodSize, false) + " " + CxfCdiAutoSetup.Logs.forceLength(info.address, addressSize, true) + " ->      " + CxfCdiAutoSetup.Logs.forceLength(info.method, resource.methodStrSize, true)));
                  resource.operations.clear();
               });
               resourcesToLog.clear();
               if (((Meecrowave.Builder)Meecrowave.Builder.class.cast(sc.getServletContext().getAttribute("meecrowave.configuration"))).isJaxrsLogProviders()) {
                  ServerProviderFactory spf = (ServerProviderFactory)ServerProviderFactory.class.cast(endpoint.get(ServerProviderFactory.class.getName()));
                  this.dump(log, spf, "MessageBodyReaders", "messageReaders");
                  this.dump(log, spf, "MessageBodyWriters", "messageWriters");
               }
            } else {
               EndpointInfo endpointInfo = endpoint.getEndpointInfo();
               if (endpointInfo.getName() != null) {
                  log.info("@WebService > " + endpointInfo.getName().toString() + " -> " + base);
               }
            }

            return base;
         };
      }

      private void dump(LogFacade log, ServerProviderFactory spf, String description, String fieldName) {
         Field field = ReflectionUtil.getDeclaredField(ProviderFactory.class, fieldName);
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }

         try {
            Collection<ProviderInfo<?>> providers = (Collection)Collection.class.cast(field.get(spf));
            log.info("     " + description);
            providers.stream().map(ProviderInfo::getProvider).forEach((o) -> {
               try {
                  log.info("       - " + o);
               } catch (RuntimeException var3) {
               }

            });
         } catch (IllegalAccessException var7) {
         }

      }
   }
}
