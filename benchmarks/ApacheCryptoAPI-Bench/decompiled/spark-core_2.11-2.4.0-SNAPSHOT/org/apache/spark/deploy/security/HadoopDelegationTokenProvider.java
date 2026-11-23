package org.apache.spark.deploy.security;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.spark.SparkConf;
import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00193\u0001\"\u0001\u0002\u0011\u0002G\u0005a\u0001\u0004\u0002\u001e\u0011\u0006$wn\u001c9EK2,w-\u0019;j_:$vn[3o!J|g/\u001b3fe*\u00111\u0001B\u0001\tg\u0016\u001cWO]5us*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\"\u0001A\u0007\u0011\u00059\tR\"A\b\u000b\u0003A\tQa]2bY\u0006L!AE\b\u0003\r\u0005s\u0017PU3g\u0011\u0015!\u0002A\"\u0001\u0017\u0003-\u0019XM\u001d<jG\u0016t\u0015-\\3\u0004\u0001U\tq\u0003\u0005\u0002\u001979\u0011a\"G\u0005\u00035=\ta\u0001\u0015:fI\u00164\u0017B\u0001\u000f\u001e\u0005\u0019\u0019FO]5oO*\u0011!d\u0004\u0005\u0006?\u00011\t\u0001I\u0001\u0019I\u0016dWmZ1uS>tGk\\6f]N\u0014V-];je\u0016$GcA\u0011%UA\u0011aBI\u0005\u0003G=\u0011qAQ8pY\u0016\fg\u000eC\u0003&=\u0001\u0007a%A\u0005ta\u0006\u00148nQ8oMB\u0011q\u0005K\u0007\u0002\r%\u0011\u0011F\u0002\u0002\n'B\f'o[\"p]\u001aDQa\u000b\u0010A\u00021\n!\u0002[1e_>\u00048i\u001c8g!\ti#'D\u0001/\u0015\ty\u0003'\u0001\u0003d_:4'BA\u0019\t\u0003\u0019A\u0017\rZ8pa&\u00111G\f\u0002\u000e\u0007>tg-[4ve\u0006$\u0018n\u001c8\t\u000bU\u0002a\u0011\u0001\u001c\u0002-=\u0014G/Y5o\t\u0016dWmZ1uS>tGk\\6f]N$BaN\u001f?\u007fA\u0019a\u0002\u000f\u001e\n\u0005ez!AB(qi&|g\u000e\u0005\u0002\u000fw%\u0011Ah\u0004\u0002\u0005\u0019>tw\rC\u0003,i\u0001\u0007A\u0006C\u0003&i\u0001\u0007a\u0005C\u0003Ai\u0001\u0007\u0011)A\u0003de\u0016$7\u000f\u0005\u0002C\t6\t1I\u0003\u0002\u0004a%\u0011Qi\u0011\u0002\f\u0007J,G-\u001a8uS\u0006d7\u000f"
)
public interface HadoopDelegationTokenProvider {
   String serviceName();

   boolean delegationTokensRequired(SparkConf var1, Configuration var2);

   Option<Object> obtainDelegationTokens(Configuration var1, SparkConf var2, Credentials var3);
}
