package org.apache.spark.status.api.v1;

import javax.servlet.ServletContext;
import org.spark_project.jetty.server.handler.ContextHandler;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A;a!\u0001\u0002\t\u0002\tq\u0011\u0001G+J%>|GO\u0012:p[N+'O\u001e7fi\u000e{g\u000e^3yi*\u00111\u0001B\u0001\u0003mFR!!\u0002\u0004\u0002\u0007\u0005\u0004\u0018N\u0003\u0002\b\u0011\u000511\u000f^1ukNT!!\u0003\u0006\u0002\u000bM\u0004\u0018M]6\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'o\u001a\t\u0003\u001fAi\u0011A\u0001\u0004\u0007#\tA\tA\u0001\n\u00031UK%k\\8u\rJ|WnU3sm2,GoQ8oi\u0016DHo\u0005\u0002\u0011'A\u0011AcF\u0007\u0002+)\ta#A\u0003tG\u0006d\u0017-\u0003\u0002\u0019+\t1\u0011I\\=SK\u001aDQA\u0007\t\u0005\u0002q\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u001d!9a\u0004\u0005b\u0001\n\u0013y\u0012!C1uiJL'-\u001e;f+\u0005\u0001\u0003CA\u0011'\u001b\u0005\u0011#BA\u0012%\u0003\u0011a\u0017M\\4\u000b\u0003\u0015\nAA[1wC&\u0011qE\t\u0002\u0007'R\u0014\u0018N\\4\t\r%\u0002\u0002\u0015!\u0003!\u0003)\tG\u000f\u001e:jEV$X\r\t\u0005\u0006WA!\t\u0001L\u0001\ng\u0016$X+\u001b*p_R$2!\f\u0019?!\t!b&\u0003\u00020+\t!QK\\5u\u0011\u0015\t$\u00061\u00013\u00039\u0019wN\u001c;fqRD\u0015M\u001c3mKJ\u0004\"a\r\u001f\u000e\u0003QR!!\u000e\u001c\u0002\u000f!\fg\u000e\u001a7fe*\u0011q\u0007O\u0001\u0007g\u0016\u0014h/\u001a:\u000b\u0005eR\u0014!\u00026fiRL(BA\u001e\r\u0003\u001d)7\r\\5qg\u0016L!!\u0010\u001b\u0003\u001d\r{g\u000e^3yi\"\u000bg\u000e\u001a7fe\")qH\u000ba\u0001\u0001\u00061Q/\u001b*p_R\u0004\"aD!\n\u0005\t\u0013!AB+J%>|G\u000fC\u0003E!\u0011\u0005Q)A\u0005hKR,\u0016NU8piR\u0011\u0001I\u0012\u0005\u0006\u000f\u000e\u0003\r\u0001S\u0001\bG>tG/\u001a=u!\tIe*D\u0001K\u0015\tYE*A\u0004tKJ4H.\u001a;\u000b\u00035\u000bQA[1wCbL!a\u0014&\u0003\u001dM+'O\u001e7fi\u000e{g\u000e^3yi\u0002"
)
public final class UIRootFromServletContext {
   public static UIRoot getUiRoot(ServletContext var0) {
      return UIRootFromServletContext$.MODULE$.getUiRoot(var0);
   }

   public static void setUiRoot(ContextHandler var0, UIRoot var1) {
      UIRootFromServletContext$.MODULE$.setUiRoot(var0, var1);
   }
}
