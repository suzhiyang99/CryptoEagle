package org.apache.tika.parser.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.NullOutputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExternalParser extends AbstractParser {
   private static final long serialVersionUID = -1079128990650687037L;
   public static final String INPUT_FILE_TOKEN = "${INPUT}";
   public static final String OUTPUT_FILE_TOKEN = "${OUTPUT}";
   private Set<MediaType> supportedTypes = Collections.emptySet();
   private Map<Pattern, String> metadataPatterns = null;
   private String[] command = new String[]{"cat"};
   private LineConsumer ignoredLineConsumer;

   public ExternalParser() {
      this.ignoredLineConsumer = ExternalParser.LineConsumer.NULL;
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.getSupportedTypes();
   }

   public Set<MediaType> getSupportedTypes() {
      return this.supportedTypes;
   }

   public void setSupportedTypes(Set<MediaType> supportedTypes) {
      this.supportedTypes = Collections.unmodifiableSet(new HashSet(supportedTypes));
   }

   public String[] getCommand() {
      return this.command;
   }

   public void setCommand(String... command) {
      this.command = command;
   }

   public LineConsumer getIgnoredLineConsumer() {
      return this.ignoredLineConsumer;
   }

   public void setIgnoredLineConsumer(LineConsumer ignoredLineConsumer) {
      this.ignoredLineConsumer = ignoredLineConsumer;
   }

   public Map<Pattern, String> getMetadataExtractionPatterns() {
      return this.metadataPatterns;
   }

   public void setMetadataExtractionPatterns(Map<Pattern, String> patterns) {
      this.metadataPatterns = patterns;
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
      TemporaryResources tmp = new TemporaryResources();

      try {
         this.parse(TikaInputStream.get(stream, tmp), xhtml, metadata, tmp);
      } finally {
         tmp.dispose();
      }

   }

   private void parse(TikaInputStream stream, XHTMLContentHandler xhtml, Metadata metadata, TemporaryResources tmp) throws IOException, SAXException, TikaException {
      boolean inputToStdIn = true;
      boolean outputFromStdOut = true;
      boolean hasPatterns = this.metadataPatterns != null && !this.metadataPatterns.isEmpty();
      File output = null;
      String[] cmd;
      if (this.command.length == 1) {
         cmd = this.command[0].split(" ");
      } else {
         cmd = new String[this.command.length];
         System.arraycopy(this.command, 0, cmd, 0, this.command.length);
      }

      for(int i = 0; i < cmd.length; ++i) {
         if (cmd[i].indexOf("${INPUT}") != -1) {
            cmd[i] = cmd[i].replace("${INPUT}", stream.getFile().getPath());
            inputToStdIn = false;
         }

         if (cmd[i].indexOf("${OUTPUT}") != -1) {
            output = tmp.createTemporaryFile();
            outputFromStdOut = false;
            cmd[i] = cmd[i].replace("${OUTPUT}", output.getPath());
         }
      }

      Process process = null;

      try {
         if (cmd.length == 1) {
            process = Runtime.getRuntime().exec(cmd[0]);
         } else {
            process = Runtime.getRuntime().exec(cmd);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         if (inputToStdIn) {
            this.sendInput(process, stream);
         } else {
            process.getOutputStream().close();
         }

         InputStream out = process.getInputStream();
         InputStream err = process.getErrorStream();
         if (hasPatterns) {
            this.extractMetadata(err, metadata);
            if (outputFromStdOut) {
               this.extractOutput(out, xhtml);
            } else {
               this.extractMetadata(out, metadata);
            }
         } else {
            ignoreStream(err);
            if (outputFromStdOut) {
               this.extractOutput(out, xhtml);
            } else {
               ignoreStream(out);
            }
         }
      } finally {
         try {
            process.waitFor();
         } catch (InterruptedException var19) {
         }

      }

      if (!outputFromStdOut) {
         this.extractOutput(new FileInputStream(output), xhtml);
      }

   }

   private void extractOutput(InputStream stream, XHTMLContentHandler xhtml) throws SAXException, IOException {
      Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
      Throwable var4 = null;

      try {
         xhtml.startDocument();
         xhtml.startElement("p");
         char[] buffer = new char[1024];

         for(int n = reader.read(buffer); n != -1; n = reader.read(buffer)) {
            xhtml.characters(buffer, 0, n);
         }

         xhtml.endElement("p");
         xhtml.endDocument();
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (reader != null) {
            if (var4 != null) {
               try {
                  reader.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               reader.close();
            }
         }

      }

   }

   private void sendInput(final Process process, final InputStream stream) {
      Thread t = new Thread() {
         public void run() {
            OutputStream stdin = process.getOutputStream();

            try {
               IOUtils.copy(stream, stdin);
            } catch (IOException var3) {
            }

         }
      };
      t.start();

      try {
         t.join();
      } catch (InterruptedException var5) {
      }

   }

   private static void ignoreStream(InputStream stream) {
      ignoreStream(stream, true);
   }

   private static Thread ignoreStream(final InputStream stream, boolean waitForDeath) {
      Thread t = new Thread() {
         public void run() {
            try {
               IOUtils.copy((InputStream)stream, (OutputStream)(new NullOutputStream()));
            } catch (IOException var5) {
            } finally {
               IOUtils.closeQuietly(stream);
            }

         }
      };
      t.start();
      if (waitForDeath) {
         try {
            t.join();
         } catch (InterruptedException var4) {
         }
      }

      return t;
   }

   private void extractMetadata(final InputStream stream, final Metadata metadata) {
      Thread t = new Thread() {
         public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            try {
               String line;
               try {
                  while((line = reader.readLine()) != null) {
                     boolean consumed = false;

                     for(Pattern p : ExternalParser.this.metadataPatterns.keySet()) {
                        Matcher m = p.matcher(line);
                        if (m.find()) {
                           consumed = true;
                           if (ExternalParser.this.metadataPatterns.get(p) != null && !((String)ExternalParser.this.metadataPatterns.get(p)).equals("")) {
                              metadata.add((String)ExternalParser.this.metadataPatterns.get(p), m.group(1));
                           } else {
                              metadata.add(m.group(1), m.group(2));
                           }
                        }
                     }

                     if (!consumed) {
                        ExternalParser.this.ignoredLineConsumer.consume(line);
                     }
                  }
               } catch (IOException var10) {
               }
            } finally {
               IOUtils.closeQuietly((Reader)reader);
               IOUtils.closeQuietly(stream);
            }

         }
      };
      t.start();

      try {
         t.join();
      } catch (InterruptedException var5) {
      }

   }

   public static boolean check(String checkCmd, int... errorValue) {
      return check(new String[]{checkCmd}, errorValue);
   }

   public static boolean check(String[] checkCmd, int... errorValue) {
      if (errorValue.length == 0) {
         errorValue = new int[]{127};
      }

      try {
         Process process = Runtime.getRuntime().exec(checkCmd);
         Thread stdErrSuckerThread = ignoreStream(process.getErrorStream(), false);
         Thread stdOutSuckerThread = ignoreStream(process.getInputStream(), false);
         stdErrSuckerThread.join();
         stdOutSuckerThread.join();
         int result = process.waitFor();

         for(int err : errorValue) {
            if (result == err) {
               return false;
            }
         }

         return true;
      } catch (IOException var10) {
         return false;
      } catch (InterruptedException var11) {
         return false;
      } catch (SecurityException var12) {
         return false;
      } catch (Error var13) {
         if (var13.getMessage() == null || !var13.getMessage().contains("posix_spawn") && !var13.getMessage().contains("UNIXProcess")) {
            throw var13;
         } else {
            return false;
         }
      }
   }

   public interface LineConsumer extends Serializable {
      LineConsumer NULL = new LineConsumer() {
         public void consume(String line) {
         }
      };

      void consume(String var1);
   }
}
