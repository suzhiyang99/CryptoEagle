package org.spark_project.jetty.http.pathmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("Path Mappings")
public class PathMappings<E> implements Iterable<MappedResource<E>>, Dumpable {
   private static final Logger LOG = Log.getLogger(PathMappings.class);
   private List<MappedResource<E>> mappings = new ArrayList();
   private MappedResource<E> defaultResource = null;
   private MappedResource<E> rootResource = null;

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      ContainerLifeCycle.dump(out, indent, this.mappings);
   }

   @ManagedAttribute(
      value = "mappings",
      readonly = true
   )
   public List<MappedResource<E>> getMappings() {
      return this.mappings;
   }

   public void reset() {
      this.mappings.clear();
   }

   public void removeIf(Predicate<MappedResource<E>> predicate) {
      this.mappings.removeIf(predicate);
   }

   public List<MappedResource<E>> getMatches(String path) {
      boolean matchRoot = "/".equals(path);
      List<MappedResource<E>> ret = new ArrayList();
      int len = this.mappings.size();

      for(int i = 0; i < len; ++i) {
         MappedResource<E> mr = (MappedResource)this.mappings.get(i);
         switch (mr.getPathSpec().group) {
            case ROOT:
               if (matchRoot) {
                  ret.add(mr);
               }
               break;
            case DEFAULT:
               if (matchRoot || mr.getPathSpec().matches(path)) {
                  ret.add(mr);
               }
               break;
            default:
               if (mr.getPathSpec().matches(path)) {
                  ret.add(mr);
               }
         }
      }

      return ret;
   }

   public MappedResource<E> getMatch(String path) {
      if (path.equals("/") && this.rootResource != null) {
         return this.rootResource;
      } else {
         int len = this.mappings.size();

         for(int i = 0; i < len; ++i) {
            MappedResource<E> mr = (MappedResource)this.mappings.get(i);
            if (mr.getPathSpec().matches(path)) {
               return mr;
            }
         }

         return this.defaultResource;
      }
   }

   public Iterator<MappedResource<E>> iterator() {
      return this.mappings.iterator();
   }

   public void put(PathSpec pathSpec, E resource) {
      MappedResource<E> entry = new MappedResource<E>(pathSpec, resource);
      switch (pathSpec.group) {
         case ROOT:
            this.rootResource = entry;
            break;
         case DEFAULT:
            this.defaultResource = entry;
      }

      this.mappings.add(entry);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Added {} to {}", entry, this);
      }

      Collections.sort(this.mappings);
   }

   public String toString() {
      return String.format("%s[size=%d]", this.getClass().getSimpleName(), this.mappings.size());
   }
}
