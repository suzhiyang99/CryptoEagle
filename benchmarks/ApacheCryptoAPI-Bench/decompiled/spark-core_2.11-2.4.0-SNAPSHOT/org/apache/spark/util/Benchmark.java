package org.apache.spark.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.commons.io.output.TeeOutputStream;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.ArrayBuffer$;
import scala.collection.mutable.ResizableArray;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.FiniteDuration;
import scala.math.Numeric;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

@ScalaSignature(
   bytes = "\u0006\u0001\r5b!B\u0001\u0003\u0001\u0011Q!!\u0003\"f]\u000eDW.\u0019:l\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0003\u0005\u0013\u0001\t\u0005\t\u0015!\u0003\u0015\u0003\u0011q\u0017-\\3\u0004\u0001A\u0011Q\u0003\u0007\b\u0003\u0019YI!aF\u0007\u0002\rA\u0013X\rZ3g\u0013\tI\"D\u0001\u0004TiJLgn\u001a\u0006\u0003/5A\u0001\u0002\b\u0001\u0003\u0002\u0003\u0006I!H\u0001\u0013m\u0006dW/Z:QKJLE/\u001a:bi&|g\u000e\u0005\u0002\r=%\u0011q$\u0004\u0002\u0005\u0019>tw\r\u0003\u0005\"\u0001\t\u0005\t\u0015!\u0003#\u0003-i\u0017N\u001c(v[&#XM]:\u0011\u00051\u0019\u0013B\u0001\u0013\u000e\u0005\rIe\u000e\u001e\u0005\tM\u0001\u0011\t\u0011)A\u0005O\u0005Qq/\u0019:nkB$\u0016.\\3\u0011\u0005!jS\"A\u0015\u000b\u0005)Z\u0013\u0001\u00033ve\u0006$\u0018n\u001c8\u000b\u00051j\u0011AC2p]\u000e,(O]3oi&\u0011a&\u000b\u0002\u000f\r&t\u0017\u000e^3EkJ\fG/[8o\u0011!\u0001\u0004A!A!\u0002\u00139\u0013aB7j]RKW.\u001a\u0005\te\u0001\u0011\t\u0011)A\u0005g\u0005\u0011r.\u001e;qkR\u0004VM]%uKJ\fG/[8o!\taA'\u0003\u00026\u001b\t9!i\\8mK\u0006t\u0007\u0002C\u001c\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u001d\u0002\r=,H\u000f];u!\ra\u0011hO\u0005\u0003u5\u0011aa\u00149uS>t\u0007C\u0001\u001fB\u001b\u0005i$B\u0001 @\u0003\tIwNC\u0001A\u0003\u0011Q\u0017M^1\n\u0005\tk$\u0001D(viB,Ho\u0015;sK\u0006l\u0007\"\u0002#\u0001\t\u0003)\u0015A\u0002\u001fj]&$h\b\u0006\u0005G\u0011&S5\nT'O!\t9\u0005!D\u0001\u0003\u0011\u0015\u00112\t1\u0001\u0015\u0011\u0015a2\t1\u0001\u001e\u0011\u001d\t3\t%AA\u0002\tBqAJ\"\u0011\u0002\u0003\u0007q\u0005C\u00041\u0007B\u0005\t\u0019A\u0014\t\u000fI\u001a\u0005\u0013!a\u0001g!9qg\u0011I\u0001\u0002\u0004A\u0004b\u0002)\u0001\u0005\u0004%\t!U\u0001\u000bE\u0016t7\r[7be.\u001cX#\u0001*\u0011\u0007MC&,D\u0001U\u0015\t)f+A\u0004nkR\f'\r\\3\u000b\u0005]k\u0011AC2pY2,7\r^5p]&\u0011\u0011\f\u0016\u0002\f\u0003J\u0014\u0018-\u001f\"vM\u001a,'\u000fE\u0002\\\u00037q!a\u0012/\b\ru\u0013\u0001\u0012\u0001\u0003_\u0003%\u0011UM\\2i[\u0006\u00148\u000e\u0005\u0002H?\u001a1\u0011A\u0001E\u0001\t\u0001\u001c\"aX\u0006\t\u000b\u0011{F\u0011\u00012\u0015\u0003y3A\u0001Z0\u0001K\n)A+[7feN\u00111m\u0003\u0005\tO\u000e\u0014)\u0019!C\u0001Q\u0006I\u0011\u000e^3sCRLwN\\\u000b\u0002E!A!n\u0019B\u0001B\u0003%!%\u0001\u0006ji\u0016\u0014\u0018\r^5p]\u0002BQ\u0001R2\u0005\u00021$\"!\\8\u0011\u00059\u001cW\"A0\t\u000b\u001d\\\u0007\u0019\u0001\u0012\t\u000fE\u001c\u0007\u0019!C\u0005e\u0006y\u0011mY2v[Vd\u0017\r^3e)&lW-F\u0001\u001e\u0011\u001d!8\r1A\u0005\nU\f1#Y2dk6,H.\u0019;fIRKW.Z0%KF$\"A^=\u0011\u000519\u0018B\u0001=\u000e\u0005\u0011)f.\u001b;\t\u000fi\u001c\u0018\u0011!a\u0001;\u0005\u0019\u0001\u0010J\u0019\t\rq\u001c\u0007\u0015)\u0003\u001e\u0003A\t7mY;nk2\fG/\u001a3US6,\u0007\u0005C\u0004\u007fG\u0002\u0007I\u0011\u0002:\u0002\u0013QLW.Z*uCJ$\b\"CA\u0001G\u0002\u0007I\u0011BA\u0002\u00035!\u0018.\\3Ti\u0006\u0014Ho\u0018\u0013fcR\u0019a/!\u0002\t\u000fi|\u0018\u0011!a\u0001;!9\u0011\u0011B2!B\u0013i\u0012A\u0003;j[\u0016\u001cF/\u0019:uA!9\u0011QB2\u0005\u0002\u0005=\u0011aC:uCJ$H+[7j]\u001e$\u0012A\u001e\u0005\b\u0003'\u0019G\u0011AA\b\u0003)\u0019Ho\u001c9US6Lgn\u001a\u0005\b\u0003/\u0019G\u0011AA\r\u0003%!x\u000e^1m)&lW\rF\u0001\u001e\r\u0019\tib\u0018!\u0002 \t!1)Y:f'\u001d\tYbCA\u0011\u0003O\u00012\u0001DA\u0012\u0013\r\t)#\u0004\u0002\b!J|G-^2u!\ra\u0011\u0011F\u0005\u0004\u0003Wi!\u0001D*fe&\fG.\u001b>bE2,\u0007B\u0003\n\u0002\u001c\tU\r\u0011\"\u0001\u00020U\tA\u0003\u0003\u0006\u00024\u0005m!\u0011#Q\u0001\nQ\tQA\\1nK\u0002B1\"a\u000e\u0002\u001c\tU\r\u0011\"\u0001\u0002:\u0005\u0011aM\\\u000b\u0003\u0003w\u0001R\u0001DA\u001f[ZL1!a\u0010\u000e\u0005%1UO\\2uS>t\u0017\u0007C\u0006\u0002D\u0005m!\u0011#Q\u0001\n\u0005m\u0012a\u00014oA!Q\u0011qIA\u000e\u0005+\u0007I\u0011\u00015\u0002\u00119,X.\u0013;feND!\"a\u0013\u0002\u001c\tE\t\u0015!\u0003#\u0003%qW/\\%uKJ\u001c\b\u0005C\u0004E\u00037!\t!a\u0014\u0015\u0011\u0005E\u00131KA+\u0003/\u00022A\\A\u000e\u0011\u0019\u0011\u0012Q\na\u0001)!A\u0011qGA'\u0001\u0004\tY\u0004C\u0004\u0002H\u00055\u0003\u0019\u0001\u0012\t\u0015\u0005m\u00131DA\u0001\n\u0003\ti&\u0001\u0003d_BLH\u0003CA)\u0003?\n\t'a\u0019\t\u0011I\tI\u0006%AA\u0002QA!\"a\u000e\u0002ZA\u0005\t\u0019AA\u001e\u0011%\t9%!\u0017\u0011\u0002\u0003\u0007!\u0005\u0003\u0006\u0002h\u0005m\u0011\u0013!C\u0001\u0003S\nabY8qs\u0012\"WMZ1vYR$\u0013'\u0006\u0002\u0002l)\u001aA#!\u001c,\u0005\u0005=\u0004\u0003BA9\u0003wj!!a\u001d\u000b\t\u0005U\u0014qO\u0001\nk:\u001c\u0007.Z2lK\u0012T1!!\u001f\u000e\u0003)\tgN\\8uCRLwN\\\u0005\u0005\u0003{\n\u0019HA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016D!\"!!\u0002\u001cE\u0005I\u0011AAB\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uII*\"!!\"+\t\u0005m\u0012Q\u000e\u0005\u000b\u0003\u0013\u000bY\"%A\u0005\u0002\u0005-\u0015AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0003\u001bS3AIA7\u0011)\t\t*a\u0007\u0002\u0002\u0013\u0005\u00131S\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005U\u0005\u0003BAL\u0003;k!!!'\u000b\u0007\u0005mu(\u0001\u0003mC:<\u0017bA\r\u0002\u001a\"I\u0011\u0011UA\u000e\u0003\u0003%\t\u0001[\u0001\raJ|G-^2u\u0003JLG/\u001f\u0005\u000b\u0003K\u000bY\"!A\u0005\u0002\u0005\u001d\u0016A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0005\u0003S\u000by\u000bE\u0002\r\u0003WK1!!,\u000e\u0005\r\te.\u001f\u0005\tu\u0006\r\u0016\u0011!a\u0001E!Q\u00111WA\u000e\u0003\u0003%\t%!.\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!a.\u0011\r\u0005e\u00161XAU\u001b\u00051\u0016bAA_-\nA\u0011\n^3sCR|'\u000f\u0003\u0006\u0002B\u0006m\u0011\u0011!C\u0001\u0003\u0007\f\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0004g\u0005\u0015\u0007\"\u0003>\u0002@\u0006\u0005\t\u0019AAU\u0011)\tI-a\u0007\u0002\u0002\u0013\u0005\u00131Z\u0001\tQ\u0006\u001c\bnQ8eKR\t!\u0005\u0003\u0006\u0002P\u0006m\u0011\u0011!C!\u0003#\f\u0001\u0002^8TiJLgn\u001a\u000b\u0003\u0003+C!\"!6\u0002\u001c\u0005\u0005I\u0011IAl\u0003\u0019)\u0017/^1mgR\u00191'!7\t\u0013i\f\u0019.!AA\u0002\u0005%v!CAo?\u0006\u0005\t\u0012AAp\u0003\u0011\u0019\u0015m]3\u0011\u00079\f\tOB\u0005\u0002\u001e}\u000b\t\u0011#\u0001\u0002dN1\u0011\u0011]As\u0003O\u0001\"\"a:\u0002nR\tYDIA)\u001b\t\tIOC\u0002\u0002l6\tqA];oi&lW-\u0003\u0003\u0002p\u0006%(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8og!9A)!9\u0005\u0002\u0005MHCAAp\u0011)\ty-!9\u0002\u0002\u0013\u0015\u0013\u0011\u001b\u0005\u000b\u0003s\f\t/!A\u0005\u0002\u0006m\u0018!B1qa2LH\u0003CA)\u0003{\fyP!\u0001\t\rI\t9\u00101\u0001\u0015\u0011!\t9$a>A\u0002\u0005m\u0002bBA$\u0003o\u0004\rA\t\u0005\u000b\u0005\u000b\t\t/!A\u0005\u0002\n\u001d\u0011aB;oCB\u0004H.\u001f\u000b\u0005\u0005\u0013\u0011\t\u0002\u0005\u0003\rs\t-\u0001c\u0002\u0007\u0003\u000eQ\tYDI\u0005\u0004\u0005\u001fi!A\u0002+va2,7\u0007\u0003\u0006\u0003\u0014\t\r\u0011\u0011!a\u0001\u0003#\n1\u0001\u001f\u00131\u0011)\u00119\"!9\u0002\u0002\u0013%!\u0011D\u0001\fe\u0016\fGMU3t_24X\r\u0006\u0002\u0003\u001cA!\u0011q\u0013B\u000f\u0013\u0011\u0011y\"!'\u0003\r=\u0013'.Z2u\r\u0019\u0011\u0019c\u0018!\u0003&\t1!+Z:vYR\u001crA!\t\f\u0003C\t9\u0003C\u0006\u0003*\t\u0005\"Q3A\u0005\u0002\t-\u0012!B1wO6\u001bXC\u0001B\u0017!\ra!qF\u0005\u0004\u0005ci!A\u0002#pk\ndW\rC\u0006\u00036\t\u0005\"\u0011#Q\u0001\n\t5\u0012AB1wO6\u001b\b\u0005C\u0006\u0003:\t\u0005\"Q3A\u0005\u0002\t-\u0012\u0001\u00032fgR\u0014\u0016\r^3\t\u0017\tu\"\u0011\u0005B\tB\u0003%!QF\u0001\nE\u0016\u001cHOU1uK\u0002B1B!\u0011\u0003\"\tU\r\u0011\"\u0001\u0003,\u00051!-Z:u\u001bND1B!\u0012\u0003\"\tE\t\u0015!\u0003\u0003.\u00059!-Z:u\u001bN\u0004\u0003b\u0002#\u0003\"\u0011\u0005!\u0011\n\u000b\t\u0005\u0017\u0012iEa\u0014\u0003RA\u0019aN!\t\t\u0011\t%\"q\ta\u0001\u0005[A\u0001B!\u000f\u0003H\u0001\u0007!Q\u0006\u0005\t\u0005\u0003\u00129\u00051\u0001\u0003.!Q\u00111\fB\u0011\u0003\u0003%\tA!\u0016\u0015\u0011\t-#q\u000bB-\u00057B!B!\u000b\u0003TA\u0005\t\u0019\u0001B\u0017\u0011)\u0011IDa\u0015\u0011\u0002\u0003\u0007!Q\u0006\u0005\u000b\u0005\u0003\u0012\u0019\u0006%AA\u0002\t5\u0002BCA4\u0005C\t\n\u0011\"\u0001\u0003`U\u0011!\u0011\r\u0016\u0005\u0005[\ti\u0007\u0003\u0006\u0002\u0002\n\u0005\u0012\u0013!C\u0001\u0005?B!\"!#\u0003\"E\u0005I\u0011\u0001B0\u0011)\t\tJ!\t\u0002\u0002\u0013\u0005\u00131\u0013\u0005\n\u0003C\u0013\t#!A\u0005\u0002!D!\"!*\u0003\"\u0005\u0005I\u0011\u0001B7)\u0011\tIKa\u001c\t\u0011i\u0014Y'!AA\u0002\tB!\"a-\u0003\"\u0005\u0005I\u0011IA[\u0011)\t\tM!\t\u0002\u0002\u0013\u0005!Q\u000f\u000b\u0004g\t]\u0004\"\u0003>\u0003t\u0005\u0005\t\u0019AAU\u0011)\tIM!\t\u0002\u0002\u0013\u0005\u00131\u001a\u0005\u000b\u0003\u001f\u0014\t#!A\u0005B\u0005E\u0007BCAk\u0005C\t\t\u0011\"\u0011\u0003\u0000Q\u00191G!!\t\u0013i\u0014i(!AA\u0002\u0005%v!\u0003BC?\u0006\u0005\t\u0012\u0001BD\u0003\u0019\u0011Vm];miB\u0019aN!#\u0007\u0013\t\rr,!A\t\u0002\t-5C\u0002BE\u0005\u001b\u000b9\u0003\u0005\u0007\u0002h\u00065(Q\u0006B\u0017\u0005[\u0011Y\u0005C\u0004E\u0005\u0013#\tA!%\u0015\u0005\t\u001d\u0005BCAh\u0005\u0013\u000b\t\u0011\"\u0012\u0002R\"Q\u0011\u0011 BE\u0003\u0003%\tIa&\u0015\u0011\t-#\u0011\u0014BN\u0005;C\u0001B!\u000b\u0003\u0016\u0002\u0007!Q\u0006\u0005\t\u0005s\u0011)\n1\u0001\u0003.!A!\u0011\tBK\u0001\u0004\u0011i\u0003\u0003\u0006\u0003\u0006\t%\u0015\u0011!CA\u0005C#BAa)\u0003(B!A\"\u000fBS!%a!Q\u0002B\u0017\u0005[\u0011i\u0003\u0003\u0006\u0003\u0014\t}\u0015\u0011!a\u0001\u0005\u0017B!Ba\u0006\u0003\n\u0006\u0005I\u0011\u0002B\r\u0011\u001d\u0011ik\u0018C\u0001\u0005_\u000b\u0001cZ3u!J|7-Z:t_Jt\u0015-\\3\u0015\u0003QAqAa-`\t\u0003\u0011y+\u0001\u0007hKRTe+T(T\u0013:4w\u000eC\u0005\u00038~\u000b\n\u0011\"\u0001\u0002\f\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIMB\u0011Ba/`#\u0003%\tA!0\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00135+\t\u0011yLK\u0002(\u0003[B\u0011Ba1`#\u0003%\tA!0\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00136\u0011%\u00119mXI\u0001\n\u0003\u0011I-A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HEN\u000b\u0003\u0005\u0017T3aMA7\u0011%\u0011ymXI\u0001\n\u0003\u0011\t.A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$HeN\u000b\u0003\u0005'T3\u0001OA7\u0011\u001d\u00119\u000e\u0001Q\u0001\nI\u000b1BY3oG\"l\u0017M]6tA!I!1\u001c\u0001C\u0002\u0013\u0005!Q\\\u0001\u0004_V$XC\u0001Bp!\ra$\u0011]\u0005\u0004\u0005Gl$a\u0003)sS:$8\u000b\u001e:fC6D\u0001Ba:\u0001A\u0003%!q\\\u0001\u0005_V$\b\u0005C\u0004\u0003l\u0002!\tA!<\u0002\u000f\u0005$GmQ1tKR1!q\u001eB|\u0005s$2A\u001eBy\u0011!\u0011\u0019P!;A\u0002\tU\u0018!\u00014\u0011\u000b1\tiD\t<\t\rI\u0011I\u000f1\u0001\u0015\u0011%\t9E!;\u0011\u0002\u0003\u0007!\u0005C\u0004\u0003~\u0002!\tAa@\u0002\u0019\u0005$G\rV5nKJ\u001c\u0015m]3\u0015\r\r\u00051\u0011BB\u0006)\r181\u0001\u0005\t\u0005g\u0014Y\u00101\u0001\u0004\u0006A1A\"!\u0010\u0004\bY\u0004\"aW2\t\rI\u0011Y\u00101\u0001\u0015\u0011%\t9Ea?\u0011\u0002\u0003\u0007!\u0005C\u0004\u0004\u0010\u0001!\t!a\u0004\u0002\u0007I,h\u000eC\u0004\u0004\u0014\u0001!\ta!\u0006\u0002\u000f5,\u0017m];sKR11qCB\u000f\u0007C!Ba!\u0007\u0004\u001cA\u00191L!\t\t\u0011\tM8\u0011\u0003a\u0001\u0007\u000bAqaa\b\u0004\u0012\u0001\u0007Q$A\u0002ok6Dqaa\t\u0004\u0012\u0001\u0007!%\u0001\tpm\u0016\u0014(/\u001b3f\u001dVl\u0017\n^3sg\"I1q\u0005\u0001\u0012\u0002\u0013\u0005\u00111R\u0001\u0012C\u0012$7)Y:fI\u0011,g-Y;mi\u0012\u0012\u0004\"CB\u0016\u0001E\u0005I\u0011AAF\u0003Y\tG\r\u001a+j[\u0016\u00148)Y:fI\u0011,g-Y;mi\u0012\u0012\u0004"
)
public class Benchmark {
   private final String name;
   public final long org$apache$spark$util$Benchmark$$valuesPerIteration;
   private final int minNumIters;
   private final FiniteDuration warmupTime;
   private final FiniteDuration minTime;
   private final boolean outputPerIteration;
   private final ArrayBuffer<Case> benchmarks;
   private final PrintStream out;

   public static Option<OutputStream> $lessinit$greater$default$7() {
      return Benchmark$.MODULE$.$lessinit$greater$default$7();
   }

   public static boolean $lessinit$greater$default$6() {
      return Benchmark$.MODULE$.$lessinit$greater$default$6();
   }

   public static FiniteDuration $lessinit$greater$default$5() {
      return Benchmark$.MODULE$.$lessinit$greater$default$5();
   }

   public static FiniteDuration $lessinit$greater$default$4() {
      return Benchmark$.MODULE$.$lessinit$greater$default$4();
   }

   public static int $lessinit$greater$default$3() {
      return Benchmark$.MODULE$.$lessinit$greater$default$3();
   }

   public static String getJVMOSInfo() {
      return Benchmark$.MODULE$.getJVMOSInfo();
   }

   public static String getProcessorName() {
      return Benchmark$.MODULE$.getProcessorName();
   }

   public ArrayBuffer<Case> benchmarks() {
      return this.benchmarks;
   }

   public PrintStream out() {
      return this.out;
   }

   public void addCase(String name, int numIters, Function1<Object, BoxedUnit> f) {
      this.addTimerCase(name, numIters, new Serializable(this, f) {
         public static final long serialVersionUID = 0L;
         private final Function1 f$1;

         public final void apply(Timer timer) {
            timer.startTiming();
            this.f$1.apply$mcVI$sp(timer.iteration());
            timer.stopTiming();
         }

         public {
            this.f$1 = f$1;
         }
      });
   }

   public int addCase$default$2() {
      return 0;
   }

   public void addTimerCase(String name, int numIters, Function1<Timer, BoxedUnit> f) {
      this.benchmarks().$plus$eq(new Case(name, f, numIters));
   }

   public int addTimerCase$default$2() {
      return 0;
   }

   public void run() {
      Predef$.MODULE$.require(this.benchmarks().nonEmpty());
      Predef$.MODULE$.println((new StringBuilder()).append((Object)"Running benchmark: ").append((Object)this.name).toString());
      ArrayBuffer results = (ArrayBuffer)this.benchmarks().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Benchmark $outer;

         public final Result apply(Case c) {
            Predef$.MODULE$.println((new StringBuilder()).append((Object)"  Running case: ").append((Object)c.name()).toString());
            return this.$outer.measure(this.$outer.org$apache$spark$util$Benchmark$$valuesPerIteration, c.numIters(), c.fn());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, ArrayBuffer$.MODULE$.canBuildFrom());
      Predef$.MODULE$.println();
      double firstBest = ((Result)results.head()).bestMs();
      this.out().println(Benchmark$.MODULE$.getJVMOSInfo());
      this.out().println(Benchmark$.MODULE$.getProcessorName());
      this.out().printf("%-40s %16s %12s %13s %10s\n", (new StringBuilder()).append((Object)this.name).append((Object)":").toString(), "Best/Avg Time(ms)", "Rate(M/s)", "Per Row(ns)", "Relative");
      this.out().println((new StringOps(Predef$.MODULE$.augmentString("-"))).$times(96));
      ((ResizableArray)results.zip(this.benchmarks(), ArrayBuffer$.MODULE$.canBuildFrom())).foreach(new Serializable(this, firstBest) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final Benchmark $outer;
         private final double firstBest$1;

         public final PrintStream apply(Tuple2<Result, Case> x0$1) {
            if (x0$1 != null) {
               Result result = (Result)x0$1._1();
               Case benchmark = (Case)x0$1._2();
               PrintStream var5 = this.$outer.out().printf("%-40s %16s %12s %13s %10s\n", benchmark.name(), (new StringOps(Predef$.MODULE$.augmentString("%5.0f / %4.0f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(result.bestMs()), BoxesRunTime.boxToDouble(result.avgMs())})), (new StringOps(Predef$.MODULE$.augmentString("%10.1f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(result.bestRate())})), (new StringOps(Predef$.MODULE$.augmentString("%6.1f"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble((double)1000 / result.bestRate())})), (new StringOps(Predef$.MODULE$.augmentString("%3.1fX"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToDouble(this.firstBest$1 / result.bestMs())})));
               return var5;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.firstBest$1 = firstBest$1;
            }
         }
      });
      this.out().println();
   }

   public Result measure(long num, int overrideNumIters, Function1<Timer, BoxedUnit> f) {
      System.gc();
      Deadline warmupDeadline = this.warmupTime.fromNow();

      while(!warmupDeadline.isOverdue()) {
         f.apply(new Timer(-1));
      }

      int minIters = overrideNumIters != 0 ? overrideNumIters : this.minNumIters;
      long minDuration = overrideNumIters != 0 ? 0L : this.minTime.toNanos();
      ArrayBuffer runTimes = (ArrayBuffer)ArrayBuffer$.MODULE$.apply(Nil$.MODULE$);

      int i;
      for(i = 0; i < minIters || BoxesRunTime.unboxToLong(runTimes.sum(Numeric.LongIsIntegral$.MODULE$)) < minDuration; ++i) {
         Timer timer = new Timer(i);
         f.apply(timer);
         long runTime = timer.totalTime();
         runTimes.$plus$eq(BoxesRunTime.boxToLong(runTime));
         if (this.outputPerIteration) {
            Predef$.MODULE$.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Iteration ", " took ", " microseconds"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(i), BoxesRunTime.boxToLong(runTime / 1000L)})));
         }
      }

      Predef$.MODULE$.println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"  Stopped after ", " iterations, ", " ms"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(i), BoxesRunTime.boxToLong(BoxesRunTime.unboxToLong(runTimes.sum(Numeric.LongIsIntegral$.MODULE$)) / 1000000L)})));
      long best = BoxesRunTime.unboxToLong(runTimes.min(Ordering.Long$.MODULE$));
      long avg = BoxesRunTime.unboxToLong(runTimes.sum(Numeric.LongIsIntegral$.MODULE$)) / (long)runTimes.size();
      return new Result((double)avg / (double)1000000.0F, (double)num / ((double)best / (double)1000.0F), (double)best / (double)1000000.0F);
   }

   public Benchmark(String name, long valuesPerIteration, int minNumIters, FiniteDuration warmupTime, FiniteDuration minTime, boolean outputPerIteration, Option<OutputStream> output) {
      this.name = name;
      this.org$apache$spark$util$Benchmark$$valuesPerIteration = valuesPerIteration;
      this.minNumIters = minNumIters;
      this.warmupTime = warmupTime;
      this.minTime = minTime;
      this.outputPerIteration = outputPerIteration;
      this.benchmarks = (ArrayBuffer)ArrayBuffer$.MODULE$.empty();
      this.out = output.isDefined() ? new PrintStream(new TeeOutputStream(System.out, (OutputStream)output.get())) : System.out;
   }

   public static class Timer {
      private final int iteration;
      private long accumulatedTime;
      private long timeStart;

      public int iteration() {
         return this.iteration;
      }

      private long accumulatedTime() {
         return this.accumulatedTime;
      }

      private void accumulatedTime_$eq(long x$1) {
         this.accumulatedTime = x$1;
      }

      private long timeStart() {
         return this.timeStart;
      }

      private void timeStart_$eq(long x$1) {
         this.timeStart = x$1;
      }

      public void startTiming() {
         Predef$.MODULE$.assert(this.timeStart() == 0L, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Already started timing.";
            }
         });
         this.timeStart_$eq(System.nanoTime());
      }

      public void stopTiming() {
         Predef$.MODULE$.assert(this.timeStart() != 0L, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Have not started timing.";
            }
         });
         this.accumulatedTime_$eq(this.accumulatedTime() + (System.nanoTime() - this.timeStart()));
         this.timeStart_$eq(0L);
      }

      public long totalTime() {
         Predef$.MODULE$.assert(this.timeStart() == 0L, new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Have not stopped timing.";
            }
         });
         return this.accumulatedTime();
      }

      public Timer(int iteration) {
         this.iteration = iteration;
         this.accumulatedTime = 0L;
         this.timeStart = 0L;
      }
   }

   public static class Case implements Product, Serializable {
      private final String name;
      private final Function1<Timer, BoxedUnit> fn;
      private final int numIters;

      public String name() {
         return this.name;
      }

      public Function1<Timer, BoxedUnit> fn() {
         return this.fn;
      }

      public int numIters() {
         return this.numIters;
      }

      public Case copy(String name, Function1<Timer, BoxedUnit> fn, int numIters) {
         return new Case(name, fn, numIters);
      }

      public String copy$default$1() {
         return this.name();
      }

      public Function1<Timer, BoxedUnit> copy$default$2() {
         return this.fn();
      }

      public int copy$default$3() {
         return this.numIters();
      }

      public String productPrefix() {
         return "Case";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = this.name();
               break;
            case 1:
               var10000 = this.fn();
               break;
            case 2:
               var10000 = BoxesRunTime.boxToInteger(this.numIters());
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
         return x$1 instanceof Case;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.anyHash(this.name()));
         var1 = Statics.mix(var1, Statics.anyHash(this.fn()));
         var1 = Statics.mix(var1, this.numIters());
         return Statics.finalizeHash(var1, 3);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var9;
         if (this != x$1) {
            label64: {
               boolean var3;
               if (x$1 instanceof Case) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label46: {
                     label55: {
                        Case var4 = (Case)x$1;
                        String var10000 = this.name();
                        String var5 = var4.name();
                        if (var10000 == null) {
                           if (var5 != null) {
                              break label55;
                           }
                        } else if (!var10000.equals(var5)) {
                           break label55;
                        }

                        Function1 var7 = this.fn();
                        Function1 var6 = var4.fn();
                        if (var7 == null) {
                           if (var6 != null) {
                              break label55;
                           }
                        } else if (!var7.equals(var6)) {
                           break label55;
                        }

                        if (this.numIters() == var4.numIters() && var4.canEqual(this)) {
                           var9 = true;
                           break label46;
                        }
                     }

                     var9 = false;
                  }

                  if (var9) {
                     break label64;
                  }
               }

               var9 = false;
               return var9;
            }
         }

         var9 = true;
         return var9;
      }

      public Case(String name, Function1<Timer, BoxedUnit> fn, int numIters) {
         this.name = name;
         this.fn = fn;
         this.numIters = numIters;
         Product$class.$init$(this);
      }
   }

   public static class Case$ extends AbstractFunction3<String, Function1<Timer, BoxedUnit>, Object, Case> implements Serializable {
      public static final Case$ MODULE$;

      static {
         new Case$();
      }

      public final String toString() {
         return "Case";
      }

      public Case apply(String name, Function1<Timer, BoxedUnit> fn, int numIters) {
         return new Case(name, fn, numIters);
      }

      public Option<Tuple3<String, Function1<Timer, BoxedUnit>, Object>> unapply(Case x$0) {
         return (Option<Tuple3<String, Function1<Timer, BoxedUnit>, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(x$0.name(), x$0.fn(), BoxesRunTime.boxToInteger(x$0.numIters()))));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public Case$() {
         MODULE$ = this;
      }
   }

   public static class Result implements Product, Serializable {
      private final double avgMs;
      private final double bestRate;
      private final double bestMs;

      public double avgMs() {
         return this.avgMs;
      }

      public double bestRate() {
         return this.bestRate;
      }

      public double bestMs() {
         return this.bestMs;
      }

      public Result copy(double avgMs, double bestRate, double bestMs) {
         return new Result(avgMs, bestRate, bestMs);
      }

      public double copy$default$1() {
         return this.avgMs();
      }

      public double copy$default$2() {
         return this.bestRate();
      }

      public double copy$default$3() {
         return this.bestMs();
      }

      public String productPrefix() {
         return "Result";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Double var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToDouble(this.avgMs());
               break;
            case 1:
               var10000 = BoxesRunTime.boxToDouble(this.bestRate());
               break;
            case 2:
               var10000 = BoxesRunTime.boxToDouble(this.bestMs());
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
         return x$1 instanceof Result;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.doubleHash(this.avgMs()));
         var1 = Statics.mix(var1, Statics.doubleHash(this.bestRate()));
         var1 = Statics.mix(var1, Statics.doubleHash(this.bestMs()));
         return Statics.finalizeHash(var1, 3);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var10000;
         if (this != x$1) {
            label52: {
               boolean var3;
               if (x$1 instanceof Result) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  Result var4 = (Result)x$1;
                  if (this.avgMs() == var4.avgMs() && this.bestRate() == var4.bestRate() && this.bestMs() == var4.bestMs() && var4.canEqual(this)) {
                     break label52;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public Result(double avgMs, double bestRate, double bestMs) {
         this.avgMs = avgMs;
         this.bestRate = bestRate;
         this.bestMs = bestMs;
         Product$class.$init$(this);
      }
   }

   public static class Result$ extends AbstractFunction3<Object, Object, Object, Result> implements Serializable {
      public static final Result$ MODULE$;

      static {
         new Result$();
      }

      public final String toString() {
         return "Result";
      }

      public Result apply(double avgMs, double bestRate, double bestMs) {
         return new Result(avgMs, bestRate, bestMs);
      }

      public Option<Tuple3<Object, Object, Object>> unapply(Result x$0) {
         return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToDouble(x$0.avgMs()), BoxesRunTime.boxToDouble(x$0.bestRate()), BoxesRunTime.boxToDouble(x$0.bestMs()))));
      }

      private Object readResolve() {
         return MODULE$;
      }

      public Result$() {
         MODULE$ = this;
      }
   }
}
