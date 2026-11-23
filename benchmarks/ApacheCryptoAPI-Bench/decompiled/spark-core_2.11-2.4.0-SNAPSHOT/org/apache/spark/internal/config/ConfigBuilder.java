package org.apache.spark.internal.config;

import java.util.concurrent.TimeUnit;
import org.apache.spark.network.util.ByteUnit;
import scala.Function1;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import scala.collection.immutable.List$;
import scala.collection.immutable.StringOps;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;
import scala.util.matching.Regex;

@ScalaSignature(
   bytes = "\u0006\u0001\t5d!B\u0001\u0003\u0001\u001aa!!D\"p]\u001aLwMQ;jY\u0012,'O\u0003\u0002\u0004\t\u000511m\u001c8gS\u001eT!!\u0002\u0004\u0002\u0011%tG/\u001a:oC2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\n\u0005\u00015\u0019b\u0003\u0005\u0002\u000f#5\tqBC\u0001\u0011\u0003\u0015\u00198-\u00197b\u0013\t\u0011rB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u001dQI!!F\b\u0003\u000fA\u0013x\u000eZ;diB\u0011abF\u0005\u00031=\u0011AbU3sS\u0006d\u0017N_1cY\u0016D\u0001B\u0007\u0001\u0003\u0016\u0004%\t\u0001H\u0001\u0004W\u0016L8\u0001A\u000b\u0002;A\u0011a$\t\b\u0003\u001d}I!\u0001I\b\u0002\rA\u0013X\rZ3g\u0013\t\u00113E\u0001\u0004TiJLgn\u001a\u0006\u0003A=A\u0001\"\n\u0001\u0003\u0012\u0003\u0006I!H\u0001\u0005W\u0016L\b\u0005C\u0003(\u0001\u0011\u0005\u0001&\u0001\u0004=S:LGO\u0010\u000b\u0003S-\u0002\"A\u000b\u0001\u000e\u0003\tAQA\u0007\u0014A\u0002uA\u0001\"\f\u0001A\u0002\u0013\u0005!AL\u0001\b?B,(\r\\5d+\u0005y\u0003C\u0001\b1\u0013\t\ttBA\u0004C_>dW-\u00198\t\u0011M\u0002\u0001\u0019!C\u0001\u0005Q\n1b\u00189vE2L7m\u0018\u0013fcR\u0011Q\u0007\u000f\t\u0003\u001dYJ!aN\b\u0003\tUs\u0017\u000e\u001e\u0005\bsI\n\t\u00111\u00010\u0003\rAH%\r\u0005\u0007w\u0001\u0001\u000b\u0015B\u0018\u0002\u0011}\u0003XO\u00197jG\u0002B\u0001\"\u0010\u0001A\u0002\u0013\u0005!AP\u0001\u0005?\u0012|7-F\u0001@!\t\u0001U)D\u0001B\u0015\t\u00115)\u0001\u0003mC:<'\"\u0001#\u0002\t)\fg/Y\u0005\u0003E\u0005C\u0001b\u0012\u0001A\u0002\u0013\u0005!\u0001S\u0001\t?\u0012|7m\u0018\u0013fcR\u0011Q'\u0013\u0005\bs\u0019\u000b\t\u00111\u0001@\u0011\u0019Y\u0005\u0001)Q\u0005\u007f\u0005)q\fZ8dA!AQ\n\u0001a\u0001\n\u0003\u0011a*A\u0005`_:\u001c%/Z1uKV\tq\nE\u0002\u000f!JK!!U\b\u0003\r=\u0003H/[8o!\u0011q1+V\u001b\n\u0005Q{!!\u0003$v]\u000e$\u0018n\u001c82a\t16\fE\u0002+/fK!\u0001\u0017\u0002\u0003\u0017\r{gNZ5h\u000b:$(/\u001f\t\u00035nc\u0001\u0001B\u0005];\u0006\u0005\t\u0011!B\u0001K\n\u0019q\fJ\u0019\t\ry\u0003\u0001\u0015)\u0003`\u0003)yvN\\\"sK\u0006$X\r\t\t\u0004\u001dA\u0003\u0007\u0003\u0002\bTCV\u0002$A\u00193\u0011\u0007):6\r\u0005\u0002[I\u0012IA,XA\u0001\u0002\u0003\u0015\t!Z\t\u0003M&\u0004\"AD4\n\u0005!|!a\u0002(pi\"Lgn\u001a\t\u0003\u001d)L!a[\b\u0003\u0007\u0005s\u0017\u0010\u0003\u0005n\u0001\u0001\u0007I\u0011\u0001\u0002o\u00035yvN\\\"sK\u0006$Xm\u0018\u0013fcR\u0011Qg\u001c\u0005\bs1\f\t\u00111\u0001q!\rq\u0001+\u001d\t\u0005\u001dM\u0013X\u0007\r\u0002tkB\u0019!f\u0016;\u0011\u0005i+H!\u0003/^\u0003\u0003\u0005\tQ!\u0001f\u0011!9\b\u00011A\u0005\u0002\tA\u0018!D0bYR,'O\\1uSZ,7/F\u0001z!\rQx0H\u0007\u0002w*\u0011A0`\u0001\nS6lW\u000f^1cY\u0016T!A`\b\u0002\u0015\r|G\u000e\\3di&|g.C\u0002\u0002\u0002m\u0014A\u0001T5ti\"Q\u0011Q\u0001\u0001A\u0002\u0013\u0005!!a\u0002\u0002#}\u000bG\u000e^3s]\u0006$\u0018N^3t?\u0012*\u0017\u000fF\u00026\u0003\u0013A\u0001\"OA\u0002\u0003\u0003\u0005\r!\u001f\u0005\b\u0003\u001b\u0001\u0001\u0015)\u0003z\u00039y\u0016\r\u001c;fe:\fG/\u001b<fg\u0002Ba!\u0002\u0001\u0005\u0002\u0005EA#A\u0015\t\u000f\u0005U\u0001\u0001\"\u0001\u0002\u0018\u0005\u0019Am\\2\u0015\u0007%\nI\u0002C\u0004\u0002\u001c\u0005M\u0001\u0019A\u000f\u0002\u0003MDq!a\b\u0001\t\u0003\t\t#\u0001\u0005p]\u000e\u0013X-\u0019;f)\rI\u00131\u0005\u0005\t\u0003K\ti\u00021\u0001\u0002(\u0005A1-\u00197mE\u0006\u001c7\u000eE\u0003\u000f'\u0006%R\u0007\r\u0003\u0002,\u0005=\u0002\u0003\u0002\u0016X\u0003[\u00012AWA\u0018\t-\t\t$a\t\u0002\u0002\u0003\u0005)\u0011A3\u0003\u0007}##\u0007C\u0004\u00026\u0001!\t!a\u000e\u0002\u001f]LG\u000f[!mi\u0016\u0014h.\u0019;jm\u0016$2!KA\u001d\u0011\u0019Q\u00121\u0007a\u0001;!9\u0011Q\b\u0001\u0005\u0002\u0005}\u0012aB5oi\u000e{gNZ\u000b\u0003\u0003\u0003\u0002RAKA\"\u0003\u000fJ1!!\u0012\u0003\u0005I!\u0016\u0010]3e\u0007>tg-[4Ck&dG-\u001a:\u0011\u00079\tI%C\u0002\u0002L=\u00111!\u00138u\u0011\u001d\ty\u0005\u0001C\u0001\u0003#\n\u0001\u0002\\8oO\u000e{gNZ\u000b\u0003\u0003'\u0002RAKA\"\u0003+\u00022ADA,\u0013\r\tIf\u0004\u0002\u0005\u0019>tw\rC\u0004\u0002^\u0001!\t!a\u0018\u0002\u0015\u0011|WO\u00197f\u0007>tg-\u0006\u0002\u0002bA)!&a\u0011\u0002dA\u0019a\"!\u001a\n\u0007\u0005\u001dtB\u0001\u0004E_V\u0014G.\u001a\u0005\b\u0003W\u0002A\u0011AA7\u0003-\u0011wn\u001c7fC:\u001cuN\u001c4\u0016\u0005\u0005=\u0004\u0003\u0002\u0016\u0002D=Bq!a\u001d\u0001\t\u0003\t)(\u0001\u0006tiJLgnZ\"p]\u001a,\"!a\u001e\u0011\t)\n\u0019%\b\u0005\b\u0003w\u0002A\u0011AA?\u0003!!\u0018.\\3D_:4G\u0003BA*\u0003\u007fB\u0001\"!!\u0002z\u0001\u0007\u00111Q\u0001\u0005k:LG\u000f\u0005\u0003\u0002\u0006\u0006=UBAAD\u0015\u0011\tI)a#\u0002\u0015\r|gnY;se\u0016tGOC\u0002\u0002\u000e\u000e\u000bA!\u001e;jY&!\u0011\u0011SAD\u0005!!\u0016.\\3V]&$\bbBAK\u0001\u0011\u0005\u0011qS\u0001\nEf$Xm]\"p]\u001a$B!a\u0015\u0002\u001a\"A\u0011\u0011QAJ\u0001\u0004\tY\n\u0005\u0003\u0002\u001e\u0006\u0015VBAAP\u0015\u0011\ti)!)\u000b\u0007\u0005\rf!A\u0004oKR<xN]6\n\t\u0005\u001d\u0016q\u0014\u0002\t\u0005f$X-\u00168ji\"9\u00111\u0016\u0001\u0005\u0002\u00055\u0016\u0001\u00044bY2\u0014\u0017mY6D_:4W\u0003BAX\u0003k#B!!-\u0002:B!!fVAZ!\rQ\u0016Q\u0017\u0003\b\u0003o\u000bIK1\u0001f\u0005\u0005!\u0006\u0002CA^\u0003S\u0003\r!!-\u0002\u0011\u0019\fG\u000e\u001c2bG.Dq!a0\u0001\t\u0003\t\t-A\u0005sK\u001e,\u0007pQ8oMV\u0011\u00111\u0019\t\u0006U\u0005\r\u0013Q\u0019\t\u0005\u0003\u000f\fy-\u0004\u0002\u0002J*!\u00111ZAg\u0003!i\u0017\r^2iS:<'bAAG\u001f%!\u0011\u0011[Ae\u0005\u0015\u0011VmZ3y\u0011%\t)\u000eAA\u0001\n\u0003\t9.\u0001\u0003d_BLHcA\u0015\u0002Z\"A!$a5\u0011\u0002\u0003\u0007Q\u0004C\u0005\u0002^\u0002\t\n\u0011\"\u0001\u0002`\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nTCAAqU\ri\u00121]\u0016\u0003\u0003K\u0004B!a:\u0002r6\u0011\u0011\u0011\u001e\u0006\u0005\u0003W\fi/A\u0005v]\u000eDWmY6fI*\u0019\u0011q^\b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002t\u0006%(!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"A\u0011q\u001f\u0001\u0002\u0002\u0013\u0005c(A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e\u001f\u0005\n\u0003w\u0004\u0011\u0011!C\u0001\u0003{\fA\u0002\u001d:pIV\u001cG/\u0011:jif,\"!a\u0012\t\u0013\t\u0005\u0001!!A\u0005\u0002\t\r\u0011A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0004S\n\u0015\u0001\"C\u001d\u0002\u0000\u0006\u0005\t\u0019AA$\u0011%\u0011I\u0001AA\u0001\n\u0003\u0012Y!A\bqe>$Wo\u0019;Ji\u0016\u0014\u0018\r^8s+\t\u0011i\u0001E\u0003\u0003\u0010\tE\u0011.D\u0001~\u0013\r\u0011\u0019\" \u0002\t\u0013R,'/\u0019;pe\"I!q\u0003\u0001\u0002\u0002\u0013\u0005!\u0011D\u0001\tG\u0006tW)];bYR\u0019qFa\u0007\t\u0011e\u0012)\"!AA\u0002%D\u0011Ba\b\u0001\u0003\u0003%\tE!\t\u0002\u0011!\f7\u000f[\"pI\u0016$\"!a\u0012\t\u0013\t\u0015\u0002!!A\u0005B\t\u001d\u0012\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003}B\u0011Ba\u000b\u0001\u0003\u0003%\tE!\f\u0002\r\u0015\fX/\u00197t)\ry#q\u0006\u0005\ts\t%\u0012\u0011!a\u0001S\u001eQ!1\u0007\u0002\u0002\u0002#\u0005aA!\u000e\u0002\u001b\r{gNZ5h\u0005VLG\u000eZ3s!\rQ#q\u0007\u0004\n\u0003\t\t\t\u0011#\u0001\u0007\u0005s\u0019RAa\u000e\u0003<Y\u0001bA!\u0010\u0003DuISB\u0001B \u0015\r\u0011\teD\u0001\beVtG/[7f\u0013\u0011\u0011)Ea\u0010\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0017\u0007C\u0004(\u0005o!\tA!\u0013\u0015\u0005\tU\u0002B\u0003B\u0013\u0005o\t\t\u0011\"\u0012\u0003(!Q!q\nB\u001c\u0003\u0003%\tI!\u0015\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0007%\u0012\u0019\u0006\u0003\u0004\u001b\u0005\u001b\u0002\r!\b\u0005\u000b\u0005/\u00129$!A\u0005\u0002\ne\u0013aB;oCB\u0004H.\u001f\u000b\u0005\u00057\u0012i\u0006E\u0002\u000f!vA\u0011Ba\u0018\u0003V\u0005\u0005\t\u0019A\u0015\u0002\u0007a$\u0003\u0007\u0003\u0006\u0003d\t]\u0012\u0011!C\u0005\u0005K\n1B]3bIJ+7o\u001c7wKR\u0011!q\r\t\u0004\u0001\n%\u0014b\u0001B6\u0003\n1qJ\u00196fGR\u0004"
)
public class ConfigBuilder implements Product, Serializable {
   private final String key;
   private boolean _public;
   private String _doc;
   private Option<Function1<ConfigEntry<?>, BoxedUnit>> _onCreate;
   private List<String> _alternatives;

   public static Option<String> unapply(ConfigBuilder var0) {
      return ConfigBuilder$.MODULE$.unapply(var0);
   }

   public static ConfigBuilder apply(String var0) {
      return ConfigBuilder$.MODULE$.apply(var0);
   }

   public static <A> Function1<String, A> andThen(Function1<ConfigBuilder, A> var0) {
      return ConfigBuilder$.MODULE$.andThen(var0);
   }

   public static <A> Function1<A, ConfigBuilder> compose(Function1<A, String> var0) {
      return ConfigBuilder$.MODULE$.compose(var0);
   }

   public String key() {
      return this.key;
   }

   public boolean _public() {
      return this._public;
   }

   public void _public_$eq(boolean x$1) {
      this._public = x$1;
   }

   public String _doc() {
      return this._doc;
   }

   public void _doc_$eq(String x$1) {
      this._doc = x$1;
   }

   public Option<Function1<ConfigEntry<?>, BoxedUnit>> _onCreate() {
      return this._onCreate;
   }

   public void _onCreate_$eq(Option<Function1<ConfigEntry<?>, BoxedUnit>> x$1) {
      this._onCreate = x$1;
   }

   public List<String> _alternatives() {
      return this._alternatives;
   }

   public void _alternatives_$eq(List<String> x$1) {
      this._alternatives = x$1;
   }

   public ConfigBuilder internal() {
      this._public_$eq(false);
      return this;
   }

   public ConfigBuilder doc(String s) {
      this._doc_$eq(s);
      return this;
   }

   public ConfigBuilder onCreate(Function1<ConfigEntry<?>, BoxedUnit> callback) {
      this._onCreate_$eq(Option$.MODULE$.apply(callback));
      return this;
   }

   public ConfigBuilder withAlternative(String key) {
      this._alternatives_$eq((List)this._alternatives().$colon$plus(key, List$.MODULE$.canBuildFrom()));
      return this;
   }

   public TypedConfigBuilder<Object> intConf() {
      return new TypedConfigBuilder<Object>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigBuilder $outer;

         public final int apply(String x$12) {
            return BoxesRunTime.unboxToInt(ConfigHelpers$.MODULE$.toNumber(x$12, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final int apply(String x$13) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$13))).toInt();
               }
            }, this.$outer.key(), "int"));
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

   public TypedConfigBuilder<Object> longConf() {
      return new TypedConfigBuilder<Object>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigBuilder $outer;

         public final long apply(String x$14) {
            return BoxesRunTime.unboxToLong(ConfigHelpers$.MODULE$.toNumber(x$14, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply(String x$15) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$15))).toLong();
               }
            }, this.$outer.key(), "long"));
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

   public TypedConfigBuilder<Object> doubleConf() {
      return new TypedConfigBuilder<Object>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigBuilder $outer;

         public final double apply(String x$16) {
            return BoxesRunTime.unboxToDouble(ConfigHelpers$.MODULE$.toNumber(x$16, new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final double apply(String x$17) {
                  return (new StringOps(Predef$.MODULE$.augmentString(x$17))).toDouble();
               }
            }, this.$outer.key(), "double"));
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

   public TypedConfigBuilder<Object> booleanConf() {
      return new TypedConfigBuilder<Object>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigBuilder $outer;

         public final boolean apply(String x$18) {
            return ConfigHelpers$.MODULE$.toBoolean(x$18, this.$outer.key());
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

   public TypedConfigBuilder<String> stringConf() {
      return new TypedConfigBuilder<String>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String v) {
            return v;
         }
      });
   }

   public TypedConfigBuilder<Object> timeConf(TimeUnit unit) {
      return new TypedConfigBuilder<Object>(this, new Serializable(this, unit) {
         public static final long serialVersionUID = 0L;
         private final TimeUnit unit$1;

         public final long apply(String x$19) {
            return ConfigHelpers$.MODULE$.timeFromString(x$19, this.unit$1);
         }

         public {
            this.unit$1 = unit$1;
         }
      }, new Serializable(this, unit) {
         public static final long serialVersionUID = 0L;
         private final TimeUnit unit$1;

         public final String apply(long x$20) {
            return ConfigHelpers$.MODULE$.timeToString(x$20, this.unit$1);
         }

         public {
            this.unit$1 = unit$1;
         }
      });
   }

   public TypedConfigBuilder<Object> bytesConf(ByteUnit unit) {
      return new TypedConfigBuilder<Object>(this, new Serializable(this, unit) {
         public static final long serialVersionUID = 0L;
         private final ByteUnit unit$2;

         public final long apply(String x$21) {
            return ConfigHelpers$.MODULE$.byteFromString(x$21, this.unit$2);
         }

         public {
            this.unit$2 = unit$2;
         }
      }, new Serializable(this, unit) {
         public static final long serialVersionUID = 0L;
         private final ByteUnit unit$2;

         public final String apply(long x$22) {
            return ConfigHelpers$.MODULE$.byteToString(x$22, this.unit$2);
         }

         public {
            this.unit$2 = unit$2;
         }
      });
   }

   public <T> ConfigEntry<T> fallbackConf(ConfigEntry<T> fallback) {
      FallbackConfigEntry entry = new FallbackConfigEntry(this.key(), this._alternatives(), this._doc(), this._public(), fallback);
      this._onCreate().foreach(new Serializable(this, entry) {
         public static final long serialVersionUID = 0L;
         private final FallbackConfigEntry entry$5;

         public final void apply(Function1<ConfigEntry<?>, BoxedUnit> x$23) {
            x$23.apply(this.entry$5);
         }

         public {
            this.entry$5 = entry$5;
         }
      });
      return entry;
   }

   public TypedConfigBuilder<Regex> regexConf() {
      return new TypedConfigBuilder<Regex>(this, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigBuilder $outer;

         public final Regex apply(String x$24) {
            return ConfigHelpers$.MODULE$.regexFromString(x$24, this.$outer.key());
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }, new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(Regex x$25) {
            return x$25.toString();
         }
      });
   }

   public ConfigBuilder copy(String key) {
      return new ConfigBuilder(key);
   }

   public String copy$default$1() {
      return this.key();
   }

   public String productPrefix() {
      return "ConfigBuilder";
   }

   public int productArity() {
      return 1;
   }

   public Object productElement(int x$1) {
      switch (x$1) {
         case 0:
            return this.key();
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ConfigBuilder;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var7;
      if (this != x$1) {
         label52: {
            boolean var3;
            if (x$1 instanceof ConfigBuilder) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label35: {
                  label34: {
                     ConfigBuilder var4 = (ConfigBuilder)x$1;
                     String var10000 = this.key();
                     String var5 = var4.key();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label34;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label34;
                     }

                     if (var4.canEqual(this)) {
                        var7 = true;
                        break label35;
                     }
                  }

                  var7 = false;
               }

               if (var7) {
                  break label52;
               }
            }

            var7 = false;
            return var7;
         }
      }

      var7 = true;
      return var7;
   }

   public ConfigBuilder(String key) {
      this.key = key;
      Product$class.$init$(this);
      this._public = true;
      this._doc = "";
      this._onCreate = None$.MODULE$;
      this._alternatives = List$.MODULE$.empty();
   }
}
