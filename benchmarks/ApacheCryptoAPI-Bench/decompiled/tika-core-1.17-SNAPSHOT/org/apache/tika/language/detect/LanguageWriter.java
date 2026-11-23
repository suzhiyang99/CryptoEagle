package org.apache.tika.language.detect;

import java.io.IOException;
import java.io.Writer;

public class LanguageWriter extends Writer {
   private final LanguageDetector detector;

   public LanguageWriter(LanguageDetector detector) {
      this.detector = detector;
      detector.reset();
   }

   public LanguageDetector getDetector() {
      return this.detector;
   }

   public LanguageResult getLanguage() {
      return this.detector.detect();
   }

   public void write(char[] cbuf, int off, int len) {
      this.detector.addText(cbuf, off, len);
   }

   public void close() throws IOException {
   }

   public void flush() {
   }

   public void reset() {
      this.detector.reset();
   }
}
