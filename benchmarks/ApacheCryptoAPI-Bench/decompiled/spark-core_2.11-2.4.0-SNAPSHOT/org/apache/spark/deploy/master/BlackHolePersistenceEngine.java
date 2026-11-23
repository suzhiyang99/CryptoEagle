package org.apache.spark.deploy.master;

import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001m3Q!\u0001\u0002\u0001\u00051\u0011!D\u00117bG.Du\u000e\\3QKJ\u001c\u0018n\u001d;f]\u000e,WI\\4j]\u0016T!a\u0001\u0003\u0002\r5\f7\u000f^3s\u0015\t)a!\u0001\u0004eKBdw.\u001f\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001d=i\u0011AA\u0005\u0003!\t\u0011\u0011\u0003U3sg&\u001cH/\u001a8dK\u0016sw-\u001b8f\u0011\u0015\u0011\u0002\u0001\"\u0001\u0015\u0003\u0019a\u0014N\\5u}\r\u0001A#A\u000b\u0011\u00059\u0001\u0001\"B\f\u0001\t\u0003B\u0012a\u00029feNL7\u000f\u001e\u000b\u00043}A\u0003C\u0001\u000e\u001e\u001b\u0005Y\"\"\u0001\u000f\u0002\u000bM\u001c\u0017\r\\1\n\u0005yY\"\u0001B+oSRDQ\u0001\t\fA\u0002\u0005\nAA\\1nKB\u0011!%\n\b\u00035\rJ!\u0001J\u000e\u0002\rA\u0013X\rZ3g\u0013\t1sE\u0001\u0004TiJLgn\u001a\u0006\u0003ImAQ!\u000b\fA\u0002)\n1a\u001c2k!\tY\u0003'D\u0001-\u0015\tic&\u0001\u0003mC:<'\"A\u0018\u0002\t)\fg/Y\u0005\u0003c1\u0012aa\u00142kK\u000e$\b\"B\u001a\u0001\t\u0003\"\u0014!C;oa\u0016\u00148/[:u)\tIR\u0007C\u0003!e\u0001\u0007\u0011\u0005C\u00038\u0001\u0011\u0005\u0003(\u0001\u0003sK\u0006$WCA\u001dJ)\tQ$\f\u0006\u0002<%B\u0019A\bR$\u000f\u0005u\u0012eB\u0001 B\u001b\u0005y$B\u0001!\u0014\u0003\u0019a$o\\8u}%\tA$\u0003\u0002D7\u00059\u0001/Y2lC\u001e,\u0017BA#G\u0005\r\u0019V-\u001d\u0006\u0003\u0007n\u0001\"\u0001S%\r\u0001\u0011)!J\u000eb\u0001\u0017\n\tA+\u0005\u0002M\u001fB\u0011!$T\u0005\u0003\u001dn\u0011qAT8uQ&tw\r\u0005\u0002\u001b!&\u0011\u0011k\u0007\u0002\u0004\u0003:L\bbB*7\u0003\u0003\u0005\u001d\u0001V\u0001\u000bKZLG-\u001a8dK\u0012\u0012\u0004cA+Y\u000f6\taK\u0003\u0002X7\u00059!/\u001a4mK\u000e$\u0018BA-W\u0005!\u0019E.Y:t)\u0006<\u0007\"\u0002\u00117\u0001\u0004\t\u0003"
)
public class BlackHolePersistenceEngine extends PersistenceEngine {
   public void persist(String name, Object obj) {
   }

   public void unpersist(String name) {
   }

   public <T> Seq<T> read(String name, ClassTag<T> evidence$2) {
      return Nil$.MODULE$;
   }
}
