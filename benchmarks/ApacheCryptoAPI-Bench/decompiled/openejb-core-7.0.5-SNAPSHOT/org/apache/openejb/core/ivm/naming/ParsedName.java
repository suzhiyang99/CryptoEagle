package org.apache.openejb.core.ivm.naming;

import java.io.Serializable;
import java.util.StringTokenizer;

public class ParsedName implements Serializable {
   static final int IS_EQUAL = 0;
   static final int IS_LESS = -1;
   static final int IS_GREATER = 1;
   String[] components;
   int pos;
   int hashcode;

   public ParsedName(String path) {
      path = this.normalize(path);
      if (path != null && !path.equals("/")) {
         if (path.length() > 0) {
            StringTokenizer st = new StringTokenizer(path, "/");
            this.components = new String[st.countTokens()];

            for(int i = 0; st.hasMoreTokens() && i < this.components.length; ++i) {
               this.components[i] = st.nextToken();
            }

            this.hashcode = this.components[0].hashCode();
         } else {
            this.components = new String[]{""};
            this.hashcode = 0;
         }
      } else {
         this.components = new String[1];
         this.components[0] = "";
         this.hashcode = 0;
      }

   }

   public String getComponent() {
      return this.components[this.pos];
   }

   public boolean next() {
      if (this.components.length > this.pos + 1) {
         this.hashcode = this.components[++this.pos].hashCode();
         return true;
      } else {
         return false;
      }
   }

   public void reset() {
      this.pos = 0;
      this.hashcode = this.components[0].hashCode();
   }

   public int getPos() {
      return this.pos;
   }

   public void reset(int pos) {
      if (pos >= 0 && pos < this.components.length) {
         this.pos = pos;
         this.hashcode = this.components[pos].hashCode();
      } else {
         throw new IllegalArgumentException("pos out of range 0 to " + this.components.length);
      }
   }

   public int compareTo(int otherHash) {
      if (this.hashcode == otherHash) {
         return 0;
      } else {
         return this.hashcode > otherHash ? 1 : -1;
      }
   }

   public int getComponentHashCode() {
      return this.hashcode;
   }

   public int compareTo(String other) {
      int otherHash = other.hashCode();
      return this.compareTo(otherHash);
   }

   public static void main(String[] args) {
      ParsedName name = new ParsedName("comp/env/jdbc/mydatabase");

      while(name.next()) {
         System.out.println(name.getComponent());
      }

   }

   public ParsedName remaining() {
      ParsedName name = new ParsedName("");
      int next = this.pos + 1;
      if (next > this.components.length) {
         return name;
      } else {
         String[] dest = new String[this.components.length - next];
         System.arraycopy(this.components, next, dest, 0, dest.length);
         name.components = dest;
         return name;
      }
   }

   public String toString() {
      return "ParsedName{path=" + this.path() + ", component=" + this.getComponent() + '}';
   }

   public String path() {
      if (this.components.length == 0) {
         return "";
      } else {
         StringBuilder buffer = new StringBuilder(this.components[0]);

         for(int i = 1; i < this.components.length; ++i) {
            buffer.append('/');
            buffer.append(this.components[i]);
         }

         return buffer.toString();
      }
   }

   private String normalize(String pathname, int len, int off) {
      if (len == 0) {
         return pathname;
      } else {
         int n;
         for(n = len; n > 0 && pathname.charAt(n - 1) == '/'; --n) {
         }

         if (n == 0) {
            return "/";
         } else {
            StringBuilder sb = new StringBuilder(pathname.length());
            if (off > 0) {
               sb.append(pathname.substring(0, off));
            }

            char prevChar = 0;

            for(int i = off; i < n; ++i) {
               char c = pathname.charAt(i);
               if (prevChar != '/' || c != '/') {
                  sb.append(c);
                  prevChar = c;
               }
            }

            return sb.toString();
         }
      }
   }

   private String normalize(String pathname) {
      int n = pathname.length();
      char prevChar = 0;

      for(int i = 0; i < n; ++i) {
         char c = pathname.charAt(i);
         if (prevChar == '/' && c == '/') {
            return this.normalize(pathname, n, i - 1);
         }

         prevChar = c;
      }

      if (prevChar == '/') {
         return this.normalize(pathname, n, n - 1);
      } else {
         return pathname;
      }
   }
}
