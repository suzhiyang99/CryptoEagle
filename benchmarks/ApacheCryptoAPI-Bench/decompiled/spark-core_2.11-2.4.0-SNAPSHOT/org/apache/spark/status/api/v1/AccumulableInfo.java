package org.apache.spark.status.api.v1;

import scala.Option;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001}2A!\u0001\u0002\u0001\u001f\ty\u0011iY2v[Vd\u0017M\u00197f\u0013:4wN\u0003\u0002\u0004\t\u0005\u0011a/\r\u0006\u0003\u000b\u0019\t1!\u00199j\u0015\t9\u0001\"\u0001\u0004ti\u0006$Xo\u001d\u0006\u0003\u0013)\tQa\u001d9be.T!a\u0003\u0007\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005i\u0011aA8sO\u000e\u00011C\u0001\u0001\u0011!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fM\"Aq\u0003\u0001BC\u0002\u0013\u0005\u0001$\u0001\u0002jIV\t\u0011\u0004\u0005\u0002\u00125%\u00111D\u0005\u0002\u0005\u0019>tw\r\u0003\u0005\u001e\u0001\t\u0005\t\u0015!\u0003\u001a\u0003\rIG\r\t\u0005\t?\u0001\u0011)\u0019!C\u0001A\u0005!a.Y7f+\u0005\t\u0003C\u0001\u0012&\u001d\t\t2%\u0003\u0002%%\u00051\u0001K]3eK\u001aL!AJ\u0014\u0003\rM#(/\u001b8h\u0015\t!#\u0003\u0003\u0005*\u0001\t\u0005\t\u0015!\u0003\"\u0003\u0015q\u0017-\\3!\u0011!Y\u0003A!b\u0001\n\u0003a\u0013AB;qI\u0006$X-F\u0001.!\r\tb&I\u0005\u0003_I\u0011aa\u00149uS>t\u0007\u0002C\u0019\u0001\u0005\u0003\u0005\u000b\u0011B\u0017\u0002\u000fU\u0004H-\u0019;fA!A1\u0007\u0001BC\u0002\u0013\u0005\u0001%A\u0003wC2,X\r\u0003\u00056\u0001\t\u0005\t\u0015!\u0003\"\u0003\u00191\u0018\r\\;fA!1q\u0007\u0001C\u0001\u0011a\na\u0001P5oSRtD#B\u001d<yur\u0004C\u0001\u001e\u0001\u001b\u0005\u0011\u0001\"B\f7\u0001\u0004I\u0002\"B\u00107\u0001\u0004\t\u0003\"B\u00167\u0001\u0004i\u0003\"B\u001a7\u0001\u0004\t\u0003"
)
public class AccumulableInfo {
   private final long id;
   private final String name;
   private final Option<String> update;
   private final String value;

   public long id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public Option<String> update() {
      return this.update;
   }

   public String value() {
      return this.value;
   }

   public AccumulableInfo(long id, String name, Option<String> update, String value) {
      this.id = id;
      this.name = name;
      this.update = update;
      this.value = value;
   }
}
