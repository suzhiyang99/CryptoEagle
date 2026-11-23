package org.apache.spark.deploy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.util.List;
import java.util.jar.JarFile;
import org.apache.spark.launcher.SparkSubmitArgumentsParser;
import org.apache.spark.network.util.JavaUtils;
import org.apache.spark.util.Utils$;
import scala.Enumeration;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.Map;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.io.Source$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.sys.package$;
import scala.util.Try$;

@ScalaSignature(
   bytes = "\u0006\u0001\rUf!B\u0001\u0003\u0001\tQ!\u0001F*qCJ\\7+\u001e2nSR\f%oZ;nK:$8O\u0003\u0002\u0004\t\u00051A-\u001a9m_fT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0003\u0001-\u0001\"\u0001D\b\u000e\u00035Q!A\u0004\u0003\u0002\u00111\fWO\\2iKJL!\u0001E\u0007\u00035M\u0003\u0018M]6Tk\nl\u0017\u000e^!sOVlWM\u001c;t!\u0006\u00148/\u001a:\t\u0011I\u0001!\u0011!Q\u0001\nQ\tA!\u0019:hg\u000e\u0001\u0001cA\u000b E9\u0011a\u0003\b\b\u0003/ii\u0011\u0001\u0007\u0006\u00033M\ta\u0001\u0010:p_Rt\u0014\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uq\u0012a\u00029bG.\fw-\u001a\u0006\u00027%\u0011\u0001%\t\u0002\u0004'\u0016\f(BA\u000f\u001f!\t\u0019sE\u0004\u0002%K5\ta$\u0003\u0002'=\u00051\u0001K]3eK\u001aL!\u0001K\u0015\u0003\rM#(/\u001b8h\u0015\t1c\u0004\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003-\u0003\r)gN\u001e\t\u0005G5\u0012#%\u0003\u0002/S\t\u0019Q*\u00199\t\u000bA\u0002A\u0011A\u0019\u0002\rqJg.\u001b;?)\r\u0011D'\u000e\t\u0003g\u0001i\u0011A\u0001\u0005\u0006%=\u0002\r\u0001\u0006\u0005\bW=\u0002\n\u00111\u0001-\u0011\u001d9\u0004\u00011A\u0005\u0002a\na!\\1ti\u0016\u0014X#\u0001\u0012\t\u000fi\u0002\u0001\u0019!C\u0001w\u0005QQ.Y:uKJ|F%Z9\u0015\u0005qz\u0004C\u0001\u0013>\u0013\tqdD\u0001\u0003V]&$\bb\u0002!:\u0003\u0003\u0005\rAI\u0001\u0004q\u0012\n\u0004B\u0002\"\u0001A\u0003&!%A\u0004nCN$XM\u001d\u0011\t\u000f\u0011\u0003\u0001\u0019!C\u0001q\u0005QA-\u001a9m_flu\u000eZ3\t\u000f\u0019\u0003\u0001\u0019!C\u0001\u000f\u0006qA-\u001a9m_flu\u000eZ3`I\u0015\fHC\u0001\u001fI\u0011\u001d\u0001U)!AA\u0002\tBaA\u0013\u0001!B\u0013\u0011\u0013a\u00033fa2|\u00170T8eK\u0002Bq\u0001\u0014\u0001A\u0002\u0013\u0005\u0001(\u0001\bfq\u0016\u001cW\u000f^8s\u001b\u0016lwN]=\t\u000f9\u0003\u0001\u0019!C\u0001\u001f\u0006\u0011R\r_3dkR|'/T3n_JLx\fJ3r)\ta\u0004\u000bC\u0004A\u001b\u0006\u0005\t\u0019\u0001\u0012\t\rI\u0003\u0001\u0015)\u0003#\u0003=)\u00070Z2vi>\u0014X*Z7pef\u0004\u0003b\u0002+\u0001\u0001\u0004%\t\u0001O\u0001\u000eKb,7-\u001e;pe\u000e{'/Z:\t\u000fY\u0003\u0001\u0019!C\u0001/\u0006\tR\r_3dkR|'oQ8sKN|F%Z9\u0015\u0005qB\u0006b\u0002!V\u0003\u0003\u0005\rA\t\u0005\u00075\u0002\u0001\u000b\u0015\u0002\u0012\u0002\u001d\u0015DXmY;u_J\u001cuN]3tA!9A\f\u0001a\u0001\n\u0003A\u0014A\u0005;pi\u0006dW\t_3dkR|'oQ8sKNDqA\u0018\u0001A\u0002\u0013\u0005q,\u0001\fu_R\fG.\u0012=fGV$xN]\"pe\u0016\u001cx\fJ3r)\ta\u0004\rC\u0004A;\u0006\u0005\t\u0019\u0001\u0012\t\r\t\u0004\u0001\u0015)\u0003#\u0003M!x\u000e^1m\u000bb,7-\u001e;pe\u000e{'/Z:!\u0011\u001d!\u0007\u00011A\u0005\u0002a\na\u0002\u001d:pa\u0016\u0014H/[3t\r&dW\rC\u0004g\u0001\u0001\u0007I\u0011A4\u0002%A\u0014x\u000e]3si&,7OR5mK~#S-\u001d\u000b\u0003y!Dq\u0001Q3\u0002\u0002\u0003\u0007!\u0005\u0003\u0004k\u0001\u0001\u0006KAI\u0001\u0010aJ|\u0007/\u001a:uS\u0016\u001ch)\u001b7fA!9A\u000e\u0001a\u0001\n\u0003A\u0014\u0001\u00043sSZ,'/T3n_JL\bb\u00028\u0001\u0001\u0004%\ta\\\u0001\u0011IJLg/\u001a:NK6|'/_0%KF$\"\u0001\u00109\t\u000f\u0001k\u0017\u0011!a\u0001E!1!\u000f\u0001Q!\n\t\nQ\u0002\u001a:jm\u0016\u0014X*Z7pef\u0004\u0003b\u0002;\u0001\u0001\u0004%\t\u0001O\u0001\u0015IJLg/\u001a:FqR\u0014\u0018m\u00117bgN\u0004\u0016\r\u001e5\t\u000fY\u0004\u0001\u0019!C\u0001o\u0006ABM]5wKJ,\u0005\u0010\u001e:b\u00072\f7o\u001d)bi\"|F%Z9\u0015\u0005qB\bb\u0002!v\u0003\u0003\u0005\rA\t\u0005\u0007u\u0002\u0001\u000b\u0015\u0002\u0012\u0002+\u0011\u0014\u0018N^3s\u000bb$(/Y\"mCN\u001c\b+\u0019;iA!9A\u0010\u0001a\u0001\n\u0003A\u0014A\u00063sSZ,'/\u0012=ue\u0006d\u0015N\u0019:bef\u0004\u0016\r\u001e5\t\u000fy\u0004\u0001\u0019!C\u0001\u007f\u0006QBM]5wKJ,\u0005\u0010\u001e:b\u0019&\u0014'/\u0019:z!\u0006$\bn\u0018\u0013fcR\u0019A(!\u0001\t\u000f\u0001k\u0018\u0011!a\u0001E!9\u0011Q\u0001\u0001!B\u0013\u0011\u0013a\u00063sSZ,'/\u0012=ue\u0006d\u0015N\u0019:bef\u0004\u0016\r\u001e5!\u0011!\tI\u0001\u0001a\u0001\n\u0003A\u0014A\u00063sSZ,'/\u0012=ue\u0006T\u0015M^1PaRLwN\\:\t\u0013\u00055\u0001\u00011A\u0005\u0002\u0005=\u0011A\u00073sSZ,'/\u0012=ue\u0006T\u0015M^1PaRLwN\\:`I\u0015\fHc\u0001\u001f\u0002\u0012!A\u0001)a\u0003\u0002\u0002\u0003\u0007!\u0005C\u0004\u0002\u0016\u0001\u0001\u000b\u0015\u0002\u0012\u0002/\u0011\u0014\u0018N^3s\u000bb$(/\u0019&bm\u0006|\u0005\u000f^5p]N\u0004\u0003\u0002CA\r\u0001\u0001\u0007I\u0011\u0001\u001d\u0002\u000bE,X-^3\t\u0013\u0005u\u0001\u00011A\u0005\u0002\u0005}\u0011!C9vKV,w\fJ3r)\ra\u0014\u0011\u0005\u0005\t\u0001\u0006m\u0011\u0011!a\u0001E!9\u0011Q\u0005\u0001!B\u0013\u0011\u0013AB9vKV,\u0007\u0005\u0003\u0005\u0002*\u0001\u0001\r\u0011\"\u00019\u00031qW/\\#yK\u000e,Ho\u001c:t\u0011%\ti\u0003\u0001a\u0001\n\u0003\ty#\u0001\tok6,\u00050Z2vi>\u00148o\u0018\u0013fcR\u0019A(!\r\t\u0011\u0001\u000bY#!AA\u0002\tBq!!\u000e\u0001A\u0003&!%A\u0007ok6,\u00050Z2vi>\u00148\u000f\t\u0005\t\u0003s\u0001\u0001\u0019!C\u0001q\u0005)a-\u001b7fg\"I\u0011Q\b\u0001A\u0002\u0013\u0005\u0011qH\u0001\nM&dWm]0%KF$2\u0001PA!\u0011!\u0001\u00151HA\u0001\u0002\u0004\u0011\u0003bBA#\u0001\u0001\u0006KAI\u0001\u0007M&dWm\u001d\u0011\t\u0011\u0005%\u0003\u00011A\u0005\u0002a\n\u0001\"\u0019:dQ&4Xm\u001d\u0005\n\u0003\u001b\u0002\u0001\u0019!C\u0001\u0003\u001f\nA\"\u0019:dQ&4Xm]0%KF$2\u0001PA)\u0011!\u0001\u00151JA\u0001\u0002\u0004\u0011\u0003bBA+\u0001\u0001\u0006KAI\u0001\nCJ\u001c\u0007.\u001b<fg\u0002B\u0001\"!\u0017\u0001\u0001\u0004%\t\u0001O\u0001\n[\u0006Lgn\u00117bgND\u0011\"!\u0018\u0001\u0001\u0004%\t!a\u0018\u0002\u001b5\f\u0017N\\\"mCN\u001cx\fJ3r)\ra\u0014\u0011\r\u0005\t\u0001\u0006m\u0013\u0011!a\u0001E!9\u0011Q\r\u0001!B\u0013\u0011\u0013AC7bS:\u001cE.Y:tA!A\u0011\u0011\u000e\u0001A\u0002\u0013\u0005\u0001(A\bqe&l\u0017M]=SKN|WO]2f\u0011%\ti\u0007\u0001a\u0001\n\u0003\ty'A\nqe&l\u0017M]=SKN|WO]2f?\u0012*\u0017\u000fF\u0002=\u0003cB\u0001\u0002QA6\u0003\u0003\u0005\rA\t\u0005\b\u0003k\u0002\u0001\u0015)\u0003#\u0003A\u0001(/[7bef\u0014Vm]8ve\u000e,\u0007\u0005\u0003\u0005\u0002z\u0001\u0001\r\u0011\"\u00019\u0003\u0011q\u0017-\\3\t\u0013\u0005u\u0004\u00011A\u0005\u0002\u0005}\u0014\u0001\u00038b[\u0016|F%Z9\u0015\u0007q\n\t\t\u0003\u0005A\u0003w\n\t\u00111\u0001#\u0011\u001d\t)\t\u0001Q!\n\t\nQA\\1nK\u0002B\u0011\"!#\u0001\u0001\u0004%\t!a#\u0002\u0013\rD\u0017\u000e\u001c3Be\u001e\u001cXCAAG!\u0015\ty)!'#\u001b\t\t\tJ\u0003\u0003\u0002\u0014\u0006U\u0015aB7vi\u0006\u0014G.\u001a\u0006\u0004\u0003/s\u0012AC2pY2,7\r^5p]&!\u00111TAI\u0005-\t%O]1z\u0005V4g-\u001a:\t\u0013\u0005}\u0005\u00011A\u0005\u0002\u0005\u0005\u0016!D2iS2$\u0017I]4t?\u0012*\u0017\u000fF\u0002=\u0003GC\u0011\u0002QAO\u0003\u0003\u0005\r!!$\t\u0011\u0005\u001d\u0006\u0001)Q\u0005\u0003\u001b\u000b!b\u00195jY\u0012\f%oZ:!\u0011!\tY\u000b\u0001a\u0001\n\u0003A\u0014\u0001\u00026beND\u0011\"a,\u0001\u0001\u0004%\t!!-\u0002\u0011)\f'o]0%KF$2\u0001PAZ\u0011!\u0001\u0015QVA\u0001\u0002\u0004\u0011\u0003bBA\\\u0001\u0001\u0006KAI\u0001\u0006U\u0006\u00148\u000f\t\u0005\t\u0003w\u0003\u0001\u0019!C\u0001q\u0005A\u0001/Y2lC\u001e,7\u000fC\u0005\u0002@\u0002\u0001\r\u0011\"\u0001\u0002B\u0006a\u0001/Y2lC\u001e,7o\u0018\u0013fcR\u0019A(a1\t\u0011\u0001\u000bi,!AA\u0002\tBq!a2\u0001A\u0003&!%A\u0005qC\u000e\\\u0017mZ3tA!A\u00111\u001a\u0001A\u0002\u0013\u0005\u0001(\u0001\u0007sKB|7/\u001b;pe&,7\u000fC\u0005\u0002P\u0002\u0001\r\u0011\"\u0001\u0002R\u0006\u0001\"/\u001a9pg&$xN]5fg~#S-\u001d\u000b\u0004y\u0005M\u0007\u0002\u0003!\u0002N\u0006\u0005\t\u0019\u0001\u0012\t\u000f\u0005]\u0007\u0001)Q\u0005E\u0005i!/\u001a9pg&$xN]5fg\u0002B\u0001\"a7\u0001\u0001\u0004%\t\u0001O\u0001\fSZL(+\u001a9p!\u0006$\b\u000eC\u0005\u0002`\u0002\u0001\r\u0011\"\u0001\u0002b\u0006y\u0011N^=SKB|\u0007+\u0019;i?\u0012*\u0017\u000fF\u0002=\u0003GD\u0001\u0002QAo\u0003\u0003\u0005\rA\t\u0005\b\u0003O\u0004\u0001\u0015)\u0003#\u00031Ig/\u001f*fa>\u0004\u0016\r\u001e5!\u0011!\tY\u000f\u0001a\u0001\n\u0003A\u0014A\u00059bG.\fw-Z:Fq\u000edWo]5p]ND\u0011\"a<\u0001\u0001\u0004%\t!!=\u0002-A\f7m[1hKN,\u0005p\u00197vg&|gn]0%KF$2\u0001PAz\u0011!\u0001\u0015Q^A\u0001\u0002\u0004\u0011\u0003bBA|\u0001\u0001\u0006KAI\u0001\u0014a\u0006\u001c7.Y4fg\u0016C8\r\\;tS>t7\u000f\t\u0005\n\u0003w\u0004\u0001\u0019!C\u0001\u0003{\fqA^3sE>\u001cX-\u0006\u0002\u0002\u0000B\u0019AE!\u0001\n\u0007\t\raDA\u0004C_>dW-\u00198\t\u0013\t\u001d\u0001\u00011A\u0005\u0002\t%\u0011a\u0003<fe\n|7/Z0%KF$2\u0001\u0010B\u0006\u0011%\u0001%QAA\u0001\u0002\u0004\ty\u0010\u0003\u0005\u0003\u0010\u0001\u0001\u000b\u0015BA\u0000\u0003!1XM\u001d2pg\u0016\u0004\u0003\"\u0003B\n\u0001\u0001\u0007I\u0011AA\u007f\u0003!I7\u000fU=uQ>t\u0007\"\u0003B\f\u0001\u0001\u0007I\u0011\u0001B\r\u00031I7\u000fU=uQ>tw\fJ3r)\ra$1\u0004\u0005\n\u0001\nU\u0011\u0011!a\u0001\u0003\u007fD\u0001Ba\b\u0001A\u0003&\u0011q`\u0001\nSN\u0004\u0016\u0010\u001e5p]\u0002B\u0001Ba\t\u0001\u0001\u0004%\t\u0001O\u0001\baf4\u0015\u000e\\3t\u0011%\u00119\u0003\u0001a\u0001\n\u0003\u0011I#A\u0006qs\u001aKG.Z:`I\u0015\fHc\u0001\u001f\u0003,!A\u0001I!\n\u0002\u0002\u0003\u0007!\u0005C\u0004\u00030\u0001\u0001\u000b\u0015\u0002\u0012\u0002\u0011ALh)\u001b7fg\u0002B\u0011Ba\r\u0001\u0001\u0004%\t!!@\u0002\u0007%\u001c(\u000bC\u0005\u00038\u0001\u0001\r\u0011\"\u0001\u0003:\u00059\u0011n\u001d*`I\u0015\fHc\u0001\u001f\u0003<!I\u0001I!\u000e\u0002\u0002\u0003\u0007\u0011q \u0005\t\u0005\u007f\u0001\u0001\u0015)\u0003\u0002\u0000\u0006!\u0011n\u001d*!\u0011%\u0011\u0019\u0005\u0001a\u0001\n\u0003\u0011)%\u0001\u0004bGRLwN\\\u000b\u0003\u0005\u000f\u0002BA!\u0013\u0003`9!!1\nB.\u001d\u0011\u0011iE!\u0017\u000f\t\t=#q\u000b\b\u0005\u0005#\u0012)FD\u0002\u0018\u0005'J\u0011!C\u0005\u0003\u000f!I!!\u0002\u0004\n\u0005\r!\u0011b\u0001B/\u0005\u0005\t2\u000b]1sWN+(-\\5u\u0003\u000e$\u0018n\u001c8\n\t\t\u0005$1\r\u0002\u0012'B\f'o[*vE6LG/Q2uS>t'b\u0001B/\u0005!I!q\r\u0001A\u0002\u0013\u0005!\u0011N\u0001\u000bC\u000e$\u0018n\u001c8`I\u0015\fHc\u0001\u001f\u0003l!I\u0001I!\u001a\u0002\u0002\u0003\u0007!q\t\u0005\t\u0005_\u0002\u0001\u0015)\u0003\u0003H\u00059\u0011m\u0019;j_:\u0004\u0003\"\u0003B:\u0001\t\u0007I\u0011\u0001B;\u0003=\u0019\b/\u0019:l!J|\u0007/\u001a:uS\u0016\u001cXC\u0001B<!\u0019\tyI!\u001f#E%!!1PAI\u0005\u001dA\u0015m\u001d5NCBD\u0001Ba \u0001A\u0003%!qO\u0001\u0011gB\f'o\u001b)s_B,'\u000f^5fg\u0002B\u0001Ba!\u0001\u0001\u0004%\t\u0001O\u0001\naJ|\u00070_+tKJD\u0011Ba\"\u0001\u0001\u0004%\tA!#\u0002\u001bA\u0014x\u000e_=Vg\u0016\u0014x\fJ3r)\ra$1\u0012\u0005\t\u0001\n\u0015\u0015\u0011!a\u0001E!9!q\u0012\u0001!B\u0013\u0011\u0013A\u00039s_bLXk]3sA!A!1\u0013\u0001A\u0002\u0013\u0005\u0001(A\u0005qe&t7-\u001b9bY\"I!q\u0013\u0001A\u0002\u0013\u0005!\u0011T\u0001\u000eaJLgnY5qC2|F%Z9\u0015\u0007q\u0012Y\n\u0003\u0005A\u0005+\u000b\t\u00111\u0001#\u0011\u001d\u0011y\n\u0001Q!\n\t\n!\u0002\u001d:j]\u000eL\u0007/\u00197!\u0011!\u0011\u0019\u000b\u0001a\u0001\n\u0003A\u0014AB6fsR\f'\rC\u0005\u0003(\u0002\u0001\r\u0011\"\u0001\u0003*\u0006Q1.Z=uC\n|F%Z9\u0015\u0007q\u0012Y\u000b\u0003\u0005A\u0005K\u000b\t\u00111\u0001#\u0011\u001d\u0011y\u000b\u0001Q!\n\t\nqa[3zi\u0006\u0014\u0007\u0005C\u0005\u00034\u0002\u0001\r\u0011\"\u0001\u0002~\u0006I1/\u001e9feZL7/\u001a\u0005\n\u0005o\u0003\u0001\u0019!C\u0001\u0005s\u000bQb];qKJ4\u0018n]3`I\u0015\fHc\u0001\u001f\u0003<\"I\u0001I!.\u0002\u0002\u0003\u0007\u0011q \u0005\t\u0005\u007f\u0003\u0001\u0015)\u0003\u0002\u0000\u0006Q1/\u001e9feZL7/\u001a\u0011\t\u0011\t\r\u0007\u00011A\u0005\u0002a\n1\u0002\u001a:jm\u0016\u00148i\u001c:fg\"I!q\u0019\u0001A\u0002\u0013\u0005!\u0011Z\u0001\u0010IJLg/\u001a:D_J,7o\u0018\u0013fcR\u0019AHa3\t\u0011\u0001\u0013)-!AA\u0002\tBqAa4\u0001A\u0003&!%\u0001\u0007ee&4XM]\"pe\u0016\u001c\b\u0005\u0003\u0005\u0003T\u0002\u0001\r\u0011\"\u00019\u0003A\u0019XOY7jgNLwN\u001c+p\u0017&dG\u000eC\u0005\u0003X\u0002\u0001\r\u0011\"\u0001\u0003Z\u0006!2/\u001e2nSN\u001c\u0018n\u001c8U_.KG\u000e\\0%KF$2\u0001\u0010Bn\u0011!\u0001%Q[A\u0001\u0002\u0004\u0011\u0003b\u0002Bp\u0001\u0001\u0006KAI\u0001\u0012gV\u0014W.[:tS>tGk\\&jY2\u0004\u0003\u0002\u0003Br\u0001\u0001\u0007I\u0011\u0001\u001d\u00029M,(-\\5tg&|g\u000eV8SKF,Xm\u001d;Ti\u0006$Xo\u001d$pe\"I!q\u001d\u0001A\u0002\u0013\u0005!\u0011^\u0001!gV\u0014W.[:tS>tGk\u001c*fcV,7\u000f^*uCR,8OR8s?\u0012*\u0017\u000fF\u0002=\u0005WD\u0001\u0002\u0011Bs\u0003\u0003\u0005\rA\t\u0005\b\u0005_\u0004\u0001\u0015)\u0003#\u0003u\u0019XOY7jgNLwN\u001c+p%\u0016\fX/Z:u'R\fG/^:G_J\u0004\u0003\"\u0003Bz\u0001\u0001\u0007I\u0011AA\u007f\u0003\u001d)8/\u001a*fgRD\u0011Ba>\u0001\u0001\u0004%\tA!?\u0002\u0017U\u001cXMU3ti~#S-\u001d\u000b\u0004y\tm\b\"\u0003!\u0003v\u0006\u0005\t\u0019AA\u0000\u0011!\u0011y\u0010\u0001Q!\n\u0005}\u0018\u0001C;tKJ+7\u000f\u001e\u0011\t\u0015\r\r\u0001\u0001#b\u0001\n\u0003\u0011)(\u0001\feK\u001a\fW\u000f\u001c;Ta\u0006\u00148\u000e\u0015:pa\u0016\u0014H/[3t\u0011)\u00199\u0001\u0001E\u0001B\u0003&!qO\u0001\u0018I\u00164\u0017-\u001e7u'B\f'o\u001b)s_B,'\u000f^5fg\u0002Bqaa\u0003\u0001\t\u0013\u0019i!A\u000enKJ<W\rR3gCVdGo\u00159be.\u0004&o\u001c9feRLWm\u001d\u000b\u0002y!91\u0011\u0003\u0001\u0005\n\r5\u0011\u0001G5h]>\u0014XMT8o'B\f'o\u001b)s_B,'\u000f^5fg\"91Q\u0003\u0001\u0005\n\r5\u0011\u0001\u00077pC\u0012,eN^5s_:lWM\u001c;Be\u001e,X.\u001a8ug\"91\u0011\u0004\u0001\u0005\n\r5\u0011!\u0005<bY&$\u0017\r^3Be\u001e,X.\u001a8ug\"91Q\u0004\u0001\u0005\n\r5\u0011a\u0006<bY&$\u0017\r^3Tk\nl\u0017\u000e^!sOVlWM\u001c;t\u0011\u001d\u0019\t\u0003\u0001C\u0005\u0007\u001b\tQC^1mS\u0012\fG/Z&jY2\f%oZ;nK:$8\u000fC\u0004\u0004&\u0001!Ia!\u0004\u0002=Y\fG.\u001b3bi\u0016\u001cF/\u0019;vgJ+\u0017/^3ti\u0006\u0013x-^7f]R\u001c\bbBB\u0015\u0001\u0011\u0005\u0011Q`\u0001\u0014SN\u001cF/\u00198eC2|g.Z\"mkN$XM\u001d\u0005\b\u0007[\u0001A\u0011IB\u0018\u0003!!xn\u0015;sS:<G#\u0001\u0012\t\u000f\rM\u0002\u0001\"\u0015\u00046\u00051\u0001.\u00198eY\u0016$b!a@\u00048\rm\u0002bBB\u001d\u0007c\u0001\rAI\u0001\u0004_B$\bbBB\u001f\u0007c\u0001\rAI\u0001\u0006m\u0006dW/\u001a\u0005\b\u0007\u0003\u0002A\u0011KB\"\u00035A\u0017M\u001c3mKVs7N\\8x]R!\u0011q`B#\u0011\u001d\u0019Ida\u0010A\u0002\tBqa!\u0013\u0001\t#\u001aY%A\biC:$G.Z#yiJ\f\u0017I]4t)\ra4Q\n\u0005\t\u0007\u001f\u001a9\u00051\u0001\u0004R\u0005)Q\r\u001f;sCB)11KB/E5\u00111Q\u000b\u0006\u0005\u0007/\u001aI&\u0001\u0003vi&d'BAB.\u0003\u0011Q\u0017M^1\n\t\r}3Q\u000b\u0002\u0005\u0019&\u001cH\u000fC\u0004\u0004d\u0001!Ia!\u001a\u0002#A\u0014\u0018N\u001c;Vg\u0006<W-\u00118e\u000bbLG\u000fF\u0003=\u0007O\u001a\t\b\u0003\u0005\u0004j\r\u0005\u0004\u0019AB6\u0003!)\u00070\u001b;D_\u0012,\u0007c\u0001\u0013\u0004n%\u00191q\u000e\u0010\u0003\u0007%sG\u000f\u0003\u0006\u0004t\r\u0005\u0004\u0013!a\u0001\u0007k\nA\"\u001e8l]><h\u000eU1sC6\u00042\u0001JB<\u0013\r\u0019IH\b\u0002\u0004\u0003:L\bbBB?\u0001\u0011%1qF\u0001\u0013O\u0016$8+\u001d7TQ\u0016dGn\u00149uS>t7\u000fC\u0005\u0004\u0002\u0002\t\n\u0011\"\u0003\u0004\u0004\u0006Y\u0002O]5oiV\u001b\u0018mZ3B]\u0012,\u00050\u001b;%I\u00164\u0017-\u001e7uII*\"a!\"+\t\rU4qQ\u0016\u0003\u0007\u0013\u0003Baa#\u0004\u00166\u00111Q\u0012\u0006\u0005\u0007\u001f\u001b\t*A\u0005v]\u000eDWmY6fI*\u001911\u0013\u0010\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0004\u0018\u000e5%!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\u001eQ11\u0014\u0002\u0002\u0002#\u0005!a!(\u0002)M\u0003\u0018M]6Tk\nl\u0017\u000e^!sOVlWM\u001c;t!\r\u00194q\u0014\u0004\n\u0003\t\t\t\u0011#\u0001\u0003\u0007C\u001bBaa(\u0004$B\u0019Ae!*\n\u0007\r\u001dfD\u0001\u0004B]f\u0014VM\u001a\u0005\ba\r}E\u0011ABV)\t\u0019i\n\u0003\u0006\u00040\u000e}\u0015\u0013!C\u0001\u0007c\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u0012TCABZU\ra3q\u0011"
)
public class SparkSubmitArguments extends SparkSubmitArgumentsParser {
   private final Seq<String> args;
   public final Map<String, String> org$apache$spark$deploy$SparkSubmitArguments$$env;
   private String master;
   private String deployMode;
   private String executorMemory;
   private String executorCores;
   private String totalExecutorCores;
   private String propertiesFile;
   private String driverMemory;
   private String driverExtraClassPath;
   private String driverExtraLibraryPath;
   private String driverExtraJavaOptions;
   private String queue;
   private String numExecutors;
   private String files;
   private String archives;
   private String mainClass;
   private String primaryResource;
   private String name;
   private ArrayBuffer<String> childArgs;
   private String jars;
   private String packages;
   private String repositories;
   private String ivyRepoPath;
   private String packagesExclusions;
   private boolean verbose;
   private boolean isPython;
   private String pyFiles;
   private boolean isR;
   private Enumeration.Value action;
   private final HashMap<String, String> sparkProperties;
   private String proxyUser;
   private String principal;
   private String keytab;
   private boolean supervise;
   private String driverCores;
   private String submissionToKill;
   private String submissionToRequestStatusFor;
   private boolean useRest;
   private HashMap<String, String> defaultSparkProperties;
   private volatile boolean bitmap$0;

   public static Map<String, String> $lessinit$greater$default$2() {
      return SparkSubmitArguments$.MODULE$.$lessinit$greater$default$2();
   }

   private HashMap defaultSparkProperties$lzycompute() {
      synchronized(this) {
         if (!this.bitmap$0) {
            HashMap defaultProperties = new HashMap();
            if (this.verbose()) {
               SparkSubmit$.MODULE$.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Using properties file: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.propertiesFile()})));
            }

            Option$.MODULE$.apply(this.propertiesFile()).foreach(new Serializable(this, defaultProperties) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final SparkSubmitArguments $outer;
               public final HashMap defaultProperties$1;

               public final void apply(String filename) {
                  scala.collection.Map properties = Utils$.MODULE$.getPropertiesFromFile(filename);
                  properties.foreach(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final void apply(Tuple2<String, String> x0$1) {
                        if (x0$1 != null) {
                           String k = (String)x0$1._1();
                           String v = (String)x0$1._2();
                           this.$outer.defaultProperties$1.update(k, v);
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
                  if (this.$outer.verbose()) {
                     Utils$.MODULE$.redact(properties).foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final void apply(Tuple2<String, String> x0$2) {
                           if (x0$2 != null) {
                              String k = (String)x0$2._1();
                              String v = (String)x0$2._2();
                              SparkSubmit$.MODULE$.printStream().println((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Adding default property: ", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{k, v})));
                              BoxedUnit var5 = BoxedUnit.UNIT;
                           } else {
                              throw new MatchError(x0$2);
                           }
                        }
                     });
                  }

               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.defaultProperties$1 = defaultProperties$1;
                  }
               }
            });
            this.defaultSparkProperties = defaultProperties;
            this.bitmap$0 = true;
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return this.defaultSparkProperties;
   }

   public String master() {
      return this.master;
   }

   public void master_$eq(String x$1) {
      this.master = x$1;
   }

   public String deployMode() {
      return this.deployMode;
   }

   public void deployMode_$eq(String x$1) {
      this.deployMode = x$1;
   }

   public String executorMemory() {
      return this.executorMemory;
   }

   public void executorMemory_$eq(String x$1) {
      this.executorMemory = x$1;
   }

   public String executorCores() {
      return this.executorCores;
   }

   public void executorCores_$eq(String x$1) {
      this.executorCores = x$1;
   }

   public String totalExecutorCores() {
      return this.totalExecutorCores;
   }

   public void totalExecutorCores_$eq(String x$1) {
      this.totalExecutorCores = x$1;
   }

   public String propertiesFile() {
      return this.propertiesFile;
   }

   public void propertiesFile_$eq(String x$1) {
      this.propertiesFile = x$1;
   }

   public String driverMemory() {
      return this.driverMemory;
   }

   public void driverMemory_$eq(String x$1) {
      this.driverMemory = x$1;
   }

   public String driverExtraClassPath() {
      return this.driverExtraClassPath;
   }

   public void driverExtraClassPath_$eq(String x$1) {
      this.driverExtraClassPath = x$1;
   }

   public String driverExtraLibraryPath() {
      return this.driverExtraLibraryPath;
   }

   public void driverExtraLibraryPath_$eq(String x$1) {
      this.driverExtraLibraryPath = x$1;
   }

   public String driverExtraJavaOptions() {
      return this.driverExtraJavaOptions;
   }

   public void driverExtraJavaOptions_$eq(String x$1) {
      this.driverExtraJavaOptions = x$1;
   }

   public String queue() {
      return this.queue;
   }

   public void queue_$eq(String x$1) {
      this.queue = x$1;
   }

   public String numExecutors() {
      return this.numExecutors;
   }

   public void numExecutors_$eq(String x$1) {
      this.numExecutors = x$1;
   }

   public String files() {
      return this.files;
   }

   public void files_$eq(String x$1) {
      this.files = x$1;
   }

   public String archives() {
      return this.archives;
   }

   public void archives_$eq(String x$1) {
      this.archives = x$1;
   }

   public String mainClass() {
      return this.mainClass;
   }

   public void mainClass_$eq(String x$1) {
      this.mainClass = x$1;
   }

   public String primaryResource() {
      return this.primaryResource;
   }

   public void primaryResource_$eq(String x$1) {
      this.primaryResource = x$1;
   }

   public String name() {
      return this.name;
   }

   public void name_$eq(String x$1) {
      this.name = x$1;
   }

   public ArrayBuffer<String> childArgs() {
      return this.childArgs;
   }

   public void childArgs_$eq(ArrayBuffer<String> x$1) {
      this.childArgs = x$1;
   }

   public String jars() {
      return this.jars;
   }

   public void jars_$eq(String x$1) {
      this.jars = x$1;
   }

   public String packages() {
      return this.packages;
   }

   public void packages_$eq(String x$1) {
      this.packages = x$1;
   }

   public String repositories() {
      return this.repositories;
   }

   public void repositories_$eq(String x$1) {
      this.repositories = x$1;
   }

   public String ivyRepoPath() {
      return this.ivyRepoPath;
   }

   public void ivyRepoPath_$eq(String x$1) {
      this.ivyRepoPath = x$1;
   }

   public String packagesExclusions() {
      return this.packagesExclusions;
   }

   public void packagesExclusions_$eq(String x$1) {
      this.packagesExclusions = x$1;
   }

   public boolean verbose() {
      return this.verbose;
   }

   public void verbose_$eq(boolean x$1) {
      this.verbose = x$1;
   }

   public boolean isPython() {
      return this.isPython;
   }

   public void isPython_$eq(boolean x$1) {
      this.isPython = x$1;
   }

   public String pyFiles() {
      return this.pyFiles;
   }

   public void pyFiles_$eq(String x$1) {
      this.pyFiles = x$1;
   }

   public boolean isR() {
      return this.isR;
   }

   public void isR_$eq(boolean x$1) {
      this.isR = x$1;
   }

   public Enumeration.Value action() {
      return this.action;
   }

   public void action_$eq(Enumeration.Value x$1) {
      this.action = x$1;
   }

   public HashMap<String, String> sparkProperties() {
      return this.sparkProperties;
   }

   public String proxyUser() {
      return this.proxyUser;
   }

   public void proxyUser_$eq(String x$1) {
      this.proxyUser = x$1;
   }

   public String principal() {
      return this.principal;
   }

   public void principal_$eq(String x$1) {
      this.principal = x$1;
   }

   public String keytab() {
      return this.keytab;
   }

   public void keytab_$eq(String x$1) {
      this.keytab = x$1;
   }

   public boolean supervise() {
      return this.supervise;
   }

   public void supervise_$eq(boolean x$1) {
      this.supervise = x$1;
   }

   public String driverCores() {
      return this.driverCores;
   }

   public void driverCores_$eq(String x$1) {
      this.driverCores = x$1;
   }

   public String submissionToKill() {
      return this.submissionToKill;
   }

   public void submissionToKill_$eq(String x$1) {
      this.submissionToKill = x$1;
   }

   public String submissionToRequestStatusFor() {
      return this.submissionToRequestStatusFor;
   }

   public void submissionToRequestStatusFor_$eq(String x$1) {
      this.submissionToRequestStatusFor = x$1;
   }

   public boolean useRest() {
      return this.useRest;
   }

   public void useRest_$eq(boolean x$1) {
      this.useRest = x$1;
   }

   public HashMap<String, String> defaultSparkProperties() {
      return this.bitmap$0 ? this.defaultSparkProperties : this.defaultSparkProperties$lzycompute();
   }

   private void mergeDefaultSparkProperties() {
      this.propertiesFile_$eq((String)Option$.MODULE$.apply(this.propertiesFile()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final String apply() {
            return Utils$.MODULE$.getDefaultPropertiesFile(this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.defaultSparkProperties().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final void apply(Tuple2<String, String> x0$3) {
            if (x0$3 != null) {
               String k = (String)x0$3._1();
               String v = (String)x0$3._2();
               if (this.$outer.sparkProperties().contains(k)) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  this.$outer.sparkProperties().update(k, v);
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }

            } else {
               throw new MatchError(x0$3);
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

   private void ignoreNonSparkProperties() {
      this.sparkProperties().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final void apply(Tuple2<String, String> x0$4) {
            if (x0$4 != null) {
               String k = (String)x0$4._1();
               String v = (String)x0$4._2();
               if (k.startsWith("spark.")) {
                  BoxedUnit var10000 = BoxedUnit.UNIT;
               } else {
                  this.$outer.sparkProperties().$minus$eq(k);
                  SparkSubmit$.MODULE$.printWarning((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignoring non-spark config property: ", "=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{k, v})));
                  BoxedUnit var6 = BoxedUnit.UNIT;
               }

            } else {
               throw new MatchError(x0$4);
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

   private void loadEnvironmentArguments() {
      this.master_$eq((String)Option$.MODULE$.apply(this.master()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.master");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("MASTER");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.driverExtraClassPath_$eq((String)Option$.MODULE$.apply(this.driverExtraClassPath()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.driver.extraClassPath");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.driverExtraJavaOptions_$eq((String)Option$.MODULE$.apply(this.driverExtraJavaOptions()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.driver.extraJavaOptions");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.driverExtraLibraryPath_$eq((String)Option$.MODULE$.apply(this.driverExtraLibraryPath()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.driver.extraLibraryPath");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.driverMemory_$eq((String)Option$.MODULE$.apply(this.driverMemory()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.driver.memory");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("SPARK_DRIVER_MEMORY");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.driverCores_$eq((String)Option$.MODULE$.apply(this.driverCores()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.driver.cores");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.executorMemory_$eq((String)Option$.MODULE$.apply(this.executorMemory()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.executor.memory");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("SPARK_EXECUTOR_MEMORY");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.executorCores_$eq((String)Option$.MODULE$.apply(this.executorCores()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.executor.cores");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("SPARK_EXECUTOR_CORES");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.totalExecutorCores_$eq((String)Option$.MODULE$.apply(this.totalExecutorCores()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.cores.max");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.name_$eq((String)Option$.MODULE$.apply(this.name()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.app.name");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.jars_$eq((String)Option$.MODULE$.apply(this.jars()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.jars");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.files_$eq((String)Option$.MODULE$.apply(this.files()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.files");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.ivyRepoPath_$eq((String)this.sparkProperties().get("spark.jars.ivy").orNull(Predef$.MODULE$.$conforms()));
      this.packages_$eq((String)Option$.MODULE$.apply(this.packages()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.jars.packages");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.packagesExclusions_$eq((String)Option$.MODULE$.apply(this.packagesExclusions()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.jars.excludes");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.repositories_$eq((String)Option$.MODULE$.apply(this.repositories()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.jars.repositories");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.deployMode_$eq((String)Option$.MODULE$.apply(this.deployMode()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.submit.deployMode");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("DEPLOY_MODE");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.numExecutors_$eq((String)Option$.MODULE$.apply(this.numExecutors()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final String apply() {
            return (String)this.$outer.sparkProperties().get("spark.executor.instances").orNull(Predef$.MODULE$.$conforms());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
      this.queue_$eq((String)Option$.MODULE$.apply(this.queue()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.yarn.queue");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.keytab_$eq((String)Option$.MODULE$.apply(this.keytab()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.yarn.keytab");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      this.principal_$eq((String)Option$.MODULE$.apply(this.principal()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return this.$outer.sparkProperties().get("spark.yarn.principal");
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      if (this.mainClass() == null && !this.isPython() && !this.isR() && this.primaryResource() != null) {
         URI uri = new URI(this.primaryResource());
         String uriScheme = uri.getScheme();
         if ("file".equals(uriScheme)) {
            try {
               BoxedUnit var6 = (BoxedUnit)Utils$.MODULE$.tryWithResource(new Serializable(this, uri) {
                  public static final long serialVersionUID = 0L;
                  private final URI uri$1;

                  public final JarFile apply() {
                     return new JarFile(this.uri$1.getPath());
                  }

                  public {
                     this.uri$1 = uri$1;
                  }
               }, new Serializable(this) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final SparkSubmitArguments $outer;

                  public final void apply(JarFile jar) {
                     this.$outer.mainClass_$eq(jar.getManifest().getMainAttributes().getValue("Main-Class"));
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                     }
                  }
               });
            } catch (Exception var5) {
               SparkSubmit$.MODULE$.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot load main class from JAR ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.primaryResource()})));
               BoxedUnit var10000 = BoxedUnit.UNIT;
            }
         } else {
            SparkSubmit$.MODULE$.printErrorAndExit((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot load main class from JAR ", " with URI ", ". "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.primaryResource(), uriScheme}))).append((Object)"Please specify a class through --class.").toString());
            BoxedUnit var4 = BoxedUnit.UNIT;
         }
      }

      this.master_$eq((String)Option$.MODULE$.apply(this.master()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "local[*]";
         }
      }));
      if (this.master().startsWith("yarn")) {
         this.name_$eq((String)Option$.MODULE$.apply(this.name()).orElse(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final SparkSubmitArguments $outer;

            public final Option<String> apply() {
               return this.$outer.org$apache$spark$deploy$SparkSubmitArguments$$env.get("SPARK_YARN_APP_NAME");
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }).orNull(Predef$.MODULE$.$conforms()));
      }

      this.name_$eq((String)Option$.MODULE$.apply(this.name()).orElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final Option<String> apply() {
            return Option$.MODULE$.<String>apply(this.$outer.mainClass());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }).orNull(Predef$.MODULE$.$conforms()));
      if (this.name() == null && this.primaryResource() != null) {
         this.name_$eq(Utils$.MODULE$.stripDirectory(this.primaryResource()));
      }

      this.action_$eq((Enumeration.Value)Option$.MODULE$.apply(this.action()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final Enumeration.Value apply() {
            return SparkSubmitAction$.MODULE$.SUBMIT();
         }
      }));
   }

   private void validateArguments() {
      label52: {
         Enumeration.Value var1 = this.action();
         Enumeration.Value var10000 = SparkSubmitAction$.MODULE$.SUBMIT();
         if (var10000 == null) {
            if (var1 == null) {
               break label52;
            }
         } else if (var10000.equals(var1)) {
            break label52;
         }

         label53: {
            var10000 = SparkSubmitAction$.MODULE$.KILL();
            if (var10000 == null) {
               if (var1 == null) {
                  break label53;
               }
            } else if (var10000.equals(var1)) {
               break label53;
            }

            label27: {
               var10000 = SparkSubmitAction$.MODULE$.REQUEST_STATUS();
               if (var10000 == null) {
                  if (var1 == null) {
                     break label27;
                  }
               } else if (var10000.equals(var1)) {
                  break label27;
               }

               throw new MatchError(var1);
            }

            this.validateStatusRequestArguments();
            BoxedUnit var3 = BoxedUnit.UNIT;
            return;
         }

         this.validateKillArguments();
         BoxedUnit var6 = BoxedUnit.UNIT;
         return;
      }

      this.validateSubmitArguments();
      BoxedUnit var7 = BoxedUnit.UNIT;
   }

   private void validateSubmitArguments() {
      if (this.args.length() == 0) {
         this.printUsageAndExit(-1, this.printUsageAndExit$default$2());
      }

      if (this.primaryResource() == null) {
         SparkSubmit$.MODULE$.printErrorAndExit("Must specify a primary resource (JAR or Python or R file)");
      }

      if (this.mainClass() == null && SparkSubmit$.MODULE$.isUserJar(this.primaryResource())) {
         SparkSubmit$.MODULE$.printErrorAndExit("No main class set in JAR; please specify one with --class");
      }

      if (this.driverMemory() != null && BoxesRunTime.unboxToLong(Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return JavaUtils.byteStringAsBytes(this.$outer.driverMemory());
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

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      })) <= 0L) {
         SparkSubmit$.MODULE$.printErrorAndExit("Driver Memory must be a positive number");
      }

      if (this.executorMemory() != null && BoxesRunTime.unboxToLong(Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return JavaUtils.byteStringAsBytes(this.$outer.executorMemory());
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

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return -1L;
         }
      })) <= 0L) {
         SparkSubmit$.MODULE$.printErrorAndExit("Executor Memory cores must be a positive number");
      }

      if (this.executorCores() != null && BoxesRunTime.unboxToInt(Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return (new StringOps(Predef$.MODULE$.augmentString(this.$outer.executorCores()))).toInt();
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

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      })) <= 0) {
         SparkSubmit$.MODULE$.printErrorAndExit("Executor cores must be a positive number");
      }

      if (this.totalExecutorCores() != null && BoxesRunTime.unboxToInt(Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return (new StringOps(Predef$.MODULE$.augmentString(this.$outer.totalExecutorCores()))).toInt();
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

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      })) <= 0) {
         SparkSubmit$.MODULE$.printErrorAndExit("Total executor cores must be a positive number");
      }

      if (this.numExecutors() != null && BoxesRunTime.unboxToInt(Try$.MODULE$.apply(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final SparkSubmitArguments $outer;

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return (new StringOps(Predef$.MODULE$.augmentString(this.$outer.numExecutors()))).toInt();
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

         public final int apply() {
            return this.apply$mcI$sp();
         }

         public int apply$mcI$sp() {
            return -1;
         }
      })) <= 0) {
         SparkSubmit$.MODULE$.printErrorAndExit("Number of executors must be a positive number");
      }

      if (this.pyFiles() != null && !this.isPython()) {
         SparkSubmit$.MODULE$.printErrorAndExit("--py-files given but primary resource is not a Python script");
      }

      if (this.master().startsWith("yarn")) {
         boolean hasHadoopEnv = this.org$apache$spark$deploy$SparkSubmitArguments$$env.contains("HADOOP_CONF_DIR") || this.org$apache$spark$deploy$SparkSubmitArguments$$env.contains("YARN_CONF_DIR");
         if (!hasHadoopEnv && !Utils$.MODULE$.isTesting()) {
            throw new Exception((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"When running with master '", "' "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.master()}))).append((Object)"either HADOOP_CONF_DIR or YARN_CONF_DIR must be set in the environment.").toString());
         }
      }

      if (this.proxyUser() != null && this.principal() != null) {
         SparkSubmit$.MODULE$.printErrorAndExit("Only one of --proxy-user or --principal can be provided.");
      }

   }

   private void validateKillArguments() {
      if (!this.master().startsWith("spark://") && !this.master().startsWith("mesos://")) {
         SparkSubmit$.MODULE$.printErrorAndExit("Killing submissions is only supported in standalone or Mesos mode!");
      }

      if (this.submissionToKill() == null) {
         SparkSubmit$.MODULE$.printErrorAndExit("Please specify a submission to kill.");
      }

   }

   private void validateStatusRequestArguments() {
      if (!this.master().startsWith("spark://") && !this.master().startsWith("mesos://")) {
         SparkSubmit$.MODULE$.printErrorAndExit("Requesting submission statuses is only supported in standalone or Mesos mode!");
      }

      if (this.submissionToRequestStatusFor() == null) {
         SparkSubmit$.MODULE$.printErrorAndExit("Please specify a submission to request status for.");
      }

   }

   public boolean isStandaloneCluster() {
      boolean var2;
      label25: {
         if (this.master().startsWith("spark://")) {
            String var10000 = this.deployMode();
            String var1 = "cluster";
            if (var10000 == null) {
               if (var1 == null) {
                  break label25;
               }
            } else if (var10000.equals(var1)) {
               break label25;
            }
         }

         var2 = false;
         return var2;
      }

      var2 = true;
      return var2;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Parsed arguments:\n    |  master                  ", "\n    |  deployMode              ", "\n    |  executorMemory          ", "\n    |  executorCores           ", "\n    |  totalExecutorCores      ", "\n    |  propertiesFile          ", "\n    |  driverMemory            ", "\n    |  driverCores             ", "\n    |  driverExtraClassPath    ", "\n    |  driverExtraLibraryPath  ", "\n    |  driverExtraJavaOptions  ", "\n    |  supervise               ", "\n    |  queue                   ", "\n    |  numExecutors            ", "\n    |  files                   ", "\n    |  pyFiles                 ", "\n    |  archives                ", "\n    |  mainClass               ", "\n    |  primaryResource         ", "\n    |  name                    ", "\n    |  childArgs               [", "]\n    |  jars                    ", "\n    |  packages                ", "\n    |  packagesExclusions      ", "\n    |  repositories            ", "\n    |  verbose                 ", "\n    |\n    |Spark properties used, including those specified through\n    | --conf and those from the properties file ", ":\n    |", "\n    "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.master(), this.deployMode(), this.executorMemory(), this.executorCores(), this.totalExecutorCores(), this.propertiesFile(), this.driverMemory(), this.driverCores(), this.driverExtraClassPath(), this.driverExtraLibraryPath(), this.driverExtraJavaOptions(), BoxesRunTime.boxToBoolean(this.supervise()), this.queue(), this.numExecutors(), this.files(), this.pyFiles(), this.archives(), this.mainClass(), this.primaryResource(), this.name(), this.childArgs().mkString(" "), this.jars(), this.packages(), this.packagesExclusions(), this.repositories(), BoxesRunTime.boxToBoolean(this.verbose()), this.propertiesFile(), Utils$.MODULE$.redact(this.sparkProperties()).mkString("  ", "\n  ", "\n")}))))).stripMargin();
   }

   public boolean handle(String opt, String value) {
      if ("--name".equals(opt)) {
         this.name_$eq(value);
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else if ("--master".equals(opt)) {
         this.master_$eq(value);
         BoxedUnit var14 = BoxedUnit.UNIT;
      } else if ("--class".equals(opt)) {
         this.mainClass_$eq(value);
         BoxedUnit var15 = BoxedUnit.UNIT;
      } else if ("--deploy-mode".equals(opt)) {
         label138: {
            String var5 = "client";
            if (value == null) {
               if (var5 == null) {
                  break label138;
               }
            } else if (value.equals(var5)) {
               break label138;
            }

            String var6 = "cluster";
            if (value == null) {
               if (var6 == null) {
                  break label138;
               }
            } else if (value.equals(var6)) {
               break label138;
            }

            SparkSubmit$.MODULE$.printErrorAndExit("--deploy-mode must be either \"client\" or \"cluster\"");
         }

         this.deployMode_$eq(value);
         BoxedUnit var16 = BoxedUnit.UNIT;
      } else if ("--num-executors".equals(opt)) {
         this.numExecutors_$eq(value);
         BoxedUnit var17 = BoxedUnit.UNIT;
      } else if ("--total-executor-cores".equals(opt)) {
         this.totalExecutorCores_$eq(value);
         BoxedUnit var18 = BoxedUnit.UNIT;
      } else if ("--executor-cores".equals(opt)) {
         this.executorCores_$eq(value);
         BoxedUnit var19 = BoxedUnit.UNIT;
      } else if ("--executor-memory".equals(opt)) {
         this.executorMemory_$eq(value);
         BoxedUnit var20 = BoxedUnit.UNIT;
      } else if ("--driver-memory".equals(opt)) {
         this.driverMemory_$eq(value);
         BoxedUnit var21 = BoxedUnit.UNIT;
      } else if ("--driver-cores".equals(opt)) {
         this.driverCores_$eq(value);
         BoxedUnit var22 = BoxedUnit.UNIT;
      } else if ("--driver-class-path".equals(opt)) {
         this.driverExtraClassPath_$eq(value);
         BoxedUnit var23 = BoxedUnit.UNIT;
      } else if ("--driver-java-options".equals(opt)) {
         this.driverExtraJavaOptions_$eq(value);
         BoxedUnit var24 = BoxedUnit.UNIT;
      } else if ("--driver-library-path".equals(opt)) {
         this.driverExtraLibraryPath_$eq(value);
         BoxedUnit var25 = BoxedUnit.UNIT;
      } else if ("--properties-file".equals(opt)) {
         this.propertiesFile_$eq(value);
         BoxedUnit var26 = BoxedUnit.UNIT;
      } else if ("--kill".equals(opt)) {
         this.submissionToKill_$eq(value);
         if (this.action() != null) {
            SparkSubmit$.MODULE$.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Action cannot be both ", " and ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.action(), SparkSubmitAction$.MODULE$.KILL()})));
         }

         this.action_$eq(SparkSubmitAction$.MODULE$.KILL());
         BoxedUnit var27 = BoxedUnit.UNIT;
      } else if ("--status".equals(opt)) {
         this.submissionToRequestStatusFor_$eq(value);
         if (this.action() != null) {
            SparkSubmit$.MODULE$.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Action cannot be both ", " and ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.action(), SparkSubmitAction$.MODULE$.REQUEST_STATUS()})));
         }

         this.action_$eq(SparkSubmitAction$.MODULE$.REQUEST_STATUS());
         BoxedUnit var28 = BoxedUnit.UNIT;
      } else if ("--supervise".equals(opt)) {
         this.supervise_$eq(true);
         BoxedUnit var29 = BoxedUnit.UNIT;
      } else if ("--queue".equals(opt)) {
         this.queue_$eq(value);
         BoxedUnit var30 = BoxedUnit.UNIT;
      } else if ("--files".equals(opt)) {
         this.files_$eq(Utils$.MODULE$.resolveURIs(value));
         BoxedUnit var31 = BoxedUnit.UNIT;
      } else if ("--py-files".equals(opt)) {
         this.pyFiles_$eq(Utils$.MODULE$.resolveURIs(value));
         BoxedUnit var32 = BoxedUnit.UNIT;
      } else if ("--archives".equals(opt)) {
         this.archives_$eq(Utils$.MODULE$.resolveURIs(value));
         BoxedUnit var33 = BoxedUnit.UNIT;
      } else if ("--jars".equals(opt)) {
         this.jars_$eq(Utils$.MODULE$.resolveURIs(value));
         BoxedUnit var34 = BoxedUnit.UNIT;
      } else if ("--packages".equals(opt)) {
         this.packages_$eq(value);
         BoxedUnit var35 = BoxedUnit.UNIT;
      } else if ("--exclude-packages".equals(opt)) {
         this.packagesExclusions_$eq(value);
         BoxedUnit var36 = BoxedUnit.UNIT;
      } else if ("--repositories".equals(opt)) {
         this.repositories_$eq(value);
         BoxedUnit var37 = BoxedUnit.UNIT;
      } else if ("--conf".equals(opt)) {
         Tuple2 var8 = SparkSubmit$.MODULE$.parseSparkConfProperty(value);
         if (var8 == null) {
            throw new MatchError(var8);
         }

         String confName = (String)var8._1();
         String confValue = (String)var8._2();
         Tuple2 var11 = new Tuple2(confName, confValue);
         String confName = (String)var11._1();
         String confValue = (String)var11._2();
         this.sparkProperties().update(confName, confValue);
         BoxedUnit var38 = BoxedUnit.UNIT;
      } else if ("--proxy-user".equals(opt)) {
         this.proxyUser_$eq(value);
         BoxedUnit var39 = BoxedUnit.UNIT;
      } else if ("--principal".equals(opt)) {
         this.principal_$eq(value);
         BoxedUnit var40 = BoxedUnit.UNIT;
      } else if ("--keytab".equals(opt)) {
         this.keytab_$eq(value);
         BoxedUnit var41 = BoxedUnit.UNIT;
      } else if ("--help".equals(opt)) {
         this.printUsageAndExit(0, this.printUsageAndExit$default$2());
         BoxedUnit var42 = BoxedUnit.UNIT;
      } else if ("--verbose".equals(opt)) {
         this.verbose_$eq(true);
         BoxedUnit var43 = BoxedUnit.UNIT;
      } else if ("--version".equals(opt)) {
         SparkSubmit$.MODULE$.printVersionAndExit();
         BoxedUnit var44 = BoxedUnit.UNIT;
      } else {
         if (!"--usage-error".equals(opt)) {
            throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unexpected argument '", "'."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{opt})));
         }

         this.printUsageAndExit(1, this.printUsageAndExit$default$2());
         BoxedUnit var45 = BoxedUnit.UNIT;
      }

      return true;
   }

   public boolean handleUnknown(String opt) {
      if (opt.startsWith("-")) {
         SparkSubmit$.MODULE$.printErrorAndExit((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Unrecognized option '", "'."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{opt})));
      }

      this.primaryResource_$eq(!SparkSubmit$.MODULE$.isShell(opt) && !SparkSubmit$.MODULE$.isInternal(opt) ? Utils$.MODULE$.resolveURI(opt).toString() : opt);
      this.isPython_$eq(SparkSubmit$.MODULE$.isPython(opt));
      this.isR_$eq(SparkSubmit$.MODULE$.isR(opt));
      return false;
   }

   public void handleExtraArgs(List<String> extra) {
      this.childArgs().$plus$plus$eq((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(extra).asScala());
   }

   private void printUsageAndExit(int exitCode, Object unknownParam) {
      PrintStream outStream = SparkSubmit$.MODULE$.printStream();
      if (unknownParam != null) {
         outStream.println((new StringBuilder()).append((Object)"Unknown/unsupported param ").append(unknownParam).toString());
      }

      String command = (String)package$.MODULE$.env().get("_SPARK_CMD_USAGE").getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return (new StringOps(Predef$.MODULE$.augmentString("Usage: spark-submit [options] <app jar | python file | R file> [app arguments]\n        |Usage: spark-submit --kill [submission ID] --master [spark://...]\n        |Usage: spark-submit --status [submission ID] --master [spark://...]\n        |Usage: spark-submit run-example [options] example-class [example args]"))).stripMargin();
         }
      });
      outStream.println(command);
      int mem_mb = Utils$.MODULE$.DEFAULT_DRIVER_MEM_MB();
      outStream.println((new StringOps(Predef$.MODULE$.augmentString((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"\n        |Options:\n        |  --master MASTER_URL         spark://host:port, mesos://host:port, yarn,\n        |                              k8s://https://host:port, or local (Default: local[*]).\n        |  --deploy-mode DEPLOY_MODE   Whether to launch the driver program locally (\"client\") or\n        |                              on one of the worker machines inside the cluster (\"cluster\")\n        |                              (Default: client).\n        |  --class CLASS_NAME          Your application's main class (for Java / Scala apps).\n        |  --name NAME                 A name of your application.\n        |  --jars JARS                 Comma-separated list of jars to include on the driver\n        |                              and executor classpaths.\n        |  --packages                  Comma-separated list of maven coordinates of jars to include\n        |                              on the driver and executor classpaths. Will search the local\n        |                              maven repo, then maven central and any additional remote\n        |                              repositories given by --repositories. The format for the\n        |                              coordinates should be groupId:artifactId:version.\n        |  --exclude-packages          Comma-separated list of groupId:artifactId, to exclude while\n        |                              resolving the dependencies provided in --packages to avoid\n        |                              dependency conflicts.\n        |  --repositories              Comma-separated list of additional remote repositories to\n        |                              search for the maven coordinates given with --packages.\n        |  --py-files PY_FILES         Comma-separated list of .zip, .egg, or .py files to place\n        |                              on the PYTHONPATH for Python apps.\n        |  --files FILES               Comma-separated list of files to be placed in the working\n        |                              directory of each executor. File paths of these files\n        |                              in executors can be accessed via SparkFiles.get(fileName).\n        |\n        |  --conf PROP=VALUE           Arbitrary Spark configuration property.\n        |  --properties-file FILE      Path to a file from which to load extra properties. If not\n        |                              specified, this will look for conf/spark-defaults.conf.\n        |\n        |  --driver-memory MEM         Memory for driver (e.g. 1000M, 2G) (Default: ", "M).\n        |  --driver-java-options       Extra Java options to pass to the driver.\n        |  --driver-library-path       Extra library path entries to pass to the driver.\n        |  --driver-class-path         Extra class path entries to pass to the driver. Note that\n        |                              jars added with --jars are automatically included in the\n        |                              classpath.\n        |\n        |  --executor-memory MEM       Memory per executor (e.g. 1000M, 2G) (Default: 1G).\n        |\n        |  --proxy-user NAME           User to impersonate when submitting the application.\n        |                              This argument does not work with --principal / --keytab.\n        |\n        |  --help, -h                  Show this help message and exit.\n        |  --verbose, -v               Print additional debug output.\n        |  --version,                  Print the version of current Spark.\n        |\n        | Cluster deploy mode only:\n        |  --driver-cores NUM          Number of cores used by the driver, only in cluster mode\n        |                              (Default: 1).\n        |\n        | Spark standalone or Mesos with cluster deploy mode only:\n        |  --supervise                 If given, restarts the driver on failure.\n        |  --kill SUBMISSION_ID        If given, kills the driver specified.\n        |  --status SUBMISSION_ID      If given, requests the status of the driver specified.\n        |\n        | Spark standalone and Mesos only:\n        |  --total-executor-cores NUM  Total cores for all executors.\n        |\n        | Spark standalone and YARN only:\n        |  --executor-cores NUM        Number of cores per executor. (Default: 1 in YARN mode,\n        |                              or all available cores on the worker in standalone mode)\n        |\n        | YARN-only:\n        |  --queue QUEUE_NAME          The YARN queue to submit to (Default: \"default\").\n        |  --num-executors NUM         Number of executors to launch (Default: 2).\n        |                              If dynamic allocation is enabled, the initial number of\n        |                              executors will be at least NUM.\n        |  --archives ARCHIVES         Comma separated list of archives to be extracted into the\n        |                              working directory of each executor.\n        |  --principal PRINCIPAL       Principal to be used to login to KDC, while running on\n        |                              secure HDFS.\n        |  --keytab KEYTAB             The full path to the file that contains the keytab for the\n        |                              principal specified above. This keytab will be copied to\n        |                              the node running the Application Master via the Secure\n        |                              Distributed Cache, for renewing the login tickets and the\n        |                              delegation tokens periodically.\n      "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(mem_mb)}))))).stripMargin());
      if (SparkSubmit$.MODULE$.isSqlShell(this.mainClass())) {
         outStream.println("CLI options:");
         outStream.println(this.getSqlShellOptions());
      }

      SparkSubmit$.MODULE$.exitFn().apply$mcVI$sp(exitCode);
   }

   private Object printUsageAndExit$default$2() {
      return null;
   }

   private String getSqlShellOptions() {
      PrintStream currentOut = System.out;
      PrintStream currentErr = System.err;
      SecurityManager currentSm = System.getSecurityManager();

      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         PrintStream stream = new PrintStream(out);
         System.setOut(stream);
         System.setErr(stream);
         SecurityManager sm = new SecurityManager(this) {
            public void checkExit(int status) {
               throw new SecurityException();
            }

            public void checkPermission(Permission perm) {
            }
         };
         System.setSecurityManager(sm);

         try {
            Utils$.MODULE$.classForName(this.mainClass()).getMethod("main", String[].class).invoke((Object)null, (Object)(Object[])(new String[]{"--help"}));
         } catch (InvocationTargetException var11) {
            if (!(var11.getCause() instanceof SecurityException)) {
               throw var11;
            }

            BoxedUnit var10000 = BoxedUnit.UNIT;
         }

         stream.flush();
         Source$.MODULE$.fromString(new String(out.toByteArray(), StandardCharsets.UTF_8)).getLines().filter(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(String line) {
               return !line.startsWith("log4j") && !line.startsWith("usage");
            }
         }).mkString("\n");
      } finally {
         System.setSecurityManager(currentSm);
         System.setOut(currentOut);
         System.setErr(currentErr);
      }

      return currentErr;
   }

   public SparkSubmitArguments(Seq<String> args, Map<String, String> env) {
      this.args = args;
      this.org$apache$spark$deploy$SparkSubmitArguments$$env = env;
      this.master = null;
      this.deployMode = null;
      this.executorMemory = null;
      this.executorCores = null;
      this.totalExecutorCores = null;
      this.propertiesFile = null;
      this.driverMemory = null;
      this.driverExtraClassPath = null;
      this.driverExtraLibraryPath = null;
      this.driverExtraJavaOptions = null;
      this.queue = null;
      this.numExecutors = null;
      this.files = null;
      this.archives = null;
      this.mainClass = null;
      this.primaryResource = null;
      this.name = null;
      this.childArgs = new ArrayBuffer<String>();
      this.jars = null;
      this.packages = null;
      this.repositories = null;
      this.ivyRepoPath = null;
      this.packagesExclusions = null;
      this.verbose = false;
      this.isPython = false;
      this.pyFiles = null;
      this.isR = false;
      this.action = null;
      this.sparkProperties = new HashMap<String, String>();
      this.proxyUser = null;
      this.principal = null;
      this.keytab = null;
      this.supervise = false;
      this.driverCores = null;
      this.submissionToKill = null;
      this.submissionToRequestStatusFor = null;
      this.useRest = true;

      try {
         this.parse((List)JavaConverters$.MODULE$.seqAsJavaListConverter(args).asJava());
      } catch (IllegalArgumentException var4) {
         SparkSubmit$.MODULE$.printErrorAndExit(var4.getMessage());
      }

      this.mergeDefaultSparkProperties();
      this.ignoreNonSparkProperties();
      this.loadEnvironmentArguments();
      this.validateArguments();
   }
}
