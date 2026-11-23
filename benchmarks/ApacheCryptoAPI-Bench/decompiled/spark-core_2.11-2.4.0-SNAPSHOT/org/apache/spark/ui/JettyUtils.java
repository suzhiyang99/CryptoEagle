package org.apache.spark.ui;

import java.net.URI;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.spark.SSLOptions;
import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import org.json4s.JsonAST;
import org.slf4j.Logger;
import org.spark_project.jetty.servlet.ServletContextHandler;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.xml.Node;

@ScalaSignature(
   bytes = "\u0006\u0001\t5xAB\u0001\u0003\u0011\u0003!!\"\u0001\u0006KKR$\u00180\u0016;jYNT!a\u0001\u0003\u0002\u0005UL'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0011\u0005-aQ\"\u0001\u0002\u0007\r5\u0011\u0001\u0012\u0001\u0003\u000f\u0005)QU\r\u001e;z+RLGn]\n\u0004\u0019=)\u0002C\u0001\t\u0014\u001b\u0005\t\"\"\u0001\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005Q\t\"AB!osJ+g\r\u0005\u0002\u001735\tqC\u0003\u0002\u0019\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001b/\t9Aj\\4hS:<\u0007\"\u0002\u000f\r\t\u0003q\u0012A\u0002\u001fj]&$hh\u0001\u0001\u0015\u0003)Aq\u0001\t\u0007C\u0002\u0013\u0005\u0011%\u0001\u000bT!\u0006\u00136jX\"P\u001d:+5\tV(S?:\u000bU*R\u000b\u0002EA\u00111\u0005K\u0007\u0002I)\u0011QEJ\u0001\u0005Y\u0006twMC\u0001(\u0003\u0011Q\u0017M^1\n\u0005%\"#AB*ue&tw\r\u0003\u0004,\u0019\u0001\u0006IAI\u0001\u0016'B\u000b%kS0D\u001f:sUi\u0011+P%~s\u0015)T#!\u0011\u001diCB1A\u0005\u0002\u0005\nqCU#E\u0013J+5\tV0D\u001f:sUi\u0011+P%~s\u0015)T#\t\r=b\u0001\u0015!\u0003#\u0003a\u0011V\tR%S\u000b\u000e#vlQ(O\u001d\u0016\u001bEk\u0014*`\u001d\u0006kU\tI\u0003\u0005c1\u0001!GA\u0005SKN\u0004xN\u001c3feV\u00111G\u0011\t\u0005!Q2\u0004)\u0003\u00026#\tIa)\u001e8di&|g.\r\t\u0003oyj\u0011\u0001\u000f\u0006\u0003si\nA\u0001\u001b;ua*\u00111\bP\u0001\bg\u0016\u0014h\u000f\\3u\u0015\u0005i\u0014!\u00026bm\u0006D\u0018BA 9\u0005IAE\u000f\u001e9TKJ4H.\u001a;SKF,Xm\u001d;\u0011\u0005\u0005\u0013E\u0002\u0001\u0003\u0006\u0007B\u0012\r\u0001\u0012\u0002\u0002)F\u0011Q\t\u0013\t\u0003!\u0019K!aR\t\u0003\u000f9{G\u000f[5oOB\u0011\u0001#S\u0005\u0003\u0015F\u00111!\u00118z\r\u0011aE\u0002A'\u0003\u001bM+'O\u001e7fiB\u000b'/Y7t+\tqUk\u0005\u0002L\u001f!A\u0001k\u0013BC\u0002\u0013\u0005\u0011+A\u0005sKN\u0004xN\u001c3feV\t!\u000bE\u0002TaQk\u0011\u0001\u0004\t\u0003\u0003V#QaQ&C\u0002Y\u000b\"!R\b\t\u0011a[%\u0011!Q\u0001\nI\u000b!B]3ta>tG-\u001a:!\u0011!Q6J!b\u0001\n\u0003Y\u0016aC2p]R,g\u000e\u001e+za\u0016,\u0012\u0001\u0018\t\u0003;\u0002t!\u0001\u00050\n\u0005}\u000b\u0012A\u0002)sK\u0012,g-\u0003\u0002*C*\u0011q,\u0005\u0005\tG.\u0013\t\u0011)A\u00059\u0006a1m\u001c8uK:$H+\u001f9fA!AQm\u0013BC\u0002\u0013\u0005a-A\u0005fqR\u0014\u0018m\u0019;G]V\tq\r\u0005\u0003\u0011iQc\u0006\u0002C5L\u0005\u0003\u0005\u000b\u0011B4\u0002\u0015\u0015DHO]1di\u001as\u0007\u0005C\u0003\u001d\u0017\u0012\u00051\u000e\u0006\u0003m[:|\u0007cA*L)\")\u0001K\u001ba\u0001%\")!L\u001ba\u00019\"9QM\u001bI\u0001\u0002\u00049waB9\r\u0003\u0003E\tA]\u0001\u000e'\u0016\u0014h\u000f\\3u!\u0006\u0014\u0018-\\:\u0011\u0005M\u001bha\u0002'\r\u0003\u0003E\t\u0001^\n\u0003g>AQ\u0001H:\u0005\u0002Y$\u0012A\u001d\u0005\bqN\f\n\u0011\"\u0001z\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%gU\u0019!0!\u0004\u0016\u0003mT#\u0001`?\u0011\tA!\u0004JI\u0016\u0002}B\u0019q0!\u0003\u000e\u0005\u0005\u0005!\u0002BA\u0002\u0003\u000b\t\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005\u001d\u0011#\u0001\u0006b]:|G/\u0019;j_:LA!a\u0003\u0002\u0002\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\u0005\u000b\r;(\u0019\u0001,\t\u000f\u0005EA\u0002b\u0001\u0002\u0014\u00051\"n]8o%\u0016\u001c\bo\u001c8eKJ$vnU3sm2,G\u000f\u0006\u0003\u0002\u0016\u0005]\u0002\u0003B*L\u0003/\u0001B!!\u0007\u000229!\u00111DA\u0016\u001d\u0011\ti\"a\n\u000f\t\u0005}\u0011QE\u0007\u0003\u0003CQ1!a\t\u001e\u0003\u0019a$o\\8u}%\t\u0011\"C\u0002\u0002*!\taA[:p]R\u001a\u0018\u0002BA\u0017\u0003_\tq\u0001]1dW\u0006<WMC\u0002\u0002*!IA!a\r\u00026\t1!JV1mk\u0016TA!!\f\u00020!9\u0001+a\u0004A\u0002\u0005e\u0002\u0003B*1\u0003/Aq!!\u0010\r\t\u0007\ty$\u0001\fii6d'+Z:q_:$WM\u001d+p'\u0016\u0014h\u000f\\3u)\u0011\t\t%a\u0018\u0011\tM[\u00151\t\t\u0007\u0003\u000b\ni%a\u0015\u000f\t\u0005\u001d\u00131\n\b\u0005\u0003?\tI%C\u0001\u0013\u0013\r\ti#E\u0005\u0005\u0003\u001f\n\tFA\u0002TKFT1!!\f\u0012!\u0011\t)&a\u0017\u000e\u0005\u0005]#bAA-#\u0005\u0019\u00010\u001c7\n\t\u0005u\u0013q\u000b\u0002\u0005\u001d>$W\rC\u0004Q\u0003w\u0001\r!!\u0019\u0011\tM\u0003\u00141\t\u0005\b\u0003KbA1AA4\u0003Y!X\r\u001f;SKN\u0004xN\u001c3feR{7+\u001a:wY\u0016$H\u0003BA5\u0003W\u00022aU&]\u0011\u001d\u0001\u00161\ra\u0001\u0003[\u00022a\u0015\u0019]\u0011\u001d\t\t\b\u0004C\u0001\u0003g\nQb\u0019:fCR,7+\u001a:wY\u0016$X\u0003BA;\u0003\u000b#\u0002\"a\u001e\u0002~\u0005\u001d\u00151\u0013\t\u0004o\u0005e\u0014bAA>q\tY\u0001\n\u001e;q'\u0016\u0014h\u000f\\3u\u0011!\ty(a\u001cA\u0002\u0005\u0005\u0015!D:feZdW\r\u001e)be\u0006l7\u000f\u0005\u0003T\u0017\u0006\r\u0005cA!\u0002\u0006\u001211)a\u001cC\u0002YC\u0001\"!#\u0002p\u0001\u0007\u00111R\u0001\fg\u0016\u001cWO]5us6;'\u000f\u0005\u0003\u0002\u000e\u0006=U\"\u0001\u0003\n\u0007\u0005EEAA\bTK\u000e,(/\u001b;z\u001b\u0006t\u0017mZ3s\u0011!\t)*a\u001cA\u0002\u0005]\u0015\u0001B2p]\u001a\u0004B!!$\u0002\u001a&\u0019\u00111\u0014\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007bBAP\u0019\u0011\u0005\u0011\u0011U\u0001\u0015GJ,\u0017\r^3TKJ4H.\u001a;IC:$G.\u001a:\u0016\t\u0005\r\u0016\u0011\u0019\u000b\r\u0003K\u000b9,a/\u0002D\u0006\u0015\u0017q\u0019\t\u0005\u0003O\u000b\u0019,\u0004\u0002\u0002**\u00191(a+\u000b\t\u00055\u0016qV\u0001\u0006U\u0016$H/\u001f\u0006\u0004\u0003cC\u0011aB3dY&\u00048/Z\u0005\u0005\u0003k\u000bIKA\u000bTKJ4H.\u001a;D_:$X\r\u001f;IC:$G.\u001a:\t\u000f\u0005e\u0016Q\u0014a\u00019\u0006!\u0001/\u0019;i\u0011!\ty(!(A\u0002\u0005u\u0006\u0003B*L\u0003\u007f\u00032!QAa\t\u0019\u0019\u0015Q\u0014b\u0001-\"A\u0011\u0011RAO\u0001\u0004\tY\t\u0003\u0005\u0002\u0016\u0006u\u0005\u0019AAL\u0011%\tI-!(\u0011\u0002\u0003\u0007A,\u0001\u0005cCN,\u0007+\u0019;i\u0011\u001d\ty\n\u0004C\u0001\u0003\u001b$\u0002\"!*\u0002P\u0006E\u00171\u001b\u0005\b\u0003s\u000bY\r1\u0001]\u0011\u001dY\u00141\u001aa\u0001\u0003oBq!!3\u0002L\u0002\u0007A\fC\u0004\u0002X2!\t!!7\u0002+\r\u0014X-\u0019;f%\u0016$\u0017N]3di\"\u000bg\u000e\u001a7feRa\u0011QUAn\u0003?\f\u0019/a<\u0002r\"9\u0011Q\\Ak\u0001\u0004a\u0016aB:sGB\u000bG\u000f\u001b\u0005\b\u0003C\f)\u000e1\u0001]\u0003!!Wm\u001d;QCRD\u0007BCAs\u0003+\u0004\n\u00111\u0001\u0002h\u0006q!-\u001a4pe\u0016\u0014V\rZ5sK\u000e$\b#\u0002\t5m\u0005%\bc\u0001\t\u0002l&\u0019\u0011Q^\t\u0003\tUs\u0017\u000e\u001e\u0005\n\u0003\u0013\f)\u000e%AA\u0002qC!\"a=\u0002VB\u0005\t\u0019AA{\u0003-AG\u000f\u001e9NKRDw\u000eZ:\u0011\tu\u000b9\u0010X\u0005\u0004\u0003s\f'aA*fi\"9\u0011Q \u0007\u0005\u0002\u0005}\u0018aE2sK\u0006$Xm\u0015;bi&\u001c\u0007*\u00198eY\u0016\u0014HCBAS\u0005\u0003\u0011)\u0001C\u0004\u0003\u0004\u0005m\b\u0019\u0001/\u0002\u0019I,7o\\;sG\u0016\u0014\u0015m]3\t\u000f\u0005e\u00161 a\u00019\"9!\u0011\u0002\u0007\u0005\u0002\t-\u0011AE2sK\u0006$X\r\u0015:pqfD\u0015M\u001c3mKJ$B!!*\u0003\u000e!A!q\u0002B\u0004\u0001\u0004\u0011\t\"A\u0007jIR{W+[!eIJ,7o\u001d\t\u0006!Qb&1\u0003\t\u0005!\tUA,C\u0002\u0003\u0018E\u0011aa\u00149uS>t\u0007b\u0002B\u000e\u0019\u0011\u0005!QD\u0001\u000bC\u0012$g)\u001b7uKJ\u001cHCBAu\u0005?\u0011)\u0003\u0003\u0005\u0003\"\te\u0001\u0019\u0001B\u0012\u0003!A\u0017M\u001c3mKJ\u001c\bCBA#\u0003\u001b\n)\u000b\u0003\u0005\u0002\u0016\ne\u0001\u0019AAL\u0011\u001d\u0011I\u0003\u0004C\u0001\u0005W\t\u0001c\u001d;beRTU\r\u001e;z'\u0016\u0014h/\u001a:\u0015\u001d\t5\"1\u0007B\u001c\u0005\u0003\u0012YE!\u0014\u0003PA\u00191Ba\f\n\u0007\tE\"A\u0001\u0006TKJ4XM]%oM>DqA!\u000e\u0003(\u0001\u0007A,\u0001\u0005i_N$h*Y7f\u0011!\u0011IDa\nA\u0002\tm\u0012\u0001\u00029peR\u00042\u0001\u0005B\u001f\u0013\r\u0011y$\u0005\u0002\u0004\u0013:$\b\u0002\u0003B\"\u0005O\u0001\rA!\u0012\u0002\u0015M\u001cHn\u00149uS>t7\u000f\u0005\u0003\u0002\u000e\n\u001d\u0013b\u0001B%\t\tQ1k\u0015'PaRLwN\\:\t\u0011\t\u0005\"q\u0005a\u0001\u0005GA\u0001\"!&\u0003(\u0001\u0007\u0011q\u0013\u0005\n\u0005#\u00129\u0003%AA\u0002q\u000b!b]3sm\u0016\u0014h*Y7f\u0011\u001d\u0011)\u0006\u0004C\u0005\u0005/\n!d\u0019:fCR,'+\u001a3je\u0016\u001cG\u000f\u0013;uaND\u0015M\u001c3mKJ$bA!\u0017\u0003j\t5\u0004\u0003\u0002B.\u0005Kj!A!\u0018\u000b\t\t}#\u0011M\u0001\bQ\u0006tG\r\\3s\u0015\u0011\u0011\u0019'a+\u0002\rM,'O^3s\u0013\u0011\u00119G!\u0018\u0003\u001d\r{g\u000e^3yi\"\u000bg\u000e\u001a7fe\"A!1\u000eB*\u0001\u0004\u0011Y$\u0001\u0006tK\u000e,(/\u001a)peRDqAa\u001c\u0003T\u0001\u0007A,\u0001\u0004tG\",W.\u001a\u0005\b\u0005gbA\u0011\u0001B;\u00039\u0019'/Z1uKB\u0013x\u000e_=V%&#\"Ba\u001e\u0003\u0004\n\u001d%1\u0012BG!\u0011\u0011IHa \u000e\u0005\tm$b\u0001B?M\u0005\u0019a.\u001a;\n\t\t\u0005%1\u0010\u0002\u0004+JK\u0005b\u0002BC\u0005c\u0002\r\u0001X\u0001\u0007aJ,g-\u001b=\t\u000f\t%%\u0011\u000fa\u00019\u00061A/\u0019:hKRDq!!/\u0003r\u0001\u0007A\fC\u0004\u0003\u0010\nE\u0004\u0019\u0001/\u0002\u000bE,XM]=\t\u000f\tME\u0002\"\u0001\u0003\u0016\u0006I2M]3bi\u0016\u0004&o\u001c=z\u0019>\u001c\u0017\r^5p]\"+\u0017\rZ3s)\u001da&q\u0013BN\u0005?CqA!'\u0003\u0012\u0002\u0007A,A\u0006iK\u0006$WM\u001d,bYV,\u0007b\u0002BO\u0005#\u0003\rAN\u0001\u000eG2LWM\u001c;SKF,Xm\u001d;\t\u0011\t\u0005&\u0011\u0013a\u0001\u0005o\n\u0011\u0002^1sO\u0016$XK]5\t\u000f\t\u0015F\u0002\"\u0003\u0003(\u0006\t2M]3bi\u0016\u0014V\rZ5sK\u000e$XKU%\u0015\u0017\t\u0012IKa+\u0003.\n=&\u0011\u0017\u0005\b\u0005_\u0012\u0019\u000b1\u0001]\u0011\u001d\u0011\u0019Ga)A\u0002qC\u0001B!\u000f\u0003$\u0002\u0007!1\b\u0005\b\u0003s\u0013\u0019\u000b1\u0001]\u0011\u001d\u0011yIa)A\u0002qCqA!.\r\t\u0003\u00119,\u0001\bu_ZK'\u000f^;bY\"{7\u000f^:\u0015\t\te&q\u0018\t\u0005!\tmF,C\u0002\u0003>F\u0011Q!\u0011:sCfD\u0001B!1\u00034\u0002\u0007!1Y\u0001\u000bG>tg.Z2u_J\u001c\b\u0003\u0002\t\u0003FrK1Aa2\u0012\u0005)a$/\u001a9fCR,GM\u0010\u0005\n\u0005\u0017d\u0011\u0013!C\u0001\u0005\u001b\fqd\u0019:fCR,'+\u001a3je\u0016\u001cG\u000fS1oI2,'\u000f\n3fM\u0006,H\u000e\u001e\u00134+\t\u0011yMK\u0002\u0002hvD\u0011Ba5\r#\u0003%\tA!6\u0002?\r\u0014X-\u0019;f%\u0016$\u0017N]3di\"\u000bg\u000e\u001a7fe\u0012\"WMZ1vYR$C'\u0006\u0002\u0003X*\u0012A, \u0005\n\u00057d\u0011\u0013!C\u0001\u0005;\fqd\u0019:fCR,'+\u001a3je\u0016\u001cG\u000fS1oI2,'\u000f\n3fM\u0006,H\u000e\u001e\u00136+\t\u0011yNK\u0002\u0002vvD\u0011Ba9\r#\u0003%\tA!:\u0002=\r\u0014X-\u0019;f'\u0016\u0014h\u000f\\3u\u0011\u0006tG\r\\3sI\u0011,g-Y;mi\u0012*T\u0003\u0002Bk\u0005O$aa\u0011Bq\u0005\u00041\u0006\"\u0003Bv\u0019E\u0005I\u0011\u0001Bk\u0003i\u0019H/\u0019:u\u0015\u0016$H/_*feZ,'\u000f\n3fM\u0006,H\u000e\u001e\u00137\u0001"
)
public final class JettyUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return JettyUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return JettyUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      JettyUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return JettyUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      JettyUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      JettyUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      JettyUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      JettyUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      JettyUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      JettyUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      JettyUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      JettyUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      JettyUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      JettyUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return JettyUtils$.MODULE$.log();
   }

   public static String logName() {
      return JettyUtils$.MODULE$.logName();
   }

   public static String startJettyServer$default$6() {
      return JettyUtils$.MODULE$.startJettyServer$default$6();
   }

   public static <T> String createServletHandler$default$5() {
      return JettyUtils$.MODULE$.createServletHandler$default$5();
   }

   public static Set<String> createRedirectHandler$default$5() {
      return JettyUtils$.MODULE$.createRedirectHandler$default$5();
   }

   public static String createRedirectHandler$default$4() {
      return JettyUtils$.MODULE$.createRedirectHandler$default$4();
   }

   public static Function1<HttpServletRequest, BoxedUnit> createRedirectHandler$default$3() {
      return JettyUtils$.MODULE$.createRedirectHandler$default$3();
   }

   public static String[] toVirtualHosts(Seq<String> var0) {
      return JettyUtils$.MODULE$.toVirtualHosts(var0);
   }

   public static String createProxyLocationHeader(String var0, HttpServletRequest var1, URI var2) {
      return JettyUtils$.MODULE$.createProxyLocationHeader(var0, var1, var2);
   }

   public static URI createProxyURI(String var0, String var1, String var2, String var3) {
      return JettyUtils$.MODULE$.createProxyURI(var0, var1, var2, var3);
   }

   public static ServerInfo startJettyServer(String var0, int var1, SSLOptions var2, Seq<ServletContextHandler> var3, SparkConf var4, String var5) {
      return JettyUtils$.MODULE$.startJettyServer(var0, var1, var2, var3, var4, var5);
   }

   public static void addFilters(Seq<ServletContextHandler> var0, SparkConf var1) {
      JettyUtils$.MODULE$.addFilters(var0, var1);
   }

   public static ServletContextHandler createProxyHandler(Function1<String, Option<String>> var0) {
      return JettyUtils$.MODULE$.createProxyHandler(var0);
   }

   public static ServletContextHandler createStaticHandler(String var0, String var1) {
      return JettyUtils$.MODULE$.createStaticHandler(var0, var1);
   }

   public static ServletContextHandler createRedirectHandler(String var0, String var1, Function1<HttpServletRequest, BoxedUnit> var2, String var3, Set<String> var4) {
      return JettyUtils$.MODULE$.createRedirectHandler(var0, var1, var2, var3, var4);
   }

   public static ServletContextHandler createServletHandler(String var0, HttpServlet var1, String var2) {
      return JettyUtils$.MODULE$.createServletHandler(var0, var1, var2);
   }

   public static <T> ServletContextHandler createServletHandler(String var0, ServletParams<T> var1, SecurityManager var2, SparkConf var3, String var4) {
      return JettyUtils$.MODULE$.createServletHandler(var0, var1, var2, var3, var4);
   }

   public static <T> HttpServlet createServlet(ServletParams<T> var0, SecurityManager var1, SparkConf var2) {
      return JettyUtils$.MODULE$.createServlet(var0, var1, var2);
   }

   public static ServletParams<String> textResponderToServlet(Function1<HttpServletRequest, String> var0) {
      return JettyUtils$.MODULE$.textResponderToServlet(var0);
   }

   public static ServletParams<Seq<Node>> htmlResponderToServlet(Function1<HttpServletRequest, Seq<Node>> var0) {
      return JettyUtils$.MODULE$.htmlResponderToServlet(var0);
   }

   public static ServletParams<JsonAST.JValue> jsonResponderToServlet(Function1<HttpServletRequest, JsonAST.JValue> var0) {
      return JettyUtils$.MODULE$.jsonResponderToServlet(var0);
   }

   public static String REDIRECT_CONNECTOR_NAME() {
      return JettyUtils$.MODULE$.REDIRECT_CONNECTOR_NAME();
   }

   public static String SPARK_CONNECTOR_NAME() {
      return JettyUtils$.MODULE$.SPARK_CONNECTOR_NAME();
   }

   public static class ServletParams<T> {
      private final Function1<HttpServletRequest, T> responder;
      private final String contentType;
      private final Function1<T, String> extractFn;

      public Function1<HttpServletRequest, T> responder() {
         return this.responder;
      }

      public String contentType() {
         return this.contentType;
      }

      public Function1<T, String> extractFn() {
         return this.extractFn;
      }

      public ServletParams(Function1<HttpServletRequest, T> responder, String contentType, Function1<T, String> extractFn) {
         this.responder = responder;
         this.contentType = contentType;
         this.extractFn = extractFn;
      }
   }

   public static class ServletParams$ {
      public static final ServletParams$ MODULE$;

      static {
         new ServletParams$();
      }

      public <T> Function1<Object, String> $lessinit$greater$default$3() {
         return new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply(Object in) {
               return in.toString();
            }
         };
      }

      public ServletParams$() {
         MODULE$ = this;
      }
   }
}
