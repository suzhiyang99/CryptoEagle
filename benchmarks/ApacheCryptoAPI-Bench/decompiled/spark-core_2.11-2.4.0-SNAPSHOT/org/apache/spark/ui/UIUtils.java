package org.apache.spark.ui;

import java.util.Date;
import org.apache.spark.ui.scope.RDDOperationGraph;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
import scala.xml.Node;
import scala.xml.NodeSeq;

@ScalaSignature(
   bytes = "\u0006\u0001\t%xAB\u0001\u0003\u0011\u0003!!\"A\u0004V\u0013V#\u0018\u000e\\:\u000b\u0005\r!\u0011AA;j\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<\u0007CA\u0006\r\u001b\u0005\u0011aAB\u0007\u0003\u0011\u0003!aBA\u0004V\u0013V#\u0018\u000e\\:\u0014\u00071yQ\u0003\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\rC\u0003\u001d\u0019\u0011\u0005a$\u0001\u0004=S:LGOP\u0002\u0001)\u0005Q\u0001b\u0002\u0011\r\u0005\u0004%\t!I\u0001\u0018)\u0006\u0013E*R0D\u0019\u0006\u001b6k\u0018(P)~\u001bFKU%Q\u000b\u0012+\u0012A\t\t\u0003G!j\u0011\u0001\n\u0006\u0003K\u0019\nA\u0001\\1oO*\tq%\u0001\u0003kCZ\f\u0017BA\u0015%\u0005\u0019\u0019FO]5oO\"11\u0006\u0004Q\u0001\n\t\n\u0001\u0004V!C\u0019\u0016{6\tT!T'~su\nV0T)JK\u0005+\u0012#!\u0011\u001diCB1A\u0005\u0002\u0005\n1\u0003V!C\u0019\u0016{6\tT!T'~\u001bFKU%Q\u000b\u0012Caa\f\u0007!\u0002\u0013\u0011\u0013\u0001\u0006+B\u00052+ul\u0011'B'N{6\u000b\u0016*J!\u0016#\u0005\u0005C\u00042\u0019\t\u0007I\u0011A\u0011\u00029Q\u000b%\tT#`\u00072\u000b5kU0T)JK\u0005+\u0012#`'>\u0013F+\u0011\"M\u000b\"11\u0007\u0004Q\u0001\n\t\nQ\u0004V!C\u0019\u0016{6\tT!T'~\u001bFKU%Q\u000b\u0012{6k\u0014*U\u0003\ncU\t\t\u0005\bk1\u0011\r\u0011\"\u00037\u0003yqUi\u0016'J\u001d\u0016{\u0016I\u0014#`'&su\tT#`#V{E+R0S\u000b\u001e+\u0005,F\u00018!\tAT(D\u0001:\u0015\tQ4(\u0001\u0005nCR\u001c\u0007.\u001b8h\u0015\ta\u0014#\u0001\u0003vi&d\u0017B\u0001 :\u0005\u0015\u0011VmZ3y\u0011\u0019\u0001E\u0002)A\u0005o\u0005yb*R,M\u0013:+u,\u0011(E?NKej\u0012'F?F+v\nV#`%\u0016;U\t\u0017\u0011\t\u000f\tc!\u0019!C\u0005\u0007\u0006QA-\u0019;f\r>\u0014X.\u0019;\u0016\u0003\u0011\u00032aI#H\u0013\t1EEA\u0006UQJ,\u0017\r\u001a'pG\u0006d\u0007C\u0001%L\u001b\u0005I%B\u0001&'\u0003\u0011!X\r\u001f;\n\u00051K%\u0001E*j[BdW\rR1uK\u001a{'/\\1u\u0011\u0019qE\u0002)A\u0005\t\u0006YA-\u0019;f\r>\u0014X.\u0019;!\u0011\u0015\u0001F\u0002\"\u0001R\u0003)1wN]7bi\u0012\u000bG/\u001a\u000b\u0003%b\u0003\"a\u0015,\u000f\u0005A!\u0016BA+\u0012\u0003\u0019\u0001&/\u001a3fM&\u0011\u0011f\u0016\u0006\u0003+FAQ!W(A\u0002i\u000bA\u0001Z1uKB\u00111,X\u0007\u00029*\u0011AHJ\u0005\u0003=r\u0013A\u0001R1uK\")\u0001\u000b\u0004C\u0001AR\u0011!+\u0019\u0005\u0006E~\u0003\raY\u0001\ni&lWm\u001d;b[B\u0004\"\u0001\u00053\n\u0005\u0015\f\"\u0001\u0002'p]\u001eDQa\u001a\u0007\u0005\u0002!\faBZ8s[\u0006$H)\u001e:bi&|g\u000e\u0006\u0002SS\")!N\u001aa\u0001G\u0006aQ.\u001b7mSN,7m\u001c8eg\")A\u000e\u0004C\u0001[\u0006)bm\u001c:nCR$UO]1uS>tg+\u001a:c_N,GC\u0001*o\u0011\u0015y7\u000e1\u0001d\u0003\ti7\u000fC\u0003r\u0019\u0011\u0005!/\u0001\u0007g_Jl\u0017\r\u001e(v[\n,'\u000f\u0006\u0002Sg\")A\u000f\u001da\u0001k\u00069!/Z2pe\u0012\u001c\bC\u0001\tw\u0013\t9\u0018C\u0001\u0004E_V\u0014G.\u001a\u0005\u0006s2!\tA_\u0001\u0007k&\u0014vn\u001c;\u0016\u0003ICQ\u0001 \u0007\u0005\u0002u\fa\u0002\u001d:fa\u0016tGMQ1tKV\u0013\u0018\u000e\u0006\u0003S}\u0006\u0005\u0001bB@|!\u0003\u0005\rAU\u0001\tE\u0006\u001cX\rU1uQ\"A\u00111A>\u0011\u0002\u0003\u0007!+\u0001\u0005sKN|WO]2f\u0011\u001d\t9\u0001\u0004C\u0001\u0003\u0013\t\u0011cY8n[>t\u0007*Z1eKJtu\u000eZ3t+\t\tY\u0001\u0005\u0004\u0002\u000e\u0005u\u00111\u0005\b\u0005\u0003\u001f\tIB\u0004\u0003\u0002\u0012\u0005]QBAA\n\u0015\r\t)\"H\u0001\u0007yI|w\u000e\u001e \n\u0003II1!a\u0007\u0012\u0003\u001d\u0001\u0018mY6bO\u0016LA!a\b\u0002\"\t\u00191+Z9\u000b\u0007\u0005m\u0011\u0003\u0005\u0003\u0002&\u0005-RBAA\u0014\u0015\r\tI#E\u0001\u0004q6d\u0017\u0002BA\u0017\u0003O\u0011AAT8eK\"9\u0011\u0011\u0007\u0007\u0005\u0002\u0005%\u0011A\u0004<ju\"+\u0017\rZ3s\u001d>$Wm\u001d\u0005\b\u0003kaA\u0011AA\u0005\u0003U!\u0017\r^1UC\ndWm\u001d%fC\u0012,'OT8eKNDq!!\u000f\r\t\u0003\tY$A\biK\u0006$WM]*qCJ\\\u0007+Y4f)A\tY!!\u0010\u0002B\u0005-\u0013QKA3\u0003W\n)\bC\u0004\u0002@\u0005]\u0002\u0019\u0001*\u0002\u000bQLG\u000f\\3\t\u0013\u0005\r\u0013q\u0007CA\u0002\u0005\u0015\u0013aB2p]R,g\u000e\u001e\t\u0006!\u0005\u001d\u00131B\u0005\u0004\u0003\u0013\n\"\u0001\u0003\u001fcs:\fW.\u001a \t\u0011\u00055\u0013q\u0007a\u0001\u0003\u001f\n\u0011\"Y2uSZ,G+\u00192\u0011\u0007-\t\t&C\u0002\u0002T\t\u0011!b\u00159be.,\u0016\nV1c\u0011)\t9&a\u000e\u0011\u0002\u0003\u0007\u0011\u0011L\u0001\u0010e\u00164'/Z:i\u0013:$XM\u001d<bYB)\u0001#a\u0017\u0002`%\u0019\u0011QL\t\u0003\r=\u0003H/[8o!\r\u0001\u0012\u0011M\u0005\u0004\u0003G\n\"aA%oi\"Q\u0011qMA\u001c!\u0003\u0005\r!!\u001b\u0002\u0011!,G\u000e\u001d+fqR\u0004B\u0001EA.%\"Q\u0011QNA\u001c!\u0003\u0005\r!a\u001c\u0002#MDwn\u001e,jgV\fG.\u001b>bi&|g\u000eE\u0002\u0011\u0003cJ1!a\u001d\u0012\u0005\u001d\u0011un\u001c7fC:D!\"a\u001e\u00028A\u0005\t\u0019AA8\u00035)8/\u001a#bi\u0006$\u0016M\u00197fg\"9\u00111\u0010\u0007\u0005\u0002\u0005u\u0014A\u00042bg&\u001c7\u000b]1sWB\u000bw-\u001a\u000b\t\u0003\u0017\ty(!!\u0002\u0004\"I\u00111IA=\t\u0003\u0007\u0011Q\t\u0005\b\u0003\u007f\tI\b1\u0001S\u0011)\t9(!\u001f\u0011\u0002\u0003\u0007\u0011q\u000e\u0005\b\u0003\u000fcA\u0011AAE\u00031a\u0017n\u001d;j]\u001e$\u0016M\u00197f+\u0011\tY)!)\u0015%\u0005-\u0011QRAJ\u0003g\u000bi,!1\u0002F\u0006%\u0017Q\u001a\u0005\t\u0003\u001f\u000b)\t1\u0001\u0002\u0012\u00069\u0001.Z1eKJ\u001c\b#BA\u0007\u0003;\u0011\u0006\u0002CAK\u0003\u000b\u0003\r!a&\u0002\u001f\u001d,g.\u001a:bi\u0016$\u0015\r^1S_^\u0004r\u0001EAM\u0003;\u000bY!C\u0002\u0002\u001cF\u0011\u0011BR;oGRLwN\\\u0019\u0011\t\u0005}\u0015\u0011\u0015\u0007\u0001\t!\t\u0019+!\"C\u0002\u0005\u0015&!\u0001+\u0012\t\u0005\u001d\u0016Q\u0016\t\u0004!\u0005%\u0016bAAV#\t9aj\u001c;iS:<\u0007c\u0001\t\u00020&\u0019\u0011\u0011W\t\u0003\u0007\u0005s\u0017\u0010\u0003\u0005\u00026\u0006\u0015\u0005\u0019AA\\\u0003\u0011!\u0017\r^1\u0011\r\u00055\u0011\u0011XAO\u0013\u0011\tY,!\t\u0003\u0011%#XM]1cY\u0016D!\"a0\u0002\u0006B\u0005\t\u0019AA8\u0003)1\u0017\u000e_3e/&$G\u000f\u001b\u0005\u000b\u0003\u0007\f)\t%AA\u0002\u0005%\u0014AA5e\u0011)\t9-!\"\u0011\u0002\u0003\u0007\u0011\u0011S\u0001\u000eQ\u0016\fG-\u001a:DY\u0006\u001c8/Z:\t\u0015\u0005-\u0017Q\u0011I\u0001\u0002\u0004\ty'A\ttiJL\u0007/\u001a*poN<\u0016\u000e\u001e5DgND!\"a4\u0002\u0006B\u0005\t\u0019AA8\u0003!\u0019xN\u001d;bE2,\u0007bBAj\u0019\u0011\u0005\u0011Q[\u0001\u0010[\u0006\\W\r\u0015:pOJ,7o\u001d\"beRq\u00111BAl\u00037\fy.a9\u0002h\u0006E\b\u0002CAm\u0003#\u0004\r!a\u0018\u0002\u000fM$\u0018M\u001d;fI\"A\u0011Q\\Ai\u0001\u0004\ty&A\u0005d_6\u0004H.\u001a;fI\"A\u0011\u0011]Ai\u0001\u0004\ty&\u0001\u0004gC&dW\r\u001a\u0005\t\u0003K\f\t\u000e1\u0001\u0002`\u000591o[5qa\u0016$\u0007\u0002CAu\u0003#\u0004\r!a;\u0002#I,\u0017m]8o)>tU/\\&jY2,G\r\u0005\u0004T\u0003[\u0014\u0016qL\u0005\u0004\u0003_<&aA'ba\"A\u00111_Ai\u0001\u0004\ty&A\u0003u_R\fG\u000eC\u0004\u0002x2!\t!!?\u0002%MDwn\u001e#bOZK'PR8s'R\fw-\u001a\u000b\u0007\u0003\u0017\tY0a@\t\u0011\u0005u\u0018Q\u001fa\u0001\u0003?\nqa\u001d;bO\u0016LE\r\u0003\u0005\u0003\u0002\u0005U\b\u0019\u0001B\u0002\u0003\u00159'/\u00199i!\u0015\u0001\u00121\fB\u0003!\u0011\u00119A!\u0004\u000e\u0005\t%!b\u0001B\u0006\u0005\u0005)1oY8qK&!!q\u0002B\u0005\u0005E\u0011F\tR(qKJ\fG/[8o\u000fJ\f\u0007\u000f\u001b\u0005\b\u0005'aA\u0011\u0001B\u000b\u0003A\u0019\bn\\<EC\u001e4\u0016N\u001f$pe*{'\r\u0006\u0004\u0002\f\t]!1\u0004\u0005\t\u00053\u0011\t\u00021\u0001\u0002`\u0005)!n\u001c2JI\"A!Q\u0004B\t\u0001\u0004\u0011y\"\u0001\u0004he\u0006\u0004\bn\u001d\t\u0007\u0003\u001b\tiB!\u0002\t\u000f\t\rB\u0002\"\u0003\u0003&\u0005Q1\u000f[8x\t\u0006<g+\u001b>\u0015\r\u0005-!q\u0005B\u0015\u0011!\u0011iB!\tA\u0002\t}\u0001\u0002\u0003B\u0016\u0005C\u0001\r!a\u001c\u0002\r\u0019|'OS8c\u0011\u001d\u0011y\u0003\u0004C\u0001\u0005c\tq\u0001^8pYRL\u0007\u000f\u0006\u0004\u0002\f\tM\"Q\u0007\u0005\u0007\u0015\n5\u0002\u0019\u0001*\t\u000f\t]\"Q\u0006a\u0001%\u0006A\u0001o\\:ji&|g\u000eC\u0004\u0003<1!\tA!\u0010\u0002\u001f5\f7.\u001a#fg\u000e\u0014\u0018\u000e\u001d;j_:$\u0002Ba\u0010\u0003F\t%#Q\n\t\u0005\u0003K\u0011\t%\u0003\u0003\u0003D\u0005\u001d\"a\u0002(pI\u0016\u001cV-\u001d\u0005\b\u0005\u000f\u0012I\u00041\u0001S\u0003\u0011!Wm]2\t\u000f\t-#\u0011\ba\u0001%\u0006Y!-Y:f!\u0006$\b.\u0016:j\u0011)\u0011yE!\u000f\u0011\u0002\u0003\u0007\u0011qN\u0001\na2\f\u0017N\u001c+fqRDqAa\u0015\r\t\u0003\u0011)&\u0001\neK\u000e|G-Z+S\u0019B\u000b'/Y7fi\u0016\u0014Hc\u0001*\u0003X!9!\u0011\fB)\u0001\u0004\u0011\u0016\u0001C;sYB\u000b'/Y7\t\u000f\tuC\u0002\"\u0001\u0003`\u0005\tr-\u001a;US6,'l\u001c8f\u001f\u001a47/\u001a;\u0015\u0005\u0005}\u0003b\u0002B2\u0019\u0011\u0005!QM\u0001\t[\u0006\\W\r\u0013:fMR9!Ka\u001a\u0003l\t5\u0004\u0002\u0003B5\u0005C\u0002\r!a\u001c\u0002\u000bA\u0014x\u000e_=\t\u000f\u0005\r'\u0011\ra\u0001%\"9!q\u000eB1\u0001\u0004\u0011\u0016\u0001C8sS\u001eD%/\u001a4\t\u000f\tMD\u0002\"\u0001\u0003v\u0005A1\u000f\u001e:jab\u001b6\u000bF\u0002S\u0005oBqA!\u001f\u0003r\u0001\u0007!+\u0001\tsKF,Xm\u001d;QCJ\fW.\u001a;fe\"I!Q\u0010\u0007\u0012\u0002\u0013\u0005!qP\u0001\u0019aJ,\u0007/\u001a8e\u0005\u0006\u001cX-\u0016:jI\u0011,g-Y;mi\u0012\nTC\u0001BAU\r\u0011&1Q\u0016\u0003\u0005\u000b\u0003BAa\"\u0003\u00126\u0011!\u0011\u0012\u0006\u0005\u0005\u0017\u0013i)A\u0005v]\u000eDWmY6fI*\u0019!qR\t\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0003\u0014\n%%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I!q\u0013\u0007\u0012\u0002\u0013\u0005!qP\u0001\u0019aJ,\u0007/\u001a8e\u0005\u0006\u001cX-\u0016:jI\u0011,g-Y;mi\u0012\u0012\u0004\"\u0003BN\u0019E\u0005I\u0011\u0001BO\u0003a\u0011\u0017m]5d'B\f'o\u001b)bO\u0016$C-\u001a4bk2$HeM\u000b\u0003\u0005?SC!a\u001c\u0003\u0004\"I!1\u0015\u0007\u0012\u0002\u0013\u0005!QU\u0001\u0017Y&\u001cH/\u001b8h)\u0006\u0014G.\u001a\u0013eK\u001a\fW\u000f\u001c;%iU!!Q\u0014BT\t!\t\u0019K!)C\u0002\u0005\u0015\u0006\"\u0003BV\u0019E\u0005I\u0011\u0001BW\u0003Ya\u0017n\u001d;j]\u001e$\u0016M\u00197fI\u0011,g-Y;mi\u0012*T\u0003\u0002BX\u0005g+\"A!-+\t\u0005%$1\u0011\u0003\t\u0003G\u0013IK1\u0001\u0002&\"I!q\u0017\u0007\u0012\u0002\u0013\u0005!\u0011X\u0001\u0017Y&\u001cH/\u001b8h)\u0006\u0014G.\u001a\u0013eK\u001a\fW\u000f\u001c;%mU!!1\u0018B`+\t\u0011iL\u000b\u0003\u0002\u0012\n\rE\u0001CAR\u0005k\u0013\r!!*\t\u0013\t\rG\"%A\u0005\u0002\t\u0015\u0017A\u00067jgRLgn\u001a+bE2,G\u0005Z3gCVdG\u000fJ\u001c\u0016\t\tu%q\u0019\u0003\t\u0003G\u0013\tM1\u0001\u0002&\"I!1\u001a\u0007\u0012\u0002\u0013\u0005!QZ\u0001\u0017Y&\u001cH/\u001b8h)\u0006\u0014G.\u001a\u0013eK\u001a\fW\u000f\u001c;%qU!!Q\u0014Bh\t!\t\u0019K!3C\u0002\u0005\u0015\u0006\"\u0003Bj\u0019E\u0005I\u0011\u0001Bk\u0003eAW-\u00193feN\u0003\u0018M]6QC\u001e,G\u0005Z3gCVdG\u000f\n\u001b\u0016\u0005\t]'\u0006BA-\u0005\u0007C\u0011Ba7\r#\u0003%\tAa,\u00023!,\u0017\rZ3s'B\f'o\u001b)bO\u0016$C-\u001a4bk2$H%\u000e\u0005\n\u0005?d\u0011\u0013!C\u0001\u0005;\u000b\u0011\u0004[3bI\u0016\u00148\u000b]1sWB\u000bw-\u001a\u0013eK\u001a\fW\u000f\u001c;%m!I!1\u001d\u0007\u0012\u0002\u0013\u0005!QT\u0001\u001aQ\u0016\fG-\u001a:Ta\u0006\u00148\u000eU1hK\u0012\"WMZ1vYR$s\u0007C\u0005\u0003h2\t\n\u0011\"\u0001\u0003\u001e\u0006IR.Y6f\t\u0016\u001c8M]5qi&|g\u000e\n3fM\u0006,H\u000e\u001e\u00134\u0001"
)
public final class UIUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return UIUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return UIUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      UIUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return UIUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      UIUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      UIUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      UIUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      UIUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      UIUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      UIUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      UIUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      UIUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      UIUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      UIUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return UIUtils$.MODULE$.log();
   }

   public static String logName() {
      return UIUtils$.MODULE$.logName();
   }

   public static boolean makeDescription$default$3() {
      return UIUtils$.MODULE$.makeDescription$default$3();
   }

   public static boolean headerSparkPage$default$7() {
      return UIUtils$.MODULE$.headerSparkPage$default$7();
   }

   public static boolean headerSparkPage$default$6() {
      return UIUtils$.MODULE$.headerSparkPage$default$6();
   }

   public static Option<String> headerSparkPage$default$5() {
      return UIUtils$.MODULE$.headerSparkPage$default$5();
   }

   public static Option<Object> headerSparkPage$default$4() {
      return UIUtils$.MODULE$.headerSparkPage$default$4();
   }

   public static <T> boolean listingTable$default$8() {
      return UIUtils$.MODULE$.listingTable$default$8();
   }

   public static <T> boolean listingTable$default$7() {
      return UIUtils$.MODULE$.listingTable$default$7();
   }

   public static <T> Seq<String> listingTable$default$6() {
      return UIUtils$.MODULE$.listingTable$default$6();
   }

   public static <T> Option<String> listingTable$default$5() {
      return UIUtils$.MODULE$.listingTable$default$5();
   }

   public static <T> boolean listingTable$default$4() {
      return UIUtils$.MODULE$.listingTable$default$4();
   }

   public static boolean basicSparkPage$default$3() {
      return UIUtils$.MODULE$.basicSparkPage$default$3();
   }

   public static String prependBaseUri$default$2() {
      return UIUtils$.MODULE$.prependBaseUri$default$2();
   }

   public static String prependBaseUri$default$1() {
      return UIUtils$.MODULE$.prependBaseUri$default$1();
   }

   public static String stripXSS(String var0) {
      return UIUtils$.MODULE$.stripXSS(var0);
   }

   public static String makeHref(boolean var0, String var1, String var2) {
      return UIUtils$.MODULE$.makeHref(var0, var1, var2);
   }

   public static int getTimeZoneOffset() {
      return UIUtils$.MODULE$.getTimeZoneOffset();
   }

   public static String decodeURLParameter(String var0) {
      return UIUtils$.MODULE$.decodeURLParameter(var0);
   }

   public static NodeSeq makeDescription(String var0, String var1, boolean var2) {
      return UIUtils$.MODULE$.makeDescription(var0, var1, var2);
   }

   public static Seq<Node> tooltip(String var0, String var1) {
      return UIUtils$.MODULE$.tooltip(var0, var1);
   }

   public static Seq<Node> showDagVizForJob(int var0, Seq<RDDOperationGraph> var1) {
      return UIUtils$.MODULE$.showDagVizForJob(var0, var1);
   }

   public static Seq<Node> showDagVizForStage(int var0, Option<RDDOperationGraph> var1) {
      return UIUtils$.MODULE$.showDagVizForStage(var0, var1);
   }

   public static Seq<Node> makeProgressBar(int var0, int var1, int var2, int var3, Map<String, Object> var4, int var5) {
      return UIUtils$.MODULE$.makeProgressBar(var0, var1, var2, var3, var4, var5);
   }

   public static <T> Seq<Node> listingTable(Seq<String> var0, Function1<T, Seq<Node>> var1, Iterable<T> var2, boolean var3, Option<String> var4, Seq<String> var5, boolean var6, boolean var7) {
      return UIUtils$.MODULE$.listingTable(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static Seq<Node> basicSparkPage(Function0<Seq<Node>> var0, String var1, boolean var2) {
      return UIUtils$.MODULE$.basicSparkPage(var0, var1, var2);
   }

   public static Seq<Node> headerSparkPage(String var0, Function0<Seq<Node>> var1, SparkUITab var2, Option<Object> var3, Option<String> var4, boolean var5, boolean var6) {
      return UIUtils$.MODULE$.headerSparkPage(var0, var1, var2, var3, var4, var5, var6);
   }

   public static Seq<Node> dataTablesHeaderNodes() {
      return UIUtils$.MODULE$.dataTablesHeaderNodes();
   }

   public static Seq<Node> vizHeaderNodes() {
      return UIUtils$.MODULE$.vizHeaderNodes();
   }

   public static Seq<Node> commonHeaderNodes() {
      return UIUtils$.MODULE$.commonHeaderNodes();
   }

   public static String prependBaseUri(String var0, String var1) {
      return UIUtils$.MODULE$.prependBaseUri(var0, var1);
   }

   public static String uiRoot() {
      return UIUtils$.MODULE$.uiRoot();
   }

   public static String formatNumber(double var0) {
      return UIUtils$.MODULE$.formatNumber(var0);
   }

   public static String formatDurationVerbose(long var0) {
      return UIUtils$.MODULE$.formatDurationVerbose(var0);
   }

   public static String formatDuration(long var0) {
      return UIUtils$.MODULE$.formatDuration(var0);
   }

   public static String formatDate(long var0) {
      return UIUtils$.MODULE$.formatDate(var0);
   }

   public static String formatDate(Date var0) {
      return UIUtils$.MODULE$.formatDate(var0);
   }

   public static String TABLE_CLASS_STRIPED_SORTABLE() {
      return UIUtils$.MODULE$.TABLE_CLASS_STRIPED_SORTABLE();
   }

   public static String TABLE_CLASS_STRIPED() {
      return UIUtils$.MODULE$.TABLE_CLASS_STRIPED();
   }

   public static String TABLE_CLASS_NOT_STRIPED() {
      return UIUtils$.MODULE$.TABLE_CLASS_NOT_STRIPED();
   }
}
