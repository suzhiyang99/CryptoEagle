package org.apache.wicket.util.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;

public abstract class AbstractStringResourceStream extends AbstractResourceStream implements IStringResourceStream {
   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_CONTENT_TYPE = "text";
   private String charsetName;
   private final String contentType;
   private Time lastModified;

   public AbstractStringResourceStream() {
      this("text");
   }

   public AbstractStringResourceStream(String contentType) {
      this.lastModified = null;
      this.contentType = contentType;
      this.lastModified = Time.now();
   }

   public String asString() {
      Reader reader = null;

      String var2;
      try {
         if (this.charsetName == null) {
            reader = new InputStreamReader(this.getInputStream());
         } else {
            reader = new InputStreamReader(this.getInputStream(), this.getCharset());
         }

         var2 = Streams.readString(reader);
      } catch (IOException e) {
         throw new RuntimeException("Unable to read resource as String", e);
      } catch (ResourceStreamNotFoundException e) {
         throw new RuntimeException("Unable to read resource as String", e);
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly(this);
      }

      return var2;
   }

   protected Charset getCharset() {
      return this.charsetName != null ? Charset.forName(this.charsetName) : null;
   }

   public void setCharset(Charset charset) {
      this.charsetName = charset != null ? charset.name() : null;
   }

   public void close() throws IOException {
   }

   public String getContentType() {
      return this.contentType;
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      byte[] bytes;
      if (this.getCharset() != null) {
         try {
            bytes = this.getString().getBytes(this.getCharset().name());
         } catch (UnsupportedEncodingException e) {
            throw new ResourceStreamNotFoundException("Could not encode resource", e);
         }
      } else {
         bytes = this.getString().getBytes();
      }

      return new ByteArrayInputStream(bytes);
   }

   public Time lastModifiedTime() {
      return this.lastModified;
   }

   public void setLastModified(Time lastModified) {
      this.lastModified = lastModified;
   }

   protected abstract String getString();

   public final Bytes length() {
      int lengthInBytes = Strings.lengthInBytes(this.getString(), this.getCharset());
      return Bytes.bytes((long)lengthInBytes);
   }
}
