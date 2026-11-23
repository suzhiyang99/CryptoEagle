package org.apache.deltaspike.core.impl.crypto;

public class CipherCli {
   private CipherCli() {
   }

   public static void main(String[] args) throws Exception {
      if (args.length < 5) {
         printHelp();
         System.exit(-1);
      }

      if (!"encode".equals(args[0])) {
         printHelp();
         System.exit(-1);
      }

      String masterPwd = null;
      String plaintext = null;
      String masterSalt = null;
      boolean overwrite = false;

      for(int i = 1; i < args.length; ++i) {
         String arg = args[i];
         if ("-masterPassword".equals(arg) && i < args.length - 1) {
            ++i;
            masterPwd = args[i];
         } else if ("-masterSalt".equals(arg) && i < args.length - 1) {
            ++i;
            masterSalt = args[i];
         } else if ("-plaintext".equals(arg) && i < args.length - 1) {
            ++i;
            plaintext = args[i];
         } else if ("-overwrite".equals(arg)) {
            overwrite = true;
         }
      }

      DefaultCipherService defaultCipherService = new DefaultCipherService();
      if (masterPwd != null && masterSalt != null) {
         String masterSaltHash = defaultCipherService.setMasterHash(masterPwd, masterSalt, overwrite);
         System.out.println("A new master password got set. Hash key is " + masterSaltHash);
      } else if (plaintext != null && masterSalt != null) {
         String encrypted = defaultCipherService.encrypt(plaintext, masterSalt);
         System.out.println("Encrypted value: " + encrypted);
      } else {
         printHelp();
         System.exit(-1);
      }

   }

   private static void printHelp() {
      StringBuilder usage = new StringBuilder(1024);
      usage.append("To create a master password use:");
      usage.append("\n$> java -jar deltaspike-core-impl.jar encode -masterPassword yourMasterPassword -masterSalt someSecretOnlyKnownToYouAndTheApplication");
      usage.append("\n   you can also specify -overwrite to replace an existing masterpassword.");
      usage.append("\n\nFor encrypting a secret with a previously stored masterPassword use:");
      usage.append("\n$> java -jar deltaspike-core-impl.jar encode -plaintext plaintextToEncrypt -masterSalt someSecretOnlyKnownToYouAndTheApplication");
      usage.append("\n\nVisit https://deltaspike.apache.org for more information.\n");
      System.out.print(usage.toString());
   }
}
