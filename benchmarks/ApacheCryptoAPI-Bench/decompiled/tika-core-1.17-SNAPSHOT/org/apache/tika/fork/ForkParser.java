package org.apache.tika.fork;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ForkParser extends AbstractParser {
   private static final long serialVersionUID = -4962742892274663950L;
   private final ClassLoader loader;
   private final Parser parser;
   private List<String> java;
   private int poolSize;
   private int currentlyInUse;
   private final Queue<ForkClient> pool;
   private long serverPulseMillis;

   public ForkParser(ClassLoader loader, Parser parser) {
      this.java = Arrays.asList("java", "-Xmx32m");
      this.poolSize = 5;
      this.currentlyInUse = 0;
      this.pool = new LinkedList();
      this.serverPulseMillis = 5000L;
      if (parser instanceof ForkParser) {
         throw new IllegalArgumentException("The underlying parser of a ForkParser should not be a ForkParser, but a specific implementation.");
      } else {
         this.loader = loader;
         this.parser = parser;
      }
   }

   public ForkParser(ClassLoader loader) {
      this(loader, new AutoDetectParser());
   }

   public ForkParser() {
      this(ForkParser.class.getClassLoader());
   }

   public synchronized int getPoolSize() {
      return this.poolSize;
   }

   public synchronized void setPoolSize(int poolSize) {
      this.poolSize = poolSize;
   }

   /** @deprecated */
   @Deprecated
   public String getJavaCommand() {
      StringBuilder sb = new StringBuilder();

      for(String part : this.getJavaCommandAsList()) {
         sb.append(part).append(' ');
      }

      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }

   public List<String> getJavaCommandAsList() {
      return Collections.unmodifiableList(this.java);
   }

   public void setJavaCommand(List<String> java) {
      this.java = new ArrayList(java);
   }

   /** @deprecated */
   @Deprecated
   public void setJavaCommand(String java) {
      this.setJavaCommand(Arrays.asList(java.split(" ")));
   }

   public Set<MediaType> getSupportedTypes(ParseContext context) {
      return this.parser.getSupportedTypes(context);
   }

   public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
      if (stream == null) {
         throw new NullPointerException("null stream");
      } else {
         boolean alive = false;
         ForkClient client = this.acquireClient();

         Throwable t;
         try {
            ContentHandler tee = new TeeContentHandler(new ContentHandler[]{handler, new MetadataContentHandler(metadata)});
            t = client.call("parse", stream, tee, metadata, context);
            alive = true;
         } catch (TikaException te) {
            alive = true;
            throw te;
         } catch (IOException e) {
            throw new TikaException("Failed to communicate with a forked parser process. The process has most likely crashed due to some error like running out of memory. A new process will be started for the next parsing request.", e);
         } finally {
            this.releaseClient(client, alive);
         }

         if (t instanceof IOException) {
            throw (IOException)t;
         } else if (t instanceof SAXException) {
            throw (SAXException)t;
         } else if (t instanceof TikaException) {
            throw (TikaException)t;
         } else if (t != null) {
            throw new TikaException("Unexpected error in forked server process", t);
         }
      }
   }

   public synchronized void close() {
      for(ForkClient client : this.pool) {
         client.close();
      }

      this.pool.clear();
      this.poolSize = 0;
   }

   private synchronized ForkClient acquireClient() throws IOException, TikaException {
      while(true) {
         ForkClient client = (ForkClient)this.pool.poll();
         if (client == null && this.currentlyInUse < this.poolSize) {
            client = new ForkClient(this.loader, this.parser, this.java, this.serverPulseMillis);
         }

         if (client != null && !client.ping()) {
            client.close();
            client = null;
         }

         if (client != null) {
            ++this.currentlyInUse;
            return client;
         }

         if (this.currentlyInUse >= this.poolSize) {
            try {
               this.wait();
            } catch (InterruptedException e) {
               throw new TikaException("Interrupted while waiting for a fork parser", e);
            }
         }
      }
   }

   private synchronized void releaseClient(ForkClient client, boolean alive) {
      --this.currentlyInUse;
      if (this.currentlyInUse + this.pool.size() < this.poolSize && alive) {
         this.pool.offer(client);
         this.notifyAll();
      } else {
         client.close();
      }

   }

   public void setServerPulseMillis(long serverPulseMillis) {
      this.serverPulseMillis = serverPulseMillis;
   }
}
