package org.apache.spark.deploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.UserGroupInformation.AuthenticationMethod;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.primitives.Longs;
import scala.Array$;
import scala.Function0;
import scala.Function0$class;
import scala.Function0$mcJ$sp;
import scala.Function0$mcJ$sp$class;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple2$mcJJ$sp;
import scala.collection.Iterable;
import scala.collection.JavaConverters$;
import scala.collection.LinearSeqOptimized;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Iterable$;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.util.control.NonFatal$;
import scala.util.matching.Regex;
import scala.util.matching.UnanchoredRegex;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\r}a\u0001B\u0001\u0003\u0001-\u0011qb\u00159be.D\u0015\rZ8paV#\u0018\u000e\u001c\u0006\u0003\u0007\u0011\ta\u0001Z3qY>L(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019b#D\u0001\u0015\u0015\t)B!\u0001\u0005j]R,'O\\1m\u0013\t9BCA\u0004M_\u001e<\u0017N\\4\t\u000be\u0001A\u0011\u0001\u000e\u0002\rqJg.\u001b;?)\u0005Y\u0002C\u0001\u000f\u0001\u001b\u0005\u0011\u0001b\u0002\u0010\u0001\u0005\u0004%IaH\u0001\ngB\f'o[\"p]\u001a,\u0012\u0001\t\t\u0003C\tj\u0011\u0001B\u0005\u0003G\u0011\u0011\u0011b\u00159be.\u001cuN\u001c4\t\r\u0015\u0002\u0001\u0015!\u0003!\u0003)\u0019\b/\u0019:l\u0007>tg\r\t\u0005\bO\u0001\u0011\r\u0011\"\u0001)\u0003\u0011\u0019wN\u001c4\u0016\u0003%\u0002\"A\u000b\u0018\u000e\u0003-R!a\n\u0017\u000b\u000552\u0011A\u00025bI>|\u0007/\u0003\u00020W\ti1i\u001c8gS\u001e,(/\u0019;j_:Da!\r\u0001!\u0002\u0013I\u0013!B2p]\u001a\u0004\u0003\"B\u001a\u0001\t\u0003!\u0014A\u0004:v]\u0006\u001b8\u000b]1sWV\u001bXM\u001d\u000b\u0003ka\u0002\"!\u0004\u001c\n\u0005]r!\u0001B+oSRDQ!\u000f\u001aA\u0002i\nAAZ;oGB\u0019QbO\u001b\n\u0005qr!!\u0003$v]\u000e$\u0018n\u001c81\u0011\u0015q\u0004\u0001\"\u0001@\u0003=\u0019'/Z1uKN\u0003\u0018M]6Vg\u0016\u0014H#\u0001!\u0011\u0005\u0005#U\"\u0001\"\u000b\u0005\rc\u0013\u0001C:fGV\u0014\u0018\u000e^=\n\u0005\u0015\u0013%\u0001F+tKJ<%o\\;q\u0013:4wN]7bi&|g\u000eC\u0003H\u0001\u0011\u0005\u0001*A\nue\u0006t7OZ3s\u0007J,G-\u001a8uS\u0006d7\u000fF\u00026\u0013.CQA\u0013$A\u0002\u0001\u000baa]8ve\u000e,\u0007\"\u0002'G\u0001\u0004\u0001\u0015\u0001\u00023fgRDQA\u0014\u0001\u0005\u0002=\u000bA%\u00199qK:$7kM!oIN\u0003\u0018M]6IC\u0012|w\u000e]\"p]\u001aLw-\u001e:bi&|gn\u001d\u000b\u0004kA\u000b\u0006\"B\u0014N\u0001\u0004\u0001\u0003\"\u0002*N\u0001\u0004I\u0013A\u00035bI>|\u0007oQ8oM\")A\u000b\u0001C\u0001+\u0006A\u0012\r\u001d9f]\u0012\u001c\u0006/\u0019:l\u0011\u0006$wn\u001c9D_:4\u0017nZ:\u0015\u0007U2v\u000bC\u0003('\u0002\u0007\u0001\u0005C\u0003S'\u0002\u0007\u0011\u0006C\u0003U\u0001\u0011\u0005\u0011\fF\u000265.DQa\u0017-A\u0002q\u000baa\u001d:d\u001b\u0006\u0004\b\u0003B/cI\u0012l\u0011A\u0018\u0006\u0003?\u0002\f\u0011\"[7nkR\f'\r\\3\u000b\u0005\u0005t\u0011AC2pY2,7\r^5p]&\u00111M\u0018\u0002\u0004\u001b\u0006\u0004\bCA3i\u001d\tia-\u0003\u0002h\u001d\u00051\u0001K]3eK\u001aL!!\u001b6\u0003\rM#(/\u001b8h\u0015\t9g\u0002C\u0003m1\u0002\u0007Q.A\u0004eKN$X*\u00199\u0011\t9\fH\rZ\u0007\u0002_*\u0011\u0001\u000fY\u0001\b[V$\u0018M\u00197f\u0013\t\u0011xNA\u0004ICNDW*\u00199\t\u000bQ\u0004A\u0011A;\u0002!9,woQ8oM&<WO]1uS>tGCA\u0015w\u0011\u001593\u000f1\u0001!\u0011\u0015A\b\u0001\"\u0001z\u00039\tG\rZ\"sK\u0012,g\u000e^5bYN$\"!\u000e>\t\u000b\u001d:\b\u0019A>\u0011\u0005q|X\"A?\u000b\u0005yd\u0013AB7baJ,G-C\u0002\u0002\u0002u\u0014qAS8c\u0007>tg\rC\u0004\u0002\u0006\u0001!\t!a\u0002\u00023\u0005$GmQ;se\u0016tG/V:fe\u000e\u0013X\rZ3oi&\fGn\u001d\u000b\u0004k\u0005%\u0001\u0002CA\u0006\u0003\u0007\u0001\r!!\u0004\u0002\u000b\r\u0014X\rZ:\u0011\u0007\u0005\u000by!C\u0002\u0002\u0012\t\u00131b\u0011:fI\u0016tG/[1mg\"9\u0011Q\u0003\u0001\u0005\u0002\u0005]\u0011a\u00057pO&tWk]3s\rJ|WnS3zi\u0006\u0014G#B\u001b\u0002\u001a\u0005u\u0001bBA\u000e\u0003'\u0001\r\u0001Z\u0001\u000eaJLgnY5qC2t\u0015-\\3\t\u000f\u0005}\u00111\u0003a\u0001I\u0006q1.Z=uC\n4\u0015\u000e\\3oC6,\u0007\u0002CA\u0012\u0001\u0011\u0005A!!\n\u0002'\u0005$G\rR3mK\u001e\fG/[8o)>\\WM\\:\u0015\u000bU\n9#a\u000e\t\u0011\u0005%\u0012\u0011\u0005a\u0001\u0003W\ta\u0001^8lK:\u001c\b#B\u0007\u0002.\u0005E\u0012bAA\u0018\u001d\t)\u0011I\u001d:bsB\u0019Q\"a\r\n\u0007\u0005UbB\u0001\u0003CsR,\u0007B\u0002\u0010\u0002\"\u0001\u0007\u0001\u0005\u0003\u0005\u0002<\u0001!\t\u0001BA\u001f\u0003y9W\r\u001e$T\u0005f$Xm\u001d*fC\u0012|e\u000e\u00165sK\u0006$7)\u00197mE\u0006\u001c7\u000e\u0006\u0002\u0002@A!QbOA!!\ri\u00111I\u0005\u0004\u0003\u000br!\u0001\u0002'p]\u001eD\u0001\"!\u0013\u0001\t\u0003!\u0011QH\u0001\"O\u0016$hi\u0015\"zi\u0016\u001cxK]5ui\u0016twJ\u001c+ie\u0016\fGmQ1mY\n\f7m\u001b\u0005\b\u0003\u001b\u0002A\u0011AA(\u0003Aa\u0017n\u001d;MK\u000647\u000b^1ukN,7\u000f\u0006\u0004\u0002R\u0005U\u0014Q\u0010\t\u0007\u0003'\n\u0019'!\u001b\u000f\t\u0005U\u0013q\f\b\u0005\u0003/\ni&\u0004\u0002\u0002Z)\u0019\u00111\f\u0006\u0002\rq\u0012xn\u001c;?\u0013\u0005y\u0011bAA1\u001d\u00059\u0001/Y2lC\u001e,\u0017\u0002BA3\u0003O\u00121aU3r\u0015\r\t\tG\u0004\t\u0005\u0003W\n\t(\u0004\u0002\u0002n)\u0019\u0011q\u000e\u0017\u0002\u0005\u0019\u001c\u0018\u0002BA:\u0003[\u0012!BR5mKN#\u0018\r^;t\u0011!\ty'a\u0013A\u0002\u0005]\u0004\u0003BA6\u0003sJA!a\u001f\u0002n\tQa)\u001b7f'f\u001cH/Z7\t\u0011\u0005}\u00141\na\u0001\u0003\u0003\u000b\u0001BY1tKB\u000bG\u000f\u001b\t\u0005\u0003W\n\u0019)\u0003\u0003\u0002\u0006\u00065$\u0001\u0002)bi\"Dq!!\u0014\u0001\t\u0003\tI\t\u0006\u0004\u0002R\u0005-\u0015Q\u0012\u0005\t\u0003_\n9\t1\u0001\u0002x!A\u0011qRAD\u0001\u0004\tI'\u0001\u0006cCN,7\u000b^1ukNDq!a%\u0001\t\u0003\t)*A\nmSN$H*Z1g\t&\u00148\u000b^1ukN,7\u000f\u0006\u0004\u0002R\u0005]\u0015\u0011\u0014\u0005\t\u0003_\n\t\n1\u0001\u0002x!A\u0011qPAI\u0001\u0004\t\t\tC\u0004\u0002\u0014\u0002!\t!!(\u0015\r\u0005E\u0013qTAQ\u0011!\ty'a'A\u0002\u0005]\u0004\u0002CAH\u00037\u0003\r!!\u001b\t\u000f\u0005\u0015\u0006\u0001\"\u0001\u0002(\u0006Q\u0011n]$m_\n\u0004\u0016\r\u001e5\u0015\t\u0005%\u0016q\u0016\t\u0004\u001b\u0005-\u0016bAAW\u001d\t9!i\\8mK\u0006t\u0007\u0002CAY\u0003G\u0003\r!!!\u0002\u000fA\fG\u000f^3s]\"9\u0011Q\u0017\u0001\u0005\u0002\u0005]\u0016\u0001C4m_\n\u0004\u0016\r\u001e5\u0015\t\u0005e\u00161\u0018\t\u0007\u0003'\n\u0019'!!\t\u0011\u0005E\u00161\u0017a\u0001\u0003\u0003Cq!!.\u0001\t\u0003\ty\f\u0006\u0004\u0002:\u0006\u0005\u00171\u0019\u0005\t\u0003_\ni\f1\u0001\u0002x!A\u0011\u0011WA_\u0001\u0004\t\t\tC\u0004\u0002H\u0002!\t!!3\u0002'\u001ddwN\u0019)bi\"LeMT3dKN\u001c\u0018M]=\u0015\t\u0005e\u00161\u001a\u0005\t\u0003c\u000b)\r1\u0001\u0002\u0002\"9\u0011q\u0019\u0001\u0005\u0002\u0005=GCBA]\u0003#\f\u0019\u000e\u0003\u0005\u0002p\u00055\u0007\u0019AA<\u0011!\t\t,!4A\u0002\u0005\u0005\u0005bBAl\u0001\u0011\u0005\u0011\u0011\\\u0001\u0010Y&\u001cHOR5mKN\u001cvN\u001d;fIRQ\u00111\\Ao\u0003C\f)/!;\u0011\u000b5\ti#!\u001b\t\u0011\u0005}\u0017Q\u001ba\u0001\u0003o\n\u0001B]3n_R,gi\u001d\u0005\t\u0003G\f)\u000e1\u0001\u0002\u0002\u0006\u0019A-\u001b:\t\u000f\u0005\u001d\u0018Q\u001ba\u0001I\u00061\u0001O]3gSbDq!a;\u0002V\u0002\u0007A-A\bfq\u000edWo]5p]N+hMZ5y\u0011!\ty\u000f\u0001C\u0001\t\u0005E\u0018aG4fiN+hMZ5y\r>\u00148I]3eK:$\u0018.\u00197t!\u0006$\b\u000e\u0006\u0003\u0002t\u0006e\bcA\u0007\u0002v&\u0019\u0011q\u001f\b\u0003\u0007%sG\u000f\u0003\u0005\u0002|\u00065\b\u0019AAA\u0003=\u0019'/\u001a3f]RL\u0017\r\\:QCRD\u0007\"CA\u0000\u0001\t\u0007I\u0011\u0002B\u0001\u0003MA\u0015\tR(P!~\u001buJ\u0014$`!\u0006#F+\u0012*O+\t\u0011\u0019\u0001\u0005\u0003\u0003\u0006\t=QB\u0001B\u0004\u0015\u0011\u0011IAa\u0003\u0002\u00115\fGo\u00195j]\u001eT1A!\u0004\u000f\u0003\u0011)H/\u001b7\n\t\tE!q\u0001\u0002\u0010+:\fgn\u00195pe\u0016$'+Z4fq\"A!Q\u0003\u0001!\u0002\u0013\u0011\u0019!\u0001\u000bI\u0003\u0012{u\nU0D\u001f:3u\fU!U)\u0016\u0013f\n\t\u0005\b\u00053\u0001A\u0011\u0001B\u000e\u0003e\u0019XOY:uSR,H/\u001a%bI>|\u0007OV1sS\u0006\u0014G.Z:\u0015\u000b\u0011\u0014iB!\t\t\u000f\t}!q\u0003a\u0001I\u0006!A/\u001a=u\u0011\u0019\u0011&q\u0003a\u0001S!A!Q\u0005\u0001\u0005\u0002\u0011\u00119#A\fhKR\u001cuN\u001c4CsB\f7o]5oO\u001a\u001b6)Y2iKR)\u0011F!\u000b\u0003,!1!Ka\tA\u0002%BqA!\f\u0003$\u0001\u0007A-\u0001\u0004tG\",W.\u001a\u0005\t\u0005c\u0001A\u0011\u0001\u0003\u00034\u0005QA-^7q)>\\WM\\:\u0015\t\tU\"1\b\t\u0006\u0003'\u00129\u0004Z\u0005\u0005\u0005s\t9G\u0001\u0005Ji\u0016\u0014\u0018M\u00197f\u0011!\u0011iDa\fA\u0002\u00055\u0011aC2sK\u0012,g\u000e^5bYND\u0001B!\u0011\u0001\t\u0003!!1I\u0001\u000ei>\\WM\u001c+p'R\u0014\u0018N\\4\u0015\u0007\u0011\u0014)\u0005\u0003\u0005\u0003H\t}\u0002\u0019\u0001B%\u0003\u0015!xn[3oa\u0011\u0011YE!\u0017\u0011\r\t5#\u0011\u000bB+\u001b\t\u0011yEC\u0002\u0003H\tKAAa\u0015\u0003P\t)Ak\\6f]B!!q\u000bB-\u0019\u0001!ABa\u0017\u0003F\u0005\u0005\t\u0011!B\u0001\u0005;\u00121a\u0018\u00132#\u0011\u0011yF!\u001a\u0011\u00075\u0011\t'C\u0002\u0003d9\u0011qAT8uQ&tw\r\u0005\u0003\u0003N\t\u001d\u0014\u0002\u0002B5\u0005\u001f\u0012q\u0002V8lK:LE-\u001a8uS\u001aLWM\u001d\u0005\t\u0005[\u0002A\u0011\u0001\u0003\u0003p\u0005)2\r[3dW\u0006\u001b7-Z:t!\u0016\u0014X.[:tS>tGCBAU\u0005c\u0012)\b\u0003\u0005\u0003t\t-\u0004\u0019AA5\u0003\u0019\u0019H/\u0019;vg\"A!q\u000fB6\u0001\u0004\u0011I(\u0001\u0003n_\u0012,\u0007\u0003\u0002B>\u0005\u0003k!A! \u000b\t\t}\u0014QN\u0001\u000ba\u0016\u0014X.[:tS>t\u0017\u0002\u0002BB\u0005{\u0012\u0001BR:BGRLwN\u001c\u0005\b\u0005\u000f\u0003A\u0011\u0001BE\u0003%\u0019XM]5bY&TX\r\u0006\u0003\u0002,\t-\u0005\u0002CA\u0006\u0005\u000b\u0003\r!!\u0004\t\u000f\t=\u0005\u0001\"\u0001\u0003\u0012\u0006YA-Z:fe&\fG.\u001b>f)\u0011\tiAa%\t\u0011\tU%Q\u0012a\u0001\u0003W\t!\u0002^8lK:\u0014\u0015\u0010^3t\u0011\u001d\u0011I\n\u0001C\u0001\u00057\u000b1\"[:Qe>D\u00180V:feR!\u0011\u0011\u0016BO\u0011\u001d\u0011yJa&A\u0002\u0001\u000b1!^4jQ\r\u0001!1\u0015\t\u0005\u0005K\u0013Y+\u0004\u0002\u0003(*\u0019!\u0011\u0016\u0003\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0003.\n\u001d&\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLwa\u0002BY\u0005!\u0005!1W\u0001\u0010'B\f'o\u001b%bI>|\u0007/\u0016;jYB\u0019AD!.\u0007\r\u0005\u0011\u0001\u0012\u0001B\\'\r\u0011)\f\u0004\u0005\b3\tUF\u0011\u0001B^)\t\u0011\u0019\fC\u0006\u0003@\nU\u0006R1A\u0005\n\t\u0005\u0017\u0001C5ogR\fgnY3\u0016\u0003mA!B!2\u00036\"\u0005\t\u0015)\u0003\u001c\u0003%Ign\u001d;b]\u000e,\u0007\u0005\u0003\u0006\u0003J\nU&\u0019!C\u0001\u0005\u0017\fqd\u0015)B%.{\u0016,\u0011*O?\u000e\u0013V\tR*`)\u0016k\u0005kX#Y)\u0016s5+S(O+\t\u0011i\r\u0005\u0003\u0003P\neWB\u0001Bi\u0015\u0011\u0011\u0019N!6\u0002\t1\fgn\u001a\u0006\u0003\u0005/\fAA[1wC&\u0019\u0011N!5\t\u0013\tu'Q\u0017Q\u0001\n\t5\u0017\u0001I*Q\u0003J[u,W!S\u001d~\u001b%+\u0012#T?R+U\nU0F1R+ejU%P\u001d\u0002B!B!9\u00036\n\u0007I\u0011\u0001Bf\u0003y\u0019\u0006+\u0011*L?f\u000b%KT0D%\u0016#5kX\"P+:#VIU0E\u000b2KU\nC\u0005\u0003f\nU\u0006\u0015!\u0003\u0003N\u0006y2\u000bU!S\u0017~K\u0016I\u0015(`\u0007J+EiU0D\u001fVsE+\u0012*`\t\u0016c\u0015*\u0014\u0011\t\u0017\t%(Q\u0017b\u0001\n\u0003!!1^\u0001&+B#\u0015\tV#`\u0013:\u0003V\u000bV0N\u000bR\u0013\u0016jQ*`\u0013:#VI\u0015,B\u0019~\u0013ViQ(S\tN+\"!a=\t\u0013\t=(Q\u0017Q\u0001\n\u0005M\u0018AJ+Q\t\u0006#ViX%O!V#v,T#U%&\u001b5kX%O)\u0016\u0013f+\u0011'`%\u0016\u001buJ\u0015#TA!A!1\u001fB[\t\u0003\u0011\t-A\u0002hKRD\u0011Ba>\u00036\u0012\u0005AA!?\u0002'\u001d,G\u000fR1uK>3g*\u001a=u+B$\u0017\r^3\u0015\r\u0005\u0005#1 B\u0000\u0011!\u0011iP!>A\u0002\u0005\u0005\u0013AD3ya&\u0014\u0018\r^5p]\u0012\u000bG/\u001a\u0005\t\u0007\u0003\u0011)\u00101\u0001\u0004\u0004\u0005AaM]1di&|g\u000eE\u0002\u000e\u0007\u000bI1aa\u0002\u000f\u0005\u0019!u.\u001e2mK\"AAO!.\u0005\u0002\u0011\u0019Y\u0001F\u0002*\u0007\u001bAaaJB\u0005\u0001\u0004\u0001\u0003b\u0002(\u00036\u0012%1\u0011\u0003\u000b\u0006k\rM1Q\u0003\u0005\u0007O\r=\u0001\u0019\u0001\u0011\t\rI\u001by\u00011\u0001*\u0011\u001d!&Q\u0017C\u0005\u00073!R!NB\u000e\u0007;AaaJB\f\u0001\u0004\u0001\u0003B\u0002*\u0004\u0018\u0001\u0007\u0011\u0006"
)
public class SparkHadoopUtil implements Logging {
   private final SparkConf sparkConf;
   private final Configuration conf;
   private final UnanchoredRegex org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static SparkHadoopUtil get() {
      return SparkHadoopUtil$.MODULE$.get();
   }

   public static String SPARK_YARN_CREDS_COUNTER_DELIM() {
      return SparkHadoopUtil$.MODULE$.SPARK_YARN_CREDS_COUNTER_DELIM();
   }

   public static String SPARK_YARN_CREDS_TEMP_EXTENSION() {
      return SparkHadoopUtil$.MODULE$.SPARK_YARN_CREDS_TEMP_EXTENSION();
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

   private SparkConf sparkConf() {
      return this.sparkConf;
   }

   public Configuration conf() {
      return this.conf;
   }

   public void runAsSparkUser(Function0<BoxedUnit> func) {
      this.createSparkUser().doAs(new PrivilegedExceptionAction<BoxedUnit>(this, func) {
         private final Function0 func$1;

         public void run() {
            this.func$1.apply$mcV$sp();
         }

         public {
            this.func$1 = func$1;
         }
      });
   }

   public UserGroupInformation createSparkUser() {
      String user = Utils$.MODULE$.getCurrentUserName();
      this.logDebug(new Serializable(this, user) {
         public static final long serialVersionUID = 0L;
         private final String user$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)"creating UGI for user: ").append((Object)this.user$1).toString();
         }

         public {
            this.user$1 = user$1;
         }
      });
      UserGroupInformation ugi = UserGroupInformation.createRemoteUser(user);
      this.transferCredentials(UserGroupInformation.getCurrentUser(), ugi);
      return ugi;
   }

   public void transferCredentials(UserGroupInformation source, UserGroupInformation dest) {
      dest.addCredentials(source.getCredentials());
   }

   public void appendS3AndSparkHadoopConfigurations(SparkConf conf, Configuration hadoopConf) {
      SparkHadoopUtil$.MODULE$.org$apache$spark$deploy$SparkHadoopUtil$$appendS3AndSparkHadoopConfigurations(conf, hadoopConf);
   }

   public void appendSparkHadoopConfigs(SparkConf conf, Configuration hadoopConf) {
      SparkHadoopUtil$.MODULE$.org$apache$spark$deploy$SparkHadoopUtil$$appendSparkHadoopConfigs(conf, hadoopConf);
   }

   public void appendSparkHadoopConfigs(Map<String, String> srcMap, HashMap<String, String> destMap) {
      srcMap.withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> check$ifrefutable$1) {
            boolean var3;
            if (check$ifrefutable$1 != null) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }
      }).withFilter(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(Tuple2<String, String> x$1) {
            if (x$1 != null) {
               String key = (String)x$1._1();
               boolean var4 = key.startsWith("spark.hadoop.");
               return var4;
            } else {
               throw new MatchError(x$1);
            }
         }
      }).foreach(new Serializable(this, destMap) {
         public static final long serialVersionUID = 0L;
         private final HashMap destMap$1;

         public final Option<String> apply(Tuple2<String, String> x$2) {
            if (x$2 != null) {
               String key = (String)x$2._1();
               String value = (String)x$2._2();
               Option var5 = this.destMap$1.put(key.substring("spark.hadoop.".length()), value);
               return var5;
            } else {
               throw new MatchError(x$2);
            }
         }

         public {
            this.destMap$1 = destMap$1;
         }
      });
   }

   public Configuration newConfiguration(SparkConf conf) {
      return SparkHadoopUtil$.MODULE$.newConfiguration(conf);
   }

   public void addCredentials(JobConf conf) {
      Credentials jobCreds = conf.getCredentials();
      jobCreds.mergeAll(UserGroupInformation.getCurrentUser().getCredentials());
   }

   public void addCurrentUserCredentials(Credentials creds) {
      UserGroupInformation.getCurrentUser().addCredentials(creds);
   }

   public void loginUserFromKeytab(String principalName, String keytabFilename) {
      if ((new File(keytabFilename)).exists()) {
         this.logInfo(new Serializable(this, principalName, keytabFilename) {
            public static final long serialVersionUID = 0L;
            private final String principalName$1;
            private final String keytabFilename$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Attempting to login to Kerberos ").append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"using principal: ", " and keytab: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.principalName$1, this.keytabFilename$1}))).toString();
            }

            public {
               this.principalName$1 = principalName$1;
               this.keytabFilename$1 = keytabFilename$1;
            }
         });
         UserGroupInformation.loginUserFromKeytab(principalName, keytabFilename);
      } else {
         throw new SparkException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Keytab file: ", " does not exist"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{keytabFilename})));
      }
   }

   public void addDelegationTokens(byte[] tokens, SparkConf sparkConf) {
      UserGroupInformation.setConfiguration(this.newConfiguration(sparkConf));
      Credentials creds = this.deserialize(tokens);
      this.logInfo(new Serializable(this, creds) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkHadoopUtil $outer;
         private final Credentials creds$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Adding/updating delegation tokens ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.dumpTokens(this.creds$1)}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.creds$1 = creds$1;
            }
         }
      });
      this.addCurrentUserCredentials(creds);
   }

   public Function0<Object> getFSBytesReadOnThreadCallback() {
      Function0 f = new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BoxesRunTime.unboxToLong(((TraversableOnce)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(FileSystem.getAllStatistics()).asScala()).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(FileSystem.Statistics x$3) {
                  return x$3.getThreadStatistics().getBytesRead();
               }
            }, Buffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
         }
      };
      Tuple2 baseline = new Tuple2$mcJJ$sp(Thread.currentThread().getId(), f.apply$mcJ$sp());
      return new Function0$mcJ$sp(this, f, baseline) {
         private final HashMap<Object, Object> org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap;
         private final Function0 f$1;
         public final Tuple2 baseline$1;

         public boolean apply$mcZ$sp() {
            return Function0$class.apply$mcZ$sp(this);
         }

         public byte apply$mcB$sp() {
            return Function0$class.apply$mcB$sp(this);
         }

         public char apply$mcC$sp() {
            return Function0$class.apply$mcC$sp(this);
         }

         public double apply$mcD$sp() {
            return Function0$class.apply$mcD$sp(this);
         }

         public float apply$mcF$sp() {
            return Function0$class.apply$mcF$sp(this);
         }

         public int apply$mcI$sp() {
            return Function0$class.apply$mcI$sp(this);
         }

         public short apply$mcS$sp() {
            return Function0$class.apply$mcS$sp(this);
         }

         public void apply$mcV$sp() {
            Function0$class.apply$mcV$sp(this);
         }

         public String toString() {
            return Function0$class.toString(this);
         }

         public HashMap<Object, Object> org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap() {
            return this.org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap;
         }

         public long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            Object var2;
            synchronized(this.org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap()) {
               this.org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap().put(BoxesRunTime.boxToLong(Thread.currentThread().getId()), BoxesRunTime.boxToLong(this.f$1.apply$mcJ$sp()));
               var2 = ((TraversableOnce)this.org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap().map(new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;

                  public final long apply(Tuple2<Object, Object> x0$1) {
                     if (x0$1 != null) {
                        long k = x0$1._1$mcJ$sp();
                        long v = x0$1._2$mcJ$sp();
                        long var7 = v - (k == this.$outer.baseline$1._1$mcJ$sp() ? this.$outer.baseline$1._2$mcJ$sp() : 0L);
                        return var7;
                     } else {
                        throw new MatchError(x0$1);
                     }
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$);
            }

            return BoxesRunTime.unboxToLong(var2);
         }

         public {
            this.f$1 = f$1;
            this.baseline$1 = baseline$1;
            Function0$class.$init$(this);
            Function0$mcJ$sp$class.$init$(this);
            this.org$apache$spark$deploy$SparkHadoopUtil$$anon$$bytesReadMap = new HashMap<Object, Object>();
         }
      };
   }

   public Function0<Object> getFSBytesWrittenOnThreadCallback() {
      Buffer threadStats = (Buffer)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(FileSystem.getAllStatistics()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final FileSystem.Statistics.StatisticsData apply(FileSystem.Statistics x$4) {
            return x$4.getThreadStatistics();
         }
      }, Buffer$.MODULE$.canBuildFrom());
      Function0 f = new Serializable(this, threadStats) {
         public static final long serialVersionUID = 0L;
         private final Buffer threadStats$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BoxesRunTime.unboxToLong(((TraversableOnce)this.threadStats$1.map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(FileSystem.Statistics.StatisticsData x$5) {
                  return x$5.getBytesWritten();
               }
            }, Buffer$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
         }

         public {
            this.threadStats$1 = threadStats$1;
         }
      };
      long baselineBytesWritten = f.apply$mcJ$sp();
      return new Serializable(this, f, baselineBytesWritten) {
         public static final long serialVersionUID = 0L;
         private final Function0 f$2;
         private final long baselineBytesWritten$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return this.f$2.apply$mcJ$sp() - this.baselineBytesWritten$1;
         }

         public {
            this.f$2 = f$2;
            this.baselineBytesWritten$1 = baselineBytesWritten$1;
         }
      };
   }

   public Seq<FileStatus> listLeafStatuses(FileSystem fs, Path basePath) {
      return this.listLeafStatuses(fs, fs.getFileStatus(basePath));
   }

   public Seq<FileStatus> listLeafStatuses(FileSystem fs, FileStatus baseStatus) {
      return baseStatus.isDirectory() ? this.recurse$1(baseStatus, fs) : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new FileStatus[]{baseStatus})));
   }

   public Seq<FileStatus> listLeafDirStatuses(FileSystem fs, Path basePath) {
      return this.listLeafDirStatuses(fs, fs.getFileStatus(basePath));
   }

   public Seq<FileStatus> listLeafDirStatuses(FileSystem fs, FileStatus baseStatus) {
      Predef$.MODULE$.assert(baseStatus.isDirectory());
      return this.recurse$2(baseStatus, fs);
   }

   public boolean isGlobPath(Path pattern) {
      StringOps var10000 = new StringOps(Predef$.MODULE$.augmentString(pattern.toString()));
      Set var2 = (new StringOps(Predef$.MODULE$.augmentString("{}[]*?\\"))).toSet();
      return var10000.exists(new Serializable(this, var2) {
         public static final long serialVersionUID = 0L;
         private final Set eta$0$1$1;

         public final boolean apply(Object elem) {
            return this.eta$0$1$1.contains(elem);
         }

         public {
            this.eta$0$1$1 = eta$0$1$1;
         }
      });
   }

   public Seq<Path> globPath(Path pattern) {
      FileSystem fs = pattern.getFileSystem(this.conf());
      return this.globPath(fs, pattern);
   }

   public Seq<Path> globPath(FileSystem fs, Path pattern) {
      return (Seq)Option$.MODULE$.<Path>apply(fs.globStatus(pattern)).<Path>map(new Serializable(this, fs) {
         public static final long serialVersionUID = 0L;
         public final FileSystem fs$3;

         public final Seq<Path> apply(FileStatus[] statuses) {
            return Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])statuses).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;

               public final Path apply(FileStatus x$10) {
                  return x$10.getPath().makeQualified(this.$outer.fs$3.getUri(), this.$outer.fs$3.getWorkingDirectory());
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(Path.class)))).toSeq();
         }

         public {
            this.fs$3 = fs$3;
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Seq<Path> apply() {
            return (Seq)Seq$.MODULE$.empty();
         }
      });
   }

   public Seq<Path> globPathIfNecessary(Path pattern) {
      return this.isGlobPath(pattern) ? this.globPath(pattern) : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new Path[]{pattern})));
   }

   public Seq<Path> globPathIfNecessary(FileSystem fs, Path pattern) {
      return this.isGlobPath(pattern) ? this.globPath(fs, pattern) : (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new Path[]{pattern})));
   }

   public FileStatus[] listFilesSorted(FileSystem remoteFs, Path dir, String prefix, String exclusionSuffix) {
      FileStatus[] var10000;
      try {
         FileStatus[] fileStatuses = remoteFs.listStatus(dir, new PathFilter(this, prefix, exclusionSuffix) {
            private final String prefix$1;
            private final String exclusionSuffix$1;

            public boolean accept(Path path) {
               String name = path.getName();
               return name.startsWith(this.prefix$1) && !name.endsWith(this.exclusionSuffix$1);
            }

            public {
               this.prefix$1 = prefix$1;
               this.exclusionSuffix$1 = exclusionSuffix$1;
            }
         });
         Arrays.sort((Object[])fileStatuses, new Comparator<FileStatus>(this) {
            public int compare(FileStatus o1, FileStatus o2) {
               return Longs.compare(o1.getModificationTime(), o2.getModificationTime());
            }
         });
         var10000 = fileStatuses;
      } catch (Throwable var12) {
         Option var7 = NonFatal$.MODULE$.unapply(var12);
         if (var7.isEmpty()) {
            throw var12;
         }

         Throwable e = (Throwable)var7.get();
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Error while attempting to list files from application staging dir";
            }
         }, e);
         FileStatus[] var9 = (FileStatus[])Array$.MODULE$.empty(ClassTag$.MODULE$.apply(FileStatus.class));
         var10000 = var9;
         return var10000;
      }

      return var10000;
   }

   public int getSuffixForCredentialsPath(Path credentialsPath) {
      String fileName = credentialsPath.getName();
      return (new StringOps(Predef$.MODULE$.augmentString(fileName.substring(fileName.lastIndexOf(SparkHadoopUtil$.MODULE$.SPARK_YARN_CREDS_COUNTER_DELIM()) + 1)))).toInt();
   }

   public UnanchoredRegex org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN() {
      return this.org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN;
   }

   public String substituteHadoopVariables(String text, Configuration hadoopConf) {
      Option var4 = ((Regex)this.org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN()).unapplySeq((CharSequence)text);
      String var6;
      if (!var4.isEmpty() && var4.get() != null && ((LinearSeqOptimized)var4.get()).lengthCompare(1) == 0) {
         String matched = (String)((LinearSeqOptimized)var4.get()).apply(0);
         this.logDebug(new Serializable(this, text) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkHadoopUtil $outer;
            private final String text$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)this.text$1).append((Object)" matched ").append((Object)this.$outer.org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN()).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.text$1 = text$1;
               }
            }
         });
         String key = matched.substring(13, matched.length() - 1);
         Option eval = Option$.MODULE$.apply(hadoopConf.get(key)).map(new Serializable(this, text, matched) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkHadoopUtil $outer;
            private final String text$1;
            public final String matched$1;

            public final String apply(String value) {
               this.$outer.logDebug(new Serializable(this, value) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String value$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)"Substituted ").append((Object)this.$outer.matched$1).append((Object)" with ").append((Object)this.value$1).toString();
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.value$1 = value$1;
                     }
                  }
               });
               return this.text$1.replace(this.matched$1, value);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.text$1 = text$1;
                  this.matched$1 = matched$1;
               }
            }
         });
         var6 = eval.isEmpty() ? text : this.substituteHadoopVariables((String)eval.get(), hadoopConf);
      } else {
         this.logDebug(new Serializable(this, text) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkHadoopUtil $outer;
            private final String text$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)this.text$1).append((Object)" didn't match ").append((Object)this.$outer.org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN()).toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.text$1 = text$1;
               }
            }
         });
         var6 = text;
      }

      return var6;
   }

   public Configuration getConfBypassingFSCache(Configuration hadoopConf, String scheme) {
      Configuration newConf = new Configuration(hadoopConf);
      String confKey = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"fs.", ".impl.disable.cache"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{scheme}));
      newConf.setBoolean(confKey, true);
      return newConf;
   }

   public Iterable<String> dumpTokens(Credentials credentials) {
      return credentials == null ? (Iterable)Seq$.MODULE$.empty() : (Iterable)((TraversableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(credentials.getAllTokens()).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkHadoopUtil $outer;

         public final String apply(Token<? extends TokenIdentifier> token) {
            return this.$outer.tokenToString(token);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, scala.collection.Iterable$.MODULE$.canBuildFrom());
   }

   public String tokenToString(Token<? extends TokenIdentifier> token) {
      DateFormat df = DateFormat.getDateTimeInstance(3, 3, Locale.US);
      StringBuilder buffer = new StringBuilder(128);
      buffer.append(token.toString());

      try {
         TokenIdentifier ti = token.decodeIdentifier();
         buffer.append("; ").append((Object)ti);
         if (ti instanceof AbstractDelegationTokenIdentifier) {
            AbstractDelegationTokenIdentifier var7 = (AbstractDelegationTokenIdentifier)ti;
            buffer.append("; Renewer: ").append((Object)var7.getRenewer());
            buffer.append("; Issued: ").append(df.format(new Date(var7.getIssueDate())));
            StringBuilder var8 = buffer.append("; Max Date: ").append(df.format(new Date(var7.getMaxDate())));
         } else {
            BoxedUnit var10 = BoxedUnit.UNIT;
         }
      } catch (IOException var9) {
         this.logDebug(new Serializable(this, token, var9) {
            public static final long serialVersionUID = 0L;
            private final Token token$1;
            private final IOException e$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to decode ", ": ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.token$1, this.e$1}));
            }

            public {
               this.token$1 = token$1;
               this.e$1 = e$1;
            }
         }, var9);
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return buffer.toString();
   }

   public boolean checkAccessPermission(FileStatus status, FsAction mode) {
      FsPermission perm;
      UserGroupInformation ugi;
      label26: {
         label25: {
            perm = status.getPermission();
            ugi = UserGroupInformation.getCurrentUser();
            String var10000 = ugi.getShortUserName();
            String var5 = status.getOwner();
            if (var10000 == null) {
               if (var5 != null) {
                  break label25;
               }
            } else if (!var10000.equals(var5)) {
               break label25;
            }

            if (perm.getUserAction().implies(mode)) {
               return true;
            }
            break label26;
         }

         if (Predef$.MODULE$.refArrayOps((Object[])ugi.getGroupNames()).contains(status.getGroup())) {
            if (perm.getGroupAction().implies(mode)) {
               return true;
            }
         } else if (perm.getOtherAction().implies(mode)) {
            return true;
         }
      }

      this.logDebug(new Serializable(this, status, perm, ugi) {
         public static final long serialVersionUID = 0L;
         private final FileStatus status$1;
         private final FsPermission perm$1;
         private final UserGroupInformation ugi$1;

         public final String apply() {
            return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Permission denied: user=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.ugi$1.getShortUserName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"path=", ":", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.status$1.getPath(), this.status$1.getOwner(), this.status$1.getGroup()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.status$1.isDirectory() ? "d" : "-", this.perm$1}))).toString();
         }

         public {
            this.status$1 = status$1;
            this.perm$1 = perm$1;
            this.ugi$1 = ugi$1;
         }
      });
      return false;
   }

   public byte[] serialize(Credentials creds) {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      DataOutputStream dataStream = new DataOutputStream(byteStream);
      creds.writeTokenStorageToStream(dataStream);
      return byteStream.toByteArray();
   }

   public Credentials deserialize(byte[] tokenBytes) {
      ByteArrayInputStream tokensBuf = new ByteArrayInputStream(tokenBytes);
      Credentials creds = new Credentials();
      creds.readTokenStorageStream(new DataInputStream(tokensBuf));
      return creds;
   }

   public boolean isProxyUser(UserGroupInformation ugi) {
      boolean var3;
      label23: {
         UserGroupInformation.AuthenticationMethod var10000 = ugi.getAuthenticationMethod();
         UserGroupInformation.AuthenticationMethod var2 = AuthenticationMethod.PROXY;
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

   private final Seq recurse$1(FileStatus status, FileSystem fs$1) {
      Tuple2 var4 = Predef$.MODULE$.refArrayOps((Object[])fs$1.listStatus(status.getPath())).partition(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(FileStatus x$6) {
            return x$6.isDirectory();
         }
      });
      if (var4 != null) {
         FileStatus[] directories = (FileStatus[])var4._1();
         FileStatus[] leaves = (FileStatus[])var4._2();
         Tuple2 var7 = new Tuple2(directories, leaves);
         FileStatus[] directories = (FileStatus[])var7._1();
         FileStatus[] leaves = (FileStatus[])var7._2();
         return (Seq)Predef$.MODULE$.refArrayOps((Object[])leaves).$plus$plus(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])directories).flatMap(new Serializable(this, fs$1) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkHadoopUtil $outer;
            private final FileSystem fs$1;

            public final Seq<FileStatus> apply(FileStatus f) {
               return this.$outer.listLeafStatuses(this.fs$1, f);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.fs$1 = fs$1;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(FileStatus.class)))), Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()));
      } else {
         throw new MatchError(var4);
      }
   }

   private final Seq recurse$2(FileStatus status, FileSystem fs$2) {
      Tuple2 var4 = Predef$.MODULE$.refArrayOps((Object[])fs$2.listStatus(status.getPath())).partition(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final boolean apply(FileStatus x$8) {
            return x$8.isDirectory();
         }
      });
      if (var4 != null) {
         FileStatus[] directories = (FileStatus[])var4._1();
         FileStatus[] files = (FileStatus[])var4._2();
         Tuple2 var7 = new Tuple2(directories, files);
         FileStatus[] directories = (FileStatus[])var7._1();
         FileStatus[] files = (FileStatus[])var7._2();
         Seq leaves = Predef$.MODULE$.refArrayOps((Object[])directories).isEmpty() ? (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new FileStatus[]{status}))) : (Seq)Seq$.MODULE$.empty();
         return (Seq)leaves.$plus$plus(Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])directories).flatMap(new Serializable(this, fs$2) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkHadoopUtil $outer;
            private final FileSystem fs$2;

            public final Seq<FileStatus> apply(FileStatus dir) {
               return this.$outer.listLeafDirStatuses(this.fs$2, dir);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.fs$2 = fs$2;
               }
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(FileStatus.class)))), Seq$.MODULE$.canBuildFrom());
      } else {
         throw new MatchError(var4);
      }
   }

   public SparkHadoopUtil() {
      Logging$class.$init$(this);
      this.sparkConf = (new SparkConf(false)).loadFromSystemProperties(true);
      this.conf = this.newConfiguration(this.sparkConf());
      UserGroupInformation.setConfiguration(this.conf());
      this.org$apache$spark$deploy$SparkHadoopUtil$$HADOOP_CONF_PATTERN = (new StringOps(Predef$.MODULE$.augmentString("(\\$\\{hadoopconf-[^\\}\\$\\s]+\\})"))).r().unanchored();
   }
}
