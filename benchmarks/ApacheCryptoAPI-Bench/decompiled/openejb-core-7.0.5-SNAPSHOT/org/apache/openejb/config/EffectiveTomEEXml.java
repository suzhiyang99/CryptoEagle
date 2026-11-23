package org.apache.openejb.config;

import java.io.File;
import java.util.Arrays;
import javax.xml.bind.Marshaller;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.cli.SystemExitException;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.Openejb;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.util.JavaSecurityManagers;

public final class EffectiveTomEEXml {
   public static void main(String[] args) throws Exception {
      CommandLine line = parseCommand(args);
      if (line != null) {
         Openejb openejb = JaxbOpenejb.readConfig(findXml(line).getCanonicalPath());
         ConfigurationFactory configFact = new ConfigurationFactory();

         for(Resource r : openejb.getResource()) {
            ResourceInfo ri = (ResourceInfo)configFact.configureService((Service)r, ResourceInfo.class);
            if (!ri.properties.containsKey("SkipImplicitAttributes")) {
               ri.properties.put("SkipImplicitAttributes", "false");
            }

            r.getProperties().clear();
            r.getProperties().putAll(ri.properties);
         }

         Marshaller marshaller = JaxbOpenejb.getContext(Openejb.class).createMarshaller();
         marshaller.setProperty("jaxb.formatted.output", true);
         marshaller.marshal(openejb, System.out);
      }
   }

   private static CommandLine parseCommand(String[] args) throws SystemExitException {
      Options options = new Options();
      OptionBuilder.hasArg(true);
      OptionBuilder.withLongOpt("path");
      OptionBuilder.withDescription("[openejb|tomee].xml path");
      options.addOption(OptionBuilder.create("p"));

      CommandLine line;
      try {
         line = (new PosixParser()).parse(options, args);
      } catch (ParseException var4) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
         return null;
      } else {
         return line;
      }
   }

   private static File findXml(CommandLine line) {
      File xml = null;
      if (line.hasOption("path")) {
         xml = new File(line.getOptionValue("path"));
      } else {
         for(String config : Arrays.asList("tomee.xml", "openejb.xml")) {
            xml = new File(JavaSecurityManagers.getSystemProperty("openejb.base", JavaSecurityManagers.getSystemProperty("openejb.home", JavaSecurityManagers.getSystemProperty("catalina.base", "missing"))), config);
            if (xml.isFile()) {
               break;
            }
         }
      }

      if (xml != null && xml.isFile()) {
         return xml;
      } else {
         throw new IllegalArgumentException(xml + " doesnt exist");
      }
   }

   private static void help(Options options) {
      (new HelpFormatter()).printHelp("effectivetomee [options] <value>", "\n", options, "\n");
   }

   private EffectiveTomEEXml() {
   }
}
