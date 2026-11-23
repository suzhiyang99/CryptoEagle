package org.apache.spark.api.java;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.Accumulable;
import org.apache.spark.AccumulableParam;
import org.apache.spark.Accumulator;
import org.apache.spark.AccumulatorParam;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkEnv;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.input.PortableDataStream;
import org.apache.spark.rdd.EmptyRDD;
import org.apache.spark.rdd.HadoopRDD;
import org.apache.spark.rdd.NewHadoopRDD;
import org.apache.spark.rdd.RDD;
import scala.Predef$;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.TraversableLike;
import scala.collection.mutable.Buffer$;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0019\u001db\u0001B\u0001\u0003\u00015\u0011\u0001CS1wCN\u0003\u0018M]6D_:$X\r\u001f;\u000b\u0005\r!\u0011\u0001\u00026bm\u0006T!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u0005)1\u000f]1sW*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\b\u0013!\ty\u0001#D\u0001\u0003\u0013\t\t\"AA\u0011KCZ\f7\u000b]1sW\u000e{g\u000e^3yiZ\u000b'/\u0019:hg^{'o[1s_VtG\r\u0005\u0002\u0014/5\tAC\u0003\u0002\u0016-\u0005\u0011\u0011n\u001c\u0006\u0002\u0007%\u0011\u0001\u0004\u0006\u0002\n\u00072|7/Z1cY\u0016D\u0001B\u0007\u0001\u0003\u0006\u0004%\taG\u0001\u0003g\u000e,\u0012\u0001\b\t\u0003;yi\u0011AB\u0005\u0003?\u0019\u0011Ab\u00159be.\u001cuN\u001c;fqRD\u0001\"\t\u0001\u0003\u0002\u0003\u0006I\u0001H\u0001\u0004g\u000e\u0004\u0003\"B\u0012\u0001\t\u0003!\u0013A\u0002\u001fj]&$h\b\u0006\u0002&MA\u0011q\u0002\u0001\u0005\u00065\t\u0002\r\u0001\b\u0005\u0006G\u0001!\t\u0001\u000b\u000b\u0002K!)1\u0005\u0001C\u0001UQ\u0011Qe\u000b\u0005\u0006Y%\u0002\r!L\u0001\u0005G>tg\r\u0005\u0002\u001e]%\u0011qF\u0002\u0002\n'B\f'o[\"p]\u001aDQa\t\u0001\u0005\u0002E\"2!\n\u001a?\u0011\u0015\u0019\u0004\u00071\u00015\u0003\u0019i\u0017m\u001d;feB\u0011Qg\u000f\b\u0003mej\u0011a\u000e\u0006\u0002q\u0005)1oY1mC&\u0011!hN\u0001\u0007!J,G-\u001a4\n\u0005qj$AB*ue&twM\u0003\u0002;o!)q\b\ra\u0001i\u00059\u0011\r\u001d9OC6,\u0007\"B\u0012\u0001\t\u0003\tE\u0003B\u0013C\u0007\u0012CQa\r!A\u0002QBQa\u0010!A\u0002QBQ\u0001\f!A\u00025BQa\t\u0001\u0005\u0002\u0019#R!J$I\u0013.CQaM#A\u0002QBQaP#A\u0002QBQAS#A\u0002Q\n\u0011b\u001d9be.Du.\\3\t\u000b1+\u0005\u0019\u0001\u001b\u0002\u000f)\f'OR5mK\")1\u0005\u0001C\u0001\u001dR)Qe\u0014)R%\")1'\u0014a\u0001i!)q(\u0014a\u0001i!)!*\u0014a\u0001i!)1+\u0014a\u0001)\u0006!!.\u0019:t!\r1T\u000bN\u0005\u0003-^\u0012Q!\u0011:sCfDQa\t\u0001\u0005\u0002a#b!J-[7rk\u0006\"B\u001aX\u0001\u0004!\u0004\"B X\u0001\u0004!\u0004\"\u0002&X\u0001\u0004!\u0004\"B*X\u0001\u0004!\u0006\"\u00020X\u0001\u0004y\u0016aC3om&\u0014xN\\7f]R\u0004B\u0001Y25i5\t\u0011M\u0003\u0002c-\u0005!Q\u000f^5m\u0013\t!\u0017MA\u0002NCBD\u0001B\u001a\u0001C\u0002\u0013\u0005aaZ\u0001\u0004K:4X#\u00015\u0011\u0005uI\u0017B\u00016\u0007\u0005!\u0019\u0006/\u0019:l\u000b:4\bB\u00027\u0001A\u0003%\u0001.\u0001\u0003f]Z\u0004\u0003\"\u00028\u0001\t\u0003y\u0017!D:uCR,8\u000f\u0016:bG.,'/F\u0001q!\ty\u0011/\u0003\u0002s\u0005\t1\"*\u0019<b'B\f'o[*uCR,8\u000f\u0016:bG.,'\u000fC\u0003u\u0001\u0011\u0005Q/A\u0004jg2{7-\u00197\u0016\u0003Y\u0004\"a\u001e>\u000e\u0003aT!!\u001f\f\u0002\t1\fgnZ\u0005\u0003wb\u0014qAQ8pY\u0016\fg\u000eC\u0003~\u0001\u0011\u0005a0A\u0005ta\u0006\u00148.V:feV\tA\u0007C\u00034\u0001\u0011\u0005a\u0010C\u0003@\u0001\u0011\u0005a\u0010\u0003\u0004T\u0001\u0011\u0005\u0011QA\u000b\u0003\u0003\u000f\u0001B\u0001YA\u0005i%\u0019\u00111B1\u0003\t1K7\u000f\u001e\u0005\b\u0003\u001f\u0001A\u0011AA\t\u0003%\u0019H/\u0019:u)&lW-\u0006\u0002\u0002\u0014A\u0019q/!\u0006\n\u0007\u0005]\u0001P\u0001\u0003M_:<\u0007BBA\u000e\u0001\u0011\u0005a0A\u0004wKJ\u001c\u0018n\u001c8\t\u000f\u0005}\u0001\u0001\"\u0001\u0002\"\u0005\u0011B-\u001a4bk2$\b+\u0019:bY2,G.[:n+\t\t\u0019\u0003E\u0002x\u0003KI1!a\ny\u0005\u001dIe\u000e^3hKJDq!a\u000b\u0001\t\u0003\t\t#\u0001\u000beK\u001a\fW\u000f\u001c;NS:\u0004\u0016M\u001d;ji&|gn\u001d\u0005\b\u0003_\u0001A\u0011AA\u0019\u0003-\u0001\u0018M]1mY\u0016d\u0017N_3\u0016\t\u0005M\u0012q\b\u000b\u0007\u0003k\t\t&a\u0016\u0011\u000b=\t9$a\u000f\n\u0007\u0005e\"AA\u0004KCZ\f'\u000b\u0012#\u0011\t\u0005u\u0012q\b\u0007\u0001\t!\t\t%!\fC\u0002\u0005\r#!\u0001+\u0012\t\u0005\u0015\u00131\n\t\u0004m\u0005\u001d\u0013bAA%o\t9aj\u001c;iS:<\u0007c\u0001\u001c\u0002N%\u0019\u0011qJ\u001c\u0003\u0007\u0005s\u0017\u0010\u0003\u0005\u0002T\u00055\u0002\u0019AA+\u0003\u0011a\u0017n\u001d;\u0011\u000b\u0001\fI!a\u000f\t\u0011\u0005e\u0013Q\u0006a\u0001\u00037\n\u0011B\\;n'2L7-Z:\u0011\u0007Y\ni&C\u0002\u0002`]\u00121!\u00138u\u0011\u001d\t\u0019\u0007\u0001C\u0001\u0003K\n\u0001\"Z7qif\u0014F\tR\u000b\u0005\u0003O\ni'\u0006\u0002\u0002jA)q\"a\u000e\u0002lA!\u0011QHA7\t!\t\t%!\u0019C\u0002\u0005\r\u0003bBA\u0018\u0001\u0011\u0005\u0011\u0011O\u000b\u0005\u0003g\nI\b\u0006\u0003\u0002v\u0005m\u0004#B\b\u00028\u0005]\u0004\u0003BA\u001f\u0003s\"\u0001\"!\u0011\u0002p\t\u0007\u00111\t\u0005\t\u0003'\ny\u00071\u0001\u0002~A)\u0001-!\u0003\u0002x!9\u0011\u0011\u0011\u0001\u0005\u0002\u0005\r\u0015\u0001\u00059be\u0006dG.\u001a7ju\u0016\u0004\u0016-\u001b:t+\u0019\t))a$\u0002\u0016R1\u0011qQAM\u0003G\u0003raDAE\u0003\u001b\u000b\u0019*C\u0002\u0002\f\n\u00111BS1wCB\u000b\u0017N\u001d*E\tB!\u0011QHAH\t!\t\t*a C\u0002\u0005\r#!A&\u0011\t\u0005u\u0012Q\u0013\u0003\t\u0003/\u000byH1\u0001\u0002D\t\ta\u000b\u0003\u0005\u0002T\u0005}\u0004\u0019AAN!\u0015\u0001\u0017\u0011BAO!\u001d1\u0014qTAG\u0003'K1!!)8\u0005\u0019!V\u000f\u001d7fe!A\u0011\u0011LA@\u0001\u0004\tY\u0006C\u0004\u0002\u0002\u0002!\t!a*\u0016\r\u0005%\u0016qVAZ)\u0011\tY+!.\u0011\u000f=\tI)!,\u00022B!\u0011QHAX\t!\t\t*!*C\u0002\u0005\r\u0003\u0003BA\u001f\u0003g#\u0001\"a&\u0002&\n\u0007\u00111\t\u0005\t\u0003'\n)\u000b1\u0001\u00028B)\u0001-!\u0003\u0002:B9a'a(\u0002.\u0006E\u0006bBA_\u0001\u0011\u0005\u0011qX\u0001\u0013a\u0006\u0014\u0018\r\u001c7fY&TX\rR8vE2,7\u000f\u0006\u0004\u0002B\u0006\u001d\u0017\u0011\u001b\t\u0004\u001f\u0005\r\u0017bAAc\u0005\ti!*\u0019<b\t>,(\r\\3S\t\u0012C\u0001\"a\u0015\u0002<\u0002\u0007\u0011\u0011\u001a\t\u0006A\u0006%\u00111\u001a\t\u0004o\u00065\u0017bAAhq\n1Ai\\;cY\u0016D\u0001\"!\u0017\u0002<\u0002\u0007\u00111\f\u0005\b\u0003{\u0003A\u0011AAk)\u0011\t\t-a6\t\u0011\u0005M\u00131\u001ba\u0001\u0003\u0013Dq!a7\u0001\t\u0003\ti.\u0001\u0005uKb$h)\u001b7f)\u0011\ty.!9\u0011\t=\t9\u0004\u000e\u0005\b\u0003G\fI\u000e1\u00015\u0003\u0011\u0001\u0018\r\u001e5\t\u000f\u0005m\u0007\u0001\"\u0001\u0002hR1\u0011q\\Au\u0003WDq!a9\u0002f\u0002\u0007A\u0007\u0003\u0005\u0002n\u0006\u0015\b\u0019AA.\u00035i\u0017N\u001c)beRLG/[8og\"9\u0011\u0011\u001f\u0001\u0005\u0002\u0005M\u0018AD<i_2,G+\u001a=u\r&dWm\u001d\u000b\u0007\u0003k\f90!?\u0011\u000b=\tI\t\u000e\u001b\t\u000f\u0005\r\u0018q\u001ea\u0001i!A\u0011Q^Ax\u0001\u0004\tY\u0006C\u0004\u0002r\u0002!\t!!@\u0015\t\u0005U\u0018q \u0005\b\u0003G\fY\u00101\u00015\u0011\u001d\u0011\u0019\u0001\u0001C\u0001\u0005\u000b\t1BY5oCJLh)\u001b7fgR1!q\u0001B\u000b\u0005/\u0001baDAEi\t%\u0001\u0003\u0002B\u0006\u0005#i!A!\u0004\u000b\u0007\t=a!A\u0003j]B,H/\u0003\u0003\u0003\u0014\t5!A\u0005)peR\f'\r\\3ECR\f7\u000b\u001e:fC6Dq!a9\u0003\u0002\u0001\u0007A\u0007\u0003\u0005\u0002n\n\u0005\u0001\u0019AA.\u0011\u001d\u0011\u0019\u0001\u0001C\u0001\u00057!BAa\u0002\u0003\u001e!9\u00111\u001dB\r\u0001\u0004!\u0004b\u0002B\u0011\u0001\u0011\u0005!1E\u0001\u000eE&t\u0017M]=SK\u000e|'\u000fZ:\u0015\r\t\u0015\"q\u0006B\u0019!\u0015y\u0011q\u0007B\u0014!\u00111TK!\u000b\u0011\u0007Y\u0012Y#C\u0002\u0003.]\u0012AAQ=uK\"9\u00111\u001dB\u0010\u0001\u0004!\u0004\u0002\u0003B\u001a\u0005?\u0001\r!a\u0017\u0002\u0019I,7m\u001c:e\u0019\u0016tw\r\u001e5\t\u000f\t]\u0002\u0001\"\u0001\u0003:\u0005a1/Z9vK:\u001cWMR5mKV1!1\bB!\u0005\u000b\"\"B!\u0010\u0003H\t%#1\u000bB-!\u001dy\u0011\u0011\u0012B \u0005\u0007\u0002B!!\u0010\u0003B\u0011A\u0011\u0011\u0013B\u001b\u0005\u0004\t\u0019\u0005\u0005\u0003\u0002>\t\u0015C\u0001CAL\u0005k\u0011\r!a\u0011\t\u000f\u0005\r(Q\u0007a\u0001i!A!1\nB\u001b\u0001\u0004\u0011i%\u0001\u0005lKf\u001cE.Y:t!\u0015)$q\nB \u0013\r\u0011\t&\u0010\u0002\u0006\u00072\f7o\u001d\u0005\t\u0005+\u0012)\u00041\u0001\u0003X\u0005Qa/\u00197vK\u000ec\u0017m]:\u0011\u000bU\u0012yEa\u0011\t\u0011\u00055(Q\u0007a\u0001\u00037BqAa\u000e\u0001\t\u0003\u0011i&\u0006\u0004\u0003`\t\u0015$\u0011\u000e\u000b\t\u0005C\u0012YG!\u001c\u0003rA9q\"!#\u0003d\t\u001d\u0004\u0003BA\u001f\u0005K\"\u0001\"!%\u0003\\\t\u0007\u00111\t\t\u0005\u0003{\u0011I\u0007\u0002\u0005\u0002\u0018\nm#\u0019AA\"\u0011\u001d\t\u0019Oa\u0017A\u0002QB\u0001Ba\u0013\u0003\\\u0001\u0007!q\u000e\t\u0006k\t=#1\r\u0005\t\u0005+\u0012Y\u00061\u0001\u0003tA)QGa\u0014\u0003h!9!q\u000f\u0001\u0005\u0002\te\u0014AC8cU\u0016\u001cGOR5mKV!!1\u0010BA)\u0019\u0011iHa!\u0003\u0006B)q\"a\u000e\u0003\u0000A!\u0011Q\bBA\t!\t\tE!\u001eC\u0002\u0005\r\u0003bBAr\u0005k\u0002\r\u0001\u000e\u0005\t\u0003[\u0014)\b1\u0001\u0002\\!9!q\u000f\u0001\u0005\u0002\t%U\u0003\u0002BF\u0005##BA!$\u0003\u0014B)q\"a\u000e\u0003\u0010B!\u0011Q\bBI\t!\t\tEa\"C\u0002\u0005\r\u0003bBAr\u0005\u000f\u0003\r\u0001\u000e\u0005\b\u0005/\u0003A\u0011\u0001BM\u0003%A\u0017\rZ8paJ#E)\u0006\u0005\u0003\u001c\n\u0005&Q\u0015Ba)1\u0011iJa*\u0003:\n5'\u0011\u001bBk!\u001dy\u0011\u0011\u0012BP\u0005G\u0003B!!\u0010\u0003\"\u0012A\u0011\u0011\u0013BK\u0005\u0004\t\u0019\u0005\u0005\u0003\u0002>\t\u0015F\u0001CAL\u0005+\u0013\r!a\u0011\t\u000f1\u0012)\n1\u0001\u0003*B!!1\u0016B[\u001b\t\u0011iK\u0003\u0003\u00030\nE\u0016AB7baJ,GMC\u0002\u00034\"\ta\u0001[1e_>\u0004\u0018\u0002\u0002B\\\u0005[\u0013qAS8c\u0007>tg\r\u0003\u0005\u0003<\nU\u0005\u0019\u0001B_\u0003AIg\u000e];u\r>\u0014X.\u0019;DY\u0006\u001c8\u000fE\u00036\u0005\u001f\u0012y\f\u0005\u0003\u0002>\t\u0005G\u0001\u0003Bb\u0005+\u0013\rA!2\u0003\u0003\u0019\u000bB!!\u0012\u0003HBA!1\u0016Be\u0005?\u0013\u0019+\u0003\u0003\u0003L\n5&aC%oaV$hi\u001c:nCRD\u0001Ba\u0013\u0003\u0016\u0002\u0007!q\u001a\t\u0006k\t=#q\u0014\u0005\t\u0005+\u0012)\n1\u0001\u0003TB)QGa\u0014\u0003$\"A\u0011Q\u001eBK\u0001\u0004\tY\u0006C\u0004\u0003\u0018\u0002!\tA!7\u0016\u0011\tm'\u0011\u001dBs\u0005_$\"B!8\u0003h\n%(Q\u001fB}!\u001dy\u0011\u0011\u0012Bp\u0005G\u0004B!!\u0010\u0003b\u0012A\u0011\u0011\u0013Bl\u0005\u0004\t\u0019\u0005\u0005\u0003\u0002>\t\u0015H\u0001CAL\u0005/\u0014\r!a\u0011\t\u000f1\u00129\u000e1\u0001\u0003*\"A!1\u0018Bl\u0001\u0004\u0011Y\u000fE\u00036\u0005\u001f\u0012i\u000f\u0005\u0003\u0002>\t=H\u0001\u0003Bb\u0005/\u0014\rA!=\u0012\t\u0005\u0015#1\u001f\t\t\u0005W\u0013IMa8\u0003d\"A!1\nBl\u0001\u0004\u00119\u0010E\u00036\u0005\u001f\u0012y\u000e\u0003\u0005\u0003V\t]\u0007\u0019\u0001B~!\u0015)$q\nBr\u0011\u001d\u0011y\u0010\u0001C\u0001\u0007\u0003\t!\u0002[1e_>\u0004h)\u001b7f+!\u0019\u0019a!\u0003\u0004\u000e\r]A\u0003DB\u0003\u0007\u001f\u0019\tb!\b\u0004\"\r\u0015\u0002cB\b\u0002\n\u000e\u001d11\u0002\t\u0005\u0003{\u0019I\u0001\u0002\u0005\u0002\u0012\nu(\u0019AA\"!\u0011\tid!\u0004\u0005\u0011\u0005]%Q b\u0001\u0003\u0007Bq!a9\u0003~\u0002\u0007A\u0007\u0003\u0005\u0003<\nu\b\u0019AB\n!\u0015)$qJB\u000b!\u0011\tida\u0006\u0005\u0011\t\r'Q b\u0001\u00073\tB!!\u0012\u0004\u001cAA!1\u0016Be\u0007\u000f\u0019Y\u0001\u0003\u0005\u0003L\tu\b\u0019AB\u0010!\u0015)$qJB\u0004\u0011!\u0011)F!@A\u0002\r\r\u0002#B\u001b\u0003P\r-\u0001\u0002CAw\u0005{\u0004\r!a\u0017\t\u000f\t}\b\u0001\"\u0001\u0004*UA11FB\u0019\u0007k\u0019y\u0004\u0006\u0006\u0004.\r]2\u0011HB#\u0007\u0013\u0002raDAE\u0007_\u0019\u0019\u0004\u0005\u0003\u0002>\rEB\u0001CAI\u0007O\u0011\r!a\u0011\u0011\t\u0005u2Q\u0007\u0003\t\u0003/\u001b9C1\u0001\u0002D!9\u00111]B\u0014\u0001\u0004!\u0004\u0002\u0003B^\u0007O\u0001\raa\u000f\u0011\u000bU\u0012ye!\u0010\u0011\t\u0005u2q\b\u0003\t\u0005\u0007\u001c9C1\u0001\u0004BE!\u0011QIB\"!!\u0011YK!3\u00040\rM\u0002\u0002\u0003B&\u0007O\u0001\raa\u0012\u0011\u000bU\u0012yea\f\t\u0011\tU3q\u0005a\u0001\u0007\u0017\u0002R!\u000eB(\u0007gAqaa\u0014\u0001\t\u0003\u0019\t&\u0001\toK^\f\u0005+\u0013%bI>|\u0007OR5mKVA11KB-\u0007;\u001aI\u0007\u0006\u0007\u0004V\r}3\u0011MB<\u0007{\u001a\u0019\tE\u0004\u0010\u0003\u0013\u001b9fa\u0017\u0011\t\u0005u2\u0011\f\u0003\t\u0003#\u001biE1\u0001\u0002DA!\u0011QHB/\t!\t9j!\u0014C\u0002\u0005\r\u0003bBAr\u0007\u001b\u0002\r\u0001\u000e\u0005\t\u0007G\u001ai\u00051\u0001\u0004f\u00051am\u00117bgN\u0004R!\u000eB(\u0007O\u0002B!!\u0010\u0004j\u0011A!1YB'\u0005\u0004\u0019Y'\u0005\u0003\u0002F\r5\u0004\u0003CB8\u0007k\u001a9fa\u0017\u000e\u0005\rE$\u0002BB:\u0005c\u000b\u0011\"\\1qe\u0016$WoY3\n\t\t-7\u0011\u000f\u0005\t\u0007s\u001ai\u00051\u0001\u0004|\u000511n\u00117bgN\u0004R!\u000eB(\u0007/B\u0001ba \u0004N\u0001\u00071\u0011Q\u0001\u0007m\u000ec\u0017m]:\u0011\u000bU\u0012yea\u0017\t\u000f1\u001ai\u00051\u0001\u0004\u0006B!1qQBF\u001b\t\u0019IIC\u0002-\u0005cKAa!$\u0004\n\ni1i\u001c8gS\u001e,(/\u0019;j_:Dqa!%\u0001\t\u0003\u0019\u0019*A\boK^\f\u0005+\u0013%bI>|\u0007O\u0015#E+!\u0019)ja'\u0004 \u000e%FCCBL\u0007C\u001b\u0019ka,\u00044B9q\"!#\u0004\u001a\u000eu\u0005\u0003BA\u001f\u00077#\u0001\"!%\u0004\u0010\n\u0007\u00111\t\t\u0005\u0003{\u0019y\n\u0002\u0005\u0002\u0018\u000e=%\u0019AA\"\u0011\u001da3q\u0012a\u0001\u0007\u000bC\u0001ba\u0019\u0004\u0010\u0002\u00071Q\u0015\t\u0006k\t=3q\u0015\t\u0005\u0003{\u0019I\u000b\u0002\u0005\u0003D\u000e=%\u0019ABV#\u0011\t)e!,\u0011\u0011\r=4QOBM\u0007;C\u0001b!\u001f\u0004\u0010\u0002\u00071\u0011\u0017\t\u0006k\t=3\u0011\u0014\u0005\t\u0007\u007f\u001ay\t1\u0001\u00046B)QGa\u0014\u0004\u001e\"91\u0011\u0018\u0001\u0005B\rm\u0016!B;oS>tW\u0003BB_\u0007\u0007$baa0\u0004F\u000e%\u0007#B\b\u00028\r\u0005\u0007\u0003BA\u001f\u0007\u0007$\u0001\"!\u0011\u00048\n\u0007\u00111\t\u0005\t\u0007\u000f\u001c9\f1\u0001\u0004@\u0006)a-\u001b:ti\"A11ZB\\\u0001\u0004\u0019i-\u0001\u0003sKN$\b#\u00021\u0002\n\r}\u0006bBB]\u0001\u0011\u00053\u0011[\u000b\u0007\u0007'\u001cIn!8\u0015\r\rU7q\\Bq!\u001dy\u0011\u0011RBl\u00077\u0004B!!\u0010\u0004Z\u0012A\u0011\u0011SBh\u0005\u0004\t\u0019\u0005\u0005\u0003\u0002>\ruG\u0001CAL\u0007\u001f\u0014\r!a\u0011\t\u0011\r\u001d7q\u001aa\u0001\u0007+D\u0001ba3\u0004P\u0002\u000711\u001d\t\u0006A\u0006%1Q\u001b\u0005\b\u0007s\u0003A\u0011IBt)\u0019\t\tm!;\u0004l\"A1qYBs\u0001\u0004\t\t\r\u0003\u0005\u0004L\u000e\u0015\b\u0019ABw!\u0015\u0001\u0017\u0011BAa\u0011\u001d\u0019\t\u0010\u0001C\u0001\u0007g\fa\"\u001b8u\u0003\u000e\u001cW/\\;mCR|'\u000f\u0006\u0003\u0004v\u000em\b#B\u000f\u0004x\u0006\r\u0012bAB}\r\tY\u0011iY2v[Vd\u0017\r^8s\u0011!\u0019ipa<A\u0002\u0005m\u0013\u0001D5oSRL\u0017\r\u001c,bYV,\u0007\u0006CBx\t\u0003!9\u0001b\u0003\u0011\u0007Y\"\u0019!C\u0002\u0005\u0006]\u0012!\u0002Z3qe\u0016\u001c\u0017\r^3eC\t!I!\u0001\u000evg\u0016\u00043o\u0019\u0015*]1|gnZ!dGVlW\u000f\\1u_JD\u0013&\t\u0002\u0005\u000e\u0005)!G\f\u0019/a!91\u0011\u001f\u0001\u0005\u0002\u0011EACBB{\t'!)\u0002\u0003\u0005\u0004~\u0012=\u0001\u0019AA.\u0011\u001d!9\u0002b\u0004A\u0002Q\nAA\\1nK\"BAq\u0002C\u0001\t7!Y!\t\u0002\u0005\u001e\u0005\u0001So]3!g\u000eD\u0013F\f7p]\u001e\f5mY;nk2\fGo\u001c:)'R\u0014\u0018N\\4*\u0011\u001d!\t\u0003\u0001C\u0001\tG\t\u0011\u0003Z8vE2,\u0017iY2v[Vd\u0017\r^8s)\u0011!)\u0003b\n\u0011\u000bu\u001990a3\t\u0011\ruHq\u0004a\u0001\tS\u00012A\u000eC\u0016\u0013\r\tym\u000e\u0015\t\t?!\t\u0001b\f\u0005\f\u0005\u0012A\u0011G\u0001\u001dkN,\u0007e]2)S9\"w.\u001e2mK\u0006\u001b7-^7vY\u0006$xN\u001d\u0015*\u0011\u001d!\t\u0003\u0001C\u0001\tk!b\u0001\"\n\u00058\u0011e\u0002\u0002CB\u007f\tg\u0001\r\u0001\"\u000b\t\u000f\u0011]A1\u0007a\u0001i!BA1\u0007C\u0001\t{!Y!\t\u0002\u0005@\u0005\u0011So]3!g\u000eD\u0013F\f3pk\ndW-Q2dk6,H.\u0019;pe\"\u001aFO]5oO&Bq\u0001b\u0011\u0001\t\u0003!)%A\u0006bG\u000e,X.\u001e7bi>\u0014H\u0003BB{\t\u000fB\u0001b!@\u0005B\u0001\u0007\u00111\f\u0015\t\t\u0003\"\t\u0001b\u0002\u0005\f!9A1\t\u0001\u0005\u0002\u00115CCBB{\t\u001f\"\t\u0006\u0003\u0005\u0004~\u0012-\u0003\u0019AA.\u0011\u001d!9\u0002b\u0013A\u0002QB\u0003\u0002b\u0013\u0005\u0002\u0011mA1\u0002\u0005\b\t\u0007\u0002A\u0011\u0001C,)\u0011!)\u0003\"\u0017\t\u0011\ruHQ\u000ba\u0001\tSA\u0003\u0002\"\u0016\u0005\u0002\u0011=B1\u0002\u0005\b\t\u0007\u0002A\u0011\u0001C0)\u0019!)\u0003\"\u0019\u0005d!A1Q C/\u0001\u0004!I\u0003C\u0004\u0005\u0018\u0011u\u0003\u0019\u0001\u001b)\u0011\u0011uC\u0011\u0001C\u001f\t\u0017Aq\u0001b\u0011\u0001\t\u0003!I'\u0006\u0003\u0005l\u0011EDC\u0002C7\tg\")\bE\u0003\u001e\u0007o$y\u0007\u0005\u0003\u0002>\u0011ED\u0001CA!\tO\u0012\r!a\u0011\t\u0011\ruHq\ra\u0001\t_B\u0001\u0002b\u001e\u0005h\u0001\u0007A\u0011P\u0001\u0011C\u000e\u001cW/\\;mCR|'\u000fU1sC6\u0004R!\bC>\t_J1\u0001\" \u0007\u0005A\t5mY;nk2\fGo\u001c:QCJ\fW\u000e\u000b\u0005\u0005h\u0011\u0005A\u0011\u0011C\u0006C\t!\u0019)A\tvg\u0016\u0004\u0013iY2v[Vd\u0017\r^8s-JBq\u0001b\u0011\u0001\t\u0003!9)\u0006\u0003\u0005\n\u0012=E\u0003\u0003CF\t##\u0019\n\"&\u0011\u000bu\u00199\u0010\"$\u0011\t\u0005uBq\u0012\u0003\t\u0003\u0003\")I1\u0001\u0002D!A1Q CC\u0001\u0004!i\tC\u0004\u0005\u0018\u0011\u0015\u0005\u0019\u0001\u001b\t\u0011\u0011]DQ\u0011a\u0001\t/\u0003R!\bC>\t\u001bC\u0003\u0002\"\"\u0005\u0002\u0011\u0005E1\u0002\u0005\b\t;\u0003A\u0011\u0001CP\u0003-\t7mY;nk2\f'\r\\3\u0016\r\u0011\u0005F1\u0016CX)\u0019!\u0019\u000bb-\u00056B9Q\u0004\"*\u0005*\u00125\u0016b\u0001CT\r\tY\u0011iY2v[Vd\u0017M\u00197f!\u0011\ti\u0004b+\u0005\u0011\u0005\u0005C1\u0014b\u0001\u0003\u0007\u0002B!!\u0010\u00050\u0012AA\u0011\u0017CN\u0005\u0004\t\u0019EA\u0001S\u0011!\u0019i\u0010b'A\u0002\u0011%\u0006\u0002\u0003C\\\t7\u0003\r\u0001\"/\u0002\u000bA\f'/Y7\u0011\u000fu!Y\f\"+\u0005.&\u0019AQ\u0018\u0004\u0003!\u0005\u001b7-^7vY\u0006\u0014G.\u001a)be\u0006l\u0007\u0006\u0003CN\t\u0003!\t\tb\u0003\t\u000f\u0011u\u0005\u0001\"\u0001\u0005DV1AQ\u0019Cf\t\u001f$\u0002\u0002b2\u0005R\u0012MGQ\u001b\t\b;\u0011\u0015F\u0011\u001aCg!\u0011\ti\u0004b3\u0005\u0011\u0005\u0005C\u0011\u0019b\u0001\u0003\u0007\u0002B!!\u0010\u0005P\u0012AA\u0011\u0017Ca\u0005\u0004\t\u0019\u0005\u0003\u0005\u0004~\u0012\u0005\u0007\u0019\u0001Ce\u0011\u001d!9\u0002\"1A\u0002QB\u0001\u0002b.\u0005B\u0002\u0007Aq\u001b\t\b;\u0011mF\u0011\u001aCgQ!!\t\r\"\u0001\u0005\u0002\u0012-\u0001b\u0002Co\u0001\u0011\u0005Aq\\\u0001\nEJ|\u0017\rZ2bgR,B\u0001\"9\u0005pR!A1\u001dCy!\u0019!)\u000f\";\u0005n6\u0011Aq\u001d\u0006\u0004\t;4\u0011\u0002\u0002Cv\tO\u0014\u0011B\u0011:pC\u0012\u001c\u0017m\u001d;\u0011\t\u0005uBq\u001e\u0003\t\u0003\u0003\"YN1\u0001\u0002D!AA1\u001fCn\u0001\u0004!i/A\u0003wC2,X\rC\u0004\u0005x\u0002!\t\u0001\"?\u0002\tM$x\u000e\u001d\u000b\u0003\tw\u00042A\u000eC\u007f\u0013\r!yp\u000e\u0002\u0005+:LG\u000fC\u0004\u0006\u0004\u0001!\t\u0005\"?\u0002\u000b\rdwn]3\t\u000f\u0015\u001d\u0001\u0001\"\u0001\u0006\n\u0005aq-\u001a;Ta\u0006\u00148\u000eS8nKR\u0011Q1\u0002\t\u0005\u001f\u00155A'C\u0002\u0006\u0010\t\u0011\u0001b\u00149uS>t\u0017\r\u001c\u0005\b\u000b'\u0001A\u0011AC\u000b\u0003\u001d\tG\r\u001a$jY\u0016$B\u0001b?\u0006\u0018!9\u00111]C\t\u0001\u0004!\u0004bBC\n\u0001\u0011\u0005Q1\u0004\u000b\u0007\tw,i\"b\b\t\u000f\u0005\rX\u0011\u0004a\u0001i!AQ\u0011EC\r\u0001\u0004)\u0019#A\u0005sK\u000e,(o]5wKB\u0019a'\"\n\n\u0005m<\u0004bBC\u0015\u0001\u0011\u0005Q1F\u0001\u0007C\u0012$'*\u0019:\u0015\t\u0011mXQ\u0006\u0005\b\u0003G,9\u00031\u00015\u0011\u001d)\t\u0004\u0001C\u0001\u000bg\t1\u0003[1e_>\u00048i\u001c8gS\u001e,(/\u0019;j_:$\"a!\"\t\u000f\u0015]\u0002\u0001\"\u0001\u0006:\u0005\u00012/\u001a;DQ\u0016\u001c7\u000e]8j]R$\u0015N\u001d\u000b\u0005\tw,Y\u0004C\u0004\u0006>\u0015U\u0002\u0019\u0001\u001b\u0002\u0007\u0011L'\u000fC\u0004\u0006B\u0001!\t!b\u0011\u0002!\u001d,Go\u00115fG.\u0004x.\u001b8u\t&\u0014XCAC\u0006\u0011\u001d)9\u0005\u0001C\t\u000b\u0013\nab\u00195fG.\u0004x.\u001b8u\r&dW-\u0006\u0003\u0006L\u0015EC\u0003BC'\u000b'\u0002RaDA\u001c\u000b\u001f\u0002B!!\u0010\u0006R\u0011A\u0011\u0011IC#\u0005\u0004\t\u0019\u0005C\u0004\u0002d\u0016\u0015\u0003\u0019\u0001\u001b\t\u000f\u0015]\u0003\u0001\"\u0001\u0006Z\u00059q-\u001a;D_:4W#A\u0017\t\u000f\u0015u\u0003\u0001\"\u0001\u0006`\u0005Y1/\u001a;DC2d7+\u001b;f)\u0011!Y0\"\u0019\t\u000f\u0015\rT1\fa\u0001i\u0005!1/\u001b;f\u0011\u001d)9\u0007\u0001C\u0001\ts\fQb\u00197fCJ\u001c\u0015\r\u001c7TSR,\u0007bBC6\u0001\u0011\u0005QQN\u0001\u0011g\u0016$Hj\\2bYB\u0013x\u000e]3sif$b\u0001b?\u0006p\u0015M\u0004bBC9\u000bS\u0002\r\u0001N\u0001\u0004W\u0016L\bb\u0002Cz\u000bS\u0002\r\u0001\u000e\u0005\b\u000bo\u0002A\u0011AC=\u0003A9W\r\u001e'pG\u0006d\u0007K]8qKJ$\u0018\u0010F\u00025\u000bwBq!\"\u001d\u0006v\u0001\u0007A\u0007C\u0004\u0006\u0000\u0001!\t!\"!\u0002#M,GOS8c\t\u0016\u001c8M]5qi&|g\u000e\u0006\u0003\u0005|\u0016\r\u0005b\u0002Cz\u000b{\u0002\r\u0001\u000e\u0005\b\u000b\u000f\u0003A\u0011ACE\u0003-\u0019X\r\u001e'pO2+g/\u001a7\u0015\t\u0011mX1\u0012\u0005\b\u000b\u001b+)\t1\u00015\u0003!awn\u001a'fm\u0016d\u0007bBCI\u0001\u0011\u0005Q1S\u0001\fg\u0016$(j\u001c2He>,\b\u000f\u0006\u0005\u0005|\u0016UU\u0011TCO\u0011\u001d)9*b$A\u0002Q\nqa\u001a:pkBLE\rC\u0004\u0006\u001c\u0016=\u0005\u0019\u0001\u001b\u0002\u0017\u0011,7o\u0019:jaRLwN\u001c\u0005\t\u000b?+y\t1\u0001\u0006$\u0005\t\u0012N\u001c;feJ,\b\u000f^(o\u0007\u0006t7-\u001a7\t\u000f\u0015E\u0005\u0001\"\u0001\u0006$R1A1`CS\u000bOCq!b&\u0006\"\u0002\u0007A\u0007C\u0004\u0006\u001c\u0016\u0005\u0006\u0019\u0001\u001b\t\u000f\u0015-\u0006\u0001\"\u0001\u0005z\u0006i1\r\\3be*{'m\u0012:pkBDq!b,\u0001\t\u0003)\t,\u0001\bdC:\u001cW\r\u001c&pE\u001e\u0013x.\u001e9\u0015\t\u0011mX1\u0017\u0005\b\u000b/+i\u000b1\u00015\u0011\u001d)9\f\u0001C\u0001\ts\fQbY1oG\u0016d\u0017\t\u001c7K_\n\u001c\bbBC^\u0001\u0011\u0005QQX\u0001\u0012O\u0016$\b+\u001a:tSN$XM\u001c;S\t\u0012\u001bXCAC`!\u0019\u00017-a\t\u0006BB\"Q1YCd!\u0015y\u0011qGCc!\u0011\ti$b2\u0005\u0019\u0015%W\u0011XA\u0001\u0002\u0003\u0015\t!a\u0011\u0003\u0007}#\u0013gB\u0004\u0006N\nA\t!b4\u0002!)\u000bg/Y*qCJ\\7i\u001c8uKb$\bcA\b\u0006R\u001a1\u0011A\u0001E\u0001\u000b'\u001cB!\"5\u0006VB\u0019a'b6\n\u0007\u0015ewG\u0001\u0004B]f\u0014VM\u001a\u0005\bG\u0015EG\u0011ACo)\t)y\r\u0003\u0005\u0006b\u0016EG1ACr\u0003A1'o\\7Ta\u0006\u00148nQ8oi\u0016DH\u000fF\u0002&\u000bKDaAGCp\u0001\u0004a\u0002\u0002CCu\u000b#$\u0019!b;\u0002\u001dQ|7\u000b]1sW\u000e{g\u000e^3yiR\u0019A$\"<\t\u000f\u0015=Xq\u001da\u0001K\u0005\u0019!n]2\t\u0011\u0015MX\u0011\u001bC\u0001\u000bk\f!B[1s\u001f\u001a\u001cE.Y:t)\r!Vq\u001f\u0005\t\u000bs,\t\u00101\u0001\u0006|\u0006\u00191\r\\:1\t\u0015uh\u0011\u0001\t\u0006k\t=Sq \t\u0005\u0003{1\t\u0001\u0002\u0007\u0007\u0004\u0015]\u0018\u0011!A\u0001\u0006\u0003\t\u0019EA\u0002`IMB\u0001Bb\u0002\u0006R\u0012\u0005a\u0011B\u0001\fU\u0006\u0014xJZ(cU\u0016\u001cG\u000fF\u0002U\r\u0017A\u0001B\"\u0004\u0007\u0006\u0001\u0007QQ[\u0001\u0004_\nT\u0007\"\u0003D\t\u000b#$\tA\u0002D\n\u000311\u0017m[3DY\u0006\u001c8\u000fV1h+\u00111)B\"\n\u0016\u0005\u0019]\u0001C\u0002D\r\r?1\u0019#\u0004\u0002\u0007\u001c)\u0019aQD\u001c\u0002\u000fI,g\r\\3di&!a\u0011\u0005D\u000e\u0005!\u0019E.Y:t)\u0006<\u0007\u0003BA\u001f\rK!\u0001\"!\u0011\u0007\u0010\t\u0007\u00111\t"
)
public class JavaSparkContext extends JavaSparkContextVarargsWorkaround implements Closeable {
   private final SparkContext sc;
   private final SparkEnv env;

   public static String[] jarOfObject(Object var0) {
      return JavaSparkContext$.MODULE$.jarOfObject(var0);
   }

   public static String[] jarOfClass(Class<?> var0) {
      return JavaSparkContext$.MODULE$.jarOfClass(var0);
   }

   public static SparkContext toSparkContext(JavaSparkContext var0) {
      return JavaSparkContext$.MODULE$.toSparkContext(var0);
   }

   public static JavaSparkContext fromSparkContext(SparkContext var0) {
      return JavaSparkContext$.MODULE$.fromSparkContext(var0);
   }

   public SparkContext sc() {
      return this.sc;
   }

   public SparkEnv env() {
      return this.env;
   }

   public JavaSparkStatusTracker statusTracker() {
      return new JavaSparkStatusTracker(this.sc());
   }

   public Boolean isLocal() {
      return Predef$.MODULE$.boolean2Boolean(this.sc().isLocal());
   }

   public String sparkUser() {
      return this.sc().sparkUser();
   }

   public String master() {
      return this.sc().master();
   }

   public String appName() {
      return this.sc().appName();
   }

   public List<String> jars() {
      return (List)JavaConverters$.MODULE$.seqAsJavaListConverter(this.sc().jars()).asJava();
   }

   public Long startTime() {
      return Predef$.MODULE$.long2Long(this.sc().startTime());
   }

   public String version() {
      return this.sc().version();
   }

   public Integer defaultParallelism() {
      return Predef$.MODULE$.int2Integer(this.sc().defaultParallelism());
   }

   public Integer defaultMinPartitions() {
      return Predef$.MODULE$.int2Integer(this.sc().defaultMinPartitions());
   }

   public <T> JavaRDD<T> parallelize(List<T> list, int numSlices) {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaRDD$.MODULE$.<T>fromRDD(this.sc().parallelize((Seq)JavaConverters$.MODULE$.asScalaBufferConverter(list).asScala(), numSlices, ctag), ctag);
   }

   public <T> JavaRDD<T> emptyRDD() {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaRDD$.MODULE$.<T>fromRDD(new EmptyRDD(this.sc(), ctag), ctag);
   }

   public <T> JavaRDD<T> parallelize(List<T> list) {
      return this.<T>parallelize(list, this.sc().defaultParallelism());
   }

   public <K, V> JavaPairRDD<K, V> parallelizePairs(List<Tuple2<K, V>> list, int numSlices) {
      ClassTag ctagK = JavaSparkContext$.MODULE$.fakeClassTag();
      ClassTag ctagV = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaPairRDD$.MODULE$.<K, V>fromRDD(this.sc().parallelize((Seq)JavaConverters$.MODULE$.asScalaBufferConverter(list).asScala(), numSlices, ClassTag$.MODULE$.apply(Tuple2.class)), ctagK, ctagV);
   }

   public <K, V> JavaPairRDD<K, V> parallelizePairs(List<Tuple2<K, V>> list) {
      return this.<K, V>parallelizePairs(list, this.sc().defaultParallelism());
   }

   public JavaDoubleRDD parallelizeDoubles(List<Double> list, int numSlices) {
      return JavaDoubleRDD$.MODULE$.fromRDD(this.sc().parallelize((Seq)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(list).asScala()).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final double apply(Double x$1) {
            return x$1;
         }
      }, Buffer$.MODULE$.canBuildFrom()), numSlices, ClassTag$.MODULE$.Double()));
   }

   public JavaDoubleRDD parallelizeDoubles(List<Double> list) {
      return this.parallelizeDoubles(list, this.sc().defaultParallelism());
   }

   public JavaRDD<String> textFile(String path) {
      return JavaRDD$.MODULE$.<String>fromRDD(this.sc().textFile(path, this.sc().textFile$default$2()), ClassTag$.MODULE$.apply(String.class));
   }

   public JavaRDD<String> textFile(String path, int minPartitions) {
      return JavaRDD$.MODULE$.<String>fromRDD(this.sc().textFile(path, minPartitions), ClassTag$.MODULE$.apply(String.class));
   }

   public JavaPairRDD<String, String> wholeTextFiles(String path, int minPartitions) {
      return new JavaPairRDD<String, String>(this.sc().wholeTextFiles(path, minPartitions), ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(String.class));
   }

   public JavaPairRDD<String, String> wholeTextFiles(String path) {
      return new JavaPairRDD<String, String>(this.sc().wholeTextFiles(path, this.sc().wholeTextFiles$default$2()), ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(String.class));
   }

   public JavaPairRDD<String, PortableDataStream> binaryFiles(String path, int minPartitions) {
      return new JavaPairRDD<String, PortableDataStream>(this.sc().binaryFiles(path, minPartitions), ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(PortableDataStream.class));
   }

   public JavaPairRDD<String, PortableDataStream> binaryFiles(String path) {
      return new JavaPairRDD<String, PortableDataStream>(this.sc().binaryFiles(path, Predef$.MODULE$.Integer2int(this.defaultMinPartitions())), ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(PortableDataStream.class));
   }

   public JavaRDD<byte[]> binaryRecords(String path, int recordLength) {
      return new JavaRDD<byte[]>(this.sc().binaryRecords(path, recordLength, this.sc().binaryRecords$default$3()), ClassTag$.MODULE$.apply(ScalaRunTime$.MODULE$.arrayClass(Byte.TYPE)));
   }

   public <K, V> JavaPairRDD<K, V> sequenceFile(String path, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      return new JavaPairRDD<K, V>(this.sc().sequenceFile(path, keyClass, valueClass, minPartitions), ctagK, ctagV);
   }

   public <K, V> JavaPairRDD<K, V> sequenceFile(String path, Class<K> keyClass, Class<V> valueClass) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      return new JavaPairRDD<K, V>(this.sc().sequenceFile(path, keyClass, valueClass), ctagK, ctagV);
   }

   public <T> JavaRDD<T> objectFile(String path, int minPartitions) {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaRDD$.MODULE$.<T>fromRDD(this.sc().objectFile(path, minPartitions, ctag), ctag);
   }

   public <T> JavaRDD<T> objectFile(String path) {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return JavaRDD$.MODULE$.<T>fromRDD(this.sc().objectFile(path, this.sc().objectFile$default$2(), ctag), ctag);
   }

   public <K, V, F extends InputFormat<K, V>> JavaPairRDD<K, V> hadoopRDD(JobConf conf, Class<F> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      RDD rdd = this.sc().hadoopRDD(conf, inputFormatClass, keyClass, valueClass, minPartitions);
      return new JavaHadoopRDD<K, V>((HadoopRDD)rdd, ctagK, ctagV);
   }

   public <K, V, F extends InputFormat<K, V>> JavaPairRDD<K, V> hadoopRDD(JobConf conf, Class<F> inputFormatClass, Class<K> keyClass, Class<V> valueClass) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      RDD rdd = this.sc().hadoopRDD(conf, inputFormatClass, keyClass, valueClass, this.sc().hadoopRDD$default$5());
      return new JavaHadoopRDD<K, V>((HadoopRDD)rdd, ctagK, ctagV);
   }

   public <K, V, F extends InputFormat<K, V>> JavaPairRDD<K, V> hadoopFile(String path, Class<F> inputFormatClass, Class<K> keyClass, Class<V> valueClass, int minPartitions) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      RDD rdd = this.sc().hadoopFile(path, inputFormatClass, keyClass, valueClass, minPartitions);
      return new JavaHadoopRDD<K, V>((HadoopRDD)rdd, ctagK, ctagV);
   }

   public <K, V, F extends InputFormat<K, V>> JavaPairRDD<K, V> hadoopFile(String path, Class<F> inputFormatClass, Class<K> keyClass, Class<V> valueClass) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(keyClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(valueClass);
      RDD rdd = this.sc().hadoopFile(path, inputFormatClass, keyClass, valueClass, this.sc().hadoopFile$default$5());
      return new JavaHadoopRDD<K, V>((HadoopRDD)rdd, ctagK, ctagV);
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> JavaPairRDD<K, V> newAPIHadoopFile(String path, Class<F> fClass, Class<K> kClass, Class<V> vClass, Configuration conf) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(kClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(vClass);
      RDD rdd = this.sc().newAPIHadoopFile(path, fClass, kClass, vClass, conf);
      return new JavaNewHadoopRDD<K, V>((NewHadoopRDD)rdd, ctagK, ctagV);
   }

   public <K, V, F extends org.apache.hadoop.mapreduce.InputFormat<K, V>> JavaPairRDD<K, V> newAPIHadoopRDD(Configuration conf, Class<F> fClass, Class<K> kClass, Class<V> vClass) {
      ClassTag ctagK = ClassTag$.MODULE$.apply(kClass);
      ClassTag ctagV = ClassTag$.MODULE$.apply(vClass);
      RDD rdd = this.sc().newAPIHadoopRDD(conf, fClass, kClass, vClass);
      return new JavaNewHadoopRDD<K, V>((NewHadoopRDD)rdd, ctagK, ctagV);
   }

   public <T> JavaRDD<T> union(JavaRDD<T> first, List<JavaRDD<T>> rest) {
      Seq rdds = (Seq)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new JavaRDD[]{first}))).$plus$plus((GenTraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(rest).asScala(), Seq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDD<T> apply(JavaRDD<T> x$2) {
            return x$2.rdd();
         }
      }, Seq$.MODULE$.canBuildFrom());
      ClassTag ctag = first.classTag();
      return JavaRDD$.MODULE$.<T>fromRDD(this.sc().union(rdds, ctag), ctag);
   }

   public <K, V> JavaPairRDD<K, V> union(JavaPairRDD<K, V> first, List<JavaPairRDD<K, V>> rest) {
      Seq rdds = (Seq)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new JavaPairRDD[]{first}))).$plus$plus((GenTraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(rest).asScala(), Seq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDD<Tuple2<K, V>> apply(JavaPairRDD<K, V> x$3) {
            return x$3.rdd();
         }
      }, Seq$.MODULE$.canBuildFrom());
      ClassTag ctag = first.classTag();
      ClassTag ctagK = first.kClassTag();
      ClassTag ctagV = first.vClassTag();
      return new JavaPairRDD<K, V>(this.sc().union(rdds, ctag), ctagK, ctagV);
   }

   public JavaDoubleRDD union(JavaDoubleRDD first, List<JavaDoubleRDD> rest) {
      Seq rdds = (Seq)((TraversableLike)((TraversableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new JavaDoubleRDD[]{first}))).$plus$plus((GenTraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(rest).asScala(), Seq$.MODULE$.canBuildFrom())).map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final RDD<Object> apply(JavaDoubleRDD x$4) {
            return x$4.srdd();
         }
      }, Seq$.MODULE$.canBuildFrom());
      return new JavaDoubleRDD(this.sc().union(rdds, ClassTag$.MODULE$.Double()));
   }

   /** @deprecated */
   public Accumulator<Integer> intAccumulator(int initialValue) {
      return this.sc().<Integer>accumulator(BoxesRunTime.boxToInteger(initialValue), AccumulatorParam.IntAccumulatorParam$.MODULE$);
   }

   /** @deprecated */
   public Accumulator<Integer> intAccumulator(int initialValue, String name) {
      return this.sc().<Integer>accumulator(BoxesRunTime.boxToInteger(initialValue), name, AccumulatorParam.IntAccumulatorParam$.MODULE$);
   }

   /** @deprecated */
   public Accumulator<Double> doubleAccumulator(double initialValue) {
      return this.sc().<Double>accumulator(BoxesRunTime.boxToDouble(initialValue), AccumulatorParam.DoubleAccumulatorParam$.MODULE$);
   }

   /** @deprecated */
   public Accumulator<Double> doubleAccumulator(double initialValue, String name) {
      return this.sc().<Double>accumulator(BoxesRunTime.boxToDouble(initialValue), name, AccumulatorParam.DoubleAccumulatorParam$.MODULE$);
   }

   /** @deprecated */
   public Accumulator<Integer> accumulator(int initialValue) {
      return this.intAccumulator(initialValue);
   }

   /** @deprecated */
   public Accumulator<Integer> accumulator(int initialValue, String name) {
      return this.intAccumulator(initialValue, name);
   }

   /** @deprecated */
   public Accumulator<Double> accumulator(double initialValue) {
      return this.doubleAccumulator(initialValue);
   }

   /** @deprecated */
   public Accumulator<Double> accumulator(double initialValue, String name) {
      return this.doubleAccumulator(initialValue, name);
   }

   /** @deprecated */
   public <T> Accumulator<T> accumulator(T initialValue, AccumulatorParam<T> accumulatorParam) {
      return this.sc().<T>accumulator(initialValue, accumulatorParam);
   }

   /** @deprecated */
   public <T> Accumulator<T> accumulator(T initialValue, String name, AccumulatorParam<T> accumulatorParam) {
      return this.sc().<T>accumulator(initialValue, name, accumulatorParam);
   }

   /** @deprecated */
   public <T, R> Accumulable<T, R> accumulable(T initialValue, AccumulableParam<T, R> param) {
      return this.sc().<T, R>accumulable(initialValue, param);
   }

   /** @deprecated */
   public <T, R> Accumulable<T, R> accumulable(T initialValue, String name, AccumulableParam<T, R> param) {
      return this.sc().<T, R>accumulable(initialValue, name, param);
   }

   public <T> Broadcast<T> broadcast(T value) {
      return this.sc().<T>broadcast(value, JavaSparkContext$.MODULE$.fakeClassTag());
   }

   public void stop() {
      this.sc().stop();
   }

   public void close() {
      this.stop();
   }

   public Optional<String> getSparkHome() {
      return JavaUtils$.MODULE$.<String>optionToOptional(this.sc().getSparkHome());
   }

   public void addFile(String path) {
      this.sc().addFile(path);
   }

   public void addFile(String path, boolean recursive) {
      this.sc().addFile(path, recursive);
   }

   public void addJar(String path) {
      this.sc().addJar(path);
   }

   public Configuration hadoopConfiguration() {
      return this.sc().hadoopConfiguration();
   }

   public void setCheckpointDir(String dir) {
      this.sc().setCheckpointDir(dir);
   }

   public Optional<String> getCheckpointDir() {
      return JavaUtils$.MODULE$.<String>optionToOptional(this.sc().getCheckpointDir());
   }

   public <T> JavaRDD<T> checkpointFile(String path) {
      ClassTag ctag = JavaSparkContext$.MODULE$.fakeClassTag();
      return new JavaRDD<T>(this.sc().checkpointFile(path, ctag), ctag);
   }

   public SparkConf getConf() {
      return this.sc().getConf();
   }

   public void setCallSite(String site) {
      this.sc().setCallSite(site);
   }

   public void clearCallSite() {
      this.sc().clearCallSite();
   }

   public void setLocalProperty(String key, String value) {
      this.sc().setLocalProperty(key, value);
   }

   public String getLocalProperty(String key) {
      return this.sc().getLocalProperty(key);
   }

   public void setJobDescription(String value) {
      this.sc().setJobDescription(value);
   }

   public void setLogLevel(String logLevel) {
      this.sc().setLogLevel(logLevel);
   }

   public void setJobGroup(String groupId, String description, boolean interruptOnCancel) {
      this.sc().setJobGroup(groupId, description, interruptOnCancel);
   }

   public void setJobGroup(String groupId, String description) {
      this.sc().setJobGroup(groupId, description, this.sc().setJobGroup$default$3());
   }

   public void clearJobGroup() {
      this.sc().clearJobGroup();
   }

   public void cancelJobGroup(String groupId) {
      this.sc().cancelJobGroup(groupId);
   }

   public void cancelAllJobs() {
      this.sc().cancelAllJobs();
   }

   public Map<Integer, JavaRDD<?>> getPersistentRDDs() {
      return (Map)JavaConverters$.MODULE$.mapAsJavaMapConverter(this.sc().getPersistentRDDs().mapValues(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final JavaRDD<?> apply(RDD<?> s) {
            return JavaRDD$.MODULE$.fromRDD(s, ClassTag$.MODULE$.apply(Object.class));
         }
      })).asJava();
   }

   public JavaSparkContext(SparkContext sc) {
      this.sc = sc;
      this.env = sc.env();
   }

   public JavaSparkContext() {
      this(new SparkContext());
   }

   public JavaSparkContext(SparkConf conf) {
      this(new SparkContext(conf));
   }

   public JavaSparkContext(String master, String appName) {
      this(new SparkContext(master, appName));
   }

   public JavaSparkContext(String master, String appName, SparkConf conf) {
      this(conf.setMaster(master).setAppName(appName));
   }

   public JavaSparkContext(String master, String appName, String sparkHome, String jarFile) {
      this(new SparkContext(master, appName, sparkHome, (Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{jarFile})))));
   }

   public JavaSparkContext(String master, String appName, String sparkHome, String[] jars) {
      this(new SparkContext(master, appName, sparkHome, Predef$.MODULE$.refArrayOps((Object[])jars).toSeq()));
   }

   public JavaSparkContext(String master, String appName, String sparkHome, String[] jars, Map<String, String> environment) {
      this(new SparkContext(master, appName, sparkHome, Predef$.MODULE$.refArrayOps((Object[])jars).toSeq(), (scala.collection.Map)JavaConverters$.MODULE$.mapAsScalaMapConverter(environment).asScala()));
   }
}
