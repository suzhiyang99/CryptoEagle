package org.apache.spark.util;

import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001A3Q!\u0001\u0002\u0001\t)\u0011\u0011\u0003U1sK:$8\t\\1tg2{\u0017\rZ3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0012\u001b\u0005i!B\u0001\b\u0010\u0003\u0011a\u0017M\\4\u000b\u0003A\tAA[1wC&\u0011!#\u0004\u0002\f\u00072\f7o\u001d'pC\u0012,'\u000f\u0003\u0005\u0015\u0001\t\u0005\t\u0015!\u0003\f\u0003\u0019\u0001\u0018M]3oi\u000e\u0001\u0001\"B\f\u0001\t\u0003A\u0012A\u0002\u001fj]&$h\b\u0006\u0002\u001a7A\u0011!\u0004A\u0007\u0002\u0005!)AC\u0006a\u0001\u0017!)Q\u0004\u0001C!=\u0005Ia-\u001b8e\u00072\f7o\u001d\u000b\u0003?U\u0002$\u0001\t\u0017\u0011\u0007\u0005:#F\u0004\u0002#K5\t1EC\u0001%\u0003\u0015\u00198-\u00197b\u0013\t13%\u0001\u0004Qe\u0016$WMZ\u0005\u0003Q%\u0012Qa\u00117bgNT!AJ\u0012\u0011\u0005-bC\u0002\u0001\u0003\n[q\t\t\u0011!A\u0003\u00029\u00121a\u0018\u00132#\ty#\u0007\u0005\u0002#a%\u0011\u0011g\t\u0002\b\u001d>$\b.\u001b8h!\t\u00113'\u0003\u00025G\t\u0019\u0011I\\=\t\u000bYb\u0002\u0019A\u001c\u0002\t9\fW.\u001a\t\u0003CaJ!!O\u0015\u0003\rM#(/\u001b8h\u0011\u0015Y\u0004\u0001\"\u0011=\u0003%aw.\u00193DY\u0006\u001c8\u000f\u0006\u0002>\u0005B\u0012a\b\u0011\t\u0004C\u001dz\u0004CA\u0016A\t%\t%(!A\u0001\u0002\u000b\u0005aFA\u0002`IIBQA\u000e\u001eA\u0002]BQa\u000f\u0001\u0005B\u0011#2!\u0012&La\t1\u0005\nE\u0002\"O\u001d\u0003\"a\u000b%\u0005\u0013%\u001b\u0015\u0011!A\u0001\u0006\u0003q#aA0%g!)ag\u0011a\u0001o!)Aj\u0011a\u0001\u001b\u00069!/Z:pYZ,\u0007C\u0001\u0012O\u0013\ty5EA\u0004C_>dW-\u00198"
)
public class ParentClassLoader extends ClassLoader {
   public Class<?> findClass(String name) {
      return super.findClass(name);
   }

   public Class<?> loadClass(String name) {
      return super.loadClass(name);
   }

   public Class<?> loadClass(String name, boolean resolve) {
      return super.loadClass(name, resolve);
   }

   public ParentClassLoader(ClassLoader parent) {
      super(parent);
   }
}
