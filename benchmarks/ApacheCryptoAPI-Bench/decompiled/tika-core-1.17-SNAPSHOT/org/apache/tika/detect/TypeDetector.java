package org.apache.tika.detect;

import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class TypeDetector implements Detector {
   public MediaType detect(InputStream input, Metadata metadata) {
      String hint = metadata.get("Content-Type");
      if (hint != null) {
         MediaType type = MediaType.parse(hint);
         if (type != null) {
            return type;
         }
      }

      return MediaType.OCTET_STREAM;
   }
}
