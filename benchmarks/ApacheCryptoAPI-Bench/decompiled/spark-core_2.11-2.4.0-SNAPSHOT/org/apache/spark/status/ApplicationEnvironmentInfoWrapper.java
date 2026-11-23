package org.apache.spark.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.status.api.v1.ApplicationEnvironmentInfo;
import org.apache.spark.util.kvstore.KVIndex;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00153Q!\u0001\u0002\u0001\t)\u0011\u0011%\u00119qY&\u001c\u0017\r^5p]\u0016sg/\u001b:p]6,g\u000e^%oM><&/\u00199qKJT!a\u0001\u0003\u0002\rM$\u0018\r^;t\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#\u0001\u0003j]\u001a|7\u0001A\u000b\u0002+A\u0011acG\u0007\u0002/)\u0011\u0001$G\u0001\u0003mFR!A\u0007\u0002\u0002\u0007\u0005\u0004\u0018.\u0003\u0002\u001d/\tQ\u0012\t\u001d9mS\u000e\fG/[8o\u000b:4\u0018N]8o[\u0016tG/\u00138g_\"Aa\u0004\u0001B\u0001B\u0003%Q#A\u0003j]\u001a|\u0007\u0005C\u0003!\u0001\u0011\u0005\u0011%\u0001\u0004=S:LGO\u0010\u000b\u0003E\u0011\u0002\"a\t\u0001\u000e\u0003\tAQAE\u0010A\u0002UAQA\n\u0001\u0005\u0002\u001d\n!!\u001b3\u0016\u0003!\u0002\"!\u000b\u0017\u000f\u00051Q\u0013BA\u0016\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011QF\f\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005-j\u0001FA\u00131!\t\td'D\u00013\u0015\t\u0019D'A\u0004lmN$xN]3\u000b\u0005U\"\u0011\u0001B;uS2L!a\u000e\u001a\u0003\u000f-3\u0016J\u001c3fq\"\u0012Q%\u000f\t\u0003u\rk\u0011a\u000f\u0006\u0003yu\n!\"\u00198o_R\fG/[8o\u0015\tqt(A\u0004kC\u000e\\7o\u001c8\u000b\u0005\u0001\u000b\u0015!\u00034bgR,'\u000f_7m\u0015\u0005\u0011\u0015aA2p[&\u0011Ai\u000f\u0002\u000b\u0015N|g.S4o_J,\u0007"
)
public class ApplicationEnvironmentInfoWrapper {
   private final ApplicationEnvironmentInfo info;

   public ApplicationEnvironmentInfo info() {
      return this.info;
   }

   @JsonIgnore
   @KVIndex
   public String id() {
      return ApplicationEnvironmentInfoWrapper.class.getName();
   }

   public ApplicationEnvironmentInfoWrapper(ApplicationEnvironmentInfo info) {
      this.info = info;
   }
}
