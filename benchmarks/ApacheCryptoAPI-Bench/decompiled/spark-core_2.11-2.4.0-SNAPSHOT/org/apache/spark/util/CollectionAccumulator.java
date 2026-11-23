package org.apache.spark.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import scala.Predef$;
import scala.StringContext;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001A3A!\u0001\u0002\u0001\u0017\t)2i\u001c7mK\u000e$\u0018n\u001c8BG\u000e,X.\u001e7bi>\u0014(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001!\u0006\u0002\r'M\u0011\u0001!\u0004\t\u0005\u001d=\tr$D\u0001\u0003\u0013\t\u0001\"AA\u0007BG\u000e,X.\u001e7bi>\u0014hK\r\t\u0003%Ma\u0001\u0001B\u0003\u0015\u0001\t\u0007QCA\u0001U#\t1B\u0004\u0005\u0002\u001855\t\u0001DC\u0001\u001a\u0003\u0015\u00198-\u00197b\u0013\tY\u0002DA\u0004O_RD\u0017N\\4\u0011\u0005]i\u0012B\u0001\u0010\u0019\u0005\r\te.\u001f\t\u0004A\u0011\nR\"A\u0011\u000b\u0005\r\u0011#\"A\u0012\u0002\t)\fg/Y\u0005\u0003K\u0005\u0012A\u0001T5ti\")q\u0005\u0001C\u0001Q\u00051A(\u001b8jiz\"\u0012!\u000b\t\u0004\u001d\u0001\t\u0002bB\u0016\u0001\u0005\u0004%I\u0001L\u0001\u0006?2L7\u000f^\u000b\u0002?!1a\u0006\u0001Q\u0001\n}\taa\u00187jgR\u0004\u0003\"\u0002\u0019\u0001\t\u0003\n\u0014AB5t5\u0016\u0014x.F\u00013!\t92'\u0003\u000251\t9!i\\8mK\u0006t\u0007\"\u0002\u001c\u0001\t\u0003B\u0013\u0001D2paf\fe\u000e\u001a*fg\u0016$\b\"\u0002\u001d\u0001\t\u0003B\u0013\u0001B2pafDQA\u000f\u0001\u0005Bm\nQA]3tKR$\u0012\u0001\u0010\t\u0003/uJ!A\u0010\r\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u0001\u0002!\t%Q\u0001\u0004C\u0012$GC\u0001\u001fC\u0011\u0015\u0019u\b1\u0001\u0012\u0003\u00051\b\"B#\u0001\t\u00032\u0015!B7fe\u001e,GC\u0001\u001fH\u0011\u0015AE\t1\u0001\u000e\u0003\u0015yG\u000f[3s\u0011\u0015Q\u0005\u0001\"\u0011-\u0003\u00151\u0018\r\\;f\u0011\u0019a\u0005\u0001\"\u0001\u0005\u001b\u0006A1/\u001a;WC2,X\r\u0006\u0002=\u001d\")qj\u0013a\u0001?\u0005Aa.Z<WC2,X\r"
)
public class CollectionAccumulator<T> extends AccumulatorV2<T, List<T>> {
   private final List<T> _list = Collections.synchronizedList(new ArrayList());

   private List<T> _list() {
      return this._list;
   }

   public boolean isZero() {
      return this._list().isEmpty();
   }

   public CollectionAccumulator<T> copyAndReset() {
      return new CollectionAccumulator<T>();
   }

   public CollectionAccumulator<T> copy() {
      CollectionAccumulator newAcc = new CollectionAccumulator();
      synchronized(this._list()) {
         BoxesRunTime.boxToBoolean(newAcc._list().addAll(this._list()));
         return newAcc;
      }
   }

   public void reset() {
      this._list().clear();
   }

   public void add(T v) {
      this._list().add(v);
   }

   public void merge(AccumulatorV2<T, List<T>> other) {
      if (other instanceof CollectionAccumulator) {
         CollectionAccumulator var3 = (CollectionAccumulator)other;
         this._list().addAll(var3.value());
         BoxedUnit var4 = BoxedUnit.UNIT;
      } else {
         throw new UnsupportedOperationException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cannot merge ", " with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.getClass().getName(), other.getClass().getName()})));
      }
   }

   public List<T> value() {
      List var2;
      synchronized(this._list()) {
         var2 = Collections.unmodifiableList(new ArrayList(this._list()));
      }

      return var2;
   }

   public void setValue(List<T> newValue) {
      this._list().clear();
      this._list().addAll(newValue);
   }
}
