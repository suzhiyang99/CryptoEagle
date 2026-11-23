package org.apache.spark;

import java.io.File;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.network.sasl.SecretKeyHolder;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.io.Files;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ObjectRef;
import scala.runtime.VolatileByteRef;

@ScalaSignature(
   bytes = "\u0006\u0001\t}f!B\u0001\u0003\u0001\tA!aD*fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\u000b\u0005\r!\u0011!B:qCJ\\'BA\u0003\u0007\u0003\u0019\t\u0007/Y2iK*\tq!A\u0002pe\u001e\u001cB\u0001A\u0005\u0010+A\u0011!\"D\u0007\u0002\u0017)\tA\"A\u0003tG\u0006d\u0017-\u0003\u0002\u000f\u0017\t1\u0011I\\=SK\u001a\u0004\"\u0001E\n\u000e\u0003EQ!A\u0005\u0002\u0002\u0011%tG/\u001a:oC2L!\u0001F\t\u0003\u000f1{wmZ5oOB\u0011acG\u0007\u0002/)\u0011\u0001$G\u0001\u0005g\u0006\u001cHN\u0003\u0002\u001b\u0005\u00059a.\u001a;x_J\\\u0017B\u0001\u000f\u0018\u0005=\u0019Vm\u0019:fi.+\u0017\u0010S8mI\u0016\u0014\b\u0002\u0003\u0010\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0011\u0002\u0013M\u0004\u0018M]6D_:47\u0001\u0001\t\u0003C\tj\u0011AA\u0005\u0003G\t\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011\u0015\u0002!Q1A\u0005\u0002\u0019\nq\"[8F]\u000e\u0014\u0018\u0010\u001d;j_:\\U-_\u000b\u0002OA\u0019!\u0002\u000b\u0016\n\u0005%Z!AB(qi&|g\u000eE\u0002\u000bW5J!\u0001L\u0006\u0003\u000b\u0005\u0013(/Y=\u0011\u0005)q\u0013BA\u0018\f\u0005\u0011\u0011\u0015\u0010^3\t\u0011E\u0002!\u0011!Q\u0001\n\u001d\n\u0001#[8F]\u000e\u0014\u0018\u0010\u001d;j_:\\U-\u001f\u0011\t\u000bM\u0002A\u0011\u0001\u001b\u0002\rqJg.\u001b;?)\r)dg\u000e\t\u0003C\u0001AQA\b\u001aA\u0002\u0001Bq!\n\u001a\u0011\u0002\u0003\u0007q\u0005C\u0004:\u0001\t\u0007I\u0011\u0002\u001e\u0002\u0019]KE\nR\"B%\u0012{\u0016i\u0011'\u0016\u0003m\u0002\"\u0001P!\u000e\u0003uR!AP \u0002\t1\fgn\u001a\u0006\u0002\u0001\u0006!!.\u0019<b\u0013\t\u0011UH\u0001\u0004TiJLgn\u001a\u0005\u0007\t\u0002\u0001\u000b\u0011B\u001e\u0002\u001b]KE\nR\"B%\u0012{\u0016i\u0011'!\u0011\u001d1\u0005A1A\u0005\n\u001d\u000ba!Y;uQ>sW#\u0001%\u0011\u0005)I\u0015B\u0001&\f\u0005\u001d\u0011un\u001c7fC:Da\u0001\u0014\u0001!\u0002\u0013A\u0015aB1vi\"|e\u000e\t\u0005\b\u001d\u0002\u0001\r\u0011\"\u0003H\u0003\u0019\t7\r\\:P]\"9\u0001\u000b\u0001a\u0001\n\u0013\t\u0016AC1dYN|en\u0018\u0013fcR\u0011!+\u0016\t\u0003\u0015MK!\u0001V\u0006\u0003\tUs\u0017\u000e\u001e\u0005\b->\u000b\t\u00111\u0001I\u0003\rAH%\r\u0005\u00071\u0002\u0001\u000b\u0015\u0002%\u0002\u000f\u0005\u001cGn](oA!9!\f\u0001a\u0001\n\u0013Y\u0016!C1e[&t\u0017i\u00197t+\u0005a\u0006cA/aG:\u0011!BX\u0005\u0003?.\ta\u0001\u0015:fI\u00164\u0017BA1c\u0005\r\u0019V\r\u001e\u0006\u0003?.\u0001\"!\u00183\n\u0005\t\u0013\u0007b\u00024\u0001\u0001\u0004%IaZ\u0001\u000eC\u0012l\u0017N\\!dYN|F%Z9\u0015\u0005IC\u0007b\u0002,f\u0003\u0003\u0005\r\u0001\u0018\u0005\u0007U\u0002\u0001\u000b\u0015\u0002/\u0002\u0015\u0005$W.\u001b8BG2\u001c\b\u0005C\u0004m\u0001\u0001\u0007I\u0011B.\u0002\u001f\u0005$W.\u001b8BG2\u001cxI]8vaNDqA\u001c\u0001A\u0002\u0013%q.A\nbI6Lg.Q2mg\u001e\u0013x.\u001e9t?\u0012*\u0017\u000f\u0006\u0002Sa\"9a+\\A\u0001\u0002\u0004a\u0006B\u0002:\u0001A\u0003&A,\u0001\tbI6Lg.Q2mg\u001e\u0013x.\u001e9tA!IA\u000f\u0001a\u0001\u0002\u0004%IaW\u0001\tm&,w/Q2mg\"Ia\u000f\u0001a\u0001\u0002\u0004%Ia^\u0001\rm&,w/Q2mg~#S-\u001d\u000b\u0003%bDqAV;\u0002\u0002\u0003\u0007A\f\u0003\u0004{\u0001\u0001\u0006K\u0001X\u0001\nm&,w/Q2mg\u0002B\u0011\u0002 \u0001A\u0002\u0003\u0007I\u0011B.\u0002\u001dYLWm^!dYN<%o\\;qg\"Ia\u0010\u0001a\u0001\u0002\u0004%Ia`\u0001\u0013m&,w/Q2mg\u001e\u0013x.\u001e9t?\u0012*\u0017\u000fF\u0002S\u0003\u0003AqAV?\u0002\u0002\u0003\u0007A\fC\u0004\u0002\u0006\u0001\u0001\u000b\u0015\u0002/\u0002\u001fYLWm^!dYN<%o\\;qg\u0002B!\"!\u0003\u0001\u0001\u0004\u0005\r\u0011\"\u0003\\\u0003)iw\u000eZ5gs\u0006\u001bGn\u001d\u0005\f\u0003\u001b\u0001\u0001\u0019!a\u0001\n\u0013\ty!\u0001\bn_\u0012Lg-_!dYN|F%Z9\u0015\u0007I\u000b\t\u0002\u0003\u0005W\u0003\u0017\t\t\u00111\u0001]\u0011\u001d\t)\u0002\u0001Q!\nq\u000b1\"\\8eS\u001aL\u0018i\u00197tA!Q\u0011\u0011\u0004\u0001A\u0002\u0003\u0007I\u0011B.\u0002!5|G-\u001b4z\u0003\u000ed7o\u0012:pkB\u001c\bbCA\u000f\u0001\u0001\u0007\t\u0019!C\u0005\u0003?\tA#\\8eS\u001aL\u0018i\u00197t\u000fJ|W\u000f]:`I\u0015\fHc\u0001*\u0002\"!Aa+a\u0007\u0002\u0002\u0003\u0007A\fC\u0004\u0002&\u0001\u0001\u000b\u0015\u0002/\u0002#5|G-\u001b4z\u0003\u000ed7o\u0012:pkB\u001c\b\u0005C\u0005\u0002*\u0001\u0011\r\u0011\"\u0003\u0002,\u0005yA-\u001a4bk2$\u0018i\u00197Vg\u0016\u00148/\u0006\u0002\u0002.A)\u0011qFA\u001dG6\u0011\u0011\u0011\u0007\u0006\u0005\u0003g\t)$A\u0005j[6,H/\u00192mK*\u0019\u0011qG\u0006\u0002\u0015\r|G\u000e\\3di&|g.C\u0002b\u0003cA\u0001\"!\u0010\u0001A\u0003%\u0011QF\u0001\u0011I\u00164\u0017-\u001e7u\u0003\u000edWk]3sg\u0002B\u0011\"!\u0011\u0001\u0005\u0004%I!a\u0011\u0002#\u0011,g-Y;miN\u001bFj\u00149uS>t7/\u0006\u0002\u0002FA\u0019\u0011%a\u0012\n\u0007\u0005%#A\u0001\u0006T'2{\u0005\u000f^5p]ND\u0001\"!\u0014\u0001A\u0003%\u0011QI\u0001\u0013I\u00164\u0017-\u001e7u'Ncu\n\u001d;j_:\u001c\b\u0005C\u0005\u0002R\u0001\u0011\r\u0011\"\u0001\u0002D\u0005!b-\u001b7f'\u0016\u0014h/\u001a:T'2{\u0005\u000f^5p]ND\u0001\"!\u0016\u0001A\u0003%\u0011QI\u0001\u0016M&dWmU3sm\u0016\u00148k\u0015'PaRLwN\\:!\u0011-1\u0006\u0001%A\u0001\u0004\u0003\u0006I!!\u0017\u0011\u000f)\tY&a\u0018\u0002v%\u0019\u0011QL\u0006\u0003\rQ+\b\u000f\\33!\u0011Q\u0001&!\u0019\u0011\t\u0005\r\u0014\u0011O\u0007\u0003\u0003KRA!a\u001a\u0002j\u0005\u00191o\u001d7\u000b\t\u0005-\u0014QN\u0001\u0004]\u0016$(BAA8\u0003\u0015Q\u0017M^1y\u0013\u0011\t\u0019(!\u001a\u0003!M\u001bFjU8dW\u0016$h)Y2u_JL\b\u0003\u0002\u0006)\u0003o\u0012b!!\u001f\u0002\u0006\u0006-eaBA>\u0003{\u0002\u0011q\u000f\u0002\ryI,g-\u001b8f[\u0016tGO\u0010\u0005\b\u0003\u007f\n\t\tAA<\u00031Awn\u001d;WKJLg-[3s\u0011)\t\u0019)a\u0016\u0002\u0002\u0003\u0005\u0015\u0011L\u0001\u0003qF\u00022\u0001PAD\u0013\r\tI)\u0010\u0002\u0007\u001f\nTWm\u0019;\u0011\t\u0005\r\u0014QR\u0005\u0005\u0003\u001f\u000b)G\u0001\tI_N$h.Y7f-\u0016\u0014\u0018NZ5fe\"I\u00111\u0013\u0001C\u0002\u0013\u0005\u0011QS\u0001\u0011gNd7k\\2lKR4\u0015m\u0019;pef,\"!a\u0018\t\u0011\u0005e\u0005\u0001)A\u0005\u0003?\n\u0011c]:m'>\u001c7.\u001a;GC\u000e$xN]=!\u0011%\ti\n\u0001b\u0001\n\u0003\ty*\u0001\ti_N$h.Y7f-\u0016\u0014\u0018NZ5feV\u0011\u0011Q\u000f\u0005\t\u0003G\u0003\u0001\u0015!\u0003\u0002v\u0005\t\u0002n\\:u]\u0006lWMV3sS\u001aLWM\u001d\u0011\t\u000f\u0005\u001d\u0006\u0001\"\u0001\u0002*\u0006iq-\u001a;T'2{\u0005\u000f^5p]N$B!!\u0012\u0002,\"9\u0011QVAS\u0001\u0004\u0019\u0017AB7pIVdW\rC\u0004\u00022\u0002!I!a-\u0002\u0017M$(/\u001b8h)>\u001cV\r\u001e\u000b\u00049\u0006U\u0006bBA\\\u0003_\u0003\raY\u0001\u0005Y&\u001cH\u000fC\u0004\u0002<\u0002!\t!!0\u0002\u0017M,GOV5fo\u0006\u001bGn\u001d\u000b\u0006%\u0006}\u00161\u0019\u0005\b\u0003\u0003\fI\f1\u0001]\u00031!WMZ1vYR,6/\u001a:t\u0011\u001d\t)-!/A\u0002\r\fA\"\u00197m_^,G-V:feNDq!a/\u0001\t\u0003\tI\rF\u0003S\u0003\u0017\fy\rC\u0004\u0002N\u0006\u001d\u0007\u0019A2\u0002\u0017\u0011,g-Y;miV\u001bXM\u001d\u0005\b\u0003\u000b\f9\r1\u0001d\u0011\u001d\t\u0019\u000e\u0001C\u0001\u0003+\f\u0011c]3u-&,w/Q2mg\u001e\u0013x.\u001e9t)\r\u0011\u0016q\u001b\u0005\b\u00033\f\t\u000e1\u0001d\u0003E\tG\u000e\\8xK\u0012,6/\u001a:He>,\bo\u001d\u0005\b\u0003;\u0004A\u0011AAp\u0003-9W\r\u001e,jK^\f5\r\\:\u0016\u0003\rDq!a9\u0001\t\u0003\ty.A\thKR4\u0016.Z<BG2\u001cxI]8vaNDq!a:\u0001\t\u0003\tI/A\u0007tKRlu\u000eZ5gs\u0006\u001bGn\u001d\u000b\u0006%\u0006-\u0018Q\u001e\u0005\b\u0003\u0003\f)\u000f1\u0001]\u0011\u001d\t)-!:A\u0002\rDq!!=\u0001\t\u0003\t\u00190A\ntKRlu\u000eZ5gs\u0006\u001bGn]$s_V\u00048\u000fF\u0002S\u0003kDq!!7\u0002p\u0002\u00071\rC\u0004\u0002z\u0002!\t!a8\u0002\u001b\u001d,G/T8eS\u001aL\u0018i\u00197t\u0011\u001d\ti\u0010\u0001C\u0001\u0003?\f1cZ3u\u001b>$\u0017NZ=BG2\u001cxI]8vaNDqA!\u0001\u0001\t\u0003\u0011\u0019!\u0001\u0007tKR\fE-\\5o\u0003\u000ed7\u000fF\u0002S\u0005\u000bAqAa\u0002\u0002\u0000\u0002\u00071-\u0001\u0006bI6Lg.V:feNDqAa\u0003\u0001\t\u0003\u0011i!\u0001\ntKR\fE-\\5o\u0003\u000ed7o\u0012:pkB\u001cHc\u0001*\u0003\u0010!9!\u0011\u0003B\u0005\u0001\u0004\u0019\u0017aD1e[&tWk]3s\u000fJ|W\u000f]:\t\u000f\tU\u0001\u0001\"\u0001\u0003\u0018\u000591/\u001a;BG2\u001cHc\u0001*\u0003\u001a!9!1\u0004B\n\u0001\u0004A\u0015AC1dYN+G\u000f^5oO\"9!q\u0004\u0001\u0005\u0002\t\u0005\u0012AE4fi&{UI\\2ssB$\u0018n\u001c8LKf$\u0012a\n\u0005\b\u0005K\u0001A\u0011\u0001B\u0014\u0003-\t7\r\\:F]\u0006\u0014G.\u001a3\u0015\u0003!CqAa\u000b\u0001\t\u0003\u0011i#\u0001\fdQ\u0016\u001c7.V%WS\u0016<\b+\u001a:nSN\u001c\u0018n\u001c8t)\rA%q\u0006\u0005\b\u0005c\u0011I\u00031\u0001d\u0003\u0011)8/\u001a:\t\u000f\tU\u0002\u0001\"\u0001\u00038\u000512\r[3dW6{G-\u001b4z!\u0016\u0014X.[:tS>t7\u000fF\u0002I\u0005sAqA!\r\u00034\u0001\u00071\rC\u0004\u0003>\u0001!\tAa\n\u0002/%\u001c\u0018)\u001e;iK:$\u0018nY1uS>tWI\\1cY\u0016$\u0007b\u0002B!\u0001\u0011\u0005!qE\u0001\u0014SN,en\u0019:zaRLwN\\#oC\ndW\r\u001a\u0005\b\u0005\u000b\u0002A\u0011\u0001B$\u0003-9W\r\u001e%uiB,6/\u001a:\u0015\u0003\rDqAa\u0013\u0001\t\u0003\u00119%A\u0006hKR\u001c\u0016m\u001d7Vg\u0016\u0014\bb\u0002B(\u0001\u0011\u0005!qI\u0001\rO\u0016$8+Z2sKR\\U-\u001f\u0005\b\u0005'\u0002A\u0011\u0001B+\u00039Ig.\u001b;jC2L'0Z!vi\"$\u0012A\u0015\u0005\b\u0005\u0017\u0002A\u0011\tB-)\r\u0019'1\f\u0005\b\u0005;\u00129\u00061\u0001d\u0003\u0015\t\u0007\u000f]%e\u0011\u001d\u0011y\u0005\u0001C!\u0005C\"2a\u0019B2\u0011\u001d\u0011iFa\u0018A\u0002\r<\u0001Ba\u001a\u0003\u0011\u0003\u0011!\u0011N\u0001\u0010'\u0016\u001cWO]5us6\u000bg.Y4feB\u0019\u0011Ea\u001b\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0002\u0003nM\u0019!1N\u0005\t\u000fM\u0012Y\u0007\"\u0001\u0003rQ\u0011!\u0011\u000e\u0005\u000b\u0005k\u0012YG1A\u0005\u0002\u0005}\u0017aD*Q\u0003J[u,Q+U\u0011~\u001buJ\u0014$\t\u0011\te$1\u000eQ\u0001\n\r\f\u0001c\u0015)B%.{\u0016)\u0016+I?\u000e{eJ\u0012\u0011\t\u0013\tu$1\u000eb\u0001\n\u0003Q\u0014AF*Q\u0003J[u,Q+U\u0011~\u001bVi\u0011*F)~\u001buJ\u0014$\t\u0011\t\u0005%1\u000eQ\u0001\nm\nqc\u0015)B%.{\u0016)\u0016+I?N+5IU#U?\u000e{eJ\u0012\u0011\t\u0013\t\u0015%1\u000eb\u0001\n\u0003Q\u0014aD#O-~\u000bU\u000b\u0016%`'\u0016\u001b%+\u0012+\t\u0011\t%%1\u000eQ\u0001\nm\n\u0001#\u0012(W?\u0006+F\u000bS0T\u000b\u000e\u0013V\t\u0016\u0011\t\u0015\t5%1\u000eb\u0001\n\u0003\u0011y)A\tT\u000b\u000e\u0013V\tV0M\u001f>[U\u000bU0L\u000bf+\"A!%\u0011\t\tM%QT\u0007\u0003\u0005+SAAa&\u0003\u001a\u0006\u0011\u0011n\u001c\u0006\u0004\u00057#\u0011A\u00025bI>|\u0007/\u0003\u0003\u0003 \nU%\u0001\u0002+fqRD\u0011Ba)\u0003l\u0001\u0006IA!%\u0002%M+5IU#U?2{ujS+Q?.+\u0015\f\t\u0005\u000b\u0005O\u0013Y'%A\u0005\u0002\t%\u0016a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$#'\u0006\u0002\u0003,*\u001aqE!,,\u0005\t=\u0006\u0003\u0002BY\u0005wk!Aa-\u000b\t\tU&qW\u0001\nk:\u001c\u0007.Z2lK\u0012T1A!/\f\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005{\u0013\u0019LA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016\u0004"
)
public class SecurityManager implements Logging, SecretKeyHolder {
   public final SparkConf org$apache$spark$SecurityManager$$sparkConf;
   private final Option<byte[]> ioEncryptionKey;
   private final String WILDCARD_ACL;
   private final boolean org$apache$spark$SecurityManager$$authOn;
   private boolean org$apache$spark$SecurityManager$$aclsOn;
   private Set<String> org$apache$spark$SecurityManager$$adminAcls;
   private Set<String> org$apache$spark$SecurityManager$$adminAclsGroups;
   private Set<String> org$apache$spark$SecurityManager$$viewAcls;
   private Set<String> org$apache$spark$SecurityManager$$viewAclsGroups;
   private Set<String> org$apache$spark$SecurityManager$$modifyAcls;
   private Set<String> org$apache$spark$SecurityManager$$modifyAclsGroups;
   private final Set<String> defaultAclUsers;
   private final SSLOptions defaultSSLOptions;
   private final SSLOptions fileServerSSLOptions;
   // $FF: synthetic field
   private final Tuple2 x$1;
   private final Option<SSLSocketFactory> sslSocketFactory;
   private final Option<Object> hostnameVerifier;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<byte[]> $lessinit$greater$default$2() {
      return SecurityManager$.MODULE$.$lessinit$greater$default$2();
   }

   public static Text SECRET_LOOKUP_KEY() {
      return SecurityManager$.MODULE$.SECRET_LOOKUP_KEY();
   }

   public static String ENV_AUTH_SECRET() {
      return SecurityManager$.MODULE$.ENV_AUTH_SECRET();
   }

   public static String SPARK_AUTH_SECRET_CONF() {
      return SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF();
   }

   public static String SPARK_AUTH_CONF() {
      return SecurityManager$.MODULE$.SPARK_AUTH_CONF();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   public Option<byte[]> ioEncryptionKey() {
      return this.ioEncryptionKey;
   }

   private String WILDCARD_ACL() {
      return this.WILDCARD_ACL;
   }

   public boolean org$apache$spark$SecurityManager$$authOn() {
      return this.org$apache$spark$SecurityManager$$authOn;
   }

   public boolean org$apache$spark$SecurityManager$$aclsOn() {
      return this.org$apache$spark$SecurityManager$$aclsOn;
   }

   private void org$apache$spark$SecurityManager$$aclsOn_$eq(boolean x$1) {
      this.org$apache$spark$SecurityManager$$aclsOn = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$adminAcls() {
      return this.org$apache$spark$SecurityManager$$adminAcls;
   }

   private void org$apache$spark$SecurityManager$$adminAcls_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$adminAcls = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$adminAclsGroups() {
      return this.org$apache$spark$SecurityManager$$adminAclsGroups;
   }

   private void org$apache$spark$SecurityManager$$adminAclsGroups_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$adminAclsGroups = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$viewAcls() {
      return this.org$apache$spark$SecurityManager$$viewAcls;
   }

   private void org$apache$spark$SecurityManager$$viewAcls_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$viewAcls = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$viewAclsGroups() {
      return this.org$apache$spark$SecurityManager$$viewAclsGroups;
   }

   private void org$apache$spark$SecurityManager$$viewAclsGroups_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$viewAclsGroups = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$modifyAcls() {
      return this.org$apache$spark$SecurityManager$$modifyAcls;
   }

   private void org$apache$spark$SecurityManager$$modifyAcls_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$modifyAcls = x$1;
   }

   public Set<String> org$apache$spark$SecurityManager$$modifyAclsGroups() {
      return this.org$apache$spark$SecurityManager$$modifyAclsGroups;
   }

   private void org$apache$spark$SecurityManager$$modifyAclsGroups_$eq(Set<String> x$1) {
      this.org$apache$spark$SecurityManager$$modifyAclsGroups = x$1;
   }

   private Set<String> defaultAclUsers() {
      return this.defaultAclUsers;
   }

   private SSLOptions defaultSSLOptions() {
      return this.defaultSSLOptions;
   }

   public SSLOptions fileServerSSLOptions() {
      return this.fileServerSSLOptions;
   }

   public Option<SSLSocketFactory> sslSocketFactory() {
      return this.sslSocketFactory;
   }

   public Option<Object> hostnameVerifier() {
      return this.hostnameVerifier;
   }

   public SSLOptions getSSLOptions(String module) {
      SSLOptions opts = SSLOptions$.MODULE$.parse(this.org$apache$spark$SecurityManager$$sparkConf, (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"spark.ssl.", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{module})), new Some(this.defaultSSLOptions()));
      this.logDebug(new Serializable(this, module, opts) {
         public static final long serialVersionUID = 0L;
         private final String module$1;
         private final SSLOptions opts$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Created SSL options for ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.module$1, this.opts$1}));
         }

         public {
            this.module$1 = module$1;
            this.opts$1 = opts$1;
         }
      });
      return opts;
   }

   private Set<String> stringToSet(String list) {
      return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])(new StringOps(Predef$.MODULE$.augmentString(list))).split(',')).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$2) {
            return x$2.trim();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String x$3) {
            return !x$3.isEmpty();
         }
      })).toSet();
   }

   public void setViewAcls(Set<String> defaultUsers, String allowedUsers) {
      this.org$apache$spark$SecurityManager$$viewAcls_$eq((Set)this.org$apache$spark$SecurityManager$$adminAcls().$plus$plus(defaultUsers).$plus$plus(this.stringToSet(allowedUsers)));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing view acls to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAcls().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void setViewAcls(String defaultUser, String allowedUsers) {
      this.setViewAcls((Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{defaultUser}))), allowedUsers);
   }

   public void setViewAclsGroups(String allowedUserGroups) {
      this.org$apache$spark$SecurityManager$$viewAclsGroups_$eq((Set)this.org$apache$spark$SecurityManager$$adminAclsGroups().$plus$plus(this.stringToSet(allowedUserGroups)));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing view acls groups to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAclsGroups().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public String getViewAcls() {
      return this.org$apache$spark$SecurityManager$$viewAcls().contains(this.WILDCARD_ACL()) ? this.WILDCARD_ACL() : this.org$apache$spark$SecurityManager$$viewAcls().mkString(",");
   }

   public String getViewAclsGroups() {
      return this.org$apache$spark$SecurityManager$$viewAclsGroups().contains(this.WILDCARD_ACL()) ? this.WILDCARD_ACL() : this.org$apache$spark$SecurityManager$$viewAclsGroups().mkString(",");
   }

   public void setModifyAcls(Set<String> defaultUsers, String allowedUsers) {
      this.org$apache$spark$SecurityManager$$modifyAcls_$eq((Set)this.org$apache$spark$SecurityManager$$adminAcls().$plus$plus(defaultUsers).$plus$plus(this.stringToSet(allowedUsers)));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing modify acls to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAcls().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void setModifyAclsGroups(String allowedUserGroups) {
      this.org$apache$spark$SecurityManager$$modifyAclsGroups_$eq((Set)this.org$apache$spark$SecurityManager$$adminAclsGroups().$plus$plus(this.stringToSet(allowedUserGroups)));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing modify acls groups to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAclsGroups().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public String getModifyAcls() {
      return this.org$apache$spark$SecurityManager$$modifyAcls().contains(this.WILDCARD_ACL()) ? this.WILDCARD_ACL() : this.org$apache$spark$SecurityManager$$modifyAcls().mkString(",");
   }

   public String getModifyAclsGroups() {
      return this.org$apache$spark$SecurityManager$$modifyAclsGroups().contains(this.WILDCARD_ACL()) ? this.WILDCARD_ACL() : this.org$apache$spark$SecurityManager$$modifyAclsGroups().mkString(",");
   }

   public void setAdminAcls(String adminUsers) {
      this.org$apache$spark$SecurityManager$$adminAcls_$eq(this.stringToSet(adminUsers));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing admin acls to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$adminAcls().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void setAdminAclsGroups(String adminUserGroups) {
      this.org$apache$spark$SecurityManager$$adminAclsGroups_$eq(this.stringToSet(adminUserGroups));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing admin acls groups to: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$adminAclsGroups().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void setAcls(boolean aclSetting) {
      this.org$apache$spark$SecurityManager$$aclsOn_$eq(aclSetting);
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Changing acls enabled to: ").append((Object)BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$SecurityManager$$aclsOn())).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public Option<byte[]> getIOEncryptionKey() {
      return this.ioEncryptionKey();
   }

   public boolean aclsEnabled() {
      return this.org$apache$spark$SecurityManager$$aclsOn();
   }

   public boolean checkUIViewPermissions(String user) {
      this.logDebug(new Serializable(this, user) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;
         private final String user$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"user=").append((Object)this.user$1).append((Object)" aclsEnabled=").append((Object)BoxesRunTime.boxToBoolean(this.$outer.aclsEnabled())).append((Object)" viewAcls=").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAcls().mkString(",")).append((Object)" viewAclsGroups=").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAclsGroups().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.user$1 = user$1;
            }
         }
      });
      if (this.aclsEnabled() && user != null && !this.org$apache$spark$SecurityManager$$viewAcls().contains(user) && !this.org$apache$spark$SecurityManager$$viewAcls().contains(this.WILDCARD_ACL()) && !this.org$apache$spark$SecurityManager$$viewAclsGroups().contains(this.WILDCARD_ACL())) {
         Set currentUserGroups = Utils$.MODULE$.getCurrentUserGroups(this.org$apache$spark$SecurityManager$$sparkConf, user);
         this.logDebug(new Serializable(this, currentUserGroups) {
            public static final long serialVersionUID = 0L;
            private final Set currentUserGroups$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"userGroups=").append((Object)this.currentUserGroups$1.mkString(",")).toString();
            }

            public {
               this.currentUserGroups$1 = currentUserGroups$1;
            }
         });
         return this.org$apache$spark$SecurityManager$$viewAclsGroups().exists(new Serializable(this, currentUserGroups) {
            public static final long serialVersionUID = 0L;
            private final Set currentUserGroups$1;

            public final boolean apply(String x$4) {
               return this.currentUserGroups$1.contains(x$4);
            }

            public {
               this.currentUserGroups$1 = currentUserGroups$1;
            }
         });
      } else {
         return true;
      }
   }

   public boolean checkModifyPermissions(String user) {
      this.logDebug(new Serializable(this, user) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;
         private final String user$2;

         public final String apply() {
            return (new StringBuilder()).append((Object)"user=").append((Object)this.user$2).append((Object)" aclsEnabled=").append((Object)BoxesRunTime.boxToBoolean(this.$outer.aclsEnabled())).append((Object)" modifyAcls=").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAcls().mkString(",")).append((Object)" modifyAclsGroups=").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAclsGroups().mkString(",")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.user$2 = user$2;
            }
         }
      });
      if (this.aclsEnabled() && user != null && !this.org$apache$spark$SecurityManager$$modifyAcls().contains(user) && !this.org$apache$spark$SecurityManager$$modifyAcls().contains(this.WILDCARD_ACL()) && !this.org$apache$spark$SecurityManager$$modifyAclsGroups().contains(this.WILDCARD_ACL())) {
         Set currentUserGroups = Utils$.MODULE$.getCurrentUserGroups(this.org$apache$spark$SecurityManager$$sparkConf, user);
         this.logDebug(new Serializable(this, currentUserGroups) {
            public static final long serialVersionUID = 0L;
            private final Set currentUserGroups$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"userGroups=").append((Object)this.currentUserGroups$2).toString();
            }

            public {
               this.currentUserGroups$2 = currentUserGroups$2;
            }
         });
         return this.org$apache$spark$SecurityManager$$modifyAclsGroups().exists(new Serializable(this, currentUserGroups) {
            public static final long serialVersionUID = 0L;
            private final Set currentUserGroups$2;

            public final boolean apply(String x$5) {
               return this.currentUserGroups$2.contains(x$5);
            }

            public {
               this.currentUserGroups$2 = currentUserGroups$2;
            }
         });
      } else {
         return true;
      }
   }

   public boolean isAuthenticationEnabled() {
      return this.org$apache$spark$SecurityManager$$authOn();
   }

   public boolean isEncryptionEnabled() {
      return BoxesRunTime.unboxToBoolean(this.org$apache$spark$SecurityManager$$sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.NETWORK_ENCRYPTION_ENABLED())) || BoxesRunTime.unboxToBoolean(this.org$apache$spark$SecurityManager$$sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.SASL_ENCRYPTION_ENABLED()));
   }

   public String getHttpUser() {
      return "sparkHttpUser";
   }

   public String getSaslUser() {
      return "sparkSaslUser";
   }

   public String getSecretKey() {
      String var10000;
      if (this.isAuthenticationEnabled()) {
         Credentials creds = UserGroupInformation.getCurrentUser().getCredentials();
         var10000 = (String)Option$.MODULE$.apply(creds.getSecretKey(SecurityManager$.MODULE$.SECRET_LOOKUP_KEY())).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(byte[] bytes) {
               return new String(bytes, StandardCharsets.UTF_8);
            }
         }).orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SecurityManager $outer;

            public final Option<String> apply() {
               return Option$.MODULE$.<String>apply(this.$outer.org$apache$spark$SecurityManager$$sparkConf.getenv(SecurityManager$.MODULE$.ENV_AUTH_SECRET()));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SecurityManager $outer;

            public final Option<String> apply() {
               return this.$outer.org$apache$spark$SecurityManager$$sparkConf.getOption(SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF());
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Nothing$ apply() {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"A secret key must be specified via the ", " config"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF()})));
            }
         });
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public void initializeAuth() {
      if (BoxesRunTime.unboxToBoolean(this.org$apache$spark$SecurityManager$$sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.NETWORK_AUTH_ENABLED()))) {
         label18: {
            String var10000 = this.org$apache$spark$SecurityManager$$sparkConf.get("spark.master", (String)null);
            String var1 = "yarn";
            if (var10000 == null) {
               if (var1 == null) {
                  break label18;
               }
            } else if (var10000.equals(var1)) {
               break label18;
            }

            Predef$.MODULE$.require(this.org$apache$spark$SecurityManager$$sparkConf.contains(SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF()), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"A secret key must be specified via the ", " config."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{SecurityManager$.MODULE$.SPARK_AUTH_SECRET_CONF()}));
               }
            });
            return;
         }

         SecureRandom rnd = new SecureRandom();
         int length = this.org$apache$spark$SecurityManager$$sparkConf.getInt("spark.authenticate.secretBitLength", 256) / 8;
         byte[] secretBytes = new byte[length];
         rnd.nextBytes(secretBytes);
         Credentials creds = new Credentials();
         creds.addSecretKey(SecurityManager$.MODULE$.SECRET_LOOKUP_KEY(), secretBytes);
         UserGroupInformation.getCurrentUser().addCredentials(creds);
      }
   }

   public String getSaslUser(String appId) {
      return this.getSaslUser();
   }

   public String getSecretKey(String appId) {
      return this.getSecretKey();
   }

   private final TrustManager[] credulousTrustStoreManagers$lzycompute$1(ObjectRef credulousTrustStoreManagers$lzy$1, VolatileByteRef bitmap$0$1) {
      synchronized(this) {
         if ((byte)(bitmap$0$1.elem & 1) == 0) {
            TrustManager[] var10001 = new TrustManager[1];
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Using 'accept-all' trust manager for SSL connections.";
               }
            });
            var10001[0] = new X509TrustManager(this) {
               public X509Certificate[] getAcceptedIssuers() {
                  return null;
               }

               public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
               }

               public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
               }
            };
            credulousTrustStoreManagers$lzy$1.elem = (T)((TrustManager[])((Object[])var10001));
            bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return (TrustManager[])credulousTrustStoreManagers$lzy$1.elem;
   }

   public final TrustManager[] org$apache$spark$SecurityManager$$credulousTrustStoreManagers$1(ObjectRef credulousTrustStoreManagers$lzy$1, VolatileByteRef bitmap$0$1) {
      return (byte)(bitmap$0$1.elem & 1) == 0 ? this.credulousTrustStoreManagers$lzycompute$1(credulousTrustStoreManagers$lzy$1, bitmap$0$1) : (TrustManager[])credulousTrustStoreManagers$lzy$1.elem;
   }

   public SecurityManager(SparkConf sparkConf, Option<byte[]> ioEncryptionKey) {
      this.org$apache$spark$SecurityManager$$sparkConf = sparkConf;
      this.ioEncryptionKey = ioEncryptionKey;
      Logging$class.$init$(this);
      this.WILDCARD_ACL = "*";
      this.org$apache$spark$SecurityManager$$authOn = BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.NETWORK_AUTH_ENABLED()));
      this.org$apache$spark$SecurityManager$$aclsOn = sparkConf.getBoolean("spark.acls.enable", sparkConf.getBoolean("spark.ui.acls.enable", false));
      this.org$apache$spark$SecurityManager$$adminAcls = this.stringToSet(sparkConf.get("spark.admin.acls", ""));
      this.org$apache$spark$SecurityManager$$adminAclsGroups = this.stringToSet(sparkConf.get("spark.admin.acls.groups", ""));
      this.defaultAclUsers = (Set)Predef$.MODULE$.Set().apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{System.getProperty("user.name", ""), Utils$.MODULE$.getCurrentUserName()})));
      this.setViewAcls(this.defaultAclUsers(), sparkConf.get("spark.ui.view.acls", ""));
      this.setModifyAcls(this.defaultAclUsers(), sparkConf.get("spark.modify.acls", ""));
      this.setViewAclsGroups(sparkConf.get("spark.ui.view.acls.groups", ""));
      this.setModifyAclsGroups(sparkConf.get("spark.modify.acls.groups", ""));
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SecurityManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"SecurityManager: authentication ").append((Object)(this.$outer.org$apache$spark$SecurityManager$$authOn() ? "enabled" : "disabled")).append((Object)"; ui acls ").append((Object)(this.$outer.org$apache$spark$SecurityManager$$aclsOn() ? "enabled" : "disabled")).append((Object)"; users  with view permissions: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAcls().toString()).append((Object)"; groups with view permissions: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$viewAclsGroups().toString()).append((Object)"; users  with modify permissions: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAcls().toString()).append((Object)"; groups with modify permissions: ").append((Object)this.$outer.org$apache$spark$SecurityManager$$modifyAclsGroups().toString()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      if (this.org$apache$spark$SecurityManager$$authOn()) {
         Authenticator.setDefault(new Authenticator(this) {
            public PasswordAuthentication getPasswordAuthentication() {
               PasswordAuthentication passAuth = null;
               String userInfo = this.getRequestingURL().getUserInfo();
               if (userInfo != null) {
                  String[] parts = userInfo.split(":", 2);
                  passAuth = new PasswordAuthentication(parts[0], parts[1].toCharArray());
               }

               return passAuth;
            }
         });
      }

      this.defaultSSLOptions = SSLOptions$.MODULE$.parse(sparkConf, "spark.ssl", None$.MODULE$);
      this.fileServerSSLOptions = this.getSSLOptions("fs");
      VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
      Tuple2 var10001;
      if (this.fileServerSSLOptions().enabled()) {
         ObjectRef credulousTrustStoreManagers$lzy = ObjectRef.zero();
         Option trustStoreManagers = this.fileServerSSLOptions().trustStore().map(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SecurityManager $outer;

            public final TrustManager[] apply(File trustStore) {
               InputStream input = Files.asByteSource((File)this.$outer.fileServerSSLOptions().trustStore().get()).openStream();

               TrustManager[] var10000;
               try {
                  KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                  ks.load(input, ((String)this.$outer.fileServerSSLOptions().trustStorePassword().get()).toCharArray());
                  TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                  tmf.init(ks);
                  var10000 = tmf.getTrustManagers();
               } finally {
                  input.close();
               }

               return var10000;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         Predef$.MODULE$.require(this.fileServerSSLOptions().protocol().isDefined(), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "spark.ssl.protocol is required when enabling SSL connections.";
            }
         });
         SSLContext sslContext = SSLContext.getInstance((String)this.fileServerSSLOptions().protocol().get());
         sslContext.init((KeyManager[])null, (TrustManager[])trustStoreManagers.getOrElse(new Serializable(this, credulousTrustStoreManagers$lzy, bitmap$0) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SecurityManager $outer;
            private final ObjectRef credulousTrustStoreManagers$lzy$1;
            private final VolatileByteRef bitmap$0$1;

            public final TrustManager[] apply() {
               return this.$outer.org$apache$spark$SecurityManager$$credulousTrustStoreManagers$1(this.credulousTrustStoreManagers$lzy$1, this.bitmap$0$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.credulousTrustStoreManagers$lzy$1 = credulousTrustStoreManagers$lzy$1;
                  this.bitmap$0$1 = bitmap$0$1;
               }
            }
         }), (SecureRandom)null);
         HostnameVerifier hostVerifier = new HostnameVerifier(this) {
            public boolean verify(String s, SSLSession sslSession) {
               return true;
            }
         };
         var10001 = new Tuple2(new Some(sslContext.getSocketFactory()), new Some(hostVerifier));
      } else {
         var10001 = new Tuple2(None$.MODULE$, None$.MODULE$);
      }

      Tuple2 var3 = var10001;
      if (var3 != null) {
         Option sslSocketFactory = (Option)var3._1();
         Option hostnameVerifier = (Option)var3._2();
         Tuple2 var11 = new Tuple2(sslSocketFactory, hostnameVerifier);
         this.x$1 = var11;
         this.sslSocketFactory = (Option)this.x$1._1();
         this.hostnameVerifier = (Option)this.x$1._2();
      } else {
         throw new MatchError(var3);
      }
   }
}
