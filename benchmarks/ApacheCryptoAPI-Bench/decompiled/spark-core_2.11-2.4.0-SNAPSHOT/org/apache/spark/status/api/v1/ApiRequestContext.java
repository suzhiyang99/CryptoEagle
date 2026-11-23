package org.apache.spark.status.api.v1;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001A3\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005!A\u0004\u0002\u0012\u0003BL'+Z9vKN$8i\u001c8uKb$(BA\u0002\u0005\u0003\t1\u0018G\u0003\u0002\u0006\r\u0005\u0019\u0011\r]5\u000b\u0005\u001dA\u0011AB:uCR,8O\u0003\u0002\n\u0015\u0005)1\u000f]1sW*\u00111\u0002D\u0001\u0007CB\f7\r[3\u000b\u00035\t1a\u001c:h'\t\u0001q\u0002\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\u0005\u0006-\u0001!\t\u0001G\u0001\u0007I%t\u0017\u000e\u001e\u0013\u0004\u0001Q\t\u0011\u0004\u0005\u0002\u00115%\u00111$\u0005\u0002\u0005+:LG\u000fC\u0005\u001e\u0001\u0001\u0007\t\u0019!C\t=\u0005q1/\u001a:wY\u0016$8i\u001c8uKb$X#A\u0010\u0011\u0005\u0001*S\"A\u0011\u000b\u0005\t\u001a\u0013aB:feZdW\r\u001e\u0006\u0002I\u0005)!.\u0019<bq&\u0011a%\t\u0002\u000f'\u0016\u0014h\u000f\\3u\u0007>tG/\u001a=u\u0011%A\u0003\u00011AA\u0002\u0013E\u0011&\u0001\ntKJ4H.\u001a;D_:$X\r\u001f;`I\u0015\fHCA\r+\u0011\u001dYs%!AA\u0002}\t1\u0001\u001f\u00132\u0011\u0019i\u0003\u0001)Q\u0005?\u0005y1/\u001a:wY\u0016$8i\u001c8uKb$\b\u0005\u000b\u0002-_A\u0011\u0001gN\u0007\u0002c)\u0011!gM\u0001\u0005G>\u0014XM\u0003\u00025k\u0005\u0011!o\u001d\u0006\u0003m\r\n!a^:\n\u0005a\n$aB\"p]R,\u0007\u0010\u001e\u0005\nu\u0001\u0001\r\u00111A\u0005\u0012m\n1\u0002\u001b;uaJ+\u0017/^3tiV\tA\b\u0005\u0002>\u00016\taH\u0003\u0002@C\u0005!\u0001\u000e\u001e;q\u0013\t\teH\u0001\nIiR\u00048+\u001a:wY\u0016$(+Z9vKN$\b\"C\"\u0001\u0001\u0004\u0005\r\u0011\"\u0005E\u0003=AG\u000f\u001e9SKF,Xm\u001d;`I\u0015\fHCA\rF\u0011\u001dY#)!AA\u0002qBaa\u0012\u0001!B\u0013a\u0014\u0001\u00045uiB\u0014V-];fgR\u0004\u0003F\u0001$0\u0011\u0015Q\u0005\u0001\"\u0001L\u0003\u0019)\u0018NU8piV\tA\n\u0005\u0002N\u001d6\t!!\u0003\u0002P\u0005\t1Q+\u0013*p_R\u0004"
)
public interface ApiRequestContext {
   ServletContext servletContext();

   @TraitSetter
   void servletContext_$eq(ServletContext var1);

   HttpServletRequest httpRequest();

   @TraitSetter
   void httpRequest_$eq(HttpServletRequest var1);

   UIRoot uiRoot();
}
