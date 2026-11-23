package org.apache.spark.storage;

import java.io.InputStream;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u00054A!\u0001\u0002\u0005\u0017\tQ\")\u001e4gKJ\u0014V\r\\3bg&tw-\u00138qkR\u001cFO]3b[*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\n\u000e\u00039Q!a\u0004\t\u0002\u0005%|'\"A\t\u0002\t)\fg/Y\u0005\u0003'9\u00111\"\u00138qkR\u001cFO]3b[\"AQ\u0003\u0001BC\u0002\u0013%a#\u0001\u0005eK2,w-\u0019;f+\u0005a\u0001\u0002\u0003\r\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0007\u0002\u0013\u0011,G.Z4bi\u0016\u0004\u0003\u0002\u0003\u000e\u0001\u0005\u000b\u0007I\u0011B\u000e\u0002\u0011%$XM]1u_J,\u0012\u0001\b\t\u0003;yi\u0011AA\u0005\u0003?\t\u00111d\u00155vM\u001adWM\u00117pG.4U\r^2iKJLE/\u001a:bi>\u0014\b\u0002C\u0011\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u000f\u0002\u0013%$XM]1u_J\u0004\u0003\"B\u0012\u0001\t\u0003!\u0013A\u0002\u001fj]&$h\bF\u0002&M\u001d\u0002\"!\b\u0001\t\u000bU\u0011\u0003\u0019\u0001\u0007\t\u000bi\u0011\u0003\u0019\u0001\u000f\t\r%\u0002\u0001\u0015)\u0003+\u0003\u0019\u0019Gn\\:fIB\u00111FL\u0007\u0002Y)\tQ&A\u0003tG\u0006d\u0017-\u0003\u00020Y\t9!i\\8mK\u0006t\u0007\"B\u0019\u0001\t\u0003\u0012\u0014\u0001\u0002:fC\u0012$\u0012a\r\t\u0003WQJ!!\u000e\u0017\u0003\u0007%sG\u000fC\u00038\u0001\u0011\u0005\u0003(A\u0003dY>\u001cX\rF\u0001:!\tY#(\u0003\u0002<Y\t!QK\\5u\u0011\u0015i\u0004\u0001\"\u00113\u0003%\tg/Y5mC\ndW\rC\u0003@\u0001\u0011\u0005\u0003)\u0001\u0003nCJ\\GCA\u001dB\u0011\u0015\u0011e\b1\u00014\u0003%\u0011X-\u00193mS6LG\u000fC\u0003E\u0001\u0011\u0005S)\u0001\u0003tW&\u0004HC\u0001$J!\tYs)\u0003\u0002IY\t!Aj\u001c8h\u0011\u0015Q5\t1\u0001G\u0003\u0005q\u0007\"\u0002'\u0001\t\u0003j\u0015!D7be.\u001cV\u000f\u001d9peR,G\rF\u0001+\u0011\u0015\t\u0004\u0001\"\u0011P)\t\u0019\u0004\u000bC\u0003R\u001d\u0002\u0007!+A\u0001c!\rY3+V\u0005\u0003)2\u0012Q!\u0011:sCf\u0004\"a\u000b,\n\u0005]c#\u0001\u0002\"zi\u0016DQ!\r\u0001\u0005Be#Ba\r.\\;\")\u0011\u000b\u0017a\u0001%\")A\f\u0017a\u0001g\u0005\u0019qN\u001a4\t\u000byC\u0006\u0019A\u001a\u0002\u00071,g\u000eC\u0003a\u0001\u0011\u0005\u0003(A\u0003sKN,G\u000f"
)
public class BufferReleasingInputStream extends InputStream {
   private final InputStream delegate;
   private final ShuffleBlockFetcherIterator iterator;
   private boolean closed;

   private InputStream delegate() {
      return this.delegate;
   }

   private ShuffleBlockFetcherIterator iterator() {
      return this.iterator;
   }

   public int read() {
      return this.delegate().read();
   }

   public void close() {
      if (!this.closed) {
         this.delegate().close();
         this.iterator().releaseCurrentResultBuffer();
         this.closed = true;
      }

   }

   public int available() {
      return this.delegate().available();
   }

   public void mark(int readlimit) {
      this.delegate().mark(readlimit);
   }

   public long skip(long n) {
      return this.delegate().skip(n);
   }

   public boolean markSupported() {
      return this.delegate().markSupported();
   }

   public int read(byte[] b) {
      return this.delegate().read(b);
   }

   public int read(byte[] b, int off, int len) {
      return this.delegate().read(b, off, len);
   }

   public void reset() {
      this.delegate().reset();
   }

   public BufferReleasingInputStream(InputStream delegate, ShuffleBlockFetcherIterator iterator) {
      this.delegate = delegate;
      this.iterator = iterator;
      this.closed = false;
   }
}
