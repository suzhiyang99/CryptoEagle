package org.apache.spark.security;

import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00052q!\u0001\u0002\u0011\u0002G\u00051BA\u000eHe>,\b/T1qa&twmU3sm&\u001cW\r\u0015:pm&$WM\u001d\u0006\u0003\u0007\u0011\t\u0001b]3dkJLG/\u001f\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011C\u0001\u0001\r!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fM\")1\u0003\u0001D\u0001)\u0005Iq-\u001a;He>,\bo\u001d\u000b\u0003+}\u00012AF\r\u001d\u001d\tiq#\u0003\u0002\u0019\u001d\u00051\u0001K]3eK\u001aL!AG\u000e\u0003\u0007M+GO\u0003\u0002\u0019\u001dA\u0011a#H\u0005\u0003=m\u0011aa\u0015;sS:<\u0007\"\u0002\u0011\u0013\u0001\u0004a\u0012\u0001C;tKJt\u0015-\\3"
)
public interface GroupMappingServiceProvider {
   Set<String> getGroups(String var1);
}
