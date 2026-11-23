package org.apache.spark.serializer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.MatchError;
import scala.Predef$;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\r%uAB\u0001\u0003\u0011\u0003!!\"A\u000bTKJL\u0017\r\\5{CRLwN\u001c#fEV<w-\u001a:\u000b\u0005\r!\u0011AC:fe&\fG.\u001b>fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x\r\u0005\u0002\f\u00195\t!A\u0002\u0004\u000e\u0005!\u0005AA\u0004\u0002\u0016'\u0016\u0014\u0018.\u00197ju\u0006$\u0018n\u001c8EK\n,xmZ3s'\raq\"\u0006\t\u0003!Mi\u0011!\u0005\u0006\u0002%\u0005)1oY1mC&\u0011A#\u0005\u0002\u0007\u0003:L(+\u001a4\u0011\u0005YIR\"A\f\u000b\u0005a!\u0011\u0001C5oi\u0016\u0014h.\u00197\n\u0005i9\"a\u0002'pO\u001eLgn\u001a\u0005\u000691!\tAH\u0001\u0007y%t\u0017\u000e\u001e \u0004\u0001Q\t!\u0002C\u0003!\u0019\u0011\u0005\u0011%\u0001\tj[B\u0014xN^3Fq\u000e,\u0007\u000f^5p]R\u0019!EK\u0018\u0011\u0005\rBS\"\u0001\u0013\u000b\u0005\u00152\u0013AA5p\u0015\u00059\u0013\u0001\u00026bm\u0006L!!\u000b\u0013\u000319{GoU3sS\u0006d\u0017N_1cY\u0016,\u0005pY3qi&|g\u000eC\u0003,?\u0001\u0007A&A\u0002pE*\u0004\"\u0001E\u0017\n\u00059\n\"aA!os\")\u0001g\ba\u0001E\u0005\tQ\r\u0003\u00043\u0019\u0011\u0005!aM\u0001\u0005M&tG\r\u0006\u00025\u000fB\u0019Q'\u0010!\u000f\u0005YZdBA\u001c;\u001b\u0005A$BA\u001d\u001e\u0003\u0019a$o\\8u}%\t!#\u0003\u0002=#\u00059\u0001/Y2lC\u001e,\u0017B\u0001 @\u0005\u0011a\u0015n\u001d;\u000b\u0005q\n\u0002CA!E\u001d\t\u0001\")\u0003\u0002D#\u00051\u0001K]3eK\u001aL!!\u0012$\u0003\rM#(/\u001b8h\u0015\t\u0019\u0015\u0003C\u0003,c\u0001\u0007A\u0006\u0003\u0005J\u0019\u0001\u0007I\u0011\u0001\u0002K\u0003=)g.\u00192mK\u0012+'-^4hS:<W#A&\u0011\u0005Aa\u0015BA'\u0012\u0005\u001d\u0011un\u001c7fC:D\u0001b\u0014\u0007A\u0002\u0013\u0005!\u0001U\u0001\u0014K:\f'\r\\3EK\n,xmZ5oO~#S-\u001d\u000b\u0003#R\u0003\"\u0001\u0005*\n\u0005M\u000b\"\u0001B+oSRDq!\u0016(\u0002\u0002\u0003\u00071*A\u0002yIEBaa\u0016\u0007!B\u0013Y\u0015\u0001E3oC\ndW\rR3ck\u001e<\u0017N\\4!\r\u0011iA\u0002B-\u0014\u0005a{\u0001\"\u0002\u000fY\t\u0003YF#\u0001/\u0011\u0005uCV\"\u0001\u0007\t\u000f}C&\u0019!C\u0005A\u00069a/[:ji\u0016$W#A1\u0011\u0007\t<G&D\u0001d\u0015\t!W-A\u0004nkR\f'\r\\3\u000b\u0005\u0019\f\u0012AC2pY2,7\r^5p]&\u0011\u0001n\u0019\u0002\b\u0011\u0006\u001c\bnU3u\u0011\u0019Q\u0007\f)A\u0005C\u0006Aa/[:ji\u0016$\u0007\u0005C\u0003m1\u0012\u0005Q.A\u0003wSNLG\u000fF\u00025]BDQa\\6A\u00021\n\u0011a\u001c\u0005\u0006c.\u0004\r\u0001N\u0001\u0006gR\f7m\u001b\u0005\u0006gb#I\u0001^\u0001\u000bm&\u001c\u0018\u000e^!se\u0006LH\u0003\u0002\u001bv\u0003\u000bAQa\u001c:A\u0002Y\u0004$a\u001e?\u0011\u0007AA(0\u0003\u0002z#\t)\u0011I\u001d:bsB\u00111\u0010 \u0007\u0001\t%iX/!A\u0001\u0002\u000b\u0005aPA\u0002`II\n\"a \u0017\u0011\u0007A\t\t!C\u0002\u0002\u0004E\u0011qAT8uQ&tw\rC\u0003re\u0002\u0007A\u0007C\u0004\u0002\na#I!a\u0003\u0002'YL7/\u001b;FqR,'O\\1mSj\f'\r\\3\u0015\u000bQ\ni!!\u0006\t\u000f=\f9\u00011\u0001\u0002\u0010A\u00191%!\u0005\n\u0007\u0005MAE\u0001\bFqR,'O\\1mSj\f'\r\\3\t\rE\f9\u00011\u00015\u0011\u001d\tI\u0002\u0017C\u0005\u00037\t\u0011C^5tSR\u001cVM]5bY&T\u0018M\u00197f)\u0015!\u0014QDA\u0016\u0011\u001dy\u0017q\u0003a\u0001\u0003?\u0001B!!\t\u0002(5\u0011\u00111\u0005\u0006\u0004\u0003K1\u0013\u0001\u00027b]\u001eLA!!\u000b\u0002$\t1qJ\u00196fGRDa!]A\f\u0001\u0004!\u0004bBA\u00181\u0012%\u0011\u0011G\u0001'm&\u001c\u0018\u000e^*fe&\fG.\u001b>bE2,w+\u001b;i/JLG/Z(cU\u0016\u001cG/T3uQ>$G#\u0002\u001b\u00024\u0005U\u0002bB8\u0002.\u0001\u0007\u0011q\u0004\u0005\u0007c\u00065\u0002\u0019\u0001\u001b\t\u000f\u0005eB\u0002\"\u0003\u0002<\u00059b-\u001b8e\u001f\nTWm\u0019;B]\u0012$Um]2sSB$xN\u001d\u000b\u0005\u0003{\tI\u0005E\u0004\u0011\u0003\u007f\ty\"a\u0011\n\u0007\u0005\u0005\u0013C\u0001\u0004UkBdWM\r\t\u0004G\u0005\u0015\u0013bAA$I\t\trJ\u00196fGR\u001cFO]3b[\u000ec\u0017m]:\t\u000f=\f9\u00041\u0001\u0002 !\"\u0011qGA'!\u0011\ty%!\u0016\u000e\u0005\u0005E#bAA*#\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\t\u0005]\u0013\u0011\u000b\u0002\bi\u0006LGN]3d\r\u0019\tY\u0006\u0004\u0003\u0002^\t\u0001B*[:u\u001f\nTWm\u0019;PkR\u0004X\u000f^\n\u0007\u00033\ny\"a\u0018\u0011\u0007\r\n\t'C\u0002\u0002d\u0011\u0012Ab\u00142kK\u000e$x*\u001e;qkRDq\u0001HA-\t\u0003\t9\u0007\u0006\u0002\u0002jA\u0019Q,!\u0017\t\u0015\u00055\u0014\u0011\fb\u0001\n\u0013\ty'\u0001\u0004pkR\u0004X\u000f^\u000b\u0003\u0003c\u0002BAYA:Y%\u0019\u0011QO2\u0003\u0017\u0005\u0013(/Y=Ck\u001a4WM\u001d\u0005\n\u0003s\nI\u0006)A\u0005\u0003c\nqa\\;uaV$\b\u0005\u0003\u0005\u0002~\u0005eC\u0011AA@\u0003-yW\u000f\u001e9vi\u0006\u0013(/Y=\u0016\u0005\u0005\u0005\u0005c\u0001\tyY!A\u0011QQA-\t\u0003\n9)A\u0006xe&$Xm\u00142kK\u000e$HcA)\u0002\n\"1q.a!A\u00021B\u0001\"!$\u0002Z\u0011\u0005\u0013qR\u0001\u0006M2,8\u000f\u001b\u000b\u0002#\"A\u00111SA-\t\u0003\n)*A\u0003xe&$X\rF\u0002R\u0003/C\u0001\"!'\u0002\u0012\u0002\u0007\u00111T\u0001\u0002SB\u0019\u0001#!(\n\u0007\u0005}\u0015CA\u0002J]RD\u0001\"a%\u0002Z\u0011\u0005\u00131\u0015\u000b\u0004#\u0006\u0015\u0006\u0002CAT\u0003C\u0003\r!!+\u0002\u000b\tLH/Z:\u0011\tAA\u00181\u0016\t\u0004!\u00055\u0016bAAX#\t!!)\u001f;f\u0011!\t\u0019*!\u0017\u0005B\u0005MFcB)\u00026\u0006]\u0016\u0011\u0018\u0005\t\u0003O\u000b\t\f1\u0001\u0002*\"A\u0011\u0011TAY\u0001\u0004\tY\n\u0003\u0005\u0002<\u0006E\u0006\u0019AAN\u0003\tI\u0017\u0007\u0003\u0005\u0002@\u0006eC\u0011IAH\u0003\u0015\u0019Gn\\:f\u0011!\t\u0019-!\u0017\u0005B\u0005\u0015\u0017AC<sSR,g\t\\8biR\u0019\u0011+a2\t\u0011\u0005%\u0017\u0011\u0019a\u0001\u0003\u0017\f\u0011A\u001e\t\u0004!\u00055\u0017bAAh#\t)a\t\\8bi\"A\u00111[A-\t\u0003\n).\u0001\u0006xe&$Xm\u00115beN$2!UAl\u0011\u001d\tI.!5A\u0002\u0001\u000b\u0011a\u001d\u0005\t\u0003;\fI\u0006\"\u0011\u0002`\u0006YqO]5uK\u0012{WO\u00197f)\r\t\u0016\u0011\u001d\u0005\t\u0003\u0013\fY\u000e1\u0001\u0002dB\u0019\u0001#!:\n\u0007\u0005\u001d\u0018C\u0001\u0004E_V\u0014G.\u001a\u0005\t\u0003W\fI\u0006\"\u0011\u0002n\u0006AqO]5uKV#f\tF\u0002R\u0003_Dq!!7\u0002j\u0002\u0007\u0001\t\u0003\u0005\u0002t\u0006eC\u0011IA{\u0003)9(/\u001b;f'\"|'\u000f\u001e\u000b\u0004#\u0006]\b\u0002CAM\u0003c\u0004\r!a'\t\u0011\u0005m\u0018\u0011\fC!\u0003{\f\u0001b\u001e:ji\u0016Le\u000e\u001e\u000b\u0004#\u0006}\b\u0002CAM\u0003s\u0004\r!a'\t\u0011\t\r\u0011\u0011\fC!\u0005\u000b\tAb\u001e:ji\u0016\u0014un\u001c7fC:$2!\u0015B\u0004\u0011\u001d\u0011IA!\u0001A\u0002-\u000b\u0011A\u0019\u0005\t\u0005\u001b\tI\u0006\"\u0011\u0003\u0010\u0005QqO]5uK\nKH/Z:\u0015\u0007E\u0013\t\u0002C\u0004\u0002Z\n-\u0001\u0019\u0001!\t\u0011\tU\u0011\u0011\fC!\u0005/\t\u0011b\u001e:ji\u0016\u001c\u0005.\u0019:\u0015\u0007E\u0013I\u0002\u0003\u0005\u0002\u001a\nM\u0001\u0019AAN\u0011!\u0011i\"!\u0017\u0005B\t}\u0011!C<sSR,Gj\u001c8h)\r\t&\u0011\u0005\u0005\t\u0005G\u0011Y\u00021\u0001\u0003&\u0005\tA\u000eE\u0002\u0011\u0005OI1A!\u000b\u0012\u0005\u0011auN\\4\t\u0011\t5\u0012\u0011\fC!\u0005_\t\u0011b\u001e:ji\u0016\u0014\u0015\u0010^3\u0015\u0007E\u0013\t\u0004\u0003\u0005\u0002\u001a\n-\u0002\u0019AAN\r\u0019\u0011)\u0004\u0004\u0003\u00038\t\u0001b*\u001e7m\u001fV$\b/\u001e;TiJ,\u0017-\\\n\u0005\u0005g\u0011I\u0004E\u0002$\u0005wI1A!\u0010%\u00051yU\u000f\u001e9viN#(/Z1n\u0011\u001da\"1\u0007C\u0001\u0005\u0003\"\"Aa\u0011\u0011\u0007u\u0013\u0019\u0004\u0003\u0005\u0002\u0014\nMB\u0011\tB$)\r\t&\u0011\n\u0005\t\u0005\u0013\u0011)\u00051\u0001\u0002\u001c\u001a1!Q\n\u0007\u0005\u0005\u001f\u0012a\u0003T5ti>\u0013'.Z2u\u001fV$\b/\u001e;TiJ,\u0017-\\\n\u0005\u0005\u0017\u0012\t\u0006E\u0002$\u0005'J1A!\u0016%\u0005Iy%M[3di>+H\u000f];u'R\u0014X-Y7\t\u000fq\u0011Y\u0005\"\u0001\u0003ZQ\u0011!1\f\t\u0004;\n-\u0003BCA7\u0005\u0017\u0012\r\u0011\"\u0003\u0002p!I\u0011\u0011\u0010B&A\u0003%\u0011\u0011\u000f\u0005\t\u0003{\u0012Y\u0005\"\u0001\u0002\u0000!A!Q\rB&\t\u0003\u00129'A\u0007sKBd\u0017mY3PE*,7\r\u001e\u000b\u0005\u0003?\u0011I\u0007C\u0004,\u0005G\u0002\r!a\b\u0007\r\t5Db\u0001B8\u0005ay%M[3diN#(/Z1n\u00072\f7o]'fi\"|Gm]\n\u0005\u0005W\u0012\t\bE\u0002\u0011\u0005gJ1A!\u001e\u0012\u0005\u0019\te.\u001f,bY\"Y!\u0011\u0010B6\u0005\u000b\u0007I\u0011\u0001B>\u0003\u0011!Wm]2\u0016\u0005\u0005\r\u0003b\u0003B@\u0005W\u0012\t\u0011)A\u0005\u0003\u0007\nQ\u0001Z3tG\u0002Bq\u0001\bB6\t\u0003\u0011\u0019\t\u0006\u0003\u0003\u0006\n\u001d\u0005cA/\u0003l!A!\u0011\u0010BA\u0001\u0004\t\u0019\u0005\u0003\u0005\u0003\f\n-D\u0011\u0001BG\u000319W\r^*m_R$Um]2t+\t\u0011y\t\u0005\u0003\u0011q\u0006\r\u0003b\u0002BJ\u0005W\"\tAS\u0001\u0015Q\u0006\u001cxK]5uK>\u0013'.Z2u\u001b\u0016$\bn\u001c3\t\u000f\t]%1\u000eC\u0001\u0015\u0006)\u0002.Y:Xe&$XMU3qY\u0006\u001cW-T3uQ>$\u0007\u0002\u0003BN\u0005W\"\tA!(\u0002%%tgo\\6f/JLG/\u001a*fa2\f7-\u001a\u000b\u0005\u0003?\u0011y\nC\u0004,\u00053\u0003\r!a\b\t\u0011\t\r&1\u000eC\u0001\u0005K\u000bqbZ3u\u001dVlwJ\u00196GS\u0016dGm]\u000b\u0003\u00037C\u0001B!+\u0003l\u0011\u0005!1V\u0001\u0012O\u0016$xJ\u00196GS\u0016dGMV1mk\u0016\u001cH#B)\u0003.\n=\u0006bB\u0016\u0003(\u0002\u0007\u0011q\u0004\u0005\t\u0005c\u00139\u000b1\u0001\u00034\u0006\u0019q.\u001e;\u0011\tAA\u0018q\u0004\u0005\u000b\u0005o\u0013Y'!A\u0005B\te\u0016\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0005\u0005m\u0005B\u0003B_\u0005W\n\t\u0011\"\u0011\u0003@\u00061Q-];bYN$2a\u0013Ba\u0011!)&1XA\u0001\u0002\u0004a\u0003\"\u0003Bc\u0019\u0005\u0005I1\u0001Bd\u0003ay%M[3diN#(/Z1n\u00072\f7o]'fi\"|Gm\u001d\u000b\u0005\u0005\u000b\u0013I\r\u0003\u0005\u0003z\t\r\u0007\u0019AA\"\u0011%\u0011i\r\u0004b\u0001\n\u0013\u0011y-A\u0004sK\u001adWm\u0019;\u0016\u0005\tE\u0007cA/\u0003T\u001a1!Q\u001b\u0007\u0005\u0005/\u00141d\u00142kK\u000e$8\u000b\u001e:fC6\u001cE.Y:t%\u00164G.Z2uS>t7c\u0001Bj\u001f!9ADa5\u0005\u0002\tmGC\u0001Bi\u0011)\u0011yNa5C\u0002\u0013\u0005!\u0011]\u0001\u0013\u000f\u0016$8\t\\1tg\u0012\u000bG/\u0019'bs>,H/\u0006\u0002\u0003dB!!Q\u001dBu\u001b\t\u00119O\u0003\u0003\u0003N\u0006\r\u0012\u0002\u0002Bv\u0005O\u0014a!T3uQ>$\u0007\"\u0003Bx\u0005'\u0004\u000b\u0011\u0002Br\u0003M9U\r^\"mCN\u001cH)\u0019;b\u0019\u0006Lx.\u001e;!\u0011)\u0011\u0019Pa5C\u0002\u0013\u0005!\u0011]\u0001\u0015\u0011\u0006\u001cxK]5uK>\u0013'.Z2u\u001b\u0016$\bn\u001c3\t\u0013\t](1\u001bQ\u0001\n\t\r\u0018!\u0006%bg^\u0013\u0018\u000e^3PE*,7\r^'fi\"|G\r\t\u0005\u000b\u0005w\u0014\u0019N1A\u0005\u0002\t\u0005\u0018!\u0006%bg^\u0013\u0018\u000e^3SKBd\u0017mY3NKRDw\u000e\u001a\u0005\n\u0005\u007f\u0014\u0019\u000e)A\u0005\u0005G\fa\u0003S1t/JLG/\u001a*fa2\f7-Z'fi\"|G\r\t\u0005\u000b\u0007\u0007\u0011\u0019N1A\u0005\u0002\t\u0005\u0018AE%om>\\Wm\u0016:ji\u0016\u0014V\r\u001d7bG\u0016D\u0011ba\u0002\u0003T\u0002\u0006IAa9\u0002'%sgo\\6f/JLG/\u001a*fa2\f7-\u001a\u0011\t\u0015\r-!1\u001bb\u0001\n\u0003\u0011\t/A\bHKRtU/\\(cU\u001aKW\r\u001c3t\u0011%\u0019yAa5!\u0002\u0013\u0011\u0019/\u0001\tHKRtU/\\(cU\u001aKW\r\u001c3tA!Q11\u0003Bj\u0005\u0004%\tA!9\u0002#\u001d+Go\u00142k\r&,G\u000e\u001a,bYV,7\u000fC\u0005\u0004\u0018\tM\u0007\u0015!\u0003\u0003d\u0006\u0011r)\u001a;PE*4\u0015.\u001a7e-\u0006dW/Z:!\u0011)\u0019YBa5C\u0002\u0013\u00051QD\u0001\n\t\u0016\u001c8MR5fY\u0012,\"aa\b\u0011\t\t\u00158\u0011E\u0005\u0005\u0007G\u00119OA\u0003GS\u0016dG\rC\u0005\u0004(\tM\u0007\u0015!\u0003\u0004 \u0005QA)Z:d\r&,G\u000e\u001a\u0011\t\u0011\r-B\u0002)A\u0005\u0005#\f\u0001B]3gY\u0016\u001cG\u000fI\u0004\n\u0005\u000bd\u0011\u0011!E\u0001\u0007_\u00012!XB\u0019\r%\u0011i\u0007DA\u0001\u0012\u0003\u0019\u0019dE\u0002\u00042=Aq\u0001HB\u0019\t\u0003\u00199\u0004\u0006\u0002\u00040!A11HB\u0019\t\u000b\u0019i$\u0001\fhKR\u001cFn\u001c;EKN\u001c7\u000fJ3yi\u0016t7/[8o)\u0011\u0011yia\u0010\t\u0011\r\u00053\u0011\ba\u0001\u0005\u000b\u000bQ\u0001\n;iSND\u0001b!\u0012\u00042\u0011\u00151qI\u0001\u001fQ\u0006\u001cxK]5uK>\u0013'.Z2u\u001b\u0016$\bn\u001c3%Kb$XM\\:j_:$2aSB%\u0011!\u0019\tea\u0011A\u0002\t\u0015\u0005\u0002CB'\u0007c!)aa\u0014\u0002?!\f7o\u0016:ji\u0016\u0014V\r\u001d7bG\u0016lU\r\u001e5pI\u0012*\u0007\u0010^3og&|g\u000eF\u0002L\u0007#B\u0001b!\u0011\u0004L\u0001\u0007!Q\u0011\u0005\t\u0007+\u001a\t\u0004\"\u0002\u0004X\u0005a\u0012N\u001c<pW\u0016<&/\u001b;f%\u0016\u0004H.Y2fI\u0015DH/\u001a8tS>tG\u0003BB-\u0007;\"B!a\b\u0004\\!91fa\u0015A\u0002\u0005}\u0001\u0002CB!\u0007'\u0002\rA!\"\t\u0011\r\u00054\u0011\u0007C\u0003\u0007G\n\u0011dZ3u\u001dVlwJ\u00196GS\u0016dGm\u001d\u0013fqR,gn]5p]R!\u00111TB3\u0011!\u0019\tea\u0018A\u0002\t\u0015\u0005\u0002CB5\u0007c!)aa\u001b\u00027\u001d,Go\u00142k\r&,G\u000e\u001a,bYV,7\u000fJ3yi\u0016t7/[8o)\u0011\u0019iga\u001d\u0015\u000bE\u001byg!\u001d\t\u000f-\u001a9\u00071\u0001\u0002 !A!\u0011WB4\u0001\u0004\u0011\u0019\f\u0003\u0005\u0004B\r\u001d\u0004\u0019\u0001BC\u0011)\u00199h!\r\u0002\u0002\u0013\u00151\u0011P\u0001\u0013Q\u0006\u001c\bnQ8eK\u0012*\u0007\u0010^3og&|g\u000e\u0006\u0003\u0003:\u000em\u0004\u0002CB!\u0007k\u0002\rA!\"\t\u0015\r}4\u0011GA\u0001\n\u000b\u0019\t)\u0001\tfcV\fGn\u001d\u0013fqR,gn]5p]R!11QBD)\rY5Q\u0011\u0005\t+\u000eu\u0014\u0011!a\u0001Y!A1\u0011IB?\u0001\u0004\u0011)\t"
)
public final class SerializationDebugger {
   public static boolean initializeLogIfNecessary$default$2() {
      return SerializationDebugger$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SerializationDebugger$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SerializationDebugger$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SerializationDebugger$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SerializationDebugger$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SerializationDebugger$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SerializationDebugger$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SerializationDebugger$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SerializationDebugger$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SerializationDebugger$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SerializationDebugger$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SerializationDebugger$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SerializationDebugger$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SerializationDebugger$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SerializationDebugger$.MODULE$.log();
   }

   public static String logName() {
      return SerializationDebugger$.MODULE$.logName();
   }

   public static ObjectStreamClass ObjectStreamClassMethods(ObjectStreamClass var0) {
      return SerializationDebugger$.MODULE$.ObjectStreamClassMethods(var0);
   }

   public static NotSerializableException improveException(Object var0, NotSerializableException var1) {
      return SerializationDebugger$.MODULE$.improveException(var0, var1);
   }

   public static class SerializationDebugger {
      private final HashSet<Object> visited = new HashSet<Object>();

      private HashSet<Object> visited() {
         return this.visited;
      }

      public List<String> visit(Object o, List<String> stack) {
         List var10000;
         if (o == null) {
            var10000 = List$.MODULE$.empty();
         } else if (this.visited().contains(o)) {
            var10000 = List$.MODULE$.empty();
         } else {
            this.visited().$plus$eq(o);
            List var4;
            if (o.getClass().isPrimitive()) {
               var4 = List$.MODULE$.empty();
            } else if (o instanceof String) {
               var4 = List$.MODULE$.empty();
            } else if (o.getClass().isArray() && o.getClass().getComponentType().isPrimitive()) {
               var4 = List$.MODULE$.empty();
            } else if (ScalaRunTime$.MODULE$.isArray(o, 1) && o.getClass().isArray() && !o.getClass().getComponentType().isPrimitive()) {
               String elem = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"array (class ", ", size ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{o.getClass().getName(), BoxesRunTime.boxToInteger(ScalaRunTime$.MODULE$.array_length(o))}));
               var4 = this.visitArray(o, stack.$colon$colon(elem));
            } else if (o instanceof Externalizable) {
               Externalizable var8 = (Externalizable)o;
               String elem = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"externalizable object (class ", ", ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{var8.getClass().getName(), var8}));
               var4 = this.visitExternalizable(var8, stack.$colon$colon(elem));
            } else if (o instanceof Object && o instanceof Serializable) {
               String elem = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"object (class ", ", ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{o.getClass().getName(), o}));
               var4 = this.visitSerializable(o, stack.$colon$colon(elem));
            } else {
               String var14 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"object not serializable (class: ", ", value: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{o.getClass().getName(), o}));
               var4 = stack.$colon$colon(var14);
            }

            var10000 = var4;
         }

         return var10000;
      }

      private List<String> visitArray(Object o, List<String> stack) {
         for(int i = 0; i < ScalaRunTime$.MODULE$.array_length(o); ++i) {
            Object var10001 = ScalaRunTime$.MODULE$.array_apply(o, i);
            String var5 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"element of array (index: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(i)}));
            List childStack = this.visit(var10001, stack.$colon$colon(var5));
            if (childStack.nonEmpty()) {
               return childStack;
            }
         }

         return List$.MODULE$.empty();
      }

      private List<String> visitExternalizable(Externalizable o, List<String> stack) {
         org.apache.spark.serializer.SerializationDebugger.ListObjectOutput fieldList = new org.apache.spark.serializer.SerializationDebugger.ListObjectOutput();
         o.writeExternal(fieldList);
         Object[] childObjects = fieldList.outputArray();

         for(int i = 0; i < childObjects.length; ++i) {
            Object var10001 = childObjects[i];
            String var7 = "writeExternal data";
            List childStack = this.visit(var10001, stack.$colon$colon(var7));
            if (childStack.nonEmpty()) {
               return childStack;
            }
         }

         return List$.MODULE$.empty();
      }

      private List<String> visitSerializable(Object o, List<String> stack) {
         Tuple2 var4 = SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$findObjectAndDescriptor(o);
         if (var4 == null) {
            throw new MatchError(var4);
         } else {
            Object finalObj;
            label56: {
               Object finalObj = var4._1();
               ObjectStreamClass desc = (ObjectStreamClass)var4._2();
               Tuple2 var7 = new Tuple2(finalObj, desc);
               finalObj = var7._1();
               ObjectStreamClass desc = (ObjectStreamClass)var7._2();
               Class var10000 = finalObj.getClass();
               Class var10 = o.getClass();
               if (var10000 == null) {
                  if (var10 != null) {
                     break label56;
                  }
               } else if (!var10000.equals(var10)) {
                  break label56;
               }

               ObjectStreamClass[] slotDescs = org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getSlotDescs$extension(SerializationDebugger$.MODULE$.ObjectStreamClassMethods(desc));

               for(int i = 0; i < slotDescs.length; ++i) {
                  ObjectStreamClass slotDesc = slotDescs[i];
                  if (org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.hasWriteObjectMethod$extension(SerializationDebugger$.MODULE$.ObjectStreamClassMethods(slotDesc))) {
                     String elem = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"writeObject data (class: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{slotDesc.getName()}));
                     List childStack = this.visitSerializableWithWriteObjectMethod(finalObj, stack.$colon$colon(elem));
                     if (childStack.nonEmpty()) {
                        return childStack;
                     }
                  } else {
                     ObjectStreamField[] fields = slotDesc.getFields();
                     Object[] objFieldValues = new Object[org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getNumObjFields$extension(SerializationDebugger$.MODULE$.ObjectStreamClassMethods(slotDesc))];
                     int numPrims = fields.length - objFieldValues.length;
                     org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getObjFieldValues$extension(SerializationDebugger$.MODULE$.ObjectStreamClassMethods(slotDesc), finalObj, objFieldValues);

                     for(int j = 0; j < objFieldValues.length; ++j) {
                        ObjectStreamField fieldDesc = fields[numPrims + j];
                        String elem = (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"field (class: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{slotDesc.getName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{", name: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{fieldDesc.getName()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{", type: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{fieldDesc.getType()}))).toString();
                        List childStack = this.visit(objFieldValues[j], stack.$colon$colon(elem));
                        if (childStack.nonEmpty()) {
                           return childStack;
                        }
                     }
                  }
               }

               return List$.MODULE$.empty();
            }

            String var11 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"writeReplace data (class: ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{finalObj.getClass().getName()}));
            return this.visit(finalObj, stack.$colon$colon(var11));
         }
      }

      private List<String> visitSerializableWithWriteObjectMethod(Object o, List<String> stack) {
         org.apache.spark.serializer.SerializationDebugger.ListObjectOutputStream innerObjectsCatcher = new org.apache.spark.serializer.SerializationDebugger.ListObjectOutputStream();
         boolean notSerializableFound = false;

         try {
            innerObjectsCatcher.writeObject(o);
         } catch (IOException var9) {
            notSerializableFound = true;
         }

         if (notSerializableFound) {
            Object[] innerObjects = innerObjectsCatcher.outputArray();

            for(int k = 0; k < innerObjects.length; ++k) {
               List childStack = this.visit(innerObjects[k], stack);
               if (childStack.nonEmpty()) {
                  return childStack;
               }
            }

            BoxedUnit var10000 = BoxedUnit.UNIT;
         } else {
            this.visited().$plus$plus$eq(Predef$.MODULE$.genericArrayOps(innerObjectsCatcher.outputArray()));
         }

         return List$.MODULE$.empty();
      }
   }

   public static class ListObjectOutput implements ObjectOutput {
      private final ArrayBuffer<Object> output = new ArrayBuffer<Object>();

      private ArrayBuffer<Object> output() {
         return this.output;
      }

      public Object[] outputArray() {
         return this.output().toArray(ClassTag$.MODULE$.Any());
      }

      public void writeObject(Object o) {
         this.output().$plus$eq(o);
      }

      public void flush() {
      }

      public void write(int i) {
      }

      public void write(byte[] bytes) {
      }

      public void write(byte[] bytes, int i, int i1) {
      }

      public void close() {
      }

      public void writeFloat(float v) {
      }

      public void writeChars(String s) {
      }

      public void writeDouble(double v) {
      }

      public void writeUTF(String s) {
      }

      public void writeShort(int i) {
      }

      public void writeInt(int i) {
      }

      public void writeBoolean(boolean b) {
      }

      public void writeBytes(String s) {
      }

      public void writeChar(int i) {
      }

      public void writeLong(long l) {
      }

      public void writeByte(int i) {
      }
   }

   public static class NullOutputStream extends OutputStream {
      public void write(int b) {
      }
   }

   public static class ListObjectOutputStream extends ObjectOutputStream {
      private final ArrayBuffer<Object> output = new ArrayBuffer<Object>();

      private ArrayBuffer<Object> output() {
         return this.output;
      }

      public Object[] outputArray() {
         return this.output().toArray(ClassTag$.MODULE$.Any());
      }

      public Object replaceObject(Object obj) {
         this.output().$plus$eq(obj);
         return obj;
      }

      public ListObjectOutputStream() {
         super(new org.apache.spark.serializer.SerializationDebugger.NullOutputStream());
         this.enableReplaceObject(true);
      }
   }

   public static final class ObjectStreamClassMethods {
      private final ObjectStreamClass desc;

      public ObjectStreamClass desc() {
         return this.desc;
      }

      public ObjectStreamClass[] getSlotDescs() {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getSlotDescs$extension(this.desc());
      }

      public boolean hasWriteObjectMethod() {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.hasWriteObjectMethod$extension(this.desc());
      }

      public boolean hasWriteReplaceMethod() {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.hasWriteReplaceMethod$extension(this.desc());
      }

      public Object invokeWriteReplace(Object obj) {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.invokeWriteReplace$extension(this.desc(), obj);
      }

      public int getNumObjFields() {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getNumObjFields$extension(this.desc());
      }

      public void getObjFieldValues(Object obj, Object[] out) {
         org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.getObjFieldValues$extension(this.desc(), obj, out);
      }

      public int hashCode() {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.hashCode$extension(this.desc());
      }

      public boolean equals(Object x$1) {
         return org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$.MODULE$.equals$extension(this.desc(), x$1);
      }

      public ObjectStreamClassMethods(ObjectStreamClass desc) {
         this.desc = desc;
      }
   }

   public static class ObjectStreamClassMethods$ {
      public static final org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$ MODULE$;

      static {
         new org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods$();
      }

      public final ObjectStreamClass[] getSlotDescs$extension(ObjectStreamClass $this) {
         return (ObjectStreamClass[])Predef$.MODULE$.refArrayOps(SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().GetClassDataLayout().invoke($this)).map(new scala.Serializable() {
            public static final long serialVersionUID = 0L;

            public final ObjectStreamClass apply(Object classDataSlot) {
               return (ObjectStreamClass)SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().DescField().get(classDataSlot);
            }
         }, Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply(ObjectStreamClass.class)));
      }

      public final boolean hasWriteObjectMethod$extension(ObjectStreamClass $this) {
         return BoxesRunTime.unboxToBoolean(SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().HasWriteObjectMethod().invoke($this));
      }

      public final boolean hasWriteReplaceMethod$extension(ObjectStreamClass $this) {
         return BoxesRunTime.unboxToBoolean(SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().HasWriteReplaceMethod().invoke($this));
      }

      public final Object invokeWriteReplace$extension(ObjectStreamClass $this, Object obj) {
         return SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().InvokeWriteReplace().invoke($this, obj);
      }

      public final int getNumObjFields$extension(ObjectStreamClass $this) {
         return BoxesRunTime.unboxToInt(SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().GetNumObjFields().invoke($this));
      }

      public final void getObjFieldValues$extension(ObjectStreamClass $this, Object obj, Object[] out) {
         SerializationDebugger$.MODULE$.org$apache$spark$serializer$SerializationDebugger$$reflect().GetObjFieldValues().invoke($this, obj, out);
      }

      public final int hashCode$extension(ObjectStreamClass $this) {
         return $this.hashCode();
      }

      public final boolean equals$extension(ObjectStreamClass $this, Object x$1) {
         boolean var4;
         if (x$1 instanceof org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods) {
            var4 = true;
         } else {
            var4 = false;
         }

         boolean var7;
         if (var4) {
            label31: {
               label30: {
                  ObjectStreamClass var5 = x$1 == null ? null : ((org.apache.spark.serializer.SerializationDebugger.ObjectStreamClassMethods)x$1).desc();
                  if ($this == null) {
                     if (var5 == null) {
                        break label30;
                     }
                  } else if ($this.equals(var5)) {
                     break label30;
                  }

                  var7 = false;
                  break label31;
               }

               var7 = true;
            }

            if (var7) {
               var7 = true;
               return var7;
            }
         }

         var7 = false;
         return var7;
      }

      public ObjectStreamClassMethods$() {
         MODULE$ = this;
      }
   }

   public static class ObjectStreamClassReflection {
      private final Method GetClassDataLayout;
      private final Method HasWriteObjectMethod;
      private final Method HasWriteReplaceMethod;
      private final Method InvokeWriteReplace;
      private final Method GetNumObjFields;
      private final Method GetObjFieldValues;
      private final Field DescField;

      public Method GetClassDataLayout() {
         return this.GetClassDataLayout;
      }

      public Method HasWriteObjectMethod() {
         return this.HasWriteObjectMethod;
      }

      public Method HasWriteReplaceMethod() {
         return this.HasWriteReplaceMethod;
      }

      public Method InvokeWriteReplace() {
         return this.InvokeWriteReplace;
      }

      public Method GetNumObjFields() {
         return this.GetNumObjFields;
      }

      public Method GetObjFieldValues() {
         return this.GetObjFieldValues;
      }

      public Field DescField() {
         return this.DescField;
      }

      public ObjectStreamClassReflection() {
         Method f = ObjectStreamClass.class.getDeclaredMethod("getClassDataLayout");
         f.setAccessible(true);
         this.GetClassDataLayout = f;
         Method f = ObjectStreamClass.class.getDeclaredMethod("hasWriteObjectMethod");
         f.setAccessible(true);
         this.HasWriteObjectMethod = f;
         Method f = ObjectStreamClass.class.getDeclaredMethod("hasWriteReplaceMethod");
         f.setAccessible(true);
         this.HasWriteReplaceMethod = f;
         Method f = ObjectStreamClass.class.getDeclaredMethod("invokeWriteReplace", Object.class);
         f.setAccessible(true);
         this.InvokeWriteReplace = f;
         Method f = ObjectStreamClass.class.getDeclaredMethod("getNumObjFields");
         f.setAccessible(true);
         this.GetNumObjFields = f;
         Method f = ObjectStreamClass.class.getDeclaredMethod("getObjFieldValues", Object.class, Object[].class);
         f.setAccessible(true);
         this.GetObjFieldValues = f;
         Field f = Class.forName("java.io.ObjectStreamClass$ClassDataSlot").getDeclaredField("desc");
         f.setAccessible(true);
         this.DescField = f;
      }
   }
}
