package org.apache.spark.util;

import org.apache.spark.annotation.DeveloperApi;
import scala.runtime.BoxesRunTime;

@DeveloperApi
public class MutablePair$mcZI$sp extends MutablePair<Object, Object> {
   public boolean _1$mcZ$sp;
   public int _2$mcI$sp;

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

   public MutablePair<Object, Object> update(boolean n1, int n2) {
      return this.update$mcZI$sp(n1, n2);
   }

   public MutablePair<Object, Object> update$mcZI$sp(boolean n1, int n2) {
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

   public <T1, T2> int copy$default$2() {
      return this.copy$default$2$mcI$sp();
   }

   public <T1, T2> int copy$default$2$mcI$sp() {
      return this._2();
   }

   public boolean specInstance$() {
      return true;
   }

   public MutablePair$mcZI$sp(boolean _1$mcZ$sp, int _2$mcI$sp) {
      this._1$mcZ$sp = _1$mcZ$sp;
      this._2$mcI$sp = _2$mcI$sp;
      super((Object)null, (Object)null);
   }

   public MutablePair$mcZI$sp() {
      this(BoxesRunTime.unboxToBoolean((Object)null), BoxesRunTime.unboxToInt((Object)null));
   }
}
