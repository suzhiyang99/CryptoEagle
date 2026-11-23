package org.apache.tika.extractor;

import java.io.InputStream;
import org.apache.tika.mime.MediaType;

public interface EmbeddedResourceHandler {
   void handle(String var1, MediaType var2, InputStream var3);
}
