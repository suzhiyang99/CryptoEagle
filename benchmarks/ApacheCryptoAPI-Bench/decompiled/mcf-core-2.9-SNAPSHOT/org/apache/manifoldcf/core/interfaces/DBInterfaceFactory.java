package org.apache.manifoldcf.core.interfaces;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DBInterfaceFactory {
   public static final String _rcsid = "@(#)$Id: DBInterfaceFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final String dbinterfaceInstancePrefix = "_DBInterface:";

   private DBInterfaceFactory() {
   }

   public static IDBInterface make(IThreadContext context, String databaseName, String userName, String password) throws ManifoldCFException {
      String dbName = "_DBInterface:" + databaseName;
      Object x = context.get(dbName);
      if (x == null || !(x instanceof IDBInterface)) {
         String implementationClass = LockManagerFactory.getStringProperty(context, "org.apache.manifoldcf.databaseimplementationclass", "org.apache.manifoldcf.core.database.DBInterfacePostgreSQL");

         try {
            Class c = Class.forName(implementationClass);
            Constructor constructor = c.getConstructor(IThreadContext.class, String.class, String.class, String.class);
            x = constructor.newInstance(context, databaseName, userName, password);
            if (!(x instanceof IDBInterface)) {
               throw new ManifoldCFException("Database implementation class " + implementationClass + " does not implement IDBInterface", 3);
            }

            context.save(dbName, x);
         } catch (ClassNotFoundException e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " could not be found: " + e.getMessage(), e, 3);
         } catch (ExceptionInInitializerError e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (LinkageError e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " could not be linked: " + e.getMessage(), e, 3);
         } catch (InstantiationException e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (InvocationTargetException e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (NoSuchMethodException e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " had no constructor taking (IThreadContext, String, String, String): " + e.getMessage(), e, 3);
         } catch (IllegalAccessException e) {
            throw new ManifoldCFException("Database implementation class " + implementationClass + " had no public constructor taking (IThreadContext, String, String, String): " + e.getMessage(), e, 3);
         }
      }

      return (IDBInterface)x;
   }
}
