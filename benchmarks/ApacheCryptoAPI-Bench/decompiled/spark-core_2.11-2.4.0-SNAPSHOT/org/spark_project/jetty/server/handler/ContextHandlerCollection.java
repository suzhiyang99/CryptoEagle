package org.spark_project.jetty.server.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.HandlerContainer;
import org.spark_project.jetty.server.HttpChannelState;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.util.ArrayTernaryTrie;
import org.spark_project.jetty.util.ArrayUtil;
import org.spark_project.jetty.util.Trie;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Context Handler Collection")
public class ContextHandlerCollection extends HandlerCollection {
   private static final Logger LOG = Log.getLogger(ContextHandlerCollection.class);
   private final ConcurrentMap<ContextHandler, Handler> _contextBranches = new ConcurrentHashMap();
   private volatile Trie<Map.Entry<String, Branch[]>> _pathBranches;
   private Class<? extends ContextHandler> _contextClass = ContextHandler.class;

   public ContextHandlerCollection() {
      super(true);
   }

   @ManagedOperation("update the mapping of context path to context")
   public void mapContexts() {
      this._contextBranches.clear();
      if (this.getHandlers() == null) {
         this._pathBranches = new ArrayTernaryTrie<Map.Entry<String, Branch[]>>(false, 16);
      } else {
         Map<String, Branch[]> map = new HashMap();

         for(Handler handler : this.getHandlers()) {
            Branch branch = new Branch(handler);

            for(String contextPath : branch.getContextPaths()) {
               Branch[] branches = (Branch[])map.get(contextPath);
               map.put(contextPath, ArrayUtil.addToArray(branches, branch, Branch.class));
            }

            for(ContextHandler context : branch.getContextHandlers()) {
               this._contextBranches.putIfAbsent(context, branch.getHandler());
            }
         }

         for(Map.Entry<String, Branch[]> entry : map.entrySet()) {
            Branch[] branches = (Branch[])entry.getValue();
            Branch[] sorted = new Branch[branches.length];
            int i = 0;

            for(Branch branch : branches) {
               if (branch.hasVirtualHost()) {
                  sorted[i++] = branch;
               }
            }

            for(Branch branch : branches) {
               if (!branch.hasVirtualHost()) {
                  sorted[i++] = branch;
               }
            }

            entry.setValue(sorted);
         }

         int capacity = 512;

         label60:
         while(true) {
            Trie<Map.Entry<String, Branch[]>> trie = new ArrayTernaryTrie<Map.Entry<String, Branch[]>>(false, capacity);

            for(Map.Entry<String, Branch[]> entry : map.entrySet()) {
               if (!trie.put(((String)entry.getKey()).substring(1), entry)) {
                  capacity += 512;
                  continue label60;
               }
            }

            if (LOG.isDebugEnabled()) {
               for(String ctx : trie.keySet()) {
                  LOG.debug("{}->{}", ctx, Arrays.asList(((Map.Entry)trie.get(ctx)).getValue()));
               }
            }

            this._pathBranches = trie;
            return;
         }
      }
   }

   public void setHandlers(Handler[] handlers) {
      super.setHandlers(handlers);
      if (this.isStarted()) {
         this.mapContexts();
      }

   }

   protected void doStart() throws Exception {
      this.mapContexts();
      super.doStart();
   }

   public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      Handler[] handlers = this.getHandlers();
      if (handlers != null && handlers.length != 0) {
         HttpChannelState async = baseRequest.getHttpChannelState();
         if (async.isAsync()) {
            ContextHandler context = async.getContextHandler();
            if (context != null) {
               Handler branch = (Handler)this._contextBranches.get(context);
               if (branch == null) {
                  context.handle(target, baseRequest, request, response);
               } else {
                  branch.handle(target, baseRequest, request, response);
               }

               return;
            }
         }

         int l;
         if (target.startsWith("/")) {
            for(int limit = target.length() - 1; limit >= 0; limit = l - 2) {
               Map.Entry<String, Branch[]> branches = (Map.Entry)this._pathBranches.getBest((String)target, 1, limit);
               if (branches == null) {
                  break;
               }

               l = ((String)branches.getKey()).length();
               if (l == 1 || target.length() == l || target.charAt(l) == '/') {
                  for(Branch branch : (Branch[])branches.getValue()) {
                     branch.getHandler().handle(target, baseRequest, request, response);
                     if (baseRequest.isHandled()) {
                        return;
                     }
                  }
               }
            }
         } else {
            for(int i = 0; i < handlers.length; ++i) {
               handlers[i].handle(target, baseRequest, request, response);
               if (baseRequest.isHandled()) {
                  return;
               }
            }
         }

      }
   }

   public ContextHandler addContext(String contextPath, String resourceBase) {
      try {
         ContextHandler context = (ContextHandler)this._contextClass.newInstance();
         context.setContextPath(contextPath);
         context.setResourceBase(resourceBase);
         this.addHandler(context);
         return context;
      } catch (Exception e) {
         LOG.debug(e);
         throw new Error(e);
      }
   }

   public Class<?> getContextClass() {
      return this._contextClass;
   }

   public void setContextClass(Class<? extends ContextHandler> contextClass) {
      if (contextClass != null && ContextHandler.class.isAssignableFrom(contextClass)) {
         this._contextClass = contextClass;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final class Branch {
      private final Handler _handler;
      private final ContextHandler[] _contexts;

      Branch(Handler handler) {
         this._handler = handler;
         if (handler instanceof ContextHandler) {
            this._contexts = new ContextHandler[]{(ContextHandler)handler};
         } else if (handler instanceof HandlerContainer) {
            Handler[] contexts = ((HandlerContainer)handler).getChildHandlersByClass(ContextHandler.class);
            this._contexts = new ContextHandler[contexts.length];
            System.arraycopy(contexts, 0, this._contexts, 0, contexts.length);
         } else {
            this._contexts = new ContextHandler[0];
         }

      }

      Set<String> getContextPaths() {
         Set<String> set = new HashSet();

         for(ContextHandler context : this._contexts) {
            set.add(context.getContextPath());
         }

         return set;
      }

      boolean hasVirtualHost() {
         for(ContextHandler context : this._contexts) {
            if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
               return true;
            }
         }

         return false;
      }

      ContextHandler[] getContextHandlers() {
         return this._contexts;
      }

      Handler getHandler() {
         return this._handler;
      }

      public String toString() {
         return String.format("{%s,%s}", this._handler, Arrays.asList(this._contexts));
      }
   }
}
