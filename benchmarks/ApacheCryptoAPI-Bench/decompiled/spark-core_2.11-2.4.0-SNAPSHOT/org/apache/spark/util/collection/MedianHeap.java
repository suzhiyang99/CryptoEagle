package org.apache.spark.util.collection;

import java.util.NoSuchElementException;
import scala.Predef$;
import scala.collection.mutable.PriorityQueue;
import scala.collection.mutable.PriorityQueue$;
import scala.math.Ordering;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001Q3Q!\u0001\u0002\u0001\r1\u0011!\"T3eS\u0006t\u0007*Z1q\u0015\t\u0019A!\u0001\u0006d_2dWm\u0019;j_:T!!\u0002\u0004\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000f!\tQa\u001d9be.T!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sON\u0011\u0001!\u0004\t\u0003\u001dEi\u0011a\u0004\u0006\u0002!\u0005)1oY1mC&\u0011!c\u0004\u0002\u0007\u0003:L(+\u001a4\t\u0011Q\u0001!Q1A\u0005\u0004Y\t1a\u001c:e\u0007\u0001)\u0012a\u0006\t\u00041\u0001\u001acBA\r\u001f\u001d\tQR$D\u0001\u001c\u0015\taR#\u0001\u0004=e>|GOP\u0005\u0002!%\u0011qdD\u0001\ba\u0006\u001c7.Y4f\u0013\t\t#E\u0001\u0005Pe\u0012,'/\u001b8h\u0015\tyr\u0002\u0005\u0002\u000fI%\u0011Qe\u0004\u0002\u0007\t>,(\r\\3\t\u0011\u001d\u0002!\u0011!Q\u0001\n]\tAa\u001c:eA!)\u0011\u0006\u0001C\u0001U\u00051A(\u001b8jiz\"\u0012a\u000b\u000b\u0003Y9\u0002\"!\f\u0001\u000e\u0003\tAQ\u0001\u0006\u0015A\u0004]Aa\u0001\r\u0001!B\u0013\t\u0014aC:nC2dWM\u001d%bY\u001a\u00042A\r\u001c$\u001b\u0005\u0019$B\u0001\u001b6\u0003\u001diW\u000f^1cY\u0016T!aA\b\n\u0005]\u001a$!\u0004)sS>\u0014\u0018\u000e^=Rk\u0016,X\r\u0003\u0004:\u0001\u0001\u0006K!M\u0001\u000bY\u0006\u0014x-\u001a:IC24\u0007\"B\u001e\u0001\t\u0003a\u0014aB5t\u000b6\u0004H/\u001f\u000b\u0002{A\u0011aBP\u0005\u0003\u007f=\u0011qAQ8pY\u0016\fg\u000eC\u0003B\u0001\u0011\u0005!)\u0001\u0003tSj,G#A\"\u0011\u00059!\u0015BA#\u0010\u0005\rIe\u000e\u001e\u0005\u0006\u000f\u0002!\t\u0001S\u0001\u0007S:\u001cXM\u001d;\u0015\u0005%c\u0005C\u0001\bK\u0013\tYuB\u0001\u0003V]&$\b\"B'G\u0001\u0004\u0019\u0013!\u0001=\t\r=\u0003\u0001\u0015\"\u0003Q\u0003%\u0011XMY1mC:\u001cW\rF\u0001J\u0011\u0015\u0011\u0006\u0001\"\u0001T\u0003\u0019iW\rZ5b]V\t1\u0005"
)
public class MedianHeap {
   private final Ordering<Object> ord;
   private PriorityQueue<Object> smallerHalf;
   private PriorityQueue<Object> largerHalf;

   public Ordering<Object> ord() {
      return this.ord;
   }

   public boolean isEmpty() {
      return this.smallerHalf.isEmpty() && this.largerHalf.isEmpty();
   }

   public int size() {
      return this.smallerHalf.size() + this.largerHalf.size();
   }

   public void insert(double x) {
      if (this.isEmpty()) {
         this.largerHalf.enqueue(Predef$.MODULE$.wrapDoubleArray(new double[]{x}));
      } else if (x > this.median()) {
         this.largerHalf.enqueue(Predef$.MODULE$.wrapDoubleArray(new double[]{x}));
      } else {
         this.smallerHalf.enqueue(Predef$.MODULE$.wrapDoubleArray(new double[]{x}));
      }

      this.rebalance();
   }

   private void rebalance() {
      if (this.largerHalf.size() - this.smallerHalf.size() > 1) {
         this.smallerHalf.enqueue(Predef$.MODULE$.wrapDoubleArray(new double[]{BoxesRunTime.unboxToDouble(this.largerHalf.dequeue())}));
      }

      if (this.smallerHalf.size() - this.largerHalf.size() > 1) {
         this.largerHalf.enqueue(Predef$.MODULE$.wrapDoubleArray(new double[]{BoxesRunTime.unboxToDouble(this.smallerHalf.dequeue())}));
      }

   }

   public double median() {
      if (this.isEmpty()) {
         throw new NoSuchElementException("MedianHeap is empty.");
      } else {
         return this.largerHalf.size() == this.smallerHalf.size() ? (BoxesRunTime.unboxToDouble(this.largerHalf.head()) + BoxesRunTime.unboxToDouble(this.smallerHalf.head())) / (double)2.0F : (this.largerHalf.size() > this.smallerHalf.size() ? BoxesRunTime.unboxToDouble(this.largerHalf.head()) : BoxesRunTime.unboxToDouble(this.smallerHalf.head()));
      }
   }

   public MedianHeap(Ordering<Object> ord) {
      this.ord = ord;
      this.smallerHalf = (PriorityQueue)PriorityQueue$.MODULE$.empty(ord);
      this.largerHalf = (PriorityQueue)PriorityQueue$.MODULE$.empty(ord.reverse());
   }
}
