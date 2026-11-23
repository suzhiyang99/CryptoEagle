package org.apache.wicket.util.resource;

public final class StringResourceStream extends AbstractStringResourceStream {
   private static final long serialVersionUID = 1L;
   private final CharSequence string;

   public StringResourceStream(CharSequence string) {
      this(string, (String)null);
   }

   public StringResourceStream(CharSequence string, String contentType) {
      super(contentType);
      this.string = string;
   }

   public String toString() {
      return super.toString() + ": " + this.string.toString();
   }

   protected String getString() {
      return this.string.toString();
   }

   public String asString() {
      return this.getString();
   }
}
