package org.apache.manifoldcf.core.system;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.manifoldcf.core.common.Base64;
import org.apache.manifoldcf.core.database.ConnectionFactory;
import org.apache.manifoldcf.core.interfaces.CacheManagerFactory;
import org.apache.manifoldcf.core.interfaces.ConfigurationNode;
import org.apache.manifoldcf.core.interfaces.DBInterfaceFactory;
import org.apache.manifoldcf.core.interfaces.ICacheManager;
import org.apache.manifoldcf.core.interfaces.IDBInterface;
import org.apache.manifoldcf.core.interfaces.IPollingHook;
import org.apache.manifoldcf.core.interfaces.IShutdownHook;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFConfiguration;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.StringSet;
import org.apache.manifoldcf.core.interfaces.ThreadContextFactory;

public class ManifoldCF {
   public static final String _rcsid = "@(#)$Id: ManifoldCF.java 988245 2010-08-23 18:39:35Z kwright $";
   public static final String NODE_LIBDIR = "libdir";
   public static final String ATTRIBUTE_PATH = "path";
   protected static String processID = null;
   protected static File workingDirectory = null;
   protected static ManifoldCFResourceLoader resourceLoader = null;
   protected static FileTrack tracker = null;
   protected static DatabaseShutdown dbShutdown = null;
   protected static final List<IShutdownHook> cleanupHooks = new ArrayList();
   protected static final List<IPollingHook> pollingHooks = new ArrayList();
   protected static Thread shutdownThread = new ShutdownThread();
   protected static int initializeLevel;
   protected static boolean alreadyClosed;
   protected static boolean alreadyShutdown;
   protected static Integer initializeFlagLock;
   protected static String mcfVersion;
   protected static String masterDatabaseName;
   protected static String masterDatabaseUsername;
   protected static String masterDatabasePassword;
   protected static ManifoldCFConfiguration localConfiguration;
   protected static long propertyFilelastMod;
   protected static String propertyFilePath;
   protected static final String applicationName = "lcf";
   public static final String lcfConfigFileProperty = "org.apache.manifoldcf.configfile";
   public static final String versionProperty = "org.apache.manifoldcf.versionstring";
   public static final String processIDProperty = "org.apache.manifoldcf.processid";
   public static final String masterDatabaseNameProperty = "org.apache.manifoldcf.database.name";
   public static final String masterDatabaseUsernameProperty = "org.apache.manifoldcf.database.username";
   public static final String masterDatabasePasswordProperty = "org.apache.manifoldcf.database.password";
   public static final String databaseHandleMaxcountProperty = "org.apache.manifoldcf.database.maxhandles";
   public static final String databaseHandleTimeoutProperty = "org.apache.manifoldcf.database.handletimeout";
   public static final String databaseConnectionTrackingProperty = "org.apache.manifoldcf.database.connectiontracking";
   public static final String databaseQueryMaxTimeProperty = "org.apache.manifoldcf.database.maxquerytime";
   public static final String logConfigFileProperty = "org.apache.manifoldcf.logconfigfile";
   public static final String fileResourcesProperty = "org.apache.manifoldcf.fileresources";
   public static final String lockManagerImplementation = "org.apache.manifoldcf.lockmanagerclass";
   public static final String databaseImplementation = "org.apache.manifoldcf.databaseimplementationclass";
   public static final String authImplementation = "org.apache.manifoldcf.authimplementationclass";
   public static final String configSignalCommandProperty = "org.apache.manifoldcf.configuration.change.command";
   public static final String maintenanceFileSignalProperty = "org.apache.manifoldcf.database.maintenanceflag";
   public static final String saltProperty = "org.apache.manifoldcf.salt";
   protected static final int IV_LENGTH = 16;
   private static String OBFUSCATION_PASSCODE;
   private static String OBFUSCATION_SALT;

   /** @deprecated */
   @Deprecated
   public static void resetEnvironment() {
      resetEnvironment(ThreadContextFactory.make());
   }

   public static void resetEnvironment(IThreadContext threadContext) {
      synchronized(initializeFlagLock) {
         if (initializeLevel > 0) {
            cleanUpEnvironment(threadContext);
            processID = null;
            mcfVersion = null;
            masterDatabaseName = null;
            masterDatabaseUsername = null;
            masterDatabasePassword = null;
            localConfiguration = null;
            propertyFilelastMod = -1L;
            propertyFilePath = null;
            alreadyClosed = false;
            alreadyShutdown = false;
            initializeLevel = 0;
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public static void initializeEnvironment() throws ManifoldCFException {
      initializeEnvironment(ThreadContextFactory.make());
   }

   public static void initializeEnvironment(IThreadContext threadContext) throws ManifoldCFException {
      synchronized(initializeFlagLock) {
         if (initializeLevel == 0) {
            try {
               Properties props = System.getProperties();
               propertyFilePath = (String)props.get("org.apache.manifoldcf.configfile");
               if (propertyFilePath == null) {
                  System.err.println("Couldn't find org.apache.manifoldcf.configfile property; using default");
                  String configPath = (String)props.get("user.home") + "/" + "lcf";
                  configPath = configPath.replace('\\', '/');
                  propertyFilePath = (new File(configPath, "properties.xml")).toString();
               }

               workingDirectory = (new File(propertyFilePath)).getAbsoluteFile().getParentFile();
               resourceLoader = new ManifoldCFResourceLoader(Thread.currentThread().getContextClassLoader());
               localConfiguration = new OverrideableManifoldCFConfiguration();
               checkProperties();
               processID = getStringProperty("org.apache.manifoldcf.processid", "");
               if (processID.length() > 16) {
                  throw new ManifoldCFException("Process ID cannot exceed 16 characters!");
               }

               File logConfigFile = getFileProperty("org.apache.manifoldcf.logconfigfile");
               if (logConfigFile == null) {
                  System.err.println("Couldn't find org.apache.manifoldcf.logconfigfile property; using default");
                  String configPath = (String)props.get("user.home") + "/" + "lcf";
                  configPath = configPath.replace('\\', '/');
                  logConfigFile = new File(configPath, "logging.xml");
               }

               synchronized(cleanupHooks) {
                  cleanupHooks.clear();
               }

               synchronized(pollingHooks) {
                  pollingHooks.clear();
               }

               Logging.initializeLoggingSystem(logConfigFile);
               Logging.initializeLoggers();
               Logging.setLogLevels(threadContext);
               mcfVersion = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.versionstring", "unknown version");
               masterDatabaseName = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.database.name", "dbname");
               masterDatabaseUsername = LockManagerFactory.getStringProperty(threadContext, "org.apache.manifoldcf.database.username", "manifoldcf");
               masterDatabasePassword = LockManagerFactory.getPossiblyObfuscatedStringProperty(threadContext, "org.apache.manifoldcf.database.password", "local_pg_passwd");
               registerConnectorServices();
               addPollingHook(new CachePoll());
               tracker = new FileTrack();
               addShutdownHook(tracker);
               addShutdownHook(new DatabaseShutdown());
               DBInterfaceFactory.make(threadContext, masterDatabaseName, masterDatabaseUsername, masterDatabasePassword).openDatabase();
            } catch (ManifoldCFException e) {
               throw new ManifoldCFException("Initialization failed: " + e.getMessage(), e, 3);
            }
         }

         ++initializeLevel;
      }
   }

   protected static void registerConnectorServices() throws ManifoldCFException {
      try {
         Class connectorServicesManifoldCF = findClass("org.apache.manifoldcf.connectorcommon.system.ManifoldCF");
         Method m = connectorServicesManifoldCF.getMethod("registerConnectorServices");
         m.invoke(new Object[0]);
      } catch (ClassNotFoundException e) {
         Logging.root.warn("Could not find connectorcommon main class: " + e.getMessage(), e);
      } catch (NoSuchMethodException e) {
         Logging.root.warn("ManifoldCF.registerConnectorServices not found: " + e.getMessage(), e);
      } catch (IllegalAccessException e) {
         Logging.root.warn("Connectorcommon main class had illegal access: " + e.getMessage(), e);
      } catch (InvocationTargetException e) {
         Throwable z = e.getTargetException();
         if (z instanceof Error) {
            throw (Error)z;
         }

         if (z instanceof RuntimeException) {
            throw (RuntimeException)z;
         }

         throw new RuntimeException("Unknown exception type: " + z.getClass().getName() + ": " + z.getMessage(), z);
      }

   }

   public static final String getProcessID() {
      return processID;
   }

   public static final ManifoldCFConfiguration getConfiguration() {
      return localConfiguration;
   }

   public static final void checkProperties() throws ManifoldCFException {
      File f = new File(propertyFilePath);

      try {
         if (propertyFilelastMod == f.lastModified()) {
            System.err.println("Configuration file not read because it didn't change");
            return;
         }

         InputStream is = new FileInputStream(f);

         try {
            localConfiguration.fromXML(is);
            System.err.println("Configuration file successfully read");
            propertyFilelastMod = f.lastModified();
         } finally {
            is.close();
         }
      } catch (Exception e) {
         throw new ManifoldCFException("Could not read configuration file '" + f.toString() + "'", e);
      }

      ArrayList libDirs = new ArrayList();
      int i = 0;

      while(i < localConfiguration.getChildCount()) {
         ConfigurationNode cn = localConfiguration.findChild(i++);
         if (cn.getType().equals("libdir")) {
            String path = cn.getAttributeValue("path");
            if (path == null) {
               throw new ManifoldCFException("Node type 'libdir' requires a 'path attribute");
            }

            libDirs.add(resolvePath(path));
         }
      }

      resourceLoader.setClassPath(libDirs);
   }

   public static File resolvePath(String path) {
      File r = new File(path);
      return r.isAbsolute() ? r : new File(workingDirectory, path);
   }

   public static String getProperty(String s) {
      return localConfiguration.getProperty(s);
   }

   public static File getFileProperty(String s) {
      String value = getProperty(s);
      return value == null ? null : resolvePath(value);
   }

   public static String getStringProperty(String s, String defaultValue) {
      return localConfiguration.getStringProperty(s, defaultValue);
   }

   public static boolean getBooleanProperty(String s, boolean defaultValue) throws ManifoldCFException {
      return localConfiguration.getBooleanProperty(s, defaultValue);
   }

   public static int getIntProperty(String s, int defaultValue) throws ManifoldCFException {
      return localConfiguration.getIntProperty(s, defaultValue);
   }

   public static long getLongProperty(String s, long defaultValue) throws ManifoldCFException {
      return localConfiguration.getLongProperty(s, defaultValue);
   }

   public static double getDoubleProperty(String s, double defaultValue) throws ManifoldCFException {
      return localConfiguration.getDoubleProperty(s, defaultValue);
   }

   public static void ensureFolder(String path) throws ManifoldCFException {
      try {
         File f = new File(path);
         if (!f.isDirectory()) {
            f.mkdirs();
         }

      } catch (Exception e) {
         throw new ManifoldCFException("Can't make folder", e, 0);
      }
   }

   public static void deleteFolder(String path) {
      File directoryPath = new File(path);
      recursiveDelete(directoryPath);
   }

   public static void recursiveDelete(File directoryPath) {
      if (directoryPath.exists()) {
         if (directoryPath.isDirectory()) {
            File[] children = directoryPath.listFiles();
            if (children != null) {
               int i = 0;

               while(i < children.length) {
                  File x = children[i++];
                  recursiveDelete(x);
               }
            }
         }

         directoryPath.delete();
      }
   }

   public static boolean isFolder(String path) {
      File f = new File(path);
      return f.isDirectory();
   }

   public static String safeFileName(String value) {
      StringBuilder rval = new StringBuilder();
      int i = 0;

      while(i < value.length()) {
         char x = value.charAt(i++);
         if (x != '/' && x != '"' && x != '\\' && x != '|' && (x < 0 || x >= ' ') && x != '+' && x != ',' && x != ':' && x != ';' && x != '<' && x != '>' && x != '=' && x != '[' && x != ']' && x != '&') {
            rval.append(x);
         } else {
            rval.append("&").append(Integer.toString(x)).append("!");
         }
      }

      return rval.toString();
   }

   public static String getMcfVersion() {
      return mcfVersion;
   }

   public static String getMasterDatabaseName() {
      return masterDatabaseName;
   }

   public static String getMasterDatabaseUsername() {
      return masterDatabaseUsername;
   }

   public static String getMasterDatabasePassword() {
      return masterDatabasePassword;
   }

   public static String getChildDatabaseName(IDBInterface companyDatabase, String childDBIdentifier) {
      return companyDatabase.getDatabaseName() + "_" + childDBIdentifier;
   }

   public static String hash(String input) throws ManifoldCFException {
      MessageDigest hash = startHash();
      addToHash(hash, input);
      return getHashValue(hash);
   }

   public static MessageDigest startHash() throws ManifoldCFException {
      try {
         return MessageDigest.getInstance("SHA");
      } catch (Exception e) {
         throw new ManifoldCFException("Couldn't encrypt: " + e.getMessage(), e, 0);
      }
   }

   public static void addToHash(MessageDigest digest, String input) throws ManifoldCFException {
      try {
         byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
         digest.update(inputBytes);
      } catch (Exception e) {
         throw new ManifoldCFException("Couldn't encrypt: " + e.getMessage(), e, 0);
      }
   }

   public static String getHashValue(MessageDigest digest) throws ManifoldCFException {
      try {
         byte[] encryptedBytes = digest.digest();
         StringBuilder rval = new StringBuilder();
         int i = 0;

         while(i < encryptedBytes.length) {
            byte x = encryptedBytes[i++];
            rval.append(writeNibble(x >> 4 & 15));
            rval.append(writeNibble(x & 15));
         }

         return rval.toString();
      } catch (Exception e) {
         throw new ManifoldCFException("Couldn't encrypt: " + e.getMessage(), e, 0);
      }
   }

   protected static String getSaltValue(IThreadContext threadContext) throws ManifoldCFException {
      String saltValue = LockManagerFactory.getProperty(threadContext, "org.apache.manifoldcf.salt");
      if (saltValue != null && saltValue.length() != 0) {
         return saltValue;
      } else {
         throw new ManifoldCFException("Missing required SALT value");
      }
   }

   protected static Cipher getCipher(IThreadContext threadContext, int mode, String passCode, byte[] iv) throws ManifoldCFException {
      return getCipher(getSaltValue(threadContext), mode, passCode, iv);
   }

   protected static Cipher getCipher(String saltValue, int mode, String passCode, byte[] iv) throws ManifoldCFException {
      try {
         SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         KeySpec keySpec = new PBEKeySpec(passCode.toCharArray(), saltValue.getBytes(StandardCharsets.UTF_8), 1024, 128);
         SecretKey secretKey = factory.generateSecret(keySpec);
         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
         SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
         IvParameterSpec parameterSpec = new IvParameterSpec(iv);
         cipher.init(mode, key, parameterSpec);
         return cipher;
      } catch (GeneralSecurityException gse) {
         throw new ManifoldCFException("Could not build a cipher: " + gse.getMessage(), gse);
      }
   }

   protected static byte[] getSecureRandom() {
      SecureRandom random = new SecureRandom();
      byte[] iv = new byte[16];
      random.nextBytes(iv);
      return iv;
   }

   public static String obfuscate(String input) throws ManifoldCFException {
      return encrypt(OBFUSCATION_SALT, OBFUSCATION_PASSCODE, input);
   }

   public static String deobfuscate(String input) throws ManifoldCFException {
      return decrypt(OBFUSCATION_SALT, OBFUSCATION_PASSCODE, input);
   }

   public static String encrypt(String saltValue, String passCode, String input) throws ManifoldCFException {
      if (input == null) {
         return null;
      } else if (input.length() == 0) {
         return input;
      } else {
         try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] iv = getSecureRandom();
            os.write(iv);
            os.flush();
            Cipher cipher = getCipher((String)saltValue, 1, passCode, iv);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            Writer w = new OutputStreamWriter(cos, StandardCharsets.UTF_8);
            w.write(input);
            w.flush();
            cos.flush();
            cos.close();
            byte[] bytes = os.toByteArray();
            return (new Base64()).encodeByteArray(bytes);
         } catch (IOException e) {
            throw new ManifoldCFException(e.getMessage(), e);
         }
      }
   }

   public static String decrypt(String saltValue, String passCode, String input) throws ManifoldCFException {
      if (input == null) {
         return null;
      } else if (input.length() == 0) {
         return input;
      } else {
         try {
            ByteArrayInputStream is = new ByteArrayInputStream((new Base64()).decodeString(input));
            byte[] iv = new byte[16];

            int amt;
            for(int pointer = 0; pointer < iv.length; pointer += amt) {
               amt = is.read(iv, pointer, iv.length - pointer);
               if (amt == -1) {
                  throw new ManifoldCFException("String can't be decrypted: too short");
               }
            }

            Cipher cipher = getCipher((String)saltValue, 2, passCode, iv);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            InputStreamReader reader = new InputStreamReader(cis, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[65536];

            while(true) {
               int amt = reader.read(buffer, 0, buffer.length);
               if (amt == -1) {
                  return sb.toString();
               }

               sb.append(buffer, 0, amt);
            }
         } catch (IOException e) {
            throw new ManifoldCFException(e.getMessage(), e);
         }
      }
   }

   protected static char writeNibble(int value) {
      return value >= 10 ? (char)(value - 10 + 65) : (char)(value + 48);
   }

   protected static int readNibble(char value) throws ManifoldCFException {
      if (value >= 'A' && value <= 'F') {
         return value - 65 + 10;
      } else if (value >= '0' && value <= '9') {
         return value - 48;
      } else {
         throw new ManifoldCFException("Bad hexadecimal value", 0);
      }
   }

   public static void createSystemDatabase(IThreadContext threadcontext, String masterUsername, String masterPassword) throws ManifoldCFException {
      String databaseName = getMasterDatabaseName();
      String databaseUsername = getMasterDatabaseUsername();
      String databasePassword = getMasterDatabasePassword();
      IDBInterface master = DBInterfaceFactory.make(threadcontext, databaseName, databaseUsername, databasePassword);
      master.createUserAndDatabase(masterUsername, masterPassword, (StringSet)null);
   }

   public static void dropSystemDatabase(IThreadContext threadcontext, String masterUsername, String masterPassword) throws ManifoldCFException {
      String databaseName = getMasterDatabaseName();
      String databaseUsername = getMasterDatabaseUsername();
      String databasePassword = getMasterDatabasePassword();
      IDBInterface master = DBInterfaceFactory.make(threadcontext, databaseName, databaseUsername, databasePassword);
      master.dropUserAndDatabase(masterUsername, masterPassword, (StringSet)null);
   }

   public static File createTempDir(String prefix, String suffix) throws ManifoldCFException {
      String tempDirLocation = System.getProperty("java.io.tmpdir");
      if (tempDirLocation == null) {
         throw new ManifoldCFException("Can't find temporary directory!");
      } else {
         File tempDir = new File(tempDirLocation);
         long currentFileID = System.currentTimeMillis();
         long currentFileHash = currentFileID << 5 ^ currentFileID >> 3;
         int raceConditionRepeat = 0;

         while(raceConditionRepeat < 1000) {
            File tempCertDir = new File(tempDir, prefix + currentFileHash + suffix);
            if (tempCertDir.mkdir()) {
               return tempCertDir;
            }

            if (tempCertDir.exists()) {
               ++currentFileHash;
            } else {
               ++raceConditionRepeat;
               Thread.yield();
            }
         }

         throw new ManifoldCFException("Temporary directory appears to be unwritable");
      }
   }

   public static void addFile(File f) {
      tracker.addFile(f);
   }

   public static void deleteFile(File f) {
      tracker.deleteFile(f);
   }

   public static boolean checkMaintenanceUnderway() {
      String fileToCheck = getProperty("org.apache.manifoldcf.database.maintenanceflag");
      if (fileToCheck != null && fileToCheck.length() > 0) {
         File f = new File(fileToCheck);
         return f.exists();
      } else {
         return false;
      }
   }

   public static void noteConfigurationChange() throws ManifoldCFException {
      String configChangeSignalCommand = getProperty("org.apache.manifoldcf.configuration.change.command");
      if (configChangeSignalCommand != null && configChangeSignalCommand.length() != 0) {
         ArrayList list = new ArrayList();

         StringBuilder argBuffer;
         for(int currentIndex = 0; currentIndex < configChangeSignalCommand.length(); list.add(argBuffer.toString())) {
            while(currentIndex < configChangeSignalCommand.length()) {
               char x = configChangeSignalCommand.charAt(currentIndex);
               if (x < 0 || x > ' ') {
                  break;
               }

               ++currentIndex;
            }

            argBuffer = new StringBuilder();
            boolean isQuoted = false;

            while(currentIndex < configChangeSignalCommand.length()) {
               char x = configChangeSignalCommand.charAt(currentIndex);
               if (isQuoted) {
                  if (x == '"') {
                     ++currentIndex;
                     isQuoted = false;
                  } else if (x == '\\') {
                     ++currentIndex;
                     if (currentIndex >= configChangeSignalCommand.length()) {
                        break;
                     }

                     x = configChangeSignalCommand.charAt(currentIndex);
                     argBuffer.append(x);
                  } else {
                     ++currentIndex;
                     argBuffer.append(x);
                  }
               } else if (x == '"') {
                  ++currentIndex;
                  isQuoted = true;
               } else if (x == '\\') {
                  ++currentIndex;
                  if (currentIndex >= configChangeSignalCommand.length()) {
                     break;
                  }

                  x = configChangeSignalCommand.charAt(currentIndex);
                  argBuffer.append(x);
               } else {
                  if (x >= 0 && x <= ' ') {
                     break;
                  }

                  ++currentIndex;
                  argBuffer.append(x);
               }
            }
         }

         String[] commandArray = new String[list.size()];

         for(int i = 0; i < commandArray.length; ++i) {
            commandArray[i] = (String)list.get(i);
         }

         if (commandArray.length != 0) {
            String[] env = new String[0];
            File dir = new File("/");

            try {
               Process p = Runtime.getRuntime().exec(commandArray, env, dir);

               try {
                  int rval = p.waitFor();
                  if (rval != 0) {
                     InputStream is = p.getErrorStream();

                     try {
                        Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);

                        try {
                           BufferedReader br = new BufferedReader(r);

                           try {
                              StringBuilder sb = new StringBuilder();

                              while(true) {
                                 String value = br.readLine();
                                 if (value == null) {
                                    throw new ManifoldCFException("Shelled process '" + configChangeSignalCommand + "' failed with error " + Integer.toString(rval) + ": " + sb.toString());
                                 }

                                 sb.append(value).append("; ");
                              }
                           } finally {
                              br.close();
                           }
                        } finally {
                           r.close();
                        }
                     } finally {
                        is.close();
                     }
                  }
               } finally {
                  p.destroy();
               }

            } catch (InterruptedException e) {
               throw new ManifoldCFException("Process wait interrupted: " + e.getMessage(), e, 2);
            } catch (InterruptedIOException e) {
               throw new ManifoldCFException("IO with subprocess interrupted: " + e.getMessage(), e, 2);
            } catch (IOException e) {
               throw new ManifoldCFException("IO exception signalling change: " + e.getMessage(), e);
            }
         }
      }
   }

   public static void sleep(long milliseconds) throws InterruptedException {
      Integer x = new Integer(0);
      synchronized(x) {
         x.wait(milliseconds);
      }
   }

   public static void writeBytes(OutputStream os, byte[] byteArray) throws IOException {
      os.write(byteArray, 0, byteArray.length);
   }

   public static void writeByte(OutputStream os, int byteValue) throws IOException {
      writeBytes(os, new byte[]{(byte)byteValue});
   }

   public static void writeWord(OutputStream os, int wordValue) throws IOException {
      byte[] buffer = new byte[2];
      buffer[0] = (byte)(wordValue & 255);
      buffer[1] = (byte)(wordValue >>> 8 & 255);
      writeBytes(os, buffer);
   }

   public static void writeDword(OutputStream os, int dwordValue) throws IOException {
      if (dwordValue < 0) {
         throw new IllegalArgumentException("Attempt to use an unsigned operator to write a signed value");
      } else {
         writeSdword(os, dwordValue);
      }
   }

   public static void writeSdword(OutputStream os, int dwordValue) throws IOException {
      byte[] buffer = new byte[4];
      buffer[0] = (byte)(dwordValue & 255);
      buffer[1] = (byte)(dwordValue >>> 8 & 255);
      buffer[2] = (byte)(dwordValue >>> 16 & 255);
      buffer[3] = (byte)(dwordValue >>> 24 & 255);
      writeBytes(os, buffer);
   }

   public static void writeLong(OutputStream os, Long longValue) throws IOException {
      if (longValue == null) {
         writeByte(os, 1);
      } else {
         writeByte(os, 0);
         long value = longValue;
         byte[] buffer = new byte[8];
         buffer[0] = (byte)((int)(value & 255L));
         buffer[1] = (byte)((int)(Long.rotateRight(value, 8) & 255L));
         buffer[2] = (byte)((int)(Long.rotateRight(value, 16) & 255L));
         buffer[3] = (byte)((int)(Long.rotateRight(value, 24) & 255L));
         buffer[4] = (byte)((int)(Long.rotateRight(value, 32) & 255L));
         buffer[5] = (byte)((int)(Long.rotateRight(value, 40) & 255L));
         buffer[6] = (byte)((int)(Long.rotateRight(value, 48) & 255L));
         buffer[7] = (byte)((int)(Long.rotateRight(value, 56) & 255L));
         writeBytes(os, buffer);
      }

   }

   public static void writeString(OutputStream os, String stringValue) throws IOException {
      byte[] characters;
      if (stringValue == null) {
         characters = null;
      } else {
         characters = stringValue.getBytes(StandardCharsets.UTF_8);
      }

      writeByteArray(os, characters);
   }

   public static void writeByteArray(OutputStream os, byte[] byteArray) throws IOException {
      if (byteArray == null) {
         writeSdword(os, -1);
      } else {
         writeSdword(os, byteArray.length);
         writeBytes(os, byteArray);
      }

   }

   public static void writefloat(OutputStream os, float floatValue) throws IOException {
      writeSdword(os, Float.floatToIntBits(floatValue));
   }

   public static void readBytes(InputStream is, byte[] byteArray) throws IOException {
      int amt;
      for(int amtSoFar = 0; amtSoFar < byteArray.length; amtSoFar += amt) {
         amt = is.read(byteArray, amtSoFar, byteArray.length - amtSoFar);
         if (amt == -1) {
            throw new IOException("Unexpected EOF");
         }
      }

   }

   public static int readByte(InputStream is) throws IOException {
      byte[] inputArray = new byte[1];
      readBytes(is, inputArray);
      return inputArray[0] & 255;
   }

   public static int readWord(InputStream is) throws IOException {
      byte[] inputArray = new byte[2];
      readBytes(is, inputArray);
      return (inputArray[0] & 255) + ((inputArray[1] & 255) << 8);
   }

   public static int readDword(InputStream is) throws IOException {
      byte[] inputArray = new byte[4];
      readBytes(is, inputArray);
      return (inputArray[0] & 255) + ((inputArray[1] & 255) << 8) + ((inputArray[2] & 255) << 16) + ((inputArray[3] & 255) << 24);
   }

   public static int readSdword(InputStream is) throws IOException {
      byte[] inputArray = new byte[4];
      readBytes(is, inputArray);
      return (inputArray[0] & 255) + ((inputArray[1] & 255) << 8) + ((inputArray[2] & 255) << 16) + (inputArray[3] << 24);
   }

   public static Long readLong(InputStream is) throws IOException {
      int value = readByte(is);
      if (value == 1) {
         return null;
      } else {
         byte[] inputArray = new byte[8];
         readBytes(is, inputArray);
         return new Long((long)(inputArray[0] & 255) + Long.rotateLeft((long)(inputArray[1] & 255), 8) + Long.rotateLeft((long)(inputArray[2] & 255), 16) + Long.rotateLeft((long)(inputArray[3] & 255), 24) + Long.rotateLeft((long)(inputArray[4] & 255), 32) + Long.rotateLeft((long)(inputArray[5] & 255), 40) + Long.rotateLeft((long)(inputArray[6] & 255), 48) + Long.rotateLeft((long)(inputArray[7] & 255), 56));
      }
   }

   public static String readString(InputStream is) throws IOException {
      byte[] bytes = readByteArray(is);
      return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
   }

   public static byte[] readByteArray(InputStream is) throws IOException {
      int length = readSdword(is);
      if (length == -1) {
         return null;
      } else {
         byte[] byteArray = new byte[length];
         readBytes(is, byteArray);
         return byteArray;
      }
   }

   public static float readfloat(InputStream os) throws IOException {
      return Float.intBitsToFloat(readSdword(os));
   }

   public static void addShutdownHook(IShutdownHook hook) {
      synchronized(cleanupHooks) {
         cleanupHooks.add(hook);
      }
   }

   public static void addPollingHook(IPollingHook hook) {
      synchronized(pollingHooks) {
         pollingHooks.add(hook);
      }
   }

   public static void pollAll(IThreadContext threadContext) throws ManifoldCFException {
      synchronized(pollingHooks) {
         for(IPollingHook hook : pollingHooks) {
            hook.doPoll(threadContext);
         }

      }
   }

   public static ManifoldCFResourceLoader createResourceLoader() throws ManifoldCFException {
      return new ManifoldCFResourceLoader(resourceLoader.getClassLoader());
   }

   public static Class findClass(String cname) throws ClassNotFoundException, ManifoldCFException {
      return resourceLoader.findClass(cname);
   }

   /** @deprecated */
   @Deprecated
   public static void cleanUpEnvironment() {
      cleanUpEnvironment(ThreadContextFactory.make());
   }

   public static void cleanUpEnvironment(IThreadContext threadContext) {
      synchronized(initializeFlagLock) {
         --initializeLevel;
         if (initializeLevel == 0 && !alreadyShutdown) {
            synchronized(cleanupHooks) {
               int i = cleanupHooks.size();

               while(i > 0) {
                  --i;
                  IShutdownHook hook = (IShutdownHook)cleanupHooks.get(i);

                  try {
                     hook.doCleanup(threadContext);
                  } catch (ManifoldCFException e) {
                     Logging.root.warn("Error during system shutdown: " + e.getMessage(), e);
                  }
               }

               cleanupHooks.clear();
            }

            synchronized(pollingHooks) {
               pollingHooks.clear();
            }

            alreadyShutdown = true;
         }

      }
   }

   static {
      try {
         Runtime.getRuntime().addShutdownHook(shutdownThread);
      } catch (Exception e) {
         e.printStackTrace();
      } catch (Error e) {
         e.printStackTrace();
      }

      initializeLevel = 0;
      alreadyClosed = false;
      alreadyShutdown = false;
      initializeFlagLock = new Integer(0);
      mcfVersion = null;
      masterDatabaseName = null;
      masterDatabaseUsername = null;
      masterDatabasePassword = null;
      localConfiguration = null;
      propertyFilelastMod = -1L;
      propertyFilePath = null;
      OBFUSCATION_PASSCODE = "NowIsTheTime";
      OBFUSCATION_SALT = "Salty";
   }

   protected static class OverrideableManifoldCFConfiguration extends ManifoldCFConfiguration {
      public OverrideableManifoldCFConfiguration() {
      }

      public String getProperty(String s) {
         String rval = System.getProperty(s);
         if (rval == null) {
            rval = super.getProperty(s);
         }

         return rval;
      }
   }

   protected static class FileTrack implements IShutdownHook {
      protected Set<File> filesToDelete = new HashSet();

      public FileTrack() {
      }

      public void addFile(File f) {
         synchronized(this) {
            this.filesToDelete.add(f);
         }
      }

      public void deleteFile(File f) {
         ManifoldCF.recursiveDelete(f);
         synchronized(this) {
            this.filesToDelete.remove(f);
         }
      }

      public void doCleanup(IThreadContext threadContext) throws ManifoldCFException {
         synchronized(this) {
            for(File f : this.filesToDelete) {
               f.delete();
            }

            this.filesToDelete.clear();
         }
      }

      protected void finalize() throws Throwable {
         try {
            this.doCleanup(ThreadContextFactory.make());
         } finally {
            super.finalize();
         }

      }
   }

   protected static class CachePoll implements IPollingHook {
      public CachePoll() {
      }

      public void doPoll(IThreadContext threadContext) throws ManifoldCFException {
         ICacheManager cacheManager = CacheManagerFactory.make(threadContext);
         cacheManager.expireObjects(System.currentTimeMillis());
      }
   }

   protected static class DatabaseShutdown implements IShutdownHook {
      public DatabaseShutdown() {
      }

      public void doCleanup(IThreadContext threadContext) throws ManifoldCFException {
         Thread t = new DatabaseConnectionReleaseThread();
         t.start();

         try {
            t.join(15000L);
         } catch (InterruptedException var4) {
         }

         this.closeDatabase();
      }

      protected void closeDatabase() throws ManifoldCFException {
         synchronized(ManifoldCF.initializeFlagLock) {
            if (ManifoldCF.initializeLevel == 0 && !ManifoldCF.alreadyClosed) {
               IThreadContext threadcontext = ThreadContextFactory.make();
               String databaseName = ManifoldCF.getMasterDatabaseName();
               String databaseUsername = ManifoldCF.getMasterDatabaseUsername();
               String databasePassword = ManifoldCF.getMasterDatabasePassword();
               DBInterfaceFactory.make(threadcontext, databaseName, databaseUsername, databasePassword).closeDatabase();
               ManifoldCF.alreadyClosed = true;
            }

         }
      }

      protected void finalize() throws Throwable {
         try {
            this.closeDatabase();
         } finally {
            super.finalize();
         }

      }
   }

   protected static class ShutdownThread extends Thread {
      public ShutdownThread() {
         this.setName("Shutdown thread");
      }

      public void run() {
         ManifoldCF.cleanUpEnvironment(ThreadContextFactory.make());
      }
   }

   protected static class DatabaseConnectionReleaseThread extends Thread {
      public DatabaseConnectionReleaseThread() {
         this.setName("Database connection release thread");
         this.setDaemon(true);
      }

      public void run() {
         ConnectionFactory.releaseAll();
      }
   }
}
