package org.spark_project.jetty.http;

import java.nio.ByteBuffer;
import org.spark_project.jetty.util.ArrayTrie;
import org.spark_project.jetty.util.BufferUtil;
import org.spark_project.jetty.util.Trie;

public enum HttpScheme {
   HTTP("http"),
   HTTPS("https"),
   WS("ws"),
   WSS("wss");

   public static final Trie<HttpScheme> CACHE = new ArrayTrie<HttpScheme>();
   private final String _string;
   private final ByteBuffer _buffer;

   private HttpScheme(String s) {
      this._string = s;
      this._buffer = BufferUtil.toBuffer(s);
   }

   public ByteBuffer asByteBuffer() {
      return this._buffer.asReadOnlyBuffer();
   }

   public boolean is(String s) {
      return s != null && this._string.equalsIgnoreCase(s);
   }

   public String asString() {
      return this._string;
   }

   public String toString() {
      return this._string;
   }

   static {
      for(HttpScheme version : values()) {
         CACHE.put(version.asString(), version);
      }

   }
}
