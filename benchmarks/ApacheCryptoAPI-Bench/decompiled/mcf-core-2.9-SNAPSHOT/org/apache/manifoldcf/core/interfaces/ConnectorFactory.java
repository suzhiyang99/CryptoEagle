package org.apache.manifoldcf.core.interfaces;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.manifoldcf.core.system.ManifoldCF;

public abstract class ConnectorFactory<T extends IConnector> {
   public static final String _rcsid = "@(#)$Id: OutputConnectorFactory.java 988245 2010-08-23 18:39:35Z kwright $";

   protected ConnectorFactory() {
   }

   protected abstract boolean isInstalled(IThreadContext var1, String var2) throws ManifoldCFException;

   protected void installThis(IThreadContext threadContext, String className) throws ManifoldCFException {
      T connector = this.getThisConnectorNoCheck(className);
      connector.install(threadContext);
   }

   protected void deinstallThis(IThreadContext threadContext, String className) throws ManifoldCFException {
      T connector = this.getThisConnectorNoCheck(className);
      connector.deinstall(threadContext);
   }

   protected void outputThisConfigurationHeader(IThreadContext threadContext, String className, IHTTPOutput out, Locale locale, ConfigParams parameters, ArrayList tabsArray) throws ManifoldCFException, IOException {
      T connector = this.getThisConnector(threadContext, className);
      if (connector != null) {
         connector.outputConfigurationHeader(threadContext, out, locale, parameters, tabsArray);
      }
   }

   protected void outputThisConfigurationBody(IThreadContext threadContext, String className, IHTTPOutput out, Locale locale, ConfigParams parameters, String tabName) throws ManifoldCFException, IOException {
      T connector = this.getThisConnector(threadContext, className);
      if (connector != null) {
         connector.outputConfigurationBody(threadContext, out, locale, parameters, tabName);
      }
   }

   protected String processThisConfigurationPost(IThreadContext threadContext, String className, IPostParameters variableContext, Locale locale, ConfigParams configParams) throws ManifoldCFException {
      T connector = this.getThisConnector(threadContext, className);
      return connector == null ? null : connector.processConfigurationPost(threadContext, variableContext, locale, configParams);
   }

   protected void viewThisConfiguration(IThreadContext threadContext, String className, IHTTPOutput out, Locale locale, ConfigParams configParams) throws ManifoldCFException, IOException {
      T connector = this.getThisConnector(threadContext, className);
      if (connector != null) {
         connector.viewConfiguration(threadContext, out, locale, configParams);
      }
   }

   protected T getThisConnectorNoCheck(String className) throws ManifoldCFException {
      T rval = this.getThisConnectorRaw(className);
      if (rval == null) {
         throw new ManifoldCFException("No connector class '" + className + "' was found.");
      } else {
         return rval;
      }
   }

   protected T getThisConnector(IThreadContext threadContext, String className) throws ManifoldCFException {
      return (T)(!this.isInstalled(threadContext, className) ? null : this.getThisConnectorRaw(className));
   }

   protected T getThisConnectorRaw(String className) throws ManifoldCFException {
      try {
         Class theClass = ManifoldCF.findClass(className);
         Class[] argumentClasses = new Class[0];
         Constructor c = theClass.getConstructor(argumentClasses);
         Object[] arguments = new Object[0];
         Object o = c.newInstance(arguments);

         try {
            return (T)(o);
         } catch (ClassCastException var8) {
            throw new ManifoldCFException("Class '" + className + "' does not implement IConnector.");
         }
      } catch (InvocationTargetException e) {
         Throwable z = e.getTargetException();
         if (z instanceof Error) {
            throw (Error)z;
         } else if (z instanceof RuntimeException) {
            throw (RuntimeException)z;
         } else if (z instanceof ManifoldCFException) {
            throw (ManifoldCFException)z;
         } else {
            throw new RuntimeException("Unknown exception type: " + z.getClass().getName() + ": " + z.getMessage(), z);
         }
      } catch (ClassNotFoundException var10) {
         return null;
      } catch (NoSuchMethodException e) {
         throw new ManifoldCFException("No appropriate constructor for IConnector implementation '" + className + "'.  Need xxx().", e);
      } catch (SecurityException e) {
         throw new ManifoldCFException("Protected constructor for IConnector implementation '" + className + "'", e);
      } catch (IllegalAccessException e) {
         throw new ManifoldCFException("Unavailable constructor for IConnector implementation '" + className + "'", e);
      } catch (IllegalArgumentException e) {
         throw new ManifoldCFException("Shouldn't happen!!!", e);
      } catch (InstantiationException e) {
         throw new ManifoldCFException("InstantiationException for IConnector implementation '" + className + "'", e);
      } catch (ExceptionInInitializerError e) {
         throw new ManifoldCFException("ExceptionInInitializerError for IConnector implementation '" + className + "'", e);
      }
   }
}
