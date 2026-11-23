package org.apache.spark.util.collection;

import scala.Predef$;
import scala.reflect.ClassTag;
import scala.runtime.BoxedUnit;

public class PrimitiveVector$mcD$sp extends PrimitiveVector<Object> {
   public double[] _array$mcD$sp;
   private final int initialSize;
   private final ClassTag<Object> evidence$1;

   public double[] _array$mcD$sp() {
      return this._array$mcD$sp;
   }

   public double[] _array() {
      return this._array$mcD$sp();
   }

   public void _array$mcD$sp_$eq(double[] x$1) {
      this._array$mcD$sp = x$1;
   }

   public void _array_$eq(double[] x$1) {
      this._array$mcD$sp_$eq(x$1);
   }

   public double apply(int index) {
      return this.apply$mcD$sp(index);
   }

   public double apply$mcD$sp(int index) {
      Predef$.MODULE$.require(index < this.org$apache$spark$util$collection$PrimitiveVector$$_numElements());
      return this._array()[index];
   }

   public void $plus$eq(double value) {
      this.$plus$eq$mcD$sp(value);
   }

   public void $plus$eq$mcD$sp(double value) {
      if (this.org$apache$spark$util$collection$PrimitiveVector$$_numElements() == this._array().length) {
         this.resize$mcD$sp(this._array().length * 2);
      } else {
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this._array()[this.org$apache$spark$util$collection$PrimitiveVector$$_numElements()] = value;
      this.org$apache$spark$util$collection$PrimitiveVector$$_numElements_$eq(this.org$apache$spark$util$collection$PrimitiveVector$$_numElements() + 1);
   }

   public double[] array() {
      return this.array$mcD$sp();
   }

   public double[] array$mcD$sp() {
      return this._array();
   }

   public PrimitiveVector<Object> trim() {
      return this.trim$mcD$sp();
   }

   public PrimitiveVector<Object> trim$mcD$sp() {
      return this.resize$mcD$sp(this.size());
   }

   public PrimitiveVector<Object> resize(int newLength) {
      return this.resize$mcD$sp(newLength);
   }

   public PrimitiveVector<Object> resize$mcD$sp(int newLength) {
      this._array_$eq(this.copyArrayWithLength$mcD$sp(newLength));
      if (newLength < this.org$apache$spark$util$collection$PrimitiveVector$$_numElements()) {
         this.org$apache$spark$util$collection$PrimitiveVector$$_numElements_$eq(newLength);
      }

      return this;
   }

   public double[] toArray() {
      return this.toArray$mcD$sp();
   }

   public double[] toArray$mcD$sp() {
      return this.copyArrayWithLength$mcD$sp(this.size());
   }

   public double[] copyArrayWithLength(int length) {
      return this.copyArrayWithLength$mcD$sp(length);
   }

   public double[] copyArrayWithLength$mcD$sp(int length) {
      double[] copy = (double[])this.org$apache$spark$util$collection$PrimitiveVector$$evidence$1.newArray(length);
      Predef$.MODULE$.genericArrayOps(this._array()).copyToArray(copy);
      return copy;
   }

   public boolean specInstance$() {
      return true;
   }

   public PrimitiveVector$mcD$sp(int initialSize, ClassTag<Object> evidence$1) {
      super(initialSize, evidence$1);
      this.initialSize = initialSize;
      this.evidence$1 = evidence$1;
      super.org$apache$spark$util$collection$PrimitiveVector$$_numElements = 0;
      this._array_$eq(evidence$1.newArray(initialSize));
   }
}
