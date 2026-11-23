package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product2$mcDI$sp;
import scala.Product2$mcDI$sp$class;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcDI$sp extends MutablePair<Object, Object> implements Product2$mcDI$sp {
   public double _1$mcD$sp;
   public int _2$mcI$sp;

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

   public int _2$mcI$sp() {
      return this._2$mcI$sp;
   }

   public int _2() {
      return this._2$mcI$sp();
   }

   public void _2$mcI$sp_$eq(int x$1) {
      this._2$mcI$sp = x$1;
   }

   public void _2_$eq(int x$1) {
      this._2$mcI$sp_$eq(x$1);
   }

   public MutablePair<Object, Object> update(double n1, int n2) {
      return this.update$mcDI$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcDI$sp(double n1, int n2) {
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

   public <T1, T2> int copy$default$2() {
      return this.copy$default$2$mcI$sp();
   }

   public <T1, T2> int copy$default$2$mcI$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcDI$sp(double _1$mcD$sp, int _2$mcI$sp) {
      this._1$mcD$sp = _1$mcD$sp;
      this._2$mcI$sp = _2$mcI$sp;
      super((Object)null, (Object)null);
      Product2$mcDI$sp$class.$init$(this);
   }

   public MutablePair$mcDI$sp() {
      this(BoxesRunTime.unboxToDouble((Object)null), BoxesRunTime.unboxToInt((Object)null));
   }
}
