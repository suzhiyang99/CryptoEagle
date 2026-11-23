package org.apache.spark.deploy.history;

import org.apache.spark.ui.SparkUI;
import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00113\u0001\"\u0001\u0002\u0011\u0002G\u0005!\u0001\u0004\u0002\u001b\u0003B\u0004H.[2bi&|gnQ1dQ\u0016|\u0005/\u001a:bi&|gn\u001d\u0006\u0003\u0007\u0011\tq\u0001[5ti>\u0014\u0018P\u0003\u0002\u0006\r\u00051A-\u001a9m_fT!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0003\u00015\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007\"\u0002\u000b\u0001\r\u00031\u0012\u0001C4fi\u0006\u0003\b/V%\u0004\u0001Q\u0019qCH\u0014\u0011\u00079A\"$\u0003\u0002\u001a\u001f\t1q\n\u001d;j_:\u0004\"a\u0007\u000f\u000e\u0003\tI!!\b\u0002\u0003\u00171{\u0017\rZ3e\u0003B\u0004X+\u0013\u0005\u0006?M\u0001\r\u0001I\u0001\u0006CB\u0004\u0018\n\u001a\t\u0003C\u0011r!A\u0004\u0012\n\u0005\rz\u0011A\u0002)sK\u0012,g-\u0003\u0002&M\t11\u000b\u001e:j]\u001eT!aI\b\t\u000b!\u001a\u0002\u0019A\u0015\u0002\u0013\u0005$H/Z7qi&#\u0007c\u0001\b\u0019A!)1\u0006\u0001D\u0001Y\u0005i\u0011\r\u001e;bG\"\u001c\u0006/\u0019:l+&#R!\f\u00192ee\u0002\"A\u0004\u0018\n\u0005=z!\u0001B+oSRDQa\b\u0016A\u0002\u0001BQ\u0001\u000b\u0016A\u0002%BQa\r\u0016A\u0002Q\n!!^5\u0011\u0005U:T\"\u0001\u001c\u000b\u0005M2\u0011B\u0001\u001d7\u0005\u001d\u0019\u0006/\u0019:l+&CQA\u000f\u0016A\u0002m\n\u0011bY8na2,G/\u001a3\u0011\u00059a\u0014BA\u001f\u0010\u0005\u001d\u0011un\u001c7fC:DQa\u0010\u0001\u0007\u0002\u0001\u000bQ\u0002Z3uC\u000eD7\u000b]1sWVKE\u0003B\u0017B\u0005\u000eCQa\b A\u0002\u0001BQ\u0001\u000b A\u0002%BQa\r A\u0002Q\u0002"
)
public interface ApplicationCacheOperations {
   Option<LoadedAppUI> getAppUI(String var1, Option<String> var2);

   void attachSparkUI(String var1, Option<String> var2, SparkUI var3, boolean var4);

   void detachSparkUI(String var1, Option<String> var2, SparkUI var3);
}
