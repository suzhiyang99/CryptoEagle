package org.spark_project.jetty.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTrie<V> implements Trie<V> {
   final boolean _caseInsensitive;

   protected AbstractTrie(boolean insensitive) {
      this._caseInsensitive = insensitive;
   }

   public boolean put(V v) {
      return this.put(v.toString(), v);
   }

   public V remove(String s) {
      V o = (V)this.get(s);
      this.put(s, (Object)null);
      return o;
   }

   public V get(String s) {
      return (V)this.get(s, 0, s.length());
   }

   public V get(ByteBuffer b) {
      return (V)this.get(b, 0, b.remaining());
   }

   public V getBest(String s) {
      return (V)this.getBest(s, 0, s.length());
   }

   public V getBest(byte[] b, int offset, int len) {
      return (V)this.getBest(new String(b, offset, len, StandardCharsets.ISO_8859_1));
   }

   public boolean isCaseInsensitive() {
      return this._caseInsensitive;
   }
}
