package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcCD$sp extends MutablePair<Object, Object> {
   public char _1$mcC$sp;
   public double _2$mcD$sp;

   public char _1$mcC$sp() {
      return this._1$mcC$sp;
   }

   public char _1() {
      return this._1$mcC$sp();
   }

   public void _1$mcC$sp_$eq(char x$1) {
      this._1$mcC$sp = x$1;
   }

   public void _1_$eq(char x$1) {
      this._1$mcC$sp_$eq(x$1);
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

   public MutablePair<Object, Object> update(char n1, double n2) {
      return this.update$mcCD$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcCD$sp(char n1, double n2) {
      this._1_$eq(n1);
      this._2_$eq(n2);
      return this;
   }

   public <T1, T2> char copy$default$1() {
      return this.copy$default$1$mcC$sp();
   }

   public <T1, T2> char copy$default$1$mcC$sp() {
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

   public MutablePair$mcCD$sp(char _1$mcC$sp, double _2$mcD$sp) {
      this._1$mcC$sp = _1$mcC$sp;
      this._2$mcD$sp = _2$mcD$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcCD$sp() {
      this(BoxesRunTime.unboxToChar((Object)null), BoxesRunTime.unboxToDouble((Object)null));
   }
}
