package org.apache.spark.internal.config;

import scala.Function0;
import scala.Function1;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.immutable.Set;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005ea!B\u0001\u0003\u0001\u0019a!A\u0005+za\u0016$7i\u001c8gS\u001e\u0014U/\u001b7eKJT!a\u0001\u0003\u0002\r\r|gNZ5h\u0015\t)a!\u0001\u0005j]R,'O\\1m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<WCA\u0007.'\t\u0001a\u0002\u0005\u0002\u0010%5\t\u0001CC\u0001\u0012\u0003\u0015\u00198-\u00197b\u0013\t\u0019\u0002C\u0001\u0004B]f\u0014VM\u001a\u0005\t+\u0001\u0011)\u0019!C\u0001/\u00051\u0001/\u0019:f]R\u001c\u0001!F\u0001\u0019!\tI\"$D\u0001\u0003\u0013\tY\"AA\u0007D_:4\u0017n\u001a\"vS2$WM\u001d\u0005\t;\u0001\u0011\t\u0011)A\u00051\u00059\u0001/\u0019:f]R\u0004\u0003\u0002C\u0010\u0001\u0005\u000b\u0007I\u0011\u0001\u0011\u0002\u0013\r|gN^3si\u0016\u0014X#A\u0011\u0011\t=\u0011CeK\u0005\u0003GA\u0011\u0011BR;oGRLwN\\\u0019\u0011\u0005\u0015BcBA\b'\u0013\t9\u0003#\u0001\u0004Qe\u0016$WMZ\u0005\u0003S)\u0012aa\u0015;sS:<'BA\u0014\u0011!\taS\u0006\u0004\u0001\u0005\u000b9\u0002!\u0019A\u0018\u0003\u0003Q\u000b\"\u0001M\u001a\u0011\u0005=\t\u0014B\u0001\u001a\u0011\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"a\u0004\u001b\n\u0005U\u0002\"aA!os\"Aq\u0007\u0001B\u0001B\u0003%\u0011%\u0001\u0006d_:4XM\u001d;fe\u0002B\u0001\"\u000f\u0001\u0003\u0006\u0004%\tAO\u0001\u0010gR\u0014\u0018N\\4D_:4XM\u001d;feV\t1\b\u0005\u0003\u0010E-\"\u0003\u0002C\u001f\u0001\u0005\u0003\u0005\u000b\u0011B\u001e\u0002!M$(/\u001b8h\u0007>tg/\u001a:uKJ\u0004\u0003\"B \u0001\t\u0003\u0001\u0015A\u0002\u001fj]&$h\b\u0006\u0003B\u0005\u000e#\u0005cA\r\u0001W!)QC\u0010a\u00011!)qD\u0010a\u0001C!)\u0011H\u0010a\u0001w!)q\b\u0001C\u0001\rR\u0019\u0011i\u0012%\t\u000bU)\u0005\u0019\u0001\r\t\u000b})\u0005\u0019A\u0011\t\u000b)\u0003A\u0011A&\u0002\u0013Q\u0014\u0018M\\:g_JlGCA!M\u0011\u0015i\u0015\n1\u0001O\u0003\t1g\u000e\u0005\u0003\u0010E-Z\u0003\"\u0002)\u0001\t\u0003\t\u0016AC2iK\u000e\\g+\u00197vKR\u0019\u0011I\u0015-\t\u000bM{\u0005\u0019\u0001+\u0002\u0013Y\fG.\u001b3bi>\u0014\b\u0003B\b#WU\u0003\"a\u0004,\n\u0005]\u0003\"a\u0002\"p_2,\u0017M\u001c\u0005\u00063>\u0003\r\u0001J\u0001\tKJ\u0014xN]'tO\")1\f\u0001C\u00019\u0006Y1\r[3dWZ\u000bG.^3t)\t\tU\fC\u0003_5\u0002\u0007q,A\u0006wC2LGMV1mk\u0016\u001c\bcA\u0013aW%\u0011\u0011M\u000b\u0002\u0004'\u0016$\b\"B2\u0001\t\u0003!\u0017A\u0003;p'\u0016\fX/\u001a8dKV\tQ\rE\u0002\u001a\u0001\u0019\u00042aZ8,\u001d\tAWN\u0004\u0002jY6\t!N\u0003\u0002l-\u00051AH]8pizJ\u0011!E\u0005\u0003]B\tq\u0001]1dW\u0006<W-\u0003\u0002qc\n\u00191+Z9\u000b\u00059\u0004\u0002\"B:\u0001\t\u0003!\u0018AD2sK\u0006$Xm\u00149uS>t\u0017\r\\\u000b\u0002kB\u0019\u0011D^\u0016\n\u0005]\u0014!aE(qi&|g.\u00197D_:4\u0017nZ#oiJL\b\"B=\u0001\t\u0003Q\u0018!E2sK\u0006$XmV5uQ\u0012+g-Y;miR\u00111P \t\u00043q\\\u0013BA?\u0003\u0005-\u0019uN\u001c4jO\u0016sGO]=\t\u000b}D\b\u0019A\u0016\u0002\u000f\u0011,g-Y;mi\"9\u00111\u0001\u0001\u0005\u0002\u0005\u0015\u0011!G2sK\u0006$XmV5uQ\u0012+g-Y;mi\u001a+hn\u0019;j_:$2a_A\u0004\u0011!\tI!!\u0001A\u0002\u0005-\u0011a\u00033fM\u0006,H\u000e\u001e$v]\u000e\u0004BaDA\u0007W%\u0019\u0011q\u0002\t\u0003\u0013\u0019+hn\u0019;j_:\u0004\u0004bBA\n\u0001\u0011\u0005\u0011QC\u0001\u0018GJ,\u0017\r^3XSRDG)\u001a4bk2$8\u000b\u001e:j]\u001e$2a_A\f\u0011\u0019y\u0018\u0011\u0003a\u0001I\u0001"
)
public class TypedConfigBuilder<T> {
   private final ConfigBuilder parent;
   private final Function1<String, T> converter;
   private final Function1<T, String> stringConverter;

   public ConfigBuilder parent() {
      return this.parent;
   }

   public Function1<String, T> converter() {
      return this.converter;
   }

   public Function1<T, String> stringConverter() {
      return this.stringConverter;
   }

   public TypedConfigBuilder<T> transform(Function1<T, T> fn) {
      return new TypedConfigBuilder<T>(this.parent(), new Serializable(this, fn) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TypedConfigBuilder $outer;
         private final Function1 fn$1;

         public final T apply(String s) {
            return (T)this.fn$1.apply(this.$outer.converter().apply(s));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.fn$1 = fn$1;
            }
         }
      }, this.stringConverter());
   }

   public TypedConfigBuilder<T> checkValue(Function1<T, Object> validator, String errorMsg) {
      return this.transform(new Serializable(this, validator, errorMsg) {
         public static final long serialVersionUID = 0L;
         private final Function1 validator$1;
         private final String errorMsg$1;

         public final T apply(T v) {
            if (BoxesRunTime.unboxToBoolean(this.validator$1.apply(v))) {
               return (T)v;
            } else {
               throw new IllegalArgumentException(this.errorMsg$1);
            }
         }

         public {
            this.validator$1 = validator$1;
            this.errorMsg$1 = errorMsg$1;
         }
      });
   }

   public TypedConfigBuilder<T> checkValues(Set<T> validValues) {
      return this.transform(new Serializable(this, validValues) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TypedConfigBuilder $outer;
         private final Set validValues$1;

         public final T apply(T v) {
            if (this.validValues$1.contains(v)) {
               return (T)v;
            } else {
               throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"The value of ", " should be one of ", ", but was ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.parent().key(), this.validValues$1.mkString(", "), v})));
            }
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.validValues$1 = validValues$1;
            }
         }
      });
   }

   public TypedConfigBuilder<Seq<T>> toSequence() {
      return new TypedConfigBuilder<Seq<T>>(this.parent(), new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final TypedConfigBuilder $outer;

         public final Seq<T> apply(String x$6) {
            return ConfigHelpers$.MODULE$.<T>stringToSeq(x$6, this.$outer.converter());
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
         // $FF: synthetic field
         private final TypedConfigBuilder $outer;

         public final String apply(Seq<T> x$7) {
            return ConfigHelpers$.MODULE$.seqToString(x$7, this.$outer.stringConverter());
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

   public OptionalConfigEntry<T> createOptional() {
      OptionalConfigEntry entry = new OptionalConfigEntry(this.parent().key(), this.parent()._alternatives(), this.converter(), this.stringConverter(), this.parent()._doc(), this.parent()._public());
      this.parent()._onCreate().foreach(new Serializable(this, entry) {
         public static final long serialVersionUID = 0L;
         private final OptionalConfigEntry entry$1;

         public final void apply(Function1<ConfigEntry<?>, BoxedUnit> x$8) {
            x$8.apply(this.entry$1);
         }

         public {
            this.entry$1 = entry$1;
         }
      });
      return entry;
   }

   public ConfigEntry<T> createWithDefault(T var1) {
      Object var10000;
      if (var1 instanceof String) {
         var10000 = this.createWithDefaultString((String)var1);
      } else {
         Object transformedDefault = this.converter().apply(this.stringConverter().apply(var1));
         ConfigEntryWithDefault entry = new ConfigEntryWithDefault(this.parent().key(), this.parent()._alternatives(), transformedDefault, this.converter(), this.stringConverter(), this.parent()._doc(), this.parent()._public());
         this.parent()._onCreate().foreach(new Serializable(this, entry) {
            public static final long serialVersionUID = 0L;
            private final ConfigEntryWithDefault entry$2;

            public final void apply(Function1<ConfigEntry<?>, BoxedUnit> x$9) {
               x$9.apply(this.entry$2);
            }

            public {
               this.entry$2 = entry$2;
            }
         });
         var10000 = entry;
      }

      return (ConfigEntry<T>)var10000;
   }

   public ConfigEntry<T> createWithDefaultFunction(Function0<T> defaultFunc) {
      ConfigEntryWithDefaultFunction entry = new ConfigEntryWithDefaultFunction(this.parent().key(), this.parent()._alternatives(), defaultFunc, this.converter(), this.stringConverter(), this.parent()._doc(), this.parent()._public());
      this.parent()._onCreate().foreach(new Serializable(this, entry) {
         public static final long serialVersionUID = 0L;
         private final ConfigEntryWithDefaultFunction entry$3;

         public final void apply(Function1<ConfigEntry<?>, BoxedUnit> x$10) {
            x$10.apply(this.entry$3);
         }

         public {
            this.entry$3 = entry$3;
         }
      });
      return entry;
   }

   public ConfigEntry<T> createWithDefaultString(String var1) {
      ConfigEntryWithDefaultString entry = new ConfigEntryWithDefaultString(this.parent().key(), this.parent()._alternatives(), var1, this.converter(), this.stringConverter(), this.parent()._doc(), this.parent()._public());
      this.parent()._onCreate().foreach(new Serializable(this, entry) {
         public static final long serialVersionUID = 0L;
         private final ConfigEntryWithDefaultString entry$4;

         public final void apply(Function1<ConfigEntry<?>, BoxedUnit> x$11) {
            x$11.apply(this.entry$4);
         }

         public {
            this.entry$4 = entry$4;
         }
      });
      return entry;
   }

   public TypedConfigBuilder(ConfigBuilder parent, Function1<String, T> converter, Function1<T, String> stringConverter) {
      this.parent = parent;
      this.converter = converter;
      this.stringConverter = stringConverter;
   }

   public TypedConfigBuilder(ConfigBuilder parent, Function1<String, T> converter) {
      this(parent, converter, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(T x$4) {
            return (String)Option$.MODULE$.apply(x$4).map(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply(T x$5) {
                  return x$5.toString();
               }
            }).orNull(Predef$.MODULE$.$conforms());
         }
      });
   }
}
