package org.apache.spark.deploy;

import org.apache.log4j.Level;
import org.apache.spark.util.IntParam$;
import org.apache.spark.util.MemoryParam$;
import org.apache.spark.util.Utils$;
import scala.Option;
import scala.Predef$;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ee!B\u0001\u0003\u0001\tQ!aD\"mS\u0016tG/\u0011:hk6,g\u000e^:\u000b\u0005\r!\u0011A\u00023fa2|\u0017P\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\u0005\t%\u0001\u0011\t\u0011)A\u0005)\u0005!\u0011M]4t\u0007\u0001\u00012\u0001D\u000b\u0018\u0013\t1RBA\u0003BeJ\f\u0017\u0010\u0005\u0002\u001979\u0011A\"G\u0005\u000355\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000f\u001e\u0005\u0019\u0019FO]5oO*\u0011!$\u0004\u0005\u0006?\u0001!\t\u0001I\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u0005\u001a\u0003C\u0001\u0012\u0001\u001b\u0005\u0011\u0001\"\u0002\n\u001f\u0001\u0004!\u0002bB\u0013\u0001\u0001\u0004%\tAJ\u0001\u0004G6$W#A\f\t\u000f!\u0002\u0001\u0019!C\u0001S\u000591-\u001c3`I\u0015\fHC\u0001\u0016.!\ta1&\u0003\u0002-\u001b\t!QK\\5u\u0011\u001dqs%!AA\u0002]\t1\u0001\u001f\u00132\u0011\u0019\u0001\u0004\u0001)Q\u0005/\u0005!1-\u001c3!\u0011\u001d\u0011\u0004\u00011A\u0005\u0002M\n\u0001\u0002\\8h\u0019\u00164X\r\\\u000b\u0002iA\u0011Q\u0007O\u0007\u0002m)\u0011qGB\u0001\u0006Y><GG[\u0005\u0003sY\u0012Q\u0001T3wK2Dqa\u000f\u0001A\u0002\u0013\u0005A(\u0001\u0007m_\u001edUM^3m?\u0012*\u0017\u000f\u0006\u0002+{!9aFOA\u0001\u0002\u0004!\u0004BB \u0001A\u0003&A'A\u0005m_\u001edUM^3mA!9\u0011\t\u0001a\u0001\n\u0003\u0011\u0015aB7bgR,'o]\u000b\u0002)!9A\t\u0001a\u0001\n\u0003)\u0015aC7bgR,'o]0%KF$\"A\u000b$\t\u000f9\u001a\u0015\u0011!a\u0001)!1\u0001\n\u0001Q!\nQ\t\u0001\"\\1ti\u0016\u00148\u000f\t\u0005\b\u0015\u0002\u0001\r\u0011\"\u0001'\u0003\u0019Q\u0017M]+sY\"9A\n\u0001a\u0001\n\u0003i\u0015A\u00036beV\u0013Hn\u0018\u0013fcR\u0011!F\u0014\u0005\b]-\u000b\t\u00111\u0001\u0018\u0011\u0019\u0001\u0006\u0001)Q\u0005/\u00059!.\u0019:Ve2\u0004\u0003b\u0002*\u0001\u0001\u0004%\tAJ\u0001\n[\u0006Lgn\u00117bgNDq\u0001\u0016\u0001A\u0002\u0013\u0005Q+A\u0007nC&t7\t\\1tg~#S-\u001d\u000b\u0003UYCqAL*\u0002\u0002\u0003\u0007q\u0003\u0003\u0004Y\u0001\u0001\u0006KaF\u0001\u000b[\u0006Lgn\u00117bgN\u0004\u0003b\u0002.\u0001\u0001\u0004%\taW\u0001\ngV\u0004XM\u001d<jg\u0016,\u0012\u0001\u0018\t\u0003\u0019uK!AX\u0007\u0003\u000f\t{w\u000e\\3b]\"9\u0001\r\u0001a\u0001\n\u0003\t\u0017!D:va\u0016\u0014h/[:f?\u0012*\u0017\u000f\u0006\u0002+E\"9afXA\u0001\u0002\u0004a\u0006B\u00023\u0001A\u0003&A,\u0001\u0006tkB,'O^5tK\u0002BqA\u001a\u0001A\u0002\u0013\u0005q-\u0001\u0004nK6|'/_\u000b\u0002QB\u0011A\"[\u0005\u0003U6\u00111!\u00138u\u0011\u001da\u0007\u00011A\u0005\u00025\f!\"\\3n_JLx\fJ3r)\tQc\u000eC\u0004/W\u0006\u0005\t\u0019\u00015\t\rA\u0004\u0001\u0015)\u0003i\u0003\u001diW-\\8ss\u0002BqA\u001d\u0001A\u0002\u0013\u0005q-A\u0003d_J,7\u000fC\u0004u\u0001\u0001\u0007I\u0011A;\u0002\u0013\r|'/Z:`I\u0015\fHC\u0001\u0016w\u0011\u001dq3/!AA\u0002!Da\u0001\u001f\u0001!B\u0013A\u0017AB2pe\u0016\u001c\b\u0005C\u0004{\u0001\u0001\u0007I\u0011B>\u0002\u001d}#'/\u001b<fe>\u0003H/[8ogV\tA\u0010\u0005\u0003~\u0003\u000b9R\"\u0001@\u000b\u0007}\f\t!A\u0004nkR\f'\r\\3\u000b\u0007\u0005\rQ\"\u0001\u0006d_2dWm\u0019;j_:L1!a\u0002\u007f\u0005)a\u0015n\u001d;Ck\u001a4WM\u001d\u0005\n\u0003\u0017\u0001\u0001\u0019!C\u0005\u0003\u001b\t!c\u00183sSZ,'o\u00149uS>t7o\u0018\u0013fcR\u0019!&a\u0004\t\u00119\nI!!AA\u0002qDq!a\u0005\u0001A\u0003&A0A\b`IJLg/\u001a:PaRLwN\\:!\u0011\u001d\t9\u0002\u0001C\u0001\u00033\tQ\u0002\u001a:jm\u0016\u0014x\n\u001d;j_:\u001cXCAA\u000e!\u0015\ti\"!\f\u0018\u001d\u0011\ty\"!\u000b\u000f\t\u0005\u0005\u0012qE\u0007\u0003\u0003GQ1!!\n\u0014\u0003\u0019a$o\\8u}%\ta\"C\u0002\u0002,5\tq\u0001]1dW\u0006<W-\u0003\u0003\u00020\u0005E\"aA*fc*\u0019\u00111F\u0007\t\u0011\u0005U\u0002\u00011A\u0005\u0002\u0019\n\u0001\u0002\u001a:jm\u0016\u0014\u0018\n\u001a\u0005\n\u0003s\u0001\u0001\u0019!C\u0001\u0003w\tA\u0002\u001a:jm\u0016\u0014\u0018\nZ0%KF$2AKA\u001f\u0011!q\u0013qGA\u0001\u0002\u00049\u0002bBA!\u0001\u0001\u0006KaF\u0001\nIJLg/\u001a:JI\u0002Bq!!\u0012\u0001\t\u0013\t9%A\u0003qCJ\u001cX\rF\u0002+\u0003\u0013BqAEA\"\u0001\u0004\tY\u0005E\u0003\u0002\u001e\u00055s#\u0003\u0003\u0002P\u0005E\"\u0001\u0002'jgRDC!a\u0011\u0002TA!\u0011QKA.\u001b\t\t9FC\u0002\u0002Z5\t!\"\u00198o_R\fG/[8o\u0013\u0011\ti&a\u0016\u0003\u000fQ\f\u0017\u000e\u001c:fG\"9\u0011\u0011\r\u0001\u0005\n\u0005\r\u0014!\u00059sS:$Xk]1hK\u0006sG-\u0012=jiR\u0019!&!\u001a\t\u000f\u0005\u001d\u0014q\fa\u0001Q\u0006AQ\r_5u\u0007>$Wm\u0002\u0005\u0002l\tA\tAAA7\u0003=\u0019E.[3oi\u0006\u0013x-^7f]R\u001c\bc\u0001\u0012\u0002p\u00199\u0011A\u0001E\u0001\u0005\u0005E4cAA8\u0017!9q$a\u001c\u0005\u0002\u0005UDCAA7\u0011%\tI(a\u001cC\u0002\u0013\u0005q-A\u0007E\u000b\u001a\u000bU\u000b\u0014+`\u0007>\u0013Vi\u0015\u0005\t\u0003{\ny\u0007)A\u0005Q\u0006qA)\u0012$B+2#vlQ(S\u000bN\u0003\u0003\"CAA\u0003_\u0012\r\u0011\"\u0001h\u00039!UIR!V\u0019R{V*R'P%fC\u0001\"!\"\u0002p\u0001\u0006I\u0001[\u0001\u0010\t\u00163\u0015)\u0016'U?6+Uj\u0014*ZA!I\u0011\u0011RA8\u0005\u0004%\taW\u0001\u0012\t\u00163\u0015)\u0016'U?N+\u0006+\u0012*W\u0013N+\u0005\u0002CAG\u0003_\u0002\u000b\u0011\u0002/\u0002%\u0011+e)Q+M)~\u001bV\u000bU#S-&\u001bV\t\t\u0005\t\u0003#\u000by\u0007\"\u0001\u0002\u0014\u0006i\u0011n\u001d,bY&$'*\u0019:Ve2$2\u0001XAK\u0011\u001d\t9*a$A\u0002]\t\u0011a\u001d"
)
public class ClientArguments {
   private String cmd = "";
   private Level logLevel;
   private String[] masters;
   private String jarUrl;
   private String mainClass;
   private boolean supervise;
   private int memory;
   private int cores;
   private ListBuffer<String> _driverOptions;
   private String driverId;

   public static boolean isValidJarUrl(String var0) {
      return ClientArguments$.MODULE$.isValidJarUrl(var0);
   }

   public static boolean DEFAULT_SUPERVISE() {
      return ClientArguments$.MODULE$.DEFAULT_SUPERVISE();
   }

   public static int DEFAULT_MEMORY() {
      return ClientArguments$.MODULE$.DEFAULT_MEMORY();
   }

   public static int DEFAULT_CORES() {
      return ClientArguments$.MODULE$.DEFAULT_CORES();
   }

   public String cmd() {
      return this.cmd;
   }

   public void cmd_$eq(String x$1) {
      this.cmd = x$1;
   }

   public Level logLevel() {
      return this.logLevel;
   }

   public void logLevel_$eq(Level x$1) {
      this.logLevel = x$1;
   }

   public String[] masters() {
      return this.masters;
   }

   public void masters_$eq(String[] x$1) {
      this.masters = x$1;
   }

   public String jarUrl() {
      return this.jarUrl;
   }

   public void jarUrl_$eq(String x$1) {
      this.jarUrl = x$1;
   }

   public String mainClass() {
      return this.mainClass;
   }

   public void mainClass_$eq(String x$1) {
      this.mainClass = x$1;
   }

   public boolean supervise() {
      return this.supervise;
   }

   public void supervise_$eq(boolean x$1) {
      this.supervise = x$1;
   }

   public int memory() {
      return this.memory;
   }

   public void memory_$eq(int x$1) {
      this.memory = x$1;
   }

   public int cores() {
      return this.cores;
   }

   public void cores_$eq(int x$1) {
      this.cores = x$1;
   }

   private ListBuffer<String> _driverOptions() {
      return this._driverOptions;
   }

   private void _driverOptions_$eq(ListBuffer<String> x$1) {
      this._driverOptions = x$1;
   }

   public Seq<String> driverOptions() {
      return this._driverOptions().toSeq();
   }

   public String driverId() {
      return this.driverId;
   }

   public void driverId_$eq(String x$1) {
      this.driverId = x$1;
   }

   private void parse(List<String> args) {
      while(true) {
         boolean var3 = false;
         $colon$colon var4 = null;
         if (args instanceof $colon$colon) {
            var3 = true;
            var4 = ($colon$colon)args;
            String var6 = (String)var4.head();
            List var7 = var4.tl$1();
            boolean var8;
            if ("--cores".equals(var6)) {
               var8 = true;
            } else if ("-c".equals(var6)) {
               var8 = true;
            } else {
               var8 = false;
            }

            if (var8 && var7 instanceof $colon$colon) {
               $colon$colon var9 = ($colon$colon)var7;
               String var10 = (String)var9.head();
               List tail = var9.tl$1();
               Option var12 = IntParam$.MODULE$.unapply(var10);
               if (!var12.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var12.get());
                  this.cores_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var15 = (String)var4.head();
            List var16 = var4.tl$1();
            boolean var17;
            if ("--memory".equals(var15)) {
               var17 = true;
            } else if ("-m".equals(var15)) {
               var17 = true;
            } else {
               var17 = false;
            }

            if (var17 && var16 instanceof $colon$colon) {
               $colon$colon var18 = ($colon$colon)var16;
               String var19 = (String)var18.head();
               List tail = var18.tl$1();
               Option var21 = MemoryParam$.MODULE$.unapply(var19);
               if (!var21.isEmpty()) {
                  int value = BoxesRunTime.unboxToInt(var21.get());
                  this.memory_$eq(value);
                  args = tail;
                  continue;
               }
            }
         }

         if (var3) {
            String var23 = (String)var4.head();
            List tail = var4.tl$1();
            boolean var25;
            if ("--supervise".equals(var23)) {
               var25 = true;
            } else if ("-s".equals(var23)) {
               var25 = true;
            } else {
               var25 = false;
            }

            if (var25) {
               this.supervise_$eq(true);
               args = tail;
               continue;
            }
         }

         label116: {
            if (var3) {
               String var26 = (String)var4.head();
               boolean var27;
               if ("--help".equals(var26)) {
                  var27 = true;
               } else if ("-h".equals(var26)) {
                  var27 = true;
               } else {
                  var27 = false;
               }

               if (var27) {
                  this.printUsageAndExit(0);
                  BoxedUnit var51 = BoxedUnit.UNIT;
                  break label116;
               }
            }

            if (var3) {
               String var28 = (String)var4.head();
               List tail = var4.tl$1();
               boolean var30;
               if ("--verbose".equals(var28)) {
                  var30 = true;
               } else if ("-v".equals(var28)) {
                  var30 = true;
               } else {
                  var30 = false;
               }

               if (var30) {
                  this.logLevel_$eq(Level.INFO);
                  args = tail;
                  continue;
               }
            }

            if (var3) {
               String var31 = (String)var4.head();
               List var32 = var4.tl$1();
               if ("launch".equals(var31) && var32 instanceof $colon$colon) {
                  $colon$colon var33 = ($colon$colon)var32;
                  String _master = (String)var33.head();
                  List var35 = var33.tl$1();
                  if (var35 instanceof $colon$colon) {
                     $colon$colon var36 = ($colon$colon)var35;
                     String _jarUrl = (String)var36.head();
                     List var38 = var36.tl$1();
                     if (var38 instanceof $colon$colon) {
                        $colon$colon var39 = ($colon$colon)var38;
                        String _mainClass = (String)var39.head();
                        List tail = var39.tl$1();
                        this.cmd_$eq("launch");
                        if (!ClientArguments$.MODULE$.isValidJarUrl(_jarUrl)) {
                           Predef$.MODULE$.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Jar url '", "' is not in valid format."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{_jarUrl})));
                           Predef$.MODULE$.println((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Must be a jar file path in URL format "})))).s(Nil$.MODULE$)).append((Object)"(e.g. hdfs://host:port/XX.jar, file:///XX.jar)").toString());
                           this.printUsageAndExit(-1);
                        }

                        this.jarUrl_$eq(_jarUrl);
                        this.masters_$eq(Utils$.MODULE$.parseStandaloneMasterUrls(_master));
                        this.mainClass_$eq(_mainClass);
                        this._driverOptions().$plus$plus$eq(tail);
                        BoxedUnit var50 = BoxedUnit.UNIT;
                        break label116;
                     }
                  }
               }
            }

            if (var3) {
               String var42 = (String)var4.head();
               List var43 = var4.tl$1();
               if ("kill".equals(var42) && var43 instanceof $colon$colon) {
                  $colon$colon var44 = ($colon$colon)var43;
                  String _master = (String)var44.head();
                  List var46 = var44.tl$1();
                  if (var46 instanceof $colon$colon) {
                     $colon$colon var47 = ($colon$colon)var46;
                     String _driverId = (String)var47.head();
                     this.cmd_$eq("kill");
                     this.masters_$eq(Utils$.MODULE$.parseStandaloneMasterUrls(_master));
                     this.driverId_$eq(_driverId);
                     BoxedUnit var49 = BoxedUnit.UNIT;
                     break label116;
                  }
               }
            }

            this.printUsageAndExit(1);
            BoxedUnit var14 = BoxedUnit.UNIT;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
         return;
      }
   }

   private void printUsageAndExit(int exitCode) {
      String usage = (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n      |Usage: DriverClient [options] launch <active-master> <jar-url> <main-class> [driver options]\n      |Usage: DriverClient kill <active-master> <driver-id>\n      |\n      |Options:\n      |   -c CORES, --cores CORES        Number of cores to request (default: ", ")\n      |   -m MEMORY, --memory MEMORY     Megabytes of memory to request (default: ", ")\n      |   -s, --supervise                Whether to restart the driver on failure\n      |                                  (default: ", ")\n      |   -v, --verbose                  Print more debugging output\n     "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(ClientArguments$.MODULE$.DEFAULT_CORES()), BoxesRunTime.boxToInteger(ClientArguments$.MODULE$.DEFAULT_MEMORY()), BoxesRunTime.boxToBoolean(ClientArguments$.MODULE$.DEFAULT_SUPERVISE())}))))).stripMargin();
      System.err.println(usage);
      System.exit(exitCode);
   }

   public ClientArguments(String[] args) {
      this.logLevel = Level.WARN;
      this.masters = null;
      this.jarUrl = "";
      this.mainClass = "";
      this.supervise = ClientArguments$.MODULE$.DEFAULT_SUPERVISE();
      this.memory = ClientArguments$.MODULE$.DEFAULT_MEMORY();
      this.cores = ClientArguments$.MODULE$.DEFAULT_CORES();
      this._driverOptions = (ListBuffer)ListBuffer$.MODULE$.apply(Nil$.MODULE$);
      this.driverId = "";
      this.parse(Predef$.MODULE$.refArrayOps((Object[])args).toList());
   }
}
