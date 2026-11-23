package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class ZeroSizeFileDetector implements Detector {
   public MediaType detect(InputStream stream, Metadata metadata) throws IOException {
      if (stream != null) {
         MediaType var3;
         try {
            stream.mark(1);
            if (stream.read() != -1) {
               return MediaType.OCTET_STREAM;
            }

            var3 = MediaType.EMPTY;
         } finally {
            stream.reset();
         }

         return var3;
      } else {
         return MediaType.OCTET_STREAM;
      }
   }
}
