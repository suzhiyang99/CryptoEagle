package org.apache.spark.broadcast;

import org.apache.spark.SecurityManager;
import org.apache.spark.SparkConf;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001a3\u0001\"\u0001\u0002\u0011\u0002G\u0005AA\u0003\u0002\u0011\u0005J|\u0017\rZ2bgR4\u0015m\u0019;pefT!a\u0001\u0003\u0002\u0013\t\u0014x.\u00193dCN$(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0005\u0001Y\u0001C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\rC\u0003\u0013\u0001\u0019\u0005A#\u0001\u0006j]&$\u0018.\u00197ju\u0016\u001c\u0001\u0001\u0006\u0003\u00161u\u0019\u0003C\u0001\u0007\u0017\u0013\t9RB\u0001\u0003V]&$\b\"B\r\u0012\u0001\u0004Q\u0012\u0001C5t\tJLg/\u001a:\u0011\u00051Y\u0012B\u0001\u000f\u000e\u0005\u001d\u0011un\u001c7fC:DQAH\tA\u0002}\tAaY8oMB\u0011\u0001%I\u0007\u0002\t%\u0011!\u0005\u0002\u0002\n'B\f'o[\"p]\u001aDQ\u0001J\tA\u0002\u0015\n1b]3dkJLG/_'heB\u0011\u0001EJ\u0005\u0003O\u0011\u0011qbU3dkJLG/_'b]\u0006<WM\u001d\u0005\u0006S\u00011\tAK\u0001\r]\u0016<(I]8bI\u000e\f7\u000f^\u000b\u0003WM\"B\u0001\f#G\u0011R\u0011Q\u0006\u0010\t\u0004]=\nT\"\u0001\u0002\n\u0005A\u0012!!\u0003\"s_\u0006$7-Y:u!\t\u00114\u0007\u0004\u0001\u0005\u000bQB#\u0019A\u001b\u0003\u0003Q\u000b\"AN\u001d\u0011\u000519\u0014B\u0001\u001d\u000e\u0005\u001dqu\u000e\u001e5j]\u001e\u0004\"\u0001\u0004\u001e\n\u0005mj!aA!os\"9Q\bKA\u0001\u0002\bq\u0014AC3wS\u0012,gnY3%cA\u0019qHQ\u0019\u000e\u0003\u0001S!!Q\u0007\u0002\u000fI,g\r\\3di&\u00111\t\u0011\u0002\t\u00072\f7o\u001d+bO\")Q\t\u000ba\u0001c\u0005)a/\u00197vK\")q\t\u000ba\u00015\u00059\u0011n\u001d'pG\u0006d\u0007\"B%)\u0001\u0004Q\u0015AA5e!\ta1*\u0003\u0002M\u001b\t!Aj\u001c8h\u0011\u0015q\u0005A\"\u0001P\u0003-)hN\u0019:pC\u0012\u001c\u0017m\u001d;\u0015\tU\u0001\u0016k\u0015\u0005\u0006\u00136\u0003\rA\u0013\u0005\u0006%6\u0003\rAG\u0001\u0011e\u0016lwN^3Ge>lGI]5wKJDQ\u0001V'A\u0002i\t\u0001B\u00197pG.Lgn\u001a\u0005\u0006-\u00021\taV\u0001\u0005gR|\u0007\u000fF\u0001\u0016\u0001"
)
public interface BroadcastFactory {
   void initialize(boolean var1, SparkConf var2, SecurityManager var3);

   <T> Broadcast<T> newBroadcast(T var1, boolean var2, long var3, ClassTag<T> var5);

   void unbroadcast(long var1, boolean var3, boolean var4);

   void stop();
}
