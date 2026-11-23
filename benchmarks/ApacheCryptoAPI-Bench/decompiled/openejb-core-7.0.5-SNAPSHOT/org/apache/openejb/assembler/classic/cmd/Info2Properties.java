package org.apache.openejb.assembler.classic.cmd;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.ServiceInfo;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.OpenEjbVersion;
import org.apache.openejb.util.SuperProperties;
import org.apache.openejb.util.URISupport;

public class Info2Properties {
   private static final Messages messages = new Messages(Info2Properties.class);
   private static final String defaultServerUrl = "ejbd://localhost:4201";

   public static void main(String[] args) {
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption(option("v", "version", "cmd.properties.opt.version"));
      options.addOption(option("h", "help", "cmd.properties.opt.help"));
      options.addOption(option("s", "server-url", "url", "cmd.properties.opt.server"));
      CommandLine line = null;

      try {
         line = parser.parse(options, args);
      } catch (ParseException var13) {
         help(options);
         System.exit(-1);
      }

      if (line.hasOption("help")) {
         help(options);
         System.exit(0);
      } else if (line.hasOption("version")) {
         OpenEjbVersion.get().print(System.out);
         System.exit(0);
      }

      Properties p = new Properties();
      p.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
      String serverUrl = line.getOptionValue("server-url", "ejbd://localhost:4201");
      p.put("java.naming.provider.url", serverUrl);
      ConfigurationInfo configInfo = null;

      try {
         InitialContext ctx = new InitialContext(p);
         configInfo = (ConfigurationInfo)ctx.lookup("openejb/ConfigurationInfoBusinessRemote");
      } catch (ServiceUnavailableException e) {
         System.out.println(e.getCause().getMessage());
         System.out.println(messages.format("cmd.deploy.serverOffline"));
         System.exit(1);
      } catch (NamingException var12) {
         System.out.println("ConfigurationInfo does not exist in server '" + serverUrl + "', check the server logs to ensure it exists and has not been removed.");
         System.exit(2);
      }

      File tempFile = null;

      try {
         try {
            tempFile = File.createTempFile("configrequest", "txt");
         } catch (Throwable var14) {
            File tmp = new File("tmp");
            if (!tmp.exists() && !tmp.mkdirs()) {
               throw new IOException("Failed to create local tmp directory: " + tmp.getAbsolutePath());
            }

            tempFile = File.createTempFile("configrequest", "txt", tmp);
         }

         if (!tempFile.exists()) {
            throw new IllegalStateException("Failed to create tmp file: " + tempFile.getAbsolutePath());
         }
      } catch (Exception e) {
         System.err.println("Temp file creation failed.");
         e.printStackTrace();
         System.exit(1);
      }

      OpenEjbConfiguration configuration = null;

      try {
         configuration = configInfo.getOpenEjbConfiguration(tempFile);
      } catch (ConfigurationInfo.UnauthorizedException var10) {
         System.err.println("This tool is currently crippled to only work with server's on the same physical machine.  See this JIRA issue for details: http://issues.apache.org/jira/browse/OPENEJB-621");
         System.exit(10);
      }

      printConfig(configuration);
   }

   public static void printLocalConfig() {
      OpenEjbConfiguration configuration = (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
      if (configuration != null) {
         printConfig(configuration);
      }

   }

   public static void printConfig(OpenEjbConfiguration configuration) {
      printConfig(configuration, System.out, JavaSecurityManagers.getSystemProperty("line.separator"));
   }

   public static void printConfig(OpenEjbConfiguration configuration, PrintStream out, String cr) {
      comment(out, cr, i18n("cmd.properties.header"));
      comment(out, cr, "");
      comment(out, cr, "");
      comment(out, cr, "Generated by OpenEJB " + OpenEjbVersion.get().getVersion());
      comment(out, cr, "On " + (new Date()).toString());
      comment(out, cr, "");
      comment(out, cr, "");
      println(out, cr, "");
      println(out, cr, "");
      comment(out, cr, "-------------------------------------------------");
      comment(out, cr, " Components configurable via openejb.xml");
      comment(out, cr, "-------------------------------------------------");
      println(out, cr, "");
      println(out, cr, "");

      for(ServiceInfo info : configuration.containerSystem.containers) {
         print(out, cr, info);
      }

      for(ServiceInfo info : configuration.facilities.connectionManagers) {
         print(out, cr, info);
      }

      for(ServiceInfo info : configuration.facilities.resources) {
         print(out, cr, info);
      }

      print(out, cr, configuration.facilities.securityService);
      print(out, cr, configuration.facilities.transactionService);
      println(out, cr, "");
      comment(out, cr, "-------------------------------------------------");
      comment(out, cr, " Services configured via conf/<id>.properties");
      comment(out, cr, "-------------------------------------------------");
      println(out, cr, "");
      println(out, cr, "");

      for(ServiceInfo info : configuration.facilities.services) {
         print(out, cr, info);
      }

      println(out, cr, "");
      comment(out, cr, "-------------------------------------------------");
      comment(out, cr, " Misc OpenEJB flags and properties");
      comment(out, cr, "-------------------------------------------------");
      println(out, cr, "");
      printSystemProperties(out, cr);
   }

   private static void printSystemProperties(PrintStream out, String cr) {
      try {
         SuperProperties p = new SuperProperties();
         p.setSpaceBetweenProperties(false);
         p.setKeyValueSeparator(" = ");
         p.setLineSeparator(cr);
         copyOpenEjbProperties(JavaSecurityManagers.getSystemProperties(), p);
         copyOpenEjbProperties(SystemInstance.get().getProperties(), p);
         p.store(out, (String)null);
         Properties p2 = JavaSecurityManagers.getSystemProperties();
         String[] misc = new String[]{"os.version", "os.name", "os.arch", "java.version", "java.vendor"};

         for(String prop : misc) {
            comment(out, cr, prop + "=" + p2.get(prop));
         }
      } catch (IOException e) {
         e.printStackTrace(new PrintWriter(new CommentsFilter(out)));
      }

   }

   private static void copyOpenEjbProperties(Properties source, Properties dest) {
      for(Map.Entry<Object, Object> entry : source.entrySet()) {
         if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
            String key = (String)entry.getKey();
            if (key.startsWith("openejb.")) {
               dest.put(entry.getKey(), entry.getValue());
            }
         }
      }

   }

   private static void comment(PrintStream out, String cr, String text) {
      text = text.replaceAll("\n", "\n# ").replace("\n", cr);
      print(out, "# ");
      println(out, cr, text);
   }

   private static void print(PrintStream out, String text) {
      out.print(text);
   }

   private static void println(PrintStream out, String cr, String text) {
      out.print(text.replace("\n", cr) + cr);
      out.flush();
   }

   private static void print(PrintStream out, String cr, ServiceInfo info) {
      try {
         println(out, cr, "");
         comment(out, cr, info.service + "(id=" + info.id + ")");
         comment(out, cr, "className: " + info.className);
         comment(out, cr, "");
         SuperProperties p = new SuperProperties();
         p.setSpaceBetweenProperties(false);
         p.setKeyValueSeparator(" = ");
         p.setLineSeparator(cr);
         String uri = "new://" + info.service;
         if (info.service.matches("Container|Resource|Connector")) {
            try {
               Map query = new HashMap();
               query.put("type", info.types.get(0));
               uri = uri + "?" + URISupport.createQueryString(query);
            } catch (Exception var7) {
            }
         }

         p.put(info.id, uri);

         for(Map.Entry<Object, Object> entry : info.properties.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
               if ("password".equalsIgnoreCase((String)entry.getKey())) {
                  p.put(info.id + "." + entry.getKey(), "xxxx");
               } else {
                  p.put(info.id + "." + entry.getKey(), entry.getValue());
               }
            }
         }

         p.store(out, (String)null);
      } catch (IOException e) {
         out.println("# Printing service(id=" + info.id + ") failed.");
         e.printStackTrace(new PrintWriter(new CommentsFilter(out)));
      }

   }

   private static void help(Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("properties [options]", "\n" + i18n("cmd.properties.description"), options, "\n");
   }

   private static Option option(String shortOpt, String longOpt, String description) {
      OptionBuilder.withLongOpt(longOpt);
      OptionBuilder.withDescription(i18n(description));
      return OptionBuilder.create(shortOpt);
   }

   private static Option option(String shortOpt, String longOpt, String argName, String description) {
      OptionBuilder.withLongOpt(longOpt);
      OptionBuilder.withArgName(argName);
      OptionBuilder.hasArg();
      OptionBuilder.withDescription(i18n(description));
      return OptionBuilder.create(shortOpt);
   }

   private static String i18n(String key) {
      return messages.format(key);
   }

   static class Filter extends FilterOutputStream {
      private boolean pastFirstLine;

      public Filter(OutputStream out) {
         super(out);
      }

      public void write(int b) throws IOException {
         if (this.pastFirstLine) {
            super.write(b);
         } else {
            this.pastFirstLine = b == 10;
         }

      }
   }

   static class CommentsFilter extends FilterOutputStream {
      public CommentsFilter(OutputStream out) {
         super(out);
      }

      public void write(int b) throws IOException {
         super.write(b);
         if (b == 10) {
            super.write(35);
         }

      }
   }
}
