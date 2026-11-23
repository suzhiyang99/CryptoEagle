package org.apache.spark.util;

import java.net.URL;
import java.net.URLClassLoader;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001e2Q!\u0001\u0002\u0001\t)\u0011Q#T;uC\ndW-\u0016*M\u00072\f7o\u001d'pC\u0012,'O\u0003\u0002\u0004\t\u0005!Q\u000f^5m\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\ta\u0011#D\u0001\u000e\u0015\tqq\"A\u0002oKRT\u0011\u0001E\u0001\u0005U\u00064\u0018-\u0003\u0002\u0013\u001b\tqQK\u0015'DY\u0006\u001c8\u000fT8bI\u0016\u0014\b\u0002\u0003\u000b\u0001\u0005\u0003\u0005\u000b\u0011\u0002\f\u0002\tU\u0014Hn]\u0002\u0001!\r9\"\u0004H\u0007\u00021)\t\u0011$A\u0003tG\u0006d\u0017-\u0003\u0002\u001c1\t)\u0011I\u001d:bsB\u0011A\"H\u0005\u0003=5\u00111!\u0016*M\u0011!\u0001\u0003A!A!\u0002\u0013\t\u0013A\u00029be\u0016tG\u000f\u0005\u0002#K5\t1E\u0003\u0002%\u001f\u0005!A.\u00198h\u0013\t13EA\u0006DY\u0006\u001c8\u000fT8bI\u0016\u0014\b\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\bF\u0002+Y5\u0002\"a\u000b\u0001\u000e\u0003\tAQ\u0001F\u0014A\u0002YAQ\u0001I\u0014A\u0002\u0005BQa\f\u0001\u0005BA\na!\u00193e+JcECA\u00195!\t9\"'\u0003\u000241\t!QK\\5u\u0011\u0015)d\u00061\u0001\u001d\u0003\r)(\u000f\u001c\u0005\u0006o\u0001!\t\u0005O\u0001\bO\u0016$XK\u0015't)\u00051\u0002"
)
public class MutableURLClassLoader extends URLClassLoader {
   public void addURL(URL url) {
      super.addURL(url);
   }

   public URL[] getURLs() {
      return super.getURLs();
   }

   public MutableURLClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
   }
}
