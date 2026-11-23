package org.apache.openejb.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.cli.SystemExitException;

public final class ListSetters {
   public static void main(String[] args) throws Exception {
      Options options = new Options();
      OptionBuilder.isRequired(true);
      OptionBuilder.hasArg(true);
      OptionBuilder.withLongOpt("class");
      OptionBuilder.withDescription("the class to introspect");
      options.addOption(OptionBuilder.create("c"));

      CommandLine line;
      try {
         line = (new PosixParser()).parse(options, args);
      } catch (ParseException var12) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
      } else {
         String clazz = line.getOptionValue("class");
         Collection<Class<?>> ancestors = Classes.ancestors(Thread.currentThread().getContextClassLoader().loadClass(clazz));
         List<String> list = new LinkedList();

         for(Class<?> c : ancestors) {
            for(Method m : c.getDeclaredMethods()) {
               if (Modifier.isPublic(m.getModifiers()) && m.getName().startsWith("set") && m.getGenericParameterTypes().length == 1) {
                  list.add(m.getName().substring(3));
               }
            }
         }

         Collections.sort(list);

         for(String s : list) {
            System.out.println("- " + s);
         }

      }
   }

   private static void help(Options options) {
      (new HelpFormatter()).printHelp("setters [options] <value>", "\n", options, "\n");
   }

   private ListSetters() {
   }
}
