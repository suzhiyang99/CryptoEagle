package org.apache.spark;

import java.io.File;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.api.python.PythonWorkerFactory;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.memory.MemoryManager;
import org.apache.spark.metrics.MetricsSystem;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.scheduler.OutputCommitCoordinator;
import org.apache.spark.serializer.Serializer;
import org.apache.spark.serializer.SerializerManager;
import org.apache.spark.shuffle.ShuffleManager;
import org.apache.spark.storage.BlockManager;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import org.spark_project.guava.collect.MapMaker;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.Map;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\rEc\u0001B\u0001\u0003\u0001%\u0011\u0001b\u00159be.,eN\u001e\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sO\u000e\u00011c\u0001\u0001\u000b!A\u00111BD\u0007\u0002\u0019)\tQ\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0010\u0019\t1\u0011I\\=SK\u001a\u0004\"!\u0005\u000b\u000e\u0003IQ!a\u0005\u0002\u0002\u0011%tG/\u001a:oC2L!!\u0006\n\u0003\u000f1{wmZ5oO\"Aq\u0003\u0001BC\u0002\u0013\u0005\u0001$\u0001\u0006fq\u0016\u001cW\u000f^8s\u0013\u0012,\u0012!\u0007\t\u00035uq!aC\u000e\n\u0005qa\u0011A\u0002)sK\u0012,g-\u0003\u0002\u001f?\t11\u000b\u001e:j]\u001eT!\u0001\b\u0007\t\u0011\u0005\u0002!\u0011!Q\u0001\ne\t1\"\u001a=fGV$xN]%eA!I1\u0005\u0001BC\u0002\u0013\u0005!\u0001J\u0001\u0007eB\u001cWI\u001c<\u0016\u0003\u0015\u0002\"AJ\u0015\u000e\u0003\u001dR!\u0001\u000b\u0002\u0002\u0007I\u00048-\u0003\u0002+O\t1!\u000b]2F]ZD\u0001\u0002\f\u0001\u0003\u0002\u0003\u0006I!J\u0001\beB\u001cWI\u001c<!\u0011!q\u0003A!b\u0001\n\u0003y\u0013AC:fe&\fG.\u001b>feV\t\u0001\u0007\u0005\u00022g5\t!G\u0003\u0002/\u0005%\u0011AG\r\u0002\u000b'\u0016\u0014\u0018.\u00197ju\u0016\u0014\b\u0002\u0003\u001c\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0019\u0002\u0017M,'/[1mSj,'\u000f\t\u0005\tq\u0001\u0011)\u0019!C\u0001_\u0005\t2\r\\8tkJ,7+\u001a:jC2L'0\u001a:\t\u0011i\u0002!\u0011!Q\u0001\nA\n!c\u00197pgV\u0014XmU3sS\u0006d\u0017N_3sA!AA\b\u0001BC\u0002\u0013\u0005Q(A\ttKJL\u0017\r\\5{KJl\u0015M\\1hKJ,\u0012A\u0010\t\u0003c}J!\u0001\u0011\u001a\u0003#M+'/[1mSj,'/T1oC\u001e,'\u000f\u0003\u0005C\u0001\t\u0005\t\u0015!\u0003?\u0003I\u0019XM]5bY&TXM]'b]\u0006<WM\u001d\u0011\t\u0011\u0011\u0003!Q1A\u0005\u0002\u0015\u000b\u0001#\\1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:\u0016\u0003\u0019\u0003\"a\u0012%\u000e\u0003\tI!!\u0013\u0002\u0003!5\u000b\u0007oT;uaV$HK]1dW\u0016\u0014\b\u0002C&\u0001\u0005\u0003\u0005\u000b\u0011\u0002$\u0002#5\f\u0007oT;uaV$HK]1dW\u0016\u0014\b\u0005\u0003\u0005N\u0001\t\u0015\r\u0011\"\u0001O\u00039\u0019\b.\u001e4gY\u0016l\u0015M\\1hKJ,\u0012a\u0014\t\u0003!Nk\u0011!\u0015\u0006\u0003%\n\tqa\u001d5vM\u001adW-\u0003\u0002U#\nq1\u000b[;gM2,W*\u00198bO\u0016\u0014\b\u0002\u0003,\u0001\u0005\u0003\u0005\u000b\u0011B(\u0002\u001fMDWO\u001a4mK6\u000bg.Y4fe\u0002B\u0001\u0002\u0017\u0001\u0003\u0006\u0004%\t!W\u0001\u0011EJ|\u0017\rZ2bgRl\u0015M\\1hKJ,\u0012A\u0017\t\u00037zk\u0011\u0001\u0018\u0006\u0003;\n\t\u0011B\u0019:pC\u0012\u001c\u0017m\u001d;\n\u0005}c&\u0001\u0005\"s_\u0006$7-Y:u\u001b\u0006t\u0017mZ3s\u0011!\t\u0007A!A!\u0002\u0013Q\u0016!\u00052s_\u0006$7-Y:u\u001b\u0006t\u0017mZ3sA!A1\r\u0001BC\u0002\u0013\u0005A-\u0001\u0007cY>\u001c7.T1oC\u001e,'/F\u0001f!\t1\u0017.D\u0001h\u0015\tA'!A\u0004ti>\u0014\u0018mZ3\n\u0005)<'\u0001\u0004\"m_\u000e\\W*\u00198bO\u0016\u0014\b\u0002\u00037\u0001\u0005\u0003\u0005\u000b\u0011B3\u0002\u001b\tdwnY6NC:\fw-\u001a:!\u0011!q\u0007A!b\u0001\n\u0003y\u0017aD:fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\u0016\u0003A\u0004\"aR9\n\u0005I\u0014!aD*fGV\u0014\u0018\u000e^=NC:\fw-\u001a:\t\u0011Q\u0004!\u0011!Q\u0001\nA\f\u0001c]3dkJLG/_'b]\u0006<WM\u001d\u0011\t\u0011Y\u0004!Q1A\u0005\u0002]\fQ\"\\3ue&\u001c7oU=ti\u0016lW#\u0001=\u0011\u0005edX\"\u0001>\u000b\u0005m\u0014\u0011aB7fiJL7m]\u0005\u0003{j\u0014Q\"T3ue&\u001c7oU=ti\u0016l\u0007\u0002C@\u0001\u0005\u0003\u0005\u000b\u0011\u0002=\u0002\u001d5,GO]5dgNK8\u000f^3nA!Q\u00111\u0001\u0001\u0003\u0006\u0004%\t!!\u0002\u0002\u001b5,Wn\u001c:z\u001b\u0006t\u0017mZ3s+\t\t9\u0001\u0005\u0003\u0002\n\u0005=QBAA\u0006\u0015\r\tiAA\u0001\u0007[\u0016lwN]=\n\t\u0005E\u00111\u0002\u0002\u000e\u001b\u0016lwN]=NC:\fw-\u001a:\t\u0015\u0005U\u0001A!A!\u0002\u0013\t9!\u0001\bnK6|'/_'b]\u0006<WM\u001d\u0011\t\u0015\u0005e\u0001A!b\u0001\n\u0003\tY\"A\fpkR\u0004X\u000f^\"p[6LGoQ8pe\u0012Lg.\u0019;peV\u0011\u0011Q\u0004\t\u0005\u0003?\t)#\u0004\u0002\u0002\")\u0019\u00111\u0005\u0002\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014\u0018\u0002BA\u0014\u0003C\u0011qcT;uaV$8i\\7nSR\u001cun\u001c:eS:\fGo\u001c:\t\u0015\u0005-\u0002A!A!\u0002\u0013\ti\"\u0001\rpkR\u0004X\u000f^\"p[6LGoQ8pe\u0012Lg.\u0019;pe\u0002B!\"a\f\u0001\u0005\u000b\u0007I\u0011AA\u0019\u0003\u0011\u0019wN\u001c4\u0016\u0005\u0005M\u0002cA$\u00026%\u0019\u0011q\u0007\u0002\u0003\u0013M\u0003\u0018M]6D_:4\u0007BCA\u001e\u0001\t\u0005\t\u0015!\u0003\u00024\u0005)1m\u001c8gA!9\u0011q\b\u0001\u0005\u0002\u0005\u0005\u0013A\u0002\u001fj]&$h\b\u0006\u0010\u0002D\u0005\u0015\u0013qIA%\u0003\u0017\ni%a\u0014\u0002R\u0005M\u0013QKA,\u00033\nY&!\u0018\u0002`A\u0011q\t\u0001\u0005\u0007/\u0005u\u0002\u0019A\r\t\r\r\ni\u00041\u0001&\u0011\u0019q\u0013Q\ba\u0001a!1\u0001(!\u0010A\u0002ABa\u0001PA\u001f\u0001\u0004q\u0004B\u0002#\u0002>\u0001\u0007a\t\u0003\u0004N\u0003{\u0001\ra\u0014\u0005\u00071\u0006u\u0002\u0019\u0001.\t\r\r\fi\u00041\u0001f\u0011\u0019q\u0017Q\ba\u0001a\"1a/!\u0010A\u0002aD\u0001\"a\u0001\u0002>\u0001\u0007\u0011q\u0001\u0005\t\u00033\ti\u00041\u0001\u0002\u001e!A\u0011qFA\u001f\u0001\u0004\t\u0019\u0004\u0003\u0006\u0002d\u0001\u0001\r\u0011\"\u0001\u0003\u0003K\n\u0011\"[:Ti>\u0004\b/\u001a3\u0016\u0005\u0005\u001d\u0004cA\u0006\u0002j%\u0019\u00111\u000e\u0007\u0003\u000f\t{w\u000e\\3b]\"Q\u0011q\u000e\u0001A\u0002\u0013\u0005!!!\u001d\u0002\u001b%\u001c8\u000b^8qa\u0016$w\fJ3r)\u0011\t\u0019(!\u001f\u0011\u0007-\t)(C\u0002\u0002x1\u0011A!\u00168ji\"Q\u00111PA7\u0003\u0003\u0005\r!a\u001a\u0002\u0007a$\u0013\u0007\u0003\u0005\u0002\u0000\u0001\u0001\u000b\u0015BA4\u0003)I7o\u0015;paB,G\r\t\u0005\n\u0003\u0007\u0003!\u0019!C\u0005\u0003\u000b\u000bQ\u0002]=uQ>twk\u001c:lKJ\u001cXCAAD!!\tI)a%\u0002\u0018\u0006\rVBAAF\u0015\u0011\ti)a$\u0002\u000f5,H/\u00192mK*\u0019\u0011\u0011\u0013\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002\u0016\u0006-%a\u0002%bg\"l\u0015\r\u001d\t\u0007\u0017\u0005e\u0015$!(\n\u0007\u0005mEB\u0001\u0004UkBdWM\r\t\u00065\u0005}\u0015$G\u0005\u0004\u0003C{\"aA'baB!\u0011QUAX\u001b\t\t9K\u0003\u0003\u0002*\u0006-\u0016A\u00029zi\"|gNC\u0002\u0002.\n\t1!\u00199j\u0013\u0011\t\t,a*\u0003'AKH\u000f[8o/>\u00148.\u001a:GC\u000e$xN]=\t\u0011\u0005U\u0006\u0001)A\u0005\u0003\u000f\u000ba\u0002]=uQ>twk\u001c:lKJ\u001c\b\u0005\u0003\u0006\u0002:\u0002\u0011\r\u0011\"\u0001\u0003\u0003w\u000b\u0011\u0003[1e_>\u0004(j\u001c2NKR\fG-\u0019;b+\t\ti\fE\u0004\u0002@\u00065\u0017$!5\u000e\u0005\u0005\u0005'\u0002BAb\u0003\u000b\f!bY8oGV\u0014(/\u001a8u\u0015\u0011\t9-!3\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u0003\u0017\fAA[1wC&!\u0011qZAa\u00055\u0019uN\\2veJ,g\u000e^'baB\u00191\"a5\n\u0007\u0005UGBA\u0002B]fD\u0001\"!7\u0001A\u0003%\u0011QX\u0001\u0013Q\u0006$wn\u001c9K_\nlU\r^1eCR\f\u0007\u0005\u0003\u0006\u0002^\u0002\u0001\r\u0011\"\u0001\u0003\u0003?\fA\u0002\u001a:jm\u0016\u0014H+\u001c9ESJ,\"!!9\u0011\t-\t\u0019/G\u0005\u0004\u0003Kd!AB(qi&|g\u000e\u0003\u0006\u0002j\u0002\u0001\r\u0011\"\u0001\u0003\u0003W\f\u0001\u0003\u001a:jm\u0016\u0014H+\u001c9ESJ|F%Z9\u0015\t\u0005M\u0014Q\u001e\u0005\u000b\u0003w\n9/!AA\u0002\u0005\u0005\b\u0002CAy\u0001\u0001\u0006K!!9\u0002\u001b\u0011\u0014\u0018N^3s)6\u0004H)\u001b:!\u0011!\t)\u0010\u0001C\u0001\u0005\u0005]\u0018\u0001B:u_B$\"!a\u001d\t\u0011\u0005m\b\u0001\"\u0001\u0003\u0003{\f!c\u0019:fCR,\u0007+\u001f;i_:<vN]6feR1\u0011q B\u0006\u0005\u001f\u0001BA!\u0001\u0003\b5\u0011!1\u0001\u0006\u0005\u0005\u000b\tI-A\u0002oKRLAA!\u0003\u0003\u0004\t11k\\2lKRDqA!\u0004\u0002z\u0002\u0007\u0011$\u0001\u0006qsRDwN\\#yK\u000eD\u0001B!\u0005\u0002z\u0002\u0007\u0011QT\u0001\bK:4h+\u0019:t\u0011!\u0011)\u0002\u0001C\u0001\u0005\t]\u0011a\u00053fgR\u0014x.\u001f)zi\"|gnV8sW\u0016\u0014H\u0003CA:\u00053\u0011YB!\b\t\u000f\t5!1\u0003a\u00013!A!\u0011\u0003B\n\u0001\u0004\ti\n\u0003\u0005\u0003 \tM\u0001\u0019AA\u0000\u0003\u00199xN]6fe\"A!1\u0005\u0001\u0005\u0002\t\u0011)#A\nsK2,\u0017m]3QsRDwN\\,pe.,'\u000f\u0006\u0005\u0002t\t\u001d\"\u0011\u0006B\u0016\u0011\u001d\u0011iA!\tA\u0002eA\u0001B!\u0005\u0003\"\u0001\u0007\u0011Q\u0014\u0005\t\u0005?\u0011\t\u00031\u0001\u0002\u0000\"\u001a\u0001Aa\f\u0011\t\tE\"qG\u0007\u0003\u0005gQ1A!\u000e\u0003\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0005s\u0011\u0019D\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018nB\u0004\u0003>\tA\tAa\u0010\u0002\u0011M\u0003\u0018M]6F]Z\u00042a\u0012B!\r\u0019\t!\u0001#\u0001\u0003DM!!\u0011\t\u0006\u0011\u0011!\tyD!\u0011\u0005\u0002\t\u001dCC\u0001B \u00111\u0011YE!\u0011A\u0002\u0003\u0007I\u0011\u0002B'\u0003\r)gN^\u000b\u0003\u0003\u0007BAB!\u0015\u0003B\u0001\u0007\t\u0019!C\u0005\u0005'\nq!\u001a8w?\u0012*\u0017\u000f\u0006\u0003\u0002t\tU\u0003BCA>\u0005\u001f\n\t\u00111\u0001\u0002D!I!\u0011\fB!A\u0003&\u00111I\u0001\u0005K:4\b\u0005\u000b\u0003\u0003X\tu\u0003cA\u0006\u0003`%\u0019!\u0011\r\u0007\u0003\u0011Y|G.\u0019;jY\u0016D1B!\u001a\u0003B\t\u0007I\u0011\u0001\u0002\u0003h\u0005\u0001BM]5wKJ\u001c\u0016p\u001d;f[:\u000bW.Z\u000b\u0003\u0005S\u0002BAa\u001b\u0003r5\u0011!Q\u000e\u0006\u0005\u0005_\nI-\u0001\u0003mC:<\u0017b\u0001\u0010\u0003n!I!Q\u000fB!A\u0003%!\u0011N\u0001\u0012IJLg/\u001a:TsN$X-\u001c(b[\u0016\u0004\u0003b\u0003B=\u0005\u0003\u0012\r\u0011\"\u0001\u0003\u0005O\n!#\u001a=fGV$xN]*zgR,WNT1nK\"I!Q\u0010B!A\u0003%!\u0011N\u0001\u0014Kb,7-\u001e;peNK8\u000f^3n\u001d\u0006lW\r\t\u0005\t\u0005\u0003\u0013\t\u0005\"\u0001\u0003\u0004\u0006\u00191/\u001a;\u0015\t\u0005M$Q\u0011\u0005\t\u0005\u000f\u0013y\b1\u0001\u0002D\u0005\tQ\r\u0003\u0005\u0003\f\n\u0005C\u0011\u0001B'\u0003\r9W\r\u001e\u0005\n\u0005\u001f\u0013\t\u0005\"\u0001\u0003\u0005#\u000bqb\u0019:fCR,GI]5wKJ,eN\u001e\u000b\r\u0003\u0007\u0012\u0019J!&\u0003\u001a\n\r&Q\u0016\u0005\t\u0003_\u0011i\t1\u0001\u00024!A!q\u0013BG\u0001\u0004\t9'A\u0004jg2{7-\u00197\t\u0011\tm%Q\u0012a\u0001\u0005;\u000b1\u0002\\5ti\u0016tWM\u001d\"vgB!\u0011q\u0004BP\u0013\u0011\u0011\t+!\t\u0003\u001f1Kg/\u001a'jgR,g.\u001a:CkND\u0001B!*\u0003\u000e\u0002\u0007!qU\u0001\t]Vl7i\u001c:fgB\u00191B!+\n\u0007\t-FBA\u0002J]RD!Ba,\u0003\u000eB\u0005\t\u0019\u0001BY\u0003miwnY6PkR\u0004X\u000f^\"p[6LGoQ8pe\u0012Lg.\u0019;peB)1\"a9\u0002\u001e!I!Q\u0017B!\t\u0003\u0011!qW\u0001\u0012GJ,\u0017\r^3Fq\u0016\u001cW\u000f^8s\u000b:4HCDA\"\u0005s\u0013YL!0\u0003B\n\r'Q\u001b\u0005\t\u0003_\u0011\u0019\f1\u0001\u00024!1qCa-A\u0002eAqAa0\u00034\u0002\u0007\u0011$\u0001\u0005i_N$h.Y7f\u0011!\u0011)Ka-A\u0002\t\u001d\u0006\u0002\u0003Bc\u0005g\u0003\rAa2\u0002\u001f%|WI\\2ssB$\u0018n\u001c8LKf\u0004RaCAr\u0005\u0013\u0004Ra\u0003Bf\u0005\u001fL1A!4\r\u0005\u0015\t%O]1z!\rY!\u0011[\u0005\u0004\u0005'd!\u0001\u0002\"zi\u0016D\u0001Ba&\u00034\u0002\u0007\u0011q\r\u0005\t\u00053\u0014\t\u0005\"\u0003\u0003\\\u000611M]3bi\u0016$b#a\u0011\u0003^\n}'\u0011\u001dBs\u0005S\u0014yO!=\u0003v\n](\u0011 \u0005\t\u0003_\u00119\u000e1\u0001\u00024!1qCa6A\u0002eAqAa9\u0003X\u0002\u0007\u0011$A\u0006cS:$\u0017\t\u001a3sKN\u001c\bb\u0002Bt\u0005/\u0004\r!G\u0001\u0011C\u00124XM\u001d;jg\u0016\fE\r\u001a:fgND\u0001Ba;\u0003X\u0002\u0007!Q^\u0001\u0005a>\u0014H\u000fE\u0003\f\u0003G\u00149\u000b\u0003\u0005\u0003\u0018\n]\u0007\u0019AA4\u0011!\u0011\u0019Pa6A\u0002\t\u001d\u0016A\u00048v[V\u001b\u0018M\u00197f\u0007>\u0014Xm\u001d\u0005\t\u0005\u000b\u00149\u000e1\u0001\u0003H\"Q!1\u0014Bl!\u0003\u0005\rA!(\t\u0015\t=&q\u001bI\u0001\u0002\u0004\u0011\t\fC\u0005\u0003~\n\u0005C\u0011\u0001\u0002\u0003\u0000\u0006\u0011RM\u001c<je>tW.\u001a8u\t\u0016$\u0018-\u001b7t))\u0019\ta!\b\u0004 \r\r2\u0011\u0006\t\u00075\u0005}\u0015da\u0001\u0011\r\r\u00151QCB\u000e\u001d\u0011\u00199a!\u0005\u000f\t\r%1qB\u0007\u0003\u0007\u0017Q1a!\u0004\t\u0003\u0019a$o\\8u}%\tQ\"C\u0002\u0004\u00141\tq\u0001]1dW\u0006<W-\u0003\u0003\u0004\u0018\re!aA*fc*\u001911\u0003\u0007\u0011\u000b-\tI*G\r\t\u0011\u0005=\"1 a\u0001\u0003gAqa!\t\u0003|\u0002\u0007\u0011$\u0001\btG\",G-\u001e7j]\u001elu\u000eZ3\t\u0011\r\u0015\"1 a\u0001\u0007O\t\u0011\"\u00193eK\u0012T\u0015M]:\u0011\u000b\r\u00151QC\r\t\u0011\r-\"1 a\u0001\u0007O\t!\"\u00193eK\u00124\u0015\u000e\\3t\u0011)\u0019yC!\u0011\u0012\u0002\u0013\u00051\u0011G\u0001\u001aGJ,\u0017\r^3Ee&4XM]#om\u0012\"WMZ1vYR$S'\u0006\u0002\u00044)\"!\u0011WB\u001bW\t\u00199\u0004\u0005\u0003\u0004:\r\u0005SBAB\u001e\u0015\u0011\u0019ida\u0010\u0002\u0013Ut7\r[3dW\u0016$'b\u0001B\u001b\u0019%!11IB\u001e\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\u000b\u0007\u000f\u0012\t%%A\u0005\n\r%\u0013\u0001E2sK\u0006$X\r\n3fM\u0006,H\u000e\u001e\u0013:+\t\u0019YE\u000b\u0003\u0003\u001e\u000eU\u0002BCB(\u0005\u0003\n\n\u0011\"\u0003\u00042\u0005\t2M]3bi\u0016$C-\u001a4bk2$H%\r\u0019"
)
public class SparkEnv implements Logging {
   private final String executorId;
   private final RpcEnv rpcEnv;
   private final Serializer serializer;
   private final Serializer closureSerializer;
   private final SerializerManager serializerManager;
   private final MapOutputTracker mapOutputTracker;
   private final ShuffleManager shuffleManager;
   private final BroadcastManager broadcastManager;
   private final BlockManager blockManager;
   private final SecurityManager securityManager;
   private final MetricsSystem metricsSystem;
   private final MemoryManager memoryManager;
   private final OutputCommitCoordinator outputCommitCoordinator;
   private final SparkConf conf;
   private boolean isStopped;
   private final HashMap<Tuple2<String, Map<String, String>>, PythonWorkerFactory> pythonWorkers;
   private final ConcurrentMap<String, Object> hadoopJobMetadata;
   private Option<String> driverTmpDir;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<OutputCommitCoordinator> createDriverEnv$default$5() {
      return SparkEnv$.MODULE$.createDriverEnv$default$5();
   }

   public static SparkEnv get() {
      return SparkEnv$.MODULE$.get();
   }

   public static void set(SparkEnv var0) {
      SparkEnv$.MODULE$.set(var0);
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

   public String executorId() {
      return this.executorId;
   }

   public RpcEnv rpcEnv() {
      return this.rpcEnv;
   }

   public Serializer serializer() {
      return this.serializer;
   }

   public Serializer closureSerializer() {
      return this.closureSerializer;
   }

   public SerializerManager serializerManager() {
      return this.serializerManager;
   }

   public MapOutputTracker mapOutputTracker() {
      return this.mapOutputTracker;
   }

   public ShuffleManager shuffleManager() {
      return this.shuffleManager;
   }

   public BroadcastManager broadcastManager() {
      return this.broadcastManager;
   }

   public BlockManager blockManager() {
      return this.blockManager;
   }

   public SecurityManager securityManager() {
      return this.securityManager;
   }

   public MetricsSystem metricsSystem() {
      return this.metricsSystem;
   }

   public MemoryManager memoryManager() {
      return this.memoryManager;
   }

   public OutputCommitCoordinator outputCommitCoordinator() {
      return this.outputCommitCoordinator;
   }

   public SparkConf conf() {
      return this.conf;
   }

   public boolean isStopped() {
      return this.isStopped;
   }

   public void isStopped_$eq(boolean x$1) {
      this.isStopped = x$1;
   }

   private HashMap<Tuple2<String, Map<String, String>>, PythonWorkerFactory> pythonWorkers() {
      return this.pythonWorkers;
   }

   public ConcurrentMap<String, Object> hadoopJobMetadata() {
      return this.hadoopJobMetadata;
   }

   public Option<String> driverTmpDir() {
      return this.driverTmpDir;
   }

   public void driverTmpDir_$eq(Option<String> x$1) {
      this.driverTmpDir = x$1;
   }

   public void stop() {
      if (!this.isStopped()) {
         this.isStopped_$eq(true);
         this.pythonWorkers().values().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(PythonWorkerFactory x$1) {
               x$1.stop();
            }
         });
         this.mapOutputTracker().stop();
         this.shuffleManager().stop();
         this.broadcastManager().stop();
         this.blockManager().stop();
         this.blockManager().master().stop();
         this.metricsSystem().stop();
         this.outputCommitCoordinator().stop();
         this.rpcEnv().shutdown();
         this.rpcEnv().awaitTermination();
         Option var1 = this.driverTmpDir();
         if (var1 instanceof Some) {
            Some var2 = (Some)var1;
            String path = (String)var2.x();

            try {
               Utils$.MODULE$.deleteRecursively(new File(path));
               BoxedUnit var7 = BoxedUnit.UNIT;
            } catch (Exception var6) {
               this.logWarning(new Serializable(this, path) {
                  public static final long serialVersionUID = 0L;
                  private final String path$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Exception while deleting Spark temp dir: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$1}));
                  }

                  public {
                     this.path$1 = path$1;
                  }
               }, var6);
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         } else {
            if (!None$.MODULE$.equals(var1)) {
               throw new MatchError(var1);
            }

            BoxedUnit var4 = BoxedUnit.UNIT;
         }
      }

   }

   public synchronized Socket createPythonWorker(String pythonExec, Map<String, String> envVars) {
      Tuple2 key = new Tuple2(pythonExec, envVars);
      return ((PythonWorkerFactory)this.pythonWorkers().getOrElseUpdate(key, new Serializable(this, pythonExec, envVars) {
         public static final long serialVersionUID = 0L;
         private final String pythonExec$1;
         private final Map envVars$1;

         public final PythonWorkerFactory apply() {
            return new PythonWorkerFactory(this.pythonExec$1, this.envVars$1);
         }

         public {
            this.pythonExec$1 = pythonExec$1;
            this.envVars$1 = envVars$1;
         }
      })).create();
   }

   public synchronized void destroyPythonWorker(String pythonExec, Map<String, String> envVars, Socket worker) {
      Tuple2 key = new Tuple2(pythonExec, envVars);
      this.pythonWorkers().get(key).foreach(new Serializable(this, worker) {
         public static final long serialVersionUID = 0L;
         private final Socket worker$1;

         public final void apply(PythonWorkerFactory x$2) {
            x$2.stopWorker(this.worker$1);
         }

         public {
            this.worker$1 = worker$1;
         }
      });
   }

   public synchronized void releasePythonWorker(String pythonExec, Map<String, String> envVars, Socket worker) {
      Tuple2 key = new Tuple2(pythonExec, envVars);
      this.pythonWorkers().get(key).foreach(new Serializable(this, worker) {
         public static final long serialVersionUID = 0L;
         private final Socket worker$2;

         public final void apply(PythonWorkerFactory x$3) {
            x$3.releaseWorker(this.worker$2);
         }

         public {
            this.worker$2 = worker$2;
         }
      });
   }

   public SparkEnv(String executorId, RpcEnv rpcEnv, Serializer serializer, Serializer closureSerializer, SerializerManager serializerManager, MapOutputTracker mapOutputTracker, ShuffleManager shuffleManager, BroadcastManager broadcastManager, BlockManager blockManager, SecurityManager securityManager, MetricsSystem metricsSystem, MemoryManager memoryManager, OutputCommitCoordinator outputCommitCoordinator, SparkConf conf) {
      this.executorId = executorId;
      this.rpcEnv = rpcEnv;
      this.serializer = serializer;
      this.closureSerializer = closureSerializer;
      this.serializerManager = serializerManager;
      this.mapOutputTracker = mapOutputTracker;
      this.shuffleManager = shuffleManager;
      this.broadcastManager = broadcastManager;
      this.blockManager = blockManager;
      this.securityManager = securityManager;
      this.metricsSystem = metricsSystem;
      this.memoryManager = memoryManager;
      this.outputCommitCoordinator = outputCommitCoordinator;
      this.conf = conf;
      Logging$class.$init$(this);
      this.isStopped = false;
      this.pythonWorkers = (HashMap)HashMap$.MODULE$.apply(Nil$.MODULE$);
      this.hadoopJobMetadata = (new MapMaker()).softValues().makeMap();
      this.driverTmpDir = None$.MODULE$;
   }
}
