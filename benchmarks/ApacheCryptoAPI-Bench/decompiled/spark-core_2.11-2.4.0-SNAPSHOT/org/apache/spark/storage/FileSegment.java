package org.apache.spark.storage;

import java.io.File;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001u2Q!\u0001\u0002\u0001\t)\u00111BR5mKN+w-\\3oi*\u00111\u0001B\u0001\bgR|'/Y4f\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7C\u0001\u0001\f!\taq\"D\u0001\u000e\u0015\u0005q\u0011!B:dC2\f\u0017B\u0001\t\u000e\u0005\u0019\te.\u001f*fM\"A!\u0003\u0001BC\u0002\u0013\u0005A#\u0001\u0003gS2,7\u0001A\u000b\u0002+A\u0011acG\u0007\u0002/)\u0011\u0001$G\u0001\u0003S>T\u0011AG\u0001\u0005U\u00064\u0018-\u0003\u0002\u001d/\t!a)\u001b7f\u0011!q\u0002A!A!\u0002\u0013)\u0012!\u00024jY\u0016\u0004\u0003\u0002\u0003\u0011\u0001\u0005\u000b\u0007I\u0011A\u0011\u0002\r=4gm]3u+\u0005\u0011\u0003C\u0001\u0007$\u0013\t!SB\u0001\u0003M_:<\u0007\u0002\u0003\u0014\u0001\u0005\u0003\u0005\u000b\u0011\u0002\u0012\u0002\u000f=4gm]3uA!A\u0001\u0006\u0001BC\u0002\u0013\u0005\u0011%\u0001\u0004mK:<G\u000f\u001b\u0005\tU\u0001\u0011\t\u0011)A\u0005E\u00059A.\u001a8hi\"\u0004\u0003\"\u0002\u0017\u0001\t\u0003i\u0013A\u0002\u001fj]&$h\b\u0006\u0003/aE\u0012\u0004CA\u0018\u0001\u001b\u0005\u0011\u0001\"\u0002\n,\u0001\u0004)\u0002\"\u0002\u0011,\u0001\u0004\u0011\u0003\"\u0002\u0015,\u0001\u0004\u0011\u0003\"\u0002\u001b\u0001\t\u0003*\u0014\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003Y\u0002\"a\u000e\u001e\u000f\u00051A\u0014BA\u001d\u000e\u0003\u0019\u0001&/\u001a3fM&\u00111\b\u0010\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005ej\u0001"
)
public class FileSegment {
   private final File file;
   private final long offset;
   private final long length;

   public File file() {
      return this.file;
   }

   public long offset() {
      return this.offset;
   }

   public long length() {
      return this.length;
   }

   public String toString() {
      return (new StringOps(Predef$.MODULE$.augmentString("(name=%s, offset=%d, length=%d)"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{this.file().getName(), BoxesRunTime.boxToLong(this.offset()), BoxesRunTime.boxToLong(this.length())}));
   }

   public FileSegment(File file, long offset, long length) {
      this.file = file;
      this.offset = offset;
      this.length = length;
      Predef$.MODULE$.require(offset >= 0L, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileSegment $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File segment offset cannot be negative (got ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.offset())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      Predef$.MODULE$.require(length >= 0L, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FileSegment $outer;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"File segment length cannot be negative (got ", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.$outer.length())}));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }
}
