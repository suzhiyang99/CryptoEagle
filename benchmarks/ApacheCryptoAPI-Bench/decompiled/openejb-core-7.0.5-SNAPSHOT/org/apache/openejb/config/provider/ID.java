package org.apache.openejb.config.provider;

public class ID {
   private final String namespace;
   private final String name;

   public ID(String namespace, String name) {
      if (name == null) {
         throw new IllegalArgumentException("name cannot be null");
      } else {
         this.namespace = namespace != null ? namespace.toLowerCase() : null;
         this.name = name.toLowerCase();
      }
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String getName() {
      return this.name;
   }

   public void validate() {
      if (this.namespace == null) {
         throw new IllegalArgumentException("namespace cannot be null");
      } else if (this.name == null) {
         throw new IllegalArgumentException("name cannot be null");
      }
   }

   public static ID parse(String raw) {
      return parse(raw, (String)null);
   }

   public static ID parse(String raw, ID id) {
      return parse(raw, id.getNamespace());
   }

   public static ID parse(String raw, String namespace) {
      if (raw == null) {
         throw new NullPointerException("provider id cannot be null");
      } else {
         String[] parts = raw.split("[#:]");
         if (parts.length == 1) {
            return new ID(namespace, parts[0]);
         } else if (parts.length == 2) {
            return new ID(parts[0], parts[1]);
         } else {
            throw new MalformedProviderNameException(raw);
         }
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ID id = (ID)o;
         if (!this.name.equals(id.name)) {
            return false;
         } else {
            if (this.namespace != null) {
               if (!this.namespace.equals(id.namespace)) {
                  return false;
               }
            } else if (id.namespace != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.namespace != null ? this.namespace.hashCode() : 0;
      result = 31 * result + this.name.hashCode();
      return result;
   }

   public String toString() {
      return "ID{namespace='" + this.namespace + '\'' + ", name='" + this.name + '\'' + '}';
   }
}
