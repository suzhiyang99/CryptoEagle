package org.apache.spark.util;

import java.net.URL;
import java.util.Enumeration;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.JavaConverters$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-4Q!\u0001\u0002\u0001\t)\u0011\u0001d\u00115jY\u00124\u0015N]:u+Jc5\t\\1tg2{\u0017\rZ3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u000e\u001b\u0005\u0011\u0011B\u0001\b\u0003\u0005UiU\u000f^1cY\u0016,&\u000bT\"mCN\u001cHj\\1eKJD\u0001\u0002\u0005\u0001\u0003\u0002\u0003\u0006IAE\u0001\u0005kJd7o\u0001\u0001\u0011\u0007M1\u0002$D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005\u0015\t%O]1z!\tIb$D\u0001\u001b\u0015\tYB$A\u0002oKRT\u0011!H\u0001\u0005U\u00064\u0018-\u0003\u0002 5\t\u0019QK\u0015'\t\u0011\u0005\u0002!\u0011!Q\u0001\n\t\na\u0001]1sK:$\bCA\u0012'\u001b\u0005!#BA\u0013\u001d\u0003\u0011a\u0017M\\4\n\u0005\u001d\"#aC\"mCN\u001cHj\\1eKJDQ!\u000b\u0001\u0005\u0002)\na\u0001P5oSRtDcA\u0016-[A\u0011A\u0002\u0001\u0005\u0006!!\u0002\rA\u0005\u0005\u0006C!\u0002\rA\t\u0005\b_\u0001\u0011\r\u0011\"\u00031\u0003E\u0001\u0018M]3oi\u000ec\u0017m]:M_\u0006$WM]\u000b\u0002cA\u0011ABM\u0005\u0003g\t\u0011\u0011\u0003U1sK:$8\t\\1tg2{\u0017\rZ3s\u0011\u0019)\u0004\u0001)A\u0005c\u0005\u0011\u0002/\u0019:f]R\u001cE.Y:t\u0019>\fG-\u001a:!\u0011\u00159\u0004\u0001\"\u00119\u0003%aw.\u00193DY\u0006\u001c8\u000fF\u0002:\u0019F\u0003$AO\"\u0011\u0007mr\u0014I\u0004\u0002\u0014y%\u0011Q\bF\u0001\u0007!J,G-\u001a4\n\u0005}\u0002%!B\"mCN\u001c(BA\u001f\u0015!\t\u00115\t\u0004\u0001\u0005\u0013\u00113\u0014\u0011!A\u0001\u0006\u0003)%aA0%cE\u0011a)\u0013\t\u0003'\u001dK!\u0001\u0013\u000b\u0003\u000f9{G\u000f[5oOB\u00111CS\u0005\u0003\u0017R\u00111!\u00118z\u0011\u0015ie\u00071\u0001O\u0003\u0011q\u0017-\\3\u0011\u0005mz\u0015B\u0001)A\u0005\u0019\u0019FO]5oO\")!K\u000ea\u0001'\u00069!/Z:pYZ,\u0007CA\nU\u0013\t)FCA\u0004C_>dW-\u00198\t\u000b]\u0003A\u0011\t-\u0002\u0017\u001d,GOU3t_V\u00148-\u001a\u000b\u00031eCQ!\u0014,A\u00029CQa\u0017\u0001\u0005Bq\u000bAbZ3u%\u0016\u001cx.\u001e:dKN$\"!\u00182\u0011\u0007y\u0003\u0007$D\u0001`\u0015\t\u0019A$\u0003\u0002b?\nYQI\\;nKJ\fG/[8o\u0011\u0015i%\f1\u0001O\u0011\u0015!\u0007\u0001\"\u0011f\u0003\u0019\tG\rZ+S\u0019R\u0011a-\u001b\t\u0003'\u001dL!\u0001\u001b\u000b\u0003\tUs\u0017\u000e\u001e\u0005\u0006U\u000e\u0004\r\u0001G\u0001\u0004kJd\u0007"
)
public class ChildFirstURLClassLoader extends MutableURLClassLoader {
   private final ParentClassLoader parentClassLoader;

   private ParentClassLoader parentClassLoader() {
      return this.parentClassLoader;
   }

   public Class<?> loadClass(String name, boolean resolve) {
      Class var10000;
      try {
         var10000 = super.loadClass(name, resolve);
      } catch (ClassNotFoundException var4) {
         var10000 = this.parentClassLoader().loadClass(name, resolve);
      }

      return var10000;
   }

   public URL getResource(String name) {
      URL url = super.findResource(name);
      URL res = url == null ? this.parentClassLoader().getResource(name) : url;
      return res;
   }

   public Enumeration<URL> getResources(String name) {
      Iterator childUrls = (Iterator)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(super.findResources(name)).asScala();
      Iterator parentUrls = (Iterator)JavaConverters$.MODULE$.enumerationAsScalaIteratorConverter(this.parentClassLoader().getResources(name)).asScala();
      return JavaConverters$.MODULE$.<URL>asJavaEnumerationConverter(childUrls.$plus$plus(new Serializable(this, parentUrls) {
         public static final long serialVersionUID = 0L;
         private final Iterator parentUrls$1;

         public final Iterator<URL> apply() {
            return this.parentUrls$1;
         }

         public {
            this.parentUrls$1 = parentUrls$1;
         }
      })).asJavaEnumeration();
   }

   public void addURL(URL url) {
      super.addURL(url);
   }

   public ChildFirstURLClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, (ClassLoader)null);
      this.parentClassLoader = new ParentClassLoader(parent);
   }
}
