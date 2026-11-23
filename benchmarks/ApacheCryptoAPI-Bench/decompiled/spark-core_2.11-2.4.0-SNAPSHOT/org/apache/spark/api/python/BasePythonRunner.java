package org.apache.spark.api.python;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.spark.InterruptibleIterator;
import org.apache.spark.SparkEnv;
import org.apache.spark.SparkEnv$;
import org.apache.spark.SparkException;
import org.apache.spark.SparkFiles$;
import org.apache.spark.TaskContext;
import org.apache.spark.TaskContext$;
import org.apache.spark.TaskKilledException;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.Option$;
import scala.PartialFunction;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.package$;
import scala.collection.BufferedIterator;
import scala.collection.GenTraversableOnce;
import scala.collection.Iterable;
import scala.collection.Iterator;
import scala.collection.Iterator$class;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.Traversable;
import scala.collection.TraversableOnce;
import scala.collection.TraversableOnce$class;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.IndexedSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Set;
import scala.collection.immutable.Stream;
import scala.collection.immutable.StringOps;
import scala.collection.immutable.Vector;
import scala.collection.mutable.Buffer;
import scala.collection.mutable.StringBuilder;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.Null$;
import scala.runtime.RichInt$;

@ScalaSignature(
   bytes = "\u0006\u0001\t\u0015hAB\u0001\u0003\u0003\u00031AB\u0001\tCCN,\u0007+\u001f;i_:\u0014VO\u001c8fe*\u00111\u0001B\u0001\u0007af$\bn\u001c8\u000b\u0005\u00151\u0011aA1qS*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-F\u0002\u000e\u000b>\u001b2\u0001\u0001\b\u0015!\ty!#D\u0001\u0011\u0015\u0005\t\u0012!B:dC2\f\u0017BA\n\u0011\u0005\u0019\te.\u001f*fMB\u0011Q\u0003G\u0007\u0002-)\u0011qCB\u0001\tS:$XM\u001d8bY&\u0011\u0011D\u0006\u0002\b\u0019><w-\u001b8h\u0011!Y\u0002A!A!\u0002\u0013i\u0012!\u00024v]\u000e\u001c8\u0001\u0001\t\u0004=\u0019JcBA\u0010%\u001d\t\u00013%D\u0001\"\u0015\t\u0011C$\u0001\u0004=e>|GOP\u0005\u0002#%\u0011Q\u0005E\u0001\ba\u0006\u001c7.Y4f\u0013\t9\u0003FA\u0002TKFT!!\n\t\u0011\u0005)ZS\"\u0001\u0002\n\u00051\u0012!AF\"iC&tW\r\u001a)zi\"|gNR;oGRLwN\\:\t\u00119\u0002!\u0011!Q\u0001\n=\n!BY;gM\u0016\u00148+\u001b>f!\ty\u0001'\u0003\u00022!\t\u0019\u0011J\u001c;\t\u0011M\u0002!\u0011!Q\u0001\nQ\n1B]3vg\u0016<vN]6feB\u0011q\"N\u0005\u0003mA\u0011qAQ8pY\u0016\fg\u000e\u0003\u00059\u0001\t\u0005\t\u0015!\u00030\u0003!)g/\u00197UsB,\u0007\u0002\u0003\u001e\u0001\u0005\u0003\u0005\u000b\u0011B\u001e\u0002\u0015\u0005\u0014xm\u00144gg\u0016$8\u000fE\u0002\u0010yyJ!!\u0010\t\u0003\u000b\u0005\u0013(/Y=\u0011\u0007=at\u0006C\u0003A\u0001\u0011\u0005\u0011)\u0001\u0004=S:LGO\u0010\u000b\u0007\u0005F\u00136\u000bV+\u0011\t)\u00021I\u0014\t\u0003\t\u0016c\u0001\u0001B\u0003G\u0001\t\u0007qI\u0001\u0002J\u001dF\u0011\u0001j\u0013\t\u0003\u001f%K!A\u0013\t\u0003\u000f9{G\u000f[5oOB\u0011q\u0002T\u0005\u0003\u001bB\u00111!\u00118z!\t!u\nB\u0003Q\u0001\t\u0007qIA\u0002P+RCQaG A\u0002uAQAL A\u0002=BQaM A\u0002QBQ\u0001O A\u0002=BQAO A\u0002mBqa\u0016\u0001C\u0002\u0013E\u0001,A\u0004f]Z4\u0016M]:\u0016\u0003e\u0003BAW0bC6\t1L\u0003\u0002];\u0006!Q\u000f^5m\u0015\u0005q\u0016\u0001\u00026bm\u0006L!\u0001Y.\u0003\u00075\u000b\u0007\u000f\u0005\u0002cK:\u0011qbY\u0005\u0003IB\ta\u0001\u0015:fI\u00164\u0017B\u00014h\u0005\u0019\u0019FO]5oO*\u0011A\r\u0005\u0005\u0007S\u0002\u0001\u000b\u0011B-\u0002\u0011\u0015tgOV1sg\u0002Bqa\u001b\u0001C\u0002\u0013EA.\u0001\u0006qsRDwN\\#yK\u000e,\u0012!\u0019\u0005\u0007]\u0002\u0001\u000b\u0011B1\u0002\u0017ALH\u000f[8o\u000bb,7\r\t\u0005\ba\u0002\u0011\r\u0011\"\u0005m\u0003%\u0001\u0018\u0010\u001e5p]Z+'\u000f\u0003\u0004s\u0001\u0001\u0006I!Y\u0001\u000baf$\bn\u001c8WKJ\u0004\u0003b\u0002;\u0001\u0005\u0004%\t\"^\u0001\fC\u000e\u001cW/\\;mCR|'/F\u0001w!\tQs/\u0003\u0002y\u0005\t\u0019\u0002+\u001f;i_:\f5mY;nk2\fGo\u001c:We!1!\u0010\u0001Q\u0001\nY\fA\"Y2dk6,H.\u0019;pe\u0002BQ\u0001 \u0001\u0005\u0002u\fqaY8naV$X\rF\u0004\u007f\u0003\u0007\tI!!\u0004\u0011\u0007yyh*C\u0002\u0002\u0002!\u0012\u0001\"\u0013;fe\u0006$xN\u001d\u0005\b\u0003\u000bY\b\u0019AA\u0004\u00035Ig\u000e];u\u0013R,'/\u0019;peB\u0019ad`\"\t\r\u0005-1\u00101\u00010\u00039\u0001\u0018M\u001d;ji&|g.\u00138eKbDq!a\u0004|\u0001\u0004\t\t\"A\u0004d_:$X\r\u001f;\u0011\t\u0005M\u0011QC\u0007\u0002\r%\u0019\u0011q\u0003\u0004\u0003\u0017Q\u000b7o[\"p]R,\u0007\u0010\u001e\u0005\b\u00037\u0001a\u0011CA\u000f\u0003=qWm^,sSR,'\u000f\u00165sK\u0006$G\u0003DA\u0010\u0003w\fi0a@\u0003\u0002\t\r\u0001\u0003BA\u0011\u0003Gi\u0011\u0001\u0001\u0004\b\u0003K\u0001\u0011\u0011AA\u0014\u000519&/\u001b;feRC'/Z1e'\u0011\t\u0019#!\u000b\u0011\t\u0005-\u0012\u0011G\u0007\u0003\u0003[Q1!a\f^\u0003\u0011a\u0017M\\4\n\t\u0005M\u0012Q\u0006\u0002\u0007)\"\u0014X-\u00193\t\u0017\u0005]\u00121\u0005B\u0001B\u0003%\u0011\u0011H\u0001\u0004K:4\b\u0003BA\n\u0003wI1!!\u0010\u0007\u0005!\u0019\u0006/\u0019:l\u000b:4\bbCA!\u0003G\u0011\t\u0011)A\u0005\u0003\u0007\naa^8sW\u0016\u0014\b\u0003BA#\u0003\u0017j!!a\u0012\u000b\u0007\u0005%S,A\u0002oKRLA!!\u0014\u0002H\t11k\\2lKRD1\"!\u0002\u0002$\t\u0005\t\u0015!\u0003\u0002\b!Q\u00111BA\u0012\u0005\u0003\u0005\u000b\u0011B\u0018\t\u0017\u0005=\u00111\u0005B\u0001B\u0003%\u0011\u0011\u0003\u0005\b\u0001\u0006\rB\u0011AA,)1\ty\"!\u0017\u0002\\\u0005u\u0013qLA1\u0011!\t9$!\u0016A\u0002\u0005e\u0002\u0002CA!\u0003+\u0002\r!a\u0011\t\u0011\u0005\u0015\u0011Q\u000ba\u0001\u0003\u000fAq!a\u0003\u0002V\u0001\u0007q\u0006\u0003\u0005\u0002\u0010\u0005U\u0003\u0019AA\t\u0011)\t)'a\tA\u0002\u0013%\u0011qM\u0001\u000b?\u0016D8-\u001a9uS>tWCAA5!\rq\u00121N\u0005\u0004\u0003[B#!C#yG\u0016\u0004H/[8o\u0011)\t\t(a\tA\u0002\u0013%\u00111O\u0001\u000f?\u0016D8-\u001a9uS>tw\fJ3r)\u0011\t)(a\u001f\u0011\u0007=\t9(C\u0002\u0002zA\u0011A!\u00168ji\"Q\u0011QPA8\u0003\u0003\u0005\r!!\u001b\u0002\u0007a$\u0013\u0007C\u0005\u0002\u0002\u0006\r\u0002\u0015)\u0003\u0002j\u0005Yq,\u001a=dKB$\u0018n\u001c8!Q\u0011\ty(!\"\u0011\u0007=\t9)C\u0002\u0002\nB\u0011\u0001B^8mCRLG.\u001a\u0005\u000b\u0003\u001b\u000b\u0019C1A\u0005\n\u0005=\u0015A\u00049zi\"|g.\u00138dYV$Wm]\u000b\u0003\u0003#\u0003R!a%\u0002\u001e\u0006l!!!&\u000b\t\u0005]\u0015\u0011T\u0001\nS6lW\u000f^1cY\u0016T1!a'\u0011\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003?\u000b)JA\u0002TKRD\u0011\"a)\u0002$\u0001\u0006I!!%\u0002\u001fALH\u000f[8o\u0013:\u001cG.\u001e3fg\u0002B!\"a*\u0002$\t\u0007I\u0011BAU\u00035\u0011'o\\1eG\u0006\u001cHOV1sgV\u0011\u00111\u0016\t\u0007\u0003[\u000by+!-\u000e\u0005\u0005e\u0015bA\u0014\u0002\u001aB1\u00111WA]\u0003{k!!!.\u000b\u0007\u0005]f!A\u0005ce>\fGmY1ti&!\u00111XA[\u0005%\u0011%o\\1eG\u0006\u001cH\u000fE\u0002+\u0003\u007fK1!!1\u0003\u0005=\u0001\u0016\u0010\u001e5p]\n\u0013x.\u00193dCN$\b\"CAc\u0003G\u0001\u000b\u0011BAV\u00039\u0011'o\\1eG\u0006\u001cHOV1sg\u0002B\u0001\"!3\u0002$\u0011\u0005\u00111Z\u0001\nKb\u001cW\r\u001d;j_:,\"!!4\u0011\u000b=\ty-!\u001b\n\u0007\u0005E\u0007C\u0001\u0004PaRLwN\u001c\u0005\t\u0003+\f\u0019\u0003\"\u0001\u0002X\u0006A2\u000f[;uI><hn\u00148UCN\\7i\\7qY\u0016$\u0018n\u001c8\u0015\u0005\u0005U\u0004\u0002CAn\u0003G1\t\"!8\u0002\u0019]\u0014\u0018\u000e^3D_6l\u0017M\u001c3\u0015\t\u0005U\u0014q\u001c\u0005\t\u0003C\fI\u000e1\u0001\u0002d\u00069A-\u0019;b\u001fV$\b\u0003BAs\u0003Wl!!a:\u000b\u0007\u0005%X,\u0001\u0002j_&!\u0011Q^At\u0005A!\u0015\r^1PkR\u0004X\u000f^*ue\u0016\fW\u000e\u0003\u0005\u0002r\u0006\rb\u0011CAz\u0003U9(/\u001b;f\u0013R,'/\u0019;peR{7\u000b\u001e:fC6$B!!\u001e\u0002v\"A\u0011\u0011]Ax\u0001\u0004\t\u0019\u000f\u0003\u0005\u0002z\u0006\rB\u0011IAl\u0003\r\u0011XO\u001c\u0005\t\u0003o\tI\u00021\u0001\u0002:!A\u0011\u0011IA\r\u0001\u0004\t\u0019\u0005\u0003\u0005\u0002\u0006\u0005e\u0001\u0019AA\u0004\u0011\u001d\tY!!\u0007A\u0002=B\u0001\"a\u0004\u0002\u001a\u0001\u0007\u0011\u0011\u0003\u0005\b\u0005\u000f\u0001a\u0011\u0003B\u0005\u0003EqWm\u001e*fC\u0012,'/\u0013;fe\u0006$xN\u001d\u000b\u0010}\n-!Q\u0003B\r\u0005G\u0011)Ca\n\u0003<!A!Q\u0002B\u0003\u0001\u0004\u0011y!\u0001\u0004tiJ,\u0017-\u001c\t\u0005\u0003K\u0014\t\"\u0003\u0003\u0003\u0014\u0005\u001d(a\u0004#bi\u0006Le\u000e];u'R\u0014X-Y7\t\u0011\t]!Q\u0001a\u0001\u0003?\tAb\u001e:ji\u0016\u0014H\u000b\u001b:fC\u0012D\u0001Ba\u0007\u0003\u0006\u0001\u0007!QD\u0001\ngR\f'\u000f\u001e+j[\u0016\u00042a\u0004B\u0010\u0013\r\u0011\t\u0003\u0005\u0002\u0005\u0019>tw\r\u0003\u0005\u00028\t\u0015\u0001\u0019AA\u001d\u0011!\t\tE!\u0002A\u0002\u0005\r\u0003\u0002\u0003B\u0015\u0005\u000b\u0001\rAa\u000b\u0002\u0011I,G.Z1tK\u0012\u0004BA!\f\u000385\u0011!q\u0006\u0006\u0005\u0005c\u0011\u0019$\u0001\u0004bi>l\u0017n\u0019\u0006\u0004\u0005kY\u0016AC2p]\u000e,(O]3oi&!!\u0011\bB\u0018\u00055\tEo\\7jG\n{w\u000e\\3b]\"A\u0011q\u0002B\u0003\u0001\u0004\t\tBB\u0004\u0003@\u0001\t\tA!\u0011\u0003\u001dI+\u0017\rZ3s\u0013R,'/\u0019;peN!!Q\b\b\u007f\u0011-\u0011iA!\u0010\u0003\u0002\u0003\u0006IAa\u0004\t\u0017\t]!Q\bB\u0001B\u0003%\u0011q\u0004\u0005\f\u00057\u0011iD!A!\u0002\u0013\u0011i\u0002C\u0006\u00028\tu\"\u0011!Q\u0001\n\u0005e\u0002bCA!\u0005{\u0011\t\u0011)A\u0005\u0003\u0007B1B!\u000b\u0003>\t\u0005\t\u0015!\u0003\u0003,!Y\u0011q\u0002B\u001f\u0005\u0003\u0005\u000b\u0011BA\t\u0011\u001d\u0001%Q\bC\u0001\u0005'\"\u0002C!\u0016\u0003X\te#1\fB/\u0005?\u0012\tGa\u0019\u0011\t\u0005\u0005\"Q\b\u0005\t\u0005\u001b\u0011\t\u00061\u0001\u0003\u0010!A!q\u0003B)\u0001\u0004\ty\u0002\u0003\u0005\u0003\u001c\tE\u0003\u0019\u0001B\u000f\u0011!\t9D!\u0015A\u0002\u0005e\u0002\u0002CA!\u0005#\u0002\r!a\u0011\t\u0011\t%\"\u0011\u000ba\u0001\u0005WA\u0001\"a\u0004\u0003R\u0001\u0007\u0011\u0011\u0003\u0005\r\u0005O\u0012i\u00041AA\u0002\u0013%!\u0011N\u0001\b]\u0016DHo\u00142k+\u0005q\u0005\u0002\u0004B7\u0005{\u0001\r\u00111A\u0005\n\t=\u0014a\u00038fqR|%M[0%KF$B!!\u001e\u0003r!I\u0011Q\u0010B6\u0003\u0003\u0005\rA\u0014\u0005\t\u0005k\u0012i\u0004)Q\u0005\u001d\u0006Aa.\u001a=u\u001f\nT\u0007\u0005\u0003\u0006\u0003z\tu\u0002\u0019!C\u0005\u0005w\n1!Z8t+\u0005!\u0004B\u0003B@\u0005{\u0001\r\u0011\"\u0003\u0003\u0002\u00069Qm\\:`I\u0015\fH\u0003BA;\u0005\u0007C\u0011\"! \u0003~\u0005\u0005\t\u0019\u0001\u001b\t\u0011\t\u001d%Q\bQ!\nQ\nA!Z8tA!A!1\u0012B\u001f\t\u0003\u0012Y(A\u0004iCNtU\r\u001f;\t\u0011\t=%Q\bC!\u0005#\u000bAA\\3yiR\ta\n\u0003\u0005\u0003\u0016\nub\u0011\u0003BI\u0003\u0011\u0011X-\u00193\t\u0011\te%Q\bC\t\u0003/\f\u0001\u0003[1oI2,G+[7j]\u001e$\u0015\r^1\t\u0011\tu%Q\bC\t\u0005?\u000bQ\u0003[1oI2,\u0007+\u001f;i_:,\u0005pY3qi&|g\u000e\u0006\u0002\u0003\"B\u0019!Fa)\n\u0007\t\u0015&AA\bQsRDwN\\#yG\u0016\u0004H/[8o\u0011!\u0011IK!\u0010\u0005\u0012\u0005]\u0017A\u00065b]\u0012dW-\u00128e\u001f\u001a$\u0015\r^1TK\u000e$\u0018n\u001c8\t\u0015\t5&Q\bb\u0001\n#\u0011y+A\biC:$G.Z#yG\u0016\u0004H/[8o+\t\u0011\t\f\u0005\u0004\u0010\u0005g\u00139LT\u0005\u0004\u0005k\u0003\"a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\u0011\u0007y\u0011I,C\u0002\u0003<\"\u0012\u0011\u0002\u00165s_^\f'\r\\3\t\u0013\t}&Q\bQ\u0001\n\tE\u0016\u0001\u00055b]\u0012dW-\u0012=dKB$\u0018n\u001c8!\r\u0019\u0011\u0019\r\u0001\u0001\u0003F\niQj\u001c8ji>\u0014H\u000b\u001b:fC\u0012\u001cBA!1\u0002*!Y\u0011q\u0007Ba\u0005\u0003\u0005\u000b\u0011BA\u001d\u0011-\t\tE!1\u0003\u0002\u0003\u0006I!a\u0011\t\u0017\u0005=!\u0011\u0019B\u0001B\u0003%\u0011\u0011\u0003\u0005\b\u0001\n\u0005G\u0011\u0001Bh)!\u0011\tNa5\u0003V\n]\u0007\u0003BA\u0011\u0005\u0003D\u0001\"a\u000e\u0003N\u0002\u0007\u0011\u0011\b\u0005\t\u0003\u0003\u0012i\r1\u0001\u0002D!A\u0011q\u0002Bg\u0001\u0004\t\t\u0002\u0003\u0006\u0003\\\n\u0005'\u0019!C\u0005\u0005;\fq\u0002^1tW.KG\u000e\u001c+j[\u0016|W\u000f^\u000b\u0003\u0005;A\u0011B!9\u0003B\u0002\u0006IA!\b\u0002!Q\f7o[&jY2$\u0016.\\3pkR\u0004\u0003\u0002CA}\u0005\u0003$\t%a6"
)
public abstract class BasePythonRunner<IN, OUT> implements Logging {
   public final Seq<ChainedPythonFunctions> org$apache$spark$api$python$BasePythonRunner$$funcs;
   public final int org$apache$spark$api$python$BasePythonRunner$$bufferSize;
   public final boolean org$apache$spark$api$python$BasePythonRunner$$reuseWorker;
   public final int org$apache$spark$api$python$BasePythonRunner$$evalType;
   private final Map<String, String> envVars;
   private final String pythonExec;
   private final String pythonVer;
   private final PythonAccumulatorV2 accumulator;
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

   public Map<String, String> envVars() {
      return this.envVars;
   }

   public String pythonExec() {
      return this.pythonExec;
   }

   public String pythonVer() {
      return this.pythonVer;
   }

   public PythonAccumulatorV2 accumulator() {
      return this.accumulator;
   }

   public Iterator<OUT> compute(Iterator<IN> inputIterator, int partitionIndex, TaskContext context) {
      long startTime = System.currentTimeMillis();
      SparkEnv env = SparkEnv$.MODULE$.get();
      String localdir = Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])env.blockManager().diskBlockManager().localDirs()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(File f) {
            return f.getPath();
         }
      }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(String.class)))).mkString(",");
      this.envVars().put("SPARK_LOCAL_DIRS", localdir);
      if (this.org$apache$spark$api$python$BasePythonRunner$$reuseWorker) {
         this.envVars().put("SPARK_REUSE_WORKER", "1");
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Socket worker = env.createPythonWorker(this.pythonExec(), ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(this.envVars()).asScala()).toMap(Predef$.MODULE$.$conforms()));
      AtomicBoolean released = new AtomicBoolean(false);
      WriterThread writerThread = this.newWriterThread(env, worker, inputIterator, partitionIndex, context);
      context.addTaskCompletionListener((Function1)(new Serializable(this, worker, released, writerThread) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final BasePythonRunner $outer;
         private final Socket worker$1;
         private final AtomicBoolean released$1;
         private final WriterThread writerThread$1;

         public final void apply(TaskContext x$1) {
            this.writerThread$1.shutdownOnTaskCompletion();
            if (!this.$outer.org$apache$spark$api$python$BasePythonRunner$$reuseWorker || !this.released$1.get()) {
               try {
                  this.worker$1.close();
               } catch (Exception var3) {
                  this.$outer.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Failed to close worker socket";
                     }
                  }, var3);
               }
            }

         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.worker$1 = worker$1;
               this.released$1 = released$1;
               this.writerThread$1 = writerThread$1;
            }
         }
      }));
      writerThread.start();
      (new MonitorThread(env, worker, context)).start();
      DataInputStream stream = new DataInputStream(new BufferedInputStream(worker.getInputStream(), this.org$apache$spark$api$python$BasePythonRunner$$bufferSize));
      Iterator stdoutIterator = this.newReaderIterator(stream, writerThread, startTime, env, worker, released, context);
      return new InterruptibleIterator<OUT>(context, stdoutIterator);
   }

   public abstract BasePythonRunner<IN, OUT>.WriterThread newWriterThread(SparkEnv var1, Socket var2, Iterator<IN> var3, int var4, TaskContext var5);

   public abstract Iterator<OUT> newReaderIterator(DataInputStream var1, BasePythonRunner<IN, OUT>.WriterThread var2, long var3, SparkEnv var5, Socket var6, AtomicBoolean var7, TaskContext var8);

   public BasePythonRunner(Seq<ChainedPythonFunctions> funcs, int bufferSize, boolean reuseWorker, int evalType, int[][] argOffsets) {
      this.org$apache$spark$api$python$BasePythonRunner$$funcs = funcs;
      this.org$apache$spark$api$python$BasePythonRunner$$bufferSize = bufferSize;
      this.org$apache$spark$api$python$BasePythonRunner$$reuseWorker = reuseWorker;
      this.org$apache$spark$api$python$BasePythonRunner$$evalType = evalType;
      Logging$class.$init$(this);
      Predef$.MODULE$.require(funcs.length() == argOffsets.length, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "argOffsets should have the same length as funcs";
         }
      });
      this.envVars = ((PythonFunction)((ChainedPythonFunctions)funcs.head()).funcs().head()).envVars();
      this.pythonExec = ((PythonFunction)((ChainedPythonFunctions)funcs.head()).funcs().head()).pythonExec();
      this.pythonVer = ((PythonFunction)((ChainedPythonFunctions)funcs.head()).funcs().head()).pythonVer();
      this.accumulator = ((PythonFunction)((ChainedPythonFunctions)funcs.head()).funcs().head()).accumulator();
   }

   public abstract class WriterThread extends Thread {
      public final Socket org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker;
      public final int org$apache$spark$api$python$BasePythonRunner$WriterThread$$partitionIndex;
      public final TaskContext org$apache$spark$api$python$BasePythonRunner$WriterThread$$context;
      private volatile Exception org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception;
      private final Set<String> org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes;
      private final Seq<Broadcast<PythonBroadcast>> org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars;
      // $FF: synthetic field
      public final BasePythonRunner $outer;

      private Exception org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception() {
         return this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception;
      }

      public void org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception_$eq(Exception x$1) {
         this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception = x$1;
      }

      public Set<String> org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes() {
         return this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes;
      }

      public Seq<Broadcast<PythonBroadcast>> org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars() {
         return this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars;
      }

      public Option<Exception> exception() {
         return Option$.MODULE$.<Exception>apply(this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception());
      }

      public void shutdownOnTaskCompletion() {
         Predef$.MODULE$.assert(this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.isCompleted());
         this.interrupt();
      }

      public abstract void writeCommand(DataOutputStream var1);

      public abstract void writeIteratorToStream(DataOutputStream var1);

      public void run() {
         Utils$.MODULE$.logUncaughtExceptions(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final WriterThread $outer;

            public final Object apply() {
               Exception var2;
               Object var10000;
               try {
                  TaskContext$.MODULE$.setTaskContext(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context);
                  BufferedOutputStream stream = new BufferedOutputStream(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker.getOutputStream(), this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$$outer().org$apache$spark$api$python$BasePythonRunner$$bufferSize);
                  DataOutputStream dataOut = new DataOutputStream(stream);
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$partitionIndex);
                  PythonRDD$.MODULE$.writeUTF(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$$outer().pythonVer(), dataOut);
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.stageId());
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.partitionId());
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.attemptNumber());
                  dataOut.writeLong(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.taskAttemptId());
                  PythonRDD$.MODULE$.writeUTF(SparkFiles$.MODULE$.getRootDirectory(), dataOut);
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes().size());
                  this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes().foreach(new Serializable(this, dataOut) {
                     public static final long serialVersionUID = 0L;
                     private final DataOutputStream dataOut$1;

                     public final void apply(String include) {
                        PythonRDD$.MODULE$.writeUTF(include, this.dataOut$1);
                     }

                     public {
                        this.dataOut$1 = dataOut$1;
                     }
                  });
                  scala.collection.mutable.Set oldBids = PythonRDD$.MODULE$.getWorkerBroadcasts(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker);
                  Set newBids = ((TraversableOnce)this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars().map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final long apply(Broadcast<PythonBroadcast> x$6) {
                        return x$6.id();
                     }
                  }, Seq$.MODULE$.canBuildFrom())).toSet();
                  scala.collection.mutable.Set toRemove = (scala.collection.mutable.Set)oldBids.diff(newBids);
                  int cnt = toRemove.size() + newBids.diff(oldBids).size();
                  dataOut.writeInt(cnt);
                  toRemove.foreach(new Serializable(this, dataOut, oldBids) {
                     public static final long serialVersionUID = 0L;
                     private final DataOutputStream dataOut$1;
                     private final scala.collection.mutable.Set oldBids$1;

                     public final boolean apply(long bid) {
                        return this.apply$mcZJ$sp(bid);
                     }

                     public boolean apply$mcZJ$sp(long bid) {
                        this.dataOut$1.writeLong(-bid - 1L);
                        return this.oldBids$1.remove(BoxesRunTime.boxToLong(bid));
                     }

                     public {
                        this.dataOut$1 = dataOut$1;
                        this.oldBids$1 = oldBids$1;
                     }
                  });
                  this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars().foreach(new Serializable(this, dataOut, oldBids) {
                     public static final long serialVersionUID = 0L;
                     private final DataOutputStream dataOut$1;
                     private final scala.collection.mutable.Set oldBids$1;

                     public final Object apply(Broadcast<PythonBroadcast> broadcast) {
                        Object var10000;
                        if (this.oldBids$1.contains(BoxesRunTime.boxToLong(broadcast.id()))) {
                           var10000 = BoxedUnit.UNIT;
                        } else {
                           this.dataOut$1.writeLong(broadcast.id());
                           PythonRDD$.MODULE$.writeUTF(((PythonBroadcast)broadcast.value()).path(), this.dataOut$1);
                           var10000 = BoxesRunTime.boxToBoolean(this.oldBids$1.add(BoxesRunTime.boxToLong(broadcast.id())));
                        }

                        return var10000;
                     }

                     public {
                        this.dataOut$1 = dataOut$1;
                        this.oldBids$1 = oldBids$1;
                     }
                  });
                  dataOut.flush();
                  dataOut.writeInt(this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$$outer().org$apache$spark$api$python$BasePythonRunner$$evalType);
                  this.$outer.writeCommand(dataOut);
                  this.$outer.writeIteratorToStream(dataOut);
                  dataOut.writeInt(SpecialLengths$.MODULE$.END_OF_STREAM());
                  dataOut.flush();
                  var10000 = BoxedUnit.UNIT;
                  return var10000;
               } catch (Exception var11) {
                  var2 = var11;
               } finally {
                  ;
               }

               if (!this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.isCompleted() && !this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context.isInterrupted()) {
                  this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception_$eq(var2);
                  var10000 = this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker.isClosed() ? BoxedUnit.UNIT : Utils$.MODULE$.tryLog(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$anonfun$$$outer().org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker.shutdownOutput();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
               } else {
                  this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$$outer().logDebug(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Exception thrown after task completion (likely due to cleanup)";
                     }
                  }, var2);
                  var10000 = this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker.isClosed() ? BoxedUnit.UNIT : Utils$.MODULE$.tryLog(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply() {
                        this.apply$mcV$sp();
                     }

                     public void apply$mcV$sp() {
                        this.$outer.org$apache$spark$api$python$BasePythonRunner$WriterThread$$anonfun$$$outer().org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker.shutdownOutput();
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

               return var10000;
            }

            // $FF: synthetic method
            public WriterThread org$apache$spark$api$python$BasePythonRunner$WriterThread$$anonfun$$$outer() {
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

      // $FF: synthetic method
      public BasePythonRunner org$apache$spark$api$python$BasePythonRunner$WriterThread$$$outer() {
         return this.$outer;
      }

      public WriterThread(BasePythonRunner<IN, OUT> env, SparkEnv worker, Socket inputIterator, Iterator<IN> partitionIndex, int context) {
         this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$worker = worker;
         this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$partitionIndex = partitionIndex;
         this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$context = context;
         if (BasePythonRunner.this == null) {
            throw null;
         } else {
            this.$outer = BasePythonRunner.this;
            super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"stdout writer for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BasePythonRunner.this.pythonExec()})));
            this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$_exception = null;
            this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$pythonIncludes = ((TraversableOnce)BasePythonRunner.this.org$apache$spark$api$python$BasePythonRunner$$funcs.flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Seq<String> apply(ChainedPythonFunctions x$2) {
                  return (Seq)x$2.funcs().flatMap(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Buffer<String> apply(PythonFunction x$3) {
                        return (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(x$3.pythonIncludes()).asScala();
                     }
                  }, Seq$.MODULE$.canBuildFrom());
               }
            }, Seq$.MODULE$.canBuildFrom())).<String>toSet();
            this.org$apache$spark$api$python$BasePythonRunner$WriterThread$$broadcastVars = (Seq)BasePythonRunner.this.org$apache$spark$api$python$BasePythonRunner$$funcs.flatMap(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Seq<Broadcast<PythonBroadcast>> apply(ChainedPythonFunctions x$4) {
                  return (Seq)x$4.funcs().flatMap(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final Buffer<Broadcast<PythonBroadcast>> apply(PythonFunction x$5) {
                        return (Buffer)JavaConverters$.MODULE$.asScalaBufferConverter(x$5.broadcastVars()).asScala();
                     }
                  }, Seq$.MODULE$.canBuildFrom());
               }
            }, Seq$.MODULE$.canBuildFrom());
            this.setDaemon(true);
         }
      }
   }

   public abstract class ReaderIterator implements Iterator<OUT> {
      public final DataInputStream org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream;
      public final BasePythonRunner<IN, OUT>.WriterThread org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread;
      private final long startTime;
      private final SparkEnv env;
      private final Socket worker;
      private final AtomicBoolean released;
      public final TaskContext org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context;
      private OUT nextObj;
      private boolean eos;
      private final PartialFunction<Throwable, OUT> handleException;
      // $FF: synthetic field
      public final BasePythonRunner $outer;

      public Iterator<OUT> seq() {
         return Iterator$class.seq(this);
      }

      public boolean isEmpty() {
         return Iterator$class.isEmpty(this);
      }

      public boolean isTraversableAgain() {
         return Iterator$class.isTraversableAgain(this);
      }

      public boolean hasDefiniteSize() {
         return Iterator$class.hasDefiniteSize(this);
      }

      public Iterator<OUT> take(int n) {
         return Iterator$class.take(this, n);
      }

      public Iterator<OUT> drop(int n) {
         return Iterator$class.drop(this, n);
      }

      public Iterator<OUT> slice(int from, int until) {
         return Iterator$class.slice(this, from, until);
      }

      public <B> Iterator<B> map(Function1<OUT, B> f) {
         return Iterator$class.map(this, f);
      }

      public <B> Iterator<B> $plus$plus(Function0<GenTraversableOnce<B>> that) {
         return Iterator$class.$plus$plus(this, that);
      }

      public <B> Iterator<B> flatMap(Function1<OUT, GenTraversableOnce<B>> f) {
         return Iterator$class.flatMap(this, f);
      }

      public Iterator<OUT> filter(Function1<OUT, Object> p) {
         return Iterator$class.filter(this, p);
      }

      public <B> boolean corresponds(GenTraversableOnce<B> that, Function2<OUT, B, Object> p) {
         return Iterator$class.corresponds(this, that, p);
      }

      public Iterator<OUT> withFilter(Function1<OUT, Object> p) {
         return Iterator$class.withFilter(this, p);
      }

      public Iterator<OUT> filterNot(Function1<OUT, Object> p) {
         return Iterator$class.filterNot(this, p);
      }

      public <B> Iterator<B> collect(PartialFunction<OUT, B> pf) {
         return Iterator$class.collect(this, pf);
      }

      public <B> Iterator<B> scanLeft(B z, Function2<B, OUT, B> op) {
         return Iterator$class.scanLeft(this, z, op);
      }

      public <B> Iterator<B> scanRight(B z, Function2<OUT, B, B> op) {
         return Iterator$class.scanRight(this, z, op);
      }

      public Iterator<OUT> takeWhile(Function1<OUT, Object> p) {
         return Iterator$class.takeWhile(this, p);
      }

      public Tuple2<Iterator<OUT>, Iterator<OUT>> partition(Function1<OUT, Object> p) {
         return Iterator$class.partition(this, p);
      }

      public Tuple2<Iterator<OUT>, Iterator<OUT>> span(Function1<OUT, Object> p) {
         return Iterator$class.span(this, p);
      }

      public Iterator<OUT> dropWhile(Function1<OUT, Object> p) {
         return Iterator$class.dropWhile(this, p);
      }

      public <B> Iterator<Tuple2<OUT, B>> zip(Iterator<B> that) {
         return Iterator$class.zip(this, that);
      }

      public <A1> Iterator<A1> padTo(int len, A1 elem) {
         return Iterator$class.padTo(this, len, elem);
      }

      public Iterator<Tuple2<OUT, Object>> zipWithIndex() {
         return Iterator$class.zipWithIndex(this);
      }

      public <B, A1, B1> Iterator<Tuple2<A1, B1>> zipAll(Iterator<B> that, A1 thisElem, B1 thatElem) {
         return Iterator$class.zipAll(this, that, thisElem, thatElem);
      }

      public <U> void foreach(Function1<OUT, U> f) {
         Iterator$class.foreach(this, f);
      }

      public boolean forall(Function1<OUT, Object> p) {
         return Iterator$class.forall(this, p);
      }

      public boolean exists(Function1<OUT, Object> p) {
         return Iterator$class.exists(this, p);
      }

      public boolean contains(Object elem) {
         return Iterator$class.contains(this, elem);
      }

      public Option<OUT> find(Function1<OUT, Object> p) {
         return Iterator$class.find(this, p);
      }

      public int indexWhere(Function1<OUT, Object> p) {
         return Iterator$class.indexWhere(this, p);
      }

      public <B> int indexOf(B elem) {
         return Iterator$class.indexOf(this, elem);
      }

      public BufferedIterator<OUT> buffered() {
         return Iterator$class.buffered(this);
      }

      public <B> Iterator<OUT>.GroupedIterator<B> grouped(int size) {
         return Iterator$class.grouped(this, size);
      }

      public <B> Iterator<OUT>.GroupedIterator<B> sliding(int size, int step) {
         return Iterator$class.sliding(this, size, step);
      }

      public int length() {
         return Iterator$class.length(this);
      }

      public Tuple2<Iterator<OUT>, Iterator<OUT>> duplicate() {
         return Iterator$class.duplicate(this);
      }

      public <B> Iterator<B> patch(int from, Iterator<B> patchElems, int replaced) {
         return Iterator$class.patch(this, from, patchElems, replaced);
      }

      public <B> void copyToArray(Object xs, int start, int len) {
         Iterator$class.copyToArray(this, xs, start, len);
      }

      public boolean sameElements(Iterator<?> that) {
         return Iterator$class.sameElements(this, that);
      }

      public Traversable<OUT> toTraversable() {
         return Iterator$class.toTraversable(this);
      }

      public Iterator<OUT> toIterator() {
         return Iterator$class.toIterator(this);
      }

      public Stream<OUT> toStream() {
         return Iterator$class.toStream(this);
      }

      public String toString() {
         return Iterator$class.toString(this);
      }

      public <B> int sliding$default$2() {
         return Iterator$class.sliding$default$2(this);
      }

      public List<OUT> reversed() {
         return TraversableOnce$class.reversed(this);
      }

      public int size() {
         return TraversableOnce$class.size(this);
      }

      public boolean nonEmpty() {
         return TraversableOnce$class.nonEmpty(this);
      }

      public int count(Function1<OUT, Object> p) {
         return TraversableOnce$class.count(this, p);
      }

      public <B> Option<B> collectFirst(PartialFunction<OUT, B> pf) {
         return TraversableOnce$class.collectFirst(this, pf);
      }

      public <B> B $div$colon(B z, Function2<B, OUT, B> op) {
         return (B)TraversableOnce$class.$div$colon(this, z, op);
      }

      public <B> B $colon$bslash(B z, Function2<OUT, B, B> op) {
         return (B)TraversableOnce$class.$colon$bslash(this, z, op);
      }

      public <B> B foldLeft(B z, Function2<B, OUT, B> op) {
         return (B)TraversableOnce$class.foldLeft(this, z, op);
      }

      public <B> B foldRight(B z, Function2<OUT, B, B> op) {
         return (B)TraversableOnce$class.foldRight(this, z, op);
      }

      public <B> B reduceLeft(Function2<B, OUT, B> op) {
         return (B)TraversableOnce$class.reduceLeft(this, op);
      }

      public <B> B reduceRight(Function2<OUT, B, B> op) {
         return (B)TraversableOnce$class.reduceRight(this, op);
      }

      public <B> Option<B> reduceLeftOption(Function2<B, OUT, B> op) {
         return TraversableOnce$class.reduceLeftOption(this, op);
      }

      public <B> Option<B> reduceRightOption(Function2<OUT, B, B> op) {
         return TraversableOnce$class.reduceRightOption(this, op);
      }

      public <A1> A1 reduce(Function2<A1, A1, A1> op) {
         return (A1)TraversableOnce$class.reduce(this, op);
      }

      public <A1> Option<A1> reduceOption(Function2<A1, A1, A1> op) {
         return TraversableOnce$class.reduceOption(this, op);
      }

      public <A1> A1 fold(A1 z, Function2<A1, A1, A1> op) {
         return (A1)TraversableOnce$class.fold(this, z, op);
      }

      public <B> B aggregate(Function0<B> z, Function2<B, OUT, B> seqop, Function2<B, B, B> combop) {
         return (B)TraversableOnce$class.aggregate(this, z, seqop, combop);
      }

      public <B> B sum(Numeric<B> num) {
         return (B)TraversableOnce$class.sum(this, num);
      }

      public <B> B product(Numeric<B> num) {
         return (B)TraversableOnce$class.product(this, num);
      }

      public <B> OUT min(Ordering<B> cmp) {
         return (OUT)TraversableOnce$class.min(this, cmp);
      }

      public <B> OUT max(Ordering<B> cmp) {
         return (OUT)TraversableOnce$class.max(this, cmp);
      }

      public <B> OUT maxBy(Function1<OUT, B> f, Ordering<B> cmp) {
         return (OUT)TraversableOnce$class.maxBy(this, f, cmp);
      }

      public <B> OUT minBy(Function1<OUT, B> f, Ordering<B> cmp) {
         return (OUT)TraversableOnce$class.minBy(this, f, cmp);
      }

      public <B> void copyToBuffer(Buffer<B> dest) {
         TraversableOnce$class.copyToBuffer(this, dest);
      }

      public <B> void copyToArray(Object xs, int start) {
         TraversableOnce$class.copyToArray(this, xs, start);
      }

      public <B> void copyToArray(Object xs) {
         TraversableOnce$class.copyToArray(this, xs);
      }

      public <B> Object toArray(ClassTag<B> evidence$1) {
         return TraversableOnce$class.toArray(this, evidence$1);
      }

      public List<OUT> toList() {
         return TraversableOnce$class.toList(this);
      }

      public Iterable<OUT> toIterable() {
         return TraversableOnce$class.toIterable(this);
      }

      public Seq<OUT> toSeq() {
         return TraversableOnce$class.toSeq(this);
      }

      public IndexedSeq<OUT> toIndexedSeq() {
         return TraversableOnce$class.toIndexedSeq(this);
      }

      public <B> Buffer<B> toBuffer() {
         return TraversableOnce$class.toBuffer(this);
      }

      public <B> Set<B> toSet() {
         return TraversableOnce$class.toSet(this);
      }

      public Vector<OUT> toVector() {
         return TraversableOnce$class.toVector(this);
      }

      public <Col> Col to(CanBuildFrom<Nothing$, OUT, Col> cbf) {
         return (Col)TraversableOnce$class.to(this, cbf);
      }

      public <T, U> scala.collection.immutable.Map<T, U> toMap(Predef.$less$colon$less<OUT, Tuple2<T, U>> ev) {
         return TraversableOnce$class.toMap(this, ev);
      }

      public String mkString(String start, String sep, String end) {
         return TraversableOnce$class.mkString(this, start, sep, end);
      }

      public String mkString(String sep) {
         return TraversableOnce$class.mkString(this, sep);
      }

      public String mkString() {
         return TraversableOnce$class.mkString(this);
      }

      public StringBuilder addString(StringBuilder b, String start, String sep, String end) {
         return TraversableOnce$class.addString(this, b, start, sep, end);
      }

      public StringBuilder addString(StringBuilder b, String sep) {
         return TraversableOnce$class.addString(this, b, sep);
      }

      public StringBuilder addString(StringBuilder b) {
         return TraversableOnce$class.addString(this, b);
      }

      private OUT nextObj() {
         return this.nextObj;
      }

      private void nextObj_$eq(OUT x$1) {
         this.nextObj = (OUT)x$1;
      }

      private boolean eos() {
         return this.eos;
      }

      private void eos_$eq(boolean x$1) {
         this.eos = x$1;
      }

      public boolean hasNext() {
         boolean var1;
         if (this.nextObj() == null) {
            if (this.eos()) {
               var1 = false;
            } else {
               this.nextObj_$eq(this.read());
               var1 = this.hasNext();
            }

            if (!var1) {
               var1 = false;
               return var1;
            }
         }

         var1 = true;
         return var1;
      }

      public OUT next() {
         Object var10000;
         if (this.hasNext()) {
            Object obj = this.nextObj();
            this.nextObj_$eq((Object)null);
            var10000 = obj;
         } else {
            var10000 = package$.MODULE$.Iterator().empty().next();
         }

         return (OUT)var10000;
      }

      public abstract OUT read();

      public void handleTimingData() {
         long bootTime = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readLong();
         long initTime = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readLong();
         long finishTime = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readLong();
         long boot = bootTime - this.startTime;
         long init = initTime - bootTime;
         long finish = finishTime - initTime;
         long total = finishTime - this.startTime;
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().logInfo(new Serializable(this, boot, init, finish, total) {
            public static final long serialVersionUID = 0L;
            private final long boot$1;
            private final long init$1;
            private final long finish$1;
            private final long total$1;

            public final String apply() {
               return (new StringOps(Predef$.MODULE$.augmentString("Times: total = %s, boot = %s, init = %s, finish = %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.total$1), BoxesRunTime.boxToLong(this.boot$1), BoxesRunTime.boxToLong(this.init$1), BoxesRunTime.boxToLong(this.finish$1)}));
            }

            public {
               this.boot$1 = boot$1;
               this.init$1 = init$1;
               this.finish$1 = finish$1;
               this.total$1 = total$1;
            }
         });
         long memoryBytesSpilled = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readLong();
         long diskBytesSpilled = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readLong();
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context.taskMetrics().incMemoryBytesSpilled(memoryBytesSpilled);
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context.taskMetrics().incDiskBytesSpilled(diskBytesSpilled);
      }

      public PythonException handlePythonException() {
         int exLength = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readInt();
         byte[] obj = new byte[exLength];
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readFully(obj);
         return new PythonException(new String(obj, StandardCharsets.UTF_8), (Exception)this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread.exception().getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Null$ apply() {
               return null;
            }
         }));
      }

      public void handleEndOfDataSection() {
         int numAccumulatorUpdates = this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readInt();
         RichInt$.MODULE$.to$extension0(Predef$.MODULE$.intWrapper(1), numAccumulatorUpdates).foreach$mVc$sp(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ReaderIterator $outer;

            public final void apply(int x$7) {
               this.apply$mcVI$sp(x$7);
            }

            public void apply$mcVI$sp(int x$7) {
               int updateLen = this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readInt();
               byte[] update = new byte[updateLen];
               this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readFully(update);
               this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().accumulator().add(update);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         if (this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream.readInt() == SpecialLengths$.MODULE$.END_OF_STREAM() && this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().org$apache$spark$api$python$BasePythonRunner$$reuseWorker) {
            this.env.releasePythonWorker(this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().pythonExec(), ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().envVars()).asScala()).toMap(Predef$.MODULE$.$conforms()), this.worker);
            this.released.set(true);
         }

         this.eos_$eq(true);
      }

      public PartialFunction<Throwable, OUT> handleException() {
         return this.handleException;
      }

      // $FF: synthetic method
      public BasePythonRunner org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer() {
         return this.$outer;
      }

      public ReaderIterator(BasePythonRunner<IN, OUT> stream, DataInputStream writerThread, BasePythonRunner<IN, OUT>.WriterThread startTime, long env, SparkEnv worker, Socket released, AtomicBoolean context) {
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$stream = stream;
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread = writerThread;
         this.startTime = startTime;
         this.env = env;
         this.worker = worker;
         this.released = released;
         this.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context = context;
         if (BasePythonRunner.this == null) {
            throw null;
         } else {
            this.$outer = BasePythonRunner.this;
            super();
            TraversableOnce$class.$init$(this);
            Iterator$class.$init$(this);
            this.eos = false;
            this.handleException = new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ReaderIterator $outer;

               public final <A1 extends Throwable, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
                  boolean var3 = false;
                  Exception var4 = null;
                  if (x1 instanceof Exception) {
                     var3 = true;
                     var4 = (Exception)x1;
                     if (this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context.isInterrupted()) {
                        this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().logDebug(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "Exception thrown after task interruption";
                           }
                        }, var4);
                        throw new TaskKilledException((String)this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context.getKillReason().getOrElse(new Serializable(this) {
                           public static final long serialVersionUID = 0L;

                           public final String apply() {
                              return "unknown reason";
                           }
                        }));
                     }
                  }

                  if (var3 && this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread.exception().isDefined()) {
                     this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Python worker exited unexpectedly (crashed)";
                        }
                     }, var4);
                     this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$$outer().logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "This may have been caused by a prior exception:";
                        }
                     }, (Throwable)this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread.exception().get());
                     throw (Throwable)this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread.exception().get();
                  } else if (x1 instanceof EOFException) {
                     EOFException var7 = (EOFException)x1;
                     throw new SparkException("Python worker exited unexpectedly (crashed)", var7);
                  } else {
                     Object var6 = var2.apply(x1);
                     return (B1)var6;
                  }
               }

               public final boolean isDefinedAt(Throwable x1) {
                  boolean var2 = false;
                  Object var3 = null;
                  boolean var5;
                  if (x1 instanceof Exception) {
                     var2 = true;
                     Exception var6 = (Exception)x1;
                     if (this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$context.isInterrupted()) {
                        var5 = true;
                        return var5;
                     }
                  }

                  if (var2 && this.$outer.org$apache$spark$api$python$BasePythonRunner$ReaderIterator$$writerThread.exception().isDefined()) {
                     var5 = true;
                  } else if (x1 instanceof EOFException) {
                     var5 = true;
                  } else {
                     var5 = false;
                  }

                  return var5;
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
      }
   }

   public class MonitorThread extends Thread {
      private final SparkEnv env;
      private final Socket worker;
      private final TaskContext context;
      private final long taskKillTimeout;
      // $FF: synthetic field
      public final BasePythonRunner $outer;

      private long taskKillTimeout() {
         return this.taskKillTimeout;
      }

      public void run() {
         while(!this.context.isInterrupted() && !this.context.isCompleted()) {
            Thread.sleep(2000L);
         }

         if (!this.context.isCompleted()) {
            Thread.sleep(this.taskKillTimeout());
            if (!this.context.isCompleted()) {
               try {
                  String taskName = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ".", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.context.partitionId()), BoxesRunTime.boxToLong(this.context.taskAttemptId())}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"in stage ", " (TID ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.context.stageId()), BoxesRunTime.boxToLong(this.context.taskAttemptId())}))).toString();
                  this.org$apache$spark$api$python$BasePythonRunner$MonitorThread$$$outer().logWarning(new Serializable(this, taskName) {
                     public static final long serialVersionUID = 0L;
                     private final String taskName$1;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Incomplete task ", " interrupted: Attempting to kill Python Worker"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.taskName$1}));
                     }

                     public {
                        this.taskName$1 = taskName$1;
                     }
                  });
                  this.env.destroyPythonWorker(this.org$apache$spark$api$python$BasePythonRunner$MonitorThread$$$outer().pythonExec(), ((TraversableOnce)JavaConverters$.MODULE$.mapAsScalaMapConverter(this.org$apache$spark$api$python$BasePythonRunner$MonitorThread$$$outer().envVars()).asScala()).toMap(Predef$.MODULE$.$conforms()), this.worker);
               } catch (Exception var3) {
                  this.org$apache$spark$api$python$BasePythonRunner$MonitorThread$$$outer().logError(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "Exception when trying to kill worker";
                     }
                  }, var3);
               }
            }
         }

      }

      // $FF: synthetic method
      public BasePythonRunner org$apache$spark$api$python$BasePythonRunner$MonitorThread$$$outer() {
         return this.$outer;
      }

      public MonitorThread(BasePythonRunner<IN, OUT> env, SparkEnv worker, Socket context) {
         this.env = env;
         this.worker = worker;
         this.context = context;
         if (BasePythonRunner.this == null) {
            throw null;
         } else {
            this.$outer = BasePythonRunner.this;
            super((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Worker Monitor for ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BasePythonRunner.this.pythonExec()})));
            this.taskKillTimeout = env.conf().getTimeAsMs("spark.python.task.killTimeout", "2s");
            this.setDaemon(true);
         }
      }
   }
}
