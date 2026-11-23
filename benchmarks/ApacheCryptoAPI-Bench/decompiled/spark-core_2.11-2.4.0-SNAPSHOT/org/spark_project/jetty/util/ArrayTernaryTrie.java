package org.spark_project.jetty.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArrayTernaryTrie<V> extends AbstractTrie<V> {
   private static int LO = 1;
   private static int EQ = 2;
   private static int HI = 3;
   private static final int ROW_SIZE = 4;
   private final char[] _tree;
   private final String[] _key;
   private final V[] _value;
   private char _rows;

   public ArrayTernaryTrie() {
      this(128);
   }

   public ArrayTernaryTrie(boolean insensitive) {
      this(insensitive, 128);
   }

   public ArrayTernaryTrie(int capacity) {
      this(true, capacity);
   }

   public ArrayTernaryTrie(boolean insensitive, int capacity) {
      super(insensitive);
      this._value = (V[])(new Object[capacity]);
      this._tree = new char[capacity * 4];
      this._key = new String[capacity];
   }

   public ArrayTernaryTrie(ArrayTernaryTrie<V> trie, double factor) {
      super(trie.isCaseInsensitive());
      int capacity = (int)((double)trie._value.length * factor);
      this._rows = trie._rows;
      this._value = (V[])Arrays.copyOf(trie._value, capacity);
      this._tree = Arrays.copyOf(trie._tree, capacity * 4);
      this._key = (String[])Arrays.copyOf(trie._key, capacity);
   }

   public void clear() {
      this._rows = 0;
      Arrays.fill(this._value, (Object)null);
      Arrays.fill(this._tree, '\u0000');
      Arrays.fill(this._key, (Object)null);
   }

   public boolean put(String s, V v) {
      int t = 0;
      int limit = s.length();
      int last = 0;

      for(int k = 0; k < limit; ++k) {
         char c = s.charAt(k);
         if (this.isCaseInsensitive() && c < 128) {
            c = StringUtil.lowercases[c];
         }

         while(true) {
            int row = 4 * t;
            if (t == this._rows) {
               ++this._rows;
               if (this._rows >= this._key.length) {
                  --this._rows;
                  return false;
               }

               this._tree[row] = c;
            }

            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[last = row + EQ];
            } else if (diff < 0) {
               t = this._tree[last = row + LO];
            } else {
               t = this._tree[last = row + HI];
            }

            if (t == 0) {
               t = this._rows;
               this._tree[last] = (char)t;
            }

            if (diff == 0) {
               break;
            }
         }
      }

      if (t == this._rows) {
         ++this._rows;
         if (this._rows >= this._key.length) {
            --this._rows;
            return false;
         }
      }

      this._key[t] = v == null ? null : s;
      this._value[t] = v;
      return true;
   }

   public V get(String s, int offset, int len) {
      int t = 0;
      int i = 0;

      label29:
      while(i < len) {
         char c = s.charAt(offset + i++);
         if (this.isCaseInsensitive() && c < 128) {
            c = StringUtil.lowercases[c];
         }

         do {
            int row = 4 * t;
            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[row + EQ];
               if (t == 0) {
                  return null;
               }
               continue label29;
            }

            t = this._tree[row + hilo(diff)];
         } while(t != 0);

         return null;
      }

      return (V)this._value[t];
   }

   public V get(ByteBuffer b, int offset, int len) {
      int t = 0;
      offset += b.position();
      int i = 0;

      label27:
      while(i < len) {
         byte c = (byte)(b.get(offset + i++) & 127);
         if (this.isCaseInsensitive()) {
            c = (byte)StringUtil.lowercases[c];
         }

         do {
            int row = 4 * t;
            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[row + EQ];
               if (t == 0) {
                  return null;
               }
               continue label27;
            }

            t = this._tree[row + hilo(diff)];
         } while(t != 0);

         return null;
      }

      return (V)this._value[t];
   }

   public V getBest(String s) {
      return (V)this.getBest(0, (String)s, 0, s.length());
   }

   public V getBest(String s, int offset, int length) {
      return (V)this.getBest(0, (String)s, offset, length);
   }

   private V getBest(int t, String s, int offset, int len) {
      int node = t;
      int end = offset + len;

      label35:
      while(offset < end) {
         char c = s.charAt(offset++);
         --len;
         if (this.isCaseInsensitive() && c < 128) {
            c = StringUtil.lowercases[c];
         }

         do {
            int row = 4 * t;
            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[row + EQ];
               if (t == 0) {
                  return (V)this._value[node];
               }

               if (this._key[t] != null) {
                  node = t;
                  V better = (V)this.getBest(t, s, offset, len);
                  if (better != null) {
                     return better;
                  }
               }
               continue label35;
            }

            t = this._tree[row + hilo(diff)];
         } while(t != 0);

         return (V)this._value[node];
      }

      return (V)this._value[node];
   }

   public V getBest(ByteBuffer b, int offset, int len) {
      return (V)(b.hasArray() ? this.getBest(0, (byte[])b.array(), b.arrayOffset() + b.position() + offset, len) : this.getBest(0, (ByteBuffer)b, offset, len));
   }

   private V getBest(int t, byte[] b, int offset, int len) {
      int node = t;
      int end = offset + len;

      label33:
      while(offset < end) {
         byte c = (byte)(b[offset++] & 127);
         --len;
         if (this.isCaseInsensitive()) {
            c = (byte)StringUtil.lowercases[c];
         }

         do {
            int row = 4 * t;
            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[row + EQ];
               if (t == 0) {
                  return (V)this._value[node];
               }

               if (this._key[t] != null) {
                  node = t;
                  V better = (V)this.getBest(t, b, offset, len);
                  if (better != null) {
                     return better;
                  }
               }
               continue label33;
            }

            t = this._tree[row + hilo(diff)];
         } while(t != 0);

         return (V)this._value[node];
      }

      return (V)this._value[node];
   }

   private V getBest(int t, ByteBuffer b, int offset, int len) {
      int node = t;
      int o = offset + b.position();
      int i = 0;

      label34:
      while(i < len) {
         byte c = (byte)(b.get(o + i) & 127);
         if (this.isCaseInsensitive()) {
            c = (byte)StringUtil.lowercases[c];
         }

         do {
            int row = 4 * t;
            char n = this._tree[row];
            int diff = n - c;
            if (diff == 0) {
               t = this._tree[row + EQ];
               if (t == 0) {
                  return (V)this._value[node];
               }

               if (this._key[t] != null) {
                  node = t;
                  V best = (V)this.getBest(t, b, offset + i + 1, len - i - 1);
                  if (best != null) {
                     return best;
                  }
               }

               ++i;
               continue label34;
            }

            t = this._tree[row + hilo(diff)];
         } while(t != 0);

         return (V)this._value[node];
      }

      return (V)this._value[node];
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();

      for(int r = 0; r <= this._rows; ++r) {
         if (this._key[r] != null && this._value[r] != null) {
            buf.append(',');
            buf.append(this._key[r]);
            buf.append('=');
            buf.append(this._value[r].toString());
         }
      }

      if (buf.length() == 0) {
         return "{}";
      } else {
         buf.setCharAt(0, '{');
         buf.append('}');
         return buf.toString();
      }
   }

   public Set<String> keySet() {
      Set<String> keys = new HashSet();

      for(int r = 0; r <= this._rows; ++r) {
         if (this._key[r] != null && this._value[r] != null) {
            keys.add(this._key[r]);
         }
      }

      return keys;
   }

   public boolean isFull() {
      return this._rows + 1 == this._key.length;
   }

   public static int hilo(int diff) {
      return 1 + (diff | Integer.MAX_VALUE) / 1073741823;
   }

   public void dump() {
      for(int r = 0; r < this._rows; ++r) {
         char c = this._tree[r * 4 + 0];
         System.err.printf("%4d [%s,%d,%d,%d] '%s':%s%n", r, c >= ' ' && c <= 127 ? "'" + c + "'" : "" + c, Integer.valueOf(this._tree[r * 4 + LO]), Integer.valueOf(this._tree[r * 4 + EQ]), Integer.valueOf(this._tree[r * 4 + HI]), this._key[r], this._value[r]);
      }

   }
}
