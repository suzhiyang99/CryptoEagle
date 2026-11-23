package org.apache.spark.api.java;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.Map;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dqAB\u0001\u0003\u0011\u00031A\"A\u0005KCZ\fW\u000b^5mg*\u00111\u0001B\u0001\u0005U\u00064\u0018M\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u0004\"!\u0004\b\u000e\u0003\t1aa\u0004\u0002\t\u0002\u0019\u0001\"!\u0003&bm\u0006,F/\u001b7t'\tq\u0011\u0003\u0005\u0002\u0013+5\t1CC\u0001\u0015\u0003\u0015\u00198-\u00197b\u0013\t12C\u0001\u0004B]f\u0014VM\u001a\u0005\u000619!\tAG\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\tA\u0002C\u0003\u001d\u001d\u0011\u0005Q$\u0001\tpaRLwN\u001c+p\u001fB$\u0018n\u001c8bYV\u0011a\u0004\n\u000b\u0003?5\u00022!\u0004\u0011#\u0013\t\t#A\u0001\u0005PaRLwN\\1m!\t\u0019C\u0005\u0004\u0001\u0005\u000b\u0015Z\"\u0019\u0001\u0014\u0003\u0003Q\u000b\"a\n\u0016\u0011\u0005IA\u0013BA\u0015\u0014\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"AE\u0016\n\u00051\u001a\"aA!os\")af\u0007a\u0001_\u00051q\u000e\u001d;j_:\u00042A\u0005\u0019#\u0013\t\t4C\u0001\u0004PaRLwN\u001c\u0005\u0006g9!\t\u0001N\u0001\u0019[\u0006\u0004\u0018i]*fe&\fG.\u001b>bE2,'*\u0019<b\u001b\u0006\u0004X\u0003B\u001b\u007f\u0003\u0003!2ANA\u0002!\u00119\u0004(`@\u000e\u000391Q!\u000f\b\u0001ue\u0013acU3sS\u0006d\u0017N_1cY\u0016l\u0015\r],sCB\u0004XM]\u000b\u0004w\u0011;5c\u0001\u001d=\u0013B!Q(Q\"G\u001b\u0005q$BA A\u0003\u0011)H/\u001b7\u000b\u0003\rI!A\u0011 \u0003\u0017\u0005\u00137\u000f\u001e:bGRl\u0015\r\u001d\t\u0003G\u0011#Q!\u0012\u001dC\u0002\u0019\u0012\u0011!\u0011\t\u0003G\u001d#Q\u0001\u0013\u001dC\u0002\u0019\u0012\u0011A\u0011\t\u0003\u00156k\u0011a\u0013\u0006\u0003\u0019\u0002\u000b!![8\n\u00059[%\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003)9\u0005\u0003\u0005\u000b\u0011B)\u0002\u0015UtG-\u001a:ms&tw\r\u0005\u0003S+\u000e3U\"A*\u000b\u0005Q\u001b\u0012AC2pY2,7\r^5p]&\u0011ak\u0015\u0002\u0004\u001b\u0006\u0004\b\"\u0002\r9\t\u0003AFCA-[!\u00119\u0004h\u0011$\t\u000bA;\u0006\u0019A)\t\u000bqCD\u0011I/\u0002\tML'0\u001a\u000b\u0002=B\u0011!cX\u0005\u0003AN\u00111!\u00138u\u0011\u0015\u0011\u0007\b\"\u0011d\u0003-\u0019wN\u001c;bS:\u001c8*Z=\u0015\u0005\u0011<\u0007C\u0001\nf\u0013\t17CA\u0004C_>dW-\u00198\t\u000b!\f\u0007\u0019A\t\u0002\u0007-,\u0017\u0010C\u0003kq\u0011\u00053.A\u0002hKR$\"A\u00127\t\u000b!L\u0007\u0019A\t\t\u000b9DD\u0011I8\u0002\u0011\u0015tGO]=TKR$\u0012\u0001\u001d\t\u0004{E\u001c\u0018B\u0001:?\u0005\r\u0019V\r\u001e\t\u0005ij\u001ceI\u0004\u0002vq:\u0011ao^\u0007\u0002\u0001&\u0011q\bQ\u0005\u0003sz\n1!T1q\u0013\tYHPA\u0003F]R\u0014\u0018P\u0003\u0002z}A\u00111E \u0003\u0006\u000bJ\u0012\rA\n\t\u0004G\u0005\u0005A!\u0002%3\u0005\u00041\u0003B\u0002)3\u0001\u0004\t)\u0001\u0005\u0003S+v|\b"
)
public final class JavaUtils {
   public static <A, B> SerializableMapWrapper<A, B> mapAsSerializableJavaMap(Map<A, B> var0) {
      return JavaUtils$.MODULE$.<A, B>mapAsSerializableJavaMap(var0);
   }

   public static <T> Optional<T> optionToOptional(Option<T> var0) {
      return JavaUtils$.MODULE$.<T>optionToOptional(var0);
   }

   public static class SerializableMapWrapper<A, B> extends AbstractMap<A, B> implements Serializable {
      public final Map<A, B> org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying;

      public int size() {
         return this.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying.size();
      }

      public boolean containsKey(Object key) {
         boolean var10000;
         try {
            var10000 = this.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying.contains(key);
         } catch (ClassCastException var2) {
            var10000 = false;
         }

         return var10000;
      }

      public B get(Object key) {
         Object var10000;
         try {
            var10000 = this.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying.getOrElse(key, new scala.Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final B apply() {
                  return null;
               }
            });
         } catch (ClassCastException var2) {
            var10000 = null;
         }

         return (B)var10000;
      }

      public Set<java.util.Map.Entry<A, B>> entrySet() {
         return new AbstractSet<java.util.Map.Entry<A, B>>(this) {
            // $FF: synthetic field
            private final SerializableMapWrapper $outer;

            public int size() {
               return this.$outer.size();
            }

            public Iterator<java.util.Map.Entry<A, B>> iterator() {
               return new Iterator<java.util.Map.Entry<A, B>>(this) {
                  private final scala.collection.Iterator<Tuple2<A, B>> ui;
                  private Option<A> prev;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  private scala.collection.Iterator<Tuple2<A, B>> ui() {
                     return this.ui;
                  }

                  private Option<A> prev() {
                     return this.prev;
                  }

                  private void prev_$eq(Option<A> x$1) {
                     this.prev = x$1;
                  }

                  public boolean hasNext() {
                     return this.ui().hasNext();
                  }

                  public java.util.Map.Entry<A, B> next() {
                     Tuple2 var2 = (Tuple2)this.ui().next();
                     if (var2 != null) {
                        Object k = var2._1();
                        Object v = var2._2();
                        Tuple2 var5 = new Tuple2(k, v);
                        Object k = var5._1();
                        Object v = var5._2();
                        this.prev_$eq(new Some(k));
                        return new java.util.Map.Entry<A, B>(this, k, v) {
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final Object k$1;
                           private final Object v$1;

                           public A getKey() {
                              return (A)this.k$1;
                           }

                           public B getValue() {
                              return (B)this.v$1;
                           }

                           public B setValue(B v1) {
                              return (B)this.$outer.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$anon$$$outer().org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$$outer().put(this.k$1, v1);
                           }

                           public int hashCode() {
                              return scala.util.hashing.package$.MODULE$.byteswap32(this.k$1.hashCode()) + (scala.util.hashing.package$.MODULE$.byteswap32(this.v$1.hashCode()) << 16);
                           }

                           public boolean equals(Object other) {
                              boolean var4;
                              if (other instanceof java.util.Map.Entry) {
                                 java.util.Map.Entry var3 = (java.util.Map.Entry)other;
                                 var4 = BoxesRunTime.equals(this.k$1, var3.getKey()) && BoxesRunTime.equals(this.v$1, var3.getValue());
                              } else {
                                 var4 = false;
                              }

                              return var4;
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.k$1 = k$1;
                                 this.v$1 = v$1;
                              }
                           }
                        };
                     } else {
                        throw new MatchError(var2);
                     }
                  }

                  public void remove() {
                     Option var1 = this.prev();
                     if (var1 instanceof Some) {
                        Some var2 = (Some)var1;
                        Object k = var2.x();
                        Map var5 = this.$outer.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$$outer().org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying;
                        if (var5 instanceof scala.collection.mutable.Map) {
                           scala.collection.mutable.Map var6 = (scala.collection.mutable.Map)var5;
                           var6.remove(k);
                           this.prev_$eq(None$.MODULE$);
                           BoxedUnit var7 = BoxedUnit.UNIT;
                           BoxedUnit var4 = BoxedUnit.UNIT;
                        } else {
                           throw new UnsupportedOperationException("remove");
                        }
                     } else {
                        throw new IllegalStateException("next must be called at least once before remove");
                     }
                  }

                  // $FF: synthetic method
                  public <undefinedtype> org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$anon$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.ui = $outer.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$$outer().org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying.iterator();
                        this.prev = None$.MODULE$;
                     }
                  }
               };
            }

            // $FF: synthetic method
            public SerializableMapWrapper org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$anon$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
      }

      public SerializableMapWrapper(Map<A, B> underlying) {
         this.org$apache$spark$api$java$JavaUtils$SerializableMapWrapper$$underlying = underlying;
      }
   }
}
