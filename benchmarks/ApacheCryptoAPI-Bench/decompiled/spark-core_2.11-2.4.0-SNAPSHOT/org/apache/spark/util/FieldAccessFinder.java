package org.apache.spark.util;

import org.apache.xbean.asm5.ClassVisitor;
import org.apache.xbean.asm5.MethodVisitor;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.mutable.Map;
import scala.collection.mutable.Set;
import scala.collection.mutable.SetLike;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055c!B\u0001\u0003\u0001\tQ!!\u0005$jK2$\u0017iY2fgN4\u0015N\u001c3fe*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r#5\tQB\u0003\u0002\u000f\u001f\u0005!\u0011m]76\u0015\t\u0001b!A\u0003yE\u0016\fg.\u0003\u0002\u0013\u001b\ta1\t\\1tgZK7/\u001b;pe\"AA\u0003\u0001B\u0001B\u0003%a#\u0001\u0004gS\u0016dGm]\u0002\u0001!\u00119b\u0004\t\u001b\u000e\u0003aQ!!\u0007\u000e\u0002\u000f5,H/\u00192mK*\u00111\u0004H\u0001\u000bG>dG.Z2uS>t'\"A\u000f\u0002\u000bM\u001c\u0017\r\\1\n\u0005}A\"aA'baB\u0012\u0011e\u000b\t\u0004E\u0019JcBA\u0012%\u001b\u0005a\u0012BA\u0013\u001d\u0003\u0019\u0001&/\u001a3fM&\u0011q\u0005\u000b\u0002\u0006\u00072\f7o\u001d\u0006\u0003Kq\u0001\"AK\u0016\r\u0001\u0011IAfEA\u0001\u0002\u0003\u0015\t!\f\u0002\u0005?\u0012\nt'\u0005\u0002/cA\u00111eL\u0005\u0003aq\u0011qAT8uQ&tw\r\u0005\u0002$e%\u00111\u0007\b\u0002\u0004\u0003:L\bcA\f6o%\u0011a\u0007\u0007\u0002\u0004'\u0016$\bC\u0001\u00129\u0013\tI\u0004F\u0001\u0004TiJLgn\u001a\u0005\tw\u0001\u0011\t\u0011)A\u0005y\u0005\u0001b-\u001b8e)J\fgn]5uSZ,G.\u001f\t\u0003GuJ!A\u0010\u000f\u0003\u000f\t{w\u000e\\3b]\"A\u0001\t\u0001B\u0001B\u0003%\u0011)\u0001\bta\u0016\u001c\u0017NZ5d\u001b\u0016$\bn\u001c3\u0011\u0007\r\u0012E)\u0003\u0002D9\t1q\n\u001d;j_:\u0004$!\u0012&\u0011\u0007\u0019;\u0015*D\u0001\u0003\u0013\tA%A\u0001\tNKRDw\u000eZ%eK:$\u0018NZ5feB\u0011!F\u0013\u0003\n\u0017~\n\t\u0011!A\u0003\u00025\u0012Aa\u0018\u00132q!AQ\n\u0001B\u0001B\u0003%a*\u0001\bwSNLG/\u001a3NKRDw\u000eZ:\u0011\u0007])t\n\r\u0002Q%B\u0019aiR)\u0011\u0005)\u0012F!C*M\u0003\u0003\u0005\tQ!\u0001.\u0005\u0011yF%M\u001d\t\u000bU\u0003A\u0011\u0001,\u0002\rqJg.\u001b;?)\u00159\u0006LX0f!\t1\u0005\u0001C\u0003\u0015)\u0002\u0007\u0011\f\u0005\u0003\u0018=i#\u0004GA.^!\r\u0011c\u0005\u0018\t\u0003Uu#\u0011\u0002\f-\u0002\u0002\u0003\u0005)\u0011A\u0017\t\u000bm\"\u0006\u0019\u0001\u001f\t\u000f\u0001#\u0006\u0013!a\u0001AB\u00191EQ11\u0005\t$\u0007c\u0001$HGB\u0011!\u0006\u001a\u0003\n\u0017~\u000b\t\u0011!A\u0003\u00025Bq!\u0014+\u0011\u0002\u0003\u0007a\rE\u0002\u0018k\u001d\u0004$\u0001\u001b6\u0011\u0007\u0019;\u0015\u000e\u0005\u0002+U\u0012I1+ZA\u0001\u0002\u0003\u0015\t!\f\u0005\u0006Y\u0002!\t%\\\u0001\fm&\u001c\u0018\u000e^'fi\"|G\r\u0006\u0004ocZD(\u0010 \t\u0003\u0019=L!\u0001]\u0007\u0003\u001b5+G\u000f[8e-&\u001c\u0018\u000e^8s\u0011\u0015\u00118\u000e1\u0001t\u0003\u0019\t7mY3tgB\u00111\u0005^\u0005\u0003kr\u00111!\u00138u\u0011\u001598\u000e1\u00018\u0003\u0011q\u0017-\\3\t\u000be\\\u0007\u0019A\u001c\u0002\t\u0011,7o\u0019\u0005\u0006w.\u0004\raN\u0001\u0004g&<\u0007\"B?l\u0001\u0004q\u0018AC3yG\u0016\u0004H/[8ogB\u00191e`\u001c\n\u0007\u0005\u0005ADA\u0003BeJ\f\u0017p\u0002\u0006\u0002\u0006\t\t\t\u0011#\u0001\u0003\u0003\u000f\t\u0011CR5fY\u0012\f5mY3tg\u001aKg\u000eZ3s!\r1\u0015\u0011\u0002\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0003\u0017\u0019B!!\u0003\u0002\u000eA\u00191%a\u0004\n\u0007\u0005EAD\u0001\u0004B]f\u0014VM\u001a\u0005\b+\u0006%A\u0011AA\u000b)\t\t9\u0001\u0003\u0006\u0002\u001a\u0005%\u0011\u0013!C\u0001\u00037\t1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u001aTCAA\u000fU\u0011\ty\"!\u000b\u0011\t\r\u0012\u0015\u0011\u0005\u0019\u0005\u0003G\t9\u0003\u0005\u0003G\u000f\u0006\u0015\u0002c\u0001\u0016\u0002(\u0011Q1*a\u0006\u0002\u0002\u0003\u0005)\u0011A\u0017,\u0005\u0005-\u0002\u0003BA\u0017\u0003oi!!a\f\u000b\t\u0005E\u00121G\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\u000e\u001d\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003s\tyCA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D!\"!\u0010\u0002\nE\u0005I\u0011AA \u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%iU\u0011\u0011\u0011\t\u0016\u0005\u0003\u0007\nI\u0003\u0005\u0003\u0018k\u0005\u0015\u0003\u0007BA$\u0003\u0017\u0002BAR$\u0002JA\u0019!&a\u0013\u0005\u0015M\u000bY$!A\u0001\u0002\u000b\u0005Q\u0006"
)
public class FieldAccessFinder extends ClassVisitor {
   public final Map<Class<?>, Set<String>> org$apache$spark$util$FieldAccessFinder$$fields;
   public final boolean org$apache$spark$util$FieldAccessFinder$$findTransitively;
   private final Option<MethodIdentifier<?>> specificMethod;
   public final Set<MethodIdentifier<?>> org$apache$spark$util$FieldAccessFinder$$visitedMethods;

   public static Set<MethodIdentifier<?>> $lessinit$greater$default$4() {
      return FieldAccessFinder$.MODULE$.$lessinit$greater$default$4();
   }

   public static Option<MethodIdentifier<?>> $lessinit$greater$default$3() {
      return FieldAccessFinder$.MODULE$.$lessinit$greater$default$3();
   }

   public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
      label24: {
         if (this.specificMethod.isDefined()) {
            String var10000 = ((MethodIdentifier)this.specificMethod.get()).name();
            if (var10000 == null) {
               if (name != null) {
                  break label24;
               }
            } else if (!var10000.equals(name)) {
               break label24;
            }

            var10000 = ((MethodIdentifier)this.specificMethod.get()).desc();
            if (var10000 == null) {
               if (desc != null) {
                  break label24;
               }
            } else if (!var10000.equals(desc)) {
               break label24;
            }
         }

         return new MethodVisitor(this) {
            // $FF: synthetic field
            private final FieldAccessFinder $outer;

            public void visitFieldInsn(int op, String owner, String name, String desc) {
               if (op == 180) {
                  this.$outer.org$apache$spark$util$FieldAccessFinder$$fields.keys().withFilter(new Serializable(this, owner) {
                     public static final long serialVersionUID = 0L;
                     private final String owner$1;

                     public final boolean apply(Class<?> cl) {
                        boolean var3;
                        label23: {
                           String var10000 = cl.getName();
                           String var2 = this.owner$1.replace('/', '.');
                           if (var10000 == null) {
                              if (var2 == null) {
                                 break label23;
                              }
                           } else if (var10000.equals(var2)) {
                              break label23;
                           }

                           var3 = false;
                           return var3;
                        }

                        var3 = true;
                        return var3;
                     }

                     public {
                        this.owner$1 = owner$1;
                     }
                  }).foreach(new Serializable(this, name) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final String name$1;

                     public final Set<String> apply(Class<?> cl) {
                        return (Set)((SetLike)this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$fields.apply(cl)).$plus$eq(this.name$1);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.name$1 = name$1;
                        }
                     }
                  });
               }

            }

            public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
               this.$outer.org$apache$spark$util$FieldAccessFinder$$fields.keys().withFilter(new Serializable(this, owner) {
                  public static final long serialVersionUID = 0L;
                  private final String owner$2;

                  public final boolean apply(Class<?> cl) {
                     boolean var3;
                     label23: {
                        String var10000 = cl.getName();
                        String var2 = this.owner$2.replace('/', '.');
                        if (var10000 == null) {
                           if (var2 == null) {
                              break label23;
                           }
                        } else if (var10000.equals(var2)) {
                           break label23;
                        }

                        var3 = false;
                        return var3;
                     }

                     var3 = true;
                     return var3;
                  }

                  public {
                     this.owner$2 = owner$2;
                  }
               }).foreach(new Serializable(this, op, owner, name, desc) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final int op$1;
                  private final String owner$2;
                  private final String name$2;
                  private final String desc$1;

                  public final void apply(Class<?> cl) {
                     if (this.op$1 == 182 && this.owner$2.endsWith("$iwC") && !this.name$2.endsWith("$outer")) {
                        ((SetLike)this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$fields.apply(cl)).$plus$eq(this.name$2);
                     } else {
                        BoxedUnit var10000 = BoxedUnit.UNIT;
                     }

                     if (this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$findTransitively) {
                        MethodIdentifier m = new MethodIdentifier(cl, this.name$2, this.desc$1);
                        if (!this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$visitedMethods.contains(m)) {
                           this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$visitedMethods.$plus$eq(m);
                           Class currentClass = cl;
                           Predef$.MODULE$.assert(cl != null, new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final String apply() {
                                 return "The outer class can't be null.";
                              }
                           });

                           while(currentClass != null) {
                              ClosureCleaner$.MODULE$.getClassReader(currentClass).accept(new FieldAccessFinder(this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$fields, this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$findTransitively, new Some(m), this.$outer.org$apache$spark$util$FieldAccessFinder$$anon$$$outer().org$apache$spark$util$FieldAccessFinder$$visitedMethods), 0);
                              currentClass = currentClass.getSuperclass();
                           }
                        }
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.op$1 = op$1;
                        this.owner$2 = owner$2;
                        this.name$2 = name$2;
                        this.desc$1 = desc$1;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public FieldAccessFinder org$apache$spark$util$FieldAccessFinder$$anon$$$outer() {
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

      Object var9 = null;
      return null;
   }

   public FieldAccessFinder(Map<Class<?>, Set<String>> fields, boolean findTransitively, Option<MethodIdentifier<?>> specificMethod, Set<MethodIdentifier<?>> visitedMethods) {
      super(327680);
      this.org$apache$spark$util$FieldAccessFinder$$fields = fields;
      this.org$apache$spark$util$FieldAccessFinder$$findTransitively = findTransitively;
      this.specificMethod = specificMethod;
      this.org$apache$spark$util$FieldAccessFinder$$visitedMethods = visitedMethods;
   }
}
