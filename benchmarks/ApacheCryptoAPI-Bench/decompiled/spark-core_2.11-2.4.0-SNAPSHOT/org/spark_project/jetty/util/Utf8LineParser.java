package org.spark_project.jetty.util;

import java.nio.ByteBuffer;

public class Utf8LineParser {
   private State state;
   private Utf8StringBuilder utf;

   public Utf8LineParser() {
      this.state = Utf8LineParser.State.START;
   }

   public String parse(ByteBuffer buf) {
      while(true) {
         if (buf.remaining() > 0) {
            byte b = buf.get();
            if (!this.parseByte(b)) {
               continue;
            }

            this.state = Utf8LineParser.State.START;
            return this.utf.toString();
         }

         return null;
      }
   }

   private boolean parseByte(byte b) {
      switch (this.state) {
         case START:
            this.utf = new Utf8StringBuilder();
            this.state = Utf8LineParser.State.PARSE;
            return this.parseByte(b);
         case PARSE:
            if (this.utf.isUtf8SequenceComplete() && (b == 13 || b == 10)) {
               this.state = Utf8LineParser.State.END;
               return this.parseByte(b);
            }

            this.utf.append(b);
            break;
         case END:
            if (b == 10) {
               this.state = Utf8LineParser.State.START;
               return true;
            }
      }

      return false;
   }

   private static enum State {
      START,
      PARSE,
      END;
   }
}
