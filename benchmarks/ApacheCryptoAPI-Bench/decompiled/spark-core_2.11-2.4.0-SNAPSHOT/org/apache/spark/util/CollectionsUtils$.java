package org.apache.spark.util;

import java.util.Arrays;
import java.util.Comparator;
import scala.Function2;
import scala.Predef$;
import scala.Serializable;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.BoxesRunTime;

public final class CollectionsUtils$ {
   public static final CollectionsUtils$ MODULE$;

   static {
      new CollectionsUtils$();
   }

   public <K> Function2<Object, K, Object> makeBinarySearch(Ordering<K> evidence$1, ClassTag<K> evidence$2) {
      Serializable var5;
      label119: {
         ClassTag var3 = scala.reflect.package$.MODULE$.classTag(evidence$2);
         ClassTag var10000 = ClassTag$.MODULE$.Float();
         if (var10000 == null) {
            if (var3 == null) {
               break label119;
            }
         } else if (var10000.equals(var3)) {
            break label119;
         }

         label120: {
            var10000 = ClassTag$.MODULE$.Double();
            if (var10000 == null) {
               if (var3 == null) {
                  break label120;
               }
            } else if (var10000.equals(var3)) {
               break label120;
            }

            label121: {
               var10000 = ClassTag$.MODULE$.Byte();
               if (var10000 == null) {
                  if (var3 == null) {
                     break label121;
                  }
               } else if (var10000.equals(var3)) {
                  break label121;
               }

               label122: {
                  var10000 = ClassTag$.MODULE$.Char();
                  if (var10000 == null) {
                     if (var3 == null) {
                        break label122;
                     }
                  } else if (var10000.equals(var3)) {
                     break label122;
                  }

                  label123: {
                     var10000 = ClassTag$.MODULE$.Short();
                     if (var10000 == null) {
                        if (var3 == null) {
                           break label123;
                        }
                     } else if (var10000.equals(var3)) {
                        break label123;
                     }

                     label124: {
                        var10000 = ClassTag$.MODULE$.Int();
                        if (var10000 == null) {
                           if (var3 == null) {
                              break label124;
                           }
                        } else if (var10000.equals(var3)) {
                           break label124;
                        }

                        label125: {
                           var10000 = ClassTag$.MODULE$.Long();
                           if (var10000 == null) {
                              if (var3 == null) {
                                 break label125;
                              }
                           } else if (var10000.equals(var3)) {
                              break label125;
                           }

                           Comparator comparator = (Comparator)Predef$.MODULE$.implicitly(evidence$1);
                           var5 = new Serializable(comparator) {
                              public static final long serialVersionUID = 0L;
                              private final Comparator comparator$1;

                              public final int apply(Object l, K x) {
                                 return Arrays.binarySearch(l, x, this.comparator$1);
                              }

                              public {
                                 this.comparator$1 = comparator$1;
                              }
                           };
                           return var5;
                        }

                        var5 = new Serializable() {
                           public static final long serialVersionUID = 0L;

                           public final int apply(Object l, K x) {
                              return Arrays.binarySearch((long[])l, BoxesRunTime.unboxToLong(x));
                           }
                        };
                        return var5;
                     }

                     var5 = new Serializable() {
                        public static final long serialVersionUID = 0L;

                        public final int apply(Object l, K x) {
                           return Arrays.binarySearch((int[])l, BoxesRunTime.unboxToInt(x));
                        }
                     };
                     return var5;
                  }

                  var5 = new Serializable() {
                     public static final long serialVersionUID = 0L;

                     public final int apply(Object l, K x) {
                        return Arrays.binarySearch((short[])l, BoxesRunTime.unboxToShort(x));
                     }
                  };
                  return var5;
               }

               var5 = new Serializable() {
                  public static final long serialVersionUID = 0L;

                  public final int apply(Object l, K x) {
                     return Arrays.binarySearch((char[])l, BoxesRunTime.unboxToChar(x));
                  }
               };
               return var5;
            }

            var5 = new Serializable() {
               public static final long serialVersionUID = 0L;

               public final int apply(Object l, K x) {
                  return Arrays.binarySearch((byte[])l, BoxesRunTime.unboxToByte(x));
               }
            };
            return var5;
         }

         var5 = new Serializable() {
            public static final long serialVersionUID = 0L;

            public final int apply(Object l, K x) {
               return Arrays.binarySearch((double[])l, BoxesRunTime.unboxToDouble(x));
            }
         };
         return var5;
      }

      var5 = new Serializable() {
         public static final long serialVersionUID = 0L;

         public final int apply(Object l, K x) {
            return Arrays.binarySearch((float[])l, BoxesRunTime.unboxToFloat(x));
         }
      };
      return var5;
   }

   private CollectionsUtils$() {
      MODULE$ = this;
   }
}
