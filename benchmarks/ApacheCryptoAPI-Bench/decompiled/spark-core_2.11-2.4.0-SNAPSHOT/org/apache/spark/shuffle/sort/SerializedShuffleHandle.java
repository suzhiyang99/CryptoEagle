package org.apache.spark.shuffle.sort;

import org.apache.spark.ShuffleDependency;
import org.apache.spark.shuffle.BaseShuffleHandle;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001y2Q!\u0001\u0002\u0001\r1\u0011qcU3sS\u0006d\u0017N_3e'\",hM\u001a7f\u0011\u0006tG\r\\3\u000b\u0005\r!\u0011\u0001B:peRT!!\u0002\u0004\u0002\u000fMDWO\u001a4mK*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014x-F\u0002\u000e)\t\u001a\"\u0001\u0001\b\u0011\u000b=\u0001\"#I\u0011\u000e\u0003\u0011I!!\u0005\u0003\u0003#\t\u000b7/Z*ik\u001a4G.\u001a%b]\u0012dW\r\u0005\u0002\u0014)1\u0001A!B\u000b\u0001\u0005\u00049\"!A&\u0004\u0001E\u0011\u0001D\b\t\u00033qi\u0011A\u0007\u0006\u00027\u0005)1oY1mC&\u0011QD\u0007\u0002\b\u001d>$\b.\u001b8h!\tIr$\u0003\u0002!5\t\u0019\u0011I\\=\u0011\u0005M\u0011C!B\u0012\u0001\u0005\u00049\"!\u0001,\t\u0013\u0015\u0002!\u0011!Q\u0001\n\u0019J\u0013!C:ik\u001a4G.Z%e!\tIr%\u0003\u0002)5\t\u0019\u0011J\u001c;\n\u0005\u0015R\u0013BA\u0016\u0005\u00055\u0019\u0006.\u001e4gY\u0016D\u0015M\u001c3mK\"IQ\u0006\u0001B\u0001B\u0003%aEL\u0001\b]VlW*\u00199t\u0013\ti\u0003\u0003C\u00051\u0001\t\u0005\t\u0015!\u00032k\u0005QA-\u001a9f]\u0012,gnY=\u0011\u000bI\u001a$#I\u0011\u000e\u0003\u0019I!\u0001\u000e\u0004\u0003#MCWO\u001a4mK\u0012+\u0007/\u001a8eK:\u001c\u00170\u0003\u00021!!)q\u0007\u0001C\u0001q\u00051A(\u001b8jiz\"B!O\u001e={A!!\b\u0001\n\"\u001b\u0005\u0011\u0001\"B\u00137\u0001\u00041\u0003\"B\u00177\u0001\u00041\u0003\"\u0002\u00197\u0001\u0004\t\u0004"
)
public class SerializedShuffleHandle<K, V> extends BaseShuffleHandle<K, V, V> {
   public SerializedShuffleHandle(int shuffleId, int numMaps, ShuffleDependency<K, V, V> dependency) {
      super(shuffleId, numMaps, dependency);
   }
}
