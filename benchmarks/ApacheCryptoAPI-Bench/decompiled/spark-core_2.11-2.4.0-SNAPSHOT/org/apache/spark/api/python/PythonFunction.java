package org.apache.spark.api.python;

import java.util.List;
import java.util.Map;
import org.apache.spark.broadcast.Broadcast;
import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple7;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u001dh!B\u0001\u0003\u0001\u001aa!A\u0004)zi\"|gNR;oGRLwN\u001c\u0006\u0003\u0007\u0011\ta\u0001]=uQ>t'BA\u0003\u0007\u0003\r\t\u0007/\u001b\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON!\u0001!D\n\u0017!\tq\u0011#D\u0001\u0010\u0015\u0005\u0001\u0012!B:dC2\f\u0017B\u0001\n\u0010\u0005\u0019\te.\u001f*fMB\u0011a\u0002F\u0005\u0003+=\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000f/%\u0011\u0001d\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t5\u0001\u0011)\u001a!C\u00019\u000591m\\7nC:$7\u0001A\u000b\u0002;A\u0019aB\b\u0011\n\u0005}y!!B!se\u0006L\bC\u0001\b\"\u0013\t\u0011sB\u0001\u0003CsR,\u0007\u0002\u0003\u0013\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u0011\r|W.\\1oI\u0002B\u0001B\n\u0001\u0003\u0016\u0004%\taJ\u0001\bK:4h+\u0019:t+\u0005A\u0003\u0003B\u0015/aAj\u0011A\u000b\u0006\u0003W1\nA!\u001e;jY*\tQ&\u0001\u0003kCZ\f\u0017BA\u0018+\u0005\ri\u0015\r\u001d\t\u0003cQr!A\u0004\u001a\n\u0005Mz\u0011A\u0002)sK\u0012,g-\u0003\u00026m\t11\u000b\u001e:j]\u001eT!aM\b\t\u0011a\u0002!\u0011#Q\u0001\n!\n\u0001\"\u001a8w-\u0006\u00148\u000f\t\u0005\tu\u0001\u0011)\u001a!C\u0001w\u0005q\u0001/\u001f;i_:Len\u00197vI\u0016\u001cX#\u0001\u001f\u0011\u0007%j\u0004'\u0003\u0002?U\t!A*[:u\u0011!\u0001\u0005A!E!\u0002\u0013a\u0014a\u00049zi\"|g.\u00138dYV$Wm\u001d\u0011\t\u0011\t\u0003!Q3A\u0005\u0002\r\u000b!\u0002]=uQ>tW\t_3d+\u0005\u0001\u0004\u0002C#\u0001\u0005#\u0005\u000b\u0011\u0002\u0019\u0002\u0017ALH\u000f[8o\u000bb,7\r\t\u0005\t\u000f\u0002\u0011)\u001a!C\u0001\u0007\u0006I\u0001/\u001f;i_:4VM\u001d\u0005\t\u0013\u0002\u0011\t\u0012)A\u0005a\u0005Q\u0001/\u001f;i_:4VM\u001d\u0011\t\u0011-\u0003!Q3A\u0005\u00021\u000bQB\u0019:pC\u0012\u001c\u0017m\u001d;WCJ\u001cX#A'\u0011\u0007%jd\nE\u0002P%Rk\u0011\u0001\u0015\u0006\u0003#\u001a\t\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\n\u0005M\u0003&!\u0003\"s_\u0006$7-Y:u!\t)f+D\u0001\u0003\u0013\t9&AA\bQsRDwN\u001c\"s_\u0006$7-Y:u\u0011!I\u0006A!E!\u0002\u0013i\u0015A\u00042s_\u0006$7-Y:u-\u0006\u00148\u000f\t\u0005\t7\u0002\u0011)\u001a!C\u00019\u0006Y\u0011mY2v[Vd\u0017\r^8s+\u0005i\u0006CA+_\u0013\ty&AA\nQsRDwN\\!dGVlW\u000f\\1u_J4&\u0007\u0003\u0005b\u0001\tE\t\u0015!\u0003^\u00031\t7mY;nk2\fGo\u001c:!\u0011\u0015\u0019\u0007\u0001\"\u0001e\u0003\u0019a\u0014N\\5u}QAQMZ4iS*\\G\u000e\u0005\u0002V\u0001!)!D\u0019a\u0001;!)aE\u0019a\u0001Q!)!H\u0019a\u0001y!)!I\u0019a\u0001a!)qI\u0019a\u0001a!)1J\u0019a\u0001\u001b\")1L\u0019a\u0001;\"9a\u000eAA\u0001\n\u0003y\u0017\u0001B2paf$\u0002\"\u001a9reN$XO\u001e\u0005\b55\u0004\n\u00111\u0001\u001e\u0011\u001d1S\u000e%AA\u0002!BqAO7\u0011\u0002\u0003\u0007A\bC\u0004C[B\u0005\t\u0019\u0001\u0019\t\u000f\u001dk\u0007\u0013!a\u0001a!91*\u001cI\u0001\u0002\u0004i\u0005bB.n!\u0003\u0005\r!\u0018\u0005\bq\u0002\t\n\u0011\"\u0001z\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012A\u001f\u0016\u0003;m\\\u0013\u0001 \t\u0004{\u0006\u0015Q\"\u0001@\u000b\u0007}\f\t!A\u0005v]\u000eDWmY6fI*\u0019\u00111A\b\u0002\u0015\u0005tgn\u001c;bi&|g.C\u0002\u0002\by\u0014\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011%\tY\u0001AI\u0001\n\u0003\ti!\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005=!F\u0001\u0015|\u0011%\t\u0019\u0002AI\u0001\n\u0003\t)\"\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\u0016\u0005\u0005]!F\u0001\u001f|\u0011%\tY\u0002AI\u0001\n\u0003\ti\"\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001b\u0016\u0005\u0005}!F\u0001\u0019|\u0011%\t\u0019\u0003AI\u0001\n\u0003\ti\"\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001b\t\u0013\u0005\u001d\u0002!%A\u0005\u0002\u0005%\u0012AD2paf$C-\u001a4bk2$HEN\u000b\u0003\u0003WQ#!T>\t\u0013\u0005=\u0002!%A\u0005\u0002\u0005E\u0012AD2paf$C-\u001a4bk2$HeN\u000b\u0003\u0003gQ#!X>\t\u0013\u0005]\u0002!!A\u0005B\u0005e\u0012!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070\u0006\u0002\u0002<A!\u0011QHA\"\u001b\t\tyDC\u0002\u0002B1\nA\u0001\\1oO&\u0019Q'a\u0010\t\u0013\u0005\u001d\u0003!!A\u0005\u0002\u0005%\u0013\u0001\u00049s_\u0012,8\r^!sSRLXCAA&!\rq\u0011QJ\u0005\u0004\u0003\u001fz!aA%oi\"I\u00111\u000b\u0001\u0002\u0002\u0013\u0005\u0011QK\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\t9&!\u0018\u0011\u00079\tI&C\u0002\u0002\\=\u00111!\u00118z\u0011)\ty&!\u0015\u0002\u0002\u0003\u0007\u00111J\u0001\u0004q\u0012\n\u0004\"CA2\u0001\u0005\u0005I\u0011IA3\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA4!\u0019\tI'a\u001c\u0002X5\u0011\u00111\u000e\u0006\u0004\u0003[z\u0011AC2pY2,7\r^5p]&!\u0011\u0011OA6\u0005!IE/\u001a:bi>\u0014\b\"CA;\u0001\u0005\u0005I\u0011AA<\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BA=\u0003\u007f\u00022ADA>\u0013\r\tih\u0004\u0002\b\u0005>|G.Z1o\u0011)\ty&a\u001d\u0002\u0002\u0003\u0007\u0011q\u000b\u0005\n\u0003\u0007\u0003\u0011\u0011!C!\u0003\u000b\u000b\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0003\u0003\u0017B\u0011\"!#\u0001\u0003\u0003%\t%a#\u0002\u0011Q|7\u000b\u001e:j]\u001e$\"!a\u000f\t\u0013\u0005=\u0005!!A\u0005B\u0005E\u0015AB3rk\u0006d7\u000f\u0006\u0003\u0002z\u0005M\u0005BCA0\u0003\u001b\u000b\t\u00111\u0001\u0002X\u001dQ\u0011q\u0013\u0002\u0002\u0002#\u0005a!!'\u0002\u001dAKH\u000f[8o\rVt7\r^5p]B\u0019Q+a'\u0007\u0013\u0005\u0011\u0011\u0011!E\u0001\r\u0005u5#BAN\u0003?3\u0002\u0003DAQ\u0003Ok\u0002\u0006\u0010\u00191\u001bv+WBAAR\u0015\r\t)kD\u0001\beVtG/[7f\u0013\u0011\tI+a)\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tw\u0007C\u0004d\u00037#\t!!,\u0015\u0005\u0005e\u0005BCAE\u00037\u000b\t\u0011\"\u0012\u0002\f\"Q\u00111WAN\u0003\u0003%\t)!.\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u001f\u0015\f9,!/\u0002<\u0006u\u0016qXAa\u0003\u0007DaAGAY\u0001\u0004i\u0002B\u0002\u0014\u00022\u0002\u0007\u0001\u0006\u0003\u0004;\u0003c\u0003\r\u0001\u0010\u0005\u0007\u0005\u0006E\u0006\u0019\u0001\u0019\t\r\u001d\u000b\t\f1\u00011\u0011\u0019Y\u0015\u0011\u0017a\u0001\u001b\"11,!-A\u0002uC!\"a2\u0002\u001c\u0006\u0005I\u0011QAe\u0003\u001d)h.\u00199qYf$B!a3\u0002XB)a\"!4\u0002R&\u0019\u0011qZ\b\u0003\r=\u0003H/[8o!)q\u00111[\u000f)yA\u0002T*X\u0005\u0004\u0003+|!A\u0002+va2,w\u0007C\u0005\u0002Z\u0006\u0015\u0017\u0011!a\u0001K\u0006\u0019\u0001\u0010\n\u0019\t\u0015\u0005u\u00171TA\u0001\n\u0013\ty.A\u0006sK\u0006$'+Z:pYZ,GCAAq!\u0011\ti$a9\n\t\u0005\u0015\u0018q\b\u0002\u0007\u001f\nTWm\u0019;"
)
public class PythonFunction implements Product, Serializable {
   private final byte[] command;
   private final Map<String, String> envVars;
   private final List<String> pythonIncludes;
   private final String pythonExec;
   private final String pythonVer;
   private final List<Broadcast<PythonBroadcast>> broadcastVars;
   private final PythonAccumulatorV2 accumulator;

   public static Option<Tuple7<byte[], Map<String, String>, List<String>, String, String, List<Broadcast<PythonBroadcast>>, PythonAccumulatorV2>> unapply(PythonFunction var0) {
      return PythonFunction$.MODULE$.unapply(var0);
   }

   public static PythonFunction apply(byte[] var0, Map<String, String> var1, List<String> var2, String var3, String var4, List<Broadcast<PythonBroadcast>> var5, PythonAccumulatorV2 var6) {
      return PythonFunction$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6);
   }

   public static Function1<Tuple7<byte[], Map<String, String>, List<String>, String, String, List<Broadcast<PythonBroadcast>>, PythonAccumulatorV2>, PythonFunction> tupled() {
      return PythonFunction$.MODULE$.tupled();
   }

   public static Function1<byte[], Function1<Map<String, String>, Function1<List<String>, Function1<String, Function1<String, Function1<List<Broadcast<PythonBroadcast>>, Function1<PythonAccumulatorV2, PythonFunction>>>>>>> curried() {
      return PythonFunction$.MODULE$.curried();
   }

   public byte[] command() {
      return this.command;
   }

   public Map<String, String> envVars() {
      return this.envVars;
   }

   public List<String> pythonIncludes() {
      return this.pythonIncludes;
   }

   public String pythonExec() {
      return this.pythonExec;
   }

   public String pythonVer() {
      return this.pythonVer;
   }

   public List<Broadcast<PythonBroadcast>> broadcastVars() {
      return this.broadcastVars;
   }

   public PythonAccumulatorV2 accumulator() {
      return this.accumulator;
   }

   public PythonFunction copy(byte[] command, Map<String, String> envVars, List<String> pythonIncludes, String pythonExec, String pythonVer, List<Broadcast<PythonBroadcast>> broadcastVars, PythonAccumulatorV2 accumulator) {
      return new PythonFunction(command, envVars, pythonIncludes, pythonExec, pythonVer, broadcastVars, accumulator);
   }

   public byte[] copy$default$1() {
      return this.command();
   }

   public Map<String, String> copy$default$2() {
      return this.envVars();
   }

   public List<String> copy$default$3() {
      return this.pythonIncludes();
   }

   public String copy$default$4() {
      return this.pythonExec();
   }

   public String copy$default$5() {
      return this.pythonVer();
   }

   public List<Broadcast<PythonBroadcast>> copy$default$6() {
      return this.broadcastVars();
   }

   public PythonAccumulatorV2 copy$default$7() {
      return this.accumulator();
   }

   public String productPrefix() {
      return "PythonFunction";
   }

   public int productArity() {
      return 7;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.command();
            break;
         case 1:
            var10000 = this.envVars();
            break;
         case 2:
            var10000 = this.pythonIncludes();
            break;
         case 3:
            var10000 = this.pythonExec();
            break;
         case 4:
            var10000 = this.pythonVer();
            break;
         case 5:
            var10000 = this.broadcastVars();
            break;
         case 6:
            var10000 = this.accumulator();
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
      return x$1 instanceof PythonFunction;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var17;
      if (this != x$1) {
         label100: {
            boolean var3;
            if (x$1 instanceof PythonFunction) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label82: {
                  PythonFunction var4 = (PythonFunction)x$1;
                  if (this.command() == var4.command()) {
                     label91: {
                        Map var10000 = this.envVars();
                        Map var5 = var4.envVars();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label91;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label91;
                        }

                        List var11 = this.pythonIncludes();
                        List var6 = var4.pythonIncludes();
                        if (var11 == null) {
                           if (var6 != null) {
                              break label91;
                           }
                        } else if (!var11.equals(var6)) {
                           break label91;
                        }

                        String var12 = this.pythonExec();
                        String var7 = var4.pythonExec();
                        if (var12 == null) {
                           if (var7 != null) {
                              break label91;
                           }
                        } else if (!var12.equals(var7)) {
                           break label91;
                        }

                        var12 = this.pythonVer();
                        String var8 = var4.pythonVer();
                        if (var12 == null) {
                           if (var8 != null) {
                              break label91;
                           }
                        } else if (!var12.equals(var8)) {
                           break label91;
                        }

                        List var14 = this.broadcastVars();
                        List var9 = var4.broadcastVars();
                        if (var14 == null) {
                           if (var9 != null) {
                              break label91;
                           }
                        } else if (!var14.equals(var9)) {
                           break label91;
                        }

                        PythonAccumulatorV2 var15 = this.accumulator();
                        PythonAccumulatorV2 var10 = var4.accumulator();
                        if (var15 == null) {
                           if (var10 != null) {
                              break label91;
                           }
                        } else if (!var15.equals(var10)) {
                           break label91;
                        }

                        if (var4.canEqual(this)) {
                           var17 = true;
                           break label82;
                        }
                     }
                  }

                  var17 = false;
               }

               if (var17) {
                  break label100;
               }
            }

            var17 = false;
            return var17;
         }
      }

      var17 = true;
      return var17;
   }

   public PythonFunction(byte[] command, Map<String, String> envVars, List<String> pythonIncludes, String pythonExec, String pythonVer, List<Broadcast<PythonBroadcast>> broadcastVars, PythonAccumulatorV2 accumulator) {
      this.command = command;
      this.envVars = envVars;
      this.pythonIncludes = pythonIncludes;
      this.pythonExec = pythonExec;
      this.pythonVer = pythonVer;
      this.broadcastVars = broadcastVars;
      this.accumulator = accumulator;
      Product$class.$init$(this);
   }
}
