package org.apache.spark;

import org.apache.spark.annotation.DeveloperApi;
import org.apache.spark.rdd.RDD;
import scala.Function0;
import scala.Function1;
import scala.Function2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001u3q!\u0001\u0002\u0011\u0002G\u0005\u0011B\u0001\u0007K_\n\u001cVOY7jiR,'O\u0003\u0002\u0004\t\u0005)1\u000f]1sW*\u0011QAB\u0001\u0007CB\f7\r[3\u000b\u0003\u001d\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0006\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\t\u0002A\"\u0001\u0013\u0003%\u0019XOY7ji*{'-\u0006\u0003\u0014W}RBC\u0002\u000b$[\u0005K\u0015\u000bE\u0002\u0016-ai\u0011AA\u0005\u0003/\t\u0011ABR;ukJ,\u0017i\u0019;j_:\u0004\"!\u0007\u000e\r\u0001\u0011)1\u0004\u0005b\u00019\t\t!+\u0005\u0002\u001eAA\u00111BH\u0005\u0003?1\u0011qAT8uQ&tw\r\u0005\u0002\fC%\u0011!\u0005\u0004\u0002\u0004\u0003:L\b\"\u0002\u0013\u0011\u0001\u0004)\u0013a\u0001:eIB\u0019a\u0005\u000b\u0016\u000e\u0003\u001dR!\u0001\n\u0002\n\u0005%:#a\u0001*E\tB\u0011\u0011d\u000b\u0003\u0006YA\u0011\r\u0001\b\u0002\u0002)\")a\u0006\u0005a\u0001_\u0005\u0001\u0002O]8dKN\u001c\b+\u0019:uSRLwN\u001c\t\u0005\u0017A\u0012d(\u0003\u00022\u0019\tIa)\u001e8di&|g.\r\t\u0004gmRcB\u0001\u001b:\u001d\t)\u0004(D\u00017\u0015\t9\u0004\"\u0001\u0004=e>|GOP\u0005\u0002\u001b%\u0011!\bD\u0001\ba\u0006\u001c7.Y4f\u0013\taTH\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0015\tQD\u0002\u0005\u0002\u001a\u007f\u0011)\u0001\t\u0005b\u00019\t\tQ\u000bC\u0003C!\u0001\u00071)\u0001\u0006qCJ$\u0018\u000e^5p]N\u00042a\r#G\u0013\t)UHA\u0002TKF\u0004\"aC$\n\u0005!c!aA%oi\")!\n\u0005a\u0001\u0017\u0006i!/Z:vYRD\u0015M\u001c3mKJ\u0004Ra\u0003'G}9K!!\u0014\u0007\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004CA\u0006P\u0013\t\u0001FB\u0001\u0003V]&$\bB\u0002*\u0011\t\u0003\u00071+\u0001\u0006sKN,H\u000e\u001e$v]\u000e\u00042a\u0003+\u0019\u0013\t)FB\u0001\u0005=Eft\u0017-\\3?Q\t\u0001q\u000b\u0005\u0002Y76\t\u0011L\u0003\u0002[\u0005\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005qK&\u0001\u0004#fm\u0016dw\u000e]3s\u0003BL\u0007"
)
public interface JobSubmitter {
   <T, U, R> FutureAction<R> submitJob(RDD<T> var1, Function1<Iterator<T>, U> var2, Seq<Object> var3, Function2<Object, U, BoxedUnit> var4, Function0<R> var5);
}
