package org.apache.openejb.config;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.PasswordCipherException;
import org.apache.openejb.cipher.PasswordCipherFactory;
import org.apache.openejb.cli.SystemExitException;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.Messages;
import org.apache.xbean.finder.ResourceFinder;

public class Cipher {
   private static final Messages messages = new Messages(Cipher.class);

   public static void main(String[] args) throws SystemExitException {
      CommandLineParser parser = new PosixParser();
      Options options = new Options();
      options.addOption(option("h", "help", "cmd.cipher.opt.help"));
      options.addOption(option("c", "cipher", "c", "cmd.cipher.opt.impl"));
      options.addOption(option("d", "decrypt", "cmd.cipher.opt.decrypt"));
      options.addOption(option("e", "encrypt", "cmd.cipher.opt.encrypt"));

      CommandLine line;
      try {
         line = parser.parse(options, args);
      } catch (ParseException var9) {
         help(options);
         throw new SystemExitException(-1);
      }

      if (line.hasOption("help")) {
         help(options);
      } else {
         String cipherName = "Static3DES";
         if (line.hasOption("cipher")) {
            cipherName = line.getOptionValue("cipher");
         }

         if (line.getArgList().size() != 1) {
            System.out.println("Must specify either a plain text to encrypt or a ciphered value to decrypt.");
            help(options);
         } else {
            PasswordCipher cipher;
            try {
               cipher = PasswordCipherFactory.getPasswordCipher(cipherName);
            } catch (PasswordCipherException var8) {
               System.out.println("Could not load password cipher implementation class. Check your classpath.");
               availableCiphers();
               throw new SystemExitException(-1);
            }

            if (line.hasOption("decrypt")) {
               String pwdArg = (String)line.getArgList().get(0);
               char[] encryptdPassword = pwdArg.toCharArray();
               System.out.println(cipher.decrypt(encryptdPassword));
            } else {
               String plainPassword = (String)line.getArgList().get(0);
               System.out.println(new String(cipher.encrypt(plainPassword)));
            }

         }
      }
   }

   private static void availableCiphers() {
      try {
         ResourceFinder finder = new ResourceFinder("META-INF/");
         Map<String, Class<? extends PasswordCipher>> impls = finder.mapAllImplementations(PasswordCipher.class);
         System.out.println("Available ciphers are: " + Join.join(", ", (Collection)impls.keySet()));
      } catch (Exception var2) {
      }

   }

   private static void help(Options options) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("cipher [options] <value>", "\n" + i18n("cmd.cipher.description"), options, "\n");
      System.out.println("");
      availableCiphers();
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
}
