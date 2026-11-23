package org.apache.manifoldcf.core;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.UTF8Stdout;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class Obfuscate {
   public static final String _rcsid = "@(#)$Id: Obfuscate.java 988245 2010-08-23 18:39:35Z kwright $";

   private Obfuscate() {
   }

   public static void main(String[] args) {
      if (args.length != 1) {
         System.err.println("Usage: Obfuscate <string>");
         System.exit(1);
      }

      String string = args[0];

      try {
         String ob = ManifoldCF.obfuscate(string);
         UTF8Stdout.println(ob);
      } catch (ManifoldCFException e) {
         e.printStackTrace();
         System.exit(1);
      }

   }
}
