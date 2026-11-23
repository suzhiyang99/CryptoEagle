package org.spark_project.jetty.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class EncodingHttpWriter extends HttpWriter {
   final Writer _converter;

   public EncodingHttpWriter(HttpOutput out, String encoding) {
      super(out);

      try {
         this._converter = new OutputStreamWriter(this._bytes, encoding);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }

   public void write(char[] s, int offset, int length) throws IOException {
      HttpOutput out = this._out;
      if (length == 0 && out.isAllContentWritten()) {
         out.close();
      } else {
         while(length > 0) {
            this._bytes.reset();
            int chars = length > 512 ? 512 : length;
            this._converter.write(s, offset, chars);
            this._converter.flush();
            this._bytes.writeTo(out);
            length -= chars;
            offset += chars;
         }

      }
   }
}
