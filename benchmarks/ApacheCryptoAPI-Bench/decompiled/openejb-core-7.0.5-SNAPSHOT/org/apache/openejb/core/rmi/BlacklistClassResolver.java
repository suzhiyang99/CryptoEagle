package org.apache.openejb.core.rmi;

import java.io.ObjectStreamClass;
import java.util.regex.Pattern;
import org.apache.openejb.util.JavaSecurityManagers;

public class BlacklistClassResolver {
   public static final BlacklistClassResolver DEFAULT = new BlacklistClassResolver(toArray(JavaSecurityManagers.getSystemProperty("tomee.serialization.class.blacklist", "org.codehaus.groovy.runtime.,org.apache.commons.collections.functors.,org.apache.xalan,java.lang.Process")), toArray(JavaSecurityManagers.getSystemProperty("tomee.serialization.class.whitelist")));
   public static final Pattern PRIMITIVE_ARRAY = Pattern.compile("^\\[+[BCDFIJSVZ]$");
   private final String[] blacklist;
   private final String[] whitelist;

   protected BlacklistClassResolver(String[] blacklist, String[] whitelist) {
      this.whitelist = whitelist;
      this.blacklist = blacklist;
   }

   protected boolean isBlacklisted(String name) {
      if (PRIMITIVE_ARRAY.matcher(name).matches()) {
         return false;
      } else if (name != null && name.startsWith("[L") && name.endsWith(";")) {
         return this.isBlacklisted(name.substring(2, name.length() - 1));
      } else {
         return this.whitelist != null && !contains(this.whitelist, name) || contains(this.blacklist, name);
      }
   }

   public final ObjectStreamClass check(ObjectStreamClass classDesc) {
      this.check(classDesc.getName());
      return classDesc;
   }

   public final String check(String name) {
      if (this.isBlacklisted(name)) {
         throw new SecurityException(name + " is not whitelisted as deserialisable, prevented before loading it, customize tomee.serialization.class.blacklist and tomee.serialization.class.whitelist to add it to not fail there. -Dtomee.serialization.class.blacklist=- -Dtomee.serialization.class.whitelist=" + name + " for instance (or in conf/system.properties).");
      } else {
         return name;
      }
   }

   private static String[] toArray(String property) {
      return property == null ? null : property.split(" *, *");
   }

   private static boolean contains(String[] list, String name) {
      if (list != null) {
         for(String white : list) {
            if ("*".equals(white) || name.startsWith(white)) {
               return true;
            }
         }
      }

      return false;
   }
}
