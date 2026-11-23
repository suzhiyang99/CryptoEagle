package org.apache.manifoldcf.core.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public interface IConnector {
   void install(IThreadContext var1) throws ManifoldCFException;

   void deinstall(IThreadContext var1) throws ManifoldCFException;

   void connect(ConfigParams var1);

   String check() throws ManifoldCFException;

   void poll() throws ManifoldCFException;

   boolean isConnected();

   void disconnect() throws ManifoldCFException;

   ConfigParams getConfiguration();

   void clearThreadContext();

   void setThreadContext(IThreadContext var1) throws ManifoldCFException;

   void outputConfigurationHeader(IThreadContext var1, IHTTPOutput var2, Locale var3, ConfigParams var4, List<String> var5) throws ManifoldCFException, IOException;

   void outputConfigurationBody(IThreadContext var1, IHTTPOutput var2, Locale var3, ConfigParams var4, String var5) throws ManifoldCFException, IOException;

   String processConfigurationPost(IThreadContext var1, IPostParameters var2, Locale var3, ConfigParams var4) throws ManifoldCFException;

   void viewConfiguration(IThreadContext var1, IHTTPOutput var2, Locale var3, ConfigParams var4) throws ManifoldCFException, IOException;
}
