package org.apache.spark.internal.config;

import scala.Function0;
import scala.Function1;
import scala.Option;
import scala.Some;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000194A!\u0001\u0002\u0005\u001b\tq2i\u001c8gS\u001e,e\u000e\u001e:z/&$\b\u000eR3gCVdGOR;oGRLwN\u001c\u0006\u0003\u0007\u0011\taaY8oM&<'BA\u0003\u0007\u0003!Ig\u000e^3s]\u0006d'BA\u0004\t\u0003\u0015\u0019\b/\u0019:l\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001U\u0011a\"F\n\u0003\u0001=\u00012\u0001E\t\u0014\u001b\u0005\u0011\u0011B\u0001\n\u0003\u0005-\u0019uN\u001c4jO\u0016sGO]=\u0011\u0005Q)B\u0002\u0001\u0003\u0006-\u0001\u0011\ra\u0006\u0002\u0002)F\u0011\u0001D\b\t\u00033qi\u0011A\u0007\u0006\u00027\u0005)1oY1mC&\u0011QD\u0007\u0002\b\u001d>$\b.\u001b8h!\tIr$\u0003\u0002!5\t\u0019\u0011I\\=\t\u0013\t\u0002!\u0011!Q\u0001\n\rR\u0013aA6fsB\u0011Ae\n\b\u00033\u0015J!A\n\u000e\u0002\rA\u0013X\rZ3g\u0013\tA\u0013F\u0001\u0004TiJLgn\u001a\u0006\u0003MiI!AI\t\t\u00131\u0002!\u0011!Q\u0001\n5J\u0014\u0001D1mi\u0016\u0014h.\u0019;jm\u0016\u001c\bc\u0001\u00187G9\u0011q\u0006\u000e\b\u0003aMj\u0011!\r\u0006\u0003e1\ta\u0001\u0010:p_Rt\u0014\"A\u000e\n\u0005UR\u0012a\u00029bG.\fw-Z\u0005\u0003oa\u0012A\u0001T5ti*\u0011QGG\u0005\u0003YEA\u0001b\u000f\u0001\u0003\u0002\u0003\u0006I\u0001P\u0001\u0011?\u0012,g-Y;mi\u001a+hn\u0019;j_:\u00042!G\u001f\u0014\u0013\tq$DA\u0005Gk:\u001cG/[8oa!I\u0001\t\u0001B\u0001B\u0003%\u0011\tR\u0001\u000fm\u0006dW/Z\"p]Z,'\u000f^3s!\u0011I\"iI\n\n\u0005\rS\"!\u0003$v]\u000e$\u0018n\u001c82\u0013\t\u0001\u0015\u0003C\u0005G\u0001\t\u0005\t\u0015!\u0003H\u0011\u0006y1\u000f\u001e:j]\u001e\u001cuN\u001c<feR,'\u000f\u0005\u0003\u001a\u0005N\u0019\u0013B\u0001$\u0012\u0011%Q\u0005A!A!\u0002\u0013\u00193*A\u0002e_\u000eL!AS\t\t\u00135\u0003!\u0011!Q\u0001\n9\u000b\u0016\u0001C5t!V\u0014G.[2\u0011\u0005ey\u0015B\u0001)\u001b\u0005\u001d\u0011un\u001c7fC:L!!T\t\t\u000bM\u0003A\u0011\u0001+\u0002\rqJg.\u001b;?)!)fk\u0016-Z5nc\u0006c\u0001\t\u0001'!)!E\u0015a\u0001G!)AF\u0015a\u0001[!)1H\u0015a\u0001y!)\u0001I\u0015a\u0001\u0003\")aI\u0015a\u0001\u000f\")!J\u0015a\u0001G!)QJ\u0015a\u0001\u001d\")a\f\u0001C!?\u0006aA-\u001a4bk2$h+\u00197vKV\t\u0001\rE\u0002\u001aCNI!A\u0019\u000e\u0003\r=\u0003H/[8o\u0011\u0015!\u0007\u0001\"\u0011f\u0003I!WMZ1vYR4\u0016\r\\;f'R\u0014\u0018N\\4\u0016\u0003\rBQa\u001a\u0001\u0005\u0002!\f\u0001B]3bI\u001a\u0013x.\u001c\u000b\u0003'%DQA\u001b4A\u0002-\faA]3bI\u0016\u0014\bC\u0001\tm\u0013\ti'A\u0001\u0007D_:4\u0017n\u001a*fC\u0012,'\u000f"
)
public class ConfigEntryWithDefaultFunction<T> extends ConfigEntry<T> {
   private final Function0<T> _defaultFunction;

   public Option<T> defaultValue() {
      return new Some<T>(this._defaultFunction.apply());
   }

   public String defaultValueString() {
      return (String)super.stringConverter().apply(this._defaultFunction.apply());
   }

   public T readFrom(ConfigReader reader) {
      return (T)this.readString(reader).map(super.valueConverter()).getOrElse(this._defaultFunction);
   }

   public ConfigEntryWithDefaultFunction(String key, List<String> alternatives, Function0<T> _defaultFunction, Function1<String, T> valueConverter, Function1<T, String> stringConverter, String doc, boolean isPublic) {
      super(key, alternatives, valueConverter, stringConverter, doc, isPublic);
      this._defaultFunction = _defaultFunction;
   }
}
