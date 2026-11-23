package org.apache.tika.language;

import java.io.IOException;
import java.io.Writer;

/** @deprecated */
@Deprecated
public class ProfilingWriter extends Writer {
   private final LanguageProfile profile;
   private char[] buffer;
   private int n;

   public ProfilingWriter(LanguageProfile profile) {
      this.buffer = new char[]{'\u0000', '\u0000', '_'};
      this.n = 1;
      this.profile = profile;
   }

   public ProfilingWriter() {
      this(new LanguageProfile());
   }

   public LanguageProfile getProfile() {
      return this.profile;
   }

   public LanguageIdentifier getLanguage() {
      return new LanguageIdentifier(this.profile);
   }

   public void write(char[] cbuf, int off, int len) {
      for(int i = 0; i < len; ++i) {
         char c = Character.toLowerCase(cbuf[off + i]);
         if (Character.isLetter(c)) {
            this.addLetter(c);
         } else {
            this.addSeparator();
         }
      }

   }

   private void addLetter(char c) {
      System.arraycopy(this.buffer, 1, this.buffer, 0, this.buffer.length - 1);
      this.buffer[this.buffer.length - 1] = c;
      ++this.n;
      if (this.n >= this.buffer.length) {
         this.profile.add(new String(this.buffer));
      }

   }

   private void addSeparator() {
      this.addLetter('_');
      this.n = 1;
   }

   public void close() throws IOException {
      this.addSeparator();
   }

   public void flush() {
   }
}
