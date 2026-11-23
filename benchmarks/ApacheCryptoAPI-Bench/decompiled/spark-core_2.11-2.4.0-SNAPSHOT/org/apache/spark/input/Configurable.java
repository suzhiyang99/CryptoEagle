package org.apache.spark.input;

import org.apache.hadoop.conf.Configuration;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001a2\u0001\"\u0001\u0002\u0011\u0002\u0007\u0005AA\u0003\u0002\r\u0007>tg-[4ve\u0006\u0014G.\u001a\u0006\u0003\u0007\u0011\tQ!\u001b8qkRT!!\u0002\u0004\u0002\u000bM\u0004\u0018M]6\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\n\u0004\u0001-\u0019\u0002C\u0001\u0007\u0012\u001b\u0005i!B\u0001\b\u0010\u0003\u0011a\u0017M\\4\u000b\u0003A\tAA[1wC&\u0011!#\u0004\u0002\u0007\u001f\nTWm\u0019;\u0011\u0005QIR\"A\u000b\u000b\u0005Y9\u0012\u0001B2p]\u001aT!\u0001\u0007\u0004\u0002\r!\fGm\\8q\u0013\t\tQ\u0003C\u0003\u001c\u0001\u0011\u0005Q$\u0001\u0004%S:LG\u000fJ\u0002\u0001)\u0005q\u0002CA\u0010#\u001b\u0005\u0001#\"A\u0011\u0002\u000bM\u001c\u0017\r\\1\n\u0005\r\u0002#\u0001B+oSRD\u0011B\u0006\u0001A\u0002\u0003\u0007I\u0011B\u0013\u0016\u0003\u0019\u0002\"\u0001F\u0014\n\u0005!*\"!D\"p]\u001aLw-\u001e:bi&|g\u000eC\u0005+\u0001\u0001\u0007\t\u0019!C\u0005W\u0005A1m\u001c8g?\u0012*\u0017\u000f\u0006\u0002\u001fY!9Q&KA\u0001\u0002\u00041\u0013a\u0001=%c!1q\u0006\u0001Q!\n\u0019\nQaY8oM\u0002BQ!\r\u0001\u0005\u0002I\nqa]3u\u0007>tg\r\u0006\u0002\u001fg!)A\u0007\ra\u0001M\u0005\t1\rC\u00037\u0001\u0011\u0005q'A\u0004hKR\u001cuN\u001c4\u0015\u0003\u0019\u0002"
)
public interface Configurable extends org.apache.hadoop.conf.Configurable {
   Configuration org$apache$spark$input$Configurable$$conf();

   @TraitSetter
   void org$apache$spark$input$Configurable$$conf_$eq(Configuration var1);

   void setConf(Configuration var1);

   Configuration getConf();
}
