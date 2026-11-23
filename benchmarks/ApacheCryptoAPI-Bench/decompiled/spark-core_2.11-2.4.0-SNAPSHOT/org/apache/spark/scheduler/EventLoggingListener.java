package org.apache.spark.scheduler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream.SyncFlag;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.io.CompressionCodec;
import org.apache.spark.io.CompressionCodec$;
import org.apache.spark.util.JsonProtocol$;
import org.apache.spark.util.Utils$;
import org.json4s.Formats;
import org.json4s.JsonAST;
import org.json4s.jackson.JsonMethods;
import org.json4s.jackson.JsonMethods.;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Map;
import scala.collection.Map$;
import scala.collection.Seq;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.ArrayBuffer;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\r%g!B\u0001\u0003\u0001\u0011Q!\u0001F#wK:$Hj\\4hS:<G*[:uK:,'O\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\b\u0011\u00051iQ\"\u0001\u0002\n\u00059\u0011!!D*qCJ\\G*[:uK:,'\u000f\u0005\u0002\u0011'5\t\u0011C\u0003\u0002\u0013\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u0015#\t9Aj\\4hS:<\u0007\u0002\u0003\f\u0001\u0005\u0003\u0005\u000b\u0011\u0002\r\u0002\u000b\u0005\u0004\b/\u00133\u0004\u0001A\u0011\u0011d\b\b\u00035ui\u0011a\u0007\u0006\u00029\u0005)1oY1mC&\u0011adG\u0001\u0007!J,G-\u001a4\n\u0005\u0001\n#AB*ue&twM\u0003\u0002\u001f7!A1\u0005\u0001B\u0001B\u0003%A%\u0001\u0007baB\fE\u000f^3naRLE\rE\u0002\u001bKaI!AJ\u000e\u0003\r=\u0003H/[8o\u0011!A\u0003A!A!\u0002\u0013I\u0013A\u00037pO\n\u000b7/\u001a#jeB\u0011!fL\u0007\u0002W)\u0011A&L\u0001\u0004]\u0016$(\"\u0001\u0018\u0002\t)\fg/Y\u0005\u0003a-\u00121!\u0016*J\u0011!\u0011\u0004A!A!\u0002\u0013\u0019\u0014!C:qCJ\\7i\u001c8g!\t!T'D\u0001\u0005\u0013\t1DAA\u0005Ta\u0006\u00148nQ8oM\"A\u0001\b\u0001B\u0001B\u0003%\u0011(\u0001\u0006iC\u0012|w\u000e]\"p]\u001a\u0004\"AO \u000e\u0003mR!\u0001P\u001f\u0002\t\r|gN\u001a\u0006\u0003}\u0019\ta\u0001[1e_>\u0004\u0018B\u0001!<\u00055\u0019uN\u001c4jOV\u0014\u0018\r^5p]\")!\t\u0001C\u0001\u0007\u00061A(\u001b8jiz\"b\u0001R#G\u000f\"K\u0005C\u0001\u0007\u0001\u0011\u00151\u0012\t1\u0001\u0019\u0011\u0015\u0019\u0013\t1\u0001%\u0011\u0015A\u0013\t1\u0001*\u0011\u0015\u0011\u0014\t1\u00014\u0011\u0015A\u0014\t1\u0001:\u0011\u0015\u0011\u0005\u0001\"\u0001L)\u0015!E*\u0014(P\u0011\u00151\"\n1\u0001\u0019\u0011\u0015\u0019#\n1\u0001%\u0011\u0015A#\n1\u0001*\u0011\u0015\u0011$\n1\u00014\u0011\u001d\t\u0006A1A\u0005\nI\u000bab\u001d5pk2$7i\\7qe\u0016\u001c8/F\u0001T!\tQB+\u0003\u0002V7\t9!i\\8mK\u0006t\u0007BB,\u0001A\u0003%1+A\btQ>,H\u000eZ\"p[B\u0014Xm]:!\u0011\u001dI\u0006A1A\u0005\nI\u000bqb\u001d5pk2$wJ^3soJLG/\u001a\u0005\u00077\u0002\u0001\u000b\u0011B*\u0002!MDw.\u001e7e\u001fZ,'o\u001e:ji\u0016\u0004\u0003bB/\u0001\u0005\u0004%IAU\u0001\u0016g\"|W\u000f\u001c3M_\u001e\u0014En\\2l+B$\u0017\r^3t\u0011\u0019y\u0006\u0001)A\u0005'\u000612\u000f[8vY\u0012dun\u001a\"m_\u000e\\W\u000b\u001d3bi\u0016\u001c\b\u0005C\u0004b\u0001\t\u0007I\u0011\u0002*\u0002\u000fQ,7\u000f^5oO\"11\r\u0001Q\u0001\nM\u000b\u0001\u0002^3ti&tw\r\t\u0005\bK\u0002\u0011\r\u0011\"\u0003g\u0003AyW\u000f\u001e9vi\n+hMZ3s'&TX-F\u0001h!\tQ\u0002.\u0003\u0002j7\t\u0019\u0011J\u001c;\t\r-\u0004\u0001\u0015!\u0003h\u0003EyW\u000f\u001e9vi\n+hMZ3s'&TX\r\t\u0005\b[\u0002\u0011\r\u0011\"\u0003o\u0003)1\u0017\u000e\\3TsN$X-\\\u000b\u0002_B\u0011\u0001o]\u0007\u0002c*\u0011!/P\u0001\u0003MNL!\u0001^9\u0003\u0015\u0019KG.Z*zgR,W\u000e\u0003\u0004w\u0001\u0001\u0006Ia\\\u0001\fM&dWmU=ti\u0016l\u0007\u0005C\u0004y\u0001\t\u0007I\u0011B=\u0002!\r|W\u000e\u001d:fgNLwN\\\"pI\u0016\u001cW#\u0001>\u0011\u0007i)3\u0010\u0005\u0002}\u007f6\tQP\u0003\u0002\u007f\t\u0005\u0011\u0011n\\\u0005\u0004\u0003\u0003i(\u0001E\"p[B\u0014Xm]:j_:\u001cu\u000eZ3d\u0011\u001d\t)\u0001\u0001Q\u0001\ni\f\u0011cY8naJ,7o]5p]\u000e{G-Z2!\u0011%\tI\u0001\u0001b\u0001\n\u0013\tY!\u0001\u000bd_6\u0004(/Z:tS>t7i\u001c3fG:\u000bW.Z\u000b\u0002I!9\u0011q\u0002\u0001!\u0002\u0013!\u0013!F2p[B\u0014Xm]:j_:\u001cu\u000eZ3d\u001d\u0006lW\r\t\u0005\n\u0003'\u0001\u0001\u0019!C\u0005\u0003+\t\u0001\u0003[1e_>\u0004H)\u0019;b'R\u0014X-Y7\u0016\u0005\u0005]\u0001\u0003\u0002\u000e&\u00033\u00012\u0001]A\u000e\u0013\r\ti\"\u001d\u0002\u0013\rN#\u0015\r^1PkR\u0004X\u000f^*ue\u0016\fW\u000eC\u0005\u0002\"\u0001\u0001\r\u0011\"\u0003\u0002$\u0005!\u0002.\u00193p_B$\u0015\r^1TiJ,\u0017-\\0%KF$B!!\n\u0002,A\u0019!$a\n\n\u0007\u0005%2D\u0001\u0003V]&$\bBCA\u0017\u0003?\t\t\u00111\u0001\u0002\u0018\u0005\u0019\u0001\u0010J\u0019\t\u0011\u0005E\u0002\u0001)Q\u0005\u0003/\t\u0011\u0003[1e_>\u0004H)\u0019;b'R\u0014X-Y7!\u0011%\t)\u0004\u0001a\u0001\n\u0013\t9$\u0001\u0004xe&$XM]\u000b\u0003\u0003s\u0001BAG\u0013\u0002<A!\u0011QHA!\u001b\t\tyD\u0003\u0002\u007f[%!\u00111IA \u0005-\u0001&/\u001b8u/JLG/\u001a:\t\u0013\u0005\u001d\u0003\u00011A\u0005\n\u0005%\u0013AC<sSR,'o\u0018\u0013fcR!\u0011QEA&\u0011)\ti#!\u0012\u0002\u0002\u0003\u0007\u0011\u0011\b\u0005\t\u0003\u001f\u0002\u0001\u0015)\u0003\u0002:\u00059qO]5uKJ\u0004\u0003BCA*\u0001\t\u0007I\u0011\u0001\u0002\u0002V\u0005aAn\\4hK\u0012,e/\u001a8ugV\u0011\u0011q\u000b\t\u0007\u00033\n\u0019'a\u001a\u000e\u0005\u0005m#\u0002BA/\u0003?\nq!\\;uC\ndWMC\u0002\u0002bm\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t)'a\u0017\u0003\u0017\u0005\u0013(/Y=Ck\u001a4WM\u001d\t\u0005\u0003S\n\tI\u0004\u0003\u0002l\u0005md\u0002BA7\u0003orA!a\u001c\u0002v5\u0011\u0011\u0011\u000f\u0006\u0004\u0003g:\u0012A\u0002\u001fs_>$h(C\u0001\n\u0013\r\tI\bC\u0001\u0007UN|g\u000eN:\n\t\u0005u\u0014qP\u0001\b\u0015N|g.Q*U\u0015\r\tI\bC\u0005\u0005\u0003\u0007\u000b)I\u0001\u0004K-\u0006dW/\u001a\u0006\u0005\u0003{\ny\b\u0003\u0005\u0002\n\u0002\u0001\u000b\u0011BA,\u00035awnZ4fI\u00163XM\u001c;tA!Q\u0011Q\u0012\u0001C\u0002\u0013\u0005!!a$\u0002\u000f1|w\rU1uQV\t\u0001\u0004C\u0004\u0002\u0014\u0002\u0001\u000b\u0011\u0002\r\u0002\u00111|w\rU1uQ\u0002Bq!a&\u0001\t\u0003\tI*A\u0003ti\u0006\u0014H\u000f\u0006\u0002\u0002&!9\u0011Q\u0014\u0001\u0005\n\u0005}\u0015\u0001\u00037pO\u00163XM\u001c;\u0015\r\u0005\u0015\u0012\u0011UAV\u0011!\t\u0019+a'A\u0002\u0005\u0015\u0016!B3wK:$\bc\u0001\u0007\u0002(&\u0019\u0011\u0011\u0016\u0002\u0003%M\u0003\u0018M]6MSN$XM\\3s\u000bZ,g\u000e\u001e\u0005\n\u0003[\u000bY\n%AA\u0002M\u000b1B\u001a7vg\"dunZ4fe\"9\u0011\u0011\u0017\u0001\u0005B\u0005M\u0016\u0001E8o'R\fw-Z*vE6LG\u000f^3e)\u0011\t)#!.\t\u0011\u0005\r\u0016q\u0016a\u0001\u0003o\u00032\u0001DA]\u0013\r\tYL\u0001\u0002\u001c'B\f'o\u001b'jgR,g.\u001a:Ti\u0006<WmU;c[&$H/\u001a3\t\u000f\u0005}\u0006\u0001\"\u0011\u0002B\u0006YqN\u001c+bg.\u001cF/\u0019:u)\u0011\t)#a1\t\u0011\u0005\r\u0016Q\u0018a\u0001\u0003\u000b\u00042\u0001DAd\u0013\r\tIM\u0001\u0002\u0017'B\f'o\u001b'jgR,g.\u001a:UCN\\7\u000b^1si\"9\u0011Q\u001a\u0001\u0005B\u0005=\u0017aE8o)\u0006\u001c8nR3ui&twMU3tk2$H\u0003BA\u0013\u0003#D\u0001\"a)\u0002L\u0002\u0007\u00111\u001b\t\u0004\u0019\u0005U\u0017bAAl\u0005\tq2\u000b]1sW2K7\u000f^3oKJ$\u0016m]6HKR$\u0018N\\4SKN,H\u000e\u001e\u0005\b\u00037\u0004A\u0011IAo\u0003%yg\u000eV1tW\u0016sG\r\u0006\u0003\u0002&\u0005}\u0007\u0002CAR\u00033\u0004\r!!9\u0011\u00071\t\u0019/C\u0002\u0002f\n\u0011Ac\u00159be.d\u0015n\u001d;f]\u0016\u0014H+Y:l\u000b:$\u0007bBAu\u0001\u0011\u0005\u00131^\u0001\u0014_:,eN^5s_:lWM\u001c;Va\u0012\fG/\u001a\u000b\u0005\u0003K\ti\u000f\u0003\u0005\u0002$\u0006\u001d\b\u0019AAx!\ra\u0011\u0011_\u0005\u0004\u0003g\u0014!AH*qCJ\\G*[:uK:,'/\u00128wSJ|g.\\3oiV\u0003H-\u0019;f\u0011\u001d\t9\u0010\u0001C!\u0003s\f\u0001c\u001c8Ti\u0006<WmQ8na2,G/\u001a3\u0015\t\u0005\u0015\u00121 \u0005\t\u0003G\u000b)\u00101\u0001\u0002~B\u0019A\"a@\n\u0007\t\u0005!AA\u000eTa\u0006\u00148\u000eT5ti\u0016tWM]*uC\u001e,7i\\7qY\u0016$X\r\u001a\u0005\b\u0005\u000b\u0001A\u0011\tB\u0004\u0003)ygNS8c'R\f'\u000f\u001e\u000b\u0005\u0003K\u0011I\u0001\u0003\u0005\u0002$\n\r\u0001\u0019\u0001B\u0006!\ra!QB\u0005\u0004\u0005\u001f\u0011!!F*qCJ\\G*[:uK:,'OS8c'R\f'\u000f\u001e\u0005\b\u0005'\u0001A\u0011\tB\u000b\u0003!ygNS8c\u000b:$G\u0003BA\u0013\u0005/A\u0001\"a)\u0003\u0012\u0001\u0007!\u0011\u0004\t\u0004\u0019\tm\u0011b\u0001B\u000f\u0005\t\u00192\u000b]1sW2K7\u000f^3oKJTuNY#oI\"9!\u0011\u0005\u0001\u0005B\t\r\u0012aE8o\u00052|7m['b]\u0006<WM]!eI\u0016$G\u0003BA\u0013\u0005KA\u0001\"a)\u0003 \u0001\u0007!q\u0005\t\u0004\u0019\t%\u0012b\u0001B\u0016\u0005\tq2\u000b]1sW2K7\u000f^3oKJ\u0014En\\2l\u001b\u0006t\u0017mZ3s\u0003\u0012$W\r\u001a\u0005\b\u0005_\u0001A\u0011\tB\u0019\u0003UygN\u00117pG.l\u0015M\\1hKJ\u0014V-\\8wK\u0012$B!!\n\u00034!A\u00111\u0015B\u0017\u0001\u0004\u0011)\u0004E\u0002\r\u0005oI1A!\u000f\u0003\u0005\u0001\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\ncwnY6NC:\fw-\u001a:SK6|g/\u001a3\t\u000f\tu\u0002\u0001\"\u0011\u0003@\u0005qqN\\+oa\u0016\u00148/[:u%\u0012#E\u0003BA\u0013\u0005\u0003B\u0001\"a)\u0003<\u0001\u0007!1\t\t\u0004\u0019\t\u0015\u0013b\u0001B$\u0005\tI2\u000b]1sW2K7\u000f^3oKJ,f\u000e]3sg&\u001cHO\u0015#E\u0011\u001d\u0011Y\u0005\u0001C!\u0005\u001b\n!c\u001c8BaBd\u0017nY1uS>t7\u000b^1siR!\u0011Q\u0005B(\u0011!\t\u0019K!\u0013A\u0002\tE\u0003c\u0001\u0007\u0003T%\u0019!Q\u000b\u0002\u0003;M\u0003\u0018M]6MSN$XM\\3s\u0003B\u0004H.[2bi&|gn\u0015;beRDqA!\u0017\u0001\t\u0003\u0012Y&\u0001\tp]\u0006\u0003\b\u000f\\5dCRLwN\\#oIR!\u0011Q\u0005B/\u0011!\t\u0019Ka\u0016A\u0002\t}\u0003c\u0001\u0007\u0003b%\u0019!1\r\u0002\u00037M\u0003\u0018M]6MSN$XM\\3s\u0003B\u0004H.[2bi&|g.\u00128e\u0011\u001d\u00119\u0007\u0001C!\u0005S\nqb\u001c8Fq\u0016\u001cW\u000f^8s\u0003\u0012$W\r\u001a\u000b\u0005\u0003K\u0011Y\u0007\u0003\u0005\u0002$\n\u0015\u0004\u0019\u0001B7!\ra!qN\u0005\u0004\u0005c\u0012!AG*qCJ\\G*[:uK:,'/\u0012=fGV$xN]!eI\u0016$\u0007b\u0002B;\u0001\u0011\u0005#qO\u0001\u0012_:,\u00050Z2vi>\u0014(+Z7pm\u0016$G\u0003BA\u0013\u0005sB\u0001\"a)\u0003t\u0001\u0007!1\u0010\t\u0004\u0019\tu\u0014b\u0001B@\u0005\ta2\u000b]1sW2K7\u000f^3oKJ,\u00050Z2vi>\u0014(+Z7pm\u0016$\u0007b\u0002BB\u0001\u0011\u0005#QQ\u0001\u0016_:,\u00050Z2vi>\u0014(\t\\1dW2L7\u000f^3e)\u0011\t)Ca\"\t\u0011\u0005\r&\u0011\u0011a\u0001\u0005\u0013\u00032\u0001\u0004BF\u0013\r\u0011iI\u0001\u0002!'B\f'o\u001b'jgR,g.\u001a:Fq\u0016\u001cW\u000f^8s\u00052\f7m\u001b7jgR,G\rC\u0004\u0003\u0012\u0002!\tEa%\u0002;=tW\t_3dkR|'O\u00117bG.d\u0017n\u001d;fI\u001a{'o\u0015;bO\u0016$B!!\n\u0003\u0016\"A\u00111\u0015BH\u0001\u0004\u00119\nE\u0002\r\u00053K1Aa'\u0003\u0005!\u001a\u0006/\u0019:l\u0019&\u001cH/\u001a8fe\u0016CXmY;u_J\u0014E.Y2lY&\u001cH/\u001a3G_J\u001cF/Y4f\u0011\u001d\u0011y\n\u0001C!\u0005C\u000b\u0011d\u001c8O_\u0012,'\t\\1dW2L7\u000f^3e\r>\u00148\u000b^1hKR!\u0011Q\u0005BR\u0011!\t\u0019K!(A\u0002\t\u0015\u0006c\u0001\u0007\u0003(&\u0019!\u0011\u0016\u0002\u0003IM\u0003\u0018M]6MSN$XM\\3s\u001d>$WM\u00117bG.d\u0017n\u001d;fI\u001a{'o\u0015;bO\u0016DqA!,\u0001\t\u0003\u0012y+A\fp]\u0016CXmY;u_J,fN\u00197bG.d\u0017n\u001d;fIR!\u0011Q\u0005BY\u0011!\t\u0019Ka+A\u0002\tM\u0006c\u0001\u0007\u00036&\u0019!q\u0017\u0002\u0003EM\u0003\u0018M]6MSN$XM\\3s\u000bb,7-\u001e;peVs'\r\\1dW2L7\u000f^3e\u0011\u001d\u0011Y\f\u0001C!\u0005{\u000b\u0011c\u001c8O_\u0012,'\t\\1dW2L7\u000f^3e)\u0011\t)Ca0\t\u0011\u0005\r&\u0011\u0018a\u0001\u0005\u0003\u00042\u0001\u0004Bb\u0013\r\u0011)M\u0001\u0002\u001d'B\f'o\u001b'jgR,g.\u001a:O_\u0012,'\t\\1dW2L7\u000f^3e\u0011\u001d\u0011I\r\u0001C!\u0005\u0017\f1c\u001c8O_\u0012,WK\u001c2mC\u000e\\G.[:uK\u0012$B!!\n\u0003N\"A\u00111\u0015Bd\u0001\u0004\u0011y\rE\u0002\r\u0005#L1Aa5\u0003\u0005y\u0019\u0006/\u0019:l\u0019&\u001cH/\u001a8fe:{G-Z+oE2\f7m\u001b7jgR,G\rC\u0004\u0003X\u0002!\tE!7\u0002\u001d=t'\t\\8dWV\u0003H-\u0019;fIR!\u0011Q\u0005Bn\u0011!\t\u0019K!6A\u0002\tu\u0007c\u0001\u0007\u0003`&\u0019!\u0011\u001d\u0002\u00033M\u0003\u0018M]6MSN$XM\\3s\u00052|7m[+qI\u0006$X\r\u001a\u0005\b\u0005K\u0004A\u0011\tBt\u0003]yg.\u0012=fGV$xN]'fiJL7m]+qI\u0006$X\r\u0006\u0003\u0002&\t%\b\u0002CAR\u0005G\u0004\rAa;\u0011\u00071\u0011i/C\u0002\u0003p\n\u0011!e\u00159be.d\u0015n\u001d;f]\u0016\u0014X\t_3dkR|'/T3ue&\u001c7/\u00169eCR,\u0007b\u0002Bz\u0001\u0011\u0005#Q_\u0001\r_:|E\u000f[3s\u000bZ,g\u000e\u001e\u000b\u0005\u0003K\u00119\u0010\u0003\u0005\u0002$\nE\b\u0019AAS\u0011\u001d\u0011Y\u0010\u0001C\u0001\u00033\u000bAa\u001d;pa\"A!q \u0001\u0005\u0002\u0011\u0019\t!A\u0006sK\u0012\f7\r^#wK:$H\u0003BAx\u0007\u0007A\u0001\"a)\u0003~\u0002\u0007\u0011q\u001e\u0005\n\u0007\u000f\u0001\u0011\u0013!C\u0005\u0007\u0013\t!\u0003\\8h\u000bZ,g\u000e\u001e\u0013eK\u001a\fW\u000f\u001c;%eU\u001111\u0002\u0016\u0004'\u000e51FAB\b!\u0011\u0019\tba\u0007\u000e\u0005\rM!\u0002BB\u000b\u0007/\t\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\re1$\u0001\u0006b]:|G/\u0019;j_:LAa!\b\u0004\u0014\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\b\u0011\r\u0005\"\u0001#\u0001\u0005\u0007G\tA#\u0012<f]RdunZ4j]\u001ed\u0015n\u001d;f]\u0016\u0014\bc\u0001\u0007\u0004&\u00199\u0011A\u0001E\u0001\t\r\u001d2#BB\u0013\u0007Sy\u0001c\u0001\u000e\u0004,%\u00191QF\u000e\u0003\r\u0005s\u0017PU3g\u0011\u001d\u00115Q\u0005C\u0001\u0007c!\"aa\t\t\u0015\rU2Q\u0005b\u0001\n\u0003\u00199$A\u0006J\u001d~\u0003&kT$S\u000bN\u001bVCAB\u001d!\u0011\u0019Yd!\u0011\u000e\u0005\ru\"bAB [\u0005!A.\u00198h\u0013\r\u00013Q\b\u0005\n\u0007\u000b\u001a)\u0003)A\u0005\u0007s\tA\"\u0013(`!J{uIU#T'\u0002B!b!\u0013\u0004&\t\u0007I\u0011AB\u001c\u0003=!UIR!V\u0019R{FjT$`\t&\u0013\u0006\"CB'\u0007K\u0001\u000b\u0011BB\u001d\u0003A!UIR!V\u0019R{FjT$`\t&\u0013\u0006\u0005\u0003\u0006\u0004R\r\u0015\"\u0019!C\u0005\u0007'\nA\u0003T(H?\u001aKE*R0Q\u000bJk\u0015jU*J\u001f:\u001bVCAB+!\u0011\u00199f!\u0018\u000e\u0005\re#bAB.c\u0006Q\u0001/\u001a:nSN\u001c\u0018n\u001c8\n\t\r}3\u0011\f\u0002\r\rN\u0004VM]7jgNLwN\u001c\u0005\n\u0007G\u001a)\u0003)A\u0005\u0007+\nQ\u0003T(H?\u001aKE*R0Q\u000bJk\u0015jU*J\u001f:\u001b\u0006\u0005\u0003\u0006\u0004h\r\u0015\"\u0019!C\u0005\u0007S\n\u0001bY8eK\u000el\u0015\r]\u000b\u0003\u0007W\u0002b!!\u0017\u0004naY\u0018\u0002BB8\u00037\u0012q\u0001S1tQ6\u000b\u0007\u000fC\u0005\u0004t\r\u0015\u0002\u0015!\u0003\u0004l\u0005I1m\u001c3fG6\u000b\u0007\u000f\t\u0005\t\u0007o\u001a)\u0003\"\u0001\u0004z\u0005a\u0011N\\5u\u000bZ,g\u000e\u001e'pORA\u0011QEB>\u0007\u000b\u001b9\t\u0003\u0005\u0004~\rU\u0004\u0019AB@\u0003%awnZ*ue\u0016\fW\u000e\u0005\u0003\u0002>\r\u0005\u0015\u0002BBB\u0003\u007f\u0011AbT;uaV$8\u000b\u001e:fC6Da!YB;\u0001\u0004\u0019\u0006\u0002CA*\u0007k\u0002\r!a\u0016\t\u0011\r-5Q\u0005C\u0001\u0007\u001b\u000b!bZ3u\u0019><\u0007+\u0019;i)%A2qRBI\u0007'\u001b)\n\u0003\u0004)\u0007\u0013\u0003\r!\u000b\u0005\u0007-\r%\u0005\u0019\u0001\r\t\r\r\u001aI\t1\u0001%\u0011%\tIa!#\u0011\u0002\u0003\u0007A\u0005\u0003\u0005\u0004\u001a\u000e\u0015B\u0011BBN\u0003!\u0019\u0018M\\5uSj,Gc\u0001\r\u0004\u001e\"91qTBL\u0001\u0004A\u0012aA:ue\"A11UB\u0013\t\u0003\u0019)+\u0001\u0007pa\u0016tWI^3oi2{w\r\u0006\u0004\u0004(\u000e56q\u0017\t\u0005\u0003{\u0019I+\u0003\u0003\u0004,\u0006}\"aC%oaV$8\u000b\u001e:fC6D\u0001ba,\u0004\"\u0002\u00071\u0011W\u0001\u0004Y><\u0007c\u00019\u00044&\u00191QW9\u0003\tA\u000bG\u000f\u001b\u0005\u0007e\u000e\u0005\u0006\u0019A8\t\u0011\rm6Q\u0005C\u0001\u0007{\u000b\u0011bY8eK\u000et\u0015-\\3\u0015\u0007\u0011\u001ay\f\u0003\u0005\u00040\u000ee\u0006\u0019ABY\u0011)\u0019\u0019m!\n\u0012\u0002\u0013\u00051QY\u0001\u0015O\u0016$Hj\\4QCRDG\u0005Z3gCVdG\u000f\n\u001b\u0016\u0005\r\u001d'f\u0001\u0013\u0004\u000e\u0001"
)
public class EventLoggingListener extends SparkListener implements Logging {
   private final URI logBaseDir;
   public final SparkConf org$apache$spark$scheduler$EventLoggingListener$$sparkConf;
   private final Configuration hadoopConf;
   private final boolean shouldCompress;
   private final boolean shouldOverwrite;
   private final boolean shouldLogBlockUpdates;
   private final boolean testing;
   private final int outputBufferSize;
   private final FileSystem fileSystem;
   private final Option<CompressionCodec> compressionCodec;
   private final Option<String> compressionCodecName;
   private Option<FSDataOutputStream> hadoopDataStream;
   private Option<PrintWriter> writer;
   private final ArrayBuffer<JsonAST.JValue> loggedEvents;
   private final String logPath;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<String> getLogPath$default$4() {
      return EventLoggingListener$.MODULE$.getLogPath$default$4();
   }

   public static Option<String> codecName(Path var0) {
      return EventLoggingListener$.MODULE$.codecName(var0);
   }

   public static InputStream openEventLog(Path var0, FileSystem var1) {
      return EventLoggingListener$.MODULE$.openEventLog(var0, var1);
   }

   public static String getLogPath(URI var0, String var1, Option<String> var2, Option<String> var3) {
      return EventLoggingListener$.MODULE$.getLogPath(var0, var1, var2, var3);
   }

   public static void initEventLog(OutputStream var0, boolean var1, ArrayBuffer<JsonAST.JValue> var2) {
      EventLoggingListener$.MODULE$.initEventLog(var0, var1, var2);
   }

   public static String DEFAULT_LOG_DIR() {
      return EventLoggingListener$.MODULE$.DEFAULT_LOG_DIR();
   }

   public static String IN_PROGRESS() {
      return EventLoggingListener$.MODULE$.IN_PROGRESS();
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

   private boolean shouldCompress() {
      return this.shouldCompress;
   }

   private boolean shouldOverwrite() {
      return this.shouldOverwrite;
   }

   private boolean shouldLogBlockUpdates() {
      return this.shouldLogBlockUpdates;
   }

   private boolean testing() {
      return this.testing;
   }

   private int outputBufferSize() {
      return this.outputBufferSize;
   }

   private FileSystem fileSystem() {
      return this.fileSystem;
   }

   private Option<CompressionCodec> compressionCodec() {
      return this.compressionCodec;
   }

   private Option<String> compressionCodecName() {
      return this.compressionCodecName;
   }

   private Option<FSDataOutputStream> hadoopDataStream() {
      return this.hadoopDataStream;
   }

   private void hadoopDataStream_$eq(Option<FSDataOutputStream> x$1) {
      this.hadoopDataStream = x$1;
   }

   private Option<PrintWriter> writer() {
      return this.writer;
   }

   private void writer_$eq(Option<PrintWriter> x$1) {
      this.writer = x$1;
   }

   public ArrayBuffer<JsonAST.JValue> loggedEvents() {
      return this.loggedEvents;
   }

   public String logPath() {
      return this.logPath;
   }

   public void start() {
      if (!this.fileSystem().getFileStatus(new Path(this.logBaseDir)).isDirectory()) {
         throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Log directory ", " is not a directory."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.logBaseDir})));
      } else {
         Path path;
         URI uri;
         boolean var10000;
         label63: {
            String workingPath = (new StringBuilder()).append((Object)this.logPath()).append((Object)EventLoggingListener$.MODULE$.IN_PROGRESS()).toString();
            path = new Path(workingPath);
            uri = path.toUri();
            String defaultFs = FileSystem.getDefaultUri(this.hadoopConf).getScheme();
            if (defaultFs != null) {
               label62: {
                  String var6 = "file";
                  if (defaultFs == null) {
                     if (var6 == null) {
                        break label62;
                     }
                  } else if (defaultFs.equals(var6)) {
                     break label62;
                  }

                  var10000 = false;
                  break label63;
               }
            }

            var10000 = true;
         }

         boolean isDefaultLocal = var10000;
         if (this.shouldOverwrite() && this.fileSystem().delete(path, true)) {
            this.logWarning(new Serializable(this, path) {
               public static final long serialVersionUID = 0L;
               private final Path path$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Event log ", " already exists. Overwriting..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.path$1}));
               }

               public {
                  this.path$1 = path$1;
               }
            });
         }

         label65: {
            if (!isDefaultLocal || uri.getScheme() != null) {
               label64: {
                  String var13 = uri.getScheme();
                  String var8 = "file";
                  if (var13 == null) {
                     if (var8 == null) {
                        break label64;
                     }
                  } else if (var13.equals(var8)) {
                     break label64;
                  }

                  this.hadoopDataStream_$eq(new Some(this.fileSystem().create(path)));
                  var14 = (OutputStream)this.hadoopDataStream().get();
                  break label65;
               }
            }

            var14 = new FileOutputStream(uri.getPath());
         }

         OutputStream dstream = (OutputStream)var14;

         try {
            OutputStream cstream = (OutputStream)this.compressionCodec().map(new Serializable(this, dstream) {
               public static final long serialVersionUID = 0L;
               private final OutputStream dstream$1;

               public final OutputStream apply(CompressionCodec x$1) {
                  return x$1.compressedOutputStream(this.dstream$1);
               }

               public {
                  this.dstream$1 = dstream$1;
               }
            }).getOrElse(new Serializable(this, dstream) {
               public static final long serialVersionUID = 0L;
               private final OutputStream dstream$1;

               public final OutputStream apply() {
                  return this.dstream$1;
               }

               public {
                  this.dstream$1 = dstream$1;
               }
            });
            BufferedOutputStream bstream = new BufferedOutputStream(cstream, this.outputBufferSize());
            EventLoggingListener$.MODULE$.initEventLog(bstream, this.testing(), this.loggedEvents());
            this.fileSystem().setPermission(path, EventLoggingListener$.MODULE$.org$apache$spark$scheduler$EventLoggingListener$$LOG_FILE_PERMISSIONS());
            this.writer_$eq(new Some(new PrintWriter(bstream)));
            this.logInfo(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final EventLoggingListener $outer;

               public final String apply() {
                  return (new StringOps(Predef$.MODULE$.augmentString("Logging events to %s"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.logPath()}));
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            });
         } catch (Exception var12) {
            dstream.close();
            throw var12;
         }
      }
   }

   private void logEvent(SparkListenerEvent event, boolean flushLogger) {
      JsonAST.JValue eventJson = JsonProtocol$.MODULE$.sparkEventToJson(event);
      this.writer().foreach(new Serializable(this, eventJson) {
         public static final long serialVersionUID = 0L;
         private final JsonAST.JValue eventJson$1;

         public final void apply(PrintWriter x$2) {
            JsonMethods var10001 = .MODULE$;
            JsonAST.JValue x$7 = this.eventJson$1;
            Formats x$8 = .MODULE$.render$default$2(x$7);
            x$2.println(var10001.compact(.MODULE$.render(x$7, x$8)));
         }

         public {
            this.eventJson$1 = eventJson$1;
         }
      });
      if (flushLogger) {
         this.writer().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(PrintWriter x$3) {
               x$3.flush();
            }
         });
         this.hadoopDataStream().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(FSDataOutputStream ds) {
               OutputStream var2 = ds.getWrappedStream();
               if (var2 instanceof DFSOutputStream) {
                  DFSOutputStream var3 = (DFSOutputStream)var2;
                  var3.hsync(EnumSet.of(SyncFlag.UPDATE_LENGTH));
                  BoxedUnit var4 = BoxedUnit.UNIT;
               } else {
                  ds.hflush();
                  BoxedUnit var5 = BoxedUnit.UNIT;
               }

            }
         });
      }

      if (this.testing()) {
         this.loggedEvents().$plus$eq(eventJson);
      }

   }

   private boolean logEvent$default$2() {
      return false;
   }

   public void onStageSubmitted(SparkListenerStageSubmitted event) {
      this.logEvent(event, this.logEvent$default$2());
   }

   public void onTaskStart(SparkListenerTaskStart event) {
      this.logEvent(event, this.logEvent$default$2());
   }

   public void onTaskGettingResult(SparkListenerTaskGettingResult event) {
      this.logEvent(event, this.logEvent$default$2());
   }

   public void onTaskEnd(SparkListenerTaskEnd event) {
      this.logEvent(event, this.logEvent$default$2());
   }

   public void onEnvironmentUpdate(SparkListenerEnvironmentUpdate event) {
      this.logEvent(this.redactEvent(event), this.logEvent$default$2());
   }

   public void onStageCompleted(SparkListenerStageCompleted event) {
      this.logEvent(event, true);
   }

   public void onJobStart(SparkListenerJobStart event) {
      this.logEvent(event, true);
   }

   public void onJobEnd(SparkListenerJobEnd event) {
      this.logEvent(event, true);
   }

   public void onBlockManagerAdded(SparkListenerBlockManagerAdded event) {
      this.logEvent(event, true);
   }

   public void onBlockManagerRemoved(SparkListenerBlockManagerRemoved event) {
      this.logEvent(event, true);
   }

   public void onUnpersistRDD(SparkListenerUnpersistRDD event) {
      this.logEvent(event, true);
   }

   public void onApplicationStart(SparkListenerApplicationStart event) {
      this.logEvent(event, true);
   }

   public void onApplicationEnd(SparkListenerApplicationEnd event) {
      this.logEvent(event, true);
   }

   public void onExecutorAdded(SparkListenerExecutorAdded event) {
      this.logEvent(event, true);
   }

   public void onExecutorRemoved(SparkListenerExecutorRemoved event) {
      this.logEvent(event, true);
   }

   public void onExecutorBlacklisted(SparkListenerExecutorBlacklisted event) {
      this.logEvent(event, true);
   }

   public void onExecutorBlacklistedForStage(SparkListenerExecutorBlacklistedForStage event) {
      this.logEvent(event, true);
   }

   public void onNodeBlacklistedForStage(SparkListenerNodeBlacklistedForStage event) {
      this.logEvent(event, true);
   }

   public void onExecutorUnblacklisted(SparkListenerExecutorUnblacklisted event) {
      this.logEvent(event, true);
   }

   public void onNodeBlacklisted(SparkListenerNodeBlacklisted event) {
      this.logEvent(event, true);
   }

   public void onNodeUnblacklisted(SparkListenerNodeUnblacklisted event) {
      this.logEvent(event, true);
   }

   public void onBlockUpdated(SparkListenerBlockUpdated event) {
      if (this.shouldLogBlockUpdates()) {
         this.logEvent(event, true);
      }

   }

   public void onExecutorMetricsUpdate(SparkListenerExecutorMetricsUpdate event) {
   }

   public void onOtherEvent(SparkListenerEvent event) {
      if (event.logEvent()) {
         this.logEvent(event, true);
      }

   }

   public void stop() {
      this.writer().foreach(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final void apply(PrintWriter x$4) {
            x$4.close();
         }
      });
      Path target = new Path(this.logPath());
      if (this.fileSystem().exists(target)) {
         if (!this.shouldOverwrite()) {
            throw new IOException((new StringOps(Predef$.MODULE$.augmentString("Target log file already exists (%s)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.logPath()})));
         }

         this.logWarning(new Serializable(this, target) {
            public static final long serialVersionUID = 0L;
            private final Path target$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Event log ", " already exists. Overwriting..."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.target$1}));
            }

            public {
               this.target$1 = target$1;
            }
         });
         if (!this.fileSystem().delete(target, true)) {
            this.logWarning(new Serializable(this, target) {
               public static final long serialVersionUID = 0L;
               private final Path target$1;

               public final String apply() {
                  return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Error deleting ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.target$1}));
               }

               public {
                  this.target$1 = target$1;
               }
            });
         }
      }

      this.fileSystem().rename(new Path((new StringBuilder()).append((Object)this.logPath()).append((Object)EventLoggingListener$.MODULE$.IN_PROGRESS()).toString()), target);

      try {
         this.fileSystem().setTimes(target, System.currentTimeMillis(), -1L);
      } catch (Exception var3) {
         this.logDebug(new Serializable(this, target) {
            public static final long serialVersionUID = 0L;
            private final Path target$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"failed to set time of ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.target$1}));
            }

            public {
               this.target$1 = target$1;
            }
         }, var3);
      }

   }

   public SparkListenerEnvironmentUpdate redactEvent(SparkListenerEnvironmentUpdate event) {
      Map redactedProps = (Map)event.environmentDetails().map(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final EventLoggingListener $outer;

         public final Tuple2<String, Seq<Tuple2<String, String>>> apply(Tuple2<String, Seq<Tuple2<String, String>>> x0$1) {
            if (x0$1 != null) {
               String name = (String)x0$1._1();
               Seq props = (Seq)x0$1._2();
               Tuple2 var5 = Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc(name), Utils$.MODULE$.redact(this.$outer.org$apache$spark$scheduler$EventLoggingListener$$sparkConf, props));
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
            }
         }
      }, Map$.MODULE$.canBuildFrom());
      return new SparkListenerEnvironmentUpdate(redactedProps);
   }

   public EventLoggingListener(String appId, Option<String> appAttemptId, URI logBaseDir, SparkConf sparkConf, Configuration hadoopConf) {
      this.logBaseDir = logBaseDir;
      this.org$apache$spark$scheduler$EventLoggingListener$$sparkConf = sparkConf;
      this.hadoopConf = hadoopConf;
      Logging$class.$init$(this);
      this.shouldCompress = BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.EVENT_LOG_COMPRESS()));
      this.shouldOverwrite = BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.EVENT_LOG_OVERWRITE()));
      this.shouldLogBlockUpdates = BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.EVENT_LOG_BLOCK_UPDATES()));
      this.testing = BoxesRunTime.unboxToBoolean(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.EVENT_LOG_TESTING()));
      this.outputBufferSize = (int)BoxesRunTime.unboxToLong(sparkConf.get(org.apache.spark.internal.config.package$.MODULE$.EVENT_LOG_OUTPUT_BUFFER_SIZE()));
      this.fileSystem = Utils$.MODULE$.getHadoopFileSystem(logBaseDir, hadoopConf);
      this.compressionCodec = (Option<CompressionCodec>)(this.shouldCompress() ? new Some(CompressionCodec$.MODULE$.createCodec(sparkConf)) : None$.MODULE$);
      this.compressionCodecName = this.compressionCodec().<String>map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(CompressionCodec c) {
            return CompressionCodec$.MODULE$.getShortName(c.getClass().getName());
         }
      });
      this.hadoopDataStream = None$.MODULE$;
      this.writer = None$.MODULE$;
      this.loggedEvents = new ArrayBuffer<JsonAST.JValue>();
      this.logPath = EventLoggingListener$.MODULE$.getLogPath(logBaseDir, appId, appAttemptId, this.compressionCodecName());
   }

   public EventLoggingListener(String appId, Option<String> appAttemptId, URI logBaseDir, SparkConf sparkConf) {
      this(appId, appAttemptId, logBaseDir, sparkConf, SparkHadoopUtil$.MODULE$.get().newConfiguration(sparkConf));
   }
}
