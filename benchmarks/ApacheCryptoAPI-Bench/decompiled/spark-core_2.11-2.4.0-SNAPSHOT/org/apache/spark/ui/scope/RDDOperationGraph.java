package org.apache.spark.ui.scope;

import org.apache.spark.scheduler.StageInfo;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple4;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\rh!B\u0001\u0003\u0001\u001aa!!\u0005*E\t>\u0003XM]1uS>twI]1qQ*\u00111\u0001B\u0001\u0006g\u000e|\u0007/\u001a\u0006\u0003\u000b\u0019\t!!^5\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001cB\u0001A\u0007\u0014-A\u0011a\"E\u0007\u0002\u001f)\t\u0001#A\u0003tG\u0006d\u0017-\u0003\u0002\u0013\u001f\t1\u0011I\\=SK\u001a\u0004\"A\u0004\u000b\n\u0005Uy!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001d]I!\u0001G\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011i\u0001!Q3A\u0005\u0002q\tQ!\u001a3hKN\u001c\u0001!F\u0001\u001e!\rqb%\u000b\b\u0003?\u0011r!\u0001I\u0012\u000e\u0003\u0005R!AI\u000e\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0001\u0012BA\u0013\u0010\u0003\u001d\u0001\u0018mY6bO\u0016L!a\n\u0015\u0003\u0007M+\u0017O\u0003\u0002&\u001fA\u0011!fK\u0007\u0002\u0005%\u0011AF\u0001\u0002\u0011%\u0012#u\n]3sCRLwN\\#eO\u0016D\u0001B\f\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0007K\u0012<Wm\u001d\u0011\t\u0011A\u0002!Q3A\u0005\u0002q\tQb\\;uO>LgnZ#eO\u0016\u001c\b\u0002\u0003\u001a\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u001d=,HoZ8j]\u001e,EmZ3tA!AA\u0007\u0001BK\u0002\u0013\u0005A$A\u0007j]\u000e|W.\u001b8h\u000b\u0012<Wm\u001d\u0005\tm\u0001\u0011\t\u0012)A\u0005;\u0005q\u0011N\\2p[&tw-\u00123hKN\u0004\u0003\u0002\u0003\u001d\u0001\u0005+\u0007I\u0011A\u001d\u0002\u0017I|w\u000e^\"mkN$XM]\u000b\u0002uA\u0011!fO\u0005\u0003y\t\u00111C\u0015#E\u001fB,'/\u0019;j_:\u001cE.^:uKJD\u0001B\u0010\u0001\u0003\u0012\u0003\u0006IAO\u0001\re>|Go\u00117vgR,'\u000f\t\u0005\u0006\u0001\u0002!\t!Q\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000b\t\u001bE)\u0012$\u0011\u0005)\u0002\u0001\"\u0002\u000e@\u0001\u0004i\u0002\"\u0002\u0019@\u0001\u0004i\u0002\"\u0002\u001b@\u0001\u0004i\u0002\"\u0002\u001d@\u0001\u0004Q\u0004b\u0002%\u0001\u0003\u0003%\t!S\u0001\u0005G>\u0004\u0018\u0010F\u0003C\u0015.cU\nC\u0004\u001b\u000fB\u0005\t\u0019A\u000f\t\u000fA:\u0005\u0013!a\u0001;!9Ag\u0012I\u0001\u0002\u0004i\u0002b\u0002\u001dH!\u0003\u0005\rA\u000f\u0005\b\u001f\u0002\t\n\u0011\"\u0001Q\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012!\u0015\u0016\u0003;I[\u0013a\u0015\t\u0003)fk\u0011!\u0016\u0006\u0003-^\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005a{\u0011AC1o]>$\u0018\r^5p]&\u0011!,\u0016\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002/\u0001#\u0003%\t\u0001U\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133\u0011\u001dq\u0006!%A\u0005\u0002A\u000babY8qs\u0012\"WMZ1vYR$3\u0007C\u0004a\u0001E\u0005I\u0011A1\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%iU\t!M\u000b\u0002;%\"9A\rAA\u0001\n\u0003*\u0017!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001g!\t9G.D\u0001i\u0015\tI'.\u0001\u0003mC:<'\"A6\u0002\t)\fg/Y\u0005\u0003[\"\u0014aa\u0015;sS:<\u0007bB8\u0001\u0003\u0003%\t\u0001]\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002cB\u0011aB]\u0005\u0003g>\u00111!\u00138u\u0011\u001d)\b!!A\u0005\u0002Y\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002xuB\u0011a\u0002_\u0005\u0003s>\u00111!\u00118z\u0011\u001dYH/!AA\u0002E\f1\u0001\u001f\u00132\u0011\u001di\b!!A\u0005By\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002\u007fB)\u0011\u0011AA\u0004o6\u0011\u00111\u0001\u0006\u0004\u0003\u000by\u0011AC2pY2,7\r^5p]&!\u0011\u0011BA\u0002\u0005!IE/\u001a:bi>\u0014\b\"CA\u0007\u0001\u0005\u0005I\u0011AA\b\u0003!\u0019\u0017M\\#rk\u0006dG\u0003BA\t\u0003/\u00012ADA\n\u0013\r\t)b\u0004\u0002\b\u0005>|G.Z1o\u0011!Y\u00181BA\u0001\u0002\u00049\b\"CA\u000e\u0001\u0005\u0005I\u0011IA\u000f\u0003!A\u0017m\u001d5D_\u0012,G#A9\t\u0013\u0005\u0005\u0002!!A\u0005B\u0005\r\u0012\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003\u0019D\u0011\"a\n\u0001\u0003\u0003%\t%!\u000b\u0002\r\u0015\fX/\u00197t)\u0011\t\t\"a\u000b\t\u0011m\f)#!AA\u0002]<\u0001\"a\f\u0003\u0011\u00031\u0011\u0011G\u0001\u0012%\u0012#u\n]3sCRLwN\\$sCBD\u0007c\u0001\u0016\u00024\u00199\u0011A\u0001E\u0001\r\u0005U2CBA\u001a\u001b\u0005]b\u0003\u0005\u0003\u0002:\u0005}RBAA\u001e\u0015\r\tiDB\u0001\tS:$XM\u001d8bY&!\u0011\u0011IA\u001e\u0005\u001daunZ4j]\u001eDq\u0001QA\u001a\t\u0003\t)\u0005\u0006\u0002\u00022!I\u0011\u0011JA\u001a\u0005\u0004%\t!Z\u0001\u0015'R\u000bu)R0D\u0019V\u001bF+\u0012*`!J+e)\u0013-\t\u0011\u00055\u00131\u0007Q\u0001\n\u0019\fQc\u0015+B\u000f\u0016{6\tT+T)\u0016\u0013v\f\u0015*F\r&C\u0006\u0005\u0003\u0005\u0002R\u0005MB\u0011AA*\u0003Ii\u0017m[3Pa\u0016\u0014\u0018\r^5p]\u001e\u0013\u0018\r\u001d5\u0015\u000b\t\u000b)&!\u001a\t\u0011\u0005]\u0013q\na\u0001\u00033\nQa\u001d;bO\u0016\u0004B!a\u0017\u0002b5\u0011\u0011Q\f\u0006\u0004\u0003?2\u0011!C:dQ\u0016$W\u000f\\3s\u0013\u0011\t\u0019'!\u0018\u0003\u0013M#\u0018mZ3J]\u001a|\u0007bBA4\u0003\u001f\u0002\r!]\u0001\u000ee\u0016$\u0018-\u001b8fI:{G-Z:\t\u0011\u0005-\u00141\u0007C\u0001\u0003[\n1\"\\1lK\u0012{GOR5mKR!\u0011qNA>!\u0011\t\t(a\u001e\u000f\u00079\t\u0019(C\u0002\u0002v=\ta\u0001\u0015:fI\u00164\u0017bA7\u0002z)\u0019\u0011QO\b\t\u000f\u0005u\u0014\u0011\u000ea\u0001\u0005\u0006)qM]1qQ\"A\u0011\u0011QA\u001a\t\u0013\t\u0019)A\u0006nC.,Gi\u001c;O_\u0012,G\u0003BA8\u0003\u000bC\u0001\"a\"\u0002\u0000\u0001\u0007\u0011\u0011R\u0001\u0005]>$W\rE\u0002+\u0003\u0017K1!!$\u0003\u0005A\u0011F\tR(qKJ\fG/[8o\u001d>$W\r\u0003\u0005\u0002\u0012\u0006MB\u0011BAJ\u0003=i\u0017m[3E_R\u001cVOY4sCBDG\u0003CAK\u00037\u000bY+a,\u0011\u00079\t9*C\u0002\u0002\u001a>\u0011A!\u00168ji\"A\u0011QTAH\u0001\u0004\ty*\u0001\u0005tk\n<'/\u00199i!\u0011\t\t+a*\u000e\u0005\u0005\r&\u0002BAS\u0003\u0007\tq!\\;uC\ndW-\u0003\u0003\u0002*\u0006\r&!D*ue&twMQ;jY\u0012,'\u000fC\u0004\u0002.\u0006=\u0005\u0019\u0001\u001e\u0002\u000f\rdWo\u001d;fe\"A\u0011\u0011WAH\u0001\u0004\ty'\u0001\u0004j]\u0012,g\u000e\u001e\u0005\u000b\u0003k\u000b\u0019$!A\u0005\u0002\u0006]\u0016!B1qa2LH#\u0003\"\u0002:\u0006m\u0016QXA`\u0011\u0019Q\u00121\u0017a\u0001;!1\u0001'a-A\u0002uAa\u0001NAZ\u0001\u0004i\u0002B\u0002\u001d\u00024\u0002\u0007!\b\u0003\u0006\u0002D\u0006M\u0012\u0011!CA\u0003\u000b\fq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002H\u0006M\u0007#\u0002\b\u0002J\u00065\u0017bAAf\u001f\t1q\n\u001d;j_:\u0004rADAh;ui\"(C\u0002\u0002R>\u0011a\u0001V;qY\u0016$\u0004\"CAk\u0003\u0003\f\t\u00111\u0001C\u0003\rAH\u0005\r\u0005\u000b\u00033\f\u0019$!A\u0005\n\u0005m\u0017a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!8\u0011\u0007\u001d\fy.C\u0002\u0002b\"\u0014aa\u00142kK\u000e$\b"
)
public class RDDOperationGraph implements Product, Serializable {
   private final Seq<RDDOperationEdge> edges;
   private final Seq<RDDOperationEdge> outgoingEdges;
   private final Seq<RDDOperationEdge> incomingEdges;
   private final RDDOperationCluster rootCluster;

   public static boolean initializeLogIfNecessary$default$2() {
      return RDDOperationGraph$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return RDDOperationGraph$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      RDDOperationGraph$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return RDDOperationGraph$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      RDDOperationGraph$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      RDDOperationGraph$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      RDDOperationGraph$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      RDDOperationGraph$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      RDDOperationGraph$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      RDDOperationGraph$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      RDDOperationGraph$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      RDDOperationGraph$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      RDDOperationGraph$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      RDDOperationGraph$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return RDDOperationGraph$.MODULE$.log();
   }

   public static String logName() {
      return RDDOperationGraph$.MODULE$.logName();
   }

   public static Option<Tuple4<Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, Seq<RDDOperationEdge>, RDDOperationCluster>> unapply(RDDOperationGraph var0) {
      return RDDOperationGraph$.MODULE$.unapply(var0);
   }

   public static RDDOperationGraph apply(Seq<RDDOperationEdge> var0, Seq<RDDOperationEdge> var1, Seq<RDDOperationEdge> var2, RDDOperationCluster var3) {
      return RDDOperationGraph$.MODULE$.apply(var0, var1, var2, var3);
   }

   public static String makeDotFile(RDDOperationGraph var0) {
      return RDDOperationGraph$.MODULE$.makeDotFile(var0);
   }

   public static RDDOperationGraph makeOperationGraph(StageInfo var0, int var1) {
      return RDDOperationGraph$.MODULE$.makeOperationGraph(var0, var1);
   }

   public static String STAGE_CLUSTER_PREFIX() {
      return RDDOperationGraph$.MODULE$.STAGE_CLUSTER_PREFIX();
   }

   public Seq<RDDOperationEdge> edges() {
      return this.edges;
   }

   public Seq<RDDOperationEdge> outgoingEdges() {
      return this.outgoingEdges;
   }

   public Seq<RDDOperationEdge> incomingEdges() {
      return this.incomingEdges;
   }

   public RDDOperationCluster rootCluster() {
      return this.rootCluster;
   }

   public RDDOperationGraph copy(Seq<RDDOperationEdge> edges, Seq<RDDOperationEdge> outgoingEdges, Seq<RDDOperationEdge> incomingEdges, RDDOperationCluster rootCluster) {
      return new RDDOperationGraph(edges, outgoingEdges, incomingEdges, rootCluster);
   }

   public Seq<RDDOperationEdge> copy$default$1() {
      return this.edges();
   }

   public Seq<RDDOperationEdge> copy$default$2() {
      return this.outgoingEdges();
   }

   public Seq<RDDOperationEdge> copy$default$3() {
      return this.incomingEdges();
   }

   public RDDOperationCluster copy$default$4() {
      return this.rootCluster();
   }

   public String productPrefix() {
      return "RDDOperationGraph";
   }

   public int productArity() {
      return 4;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = this.edges();
            break;
         case 1:
            var10000 = this.outgoingEdges();
            break;
         case 2:
            var10000 = this.incomingEdges();
            break;
         case 3:
            var10000 = this.rootCluster();
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
      return x$1 instanceof RDDOperationGraph;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var13;
      if (this != x$1) {
         label80: {
            boolean var3;
            if (x$1 instanceof RDDOperationGraph) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label62: {
                  label71: {
                     RDDOperationGraph var4 = (RDDOperationGraph)x$1;
                     Seq var10000 = this.edges();
                     Seq var5 = var4.edges();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label71;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label71;
                     }

                     var10000 = this.outgoingEdges();
                     Seq var6 = var4.outgoingEdges();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label71;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label71;
                     }

                     var10000 = this.incomingEdges();
                     Seq var7 = var4.incomingEdges();
                     if (var10000 == null) {
                        if (var7 != null) {
                           break label71;
                        }
                     } else if (!var10000.equals(var7)) {
                        break label71;
                     }

                     RDDOperationCluster var11 = this.rootCluster();
                     RDDOperationCluster var8 = var4.rootCluster();
                     if (var11 == null) {
                        if (var8 != null) {
                           break label71;
                        }
                     } else if (!var11.equals(var8)) {
                        break label71;
                     }

                     if (var4.canEqual(this)) {
                        var13 = true;
                        break label62;
                     }
                  }

                  var13 = false;
               }

               if (var13) {
                  break label80;
               }
            }

            var13 = false;
            return var13;
         }
      }

      var13 = true;
      return var13;
   }

   public RDDOperationGraph(Seq<RDDOperationEdge> edges, Seq<RDDOperationEdge> outgoingEdges, Seq<RDDOperationEdge> incomingEdges, RDDOperationCluster rootCluster) {
      this.edges = edges;
      this.outgoingEdges = outgoingEdges;
      this.incomingEdges = incomingEdges;
      this.rootCluster = rootCluster;
      Product$class.$init$(this);
   }
}
