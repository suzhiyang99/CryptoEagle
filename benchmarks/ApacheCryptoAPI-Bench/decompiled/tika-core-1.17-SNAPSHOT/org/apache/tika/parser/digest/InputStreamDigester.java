package org.apache.tika.parser.digest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOExceptionWithCause;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DigestingParser;
import org.apache.tika.parser.ParseContext;

public class InputStreamDigester implements DigestingParser.Digester {
   private final String algorithm;
   private final String algorithmKeyName;
   private final DigestingParser.Encoder encoder;
   private final int markLimit;

   public InputStreamDigester(int markLimit, String algorithm, DigestingParser.Encoder encoder) {
      this(markLimit, algorithm, algorithm, encoder);
   }

   public InputStreamDigester(int markLimit, String algorithm, String algorithmKeyName, DigestingParser.Encoder encoder) {
      this.algorithm = algorithm;
      this.algorithmKeyName = algorithmKeyName;
      this.encoder = encoder;
      this.markLimit = markLimit;
      if (markLimit < 0) {
         throw new IllegalArgumentException("markLimit must be >= 0");
      }
   }

   private MessageDigest newMessageDigest() {
      try {
         Provider provider = this.getProvider();
         return provider == null ? MessageDigest.getInstance(this.algorithm) : MessageDigest.getInstance(this.algorithm, provider);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalArgumentException(e);
      }
   }

   protected Provider getProvider() {
      return null;
   }

   public void digest(InputStream is, Metadata metadata, ParseContext parseContext) throws IOException {
      TikaInputStream tis = TikaInputStream.cast(is);
      if (tis != null && tis.hasFile()) {
         long sz = -1L;
         if (tis.hasFile()) {
            sz = tis.getLength();
         }

         if (sz > (long)this.markLimit) {
            this.digestFile(tis.getFile(), metadata);
            return;
         }
      }

      SimpleBoundedInputStream bis = new SimpleBoundedInputStream((long)this.markLimit, is);
      boolean finishedStream = false;
      bis.mark(this.markLimit + 1);
      finishedStream = this.digestStream(bis, metadata);
      bis.reset();
      if (!finishedStream) {
         if (tis != null) {
            this.digestFile(tis.getFile(), metadata);
         } else {
            TemporaryResources tmp = new TemporaryResources();

            try {
               TikaInputStream tmpTikaInputStream = TikaInputStream.get(is, tmp);
               this.digestFile(tmpTikaInputStream.getFile(), metadata);
            } finally {
               try {
                  tmp.dispose();
               } catch (TikaException e) {
                  throw new IOExceptionWithCause(e);
               }
            }
         }

      }
   }

   private String getMetadataKey() {
      return "X-TIKA:digest:" + this.algorithmKeyName;
   }

   private void digestFile(File f, Metadata m) throws IOException {
      InputStream is = new FileInputStream(f);
      Throwable var4 = null;

      try {
         this.digestStream(is, m);
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (is != null) {
            if (var4 != null) {
               try {
                  is.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               is.close();
            }
         }

      }

   }

   private boolean digestStream(InputStream is, Metadata metadata) throws IOException {
      MessageDigest messageDigest = this.newMessageDigest();
      updateDigest(messageDigest, is);
      byte[] digestBytes = messageDigest.digest();
      if (is instanceof SimpleBoundedInputStream && ((SimpleBoundedInputStream)is).hasHitBound()) {
         return false;
      } else {
         metadata.set(this.getMetadataKey(), this.encoder.encode(digestBytes));
         return true;
      }
   }

   private static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
      byte[] buffer = new byte[1024];

      for(int read = data.read(buffer, 0, 1024); read > -1; read = data.read(buffer, 0, 1024)) {
         digest.update(buffer, 0, read);
      }

      return digest;
   }

   private static class SimpleBoundedInputStream extends InputStream {
      private static final int EOF = -1;
      private final long max;
      private final InputStream in;
      private long pos;

      private SimpleBoundedInputStream(long max, InputStream in) {
         this.max = max;
         this.in = in;
      }

      public int read() throws IOException {
         if (this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            int result = this.in.read();
            ++this.pos;
            return result;
         }
      }

      public int read(byte[] b) throws IOException {
         return this.read(b, 0, b.length);
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            long maxRead = this.max >= 0L ? Math.min((long)len, this.max - this.pos) : (long)len;
            int bytesRead = this.in.read(b, off, (int)maxRead);
            if (bytesRead == -1) {
               return -1;
            } else {
               this.pos += (long)bytesRead;
               return bytesRead;
            }
         }
      }

      public long skip(long n) throws IOException {
         long toSkip = this.max >= 0L ? Math.min(n, this.max - this.pos) : n;
         long skippedBytes = this.in.skip(toSkip);
         this.pos += skippedBytes;
         return skippedBytes;
      }

      public void reset() throws IOException {
         this.in.reset();
         this.pos = 0L;
      }

      public void mark(int readLimit) {
         this.in.mark(readLimit);
      }

      public boolean hasHitBound() {
         return this.pos >= this.max;
      }
   }
}
