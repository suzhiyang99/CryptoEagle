package org.apache.manifoldcf.core.interfaces;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AuthFactory {
   public static final String _rcsid = "@(#)$Id$";
   private static final String authName = "_Auth_";

   private AuthFactory() {
   }

   public static IAuth make(IThreadContext context) throws ManifoldCFException {
      Object x = context.get("_Auth_");
      if (x == null || !(x instanceof IAuth)) {
         String implementationClass = LockManagerFactory.getStringProperty(context, "org.apache.manifoldcf.authimplementationclass", "org.apache.manifoldcf.core.auth.DefaultAuthenticator");

         try {
            Class c = Class.forName(implementationClass);
            Constructor constructor = c.getConstructor(IThreadContext.class);
            x = constructor.newInstance(context);
            if (!(x instanceof IAuth)) {
               throw new ManifoldCFException("Auth implementation class " + implementationClass + " does not implement IAuth", 3);
            }

            context.save("_Auth_", x);
         } catch (ClassNotFoundException e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " could not be found: " + e.getMessage(), e, 3);
         } catch (ExceptionInInitializerError e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (LinkageError e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " could not be linked: " + e.getMessage(), e, 3);
         } catch (InstantiationException e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (InvocationTargetException e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (NoSuchMethodException e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " had no constructor taking (IThreadContext): " + e.getMessage(), e, 3);
         } catch (IllegalAccessException e) {
            throw new ManifoldCFException("Auth implementation class " + implementationClass + " had no public constructor taking (IThreadContext): " + e.getMessage(), e, 3);
         }
      }

      return (IAuth)x;
   }
}
