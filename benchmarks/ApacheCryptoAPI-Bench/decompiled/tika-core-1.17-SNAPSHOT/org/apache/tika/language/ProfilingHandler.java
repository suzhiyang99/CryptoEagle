package org.apache.tika.language;

import java.io.Writer;
import org.apache.tika.sax.WriteOutContentHandler;

/** @deprecated */
@Deprecated
public class ProfilingHandler extends WriteOutContentHandler {
   private final ProfilingWriter writer;

   public ProfilingHandler(ProfilingWriter writer) {
      super((Writer)writer);
      this.writer = writer;
   }

   public ProfilingHandler(LanguageProfile profile) {
      this(new ProfilingWriter(profile));
   }

   public ProfilingHandler() {
      this(new ProfilingWriter());
   }

   public LanguageProfile getProfile() {
      return this.writer.getProfile();
   }

   public LanguageIdentifier getLanguage() {
      return this.writer.getLanguage();
   }
}
