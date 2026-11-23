package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.tika.config.Field;
import org.apache.tika.metadata.Metadata;

public class NonDetectingEncodingDetector implements EncodingDetector {
   private Charset charset;

   public NonDetectingEncodingDetector() {
      this.charset = StandardCharsets.UTF_8;
   }

   public NonDetectingEncodingDetector(Charset charset) {
      this.charset = StandardCharsets.UTF_8;
      this.charset = charset;
   }

   public Charset detect(InputStream input, Metadata metadata) throws IOException {
      return this.charset;
   }

   @Field
   private void setCharset(String charsetName) {
      this.charset = Charset.forName(charsetName);
   }

   public Charset getCharset() {
      return this.charset;
   }
}
