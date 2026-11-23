package org.apache.spark.internal.io;

import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.spark.SparkConf;
import scala.Serializable;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-a!B\u0001\u0003\u0003\u0003i!!\u0006%bI>|\u0007o\u0016:ji\u0016\u001cuN\u001c4jOV#\u0018\u000e\u001c\u0006\u0003\u0007\u0011\t!![8\u000b\u0005\u00151\u0011\u0001C5oi\u0016\u0014h.\u00197\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001!F\u0002\u000fe\t\u001a2\u0001A\b\u0016!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fMB\u0011\u0001CF\u0005\u0003/E\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001\"\u0007\u0001\u0003\u0004\u0003\u0006YAG\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004cA\u000e\u001fA5\tAD\u0003\u0002\u001e#\u00059!/\u001a4mK\u000e$\u0018BA\u0010\u001d\u0005!\u0019E.Y:t)\u0006<\u0007CA\u0011#\u0019\u0001!Qa\t\u0001C\u0002\u0011\u0012\u0011AV\t\u0003K!\u0002\"\u0001\u0005\u0014\n\u0005\u001d\n\"a\u0002(pi\"Lgn\u001a\t\u0003!%J!AK\t\u0003\u0007\u0005s\u0017\u0010C\u0003-\u0001\u0011\u0005Q&\u0001\u0004=S:LGO\u0010\u000b\u0002]Q\u0011q\u0006\u000e\t\u0005a\u0001\t\u0004%D\u0001\u0003!\t\t#\u0007B\u00034\u0001\t\u0007AEA\u0001L\u0011\u0015I2\u0006q\u0001\u001b\u0011\u00151\u0004A\"\u00018\u0003A\u0019'/Z1uK*{'mQ8oi\u0016DH\u000fF\u00029\u0001&\u0003\"!\u000f \u000e\u0003iR!a\u000f\u001f\u0002\u00135\f\u0007O]3ek\u000e,'BA\u001f\t\u0003\u0019A\u0017\rZ8pa&\u0011qH\u000f\u0002\u000b\u0015>\u00147i\u001c8uKb$\b\"B!6\u0001\u0004\u0011\u0015\u0001\u00046pER\u0013\u0018mY6fe&#\u0007CA\"G\u001d\t\u0001B)\u0003\u0002F#\u00051\u0001K]3eK\u001aL!a\u0012%\u0003\rM#(/\u001b8h\u0015\t)\u0015\u0003C\u0003Kk\u0001\u00071*A\u0003k_\nLE\r\u0005\u0002\u0011\u0019&\u0011Q*\u0005\u0002\u0004\u0013:$\b\"B(\u0001\r\u0003\u0001\u0016\u0001G2sK\u0006$X\rV1tW\u0006#H/Z7qi\u000e{g\u000e^3yiR)\u0011\u000bV+W1B\u0011\u0011HU\u0005\u0003'j\u0012!\u0003V1tW\u0006#H/Z7qi\u000e{g\u000e^3yi\")\u0011I\u0014a\u0001\u0005\")!J\u0014a\u0001\u0017\")qK\u0014a\u0001\u0017\u000691\u000f\u001d7ji&#\u0007\"B-O\u0001\u0004Y\u0015!\u0004;bg.\fE\u000f^3naRLE\rC\u0003\\\u0001\u0019\u0005A,A\bde\u0016\fG/Z\"p[6LG\u000f^3s)\ti\u0006\r\u0005\u00021=&\u0011qL\u0001\u0002\u001e\u0011\u0006$wn\u001c9NCB\u0014V\rZ;dK\u000e{W.\\5u!J|Go\\2pY\")!J\u0017a\u0001\u0017\")!\r\u0001D\u0001G\u0006Q\u0011N\\5u/JLG/\u001a:\u0015\u0007\u0011<\u0017\u000e\u0005\u0002\u0011K&\u0011a-\u0005\u0002\u0005+:LG\u000fC\u0003iC\u0002\u0007\u0011+A\u0006uCN\\7i\u001c8uKb$\b\"B,b\u0001\u0004Y\u0005\"B6\u0001\r\u0003a\u0017!B<sSR,GC\u00013n\u0011\u0015q'\u000e1\u0001p\u0003\u0011\u0001\u0018-\u001b:\u0011\tA\u0001\u0018\u0007I\u0005\u0003cF\u0011a\u0001V;qY\u0016\u0014\u0004\"B:\u0001\r\u0003!\u0018aC2m_N,wK]5uKJ$\"\u0001Z;\t\u000b!\u0014\b\u0019A)\t\u000b]\u0004a\u0011\u0001=\u0002!%t\u0017\u000e^(viB,HOR8s[\u0006$HC\u00013z\u0011\u0015Qh\u000f1\u00019\u0003)QwNY\"p]R,\u0007\u0010\u001e\u0005\u0006y\u00021\t!`\u0001\u000bCN\u001cXM\u001d;D_:4Gc\u00013\u007f\u007f\")!p\u001fa\u0001q!9\u0011\u0011A>A\u0002\u0005\r\u0011\u0001B2p]\u001a\u0004B!!\u0002\u0002\b5\ta!C\u0002\u0002\n\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4"
)
public abstract class HadoopWriteConfigUtil<K, V> implements Serializable {
   public abstract JobContext createJobContext(String var1, int var2);

   public abstract TaskAttemptContext createTaskAttemptContext(String var1, int var2, int var3, int var4);

   public abstract HadoopMapReduceCommitProtocol createCommitter(int var1);

   public abstract void initWriter(TaskAttemptContext var1, int var2);

   public abstract void write(Tuple2<K, V> var1);

   public abstract void closeWriter(TaskAttemptContext var1);

   public abstract void initOutputFormat(JobContext var1);

   public abstract void assertConf(JobContext var1, SparkConf var2);

   public HadoopWriteConfigUtil(ClassTag<V> evidence$1) {
   }
}
