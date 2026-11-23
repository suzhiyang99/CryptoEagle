package org.apache.spark.storage;

import org.apache.spark.annotation.DeveloperApi;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.Tuple2$mcJJ$sp;
import scala.Tuple3;
import scala.collection.Iterable$;
import scala.collection.Iterator;
import scala.collection.Map$;
import scala.collection.Seq$;
import scala.collection.TraversableOnce;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.HashMap$;
import scala.collection.mutable.Map;
import scala.collection.mutable.MapLike;
import scala.math.Numeric;
import scala.math.package$;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction3;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;

/** @deprecated */
@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\rmb\u0001B\u0001\u0003\u0001-\u0011Qb\u0015;pe\u0006<Wm\u0015;biV\u001c(BA\u0002\u0005\u0003\u001d\u0019Ho\u001c:bO\u0016T!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001'\t\u0001A\u0002\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\u0005\t'\u0001\u0011)\u0019!C\u0001)\u0005q!\r\\8dW6\u000bg.Y4fe&#W#A\u000b\u0011\u0005Y9R\"\u0001\u0002\n\u0005a\u0011!A\u0004\"m_\u000e\\W*\u00198bO\u0016\u0014\u0018\n\u001a\u0005\t5\u0001\u0011\t\u0011)A\u0005+\u0005y!\r\\8dW6\u000bg.Y4fe&#\u0007\u0005\u0003\u0005\u001d\u0001\t\u0015\r\u0011\"\u0001\u001e\u0003%i\u0017\r_'f[>\u0014\u00180F\u0001\u001f!\tiq$\u0003\u0002!\u001d\t!Aj\u001c8h\u0011!\u0011\u0003A!A!\u0002\u0013q\u0012AC7bq6+Wn\u001c:zA!AA\u0005\u0001BC\u0002\u0013\u0005Q%\u0001\u0007nCb|e\u000eS3ba6+W.F\u0001'!\riqEH\u0005\u0003Q9\u0011aa\u00149uS>t\u0007\u0002\u0003\u0016\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0014\u0002\u001b5\f\u0007p\u00148IK\u0006\u0004X*Z7!\u0011!a\u0003A!b\u0001\n\u0003)\u0013!D7bq>3g\rS3ba6+W\u000e\u0003\u0005/\u0001\t\u0005\t\u0015!\u0003'\u00039i\u0017\r_(gM\"+\u0017\r]'f[\u0002BQ\u0001\r\u0001\u0005\u0002E\na\u0001P5oSRtD#\u0002\u001a4iU2\u0004C\u0001\f\u0001\u0011\u0015\u0019r\u00061\u0001\u0016\u0011\u0015ar\u00061\u0001\u001f\u0011\u0015!s\u00061\u0001'\u0011\u0015as\u00061\u0001'\u0011\u001dA\u0004A1A\u0005\ne\n!b\u0018:eI\ncwnY6t+\u0005Q\u0004\u0003B\u001eA\u0005\u0016k\u0011\u0001\u0010\u0006\u0003{y\nq!\\;uC\ndWM\u0003\u0002@\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005\u0005c$a\u0002%bg\"l\u0015\r\u001d\t\u0003\u001b\rK!\u0001\u0012\b\u0003\u0007%sG\u000f\u0005\u0003<\r\"[\u0015BA$=\u0005\ri\u0015\r\u001d\t\u0003-%K!A\u0013\u0002\u0003\u000f\tcwnY6JIB\u0011a\u0003T\u0005\u0003\u001b\n\u00111B\u00117pG.\u001cF/\u0019;vg\"1q\n\u0001Q\u0001\ni\n1b\u0018:eI\ncwnY6tA!9\u0011\u000b\u0001b\u0001\n\u0013\u0011\u0016!D0o_:\u0014F\r\u001a\"m_\u000e\\7/F\u0001T!\u0011Y\u0004\tS&\t\rU\u0003\u0001\u0015!\u0003T\u00039yfn\u001c8SI\u0012\u0014En\\2lg\u00022Aa\u0016\u0001E1\nq!\u000b\u001a3Ti>\u0014\u0018mZ3J]\u001a|7\u0003\u0002,\r3r\u0003\"!\u0004.\n\u0005ms!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001buK!A\u0018\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011\u00014&Q3A\u0005\u0002u\t1\"\\3n_JLXk]1hK\"A!M\u0016B\tB\u0003%a$\u0001\u0007nK6|'/_+tC\u001e,\u0007\u0005\u0003\u0005e-\nU\r\u0011\"\u0001\u001e\u0003%!\u0017n]6Vg\u0006<W\r\u0003\u0005g-\nE\t\u0015!\u0003\u001f\u0003)!\u0017n]6Vg\u0006<W\r\t\u0005\tQZ\u0013)\u001a!C\u0001S\u0006)A.\u001a<fYV\t!\u000e\u0005\u0002\u0017W&\u0011AN\u0001\u0002\r'R|'/Y4f\u0019\u00164X\r\u001c\u0005\t]Z\u0013\t\u0012)A\u0005U\u00061A.\u001a<fY\u0002BQ\u0001\r,\u0005\u0002A$B!]:ukB\u0011!OV\u0007\u0002\u0001!)\u0001m\u001ca\u0001=!)Am\u001ca\u0001=!)\u0001n\u001ca\u0001U\"9qOVA\u0001\n\u0003A\u0018\u0001B2paf$B!]={w\"9\u0001M\u001eI\u0001\u0002\u0004q\u0002b\u00023w!\u0003\u0005\rA\b\u0005\bQZ\u0004\n\u00111\u0001k\u0011\u001dih+%A\u0005\u0002y\fabY8qs\u0012\"WMZ1vYR$\u0013'F\u0001\u0000U\rq\u0012\u0011A\u0016\u0003\u0003\u0007\u0001B!!\u0002\u0002\u00105\u0011\u0011q\u0001\u0006\u0005\u0003\u0013\tY!A\u0005v]\u000eDWmY6fI*\u0019\u0011Q\u0002\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002\u0012\u0005\u001d!!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"A\u0011Q\u0003,\u0012\u0002\u0013\u0005a0\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u0013\u0005ea+%A\u0005\u0002\u0005m\u0011AD2paf$C-\u001a4bk2$HeM\u000b\u0003\u0003;Q3A[A\u0001\u0011%\t\tCVA\u0001\n\u0003\n\u0019#A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u0003\u0003K\u0001B!a\n\u000225\u0011\u0011\u0011\u0006\u0006\u0005\u0003W\ti#\u0001\u0003mC:<'BAA\u0018\u0003\u0011Q\u0017M^1\n\t\u0005M\u0012\u0011\u0006\u0002\u0007'R\u0014\u0018N\\4\t\u0013\u0005]b+!A\u0005\u0002\u0005e\u0012\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001\"\t\u0013\u0005ub+!A\u0005\u0002\u0005}\u0012A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0005\u0003\u0003\n9\u0005E\u0002\u000e\u0003\u0007J1!!\u0012\u000f\u0005\r\te.\u001f\u0005\n\u0003\u0013\nY$!AA\u0002\t\u000b1\u0001\u001f\u00132\u0011%\tiEVA\u0001\n\u0003\ny%A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\t\t\u0006\u0005\u0004\u0002T\u0005U\u0013\u0011I\u0007\u0002}%\u0019\u0011q\u000b \u0003\u0011%#XM]1u_JD\u0011\"a\u0017W\u0003\u0003%\t!!\u0018\u0002\u0011\r\fg.R9vC2$B!a\u0018\u0002fA\u0019Q\"!\u0019\n\u0007\u0005\rdBA\u0004C_>dW-\u00198\t\u0015\u0005%\u0013\u0011LA\u0001\u0002\u0004\t\t\u0005C\u0005\u0002jY\u000b\t\u0011\"\u0011\u0002l\u0005A\u0001.Y:i\u0007>$W\rF\u0001C\u0011%\tyGVA\u0001\n\u0003\n\t(\u0001\u0005u_N#(/\u001b8h)\t\t)\u0003C\u0005\u0002vY\u000b\t\u0011\"\u0011\u0002x\u00051Q-];bYN$B!a\u0018\u0002z!Q\u0011\u0011JA:\u0003\u0003\u0005\r!!\u0011\b\u0013\u0005u\u0004!!A\t\n\u0005}\u0014A\u0004*eIN#xN]1hK&sgm\u001c\t\u0004e\u0006\u0005e\u0001C,\u0001\u0003\u0003EI!a!\u0014\u000b\u0005\u0005\u0015Q\u0011/\u0011\u0011\u0005\u001d\u0015Q\u0012\u0010\u001fUFl!!!#\u000b\u0007\u0005-e\"A\u0004sk:$\u0018.\\3\n\t\u0005=\u0015\u0011\u0012\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u001c\u0004b\u0002\u0019\u0002\u0002\u0012\u0005\u00111\u0013\u000b\u0003\u0003\u007fB!\"a\u001c\u0002\u0002\u0006\u0005IQIA9\u0011)\tI*!!\u0002\u0002\u0013\u0005\u00151T\u0001\u0006CB\u0004H.\u001f\u000b\bc\u0006u\u0015qTAQ\u0011\u0019\u0001\u0017q\u0013a\u0001=!1A-a&A\u0002yAa\u0001[AL\u0001\u0004Q\u0007BCAS\u0003\u0003\u000b\t\u0011\"!\u0002(\u00069QO\\1qa2LH\u0003BAU\u0003c\u0003B!D\u0014\u0002,B1Q\"!,\u001f=)L1!a,\u000f\u0005\u0019!V\u000f\u001d7fg!I\u00111WAR\u0003\u0003\u0005\r!]\u0001\u0004q\u0012\u0002\u0004\"CA\\\u0001\t\u0007I\u0011BA]\u0003=y&\u000f\u001a3Ti>\u0014\u0018mZ3J]\u001a|WCAA^!\u0011Y\u0004IQ9\t\u0011\u0005}\u0006\u0001)A\u0005\u0003w\u000b\u0001c\u0018:eIN#xN]1hK&sgm\u001c\u0011\u0007\r\u0005\r\u0007\u0001RAc\u0005EquN\u001c*eIN#xN]1hK&sgm\\\n\u0006\u0003\u0003d\u0011\f\u0018\u0005\u000b\u0003\u0013\f\tM!e\u0001\n\u0003i\u0012aC8o\u0011\u0016\f\u0007/V:bO\u0016D1\"!4\u0002B\n\u0005\r\u0011\"\u0001\u0002P\u0006yqN\u001c%fCB,6/Y4f?\u0012*\u0017\u000f\u0006\u0003\u0002R\u0006]\u0007cA\u0007\u0002T&\u0019\u0011Q\u001b\b\u0003\tUs\u0017\u000e\u001e\u0005\n\u0003\u0013\nY-!AA\u0002yA!\"a7\u0002B\nE\t\u0015)\u0003\u001f\u00031yg\u000eS3baV\u001b\u0018mZ3!\u0011)\ty.!1\u0003\u0012\u0004%\t!H\u0001\r_\u001a4\u0007*Z1q+N\fw-\u001a\u0005\f\u0003G\f\tM!a\u0001\n\u0003\t)/\u0001\tpM\u001aDU-\u00199Vg\u0006<Wm\u0018\u0013fcR!\u0011\u0011[At\u0011%\tI%!9\u0002\u0002\u0003\u0007a\u0004\u0003\u0006\u0002l\u0006\u0005'\u0011#Q!\ny\tQb\u001c4g\u0011\u0016\f\u0007/V:bO\u0016\u0004\u0003\"\u00033\u0002B\nE\r\u0011\"\u0001\u001e\u0011-\t\t0!1\u0003\u0002\u0004%\t!a=\u0002\u001b\u0011L7o[+tC\u001e,w\fJ3r)\u0011\t\t.!>\t\u0013\u0005%\u0013q^A\u0001\u0002\u0004q\u0002\"\u00034\u0002B\nE\t\u0015)\u0003\u001f\u0011\u001d\u0001\u0014\u0011\u0019C\u0001\u0003w$\u0002\"!@\u0002\u0000\n\u0005!1\u0001\t\u0004e\u0006\u0005\u0007bBAe\u0003s\u0004\rA\b\u0005\b\u0003?\fI\u00101\u0001\u001f\u0011\u0019!\u0017\u0011 a\u0001=!Iq/!1\u0002\u0002\u0013\u0005!q\u0001\u000b\t\u0003{\u0014IAa\u0003\u0003\u000e!I\u0011\u0011\u001aB\u0003!\u0003\u0005\rA\b\u0005\n\u0003?\u0014)\u0001%AA\u0002yA\u0001\u0002\u001aB\u0003!\u0003\u0005\rA\b\u0005\t{\u0006\u0005\u0017\u0013!C\u0001}\"I\u0011QCAa#\u0003%\tA \u0005\n\u00033\t\t-%A\u0005\u0002yD!\"!\t\u0002B\u0006\u0005I\u0011IA\u0012\u0011)\t9$!1\u0002\u0002\u0013\u0005\u0011\u0011\b\u0005\u000b\u0003{\t\t-!A\u0005\u0002\tmA\u0003BA!\u0005;A\u0011\"!\u0013\u0003\u001a\u0005\u0005\t\u0019\u0001\"\t\u0015\u00055\u0013\u0011YA\u0001\n\u0003\ny\u0005\u0003\u0006\u0002\\\u0005\u0005\u0017\u0011!C\u0001\u0005G!B!a\u0018\u0003&!Q\u0011\u0011\nB\u0011\u0003\u0003\u0005\r!!\u0011\t\u0015\u0005%\u0014\u0011YA\u0001\n\u0003\nY\u0007\u0003\u0006\u0002p\u0005\u0005\u0017\u0011!C!\u0003cB!\"!\u001e\u0002B\u0006\u0005I\u0011\tB\u0017)\u0011\tyFa\f\t\u0015\u0005%#1FA\u0001\u0002\u0004\t\teB\u0005\u00034\u0001\t\t\u0011#\u0003\u00036\u0005\tbj\u001c8SI\u0012\u001cFo\u001c:bO\u0016LeNZ8\u0011\u0007I\u00149DB\u0005\u0002D\u0002\t\t\u0011#\u0003\u0003:M)!q\u0007B\u001e9BI\u0011qQAG=yq\u0012Q \u0005\ba\t]B\u0011\u0001B )\t\u0011)\u0004\u0003\u0006\u0002p\t]\u0012\u0011!C#\u0003cB!\"!'\u00038\u0005\u0005I\u0011\u0011B#)!\tiPa\u0012\u0003J\t-\u0003bBAe\u0005\u0007\u0002\rA\b\u0005\b\u0003?\u0014\u0019\u00051\u0001\u001f\u0011\u0019!'1\ta\u0001=!Q\u0011Q\u0015B\u001c\u0003\u0003%\tIa\u0014\u0015\t\tE#Q\u000b\t\u0005\u001b\u001d\u0012\u0019\u0006\u0005\u0004\u000e\u0003[sbD\b\u0005\u000b\u0003g\u0013i%!AA\u0002\u0005u\b\"\u0003B-\u0001\t\u0007I\u0011\u0002B.\u0003Iyfn\u001c8SI\u0012\u001cFo\u001c:bO\u0016LeNZ8\u0016\u0005\u0005u\b\u0002\u0003B0\u0001\u0001\u0006I!!@\u0002'}swN\u001c*eIN#xN]1hK&sgm\u001c\u0011\t\rA\u0002A\u0011\u0001B2)-\u0011$Q\rB5\u0005W\u0012iGa\u001c\t\u000f\t\u001d$\u0011\ra\u0001+\u0005!!-\\5e\u0011\u0019a\"\u0011\ra\u0001=!1AE!\u0019A\u0002\u0019Ba\u0001\fB1\u0001\u00041\u0003\u0002\u0003B9\u0005C\u0002\rAa\u001d\u0002\u001b%t\u0017\u000e^5bY\ncwnY6t!\u0019\t\u0019F!\u001eI\u0017&\u0011qI\u0010\u0005\b\u0005s\u0002A\u0011\u0001B>\u0003\u0019\u0011Gn\\2lgV\u0011!1\u000f\u0005\b\u0005\u007f\u0002A\u0011\u0001B>\u0003%\u0011H\r\u001a\"m_\u000e\\7\u000fC\u0004\u0003\u0004\u0002!\tA!\"\u0002\u001bI$GM\u00117pG.\u001c()_%e)\u0011\u0011\u0019Ha\"\t\u000f\t%%\u0011\u0011a\u0001\u0005\u0006)!\u000f\u001a3JI\"A!Q\u0012\u0001\u0005\u0002\u0011\u0011y)\u0001\u0005bI\u0012\u0014En\\2l)\u0019\t\tN!%\u0003\u0016\"9!1\u0013BF\u0001\u0004A\u0015a\u00022m_\u000e\\\u0017\n\u001a\u0005\b\u0005/\u0013Y\t1\u0001L\u0003-\u0011Gn\\2l'R\fG/^:\t\u0011\tm\u0005\u0001\"\u0001\u0005\u0005;\u000b1\"\u001e9eCR,'\t\\8dWR1\u0011\u0011\u001bBP\u0005CCqAa%\u0003\u001a\u0002\u0007\u0001\nC\u0004\u0003\u0018\ne\u0005\u0019A&\t\u0011\t\u0015\u0006\u0001\"\u0001\u0005\u0005O\u000b1B]3n_Z,'\t\\8dWR!!\u0011\u0016BV!\riqe\u0013\u0005\b\u0005'\u0013\u0019\u000b1\u0001I\u0011\u001d\u0011y\u000b\u0001C\u0001\u0005c\u000bQbY8oi\u0006Lgn\u001d\"m_\u000e\\G\u0003BA0\u0005gCqAa%\u0003.\u0002\u0007\u0001\nC\u0004\u00038\u0002!\tA!/\u0002\u0011\u001d,GO\u00117pG.$BA!+\u0003<\"9!1\u0013B[\u0001\u0004A\u0005b\u0002B`\u0001\u0011\u0005\u0011\u0011H\u0001\n]Vl'\t\\8dWNDqAa1\u0001\t\u0003\tI$\u0001\u0007ok6\u0014F\r\u001a\"m_\u000e\\7\u000fC\u0004\u0003H\u0002!\tA!3\u0002!9,XN\u00153e\u00052|7m[:Cs&#Gc\u0001\"\u0003L\"9!\u0011\u0012Bc\u0001\u0004\u0011\u0005B\u0002Bh\u0001\u0011\u0005Q$\u0001\u0004nCblU-\u001c\u0005\u0007\u0005'\u0004A\u0011A\u000f\u0002\u00195,WNU3nC&t\u0017N\\4\t\r\t]\u0007\u0001\"\u0001\u001e\u0003%\u0019\u0017m\u00195f'&TX\r\u0003\u0004\u0003\\\u0002!\t!H\u0001\b[\u0016lWk]3e\u0011\u0019\u0011y\u000e\u0001C\u0001K\u0005\u0011rN\u001c%fCBlU-\u001c*f[\u0006Lg.\u001b8h\u0011\u0019\u0011\u0019\u000f\u0001C\u0001K\u0005\u0019rN\u001a4IK\u0006\u0004X*Z7SK6\f\u0017N\\5oO\"1!q\u001d\u0001\u0005\u0002\u0015\nQb\u001c8IK\u0006\u0004X*Z7Vg\u0016$\u0007B\u0002Bv\u0001\u0011\u0005Q%\u0001\bpM\u001aDU-\u00199NK6,6/\u001a3\t\r\t=\b\u0001\"\u0001&\u0003=yg\u000eS3ba\u000e\u000b7\r[3TSj,\u0007B\u0002Bz\u0001\u0011\u0005Q%\u0001\tpM\u001aDU-\u00199DC\u000eDWmU5{K\"1!q\u001f\u0001\u0005\u0002u\t\u0001\u0002Z5tWV\u001bX\r\u001a\u0005\b\u0005w\u0004A\u0011\u0001B\u007f\u00031iW-\\+tK\u0012\u0014\u0015P\u00153e)\rq\"q \u0005\b\u0005\u0013\u0013I\u00101\u0001C\u0011\u001d\u0019\u0019\u0001\u0001C\u0001\u0007\u000b\tQ\u0002Z5tWV\u001bX\r\u001a\"z%\u0012$Gc\u0001\u0010\u0004\b!9!\u0011RB\u0001\u0001\u0004\u0011\u0005bBB\u0006\u0001\u0011\u00051QB\u0001\u0010e\u0012$7\u000b^8sC\u001e,G*\u001a<fYR!1qBB\t!\riqE\u001b\u0005\b\u0005\u0013\u001bI\u00011\u0001C\u0011\u001d\u0019)\u0002\u0001C\u0005\u0007/\t\u0011#\u001e9eCR,7\u000b^8sC\u001e,\u0017J\u001c4p)\u0019\t\tn!\u0007\u0004\u001c!9!1SB\n\u0001\u0004A\u0005bBB\u000f\u0007'\u0001\raS\u0001\u000f]\u0016<(\t\\8dWN#\u0018\r^;tQ\u001d\u00011\u0011EB\u0014\u0007W\u00012!DB\u0012\u0013\r\u0019)C\u0004\u0002\u000bI\u0016\u0004(/Z2bi\u0016$\u0017EAB\u0015\u0003y\"\u0006.[:!G2\f7o\u001d\u0011nCf\u0004#-\u001a\u0011sK6|g/\u001a3!_J\u0004S.\u00193fAA\u0014\u0018N^1uK\u0002Jg\u000eI1!MV$XO]3!e\u0016dW-Y:f]\u0005\u00121QF\u0001\u0006e9\u0012d\u0006\r\u0015\u0004\u0001\rE\u0002\u0003BB\u001a\u0007oi!a!\u000e\u000b\u0007\u00055A!\u0003\u0003\u0004:\rU\"\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public class StorageStatus {
   private final BlockManagerId blockManagerId;
   private final long maxMemory;
   private final Option<Object> maxOnHeapMem;
   private final Option<Object> maxOffHeapMem;
   private final HashMap<Object, Map<BlockId, BlockStatus>> _rddBlocks;
   private final HashMap<BlockId, BlockStatus> _nonRddBlocks;
   private final HashMap<Object, RddStorageInfo> org$apache$spark$storage$StorageStatus$$_rddStorageInfo;
   private final NonRddStorageInfo org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo;
   private volatile RddStorageInfo$ org$apache$spark$storage$StorageStatus$$RddStorageInfo$module;
   private volatile NonRddStorageInfo$ org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module;

   private RddStorageInfo$ org$apache$spark$storage$StorageStatus$$RddStorageInfo$lzycompute() {
      synchronized(this) {
         if (this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$module == null) {
            this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$module = new RddStorageInfo$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$module;
   }

   private NonRddStorageInfo$ org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$lzycompute() {
      synchronized(this) {
         if (this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module == null) {
            this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module = new NonRddStorageInfo$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module;
   }

   public BlockManagerId blockManagerId() {
      return this.blockManagerId;
   }

   public long maxMemory() {
      return this.maxMemory;
   }

   public Option<Object> maxOnHeapMem() {
      return this.maxOnHeapMem;
   }

   public Option<Object> maxOffHeapMem() {
      return this.maxOffHeapMem;
   }

   private HashMap<Object, Map<BlockId, BlockStatus>> _rddBlocks() {
      return this._rddBlocks;
   }

   private HashMap<BlockId, BlockStatus> _nonRddBlocks() {
      return this._nonRddBlocks;
   }

   public RddStorageInfo$ org$apache$spark$storage$StorageStatus$$RddStorageInfo() {
      return this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$module == null ? this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$lzycompute() : this.org$apache$spark$storage$StorageStatus$$RddStorageInfo$module;
   }

   public HashMap<Object, RddStorageInfo> org$apache$spark$storage$StorageStatus$$_rddStorageInfo() {
      return this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo;
   }

   public NonRddStorageInfo$ org$apache$spark$storage$StorageStatus$$NonRddStorageInfo() {
      return this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module == null ? this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$lzycompute() : this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo$module;
   }

   public NonRddStorageInfo org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo() {
      return this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo;
   }

   public scala.collection.Map<BlockId, BlockStatus> blocks() {
      return this._nonRddBlocks().$plus$plus(this.rddBlocks());
   }

   public scala.collection.Map<BlockId, BlockStatus> rddBlocks() {
      return (scala.collection.Map)this._rddBlocks().flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Map<BlockId, BlockStatus> apply(Tuple2<Object, Map<BlockId, BlockStatus>> x0$2) {
            if (x0$2 != null) {
               Map blocks = (Map)x0$2._2();
               return blocks;
            } else {
               throw new MatchError(x0$2);
            }
         }
      }, HashMap$.MODULE$.canBuildFrom());
   }

   public scala.collection.Map<BlockId, BlockStatus> rddBlocksById(int rddId) {
      return (scala.collection.Map)this._rddBlocks().getOrElse(BoxesRunTime.boxToInteger(rddId), new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final scala.collection.immutable.Map<BlockId, Nothing$> apply() {
            return Map$.MODULE$.empty();
         }
      });
   }

   public void addBlock(BlockId blockId, BlockStatus blockStatus) {
      this.updateStorageInfo(blockId, blockStatus);
      if (blockId instanceof RDDBlockId) {
         RDDBlockId var4 = (RDDBlockId)blockId;
         int rddId = var4.rddId();
         ((MapLike)this._rddBlocks().getOrElseUpdate(BoxesRunTime.boxToInteger(rddId), new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final HashMap<BlockId, BlockStatus> apply() {
               return new HashMap<BlockId, BlockStatus>();
            }
         })).update(blockId, blockStatus);
         BoxedUnit var6 = BoxedUnit.UNIT;
      } else {
         this._nonRddBlocks().update(blockId, blockStatus);
         BoxedUnit var7 = BoxedUnit.UNIT;
      }

   }

   public void updateBlock(BlockId blockId, BlockStatus blockStatus) {
      this.addBlock(blockId, blockStatus);
   }

   public Option<BlockStatus> removeBlock(BlockId blockId) {
      this.updateStorageInfo(blockId, BlockStatus$.MODULE$.empty());
      Object var5;
      if (blockId instanceof RDDBlockId) {
         RDDBlockId var3 = (RDDBlockId)blockId;
         int rddId = var3.rddId();
         Object var7;
         if (this._rddBlocks().contains(BoxesRunTime.boxToInteger(rddId))) {
            Option removed = ((MapLike)this._rddBlocks().apply(BoxesRunTime.boxToInteger(rddId))).remove(blockId);
            if (((scala.collection.MapLike)this._rddBlocks().apply(BoxesRunTime.boxToInteger(rddId))).isEmpty()) {
               this._rddBlocks().remove(BoxesRunTime.boxToInteger(rddId));
            } else {
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }

            var7 = removed;
         } else {
            var7 = None$.MODULE$;
         }

         var5 = var7;
      } else {
         var5 = this._nonRddBlocks().remove(blockId);
      }

      return (Option<BlockStatus>)var5;
   }

   public boolean containsBlock(BlockId blockId) {
      boolean var5;
      if (blockId instanceof RDDBlockId) {
         RDDBlockId var3 = (RDDBlockId)blockId;
         int rddId = var3.rddId();
         var5 = this._rddBlocks().get(BoxesRunTime.boxToInteger(rddId)).exists(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$1;

            public final boolean apply(Map<BlockId, BlockStatus> x$1) {
               return x$1.contains(this.blockId$1);
            }

            public {
               this.blockId$1 = blockId$1;
            }
         });
      } else {
         var5 = this._nonRddBlocks().contains(blockId);
      }

      return var5;
   }

   public Option<BlockStatus> getBlock(BlockId blockId) {
      Option var5;
      if (blockId instanceof RDDBlockId) {
         RDDBlockId var3 = (RDDBlockId)blockId;
         int rddId = var3.rddId();
         var5 = this._rddBlocks().get(BoxesRunTime.boxToInteger(rddId)).flatMap(new Serializable(this, blockId) {
            public static final long serialVersionUID = 0L;
            private final BlockId blockId$2;

            public final Option<BlockStatus> apply(Map<BlockId, BlockStatus> x$2) {
               return x$2.get(this.blockId$2);
            }

            public {
               this.blockId$2 = blockId$2;
            }
         });
      } else {
         var5 = this._nonRddBlocks().get(blockId);
      }

      return var5;
   }

   public int numBlocks() {
      return this._nonRddBlocks().size() + this.numRddBlocks();
   }

   public int numRddBlocks() {
      return BoxesRunTime.unboxToInt(((TraversableOnce)this._rddBlocks().values().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Map<BlockId, BlockStatus> x$3) {
            return x$3.size();
         }
      }, Iterable$.MODULE$.canBuildFrom())).sum(Numeric.IntIsIntegral$.MODULE$));
   }

   public int numRddBlocksById(int rddId) {
      return BoxesRunTime.unboxToInt(this._rddBlocks().get(BoxesRunTime.boxToInteger(rddId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply(Map<BlockId, BlockStatus> x$4) {
            return x$4.size();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return 0;
         }
      }));
   }

   public long maxMem() {
      return this.maxMemory();
   }

   public long memRemaining() {
      return this.maxMem() - this.memUsed();
   }

   public long cacheSize() {
      return BoxesRunTime.unboxToLong(this.onHeapCacheSize().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      })) + BoxesRunTime.unboxToLong(this.offHeapCacheSize().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }));
   }

   public long memUsed() {
      return BoxesRunTime.unboxToLong(this.onHeapMemUsed().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      })) + BoxesRunTime.unboxToLong(this.offHeapMemUsed().getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }));
   }

   public Option<Object> onHeapMemRemaining() {
      return this.maxOnHeapMem().<Object>flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final Option<Object> apply(long m) {
            return this.$outer.onHeapMemUsed().<Object>map(new Serializable(this, m) {
               public static final long serialVersionUID = 0L;
               private final long m$1;

               public final long apply(long o) {
                  return this.apply$mcJJ$sp(o);
               }

               public long apply$mcJJ$sp(long o) {
                  return this.m$1 - o;
               }

               public {
                  this.m$1 = m$1;
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
   }

   public Option<Object> offHeapMemRemaining() {
      return this.maxOffHeapMem().<Object>flatMap(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final Option<Object> apply(long m) {
            return this.$outer.offHeapMemUsed().<Object>map(new Serializable(this, m) {
               public static final long serialVersionUID = 0L;
               private final long m$2;

               public final long apply(long o) {
                  return this.apply$mcJJ$sp(o);
               }

               public long apply$mcJJ$sp(long o) {
                  return this.m$2 - o;
               }

               public {
                  this.m$2 = m$2;
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
   }

   public Option<Object> onHeapMemUsed() {
      return this.onHeapCacheSize().<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final long apply(long x$5) {
            return this.apply$mcJJ$sp(x$5);
         }

         public long apply$mcJJ$sp(long x$5) {
            return x$5 + this.$outer.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().onHeapUsage();
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

   public Option<Object> offHeapMemUsed() {
      return this.offHeapCacheSize().<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final long apply(long x$6) {
            return this.apply$mcJJ$sp(x$6);
         }

         public long apply$mcJJ$sp(long x$6) {
            return x$6 + this.$outer.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().offHeapUsage();
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

   public Option<Object> onHeapCacheSize() {
      return this.maxOnHeapMem().<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final long apply(long x$7) {
            return this.apply$mcJJ$sp(x$7);
         }

         public long apply$mcJJ$sp(long x$7) {
            return BoxesRunTime.unboxToLong(((TraversableOnce)this.$outer.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().collect(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final <A1 extends Tuple2<Object, RddStorageInfo>, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
                  Object var5;
                  if (x1 != null) {
                     RddStorageInfo storageInfo = (RddStorageInfo)x1._2();
                     if (!storageInfo.level().useOffHeap()) {
                        var5 = BoxesRunTime.boxToLong(storageInfo.memoryUsage());
                        return (B1)var5;
                     }
                  }

                  var5 = var2.apply(x1);
                  return (B1)var5;
               }

               public final boolean isDefinedAt(Tuple2<Object, RddStorageInfo> x1) {
                  boolean var4;
                  if (x1 != null) {
                     RddStorageInfo storageInfo = (RddStorageInfo)x1._2();
                     if (!storageInfo.level().useOffHeap()) {
                        var4 = true;
                        return var4;
                     }
                  }

                  var4 = false;
                  return var4;
               }
            }, scala.collection.mutable.Iterable$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
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

   public Option<Object> offHeapCacheSize() {
      return this.maxOffHeapMem().<Object>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final long apply(long x$8) {
            return this.apply$mcJJ$sp(x$8);
         }

         public long apply$mcJJ$sp(long x$8) {
            return BoxesRunTime.unboxToLong(((TraversableOnce)this.$outer.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().collect(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final <A1 extends Tuple2<Object, RddStorageInfo>, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
                  Object var5;
                  if (x2 != null) {
                     RddStorageInfo storageInfo = (RddStorageInfo)x2._2();
                     if (storageInfo.level().useOffHeap()) {
                        var5 = BoxesRunTime.boxToLong(storageInfo.memoryUsage());
                        return (B1)var5;
                     }
                  }

                  var5 = var2.apply(x2);
                  return (B1)var5;
               }

               public final boolean isDefinedAt(Tuple2<Object, RddStorageInfo> x2) {
                  boolean var4;
                  if (x2 != null) {
                     RddStorageInfo storageInfo = (RddStorageInfo)x2._2();
                     if (storageInfo.level().useOffHeap()) {
                        var4 = true;
                        return var4;
                     }
                  }

                  var4 = false;
                  return var4;
               }
            }, scala.collection.mutable.Iterable$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
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

   public long diskUsed() {
      return this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().diskUsage() + BoxesRunTime.unboxToLong(((TraversableOnce)this._rddBlocks().keys().toSeq().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final long apply(int rddId) {
            return this.apply$mcJI$sp(rddId);
         }

         public long apply$mcJI$sp(int rddId) {
            return this.$outer.diskUsedByRdd(rddId);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, Seq$.MODULE$.canBuildFrom())).sum(Numeric.LongIsIntegral$.MODULE$));
   }

   public long memUsedByRdd(int rddId) {
      return BoxesRunTime.unboxToLong(this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().get(BoxesRunTime.boxToInteger(rddId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(RddStorageInfo x$9) {
            return x$9.memoryUsage();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }));
   }

   public long diskUsedByRdd(int rddId) {
      return BoxesRunTime.unboxToLong(this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().get(BoxesRunTime.boxToInteger(rddId)).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply(RddStorageInfo x$10) {
            return x$10.diskUsage();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return 0L;
         }
      }));
   }

   public Option<StorageLevel> rddStorageLevel(int rddId) {
      return this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().get(BoxesRunTime.boxToInteger(rddId)).<StorageLevel>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final StorageLevel apply(RddStorageInfo x$11) {
            return x$11.level();
         }
      });
   }

   private void updateStorageInfo(BlockId blockId, BlockStatus newBlockStatus) {
      BlockStatus oldBlockStatus = (BlockStatus)this.getBlock(blockId).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final BlockStatus apply() {
            return BlockStatus$.MODULE$.empty();
         }
      });
      long changeInMem = newBlockStatus.memSize() - oldBlockStatus.memSize();
      long changeInDisk = newBlockStatus.diskSize() - oldBlockStatus.diskSize();
      StorageLevel level = newBlockStatus.storageLevel();
      Object var14;
      if (blockId instanceof RDDBlockId) {
         RDDBlockId var12 = (RDDBlockId)blockId;
         int rddId = var12.rddId();
         var14 = (Tuple2)this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().get(BoxesRunTime.boxToInteger(rddId)).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<Object, Object> apply(RddStorageInfo x0$3) {
               if (x0$3 != null) {
                  long mem = x0$3.memoryUsage();
                  long disk = x0$3.diskUsage();
                  Tuple2$mcJJ$sp var7 = new Tuple2$mcJJ$sp(mem, disk);
                  return var7;
               } else {
                  throw new MatchError(x0$3);
               }
            }
         }).getOrElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final Tuple2<Object, Object> apply() {
               return new Tuple2$mcJJ$sp(0L, 0L);
            }
         });
      } else if (level.useOffHeap()) {
         if (!level.useOffHeap()) {
            throw new MatchError(blockId);
         }

         var14 = new Tuple2$mcJJ$sp(this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().offHeapUsage(), this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().diskUsage());
      } else {
         var14 = new Tuple2$mcJJ$sp(this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().onHeapUsage(), this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().diskUsage());
      }

      if (var14 != null) {
         long oldMem = ((Tuple2)var14)._1$mcJ$sp();
         long oldDisk = ((Tuple2)var14)._2$mcJ$sp();
         Tuple2$mcJJ$sp var19 = new Tuple2$mcJJ$sp(oldMem, oldDisk);
         long oldMem = ((Tuple2)var19)._1$mcJ$sp();
         long oldDisk = ((Tuple2)var19)._2$mcJ$sp();
         long newMem = package$.MODULE$.max(oldMem + changeInMem, 0L);
         long newDisk = package$.MODULE$.max(oldDisk + changeInDisk, 0L);
         if (blockId instanceof RDDBlockId) {
            RDDBlockId var29 = (RDDBlockId)blockId;
            int rddId = var29.rddId();
            if (newMem + newDisk == 0L) {
               this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().remove(BoxesRunTime.boxToInteger(rddId));
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo().update(BoxesRunTime.boxToInteger(rddId), this.org$apache$spark$storage$StorageStatus$$RddStorageInfo().apply(newMem, newDisk, level));
               BoxedUnit var32 = BoxedUnit.UNIT;
            }
         } else {
            if (level.useOffHeap()) {
               this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().offHeapUsage_$eq(newMem);
            } else {
               this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().onHeapUsage_$eq(newMem);
            }

            this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo().diskUsage_$eq(newDisk);
            BoxedUnit var31 = BoxedUnit.UNIT;
         }

      } else {
         throw new MatchError(var14);
      }
   }

   public StorageStatus(BlockManagerId blockManagerId, long maxMemory, Option<Object> maxOnHeapMem, Option<Object> maxOffHeapMem) {
      this.blockManagerId = blockManagerId;
      this.maxMemory = maxMemory;
      this.maxOnHeapMem = maxOnHeapMem;
      this.maxOffHeapMem = maxOffHeapMem;
      this._rddBlocks = new HashMap<Object, Map<BlockId, BlockStatus>>();
      this._nonRddBlocks = new HashMap<BlockId, BlockStatus>();
      this.org$apache$spark$storage$StorageStatus$$_rddStorageInfo = new HashMap<Object, RddStorageInfo>();
      this.org$apache$spark$storage$StorageStatus$$_nonRddStorageInfo = this.org$apache$spark$storage$StorageStatus$$NonRddStorageInfo().apply(0L, 0L, 0L);
   }

   public StorageStatus(BlockManagerId bmid, long maxMemory, Option<Object> maxOnHeapMem, Option<Object> maxOffHeapMem, scala.collection.Map<BlockId, BlockStatus> initialBlocks) {
      this(bmid, maxMemory, maxOnHeapMem, maxOffHeapMem);
      initialBlocks.foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final StorageStatus $outer;

         public final void apply(Tuple2<BlockId, BlockStatus> x0$1) {
            if (x0$1 != null) {
               BlockId bid = (BlockId)x0$1._1();
               BlockStatus bstatus = (BlockStatus)x0$1._2();
               this.$outer.addBlock(bid, bstatus);
               BoxedUnit var5 = BoxedUnit.UNIT;
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
      });
   }

   public class RddStorageInfo implements Product, Serializable {
      private final long memoryUsage;
      private final long diskUsage;
      private final StorageLevel level;
      // $FF: synthetic field
      public final StorageStatus $outer;

      public long memoryUsage() {
         return this.memoryUsage;
      }

      public long diskUsage() {
         return this.diskUsage;
      }

      public StorageLevel level() {
         return this.level;
      }

      public RddStorageInfo copy(long memoryUsage, long diskUsage, StorageLevel level) {
         return this.org$apache$spark$storage$StorageStatus$RddStorageInfo$$$outer().new RddStorageInfo(memoryUsage, diskUsage, level);
      }

      public long copy$default$1() {
         return this.memoryUsage();
      }

      public long copy$default$2() {
         return this.diskUsage();
      }

      public StorageLevel copy$default$3() {
         return this.level();
      }

      public String productPrefix() {
         return "RddStorageInfo";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Object var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToLong(this.memoryUsage());
               break;
            case 1:
               var10000 = BoxesRunTime.boxToLong(this.diskUsage());
               break;
            case 2:
               var10000 = this.level();
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
         return x$1 instanceof RddStorageInfo;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.longHash(this.memoryUsage()));
         var1 = Statics.mix(var1, Statics.longHash(this.diskUsage()));
         var1 = Statics.mix(var1, Statics.anyHash(this.level()));
         return Statics.finalizeHash(var1, 3);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var7;
         if (this != x$1) {
            label56: {
               boolean var3;
               if (x$1 instanceof RddStorageInfo) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  label39: {
                     RddStorageInfo var4 = (RddStorageInfo)x$1;
                     if (this.memoryUsage() == var4.memoryUsage() && this.diskUsage() == var4.diskUsage()) {
                        label36: {
                           StorageLevel var10000 = this.level();
                           StorageLevel var5 = var4.level();
                           if (var10000 == null) {
                              if (var5 != null) {
                                 break label36;
                              }
                           } else if (!var10000.equals(var5)) {
                              break label36;
                           }

                           if (var4.canEqual(this)) {
                              var7 = true;
                              break label39;
                           }
                        }
                     }

                     var7 = false;
                  }

                  if (var7) {
                     break label56;
                  }
               }

               var7 = false;
               return var7;
            }
         }

         var7 = true;
         return var7;
      }

      // $FF: synthetic method
      public StorageStatus org$apache$spark$storage$StorageStatus$RddStorageInfo$$$outer() {
         return this.$outer;
      }

      public RddStorageInfo(long memoryUsage, long diskUsage, StorageLevel level) {
         this.memoryUsage = memoryUsage;
         this.diskUsage = diskUsage;
         this.level = level;
         if (StorageStatus.this == null) {
            throw null;
         } else {
            this.$outer = StorageStatus.this;
            super();
            Product$class.$init$(this);
         }
      }
   }

   public class RddStorageInfo$ extends AbstractFunction3<Object, Object, StorageLevel, RddStorageInfo> implements Serializable {
      // $FF: synthetic field
      private final StorageStatus $outer;

      public final String toString() {
         return "RddStorageInfo";
      }

      public RddStorageInfo apply(long memoryUsage, long diskUsage, StorageLevel level) {
         return this.$outer.new RddStorageInfo(memoryUsage, diskUsage, level);
      }

      public Option<Tuple3<Object, Object, StorageLevel>> unapply(RddStorageInfo x$0) {
         return (Option<Tuple3<Object, Object, StorageLevel>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.memoryUsage()), BoxesRunTime.boxToLong(x$0.diskUsage()), x$0.level())));
      }

      public RddStorageInfo$() {
         if (StorageStatus.this == null) {
            throw null;
         } else {
            this.$outer = StorageStatus.this;
            super();
         }
      }
   }

   public class NonRddStorageInfo implements Product, Serializable {
      private long onHeapUsage;
      private long offHeapUsage;
      private long diskUsage;
      // $FF: synthetic field
      public final StorageStatus $outer;

      public long onHeapUsage() {
         return this.onHeapUsage;
      }

      public void onHeapUsage_$eq(long x$1) {
         this.onHeapUsage = x$1;
      }

      public long offHeapUsage() {
         return this.offHeapUsage;
      }

      public void offHeapUsage_$eq(long x$1) {
         this.offHeapUsage = x$1;
      }

      public long diskUsage() {
         return this.diskUsage;
      }

      public void diskUsage_$eq(long x$1) {
         this.diskUsage = x$1;
      }

      public NonRddStorageInfo copy(long onHeapUsage, long offHeapUsage, long diskUsage) {
         return this.org$apache$spark$storage$StorageStatus$NonRddStorageInfo$$$outer().new NonRddStorageInfo(onHeapUsage, offHeapUsage, diskUsage);
      }

      public long copy$default$1() {
         return this.onHeapUsage();
      }

      public long copy$default$2() {
         return this.offHeapUsage();
      }

      public long copy$default$3() {
         return this.diskUsage();
      }

      public String productPrefix() {
         return "NonRddStorageInfo";
      }

      public int productArity() {
         return 3;
      }

      public Object productElement(int x$1) {
         Long var10000;
         switch (x$1) {
            case 0:
               var10000 = BoxesRunTime.boxToLong(this.onHeapUsage());
               break;
            case 1:
               var10000 = BoxesRunTime.boxToLong(this.offHeapUsage());
               break;
            case 2:
               var10000 = BoxesRunTime.boxToLong(this.diskUsage());
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
         return x$1 instanceof NonRddStorageInfo;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, Statics.longHash(this.onHeapUsage()));
         var1 = Statics.mix(var1, Statics.longHash(this.offHeapUsage()));
         var1 = Statics.mix(var1, Statics.longHash(this.diskUsage()));
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
               if (x$1 instanceof NonRddStorageInfo) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  NonRddStorageInfo var4 = (NonRddStorageInfo)x$1;
                  if (this.onHeapUsage() == var4.onHeapUsage() && this.offHeapUsage() == var4.offHeapUsage() && this.diskUsage() == var4.diskUsage() && var4.canEqual(this)) {
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

      // $FF: synthetic method
      public StorageStatus org$apache$spark$storage$StorageStatus$NonRddStorageInfo$$$outer() {
         return this.$outer;
      }

      public NonRddStorageInfo(long onHeapUsage, long offHeapUsage, long diskUsage) {
         this.onHeapUsage = onHeapUsage;
         this.offHeapUsage = offHeapUsage;
         this.diskUsage = diskUsage;
         if (StorageStatus.this == null) {
            throw null;
         } else {
            this.$outer = StorageStatus.this;
            super();
            Product$class.$init$(this);
         }
      }
   }

   public class NonRddStorageInfo$ extends AbstractFunction3<Object, Object, Object, NonRddStorageInfo> implements Serializable {
      // $FF: synthetic field
      private final StorageStatus $outer;

      public final String toString() {
         return "NonRddStorageInfo";
      }

      public NonRddStorageInfo apply(long onHeapUsage, long offHeapUsage, long diskUsage) {
         return this.$outer.new NonRddStorageInfo(onHeapUsage, offHeapUsage, diskUsage);
      }

      public Option<Tuple3<Object, Object, Object>> unapply(NonRddStorageInfo x$0) {
         return (Option<Tuple3<Object, Object, Object>>)(x$0 == null ? None$.MODULE$ : new Some(new Tuple3(BoxesRunTime.boxToLong(x$0.onHeapUsage()), BoxesRunTime.boxToLong(x$0.offHeapUsage()), BoxesRunTime.boxToLong(x$0.diskUsage()))));
      }

      public NonRddStorageInfo$() {
         if (StorageStatus.this == null) {
            throw null;
         } else {
            this.$outer = StorageStatus.this;
            super();
         }
      }
   }
}
