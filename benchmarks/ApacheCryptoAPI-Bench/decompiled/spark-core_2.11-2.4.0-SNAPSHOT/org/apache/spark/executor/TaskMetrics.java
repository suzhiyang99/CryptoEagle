package org.apache.spark.executor;

import java.util.List;
import org.apache.spark.InternalAccumulator;
import org.apache.spark.InternalAccumulator$;
import org.apache.spark.SparkContext;
import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.scheduler.AccumulableInfo;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockStatus;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.LinkedHashMap$;
import scala.collection.mutable.Map;
import scala.collection.mutable.MapLike;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\r\re\u0001B\u0001\u0003\u0001-\u00111\u0002V1tW6+GO]5dg*\u00111\u0001B\u0001\tKb,7-\u001e;pe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001bMI!\u0001\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\rY\u0001A\u0011\u0001\u0003\u0018\u0003\u0019a\u0014N\\5u}Q\t\u0001\u0004\u0005\u0002\u001a\u00015\t!\u0001C\u0004\u001c\u0001\t\u0007I\u0011\u0002\u000f\u00021}+\u00070Z2vi>\u0014H)Z:fe&\fG.\u001b>f)&lW-F\u0001\u001e!\tq\u0012%D\u0001 \u0015\t\u0001C!\u0001\u0003vi&d\u0017B\u0001\u0012 \u0005=auN\\4BG\u000e,X.\u001e7bi>\u0014\bB\u0002\u0013\u0001A\u0003%Q$A\r`Kb,7-\u001e;pe\u0012+7/\u001a:jC2L'0\u001a+j[\u0016\u0004\u0003b\u0002\u0014\u0001\u0005\u0004%I\u0001H\u0001\u001c?\u0016DXmY;u_J$Um]3sS\u0006d\u0017N_3DaV$\u0016.\\3\t\r!\u0002\u0001\u0015!\u0003\u001e\u0003qyV\r_3dkR|'\u000fR3tKJL\u0017\r\\5{K\u000e\u0003X\u000fV5nK\u0002BqA\u000b\u0001C\u0002\u0013%A$\u0001\t`Kb,7-\u001e;peJ+h\u000eV5nK\"1A\u0006\u0001Q\u0001\nu\t\u0011cX3yK\u000e,Ho\u001c:Sk:$\u0016.\\3!\u0011\u001dq\u0003A1A\u0005\nq\t\u0001cX3yK\u000e,Ho\u001c:DaV$\u0016.\\3\t\rA\u0002\u0001\u0015!\u0003\u001e\u0003EyV\r_3dkR|'o\u00119v)&lW\r\t\u0005\be\u0001\u0011\r\u0011\"\u0003\u001d\u0003-y&/Z:vYR\u001c\u0016N_3\t\rQ\u0002\u0001\u0015!\u0003\u001e\u00031y&/Z:vYR\u001c\u0016N_3!\u0011\u001d1\u0004A1A\u0005\nq\t!b\u00186w[\u001e\u001bE+[7f\u0011\u0019A\u0004\u0001)A\u0005;\u0005YqL\u001b<n\u000f\u000e#\u0016.\\3!\u0011\u001dQ\u0004A1A\u0005\nq\t\u0001d\u0018:fgVdGoU3sS\u0006d\u0017N_1uS>tG+[7f\u0011\u0019a\u0004\u0001)A\u0005;\u0005IrL]3tk2$8+\u001a:jC2L'0\u0019;j_:$\u0016.\\3!\u0011\u001dq\u0004A1A\u0005\nq\t1cX7f[>\u0014\u0018PQ=uKN\u001c\u0006/\u001b7mK\u0012Da\u0001\u0011\u0001!\u0002\u0013i\u0012\u0001F0nK6|'/\u001f\"zi\u0016\u001c8\u000b]5mY\u0016$\u0007\u0005C\u0004C\u0001\t\u0007I\u0011\u0002\u000f\u0002#}#\u0017n]6CsR,7o\u00159jY2,G\r\u0003\u0004E\u0001\u0001\u0006I!H\u0001\u0013?\u0012L7o\u001b\"zi\u0016\u001c8\u000b]5mY\u0016$\u0007\u0005C\u0004G\u0001\t\u0007I\u0011\u0002\u000f\u0002)}\u0003X-Y6Fq\u0016\u001cW\u000f^5p]6+Wn\u001c:z\u0011\u0019A\u0005\u0001)A\u0005;\u0005)r\f]3bW\u0016CXmY;uS>tW*Z7pef\u0004\u0003b\u0002&\u0001\u0005\u0004%IaS\u0001\u0016?V\u0004H-\u0019;fI\ncwnY6Ti\u0006$Xo]3t+\u0005a\u0005c\u0001\u0010N\u001f&\u0011aj\b\u0002\u0016\u0007>dG.Z2uS>t\u0017iY2v[Vd\u0017\r^8s!\u0011i\u0001K\u0015-\n\u0005Es!A\u0002+va2,'\u0007\u0005\u0002T-6\tAK\u0003\u0002V\t\u000591\u000f^8sC\u001e,\u0017BA,U\u0005\u001d\u0011En\\2l\u0013\u0012\u0004\"aU-\n\u0005i#&a\u0003\"m_\u000e\\7\u000b^1ukNDa\u0001\u0018\u0001!\u0002\u0013a\u0015AF0va\u0012\fG/\u001a3CY>\u001c7n\u0015;biV\u001cXm\u001d\u0011\t\u000by\u0003A\u0011A0\u0002/\u0015DXmY;u_J$Um]3sS\u0006d\u0017N_3US6,W#\u00011\u0011\u00055\t\u0017B\u00012\u000f\u0005\u0011auN\\4\t\u000b\u0011\u0004A\u0011A0\u00025\u0015DXmY;u_J$Um]3sS\u0006d\u0017N_3DaV$\u0016.\\3\t\u000b\u0019\u0004A\u0011A0\u0002\u001f\u0015DXmY;u_J\u0014VO\u001c+j[\u0016DQ\u0001\u001b\u0001\u0005\u0002}\u000bq\"\u001a=fGV$xN]\"qkRKW.\u001a\u0005\u0006U\u0002!\taX\u0001\u000be\u0016\u001cX\u000f\u001c;TSj,\u0007\"\u00027\u0001\t\u0003y\u0016!\u00036w[\u001e\u001bE+[7f\u0011\u0015q\u0007\u0001\"\u0001`\u0003]\u0011Xm];miN+'/[1mSj\fG/[8o)&lW\rC\u0003q\u0001\u0011\u0005q,\u0001\nnK6|'/\u001f\"zi\u0016\u001c8\u000b]5mY\u0016$\u0007\"\u0002:\u0001\t\u0003y\u0016\u0001\u00053jg.\u0014\u0015\u0010^3t'BLG\u000e\\3e\u0011\u0015!\b\u0001\"\u0001`\u0003M\u0001X-Y6Fq\u0016\u001cW\u000f^5p]6+Wn\u001c:z\u0011\u00151\b\u0001\"\u0001x\u0003Q)\b\u000fZ1uK\u0012\u0014En\\2l'R\fG/^:fgV\t\u0001\u0010\u0005\u0003z\u0003\u0007yeB\u0001>\u0000\u001d\tYh0D\u0001}\u0015\ti(\"\u0001\u0004=e>|GOP\u0005\u0002\u001f%\u0019\u0011\u0011\u0001\b\u0002\u000fA\f7m[1hK&!\u0011QAA\u0004\u0005\r\u0019V-\u001d\u0006\u0004\u0003\u0003q\u0001\u0002CA\u0006\u0001\u0011\u0005A!!\u0004\u00025M,G/\u0012=fGV$xN\u001d#fg\u0016\u0014\u0018.\u00197ju\u0016$\u0016.\\3\u0015\t\u0005=\u0011Q\u0003\t\u0004\u001b\u0005E\u0011bAA\n\u001d\t!QK\\5u\u0011\u001d\t9\"!\u0003A\u0002\u0001\f\u0011A\u001e\u0005\t\u00037\u0001A\u0011\u0001\u0003\u0002\u001e\u0005i2/\u001a;Fq\u0016\u001cW\u000f^8s\t\u0016\u001cXM]5bY&TXm\u00119v)&lW\r\u0006\u0003\u0002\u0010\u0005}\u0001bBA\f\u00033\u0001\r\u0001\u0019\u0005\t\u0003G\u0001A\u0011\u0001\u0003\u0002&\u0005\u00112/\u001a;Fq\u0016\u001cW\u000f^8s%VtG+[7f)\u0011\ty!a\n\t\u000f\u0005]\u0011\u0011\u0005a\u0001A\"A\u00111\u0006\u0001\u0005\u0002\u0011\ti#\u0001\ntKR,\u00050Z2vi>\u00148\t];US6,G\u0003BA\b\u0003_Aq!a\u0006\u0002*\u0001\u0007\u0001\r\u0003\u0005\u00024\u0001!\t\u0001BA\u001b\u00035\u0019X\r\u001e*fgVdGoU5{KR!\u0011qBA\u001c\u0011\u001d\t9\"!\rA\u0002\u0001D\u0001\"a\u000f\u0001\t\u0003!\u0011QH\u0001\rg\u0016$(J^7H\u0007RKW.\u001a\u000b\u0005\u0003\u001f\ty\u0004C\u0004\u0002\u0018\u0005e\u0002\u0019\u00011\t\u0011\u0005\r\u0003\u0001\"\u0001\u0005\u0003\u000b\n!d]3u%\u0016\u001cX\u000f\u001c;TKJL\u0017\r\\5{CRLwN\u001c+j[\u0016$B!a\u0004\u0002H!9\u0011qCA!\u0001\u0004\u0001\u0007\u0002CA&\u0001\u0011\u0005A!!\u0014\u0002+%t7-T3n_JL()\u001f;fgN\u0003\u0018\u000e\u001c7fIR!\u0011qBA(\u0011\u001d\t9\"!\u0013A\u0002\u0001D\u0001\"a\u0015\u0001\t\u0003!\u0011QK\u0001\u0014S:\u001cG)[:l\u0005f$Xm]*qS2dW\r\u001a\u000b\u0005\u0003\u001f\t9\u0006C\u0004\u0002\u0018\u0005E\u0003\u0019\u00011\t\u0011\u0005m\u0003\u0001\"\u0001\u0005\u0003;\na#\u001b8d!\u0016\f7.\u0012=fGV$\u0018n\u001c8NK6|'/\u001f\u000b\u0005\u0003\u001f\ty\u0006C\u0004\u0002\u0018\u0005e\u0003\u0019\u00011\t\u0011\u0005\r\u0004\u0001\"\u0001\u0005\u0003K\nq#\u001b8d+B$\u0017\r^3e\u00052|7m[*uCR,8/Z:\u0015\t\u0005=\u0011q\r\u0005\b\u0003/\t\t\u00071\u0001P\u0011!\tY\u0007\u0001C\u0001\t\u00055\u0014aF:fiV\u0003H-\u0019;fI\ncwnY6Ti\u0006$Xo]3t)\u0011\ty!a\u001c\t\u0011\u0005]\u0011\u0011\u000ea\u0001\u0003c\u0002R!a\u001d\u0002|=k!!!\u001e\u000b\u0007\u0001\n9H\u0003\u0002\u0002z\u0005!!.\u0019<b\u0013\u0011\ti(!\u001e\u0003\t1K7\u000f\u001e\u0005\t\u0003W\u0002A\u0011\u0001\u0003\u0002\u0002R!\u0011qBAB\u0011\u001d\t9\"a A\u0002aD\u0011\"a\"\u0001\u0005\u0004%\t!!#\u0002\u0019%t\u0007/\u001e;NKR\u0014\u0018nY:\u0016\u0005\u0005-\u0005cA\r\u0002\u000e&\u0019\u0011q\u0012\u0002\u0003\u0019%s\u0007/\u001e;NKR\u0014\u0018nY:\t\u0011\u0005M\u0005\u0001)A\u0005\u0003\u0017\u000bQ\"\u001b8qkRlU\r\u001e:jGN\u0004\u0003\"CAL\u0001\t\u0007I\u0011AAM\u00035yW\u000f\u001e9vi6+GO]5dgV\u0011\u00111\u0014\t\u00043\u0005u\u0015bAAP\u0005\tiq*\u001e;qkRlU\r\u001e:jGND\u0001\"a)\u0001A\u0003%\u00111T\u0001\u000f_V$\b/\u001e;NKR\u0014\u0018nY:!\u0011%\t9\u000b\u0001b\u0001\n\u0003\tI+\u0001\ntQV4g\r\\3SK\u0006$W*\u001a;sS\u000e\u001cXCAAV!\rI\u0012QV\u0005\u0004\u0003_\u0013!AE*ik\u001a4G.\u001a*fC\u0012lU\r\u001e:jGND\u0001\"a-\u0001A\u0003%\u00111V\u0001\u0014g\",hM\u001a7f%\u0016\fG-T3ue&\u001c7\u000f\t\u0005\n\u0003o\u0003!\u0019!C\u0001\u0003s\u000b1c\u001d5vM\u001adWm\u0016:ji\u0016lU\r\u001e:jGN,\"!a/\u0011\u0007e\ti,C\u0002\u0002@\n\u00111c\u00155vM\u001adWm\u0016:ji\u0016lU\r\u001e:jGND\u0001\"a1\u0001A\u0003%\u00111X\u0001\u0015g\",hM\u001a7f/JLG/Z'fiJL7m\u001d\u0011\t\u0015\u0005\u001d\u0007\u0001#b\u0001\n\u0013\tI-\u0001\fuK6\u00048\u000b[;gM2,'+Z1e\u001b\u0016$(/[2t+\t\tY\r\u0005\u0004\u0002N\u0006]\u00171\\\u0007\u0003\u0003\u001fTA!!5\u0002T\u00069Q.\u001e;bE2,'bAAk\u001d\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\t\u0005e\u0017q\u001a\u0002\f\u0003J\u0014\u0018-\u001f\"vM\u001a,'\u000fE\u0002\u001a\u0003;L1!a8\u0003\u0005Y!V-\u001c9TQV4g\r\\3SK\u0006$W*\u001a;sS\u000e\u001c\bBCAr\u0001!\u0005\t\u0015)\u0003\u0002L\u00069B/Z7q'\",hM\u001a7f%\u0016\fG-T3ue&\u001c7\u000f\t\u0015\u0005\u0003C\f9\u000fE\u0002\u000e\u0003SL1!a;\u000f\u0005%!(/\u00198tS\u0016tG\u000f\u0003\u0005\u0002p\u0002!\t\u0001BAy\u0003q\u0019'/Z1uKR+W\u000e]*ik\u001a4G.\u001a*fC\u0012lU\r\u001e:jGN$\"!a7\t\u0011\u0005U\b\u0001\"\u0001\u0005\u0003o\fq#\\3sO\u0016\u001c\u0006.\u001e4gY\u0016\u0014V-\u00193NKR\u0014\u0018nY:\u0015\u0005\u0005=\u0001BCA~\u0001\t\u0007I\u0011\u0001\u0003\u0002~\u0006IA/Z:u\u0003\u000e\u001cW/\\\u000b\u0003\u0003\u007f\u0004B!\u0004B\u0001;%\u0019!1\u0001\b\u0003\r=\u0003H/[8o\u0011!\u00119\u0001\u0001Q\u0001\n\u0005}\u0018A\u0003;fgR\f5mY;nA!Y!1\u0002\u0001\t\u0006\u0004%\t\u0001\u0002B\u0007\u00031q\u0017-\\3U_\u0006\u001b7-^7t+\t\u0011y\u0001\u0005\u0005\u0002N\nE!Q\u0003B\u0011\u0013\u0011\u0011\u0019\"a4\u0003\u00075\u000b\u0007\u000f\u0005\u0003\u0003\u0018\tuQB\u0001B\r\u0015\u0011\u0011Y\"a\u001e\u0002\t1\fgnZ\u0005\u0005\u0005?\u0011IB\u0001\u0004TiJLgn\u001a\u0019\u0007\u0005G\u0011iCa\u0015\u0011\u000fy\u0011)C!\u000b\u0003R%\u0019!qE\u0010\u0003\u001b\u0005\u001b7-^7vY\u0006$xN\u001d,3!\u0011\u0011YC!\f\r\u0001\u0011Y!q\u0006\u0001\u0002\u0002\u0003\u0005)\u0011\u0001B\u0019\u0005\ryvgN\t\u0005\u0005g\u0011iDE\u0003\u00036=\u0013ID\u0002\u0004\u00038\u0001\u0001!1\u0007\u0002\ryI,g-\u001b8f[\u0016tGO\u0010\t\u0005\u0005/\u0011Y$C\u0002c\u00053\u0011bAa\u0010\u0003B\t\u001dcA\u0002B\u001c\u0001\u0001\u0011i\u0004\u0005\u0003\u0003\u0018\t\r\u0013\u0002\u0002B#\u00053\u0011aa\u00142kK\u000e$\b\u0003\u0002B%\u0005\u001fj!Aa\u0013\u000b\t\t5\u0013qO\u0001\u0003S>L1\u0001\u0006B&!\u0011\u0011YCa\u0015\u0005\u0017\tU\u0003!!A\u0001\u0002\u000b\u0005!q\u000b\u0002\u0004?^B\u0014\u0003\u0002B-\u0005\u0003\u0012bAa\u0017\u0002r\tebA\u0002B\u001c\u0001\u0001\u0011I\u0006\u0003\u0006\u0003`\u0001A\t\u0011)Q\u0005\u0005\u001f\tQB\\1nKR{\u0017iY2v[N\u0004\u0003\u0006\u0002B/\u0003OD1B!\u001a\u0001\u0011\u000b\u0007I\u0011\u0001\u0003\u0003h\u0005q\u0011N\u001c;fe:\fG.Q2dk6\u001cXC\u0001B5!\u0015I\u00181\u0001B6a\u0019\u0011iG!\u001d\u0003\u001cB9aD!\n\u0003p\te\u0005\u0003\u0002B\u0016\u0005c\"ABa\u001d\u0003v\u0005\u0005\t\u0011!B\u0001\u0005\u0007\u00131a\u0018\u00132\u0011)\u00119\b\u0001E\u0001B\u0003&!\u0011P\u0001\u0010S:$XM\u001d8bY\u0006\u001b7-^7tAA)\u00110a\u0001\u0003|A2!Q\u0010BA\u0005'\u0003rA\bB\u0013\u0005\u007f\u0012\t\n\u0005\u0003\u0003,\t\u0005E\u0001\u0004B:\u0005k\n\t\u0011!A\u0003\u0002\t\r\u0015\u0003\u0002BC\u0005\u0017\u00032!\u0004BD\u0013\r\u0011II\u0004\u0002\b\u001d>$\b.\u001b8h!\ri!QR\u0005\u0004\u0005\u001fs!aA!osB!!1\u0006BJ\t1\u0011)J!\u001e\u0002\u0002\u0003\u0005)\u0011\u0001BB\u0005\ryFE\r\u0015\u0005\u0005k\n9\u000f\u0005\u0003\u0003,\tmE\u0001\u0004BK\u0005k\n\t\u0011!A\u0003\u0002\t\r\u0005\u0002\u0003BP\u0001\u0011\u0005AA!)\u0002\u0011I,w-[:uKJ$B!a\u0004\u0003$\"A!Q\u0015BO\u0001\u0004\u00119+\u0001\u0002tGB!!\u0011\u0016BV\u001b\u0005!\u0011b\u0001BW\t\ta1\u000b]1sW\u000e{g\u000e^3yi\"Y!\u0011\u0017\u0001\t\u0006\u0004%\t\u0001\u0002BZ\u00039)\u0007\u0010^3s]\u0006d\u0017iY2v[N,\"A!.\u0011\r\u00055\u0017q\u001bB\\a\u0019\u0011IL!0\u0003JB9aD!\n\u0003<\n\u001d\u0007\u0003\u0002B\u0016\u0005{#ABa0\u0003B\u0006\u0005\t\u0011!B\u0001\u0005\u0007\u00131a\u0018\u00134\u0011)\u0011\u0019\r\u0001E\u0001B\u0003&!QW\u0001\u0010Kb$XM\u001d8bY\u0006\u001b7-^7tA!\"!\u0011YAt!\u0011\u0011YC!3\u0005\u0019\t-'\u0011YA\u0001\u0002\u0003\u0015\tAa!\u0003\u0007}#C\u0007\u0003\u0005\u0003P\u0002!\t\u0001\u0002Bi\u0003M\u0011XmZ5ti\u0016\u0014\u0018iY2v[Vd\u0017\r^8s)\u0011\tyAa5\t\u0011\tU'Q\u001aa\u0001\u0005/\f\u0011!\u0019\u0019\u0007\u00053\u0014iNa9\u0011\u000fy\u0011)Ca7\u0003bB!!1\u0006Bo\t1\u0011yNa5\u0002\u0002\u0003\u0005)\u0011\u0001BB\u0005\ryF%\u000e\t\u0005\u0005W\u0011\u0019\u000f\u0002\u0007\u0003f\nM\u0017\u0011!A\u0001\u0006\u0003\u0011\u0019IA\u0002`IYB\u0001B!;\u0001\t\u0003!!1^\u0001\rC\u000e\u001cW/\\;mCR|'o\u001d\u000b\u0003\u0005[\u0004R!_A\u0002\u0005_\u0004dA!=\u0003v\nm\bc\u0002\u0010\u0003&\tM(\u0011 \t\u0005\u0005W\u0011)\u0010\u0002\u0007\u0003x\n\u001d\u0018\u0011!A\u0001\u0006\u0003\u0011\u0019IA\u0002`I]\u0002BAa\u000b\u0003|\u0012a!Q Bt\u0003\u0003\u0005\tQ!\u0001\u0003\u0004\n\u0019q\f\n\u001d\t\u0011\r\u0005\u0001\u0001\"\u0001\u0005\u0007\u0007\tQC\\8o5\u0016\u0014x.\u00138uKJt\u0017\r\\!dGVl7\u000f\u0006\u0002\u0004\u0006A)\u00110a\u0001\u0004\bA21\u0011BB\u0007\u0007'\u0001rA\bB\u0013\u0007\u0017\u0019\t\u0002\u0005\u0003\u0003,\r5A\u0001DB\b\u0005\u007f\f\t\u0011!A\u0003\u0002\t\r%aA0%sA!!1FB\n\t1\u0019)Ba@\u0002\u0002\u0003\u0005)\u0011\u0001BB\u0005\u0011yF%\r\u0019)\u0007\u0001\u0019I\u0002\u0005\u0003\u0004\u001c\r\u0005RBAB\u000f\u0015\r\u0019y\u0002B\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BB\u0012\u0007;\u0011A\u0002R3wK2|\u0007/\u001a:Ba&<\u0001ba\n\u0003\u0011\u0003!1\u0011F\u0001\f)\u0006\u001c8.T3ue&\u001c7\u000fE\u0002\u001a\u0007W1q!\u0001\u0002\t\u0002\u0011\u0019ic\u0005\u0004\u0004,1\u0019yC\u0005\t\u0005\u0007c\u00199$\u0004\u0002\u00044)\u00191Q\u0007\u0003\u0002\u0011%tG/\u001a:oC2LAa!\u000f\u00044\t9Aj\\4hS:<\u0007b\u0002\f\u0004,\u0011\u00051Q\b\u000b\u0003\u0007SA\u0001b!\u0011\u0004,\u0011\u000511I\u0001\u0006K6\u0004H/_\u000b\u00021!A1qIB\u0016\t\u0003\u0019\u0019%\u0001\u0006sK\u001eL7\u000f^3sK\u0012D\u0001ba\u0013\u0004,\u0011\u00051QJ\u0001\u0015MJ|W.Q2dk6,H.\u0019;pe&sgm\\:\u0015\u0007a\u0019y\u0005\u0003\u0005\u0004R\r%\u0003\u0019AB*\u0003\u0015IgNZ8t!\u0015I\u00181AB+!\u0011\u00199f!\u0018\u000e\u0005\re#bAB.\t\u0005I1o\u00195fIVdWM]\u0005\u0005\u0007?\u001aIFA\bBG\u000e,X.\u001e7bE2,\u0017J\u001c4p\u0011!\u0019\u0019ga\u000b\u0005\u0002\r\u0015\u0014\u0001\u00054s_6\f5mY;nk2\fGo\u001c:t)\rA2q\r\u0005\t\u0007S\u001a\t\u00071\u0001\u0004l\u00051\u0011mY2v[N\u0004R!_A\u0002\u0007[\u0002daa\u001c\u0004t\re\u0004c\u0002\u0010\u0003&\rE4q\u000f\t\u0005\u0005W\u0019\u0019\b\u0002\u0007\u0004v\r\u001d\u0014\u0011!A\u0001\u0006\u0003\u0011\u0019I\u0001\u0003`IE\n\u0004\u0003\u0002B\u0016\u0007s\"Aba\u001f\u0004h\u0005\u0005\t\u0011!B\u0001\u0005\u0007\u0013Aa\u0018\u00132e!Q1qPB\u0016\u0003\u0003%Ia!!\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005\u0003\u0002"
)
public class TaskMetrics implements Serializable {
   private final LongAccumulator _executorDeserializeTime = new LongAccumulator();
   private final LongAccumulator _executorDeserializeCpuTime = new LongAccumulator();
   private final LongAccumulator _executorRunTime = new LongAccumulator();
   private final LongAccumulator _executorCpuTime = new LongAccumulator();
   private final LongAccumulator org$apache$spark$executor$TaskMetrics$$_resultSize = new LongAccumulator();
   private final LongAccumulator _jvmGCTime = new LongAccumulator();
   private final LongAccumulator _resultSerializationTime = new LongAccumulator();
   private final LongAccumulator _memoryBytesSpilled = new LongAccumulator();
   private final LongAccumulator _diskBytesSpilled = new LongAccumulator();
   private final LongAccumulator _peakExecutionMemory = new LongAccumulator();
   private final CollectionAccumulator<Tuple2<BlockId, BlockStatus>> _updatedBlockStatuses = new CollectionAccumulator<Tuple2<BlockId, BlockStatus>>();
   private final InputMetrics inputMetrics = new InputMetrics();
   private final OutputMetrics outputMetrics = new OutputMetrics();
   private final ShuffleReadMetrics shuffleReadMetrics = new ShuffleReadMetrics();
   private final ShuffleWriteMetrics shuffleWriteMetrics = new ShuffleWriteMetrics();
   private transient ArrayBuffer<TempShuffleReadMetrics> tempShuffleReadMetrics;
   private final Option<LongAccumulator> testAccum;
   private transient Map<String, AccumulatorV2<? extends Object, ? super List<Tuple2<BlockId, BlockStatus>>>> nameToAccums;
   private transient Seq<AccumulatorV2<?, ?>> internalAccums;
   private transient ArrayBuffer<AccumulatorV2<?, ?>> externalAccums;
   private transient volatile byte bitmap$trans$0;

   public static boolean initializeLogIfNecessary$default$2() {
      return TaskMetrics$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return TaskMetrics$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      TaskMetrics$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return TaskMetrics$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      TaskMetrics$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      TaskMetrics$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      TaskMetrics$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      TaskMetrics$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      TaskMetrics$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      TaskMetrics$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      TaskMetrics$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      TaskMetrics$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      TaskMetrics$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      TaskMetrics$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return TaskMetrics$.MODULE$.log();
   }

   public static String logName() {
      return TaskMetrics$.MODULE$.logName();
   }

   public static TaskMetrics fromAccumulators(Seq<AccumulatorV2<?, ?>> var0) {
      return TaskMetrics$.MODULE$.fromAccumulators(var0);
   }

   public static TaskMetrics fromAccumulatorInfos(Seq<AccumulableInfo> var0) {
      return TaskMetrics$.MODULE$.fromAccumulatorInfos(var0);
   }

   public static TaskMetrics registered() {
      return TaskMetrics$.MODULE$.registered();
   }

   public static TaskMetrics empty() {
      return TaskMetrics$.MODULE$.empty();
   }

   private ArrayBuffer tempShuffleReadMetrics$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 1) == 0) {
            this.tempShuffleReadMetrics = new ArrayBuffer<TempShuffleReadMetrics>();
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.tempShuffleReadMetrics;
   }

   private Map nameToAccums$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 2) == 0) {
            this.nameToAccums = ((MapLike)LinkedHashMap$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new Tuple2[]{Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.EXECUTOR_DESERIALIZE_TIME()), this._executorDeserializeTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.EXECUTOR_DESERIALIZE_CPU_TIME()), this._executorDeserializeCpuTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.EXECUTOR_RUN_TIME()), this._executorRunTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.EXECUTOR_CPU_TIME()), this._executorCpuTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.RESULT_SIZE()), this.org$apache$spark$executor$TaskMetrics$$_resultSize()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.JVM_GC_TIME()), this._jvmGCTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.RESULT_SERIALIZATION_TIME()), this._resultSerializationTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.MEMORY_BYTES_SPILLED()), this._memoryBytesSpilled()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.DISK_BYTES_SPILLED()), this._diskBytesSpilled()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.PEAK_EXECUTION_MEMORY()), this._peakExecutionMemory()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.UPDATED_BLOCK_STATUSES()), this._updatedBlockStatuses()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.REMOTE_BLOCKS_FETCHED()), this.shuffleReadMetrics()._remoteBlocksFetched()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.LOCAL_BLOCKS_FETCHED()), this.shuffleReadMetrics()._localBlocksFetched()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.REMOTE_BYTES_READ()), this.shuffleReadMetrics()._remoteBytesRead()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.REMOTE_BYTES_READ_TO_DISK()), this.shuffleReadMetrics()._remoteBytesReadToDisk()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.LOCAL_BYTES_READ()), this.shuffleReadMetrics()._localBytesRead()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.FETCH_WAIT_TIME()), this.shuffleReadMetrics()._fetchWaitTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleRead$.MODULE$.RECORDS_READ()), this.shuffleReadMetrics()._recordsRead()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleWrite$.MODULE$.BYTES_WRITTEN()), this.shuffleWriteMetrics()._bytesWritten()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleWrite$.MODULE$.RECORDS_WRITTEN()), this.shuffleWriteMetrics()._recordsWritten()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.shuffleWrite$.MODULE$.WRITE_TIME()), this.shuffleWriteMetrics()._writeTime()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.input$.MODULE$.BYTES_READ()), this.inputMetrics()._bytesRead()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.input$.MODULE$.RECORDS_READ()), this.inputMetrics()._recordsRead()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.output$.MODULE$.BYTES_WRITTEN()), this.outputMetrics()._bytesWritten()), Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator.output$.MODULE$.RECORDS_WRITTEN()), this.outputMetrics()._recordsWritten())}))).$plus$plus(Option$.MODULE$.option2Iterable(this.testAccum().map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Tuple2<String, LongAccumulator> apply(LongAccumulator x$2) {
                  return Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(InternalAccumulator$.MODULE$.TEST_ACCUM()), x$2);
               }
            })));
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 2);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.nameToAccums;
   }

   private Seq internalAccums$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 4) == 0) {
            this.internalAccums = this.nameToAccums().values().toIndexedSeq();
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 4);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.internalAccums;
   }

   private ArrayBuffer externalAccums$lzycompute() {
      synchronized(this) {
         if ((byte)(this.bitmap$trans$0 & 8) == 0) {
            this.externalAccums = new ArrayBuffer<AccumulatorV2<?, ?>>();
            this.bitmap$trans$0 = (byte)(this.bitmap$trans$0 | 8);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.externalAccums;
   }

   private LongAccumulator _executorDeserializeTime() {
      return this._executorDeserializeTime;
   }

   private LongAccumulator _executorDeserializeCpuTime() {
      return this._executorDeserializeCpuTime;
   }

   private LongAccumulator _executorRunTime() {
      return this._executorRunTime;
   }

   private LongAccumulator _executorCpuTime() {
      return this._executorCpuTime;
   }

   public LongAccumulator org$apache$spark$executor$TaskMetrics$$_resultSize() {
      return this.org$apache$spark$executor$TaskMetrics$$_resultSize;
   }

   private LongAccumulator _jvmGCTime() {
      return this._jvmGCTime;
   }

   private LongAccumulator _resultSerializationTime() {
      return this._resultSerializationTime;
   }

   private LongAccumulator _memoryBytesSpilled() {
      return this._memoryBytesSpilled;
   }

   private LongAccumulator _diskBytesSpilled() {
      return this._diskBytesSpilled;
   }

   private LongAccumulator _peakExecutionMemory() {
      return this._peakExecutionMemory;
   }

   private CollectionAccumulator<Tuple2<BlockId, BlockStatus>> _updatedBlockStatuses() {
      return this._updatedBlockStatuses;
   }

   public long executorDeserializeTime() {
      return this._executorDeserializeTime().sum();
   }

   public long executorDeserializeCpuTime() {
      return this._executorDeserializeCpuTime().sum();
   }

   public long executorRunTime() {
      return this._executorRunTime().sum();
   }

   public long executorCpuTime() {
      return this._executorCpuTime().sum();
   }

   public long resultSize() {
      return this.org$apache$spark$executor$TaskMetrics$$_resultSize().sum();
   }

   public long jvmGCTime() {
      return this._jvmGCTime().sum();
   }

   public long resultSerializationTime() {
      return this._resultSerializationTime().sum();
   }

   public long memoryBytesSpilled() {
      return this._memoryBytesSpilled().sum();
   }

   public long diskBytesSpilled() {
      return this._diskBytesSpilled().sum();
   }

   public long peakExecutionMemory() {
      return this._peakExecutionMemory().sum();
   }

   public Seq<Tuple2<BlockId, BlockStatus>> updatedBlockStatuses() {
      return (Seq)JavaConverters$.MODULE$.asScalaBufferConverter(this._updatedBlockStatuses().value()).asScala();
   }

   public void setExecutorDeserializeTime(long v) {
      this._executorDeserializeTime().setValue(v);
   }

   public void setExecutorDeserializeCpuTime(long v) {
      this._executorDeserializeCpuTime().setValue(v);
   }

   public void setExecutorRunTime(long v) {
      this._executorRunTime().setValue(v);
   }

   public void setExecutorCpuTime(long v) {
      this._executorCpuTime().setValue(v);
   }

   public void setResultSize(long v) {
      this.org$apache$spark$executor$TaskMetrics$$_resultSize().setValue(v);
   }

   public void setJvmGCTime(long v) {
      this._jvmGCTime().setValue(v);
   }

   public void setResultSerializationTime(long v) {
      this._resultSerializationTime().setValue(v);
   }

   public void incMemoryBytesSpilled(long v) {
      this._memoryBytesSpilled().add(v);
   }

   public void incDiskBytesSpilled(long v) {
      this._diskBytesSpilled().add(v);
   }

   public void incPeakExecutionMemory(long v) {
      this._peakExecutionMemory().add(v);
   }

   public void incUpdatedBlockStatuses(Tuple2<BlockId, BlockStatus> v) {
      this._updatedBlockStatuses().add(v);
   }

   public void setUpdatedBlockStatuses(List<Tuple2<BlockId, BlockStatus>> v) {
      this._updatedBlockStatuses().setValue(v);
   }

   public void setUpdatedBlockStatuses(Seq<Tuple2<BlockId, BlockStatus>> v) {
      this._updatedBlockStatuses().setValue((List)JavaConverters$.MODULE$.seqAsJavaListConverter(v).asJava());
   }

   public InputMetrics inputMetrics() {
      return this.inputMetrics;
   }

   public OutputMetrics outputMetrics() {
      return this.outputMetrics;
   }

   public ShuffleReadMetrics shuffleReadMetrics() {
      return this.shuffleReadMetrics;
   }

   public ShuffleWriteMetrics shuffleWriteMetrics() {
      return this.shuffleWriteMetrics;
   }

   private ArrayBuffer<TempShuffleReadMetrics> tempShuffleReadMetrics() {
      return (byte)(this.bitmap$trans$0 & 1) == 0 ? this.tempShuffleReadMetrics$lzycompute() : this.tempShuffleReadMetrics;
   }

   public synchronized TempShuffleReadMetrics createTempShuffleReadMetrics() {
      TempShuffleReadMetrics readMetrics = new TempShuffleReadMetrics();
      this.tempShuffleReadMetrics().$plus$eq(readMetrics);
      return readMetrics;
   }

   public synchronized void mergeShuffleReadMetrics() {
      if (this.tempShuffleReadMetrics().nonEmpty()) {
         this.shuffleReadMetrics().setMergeValues(this.tempShuffleReadMetrics());
      }

   }

   public Option<LongAccumulator> testAccum() {
      return this.testAccum;
   }

   public Map<String, AccumulatorV2<? extends Object, ? super List<Tuple2<BlockId, BlockStatus>>>> nameToAccums() {
      return (byte)(this.bitmap$trans$0 & 2) == 0 ? this.nameToAccums$lzycompute() : this.nameToAccums;
   }

   public Seq<AccumulatorV2<?, ?>> internalAccums() {
      return (byte)(this.bitmap$trans$0 & 4) == 0 ? this.internalAccums$lzycompute() : this.internalAccums;
   }

   public void register(SparkContext sc) {
      this.nameToAccums().foreach(new Serializable(this, sc) {
         public static final long serialVersionUID = 0L;
         private final SparkContext sc$1;

         public final void apply(Tuple2<String, AccumulatorV2<? extends Object, ? super List<Tuple2<BlockId, BlockStatus>>>> x0$1) {
            if (x0$1 != null) {
               String name = (String)x0$1._1();
               AccumulatorV2 acc = (AccumulatorV2)x0$1._2();
               acc.register(this.sc$1, new Some(name), true);
               BoxedUnit var5 = BoxedUnit.UNIT;
            } else {
               throw new MatchError(x0$1);
            }
         }

         public {
            this.sc$1 = sc$1;
         }
      });
   }

   public ArrayBuffer<AccumulatorV2<?, ?>> externalAccums() {
      return (byte)(this.bitmap$trans$0 & 8) == 0 ? this.externalAccums$lzycompute() : this.externalAccums;
   }

   public void registerAccumulator(AccumulatorV2<?, ?> a) {
      this.externalAccums().$plus$eq(a);
   }

   public Seq<AccumulatorV2<?, ?>> accumulators() {
      return (Seq)this.internalAccums().$plus$plus(this.externalAccums(), Seq$.MODULE$.canBuildFrom());
   }

   public Seq<AccumulatorV2<?, ?>> nonZeroInternalAccums() {
      return (Seq)this.internalAccums().filter(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TaskMetrics $outer;

         public final boolean apply(AccumulatorV2<?, ?> a) {
            boolean var10000;
            label25: {
               if (a.isZero()) {
                  LongAccumulator var2 = this.$outer.org$apache$spark$executor$TaskMetrics$$_resultSize();
                  if (a == null) {
                     if (var2 != null) {
                        break label25;
                     }
                  } else if (!a.equals(var2)) {
                     break label25;
                  }
               }

               var10000 = true;
               return var10000;
            }

            var10000 = false;
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
   }

   public TaskMetrics() {
      this.testAccum = scala.sys.package$.MODULE$.props().get("spark.testing").<LongAccumulator>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final LongAccumulator apply(String x$1) {
            return new LongAccumulator();
         }
      });
   }
}
