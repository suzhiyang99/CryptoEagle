package org.apache.openejb.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.OpenEjbVersion;
import org.apache.openejb.util.OptionsLog;
import org.apache.xbean.finder.ResourceFinder;

public class MainImpl implements Main {
   private static final String BASE_PATH = "META-INF/org.apache.openejb.cli/";
   private static final String MAIN_CLASS_PROPERTY_NAME = "main.class";
   private static ResourceFinder finder;
   private static String locale = "";
   private static final String descriptionBase = "description";
   private static String descriptionI18n;

   public void main(String[] args) {
      args = this.processSystemProperties(args);
      finder = new ResourceFinder("META-INF/org.apache.openejb.cli/");
      locale = Locale.getDefault().getLanguage();
      descriptionI18n = "description." + locale;
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption((String)null, "version", false, "Display version");
      options.addOption("h", "help", false, "Display help");
      options.addOption("e", "errors", false, "Produce execution error messages");
      CommandLine line = null;
      String commandName = null;

      try {
         line = parser.parse(options, args, true);
         List<String> list = line.getArgList();
         if (list.size() > 0) {
            commandName = (String)list.get(0);
            list.remove(0);
         }

         args = line.getArgs();
      } catch (ParseException exp) {
         exp.printStackTrace();
         System.exit(-1);
      }

      if (line.hasOption("version")) {
         OpenEjbVersion.get().print(System.out);
         System.exit(0);
      } else if (line.hasOption("help") || commandName == null || commandName.equals("help")) {
         help();
         System.exit(0);
      }

      Properties props = null;

      try {
         props = finder.findProperties(commandName);
      } catch (IOException var13) {
         System.out.println("Unavailable command: " + commandName);
         help(false);
         System.exit(1);
      }

      if (props == null) {
         System.out.println("Unavailable command: " + commandName);
         help(false);
         System.exit(1);
      }

      String mainClass = props.getProperty("main.class");
      if (mainClass == null) {
         throw new NullPointerException("Command " + commandName + " did not specify a " + "main.class" + " property");
      } else {
         Class<?> clazz = null;

         try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(mainClass);
         } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Main class of command " + commandName + " does not exist: " + mainClass, cnfe);
         }

         Method mainMethod = null;

         try {
            mainMethod = clazz.getMethod("main", String[].class);
         } catch (Exception e) {
            throw new IllegalStateException("Main class of command " + commandName + " does not have a static main method: " + mainClass, e);
         }

         try {
            mainMethod.invoke(clazz, args);
         } catch (Throwable e) {
            if (line.hasOption("errors")) {
               e.printStackTrace();
            }

            System.exit(-10);
         }

      }
   }

   private String[] processSystemProperties(String[] args) {
      ArrayList<String> argsList = new ArrayList();

      for(String arg : args) {
         if (arg.indexOf("-Dopenejb.base") != -1) {
            String prop = arg.substring(arg.indexOf("-D") + 2, arg.indexOf("="));
            String val = arg.substring(arg.indexOf("=") + 1);
            JavaSecurityManagers.setSystemProperty(prop, val);
         }
      }

      SystemInstance systemInstance = null;

      try {
         SystemInstance.init(new Properties());
         OptionsLog.install();
         systemInstance = SystemInstance.get();
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(2);
      }

      for(String arg : args) {
         int idx = arg.indexOf("-D");
         int eq = arg.indexOf("=");
         if (idx >= 0 && eq > idx) {
            String prop = arg.substring(idx + 2, eq);
            String val = arg.substring(eq + 1);
            JavaSecurityManagers.setSystemProperty(prop, val);
            systemInstance.setProperty(prop, val);
         } else {
            argsList.add(arg);
         }
      }

      args = (String[])argsList.toArray(new String[argsList.size()]);
      return args;
   }

   public static Enumeration<URL> doFindCommands() throws IOException {
      return Thread.currentThread().getContextClassLoader().getResources("META-INF/org.apache.openejb.cli/");
   }

   private static void help() {
      help(true);
   }

   private static void help(boolean printHeader) {
      try {
         Options options = new Options();
         ResourceFinder commandFinder = new ResourceFinder("META-INF");
         Map<String, Properties> commands = commandFinder.mapAvailableProperties("org.apache.openejb.cli");

         for(Map.Entry<String, Properties> command : commands.entrySet()) {
            if (!((String)command.getKey()).contains(".")) {
               Properties p = (Properties)command.getValue();
               String description = p.getProperty(descriptionI18n, p.getProperty("description"));
               options.addOption((String)command.getKey(), false, description);
            }
         }

         HelpFormatter formatter = new HelpFormatter();
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         String syntax = "openejb <command> [options] [args]";
         String header = "\nAvailable commands:";
         String footer = "\nTry 'openejb <command> --help' for help on a specific command.\nFor example 'openejb deploy --help'.\nImportant: to display exceptions while running commands, add -e option.\n\nApache OpenEJB -- EJB Container System and Server.\nFor additional information, see http://tomee.apache.org\nBug Reports to <users@tomee.apache.org>";
         if (!printHeader) {
            pw.append("\nAvailable commands:").append("\n\n");
            formatter.printOptions(pw, 74, options, 1, 3);
         } else {
            formatter.printHelp(pw, 74, "openejb <command> [options] [args]", "\nAvailable commands:", options, 1, 3, "\nTry 'openejb <command> --help' for help on a specific command.\nFor example 'openejb deploy --help'.\nImportant: to display exceptions while running commands, add -e option.\n\nApache OpenEJB -- EJB Container System and Server.\nFor additional information, see http://tomee.apache.org\nBug Reports to <users@tomee.apache.org>", false);
         }

         pw.flush();
         String text = sw.toString().replaceAll("\n -", "\n  ");
         text = text.replace("\nApache OpenEJB", "\n\nApache OpenEJB");
         System.out.print(text);
      } catch (IOException e) {
         e.printStackTrace();
      }

   }
}
