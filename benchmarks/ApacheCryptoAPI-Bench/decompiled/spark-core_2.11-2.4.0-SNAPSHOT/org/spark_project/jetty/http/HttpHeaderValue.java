package org.spark_project.jetty.http;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import org.spark_project.jetty.util.ArrayTrie;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Trie;

public enum HttpHeaderValue {
   CLOSE("close"),
   CHUNKED("chunked"),
   GZIP("gzip"),
   IDENTITY("identity"),
   KEEP_ALIVE("keep-alive"),
   CONTINUE("100-continue"),
   PROCESSING("102-processing"),
   TE("TE"),
   BYTES("bytes"),
   NO_CACHE("no-cache"),
   UPGRADE("Upgrade"),
   UNKNOWN("::UNKNOWN::");

   public static final Trie<HttpHeaderValue> CACHE = new ArrayTrie<HttpHeaderValue>();
   private final String _string;
   private final ByteBuffer _buffer;
   private static EnumSet<HttpHeader> __known;

   private HttpHeaderValue(String s) {
      this._string = s;
      this._buffer = BufferUtil.toBuffer(s);
   }

   public ByteBuffer toBuffer() {
      return this._buffer.asReadOnlyBuffer();
   }

   public boolean is(String s) {
      return this._string.equalsIgnoreCase(s);
   }

   public String asString() {
      return this._string;
   }

   public String toString() {
      return this._string;
   }

   public static boolean hasKnownValues(HttpHeader header) {
      return header == null ? false : __known.contains(header);
   }

   static {
      for(HttpHeaderValue value : values()) {
         if (value != UNKNOWN) {
            CACHE.put(value.toString(), value);
         }
      }

      __known = EnumSet.of(HttpHeader.CONNECTION, HttpHeader.TRANSFER_ENCODING, HttpHeader.CONTENT_ENCODING);
   }
}
