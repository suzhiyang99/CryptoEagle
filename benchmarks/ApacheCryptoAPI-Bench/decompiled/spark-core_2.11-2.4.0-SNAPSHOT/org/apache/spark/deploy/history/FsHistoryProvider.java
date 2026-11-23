package org.apache.spark.deploy.history;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.HdfsConstants.SafeModeAction;
import org.apache.hadoop.security.AccessControlException;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.scheduler.EventLoggingListener$;
import org.apache.spark.scheduler.ReplayListenerBus;
import org.apache.spark.scheduler.ReplayListenerBus$;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.status.AppHistoryServerPlugin;
import org.apache.spark.status.AppStatusListener;
import org.apache.spark.status.AppStatusStore;
import org.apache.spark.status.AppStatusStore$;
import org.apache.spark.status.AppStatusStoreMetadata;
import org.apache.spark.status.ElementTrackingStore;
import org.apache.spark.status.KVUtils;
import org.apache.spark.status.KVUtils$;
import org.apache.spark.status.api.v1.ApplicationInfo;
import org.apache.spark.ui.SparkUI;
import org.apache.spark.ui.SparkUI$;
import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.kvstore.InMemoryStore;
import org.apache.spark.util.kvstore.KVStore;
import org.apache.spark.util.kvstore.LevelDB;
import org.apache.spark.util.kvstore.UnsupportedStoreVersionException;
import org.fusesource.leveldbjni.internal.NativeDB;
import org.slf4j.Logger;
import org.spark_project.guava.io.ByteStreams;
import org.spark_project.guava.util.concurrent.MoreExecutors;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.IterableLike;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.SeqLike;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.Map;
import scala.collection.immutable.MapLike;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.util.Try$;
import scala.xml.Elem;
import scala.xml.MetaData;
import scala.xml.Node;
import scala.xml.NodeBuffer;
import scala.xml.Null;
import scala.xml.Text;
import scala.xml.TopScope;
import scala.xml.UnprefixedAttribute;
import scala.xml.Null.;

@ScalaSignature(
   bytes = "\u0006\u0001\u0011\rb!B\u0001\u0003\u0001\ta!!\u0005$t\u0011&\u001cHo\u001c:z!J|g/\u001b3fe*\u00111\u0001B\u0001\bQ&\u001cHo\u001c:z\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0019\u0001!D\t\u0011\u00059yQ\"\u0001\u0002\n\u0005A\u0011!AG!qa2L7-\u0019;j_:D\u0015n\u001d;pef\u0004&o\u001c<jI\u0016\u0014\bC\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0002\u0003\u0006IAG\u0001\u0005G>tgm\u0001\u0001\u0011\u0005maR\"\u0001\u0004\n\u0005u1!!C*qCJ\\7i\u001c8g\u0011!y\u0002A!A!\u0002\u0013\u0001\u0013!B2m_\u000e\\\u0007CA\u0011%\u001b\u0005\u0011#BA\u0012\u0007\u0003\u0011)H/\u001b7\n\u0005\u0015\u0012#!B\"m_\u000e\\\u0007\"B\u0014\u0001\t\u0003A\u0013A\u0002\u001fj]&$h\bF\u0002*U-\u0002\"A\u0004\u0001\t\u000ba1\u0003\u0019\u0001\u000e\t\u000b}1\u0003\u0019\u0001\u0011\t\u000b\u001d\u0002A\u0011A\u0017\u0015\u0005%r\u0003\"\u0002\r-\u0001\u0004Q\u0002b\u0002\u0019\u0001\u0005\u0004%I!M\u0001\u001a'\u00063U)T(E\u000b~\u001b\u0005*R\"L?&sE+\u0012*W\u00032{6+F\u00013!\t\u0019d'D\u00015\u0015\u0005)\u0014!B:dC2\f\u0017BA\u001c5\u0005\u0011auN\\4\t\re\u0002\u0001\u0015!\u00033\u0003i\u0019\u0016IR#N\u001f\u0012+ul\u0011%F\u0007.{\u0016J\u0014+F%Z\u000bEjX*!\u0011\u001dY\u0004A1A\u0005\nE\n\u0011#\u0016)E\u0003R+u,\u0013(U\u000bJ3\u0016\tT0T\u0011\u0019i\u0004\u0001)A\u0005e\u0005\u0011R\u000b\u0015#B)\u0016{\u0016J\u0014+F%Z\u000bEjX*!\u0011\u001dy\u0004A1A\u0005\nE\n\u0001c\u0011'F\u0003:{\u0016J\u0014+F%Z\u000bEjX*\t\r\u0005\u0003\u0001\u0015!\u00033\u0003E\u0019E*R!O?&sE+\u0012*W\u00032{6\u000b\t\u0005\b\u0007\u0002\u0011\r\u0011\"\u0003E\u0003YqU+T0Q%>\u001bUiU*J\u001d\u001e{F\u000b\u0013*F\u0003\u0012\u001bV#A#\u0011\u0005M2\u0015BA$5\u0005\rIe\u000e\u001e\u0005\u0007\u0013\u0002\u0001\u000b\u0011B#\u0002/9+Vj\u0018)S\u001f\u000e+5kU%O\u000f~#\u0006JU#B\tN\u0003\u0003bB&\u0001\u0005\u0004%I\u0001T\u0001\u0007Y><G)\u001b:\u0016\u00035\u0003\"AT)\u000f\u0005Mz\u0015B\u0001)5\u0003\u0019\u0001&/\u001a3fM&\u0011!k\u0015\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005A#\u0004BB+\u0001A\u0003%Q*A\u0004m_\u001e$\u0015N\u001d\u0011\t\u000f]\u0003!\u0019!C\u00051\u00061\u0002*S*U\u001fJKv,V%`\u0003\u000ec5kX#O\u0003\ncU)F\u0001Z!\t\u0019$,\u0003\u0002\\i\t9!i\\8mK\u0006t\u0007BB/\u0001A\u0003%\u0011,A\fI\u0013N#vJU-`+&{\u0016i\u0011'T?\u0016s\u0015I\u0011'FA!9q\f\u0001b\u0001\n\u0013a\u0015!\u0006%J'R{%+W0V\u0013~\u000bE)T%O?\u0006\u001bEj\u0015\u0005\u0007C\u0002\u0001\u000b\u0011B'\u0002-!K5\u000bV(S3~+\u0016jX!E\u001b&su,Q\"M'\u0002Bqa\u0019\u0001C\u0002\u0013%A*\u0001\u000fI\u0013N#vJU-`+&{\u0016\tR'J\u001d~\u000b5\tT*`\u000fJ{U\u000bU*\t\r\u0015\u0004\u0001\u0015!\u0003N\u0003uA\u0015j\u0015+P%f{V+S0B\t6KejX!D\u0019N{vIU(V!N\u0003\u0003bB4\u0001\u0005\u0004%I\u0001[\u0001\u000bQ\u0006$wn\u001c9D_:4W#A5\u0011\u0005)tW\"A6\u000b\u0005aa'BA7\t\u0003\u0019A\u0017\rZ8pa&\u0011qn\u001b\u0002\u000e\u0007>tg-[4ve\u0006$\u0018n\u001c8\t\rE\u0004\u0001\u0015!\u0003j\u0003-A\u0017\rZ8pa\u000e{gN\u001a\u0011\t\u000fM\u0004!\u0019!C\u0005i\u0006\u0011am]\u000b\u0002kB\u0011a\u000f_\u0007\u0002o*\u00111\u000f\\\u0005\u0003s^\u0014!BR5mKNK8\u000f^3n\u0011\u0019Y\b\u0001)A\u0005k\u0006\u0019am\u001d\u0011\t\u000fu\u0004!\u0019!C\u0005}\u0006!\u0001o\\8m+\u0005y\b\u0003BA\u0001\u0003\u001bi!!a\u0001\u000b\t\u0005\u0015\u0011qA\u0001\u000bG>t7-\u001e:sK:$(bA\u0012\u0002\n)\u0011\u00111B\u0001\u0005U\u00064\u0018-\u0003\u0003\u0002\u0010\u0005\r!\u0001G*dQ\u0016$W\u000f\\3e\u000bb,7-\u001e;peN+'O^5dK\"9\u00111\u0003\u0001!\u0002\u0013y\u0018!\u00029p_2\u0004\u0003\"CA\f\u0001\t\u0007I\u0011BA\r\u00031a\u0017m\u001d;TG\u0006tG+[7f+\t\tY\u0002\u0005\u0003\u0002\u001e\u0005\rRBAA\u0010\u0015\u0011\t\t#a\u0001\u0002\r\u0005$x.\\5d\u0013\u0011\t)#a\b\u0003\u0015\u0005#x.\\5d\u0019>tw\r\u0003\u0005\u0002*\u0001\u0001\u000b\u0011BA\u000e\u00035a\u0017m\u001d;TG\u0006tG+[7fA!I\u0011Q\u0006\u0001C\u0002\u0013%\u0011qF\u0001\u0018a\u0016tG-\u001b8h%\u0016\u0004H.Y=UCN\\7oQ8v]R,\"!!\r\u0011\t\u0005u\u00111G\u0005\u0005\u0003k\tyBA\u0007Bi>l\u0017nY%oi\u0016<WM\u001d\u0005\t\u0003s\u0001\u0001\u0015!\u0003\u00022\u0005A\u0002/\u001a8eS:<'+\u001a9mCf$\u0016m]6t\u0007>,h\u000e\u001e\u0011\t\u0013\u0005u\u0002A1A\u0005\n\u0005}\u0012!C:u_J,\u0007+\u0019;i+\t\t\t\u0005E\u00034\u0003\u0007\n9%C\u0002\u0002FQ\u0012aa\u00149uS>t\u0007\u0003BA%\u0003\u001fj!!a\u0013\u000b\t\u00055\u0013\u0011B\u0001\u0003S>LA!!\u0015\u0002L\t!a)\u001b7f\u0011!\t)\u0006\u0001Q\u0001\n\u0005\u0005\u0013AC:u_J,\u0007+\u0019;iA!Q\u0011\u0011\f\u0001C\u0002\u0013\u0005!!a\u0017\u0002\u000f1L7\u000f^5oOV\u0011\u0011Q\f\t\u0005\u0003?\n)'\u0004\u0002\u0002b)\u0019\u00111\r\u0012\u0002\u000f-48\u000f^8sK&!\u0011qMA1\u0005\u001dYek\u0015;pe\u0016D\u0001\"a\u001b\u0001A\u0003%\u0011QL\u0001\tY&\u001cH/\u001b8hA!I\u0011q\u000e\u0001C\u0002\u0013%\u0011\u0011O\u0001\fI&\u001c8.T1oC\u001e,'/\u0006\u0002\u0002tA)1'a\u0011\u0002vA\u0019a\"a\u001e\n\u0007\u0005e$A\u0001\rISN$xN]=TKJ4XM\u001d#jg.l\u0015M\\1hKJD\u0001\"! \u0001A\u0003%\u00111O\u0001\rI&\u001c8.T1oC\u001e,'\u000f\t\u0005\n\u0003\u0003\u0003!\u0019!C\u0005\u0003\u0007\u000b\u0011\"Y2uSZ,W+S:\u0016\u0005\u0005\u0015\u0005\u0003CAD\u0003#\u000b)*!(\u000e\u0005\u0005%%\u0002BAF\u0003\u001b\u000bq!\\;uC\ndWMC\u0002\u0002\u0010R\n!bY8mY\u0016\u001cG/[8o\u0013\u0011\t\u0019*!#\u0003\u000f!\u000b7\u000f['baB11'a&N\u00037K1!!'5\u0005\u0019!V\u000f\u001d7feA!1'a\u0011N!\rq\u0011qT\u0005\u0004\u0003C\u0013!a\u0003'pC\u0012,G-\u00119q+&C\u0001\"!*\u0001A\u0003%\u0011QQ\u0001\u000bC\u000e$\u0018N^3V\u0013N\u0004\u0003bBAU\u0001\u0011%\u00111V\u0001\nO\u0016$(+\u001e8oKJ$B!!,\u0002:B!\u0011qVA[\u001b\t\t\tL\u0003\u0003\u00024\u0006%\u0011\u0001\u00027b]\u001eLA!a.\u00022\nA!+\u001e8oC\ndW\r\u0003\u0005\u0002<\u0006\u001d\u0006\u0019AA_\u0003)y\u0007/\u001a:bi\u00164UO\u001c\t\u0006g\u0005}\u00161Y\u0005\u0004\u0003\u0003$$!\u0003$v]\u000e$\u0018n\u001c81!\r\u0019\u0014QY\u0005\u0004\u0003\u000f$$\u0001B+oSRD\u0011\"a3\u0001\u0005\u0004%I!!4\u0002\u001dI,\u0007\u000f\\1z\u000bb,7-\u001e;peV\u0011\u0011q\u001a\t\u0005\u0003\u0003\t\t.\u0003\u0003\u0002T\u0006\r!aD#yK\u000e,Ho\u001c:TKJ4\u0018nY3\t\u0011\u0005]\u0007\u0001)A\u0005\u0003\u001f\fqB]3qY\u0006LX\t_3dkR|'\u000f\t\u0005\n\u00037\u0004!\u0019!C\u0001\u0003;\f!\"\u001b8jiRC'/Z1e+\t\ty\u000e\u0005\u0003\u00020\u0006\u0005\u0018\u0002BAr\u0003c\u0013a\u0001\u00165sK\u0006$\u0007\u0002CAt\u0001\u0001\u0006I!a8\u0002\u0017%t\u0017\u000e\u001e+ie\u0016\fG\r\t\u0005\t\u0003W\u0004A\u0011\u0001\u0002\u0002n\u0006Q\u0011N\\5uS\u0006d\u0017N_3\u0015\u0005\u0005}\u0007\u0002CAy\u0001\u0011\u0005!!a=\u00021M$\u0018M\u001d;TC\u001a,Wj\u001c3f\u0007\",7m\u001b+ie\u0016\fG\r\u0006\u0003\u0002`\u0006U\b\u0002CA|\u0003_\u0004\r!!?\u0002\u0019\u0015\u0014(o\u001c:IC:$G.\u001a:\u0011\u000bM\n\u0019%a?\u0011\t\u0005u(1\u0001\b\u0005\u0003_\u000by0\u0003\u0003\u0003\u0002\u0005E\u0016A\u0002+ie\u0016\fG-\u0003\u0003\u0003\u0006\t\u001d!\u0001G+oG\u0006,x\r\u001b;Fq\u000e,\u0007\u000f^5p]\"\u000bg\u000e\u001a7fe*!!\u0011AAY\u0011\u001d\u0011Y\u0001\u0001C\u0005\u0005\u001b\tAb\u001d;beR\u0004v\u000e\u001c7j]\u001e$\"!a1\t\u000f\tE\u0001\u0001\"\u0011\u0003\u0014\u0005Qq-\u001a;MSN$\u0018N\\4\u0015\u0005\tU\u0001C\u0002B\f\u0005O\u0011iC\u0004\u0003\u0003\u001a\t\rb\u0002\u0002B\u000e\u0005Ci!A!\b\u000b\u0007\t}\u0011$\u0001\u0004=e>|GOP\u0005\u0002k%\u0019!Q\u0005\u001b\u0002\u000fA\f7m[1hK&!!\u0011\u0006B\u0016\u0005!IE/\u001a:bi>\u0014(b\u0001B\u0013iA!!q\u0006B\u001f\u001b\t\u0011\tD\u0003\u0003\u00034\tU\u0012A\u0001<2\u0015\u0011\u00119D!\u000f\u0002\u0007\u0005\u0004\u0018NC\u0002\u0003<\u0019\taa\u001d;biV\u001c\u0018\u0002\u0002B \u0005c\u0011q\"\u00119qY&\u001c\u0017\r^5p]&sgm\u001c\u0005\b\u0005\u0007\u0002A\u0011\tB#\u0003I9W\r^!qa2L7-\u0019;j_:LeNZ8\u0015\t\t\u001d#\u0011\n\t\u0006g\u0005\r#Q\u0006\u0005\b\u0005\u0017\u0012\t\u00051\u0001N\u0003\u0015\t\u0007\u000f]%e\u0011\u001d\u0011y\u0005\u0001C!\u0005#\n\u0001dZ3u\u000bZ,g\u000e\u001e'pON,f\u000eZ3s!J|7-Z:t)\u0005)\u0005b\u0002B+\u0001\u0011\u0005#qK\u0001\u0013O\u0016$H*Y:u+B$\u0017\r^3e)&lW\rF\u00013\u0011\u001d\u0011Y\u0006\u0001C!\u0005;\n\u0001bZ3u\u0003B\u0004X+\u0013\u000b\u0007\u0005?\u0012\tGa\u0019\u0011\u000bM\n\u0019%!(\t\u000f\t-#\u0011\fa\u0001\u001b\"A!Q\rB-\u0001\u0004\tY*A\u0005biR,W\u000e\u001d;JI\"9!\u0011\u000e\u0001\u0005B\t-\u0014aE4fi\u0016k\u0007\u000f^=MSN$\u0018N\\4Ii6dGC\u0001B7!\u0019\u00119Ba\u001c\u0003t%!!\u0011\u000fB\u0016\u0005\r\u0019V-\u001d\t\u0005\u0005k\u0012Y(\u0004\u0002\u0003x)\u0019!\u0011\u0010\u001b\u0002\u0007alG.\u0003\u0003\u0003~\t]$\u0001\u0002(pI\u0016DqA!!\u0001\t\u0003\u0012\u0019)A\u0005hKR\u001cuN\u001c4jOR\u0011!Q\u0011\t\u0006\u001d\n\u001dU*T\u0005\u0004\u0005\u0013\u001b&aA'ba\"9!Q\u0012\u0001\u0005B\t5\u0011\u0001B:u_BDqA!%\u0001\t\u0003\u0012\u0019*\u0001\u0007p]VKE)\u001a;bG\",G\r\u0006\u0005\u0002D\nU%q\u0013BM\u0011\u001d\u0011YEa$A\u00025C\u0001B!\u001a\u0003\u0010\u0002\u0007\u00111\u0014\u0005\t\u00057\u0013y\t1\u0001\u0003\u001e\u0006\u0011Q/\u001b\t\u0005\u0005?\u0013\u0019+\u0004\u0002\u0003\"*\u0019!1\u0014\u0004\n\t\t\u0015&\u0011\u0015\u0002\b'B\f'o[+J\u0011!\u0011I\u000b\u0001C\u0001\u0005\t5\u0011\u0001D2iK\u000e\\gi\u001c:M_\u001e\u001c\bb\u0002BW\u0001\u0011%!qV\u0001\rG2,\u0017M\\!qa\u0012\u000bG/\u0019\u000b\t\u0003\u0007\u0014\tLa-\u00036\"9!1\nBV\u0001\u0004i\u0005\u0002\u0003B3\u0005W\u0003\r!a'\t\u000f\t]&1\u0016a\u0001\u001b\u00069An\\4QCRD\u0007\u0002\u0003B^\u0001\u0011\u0005!Aa\u0016\u0002%\u001d,GOT3x\u0019\u0006\u001cHoU2b]RKW.\u001a\u0005\b\u0005\u007f\u0003A\u0011\tBa\u000399(/\u001b;f\u000bZ,g\u000e\u001e'pON$\u0002\"a1\u0003D\n\u0015'q\u0019\u0005\b\u0005\u0017\u0012i\f1\u0001N\u0011!\u0011)G!0A\u0002\u0005m\u0005\u0002\u0003Be\u0005{\u0003\rAa3\u0002\u0013iL\u0007o\u0015;sK\u0006l\u0007\u0003\u0002Bg\u0005'l!Aa4\u000b\t\tE\u0017qA\u0001\u0004u&\u0004\u0018\u0002\u0002Bk\u0005\u001f\u0014qBW5q\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\b\u00053\u0004A\u0011\u0003Bn\u0003]iWM]4f\u0003B\u0004H.[2bi&|g\u000eT5ti&tw\r\u0006\u0004\u0002D\nu'q\u001d\u0005\t\u0005?\u00149\u000e1\u0001\u0003b\u0006Qa-\u001b7f'R\fG/^:\u0011\u0007Y\u0014\u0019/C\u0002\u0003f^\u0014!BR5mKN#\u0018\r^;t\u0011\u001d\u0011IOa6A\u0002I\n\u0001b]2b]RKW.\u001a\u0005\t\u0005[\u0004A\u0011\u0001\u0002\u0003\u000e\u0005I1\r\\3b]2{wm\u001d\u0005\b\u0005c\u0004A\u0011\u0002Bz\u0003\u0019\u0011X\r\u001d7bsRA\u00111\u0019B{\u0005s\u001cI\u0001\u0003\u0005\u0003x\n=\b\u0019\u0001Bq\u0003!)g/\u001a8u\u0019><\u0007\u0002\u0003B~\u0005_\u0004\rA!@\u0002\u0007\t,8\u000f\u0005\u0003\u0003\u0000\u000e\u0015QBAB\u0001\u0015\r\u0019\u0019AB\u0001\ng\u000eDW\rZ;mKJLAaa\u0002\u0004\u0002\t\t\"+\u001a9mCfd\u0015n\u001d;f]\u0016\u0014()^:\t\u0015\r-!q\u001eI\u0001\u0002\u0004\u0019i!\u0001\u0007fm\u0016tGo\u001d$jYR,'\u000f\u0005\u0003\u0004\u0010\r\u0015b\u0002BB\t\u0007CqAaa\u0005\u0004 9!1QCB\u000f\u001d\u0011\u00199ba\u0007\u000f\t\tm1\u0011D\u0005\u0002\u0017%\u0011\u0011BC\u0005\u0003\u000f!I1aa\u0001\u0007\u0013\u0011\u0019\u0019c!\u0001\u0002#I+\u0007\u000f\\1z\u0019&\u001cH/\u001a8fe\n+8/\u0003\u0003\u0004(\r%\"A\u0005*fa2\f\u00170\u0012<f]R\u001ch)\u001b7uKJTAaa\t\u0004\u0002!91Q\u0006\u0001\u0005\n\r=\u0012a\u0004:fEVLG\u000eZ!qaN#xN]3\u0015\u0011\u0005\r7\u0011GB\u001b\u0007oA\u0001ba\r\u0004,\u0001\u0007\u0011QL\u0001\u0006gR|'/\u001a\u0005\t\u0005o\u001cY\u00031\u0001\u0003b\"91\u0011HB\u0016\u0001\u0004\u0011\u0014a\u00037bgR,\u0006\u000fZ1uK\u0012D\u0001b!\u0010\u0001\t\u0003\u00111qH\u0001\u000fSN45/\u00138TC\u001a,Wj\u001c3f)\u0005I\u0006\u0002CB\u001f\u0001\u0011\u0005!aa\u0011\u0015\u0007e\u001b)\u0005\u0003\u0005\u0004H\r\u0005\u0003\u0019AB%\u0003\r!gm\u001d\t\u0005\u0007\u0017\u001a\t&\u0004\u0002\u0004N)\u00191q\n7\u0002\t!$gm]\u0005\u0005\u0007'\u001aiEA\u000bESN$(/\u001b2vi\u0016$g)\u001b7f'f\u001cH/Z7\t\u000f\r]\u0003\u0001\"\u0011\u0004Z\u0005AAo\\*ue&tw\rF\u0001N\u0011\u001d\u0019i\u0006\u0001C\u0005\u0007?\nA\u0001\\8bIR!1\u0011MB4!\rq11M\u0005\u0004\u0007K\u0012!AF!qa2L7-\u0019;j_:LeNZ8Xe\u0006\u0004\b/\u001a:\t\u000f\t-31\fa\u0001\u001b\"911\u000e\u0001\u0005\n\r5\u0014AC1eI2K7\u000f^5oOR!\u00111YB8\u0011!\u0019\th!\u001bA\u0002\r\u0005\u0014aA1qa\"91Q\u000f\u0001\u0005\n\r]\u0014!\u00047pC\u0012$\u0015n]6Ti>\u0014X\r\u0006\u0005\u0002^\re4QPB@\u0011!\u0019Yha\u001dA\u0002\u0005U\u0014A\u00013n\u0011\u001d\u0011Yea\u001dA\u00025C\u0001b!!\u0004t\u0001\u000711Q\u0001\bCR$X-\u001c9u!\rq1QQ\u0005\u0004\u0007\u000f\u0013!AE!ui\u0016l\u0007\u000f^%oM><&/\u00199qKJDqaa#\u0001\t\u0013\u0019i)A\nde\u0016\fG/Z%o\u001b\u0016lwN]=Ti>\u0014X\r\u0006\u0003\u0002^\r=\u0005\u0002CBA\u0007\u0013\u0003\raa!\t\u000f\rM\u0005\u0001\"\u0003\u0004\u0016\u0006YAn\\1e!2,x-\u001b8t)\t\u00199\n\u0005\u0004\u0003\u0018\re5QT\u0005\u0005\u00077\u0013YC\u0001\u0005Ji\u0016\u0014\u0018M\u00197f!\u0011\u0019yj!)\u000e\u0005\te\u0012\u0002BBR\u0005s\u0011a#\u00119q\u0011&\u001cHo\u001c:z'\u0016\u0014h/\u001a:QYV<\u0017N\u001c\u0005\t\u0007O\u0003A\u0011\u0001\u0002\u0004*\u0006Qq-\u001a;BiR,W\u000e\u001d;\u0015\r\r\r51VBW\u0011\u001d\u0011Ye!*A\u00025C\u0001B!\u001a\u0004&\u0002\u0007\u00111\u0014\u0005\b\u0007c\u0003A\u0011BBZ\u0003%!W\r\\3uK2{w\r\u0006\u0003\u0002D\u000eU\u0006\u0002CB\\\u0007_\u0003\ra!/\u0002\u00071|w\rE\u0002w\u0007wK1a!0x\u0005\u0011\u0001\u0016\r\u001e5\t\u0013\r\u0005\u0007!%A\u0005\n\r\r\u0017\u0001\u0005:fa2\f\u0017\u0010\n3fM\u0006,H\u000e\u001e\u00134+\t\u0019)M\u000b\u0003\u0004\u000e\r\u001d7FABe!\u0011\u0019Ym!6\u000e\u0005\r5'\u0002BBh\u0007#\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\rMG'\u0001\u0006b]:|G/\u0019;j_:LAaa6\u0004N\n\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\b\u0011\rm'\u0001#\u0001\u0003\u0007;\f\u0011CR:ISN$xN]=Qe>4\u0018\u000eZ3s!\rq1q\u001c\u0004\b\u0003\tA\tAABq'\u0011\u0019yna9\u0011\u0007M\u001a)/C\u0002\u0004hR\u0012a!\u00118z%\u00164\u0007bB\u0014\u0004`\u0012\u000511\u001e\u000b\u0003\u0007;D!ba<\u0004`\n\u0007I\u0011BBy\u0003\r\u001a\u0006+\u0011*L?\"K5\u000bV(S3~35k\u0018(V\u001b~\u0013V\t\u0015'B3~#\u0006JU#B\tN+\"aa=\u0011\t\u0005=6Q_\u0005\u0004%\u0006E\u0006\"CB}\u0007?\u0004\u000b\u0011BBz\u0003\u0011\u001a\u0006+\u0011*L?\"K5\u000bV(S3~35k\u0018(V\u001b~\u0013V\t\u0015'B3~#\u0006JU#B\tN\u0003\u0003BCB\u007f\u0007?\u0014\r\u0011\"\u0003\u0004r\u00069\u0012\t\u0015)M?N#\u0016I\u0015+`\u000bZ+e\nV0Q%\u00163\u0015\n\u0017\u0005\n\t\u0003\u0019y\u000e)A\u0005\u0007g\f\u0001$\u0011)Q\u0019~\u001bF+\u0011*U?\u00163VI\u0014+`!J+e)\u0013-!\u0011)!)aa8C\u0002\u0013%1\u0011_\u0001\u0016\u0003B\u0003FjX#O\t~+e+\u0012(U?B\u0013VIR%Y\u0011%!Iaa8!\u0002\u0013\u0019\u00190\u0001\fB!Bcu,\u0012(E?\u00163VI\u0014+`!J+e)\u0013-!\u0011)!iaa8C\u0002\u0013%1\u0011_\u0001\u0017\u0019>;ul\u0015+B%R{VIV#O)~\u0003&+\u0012$J1\"IA\u0011CBpA\u0003%11_\u0001\u0018\u0019>;ul\u0015+B%R{VIV#O)~\u0003&+\u0012$J1\u0002B!\u0002\"\u0006\u0004`\n\u0007I\u0011BBy\u0003])eJV0V!\u0012\u000bE+R0F-\u0016sEk\u0018)S\u000b\u001aK\u0005\fC\u0005\u0005\u001a\r}\u0007\u0015!\u0003\u0004t\u0006ARI\u0014,`+B#\u0015\tV#`\u000bZ+e\nV0Q%\u00163\u0015\n\u0017\u0011\t\u0015\u0011u1q\u001cb\u0001\n\u0003\u0011\u0011'A\fD+J\u0013VI\u0014+`\u0019&\u001bF+\u0013(H?Z+%kU%P\u001d\"AA\u0011EBpA\u0003%!'\u0001\rD+J\u0013VI\u0014+`\u0019&\u001bF+\u0013(H?Z+%kU%P\u001d\u0002\u0002"
)
public class FsHistoryProvider extends ApplicationHistoryProvider implements Logging {
   public final SparkConf org$apache$spark$deploy$history$FsHistoryProvider$$conf;
   public final Clock org$apache$spark$deploy$history$FsHistoryProvider$$clock;
   private final long org$apache$spark$deploy$history$FsHistoryProvider$$SAFEMODE_CHECK_INTERVAL_S;
   private final long org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S;
   private final long CLEAN_INTERVAL_S;
   private final int NUM_PROCESSING_THREADS;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$logDir;
   private final boolean org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS;
   private final String org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS;
   private final Configuration hadoopConf;
   private final FileSystem org$apache$spark$deploy$history$FsHistoryProvider$$fs;
   private final ScheduledExecutorService pool;
   private final AtomicLong org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime;
   private final AtomicInteger org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount;
   private final Option<File> storePath;
   private final KVStore listing;
   private final Option<HistoryServerDiskManager> org$apache$spark$deploy$history$FsHistoryProvider$$diskManager;
   private final HashMap<Tuple2<String, Option<String>>, LoadedAppUI> org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs;
   private final ExecutorService org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor;
   private final Thread initThread;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public long org$apache$spark$deploy$history$FsHistoryProvider$$SAFEMODE_CHECK_INTERVAL_S() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$SAFEMODE_CHECK_INTERVAL_S;
   }

   public long org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S;
   }

   private long CLEAN_INTERVAL_S() {
      return this.CLEAN_INTERVAL_S;
   }

   private int NUM_PROCESSING_THREADS() {
      return this.NUM_PROCESSING_THREADS;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$logDir() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir;
   }

   public boolean org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS;
   }

   public String org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS;
   }

   private Configuration hadoopConf() {
      return this.hadoopConf;
   }

   public FileSystem org$apache$spark$deploy$history$FsHistoryProvider$$fs() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$fs;
   }

   private ScheduledExecutorService pool() {
      return this.pool;
   }

   public AtomicLong org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime;
   }

   public AtomicInteger org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount;
   }

   private Option<File> storePath() {
      return this.storePath;
   }

   public KVStore listing() {
      return this.listing;
   }

   public Option<HistoryServerDiskManager> org$apache$spark$deploy$history$FsHistoryProvider$$diskManager() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager;
   }

   public HashMap<Tuple2<String, Option<String>>, LoadedAppUI> org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs;
   }

   private Runnable getRunner(Function0<BoxedUnit> operateFun) {
      return new Runnable(this, operateFun) {
         private final Function0 operateFun$1;

         public void run() {
            Utils$.MODULE$.tryOrExit(this.operateFun$1);
         }

         public {
            this.operateFun$1 = operateFun$1;
         }
      };
   }

   public ExecutorService org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor;
   }

   public Thread initThread() {
      return this.initThread;
   }

   public Thread initialize() {
      Thread var10000;
      if (this.isFsInSafeMode()) {
         var10000 = this.startSafeModeCheckThread(None$.MODULE$);
      } else {
         this.org$apache$spark$deploy$history$FsHistoryProvider$$startPolling();
         var10000 = null;
      }

      return var10000;
   }

   public Thread startSafeModeCheckThread(Option<Thread.UncaughtExceptionHandler> errorHandler) {
      Thread initThread = new Thread(new Runnable(this) {
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public void run() {
            try {
               while(true) {
                  if (this.$outer.isFsInSafeMode()) {
                     this.$outer.logInfo(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "HDFS is still in safe mode. Waiting...";
                        }
                     });
                     long deadline = this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$clock.getTimeMillis() + TimeUnit.SECONDS.toMillis(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$SAFEMODE_CHECK_INTERVAL_S());
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$clock.waitTillTime(deadline);
                  } else {
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$startPolling();
                     break;
                  }
               }
            } catch (InterruptedException var3) {
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      initThread.setDaemon(true);
      initThread.setName((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "-init"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getSimpleName()})));
      initThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)errorHandler.getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public final <undefinedtype> apply() {
            return new Thread.UncaughtExceptionHandler(this) {
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public void uncaughtException(Thread t, Throwable e) {
                  this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Error initializing FsHistoryProvider.";
                     }
                  }, e);
                  System.exit(1);
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

         // $FF: synthetic method
         public FsHistoryProvider org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      initThread.start();
      return initThread;
   }

   public void org$apache$spark$deploy$history$FsHistoryProvider$$startPolling() {
      this.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(HistoryServerDiskManager x$2) {
            x$2.initialize();
         }
      });
      Path path = new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir());

      try {
         if (!this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().getFileStatus(path).isDirectory()) {
            throw new IllegalArgumentException((new StringOps(Predef$.MODULE$.augmentString("Logging directory specified is not a directory: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir()})));
         }
      } catch (FileNotFoundException var5) {
         String msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Log directory specified does not exist: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir()}));
         String var10000 = this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir();
         String var4 = config$.MODULE$.DEFAULT_LOG_DIR();
         if (var10000 == null) {
            if (var4 != null) {
               throw (new FileNotFoundException(msg)).initCause(var5);
            }
         } else if (!var10000.equals(var4)) {
            throw (new FileNotFoundException(msg)).initCause(var5);
         }

         msg = (new StringBuilder()).append((Object)msg).append((Object)" Did you configure the correct one through spark.history.fs.logDirectory?").toString();
         throw (new FileNotFoundException(msg)).initCause(var5);
      }

      if (this.org$apache$spark$deploy$history$FsHistoryProvider$$conf.contains("spark.testing")) {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Background update thread disabled for testing";
            }
         });
      } else {
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Scheduling update thread every ", " seconds"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S())}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.pool().scheduleWithFixedDelay(this.getRunner(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.$outer.checkForLogs();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }), 0L, this.org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S(), TimeUnit.SECONDS);
         if (this.org$apache$spark$deploy$history$FsHistoryProvider$$conf.getBoolean("spark.history.fs.cleaner.enabled", false)) {
            this.pool().scheduleWithFixedDelay(this.getRunner(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final FsHistoryProvider $outer;

               public final void apply() {
                  this.apply$mcV$sp();
               }

               public void apply$mcV$sp() {
                  this.$outer.cleanLogs();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }), 0L, this.CLEAN_INTERVAL_S(), TimeUnit.SECONDS);
         }
      }

   }

   public Iterator<ApplicationInfo> getListing() {
      return ((Iterator)JavaConverters$.MODULE$.asScalaIteratorConverter(this.listing().view(ApplicationInfoWrapper.class).index("endTime").reverse().iterator()).asScala()).<ApplicationInfo>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ApplicationInfo apply(ApplicationInfoWrapper x$3) {
            return x$3.toApplicationInfo();
         }
      });
   }

   public Option<ApplicationInfo> getApplicationInfo(String appId) {
      Object var10000;
      try {
         var10000 = new Some(this.load(appId).toApplicationInfo());
      } catch (NoSuchElementException var2) {
         var10000 = None$.MODULE$;
      }

      return (Option<ApplicationInfo>)var10000;
   }

   public int getEventLogsUnderProcess() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount().get();
   }

   public long getLastUpdatedTime() {
      return this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime().get();
   }

   public Option<LoadedAppUI> getAppUI(String appId, Option<String> attemptId) {
      ApplicationInfoWrapper var10000;
      try {
         var10000 = this.load(appId);
      } catch (NoSuchElementException var17) {
         return None$.MODULE$;
      }

      ApplicationInfoWrapper app = var10000;
      AttemptInfoWrapper attempt = (AttemptInfoWrapper)app.attempts().find(new Serializable(this, attemptId) {
         public static final long serialVersionUID = 0L;
         private final Option attemptId$1;

         public final boolean apply(AttemptInfoWrapper x$4) {
            boolean var3;
            label23: {
               Option var10000 = x$4.info().attemptId();
               Option var2 = this.attemptId$1;
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
            this.attemptId$1 = attemptId$1;
         }
      }).orNull(Predef$.MODULE$.$conforms());
      if (attempt == null) {
         return None$.MODULE$;
      } else {
         SparkConf conf = this.org$apache$spark$deploy$history$FsHistoryProvider$$conf.clone();
         SecurityManager secManager = new SecurityManager(conf, SecurityManager$.MODULE$.$lessinit$greater$default$2());
         secManager.setAcls(this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE());
         secManager.setAdminAcls((new StringBuilder()).append((Object)this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS()).append((Object)",").append(attempt.adminAcls().getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         })).toString());
         secManager.setViewAcls(attempt.info().sparkUser(), (String)attempt.viewAcls().getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         }));
         secManager.setAdminAclsGroups((new StringBuilder()).append((Object)this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS()).append((Object)",").append(attempt.adminAclsGroups().getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         })).toString());
         secManager.setViewAclsGroups((String)attempt.viewAclsGroups().getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "";
            }
         }));

         try {
            Option var8 = this.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager();
            KVStore var11;
            if (var8 instanceof Some) {
               Some var9 = (Some)var8;
               HistoryServerDiskManager sm = (HistoryServerDiskManager)var9.x();
               var11 = this.loadDiskStore(sm, appId, attempt);
            } else {
               var11 = this.createInMemoryStore(attempt);
            }

            var18 = var11;
         } catch (FileNotFoundException var16) {
            return None$.MODULE$;
         }

         KVStore kvstore = var18;
         SparkUI ui = SparkUI$.MODULE$.create(None$.MODULE$, new AppStatusStore(kvstore, AppStatusStore$.MODULE$.$lessinit$greater$default$2()), conf, secManager, app.info().name(), HistoryServer$.MODULE$.getAttemptURI(appId, attempt.info().attemptId()), attempt.info().startTime().getTime(), attempt.info().appSparkVersion());
         this.loadPlugins().foreach(new Serializable(this, ui) {
            public static final long serialVersionUID = 0L;
            private final SparkUI ui$1;

            public final void apply(AppHistoryServerPlugin x$5) {
               x$5.setupUI(this.ui$1);
            }

            public {
               this.ui$1 = ui$1;
            }
         });
         LoadedAppUI loadedUI = new LoadedAppUI(ui);
         synchronized(this) {
            this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().update(new Tuple2(appId, attemptId), loadedUI);
            BoxedUnit var19 = BoxedUnit.UNIT;
         }

         return new Some<LoadedAppUI>(loadedUI);
      }
   }

   public Seq<Node> getEmptyListingHtml() {
      Null var10004 = .MODULE$;
      TopScope var10005 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("\n      Did you specify the correct logging directory? Please verify your setting of\n      "));
      MetaData $md = .MODULE$;
      MetaData var4 = new UnprefixedAttribute("style", new Text("font-style:italic"), $md);
      TopScope var10013 = scala.xml.TopScope..MODULE$;
      NodeBuffer $buf = new NodeBuffer();
      $buf.$amp$plus(new Text("spark.history.fs.logDirectory"));
      $buf.$amp$plus(new Elem((String)null, "span", var4, var10013, false, $buf));
      $buf.$amp$plus(new Text("\n      listed above and whether you have the permissions to access it.\n      "));
      $buf.$amp$plus(new Elem((String)null, "br", .MODULE$, scala.xml.TopScope..MODULE$, true, Predef$.MODULE$.wrapRefArray((Object[])(new Node[0]))));
      $buf.$amp$plus(new Text("\n      It is also possible that your application did not run to\n      completion or did not stop the SparkContext.\n    "));
      return new Elem((String)null, "p", var10004, var10005, false, $buf);
   }

   public Map<String, String> getConfig() {
      Map safeMode = this.isFsInSafeMode() ? (Map)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("HDFS State"), "In safe mode, application logs not available.")})) : (Map)Predef$.MODULE$.Map().apply(Nil$.MODULE$);
      return ((MapLike)Predef$.MODULE$.Map().apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc("Event log directory"), this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir().toString())}))).$plus$plus(safeMode);
   }

   public void stop() {
      try {
         if (this.initThread() != null && this.initThread().isAlive()) {
            this.initThread().interrupt();
            this.initThread().join();
         }

         ((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new ExecutorService[]{this.pool(), this.org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor()})))).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Object apply(ExecutorService executor) {
               executor.shutdown();
               return executor.awaitTermination(5L, TimeUnit.SECONDS) ? BoxedUnit.UNIT : executor.shutdownNow();
            }
         });
      } finally {
         this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(Tuple2<Tuple2<String, Option<String>>, LoadedAppUI> x0$1) {
               if (x0$1 != null) {
                  LoadedAppUI loadedUI = (LoadedAppUI)x0$1._2();
                  loadedUI.ui().store().close();
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  throw new MatchError(x0$1);
               }
            }
         });
         this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().clear();
         this.listing().close();
      }

   }

   public void onUIDetached(String appId, Option<String> attemptId, SparkUI ui) {
      Option var6;
      synchronized(this) {
         var6 = this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().remove(new Tuple2(appId, attemptId));
      }

      Option uiOption = var6;
      uiOption.foreach(new Serializable(this, appId, attemptId) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;
         public final String appId$2;
         public final Option attemptId$2;

         public final void apply(LoadedAppUI loadedUI) {
            loadedUI.lock().writeLock().lock();

            try {
               loadedUI.ui().store().close();
            } finally {
               loadedUI.lock().writeLock().unlock();
            }

            this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager().foreach(new Serializable(this, loadedUI) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final LoadedAppUI loadedUI$1;

               public final void apply(HistoryServerDiskManager dm) {
                  dm.release(this.$outer.appId$2, this.$outer.attemptId$2, !this.loadedUI$1.valid());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.loadedUI$1 = loadedUI$1;
                  }
               }
            });
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.appId$2 = appId$2;
               this.attemptId$2 = attemptId$2;
            }
         }
      });
   }

   public void checkForLogs() {
      try {
         long newLastScanTime = this.getNewLastScanTime();
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Scanning ", " with lastScanTime==", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime()}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         Seq updated = (Seq)((SeqLike)((TraversableLike)((TraversableLike)Option$.MODULE$.apply(this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().listStatus(new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir()))).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Seq<FileStatus> apply(FileStatus[] x$6) {
               return Predef$.MODULE$.refArrayOps((Object[])x$6).toSeq();
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Nil$ apply() {
               return Nil$.MODULE$;
            }
         })).filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(FileStatus entry) {
               return !entry.isDirectory() && !entry.getPath().getName().startsWith(".") && SparkHadoopUtil$.MODULE$.get().checkAccessPermission(entry, FsAction.READ);
            }
         })).filter(new Serializable(this, newLastScanTime) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;
            private final long newLastScanTime$1;

            public final boolean apply(FileStatus entry) {
               boolean var10000;
               try {
                  LogInfo info = (LogInfo)this.$outer.listing().read(LogInfo.class, entry.getPath().toString());
                  if (info.fileSize() < entry.getLen()) {
                     var10000 = true;
                  } else {
                     if (info.appId().isDefined()) {
                        KVStore var11 = this.$outer.listing();
                        long x$20 = this.newLastScanTime$1;
                        String x$21 = info.copy$default$1();
                        Option x$22 = info.copy$default$3();
                        Option x$23 = info.copy$default$4();
                        long x$24 = info.copy$default$5();
                        var11.write(info.copy(x$21, x$20, x$22, x$23, x$24));
                     }

                     var10000 = false;
                  }
               } catch (NoSuchElementException var10) {
                  this.$outer.listing().write(new LogInfo(entry.getPath().toString(), this.newLastScanTime$1, None$.MODULE$, None$.MODULE$, entry.getLen()));
                  var10000 = entry.getLen() > 0L;
               }

               return var10000;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.newLastScanTime$1 = newLastScanTime$1;
               }
            }
         })).sortWith(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(FileStatus x0$2, FileStatus x1$1) {
               Tuple2 var3 = new Tuple2(x0$2, x1$1);
               if (var3 != null) {
                  FileStatus entry1 = (FileStatus)var3._1();
                  FileStatus entry2 = (FileStatus)var3._2();
                  boolean var6 = entry1.getModificationTime() > entry2.getModificationTime();
                  return var6;
               } else {
                  throw new MatchError(var3);
               }
            }
         });
         if (updated.nonEmpty()) {
            this.logDebug(new Serializable(this, updated) {
               public static final long serialVersionUID = 0L;
               private final Seq updated$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"New/updated attempts found: ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.updated$1.size()), this.updated$1.map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Path apply(FileStatus x$7) {
                        return x$7.getPath();
                     }
                  }, Seq$.MODULE$.canBuildFrom())}));
               }

               public {
                  this.updated$1 = updated$1;
               }
            });
         }

         Seq tasks = (Seq)((TraversableLike)updated.map(new Serializable(this, newLastScanTime) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;
            public final long newLastScanTime$1;

            public final Future<?> apply(FileStatus entry) {
               Future var10000;
               try {
                  var10000 = this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor().submit(new Runnable(this, entry) {
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final FileStatus entry$1;

                     public void run() {
                        this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().mergeApplicationListing(this.entry$1, this.$outer.newLastScanTime$1);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.entry$1 = entry$1;
                        }
                     }
                  });
               } catch (Exception var3) {
                  this.$outer.logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception while submitting event log for replay"})))).s(Nil$.MODULE$);
                     }
                  }, var3);
                  var10000 = null;
               }

               return var10000;
            }

            // $FF: synthetic method
            public FsHistoryProvider org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.newLastScanTime$1 = newLastScanTime$1;
               }
            }
         }, Seq$.MODULE$.canBuildFrom())).filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Future<?> x$8) {
               return x$8 != null;
            }
         });
         this.org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount().addAndGet(tasks.size());
         tasks.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final Object apply(Future<?> task) {
               Object var10000;
               try {
                  var10000 = task.get();
               } catch (InterruptedException var8) {
                  throw var8;
               } catch (Exception var9) {
                  this.$outer.logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Exception while merging application listings";
                     }
                  }, var9);
                  var10000 = BoxedUnit.UNIT;
               } finally {
                  this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount().decrementAndGet();
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
         List stale = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.listing().view(LogInfo.class).index("lastProcessed").last(BoxesRunTime.boxToLong(newLastScanTime - 1L))).asScala()).toList();
         stale.foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final void apply(LogInfo log) {
               log.appId().foreach(new Serializable(this, log) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final LogInfo log$2;

                  public final void apply(String appId) {
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$cleanAppData(appId, this.log$2.attemptId(), this.log$2.logPath());
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().listing().delete(LogInfo.class, this.log$2.logPath());
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.log$2 = log$2;
                     }
                  }
               });
            }

            // $FF: synthetic method
            public FsHistoryProvider org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer() {
               return this.$outer;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime().set(newLastScanTime);
      } catch (Exception var7) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception in checking for event log updates";
            }
         }, var7);
      }

   }

   public void org$apache$spark$deploy$history$FsHistoryProvider$$cleanAppData(String appId, Option<String> attemptId, String logPath) {
      try {
         ApplicationInfoWrapper app = this.load(appId);
         Tuple2 var6 = app.attempts().partition(new Serializable(this, attemptId) {
            public static final long serialVersionUID = 0L;
            private final Option attemptId$3;

            public final boolean apply(AttemptInfoWrapper x$9) {
               boolean var3;
               label23: {
                  Option var10000 = x$9.info().attemptId();
                  Option var2 = this.attemptId$3;
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
               this.attemptId$3 = attemptId$3;
            }
         });
         if (var6 == null) {
            throw new MatchError(var6);
         }

         List attempt = (List)var6._1();
         List others = (List)var6._2();
         Tuple2 var9 = new Tuple2(attempt, others);
         List attempt = (List)var9._1();
         List others = (List)var9._2();
         Predef$.MODULE$.assert(attempt.isEmpty() || attempt.size() == 1);
         boolean isStale = attempt.headOption().exists(new Serializable(this, appId, attemptId, logPath) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;
            public final String appId$3;
            public final Option attemptId$3;
            private final String logPath$1;

            public final boolean apply(AttemptInfoWrapper a) {
               boolean var7;
               label33: {
                  String var10000 = a.logPath();
                  String var2 = (new Path(this.logPath$1)).getName();
                  if (var10000 == null) {
                     if (var2 != null) {
                        break label33;
                     }
                  } else if (!var10000.equals(var2)) {
                     break label33;
                  }

                  Option var5;
                  synchronized(this.$outer) {
                     var5 = this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().remove(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(this.appId$3), this.attemptId$3));
                  }

                  Option maybeUI = var5;
                  maybeUI.foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply(LoadedAppUI ui) {
                        ui.invalidate();
                        ui.ui().store().close();
                     }
                  });
                  this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager().foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply(HistoryServerDiskManager x$11) {
                        x$11.release(this.$outer.appId$3, this.$outer.attemptId$3, true);
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  var7 = true;
                  return var7;
               }

               var7 = false;
               return var7;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.appId$3 = appId$3;
                  this.attemptId$3 = attemptId$3;
                  this.logPath$1 = logPath$1;
               }
            }
         });
         if (isStale) {
            if (others.nonEmpty()) {
               ApplicationInfoWrapper newAppInfo = new ApplicationInfoWrapper(app.info(), others);
               this.listing().write(newAppInfo);
            } else {
               this.listing().delete(ApplicationInfoWrapper.class, appId);
            }
         }
      } catch (NoSuchElementException var14) {
      }

   }

   public long getNewLastScanTime() {
      String fileName = (new StringBuilder()).append((Object)".").append((Object)UUID.randomUUID().toString()).toString();
      Path path = new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), fileName);
      FSDataOutputStream fos = this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().create(path);

      long var10000;
      try {
         fos.close();
         var10000 = this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().getFileStatus(path).getModificationTime();
      } catch (Exception var8) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception encountered when attempting to update last scan time";
            }
         }, var8);
         var10000 = this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime().get();
      } finally {
         if (!this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().delete(path, true)) {
            this.logWarning(new Serializable(this, path) {
               public static final long serialVersionUID = 0L;
               private final Path path$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$1}));
               }

               public {
                  this.path$1 = path$1;
               }
            });
         }

      }

      return var10000;
   }

   public void writeEventLogs(String appId, Option<String> attemptId, ZipOutputStream zipStream) {
      ApplicationInfoWrapper var10000;
      try {
         var10000 = this.load(appId);
      } catch (NoSuchElementException var9) {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Logs for ", " not found."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{appId})));
      }

      ApplicationInfoWrapper app = var10000;

      try {
         ((List)((List)attemptId.map(new Serializable(this, app) {
            public static final long serialVersionUID = 0L;
            private final ApplicationInfoWrapper app$1;

            public final List<AttemptInfoWrapper> apply(String id) {
               return (List)this.app$1.attempts().filter(new Serializable(this, id) {
                  public static final long serialVersionUID = 0L;
                  private final String id$1;

                  public final boolean apply(AttemptInfoWrapper x$12) {
                     boolean var3;
                     label23: {
                        Option var10000 = x$12.info().attemptId();
                        Some var2 = new Some(this.id$1);
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
                     this.id$1 = id$1;
                  }
               });
            }

            public {
               this.app$1 = app$1;
            }
         }).getOrElse(new Serializable(this, app) {
            public static final long serialVersionUID = 0L;
            private final ApplicationInfoWrapper app$1;

            public final List<AttemptInfoWrapper> apply() {
               return this.app$1.attempts();
            }

            public {
               this.app$1 = app$1;
            }
         })).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply(AttemptInfoWrapper x$13) {
               return x$13.logPath();
            }
         }, List$.MODULE$.canBuildFrom())).foreach(new Serializable(this, zipStream) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;
            private final ZipOutputStream zipStream$1;

            public final void apply(String log) {
               this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$zipFileToStream$1(new Path(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), log), log, this.zipStream$1);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.zipStream$1 = zipStream$1;
               }
            }
         });
      } finally {
         zipStream.close();
      }

   }

   public void mergeApplicationListing(FileStatus fileStatus, long scanTime) {
      Function1 eventsFilter = new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(String eventString) {
            return eventString.startsWith(FsHistoryProvider$.MODULE$.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_START_EVENT_PREFIX()) || eventString.startsWith(FsHistoryProvider$.MODULE$.org$apache$spark$deploy$history$FsHistoryProvider$$APPL_END_EVENT_PREFIX()) || eventString.startsWith(FsHistoryProvider$.MODULE$.org$apache$spark$deploy$history$FsHistoryProvider$$LOG_START_EVENT_PREFIX()) || eventString.startsWith(FsHistoryProvider$.MODULE$.org$apache$spark$deploy$history$FsHistoryProvider$$ENV_UPDATE_EVENT_PREFIX());
         }
      };
      Path logPath = fileStatus.getPath();
      ReplayListenerBus bus = new ReplayListenerBus();
      AppListingListener listener = new AppListingListener(fileStatus, this.org$apache$spark$deploy$history$FsHistoryProvider$$clock);
      bus.addListener(listener);
      this.replay(fileStatus, bus, eventsFilter);
      Option var10 = listener.applicationInfo();
      Tuple2 var13;
      if (var10 instanceof Some) {
         Some var11 = (Some)var10;
         ApplicationInfoWrapper app = (ApplicationInfoWrapper)var11.x();
         synchronized(this) {
            this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs().get(new Tuple2(app.info().id(), ((AttemptInfoWrapper)app.attempts().head()).info().attemptId())).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final void apply(LoadedAppUI ui) {
                  ui.invalidate();
                  ui.ui().store().close();
               }
            });
            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         this.addListing(app);
         var13 = new Tuple2(new Some(app.info().id()), ((AttemptInfoWrapper)app.attempts().head()).info().attemptId());
      } else {
         var13 = new Tuple2(None$.MODULE$, None$.MODULE$);
      }

      if (var13 != null) {
         Option appId = (Option)var13._1();
         Option attemptId = (Option)var13._2();
         Tuple2 var17 = new Tuple2(appId, attemptId);
         Option appId = (Option)var17._1();
         Option attemptId = (Option)var17._2();
         this.listing().write(new LogInfo(logPath.toString(), scanTime, appId, attemptId, fileStatus.getLen()));
      } else {
         throw new MatchError(var13);
      }
   }

   public void cleanLogs() {
      Utils$.MODULE$.tryLog(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            long maxTime = this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$clock.getTimeMillis() - BoxesRunTime.unboxToLong(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$conf.get(config$.MODULE$.MAX_LOG_AGE_S())) * 1000L;
            List expired = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.$outer.listing().view(ApplicationInfoWrapper.class).index("oldestAttempt").reverse().first(BoxesRunTime.boxToLong(maxTime))).asScala()).toList();
            expired.foreach(new Serializable(this, maxTime) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               public final long maxTime$1;

               public final void apply(ApplicationInfoWrapper app) {
                  Tuple2 var3 = app.attempts().partition(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final boolean apply(AttemptInfoWrapper attempt) {
                        return attempt.info().lastUpdated().getTime() >= this.$outer.maxTime$1;
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  if (var3 != null) {
                     List remaining = (List)var3._1();
                     List toDelete = (List)var3._2();
                     Tuple2 var6 = new Tuple2(remaining, toDelete);
                     List remaining = (List)var6._1();
                     List toDelete = (List)var6._2();
                     if (remaining.nonEmpty()) {
                        ApplicationInfoWrapper newApp = new ApplicationInfoWrapper(app.info(), remaining);
                        this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().listing().write(newApp);
                     }

                     toDelete.foreach(new Serializable(this, app) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final ApplicationInfoWrapper app$3;

                        public final void apply(AttemptInfoWrapper attempt) {
                           this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().logInfo(new Serializable(this, attempt) {
                              public static final long serialVersionUID = 0L;
                              private final AttemptInfoWrapper attempt$3;

                              public final String apply() {
                                 return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleting expired event log for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.attempt$3.logPath()}));
                              }

                              public {
                                 this.attempt$3 = attempt$3;
                              }
                           });
                           Path logPath = new Path(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), attempt.logPath());
                           this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().listing().delete(LogInfo.class, logPath.toString());
                           this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$cleanAppData(this.app$3.id(), attempt.info().attemptId(), logPath.toString());
                           this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$deleteLog(logPath);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.app$3 = app$3;
                           }
                        }
                     });
                     if (remaining.isEmpty()) {
                        this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().listing().delete(app.getClass(), app.id());
                     }

                  } else {
                     throw new MatchError(var3);
                  }
               }

               // $FF: synthetic method
               public <undefinedtype> org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.maxTime$1 = maxTime$1;
                  }
               }
            });
            List stale = ((TraversableOnce)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(this.$outer.listing().view(LogInfo.class).index("lastProcessed").reverse().first(BoxesRunTime.boxToLong(maxTime))).asScala()).toList();
            stale.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(LogInfo log) {
                  if (log.appId().isEmpty()) {
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().logInfo(new Serializable(this, log) {
                        public static final long serialVersionUID = 0L;
                        private final LogInfo log$3;

                        public final String apply() {
                           return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Deleting invalid / corrupt event log ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.log$3.logPath()}));
                        }

                        public {
                           this.log$3 = log$3;
                        }
                     });
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().org$apache$spark$deploy$history$FsHistoryProvider$$deleteLog(new Path(log.logPath()));
                     this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer().listing().delete(LogInfo.class, log.logPath());
                  }

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

         // $FF: synthetic method
         public FsHistoryProvider org$apache$spark$deploy$history$FsHistoryProvider$$anonfun$$$outer() {
            return this.$outer;
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

   private void replay(FileStatus eventLog, ReplayListenerBus bus, Function1<String, Object> eventsFilter) {
      Path logPath = eventLog.getPath();
      boolean isCompleted = !logPath.getName().endsWith(EventLoggingListener$.MODULE$.IN_PROGRESS());
      this.logInfo(new Serializable(this, logPath) {
         public static final long serialVersionUID = 0L;
         private final Path logPath$2;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Replaying log path: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.logPath$2}));
         }

         public {
            this.logPath$2 = logPath$2;
         }
      });
      Utils$.MODULE$.tryWithResource(new Serializable(this, logPath) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;
         private final Path logPath$2;

         public final InputStream apply() {
            return EventLoggingListener$.MODULE$.openEventLog(this.logPath$2, this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$fs());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.logPath$2 = logPath$2;
            }
         }
      }, new Serializable(this, bus, eventsFilter, logPath, isCompleted) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;
         private final ReplayListenerBus bus$1;
         private final Function1 eventsFilter$1;
         public final Path logPath$2;
         private final boolean isCompleted$1;

         public final void apply(InputStream in) {
            this.bus$1.replay(in, this.logPath$2.toString(), !this.isCompleted$1, this.eventsFilter$1);
            this.$outer.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished parsing ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.logPath$2}));
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

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.bus$1 = bus$1;
               this.eventsFilter$1 = eventsFilter$1;
               this.logPath$2 = logPath$2;
               this.isCompleted$1 = isCompleted$1;
            }
         }
      });
   }

   private Function1<String, Object> replay$default$3() {
      return ReplayListenerBus$.MODULE$.SELECT_ALL_FILTER();
   }

   public void org$apache$spark$deploy$history$FsHistoryProvider$$rebuildAppStore(KVStore store, FileStatus eventLog, long lastUpdated) {
      SparkConf replayConf = this.org$apache$spark$deploy$history$FsHistoryProvider$$conf.clone().set((ConfigEntry)org.apache.spark.status.config$.MODULE$.ASYNC_TRACKING_ENABLED(), (Object)BoxesRunTime.boxToBoolean(false));
      ElementTrackingStore trackingStore = new ElementTrackingStore(store, replayConf);
      ReplayListenerBus replayBus = new ReplayListenerBus();
      AppStatusListener listener = new AppStatusListener(trackingStore, replayConf, false, new Some(BoxesRunTime.boxToLong(lastUpdated)));
      replayBus.addListener(listener);
      this.loadPlugins().foreach(new Serializable(this, trackingStore, replayBus) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;
         private final ElementTrackingStore trackingStore$1;
         public final ReplayListenerBus replayBus$1;

         public final void apply(AppHistoryServerPlugin plugin) {
            plugin.createListeners(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$conf, this.trackingStore$1).foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final void apply(SparkListener listener) {
                  this.$outer.replayBus$1.addListener(listener);
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

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.trackingStore$1 = trackingStore$1;
               this.replayBus$1 = replayBus$1;
            }
         }
      });

      try {
         this.replay(eventLog, replayBus, this.replay$default$3());
         trackingStore.close(false);
      } catch (Exception var10) {
         Utils$.MODULE$.tryLogNonFatalError(new Serializable(this, trackingStore) {
            public static final long serialVersionUID = 0L;
            private final ElementTrackingStore trackingStore$1;

            public final void apply() {
               this.apply$mcV$sp();
            }

            public void apply$mcV$sp() {
               this.trackingStore$1.close();
            }

            public {
               this.trackingStore$1 = trackingStore$1;
            }
         });
         throw var10;
      }
   }

   public boolean isFsInSafeMode() {
      FileSystem var1 = this.org$apache$spark$deploy$history$FsHistoryProvider$$fs();
      boolean var3;
      if (var1 instanceof DistributedFileSystem) {
         DistributedFileSystem var2 = (DistributedFileSystem)var1;
         var3 = this.isFsInSafeMode(var2);
      } else {
         var3 = false;
      }

      return var3;
   }

   public boolean isFsInSafeMode(DistributedFileSystem dfs) {
      return dfs.setSafeMode(SafeModeAction.SAFEMODE_GET, true);
   }

   public String toString() {
      long count = this.listing().count(ApplicationInfoWrapper.class);
      return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"|FsHistoryProvider{logdir=", ",\n        |  storedir=", ",\n        |  last scan time=", "\n        |  application count=", "}"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), this.storePath(), this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime(), BoxesRunTime.boxToLong(count)}))))).stripMargin();
   }

   private ApplicationInfoWrapper load(String appId) {
      return (ApplicationInfoWrapper)this.listing().read(ApplicationInfoWrapper.class, appId);
   }

   private void addListing(ApplicationInfoWrapper app) {
      synchronized(this.listing()) {
         AttemptInfoWrapper attempt = (AttemptInfoWrapper)app.attempts().head();
         ApplicationInfoWrapper oldApp = this.liftedTree1$1(app);
         List attempts = (List)((List)oldApp.attempts().filter(new Serializable(this, attempt) {
            public static final long serialVersionUID = 0L;
            private final AttemptInfoWrapper attempt$2;

            public final boolean apply(AttemptInfoWrapper x$16) {
               boolean var3;
               label23: {
                  Option var10000 = x$16.info().attemptId();
                  Option var2 = this.attempt$2.info().attemptId();
                  if (var10000 == null) {
                     if (var2 == null) {
                        break label23;
                     }
                  } else if (var10000.equals(var2)) {
                     break label23;
                  }

                  var3 = true;
                  return var3;
               }

               var3 = false;
               return var3;
            }

            public {
               this.attempt$2 = attempt$2;
            }
         })).$plus$plus(List$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new AttemptInfoWrapper[]{attempt})), List$.MODULE$.canBuildFrom());
         ApplicationInfoWrapper newAppInfo = new ApplicationInfoWrapper(app.info(), (List)attempts.sortWith(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;

            public final boolean apply(AttemptInfoWrapper a1, AttemptInfoWrapper a2) {
               return this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$compareAttemptInfo$1(a1, a2);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }));
         this.listing().write(newAppInfo);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   private KVStore loadDiskStore(HistoryServerDiskManager dm, String appId, AttemptInfoWrapper attempt) {
      Object var4 = new Object();

      Object var10000;
      try {
         AppStatusStoreMetadata metadata = new AppStatusStoreMetadata(AppStatusStore$.MODULE$.CURRENT_VERSION());
         dm.openStore(appId, attempt.info().attemptId()).foreach(new Serializable(this, dm, appId, attempt, metadata, var4) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final FsHistoryProvider $outer;
            private final HistoryServerDiskManager dm$1;
            public final String appId$1;
            public final AttemptInfoWrapper attempt$1;
            private final AppStatusStoreMetadata metadata$1;
            private final Object nonLocalReturnKey1$1;

            public final void apply(File path) {
               try {
                  throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, KVUtils$.MODULE$.open(path, this.metadata$1, ClassTag$.MODULE$.apply(AppStatusStoreMetadata.class)));
               } catch (Exception var3) {
                  this.$outer.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to open existing store for ", "/", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.appId$1, this.$outer.attempt$1.info().attemptId()}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  }, var3);
                  this.dm$1.release(this.appId$1, this.attempt$1.info().attemptId(), true);
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.dm$1 = dm$1;
                  this.appId$1 = appId$1;
                  this.attempt$1 = attempt$1;
                  this.metadata$1 = metadata$1;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         FileStatus status = this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().getFileStatus(new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), attempt.logPath()));
         boolean isCompressed = EventLoggingListener$.MODULE$.codecName(status.getPath()).flatMap(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Option<String> apply(String name) {
               return Try$.MODULE$.<String>apply(new Serializable(this, name) {
                  public static final long serialVersionUID = 0L;
                  private final String name$1;

                  public final String apply() {
                     return CompressionCodec$.MODULE$.getShortName(this.name$1);
                  }

                  public {
                     this.name$1 = name$1;
                  }
               }).toOption();
            }
         }).isDefined();
         this.logInfo(new Serializable(this, appId, attempt) {
            public static final long serialVersionUID = 0L;
            private final String appId$1;
            private final AttemptInfoWrapper attempt$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Leasing disk manager space for app ", " / ", "..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.appId$1, this.attempt$1.info().attemptId()}));
            }

            public {
               this.appId$1 = appId$1;
               this.attempt$1 = attempt$1;
            }
         });
         HistoryServerDiskManager.Lease lease = dm.lease(status.getLen(), isCompressed);

         try {
            Utils$.MODULE$.tryWithResource(new Serializable(this, metadata, lease) {
               public static final long serialVersionUID = 0L;
               private final AppStatusStoreMetadata metadata$1;
               private final HistoryServerDiskManager.Lease lease$1;

               public final LevelDB apply() {
                  return KVUtils$.MODULE$.open(this.lease$1.tmpPath(), this.metadata$1, ClassTag$.MODULE$.apply(AppStatusStoreMetadata.class));
               }

               public {
                  this.metadata$1 = metadata$1;
                  this.lease$1 = lease$1;
               }
            }, new Serializable(this, attempt, status) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final FsHistoryProvider $outer;
               private final AttemptInfoWrapper attempt$1;
               private final FileStatus status$1;

               public final void apply(LevelDB store) {
                  this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$rebuildAppStore(store, this.status$1, this.attempt$1.info().lastUpdated().getTime());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.attempt$1 = attempt$1;
                     this.status$1 = status$1;
                  }
               }
            });
            var14 = lease.commit(appId, attempt.info().attemptId());
         } catch (Exception var12) {
            lease.rollback();
            throw var12;
         }

         File newStorePath = var14;
         var10000 = KVUtils$.MODULE$.open(newStorePath, metadata, ClassTag$.MODULE$.apply(AppStatusStoreMetadata.class));
      } catch (NonLocalReturnControl var13) {
         if (var13.key() != var4) {
            throw var13;
         }

         var10000 = (KVStore)var13.value();
      }

      return (KVStore)var10000;
   }

   private KVStore createInMemoryStore(AttemptInfoWrapper attempt) {
      InMemoryStore store = new InMemoryStore();
      FileStatus status = this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().getFileStatus(new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir(), attempt.logPath()));
      this.org$apache$spark$deploy$history$FsHistoryProvider$$rebuildAppStore(store, status, attempt.info().lastUpdated().getTime());
      return store;
   }

   private Iterable<AppHistoryServerPlugin> loadPlugins() {
      return (Iterable)JavaConverters$.MODULE$.iterableAsScalaIterableConverter(ServiceLoader.load(AppHistoryServerPlugin.class, Utils$.MODULE$.getContextOrSparkClassLoader())).asScala();
   }

   public AttemptInfoWrapper getAttempt(String appId, Option<String> attemptId) {
      return (AttemptInfoWrapper)this.load(appId).attempts().find(new Serializable(this, attemptId) {
         public static final long serialVersionUID = 0L;
         private final Option attemptId$4;

         public final boolean apply(AttemptInfoWrapper x$17) {
            boolean var3;
            label23: {
               Option var10000 = x$17.info().attemptId();
               Option var2 = this.attemptId$4;
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
            this.attemptId$4 = attemptId$4;
         }
      }).getOrElse(new Serializable(this, appId, attemptId) {
         public static final long serialVersionUID = 0L;
         private final String appId$4;
         private final Option attemptId$4;

         public final Nothing$ apply() {
            throw new NoSuchElementException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot find attempt ", " of ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.attemptId$4, this.appId$4})));
         }

         public {
            this.appId$4 = appId$4;
            this.attemptId$4 = attemptId$4;
         }
      });
   }

   public void org$apache$spark$deploy$history$FsHistoryProvider$$deleteLog(Path log) {
      try {
         this.org$apache$spark$deploy$history$FsHistoryProvider$$fs().delete(log, true);
      } catch (AccessControlException var3) {
         this.logInfo(new Serializable(this, log) {
            public static final long serialVersionUID = 0L;
            private final Path log$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No permission to delete ", ", ignoring."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.log$1}));
            }

            public {
               this.log$1 = log$1;
            }
         });
      } catch (IOException var4) {
         this.logError(new Serializable(this, log) {
            public static final long serialVersionUID = 0L;
            private final Path log$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"IOException in cleaning ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.log$1}));
            }

            public {
               this.log$1 = log$1;
            }
         }, var4);
      }

   }

   public final void org$apache$spark$deploy$history$FsHistoryProvider$$zipFileToStream$1(Path file, String entryName, ZipOutputStream outputStream) {
      FileSystem fs = file.getFileSystem(this.hadoopConf());
      FSDataInputStream inputStream = fs.open(file, 1048576);

      try {
         outputStream.putNextEntry(new ZipEntry(entryName));
         ByteStreams.copy(inputStream, outputStream);
         outputStream.closeEntry();
      } finally {
         inputStream.close();
      }

   }

   private final ApplicationInfoWrapper liftedTree1$1(ApplicationInfoWrapper app$2) {
      ApplicationInfoWrapper var10000;
      try {
         var10000 = this.load(app$2.id());
      } catch (NoSuchElementException var2) {
         var10000 = app$2;
      }

      return var10000;
   }

   public final boolean org$apache$spark$deploy$history$FsHistoryProvider$$compareAttemptInfo$1(AttemptInfoWrapper a1, AttemptInfoWrapper a2) {
      return a1.info().startTime().getTime() > a2.info().startTime().getTime();
   }

   public FsHistoryProvider(SparkConf conf, Clock clock) {
      this.org$apache$spark$deploy$history$FsHistoryProvider$$conf = conf;
      this.org$apache$spark$deploy$history$FsHistoryProvider$$clock = clock;
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$history$FsHistoryProvider$$SAFEMODE_CHECK_INTERVAL_S = conf.getTimeAsSeconds("spark.history.fs.safemodeCheck.interval", "5s");
      this.org$apache$spark$deploy$history$FsHistoryProvider$$UPDATE_INTERVAL_S = conf.getTimeAsSeconds("spark.history.fs.update.interval", "10s");
      this.CLEAN_INTERVAL_S = conf.getTimeAsSeconds("spark.history.fs.cleaner.interval", "1d");
      this.NUM_PROCESSING_THREADS = conf.getInt(FsHistoryProvider$.MODULE$.org$apache$spark$deploy$history$FsHistoryProvider$$SPARK_HISTORY_FS_NUM_REPLAY_THREADS(), (int)Math.ceil((double)((float)Runtime.getRuntime().availableProcessors() / 4.0F)));
      this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir = (String)conf.get(config$.MODULE$.EVENT_LOG_DIR());
      this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE = conf.getBoolean("spark.history.ui.acls.enable", false);
      this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS = conf.get("spark.history.ui.admin.acls", "");
      this.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS = conf.get("spark.history.ui.admin.acls.groups", "");
      this.logInfo(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"History server ui acls "})))).s(Nil$.MODULE$)).append((Object)(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ACLS_ENABLE() ? "enabled" : "disabled")).append((Object)"; users with admin permissions: ").append((Object)this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS().toString()).append((Object)"; groups with admin permissions").append((Object)this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$HISTORY_UI_ADMIN_ACLS_GROUPS().toString()).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.hadoopConf = SparkHadoopUtil$.MODULE$.get().newConfiguration(conf);
      this.org$apache$spark$deploy$history$FsHistoryProvider$$fs = (new Path(this.org$apache$spark$deploy$history$FsHistoryProvider$$logDir())).getFileSystem(this.hadoopConf());
      this.pool = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("spark-history-task-%d");
      this.org$apache$spark$deploy$history$FsHistoryProvider$$lastScanTime = new AtomicLong(-1L);
      this.org$apache$spark$deploy$history$FsHistoryProvider$$pendingReplayTasksCount = new AtomicInteger(0);
      this.storePath = ((Option)conf.get((ConfigEntry)config$.MODULE$.LOCAL_STORE_DIR())).<File>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final File apply(String x$1) {
            return new File(x$1);
         }
      });
      this.listing = (KVStore)this.storePath().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public final LevelDB apply(File path) {
            Predef$.MODULE$.require(path.isDirectory(), new Serializable(this, path) {
               public static final long serialVersionUID = 0L;
               private final File path$2;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Configured store directory (", ") does not exist."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$2}));
               }

               public {
                  this.path$2 = path$2;
               }
            });
            File dbPath = new File(path, "listing.ldb");
            FsHistoryProviderMetadata metadata = new FsHistoryProviderMetadata(FsHistoryProvider$.MODULE$.CURRENT_LISTING_VERSION(), AppStatusStore$.MODULE$.CURRENT_VERSION(), this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$logDir().toString());

            LevelDB var10000;
            try {
               var10000 = KVUtils$.MODULE$.open(dbPath, metadata, ClassTag$.MODULE$.apply(FsHistoryProviderMetadata.class));
            } catch (Throwable var10) {
               boolean var6;
               if (var10 instanceof UnsupportedStoreVersionException) {
                  var6 = true;
               } else if (var10 instanceof KVUtils.MetadataMismatchException) {
                  var6 = true;
               } else {
                  var6 = false;
               }

               LevelDB var7;
               if (var6) {
                  this.$outer.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Detected incompatible DB versions, deleting...";
                     }
                  });
                  Predef$.MODULE$.refArrayOps((Object[])path.listFiles()).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final void apply(File file) {
                        Utils$.MODULE$.deleteRecursively(file);
                     }
                  });
                  var7 = KVUtils$.MODULE$.open(dbPath, metadata, ClassTag$.MODULE$.apply(FsHistoryProviderMetadata.class));
               } else {
                  if (!(var10 instanceof NativeDB.DBException)) {
                     throw var10;
                  }

                  NativeDB.DBException var8 = (NativeDB.DBException)var10;
                  this.$outer.logWarning(new Serializable(this, dbPath) {
                     public static final long serialVersionUID = 0L;
                     private final File dbPath$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load disk store ", " :"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.dbPath$1}));
                     }

                     public {
                        this.dbPath$1 = dbPath$1;
                     }
                  }, var8);
                  Utils$.MODULE$.deleteRecursively(dbPath);
                  var7 = KVUtils$.MODULE$.open(dbPath, metadata, ClassTag$.MODULE$.apply(FsHistoryProviderMetadata.class));
               }

               var10000 = var7;
               return var10000;
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
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final InMemoryStore apply() {
            return new InMemoryStore();
         }
      });
      this.org$apache$spark$deploy$history$FsHistoryProvider$$diskManager = this.storePath().<HistoryServerDiskManager>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FsHistoryProvider $outer;

         public final HistoryServerDiskManager apply(File path) {
            return new HistoryServerDiskManager(this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$conf, path, this.$outer.listing(), this.$outer.org$apache$spark$deploy$history$FsHistoryProvider$$clock);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.org$apache$spark$deploy$history$FsHistoryProvider$$activeUIs = new HashMap<Tuple2<String, Option<String>>, LoadedAppUI>();
      this.org$apache$spark$deploy$history$FsHistoryProvider$$replayExecutor = (ExecutorService)(Utils$.MODULE$.isTesting() ? ThreadUtils$.MODULE$.newDaemonFixedThreadPool(this.NUM_PROCESSING_THREADS(), "log-replay-executor") : MoreExecutors.sameThreadExecutor());
      this.initThread = this.initialize();
   }

   public FsHistoryProvider(SparkConf conf) {
      this(conf, new SystemClock());
   }
}
