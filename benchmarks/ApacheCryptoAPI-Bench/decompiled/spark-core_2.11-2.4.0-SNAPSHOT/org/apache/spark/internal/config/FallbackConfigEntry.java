package org.apache.spark.internal.config;

import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001q3Q!\u0001\u0002\u0001\r1\u00111CR1mY\n\f7m[\"p]\u001aLw-\u00128uefT!a\u0001\u0003\u0002\r\r|gNZ5h\u0015\t)a!\u0001\u0005j]R,'O\\1m\u0015\t9\u0001\"A\u0003ta\u0006\u00148N\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<WCA\u0007\u0015'\t\u0001a\u0002E\u0002\u0010!Ii\u0011AA\u0005\u0003#\t\u00111bQ8oM&<WI\u001c;ssB\u00111\u0003\u0006\u0007\u0001\t\u0015)\u0002A1\u0001\u0018\u0005\u0005!6\u0001A\t\u00031y\u0001\"!\u0007\u000f\u000e\u0003iQ\u0011aG\u0001\u0006g\u000e\fG.Y\u0005\u0003;i\u0011qAT8uQ&tw\r\u0005\u0002\u001a?%\u0011\u0001E\u0007\u0002\u0004\u0003:L\b\"\u0003\u0012\u0001\u0005\u0003\u0005\u000b\u0011B\u0012+\u0003\rYW-\u001f\t\u0003I\u001dr!!G\u0013\n\u0005\u0019R\u0012A\u0002)sK\u0012,g-\u0003\u0002)S\t11\u000b\u001e:j]\u001eT!A\n\u000e\n\u0005\t\u0002\u0002\"\u0003\u0017\u0001\u0005\u0003\u0005\u000b\u0011B\u0017:\u00031\tG\u000e^3s]\u0006$\u0018N^3t!\rqcg\t\b\u0003_Qr!\u0001M\u001a\u000e\u0003ER!A\r\f\u0002\rq\u0012xn\u001c;?\u0013\u0005Y\u0012BA\u001b\u001b\u0003\u001d\u0001\u0018mY6bO\u0016L!a\u000e\u001d\u0003\t1K7\u000f\u001e\u0006\u0003kiI!\u0001\f\t\t\u0013m\u0002!\u0011!Q\u0001\n\rb\u0014a\u00013pG&\u00111\b\u0005\u0005\n}\u0001\u0011\t\u0011)A\u0005\u007f\t\u000b\u0001\"[:Qk\nd\u0017n\u0019\t\u00033\u0001K!!\u0011\u000e\u0003\u000f\t{w\u000e\\3b]&\u0011a\b\u0005\u0005\t\t\u0002\u0011)\u0019!C\u0001\u000b\u0006Aa-\u00197mE\u0006\u001c7.F\u0001\u000f\u0011!9\u0005A!A!\u0002\u0013q\u0011!\u00034bY2\u0014\u0017mY6!\u0011\u0015I\u0005\u0001\"\u0001K\u0003\u0019a\u0014N\\5u}Q11\nT'O\u001fB\u00032a\u0004\u0001\u0013\u0011\u0015\u0011\u0003\n1\u0001$\u0011\u0015a\u0003\n1\u0001.\u0011\u0015Y\u0004\n1\u0001$\u0011\u0015q\u0004\n1\u0001@\u0011\u0015!\u0005\n1\u0001\u000f\u0011\u0015\u0011\u0006\u0001\"\u0011T\u0003I!WMZ1vYR4\u0016\r\\;f'R\u0014\u0018N\\4\u0016\u0003\rBQ!\u0016\u0001\u0005BY\u000b\u0001B]3bI\u001a\u0013x.\u001c\u000b\u0003%]CQ\u0001\u0017+A\u0002e\u000baA]3bI\u0016\u0014\bCA\b[\u0013\tY&A\u0001\u0007D_:4\u0017n\u001a*fC\u0012,'\u000f"
)
public class FallbackConfigEntry<T> extends ConfigEntry<T> {
   private final ConfigEntry<T> fallback;

   public ConfigEntry<T> fallback() {
      return this.fallback;
   }

   public String defaultValueString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"<value of ", ">"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.fallback().key()}));
   }

   public T readFrom(ConfigReader reader) {
      return (T)this.readString(reader).map(this.valueConverter()).getOrElse(new Serializable(this, reader) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final FallbackConfigEntry $outer;
         private final ConfigReader reader$3;

         public final T apply() {
            return (T)this.$outer.fallback().readFrom(this.reader$3);
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.reader$3 = reader$3;
            }
         }
      });
   }

   public FallbackConfigEntry(String key, List<String> alternatives, String doc, boolean isPublic, ConfigEntry<T> fallback) {
      super(key, alternatives, fallback.valueConverter(), fallback.stringConverter(), doc, isPublic);
      this.fallback = fallback;
   }
}
