package org.apache.wicket.util.lang;

import org.apache.wicket.util.io.IClusterable;

public class PackageName implements IClusterable {
   private static final long serialVersionUID = 1L;
   private final String name;

   public static PackageName forClass(Class<?> c) {
      return new PackageName(Packages.extractPackageName(c));
   }

   public static PackageName forPackage(Package p) {
      return new PackageName(p.getName());
   }

   private PackageName(String name) {
      this.name = name;
   }

   public boolean equals(Object that) {
      return that instanceof PackageName ? ((PackageName)that).name.equals(this.name) : false;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
      return result;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }
}
