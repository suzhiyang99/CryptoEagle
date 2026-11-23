package org.apache.spark;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import org.spark_project.jetty.util.ssl.SslContextFactory;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple12;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.immutable.Set;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ObjectRef;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\tmf!B\u0001\u0003\u0001\nA!AC*T\u0019>\u0003H/[8og*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xmE\u0003\u0001\u0013=)\u0002\u0004\u0005\u0002\u000b\u001b5\t1BC\u0001\r\u0003\u0015\u00198-\u00197b\u0013\tq1B\u0001\u0004B]f\u0014VM\u001a\t\u0003!Mi\u0011!\u0005\u0006\u0003%\t\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u0003)E\u0011q\u0001T8hO&tw\r\u0005\u0002\u000b-%\u0011qc\u0003\u0002\b!J|G-^2u!\tQ\u0011$\u0003\u0002\u001b\u0017\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001BK\u0002\u0013\u0005a$A\u0004f]\u0006\u0014G.\u001a3\u0004\u0001U\tq\u0004\u0005\u0002\u000bA%\u0011\u0011e\u0003\u0002\b\u0005>|G.Z1o\u0011!\u0019\u0003A!E!\u0002\u0013y\u0012\u0001C3oC\ndW\r\u001a\u0011\t\u0011\u0015\u0002!Q3A\u0005\u0002\u0019\nA\u0001]8siV\tq\u0005E\u0002\u000bQ)J!!K\u0006\u0003\r=\u0003H/[8o!\tQ1&\u0003\u0002-\u0017\t\u0019\u0011J\u001c;\t\u00119\u0002!\u0011#Q\u0001\n\u001d\nQ\u0001]8si\u0002B\u0001\u0002\r\u0001\u0003\u0016\u0004%\t!M\u0001\tW\u0016L8\u000b^8sKV\t!\u0007E\u0002\u000bQM\u0002\"\u0001N\u001d\u000e\u0003UR!AN\u001c\u0002\u0005%|'\"\u0001\u001d\u0002\t)\fg/Y\u0005\u0003uU\u0012AAR5mK\"AA\b\u0001B\tB\u0003%!'A\u0005lKf\u001cFo\u001c:fA!Aa\b\u0001BK\u0002\u0013\u0005q(\u0001\tlKf\u001cFo\u001c:f!\u0006\u001c8o^8sIV\t\u0001\tE\u0002\u000bQ\u0005\u0003\"AQ#\u000f\u0005)\u0019\u0015B\u0001#\f\u0003\u0019\u0001&/\u001a3fM&\u0011ai\u0012\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\u0011[\u0001\u0002C%\u0001\u0005#\u0005\u000b\u0011\u0002!\u0002#-,\u0017p\u0015;pe\u0016\u0004\u0016m]:x_J$\u0007\u0005\u0003\u0005L\u0001\tU\r\u0011\"\u0001@\u0003-YW-\u001f)bgN<xN\u001d3\t\u00115\u0003!\u0011#Q\u0001\n\u0001\u000bAb[3z!\u0006\u001c8o^8sI\u0002B\u0001b\u0014\u0001\u0003\u0016\u0004%\taP\u0001\rW\u0016L8\u000b^8sKRK\b/\u001a\u0005\t#\u0002\u0011\t\u0012)A\u0005\u0001\u0006i1.Z=Ti>\u0014X\rV=qK\u0002B\u0001b\u0015\u0001\u0003\u0016\u0004%\tAH\u0001\u000f]\u0016,Gm\u00117jK:$\u0018)\u001e;i\u0011!)\u0006A!E!\u0002\u0013y\u0012a\u00048fK\u0012\u001cE.[3oi\u0006+H\u000f\u001b\u0011\t\u0011]\u0003!Q3A\u0005\u0002E\n!\u0002\u001e:vgR\u001cFo\u001c:f\u0011!I\u0006A!E!\u0002\u0013\u0011\u0014a\u0003;skN$8\u000b^8sK\u0002B\u0001b\u0017\u0001\u0003\u0016\u0004%\taP\u0001\u0013iJ,8\u000f^*u_J,\u0007+Y:to>\u0014H\r\u0003\u0005^\u0001\tE\t\u0015!\u0003A\u0003M!(/^:u'R|'/\u001a)bgN<xN\u001d3!\u0011!y\u0006A!f\u0001\n\u0003y\u0014A\u0004;skN$8\u000b^8sKRK\b/\u001a\u0005\tC\u0002\u0011\t\u0012)A\u0005\u0001\u0006yAO];tiN#xN]3UsB,\u0007\u0005\u0003\u0005d\u0001\tU\r\u0011\"\u0001@\u0003!\u0001(o\u001c;pG>d\u0007\u0002C3\u0001\u0005#\u0005\u000b\u0011\u0002!\u0002\u0013A\u0014x\u000e^8d_2\u0004\u0003\u0002C4\u0001\u0005+\u0007I\u0011\u00015\u0002#\u0015t\u0017M\u00197fI\u0006cwm\u001c:ji\"l7/F\u0001j!\r\u0011%.Q\u0005\u0003W\u001e\u00131aU3u\u0011!i\u0007A!E!\u0002\u0013I\u0017AE3oC\ndW\rZ!mO>\u0014\u0018\u000e\u001e5ng\u0002BQa\u001c\u0001\u0005\u0002A\fa\u0001P5oSRtD#D9tiV4x\u000f_={wrlh\u0010\u0005\u0002s\u00015\t!\u0001C\u0004\u001d]B\u0005\t\u0019A\u0010\t\u000f\u0015r\u0007\u0013!a\u0001O!9\u0001G\u001cI\u0001\u0002\u0004\u0011\u0004b\u0002 o!\u0003\u0005\r\u0001\u0011\u0005\b\u0017:\u0004\n\u00111\u0001A\u0011\u001dye\u000e%AA\u0002\u0001Cqa\u00158\u0011\u0002\u0003\u0007q\u0004C\u0004X]B\u0005\t\u0019\u0001\u001a\t\u000fms\u0007\u0013!a\u0001\u0001\"9qL\u001cI\u0001\u0002\u0004\u0001\u0005bB2o!\u0003\u0005\r\u0001\u0011\u0005\bO:\u0004\n\u00111\u0001j\u0011\u001d\t\t\u0001\u0001C\u0001\u0003\u0007\tAd\u0019:fCR,'*\u001a;usN\u001bHnQ8oi\u0016DHOR1di>\u0014\u0018\u0010\u0006\u0002\u0002\u0006A!!\u0002KA\u0004!\u0011\tI!a\u0007\u000e\u0005\u0005-!\u0002BA\u0007\u0003\u001f\t1a]:m\u0015\u0011\t\t\"a\u0005\u0002\tU$\u0018\u000e\u001c\u0006\u0005\u0003+\t9\"A\u0003kKR$\u0018PC\u0002\u0002\u001a\u0019\tq!Z2mSB\u001cX-\u0003\u0003\u0002\u001e\u0005-!!E*tY\u000e{g\u000e^3yi\u001a\u000b7\r^8ss\"A\u0011\u0011\u0005\u0001C\u0002\u0013%\u0001.A\ntkB\u0004xN\u001d;fI\u0006cwm\u001c:ji\"l7\u000fC\u0004\u0002&\u0001\u0001\u000b\u0011B5\u0002)M,\b\u000f]8si\u0016$\u0017\t\\4pe&$\b.\\:!\u0011\u001d\tI\u0003\u0001C!\u0003W\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002\u0003\"I\u0011q\u0006\u0001\u0002\u0002\u0013\u0005\u0011\u0011G\u0001\u0005G>\u0004\u0018\u0010F\rr\u0003g\t)$a\u000e\u0002:\u0005m\u0012QHA \u0003\u0003\n\u0019%!\u0012\u0002H\u0005%\u0003\u0002\u0003\u000f\u0002.A\u0005\t\u0019A\u0010\t\u0011\u0015\ni\u0003%AA\u0002\u001dB\u0001\u0002MA\u0017!\u0003\u0005\rA\r\u0005\t}\u00055\u0002\u0013!a\u0001\u0001\"A1*!\f\u0011\u0002\u0003\u0007\u0001\t\u0003\u0005P\u0003[\u0001\n\u00111\u0001A\u0011!\u0019\u0016Q\u0006I\u0001\u0002\u0004y\u0002\u0002C,\u0002.A\u0005\t\u0019\u0001\u001a\t\u0011m\u000bi\u0003%AA\u0002\u0001C\u0001bXA\u0017!\u0003\u0005\r\u0001\u0011\u0005\tG\u00065\u0002\u0013!a\u0001\u0001\"Aq-!\f\u0011\u0002\u0003\u0007\u0011\u000eC\u0005\u0002N\u0001\t\n\u0011\"\u0001\u0002P\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nTCAA)U\ry\u00121K\u0016\u0003\u0003+\u0002B!a\u0016\u0002b5\u0011\u0011\u0011\f\u0006\u0005\u00037\ni&A\u0005v]\u000eDWmY6fI*\u0019\u0011qL\u0006\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002d\u0005e#!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I\u0011q\r\u0001\u0012\u0002\u0013\u0005\u0011\u0011N\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\t\tYGK\u0002(\u0003'B\u0011\"a\u001c\u0001#\u0003%\t!!\u001d\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%gU\u0011\u00111\u000f\u0016\u0004e\u0005M\u0003\"CA<\u0001E\u0005I\u0011AA=\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQ*\"!a\u001f+\u0007\u0001\u000b\u0019\u0006C\u0005\u0002\u0000\u0001\t\n\u0011\"\u0001\u0002z\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012*\u0004\"CAB\u0001E\u0005I\u0011AA=\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIYB\u0011\"a\"\u0001#\u0003%\t!a\u0014\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%o!I\u00111\u0012\u0001\u0012\u0002\u0013\u0005\u0011\u0011O\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00139\u0011%\ty\tAI\u0001\n\u0003\tI(\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u001d\t\u0013\u0005M\u0005!%A\u0005\u0002\u0005e\u0014aD2paf$C-\u001a4bk2$H%\r\u0019\t\u0013\u0005]\u0005!%A\u0005\u0002\u0005e\u0014aD2paf$C-\u001a4bk2$H%M\u0019\t\u0013\u0005m\u0005!%A\u0005\u0002\u0005u\u0015aD2paf$C-\u001a4bk2$H%\r\u001a\u0016\u0005\u0005}%fA5\u0002T!I\u00111\u0015\u0001\u0002\u0002\u0013\u0005\u0013QU\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005\u001d\u0006\u0003BAU\u0003_k!!a+\u000b\u0007\u00055v'\u0001\u0003mC:<\u0017b\u0001$\u0002,\"I\u00111\u0017\u0001\u0002\u0002\u0013\u0005\u0011QW\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002U!I\u0011\u0011\u0018\u0001\u0002\u0002\u0013\u0005\u00111X\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\ti,a1\u0011\u0007)\ty,C\u0002\u0002B.\u00111!\u00118z\u0011%\t)-a.\u0002\u0002\u0003\u0007!&A\u0002yIEB\u0011\"!3\u0001\u0003\u0003%\t%a3\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!!4\u0011\r\u0005=\u0017Q[A_\u001b\t\t\tNC\u0002\u0002T.\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t9.!5\u0003\u0011%#XM]1u_JD\u0011\"a7\u0001\u0003\u0003%\t!!8\u0002\u0011\r\fg.R9vC2$2aHAp\u0011)\t)-!7\u0002\u0002\u0003\u0007\u0011Q\u0018\u0005\n\u0003G\u0004\u0011\u0011!C!\u0003K\f\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002U!I\u0011\u0011\u001e\u0001\u0002\u0002\u0013\u0005\u00131^\u0001\u0007KF,\u0018\r\\:\u0015\u0007}\ti\u000f\u0003\u0006\u0002F\u0006\u001d\u0018\u0011!a\u0001\u0003{;\u0001\"!=\u0003\u0011\u0003\u0011\u00111_\u0001\u000b'Ncu\n\u001d;j_:\u001c\bc\u0001:\u0002v\u001a9\u0011A\u0001E\u0001\u0005\u0005]8#BA{\u0013=A\u0002bB8\u0002v\u0012\u0005\u00111 \u000b\u0003\u0003gD\u0001\"a@\u0002v\u0012\u0005!\u0011A\u0001\u0006a\u0006\u00148/\u001a\u000b\bc\n\r!Q\u0002B\t\u0011!\u0011)!!@A\u0002\t\u001d\u0011\u0001B2p]\u001a\u00042A\u001dB\u0005\u0013\r\u0011YA\u0001\u0002\n'B\f'o[\"p]\u001aDqAa\u0004\u0002~\u0002\u0007\u0011)\u0001\u0002og\"Q!1CA\u007f!\u0003\u0005\rA!\u0006\u0002\u0011\u0011,g-Y;miN\u00042A\u0003\u0015r\u0011)\u0011I\"!>\u0002\u0002\u0013\u0005%1D\u0001\u0006CB\u0004H.\u001f\u000b\u001ac\nu!q\u0004B\u0011\u0005G\u0011)Ca\n\u0003*\t-\"Q\u0006B\u0018\u0005c\u0011\u0019\u0004\u0003\u0005\u001d\u0005/\u0001\n\u00111\u0001 \u0011!)#q\u0003I\u0001\u0002\u00049\u0003\u0002\u0003\u0019\u0003\u0018A\u0005\t\u0019\u0001\u001a\t\u0011y\u00129\u0002%AA\u0002\u0001C\u0001b\u0013B\f!\u0003\u0005\r\u0001\u0011\u0005\t\u001f\n]\u0001\u0013!a\u0001\u0001\"A1Ka\u0006\u0011\u0002\u0003\u0007q\u0004\u0003\u0005X\u0005/\u0001\n\u00111\u00013\u0011!Y&q\u0003I\u0001\u0002\u0004\u0001\u0005\u0002C0\u0003\u0018A\u0005\t\u0019\u0001!\t\u0011\r\u00149\u0002%AA\u0002\u0001C\u0001b\u001aB\f!\u0003\u0005\r!\u001b\u0005\u000b\u0005o\t)0!A\u0005\u0002\ne\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0005w\u0011\u0019\u0005\u0005\u0003\u000bQ\tu\u0002c\u0004\u0006\u0003@}9#\u0007\u0011!A?I\u0002\u0005\tQ5\n\u0007\t\u00053BA\u0004UkBdW-\r\u001a\t\u0013\t\u0015#QGA\u0001\u0002\u0004\t\u0018a\u0001=%a!Q!\u0011JA{#\u0003%\t!a\u0014\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00132\u0011)\u0011i%!>\u0012\u0002\u0013\u0005\u0011\u0011N\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001a\t\u0015\tE\u0013Q_I\u0001\n\u0003\t\t(A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$He\r\u0005\u000b\u0005+\n)0%A\u0005\u0002\u0005e\u0014a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$C\u0007\u0003\u0006\u0003Z\u0005U\u0018\u0013!C\u0001\u0003s\n1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012*\u0004B\u0003B/\u0003k\f\n\u0011\"\u0001\u0002z\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIYB!B!\u0019\u0002vF\u0005I\u0011AA(\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%o!Q!QMA{#\u0003%\t!!\u001d\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00139\u0011)\u0011I'!>\u0012\u0002\u0013\u0005\u0011\u0011P\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000fJ\u001d\t\u0015\t5\u0014Q_I\u0001\n\u0003\tI(\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%\r\u0019\t\u0015\tE\u0014Q_I\u0001\n\u0003\tI(\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%M\u0019\t\u0015\tU\u0014Q_I\u0001\n\u0003\ti*\u0001\u000f%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H%\r\u001a\t\u0015\te\u0014Q_I\u0001\n\u0003\u0011Y(A\bqCJ\u001cX\r\n3fM\u0006,H\u000e\u001e\u00134+\t\u0011iH\u000b\u0003\u0003\u0016\u0005M\u0003B\u0003BA\u0003k\f\n\u0011\"\u0001\u0002P\u0005y\u0011\r\u001d9ms\u0012\"WMZ1vYR$\u0013\u0007\u0003\u0006\u0003\u0006\u0006U\u0018\u0013!C\u0001\u0003S\nq\"\u00199qYf$C-\u001a4bk2$HE\r\u0005\u000b\u0005\u0013\u000b)0%A\u0005\u0002\u0005E\u0014aD1qa2LH\u0005Z3gCVdG\u000fJ\u001a\t\u0015\t5\u0015Q_I\u0001\n\u0003\tI(A\bbaBd\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00135\u0011)\u0011\t*!>\u0012\u0002\u0013\u0005\u0011\u0011P\u0001\u0010CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%k!Q!QSA{#\u0003%\t!!\u001f\u0002\u001f\u0005\u0004\b\u000f\\=%I\u00164\u0017-\u001e7uIYB!B!'\u0002vF\u0005I\u0011AA(\u0003=\t\u0007\u000f\u001d7zI\u0011,g-Y;mi\u0012:\u0004B\u0003BO\u0003k\f\n\u0011\"\u0001\u0002r\u0005y\u0011\r\u001d9ms\u0012\"WMZ1vYR$\u0003\b\u0003\u0006\u0003\"\u0006U\u0018\u0013!C\u0001\u0003s\nq\"\u00199qYf$C-\u001a4bk2$H%\u000f\u0005\u000b\u0005K\u000b)0%A\u0005\u0002\u0005e\u0014\u0001E1qa2LH\u0005Z3gCVdG\u000fJ\u00191\u0011)\u0011I+!>\u0012\u0002\u0013\u0005\u0011\u0011P\u0001\u0011CB\u0004H.\u001f\u0013eK\u001a\fW\u000f\u001c;%cEB!B!,\u0002vF\u0005I\u0011AAO\u0003A\t\u0007\u000f\u001d7zI\u0011,g-Y;mi\u0012\n$\u0007\u0003\u0006\u00032\u0006U\u0018\u0011!C\u0005\u0005g\u000b1B]3bIJ+7o\u001c7wKR\u0011!Q\u0017\t\u0005\u0003S\u00139,\u0003\u0003\u0003:\u0006-&AB(cU\u0016\u001cG\u000f"
)
public class SSLOptions implements Logging, Product, Serializable {
   private final boolean enabled;
   private final Option<Object> port;
   private final Option<File> keyStore;
   private final Option<String> keyStorePassword;
   private final Option<String> keyPassword;
   private final Option<String> keyStoreType;
   private final boolean needClientAuth;
   private final Option<File> trustStore;
   private final Option<String> trustStorePassword;
   private final Option<String> trustStoreType;
   private final Option<String> protocol;
   private final Set<String> enabledAlgorithms;
   private final Set<String> supportedAlgorithms;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Set<String> apply$default$12() {
      return SSLOptions$.MODULE$.apply$default$12();
   }

   public static Option<String> apply$default$11() {
      return SSLOptions$.MODULE$.apply$default$11();
   }

   public static Option<String> apply$default$10() {
      return SSLOptions$.MODULE$.apply$default$10();
   }

   public static Option<String> apply$default$9() {
      return SSLOptions$.MODULE$.apply$default$9();
   }

   public static Option<File> apply$default$8() {
      return SSLOptions$.MODULE$.apply$default$8();
   }

   public static boolean apply$default$7() {
      return SSLOptions$.MODULE$.apply$default$7();
   }

   public static Option<String> apply$default$6() {
      return SSLOptions$.MODULE$.apply$default$6();
   }

   public static Option<String> apply$default$5() {
      return SSLOptions$.MODULE$.apply$default$5();
   }

   public static Option<String> apply$default$4() {
      return SSLOptions$.MODULE$.apply$default$4();
   }

   public static Option<File> apply$default$3() {
      return SSLOptions$.MODULE$.apply$default$3();
   }

   public static Option<Object> apply$default$2() {
      return SSLOptions$.MODULE$.apply$default$2();
   }

   public static boolean apply$default$1() {
      return SSLOptions$.MODULE$.apply$default$1();
   }

   public static Option<SSLOptions> parse$default$3() {
      return SSLOptions$.MODULE$.parse$default$3();
   }

   public static Set<String> $lessinit$greater$default$12() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$12();
   }

   public static Option<String> $lessinit$greater$default$11() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$11();
   }

   public static Option<String> $lessinit$greater$default$10() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$10();
   }

   public static Option<String> $lessinit$greater$default$9() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$9();
   }

   public static Option<File> $lessinit$greater$default$8() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$8();
   }

   public static boolean $lessinit$greater$default$7() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$7();
   }

   public static Option<String> $lessinit$greater$default$6() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$6();
   }

   public static Option<String> $lessinit$greater$default$5() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$5();
   }

   public static Option<String> $lessinit$greater$default$4() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$4();
   }

   public static Option<File> $lessinit$greater$default$3() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$3();
   }

   public static Option<Object> $lessinit$greater$default$2() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$2();
   }

   public static boolean $lessinit$greater$default$1() {
      return SSLOptions$.MODULE$.$lessinit$greater$default$1();
   }

   public static Option<Tuple12<Object, Option<Object>, Option<File>, Option<String>, Option<String>, Option<String>, Object, Option<File>, Option<String>, Option<String>, Option<String>, Set<String>>> unapply(SSLOptions var0) {
      return SSLOptions$.MODULE$.unapply(var0);
   }

   public static SSLOptions apply(boolean var0, Option<Object> var1, Option<File> var2, Option<String> var3, Option<String> var4, Option<String> var5, boolean var6, Option<File> var7, Option<String> var8, Option<String> var9, Option<String> var10, Set<String> var11) {
      return SSLOptions$.MODULE$.apply(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static SSLOptions parse(SparkConf var0, String var1, Option<SSLOptions> var2) {
      return SSLOptions$.MODULE$.parse(var0, var1, var2);
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

   public boolean enabled() {
      return this.enabled;
   }

   public Option<Object> port() {
      return this.port;
   }

   public Option<File> keyStore() {
      return this.keyStore;
   }

   public Option<String> keyStorePassword() {
      return this.keyStorePassword;
   }

   public Option<String> keyPassword() {
      return this.keyPassword;
   }

   public Option<String> keyStoreType() {
      return this.keyStoreType;
   }

   public boolean needClientAuth() {
      return this.needClientAuth;
   }

   public Option<File> trustStore() {
      return this.trustStore;
   }

   public Option<String> trustStorePassword() {
      return this.trustStorePassword;
   }

   public Option<String> trustStoreType() {
      return this.trustStoreType;
   }

   public Option<String> protocol() {
      return this.protocol;
   }

   public Set<String> enabledAlgorithms() {
      return this.enabledAlgorithms;
   }

   public Option<SslContextFactory> createJettySslContextFactory() {
      Object var10000;
      if (this.enabled()) {
         SslContextFactory sslContextFactory = new SslContextFactory();
         this.keyStore().foreach(new Serializable(this, sslContextFactory) {
            public static final long serialVersionUID = 0L;
            private final SslContextFactory sslContextFactory$1;

            public final void apply(File file) {
               this.sslContextFactory$1.setKeyStorePath(file.getAbsolutePath());
            }

            public {
               this.sslContextFactory$1 = sslContextFactory$1;
            }
         });
         this.keyStorePassword().foreach(new Serializable(this, sslContextFactory) {
            public static final long serialVersionUID = 0L;
            private final SslContextFactory sslContextFactory$1;

            public final void apply(String x$1) {
               this.sslContextFactory$1.setKeyStorePassword(x$1);
            }

            public {
               this.sslContextFactory$1 = sslContextFactory$1;
            }
         });
         this.keyPassword().foreach(new Serializable(this, sslContextFactory) {
            public static final long serialVersionUID = 0L;
            private final SslContextFactory sslContextFactory$1;

            public final void apply(String x$1) {
               this.sslContextFactory$1.setKeyManagerPassword(x$1);
            }

            public {
               this.sslContextFactory$1 = sslContextFactory$1;
            }
         });
         this.keyStoreType().foreach(new Serializable(this, sslContextFactory) {
            public static final long serialVersionUID = 0L;
            private final SslContextFactory sslContextFactory$1;

            public final void apply(String x$1) {
               this.sslContextFactory$1.setKeyStoreType(x$1);
            }

            public {
               this.sslContextFactory$1 = sslContextFactory$1;
            }
         });
         if (this.needClientAuth()) {
            this.trustStore().foreach(new Serializable(this, sslContextFactory) {
               public static final long serialVersionUID = 0L;
               private final SslContextFactory sslContextFactory$1;

               public final void apply(File file) {
                  this.sslContextFactory$1.setTrustStorePath(file.getAbsolutePath());
               }

               public {
                  this.sslContextFactory$1 = sslContextFactory$1;
               }
            });
            this.trustStorePassword().foreach(new Serializable(this, sslContextFactory) {
               public static final long serialVersionUID = 0L;
               private final SslContextFactory sslContextFactory$1;

               public final void apply(String x$1) {
                  this.sslContextFactory$1.setTrustStorePassword(x$1);
               }

               public {
                  this.sslContextFactory$1 = sslContextFactory$1;
               }
            });
            this.trustStoreType().foreach(new Serializable(this, sslContextFactory) {
               public static final long serialVersionUID = 0L;
               private final SslContextFactory sslContextFactory$1;

               public final void apply(String x$1) {
                  this.sslContextFactory$1.setTrustStoreType(x$1);
               }

               public {
                  this.sslContextFactory$1 = sslContextFactory$1;
               }
            });
         }

         this.protocol().foreach(new Serializable(this, sslContextFactory) {
            public static final long serialVersionUID = 0L;
            private final SslContextFactory sslContextFactory$1;

            public final void apply(String x$1) {
               this.sslContextFactory$1.setProtocol(x$1);
            }

            public {
               this.sslContextFactory$1 = sslContextFactory$1;
            }
         });
         if (this.supportedAlgorithms().nonEmpty()) {
            sslContextFactory.setIncludeCipherSuites((String[])this.supportedAlgorithms().toSeq().toArray(ClassTag$.MODULE$.apply(String.class)));
         }

         var10000 = new Some(sslContextFactory);
      } else {
         var10000 = None$.MODULE$;
      }

      return (Option<SslContextFactory>)var10000;
   }

   private Set<String> supportedAlgorithms() {
      return this.supportedAlgorithms;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"SSLOptions{enabled=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToBoolean(this.enabled())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"keyStore=", ", keyStorePassword=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.keyStore(), this.keyStorePassword().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$1) {
            return "xxx";
         }
      })}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"trustStore=", ", trustStorePassword=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.trustStore(), this.trustStorePassword().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$2) {
            return "xxx";
         }
      })}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"protocol=", ", enabledAlgorithms=", "}"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.protocol(), this.enabledAlgorithms()}))).toString();
   }

   public SSLOptions copy(boolean enabled, Option<Object> port, Option<File> keyStore, Option<String> keyStorePassword, Option<String> keyPassword, Option<String> keyStoreType, boolean needClientAuth, Option<File> trustStore, Option<String> trustStorePassword, Option<String> trustStoreType, Option<String> protocol, Set<String> enabledAlgorithms) {
      return new SSLOptions(enabled, port, keyStore, keyStorePassword, keyPassword, keyStoreType, needClientAuth, trustStore, trustStorePassword, trustStoreType, protocol, enabledAlgorithms);
   }

   public boolean copy$default$1() {
      return this.enabled();
   }

   public Option<Object> copy$default$2() {
      return this.port();
   }

   public Option<File> copy$default$3() {
      return this.keyStore();
   }

   public Option<String> copy$default$4() {
      return this.keyStorePassword();
   }

   public Option<String> copy$default$5() {
      return this.keyPassword();
   }

   public Option<String> copy$default$6() {
      return this.keyStoreType();
   }

   public boolean copy$default$7() {
      return this.needClientAuth();
   }

   public Option<File> copy$default$8() {
      return this.trustStore();
   }

   public Option<String> copy$default$9() {
      return this.trustStorePassword();
   }

   public Option<String> copy$default$10() {
      return this.trustStoreType();
   }

   public Option<String> copy$default$11() {
      return this.protocol();
   }

   public Set<String> copy$default$12() {
      return this.enabledAlgorithms();
   }

   public String productPrefix() {
      return "SSLOptions";
   }

   public int productArity() {
      return 12;
   }

   public Object productElement(int x$1) {
      Object var10000;
      switch (x$1) {
         case 0:
            var10000 = BoxesRunTime.boxToBoolean(this.enabled());
            break;
         case 1:
            var10000 = this.port();
            break;
         case 2:
            var10000 = this.keyStore();
            break;
         case 3:
            var10000 = this.keyStorePassword();
            break;
         case 4:
            var10000 = this.keyPassword();
            break;
         case 5:
            var10000 = this.keyStoreType();
            break;
         case 6:
            var10000 = BoxesRunTime.boxToBoolean(this.needClientAuth());
            break;
         case 7:
            var10000 = this.trustStore();
            break;
         case 8:
            var10000 = this.trustStorePassword();
            break;
         case 9:
            var10000 = this.trustStoreType();
            break;
         case 10:
            var10000 = this.protocol();
            break;
         case 11:
            var10000 = this.enabledAlgorithms();
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
      return x$1 instanceof SSLOptions;
   }

   public int hashCode() {
      int var1 = -889275714;
      var1 = Statics.mix(var1, this.enabled() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.port()));
      var1 = Statics.mix(var1, Statics.anyHash(this.keyStore()));
      var1 = Statics.mix(var1, Statics.anyHash(this.keyStorePassword()));
      var1 = Statics.mix(var1, Statics.anyHash(this.keyPassword()));
      var1 = Statics.mix(var1, Statics.anyHash(this.keyStoreType()));
      var1 = Statics.mix(var1, this.needClientAuth() ? 1231 : 1237);
      var1 = Statics.mix(var1, Statics.anyHash(this.trustStore()));
      var1 = Statics.mix(var1, Statics.anyHash(this.trustStorePassword()));
      var1 = Statics.mix(var1, Statics.anyHash(this.trustStoreType()));
      var1 = Statics.mix(var1, Statics.anyHash(this.protocol()));
      var1 = Statics.mix(var1, Statics.anyHash(this.enabledAlgorithms()));
      return Statics.finalizeHash(var1, 12);
   }

   public boolean equals(Object x$1) {
      boolean var25;
      if (this != x$1) {
         label139: {
            boolean var3;
            if (x$1 instanceof SSLOptions) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label120: {
                  SSLOptions var4 = (SSLOptions)x$1;
                  if (this.enabled() == var4.enabled()) {
                     label129: {
                        Option var10000 = this.port();
                        Option var5 = var4.port();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label129;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label129;
                        }

                        var10000 = this.keyStore();
                        Option var6 = var4.keyStore();
                        if (var10000 == null) {
                           if (var6 != null) {
                              break label129;
                           }
                        } else if (!var10000.equals(var6)) {
                           break label129;
                        }

                        var10000 = this.keyStorePassword();
                        Option var7 = var4.keyStorePassword();
                        if (var10000 == null) {
                           if (var7 != null) {
                              break label129;
                           }
                        } else if (!var10000.equals(var7)) {
                           break label129;
                        }

                        var10000 = this.keyPassword();
                        Option var8 = var4.keyPassword();
                        if (var10000 == null) {
                           if (var8 != null) {
                              break label129;
                           }
                        } else if (!var10000.equals(var8)) {
                           break label129;
                        }

                        var10000 = this.keyStoreType();
                        Option var9 = var4.keyStoreType();
                        if (var10000 == null) {
                           if (var9 != null) {
                              break label129;
                           }
                        } else if (!var10000.equals(var9)) {
                           break label129;
                        }

                        if (this.needClientAuth() == var4.needClientAuth()) {
                           label130: {
                              var10000 = this.trustStore();
                              Option var10 = var4.trustStore();
                              if (var10000 == null) {
                                 if (var10 != null) {
                                    break label130;
                                 }
                              } else if (!var10000.equals(var10)) {
                                 break label130;
                              }

                              var10000 = this.trustStorePassword();
                              Option var11 = var4.trustStorePassword();
                              if (var10000 == null) {
                                 if (var11 != null) {
                                    break label130;
                                 }
                              } else if (!var10000.equals(var11)) {
                                 break label130;
                              }

                              var10000 = this.trustStoreType();
                              Option var12 = var4.trustStoreType();
                              if (var10000 == null) {
                                 if (var12 != null) {
                                    break label130;
                                 }
                              } else if (!var10000.equals(var12)) {
                                 break label130;
                              }

                              var10000 = this.protocol();
                              Option var13 = var4.protocol();
                              if (var10000 == null) {
                                 if (var13 != null) {
                                    break label130;
                                 }
                              } else if (!var10000.equals(var13)) {
                                 break label130;
                              }

                              Set var23 = this.enabledAlgorithms();
                              Set var14 = var4.enabledAlgorithms();
                              if (var23 == null) {
                                 if (var14 != null) {
                                    break label130;
                                 }
                              } else if (!var23.equals(var14)) {
                                 break label130;
                              }

                              if (var4.canEqual(this)) {
                                 var25 = true;
                                 break label120;
                              }
                           }
                        }
                     }
                  }

                  var25 = false;
               }

               if (var25) {
                  break label139;
               }
            }

            var25 = false;
            return var25;
         }
      }

      var25 = true;
      return var25;
   }

   private final void liftedTree1$1(ObjectRef context$1) {
      try {
         context$1.elem = (T)SSLContext.getInstance((String)this.protocol().get());
         ((SSLContext)context$1.elem).init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
      } catch (NoSuchAlgorithmException var3) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SSLOptions $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No support for requested SSL protocol ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.protocol().get()}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         context$1.elem = (T)SSLContext.getDefault();
      }

   }

   public SSLOptions(boolean enabled, Option<Object> port, Option<File> keyStore, Option<String> keyStorePassword, Option<String> keyPassword, Option<String> keyStoreType, boolean needClientAuth, Option<File> trustStore, Option<String> trustStorePassword, Option<String> trustStoreType, Option<String> protocol, Set<String> enabledAlgorithms) {
      this.enabled = enabled;
      this.port = port;
      this.keyStore = keyStore;
      this.keyStorePassword = keyStorePassword;
      this.keyPassword = keyPassword;
      this.keyStoreType = keyStoreType;
      this.needClientAuth = needClientAuth;
      this.trustStore = trustStore;
      this.trustStorePassword = trustStorePassword;
      this.trustStoreType = trustStoreType;
      this.protocol = protocol;
      this.enabledAlgorithms = enabledAlgorithms;
      Logging$class.$init$(this);
      Product$class.$init$(this);
      Set var10001;
      if (enabledAlgorithms.isEmpty()) {
         var10001 = Predef$.MODULE$.Set().empty();
      } else {
         ObjectRef context = ObjectRef.create((Object)null);
         if (protocol.isEmpty()) {
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "No SSL protocol specified";
               }
            });
            context.elem = (T)SSLContext.getDefault();
         } else {
            this.liftedTree1$1(context);
         }

         Set providerAlgorithms = Predef$.MODULE$.refArrayOps((Object[])((SSLContext)context.elem).getServerSocketFactory().getSupportedCipherSuites()).toSet();
         ((IterableLike)enabledAlgorithms.$amp$tilde(providerAlgorithms)).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SSLOptions $outer;

            public final void apply(String cipher) {
               this.$outer.logDebug(new Serializable(this, cipher) {
                  public static final long serialVersionUID = 0L;
                  private final String cipher$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Discarding unsupported cipher ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cipher$1}));
                  }

                  public {
                     this.cipher$1 = cipher$1;
                  }
               });
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         Set supported = (Set)enabledAlgorithms.$amp(providerAlgorithms);
         Predef$.MODULE$.require(supported.nonEmpty() || scala.sys.package$.MODULE$.env().contains("SPARK_TESTING"), new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SSLOptions $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"SSLContext does not support any of the enabled algorithms: ").append((Object)this.$outer.enabledAlgorithms().mkString(",")).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         var10001 = supported;
      }

      this.supportedAlgorithms = var10001;
   }
}
