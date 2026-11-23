package org.apache.spark.scheduler;

import java.io.NotSerializableException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.spark.ExceptionFailure;
import org.apache.spark.ExecutorLostFailure;
import org.apache.spark.FetchFailed;
import org.apache.spark.Resubmitted$;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.Success$;
import org.apache.spark.TaskFailedReason;
import org.apache.spark.TaskNotSerializableException;
import org.apache.spark.TaskState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.SerializerInstance;
import org.apache.spark.util.Clock;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.collection.MedianHeap;
import org.slf4j.Logger;
import scala.Array$;
import scala.Enumeration;
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
import scala.Tuple2$mcIJ$sp;
import scala.Tuple2$mcZI$sp;
import scala.Tuple3;
import scala.collection.Iterable;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Set;
import scala.collection.TraversableLike;
import scala.collection.immutable.List;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.math.Ordering;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BooleanRef;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.Nothing$;
import scala.runtime.RichDouble$;
import scala.runtime.RichInt$;
import scala.runtime.TraitSetter;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0015mf!B\u0001\u0003\u0001\u0011Q!A\u0004+bg.\u001cV\r^'b]\u0006<WM\u001d\u0006\u0003\u0007\u0011\t\u0011b]2iK\u0012,H.\u001a:\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001cB\u0001A\u0006\u0012+A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003\u0017M\u001b\u0007.\u001a3vY\u0006\u0014G.\u001a\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\r\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u001f\u0003\u0015\u00198\r[3e\u0007\u0001\u0001\"AE\u0010\n\u0005\u0001\u0012!!\u0005+bg.\u001c6\r[3ek2,'/S7qY\"A!\u0005\u0001BC\u0002\u0013\u00051%A\u0004uCN\\7+\u001a;\u0016\u0003\u0011\u0002\"AE\u0013\n\u0005\u0019\u0012!a\u0002+bg.\u001cV\r\u001e\u0005\tQ\u0001\u0011\t\u0011)A\u0005I\u0005AA/Y:l'\u0016$\b\u0005\u0003\u0005+\u0001\t\u0015\r\u0011\"\u0001,\u0003=i\u0017\r\u001f+bg.4\u0015-\u001b7ve\u0016\u001cX#\u0001\u0017\u0011\u00051i\u0013B\u0001\u0018\u000e\u0005\rIe\u000e\u001e\u0005\ta\u0001\u0011\t\u0011)A\u0005Y\u0005\u0001R.\u0019=UCN\\g)Y5mkJ,7\u000f\t\u0005\te\u0001\u0011\t\u0011)A\u0005g\u0005\u0001\"\r\\1dW2L7\u000f\u001e+sC\u000e\\WM\u001d\t\u0004\u0019Q2\u0014BA\u001b\u000e\u0005\u0019y\u0005\u000f^5p]B\u0011!cN\u0005\u0003q\t\u0011\u0001C\u00117bG.d\u0017n\u001d;Ue\u0006\u001c7.\u001a:\t\u0011i\u0002!\u0011!Q\u0001\nm\nQa\u00197pG.\u0004\"\u0001P \u000e\u0003uR!A\u0010\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003\u0001v\u0012Qa\u00117pG.DQA\u0011\u0001\u0005\u0002\r\u000ba\u0001P5oSRtDC\u0002#F\r\u001eC\u0015\n\u0005\u0002\u0013\u0001!)A$\u0011a\u0001=!)!%\u0011a\u0001I!)!&\u0011a\u0001Y!9!'\u0011I\u0001\u0002\u0004\u0019\u0004b\u0002\u001eB!\u0003\u0005\ra\u000f\u0005\b\u0017\u0002\u0011\r\u0011\"\u0003M\u0003\u0011\u0019wN\u001c4\u0016\u00035\u0003\"AT(\u000e\u0003\u0011I!\u0001\u0015\u0003\u0003\u0013M\u0003\u0018M]6D_:4\u0007B\u0002*\u0001A\u0003%Q*A\u0003d_:4\u0007\u0005C\u0004U\u0001\t\u0007I\u0011B+\u0002\u0013\u0005$G-\u001a3KCJ\u001cX#\u0001,\u0011\t]cf,Z\u0007\u00021*\u0011\u0011LW\u0001\b[V$\u0018M\u00197f\u0015\tYV\"\u0001\u0006d_2dWm\u0019;j_:L!!\u0018-\u0003\u000f!\u000b7\u000f['baB\u0011qL\u0019\b\u0003\u0019\u0001L!!Y\u0007\u0002\rA\u0013X\rZ3g\u0013\t\u0019GM\u0001\u0004TiJLgn\u001a\u0006\u0003C6\u0001\"\u0001\u00044\n\u0005\u001dl!\u0001\u0002'p]\u001eDa!\u001b\u0001!\u0002\u00131\u0016AC1eI\u0016$'*\u0019:tA!91\u000e\u0001b\u0001\n\u0013)\u0016AC1eI\u0016$g)\u001b7fg\"1Q\u000e\u0001Q\u0001\nY\u000b1\"\u00193eK\u00124\u0015\u000e\\3tA!9q\u000e\u0001b\u0001\n\u0003\u0001\u0018\u0001F*Q\u000b\u000e+F*\u0011+J\u001f:{\u0016+V!O)&cU)F\u0001r!\ta!/\u0003\u0002t\u001b\t1Ai\\;cY\u0016Da!\u001e\u0001!\u0002\u0013\t\u0018!F*Q\u000b\u000e+F*\u0011+J\u001f:{\u0016+V!O)&cU\t\t\u0005\bo\u0002\u0011\r\u0011\"\u0001q\u0003Y\u0019\u0006+R\"V\u0019\u0006#\u0016j\u0014(`\u001bVcE+\u0013)M\u0013\u0016\u0013\u0006BB=\u0001A\u0003%\u0011/A\fT!\u0016\u001bU\u000bT!U\u0013>su,T+M)&\u0003F*S#SA!91\u0010\u0001b\u0001\n\u0003a\u0018!D7bqJ+7/\u001e7u'&TX-F\u0001f\u0011\u0019q\b\u0001)A\u0005K\u0006qQ.\u0019=SKN,H\u000e^*ju\u0016\u0004\u0003\"CA\u0001\u0001\t\u0007I\u0011AA\u0002\u0003I\u0019\b/Z2vY\u0006$\u0018n\u001c8F]\u0006\u0014G.\u001a3\u0016\u0005\u0005\u0015\u0001c\u0001\u0007\u0002\b%\u0019\u0011\u0011B\u0007\u0003\u000f\t{w\u000e\\3b]\"A\u0011Q\u0002\u0001!\u0002\u0013\t)!A\nta\u0016\u001cW\u000f\\1uS>tWI\\1cY\u0016$\u0007\u0005C\u0005\u0002\u0012\u0001\u0011\r\u0011\"\u0001\u0002\u0014\u0005\u0019QM\u001c<\u0016\u0005\u0005U\u0001c\u0001(\u0002\u0018%\u0019\u0011\u0011\u0004\u0003\u0003\u0011M\u0003\u0018M]6F]ZD\u0001\"!\b\u0001A\u0003%\u0011QC\u0001\u0005K:4\b\u0005C\u0005\u0002\"\u0001\u0011\r\u0011\"\u0001\u0002$\u0005\u00191/\u001a:\u0016\u0005\u0005\u0015\u0002\u0003BA\u0014\u0003[i!!!\u000b\u000b\u0007\u0005-B!\u0001\u0006tKJL\u0017\r\\5{KJLA!a\f\u0002*\t\u00112+\u001a:jC2L'0\u001a:J]N$\u0018M\\2f\u0011!\t\u0019\u0004\u0001Q\u0001\n\u0005\u0015\u0012\u0001B:fe\u0002B\u0011\"a\u000e\u0001\u0005\u0004%\t!!\u000f\u0002\u000bQ\f7o[:\u0016\u0005\u0005m\u0002#\u0002\u0007\u0002>\u0005\u0005\u0013bAA \u001b\t)\u0011I\u001d:bsB\"\u00111IA'!\u0015\u0011\u0012QIA%\u0013\r\t9E\u0001\u0002\u0005)\u0006\u001c8\u000e\u0005\u0003\u0002L\u00055C\u0002\u0001\u0003\f\u0003\u001f\u0002\u0011\u0011!A\u0001\u0006\u0003\t)FA\u0002`IEJ1!a\u0015&\u0003\u0019!\u0018m]6tAE!\u0011qKA/!\ra\u0011\u0011L\u0005\u0004\u00037j!a\u0002(pi\"Lgn\u001a\t\u0004\u0019\u0005}\u0013bAA1\u001b\t\u0019\u0011I\\=\t\u0011\u0005M\u0003\u0001)A\u0005\u0003wA\u0001\"a\u001a\u0001\u0005\u0004%\taK\u0001\t]VlG+Y:lg\"9\u00111\u000e\u0001!\u0002\u0013a\u0013!\u00038v[R\u000b7o[:!\u0011%\ty\u0007\u0001b\u0001\n\u0003\t\t(A\u0007d_BLWm\u001d*v]:LgnZ\u000b\u0003\u0003g\u0002B\u0001DA\u001fY!A\u0011q\u000f\u0001!\u0002\u0013\t\u0019(\u0001\bd_BLWm\u001d*v]:Lgn\u001a\u0011\t\u0013\u0005m\u0004A1A\u0005\u0002\u0005u\u0014AC:vG\u000e,7o\u001d4vYV\u0011\u0011q\u0010\t\u0006\u0019\u0005u\u0012Q\u0001\u0005\t\u0003\u0007\u0003\u0001\u0015!\u0003\u0002\u0000\u0005Y1/^2dKN\u001ch-\u001e7!\u0011%\t9\t\u0001b\u0001\n\u0013\t\t(A\u0006ok64\u0015-\u001b7ve\u0016\u001c\b\u0002CAF\u0001\u0001\u0006I!a\u001d\u0002\u00199,XNR1jYV\u0014Xm\u001d\u0011\t\u0013\u0005=\u0005A1A\u0005\n\u0005u\u0014\u0001F6jY2,GMQ=Pi\",'/\u0011;uK6\u0004H\u000f\u0003\u0005\u0002\u0014\u0002\u0001\u000b\u0011BA@\u0003UY\u0017\u000e\u001c7fI\nKx\n\u001e5fe\u0006#H/Z7qi\u0002B\u0011\"a&\u0001\u0005\u0004%\t!!'\u0002\u0019Q\f7o[!ui\u0016l\u0007\u000f^:\u0016\u0005\u0005m\u0005#\u0002\u0007\u0002>\u0005u\u0005CBAP\u0003_\u000b)L\u0004\u0003\u0002\"\u0006-f\u0002BAR\u0003Sk!!!*\u000b\u0007\u0005\u001dV$\u0001\u0004=e>|GOP\u0005\u0002\u001d%\u0019\u0011QV\u0007\u0002\u000fA\f7m[1hK&!\u0011\u0011WAZ\u0005\u0011a\u0015n\u001d;\u000b\u0007\u00055V\u0002E\u0002\u0013\u0003oK1!!/\u0003\u0005!!\u0016m]6J]\u001a|\u0007\u0002CA_\u0001\u0001\u0006I!a'\u0002\u001bQ\f7o[!ui\u0016l\u0007\u000f^:!\u0011%\t\t\r\u0001a\u0001\n\u0003\u00111&A\buCN\\7oU;dG\u0016\u001c8OZ;m\u0011)\t)\r\u0001a\u0001\n\u0003\u0011\u0011qY\u0001\u0014i\u0006\u001c8n]*vG\u000e,7o\u001d4vY~#S-\u001d\u000b\u0005\u0003\u0013\fy\rE\u0002\r\u0003\u0017L1!!4\u000e\u0005\u0011)f.\u001b;\t\u0013\u0005E\u00171YA\u0001\u0002\u0004a\u0013a\u0001=%c!9\u0011Q\u001b\u0001!B\u0013a\u0013\u0001\u0005;bg.\u001c8+^2dKN\u001ch-\u001e7!\u0011!\tI\u000e\u0001b\u0001\n\u0003Y\u0013AB<fS\u001eDG\u000fC\u0004\u0002^\u0002\u0001\u000b\u0011\u0002\u0017\u0002\u000f],\u0017n\u001a5uA!A\u0011\u0011\u001d\u0001C\u0002\u0013\u00051&\u0001\u0005nS:\u001c\u0006.\u0019:f\u0011\u001d\t)\u000f\u0001Q\u0001\n1\n\u0011\"\\5o'\"\f'/\u001a\u0011\t\u0011\u0005%\b\u00011A\u0005\u0002-\n\u0001\u0002\u001d:j_JLG/\u001f\u0005\n\u0003[\u0004\u0001\u0019!C\u0001\u0003_\fA\u0002\u001d:j_JLG/_0%KF$B!!3\u0002r\"I\u0011\u0011[Av\u0003\u0003\u0005\r\u0001\f\u0005\b\u0003k\u0004\u0001\u0015)\u0003-\u0003%\u0001(/[8sSRL\b\u0005\u0003\u0005\u0002z\u0002\u0001\r\u0011\"\u0001,\u0003\u001d\u0019H/Y4f\u0013\u0012D\u0011\"!@\u0001\u0001\u0004%\t!a@\u0002\u0017M$\u0018mZ3JI~#S-\u001d\u000b\u0005\u0003\u0013\u0014\t\u0001C\u0005\u0002R\u0006m\u0018\u0011!a\u0001Y!9!Q\u0001\u0001!B\u0013a\u0013\u0001C:uC\u001e,\u0017\n\u001a\u0011\t\u0013\t%\u0001A1A\u0005\u0002\t-\u0011\u0001\u00028b[\u0016,\"A!\u0004\u0011\t\t=!\u0011D\u0007\u0003\u0005#QAAa\u0005\u0003\u0016\u0005!A.\u00198h\u0015\t\u00119\"\u0001\u0003kCZ\f\u0017bA2\u0003\u0012!A!Q\u0004\u0001!\u0002\u0013\u0011i!A\u0003oC6,\u0007\u0005C\u0005\u0003\"\u0001\u0001\r\u0011\"\u0001\u0003$\u00051\u0001/\u0019:f]R,\"A!\n\u0011\u0007I\u00119#C\u0002\u0003*\t\u0011A\u0001U8pY\"I!Q\u0006\u0001A\u0002\u0013\u0005!qF\u0001\u000ba\u0006\u0014XM\u001c;`I\u0015\fH\u0003BAe\u0005cA!\"!5\u0003,\u0005\u0005\t\u0019\u0001B\u0013\u0011!\u0011)\u0004\u0001Q!\n\t\u0015\u0012a\u00029be\u0016tG\u000f\t\u0005\t\u0005s\u0001\u0001\u0019!C\u0005y\u0006yAo\u001c;bYJ+7/\u001e7u'&TX\rC\u0005\u0003>\u0001\u0001\r\u0011\"\u0003\u0003@\u0005\u0019Bo\u001c;bYJ+7/\u001e7u'&TXm\u0018\u0013fcR!\u0011\u0011\u001aB!\u0011%\t\tNa\u000f\u0002\u0002\u0003\u0007Q\rC\u0004\u0003F\u0001\u0001\u000b\u0015B3\u0002!Q|G/\u00197SKN,H\u000e^*ju\u0016\u0004\u0003\u0002\u0003B%\u0001\u0001\u0007I\u0011B\u0016\u0002\u001f\r\fGnY;mCR,G\rV1tWND\u0011B!\u0014\u0001\u0001\u0004%IAa\u0014\u0002'\r\fGnY;mCR,G\rV1tWN|F%Z9\u0015\t\u0005%'\u0011\u000b\u0005\n\u0003#\u0014Y%!AA\u00021BqA!\u0016\u0001A\u0003&A&\u0001\tdC2\u001cW\u000f\\1uK\u0012$\u0016m]6tA!Q!\u0011\f\u0001C\u0002\u0013\u0005!Aa\u0017\u00023Q\f7o[*fi\nc\u0017mY6mSN$\b*\u001a7qKJ|\u0005\u000f^\u000b\u0003\u0005;\u0002B\u0001\u0004\u001b\u0003`A\u0019!C!\u0019\n\u0007\t\r$A\u0001\tUCN\\7+\u001a;CY\u0006\u001c7\u000e\\5ti\"A!q\r\u0001!\u0002\u0013\u0011i&\u0001\u000euCN\\7+\u001a;CY\u0006\u001c7\u000e\\5ti\"+G\u000e]3s\u001fB$\b\u0005\u0003\u0006\u0003l\u0001\u0011\r\u0011\"\u0001\u0003\u0005[\nqB];o]&tw\rV1tWN\u001cV\r^\u000b\u0003\u0005_\u0002Ba\u0016B9K&\u0019!1\u000f-\u0003\u000f!\u000b7\u000f[*fi\"A!q\u000f\u0001!\u0002\u0013\u0011y'\u0001\tsk:t\u0017N\\4UCN\\7oU3uA!1!1\u0010\u0001\u0005B-\nAB];o]&tw\rV1tWNDqAa \u0001\t\u0003\u0011\t)\u0001\u000bt_6,\u0017\t\u001e;f[B$8+^2dK\u0016$W\r\u001a\u000b\u0005\u0003\u000b\u0011\u0019\tC\u0004\u0003\u0006\nu\u0004\u0019A3\u0002\u0007QLG\r\u0003\u0006\u0003\n\u0002\u0001\r\u0011\"\u0001\u0003\u0003\u0007\t\u0001\"[:[_6\u0014\u0017.\u001a\u0005\u000b\u0005\u001b\u0003\u0001\u0019!C\u0001\u0005\t=\u0015\u0001D5t5>l'-[3`I\u0015\fH\u0003BAe\u0005#C!\"!5\u0003\f\u0006\u0005\t\u0019AA\u0003\u0011!\u0011)\n\u0001Q!\n\u0005\u0015\u0011!C5t5>l'-[3!\u0011%\u0011I\n\u0001b\u0001\n\u0013\u0011Y*A\fqK:$\u0017N\\4UCN\\7OR8s\u000bb,7-\u001e;peV\u0011!Q\u0014\t\u0006/rs&q\u0014\t\u0005/\n\u0005F&C\u0002\u0003$b\u00131\"\u0011:sCf\u0014UO\u001a4fe\"A!q\u0015\u0001!\u0002\u0013\u0011i*\u0001\rqK:$\u0017N\\4UCN\\7OR8s\u000bb,7-\u001e;pe\u0002B\u0011Ba+\u0001\u0005\u0004%IAa'\u0002'A,g\u000eZ5oOR\u000b7o[:G_JDun\u001d;\t\u0011\t=\u0006\u0001)A\u0005\u0005;\u000bA\u0003]3oI&tw\rV1tWN4uN\u001d%pgR\u0004\u0003\"\u0003BZ\u0001\t\u0007I\u0011\u0002BN\u0003M\u0001XM\u001c3j]\u001e$\u0016m]6t\r>\u0014(+Y2l\u0011!\u00119\f\u0001Q\u0001\n\tu\u0015\u0001\u00069f]\u0012Lgn\u001a+bg.\u001chi\u001c:SC\u000e\\\u0007\u0005\u0003\u0006\u0003<\u0002\u0001\r\u0011\"\u0001\u0003\u0005{\u000bq\u0003]3oI&tw\rV1tWN<\u0016\u000e\u001e5O_B\u0013XMZ:\u0016\u0005\t}\u0005B\u0003Ba\u0001\u0001\u0007I\u0011\u0001\u0002\u0003D\u0006Y\u0002/\u001a8eS:<G+Y:lg^KG\u000f\u001b(p!J,gm]0%KF$B!!3\u0003F\"Q\u0011\u0011\u001bB`\u0003\u0003\u0005\rAa(\t\u0011\t%\u0007\u0001)Q\u0005\u0005?\u000b\u0001\u0004]3oI&tw\rV1tWN<\u0016\u000e\u001e5O_B\u0013XMZ:!\u0011%\u0011i\r\u0001b\u0001\n\u0013\u0011i,A\bbY2\u0004VM\u001c3j]\u001e$\u0016m]6t\u0011!\u0011\t\u000e\u0001Q\u0001\n\t}\u0015\u0001E1mYB+g\u000eZ5oOR\u000b7o[:!\u0011)\u0011)\u000e\u0001b\u0001\n\u0003\u0011!q[\u0001\u0012gB,7-\u001e7bi\u0006\u0014G.\u001a+bg.\u001cXC\u0001Bm!\u00119&\u0011\u000f\u0017\t\u0011\tu\u0007\u0001)A\u0005\u00053\f!c\u001d9fGVd\u0017\r^1cY\u0016$\u0016m]6tA!I!\u0011\u001d\u0001C\u0002\u0013%!1]\u0001\ni\u0006\u001c8.\u00138g_N,\"A!:\u0011\u000b]cV-!.\t\u0011\t%\b\u0001)A\u0005\u0005K\f!\u0002^1tW&sgm\\:!\u0011%\u0011i\u000f\u0001b\u0001\n\u0003\u0011y/A\ftk\u000e\u001cWm]:gk2$\u0016m]6EkJ\fG/[8ogV\u0011!\u0011\u001f\t\u0005\u0005g\u001490\u0004\u0002\u0003v*\u00111,P\u0005\u0005\u0005s\u0014)P\u0001\u0006NK\u0012L\u0017M\u001c%fCBD\u0001B!@\u0001A\u0003%!\u0011_\u0001\u0019gV\u001c7-Z:tMVdG+Y:l\tV\u0014\u0018\r^5p]N\u0004\u0003\u0002CB\u0001\u0001\t\u0007I\u0011\u0001?\u00021\u0015C6)\u0012)U\u0013>su\f\u0015*J\u001dR{\u0016J\u0014+F%Z\u000bE\nC\u0004\u0004\u0006\u0001\u0001\u000b\u0011B3\u00023\u0015C6)\u0012)U\u0013>su\f\u0015*J\u001dR{\u0016J\u0014+F%Z\u000bE\n\t\u0005\n\u0007\u0013\u0001!\u0019!C\u0005\u0007\u0017\t\u0001C]3dK:$X\t_2faRLwN\\:\u0016\u0005\r5\u0001#B,]=\u000e=\u0001#\u0002\u0007\u0004\u00121*\u0017bAB\n\u001b\t1A+\u001e9mKJB\u0001ba\u0006\u0001A\u0003%1QB\u0001\u0012e\u0016\u001cWM\u001c;Fq\u000e,\u0007\u000f^5p]N\u0004\u0003\u0002CB\u000e\u0001\t\u0007I\u0011\u0001?\u0002\u000b\u0015\u0004xn\u00195\t\u000f\r}\u0001\u0001)A\u0005K\u00061Q\r]8dQ\u0002B!ba\t\u0001\u0001\u0004%\tAAB\u0013\u0003Ai\u0017\u0010T8dC2LG/\u001f'fm\u0016d7/\u0006\u0002\u0004(A)A\"!\u0010\u0004*A!11FB\u0019\u001d\r\u00112QF\u0005\u0004\u0007_\u0011\u0011\u0001\u0004+bg.dunY1mSRL\u0018\u0002BB\u001a\u0007k\u0011A\u0002V1tW2{7-\u00197jifT1aa\f\u0003\u0011)\u0019I\u0004\u0001a\u0001\n\u0003\u001111H\u0001\u0015[fdunY1mSRLH*\u001a<fYN|F%Z9\u0015\t\u0005%7Q\b\u0005\u000b\u0003#\u001c9$!AA\u0002\r\u001d\u0002\u0002CB!\u0001\u0001\u0006Kaa\n\u0002#5LHj\\2bY&$\u0018\u0010T3wK2\u001c\b\u0005\u0003\u0006\u0004F\u0001\u0001\r\u0011\"\u0001\u0003\u0007\u000f\nQ\u0002\\8dC2LG/_,bSR\u001cXCAB%!\u0011a\u0011QH3\t\u0015\r5\u0003\u00011A\u0005\u0002\t\u0019y%A\tm_\u000e\fG.\u001b;z/\u0006LGo]0%KF$B!!3\u0004R!Q\u0011\u0011[B&\u0003\u0003\u0005\ra!\u0013\t\u0011\rU\u0003\u0001)Q\u0005\u0007\u0013\na\u0002\\8dC2LG/_,bSR\u001c\b\u0005\u0003\u0005\u0004Z\u0001\u0001\r\u0011\"\u0003,\u0003Q\u0019WO\u001d:f]RdunY1mSRL\u0018J\u001c3fq\"I1Q\f\u0001A\u0002\u0013%1qL\u0001\u0019GV\u0014(/\u001a8u\u0019>\u001c\u0017\r\\5us&sG-\u001a=`I\u0015\fH\u0003BAe\u0007CB\u0011\"!5\u0004\\\u0005\u0005\t\u0019\u0001\u0017\t\u000f\r\u0015\u0004\u0001)Q\u0005Y\u0005)2-\u001e:sK:$Hj\\2bY&$\u00180\u00138eKb\u0004\u0003\u0002CB5\u0001\u0001\u0007I\u0011\u0002?\u0002\u001d1\f7\u000f\u001e'bk:\u001c\u0007\u000eV5nK\"I1Q\u000e\u0001A\u0002\u0013%1qN\u0001\u0013Y\u0006\u001cH\u000fT1v]\u000eDG+[7f?\u0012*\u0017\u000f\u0006\u0003\u0002J\u000eE\u0004\"CAi\u0007W\n\t\u00111\u0001f\u0011\u001d\u0019)\b\u0001Q!\n\u0015\fq\u0002\\1ti2\u000bWO\\2i)&lW\r\t\u0005\b\u0007s\u0002A\u0011IB>\u0003A\u00198\r[3ek2\f'\r\\3Rk\u0016,X-\u0006\u0002\u0004~A)1qPBD#5\u00111\u0011\u0011\u0006\u0005\u0007\u0007\u001b))\u0001\u0006d_:\u001cWO\u001d:f]RT1A\u0010B\u000b\u0013\u0011\u0019Ii!!\u0003+\r{gnY;se\u0016tG\u000fT5oW\u0016$\u0017+^3vK\"91Q\u0012\u0001\u0005B\r=\u0015AD:dQ\u0016$W\u000f\\5oO6{G-Z\u000b\u0003\u0007#\u0003Baa%\u0004*:!1QSBS\u001d\u0011\u00199ja)\u000f\t\re5\u0011\u0015\b\u0005\u00077\u001byJ\u0004\u0003\u0002$\u000eu\u0015\"A\u0005\n\u0005\u001dA\u0011BA\u0003\u0007\u0013\t\u0019A!C\u0002\u0004(\n\tabU2iK\u0012,H.\u001b8h\u001b>$W-\u0003\u0003\u0004,\u000e5&AD*dQ\u0016$W\u000f\\5oO6{G-\u001a\u0006\u0004\u0007O\u0013\u0001BCBY\u0001\u0001\u0007I\u0011\u0001\u0002\u0002\u0004\u00051R-\\5ui\u0016$G+Y:l'&TXmV1s]&tw\r\u0003\u0006\u00046\u0002\u0001\r\u0011\"\u0001\u0003\u0007o\u000b!$Z7jiR,G\rV1tWNK'0Z,be:LgnZ0%KF$B!!3\u0004:\"Q\u0011\u0011[BZ\u0003\u0003\u0005\r!!\u0002\t\u0011\ru\u0006\u0001)Q\u0005\u0003\u000b\tq#Z7jiR,G\rV1tWNK'0Z,be:Lgn\u001a\u0011\t\u0011\r\u0005\u0007\u0001\"\u0001\u0005\u0007\u0007\fa\"\u00193e!\u0016tG-\u001b8h)\u0006\u001c8\u000e\u0006\u0003\u0002J\u000e\u0015\u0007bBBd\u0007\u007f\u0003\r\u0001L\u0001\u0006S:$W\r\u001f\u0005\b\u0007\u0017\u0004A\u0011BBg\u0003i9W\r\u001e)f]\u0012Lgn\u001a+bg.\u001chi\u001c:Fq\u0016\u001cW\u000f^8s)\u0011\u0011yja4\t\u000f\rE7\u0011\u001aa\u0001=\u0006QQ\r_3dkR|'/\u00133\t\u000f\rU\u0007\u0001\"\u0003\u0004X\u00061r-\u001a;QK:$\u0017N\\4UCN\\7OR8s\u0011>\u001cH\u000f\u0006\u0003\u0003 \u000ee\u0007bBBn\u0007'\u0004\rAX\u0001\u0005Q>\u001cH\u000fC\u0004\u0004`\u0002!Ia!9\u0002-\u001d,G\u000fU3oI&tw\rV1tWN4uN\u001d*bG.$BAa(\u0004d\"91Q]Bo\u0001\u0004q\u0016\u0001\u0002:bG.Dqa!;\u0001\t\u0013\u0019Y/A\neKF,X-^3UCN\\gI]8n\u0019&\u001cH\u000f\u0006\u0005\u0004n\u000e=81_B{!\raA\u0007\f\u0005\b\u0007c\u001c9\u000f1\u0001_\u0003\u0019)\u00070Z2JI\"911\\Bt\u0001\u0004q\u0006\u0002CB|\u0007O\u0004\rAa(\u0002\t1L7\u000f\u001e\u0005\b\u0007w\u0004A\u0011BB\u007f\u0003AA\u0017m]!ui\u0016l\u0007\u000f^(o\u0011>\u001cH\u000f\u0006\u0004\u0002\u0006\r}H1\u0001\u0005\b\t\u0003\u0019I\u00101\u0001-\u0003%!\u0018m]6J]\u0012,\u0007\u0010C\u0004\u0004\\\u000ee\b\u0019\u00010\t\u000f\u0011\u001d\u0001\u0001\"\u0003\u0005\n\u0005i\u0012n\u001d+bg.\u0014E.Y2lY&\u001cH/\u001a3P]\u0016CXmY(s\u001d>$W\r\u0006\u0005\u0002\u0006\u0011-AQ\u0002C\b\u0011\u001d\u00199\r\"\u0002A\u00021Bqa!=\u0005\u0006\u0001\u0007a\fC\u0004\u0004\\\u0012\u0015\u0001\u0019\u00010\t\u000f\u0011M\u0001\u0001\"\u0005\u0005\u0016\u00051B-Z9vKV,7\u000b]3dk2\fG/\u001b<f)\u0006\u001c8\u000e\u0006\u0005\u0005\u0018\u0011\u0015Bq\u0005C\u0015!\u0011aA\u0007\"\u0007\u0011\r1\u0019\t\u0002\fC\u000e!\u0011\u0019Y\u0003\"\b\n\t\u0011}A\u0011\u0005\u0002\u0006-\u0006dW/Z\u0005\u0004\tGi!aC#ok6,'/\u0019;j_:Dqa!=\u0005\u0012\u0001\u0007a\fC\u0004\u0004\\\u0012E\u0001\u0019\u00010\t\u0011\u0011-B\u0011\u0003a\u0001\t7\t\u0001\u0002\\8dC2LG/\u001f\u0005\b\t_\u0001A\u0011\u0002C\u0019\u0003-!W-];fk\u0016$\u0016m]6\u0015\u0011\u0011MB1\bC\u001f\t\u007f\u0001B\u0001\u0004\u001b\u00056AAA\u0002b\u000e-\t7\t)!C\u0002\u0005:5\u0011a\u0001V;qY\u0016\u001c\u0004bBBy\t[\u0001\rA\u0018\u0005\b\u00077$i\u00031\u0001_\u0011!!\t\u0005\"\fA\u0002\u0011m\u0011aC7bq2{7-\u00197jifDq\u0001\"\u0012\u0001\t\u0003!9%A\u0007sKN|WO]2f\u001f\u001a4WM\u001d\u000b\t\t\u0013\"\t\u0006b\u0015\u0005VA!A\u0002\u000eC&!\r\u0011BQJ\u0005\u0004\t\u001f\u0012!a\u0004+bg.$Um]2sSB$\u0018n\u001c8\t\u000f\rEH1\ta\u0001=\"911\u001cC\"\u0001\u0004q\u0006\u0002\u0003C!\t\u0007\u0002\ra!\u000b)\r\u0011\rC\u0011\fC3!\u0015aA1\fC0\u0013\r!i&\u0004\u0002\u0007i\"\u0014xn^:\u0011\u00079#\t'C\u0002\u0005d\u0011\u0011A\u0004V1tW:{GoU3sS\u0006d\u0017N_1cY\u0016,\u0005pY3qi&|g.\r\u0004\u001f=\u0012\u001dDQR\u0019\nG\u0011%D\u0011\u000fCB\tg*B\u0001b\u001b\u0005nU\ta\fB\u0004\u0005pu\u0011\r\u0001\"\u001f\u0003\u0003QKA\u0001b\u001d\u0005v\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIER1\u0001b\u001e\u000e\u0003\u0019!\bN]8xgF!\u0011q\u000bC>!\u0011!i\bb \u000f\u00071\tY+\u0003\u0003\u0005\u0002\u0006M&!\u0003+ie><\u0018M\u00197fc%\u0019CQ\u0011CD\t\u0013#9HD\u0002\r\t\u000fK1\u0001b\u001e\u000ec\u0015\u0011C\"\u0004CF\u0005\u0015\u00198-\u00197bc\r1Cq\f\u0005\b\t#\u0003A\u0011\u0002CJ\u0003Ii\u0017-\u001f2f\r&t\u0017n\u001d5UCN\\7+\u001a;\u0015\u0005\u0005%\u0007b\u0002CL\u0001\u0011%A\u0011T\u0001\u0018O\u0016$\u0018\t\u001c7po\u0016$Gj\\2bY&$\u0018\u0010T3wK2$Ba!\u000b\u0005\u001c\"9AQ\u0014CK\u0001\u0004)\u0017aB2veRKW.\u001a\u0005\b\tC\u0003A\u0011\u0001CR\u0003A9W\r\u001e'pG\u0006d\u0017\u000e^=J]\u0012,\u0007\u0010F\u0002-\tKC\u0001\u0002b\u000b\u0005 \u0002\u00071\u0011\u0006\u0005\t\tS\u0003A\u0011\u0001\u0002\u0005,\u0006a\u0012MY8si&37i\\7qY\u0016$X\r\\=CY\u0006\u001c7\u000e\\5ti\u0016$G\u0003BAe\t[C\u0001\u0002b,\u0005(\u0002\u0007A\u0011W\u0001\u0010Q>\u001cH\u000fV8Fq\u0016\u001cW\u000f^8sgB)q\u000b\u00180\u00054B!qK!\u001d_\u0011\u001d!9\f\u0001C\u0001\ts\u000bq\u0003[1oI2,G+Y:l\u000f\u0016$H/\u001b8h%\u0016\u001cX\u000f\u001c;\u0015\t\u0005%G1\u0018\u0005\b\u0005\u000b#)\f1\u0001f\u0011\u001d!y\f\u0001C\u0001\t\u0003\f1cY1o\r\u0016$8\r['pe\u0016\u0014Vm];miN$B!!\u0002\u0005D\"9AQ\u0019C_\u0001\u0004)\u0017\u0001B:ju\u0016Dq\u0001\"3\u0001\t\u0003!Y-\u0001\u000biC:$G.Z*vG\u000e,7o\u001d4vYR\u000b7o\u001b\u000b\u0007\u0003\u0013$i\rb4\t\u000f\t\u0015Eq\u0019a\u0001K\"AA\u0011\u001bCd\u0001\u0004!\u0019.\u0001\u0004sKN,H\u000e\u001e\u0019\u0005\t+$i\u000eE\u0003\u0013\t/$Y.C\u0002\u0005Z\n\u0011\u0001\u0003R5sK\u000e$H+Y:l%\u0016\u001cX\u000f\u001c;\u0011\t\u0005-CQ\u001c\u0003\r\u0003\u001f\"y-!A\u0001\u0002\u000b\u0005\u0011Q\u000b\u0005\b\tC\u0004A\u0011\u0001Cr\u0003AA\u0017M\u001c3mK\u001a\u000b\u0017\u000e\\3e)\u0006\u001c8\u000e\u0006\u0005\u0002J\u0012\u0015Hq\u001dC}\u0011\u001d\u0011)\tb8A\u0002\u0015D\u0001\u0002\";\u0005`\u0002\u0007A1^\u0001\u0006gR\fG/\u001a\t\u0005\t[$\u0019P\u0004\u0003\u0004\u0018\u0012=\u0018b\u0001Cy\t\u0005IA+Y:l'R\fG/Z\u0005\u0005\tk$9PA\u0005UCN\\7\u000b^1uK*\u0019A\u0011\u001f\u0003\t\u0011\u0011mHq\u001ca\u0001\t{\faA]3bg>t\u0007c\u0001(\u0005\u0000&\u0019Q\u0011\u0001\u0003\u0003!Q\u000b7o\u001b$bS2,GMU3bg>t\u0007bBC\u0003\u0001\u0011\u0005QqA\u0001\u0006C\n|'\u000f\u001e\u000b\u0007\u0003\u0013,I!\"\u0004\t\u000f\u0015-Q1\u0001a\u0001=\u00069Q.Z:tC\u001e,\u0007BCC\b\u000b\u0007\u0001\n\u00111\u0001\u0006\u0012\u0005IQ\r_2faRLwN\u001c\t\u0005\u0019Q*\u0019\u0002\u0005\u0003\u0002 \u0012}\u0004bBC\f\u0001\u0011\u0005Q\u0011D\u0001\u000fC\u0012$'+\u001e8oS:<G+Y:l)\u0011\tI-b\u0007\t\u000f\t\u0015UQ\u0003a\u0001K\"9Qq\u0004\u0001\u0005\u0002\u0015\u0005\u0012!\u0005:f[>4XMU;o]&tw\rV1tWR!\u0011\u0011ZC\u0012\u0011\u001d\u0011))\"\bA\u0002\u0015Dq!b\n\u0001\t\u0003*I#\u0001\u000bhKR\u001c6\r[3ek2\f'\r\\3Cs:\u000bW.\u001a\u000b\u0004#\u0015-\u0002b\u0002B\u0005\u000bK\u0001\rA\u0018\u0005\b\u000b_\u0001A\u0011IC\u0019\u00039\tG\rZ*dQ\u0016$W\u000f\\1cY\u0016$B!!3\u00064!9QQGC\u0017\u0001\u0004\t\u0012aC:dQ\u0016$W\u000f\\1cY\u0016Dq!\"\u000f\u0001\t\u0003*Y$A\tsK6|g/Z*dQ\u0016$W\u000f\\1cY\u0016$B!!3\u0006>!9QQGC\u001c\u0001\u0004\t\u0002bBC!\u0001\u0011\u0005S1I\u0001\u0016O\u0016$8k\u001c:uK\u0012$\u0016m]6TKR\fV/Z;f)\t))\u0005\u0005\u0003X\u0005C#\u0005bBC%\u0001\u0011\u0005S1J\u0001\rKb,7-\u001e;pe2{7\u000f\u001e\u000b\t\u0003\u0013,i%b\u0014\u0006R!91\u0011_C$\u0001\u0004q\u0006bBBn\u000b\u000f\u0002\rA\u0018\u0005\t\tw,9\u00051\u0001\u0006TA\u0019!#\"\u0016\n\u0007\u0015]#A\u0001\nFq\u0016\u001cW\u000f^8s\u0019>\u001c8OU3bg>t\u0007bBC.\u0001\u0011\u0005SQL\u0001\u0017G\",7m[*qK\u000e,H.\u0019;bE2,G+Y:lgR!\u0011QAC0\u0011\u001d)\t'\"\u0017A\u00021\nA#\\5o)&lW\rV8Ta\u0016\u001cW\u000f\\1uS>t\u0007bBC3\u0001\u0011%QqM\u0001\u0010O\u0016$Hj\\2bY&$\u0018pV1jiR\u0019Q-\"\u001b\t\u0011\u0015-T1\ra\u0001\u0007S\tQ\u0001\\3wK2Dq!b\u001c\u0001\t\u0013)\t(\u0001\u000ed_6\u0004X\u000f^3WC2LG\rT8dC2LG/\u001f'fm\u0016d7\u000f\u0006\u0002\u0004(!9QQ\u000f\u0001\u0005\u0002\u0011M\u0015!\u0005:fG>l\u0007/\u001e;f\u0019>\u001c\u0017\r\\5us\"9Q\u0011\u0010\u0001\u0005\u0002\u0011M\u0015!D3yK\u000e,Ho\u001c:BI\u0012,G\rC\u0005\u0006~\u0001\t\n\u0011\"\u0001\u0006\u0000\u0005y\u0011MY8si\u0012\"WMZ1vYR$#'\u0006\u0002\u0006\u0002*\"Q\u0011CCBW\t))\t\u0005\u0003\u0006\b\u0016EUBACE\u0015\u0011)Y)\"$\u0002\u0013Ut7\r[3dW\u0016$'bACH\u001b\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0015MU\u0011\u0012\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,w\u0001CCL\u0005!\u0005A!\"'\u0002\u001dQ\u000b7o[*fi6\u000bg.Y4feB\u0019!#b'\u0007\u000f\u0005\u0011\u0001\u0012\u0001\u0003\u0006\u001eN\u0019Q1T\u0006\t\u000f\t+Y\n\"\u0001\u0006\"R\u0011Q\u0011\u0014\u0005\n\u000bK+YJ1A\u0005\u0002-\nA\u0003V!T\u0017~\u001b\u0016JW#`)>{v+\u0011*O?.\u0013\u0005\u0002CCU\u000b7\u0003\u000b\u0011\u0002\u0017\u0002+Q\u000b5kS0T\u0013j+u\fV(`/\u0006\u0013fjX&CA!QQQVCN#\u0003%\t!b,\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00135+\t)\tLK\u00024\u000b\u0007C!\"\".\u0006\u001cF\u0005I\u0011AC\\\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%kU\u0011Q\u0011\u0018\u0016\u0004w\u0015\r\u0005"
)
public class TaskSetManager implements Schedulable, Logging {
   public final TaskSchedulerImpl org$apache$spark$scheduler$TaskSetManager$$sched;
   private final TaskSet taskSet;
   private final int maxTaskFailures;
   public final Option<BlacklistTracker> org$apache$spark$scheduler$TaskSetManager$$blacklistTracker;
   public final Clock org$apache$spark$scheduler$TaskSetManager$$clock;
   private final SparkConf org$apache$spark$scheduler$TaskSetManager$$conf;
   private final HashMap<String, Object> org$apache$spark$scheduler$TaskSetManager$$addedJars;
   private final HashMap<String, Object> org$apache$spark$scheduler$TaskSetManager$$addedFiles;
   private final double SPECULATION_QUANTILE;
   private final double SPECULATION_MULTIPLIER;
   private final long maxResultSize;
   private final boolean speculationEnabled;
   private final SparkEnv env;
   private final SerializerInstance ser;
   private final Task<?>[] tasks;
   private final int numTasks;
   private final int[] copiesRunning;
   private final boolean[] successful;
   private final int[] numFailures;
   private final boolean[] org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt;
   private final List<TaskInfo>[] taskAttempts;
   private int tasksSuccessful;
   private final int weight;
   private final int minShare;
   private int priority;
   private int stageId;
   private final String name;
   private Pool parent;
   private long totalResultSize;
   private int calculatedTasks;
   private final Option<TaskSetBlacklist> taskSetBlacklistHelperOpt;
   private final HashSet<Object> runningTasksSet;
   private boolean isZombie;
   private final HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor;
   private final HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost;
   private final HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack;
   private ArrayBuffer<Object> pendingTasksWithNoPrefs;
   private final ArrayBuffer<Object> org$apache$spark$scheduler$TaskSetManager$$allPendingTasks;
   private final HashSet<Object> speculatableTasks;
   private final HashMap<Object, TaskInfo> org$apache$spark$scheduler$TaskSetManager$$taskInfos;
   private final MedianHeap successfulTaskDurations;
   private final long EXCEPTION_PRINT_INTERVAL;
   private final HashMap<String, Tuple2<Object, Object>> recentExceptions;
   private final long epoch;
   private Enumeration.Value[] myLocalityLevels;
   private long[] localityWaits;
   private int org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex;
   private long org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime;
   private boolean emittedTaskSizeWarning;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Clock $lessinit$greater$default$5() {
      return TaskSetManager$.MODULE$.$lessinit$greater$default$5();
   }

   public static Option<BlacklistTracker> $lessinit$greater$default$4() {
      return TaskSetManager$.MODULE$.$lessinit$greater$default$4();
   }

   public static int TASK_SIZE_TO_WARN_KB() {
      return TaskSetManager$.MODULE$.TASK_SIZE_TO_WARN_KB();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public TaskSet taskSet() {
      return this.taskSet;
   }

   public int maxTaskFailures() {
      return this.maxTaskFailures;
   }

   public SparkConf org$apache$spark$scheduler$TaskSetManager$$conf() {
      return this.org$apache$spark$scheduler$TaskSetManager$$conf;
   }

   public HashMap<String, Object> org$apache$spark$scheduler$TaskSetManager$$addedJars() {
      return this.org$apache$spark$scheduler$TaskSetManager$$addedJars;
   }

   public HashMap<String, Object> org$apache$spark$scheduler$TaskSetManager$$addedFiles() {
      return this.org$apache$spark$scheduler$TaskSetManager$$addedFiles;
   }

   public double SPECULATION_QUANTILE() {
      return this.SPECULATION_QUANTILE;
   }

   public double SPECULATION_MULTIPLIER() {
      return this.SPECULATION_MULTIPLIER;
   }

   public long maxResultSize() {
      return this.maxResultSize;
   }

   public boolean speculationEnabled() {
      return this.speculationEnabled;
   }

   public SparkEnv env() {
      return this.env;
   }

   public SerializerInstance ser() {
      return this.ser;
   }

   public Task<?>[] tasks() {
      return this.tasks;
   }

   public int numTasks() {
      return this.numTasks;
   }

   public int[] copiesRunning() {
      return this.copiesRunning;
   }

   public boolean[] successful() {
      return this.successful;
   }

   private int[] numFailures() {
      return this.numFailures;
   }

   public boolean[] org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt() {
      return this.org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt;
   }

   public List<TaskInfo>[] taskAttempts() {
      return this.taskAttempts;
   }

   public int tasksSuccessful() {
      return this.tasksSuccessful;
   }

   public void tasksSuccessful_$eq(int x$1) {
      this.tasksSuccessful = x$1;
   }

   public int weight() {
      return this.weight;
   }

   public int minShare() {
      return this.minShare;
   }

   public int priority() {
      return this.priority;
   }

   public void priority_$eq(int x$1) {
      this.priority = x$1;
   }

   public int stageId() {
      return this.stageId;
   }

   public void stageId_$eq(int x$1) {
      this.stageId = x$1;
   }

   public String name() {
      return this.name;
   }

   public Pool parent() {
      return this.parent;
   }

   public void parent_$eq(Pool x$1) {
      this.parent = x$1;
   }

   private long totalResultSize() {
      return this.totalResultSize;
   }

   private void totalResultSize_$eq(long x$1) {
      this.totalResultSize = x$1;
   }

   private int calculatedTasks() {
      return this.calculatedTasks;
   }

   private void calculatedTasks_$eq(int x$1) {
      this.calculatedTasks = x$1;
   }

   public Option<TaskSetBlacklist> taskSetBlacklistHelperOpt() {
      return this.taskSetBlacklistHelperOpt;
   }

   public HashSet<Object> runningTasksSet() {
      return this.runningTasksSet;
   }

   public int runningTasks() {
      return this.runningTasksSet().size();
   }

   public boolean someAttemptSucceeded(long tid) {
      return this.successful()[((TaskInfo)this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid))).index()];
   }

   public boolean isZombie() {
      return this.isZombie;
   }

   public void isZombie_$eq(boolean x$1) {
      this.isZombie = x$1;
   }

   public HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor() {
      return this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor;
   }

   public HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost() {
      return this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost;
   }

   public HashMap<String, ArrayBuffer<Object>> org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack() {
      return this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack;
   }

   public ArrayBuffer<Object> pendingTasksWithNoPrefs() {
      return this.pendingTasksWithNoPrefs;
   }

   public void pendingTasksWithNoPrefs_$eq(ArrayBuffer<Object> x$1) {
      this.pendingTasksWithNoPrefs = x$1;
   }

   public ArrayBuffer<Object> org$apache$spark$scheduler$TaskSetManager$$allPendingTasks() {
      return this.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks;
   }

   public HashSet<Object> speculatableTasks() {
      return this.speculatableTasks;
   }

   public HashMap<Object, TaskInfo> org$apache$spark$scheduler$TaskSetManager$$taskInfos() {
      return this.org$apache$spark$scheduler$TaskSetManager$$taskInfos;
   }

   public MedianHeap successfulTaskDurations() {
      return this.successfulTaskDurations;
   }

   public long EXCEPTION_PRINT_INTERVAL() {
      return this.EXCEPTION_PRINT_INTERVAL;
   }

   private HashMap<String, Tuple2<Object, Object>> recentExceptions() {
      return this.recentExceptions;
   }

   public long epoch() {
      return this.epoch;
   }

   public Enumeration.Value[] myLocalityLevels() {
      return this.myLocalityLevels;
   }

   public void myLocalityLevels_$eq(Enumeration.Value[] x$1) {
      this.myLocalityLevels = x$1;
   }

   public long[] localityWaits() {
      return this.localityWaits;
   }

   public void localityWaits_$eq(long[] x$1) {
      this.localityWaits = x$1;
   }

   public int org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() {
      return this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex;
   }

   public void org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex_$eq(int x$1) {
      this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex = x$1;
   }

   private long org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime() {
      return this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime;
   }

   public void org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime_$eq(long x$1) {
      this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime = x$1;
   }

   public ConcurrentLinkedQueue<Schedulable> schedulableQueue() {
      return null;
   }

   public Enumeration.Value schedulingMode() {
      return SchedulingMode$.MODULE$.NONE();
   }

   public boolean emittedTaskSizeWarning() {
      return this.emittedTaskSizeWarning;
   }

   public void emittedTaskSizeWarning_$eq(boolean x$1) {
      this.emittedTaskSizeWarning = x$1;
   }

   public void addPendingTask(int index) {
      label17: {
         label16: {
            this.tasks()[index].preferredLocations().foreach(new Serializable(this, index) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;
               public final int index$1;

               public final void apply(TaskLocation loc) {
                  if (loc instanceof ExecutorCacheTaskLocation) {
                     ExecutorCacheTaskLocation var3 = (ExecutorCacheTaskLocation)loc;
                     ArrayBuffer var4 = ((ArrayBuffer)this.$outer.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor().getOrElseUpdate(var3.executorId(), new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final ArrayBuffer<Object> apply() {
                           return new ArrayBuffer<Object>();
                        }
                     })).$plus$eq(BoxesRunTime.boxToInteger(this.index$1));
                  } else if (loc instanceof HDFSCacheTaskLocation) {
                     HDFSCacheTaskLocation var5 = (HDFSCacheTaskLocation)loc;
                     Option exe = this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.getExecutorsAliveOnHost(loc.host());
                     if (exe instanceof Some) {
                        Some var8 = (Some)exe;
                        Set set = (Set)var8.x();
                        set.foreach(new Serializable(this) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;

                           public final ArrayBuffer<Object> apply(String e) {
                              return ((ArrayBuffer)this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor().getOrElseUpdate(e, new Serializable(this) {
                                 public static final long serialVersionUID = 0L;

                                 public final ArrayBuffer<Object> apply() {
                                    return new ArrayBuffer<Object>();
                                 }
                              })).$plus$eq(BoxesRunTime.boxToInteger(this.$outer.index$1));
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                              }
                           }
                        });
                        this.$outer.logInfo(new Serializable(this, set, var5) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final Set set$1;
                           private final HDFSCacheTaskLocation x3$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Pending task ", " has a cached location at ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.index$1), this.x3$1.host()}))).append((Object)", where there are executors ").append((Object)this.set$1.mkString(",")).toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.set$1 = set$1;
                                 this.x3$1 = x3$1;
                              }
                           }
                        });
                        BoxedUnit var10 = BoxedUnit.UNIT;
                     } else {
                        if (!None$.MODULE$.equals(exe)) {
                           throw new MatchError(exe);
                        }

                        this.$outer.logDebug(new Serializable(this, var5) {
                           public static final long serialVersionUID = 0L;
                           // $FF: synthetic field
                           private final <undefinedtype> $outer;
                           private final HDFSCacheTaskLocation x3$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Pending task ", " has a cached location at ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.index$1), this.x3$1.host()}))).append((Object)", but there are no executors alive there.").toString();
                           }

                           public {
                              if ($outer == null) {
                                 throw null;
                              } else {
                                 this.$outer = $outer;
                                 this.x3$1 = x3$1;
                              }
                           }
                        });
                        BoxedUnit var13 = BoxedUnit.UNIT;
                     }

                     BoxedUnit var11 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var12 = BoxedUnit.UNIT;
                  }

                  ((ArrayBuffer)this.$outer.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost().getOrElseUpdate(loc.host(), new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final ArrayBuffer<Object> apply() {
                        return new ArrayBuffer<Object>();
                     }
                  })).$plus$eq(BoxesRunTime.boxToInteger(this.index$1));
                  this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.getRackForHost(loc.host()).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final ArrayBuffer<Object> apply(String rack) {
                        return ((ArrayBuffer)this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack().getOrElseUpdate(rack, new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final ArrayBuffer<Object> apply() {
                              return new ArrayBuffer<Object>();
                           }
                        })).$plus$eq(BoxesRunTime.boxToInteger(this.$outer.index$1));
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
               public TaskSetManager org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.index$1 = index$1;
                  }
               }
            });
            Seq var10000 = this.tasks()[index].preferredLocations();
            Nil$ var2 = Nil$.MODULE$;
            if (var10000 == null) {
               if (var2 == null) {
                  break label16;
               }
            } else if (var10000.equals(var2)) {
               break label16;
            }

            BoxedUnit var3 = BoxedUnit.UNIT;
            break label17;
         }

         this.pendingTasksWithNoPrefs().$plus$eq(BoxesRunTime.boxToInteger(index));
      }

      this.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks().$plus$eq(BoxesRunTime.boxToInteger(index));
   }

   private ArrayBuffer<Object> getPendingTasksForExecutor(String executorId) {
      return (ArrayBuffer)this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor().getOrElse(executorId, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayBuffer<Object> apply() {
            return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
         }
      });
   }

   private ArrayBuffer<Object> getPendingTasksForHost(String host) {
      return (ArrayBuffer)this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost().getOrElse(host, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayBuffer<Object> apply() {
            return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
         }
      });
   }

   public ArrayBuffer<Object> org$apache$spark$scheduler$TaskSetManager$$getPendingTasksForRack(String rack) {
      return (ArrayBuffer)this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack().getOrElse(rack, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final ArrayBuffer<Object> apply() {
            return (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);
         }
      });
   }

   public Option<Object> org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(String execId, String host, ArrayBuffer<Object> list) {
      int indexOffset = list.size();

      while(indexOffset > 0) {
         --indexOffset;
         int index = BoxesRunTime.unboxToInt(list.apply(indexOffset));
         if (!this.isTaskBlacklistedOnExecOrNode(index, execId, host)) {
            list.remove(indexOffset);
            if (this.copiesRunning()[index] == 0 && !this.successful()[index]) {
               return new Some<Object>(BoxesRunTime.boxToInteger(index));
            }
         }
      }

      return None$.MODULE$;
   }

   private boolean hasAttemptOnHost(int taskIndex, String host) {
      return this.taskAttempts()[taskIndex].exists(new Serializable(this, host) {
         public static final long serialVersionUID = 0L;
         private final String host$3;

         public final boolean apply(TaskInfo x$2) {
            boolean var3;
            label23: {
               String var10000 = x$2.host();
               String var2 = this.host$3;
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
            this.host$3 = host$3;
         }
      });
   }

   private boolean isTaskBlacklistedOnExecOrNode(int index, String execId, String host) {
      return this.taskSetBlacklistHelperOpt().exists(new Serializable(this, index, execId, host) {
         public static final long serialVersionUID = 0L;
         private final int index$2;
         private final String execId$2;
         private final String host$2;

         public final boolean apply(TaskSetBlacklist blacklist) {
            return blacklist.isNodeBlacklistedForTask(this.host$2, this.index$2) || blacklist.isExecutorBlacklistedForTask(this.execId$2, this.index$2);
         }

         public {
            this.index$2 = index$2;
            this.execId$2 = execId$2;
            this.host$2 = host$2;
         }
      });
   }

   public Option<Tuple2<Object, Enumeration.Value>> dequeueSpeculativeTask(String execId, String host, Enumeration.Value locality) {
      Object var4 = new Object();

      Object var10000;
      try {
         this.speculatableTasks().retain(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;

            public final boolean apply(int index) {
               return this.apply$mcZI$sp(index);
            }

            public boolean apply$mcZI$sp(int index) {
               return !this.$outer.successful()[index];
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         if (!this.speculatableTasks().isEmpty()) {
            this.speculatableTasks().withFilter(new Serializable(this, execId, host) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;
               private final String execId$3;
               private final String host$4;

               public final boolean apply(int index) {
                  return this.apply$mcZI$sp(index);
               }

               public boolean apply$mcZI$sp(int index) {
                  return this.$outer.org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(index, this.execId$3, this.host$4);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.execId$3 = execId$3;
                     this.host$4 = host$4;
                  }
               }
            }).foreach(new Serializable(this, execId, var4) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;
               private final String execId$3;
               private final Object nonLocalReturnKey1$1;

               public final void apply(int index) {
                  this.apply$mcVI$sp(index);
               }

               public void apply$mcVI$sp(int index) {
                  Seq prefs = this.$outer.tasks()[index].preferredLocations();
                  Seq executors = (Seq)prefs.flatMap(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Iterable<String> apply(TaskLocation x$3) {
                        Iterable var4;
                        if (x$3 instanceof ExecutorCacheTaskLocation) {
                           ExecutorCacheTaskLocation var3 = (ExecutorCacheTaskLocation)x$3;
                           var4 = Option$.MODULE$.option2Iterable(new Some(var3.executorId()));
                        } else {
                           var4 = Option$.MODULE$.option2Iterable(None$.MODULE$);
                        }

                        return var4;
                     }
                  }, Seq$.MODULE$.canBuildFrom());
                  if (executors.contains(this.execId$3)) {
                     this.$outer.speculatableTasks().$minus$eq(BoxesRunTime.boxToInteger(index));
                     throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(new Tuple2(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.PROCESS_LOCAL())));
                  }
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.execId$3 = execId$3;
                     this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                  }
               }
            });
            if (TaskLocality$.MODULE$.isAllowed(locality, TaskLocality$.MODULE$.NODE_LOCAL())) {
               this.speculatableTasks().withFilter(new Serializable(this, execId, host) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final String execId$3;
                  private final String host$4;

                  public final boolean apply(int index) {
                     return this.apply$mcZI$sp(index);
                  }

                  public boolean apply$mcZI$sp(int index) {
                     return this.$outer.org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(index, this.execId$3, this.host$4);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.execId$3 = execId$3;
                        this.host$4 = host$4;
                     }
                  }
               }).foreach(new Serializable(this, host, var4) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final String host$4;
                  private final Object nonLocalReturnKey1$1;

                  public final void apply(int index) {
                     this.apply$mcVI$sp(index);
                  }

                  public void apply$mcVI$sp(int index) {
                     Seq locations = (Seq)this.$outer.tasks()[index].preferredLocations().map(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply(TaskLocation x$4) {
                           return x$4.host();
                        }
                     }, Seq$.MODULE$.canBuildFrom());
                     if (locations.contains(this.host$4)) {
                        this.$outer.speculatableTasks().$minus$eq(BoxesRunTime.boxToInteger(index));
                        throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(new Tuple2(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.NODE_LOCAL())));
                     }
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.host$4 = host$4;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
            }

            if (TaskLocality$.MODULE$.isAllowed(locality, TaskLocality$.MODULE$.NO_PREF())) {
               this.speculatableTasks().withFilter(new Serializable(this, execId, host) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final String execId$3;
                  private final String host$4;

                  public final boolean apply(int index) {
                     return this.apply$mcZI$sp(index);
                  }

                  public boolean apply$mcZI$sp(int index) {
                     return this.$outer.org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(index, this.execId$3, this.host$4);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.execId$3 = execId$3;
                        this.host$4 = host$4;
                     }
                  }
               }).foreach(new Serializable(this, var4) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final Object nonLocalReturnKey1$1;

                  public final void apply(int index) {
                     this.apply$mcVI$sp(index);
                  }

                  public void apply$mcVI$sp(int index) {
                     Seq locations = this.$outer.tasks()[index].preferredLocations();
                     if (locations.size() == 0) {
                        this.$outer.speculatableTasks().$minus$eq(BoxesRunTime.boxToInteger(index));
                        throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(new Tuple2(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.PROCESS_LOCAL())));
                     }
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
            }

            if (TaskLocality$.MODULE$.isAllowed(locality, TaskLocality$.MODULE$.RACK_LOCAL())) {
               this.org$apache$spark$scheduler$TaskSetManager$$sched.getRackForHost(host).foreach(new Serializable(this, execId, host, var4) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  public final String execId$3;
                  public final String host$4;
                  public final Object nonLocalReturnKey1$1;

                  public final void apply(String rack) {
                     this.$outer.speculatableTasks().withFilter(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final boolean apply(int index) {
                           return this.apply$mcZI$sp(index);
                        }

                        public boolean apply$mcZI$sp(int index) {
                           return this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(index, this.$outer.execId$3, this.$outer.host$4);
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     }).foreach(new Serializable(this, rack) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final String rack$1;

                        public final void apply(int index) {
                           this.apply$mcVI$sp(index);
                        }

                        public void apply$mcVI$sp(int index) {
                           Seq racks = (Seq)((TraversableLike)this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().tasks()[index].preferredLocations().map(new Serializable(this) {
                              public static final long serialVersionUID = 0L;

                              public final String apply(TaskLocation x$5) {
                                 return x$5.host();
                              }
                           }, Seq$.MODULE$.canBuildFrom())).flatMap(new Serializable(this) {
                              public static final long serialVersionUID = 0L;
                              // $FF: synthetic field
                              private final <undefinedtype> $outer;

                              public final Iterable<String> apply(String value) {
                                 return Option$.MODULE$.<String>option2Iterable(this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$$outer().org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().org$apache$spark$scheduler$TaskSetManager$$sched.getRackForHost(value));
                              }

                              public {
                                 if ($outer == null) {
                                    throw null;
                                 } else {
                                    this.$outer = $outer;
                                 }
                              }
                           }, Seq$.MODULE$.canBuildFrom());
                           if (racks.contains(this.rack$1)) {
                              this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().speculatableTasks().$minus$eq(BoxesRunTime.boxToInteger(index));
                              throw new NonLocalReturnControl(this.$outer.nonLocalReturnKey1$1, new Some(new Tuple2(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.RACK_LOCAL())));
                           }
                        }

                        // $FF: synthetic method
                        public <undefinedtype> org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$$outer() {
                           return this.$outer;
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.rack$1 = rack$1;
                           }
                        }
                     });
                  }

                  // $FF: synthetic method
                  public TaskSetManager org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer() {
                     return this.$outer;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.execId$3 = execId$3;
                        this.host$4 = host$4;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
            }

            if (TaskLocality$.MODULE$.isAllowed(locality, TaskLocality$.MODULE$.ANY())) {
               this.speculatableTasks().withFilter(new Serializable(this, execId, host) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final String execId$3;
                  private final String host$4;

                  public final boolean apply(int index) {
                     return this.apply$mcZI$sp(index);
                  }

                  public boolean apply$mcZI$sp(int index) {
                     return this.$outer.org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(index, this.execId$3, this.host$4);
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.execId$3 = execId$3;
                        this.host$4 = host$4;
                     }
                  }
               }).foreach(new Serializable(this, var4) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final Object nonLocalReturnKey1$1;

                  public final Nothing$ apply(int index) {
                     this.$outer.speculatableTasks().$minus$eq(BoxesRunTime.boxToInteger(index));
                     throw new NonLocalReturnControl(this.nonLocalReturnKey1$1, new Some(new Tuple2(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.ANY())));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
                     }
                  }
               });
            }
         }

         var10000 = None$.MODULE$;
      } catch (NonLocalReturnControl var6) {
         if (var6.key() != var4) {
            throw var6;
         }

         var10000 = (Option)var6.value();
      }

      return (Option<Tuple2<Object, Enumeration.Value>>)var10000;
   }

   private Option<Tuple3<Object, Enumeration.Value, Object>> dequeueTask(String execId, String host, Enumeration.Value maxLocality) {
      Object var4 = new Object();

      Option var10000;
      try {
         this.org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(execId, host, this.getPendingTasksForExecutor(execId)).foreach(new Serializable(this, var4) {
            public static final long serialVersionUID = 0L;
            private final Object nonLocalReturnKey2$1;

            public final Nothing$ apply(int index) {
               throw new NonLocalReturnControl(this.nonLocalReturnKey2$1, new Some(new Tuple3(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.PROCESS_LOCAL(), BoxesRunTime.boxToBoolean(false))));
            }

            public {
               this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
            }
         });
         if (TaskLocality$.MODULE$.isAllowed(maxLocality, TaskLocality$.MODULE$.NODE_LOCAL())) {
            this.org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(execId, host, this.getPendingTasksForHost(host)).foreach(new Serializable(this, var4) {
               public static final long serialVersionUID = 0L;
               private final Object nonLocalReturnKey2$1;

               public final Nothing$ apply(int index) {
                  throw new NonLocalReturnControl(this.nonLocalReturnKey2$1, new Some(new Tuple3(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.NODE_LOCAL(), BoxesRunTime.boxToBoolean(false))));
               }

               public {
                  this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
               }
            });
         }

         if (TaskLocality$.MODULE$.isAllowed(maxLocality, TaskLocality$.MODULE$.NO_PREF())) {
            this.org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(execId, host, this.pendingTasksWithNoPrefs()).foreach(new Serializable(this, var4) {
               public static final long serialVersionUID = 0L;
               private final Object nonLocalReturnKey2$1;

               public final Nothing$ apply(int index) {
                  throw new NonLocalReturnControl(this.nonLocalReturnKey2$1, new Some(new Tuple3(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.PROCESS_LOCAL(), BoxesRunTime.boxToBoolean(false))));
               }

               public {
                  this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
               }
            });
         }

         if (TaskLocality$.MODULE$.isAllowed(maxLocality, TaskLocality$.MODULE$.RACK_LOCAL())) {
            this.org$apache$spark$scheduler$TaskSetManager$$sched.getRackForHost(host).foreach(new Serializable(this, execId, host, var4) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;
               private final String execId$4;
               private final String host$5;
               public final Object nonLocalReturnKey2$1;

               public final void apply(String rack) {
                  this.$outer.org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(this.execId$4, this.host$5, this.$outer.org$apache$spark$scheduler$TaskSetManager$$getPendingTasksForRack(rack)).foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final Nothing$ apply(int index) {
                        throw new NonLocalReturnControl(this.$outer.nonLocalReturnKey2$1, new Some(new Tuple3(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.RACK_LOCAL(), BoxesRunTime.boxToBoolean(false))));
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
                     this.execId$4 = execId$4;
                     this.host$5 = host$5;
                     this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
                  }
               }
            });
         }

         if (TaskLocality$.MODULE$.isAllowed(maxLocality, TaskLocality$.MODULE$.ANY())) {
            this.org$apache$spark$scheduler$TaskSetManager$$dequeueTaskFromList(execId, host, this.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks()).foreach(new Serializable(this, var4) {
               public static final long serialVersionUID = 0L;
               private final Object nonLocalReturnKey2$1;

               public final Nothing$ apply(int index) {
                  throw new NonLocalReturnControl(this.nonLocalReturnKey2$1, new Some(new Tuple3(BoxesRunTime.boxToInteger(index), TaskLocality$.MODULE$.ANY(), BoxesRunTime.boxToBoolean(false))));
               }

               public {
                  this.nonLocalReturnKey2$1 = nonLocalReturnKey2$1;
               }
            });
         }

         var10000 = this.dequeueSpeculativeTask(execId, host, maxLocality).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple3<Object, Enumeration.Value, Object> apply(Tuple2<Object, Enumeration.Value> x0$1) {
               if (x0$1 != null) {
                  int taskIndex = x0$1._1$mcI$sp();
                  Enumeration.Value allowedLocality = (Enumeration.Value)x0$1._2();
                  Tuple3 var5 = new Tuple3(BoxesRunTime.boxToInteger(taskIndex), allowedLocality, BoxesRunTime.boxToBoolean(true));
                  return var5;
               } else {
                  throw new MatchError(x0$1);
               }
            }
         });
      } catch (NonLocalReturnControl var6) {
         if (var6.key() != var4) {
            throw var6;
         }

         var10000 = (Option)var6.value();
      }

      return var10000;
   }

   public Option<TaskDescription> resourceOffer(String execId, String host, Enumeration.Value maxLocality) throws TaskNotSerializableException {
      boolean offerBlacklisted = this.taskSetBlacklistHelperOpt().exists(new Serializable(this, execId, host) {
         public static final long serialVersionUID = 0L;
         private final String execId$1;
         private final String host$1;

         public final boolean apply(TaskSetBlacklist blacklist) {
            return blacklist.isNodeBlacklistedForTaskSet(this.host$1) || blacklist.isExecutorBlacklistedForTaskSet(this.execId$1);
         }

         public {
            this.execId$1 = execId$1;
            this.host$1 = host$1;
         }
      });
      Object var10000;
      if (!this.isZombie() && !offerBlacklisted) {
         long curTime;
         Enumeration.Value allowedLocality;
         label20: {
            curTime = this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis();
            allowedLocality = maxLocality;
            Enumeration.Value var8 = TaskLocality$.MODULE$.NO_PREF();
            if (maxLocality == null) {
               if (var8 == null) {
                  break label20;
               }
            } else if (maxLocality.equals(var8)) {
               break label20;
            }

            allowedLocality = this.getAllowedLocalityLevel(curTime);
            if (allowedLocality.$greater(maxLocality)) {
               allowedLocality = maxLocality;
            }
         }

         var10000 = this.dequeueTask(execId, host, allowedLocality).map(new Serializable(this, execId, host, maxLocality, curTime) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;
            private final String execId$1;
            public final String host$1;
            private final Enumeration.Value maxLocality$1;
            private final long curTime$1;

            public final TaskDescription apply(Tuple3<Object, Enumeration.Value, Object> x0$2) {
               if (x0$2 == null) {
                  throw new MatchError(x0$2);
               } else {
                  int index;
                  Enumeration.Value taskLocality;
                  Task task;
                  long taskId;
                  int attemptNum;
                  TaskInfo info;
                  label68: {
                     index = BoxesRunTime.unboxToInt(x0$2._1());
                     taskLocality = (Enumeration.Value)x0$2._2();
                     boolean speculative = BoxesRunTime.unboxToBoolean(x0$2._3());
                     task = this.$outer.tasks()[index];
                     taskId = this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.newTaskId();
                     ++this.$outer.copiesRunning()[index];
                     attemptNum = this.$outer.taskAttempts()[index].size();
                     info = new TaskInfo(taskId, index, attemptNum, this.curTime$1, this.execId$1, this.host$1, taskLocality, speculative);
                     this.$outer.org$apache$spark$scheduler$TaskSetManager$$taskInfos().update(BoxesRunTime.boxToLong(taskId), info);
                     this.$outer.taskAttempts()[index] = this.$outer.taskAttempts()[index].$colon$colon(info);
                     Enumeration.Value var10000 = this.maxLocality$1;
                     Enumeration.Value var13 = TaskLocality$.MODULE$.NO_PREF();
                     if (var10000 == null) {
                        if (var13 == null) {
                           break label68;
                        }
                     } else if (var10000.equals(var13)) {
                        break label68;
                     }

                     this.$outer.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex_$eq(this.$outer.getLocalityIndex(taskLocality));
                     this.$outer.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime_$eq(this.curTime$1);
                  }

                  ByteBuffer var24;
                  try {
                     var24 = this.$outer.ser().serialize(task, ClassTag$.MODULE$.apply(Task.class));
                  } catch (Throwable var23) {
                     Option var17 = NonFatal$.MODULE$.unapply(var23);
                     if (var17.isEmpty()) {
                        throw var23;
                     }

                     Throwable e = (Throwable)var17.get();
                     String msg = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to serialize task ", ", not attempting to retry it."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(taskId)}));
                     this.$outer.logError(new Serializable(this, msg) {
                        public static final long serialVersionUID = 0L;
                        private final String msg$1;

                        public final String apply() {
                           return this.msg$1;
                        }

                        public {
                           this.msg$1 = msg$1;
                        }
                     }, e);
                     this.$outer.abort((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " Exception during serialization: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{msg, e})), this.$outer.abort$default$2());
                     throw new TaskNotSerializableException(e);
                  }

                  ByteBuffer serializedTask = var24;
                  if (serializedTask.limit() > TaskSetManager$.MODULE$.TASK_SIZE_TO_WARN_KB() * 1024 && !this.$outer.emittedTaskSizeWarning()) {
                     this.$outer.emittedTaskSizeWarning_$eq(true);
                     this.$outer.logWarning(new Serializable(this, task, serializedTask) {
                        public static final long serialVersionUID = 0L;
                        private final Task task$1;
                        private final ByteBuffer serializedTask$1;

                        public final String apply() {
                           return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage ", " contains a task of very large size "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.task$1.stageId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", " KB). The maximum recommended task size is "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.serializedTask$1.limit() / 1024)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " KB."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(TaskSetManager$.MODULE$.TASK_SIZE_TO_WARN_KB())}))).toString();
                        }

                        public {
                           this.task$1 = task$1;
                           this.serializedTask$1 = serializedTask$1;
                        }
                     });
                  }

                  this.$outer.addRunningTask(taskId);
                  String taskName = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"task ", " in stage ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{info.id(), this.$outer.taskSet().id()}));
                  this.$outer.logInfo(new Serializable(this, taskLocality, task, taskId, info, serializedTask, taskName) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;
                     private final Enumeration.Value taskLocality$1;
                     private final Task task$1;
                     private final long taskId$1;
                     private final TaskInfo info$1;
                     private final ByteBuffer serializedTask$1;
                     private final String taskName$1;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Starting ", " (TID ", ", ", ", executor ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.taskName$1, BoxesRunTime.boxToLong(this.taskId$1), this.$outer.host$1, this.info$1.executorId()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partition ", ", ", ", ", " bytes)"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.task$1.partitionId()), this.taskLocality$1, BoxesRunTime.boxToInteger(this.serializedTask$1.limit())}))).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.taskLocality$1 = taskLocality$1;
                           this.task$1 = task$1;
                           this.taskId$1 = taskId$1;
                           this.info$1 = info$1;
                           this.serializedTask$1 = serializedTask$1;
                           this.taskName$1 = taskName$1;
                        }
                     }
                  });
                  this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskStarted(task, info);
                  TaskDescription var6 = new TaskDescription(taskId, attemptNum, this.execId$1, taskName, index, this.$outer.org$apache$spark$scheduler$TaskSetManager$$addedFiles(), this.$outer.org$apache$spark$scheduler$TaskSetManager$$addedJars(), task.localProperties(), serializedTask);
                  return var6;
               }
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.execId$1 = execId$1;
                  this.host$1 = host$1;
                  this.maxLocality$1 = maxLocality$1;
                  this.curTime$1 = curTime$1;
               }
            }
         });
      } else {
         var10000 = None$.MODULE$;
      }

      return (Option<TaskDescription>)var10000;
   }

   private void maybeFinishTaskSet() {
      if (this.isZombie() && this.runningTasks() == 0) {
         this.org$apache$spark$scheduler$TaskSetManager$$sched.taskSetFinished(this);
         if (this.tasksSuccessful() == this.numTasks()) {
            this.org$apache$spark$scheduler$TaskSetManager$$blacklistTracker.foreach(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;

               public final void apply(BlacklistTracker x$7) {
                  x$7.updateBlacklistForSuccessfulTaskSet(this.$outer.taskSet().stageId(), this.$outer.taskSet().stageAttemptId(), ((TaskSetBlacklist)this.$outer.taskSetBlacklistHelperOpt().get()).execToFailures());
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
      }

   }

   private Enumeration.Value getAllowedLocalityLevel(long curTime) {
      while(this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() < this.myLocalityLevels().length - 1) {
         boolean moreTasks;
         label63: {
            label70: {
               Enumeration.Value var4 = this.myLocalityLevels()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()];
               Enumeration.Value var10000 = TaskLocality$.MODULE$.PROCESS_LOCAL();
               if (var10000 == null) {
                  if (var4 == null) {
                     break label70;
                  }
               } else if (var10000.equals(var4)) {
                  break label70;
               }

               label71: {
                  var10000 = TaskLocality$.MODULE$.NODE_LOCAL();
                  if (var10000 == null) {
                     if (var4 == null) {
                        break label71;
                     }
                  } else if (var10000.equals(var4)) {
                     break label71;
                  }

                  label72: {
                     var10000 = TaskLocality$.MODULE$.NO_PREF();
                     if (var10000 == null) {
                        if (var4 == null) {
                           break label72;
                        }
                     } else if (var10000.equals(var4)) {
                        break label72;
                     }

                     var10000 = TaskLocality$.MODULE$.RACK_LOCAL();
                     if (var10000 == null) {
                        if (var4 != null) {
                           throw new MatchError(var4);
                        }
                     } else if (!var10000.equals(var4)) {
                        throw new MatchError(var4);
                     }

                     moreTasks = this.moreTasksToRunIn$1(this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack());
                     break label63;
                  }

                  moreTasks = this.pendingTasksWithNoPrefs().nonEmpty();
                  break label63;
               }

               moreTasks = this.moreTasksToRunIn$1(this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost());
               break label63;
            }

            moreTasks = this.moreTasksToRunIn$1(this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor());
         }

         if (moreTasks) {
            if (curTime - this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime() < this.localityWaits()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()]) {
               return this.myLocalityLevels()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()];
            }

            this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime_$eq(this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime() + this.localityWaits()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()]);
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Moving to ", " after waiting for "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.myLocalityLevels()[this.$outer.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() + 1]}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.localityWaits()[this.$outer.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()])}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex_$eq(this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() + 1);
         } else {
            this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime_$eq(curTime);
            this.logDebug(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"No tasks for locality level ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.myLocalityLevels()[this.$outer.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()]}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"so moving to locality level ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.myLocalityLevels()[this.$outer.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() + 1]}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
            this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex_$eq(this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex() + 1);
         }
      }

      return this.myLocalityLevels()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()];
   }

   public int getLocalityIndex(Enumeration.Value locality) {
      int index;
      for(index = 0; locality.$greater(this.myLocalityLevels()[index]); ++index) {
      }

      return index;
   }

   public void abortIfCompletelyBlacklisted(HashMap<String, HashSet<String>> hostToExecutors) {
      this.taskSetBlacklistHelperOpt().foreach(new Serializable(this, hostToExecutors) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;
         public final HashMap hostToExecutors$1;

         public final void apply(TaskSetBlacklist taskSetBlacklist) {
            BlacklistTracker appBlacklist = this.$outer.org$apache$spark$scheduler$TaskSetManager$$blacklistTracker.get();
            if (this.hostToExecutors$1.nonEmpty()) {
               int indexOffset = this.$outer.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks().lastIndexWhere(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final boolean apply(int indexInTaskSet) {
                     return this.apply$mcZI$sp(indexInTaskSet);
                  }

                  public boolean apply$mcZI$sp(int indexInTaskSet) {
                     return this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().copiesRunning()[indexInTaskSet] == 0 && !this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().successful()[indexInTaskSet];
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
               Option pendingTask = (Option)(indexOffset == -1 ? None$.MODULE$ : new Some(this.$outer.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks().apply(indexOffset)));
               pendingTask.foreach(new Serializable(this, appBlacklist, taskSetBlacklist) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  public final BlacklistTracker appBlacklist$1;
                  public final TaskSetBlacklist taskSetBlacklist$1;

                  public final void apply(int indexInTaskSet) {
                     this.apply$mcVI$sp(indexInTaskSet);
                  }

                  public void apply$mcVI$sp(int indexInTaskSet) {
                     boolean blacklistedEverywhere = this.$outer.hostToExecutors$1.forall(new Serializable(this, indexInTaskSet) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        public final int indexInTaskSet$1;

                        public final boolean apply(Tuple2<String, HashSet<String>> x0$4) {
                           if (x0$4 == null) {
                              throw new MatchError(x0$4);
                           } else {
                              String host = (String)x0$4._1();
                              HashSet execsOnHost = (HashSet)x0$4._2();
                              boolean nodeBlacklisted = this.$outer.appBlacklist$1.isNodeBlacklisted(host) || this.$outer.taskSetBlacklist$1.isNodeBlacklistedForTaskSet(host) || this.$outer.taskSetBlacklist$1.isNodeBlacklistedForTask(host, this.indexInTaskSet$1);
                              boolean var5 = nodeBlacklisted ? true : execsOnHost.forall(new Serializable(this) {
                                 public static final long serialVersionUID = 0L;
                                 // $FF: synthetic field
                                 private final <undefinedtype> $outer;

                                 public final boolean apply(String exec) {
                                    return this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$anonfun$$$outer().appBlacklist$1.isExecutorBlacklisted(exec) || this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$anonfun$$$outer().taskSetBlacklist$1.isExecutorBlacklistedForTaskSet(exec) || this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$anonfun$$$outer().taskSetBlacklist$1.isExecutorBlacklistedForTask(exec, this.$outer.indexInTaskSet$1);
                                 }

                                 public {
                                    if ($outer == null) {
                                       throw null;
                                    } else {
                                       this.$outer = $outer;
                                    }
                                 }
                              });
                              return var5;
                           }
                        }

                        // $FF: synthetic method
                        public <undefinedtype> org$apache$spark$scheduler$TaskSetManager$$anonfun$$anonfun$$anonfun$$$outer() {
                           return this.$outer;
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.indexInTaskSet$1 = indexInTaskSet$1;
                           }
                        }
                     });
                     if (blacklistedEverywhere) {
                        int partition = this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().tasks()[indexInTaskSet].partitionId();
                        this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().abort((new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n              |Aborting ", " because task ", " (partition ", ")\n              |cannot run anywhere due to node and executor blacklist.\n              |Most recent failure:\n              |", "\n              |\n              |Blacklisting behavior can be configured via spark.blacklist.*.\n              |"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().taskSet(), BoxesRunTime.boxToInteger(indexInTaskSet), BoxesRunTime.boxToInteger(partition), this.taskSetBlacklist$1.getLatestFailureReason()}))))).stripMargin(), this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().abort$default$2());
                     }

                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.appBlacklist$1 = appBlacklist$1;
                        this.taskSetBlacklist$1 = taskSetBlacklist$1;
                     }
                  }
               });
            }

         }

         // $FF: synthetic method
         public TaskSetManager org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.hostToExecutors$1 = hostToExecutors$1;
            }
         }
      });
   }

   public void handleTaskGettingResult(long tid) {
      TaskInfo info = (TaskInfo)this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid));
      info.markGettingResult(this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis());
      this.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskGettingResult(info);
   }

   public boolean canFetchMoreResults(long size) {
      Boolean var4;
      synchronized(this.org$apache$spark$scheduler$TaskSetManager$$sched) {
         this.totalResultSize_$eq(this.totalResultSize() + size);
         this.calculatedTasks_$eq(this.calculatedTasks() + 1);
         Boolean var10000;
         if (this.maxResultSize() > 0L && this.totalResultSize() > this.maxResultSize()) {
            String msg = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Total size of serialized results of ", " tasks "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.calculatedTasks())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", ") is bigger than spark.driver.maxResultSize "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.totalResultSize())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"(", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{Utils$.MODULE$.bytesToString(this.maxResultSize())}))).toString();
            this.logError(new Serializable(this, msg) {
               public static final long serialVersionUID = 0L;
               private final String msg$2;

               public final String apply() {
                  return this.msg$2;
               }

               public {
                  this.msg$2 = msg$2;
               }
            });
            this.abort(msg, this.abort$default$2());
            var10000 = BoxesRunTime.boxToBoolean(false);
         } else {
            var10000 = BoxesRunTime.boxToBoolean(true);
         }

         var4 = var10000;
      }

      return BoxesRunTime.unboxToBoolean(var4);
   }

   public void handleSuccessfulTask(long tid, DirectTaskResult<?> result) {
      TaskInfo info = (TaskInfo)this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid));
      int index = info.index();
      info.markFinished(TaskState$.MODULE$.FINISHED(), this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis());
      if (this.speculationEnabled()) {
         this.successfulTaskDurations().insert((double)info.duration());
      }

      this.removeRunningTask(tid);
      this.taskAttempts()[index].withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(TaskInfo attemptInfo) {
            return attemptInfo.running();
         }
      }).foreach(new Serializable(this, info, index) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;
         public final TaskInfo info$2;
         private final int index$3;

         public final void apply(TaskInfo attemptInfo) {
            this.$outer.logInfo(new Serializable(this, attemptInfo) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final TaskInfo attemptInfo$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Killing attempt ", " for task ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptInfo$1.attemptNumber()), this.attemptInfo$1.id()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"in stage ", " (TID ", ") on ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().taskSet().id(), BoxesRunTime.boxToLong(this.attemptInfo$1.taskId()), this.attemptInfo$1.host()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"as the attempt ", " succeeded on ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.info$2.attemptNumber()), this.$outer.info$2.host()}))).toString();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.attemptInfo$1 = attemptInfo$1;
                  }
               }
            });
            this.$outer.org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt()[this.index$3] = true;
            this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.backend().killTask(attemptInfo.taskId(), attemptInfo.executorId(), true, "another attempt succeeded");
         }

         // $FF: synthetic method
         public TaskSetManager org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.info$2 = info$2;
               this.index$3 = index$3;
            }
         }
      });
      if (this.successful()[index]) {
         this.logInfo(new Serializable(this, info, index) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;
            private final TaskInfo info$2;
            private final int index$3;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Ignoring task-finished event for ").append((Object)this.info$2.id()).append((Object)" in stage ").append((Object)this.$outer.taskSet().id()).append((Object)" because task ").append((Object)BoxesRunTime.boxToInteger(this.index$3)).append((Object)" has already completed successfully").toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.info$2 = info$2;
                  this.index$3 = index$3;
               }
            }
         });
      } else {
         this.tasksSuccessful_$eq(this.tasksSuccessful() + 1);
         this.logInfo(new Serializable(this, info) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;
            private final TaskInfo info$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Finished task ", " in stage ", " (TID ", ") in"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$2.id(), this.$outer.taskSet().id(), BoxesRunTime.boxToLong(this.info$2.taskId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", " ms on ", " (executor ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.info$2.duration()), this.info$2.host(), this.info$2.executorId()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" (", "/", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.$outer.tasksSuccessful()), BoxesRunTime.boxToInteger(this.$outer.numTasks())}))).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.info$2 = info$2;
               }
            }
         });
         this.successful()[index] = true;
         if (this.tasksSuccessful() == this.numTasks()) {
            this.isZombie_$eq(true);
         }
      }

      this.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskEnded(this.tasks()[index], Success$.MODULE$, result.value(result.value$default$1()), result.accumUpdates(), info);
      this.maybeFinishTaskSet();
   }

   public void handleFailedTask(long tid, Enumeration.Value state, TaskFailedReason reason) {
      TaskInfo info = (TaskInfo)this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid));
      if (!info.failed() && !info.killed()) {
         ExceptionFailure var13;
         label89: {
            this.removeRunningTask(tid);
            info.markFinished(state, this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis());
            int index = info.index();
            --this.copiesRunning()[index];
            Seq accumUpdates = (Seq)Seq$.MODULE$.empty();
            String failureReason = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lost task ", " in stage ", " (TID ", ", ", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{info.id(), this.taskSet().id(), BoxesRunTime.boxToLong(tid), info.host()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" executor ", "): ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{info.executorId(), reason.toErrorString()}))).toString();
            Object failureException;
            if (reason instanceof FetchFailed) {
               FetchFailed var11 = (FetchFailed)reason;
               this.logWarning(new Serializable(this, failureReason) {
                  public static final long serialVersionUID = 0L;
                  private final String failureReason$1;

                  public final String apply() {
                     return this.failureReason$1;
                  }

                  public {
                     this.failureReason$1 = failureReason$1;
                  }
               });
               if (!this.successful()[index]) {
                  this.successful()[index] = true;
                  this.tasksSuccessful_$eq(this.tasksSuccessful() + 1);
               }

               this.isZombie_$eq(true);
               if (var11.bmAddress() != null) {
                  this.org$apache$spark$scheduler$TaskSetManager$$blacklistTracker.foreach(new Serializable(this, var11) {
                     public static final long serialVersionUID = 0L;
                     private final FetchFailed x2$1;

                     public final void apply(BlacklistTracker x$8) {
                        x$8.updateBlacklistForFetchFailure(this.x2$1.bmAddress().host(), this.x2$1.bmAddress().executorId());
                     }

                     public {
                        this.x2$1 = x2$1;
                     }
                  });
               }

               failureException = None$.MODULE$;
            } else if (reason instanceof ExceptionFailure) {
               var13 = (ExceptionFailure)reason;
               accumUpdates = var13.accums();
               String var10000 = var13.className();
               String var14 = NotSerializableException.class.getName();
               if (var10000 == null) {
                  if (var14 == null) {
                     break label89;
                  }
               } else if (var10000.equals(var14)) {
                  break label89;
               }

               String key = var13.description();
               long now = this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis();
               Tuple2$mcZI$sp var35;
               if (this.recentExceptions().contains(key)) {
                  Tuple2 var21 = (Tuple2)this.recentExceptions().apply(key);
                  if (var21 == null) {
                     throw new MatchError(var21);
                  }

                  int dupCount = var21._1$mcI$sp();
                  long printTime = var21._2$mcJ$sp();
                  Tuple2$mcIJ$sp var25 = new Tuple2$mcIJ$sp(dupCount, printTime);
                  int dupCount = ((Tuple2)var25)._1$mcI$sp();
                  long printTime = ((Tuple2)var25)._2$mcJ$sp();
                  if (now - printTime > this.EXCEPTION_PRINT_INTERVAL()) {
                     this.recentExceptions().update(key, new Tuple2$mcIJ$sp(0, now));
                     var35 = new Tuple2$mcZI$sp(true, 0);
                  } else {
                     this.recentExceptions().update(key, new Tuple2$mcIJ$sp(dupCount + 1, printTime));
                     var35 = new Tuple2$mcZI$sp(false, dupCount + 1);
                  }
               } else {
                  this.recentExceptions().update(key, new Tuple2$mcIJ$sp(0, now));
                  var35 = new Tuple2$mcZI$sp(true, 0);
               }

               Tuple2$mcZI$sp var19 = var35;
               if (var19 == null) {
                  throw new MatchError(var19);
               }

               boolean printFull = ((Tuple2)var19)._1$mcZ$sp();
               int dupCount = ((Tuple2)var19)._2$mcI$sp();
               Tuple2$mcZI$sp var31 = new Tuple2$mcZI$sp(printFull, dupCount);
               boolean printFull = ((Tuple2)var31)._1$mcZ$sp();
               int dupCount = ((Tuple2)var31)._2$mcI$sp();
               if (printFull) {
                  this.logWarning(new Serializable(this, failureReason) {
                     public static final long serialVersionUID = 0L;
                     private final String failureReason$1;

                     public final String apply() {
                        return this.failureReason$1;
                     }

                     public {
                        this.failureReason$1 = failureReason$1;
                     }
                  });
               } else {
                  this.logInfo(new Serializable(this, tid, info, dupCount, var13) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskSetManager $outer;
                     private final long tid$1;
                     private final TaskInfo info$3;
                     private final int dupCount$1;
                     private final ExceptionFailure x3$2;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Lost task ", " in stage ", " (TID ", ") on ", ", executor"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$3.id(), this.$outer.taskSet().id(), BoxesRunTime.boxToLong(this.tid$1), this.info$3.host()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" ", ": ", " (", ") [duplicate ", "]"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$3.executorId(), this.x3$2.className(), this.x3$2.description(), BoxesRunTime.boxToInteger(this.dupCount$1)}))).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.tid$1 = tid$1;
                           this.info$3 = info$3;
                           this.dupCount$1 = dupCount$1;
                           this.x3$2 = x3$2;
                        }
                     }
                  });
               }

               failureException = var13.exception();
            } else {
               label92: {
                  if (reason instanceof ExecutorLostFailure) {
                     ExecutorLostFailure var34 = (ExecutorLostFailure)reason;
                     if (!var34.exitCausedByApp()) {
                        this.logInfo(new Serializable(this, tid) {
                           public static final long serialVersionUID = 0L;
                           private final long tid$1;

                           public final String apply() {
                              return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " failed because while it was being computed, its executor "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.tid$1)}))).append((Object)"exited for a reason unrelated to the task. Not counting this failure towards the ").append((Object)"maximum number of failures for the task.").toString();
                           }

                           public {
                              this.tid$1 = tid$1;
                           }
                        });
                        failureException = None$.MODULE$;
                        break label92;
                     }
                  }

                  if (reason == null) {
                     throw new MatchError(reason);
                  }

                  this.logWarning(new Serializable(this, failureReason) {
                     public static final long serialVersionUID = 0L;
                     private final String failureReason$1;

                     public final String apply() {
                        return this.failureReason$1;
                     }

                     public {
                        this.failureReason$1 = failureReason$1;
                     }
                  });
                  failureException = None$.MODULE$;
               }
            }

            this.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskEnded(this.tasks()[index], reason, (Object)null, accumUpdates, info);
            if (!this.isZombie() && reason.countTowardsTaskFailures()) {
               Predef$.MODULE$.assert(failureReason != null);
               this.taskSetBlacklistHelperOpt().foreach(new Serializable(this, info, index, failureReason) {
                  public static final long serialVersionUID = 0L;
                  private final TaskInfo info$3;
                  private final int index$4;
                  private final String failureReason$1;

                  public final void apply(TaskSetBlacklist x$11) {
                     x$11.updateBlacklistForFailedTask(this.info$3.host(), this.info$3.executorId(), this.index$4, this.failureReason$1);
                  }

                  public {
                     this.info$3 = info$3;
                     this.index$4 = index$4;
                     this.failureReason$1 = failureReason$1;
                  }
               });
               ++this.numFailures()[index];
               if (this.numFailures()[index] >= this.maxTaskFailures()) {
                  this.logError(new Serializable(this, index) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final TaskSetManager $outer;
                     private final int index$4;

                     public final String apply() {
                        return (new StringOps(Predef$.MODULE$.augmentString("Task %d in stage %s failed %d times; aborting job"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.index$4), this.$outer.taskSet().id(), BoxesRunTime.boxToInteger(this.$outer.maxTaskFailures())}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                           this.index$4 = index$4;
                        }
                     }
                  });
                  this.abort((new StringOps(Predef$.MODULE$.augmentString("Task %d in stage %s failed %d times, most recent failure: %s\nDriver stacktrace:"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(index), this.taskSet().id(), BoxesRunTime.boxToInteger(this.maxTaskFailures()), failureReason})), (Option)failureException);
                  return;
               }
            }

            if (this.successful()[index]) {
               this.logInfo(new Serializable(this, tid, info) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final TaskSetManager $outer;
                  private final long tid$1;
                  private final TaskInfo info$3;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task ", " in stage ", " (TID ", ") failed, but the task will not"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$3.id(), this.$outer.taskSet().id(), BoxesRunTime.boxToLong(this.tid$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" be re-executed (either because the task failed with a shuffle data fetch failure,"})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" so the previous stage needs to be re-run, or because a different copy of the task"})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" has already succeeded)."})))).s(Nil$.MODULE$)).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.tid$1 = tid$1;
                        this.info$3 = info$3;
                     }
                  }
               });
            } else {
               this.addPendingTask(index);
            }

            this.maybeFinishTaskSet();
            return;
         }

         this.logError(new Serializable(this, tid, info, var13) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;
            private final long tid$1;
            private final TaskInfo info$3;
            private final ExceptionFailure x3$2;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Task %s in stage %s (TID %d) had a not serializable result: %s; not retrying"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.info$3.id(), this.$outer.taskSet().id(), BoxesRunTime.boxToLong(this.tid$1), this.x3$2.description()}));
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.tid$1 = tid$1;
                  this.info$3 = info$3;
                  this.x3$2 = x3$2;
               }
            }
         });
         this.abort((new StringOps(Predef$.MODULE$.augmentString("Task %s in stage %s (TID %d) had a not serializable result: %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{info.id(), this.taskSet().id(), BoxesRunTime.boxToLong(tid), var13.description()})), this.abort$default$2());
      }
   }

   public void abort(String message, Option<Throwable> exception) {
      synchronized(this.org$apache$spark$scheduler$TaskSetManager$$sched) {
         this.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskSetFailed(this.taskSet(), message, exception);
         this.isZombie_$eq(true);
         this.maybeFinishTaskSet();
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }
   }

   public Option<Throwable> abort$default$2() {
      return None$.MODULE$;
   }

   public void addRunningTask(long tid) {
      if (this.runningTasksSet().add(BoxesRunTime.boxToLong(tid)) && this.parent() != null) {
         this.parent().increaseRunningTasks(1);
      }

   }

   public void removeRunningTask(long tid) {
      if (this.runningTasksSet().remove(BoxesRunTime.boxToLong(tid)) && this.parent() != null) {
         this.parent().decreaseRunningTasks(1);
      }

   }

   public Schedulable getSchedulableByName(String name) {
      return null;
   }

   public void addSchedulable(Schedulable schedulable) {
   }

   public void removeSchedulable(Schedulable schedulable) {
   }

   public ArrayBuffer<TaskSetManager> getSortedTaskSetQueue() {
      ArrayBuffer sortedTaskSetQueue = new ArrayBuffer();
      sortedTaskSetQueue.$plus$eq(this);
      return sortedTaskSetQueue;
   }

   public void executorLost(String execId, String host, ExecutorLossReason reason) {
      if (this.tasks()[0] instanceof ShuffleMapTask && !this.env().blockManager().externalShuffleServiceEnabled() && !this.isZombie()) {
         this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().withFilter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Tuple2<Object, TaskInfo> check$ifrefutable$1) {
               boolean var3;
               if (check$ifrefutable$1 != null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }
         }).withFilter(new Serializable(this, execId) {
            public static final long serialVersionUID = 0L;
            private final String execId$5;

            public final boolean apply(Tuple2<Object, TaskInfo> x$12) {
               if (x$12 == null) {
                  throw new MatchError(x$12);
               } else {
                  boolean var6;
                  label20: {
                     label19: {
                        TaskInfo info = (TaskInfo)x$12._2();
                        String var10000 = info.executorId();
                        String var5 = this.execId$5;
                        if (var10000 == null) {
                           if (var5 == null) {
                              break label19;
                           }
                        } else if (var10000.equals(var5)) {
                           break label19;
                        }

                        var6 = false;
                        break label20;
                     }

                     var6 = true;
                  }

                  boolean var4 = var6;
                  return var4;
               }
            }

            public {
               this.execId$5 = execId$5;
            }
         }).foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final TaskSetManager $outer;

            public final void apply(Tuple2<Object, TaskInfo> x$13) {
               if (x$13 == null) {
                  throw new MatchError(x$13);
               } else {
                  long tid = x$13._1$mcJ$sp();
                  TaskInfo info = (TaskInfo)x$13._2();
                  int index = ((TaskInfo)this.$outer.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid))).index();
                  if (this.$outer.successful()[index] && !this.$outer.org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt()[index]) {
                     this.$outer.successful()[index] = false;
                     --this.$outer.copiesRunning()[index];
                     this.$outer.tasksSuccessful_$eq(this.$outer.tasksSuccessful() - 1);
                     this.$outer.addPendingTask(index);
                     this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().taskEnded(this.$outer.tasks()[index], Resubmitted$.MODULE$, (Object)null, (Seq)Seq$.MODULE$.empty(), info);
                     BoxedUnit var8 = BoxedUnit.UNIT;
                  } else {
                     BoxedUnit var10000 = BoxedUnit.UNIT;
                  }

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

      this.org$apache$spark$scheduler$TaskSetManager$$taskInfos().withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<Object, TaskInfo> check$ifrefutable$2) {
            boolean var3;
            if (check$ifrefutable$2 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).withFilter(new Serializable(this, execId) {
         public static final long serialVersionUID = 0L;
         private final String execId$5;

         public final boolean apply(Tuple2<Object, TaskInfo> x$14) {
            if (x$14 == null) {
               throw new MatchError(x$14);
            } else {
               boolean var6;
               label22: {
                  label21: {
                     TaskInfo info = (TaskInfo)x$14._2();
                     if (info.running()) {
                        String var10000 = info.executorId();
                        String var5 = this.execId$5;
                        if (var10000 == null) {
                           if (var5 == null) {
                              break label21;
                           }
                        } else if (var10000.equals(var5)) {
                           break label21;
                        }
                     }

                     var6 = false;
                     break label22;
                  }

                  var6 = true;
               }

               boolean var4 = var6;
               return var4;
            }
         }

         public {
            this.execId$5 = execId$5;
         }
      }).foreach(new Serializable(this, reason) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;
         private final ExecutorLossReason reason$1;

         public final void apply(Tuple2<Object, TaskInfo> x$15) {
            if (x$15 != null) {
               long tid = x$15._1$mcJ$sp();
               TaskInfo info = (TaskInfo)x$15._2();
               ExecutorLossReason var8 = this.reason$1;
               boolean exitCausedByApp;
               if (var8 instanceof ExecutorExited) {
                  ExecutorExited var9 = (ExecutorExited)var8;
                  exitCausedByApp = var9.exitCausedByApp();
               } else if (ExecutorKilled$.MODULE$.equals(var8)) {
                  exitCausedByApp = false;
               } else {
                  exitCausedByApp = true;
               }

               this.$outer.handleFailedTask(tid, TaskState$.MODULE$.FAILED(), new ExecutorLostFailure(info.executorId(), exitCausedByApp, new Some(this.reason$1.toString())));
               BoxedUnit var6 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x$15);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.reason$1 = reason$1;
            }
         }
      });
      this.recomputeLocality();
   }

   public boolean checkSpeculatableTasks(int minTimeToSpeculation) {
      if (!this.isZombie() && this.numTasks() != 1) {
         BooleanRef foundTasks = BooleanRef.create(false);
         int minFinishedForSpeculation = (int)RichDouble$.MODULE$.floor$extension(Predef$.MODULE$.doubleWrapper(this.SPECULATION_QUANTILE() * (double)this.numTasks()));
         this.logDebug(new Serializable(this, minFinishedForSpeculation) {
            public static final long serialVersionUID = 0L;
            private final int minFinishedForSpeculation$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Checking for speculative tasks: minFinished = ").append((Object)BoxesRunTime.boxToInteger(this.minFinishedForSpeculation$1)).toString();
            }

            public {
               this.minFinishedForSpeculation$1 = minFinishedForSpeculation$1;
            }
         });
         if (this.tasksSuccessful() >= minFinishedForSpeculation && this.tasksSuccessful() > 0) {
            long time = this.org$apache$spark$scheduler$TaskSetManager$$clock.getTimeMillis();
            double medianDuration = this.successfulTaskDurations().median();
            double threshold = scala.math.package$.MODULE$.max(this.SPECULATION_MULTIPLIER() * medianDuration, (double)minTimeToSpeculation);
            this.logDebug(new Serializable(this, threshold) {
               public static final long serialVersionUID = 0L;
               private final double threshold$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Task length threshold for speculation: ").append((Object)BoxesRunTime.boxToDouble(this.threshold$1)).toString();
               }

               public {
                  this.threshold$1 = threshold$1;
               }
            });
            this.runningTasksSet().foreach(new Serializable(this, foundTasks, time, threshold) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final TaskSetManager $outer;
               private final BooleanRef foundTasks$1;
               private final long time$1;
               public final double threshold$1;

               public final void apply(long tid) {
                  this.apply$mcVJ$sp(tid);
               }

               public void apply$mcVJ$sp(long tid) {
                  TaskInfo info = (TaskInfo)this.$outer.org$apache$spark$scheduler$TaskSetManager$$taskInfos().apply(BoxesRunTime.boxToLong(tid));
                  int index = info.index();
                  if (!this.$outer.successful()[index] && this.$outer.copiesRunning()[index] == 1 && (double)info.timeRunning(this.time$1) > this.threshold$1 && !this.$outer.speculatableTasks().contains(BoxesRunTime.boxToInteger(index))) {
                     this.$outer.logInfo(new Serializable(this, info, index) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;
                        private final TaskInfo info$4;
                        private final int index$5;

                        public final String apply() {
                           return (new StringOps(Predef$.MODULE$.augmentString("Marking task %d in stage %s (on %s) as speculatable because it ran more than %.0f ms"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.index$5), this.$outer.org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer().taskSet().id(), this.info$4.host(), BoxesRunTime.boxToDouble(this.$outer.threshold$1)}));
                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                              this.info$4 = info$4;
                              this.index$5 = index$5;
                           }
                        }
                     });
                     this.$outer.speculatableTasks().$plus$eq(BoxesRunTime.boxToInteger(index));
                     this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.dagScheduler().speculativeTaskSubmitted(this.$outer.tasks()[index]);
                     this.foundTasks$1.elem = true;
                  }

               }

               // $FF: synthetic method
               public TaskSetManager org$apache$spark$scheduler$TaskSetManager$$anonfun$$$outer() {
                  return this.$outer;
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.foundTasks$1 = foundTasks$1;
                     this.time$1 = time$1;
                     this.threshold$1 = threshold$1;
                  }
               }
            });
         }

         return foundTasks.elem;
      } else {
         return false;
      }
   }

   public long org$apache$spark$scheduler$TaskSetManager$$getLocalityWait(Enumeration.Value level) {
      long defaultWait;
      String localityWaitKey;
      label63: {
         defaultWait = BoxesRunTime.unboxToLong(this.org$apache$spark$scheduler$TaskSetManager$$conf().get(org.apache.spark.internal.config.package$.MODULE$.LOCALITY_WAIT()));
         Enumeration.Value var10000 = TaskLocality$.MODULE$.PROCESS_LOCAL();
         if (var10000 == null) {
            if (level == null) {
               break label63;
            }
         } else if (var10000.equals(level)) {
            break label63;
         }

         label64: {
            var10000 = TaskLocality$.MODULE$.NODE_LOCAL();
            if (var10000 == null) {
               if (level == null) {
                  break label64;
               }
            } else if (var10000.equals(level)) {
               break label64;
            }

            label65: {
               var10000 = TaskLocality$.MODULE$.RACK_LOCAL();
               if (var10000 == null) {
                  if (level == null) {
                     break label65;
                  }
               } else if (var10000.equals(level)) {
                  break label65;
               }

               localityWaitKey = null;
               return localityWaitKey == null ? 0L : this.org$apache$spark$scheduler$TaskSetManager$$conf().getTimeAsMs(localityWaitKey, BoxesRunTime.boxToLong(defaultWait).toString());
            }

            localityWaitKey = "spark.locality.wait.rack";
            return localityWaitKey == null ? 0L : this.org$apache$spark$scheduler$TaskSetManager$$conf().getTimeAsMs(localityWaitKey, BoxesRunTime.boxToLong(defaultWait).toString());
         }

         localityWaitKey = "spark.locality.wait.node";
         return localityWaitKey == null ? 0L : this.org$apache$spark$scheduler$TaskSetManager$$conf().getTimeAsMs(localityWaitKey, BoxesRunTime.boxToLong(defaultWait).toString());
      }

      localityWaitKey = "spark.locality.wait.process";
      return localityWaitKey == null ? 0L : this.org$apache$spark$scheduler$TaskSetManager$$conf().getTimeAsMs(localityWaitKey, BoxesRunTime.boxToLong(defaultWait).toString());
   }

   private Enumeration.Value[] computeValidLocalityLevels() {
      ArrayBuffer levels = new ArrayBuffer();
      if (!this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor().isEmpty() && this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor().keySet().exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final boolean apply(String x$16) {
            return this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.isExecutorAlive(x$16);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })) {
         levels.$plus$eq(TaskLocality$.MODULE$.PROCESS_LOCAL());
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      if (!this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost().isEmpty() && this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost().keySet().exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final boolean apply(String x$17) {
            return this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.hasExecutorsAliveOnHost(x$17);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })) {
         levels.$plus$eq(TaskLocality$.MODULE$.NODE_LOCAL());
      } else {
         BoxedUnit var2 = BoxedUnit.UNIT;
      }

      if (this.pendingTasksWithNoPrefs().isEmpty()) {
         BoxedUnit var3 = BoxedUnit.UNIT;
      } else {
         levels.$plus$eq(TaskLocality$.MODULE$.NO_PREF());
      }

      if (!this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack().isEmpty() && this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack().keySet().exists(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final boolean apply(String x$18) {
            return this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.hasHostAliveOnRack(x$18);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      })) {
         levels.$plus$eq(TaskLocality$.MODULE$.RACK_LOCAL());
      } else {
         BoxedUnit var4 = BoxedUnit.UNIT;
      }

      levels.$plus$eq(TaskLocality$.MODULE$.ANY());
      this.logDebug(new Serializable(this, levels) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;
         private final ArrayBuffer levels$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Valid locality levels for ").append((Object)this.$outer.taskSet()).append((Object)": ").append((Object)this.levels$1.mkString(", ")).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.levels$1 = levels$1;
            }
         }
      });
      return (Enumeration.Value[])levels.toArray(ClassTag$.MODULE$.apply(Enumeration.Value.class));
   }

   public void recomputeLocality() {
      Enumeration.Value previousLocalityLevel = this.myLocalityLevels()[this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex()];
      this.myLocalityLevels_$eq(this.computeValidLocalityLevels());
      this.localityWaits_$eq((long[])Predef$.MODULE$.refArrayOps(this.myLocalityLevels()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final long apply(Enumeration.Value level) {
            return this.$outer.org$apache$spark$scheduler$TaskSetManager$$getLocalityWait(level);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long())));
      this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex_$eq(this.getLocalityIndex(previousLocalityLevel));
   }

   public void executorAdded() {
      this.recomputeLocality();
   }

   public final boolean org$apache$spark$scheduler$TaskSetManager$$canRunOnHost$1(int index, String execId$3, String host$4) {
      return !this.hasAttemptOnHost(index, host$4) && !this.isTaskBlacklistedOnExecOrNode(index, execId$3, host$4);
   }

   public final boolean org$apache$spark$scheduler$TaskSetManager$$tasksNeedToBeScheduledFrom$1(ArrayBuffer pendingTaskIds) {
      int indexOffset = pendingTaskIds.size();

      while(indexOffset > 0) {
         --indexOffset;
         int index = BoxesRunTime.unboxToInt(pendingTaskIds.apply(indexOffset));
         if (this.copiesRunning()[index] == 0 && !this.successful()[index]) {
            return true;
         }

         pendingTaskIds.remove(indexOffset);
      }

      return false;
   }

   private final boolean moreTasksToRunIn$1(HashMap pendingTasks) {
      ArrayBuffer emptyKeys = new ArrayBuffer();
      boolean hasTasks = pendingTasks.exists(new Serializable(this, emptyKeys) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;
         private final ArrayBuffer emptyKeys$1;

         public final boolean apply(Tuple2<String, ArrayBuffer<Object>> x0$3) {
            if (x0$3 != null) {
               String id = (String)x0$3._1();
               ArrayBuffer tasks = (ArrayBuffer)x0$3._2();
               if (id != null && tasks != null) {
                  boolean var10000;
                  if (this.$outer.org$apache$spark$scheduler$TaskSetManager$$tasksNeedToBeScheduledFrom$1(tasks)) {
                     var10000 = true;
                  } else {
                     this.emptyKeys$1.$plus$eq(id);
                     var10000 = false;
                  }

                  boolean var7 = var10000;
                  return var7;
               }
            }

            throw new MatchError(x0$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.emptyKeys$1 = emptyKeys$1;
            }
         }
      });
      emptyKeys.foreach(new Serializable(this, pendingTasks) {
         public static final long serialVersionUID = 0L;
         private final HashMap pendingTasks$1;

         public final Option<ArrayBuffer<Object>> apply(String id) {
            return this.pendingTasks$1.remove(id);
         }

         public {
            this.pendingTasks$1 = pendingTasks$1;
         }
      });
      return hasTasks;
   }

   public TaskSetManager(TaskSchedulerImpl sched, TaskSet taskSet, int maxTaskFailures, Option<BlacklistTracker> blacklistTracker, Clock clock) {
      this.org$apache$spark$scheduler$TaskSetManager$$sched = sched;
      this.taskSet = taskSet;
      this.maxTaskFailures = maxTaskFailures;
      this.org$apache$spark$scheduler$TaskSetManager$$blacklistTracker = blacklistTracker;
      this.org$apache$spark$scheduler$TaskSetManager$$clock = clock;
      Logging$class.$init$(this);
      this.org$apache$spark$scheduler$TaskSetManager$$conf = sched.sc().conf();
      this.org$apache$spark$scheduler$TaskSetManager$$addedJars = (HashMap)HashMap$.MODULE$.apply(sched.sc().addedJars().toSeq());
      this.org$apache$spark$scheduler$TaskSetManager$$addedFiles = (HashMap)HashMap$.MODULE$.apply(sched.sc().addedFiles().toSeq());
      this.SPECULATION_QUANTILE = this.org$apache$spark$scheduler$TaskSetManager$$conf().getDouble("spark.speculation.quantile", (double)0.75F);
      this.SPECULATION_MULTIPLIER = this.org$apache$spark$scheduler$TaskSetManager$$conf().getDouble("spark.speculation.multiplier", (double)1.5F);
      this.maxResultSize = Utils$.MODULE$.getMaxResultSize(this.org$apache$spark$scheduler$TaskSetManager$$conf());
      this.speculationEnabled = this.org$apache$spark$scheduler$TaskSetManager$$conf().getBoolean("spark.speculation", false);
      this.env = SparkEnv$.MODULE$.get();
      this.ser = this.env().closureSerializer().newInstance();
      this.tasks = taskSet.tasks();
      this.numTasks = this.tasks().length;
      this.copiesRunning = new int[this.numTasks()];
      this.successful = new boolean[this.numTasks()];
      this.numFailures = new int[this.numTasks()];
      this.org$apache$spark$scheduler$TaskSetManager$$killedByOtherAttempt = new boolean[this.numTasks()];
      this.taskAttempts = (List[])Array$.MODULE$.fill(this.numTasks(), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Nil$ apply() {
            return Nil$.MODULE$;
         }
      }, ClassTag$.MODULE$.apply(List.class));
      this.tasksSuccessful = 0;
      this.weight = 1;
      this.minShare = 0;
      this.priority = taskSet.priority();
      this.stageId = taskSet.stageId();
      this.name = (new StringBuilder()).append((Object)"TaskSet_").append((Object)taskSet.id()).toString();
      this.parent = null;
      this.totalResultSize = 0L;
      this.calculatedTasks = 0;
      this.taskSetBlacklistHelperOpt = blacklistTracker.<TaskSetBlacklist>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final TaskSetBlacklist apply(BlacklistTracker x$1) {
            return new TaskSetBlacklist(this.$outer.org$apache$spark$scheduler$TaskSetManager$$sched.sc().listenerBus(), this.$outer.org$apache$spark$scheduler$TaskSetManager$$conf(), this.$outer.stageId(), this.$outer.taskSet().stageAttemptId(), this.$outer.org$apache$spark$scheduler$TaskSetManager$$clock);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.runningTasksSet = new HashSet<Object>();
      this.isZombie = false;
      this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForExecutor = new HashMap<String, ArrayBuffer<Object>>();
      this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForHost = new HashMap<String, ArrayBuffer<Object>>();
      this.org$apache$spark$scheduler$TaskSetManager$$pendingTasksForRack = new HashMap<String, ArrayBuffer<Object>>();
      this.pendingTasksWithNoPrefs = new ArrayBuffer<Object>();
      this.org$apache$spark$scheduler$TaskSetManager$$allPendingTasks = new ArrayBuffer<Object>();
      this.speculatableTasks = new HashSet<Object>();
      this.org$apache$spark$scheduler$TaskSetManager$$taskInfos = new HashMap<Object, TaskInfo>();
      this.successfulTaskDurations = new MedianHeap(Ordering.Double$.MODULE$);
      this.EXCEPTION_PRINT_INTERVAL = this.org$apache$spark$scheduler$TaskSetManager$$conf().getLong("spark.logging.exceptionPrintInterval", 10000L);
      this.recentExceptions = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      this.epoch = sched.mapOutputTracker().getEpoch();
      this.logDebug(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final String apply() {
            return (new StringBuilder()).append((Object)"Epoch for ").append((Object)this.$outer.taskSet()).append((Object)": ").append((Object)BoxesRunTime.boxToLong(this.$outer.epoch())).toString();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.refArrayOps(this.tasks()).foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final void apply(Task<?> t) {
            t.epoch_$eq(this.$outer.epoch());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      RichInt$.MODULE$.until$extension0(Predef$.MODULE$.intWrapper(0), this.numTasks()).reverse().foreach$mVc$sp(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final void apply(int i) {
            this.apply$mcVI$sp(i);
         }

         public void apply$mcVI$sp(int i) {
            this.$outer.addPendingTask(i);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.myLocalityLevels = this.computeValidLocalityLevels();
      this.localityWaits = (long[])Predef$.MODULE$.refArrayOps(this.myLocalityLevels()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskSetManager $outer;

         public final long apply(Enumeration.Value level) {
            return this.$outer.org$apache$spark$scheduler$TaskSetManager$$getLocalityWait(level);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.Long()));
      this.org$apache$spark$scheduler$TaskSetManager$$currentLocalityIndex = 0;
      this.org$apache$spark$scheduler$TaskSetManager$$lastLaunchTime = clock.getTimeMillis();
      this.emittedTaskSizeWarning = false;
   }
}
