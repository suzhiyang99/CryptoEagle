package org.apache.wicket.util.io;

import java.io.Writer;
import org.apache.wicket.util.string.AppendingStringBuffer;

public class StringBufferWriter extends Writer {
   private AppendingStringBuffer buffer = new AppendingStringBuffer(4096);

   public AppendingStringBuffer getStringBuffer() {
      return this.buffer;
   }

   public void setStringBuffer(AppendingStringBuffer buffer) {
      this.buffer = buffer;
   }

   public void write(char ch) {
      this.buffer.append(ch);
   }

   public void write(char[] charArray) {
      this.buffer.append(charArray);
   }

   public void write(char[] charArray, int offset, int length) {
      this.buffer.append(charArray, offset, length);
   }

   public void write(String string) {
      this.buffer.append(string);
   }

   public void write(String string, int offset, int length) {
      this.buffer.append(string.substring(offset, offset + length));
   }

   public void flush() {
   }

   public void reset() {
      this.buffer.setLength(0);
   }

   public void close() {
   }
}
