package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class TextDetector implements Detector {
   private static final long serialVersionUID = 4774601079503507765L;
   private static final int DEFAULT_NUMBER_OF_BYTES_TO_TEST = 512;
   private static final boolean[] IS_CONTROL_BYTE = new boolean[32];
   private final int bytesToTest;

   public TextDetector() {
      this(512);
   }

   public TextDetector(int bytesToTest) {
      this.bytesToTest = bytesToTest;
   }

   public MediaType detect(InputStream input, Metadata metadata) throws IOException {
      if (input == null) {
         return MediaType.OCTET_STREAM;
      } else {
         input.mark(this.bytesToTest);

         MediaType var7;
         try {
            TextStatistics stats = new TextStatistics();
            byte[] buffer = new byte[1024];
            int n = 0;

            for(int m = input.read(buffer, 0, Math.min(this.bytesToTest, buffer.length)); m != -1 && n < this.bytesToTest; m = input.read(buffer, 0, Math.min(this.bytesToTest - n, buffer.length))) {
               stats.addData(buffer, 0, m);
               n += m;
            }

            if (!stats.isMostlyAscii() && !stats.looksLikeUTF8()) {
               var7 = MediaType.OCTET_STREAM;
               return var7;
            }

            var7 = MediaType.TEXT_PLAIN;
         } finally {
            input.reset();
         }

         return var7;
      }
   }

   static {
      Arrays.fill(IS_CONTROL_BYTE, true);
      IS_CONTROL_BYTE[9] = false;
      IS_CONTROL_BYTE[10] = false;
      IS_CONTROL_BYTE[12] = false;
      IS_CONTROL_BYTE[13] = false;
      IS_CONTROL_BYTE[27] = false;
   }
}
