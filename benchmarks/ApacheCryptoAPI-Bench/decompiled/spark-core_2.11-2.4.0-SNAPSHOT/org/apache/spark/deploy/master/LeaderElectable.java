package org.apache.spark.deploy.master;

import org.apache.spark.annotation.DeveloperApi;
import scala.reflect.ScalaSignature;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\r2q!\u0001\u0002\u0011\u0002G\u0005QBA\bMK\u0006$WM]#mK\u000e$\u0018M\u00197f\u0015\t\u0019A!\u0001\u0004nCN$XM\u001d\u0006\u0003\u000b\u0019\ta\u0001Z3qY>L(BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0011\u0001A\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u000bU\u0001a\u0011\u0001\f\u0002\u001b\u0015dWm\u0019;fI2+\u0017\rZ3s)\u00059\u0002CA\b\u0019\u0013\tI\u0002C\u0001\u0003V]&$\b\"B\u000e\u0001\r\u00031\u0012!\u0005:fm>\\W\r\u001a'fC\u0012,'o\u001d5ja\"\u0012\u0001!\b\t\u0003=\u0005j\u0011a\b\u0006\u0003A\u0019\t!\"\u00198o_R\fG/[8o\u0013\t\u0011sD\u0001\u0007EKZ,Gn\u001c9fe\u0006\u0003\u0018\u000e"
)
public interface LeaderElectable {
   void electedLeader();

   void revokedLeadership();
}
