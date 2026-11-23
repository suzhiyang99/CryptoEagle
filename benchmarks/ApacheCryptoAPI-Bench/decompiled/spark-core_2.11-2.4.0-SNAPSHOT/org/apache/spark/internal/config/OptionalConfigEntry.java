package org.apache.spark.internal.config;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001%4Q!\u0001\u0002\u0001\r1\u00111c\u00149uS>t\u0017\r\\\"p]\u001aLw-\u00128uefT!a\u0001\u0003\u0002\r\r|gNZ5h\u0015\t)a!\u0001\u0005j]R,'O\\1m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<WCA\u0007\u001b'\t\u0001a\u0002E\u0002\u0010!Ii\u0011AA\u0005\u0003#\t\u00111bQ8oM&<WI\u001c;ssB\u00191C\u0006\r\u000e\u0003QQ\u0011!F\u0001\u0006g\u000e\fG.Y\u0005\u0003/Q\u0011aa\u00149uS>t\u0007CA\r\u001b\u0019\u0001!Qa\u0007\u0001C\u0002u\u0011\u0011\u0001V\u0002\u0001#\tq\u0012\u0005\u0005\u0002\u0014?%\u0011\u0001\u0005\u0006\u0002\b\u001d>$\b.\u001b8h!\t\u0019\"%\u0003\u0002$)\t\u0019\u0011I\\=\t\u0013\u0015\u0002!\u0011!Q\u0001\n\u0019j\u0013aA6fsB\u0011qE\u000b\b\u0003'!J!!\u000b\u000b\u0002\rA\u0013X\rZ3g\u0013\tYCF\u0001\u0004TiJLgn\u001a\u0006\u0003SQI!!\n\t\t\u0013=\u0002!\u0011!Q\u0001\nAb\u0014\u0001D1mi\u0016\u0014h.\u0019;jm\u0016\u001c\bcA\u0019:M9\u0011!g\u000e\b\u0003gYj\u0011\u0001\u000e\u0006\u0003kq\ta\u0001\u0010:p_Rt\u0014\"A\u000b\n\u0005a\"\u0012a\u00029bG.\fw-Z\u0005\u0003um\u0012A\u0001T5ti*\u0011\u0001\bF\u0005\u0003_AA\u0001B\u0010\u0001\u0003\u0006\u0004%\taP\u0001\u0012e\u0006<h+\u00197vK\u000e{gN^3si\u0016\u0014X#\u0001!\u0011\tM\te\u0005G\u0005\u0003\u0005R\u0011\u0011BR;oGRLwN\\\u0019\t\u0011\u0011\u0003!\u0011!Q\u0001\n\u0001\u000b!C]1x-\u0006dW/Z\"p]Z,'\u000f^3sA!Aa\t\u0001BC\u0002\u0013\u0005q)\u0001\nsC^\u001cFO]5oO\u000e{gN^3si\u0016\u0014X#\u0001%\u0011\tM\t\u0005D\n\u0005\t\u0015\u0002\u0011\t\u0011)A\u0005\u0011\u0006\u0019\"/Y<TiJLgnZ\"p]Z,'\u000f^3sA!IA\n\u0001B\u0001B\u0003%a%T\u0001\u0004I>\u001c\u0017B\u0001'\u0011\u0011%y\u0005A!A!\u0002\u0013\u00016+\u0001\u0005jgB+(\r\\5d!\t\u0019\u0012+\u0003\u0002S)\t9!i\\8mK\u0006t\u0017BA(\u0011\u0011\u0015)\u0006\u0001\"\u0001W\u0003\u0019a\u0014N\\5u}Q9q\u000bW-[7rk\u0006cA\b\u00011!)Q\u0005\u0016a\u0001M!)q\u0006\u0016a\u0001a!)a\b\u0016a\u0001\u0001\")a\t\u0016a\u0001\u0011\")A\n\u0016a\u0001M!)q\n\u0016a\u0001!\")q\f\u0001C!A\u0006\u0011B-\u001a4bk2$h+\u00197vKN#(/\u001b8h+\u00051\u0003\"\u00022\u0001\t\u0003\u001a\u0017\u0001\u0003:fC\u00124%o\\7\u0015\u0005I!\u0007\"B3b\u0001\u00041\u0017A\u0002:fC\u0012,'\u000f\u0005\u0002\u0010O&\u0011\u0001N\u0001\u0002\r\u0007>tg-[4SK\u0006$WM\u001d"
)
public class OptionalConfigEntry<T> extends ConfigEntry<Option<T>> {
   private final Function1<String, T> rawValueConverter;
   private final Function1<T, String> rawStringConverter;

   public Function1<String, T> rawValueConverter() {
      return this.rawValueConverter;
   }

   public Function1<T, String> rawStringConverter() {
      return this.rawStringConverter;
   }

   public String defaultValueString() {
      return ConfigEntry$.MODULE$.UNDEFINED();
   }

   public Option<T> readFrom(ConfigReader reader) {
      return this.readString(reader).<T>map(this.rawValueConverter());
   }

   public OptionalConfigEntry(String key, List<String> alternatives, Function1<String, T> rawValueConverter, Function1<T, String> rawStringConverter, String doc, boolean isPublic) {
      super(key, alternatives, new Serializable(rawValueConverter) {
         public static final long serialVersionUID = 0L;
         private final Function1 rawValueConverter$1;

         public final Some<T> apply(String s) {
            return new Some<T>(this.rawValueConverter$1.apply(s));
         }

         public {
            this.rawValueConverter$1 = rawValueConverter$1;
         }
      }, new Serializable(rawStringConverter) {
         public static final long serialVersionUID = 0L;
         private final Function1 rawStringConverter$1;

         public final String apply(Option<T> v) {
            return (String)v.map(this.rawStringConverter$1).orNull(Predef$.MODULE$.$conforms());
         }

         public {
            this.rawStringConverter$1 = rawStringConverter$1;
         }
      }, doc, isPublic);
      this.rawValueConverter = rawValueConverter;
      this.rawStringConverter = rawStringConverter;
   }
}
