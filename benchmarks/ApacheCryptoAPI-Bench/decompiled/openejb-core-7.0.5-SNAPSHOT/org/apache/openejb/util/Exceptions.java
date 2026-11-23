package org.apache.openejb.util;

import java.io.IOException;
import java.io.NotSerializableException;
import javax.ejb.EJBException;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.transaction.RollbackException;
import org.apache.openejb.OpenEjbContainer;

public class Exceptions {
   public static <T extends Throwable> T initCause(T t, Throwable cause) {
      return (T)t.initCause(cause);
   }

   public static IOException newIOException(String message, Throwable cause) {
      return (IOException)initCause(new IOException(message), cause);
   }

   public static IOException newIOException(Throwable cause) {
      return (IOException)initCause(new IOException(), cause);
   }

   public static NamingException newNamingException(String message, Throwable cause) {
      return (NamingException)initCause(new NamingException(message), cause);
   }

   public static NamingException newNamingException(Throwable cause) {
      return (NamingException)initCause(new NamingException(), cause);
   }

   public static RollbackException newRollbackException(String message, Throwable cause) {
      return (RollbackException)initCause(new RollbackException(message), cause);
   }

   public static RollbackException newRollbackException(Throwable cause) {
      return (RollbackException)initCause(new RollbackException(), cause);
   }

   public static AuthenticationException newAuthenticationException(String message, Throwable cause) {
      return (AuthenticationException)initCause(new AuthenticationException(message), cause);
   }

   public static AuthenticationException newAuthenticationException(Throwable cause) {
      return (AuthenticationException)initCause(new AuthenticationException(), cause);
   }

   public static EJBException newEJBException(String message, Throwable cause) {
      return (EJBException)initCause(new EJBException(message), cause);
   }

   public static EJBException newEJBException(Throwable cause) {
      return (EJBException)initCause(new EJBException(), cause);
   }

   public static NotSerializableException newNotSerializableException(String message, Throwable cause) {
      return (NotSerializableException)initCause(new NotSerializableException(message), cause);
   }

   public static NotSerializableException newNotSerializableException(Throwable cause) {
      return (NotSerializableException)initCause(new NotSerializableException(), cause);
   }

   public static OpenEjbContainer.NoModulesFoundException newNoModulesFoundException() {
      return new OpenEjbContainer.NoModulesFoundException(Join.join("\n", "No modules found to deploy.", "1)Maybe descriptors are placed in incorrect location.", "Descriptors could go under: ", "<base-dir>/META-INF or <base-dir>/WEB-INF", "but not directly under <base-dir>", "Check 'Application Discovery via the Classpath' docs page for more info", "2)Maybe no modules are present in the classpath.", "Is 'openejb.base' system property pointing to the intended location?"));
   }
}
