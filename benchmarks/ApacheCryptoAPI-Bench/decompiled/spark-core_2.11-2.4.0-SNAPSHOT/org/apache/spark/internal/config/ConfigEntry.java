package org.apache.spark.internal.config;

import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005-dAB\u0001\u0003\u0003\u00031ABA\u0006D_:4\u0017nZ#oiJL(BA\u0002\u0005\u0003\u0019\u0019wN\u001c4jO*\u0011QAB\u0001\tS:$XM\u001d8bY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-\u0006\u0002\u000euM\u0011\u0001A\u0004\t\u0003\u001fIi\u0011\u0001\u0005\u0006\u0002#\u0005)1oY1mC&\u00111\u0003\u0005\u0002\u0007\u0003:L(+\u001a4\t\u0011U\u0001!Q1A\u0005\u0002]\t1a[3z\u0007\u0001)\u0012\u0001\u0007\t\u00033qq!a\u0004\u000e\n\u0005m\u0001\u0012A\u0002)sK\u0012,g-\u0003\u0002\u001e=\t11\u000b\u001e:j]\u001eT!a\u0007\t\t\u0011\u0001\u0002!\u0011!Q\u0001\na\tAa[3zA!A!\u0005\u0001BC\u0002\u0013\u00051%\u0001\u0007bYR,'O\\1uSZ,7/F\u0001%!\r)S\u0006\u0007\b\u0003M-r!a\n\u0016\u000e\u0003!R!!\u000b\f\u0002\rq\u0012xn\u001c;?\u0013\u0005\t\u0012B\u0001\u0017\u0011\u0003\u001d\u0001\u0018mY6bO\u0016L!AL\u0018\u0003\t1K7\u000f\u001e\u0006\u0003YAA\u0001\"\r\u0001\u0003\u0002\u0003\u0006I\u0001J\u0001\u000eC2$XM\u001d8bi&4Xm\u001d\u0011\t\u0011M\u0002!Q1A\u0005\u0002Q\naB^1mk\u0016\u001cuN\u001c<feR,'/F\u00016!\u0011ya\u0007\u0007\u001d\n\u0005]\u0002\"!\u0003$v]\u000e$\u0018n\u001c82!\tI$\b\u0004\u0001\u0005\u000bm\u0002!\u0019\u0001\u001f\u0003\u0003Q\u000b\"!\u0010!\u0011\u0005=q\u0014BA \u0011\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"aD!\n\u0005\t\u0003\"aA!os\"AA\t\u0001B\u0001B\u0003%Q'A\bwC2,XmQ8om\u0016\u0014H/\u001a:!\u0011!1\u0005A!b\u0001\n\u00039\u0015aD:ue&twmQ8om\u0016\u0014H/\u001a:\u0016\u0003!\u0003Ba\u0004\u001c91!A!\n\u0001B\u0001B\u0003%\u0001*\u0001\ttiJLgnZ\"p]Z,'\u000f^3sA!AA\n\u0001BC\u0002\u0013\u0005q#A\u0002e_\u000eD\u0001B\u0014\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0005I>\u001c\u0007\u0005\u0003\u0005Q\u0001\t\u0015\r\u0011\"\u0001R\u0003!I7\u000fU;cY&\u001cW#\u0001*\u0011\u0005=\u0019\u0016B\u0001+\u0011\u0005\u001d\u0011un\u001c7fC:D\u0001B\u0016\u0001\u0003\u0002\u0003\u0006IAU\u0001\nSN\u0004VO\u00197jG\u0002BQ\u0001\u0017\u0001\u0005\u0002e\u000ba\u0001P5oSRtDc\u0002.];z{\u0006-\u0019\t\u00047\u0002AT\"\u0001\u0002\t\u000bU9\u0006\u0019\u0001\r\t\u000b\t:\u0006\u0019\u0001\u0013\t\u000bM:\u0006\u0019A\u001b\t\u000b\u0019;\u0006\u0019\u0001%\t\u000b1;\u0006\u0019\u0001\r\t\u000bA;\u0006\u0019\u0001*\t\u000b\r\u0004a\u0011A\f\u0002%\u0011,g-Y;miZ\u000bG.^3TiJLgn\u001a\u0005\u0006K\u0002!\tBZ\u0001\u000be\u0016\fGm\u0015;sS:<GCA4k!\ry\u0001\u000eG\u0005\u0003SB\u0011aa\u00149uS>t\u0007\"B6e\u0001\u0004a\u0017A\u0002:fC\u0012,'\u000f\u0005\u0002\\[&\u0011aN\u0001\u0002\r\u0007>tg-[4SK\u0006$WM\u001d\u0005\u0006a\u00021\t!]\u0001\te\u0016\fGM\u0012:p[R\u0011\u0001H\u001d\u0005\u0006W>\u0004\r\u0001\u001c\u0005\u0006i\u0002!\t!^\u0001\rI\u00164\u0017-\u001e7u-\u0006dW/Z\u000b\u0002mB\u0019q\u0002\u001b\u001d\t\u000ba\u0004A\u0011I=\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012\u0001G\u0004\u0007w\nA\tA\u0002?\u0002\u0017\r{gNZ5h\u000b:$(/\u001f\t\u00037v4a!\u0001\u0002\t\u0002\u0019q8CA?\u000f\u0011\u0019AV\u0010\"\u0001\u0002\u0002Q\tA\u0010C\u0005\u0002\u0006u\u0014\r\u0011\"\u0001\u0002\b\u0005IQK\u0014#F\r&sU\tR\u000b\u0003\u0003\u0013\u0001B!a\u0003\u0002\u00165\u0011\u0011Q\u0002\u0006\u0005\u0003\u001f\t\t\"\u0001\u0003mC:<'BAA\n\u0003\u0011Q\u0017M^1\n\u0007u\ti\u0001\u0003\u0005\u0002\u001au\u0004\u000b\u0011BA\u0005\u0003))f\nR#G\u0013:+E\t\t\u0005\n\u0003;i(\u0019!C\u0005\u0003?\tAb\u001b8po:\u001cuN\u001c4jON,\"!!\t\u0011\u000f\u0005\r\u0012Q\u0006\r\u000225\u0011\u0011Q\u0005\u0006\u0005\u0003O\tI#\u0001\u0006d_:\u001cWO\u001d:f]RTA!a\u000b\u0002\u0012\u0005!Q\u000f^5m\u0013\u0011\ty#!\n\u0003#\r{gnY;se\u0016tG\u000fS1tQ6\u000b\u0007\u000f\r\u0003\u00024\u0005]\u0002\u0003B.\u0001\u0003k\u00012!OA\u001c\t-\tI$a\u000f\u0002\u0002\u0003\u0005)\u0011\u0001\u001f\u0003\u0007}#\u0013\u0007\u0003\u0005\u0002>u\u0004\u000b\u0011BA\u0011\u00035Ygn\\<o\u0007>tg-[4tA!9\u0011\u0011I?\u0005\u0002\u0005\r\u0013!\u0004:fO&\u001cH/\u001a:F]R\u0014\u0018\u0010\u0006\u0003\u0002F\u0005-\u0003cA\b\u0002H%\u0019\u0011\u0011\n\t\u0003\tUs\u0017\u000e\u001e\u0005\t\u0003\u001b\ny\u00041\u0001\u0002P\u0005)QM\u001c;ssB\"\u0011\u0011KA+!\u0011Y\u0006!a\u0015\u0011\u0007e\n)\u0006B\u0006\u0002X\u0005-\u0013\u0011!A\u0001\u0006\u0003a$aA0%e!9\u00111L?\u0005\u0002\u0005u\u0013!\u00034j]\u0012,e\u000e\u001e:z)\u0011\ty&!\u001b1\t\u0005\u0005\u0014Q\r\t\u00057\u0002\t\u0019\u0007E\u0002:\u0003K\"1\"a\u001a\u0002Z\u0005\u0005\t\u0011!B\u0001y\t\u0019q\fJ\u001a\t\rU\tI\u00061\u0001\u0019\u0001"
)
public abstract class ConfigEntry<T> {
   private final String key;
   private final List<String> alternatives;
   private final Function1<String, T> valueConverter;
   private final Function1<T, String> stringConverter;
   private final String doc;
   private final boolean isPublic;

   public static ConfigEntry<?> findEntry(String var0) {
      return ConfigEntry$.MODULE$.findEntry(var0);
   }

   public static void registerEntry(ConfigEntry<?> var0) {
      ConfigEntry$.MODULE$.registerEntry(var0);
   }

   public static String UNDEFINED() {
      return ConfigEntry$.MODULE$.UNDEFINED();
   }

   public String key() {
      return this.key;
   }

   public List<String> alternatives() {
      return this.alternatives;
   }

   public Function1<String, T> valueConverter() {
      return this.valueConverter;
   }

   public Function1<T, String> stringConverter() {
      return this.stringConverter;
   }

   public String doc() {
      return this.doc;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public abstract String defaultValueString();

   public Option<String> readString(ConfigReader reader) {
      return (Option)this.alternatives().foldLeft(reader.get(this.key()), new Serializable(this, reader) {
         public static final long serialVersionUID = 0L;
         public final ConfigReader reader$1;

         public final Option<String> apply(Option<String> res, String nextKey) {
            return res.<String>orElse(new Serializable(this, nextKey) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final <undefinedtype> $outer;
               private final String nextKey$1;

               public final Option<String> apply() {
                  return this.$outer.reader$1.get(this.nextKey$1);
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                     this.nextKey$1 = nextKey$1;
                  }
               }
            });
         }

         public {
            this.reader$1 = reader$1;
         }
      });
   }

   public abstract T readFrom(ConfigReader var1);

   public Option<T> defaultValue() {
      return None$.MODULE$;
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"ConfigEntry(key=", ", defaultValue=", ", doc=", ", public=", ")"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.key(), this.defaultValueString(), this.doc(), BoxesRunTime.boxToBoolean(this.isPublic())}));
   }

   public ConfigEntry(String key, List<String> alternatives, Function1<String, T> valueConverter, Function1<T, String> stringConverter, String doc, boolean isPublic) {
      this.key = key;
      this.alternatives = alternatives;
      this.valueConverter = valueConverter;
      this.stringConverter = stringConverter;
      this.doc = doc;
      this.isPublic = isPublic;
      ConfigEntry$.MODULE$.registerEntry(this);
   }
}
