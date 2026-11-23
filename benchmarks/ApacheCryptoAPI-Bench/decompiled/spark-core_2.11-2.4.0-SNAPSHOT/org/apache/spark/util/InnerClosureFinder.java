package org.apache.spark.util;

import org.apache.xbean.asm5.ClassVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import org.apache.xbean.asm5.Type;
import scala.collection.mutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001E4A!\u0001\u0002\u0005\u0017\t\u0011\u0012J\u001c8fe\u000ecwn];sK\u001aKg\u000eZ3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0011\u0001\u0001\u0004\t\u0003\u001bIi\u0011A\u0004\u0006\u0003\u001fA\tA!Y:nk)\u0011\u0011CB\u0001\u0006q\n,\u0017M\\\u0005\u0003'9\u0011Ab\u00117bgN4\u0016n]5u_JD\u0001\"\u0006\u0001\u0003\u0002\u0003\u0006IAF\u0001\u0007_V$\b/\u001e;\u0011\u0007]q\u0002%D\u0001\u0019\u0015\tI\"$A\u0004nkR\f'\r\\3\u000b\u0005ma\u0012AC2pY2,7\r^5p]*\tQ$A\u0003tG\u0006d\u0017-\u0003\u0002 1\t\u00191+\u001a;1\u0005\u0005Z\u0003c\u0001\u0012'S9\u00111\u0005J\u0007\u00029%\u0011Q\u0005H\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#!B\"mCN\u001c(BA\u0013\u001d!\tQ3\u0006\u0004\u0001\u0005\u00131\"\u0012\u0011!A\u0001\u0006\u0003i#\u0001B0%eA\n\"AL\u0019\u0011\u0005\rz\u0013B\u0001\u0019\u001d\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\t\u001a\n\u0005Mb\"aA!os\")Q\u0007\u0001C\u0001m\u00051A(\u001b8jiz\"\"aN\u001d\u0011\u0005a\u0002Q\"\u0001\u0002\t\u000bU!\u0004\u0019\u0001\u001e\u0011\u0007]q2\b\r\u0002=}A\u0019!EJ\u001f\u0011\u0005)rD!\u0003\u0017:\u0003\u0003\u0005\tQ!\u0001.\u0011\u001d\u0001\u0005\u00011A\u0005\u0002\u0005\u000ba!\\=OC6,W#\u0001\"\u0011\u0005\t\u001a\u0015B\u0001#)\u0005\u0019\u0019FO]5oO\"9a\t\u0001a\u0001\n\u00039\u0015AC7z\u001d\u0006lWm\u0018\u0013fcR\u0011\u0001j\u0013\t\u0003G%K!A\u0013\u000f\u0003\tUs\u0017\u000e\u001e\u0005\b\u0019\u0016\u000b\t\u00111\u0001C\u0003\rAH%\r\u0005\u0007\u001d\u0002\u0001\u000b\u0015\u0002\"\u0002\u000f5Lh*Y7fA!)\u0001\u000b\u0001C!#\u0006)a/[:jiR9\u0001JU,Z7v{\u0006\"B*P\u0001\u0004!\u0016a\u0002<feNLwN\u001c\t\u0003GUK!A\u0016\u000f\u0003\u0007%sG\u000fC\u0003Y\u001f\u0002\u0007A+\u0001\u0004bG\u000e,7o\u001d\u0005\u00065>\u0003\rAQ\u0001\u0005]\u0006lW\rC\u0003]\u001f\u0002\u0007!)A\u0002tS\u001eDQAX(A\u0002\t\u000b\u0011b];qKJt\u0015-\\3\t\u000b\u0001|\u0005\u0019A1\u0002\u0015%tG/\u001a:gC\u000e,7\u000fE\u0002$E\nK!a\u0019\u000f\u0003\u000b\u0005\u0013(/Y=\t\u000b\u0015\u0004A\u0011\t4\u0002\u0017YL7/\u001b;NKRDw\u000e\u001a\u000b\u0007O*\\GN\\8\u0011\u00055A\u0017BA5\u000f\u00055iU\r\u001e5pIZK7/\u001b;pe\")\u0001\f\u001aa\u0001)\")!\f\u001aa\u0001\u0005\")Q\u000e\u001aa\u0001\u0005\u0006!A-Z:d\u0011\u0015aF\r1\u0001C\u0011\u0015\u0001H\r1\u0001b\u0003))\u0007pY3qi&|gn\u001d"
)
public class InnerClosureFinder extends ClassVisitor {
   public final Set<Class<?>> org$apache$spark$util$InnerClosureFinder$$output;
   private String myName;

   public String myName() {
      return this.myName;
   }

   public void myName_$eq(String x$1) {
      this.myName = x$1;
   }

   public void visit(int version, int access, String name, String sig, String superName, String[] interfaces) {
      this.myName_$eq(name);
   }

   public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
      return new MethodVisitor(this) {
         // $FF: synthetic field
         private final InnerClosureFinder $outer;

         public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
            Type[] argTypes = Type.getArgumentTypes(desc);
            if (op == 183) {
               String var7 = "<init>";
               if (name == null) {
                  if (var7 != null) {
                     return;
                  }
               } else if (!name.equals(var7)) {
                  return;
               }

               if (argTypes.length > 0 && argTypes[0].toString().startsWith("L")) {
                  String var10000 = argTypes[0].getInternalName();
                  String var8 = this.$outer.myName();
                  if (var10000 == null) {
                     if (var8 != null) {
                        return;
                     }
                  } else if (!var10000.equals(var8)) {
                     return;
                  }

                  this.$outer.org$apache$spark$util$InnerClosureFinder$$output.$plus$eq(Class.forName(owner.replace('/', '.'), false, Thread.currentThread().getContextClassLoader()));
               }
            }

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

   public InnerClosureFinder(Set<Class<?>> output) {
      super(327680);
      this.org$apache$spark$util$InnerClosureFinder$$output = output;
      this.myName = null;
   }
}
