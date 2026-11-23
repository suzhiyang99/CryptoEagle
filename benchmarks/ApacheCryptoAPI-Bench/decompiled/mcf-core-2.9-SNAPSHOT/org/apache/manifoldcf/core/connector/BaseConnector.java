package org.apache.manifoldcf.core.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.manifoldcf.core.interfaces.ConfigParams;
import org.apache.manifoldcf.core.interfaces.IConnector;
import org.apache.manifoldcf.core.interfaces.IHTTPOutput;
import org.apache.manifoldcf.core.interfaces.IPostParameters;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public abstract class BaseConnector implements IConnector {
   public static final String _rcsid = "@(#)$Id: BaseConnector.java 988245 2010-08-23 18:39:35Z kwright $";
   protected ConfigParams params = null;
   protected IThreadContext currentContext = null;

   public void install(IThreadContext threadContext) throws ManifoldCFException {
   }

   public void deinstall(IThreadContext threadContext) throws ManifoldCFException {
   }

   public void connect(ConfigParams configParams) {
      this.params = configParams;
   }

   public String check() throws ManifoldCFException {
      return "Connection working";
   }

   public void poll() throws ManifoldCFException {
   }

   public boolean isConnected() {
      return true;
   }

   public void disconnect() throws ManifoldCFException {
      this.params = null;
   }

   public void clearThreadContext() {
      this.currentContext = null;
   }

   public void setThreadContext(IThreadContext threadContext) throws ManifoldCFException {
      this.currentContext = threadContext;
   }

   public ConfigParams getConfiguration() {
      return this.params;
   }

   public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters, List<String> tabsArray) throws ManifoldCFException, IOException {
      this.outputConfigurationHeader(threadContext, out, parameters, tabsArray);
   }

   public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, List<String> tabsArray) throws ManifoldCFException, IOException {
      ArrayList<Object> localTabsArray = new ArrayList();
      this.outputConfigurationHeader(threadContext, out, parameters, localTabsArray);

      for(Object o : localTabsArray) {
         tabsArray.add((String)o);
      }

   }

   public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, ArrayList<Object> tabsArray) throws ManifoldCFException, IOException {
   }

   public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters, String tabName) throws ManifoldCFException, IOException {
      this.outputConfigurationBody(threadContext, out, parameters, tabName);
   }

   public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters, String tabName) throws ManifoldCFException, IOException {
   }

   public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext, Locale locale, ConfigParams parameters) throws ManifoldCFException {
      return this.processConfigurationPost(threadContext, variableContext, parameters);
   }

   public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext, ConfigParams parameters) throws ManifoldCFException {
      return null;
   }

   public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters) throws ManifoldCFException, IOException {
      this.viewConfiguration(threadContext, out, parameters);
   }

   public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, ConfigParams parameters) throws ManifoldCFException, IOException {
   }

   protected static void pack(StringBuilder output, String value, char delimiter) {
      char x;
      for(int i = 0; i < value.length(); output.append(x)) {
         x = value.charAt(i++);
         if (x == '\\' || x == delimiter) {
            output.append('\\');
         }
      }

      output.append(delimiter);
   }

   protected static int unpack(StringBuilder sb, String value, int startPosition, char delimiter) {
      while(true) {
         char x;
         label18: {
            if (startPosition < value.length()) {
               x = value.charAt(startPosition++);
               if (x == '\\') {
                  if (startPosition < value.length()) {
                     x = value.charAt(startPosition++);
                  }
                  break label18;
               }

               if (x != delimiter) {
                  break label18;
               }
            }

            return startPosition;
         }

         sb.append(x);
      }
   }

   protected static void packFixedList(StringBuilder output, String[] values, char delimiter) {
      int i = 0;

      while(i < values.length) {
         pack(output, values[i++], delimiter);
      }

   }

   protected static int unpackFixedList(String[] output, String value, int startPosition, char delimiter) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < output.length; output[i++] = sb.toString()) {
         sb.setLength(0);
         startPosition = unpack(sb, value, startPosition, delimiter);
      }

      return startPosition;
   }

   protected static void packList(StringBuilder output, List<String> values, char delimiter) {
      pack(output, Integer.toString(values.size()), delimiter);
      int i = 0;

      while(i < values.size()) {
         pack(output, ((String)values.get(i++)).toString(), delimiter);
      }

   }

   protected static void packList(StringBuilder output, String[] values, char delimiter) {
      pack(output, Integer.toString(values.length), delimiter);
      int i = 0;

      while(i < values.length) {
         pack(output, values[i++], delimiter);
      }

   }

   protected static int unpackList(List<String> output, String value, int startPosition, char delimiter) {
      StringBuilder sb = new StringBuilder();
      startPosition = unpack(sb, value, startPosition, delimiter);

      try {
         int count = Integer.parseInt(sb.toString());

         for(int i = 0; i < count; ++i) {
            sb.setLength(0);
            startPosition = unpack(sb, value, startPosition, delimiter);
            output.add(sb.toString());
         }
      } catch (NumberFormatException var7) {
      }

      return startPosition;
   }
}
