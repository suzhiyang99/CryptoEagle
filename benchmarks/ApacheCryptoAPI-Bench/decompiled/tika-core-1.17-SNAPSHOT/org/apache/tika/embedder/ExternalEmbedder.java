package org.apache.tika.embedder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;

public class ExternalEmbedder implements Embedder {
   private static final long serialVersionUID = -2828829275642475697L;
   public static final String METADATA_COMMAND_ARGUMENTS_TOKEN = "${METADATA}";
   public static final String METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN = "${METADATA_SERIALIZED}";
   private Set<MediaType> supportedEmbedTypes = Collections.emptySet();
   private Map<Property, String[]> metadataCommandArguments = null;
   private String[] command = new String[]{"sed", "-e", "$a\\\n${METADATA_SERIALIZED}", "${INPUT}"};
   private String commandAssignmentOperator = "=";
   private String commandAssignmentDelimeter = ", ";
   private String commandAppendOperator = "=";
   private boolean quoteAssignmentValues = false;
   private TemporaryResources tmp = new TemporaryResources();

   public Set<MediaType> getSupportedEmbedTypes(ParseContext context) {
      return this.getSupportedEmbedTypes();
   }

   public Set<MediaType> getSupportedEmbedTypes() {
      return this.supportedEmbedTypes;
   }

   public void setSupportedEmbedTypes(Set<MediaType> supportedEmbedTypes) {
      this.supportedEmbedTypes = Collections.unmodifiableSet(new HashSet(supportedEmbedTypes));
   }

   public String[] getCommand() {
      return this.command;
   }

   public void setCommand(String... command) {
      this.command = command;
   }

   public String getCommandAssignmentOperator() {
      return this.commandAssignmentOperator;
   }

   public void setCommandAssignmentOperator(String commandAssignmentOperator) {
      this.commandAssignmentOperator = commandAssignmentOperator;
   }

   public String getCommandAssignmentDelimeter() {
      return this.commandAssignmentDelimeter;
   }

   public void setCommandAssignmentDelimeter(String commandAssignmentDelimeter) {
      this.commandAssignmentDelimeter = commandAssignmentDelimeter;
   }

   public String getCommandAppendOperator() {
      return this.commandAppendOperator;
   }

   public void setCommandAppendOperator(String commandAppendOperator) {
      this.commandAppendOperator = commandAppendOperator;
   }

   public boolean isQuoteAssignmentValues() {
      return this.quoteAssignmentValues;
   }

   public void setQuoteAssignmentValues(boolean quoteAssignmentValues) {
      this.quoteAssignmentValues = quoteAssignmentValues;
   }

   public Map<Property, String[]> getMetadataCommandArguments() {
      return this.metadataCommandArguments;
   }

   public void setMetadataCommandArguments(Map<Property, String[]> arguments) {
      this.metadataCommandArguments = arguments;
   }

   protected List<String> getCommandMetadataSegments(Metadata metadata) {
      List<String> commandMetadataSegments = new ArrayList();
      if (metadata != null && metadata.names() != null) {
         for(String metadataName : metadata.names()) {
            for(Property property : this.getMetadataCommandArguments().keySet()) {
               if (metadataName.equals(property.getName())) {
                  String[] metadataCommandArguments = (String[])this.getMetadataCommandArguments().get(property);
                  if (metadataCommandArguments != null) {
                     for(String metadataCommandArgument : metadataCommandArguments) {
                        if (metadata.isMultiValued(metadataName)) {
                           for(String metadataValue : metadata.getValues(metadataName)) {
                              String assignmentValue = metadataValue;
                              if (this.quoteAssignmentValues) {
                                 assignmentValue = "'" + metadataValue + "'";
                              }

                              commandMetadataSegments.add(metadataCommandArgument + this.commandAppendOperator + assignmentValue);
                           }
                        } else {
                           String assignmentValue = metadata.get(metadataName);
                           if (this.quoteAssignmentValues) {
                              assignmentValue = "'" + assignmentValue + "'";
                           }

                           commandMetadataSegments.add(metadataCommandArgument + this.commandAssignmentOperator + assignmentValue);
                        }
                     }
                  }
               }
            }
         }

         return commandMetadataSegments;
      } else {
         return commandMetadataSegments;
      }
   }

   protected static String serializeMetadata(List<String> metadataCommandArguments) {
      return metadataCommandArguments != null ? Arrays.toString(metadataCommandArguments.toArray()) : "";
   }

   public void embed(Metadata metadata, InputStream inputStream, OutputStream outputStream, ParseContext context) throws IOException, TikaException {
      boolean inputToStdIn = true;
      boolean outputFromStdOut = true;
      boolean hasMetadataCommandArguments = this.metadataCommandArguments != null && !this.metadataCommandArguments.isEmpty();
      boolean serializeMetadataCommandArgumentsToken = false;
      boolean replacedMetadataCommandArgumentsToken = false;
      TikaInputStream tikaInputStream = TikaInputStream.get(inputStream);
      File tempOutputFile = null;
      List<String> commandMetadataSegments = null;
      if (hasMetadataCommandArguments) {
         commandMetadataSegments = this.getCommandMetadataSegments(metadata);
      }

      List<String> origCmd = Arrays.asList(this.command);
      List<String> cmd = new ArrayList();

      for(String commandSegment : origCmd) {
         if (commandSegment.indexOf("${INPUT}") != -1) {
            commandSegment = commandSegment.replace("${INPUT}", tikaInputStream.getFile().toString());
            inputToStdIn = false;
         }

         if (commandSegment.indexOf("${OUTPUT}") != -1) {
            tempOutputFile = this.tmp.createTemporaryFile();
            commandSegment = commandSegment.replace("${OUTPUT}", tempOutputFile.toString());
            outputFromStdOut = false;
         }

         if (commandSegment.indexOf("${METADATA_SERIALIZED}") != -1) {
            serializeMetadataCommandArgumentsToken = true;
         }

         if (commandSegment.indexOf("${METADATA}") == -1) {
            cmd.add(commandSegment);
         } else {
            if (hasMetadataCommandArguments) {
               for(String commandMetadataSegment : commandMetadataSegments) {
                  cmd.add(commandMetadataSegment);
               }
            }

            replacedMetadataCommandArgumentsToken = true;
         }
      }

      if (hasMetadataCommandArguments) {
         if (serializeMetadataCommandArgumentsToken) {
            int i = 0;

            for(String commandSegment : cmd) {
               if (commandSegment.indexOf("${METADATA_SERIALIZED}") != -1) {
                  commandSegment = commandSegment.replace("${METADATA_SERIALIZED}", serializeMetadata(commandMetadataSegments));
                  cmd.set(i, commandSegment);
               }

               ++i;
            }
         } else if (!replacedMetadataCommandArgumentsToken && !serializeMetadataCommandArgumentsToken) {
            cmd.addAll(commandMetadataSegments);
         }
      }

      Process process;
      if (cmd.toArray().length == 1) {
         process = Runtime.getRuntime().exec(((String[])cmd.toArray(new String[0]))[0]);
      } else {
         process = Runtime.getRuntime().exec((String[])cmd.toArray(new String[0]));
      }

      ByteArrayOutputStream stdErrOutputStream = new ByteArrayOutputStream();

      try {
         this.sendStdErrToOutputStream(process, stdErrOutputStream);
         if (inputToStdIn) {
            this.sendInputStreamToStdIn(inputStream, process);
         } else {
            process.getOutputStream().close();
         }

         if (outputFromStdOut) {
            this.sendStdOutToOutputStream(process, outputStream);
         } else {
            this.tmp.dispose();

            try {
               process.waitFor();
            } catch (InterruptedException var29) {
            }

            InputStream tempOutputFileInputStream = TikaInputStream.get(tempOutputFile);
            IOUtils.copy(tempOutputFileInputStream, outputStream);
         }
      } finally {
         if (outputFromStdOut) {
            try {
               process.waitFor();
            } catch (InterruptedException var28) {
            }
         } else {
            try {
               tempOutputFile.delete();
            } catch (Exception var27) {
            }
         }

         if (!inputToStdIn) {
            IOUtils.closeQuietly((InputStream)tikaInputStream);
         }

         IOUtils.closeQuietly(outputStream);
         IOUtils.closeQuietly((OutputStream)stdErrOutputStream);
         if (process.exitValue() != 0) {
            throw new TikaException("There was an error executing the command line\nExecutable Command:\n\n" + cmd + "\nExecutable Error:\n\n" + stdErrOutputStream.toString(StandardCharsets.UTF_8.name()));
         }

      }

   }

   private void multiThreadedStreamCopy(final InputStream inputStream, final OutputStream outputStream) {
      (new Thread(new Runnable() {
         public void run() {
            try {
               IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
               System.out.println("ERROR: " + e.getMessage());
            }

         }
      })).start();
   }

   private void sendInputStreamToStdIn(InputStream inputStream, Process process) {
      this.multiThreadedStreamCopy(inputStream, process.getOutputStream());
   }

   private void sendStdOutToOutputStream(Process process, OutputStream outputStream) {
      try {
         IOUtils.copy(process.getInputStream(), outputStream);
      } catch (IOException e) {
         System.out.println("ERROR: " + e.getMessage());
      }

   }

   private void sendStdErrToOutputStream(Process process, OutputStream outputStream) {
      this.multiThreadedStreamCopy(process.getErrorStream(), outputStream);
   }

   public static boolean check(String checkCmd, int... errorValue) {
      return check(new String[]{checkCmd}, errorValue);
   }

   public static boolean check(String[] checkCmd, int... errorValue) {
      if (errorValue.length == 0) {
         errorValue = new int[]{127};
      }

      try {
         Process process;
         if (checkCmd.length == 1) {
            process = Runtime.getRuntime().exec(checkCmd[0]);
         } else {
            process = Runtime.getRuntime().exec(checkCmd);
         }

         int result = process.waitFor();

         for(int err : errorValue) {
            if (result == err) {
               return false;
            }
         }

         return true;
      } catch (IOException var8) {
         return false;
      } catch (InterruptedException var9) {
         return false;
      }
   }
}
