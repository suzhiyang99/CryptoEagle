package org.spark_project.jetty.http.pathmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

public class PathSpecSet implements Set<String>, Predicate<String> {
   private final Set<PathSpec> specs = new TreeSet();

   public boolean test(String s) {
      for(PathSpec spec : this.specs) {
         if (spec.matches(s)) {
            return true;
         }
      }

      return false;
   }

   public boolean isEmpty() {
      return this.specs.isEmpty();
   }

   public Iterator<String> iterator() {
      return new Iterator<String>() {
         private Iterator<PathSpec> iter;

         {
            this.iter = PathSpecSet.this.specs.iterator();
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public String next() {
            PathSpec spec = (PathSpec)this.iter.next();
            return spec == null ? null : spec.getDeclaration();
         }

         public void remove() {
            throw new UnsupportedOperationException("Remove not supported by this Iterator");
         }
      };
   }

   public int size() {
      return this.specs.size();
   }

   public boolean contains(Object o) {
      if (o instanceof PathSpec) {
         return this.specs.contains(o);
      } else {
         return o instanceof String ? this.specs.contains(this.toPathSpec((String)o)) : false;
      }
   }

   private PathSpec asPathSpec(Object o) {
      if (o == null) {
         return null;
      } else if (o instanceof PathSpec) {
         return (PathSpec)o;
      } else {
         return o instanceof String ? this.toPathSpec((String)o) : this.toPathSpec(o.toString());
      }
   }

   private PathSpec toPathSpec(String rawSpec) {
      if (rawSpec != null && rawSpec.length() >= 1) {
         return (PathSpec)(rawSpec.charAt(0) == '^' ? new RegexPathSpec(rawSpec) : new ServletPathSpec(rawSpec));
      } else {
         throw new RuntimeException("Path Spec String must start with '^', '/', or '*.': got [" + rawSpec + "]");
      }
   }

   public Object[] toArray() {
      return this.toArray(new String[this.specs.size()]);
   }

   public <T> T[] toArray(T[] a) {
      int i = 0;

      for(PathSpec spec : this.specs) {
         a[i++] = spec.getDeclaration();
      }

      return a;
   }

   public boolean add(String e) {
      return this.specs.add(this.toPathSpec(e));
   }

   public boolean remove(Object o) {
      return this.specs.remove(this.asPathSpec(o));
   }

   public boolean containsAll(Collection<?> coll) {
      for(Object o : coll) {
         if (!this.specs.contains(this.asPathSpec(o))) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection<? extends String> coll) {
      boolean ret = false;

      for(String s : coll) {
         ret |= this.add(s);
      }

      return ret;
   }

   public boolean retainAll(Collection<?> coll) {
      List<PathSpec> collSpecs = new ArrayList();

      for(Object o : coll) {
         collSpecs.add(this.asPathSpec(o));
      }

      return this.specs.retainAll(collSpecs);
   }

   public boolean removeAll(Collection<?> coll) {
      List<PathSpec> collSpecs = new ArrayList();

      for(Object o : coll) {
         collSpecs.add(this.asPathSpec(o));
      }

      return this.specs.removeAll(collSpecs);
   }

   public void clear() {
      this.specs.clear();
   }
}
