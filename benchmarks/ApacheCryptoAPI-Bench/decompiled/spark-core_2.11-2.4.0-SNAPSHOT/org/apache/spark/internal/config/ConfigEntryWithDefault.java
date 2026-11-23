package org.apache.spark.internal.config;

import scala.Function1;
import scala.Option;
import scala.Serializable;
import scala.Some;
import scala.collection.immutable.List;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001-4A!\u0001\u0002\u0005\u001b\t12i\u001c8gS\u001e,e\u000e\u001e:z/&$\b\u000eR3gCVdGO\u0003\u0002\u0004\t\u000511m\u001c8gS\u001eT!!\u0002\u0004\u0002\u0011%tG/\u001a:oC2T!a\u0002\u0005\u0002\u000bM\u0004\u0018M]6\u000b\u0005%Q\u0011AB1qC\u000eDWMC\u0001\f\u0003\ry'oZ\u0002\u0001+\tqQc\u0005\u0002\u0001\u001fA\u0019\u0001#E\n\u000e\u0003\tI!A\u0005\u0002\u0003\u0017\r{gNZ5h\u000b:$(/\u001f\t\u0003)Ua\u0001\u0001B\u0003\u0017\u0001\t\u0007qCA\u0001U#\tAb\u0004\u0005\u0002\u001a95\t!DC\u0001\u001c\u0003\u0015\u00198-\u00197b\u0013\ti\"DA\u0004O_RD\u0017N\\4\u0011\u0005ey\u0012B\u0001\u0011\u001b\u0005\r\te.\u001f\u0005\nE\u0001\u0011\t\u0011)A\u0005G)\n1a[3z!\t!sE\u0004\u0002\u001aK%\u0011aEG\u0001\u0007!J,G-\u001a4\n\u0005!J#AB*ue&twM\u0003\u0002'5%\u0011!%\u0005\u0005\nY\u0001\u0011\t\u0011)A\u0005[e\nA\"\u00197uKJt\u0017\r^5wKN\u00042A\f\u001c$\u001d\tyCG\u0004\u00021g5\t\u0011G\u0003\u00023\u0019\u00051AH]8pizJ\u0011aG\u0005\u0003ki\tq\u0001]1dW\u0006<W-\u0003\u00028q\t!A*[:u\u0015\t)$$\u0003\u0002-#!A1\b\u0001B\u0001B\u0003%1#A\u0007`I\u00164\u0017-\u001e7u-\u0006dW/\u001a\u0005\n{\u0001\u0011\t\u0011)A\u0005}\u0005\u000baB^1mk\u0016\u001cuN\u001c<feR,'\u000f\u0005\u0003\u001a\u007f\r\u001a\u0012B\u0001!\u001b\u0005%1UO\\2uS>t\u0017'\u0003\u0002>#!I1\t\u0001B\u0001B\u0003%A)R\u0001\u0010gR\u0014\u0018N\\4D_:4XM\u001d;feB!\u0011dP\n$\u0013\t\u0019\u0015\u0003C\u0005H\u0001\t\u0005\t\u0015!\u0003$\u0011\u0006\u0019Am\\2\n\u0005\u001d\u000b\u0002\"\u0003&\u0001\u0005\u0003\u0005\u000b\u0011B&O\u0003!I7\u000fU;cY&\u001c\u0007CA\rM\u0013\ti%DA\u0004C_>dW-\u00198\n\u0005)\u000b\u0002\"\u0002)\u0001\t\u0003\t\u0016A\u0002\u001fj]&$h\b\u0006\u0005S'R+fk\u0016-Z!\r\u0001\u0002a\u0005\u0005\u0006E=\u0003\ra\t\u0005\u0006Y=\u0003\r!\f\u0005\u0006w=\u0003\ra\u0005\u0005\u0006{=\u0003\rA\u0010\u0005\u0006\u0007>\u0003\r\u0001\u0012\u0005\u0006\u000f>\u0003\ra\t\u0005\u0006\u0015>\u0003\ra\u0013\u0005\u00067\u0002!\t\u0005X\u0001\rI\u00164\u0017-\u001e7u-\u0006dW/Z\u000b\u0002;B\u0019\u0011DX\n\n\u0005}S\"AB(qi&|g\u000eC\u0003b\u0001\u0011\u0005#-\u0001\neK\u001a\fW\u000f\u001c;WC2,Xm\u0015;sS:<W#A\u0012\t\u000b\u0011\u0004A\u0011A3\u0002\u0011I,\u0017\r\u001a$s_6$\"a\u00054\t\u000b\u001d\u001c\u0007\u0019\u00015\u0002\rI,\u0017\rZ3s!\t\u0001\u0012.\u0003\u0002k\u0005\ta1i\u001c8gS\u001e\u0014V-\u00193fe\u0002"
)
public class ConfigEntryWithDefault<T> extends ConfigEntry<T> {
   public final T org$apache$spark$internal$config$ConfigEntryWithDefault$$_defaultValue;

   public Option<T> defaultValue() {
      return new Some<T>(this.org$apache$spark$internal$config$ConfigEntryWithDefault$$_defaultValue);
   }

   public String defaultValueString() {
      return (String)super.stringConverter().apply(this.org$apache$spark$internal$config$ConfigEntryWithDefault$$_defaultValue);
   }

   public T readFrom(ConfigReader reader) {
      return (T)this.readString(reader).map(super.valueConverter()).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ConfigEntryWithDefault $outer;

         public final T apply() {
            return this.$outer.org$apache$spark$internal$config$ConfigEntryWithDefault$$_defaultValue;
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

   public ConfigEntryWithDefault(String key, List<String> alternatives, T _defaultValue, Function1<String, T> valueConverter, Function1<T, String> stringConverter, String doc, boolean isPublic) {
      super(key, alternatives, valueConverter, stringConverter, doc, isPublic);
      this.org$apache$spark$internal$config$ConfigEntryWithDefault$$_defaultValue = (T)_defaultValue;
   }
}
