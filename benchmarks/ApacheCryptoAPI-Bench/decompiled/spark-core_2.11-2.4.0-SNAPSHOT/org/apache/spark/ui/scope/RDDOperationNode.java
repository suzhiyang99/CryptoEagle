package org.apache.spark.ui.scope;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0015d!B\u0001\u0003\u0001\u001aa!\u0001\u0005*E\t>\u0003XM]1uS>tgj\u001c3f\u0015\t\u0019A!A\u0003tG>\u0004XM\u0003\u0002\u0006\r\u0005\u0011Q/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON!\u0001!D\n\u0017!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011a\u0002F\u0005\u0003+=\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000f/%\u0011\u0001d\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t5\u0001\u0011)\u001a!C\u00019\u0005\u0011\u0011\u000eZ\u0002\u0001+\u0005i\u0002C\u0001\b\u001f\u0013\tyrBA\u0002J]RD\u0001\"\t\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0004S\u0012\u0004\u0003\u0002C\u0012\u0001\u0005+\u0007I\u0011\u0001\u0013\u0002\t9\fW.Z\u000b\u0002KA\u0011a%\u000b\b\u0003\u001d\u001dJ!\u0001K\b\u0002\rA\u0013X\rZ3g\u0013\tQ3F\u0001\u0004TiJLgn\u001a\u0006\u0003Q=A\u0001\"\f\u0001\u0003\u0012\u0003\u0006I!J\u0001\u0006]\u0006lW\r\t\u0005\t_\u0001\u0011)\u001a!C\u0001a\u000511-Y2iK\u0012,\u0012!\r\t\u0003\u001dIJ!aM\b\u0003\u000f\t{w\u000e\\3b]\"AQ\u0007\u0001B\tB\u0003%\u0011'A\u0004dC\u000eDW\r\u001a\u0011\t\u0011]\u0002!Q3A\u0005\u0002\u0011\n\u0001bY1mYNLG/\u001a\u0005\ts\u0001\u0011\t\u0012)A\u0005K\u0005I1-\u00197mg&$X\r\t\u0005\u0006w\u0001!\t\u0001P\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000buz\u0004)\u0011\"\u0011\u0005y\u0002Q\"\u0001\u0002\t\u000biQ\u0004\u0019A\u000f\t\u000b\rR\u0004\u0019A\u0013\t\u000b=R\u0004\u0019A\u0019\t\u000b]R\u0004\u0019A\u0013\t\u000f\u0011\u0003\u0011\u0011!C\u0001\u000b\u0006!1m\u001c9z)\u0015idi\u0012%J\u0011\u001dQ2\t%AA\u0002uAqaI\"\u0011\u0002\u0003\u0007Q\u0005C\u00040\u0007B\u0005\t\u0019A\u0019\t\u000f]\u001a\u0005\u0013!a\u0001K!91\nAI\u0001\n\u0003a\u0015AD2paf$C-\u001a4bk2$H%M\u000b\u0002\u001b*\u0012QDT\u0016\u0002\u001fB\u0011\u0001+V\u0007\u0002#*\u0011!kU\u0001\nk:\u001c\u0007.Z2lK\u0012T!\u0001V\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002W#\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000fa\u0003\u0011\u0013!C\u00013\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u0012T#\u0001.+\u0005\u0015r\u0005b\u0002/\u0001#\u0003%\t!X\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00134+\u0005q&FA\u0019O\u0011\u001d\u0001\u0007!%A\u0005\u0002e\u000babY8qs\u0012\"WMZ1vYR$C\u0007C\u0004c\u0001\u0005\u0005I\u0011I2\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005!\u0007CA3k\u001b\u00051'BA4i\u0003\u0011a\u0017M\\4\u000b\u0003%\fAA[1wC&\u0011!F\u001a\u0005\bY\u0002\t\t\u0011\"\u0001\u001d\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011\u001dq\u0007!!A\u0005\u0002=\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002qgB\u0011a\"]\u0005\u0003e>\u00111!\u00118z\u0011\u001d!X.!AA\u0002u\t1\u0001\u001f\u00132\u0011\u001d1\b!!A\u0005B]\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002qB\u0019\u0011\u0010 9\u000e\u0003iT!a_\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002~u\nA\u0011\n^3sCR|'\u000f\u0003\u0005\u0000\u0001\u0005\u0005I\u0011AA\u0001\u0003!\u0019\u0017M\\#rk\u0006dGcA\u0019\u0002\u0004!9AO`A\u0001\u0002\u0004\u0001\b\"CA\u0004\u0001\u0005\u0005I\u0011IA\u0005\u0003!A\u0017m\u001d5D_\u0012,G#A\u000f\t\u0013\u00055\u0001!!A\u0005B\u0005=\u0011\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0011D\u0011\"a\u0005\u0001\u0003\u0003%\t%!\u0006\u0002\r\u0015\fX/\u00197t)\r\t\u0014q\u0003\u0005\ti\u0006E\u0011\u0011!a\u0001a\u001eQ\u00111\u0004\u0002\u0002\u0002#\u0005a!!\b\u0002!I#Ei\u00149fe\u0006$\u0018n\u001c8O_\u0012,\u0007c\u0001 \u0002 \u0019I\u0011AAA\u0001\u0012\u00031\u0011\u0011E\n\u0006\u0003?\t\u0019C\u0006\t\n\u0003K\tY#H\u00132Kuj!!a\n\u000b\u0007\u0005%r\"A\u0004sk:$\u0018.\\3\n\t\u00055\u0012q\u0005\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:$\u0004bB\u001e\u0002 \u0011\u0005\u0011\u0011\u0007\u000b\u0003\u0003;A!\"!\u0004\u0002 \u0005\u0005IQIA\b\u0011)\t9$a\b\u0002\u0002\u0013\u0005\u0015\u0011H\u0001\u0006CB\u0004H.\u001f\u000b\n{\u0005m\u0012QHA \u0003\u0003BaAGA\u001b\u0001\u0004i\u0002BB\u0012\u00026\u0001\u0007Q\u0005\u0003\u00040\u0003k\u0001\r!\r\u0005\u0007o\u0005U\u0002\u0019A\u0013\t\u0015\u0005\u0015\u0013qDA\u0001\n\u0003\u000b9%A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005%\u0013Q\u000b\t\u0006\u001d\u0005-\u0013qJ\u0005\u0004\u0003\u001bz!AB(qi&|g\u000eE\u0004\u000f\u0003#jR%M\u0013\n\u0007\u0005MsB\u0001\u0004UkBdW\r\u000e\u0005\n\u0003/\n\u0019%!AA\u0002u\n1\u0001\u001f\u00131\u0011)\tY&a\b\u0002\u0002\u0013%\u0011QL\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0002`A\u0019Q-!\u0019\n\u0007\u0005\rdM\u0001\u0004PE*,7\r\u001e"
)
public class RDDOperationNode implements Product, Serializable {
   private final int id;
   private final String name;
   private final boolean cached;
   private final String callsite;

   public static Option<Tuple4<Object, String, Object, String>> unapply(RDDOperationNode var0) {
      return RDDOperationNode$.MODULE$.unapply(var0);
   }

   public static RDDOperationNode apply(int var0, String var1, boolean var2, String var3) {
      return RDDOperationNode$.MODULE$.apply(var0, var1, var2, var3);
   }

   public static Function1<Tuple4<Object, String, Object, String>, RDDOperationNode> tupled() {
      return RDDOperationNode$.MODULE$.tupled();
   }

   public static Function1<Object, Function1<String, Function1<Object, Function1<String, RDDOperationNode>>>> curried() {
      return RDDOperationNode$.MODULE$.curried();
   }

   public int id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public boolean cached() {
      return this.cached;
   }

   public String callsite() {
      return this.callsite;
   }

   public RDDOperationNode copy(int id, String name, boolean cached, String callsite) {
      return new RDDOperationNode(id, name, cached, callsite);
   }

   public int copy$default$1() {
      return this.id();
   }

   public String copy$default$2() {
      return this.name();
   }

   public boolean copy$default$3() {
      return this.cached();
   }

   public String copy$default$4() {
      return this.callsite();
   }

   public String productPrefix() {
      return "RDDOperationNode";
   }

   public int productArity() {
      return 4;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToInteger(this.id());
            break;
         case 1:
            var10000 = this.name();
            break;
         case 2:
            var10000 = BoxesRunTime.boxToBoolean(this.cached());
            break;
         case 3:
            var10000 = this.callsite();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof RDDOperationNode;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.id());
      var1 = Statics.mix(var1, Statics.anyHash(this.name()));
      var1 = Statics.mix(var1, this.cached() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.callsite()));
      return Statics.finalizeHash(var1, 4);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label65: {
            boolean var3;
            if (x$1 instanceof RDDOperationNode) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label48: {
                  RDDOperationNode var4 = (RDDOperationNode)x$1;
                  if (this.id() == var4.id()) {
                     label46: {
                        String var10000 = this.name();
                        String var5 = var4.name();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label46;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label46;
                        }

                        if (this.cached() == var4.cached()) {
                           label40: {
                              var10000 = this.callsite();
                              String var6 = var4.callsite();
                              if (var10000 == null) {
                                 if (var6 != null) {
                                    break label40;
                                 }
                              } else if (!var10000.equals(var6)) {
                                 break label40;
                              }

                              if (var4.canEqual(this)) {
                                 var9 = true;
                                 break label48;
                              }
                           }
                        }
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label65;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public RDDOperationNode(int id, String name, boolean cached, String callsite) {
      this.id = id;
      this.name = name;
      this.cached = cached;
      this.callsite = callsite;
      Product$class.$init$(this);
   }
}
