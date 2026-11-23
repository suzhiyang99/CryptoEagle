package org.apache.tika.language.translate;

import java.io.IOException;
import org.apache.tika.exception.TikaException;

public interface Translator {
   String translate(String var1, String var2, String var3) throws TikaException, IOException;

   String translate(String var1, String var2) throws TikaException, IOException;

   boolean isAvailable();
}
