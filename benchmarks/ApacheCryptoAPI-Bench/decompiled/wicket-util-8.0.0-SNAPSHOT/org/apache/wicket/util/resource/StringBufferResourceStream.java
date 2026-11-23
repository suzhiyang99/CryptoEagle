package org.apache.wicket.util.resource;

import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.time.Time;

public class StringBufferResourceStream extends AbstractStringResourceStream {
   private static final long serialVersionUID = 1L;
   private final AppendingStringBuffer buffer = new AppendingStringBuffer(128);

   public StringBufferResourceStream() {
   }

   public StringBufferResourceStream(String contentType) {
      super(contentType);
   }

   public StringBufferResourceStream append(CharSequence s) {
      this.buffer.append((Object)s);
      this.setLastModified(Time.now());
      return this;
   }

   public StringBufferResourceStream prepend(CharSequence s) {
      this.buffer.insert(0, (Object)s);
      this.setLastModified(Time.now());
      return this;
   }

   public StringBufferResourceStream clear() {
      this.buffer.delete(0, this.buffer.length());
      return this;
   }

   protected String getString() {
      return this.buffer.toString();
   }
}
