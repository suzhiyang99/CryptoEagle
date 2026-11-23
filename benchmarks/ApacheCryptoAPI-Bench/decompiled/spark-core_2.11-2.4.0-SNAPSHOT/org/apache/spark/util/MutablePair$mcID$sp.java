package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product2$mcID$sp;
import scala.Product2$mcID$sp$class;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcID$sp extends MutablePair<Object, Object> implements Product2$mcID$sp {
   public int _1$mcI$sp;
   public double _2$mcD$sp;

   public int _1$mcI$sp() {
      return this._1$mcI$sp;
   }

   public int _1() {
      return this._1$mcI$sp();
   }

   public void _1$mcI$sp_$eq(int x$1) {
      this._1$mcI$sp = x$1;
   }

   public void _1_$eq(int x$1) {
      this._1$mcI$sp_$eq(x$1);
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

   public MutablePair<Object, Object> update(int n1, double n2) {
      return this.update$mcID$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcID$sp(int n1, double n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> int copy$default$1() {
      return this.copy$default$1$mcI$sp();
   }

   public <T1, T2> int copy$default$1$mcI$sp() {
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

   public MutablePair$mcID$sp(int _1$mcI$sp, double _2$mcD$sp) {
      this._1$mcI$sp = _1$mcI$sp;
      this._2$mcD$sp = _2$mcD$sp;
      super((Object)null, (Object)null);
      Product2$mcID$sp$class.$init$(this);
   }

   public MutablePair$mcID$sp() {
      this(BoxesRunTime.unboxToInt((Object)null), BoxesRunTime.unboxToDouble((Object)null));
   }
}
