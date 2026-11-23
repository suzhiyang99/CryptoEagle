package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcZD$sp extends MutablePair<Object, Object> {
   public boolean _1$mcZ$sp;
   public double _2$mcD$sp;

   public boolean _1$mcZ$sp() {
      return this._1$mcZ$sp;
   }

   public boolean _1() {
      return this._1$mcZ$sp();
   }

   public void _1$mcZ$sp_$eq(boolean x$1) {
      this._1$mcZ$sp = x$1;
   }

   public void _1_$eq(boolean x$1) {
      this._1$mcZ$sp_$eq(x$1);
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

   public MutablePair<Object, Object> update(boolean n1, double n2) {
      return this.update$mcZD$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcZD$sp(boolean n1, double n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> boolean copy$default$1() {
      return this.copy$default$1$mcZ$sp();
   }

   public <T1, T2> boolean copy$default$1$mcZ$sp() {
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

   public MutablePair$mcZD$sp(boolean _1$mcZ$sp, double _2$mcD$sp) {
      this._1$mcZ$sp = _1$mcZ$sp;
      this._2$mcD$sp = _2$mcD$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcZD$sp() {
      this(BoxesRunTime.unboxToBoolean((Object)null), BoxesRunTime.unboxToDouble((Object)null));
   }
}
