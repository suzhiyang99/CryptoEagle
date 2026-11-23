package org.apache.openejb;

public class Injection {
   private Class target;
   private final String classname;
   private final String name;
   private final String jndiName;

   public Injection(String jndiName, String name, Class target) {
      this.jndiName = jndiName;
      this.name = name;
      this.target = target;
      this.classname = target.getName();
   }

   public Injection(String jndiName, String name, String classname) {
      this.jndiName = jndiName;
      this.name = name;
      this.classname = classname;
      this.target = null;
   }

   public String getJndiName() {
      return this.jndiName;
   }

   public String getName() {
      return this.name;
   }

   public Class getTarget() {
      return this.target;
   }

   public void setTarget(Class<?> target) {
      this.target = target;
   }

   public String getClassname() {
      return this.classname;
   }

   public String toString() {
      return "Injection{target=" + this.classname + ", name='" + this.name + '\'' + ", jndiName='" + this.jndiName + '\'' + '}';
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Injection)) {
         return false;
      } else {
         Injection injection = (Injection)o;
         if (this.name != null) {
            if (!this.name.equals(injection.name)) {
               return false;
            }
         } else if (injection.name != null) {
            return false;
         }

         if (this.classname != null) {
            if (!this.classname.equals(injection.classname)) {
               return false;
            }
         } else if (injection.classname != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int result = this.classname != null ? this.classname.hashCode() : 0;
      result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
      return result;
   }
}
