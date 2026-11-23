package org.apache.spark.util.collection;

import org.apache.spark.util.SizeEstimator$;
import scala.Predef$;
import scala.collection.immutable.$colon$colon;
import scala.collection.immutable.List;
import scala.collection.mutable.Queue;
import scala.math.package$;
import scala.runtime.BoxedUnit;

public abstract class SizeTracker$class {
   public static void resetSamples(SizeTracker $this) {
      $this.org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq(1L);
      $this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum_$eq(1L);
      $this.org$apache$spark$util$collection$SizeTracker$$samples().clear();
      takeSample($this);
   }

   public static void afterUpdate(SizeTracker $this) {
      $this.org$apache$spark$util$collection$SizeTracker$$numUpdates_$eq($this.org$apache$spark$util$collection$SizeTracker$$numUpdates() + 1L);
      if ($this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum() == $this.org$apache$spark$util$collection$SizeTracker$$numUpdates()) {
         takeSample($this);
      }

   }

   private static void takeSample(SizeTracker $this) {
      $this.org$apache$spark$util$collection$SizeTracker$$samples().enqueue(Predef$.MODULE$.wrapRefArray(new SizeTracker.Sample[]{new SizeTracker.Sample(SizeEstimator$.MODULE$.estimate($this), $this.org$apache$spark$util$collection$SizeTracker$$numUpdates())}));
      if ($this.org$apache$spark$util$collection$SizeTracker$$samples().size() > 2) {
         $this.org$apache$spark$util$collection$SizeTracker$$samples().dequeue();
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      double bytesDelta;
      label16: {
         List var3 = $this.org$apache$spark$util$collection$SizeTracker$$samples().toList().reverse();
         if (var3 instanceof $colon$colon) {
            $colon$colon var4 = ($colon$colon)var3;
            SizeTracker.Sample latest = (SizeTracker.Sample)var4.head();
            List var6 = var4.tl$1();
            if (var6 instanceof $colon$colon) {
               $colon$colon var7 = ($colon$colon)var6;
               SizeTracker.Sample previous = (SizeTracker.Sample)var7.head();
               bytesDelta = (double)(latest.size() - previous.size()) / (double)(latest.numUpdates() - previous.numUpdates());
               break label16;
            }
         }

         bytesDelta = (double)0.0F;
      }

      $this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate_$eq(package$.MODULE$.max((double)0.0F, bytesDelta));
      $this.org$apache$spark$util$collection$SizeTracker$$nextSampleNum_$eq((long)package$.MODULE$.ceil((double)$this.org$apache$spark$util$collection$SizeTracker$$numUpdates() * $this.org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE()));
   }

   public static long estimateSize(SizeTracker $this) {
      Predef$.MODULE$.assert($this.org$apache$spark$util$collection$SizeTracker$$samples().nonEmpty());
      double extrapolatedDelta = $this.org$apache$spark$util$collection$SizeTracker$$bytesPerUpdate() * (double)($this.org$apache$spark$util$collection$SizeTracker$$numUpdates() - ((SizeTracker.Sample)$this.org$apache$spark$util$collection$SizeTracker$$samples().last()).numUpdates());
      return (long)((double)((SizeTracker.Sample)$this.org$apache$spark$util$collection$SizeTracker$$samples().last()).size() + extrapolatedDelta);
   }

   public static void $init$(SizeTracker $this) {
      $this.org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$SAMPLE_GROWTH_RATE_$eq(1.1);
      $this.org$apache$spark$util$collection$SizeTracker$_setter_$org$apache$spark$util$collection$SizeTracker$$samples_$eq(new Queue());
      $this.resetSamples();
   }
}
