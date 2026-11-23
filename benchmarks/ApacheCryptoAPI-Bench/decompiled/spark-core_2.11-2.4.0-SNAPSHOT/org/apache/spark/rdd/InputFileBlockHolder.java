package org.apache.spark.rdd;

import org.apache.spark.unsafe.types.UTF8String;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005<a!\u0001\u0002\t\u0002\u0011Q\u0011\u0001F%oaV$h)\u001b7f\u00052|7m\u001b%pY\u0012,'O\u0003\u0002\u0004\t\u0005\u0019!\u000f\u001a3\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!\u0001F%oaV$h)\u001b7f\u00052|7m\u001b%pY\u0012,'o\u0005\u0002\r\u001fA\u0011\u0001cE\u0007\u0002#)\t!#A\u0003tG\u0006d\u0017-\u0003\u0002\u0015#\t1\u0011I\\=SK\u001aDQA\u0006\u0007\u0005\u0002a\ta\u0001P5oSRt4\u0001\u0001\u000b\u0002\u0015\u0019!!\u0004\u0004\u0003\u001c\u0005%1\u0015\u000e\\3CY>\u001c7n\u0005\u0002\u001a\u001f!AQ$\u0007BC\u0002\u0013\u0005a$\u0001\u0005gS2,\u0007+\u0019;i+\u0005y\u0002C\u0001\u0011&\u001b\u0005\t#B\u0001\u0012$\u0003\u0015!\u0018\u0010]3t\u0015\t!C!\u0001\u0004v]N\fg-Z\u0005\u0003M\u0005\u0012!\"\u0016+GqM#(/\u001b8h\u0011!A\u0013D!A!\u0002\u0013y\u0012!\u00034jY\u0016\u0004\u0016\r\u001e5!\u0011!Q\u0013D!b\u0001\n\u0003Y\u0013aC:uCJ$xJ\u001a4tKR,\u0012\u0001\f\t\u0003!5J!AL\t\u0003\t1{gn\u001a\u0005\tae\u0011\t\u0011)A\u0005Y\u0005a1\u000f^1si>3gm]3uA!A!'\u0007BC\u0002\u0013\u00051&\u0001\u0004mK:<G\u000f\u001b\u0005\tie\u0011\t\u0011)A\u0005Y\u00059A.\u001a8hi\"\u0004\u0003\"\u0002\f\u001a\t\u00031D\u0003B\u001c:um\u0002\"\u0001O\r\u000e\u00031AQ!H\u001bA\u0002}AQAK\u001bA\u00021BQAM\u001bA\u00021BQAF\r\u0005\u0002u\"\u0012a\u000e\u0005\u0007\u007f1\u0001\u000b\u0011\u0002!\u0002\u0015%t\u0007/\u001e;CY>\u001c7\u000eE\u0002B\r^j\u0011A\u0011\u0006\u0003\u0007\u0012\u000bA\u0001\\1oO*\tQ)\u0001\u0003kCZ\f\u0017BA$C\u0005YIe\u000e[3sSR\f'\r\\3UQJ,\u0017\r\u001a'pG\u0006d\u0007\"B%\r\t\u0003q\u0012\u0001E4fi&s\u0007/\u001e;GS2,\u0007+\u0019;i\u0011\u0015YE\u0002\"\u0001,\u000399W\r^*uCJ$xJ\u001a4tKRDQ!\u0014\u0007\u0005\u0002-\n\u0011bZ3u\u0019\u0016tw\r\u001e5\t\u000b=cA\u0011\u0001)\u0002\u0007M,G\u000f\u0006\u0003R)rk\u0006C\u0001\tS\u0013\t\u0019\u0016C\u0001\u0003V]&$\b\"B\u000fO\u0001\u0004)\u0006C\u0001,Z\u001d\t\u0001r+\u0003\u0002Y#\u00051\u0001K]3eK\u001aL!AW.\u0003\rM#(/\u001b8h\u0015\tA\u0016\u0003C\u0003+\u001d\u0002\u0007A\u0006C\u00033\u001d\u0002\u0007A\u0006C\u0003`\u0019\u0011\u0005\u0001-A\u0003v]N,G\u000fF\u0001R\u0001"
)
public final class InputFileBlockHolder {
   public static void unset() {
      InputFileBlockHolder$.MODULE$.unset();
   }

   public static void set(String var0, long var1, long var3) {
      InputFileBlockHolder$.MODULE$.set(var0, var1, var3);
   }

   public static long getLength() {
      return InputFileBlockHolder$.MODULE$.getLength();
   }

   public static long getStartOffset() {
      return InputFileBlockHolder$.MODULE$.getStartOffset();
   }

   public static UTF8String getInputFilePath() {
      return InputFileBlockHolder$.MODULE$.getInputFilePath();
   }

   public static class FileBlock {
      private final UTF8String filePath;
      private final long startOffset;
      private final long length;

      public UTF8String filePath() {
         return this.filePath;
      }

      public long startOffset() {
         return this.startOffset;
      }

      public long length() {
         return this.length;
      }

      public FileBlock(UTF8String filePath, long startOffset, long length) {
         this.filePath = filePath;
         this.startOffset = startOffset;
         this.length = length;
      }

      public FileBlock() {
         this(UTF8String.fromString(""), -1L, -1L);
      }
   }
}
