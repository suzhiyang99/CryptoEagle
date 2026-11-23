package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product2$mcDD$sp;
import scala.Product2$mcDD$sp$class;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcDD$sp extends MutablePair<Object, Object> implements Product2$mcDD$sp {
   public double _1$mcD$sp;
   public double _2$mcD$sp;

   public double _1$mcD$sp() {
      return this._1$mcD$sp;
   }

   public double _1() {
      return this._1$mcD$sp();
   }

   public void _1$mcD$sp_$eq(double x$1) {
      this._1$mcD$sp = x$1;
   }

   public void _1_$eq(double x$1) {
      this._1$mcD$sp_$eq(x$1);
   }

   public double _2$mcD$sp() {
      return this._2$mcD$sp;
   }

   public double _2() {
      return this._2$mcD$sp();
   }

   public void _2$mcD$sp_$eq(double x$1) {
      this._2$mcD$sp = x$1;
   }

   public void _2_$eq(double x$1) {
      this._2$mcD$sp_$eq(x$1);
   }

   public MutablePair<Object, Object> update(double n1, double n2) {
      return this.update$mcDD$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcDD$sp(double n1, double n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> double copy$default$1() {
      return this.copy$default$1$mcD$sp();
   }

   public <T1, T2> double copy$default$1$mcD$sp() {
      return this._1();
   }

   public <T1, T2> double copy$default$2() {
      return this.copy$default$2$mcD$sp();
   }

   public <T1, T2> double copy$default$2$mcD$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcDD$sp(double _1$mcD$sp, double _2$mcD$sp) {
      this._1$mcD$sp = _1$mcD$sp;
      this._2$mcD$sp = _2$mcD$sp;
      super((Object)null, (Object)null);
      Product2$mcDD$sp$class.$init$(this);
   }

   public MutablePair$mcDD$sp() {
      this(BoxesRunTime.unboxToDouble((Object)null), BoxesRunTime.unboxToDouble((Object)null));
   }
}
