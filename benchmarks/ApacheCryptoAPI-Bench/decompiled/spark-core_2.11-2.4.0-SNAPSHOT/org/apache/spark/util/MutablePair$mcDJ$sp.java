package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.Product2$mcDJ$sp;
import scala.Product2$mcDJ$sp$class;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcDJ$sp extends MutablePair<Object, Object> implements Product2$mcDJ$sp {
   public double _1$mcD$sp;
   public long _2$mcJ$sp;

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

   public long _2$mcJ$sp() {
      return this._2$mcJ$sp;
   }

   public long _2() {
      return this._2$mcJ$sp();
   }

   public void _2$mcJ$sp_$eq(long x$1) {
      this._2$mcJ$sp = x$1;
   }

   public void _2_$eq(long x$1) {
      this._2$mcJ$sp_$eq(x$1);
   }

   public MutablePair<Object, Object> update(double n1, long n2) {
      return this.update$mcDJ$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcDJ$sp(double n1, long n2) {
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

   public <T1, T2> long copy$default$2() {
      return this.copy$default$2$mcJ$sp();
   }

   public <T1, T2> long copy$default$2$mcJ$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcDJ$sp(double _1$mcD$sp, long _2$mcJ$sp) {
      this._1$mcD$sp = _1$mcD$sp;
      this._2$mcJ$sp = _2$mcJ$sp;
      super((Object)null, (Object)null);
      Product2$mcDJ$sp$class.$init$(this);
   }

   public MutablePair$mcDJ$sp() {
      this(BoxesRunTime.unboxToDouble((Object)null), BoxesRunTime.unboxToLong((Object)null));
   }
}
