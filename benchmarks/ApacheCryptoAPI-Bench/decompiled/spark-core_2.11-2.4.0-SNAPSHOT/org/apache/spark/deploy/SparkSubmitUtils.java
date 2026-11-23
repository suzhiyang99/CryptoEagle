package org.apache.spark.deploy;

import java.io.File;
import java.io.PrintStream;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.resolver.ChainResolver;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\teuAB\u0001\u0003\u0011\u0003!!\"\u0001\tTa\u0006\u00148nU;c[&$X\u000b^5mg*\u00111\u0001B\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!\u0001E*qCJ\\7+\u001e2nSR,F/\u001b7t'\taq\u0002\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\u0005\u0006-1!\t\u0001G\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\t!\u0002C\u0004\u001b\u0019\u0001\u0007I\u0011A\u000e\u0002\u0017A\u0014\u0018N\u001c;TiJ,\u0017-\\\u000b\u00029A\u0011QDI\u0007\u0002=)\u0011q\u0004I\u0001\u0003S>T\u0011!I\u0001\u0005U\u00064\u0018-\u0003\u0002$=\tY\u0001K]5oiN#(/Z1n\u0011\u001d)C\u00021A\u0005\u0002\u0019\nq\u0002\u001d:j]R\u001cFO]3b[~#S-\u001d\u000b\u0003O)\u0002\"\u0001\u0005\u0015\n\u0005%\n\"\u0001B+oSRDqa\u000b\u0013\u0002\u0002\u0003\u0007A$A\u0002yIEBa!\f\u0007!B\u0013a\u0012\u0001\u00049sS:$8\u000b\u001e:fC6\u0004\u0003bB\u0018\r\u0005\u0004%\t\u0001M\u0001\u0015\u0013ZKv\fR#G\u0003VcEkX#Y\u00072+F)R*\u0016\u0003E\u00022AM\u001b8\u001b\u0005\u0019$B\u0001\u001b\u0012\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003mM\u00121aU3r!\tA4(D\u0001:\u0015\tQ\u0004%\u0001\u0003mC:<\u0017B\u0001\u001f:\u0005\u0019\u0019FO]5oO\"1a\b\u0004Q\u0001\nE\nQ#\u0013,Z?\u0012+e)Q+M)~+\u0005l\u0011'V\t\u0016\u001b\u0006EB\u0003A\u0019\u0001\u0013\u0011IA\bNCZ,gnQ8pe\u0012Lg.\u0019;f'\u0011ytBQ#\u0011\u0005A\u0019\u0015B\u0001#\u0012\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001\u0005$\n\u0005\u001d\u000b\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C%@\u0005+\u0007I\u0011\u0001&\u0002\u000f\u001d\u0014x.\u001e9JIV\t1\n\u0005\u0002M\u001f:\u0011\u0001#T\u0005\u0003\u001dF\ta\u0001\u0015:fI\u00164\u0017B\u0001\u001fQ\u0015\tq\u0015\u0003\u0003\u0005S\u007f\tE\t\u0015!\u0003L\u0003!9'o\\;q\u0013\u0012\u0004\u0003\u0002\u0003+@\u0005+\u0007I\u0011\u0001&\u0002\u0015\u0005\u0014H/\u001b4bGRLE\r\u0003\u0005W\u007f\tE\t\u0015!\u0003L\u0003-\t'\u000f^5gC\u000e$\u0018\n\u001a\u0011\t\u0011a{$Q3A\u0005\u0002)\u000bqA^3sg&|g\u000e\u0003\u0005[\u007f\tE\t\u0015!\u0003L\u0003!1XM]:j_:\u0004\u0003\"\u0002\f@\t\u0003aF\u0003B/`A\u0006\u0004\"AX \u000e\u00031AQ!S.A\u0002-CQ\u0001V.A\u0002-CQ\u0001W.A\u0002-CQaY \u0005B\u0011\f\u0001\u0002^8TiJLgn\u001a\u000b\u0002\u0017\"9amPA\u0001\n\u00039\u0017\u0001B2paf$B!\u00185jU\"9\u0011*\u001aI\u0001\u0002\u0004Y\u0005b\u0002+f!\u0003\u0005\ra\u0013\u0005\b1\u0016\u0004\n\u00111\u0001L\u0011\u001daw(%A\u0005\u00025\fabY8qs\u0012\"WMZ1vYR$\u0013'F\u0001oU\tYunK\u0001q!\t\th/D\u0001s\u0015\t\u0019H/A\u0005v]\u000eDWmY6fI*\u0011Q/E\u0001\u000bC:tw\u000e^1uS>t\u0017BA<s\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bs~\n\n\u0011\"\u0001n\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIIBqa_ \u0012\u0002\u0013\u0005Q.\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001a\t\u000fu|\u0014\u0011!C!}\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012a\u000e\u0005\n\u0003\u0003y\u0014\u0011!C\u0001\u0003\u0007\tA\u0002\u001d:pIV\u001cG/\u0011:jif,\"!!\u0002\u0011\u0007A\t9!C\u0002\u0002\nE\u00111!\u00138u\u0011%\tiaPA\u0001\n\u0003\ty!\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\t\u0005E\u0011q\u0003\t\u0004!\u0005M\u0011bAA\u000b#\t\u0019\u0011I\\=\t\u0013-\nY!!AA\u0002\u0005\u0015\u0001\"CA\u000e\u007f\u0005\u0005I\u0011IA\u000f\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA\u0010!\u0015\u0011\u0014\u0011EA\t\u0013\r\t\u0019c\r\u0002\t\u0013R,'/\u0019;pe\"I\u0011qE \u0002\u0002\u0013\u0005\u0011\u0011F\u0001\tG\u0006tW)];bYR!\u00111FA\u0019!\r\u0001\u0012QF\u0005\u0004\u0003_\t\"a\u0002\"p_2,\u0017M\u001c\u0005\nW\u0005\u0015\u0012\u0011!a\u0001\u0003#A\u0011\"!\u000e@\u0003\u0003%\t%a\u000e\u0002\u0011!\f7\u000f[\"pI\u0016$\"!!\u0002\t\u0013\u0005mr(!A\u0005B\u0005u\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002,\u0005}\u0002\"C\u0016\u0002:\u0005\u0005\t\u0019AA\t\u000f)\t\u0019\u0005DA\u0001\u0012\u0003\u0011\u0011QI\u0001\u0010\u001b\u00064XM\\\"p_J$\u0017N\\1uKB\u0019a,a\u0012\u0007\u0013\u0001c\u0011\u0011!E\u0001\u0005\u0005%3#BA$\u0003\u0017*\u0005\u0003CA'\u0003'Z5jS/\u000e\u0005\u0005=#bAA)#\u00059!/\u001e8uS6,\u0017\u0002BA+\u0003\u001f\u0012\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c84\u0011\u001d1\u0012q\tC\u0001\u00033\"\"!!\u0012\t\u0013\r\f9%!A\u0005F\u0005uC#A\u001c\t\u0015\u0005\u0005\u0014qIA\u0001\n\u0003\u000b\u0019'A\u0003baBd\u0017\u0010F\u0004^\u0003K\n9'!\u001b\t\r%\u000by\u00061\u0001L\u0011\u0019!\u0016q\fa\u0001\u0017\"1\u0001,a\u0018A\u0002-C!\"!\u001c\u0002H\u0005\u0005I\u0011QA8\u0003\u001d)h.\u00199qYf$B!!\u001d\u0002~A)\u0001#a\u001d\u0002x%\u0019\u0011QO\t\u0003\r=\u0003H/[8o!\u0019\u0001\u0012\u0011P&L\u0017&\u0019\u00111P\t\u0003\rQ+\b\u000f\\34\u0011%\ty(a\u001b\u0002\u0002\u0003\u0007Q,A\u0002yIAB!\"a!\u0002H\u0005\u0005I\u0011BAC\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u001d\u0005c\u0001\u001d\u0002\n&\u0019\u00111R\u001d\u0003\r=\u0013'.Z2u\u0011\u001d\ty\t\u0004C\u0001\u0003#\u000bq#\u001a=ue\u0006\u001cG/T1wK:\u001cun\u001c:eS:\fG/Z:\u0015\t\u0005M\u0015\u0011\u0016\t\u0006\u0003+\u000b)+\u0018\b\u0005\u0003/\u000b\tK\u0004\u0003\u0002\u001a\u0006}UBAAN\u0015\r\tijF\u0001\u0007yI|w\u000e\u001e \n\u0003II1!a)\u0012\u0003\u001d\u0001\u0018mY6bO\u0016L1ANAT\u0015\r\t\u0019+\u0005\u0005\b\u0003W\u000bi\t1\u0001L\u0003-\u0019wn\u001c:eS:\fG/Z:\t\u0011\u0005=F\u0002\"\u0001\u0005\u0003c\u000ba!\u001c\u001aQCRDWCAAZ!\ri\u0012QW\u0005\u0004\u0003os\"\u0001\u0002$jY\u0016Dq!a/\r\t\u0003\ti,A\nde\u0016\fG/\u001a*fa>\u0014Vm]8mm\u0016\u00148\u000f\u0006\u0003\u0002@\u0006M\u0007\u0003BAa\u0003\u001fl!!a1\u000b\t\u0005\u0015\u0017qY\u0001\te\u0016\u001cx\u000e\u001c<fe*!\u0011\u0011ZAf\u0003\u001d\u0001H.^4j]NT1!!4\u0007\u0003\rIg/_\u0005\u0005\u0003#\f\u0019MA\u0007DQ\u0006LgNU3t_24XM\u001d\u0005\t\u0003+\fI\f1\u0001\u00024\u0006\tB-\u001a4bk2$\u0018J^=Vg\u0016\u0014H)\u001b:\t\u000f\u0005eG\u0002\"\u0001\u0002\\\u00061\"/Z:pYZ,G)\u001a9f]\u0012,gnY=QCRD7\u000fF\u0003L\u0003;\f9\u000f\u0003\u0005\u0002`\u0006]\u0007\u0019AAq\u0003%\t'\u000f^5gC\u000e$8\u000f\u0005\u0003\u0011\u0003G|\u0011bAAs#\t)\u0011I\u001d:bs\"A\u0011\u0011^Al\u0001\u0004\t\u0019,\u0001\bdC\u000eDW\rR5sK\u000e$xN]=\t\u000f\u00055H\u0002\"\u0001\u0002p\u0006!\u0012\r\u001a3EKB,g\u000eZ3oG&,7\u000fV8Jmf$raJAy\u0005\u0013\u0011Y\u0001\u0003\u0005\u0002t\u0006-\b\u0019AA{\u0003\tiG\r\u0005\u0003\u0002x\n\u0015QBAA}\u0015\u0011\tY0!@\u0002\u0015\u0011,7o\u0019:jaR|'O\u0003\u0003\u0002\u0000\n\u0005\u0011AB7pIVdWM\u0003\u0003\u0003\u0004\u0005-\u0017\u0001B2pe\u0016LAAa\u0002\u0002z\n9B)\u001a4bk2$Xj\u001c3vY\u0016$Um]2sSB$xN\u001d\u0005\t\u0003?\fY\u000f1\u0001\u0002\u0014\"9!QBAv\u0001\u0004Y\u0015aC5ws\u000e{gN\u001a(b[\u0016DqA!\u0005\r\t\u0003\u0011\u0019\"A\tbI\u0012,\u0005p\u00197vg&|gNU;mKN$ra\nB\u000b\u0005K\u00119\u0003\u0003\u0005\u0003\u0018\t=\u0001\u0019\u0001B\r\u0003-Ig/_*fiRLgnZ:\u0011\t\tm!\u0011E\u0007\u0003\u0005;QAAa\b\u0003\u0002\u0005A1/\u001a;uS:<7/\u0003\u0003\u0003$\tu!aC%wsN+G\u000f^5oONDqA!\u0004\u0003\u0010\u0001\u00071\n\u0003\u0005\u0002t\n=\u0001\u0019AA{\u0011\u001d\u0011Y\u0003\u0004C\u0001\u0005[\t\u0001CY;jY\u0012Le/_*fiRLgnZ:\u0015\r\te!q\u0006B\u001b\u0011!\u0011\tD!\u000bA\u0002\tM\u0012a\u0003:f[>$XMU3q_N\u0004B\u0001EA:\u0017\"A!q\u0007B\u0015\u0001\u0004\u0011\u0019$A\u0004jmf\u0004\u0016\r\u001e5\t\u000f\tmB\u0002\"\u0001\u0003>\u0005yAn\\1e\u0013ZL8+\u001a;uS:<7\u000f\u0006\u0005\u0003\u001a\t}\"1\tB#\u0011\u001d\u0011\tE!\u000fA\u0002-\u000bAb]3ui&twm\u001d$jY\u0016D\u0001B!\r\u0003:\u0001\u0007!1\u0007\u0005\t\u0005o\u0011I\u00041\u0001\u00034!9!\u0011\n\u0007\u0005\n\t-\u0013!\u00059s_\u000e,7o]%wsB\u000bG\u000f[!sOR)qE!\u0014\u0003P!A!q\u0003B$\u0001\u0004\u0011I\u0002\u0003\u0005\u00038\t\u001d\u0003\u0019\u0001B\u001a\u0011\u001d\u0011\u0019\u0006\u0004C\u0005\u0005+\nA\u0003\u001d:pG\u0016\u001c8OU3n_R,'+\u001a9p\u0003J<G#B\u0014\u0003X\te\u0003\u0002\u0003B\f\u0005#\u0002\rA!\u0007\t\u0011\tE\"\u0011\u000ba\u0001\u0005gAqA!\u0018\r\t\u0003\u0011y&A\nhKRlu\u000eZ;mK\u0012+7o\u0019:jaR|'/\u0006\u0002\u0002v\"9!1\r\u0007\u0005\u0002\t\u0015\u0014a\u0006:fg>dg/Z'bm\u0016t7i\\8sI&t\u0017\r^3t)%Y%q\rB5\u0005W\u0012\t\bC\u0004\u0002,\n\u0005\u0004\u0019A&\t\u0011\t]!\u0011\ra\u0001\u00053A!B!\u001c\u0003bA\u0005\t\u0019\u0001B8\u0003))\u0007p\u00197vg&|gn\u001d\t\u0006\u0003+\u000b)k\u0013\u0005\u000b\u0005g\u0012\t\u0007%AA\u0002\u0005-\u0012AB5t)\u0016\u001cH\u000f\u0003\u0005\u0003x1!\tA\u0001B=\u0003=\u0019'/Z1uK\u0016C8\r\\;tS>tG\u0003\u0003B>\u0005\u0003\u0013)Ia\"\u0011\t\u0005](QP\u0005\u0005\u0005\u007f\nIPA\u0006Fq\u000edW\u000fZ3Sk2,\u0007b\u0002BB\u0005k\u0002\raS\u0001\u0007G>|'\u000fZ:\t\u0011\t]!Q\u000fa\u0001\u00053AqA!\u0004\u0003v\u0001\u00071\nC\u0005\u0003\f2\t\n\u0011\"\u0001\u0003\u000e\u0006\t#/Z:pYZ,W*\u0019<f]\u000e{wN\u001d3j]\u0006$Xm\u001d\u0013eK\u001a\fW\u000f\u001c;%gU\u0011!q\u0012\u0016\u0004\u0005_z\u0007\"\u0003BJ\u0019E\u0005I\u0011\u0001BK\u0003\u0005\u0012Xm]8mm\u0016l\u0015M^3o\u0007>|'\u000fZ5oCR,7\u000f\n3fM\u0006,H\u000e\u001e\u00135+\t\u00119JK\u0002\u0002,=\u0004"
)
public final class SparkSubmitUtils {
   public static boolean resolveMavenCoordinates$default$4() {
      return SparkSubmitUtils$.MODULE$.resolveMavenCoordinates$default$4();
   }

   public static Seq<String> resolveMavenCoordinates$default$3() {
      return SparkSubmitUtils$.MODULE$.resolveMavenCoordinates$default$3();
   }

   public static String resolveMavenCoordinates(String var0, IvySettings var1, Seq<String> var2, boolean var3) {
      return SparkSubmitUtils$.MODULE$.resolveMavenCoordinates(var0, var1, var2, var3);
   }

   public static DefaultModuleDescriptor getModuleDescriptor() {
      return SparkSubmitUtils$.MODULE$.getModuleDescriptor();
   }

   public static IvySettings loadIvySettings(String var0, Option<String> var1, Option<String> var2) {
      return SparkSubmitUtils$.MODULE$.loadIvySettings(var0, var1, var2);
   }

   public static IvySettings buildIvySettings(Option<String> var0, Option<String> var1) {
      return SparkSubmitUtils$.MODULE$.buildIvySettings(var0, var1);
   }

   public static void addExclusionRules(IvySettings var0, String var1, DefaultModuleDescriptor var2) {
      SparkSubmitUtils$.MODULE$.addExclusionRules(var0, var1, var2);
   }

   public static void addDependenciesToIvy(DefaultModuleDescriptor var0, Seq<MavenCoordinate> var1, String var2) {
      SparkSubmitUtils$.MODULE$.addDependenciesToIvy(var0, var1, var2);
   }

   public static String resolveDependencyPaths(Object[] var0, File var1) {
      return SparkSubmitUtils$.MODULE$.resolveDependencyPaths(var0, var1);
   }

   public static ChainResolver createRepoResolvers(File var0) {
      return SparkSubmitUtils$.MODULE$.createRepoResolvers(var0);
   }

   public static Seq<MavenCoordinate> extractMavenCoordinates(String var0) {
      return SparkSubmitUtils$.MODULE$.extractMavenCoordinates(var0);
   }

   public static Seq<String> IVY_DEFAULT_EXCLUDES() {
      return SparkSubmitUtils$.MODULE$.IVY_DEFAULT_EXCLUDES();
   }

   public static void printStream_$eq(PrintStream var0) {
      SparkSubmitUtils$.MODULE$.printStream_$eq(var0);
   }

   public static PrintStream printStream() {
      return SparkSubmitUtils$.MODULE$.printStream();
   }

   public static class MavenCoordinate implements Product, Serializable {
      private final String groupId;
      private final String artifactId;
      private final String version;

      public String groupId() {
         return this.groupId;
      }

      public String artifactId() {
         return this.artifactId;
      }

      public String version() {
         return this.version;
      }

      public String toString() {
         return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ":", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.groupId(), this.artifactId(), this.version()}));
      }

      public MavenCoordinate copy(String groupId, String artifactId, String version) {
         return new MavenCoordinate(groupId, artifactId, version);
      }

      public String copy$default$1() {
         return this.groupId();
      }

      public String copy$default$2() {
         return this.artifactId();
      }

      public String copy$default$3() {
         return this.version();
      }

      public String productPrefix() {
         return "MavenCoordinate";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         String var10000;
         switch (x$1) {
            case 0:
               var10000 = this.groupId();
               break;
            case 1:
               var10000 = this.artifactId();
               break;
            case 2:
               var10000 = this.version();
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
         return x$1 instanceof MavenCoordinate;
      }

      public int hashCode() {
         return ScalaRunTime$.MODULE$._hashCode(this);
      }

      public boolean equals(Object x$1) {
         boolean var11;
         if (this != x$1) {
            label71: {
               boolean var3;
               if (x$1 instanceof MavenCoordinate) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label53: {
                     label62: {
                        MavenCoordinate var4 = (MavenCoordinate)x$1;
                        String var10000 = this.groupId();
                        String var5 = var4.groupId();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label62;
                        }

                        var10000 = this.artifactId();
                        String var6 = var4.artifactId();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label62;
                        }

                        var10000 = this.version();
                        String var7 = var4.version();
                        if (var10000 == null) {
                           if (var7 != null) {
                              break label62;
                           }
                        } else if (!var10000.equals(var7)) {
                           break label62;
                        }

                        if (var4.canEqual(this)) {
                           var11 = true;
                           break label53;
                        }
                     }

                     var11 = false;
                  }

                  if (var11) {
                     break label71;
                  }
               }

               var11 = false;
               return var11;
            }
         }

         var11 = true;
         return var11;
      }

      public MavenCoordinate(String groupId, String artifactId, String version) {
         this.groupId = groupId;
         this.artifactId = artifactId;
         this.version = version;
         Product$class.$init$(this);
      }
   }

   public static class MavenCoordinate$ extends AbstractFunction3<String, String, String, MavenCoordinate> implements Serializable {
      public static final MavenCoordinate$ MODULE$;

      static {
         new MavenCoordinate$();
      }

      public final String toString() {
         return "MavenCoordinate";
      }

      public MavenCoordinate apply(String groupId, String artifactId, String version) {
         return new MavenCoordinate(groupId, artifactId, version);
      }

      public Option<Tuple3<String, String, String>> unapply(MavenCoordinate x$0) {
         return (Option<Tuple3<String, String, String>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.groupId(), x$0.artifactId(), x$0.version())));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public MavenCoordinate$() {
         MODULE$ = this;
      }
   }
}
