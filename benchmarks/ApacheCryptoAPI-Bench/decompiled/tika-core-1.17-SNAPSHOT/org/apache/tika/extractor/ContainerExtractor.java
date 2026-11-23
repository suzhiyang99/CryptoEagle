package org.apache.tika.extractor;

import java.io.IOException;
import java.io.Serializable;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;

public interface ContainerExtractor extends Serializable {
   boolean isSupported(TikaInputStream var1) throws IOException;

   void extract(TikaInputStream var1, ContainerExtractor var2, EmbeddedResourceHandler var3) throws IOException, TikaException;
}
