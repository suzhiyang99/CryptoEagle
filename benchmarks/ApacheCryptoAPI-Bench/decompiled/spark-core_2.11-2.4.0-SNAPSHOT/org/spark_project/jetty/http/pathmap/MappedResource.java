package org.spark_project.jetty.http.pathmap;

import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;

@ManagedObject("Mapped Resource")
public class MappedResource<E> implements Comparable<MappedResource<E>> {
   private final PathSpec pathSpec;
   private final E resource;

   public MappedResource(PathSpec pathSpec, E resource) {
      this.pathSpec = pathSpec;
      this.resource = resource;
   }

   public int compareTo(MappedResource<E> other) {
      return this.pathSpec.compareTo(other.pathSpec);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         MappedResource<?> other = (MappedResource)obj;
         if (this.pathSpec == null) {
            if (other.pathSpec != null) {
               return false;
            }
         } else if (!this.pathSpec.equals(other.pathSpec)) {
            return false;
         }

         return true;
      }
   }

   @ManagedAttribute(
      value = "path spec",
      readonly = true
   )
   public PathSpec getPathSpec() {
      return this.pathSpec;
   }

   @ManagedAttribute(
      value = "resource",
      readonly = true
   )
   public E getResource() {
      return this.resource;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.pathSpec == null ? 0 : this.pathSpec.hashCode());
      return result;
   }

   public String toString() {
      return String.format("MappedResource[pathSpec=%s,resource=%s]", this.pathSpec, this.resource);
   }
}
