package org.apache.activemq.artemis.api.core;

import java.io.Serializable;

public final class Pair<A, B> implements Serializable {
   private static final long serialVersionUID = -2496357457812368127L;
   private A a;
   private B b;
   private int hash = -1;

   public Pair(A a, B b) {
      this.a = a;
      this.b = b;
   }

   public int hashCode() {
      if (this.hash == -1) {
         if (this.a == null && this.b == null) {
            return super.hashCode();
         }

         this.hash = (this.a == null ? 0 : this.a.hashCode()) + 37 * (this.b == null ? 0 : this.b.hashCode());
      }

      return this.hash;
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof Pair)) {
         return false;
      } else {
         boolean var10000;
         label43: {
            label29: {
               Pair<A, B> pother = (Pair)other;
               if (pother.a == null) {
                  if (this.a != null) {
                     break label29;
                  }
               } else if (!pother.a.equals(this.a)) {
                  break label29;
               }

               if (pother.b == null) {
                  if (this.b == null) {
                     break label43;
                  }
               } else if (pother.b.equals(this.b)) {
                  break label43;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public String toString() {
      return "Pair[a=" + this.a + ", b=" + this.b + "]";
   }

   public void setA(A a) {
      this.hash = -1;
      this.a = a;
   }

   public A getA() {
      return this.a;
   }

   public void setB(B b) {
      this.hash = -1;
      this.b = b;
   }

   public B getB() {
      return this.b;
   }
}
