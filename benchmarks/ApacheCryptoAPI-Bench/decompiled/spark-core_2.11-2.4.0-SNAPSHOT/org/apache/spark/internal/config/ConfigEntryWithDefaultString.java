package org.apache.spark.internal.config;

import scala.Function1;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-4A!\u0001\u0002\u0005\u001b\ta2i\u001c8gS\u001e,e\u000e\u001e:z/&$\b\u000eR3gCVdGo\u0015;sS:<'BA\u0002\u0005\u0003\u0019\u0019wN\u001c4jO*\u0011QAB\u0001\tS:$XM\u001d8bY*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0016\u00059)2C\u0001\u0001\u0010!\r\u0001\u0012cE\u0007\u0002\u0005%\u0011!C\u0001\u0002\f\u0007>tg-[4F]R\u0014\u0018\u0010\u0005\u0002\u0015+1\u0001A!\u0002\f\u0001\u0005\u00049\"!\u0001+\u0012\u0005aq\u0002CA\r\u001d\u001b\u0005Q\"\"A\u000e\u0002\u000bM\u001c\u0017\r\\1\n\u0005uQ\"a\u0002(pi\"Lgn\u001a\t\u00033}I!\u0001\t\u000e\u0003\u0007\u0005s\u0017\u0010C\u0005#\u0001\t\u0005\t\u0015!\u0003$U\u0005\u00191.Z=\u0011\u0005\u0011:cBA\r&\u0013\t1#$\u0001\u0004Qe\u0016$WMZ\u0005\u0003Q%\u0012aa\u0015;sS:<'B\u0001\u0014\u001b\u0013\t\u0011\u0013\u0003C\u0005-\u0001\t\u0005\t\u0015!\u0003.s\u0005a\u0011\r\u001c;fe:\fG/\u001b<fgB\u0019aFN\u0012\u000f\u0005=\"dB\u0001\u00194\u001b\u0005\t$B\u0001\u001a\r\u0003\u0019a$o\\8u}%\t1$\u0003\u000265\u00059\u0001/Y2lC\u001e,\u0017BA\u001c9\u0005\u0011a\u0015n\u001d;\u000b\u0005UR\u0012B\u0001\u0017\u0012\u0011!Y\u0004A!A!\u0002\u0013\u0019\u0013!D0eK\u001a\fW\u000f\u001c;WC2,X\rC\u0005>\u0001\t\u0005\t\u0015!\u0003?\u0003\u0006qa/\u00197vK\u000e{gN^3si\u0016\u0014\b\u0003B\r@GMI!\u0001\u0011\u000e\u0003\u0013\u0019+hn\u0019;j_:\f\u0014BA\u001f\u0012\u0011%\u0019\u0005A!A!\u0002\u0013!U)A\btiJLgnZ\"p]Z,'\u000f^3s!\u0011IrhE\u0012\n\u0005\r\u000b\u0002\"C$\u0001\u0005\u0003\u0005\u000b\u0011B\u0012I\u0003\r!wnY\u0005\u0003\u000fFA\u0011B\u0013\u0001\u0003\u0002\u0003\u0006Ia\u0013(\u0002\u0011%\u001c\b+\u001e2mS\u000e\u0004\"!\u0007'\n\u00055S\"a\u0002\"p_2,\u0017M\\\u0005\u0003\u0015FAQ\u0001\u0015\u0001\u0005\u0002E\u000ba\u0001P5oSRtD\u0003\u0003*T)V3v\u000bW-\u0011\u0007A\u00011\u0003C\u0003#\u001f\u0002\u00071\u0005C\u0003-\u001f\u0002\u0007Q\u0006C\u0003<\u001f\u0002\u00071\u0005C\u0003>\u001f\u0002\u0007a\bC\u0003D\u001f\u0002\u0007A\tC\u0003H\u001f\u0002\u00071\u0005C\u0003K\u001f\u0002\u00071\nC\u0003\\\u0001\u0011\u0005C,\u0001\u0007eK\u001a\fW\u000f\u001c;WC2,X-F\u0001^!\rIblE\u0005\u0003?j\u0011aa\u00149uS>t\u0007\"B1\u0001\t\u0003\u0012\u0017A\u00053fM\u0006,H\u000e\u001e,bYV,7\u000b\u001e:j]\u001e,\u0012a\t\u0005\u0006I\u0002!\t!Z\u0001\te\u0016\fGM\u0012:p[R\u00111C\u001a\u0005\u0006O\u000e\u0004\r\u0001[\u0001\u0007e\u0016\fG-\u001a:\u0011\u0005AI\u0017B\u00016\u0003\u00051\u0019uN\u001c4jOJ+\u0017\rZ3s\u0001"
)
public class ConfigEntryWithDefaultString<T> extends ConfigEntry<T> {
   public final String org$apache$spark$internal$config$ConfigEntryWithDefaultString$$_defaultValue;

   public Option<T> defaultValue() {
      return new Some<T>(super.valueConverter().apply(this.org$apache$spark$internal$config$ConfigEntryWithDefaultString$$_defaultValue));
   }

   public String defaultValueString() {
      return this.org$apache$spark$internal$config$ConfigEntryWithDefaultString$$_defaultValue;
   }

   public T readFrom(ConfigReader reader) {
      String value = (String)this.readString(reader).getOrElse(new Serializable(this, reader) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigEntryWithDefaultString $outer;
         private final ConfigReader reader$2;

         public final String apply() {
            return this.reader$2.substitute(this.$outer.org$apache$spark$internal$config$ConfigEntryWithDefaultString$$_defaultValue);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.reader$2 = reader$2;
            }
         }
      });
      return (T)super.valueConverter().apply(value);
   }

   public ConfigEntryWithDefaultString(String key, List<String> alternatives, String _defaultValue, Function1<String, T> valueConverter, Function1<T, String> stringConverter, String doc, boolean isPublic) {
      super(key, alternatives, valueConverter, stringConverter, doc, isPublic);
      this.org$apache$spark$internal$config$ConfigEntryWithDefaultString$$_defaultValue = _defaultValue;
   }
}
