package org.apache.spark.scheduler;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.spark.annotation.DeveloperApi;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@DeveloperApi
@ScalaSignature(
   bytes = "\u0006\u0001\u0005%c\u0001B\u0001\u0003\u0001-\u0011\u0011b\u00159mSRLeNZ8\u000b\u0005\r!\u0011!C:dQ\u0016$W\u000f\\3s\u0015\t)a!A\u0003ta\u0006\u00148N\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\n\u0003\u00011\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007\u0002C\n\u0001\u0005\u000b\u0007I\u0011\u0001\u000b\u0002!%t\u0007/\u001e;G_Jl\u0017\r^\"mCjTX#A\u000b1\u0005Yy\u0002cA\f\u001b;9\u0011Q\u0002G\u0005\u000339\ta\u0001\u0015:fI\u00164\u0017BA\u000e\u001d\u0005\u0015\u0019E.Y:t\u0015\tIb\u0002\u0005\u0002\u001f?1\u0001A!\u0003\u0011\"\u0003\u0003\u0005\tQ!\u0001(\u0005\ryF%\r\u0005\tE\u0001\u0011\t\u0011)A\u0005G\u0005\t\u0012N\u001c9vi\u001a{'/\\1u\u00072\f'P\u001f\u00111\u0005\u00112\u0003cA\f\u001bKA\u0011aD\n\u0003\nA\u0005\n\t\u0011!A\u0003\u0002\u001d\n\"\u0001K\u0016\u0011\u00055I\u0013B\u0001\u0016\u000f\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"!\u0004\u0017\n\u00055r!aA!os\"Aq\u0006\u0001BC\u0002\u0013\u0005\u0001'\u0001\u0007i_N$Hj\\2bi&|g.F\u00012!\t9\"'\u0003\u000249\t11\u000b\u001e:j]\u001eD\u0001\"\u000e\u0001\u0003\u0002\u0003\u0006I!M\u0001\u000eQ>\u001cH\u000fT8dCRLwN\u001c\u0011\t\u0011]\u0002!Q1A\u0005\u0002A\nA\u0001]1uQ\"A\u0011\b\u0001B\u0001B\u0003%\u0011'A\u0003qCRD\u0007\u0005\u0003\u0005<\u0001\t\u0015\r\u0011\"\u0001=\u0003\u0019aWM\\4uQV\tQ\b\u0005\u0002\u000e}%\u0011qH\u0004\u0002\u0005\u0019>tw\r\u0003\u0005B\u0001\t\u0005\t\u0015!\u0003>\u0003\u001daWM\\4uQ\u0002B\u0001b\u0011\u0001\u0003\u0006\u0004%\t\u0001R\u0001\u0010k:$WM\u001d7zS:<7\u000b\u001d7jiV\t1\u0006\u0003\u0005G\u0001\t\u0005\t\u0015!\u0003,\u0003A)h\u000eZ3sYfLgnZ*qY&$\b\u0005C\u0003I\u0001\u0011\u0005\u0011*\u0001\u0004=S:LGO\u0010\u000b\u0007\u00152\u000b&k\u0015+\u0011\u0005-\u0003Q\"\u0001\u0002\t\u000bM9\u0005\u0019A'1\u00059\u0003\u0006cA\f\u001b\u001fB\u0011a\u0004\u0015\u0003\nA1\u000b\t\u0011!A\u0003\u0002\u001dBQaL$A\u0002EBQaN$A\u0002EBQaO$A\u0002uBQaQ$A\u0002-BQA\u0016\u0001\u0005B]\u000b\u0001\u0002^8TiJLgn\u001a\u000b\u0002c!)\u0011\f\u0001C!5\u0006A\u0001.Y:i\u0007>$W\rF\u0001\\!\tiA,\u0003\u0002^\u001d\t\u0019\u0011J\u001c;\t\u000b}\u0003A\u0011\t1\u0002\r\u0015\fX/\u00197t)\t\tG\r\u0005\u0002\u000eE&\u00111M\u0004\u0002\b\u0005>|G.Z1o\u0011\u0015)g\f1\u0001,\u0003\u0015yG\u000f[3sQ\t\u0001q\r\u0005\u0002iW6\t\u0011N\u0003\u0002k\t\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u00051L'\u0001\u0004#fm\u0016dw\u000e]3s\u0003BLw!\u00028\u0003\u0011\u0003y\u0017!C*qY&$\u0018J\u001c4p!\tY\u0005OB\u0003\u0002\u0005!\u0005\u0011o\u0005\u0002q\u0019!)\u0001\n\u001dC\u0001gR\tq\u000eC\u0003va\u0012\u0005a/A\u0006u_N\u0003H.\u001b;J]\u001a|GcB<\u0002\b\u0005M\u0011Q\u0003\t\u0005q\u0006\u0005!J\u0004\u0002z}:\u0011!0`\u0007\u0002w*\u0011APC\u0001\u0007yI|w\u000e\u001e \n\u0003=I!a \b\u0002\u000fA\f7m[1hK&!\u00111AA\u0003\u0005\r\u0019V-\u001d\u0006\u0003\u007f:Aaa\u0005;A\u0002\u0005%\u0001\u0007BA\u0006\u0003\u001f\u0001Ba\u0006\u000e\u0002\u000eA\u0019a$a\u0004\u0005\u0017\u0005E\u0011qAA\u0001\u0002\u0003\u0015\ta\n\u0002\u0004?\u0012\u0012\u0004\"B\u001cu\u0001\u0004\t\u0004bBA\fi\u0002\u0007\u0011\u0011D\u0001\f[\u0006\u0004(/\u001a3Ta2LG\u000f\u0005\u0003\u0002\u001c\u0005\u0015RBAA\u000f\u0015\u0011\ty\"!\t\u0002\r5\f\u0007O]3e\u0015\r\t\u0019CB\u0001\u0007Q\u0006$wn\u001c9\n\t\u0005\u001d\u0012Q\u0004\u0002\u000b\u0013:\u0004X\u000f^*qY&$\bBB;q\t\u0003\tY\u0003F\u0004x\u0003[\tI$a\u000f\t\u000fM\tI\u00031\u0001\u00020A\"\u0011\u0011GA\u001b!\u00119\"$a\r\u0011\u0007y\t)\u0004B\u0006\u00028\u00055\u0012\u0011!A\u0001\u0006\u00039#aA0%g!1q'!\u000bA\u0002EB\u0001\"!\u0010\u0002*\u0001\u0007\u0011qH\u0001\u000f[\u0006\u0004(/\u001a3vG\u0016\u001c\u0006\u000f\\5u!\u0011\t\t%a\u0012\u000e\u0005\u0005\r#\u0002BA#\u0003C\t\u0011\"\\1qe\u0016$WoY3\n\t\u0005\u001d\u00121\t"
)
public class SplitInfo {
   private final Class<?> inputFormatClazz;
   private final String hostLocation;
   private final String path;
   private final long length;
   private final Object underlyingSplit;

   public static Seq<SplitInfo> toSplitInfo(Class<?> var0, String var1, InputSplit var2) {
      return SplitInfo$.MODULE$.toSplitInfo(var0, var1, var2);
   }

   public static Seq<SplitInfo> toSplitInfo(Class<?> var0, String var1, org.apache.hadoop.mapred.InputSplit var2) {
      return SplitInfo$.MODULE$.toSplitInfo(var0, var1, var2);
   }

   public Class<?> inputFormatClazz() {
      return this.inputFormatClazz;
   }

   public String hostLocation() {
      return this.hostLocation;
   }

   public String path() {
      return this.path;
   }

   public long length() {
      return this.length;
   }

   public Object underlyingSplit() {
      return this.underlyingSplit;
   }

   public String toString() {
      return (new StringBuilder()).append((Object)"SplitInfo ").append((Object)super.toString()).append((Object)" .. inputFormatClazz ").append((Object)this.inputFormatClazz()).append((Object)", hostLocation : ").append((Object)this.hostLocation()).append((Object)", path : ").append((Object)this.path()).append((Object)", length : ").append((Object)BoxesRunTime.boxToLong(this.length())).append((Object)", underlyingSplit ").append(this.underlyingSplit()).toString();
   }

   public int hashCode() {
      int hashCode = this.inputFormatClazz().hashCode();
      hashCode = hashCode * 31 + this.hostLocation().hashCode();
      hashCode = hashCode * 31 + this.path().hashCode();
      hashCode = hashCode * 31 + (int)(this.length() & 2147483647L);
      return hashCode;
   }

   public boolean equals(Object other) {
      boolean var4;
      if (other instanceof SplitInfo) {
         boolean var10;
         label42: {
            label46: {
               SplitInfo var3 = (SplitInfo)other;
               String var10000 = this.hostLocation();
               String var5 = var3.hostLocation();
               if (var10000 == null) {
                  if (var5 != null) {
                     break label46;
                  }
               } else if (!var10000.equals(var5)) {
                  break label46;
               }

               Class var8 = this.inputFormatClazz();
               Class var6 = var3.inputFormatClazz();
               if (var8 == null) {
                  if (var6 != null) {
                     break label46;
                  }
               } else if (!var8.equals(var6)) {
                  break label46;
               }

               String var9 = this.path();
               String var7 = var3.path();
               if (var9 == null) {
                  if (var7 != null) {
                     break label46;
                  }
               } else if (!var9.equals(var7)) {
                  break label46;
               }

               if (this.length() == var3.length() && BoxesRunTime.equals(this.underlyingSplit(), var3.underlyingSplit())) {
                  var10 = true;
                  break label42;
               }
            }

            var10 = false;
         }

         var4 = var10;
      } else {
         var4 = false;
      }

      return var4;
   }

   public SplitInfo(Class<?> inputFormatClazz, String hostLocation, String path, long length, Object underlyingSplit) {
      this.inputFormatClazz = inputFormatClazz;
      this.hostLocation = hostLocation;
      this.path = path;
      this.length = length;
      this.underlyingSplit = underlyingSplit;
   }
}
