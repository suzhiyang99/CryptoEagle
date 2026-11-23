package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import org.apache.spark.status.api.v1.AccumulableInfo;
import org.apache.spark.status.api.v1.InputMetrics;
import org.apache.spark.status.api.v1.OutputMetrics;
import org.apache.spark.status.api.v1.ShuffleReadMetrics;
import org.apache.spark.status.api.v1.ShuffleWriteMetrics;
import org.apache.spark.status.api.v1.TaskData;
import org.apache.spark.status.api.v1.TaskMetrics;
import org.apache.spark.util.kvstore.KVIndex;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Some;
import scala.StringContext;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\rmd!B\u0001\u0003\u0001\u0011Q!a\u0004+bg.$\u0015\r^1Xe\u0006\u0004\b/\u001a:\u000b\u0005\r!\u0011AB:uCR,8O\u0003\u0002\u0006\r\u0005)1\u000f]1sW*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h'\t\u00011\u0002\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\u0005\t%\u0001\u0011)\u0019!C\u0001)\u00051A/Y:l\u0013\u0012\u001c\u0001!F\u0001\u0016!\t12$D\u0001\u0018\u0015\tA\u0012$\u0001\u0003mC:<'\"\u0001\u000e\u0002\t)\fg/Y\u0005\u00039]\u0011A\u0001T8oO\"\u0012\u0011C\b\u0016\u0003?\u001d\u0002\"\u0001I\u0013\u000e\u0003\u0005R!AI\u0012\u0002\u000f-48\u000f^8sK*\u0011A\u0005B\u0001\u0005kRLG.\u0003\u0002'C\t91JV%oI\u0016D8&\u0001\u0015\u0011\u0005%rS\"\u0001\u0016\u000b\u0005-b\u0013\u0001B7fi\u0006T!!L\u0007\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u00020U\t1q-\u001a;uKJD\u0001\"\r\u0001\u0003\u0002\u0003\u0006I!F\u0001\bi\u0006\u001c8.\u00133!\u0011!\u0019\u0004A!b\u0001\n\u0003!\u0014!B5oI\u0016DX#A\u001b\u0011\u000511\u0014BA\u001c\u000e\u0005\rIe\u000e\u001e\u0015\u0007eyI$\bP\u001f\u0002\u000bY\fG.^3\"\u0003m\n1!\u001b3y\u0003\u0019\u0001\u0018M]3oi\u0006\na(A\u0003ti\u0006<W\r\u0003\u0005A\u0001\t\u0005\t\u0015!\u00036\u0003\u0019Ig\u000eZ3yA!A!\t\u0001BC\u0002\u0013\u0005A'A\u0004biR,W\u000e\u001d;)\r\u0005s\u0012\b\u0012\u001f>C\u0005)\u0015aA1ui\"Aq\t\u0001B\u0001B\u0003%Q'\u0001\u0005biR,W\u000e\u001d;!\u0011!I\u0005A!b\u0001\n\u0003Q\u0015A\u00037bk:\u001c\u0007\u000eV5nKV\t1\n\u0005\u0002\r\u0019&\u0011A$\u0004\u0015\u0007\u0011zId\nP\u001f\"\u0003=\u000b!\u0001\u001c;\t\u0011E\u0003!\u0011!Q\u0001\n-\u000b1\u0002\\1v]\u000eDG+[7fA!A1\u000b\u0001BC\u0002\u0013\u0005!*\u0001\tsKN,H\u000e\u001e$fi\u000eD7\u000b^1si\"AQ\u000b\u0001B\u0001B\u0003%1*A\tsKN,H\u000e\u001e$fi\u000eD7\u000b^1si\u0002B\u0001b\u0016\u0001\u0003\u0006\u0004%\tAS\u0001\tIV\u0014\u0018\r^5p]\"2aKH\u001dZyu\n\u0013AW\u0001\u0004IV\u0014\b\u0002\u0003/\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002\u0013\u0011,(/\u0019;j_:\u0004\u0003\u0002\u00030\u0001\u0005\u000b\u0007I\u0011A0\u0002\u0015\u0015DXmY;u_JLE-F\u0001a!\t\tGM\u0004\u0002\rE&\u00111-D\u0001\u0007!J,G-\u001a4\n\u0005\u00154'AB*ue&twM\u0003\u0002d\u001b!2QLH\u001diyu\n\u0013![\u0001\u0004Kb,\u0007\u0002C6\u0001\u0005\u0003\u0005\u000b\u0011\u00021\u0002\u0017\u0015DXmY;u_JLE\r\t\u0005\t[\u0002\u0011)\u0019!C\u0001?\u0006!\u0001n\\:u\u0011!y\u0007A!A!\u0002\u0013\u0001\u0017!\u00025pgR\u0004\u0003\u0002C\u0002\u0001\u0005\u000b\u0007I\u0011A0)\rAt\u0012H\u001d\u001f>C\u0005\u0019\u0018aA:uC\"AQ\u000f\u0001B\u0001B\u0003%\u0001-A\u0004ti\u0006$Xo\u001d\u0011\t\u0011]\u0004!Q1A\u0005\u0002}\u000bA\u0002^1tW2{7-\u00197jifDcA\u001e\u0010:srj\u0014%\u0001>\u0002\u00071|7\r\u0003\u0005}\u0001\t\u0005\t\u0015!\u0003a\u00035!\u0018m]6M_\u000e\fG.\u001b;zA!Aa\u0010\u0001BC\u0002\u0013\u0005q0A\u0006ta\u0016\u001cW\u000f\\1uSZ,WCAA\u0001!\ra\u00111A\u0005\u0004\u0003\u000bi!a\u0002\"p_2,\u0017M\u001c\u0005\u000b\u0003\u0013\u0001!\u0011!Q\u0001\n\u0005\u0005\u0011\u0001D:qK\u000e,H.\u0019;jm\u0016\u0004\u0003BCA\u0007\u0001\t\u0015\r\u0011\"\u0001\u0002\u0010\u0005\u0011\u0012mY2v[Vd\u0017\r^8s+B$\u0017\r^3t+\t\t\t\u0002\u0005\u0004\u0002\u0014\u0005\r\u0012\u0011\u0006\b\u0005\u0003+\tyB\u0004\u0003\u0002\u0018\u0005uQBAA\r\u0015\r\tYbE\u0001\u0007yI|w\u000e\u001e \n\u00039I1!!\t\u000e\u0003\u001d\u0001\u0018mY6bO\u0016LA!!\n\u0002(\t\u00191+Z9\u000b\u0007\u0005\u0005R\u0002\u0005\u0003\u0002,\u0005URBAA\u0017\u0015\u0011\ty#!\r\u0002\u0005Y\f$bAA\u001a\u0005\u0005\u0019\u0011\r]5\n\t\u0005]\u0012Q\u0006\u0002\u0010\u0003\u000e\u001cW/\\;mC\ndW-\u00138g_\"Q\u00111\b\u0001\u0003\u0002\u0003\u0006I!!\u0005\u0002'\u0005\u001c7-^7vY\u0006$xN]+qI\u0006$Xm\u001d\u0011\t\u0015\u0005}\u0002A!b\u0001\n\u0003\t\t%\u0001\u0007feJ|'/T3tg\u0006<W-\u0006\u0002\u0002DA!A\"!\u0012a\u0013\r\t9%\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0015\u0005-\u0003A!A!\u0002\u0013\t\u0019%A\u0007feJ|'/T3tg\u0006<W\r\t\u0005\n\u0003\u001f\u0002!Q1A\u0005\u0002)\u000bq#\u001a=fGV$xN\u001d#fg\u0016\u0014\u0018.\u00197ju\u0016$\u0016.\\3)\u0011\u00055c$OA*yu\n#!!\u0016\u0002\u0007\u0011,7\u000fC\u0005\u0002Z\u0001\u0011\t\u0011)A\u0005\u0017\u0006AR\r_3dkR|'\u000fR3tKJL\u0017\r\\5{KRKW.\u001a\u0011\t\u0013\u0005u\u0003A!b\u0001\n\u0003Q\u0015AG3yK\u000e,Ho\u001c:EKN,'/[1mSj,7\t];US6,\u0007\u0006CA.=e\n\t\u0007P\u001f\"\u0005\u0005\r\u0014a\u00013di\"I\u0011q\r\u0001\u0003\u0002\u0003\u0006IaS\u0001\u001cKb,7-\u001e;pe\u0012+7/\u001a:jC2L'0Z\"qkRKW.\u001a\u0011\t\u0013\u0005-\u0004A!b\u0001\n\u0003Q\u0015aD3yK\u000e,Ho\u001c:Sk:$\u0016.\\3)\u0011\u0005%d$OA8yu\n#!!\u001d\u0002\u0007\u0015\u0014H\u000fC\u0005\u0002v\u0001\u0011\t\u0011)A\u0005\u0017\u0006\u0001R\r_3dkR|'OU;o)&lW\r\t\u0005\n\u0003s\u0002!Q1A\u0005\u0002)\u000bq\"\u001a=fGV$xN]\"qkRKW.\u001a\u0015\t\u0003or\u0012(! ={\u0005\u0012\u0011qP\u0001\u0004K\u000e$\b\"CAB\u0001\t\u0005\t\u0015!\u0003L\u0003A)\u00070Z2vi>\u00148\t];US6,\u0007\u0005C\u0005\u0002\b\u0002\u0011)\u0019!C\u0001\u0015\u0006Q!/Z:vYR\u001c\u0016N_3)\u0011\u0005\u0015e$OAFyu\n#!!$\u0002\u0005I\u001c\b\"CAI\u0001\t\u0005\t\u0015!\u0003L\u0003-\u0011Xm];miNK'0\u001a\u0011\t\u0013\u0005U\u0005A!b\u0001\n\u0003Q\u0015!\u00036w[\u001e\u001bG+[7fQ!\t\u0019JH\u001d\u0002\u001arj\u0014EAAN\u0003\t97\rC\u0005\u0002 \u0002\u0011\t\u0011)A\u0005\u0017\u0006Q!N^7HGRKW.\u001a\u0011\t\u0013\u0005\r\u0006A!b\u0001\n\u0003Q\u0015a\u0006:fgVdGoU3sS\u0006d\u0017N_1uS>tG+[7fQ!\t\tKH\u001d\u0002(rj\u0014EAAU\u0003\r\u00118\u000f\u001e\u0005\n\u0003[\u0003!\u0011!Q\u0001\n-\u000b\u0001D]3tk2$8+\u001a:jC2L'0\u0019;j_:$\u0016.\\3!\u0011%\t\t\f\u0001BC\u0002\u0013\u0005!*\u0001\nnK6|'/\u001f\"zi\u0016\u001c8\u000b]5mY\u0016$\u0007\u0006CAX=e\n)\fP\u001f\"\u0005\u0005]\u0016aA7cg\"I\u00111\u0018\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0014[\u0016lwN]=CsR,7o\u00159jY2,G\r\t\u0005\n\u0003\u007f\u0003!Q1A\u0005\u0002)\u000b\u0001\u0003Z5tW\nKH/Z:Ta&dG.\u001a3)\u0011\u0005uf$OAbyu\n#!!2\u0002\u0007\u0011\u00147\u000fC\u0005\u0002J\u0002\u0011\t\u0011)A\u0005\u0017\u0006\tB-[:l\u0005f$Xm]*qS2dW\r\u001a\u0011\t\u0013\u00055\u0007A!b\u0001\n\u0003Q\u0015a\u00059fC.,\u00050Z2vi&|g.T3n_JL\b\u0006CAf=e\n\t\u000eP\u001f\"\u0005\u0005M\u0017a\u00019f[\"I\u0011q\u001b\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0015a\u0016\f7.\u0012=fGV$\u0018n\u001c8NK6|'/\u001f\u0011\t\u0013\u0005m\u0007A!b\u0001\n\u0003Q\u0015AD5oaV$()\u001f;fgJ+\u0017\r\u001a\u0015\t\u00033t\u0012(a8={\u0005\u0012\u0011\u0011]\u0001\u0003SND\u0011\"!:\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002\u001f%t\u0007/\u001e;CsR,7OU3bI\u0002B\u0011\"!;\u0001\u0005\u000b\u0007I\u0011\u0001&\u0002!%t\u0007/\u001e;SK\u000e|'\u000fZ:SK\u0006$\u0007\u0006CAt=e\ni\u000fP\u001f\"\u0005\u0005=\u0018AA5s\u0011%\t\u0019\u0010\u0001B\u0001B\u0003%1*A\tj]B,HOU3d_J$7OU3bI\u0002B\u0011\"a>\u0001\u0005\u000b\u0007I\u0011\u0001&\u0002%=,H\u000f];u\u0005f$Xm],sSR$XM\u001c\u0015\t\u0003kt\u0012(a?={\u0005\u0012\u0011Q`\u0001\u0003_ND\u0011B!\u0001\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002'=,H\u000f];u\u0005f$Xm],sSR$XM\u001c\u0011\t\u0013\t\u0015\u0001A!b\u0001\n\u0003Q\u0015\u0001F8viB,HOU3d_J$7o\u0016:jiR,g\u000e\u000b\u0005\u0003\u0004yI$\u0011\u0002\u001f>C\t\u0011Y!\u0001\u0002pe\"I!q\u0002\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0016_V$\b/\u001e;SK\u000e|'\u000fZ:Xe&$H/\u001a8!\u0011%\u0011\u0019\u0002\u0001BC\u0002\u0013\u0005!*\u0001\u000etQV4g\r\\3SK6|G/\u001a\"m_\u000e\\7OR3uG\",G\r\u000b\u0005\u0003\u0012yI$q\u0003\u001f>C\t\u0011I\"\u0001\u0003te\nd\u0007\"\u0003B\u000f\u0001\t\u0005\t\u0015!\u0003L\u0003m\u0019\b.\u001e4gY\u0016\u0014V-\\8uK\ncwnY6t\r\u0016$8\r[3eA!I!\u0011\u0005\u0001\u0003\u0006\u0004%\tAS\u0001\u001ag\",hM\u001a7f\u0019>\u001c\u0017\r\u001c\"m_\u000e\\7OR3uG\",G\r\u000b\u0005\u0003 yI$Q\u0005\u001f>C\t\u00119#\u0001\u0003tY\nd\u0007\"\u0003B\u0016\u0001\t\u0005\t\u0015!\u0003L\u0003i\u0019\b.\u001e4gY\u0016dunY1m\u00052|7m[:GKR\u001c\u0007.\u001a3!\u0011%\u0011y\u0003\u0001BC\u0002\u0013\u0005!*\u0001\u000btQV4g\r\\3GKR\u001c\u0007nV1jiRKW.\u001a\u0015\t\u0005[q\u0012Ha\r={\u0005\u0012!QG\u0001\u0004gJ$\b\"\u0003B\u001d\u0001\t\u0005\t\u0015!\u0003L\u0003U\u0019\b.\u001e4gY\u00164U\r^2i/\u0006LG\u000fV5nK\u0002B\u0011B!\u0010\u0001\u0005\u000b\u0007I\u0011\u0001&\u0002-MDWO\u001a4mKJ+Wn\u001c;f\u0005f$Xm\u001d*fC\u0012D\u0003Ba\u000f\u001fs\t\u0005C(P\u0011\u0003\u0005\u0007\nAa\u001d:cs\"I!q\t\u0001\u0003\u0002\u0003\u0006IaS\u0001\u0018g\",hM\u001a7f%\u0016lw\u000e^3CsR,7OU3bI\u0002B\u0011Ba\u0013\u0001\u0005\u000b\u0007I\u0011\u0001&\u00029MDWO\u001a4mKJ+Wn\u001c;f\u0005f$Xm\u001d*fC\u0012$v\u000eR5tW\"B!\u0011\n\u0010:\u0005\u001fbT(\t\u0002\u0003R\u0005!1O\u001d2e\u0011%\u0011)\u0006\u0001B\u0001B\u0003%1*A\u000ftQV4g\r\\3SK6|G/\u001a\"zi\u0016\u001c(+Z1e)>$\u0015n]6!\u0011%\u0011I\u0006\u0001BC\u0002\u0013\u0005!*A\u000btQV4g\r\\3M_\u000e\fGNQ=uKN\u0014V-\u00193\t\u0013\tu\u0003A!A!\u0002\u0013Y\u0015AF:ik\u001a4G.\u001a'pG\u0006d')\u001f;fgJ+\u0017\r\u001a\u0011\t\u0013\t\u0005\u0004A!b\u0001\n\u0003Q\u0015AE:ik\u001a4G.\u001a*fG>\u0014Hm\u001d*fC\u0012D\u0003Ba\u0018\u001fs\t\u0015D(P\u0011\u0003\u0005O\n1a\u001d:s\u0011%\u0011Y\u0007\u0001B\u0001B\u0003%1*A\ntQV4g\r\\3SK\u000e|'\u000fZ:SK\u0006$\u0007\u0005C\u0005\u0003p\u0001\u0011)\u0019!C\u0001\u0015\u0006\u00192\u000f[;gM2,')\u001f;fg^\u0013\u0018\u000e\u001e;f]\"B!Q\u000e\u0010:\u0005gbT(\t\u0002\u0003v\u0005\u00191o^:\t\u0013\te\u0004A!A!\u0002\u0013Y\u0015\u0001F:ik\u001a4G.\u001a\"zi\u0016\u001cxK]5ui\u0016t\u0007\u0005C\u0005\u0003~\u0001\u0011)\u0019!C\u0001\u0015\u0006\u00012\u000f[;gM2,wK]5uKRKW.\u001a\u0015\t\u0005wr\u0012H!!={\u0005\u0012!1Q\u0001\u0004g^$\b\"\u0003BD\u0001\t\u0005\t\u0015!\u0003L\u0003E\u0019\b.\u001e4gY\u0016<&/\u001b;f)&lW\r\t\u0005\n\u0005\u0017\u0003!Q1A\u0005\u0002)\u000bQc\u001d5vM\u001adWMU3d_J$7o\u0016:jiR,g\u000e\u000b\u0005\u0003\nzI$q\u0012\u001f>C\t\u0011\t*A\u0002toJD\u0011B!&\u0001\u0005\u0003\u0005\u000b\u0011B&\u0002-MDWO\u001a4mKJ+7m\u001c:eg^\u0013\u0018\u000e\u001e;f]\u0002B\u0011B!'\u0001\u0005\u000b\u0007I\u0011\u0001\u001b\u0002\u000fM$\u0018mZ3JI\"I!Q\u0014\u0001\u0003\u0002\u0003\u0006I!N\u0001\tgR\fw-Z%eA!I!\u0011\u0015\u0001\u0003\u0006\u0004%\t\u0001N\u0001\u000fgR\fw-Z!ui\u0016l\u0007\u000f^%e\u0011%\u0011)\u000b\u0001B\u0001B\u0003%Q'A\bti\u0006<W-\u0011;uK6\u0004H/\u00133!\u0011\u001d\u0011I\u000b\u0001C\u0001\u0005W\u000ba\u0001P5oSRtD\u0003\u0015BW\u0005c\u0013\u0019L!.\u00038\ne&1\u0018B_\u0005\u007f\u0013\tMa1\u0003F\n\u001d'\u0011\u001aBf\u0005\u001b\u0014yM!5\u0003T\nU'q\u001bBm\u00057\u0014iNa8\u0003b\n\r(Q\u001dBt\u0005S\u0014YO!<\u0003p\nE(1\u001fB{\u0005o\u0014IPa?\u0003~B\u0019!q\u0016\u0001\u000e\u0003\tAaA\u0005BT\u0001\u0004)\u0002BB\u001a\u0003(\u0002\u0007Q\u0007\u0003\u0004C\u0005O\u0003\r!\u000e\u0005\u0007\u0013\n\u001d\u0006\u0019A&\t\rM\u00139\u000b1\u0001L\u0011\u00199&q\u0015a\u0001\u0017\"1aLa*A\u0002\u0001Da!\u001cBT\u0001\u0004\u0001\u0007BB\u0002\u0003(\u0002\u0007\u0001\r\u0003\u0004x\u0005O\u0003\r\u0001\u0019\u0005\b}\n\u001d\u0006\u0019AA\u0001\u0011!\tiAa*A\u0002\u0005E\u0001\u0002CA \u0005O\u0003\r!a\u0011\t\u000f\u0005=#q\u0015a\u0001\u0017\"9\u0011Q\fBT\u0001\u0004Y\u0005bBA6\u0005O\u0003\ra\u0013\u0005\b\u0003s\u00129\u000b1\u0001L\u0011\u001d\t9Ia*A\u0002-Cq!!&\u0003(\u0002\u00071\nC\u0004\u0002$\n\u001d\u0006\u0019A&\t\u000f\u0005E&q\u0015a\u0001\u0017\"9\u0011q\u0018BT\u0001\u0004Y\u0005bBAg\u0005O\u0003\ra\u0013\u0005\b\u00037\u00149\u000b1\u0001L\u0011\u001d\tIOa*A\u0002-Cq!a>\u0003(\u0002\u00071\nC\u0004\u0003\u0006\t\u001d\u0006\u0019A&\t\u000f\tM!q\u0015a\u0001\u0017\"9!\u0011\u0005BT\u0001\u0004Y\u0005b\u0002B\u0018\u0005O\u0003\ra\u0013\u0005\b\u0005{\u00119\u000b1\u0001L\u0011\u001d\u0011YEa*A\u0002-CqA!\u0017\u0003(\u0002\u00071\nC\u0004\u0003b\t\u001d\u0006\u0019A&\t\u000f\t=$q\u0015a\u0001\u0017\"9!Q\u0010BT\u0001\u0004Y\u0005b\u0002BF\u0005O\u0003\ra\u0013\u0005\b\u00053\u00139\u000b1\u00016\u0011\u001d\u0011\tKa*A\u0002UBaa!\u0001\u0001\t\u0003y\u0018A\u00035bg6+GO]5dg\"91Q\u0001\u0001\u0005\u0002\r\u001d\u0011!\u0002;p\u0003BLWCAB\u0005!\u0011\tYca\u0003\n\t\r5\u0011Q\u0006\u0002\t)\u0006\u001c8\u000eR1uC\"1a\b\u0001C\u0005\u0007#)\"aa\u0005\u0011\t1\u0019)\"N\u0005\u0004\u0007/i!!B!se\u0006L\b&BB\b?ej\u0004\u0006BB\b\u0007;\u0001Baa\b\u000405\u00111\u0011\u0005\u0006\u0004[\r\r\"\u0002BB\u0013\u0007O\tqA[1dWN|gN\u0003\u0003\u0004*\r-\u0012!\u00034bgR,'\u000f_7m\u0015\t\u0019i#A\u0002d_6LAa!\r\u0004\"\tQ!j]8o\u0013\u001etwN]3\t\r\rU\u0002\u0001\"\u0001K\u00039\u00198\r[3ek2,'\u000fR3mCfD\u0003ba\r s\reB(P\u0011\u0003\u0007w\t1\u0001\u001a7zQ\u0011\u0019\u0019d!\b\t\r\r\u0005\u0003\u0001\"\u0001K\u0003E9W\r\u001e;j]\u001e\u0014Vm];miRKW.\u001a\u0015\t\u0007\u007fy\u0012h!\u0012={\u0005\u00121qI\u0001\u0004OJ$\b\u0006BB \u0007;Aaa!\u0014\u0001\t\u0013y\u0016\u0001D1dGVlW\u000f\\1u_J\u001c\b\u0006CB&?e\u001a\t\u0006P\u001f\"\u0005\rM\u0013aA1dG\"\"11JB\u000f\u0011\u0019\u0019I\u0006\u0001C\u0005\u0015\u0006\t2\u000f[;gM2,Gk\u001c;bYJ+\u0017\rZ:)\u0011\r]s$OB/yu\n#aa\u0018\u0002\tM$(-\u001f\u0015\u0005\u0007/\u001ai\u0002\u0003\u0004\u0004f\u0001!IAS\u0001\u0013g\",hM\u001a7f)>$\u0018\r\u001c\"m_\u000e\\7\u000f\u000b\u0005\u0004d}I4\u0011\u000e\u001f>C\t\u0019Y'\u0001\u0003ti\nd\u0007\u0006BB2\u0007;Aaa!\u001d\u0001\t\u0013y\u0016!B3se>\u0014\b\u0006CB8?e\u001a)\bP\u001f\"\u0005\r]\u0014aA3se\"\"1qNB\u000f\u0001"
)
public class TaskDataWrapper {
   private final Long taskId;
   private final int index;
   private final int attempt;
   private final long launchTime;
   private final long resultFetchStart;
   private final long duration;
   private final String executorId;
   private final String host;
   private final String status;
   private final String taskLocality;
   private final boolean speculative;
   private final Seq<AccumulableInfo> accumulatorUpdates;
   private final Option<String> errorMessage;
   private final long executorDeserializeTime;
   private final long executorDeserializeCpuTime;
   private final long executorRunTime;
   private final long executorCpuTime;
   private final long resultSize;
   private final long jvmGcTime;
   private final long resultSerializationTime;
   private final long memoryBytesSpilled;
   private final long diskBytesSpilled;
   private final long peakExecutionMemory;
   private final long inputBytesRead;
   private final long inputRecordsRead;
   private final long outputBytesWritten;
   private final long outputRecordsWritten;
   private final long shuffleRemoteBlocksFetched;
   private final long shuffleLocalBlocksFetched;
   private final long shuffleFetchWaitTime;
   private final long shuffleRemoteBytesRead;
   private final long shuffleRemoteBytesReadToDisk;
   private final long shuffleLocalBytesRead;
   private final long shuffleRecordsRead;
   private final long shuffleBytesWritten;
   private final long shuffleWriteTime;
   private final long shuffleRecordsWritten;
   private final int stageId;
   private final int stageAttemptId;

   @KVIndex
   public Long taskId() {
      return this.taskId;
   }

   @KVIndex(
      value = "idx",
      parent = "stage"
   )
   public int index() {
      return this.index;
   }

   @KVIndex(
      value = "att",
      parent = "stage"
   )
   public int attempt() {
      return this.attempt;
   }

   @KVIndex(
      value = "lt",
      parent = "stage"
   )
   public long launchTime() {
      return this.launchTime;
   }

   public long resultFetchStart() {
      return this.resultFetchStart;
   }

   @KVIndex(
      value = "dur",
      parent = "stage"
   )
   public long duration() {
      return this.duration;
   }

   @KVIndex(
      value = "exe",
      parent = "stage"
   )
   public String executorId() {
      return this.executorId;
   }

   public String host() {
      return this.host;
   }

   @KVIndex(
      value = "sta",
      parent = "stage"
   )
   public String status() {
      return this.status;
   }

   @KVIndex(
      value = "loc",
      parent = "stage"
   )
   public String taskLocality() {
      return this.taskLocality;
   }

   public boolean speculative() {
      return this.speculative;
   }

   public Seq<AccumulableInfo> accumulatorUpdates() {
      return this.accumulatorUpdates;
   }

   public Option<String> errorMessage() {
      return this.errorMessage;
   }

   @KVIndex(
      value = "des",
      parent = "stage"
   )
   public long executorDeserializeTime() {
      return this.executorDeserializeTime;
   }

   @KVIndex(
      value = "dct",
      parent = "stage"
   )
   public long executorDeserializeCpuTime() {
      return this.executorDeserializeCpuTime;
   }

   @KVIndex(
      value = "ert",
      parent = "stage"
   )
   public long executorRunTime() {
      return this.executorRunTime;
   }

   @KVIndex(
      value = "ect",
      parent = "stage"
   )
   public long executorCpuTime() {
      return this.executorCpuTime;
   }

   @KVIndex(
      value = "rs",
      parent = "stage"
   )
   public long resultSize() {
      return this.resultSize;
   }

   @KVIndex(
      value = "gc",
      parent = "stage"
   )
   public long jvmGcTime() {
      return this.jvmGcTime;
   }

   @KVIndex(
      value = "rst",
      parent = "stage"
   )
   public long resultSerializationTime() {
      return this.resultSerializationTime;
   }

   @KVIndex(
      value = "mbs",
      parent = "stage"
   )
   public long memoryBytesSpilled() {
      return this.memoryBytesSpilled;
   }

   @KVIndex(
      value = "dbs",
      parent = "stage"
   )
   public long diskBytesSpilled() {
      return this.diskBytesSpilled;
   }

   @KVIndex(
      value = "pem",
      parent = "stage"
   )
   public long peakExecutionMemory() {
      return this.peakExecutionMemory;
   }

   @KVIndex(
      value = "is",
      parent = "stage"
   )
   public long inputBytesRead() {
      return this.inputBytesRead;
   }

   @KVIndex(
      value = "ir",
      parent = "stage"
   )
   public long inputRecordsRead() {
      return this.inputRecordsRead;
   }

   @KVIndex(
      value = "os",
      parent = "stage"
   )
   public long outputBytesWritten() {
      return this.outputBytesWritten;
   }

   @KVIndex(
      value = "or",
      parent = "stage"
   )
   public long outputRecordsWritten() {
      return this.outputRecordsWritten;
   }

   @KVIndex(
      value = "srbl",
      parent = "stage"
   )
   public long shuffleRemoteBlocksFetched() {
      return this.shuffleRemoteBlocksFetched;
   }

   @KVIndex(
      value = "slbl",
      parent = "stage"
   )
   public long shuffleLocalBlocksFetched() {
      return this.shuffleLocalBlocksFetched;
   }

   @KVIndex(
      value = "srt",
      parent = "stage"
   )
   public long shuffleFetchWaitTime() {
      return this.shuffleFetchWaitTime;
   }

   @KVIndex(
      value = "srby",
      parent = "stage"
   )
   public long shuffleRemoteBytesRead() {
      return this.shuffleRemoteBytesRead;
   }

   @KVIndex(
      value = "srbd",
      parent = "stage"
   )
   public long shuffleRemoteBytesReadToDisk() {
      return this.shuffleRemoteBytesReadToDisk;
   }

   public long shuffleLocalBytesRead() {
      return this.shuffleLocalBytesRead;
   }

   @KVIndex(
      value = "srr",
      parent = "stage"
   )
   public long shuffleRecordsRead() {
      return this.shuffleRecordsRead;
   }

   @KVIndex(
      value = "sws",
      parent = "stage"
   )
   public long shuffleBytesWritten() {
      return this.shuffleBytesWritten;
   }

   @KVIndex(
      value = "swt",
      parent = "stage"
   )
   public long shuffleWriteTime() {
      return this.shuffleWriteTime;
   }

   @KVIndex(
      value = "swr",
      parent = "stage"
   )
   public long shuffleRecordsWritten() {
      return this.shuffleRecordsWritten;
   }

   public int stageId() {
      return this.stageId;
   }

   public int stageAttemptId() {
      return this.stageAttemptId;
   }

   public boolean hasMetrics() {
      return this.executorDeserializeTime() >= 0L;
   }

   public TaskData toApi() {
      Option metrics = (Option)(this.hasMetrics() ? new Some(new TaskMetrics(this.executorDeserializeTime(), this.executorDeserializeCpuTime(), this.executorRunTime(), this.executorCpuTime(), this.resultSize(), this.jvmGcTime(), this.resultSerializationTime(), this.memoryBytesSpilled(), this.diskBytesSpilled(), this.peakExecutionMemory(), new InputMetrics(this.inputBytesRead(), this.inputRecordsRead()), new OutputMetrics(this.outputBytesWritten(), this.outputRecordsWritten()), new ShuffleReadMetrics(this.shuffleRemoteBlocksFetched(), this.shuffleLocalBlocksFetched(), this.shuffleFetchWaitTime(), this.shuffleRemoteBytesRead(), this.shuffleRemoteBytesReadToDisk(), this.shuffleLocalBytesRead(), this.shuffleRecordsRead()), new ShuffleWriteMetrics(this.shuffleBytesWritten(), this.shuffleWriteTime(), this.shuffleRecordsWritten()))) : None$.MODULE$);
      return new TaskData(Predef$.MODULE$.Long2long(this.taskId()), this.index(), this.attempt(), new Date(this.launchTime()), (Option)(this.resultFetchStart() > 0L ? new Some(new Date(this.resultFetchStart())) : None$.MODULE$), (Option)(this.duration() > 0L ? new Some(BoxesRunTime.boxToLong(this.duration())) : None$.MODULE$), this.executorId(), this.host(), this.status(), this.taskLocality(), this.speculative(), this.accumulatorUpdates(), this.errorMessage(), metrics);
   }

   @JsonIgnore
   @KVIndex("stage")
   private int[] stage() {
      return new int[]{this.stageId(), this.stageAttemptId()};
   }

   @JsonIgnore
   @KVIndex(
      value = "dly",
      parent = "stage"
   )
   public long schedulerDelay() {
      return this.hasMetrics() ? AppStatusUtils$.MODULE$.schedulerDelay(this.launchTime(), this.resultFetchStart(), this.duration(), this.executorDeserializeTime(), this.resultSerializationTime(), this.executorRunTime()) : -1L;
   }

   @JsonIgnore
   @KVIndex(
      value = "grt",
      parent = "stage"
   )
   public long gettingResultTime() {
      return this.hasMetrics() ? AppStatusUtils$.MODULE$.gettingResultTime(this.launchTime(), this.resultFetchStart(), this.duration()) : -1L;
   }

   @JsonIgnore
   @KVIndex(
      value = "acc",
      parent = "stage"
   )
   private String accumulators() {
      String var10000;
      if (this.accumulatorUpdates().nonEmpty()) {
         AccumulableInfo acc = (AccumulableInfo)this.accumulatorUpdates().head();
         var10000 = (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ":", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{acc.name(), acc.value()}));
      } else {
         var10000 = "";
      }

      return var10000;
   }

   @JsonIgnore
   @KVIndex(
      value = "stby",
      parent = "stage"
   )
   private long shuffleTotalReads() {
      return this.hasMetrics() ? this.shuffleLocalBytesRead() + this.shuffleRemoteBytesRead() : -1L;
   }

   @JsonIgnore
   @KVIndex(
      value = "stbl",
      parent = "stage"
   )
   private long shuffleTotalBlocks() {
      return this.hasMetrics() ? this.shuffleLocalBlocksFetched() + this.shuffleRemoteBlocksFetched() : -1L;
   }

   @JsonIgnore
   @KVIndex(
      value = "err",
      parent = "stage"
   )
   private String error() {
      return this.errorMessage().isDefined() ? (String)this.errorMessage().get() : "";
   }

   public TaskDataWrapper(Long taskId, int index, int attempt, long launchTime, long resultFetchStart, long duration, String executorId, String host, String status, String taskLocality, boolean speculative, Seq<AccumulableInfo> accumulatorUpdates, Option<String> errorMessage, long executorDeserializeTime, long executorDeserializeCpuTime, long executorRunTime, long executorCpuTime, long resultSize, long jvmGcTime, long resultSerializationTime, long memoryBytesSpilled, long diskBytesSpilled, long peakExecutionMemory, long inputBytesRead, long inputRecordsRead, long outputBytesWritten, long outputRecordsWritten, long shuffleRemoteBlocksFetched, long shuffleLocalBlocksFetched, long shuffleFetchWaitTime, long shuffleRemoteBytesRead, long shuffleRemoteBytesReadToDisk, long shuffleLocalBytesRead, long shuffleRecordsRead, long shuffleBytesWritten, long shuffleWriteTime, long shuffleRecordsWritten, int stageId, int stageAttemptId) {
      this.taskId = taskId;
      this.index = index;
      this.attempt = attempt;
      this.launchTime = launchTime;
      this.resultFetchStart = resultFetchStart;
      this.duration = duration;
      this.executorId = executorId;
      this.host = host;
      this.status = status;
      this.taskLocality = taskLocality;
      this.speculative = speculative;
      this.accumulatorUpdates = accumulatorUpdates;
      this.errorMessage = errorMessage;
      this.executorDeserializeTime = executorDeserializeTime;
      this.executorDeserializeCpuTime = executorDeserializeCpuTime;
      this.executorRunTime = executorRunTime;
      this.executorCpuTime = executorCpuTime;
      this.resultSize = resultSize;
      this.jvmGcTime = jvmGcTime;
      this.resultSerializationTime = resultSerializationTime;
      this.memoryBytesSpilled = memoryBytesSpilled;
      this.diskBytesSpilled = diskBytesSpilled;
      this.peakExecutionMemory = peakExecutionMemory;
      this.inputBytesRead = inputBytesRead;
      this.inputRecordsRead = inputRecordsRead;
      this.outputBytesWritten = outputBytesWritten;
      this.outputRecordsWritten = outputRecordsWritten;
      this.shuffleRemoteBlocksFetched = shuffleRemoteBlocksFetched;
      this.shuffleLocalBlocksFetched = shuffleLocalBlocksFetched;
      this.shuffleFetchWaitTime = shuffleFetchWaitTime;
      this.shuffleRemoteBytesRead = shuffleRemoteBytesRead;
      this.shuffleRemoteBytesReadToDisk = shuffleRemoteBytesReadToDisk;
      this.shuffleLocalBytesRead = shuffleLocalBytesRead;
      this.shuffleRecordsRead = shuffleRecordsRead;
      this.shuffleBytesWritten = shuffleBytesWritten;
      this.shuffleWriteTime = shuffleWriteTime;
      this.shuffleRecordsWritten = shuffleRecordsWritten;
      this.stageId = stageId;
      this.stageAttemptId = stageAttemptId;
   }
}
